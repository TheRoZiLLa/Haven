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
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.haven.app.MainActivity
import com.haven.app.R

/**
 * Service that displays the cozy animal overlay floating above all other applications.
 * Features an entry bounce animation from the deep bottom to its resting bottom position
 * with squash-and-stretch scaling, followed by a continuous floating sway animation.
 *
 * Key design: the rootView is NEVER removed from WindowManager during the entry→sway
 * transition. Instead, windowManager.updateViewLayout() resizes the window in-place,
 * keeping the ImageView pixel-perfect on screen with zero flicker.
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

        imageView.setOnClickListener {
            val launchIntent = Intent(this, MainActivity::class.java).apply {
                action = MainActivity.ACTION_BREAK_SELECTION
                putExtra(MainActivity.EXTRA_GOTO_BREAK_SELECTION, true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(launchIntent)
            stopSelf()
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
        val layoutParams = WindowManager.LayoutParams().apply {
            width  = sizePx
            height = entryHeightPx
            type   = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE
            flags  = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                     WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            format  = PixelFormat.TRANSLUCENT
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            y = 0 // anchor window to very bottom edge
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
