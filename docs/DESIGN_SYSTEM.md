# HAVEN — Design System

This document defines the design tokens and component specifications for the Haven Android application.

All UI must follow this system for visual consistency.

---

## Color Palette

### Primary
| Token | Hex | Usage |
|---|---|---|
| `forest_green` | `#2E7D32` | Primary actions, headers, active states |
| `forest_green_light` | `#4CAF50` | Secondary actions, highlights |
| `forest_green_dark` | `#1B5E20` | Emphasis, contrast |

### Secondary
| Token | Hex | Usage |
|---|---|---|
| `leaf_green` | `#81C784` | Supporting elements, badges |
| `mint` | `#C8E6C9` | Backgrounds, subtle fills |

### Accent
| Token | Hex | Usage |
|---|---|---|
| `soft_yellow` | `#FFD54F` | Rewards, achievements |
| `warm_orange` | `#FFB74D` | Notifications, streaks |
| `sky_blue` | `#64B5F6` | Info, calm states |
| `soft_purple` | `#CE93D8` | Special events, milestones |

### Neutral
| Token | Hex | Usage |
|---|---|---|
| `warm_white` | `#FAFAF5` | Primary background |
| `light_gray` | `#F5F5F0` | Card backgrounds |
| `medium_gray` | `#9E9E9E` | Secondary text |
| `dark_text` | `#1B1B1B` | Primary text |

### Semantic
| Token | Hex | Usage |
|---|---|---|
| `success` | `#4CAF50` | Completed states |
| `warning` | `#FFB74D` | Caution states |
| `info` | `#64B5F6` | Informational |
| `error` | `#E57373` | Soft error (not harsh red) |

> **Rule**: Green is always the dominant color. Other colors are supporting accents only.

---

## Typography

| Style | Size | Weight | Usage |
|---|---|---|---|
| Display Large | 36sp | Bold | Screen titles |
| Title Large | 24sp | SemiBold | Section headers |
| Title Medium | 20sp | Medium | Card titles |
| Body Large | 16sp | Regular | Primary body text |
| Body Medium | 14sp | Regular | Secondary text |
| Label Large | 14sp | Medium | Buttons |
| Label Medium | 12sp | Medium | Badges, chips |

- Use Material 3 default font (or a clean sans-serif like Inter/Outfit)
- High whitespace between sections
- Never overcrowd screens

---

## Spacing

| Token | Value | Usage |
|---|---|---|
| `xs` | 4dp | Inline spacing |
| `sm` | 8dp | Tight spacing |
| `md` | 16dp | Standard spacing |
| `lg` | 24dp | Section spacing |
| `xl` | 32dp | Large gaps |
| `xxl` | 48dp | Screen padding |

---

## Corner Radius

| Token | Value | Usage |
|---|---|---|
| `sm` | 12dp | Small chips, badges |
| `md` | 16dp | Buttons |
| `lg` | 24dp | Cards |
| `xl` | 32dp | Large cards, bottom sheet |
| `full` | 50% | Circular elements |

> **Rule**: No sharp edges anywhere. Minimum radius is 12dp.

---

## Elevation & Shadows

| Level | Elevation | Usage |
|---|---|---|
| `none` | 0dp | Flat elements |
| `low` | 2dp | Subtle cards |
| `medium` | 4dp | Floating cards |
| `high` | 8dp | Bottom nav, FAB |

- Use soft, diffused shadows only
- No harsh drop shadows
- Shadows should feel warm (slightly warm-tinted, not pure black)

---

## Components

### Cards
- Corner radius: 24–32dp
- Padding: 16–24dp internal
- Background: `light_gray` or `warm_white`
- Shadow: `medium` elevation
- Appearance: floating, separated from background

### Buttons
- Corner radius: 16dp
- Min height: 48dp
- Padding: 16dp horizontal, 12dp vertical
- Primary: filled with `forest_green`
- Secondary: outlined or tonal
- Soft elevation on press

### Bottom Navigation
- Floating style (inset from edges)
- Corner radius: 24dp
- Background: `warm_white` with blur/frosted effect
- Max 5 items
- Small, rounded Material Symbols icons
- Animated selection indicator

### Progress Indicators
- Circular, organic style
- Animated fill
- Forest green color
- Optional: tree growth metaphor

### Segment Controls
- Rounded pill shape
- Animated selection with slide transition
- Soft background contrast

---

## Iconography

- Use **Material Symbols** (rounded variant)
- Consistent stroke width
- Optical size: 24dp default
- Weight: 400
- No overly detailed or filled icons

---

## Motion

| Animation | Duration | Curve | Usage |
|---|---|---|---|
| Fade in | 300ms | EaseOut | Screen transitions |
| Slide up | 400ms | Spring | Bottom sheets, cards |
| Scale | 200ms | EaseInOut | Button press |
| Float | 2000ms | Sine loop | Idle card animation |
| Grow | 800ms | Spring | Tree/progress growth |

- All transitions should feel smooth and relaxing
- Use spring animations where possible
- Avoid abrupt or jarring transitions
- Micro-interactions on every interactive element

---

## Layout Principles

- Screen padding: 24dp minimum
- Card spacing: 16dp between cards
- One primary action per screen
- Visual hierarchy must be immediately obvious
- Generous whitespace — breathing room everywhere
- Minimal information density
- Content should never feel crowded
