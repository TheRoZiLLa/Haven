# HAVEN — Architecture

## Overview

HAVEN consists of two projects:

| Project | Tech | Role |
|---|---|---|
| **Haven** (Android) | Kotlin, Jetpack Compose, Material 3 | Host application — UI, navigation, timer, dashboard, settings |
| **Haven-Game** (Unity) | Unity 6 (URP) | Forest view — rendering, gameplay, animations |

## Communication Model

```
┌──────────────────────────┐
│      Android App         │
│  (Kotlin / Compose)      │
│                          │
│  ┌────────────────────┐  │
│  │  Navigation Host   │  │
│  │                    │  │
│  │  Home              │  │
│  │  Dashboard         │  │
│  │  Timer             │  │
│  │  Profile           │  │
│  │  Settings          │  │
│  │                    │  │
│  │  ┌──────────────┐  │  │
│  │  │ Forest View  │  │  │
│  │  │ (Unity)      │  │  │
│  │  └──────────────┘  │  │
│  └────────────────────┘  │
└──────────────────────────┘
```

## Android Responsibilities

- All UI screens (Jetpack Compose)
- Navigation (Navigation Compose)
- Timer logic
- Dashboard data
- User settings
- Future: hosting Unity as a Library

## Unity Responsibilities

- Forest 3D rendering (URP)
- Tree growth animations
- Animal behaviors
- Camera system
- Visual effects
- Future: communicating with Android host via Unity as a Library bridge

## Android Package Structure

```
com.haven.app/
├── MainActivity.kt
├── core/
│   ├── ui/          → Shared composables (buttons, cards, etc.)
│   ├── common/      → Shared models, constants
│   └── util/        → Extension functions, helpers
├── feature/
│   ├── home/        → Home / landing screen
│   ├── dashboard/   → Dashboard screen
│   ├── timer/       → Timer feature
│   ├── forest/      → Forest view (Unity bridge)
│   ├── profile/     → User profile
│   └── settings/    → App settings
├── navigation/      → NavHost, routes, graph
└── ui/
    └── theme/       → Material 3 theme (Color, Type, Theme)
```

## Unity Asset Structure

```
Assets/
├── Art/
├── Animations/
├── Audio/
├── Materials/
├── Models/
├── Prefabs/
├── Scenes/
├── Scripts/
│   ├── Runtime/
│   ├── Editor/
│   └── Systems/
│       ├── Tree/
│       ├── Forest/
│       ├── Animals/
│       ├── Camera/
│       └── UI/
├── Resources/
└── Addressables/
```

## Integration (Future)

Unity will be embedded in the Android app via **Unity as a Library**.

- Android sends commands to Unity (e.g., plant tree, trigger animation)
- Unity sends events back to Android (e.g., animation complete, user interaction)
- Communication via `UnityPlayer` messaging bridge

**Do not implement integration until explicitly instructed.**

## Key Decisions

| Decision | Rationale |
|---|---|
| Single `:app` module | 2-day prototype — multi-module is overkill |
| No DI framework | Manual construction is sufficient for prototype scope |
| No Room/database | DataStore or in-memory state is enough for now |
| No backend/API | Fully offline prototype |
| String-based nav routes | Avoids Kotlin Serialization dependency until needed |
| URP (not HDRP) | Mobile-first rendering with quality tiers |
