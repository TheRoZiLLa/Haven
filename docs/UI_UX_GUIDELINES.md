# HAVEN — UI/UX Guidelines

> **This document is the permanent UI/UX standard for the entire HAVEN project.**
> Future AI coding sessions must follow this design language consistently.
> Do not redesign the visual style unless explicitly instructed.

---

## 1. Design Philosophy

HAVEN is a **nature-inspired focus & wellness app** that rewards productive time with a growing virtual forest.

The entire experience should feel:

- **Peaceful** — never stressful
- **Rewarding** — progress feels meaningful
- **Relaxing** — encourages breaks, not burnout
- **Comfortable** — intuitive, no cognitive load
- **Motivating** — gentle nudges, not pressure

The interface should encourage users to take breaks instead of forcing them.
Gamification should feel natural — like tending a garden, not grinding a game.

---

## 2. Visual Identity

### Overall Style

| Attribute | Description |
|---|---|
| Aesthetic | Modern, minimal, premium |
| Tone | Friendly, cozy, calm |
| Inspiration | Nature, forests, organic growth |
| System | Material 3 Expressive |
| UI Pattern | Floating cards, soft surfaces |
| Feel | Game-like polish, app-level usability |

### What HAVEN Is

- Soft, warm, inviting
- Highly polished with attention to detail
- Organic and alive — subtle animations everywhere
- Clean with generous whitespace
- Nature-forward — green is dominant

### What HAVEN Is NOT

- Sharp, angular, corporate
- Dark, heavy, oppressive
- Cluttered or information-dense
- Skeuomorphic or overly realistic
- Harsh or high-contrast

---

## 3. Color System

### Primary Colors

| Token | Name | Hex | Role |
|---|---|---|---|
| `primary` | Forest Green | `#2E7D32` | Primary actions, active states, headers |
| `primaryLight` | Green 400 | `#4CAF50` | Secondary actions, highlights |
| `primaryDark` | Green 900 | `#1B5E20` | Emphasis, high contrast |

### Secondary Colors

| Token | Name | Hex | Role |
|---|---|---|---|
| `secondary` | Leaf Green | `#81C784` | Supporting elements, badges |
| `secondaryLight` | Mint | `#C8E6C9` | Subtle backgrounds, fills |

### Accent Colors

| Token | Name | Hex | Role |
|---|---|---|---|
| `accentYellow` | Soft Yellow | `#FFD54F` | Rewards, achievements, stars |
| `accentOrange` | Warm Orange | `#FFB74D` | Streaks, notifications |
| `accentBlue` | Sky Blue | `#64B5F6` | Information, calm states |
| `accentPurple` | Soft Purple | `#CE93D8` | Special events, milestones |

### Neutral Colors

| Token | Name | Hex | Role |
|---|---|---|---|
| `background` | Warm White | `#FAFAF5` | App background |
| `surface` | Light Gray | `#F5F5F0` | Card surfaces |
| `onSurface` | Dark | `#1B1B1B` | Primary text |
| `onSurfaceVariant` | Medium Gray | `#9E9E9E` | Secondary text, icons |

### Color Rules

1. **Green is always dominant.** Every screen should feel green-forward.
2. **Background must feel bright and calm.** Warm white, never stark white or dark.
3. **Accent colors are supporting only.** Used sparingly for specific semantic meanings.
4. **Avoid saturated reds.** Use soft coral (`#E57373`) for errors instead.
5. **Avoid overly dark interfaces.** Even dark mode (if added) should feel warm.

---

## 4. Typography

### Type Scale

| Style | Size | Weight | Line Height | Usage |
|---|---|---|---|---|
| Display Large | 36sp | Bold (700) | 44sp | Hero numbers, timers |
| Title Large | 24sp | SemiBold (600) | 32sp | Screen titles |
| Title Medium | 20sp | Medium (500) | 28sp | Section headers, card titles |
| Body Large | 16sp | Regular (400) | 24sp | Primary body text |
| Body Medium | 14sp | Regular (400) | 20sp | Secondary body text |
| Label Large | 14sp | Medium (500) | 20sp | Buttons, interactive labels |
| Label Medium | 12sp | Medium (500) | 16sp | Badges, chips, captions |

### Typography Rules

1. Use a clean, modern sans-serif (system default or Inter/Outfit)
2. Large titles with soft hierarchy — don't shout
3. High whitespace between text blocks
4. Never overcrowd a screen with text
5. Maximum 2 font weights per screen (Regular + Medium or SemiBold)

---

## 5. Spacing & Layout

### Spacing Scale

