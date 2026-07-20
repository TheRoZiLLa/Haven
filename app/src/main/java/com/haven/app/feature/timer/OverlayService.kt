package com.haven.app.feature.timer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.haven.app.MainActivity
import com.haven.app.R
import com.haven.app.navigation.NavigationTarget
import com.haven.app.navigation.Routes
import kotlin.math.abs
import kotlin.math.sign

/**
 * Service that displays the cozy animal overlay floating above all other applications.
 * Features an entry bounce animation from the deep bottom to its resting bottom position
 * with squash-and-stretch scaling, followed by a continuous floating sway animation.
 *
 * Key design: the rootView is NEVER removed from WindowManager during the entry→sway
 * transition. Instead, windowManager.updateViewLayout() resizes the window in-place,
 * keeping the ImageView pixel-perfect on screen with zero flicker.
 *
 * Gestures:
 *  - Tap                    → opens BreakSelectionScreen (starts the break)
 *  - Swipe left / right / down → throws the animal off-screen (dismisses overlay)
 */
class OverlayService : Service() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, OverlayService::class.java)
            context.startService(intent)
        }
    }

    private lateinit var windowManager: WindowManager
    private lateinit var imageView: ImageView
    private lateinit var rootView: FrameLayout

    private var entryAnimatorSet: AnimatorSet? = null
    private var swayAnimator: ValueAnimator? = null

    // ── Swipe state ────────────────────────────────────────────────────────
    /** translationY base contributed by the sway animation at the moment drag starts */
    private var swayTranslationAtDragStart = 0f

    /** Accumulated drag delta from the finger, independently of sway */
    private var dragDeltaX = 0f
    private var dragDeltaY = 0f

    /** Raw touch-down position (used to detect tap vs. swipe) */
    private var touchDownX = 0f
    private var touchDownY = 0f

    /** True once a drag gesture has been committed (exceeded touch slop) */
    private var isDragging = false

    /** Threshold (in px) after which the swipe triggers a dismiss */
    private val dismissThresholdDp = 90f

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val density = resources.displayMetrics.density
        val sizePx        = (180 * density).toInt()
        val marginBottomPx = (48 * density).toInt()
        // Extra headroom above the animal so it can sway upward without clipping.
        // Must cover: sizePx (animal) + marginBottom (48dp) + sway range (20dp buffer)
        val entryHeightPx = sizePx + marginBottomPx + (350 * density).toInt() // full off-screen depth

        // ── ImageView ──────────────────────────────────────────────────────
        imageView = ImageView(this).apply {
            setImageResource(R.drawable.place_holder_animal_popup)
            scaleType = ImageView.ScaleType.FIT_CENTER
            isClickable = true
            isFocusable = true
        }

        // ── Touch handler: detects tap vs. drag-to-dismiss ─────────────────
        val touchSlop = (8 * density)
        val dismissThresholdPx = dismissThresholdDp * density

        imageView.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    touchDownX = event.rawX
                    touchDownY = event.rawY
                    dragDeltaX = 0f
                    dragDeltaY = 0f
                    isDragging = false
                    // Pause the sway so the drag feels anchored
                    swayTranslationAtDragStart = imageView.translationY
                    swayAnimator?.pause()
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - touchDownX
                    val dy = event.rawY - touchDownY

                    if (!isDragging && (abs(dx) > touchSlop || abs(dy) > touchSlop)) {
                        isDragging = true
                    }

                    if (isDragging) {
                        dragDeltaX = dx
                        dragDeltaY = dy

                        // Move the image with the finger
                        imageView.translationX = dragDeltaX
                        // Vertical: sway baseline + drag delta
                        imageView.translationY = swayTranslationAtDragStart + dragDeltaY

                        // Tilt proportional to horizontal drag (max ±22°)
                        val tiltFraction = (dragDeltaX / dismissThresholdPx).coerceIn(-1f, 1f)
                        imageView.rotation = tiltFraction * 22f

                        // Slight scale-down when dragged far (feels "picked up")
                        val pullScale = 1f - (abs(dragDeltaX) + abs(dragDeltaY.coerceAtLeast(0f))) /
                                (dismissThresholdPx * 6f)
                        val clamped = pullScale.coerceIn(0.85f, 1.05f)
                        imageView.scaleX = clamped
                        imageView.scaleY = clamped
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val dismissHorizontal = abs(dragDeltaX) > dismissThresholdPx
                    val dismissDown       = dragDeltaY > dismissThresholdPx

                    if (!isDragging) {
                        // It was a genuine tap — launch the break selection
                        resumeSwayFromCurrent()
                        performTap()
                    } else if (dismissHorizontal || dismissDown) {
                        // Fly off-screen in the drag direction, then stop the service
                        flyOffScreen(
                            targetX = when {
                                dismissHorizontal -> sign(dragDeltaX) * 2000f
                                else -> dragDeltaX * 0.5f
                            },
                            targetY = if (dismissDown) 2000f else swayTranslationAtDragStart + dragDeltaY * 0.5f,
                            targetRotation = (dragDeltaX / dismissThresholdPx).coerceIn(-1f, 1f) * 35f
                        )
                    } else {
                        // Not enough swipe — spring back to resting position
                        springBack()
                    }
                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    springBack()
                    true
                }

                else -> false
            }
        }

        // ── Root container ─────────────────────────────────────────────────
        // The ImageView sits at the BOTTOM of this tall container.
        // translationY on the ImageView makes it travel within this space.
        rootView = FrameLayout(this)
        val imgParams = FrameLayout.LayoutParams(sizePx, sizePx).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            bottomMargin = marginBottomPx
        }
        rootView.addView(imageView, imgParams)

        // ── Window params (set once, never updated) ───────────────────────────
        // Width/height are MATCH_PARENT so the animal can be dragged in any direction
        // without being clipped by the window boundary. FLAG_NOT_TOUCH_MODAL ensures
        // touches on the transparent empty areas pass through to the app below.
        val layoutParams = WindowManager.LayoutParams().apply {
            width  = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            type   = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE
            flags  = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                     WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                     WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            format  = PixelFormat.TRANSLUCENT
        }

        windowManager.addView(rootView, layoutParams)

        // ── Entry state: animal is off-screen below ────────────────────────
        // translationY is measured from the BOTTOM of the FrameLayout downwards,
        // so a large positive value pushes the view below the container bottom.
        val startTranslationY = (350f * density)
        imageView.translationY = startTranslationY
        imageView.alpha  = 0f
        imageView.scaleX = 0.2f
        imageView.scaleY = 0.2f

        // ── Animator 1: slide up with overshoot bounce ─────────────────────
        val translationAnim = ObjectAnimator.ofFloat(
            imageView, "translationY", startTranslationY, 0f
        ).apply {
            duration    = 950
            interpolator = OvershootInterpolator(1.6f)
        }

        // ── Animator 2: alpha fade-in (first 300 ms only) ─────────────────
        val alphaAnim = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f).apply {
            duration    = 300
            interpolator = LinearInterpolator()
        }

        // ── Animator 3: squash & stretch scale keyframes ───────────────────
        val kf0X = Keyframe.ofFloat(0f,    0.2f)
        val kf1X = Keyframe.ofFloat(0.35f, 0.8f)   // narrow on ascent
        val kf2X = Keyframe.ofFloat(0.75f, 1.2f)   // wide on bounce impact
        val kf3X = Keyframe.ofFloat(1.0f,  1.0f)

        val kf0Y = Keyframe.ofFloat(0f,    0.2f)
        val kf1Y = Keyframe.ofFloat(0.35f, 1.3f)   // tall on ascent
        val kf2Y = Keyframe.ofFloat(0.75f, 0.8f)   // squashed on impact
        val kf3Y = Keyframe.ofFloat(1.0f,  1.0f)

        val scaleAnim = ObjectAnimator.ofPropertyValuesHolder(
            imageView,
            PropertyValuesHolder.ofKeyframe("scaleX", kf0X, kf1X, kf2X, kf3X),
            PropertyValuesHolder.ofKeyframe("scaleY", kf0Y, kf1Y, kf2Y, kf3Y)
        ).apply {
            duration    = 950
            interpolator = DecelerateInterpolator()
        }

        // ── Play all entry animators together ──────────────────────────────
        entryAnimatorSet = AnimatorSet().apply {
            playTogether(translationAnim, alphaAnim, scaleAnim)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    entryAnimatorSet = null
                    // Snap all transforms to exact resting values — guards against
                    // any sub-pixel floating-point drift from the interpolator.
                    imageView.translationY = 0f
                    imageView.translationX = 0f
                    imageView.rotation     = 0f
                    imageView.scaleX       = 1f
                    imageView.scaleY       = 1f
                    imageView.alpha        = 1f
                    // Hand off directly to sway — NO window resize, no layout call.
                    startSwayAnimation()
                }
            })
            start()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Gesture helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Launch break-selection and close the overlay (tap path). */
    private fun performTap() {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            action = MainActivity.ACTION_BREAK_SELECTION
            putExtra(MainActivity.EXTRA_GOTO_BREAK_SELECTION, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(launchIntent)
        stopSelf()
    }

    /**
     * Spring the animal back to its resting position after an incomplete swipe.
     * Resumes the sway animation once the spring settles.
     */
    private fun springBack() {
        val springDuration = 380L
        val springInterp = OvershootInterpolator(1.4f)

        val animX = ObjectAnimator.ofFloat(imageView, "translationX", imageView.translationX, 0f).apply {
            duration     = springDuration
            interpolator = springInterp
        }
        val animY = ObjectAnimator.ofFloat(imageView, "translationY", imageView.translationY, 0f).apply {
            duration     = springDuration
            interpolator = springInterp
        }
        val animR = ObjectAnimator.ofFloat(imageView, "rotation", imageView.rotation, 0f).apply {
            duration     = springDuration
            interpolator = springInterp
        }
        val animSX = ObjectAnimator.ofFloat(imageView, "scaleX", imageView.scaleX, 1f).apply {
            duration = springDuration
        }
        val animSY = ObjectAnimator.ofFloat(imageView, "scaleY", imageView.scaleY, 1f).apply {
            duration = springDuration
        }

        AnimatorSet().apply {
            playTogether(animX, animY, animR, animSX, animSY)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    resumeSwayFromCurrent()
                }
            })
            start()
        }
    }

    /**
     * Fly the animal off-screen in the swipe direction, then stop the service.
     */
    private fun flyOffScreen(targetX: Float, targetY: Float, targetRotation: Float) {
        swayAnimator?.cancel()

        val flyDuration = 260L

        val animX = ObjectAnimator.ofFloat(imageView, "translationX", imageView.translationX, targetX).apply {
            duration     = flyDuration
            interpolator = DecelerateInterpolator(1.4f)
        }
        val animY = ObjectAnimator.ofFloat(imageView, "translationY", imageView.translationY, targetY).apply {
            duration     = flyDuration
            interpolator = DecelerateInterpolator(1.4f)
        }
        val animR = ObjectAnimator.ofFloat(imageView, "rotation", imageView.rotation, targetRotation).apply {
            duration = flyDuration
        }
        val animAlpha = ObjectAnimator.ofFloat(imageView, "alpha", imageView.alpha, 0f).apply {
            duration = flyDuration
        }

        AnimatorSet().apply {
            playTogether(animX, animY, animR, animAlpha)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // User dismissed the break — route the app back to Home if opened
                    NavigationTarget.pendingRoute = Routes.HOME
                    stopSelf()
                }
            })
            start()
        }
    }

    /**
     * Resumes the sway loop smoothly from wherever the animal currently sits.
     * We pick the closest sway target (up or down) to avoid a visual jump.
     */
    private fun resumeSwayFromCurrent() {
        val density = resources.displayMetrics.density
        val swayRange = 14f * density
        val currentY = imageView.translationY

        // Choose whether to swing up or down first based on current position
        val firstTarget = if (currentY <= 0f) -swayRange else 0f
        val secondTarget = if (firstTarget == -swayRange) 0f else -swayRange

        swayAnimator?.cancel()
        swayAnimator = ValueAnimator.ofFloat(firstTarget, secondTarget).apply {
            duration     = 2000
            repeatMode   = ValueAnimator.REVERSE
            repeatCount  = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                imageView.translationY = anim.animatedValue as Float
            }
            start()
        }
    }

    /**
     * Gentle infinite float loop.
     *
     * startDelay: gives the animal a visible resting moment after landing so the
     *   transition feels natural instead of immediately reversing direction.
     *
     * AccelerateDecelerateInterpolator: the very first frame of upward movement
     *   eases in from zero velocity — making the handoff imperceptible.
     */
    private fun startSwayAnimation() {
        val density = resources.displayMetrics.density
        swayAnimator = ValueAnimator.ofFloat(0f, -(14f * density)).apply {
            duration     = 2000
            startDelay   = 350L
            repeatMode   = ValueAnimator.REVERSE
            repeatCount  = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                imageView.translationY = anim.animatedValue as Float
            }
            start()
        }
    }

    override fun onDestroy() {
        entryAnimatorSet?.cancel()
        swayAnimator?.cancel()
        if (::rootView.isInitialized && rootView.parent != null) {
            windowManager.removeView(rootView)
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