| Token | Value | Usage |
|---|---|---|
| `xs` | 4dp | Inline icon gaps |
| `sm` | 8dp | Tight element spacing |
| `md` | 16dp | Standard content spacing |
| `lg` | 24dp | Section spacing, screen padding |
| `xl` | 32dp | Large section gaps |
| `xxl` | 48dp | Major structural spacing |

### Layout Rules

1. **Screen padding**: 24dp minimum on all sides
2. **Card spacing**: 16dp between cards
3. **Breathing room**: generous whitespace everywhere
4. **Minimal information density**: show less, not more
5. **One primary action per screen**: clear visual hierarchy
6. **Visual hierarchy must be immediately obvious**: user should never wonder "what do I do?"
7. **Content should never feel crowded or overwhelming**

---

## 6. Corner Radius

| Token | Value | Usage |
|---|---|---|
| `sm` | 12dp | Chips, badges, small elements |
| `md` | 16dp | Buttons, input fields |
| `lg` | 24dp | Standard cards |
| `xl` | 32dp | Large cards, bottom sheets |
| `full` | 50% | Circular avatars, progress rings |

### Corner Rules

1. **No sharp edges anywhere.** Minimum radius is 12dp.
2. **Cards always use 24–32dp** radius for the floating feel.
3. **Consistency**: same element type = same radius across the app.

---

## 7. Elevation & Shadows

| Level | Elevation | Usage |
|---|---|---|
| `none` | 0dp | Background surfaces |
| `low` | 2dp | Subtle separation |
| `medium` | 4dp | Floating cards |
| `high` | 8dp | Bottom navigation, FAB, modals |

### Shadow Rules

1. Use soft, diffused shadows — never harsh drop shadows
2. Shadows should feel warm (slightly warm-tinted, not pure black)
3. Cards should appear to gently float above the background
4. No heavy shadows — keep everything light and airy

---

## 8. Component Specifications

### Cards

| Property | Value |
|---|---|
| Corner Radius | 24–32dp |
| Internal Padding | 16–24dp |
| Background | `surface` or `background` |
| Shadow | `medium` elevation |
| Border | None (or 1dp `secondaryLight` if needed) |
| Appearance | Floating, separated from background |

Cards are the primary content container. They should feel like they're gently hovering.

### Buttons

| Property | Primary | Secondary | Text |
|---|---|---|---|
| Corner Radius | 16dp | 16dp | — |
| Min Height | 48dp | 48dp | 48dp |
| H Padding | 24dp | 24dp | 16dp |
| Fill | `primary` | transparent | — |
| Text Color | white | `primary` | `primary` |
| Border | — | 1dp `primary` | — |
| Elevation | 2dp | 0dp | 0dp |

- Large touch targets (minimum 48dp)
- Soft elevation on primary buttons
- Rounded, friendly appearance
- Scale animation on press (95% → 100%)

### Bottom Navigation

| Property | Value |
|---|---|
| Style | Floating (inset from screen edges) |
| Corner Radius | 24dp |
| Background | `background` with subtle blur |
| Max Items | 5 |
| Icon Style | Material Symbols, rounded, 24dp |
| Selection | Animated indicator (pill shape) |
| Elevation | `high` |

The bottom navigation should feel like a premium floating bar, not a standard tab bar.

### Progress Indicators

| Property | Value |
|---|---|
| Style | Circular, organic |
| Color | `primary` to `primaryLight` gradient |
| Track Color | `secondaryLight` |
| Animation | Smooth fill with spring easing |
| Size | 80–120dp for primary displays |

Consider using tree growth as a progress metaphor.

### Segment Controls

| Property | Value |
|---|---|
| Shape | Rounded pill |
| Selection | Animated slide transition |
| Background | `surface` |
| Selected | `primary` with white text |
| Unselected | transparent with `onSurfaceVariant` text |

---

## 9. Iconography

| Property | Value |
|---|---|
| Icon Set | Material Symbols (Rounded) |
| Default Size | 24dp |
| Weight | 400 |
| Grade | 0 |
| Optical Size | 24 |
| Fill | 0 (outlined by default) |

### Icon Rules

1. Always use the **rounded** variant
2. Consistent stroke width across all icons
3. No overly detailed or decorative icons
4. Active state: filled (fill = 1)
5. Inactive state: outlined (fill = 0)
6. Match icon optical size to display size

---

## 10. Motion & Animation

### Animation Specs

| Type | Duration | Easing | Usage |
|---|---|---|---|
| Fade In | 300ms | EaseOut | Screen entry, content appear |
| Fade Out | 200ms | EaseIn | Screen exit, dismiss |
| Slide Up | 400ms | Spring (stiffness: medium) | Bottom sheets, cards entering |
| Slide Horizontal | 350ms | Spring (damping: 0.8) | Screen transitions |
| Scale Press | 200ms | EaseInOut | Button/card press (→ 95%) |
| Scale Release | 150ms | Spring | Button/card release (→ 100%) |
| Float | 2000ms | Sine loop | Idle card breathing |
| Tree Grow | 800ms | Spring (damping: 0.6) | Progress, tree growth |
| Counter | 500ms | EaseOut | Number counting up |

### Motion Principles

1. **Smooth and relaxing** — no abrupt movements
2. **Spring animations preferred** — they feel natural and alive
3. **Every interactive element should respond** — micro-interactions everywhere
4. **Stagger content appearance** — cards/items enter sequentially, not all at once
5. **Idle animations** — subtle floating/breathing on key elements
6. **Transitions should feel like gentle flowing** — not snapping

### Avoid

- Abrupt transitions
- Linear easing (feels robotic)
- Fast, jarring animations
- Overshoot on navigation transitions
- Animation on EVERY element (be selective)

---

## 11. Illustration & Graphics

### Style

- **Nature-themed**: forests, plants, trees, animals, sky
- **Soft gradients**: smooth color transitions
- **Rounded shapes**: friendly, organic forms
- **Minimal geometric**: clean but not sterile
- **Flat with depth**: subtle shadows and layering, not skeuomorphic

### Forest Theme

The forest is the central visual metaphor. It communicates growth and life.

| Element | Behavior |
|---|---|
| Trees | Grow over time, sway gently |
| Animals | Appear at milestones, idle animations |
| Wind | Subtle particle effects |
| Grass | Gentle wave motion |
| Leaves | Occasional falling particles |
| Sky | Soft gradient, time-of-day aware |

The forest world should feel **calm, not busy**. Animations are ambient, not demanding attention.

---

## 12. Interaction Principles

### Core UX Rules

1. **One obvious action per screen** — the user should never wonder what to do
2. **Reduce cognitive load** — simplify decisions, reduce options
3. **Large touch areas** — minimum 48dp touch target
4. **Instant feedback** — every tap/interaction responds immediately
5. **Delightful micro-interactions** — reward interaction with subtle animations
6. **Avoid unnecessary dialogs** — use inline actions instead
7. **Avoid long forms** — collect information progressively
8. **Forgiving UI** — easy to undo, hard to make mistakes

### Emotional Design

The app should make users feel:

| Moment | Feeling |
|---|---|
| Opening the app | Calm, welcomed |
| Starting a timer | Motivated, focused |
| Completing a session | Proud, rewarded |
| Viewing their forest | Accomplished, peaceful |
| Taking a break | Guilt-free, refreshed |

**The interface should never create anxiety or pressure.**

---

## 13. Screen Patterns

### Home Screen

- Warm greeting
- Current forest preview (compact)
- Quick-start timer button (prominent)
- Today's progress summary
- Minimal — max 3–4 content blocks

### Timer Screen

- Large, central timer display
- Growing tree animation during focus
- Minimal UI — reduce distractions
- Gentle completion animation
- Easy pause/stop controls

### Dashboard

- Floating stat cards
- Visual progress (charts, trees)
- Weekly/monthly views
- Achievement highlights
- Scrollable but not overwhelming

### Forest View

- Full Unity rendering
- Immersive, minimal Chrome
- Subtle UI overlays only
- Touch to interact with elements

### Profile / Settings

- Simple list layout
- Grouped in floating cards
- Toggle-based preferences
- Clean, no clutter

---

## 14. Responsive Behavior

- Design for phone-first (portrait)
- Cards should reflow naturally
- Touch targets remain accessible at all sizes
- Bottom navigation stays anchored
- Timer display scales gracefully

---

## 15. Accessibility

- Minimum contrast ratio: 4.5:1 for text
- Touch targets: 48dp minimum
- Support dynamic font scaling
- Meaningful content descriptions
- Respect system accessibility settings

---

## Appendix: Quick Reference

### Do

✅ Use rounded corners (24–32dp for cards)
✅ Use soft shadows
✅ Use generous spacing
✅ Use spring animations
✅ Use green as dominant color
✅ Use floating card patterns
✅ Use micro-interactions
✅ Keep screens calm and uncluttered

### Don't

❌ Sharp edges or corners
❌ Heavy/dark shadows
❌ Cluttered layouts
❌ Saturated reds
❌ Dark heavy interfaces
❌ Skeuomorphic elements
❌ Information overload
❌ Abrupt transitions
❌ Small touch targets
❌ Anxiety-inducing UI patterns
