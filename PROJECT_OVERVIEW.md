# HAVEN — Project Overview

> **Version**: Prototype (v0.1.0-alpha)  
> **Target Platforms**: Android (Kotlin/Jetpack Compose), embedded Unity 6 (URP)  
> **Core Concept**: Gamified Eye-Care and Focus Wellness Application

---

## 1. Executive Summary & Vision

**HAVEN** is a gamified focus and eye-care application designed to help users establish a healthy relationship with their screens. Unlike traditional, anxiety-inducing productivity tools and basic timers, HAVEN leverages relaxing, cozy game mechanics to encourage regular breaks. 

The central design philosophy is **"Cozy Wellness over Stressful Productivity."** Instead of punishing users for failing a focus session, HAVEN rewards them for successfully resting their eyes and taking consistent breaks by growing a beautiful, living 3D forest. The primary target is to answer a single product question: **"Will users enjoy taking breaks if those breaks directly contribute to growing a peaceful, interactive forest?"**

---

## 2. System Architecture

The HAVEN ecosystem is divided into two distinct repositories to maintain a clean separation of concerns:
1. **`Haven` (Android Host)**: Manages app lifecycle, navigation, user data, timers, notifications, and settings.
2. **`Haven-Game` (Unity Client)**: Manages 3D rendering, tree growth animation, animal AI, isometric world simulation, and cozy interaction.

### 2.1 Communication & Integration Model

```
┌─────────────────────────────────────────────────────────┐
│                     Android Host Application            │
│               (Kotlin / Jetpack Compose / M3)           │
│                                                         │
│  ┌───────────────────────────────────────────────────┐  │
│  │ Navigation Host (NavHost)                         │  │
│  │                                                   │  │
│  │  ┌──────────────┐   ┌──────────────┐  ┌────────┐  │  │
│  │  │ Home Screen  │   │ Dashboard    │  │ Profile│  │  │
│  │  └──────────────┘   └──────────────┘  └────────┘  │  │
│  │  ┌──────────────┐   ┌──────────────┐  ┌────────┐  │  │
│  │  │ Timer Screen │   │ Settings     │  │ Break  │  │  │
│  │  └──────────────┘   └──────────────┘  └────────┘  │  │
│  │                                                   │  │
│  │  ┌─────────────────────────────────────────────┐  │  │
│  │  │ Forest View Screen (Unity Container View)   │  │  │
│  │  │                                             │  │  │
│  │  │   ┌─────────────────────────────────────┐   │  │  │
│  │  │   │          Unity as a Library         │   │  │  │
│  │  │   │   - 3D Forest Render (URP)          │   │  │  │
│  │  │   │   - Tree Growth System              │   │  │  │
│  │  │   │   - Active Animal AI                │   │  │  │
│  │  │   │   - Isometric World Simulation      │   │  │  │
│  │  │   └─────────────────────────────────────┘   │  │  │
│  │  └─────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### 2.2 Repository Responsibilities

| Role / Responsibility | Host App (`Haven` - Android) | Game Client (`Haven-Game` - Unity) |
|---|---|---|
| **User Interface** | Primary (All application screens, settings, overlays, navigation) | Embedded inside the container (Overlay UI for game settings only) |
| **Logic & State** | Timer counters, session storage, dashboard records, settings values | Tree state rendering, active animation cycles, animal movement |
| **Graphics & Rendering** | Jetpack Compose / Material 3 elements | Unity Universal Render Pipeline (URP), 3D Meshes, Shaders |
| **Data Persistence** | SharedPreferences / DataStore (session stats, preferences) | Stateless during gameplay (reacts to parameters passed by Android) |
| **Audio** | App sounds / ambient controls | Game ambient soundtrack, tree rustle/growth audio effects |

### 2.3 Integration Strategy (Future)
Unity is integrated directly into the Android application codebase as a library (UaaL - Unity as a Library).
* **Android-to-Unity Communication**: Android sends commands (e.g., `PlantTree(type)`, `StartGrowth(duration)`, `SetTimeOfDay(hour)`) using `UnityPlayer.UnitySendMessage()`.
* **Unity-to-Android Communication**: Unity triggers events (e.g., `TreeGrowthComplete`, `AnimalInteracted`) back to the host via a custom Java/Kotlin interface bridge.
* **Status**: **Integration is deferred.** The prototype code keeps these boundaries completely clean. Do not wire up the bridge until explicitly requested.

---

## 3. Host Application: Android (`Haven`)

* **Repository Location**: `d:\Haven`
* **Language**: Kotlin 2.4.10
* **Build System**: Android Gradle Plugin (AGP) 9.2.1, Gradle 9.4.1 (Kotlin DSL)
* **SDK Compatibility**: compileSdk 37, minSdk 26, targetSdk 37, Java 11 (JDK 21 for daemon)

### 3.1 Directory Structure
```
d:\Haven\app\src\main\java\com\haven\app\
├── MainActivity.kt          # Host Activity setting up Compose layout and edges
├── core/
│   ├── ui/                  # Reusable, design-system-compliant Composable widgets (e.g. HavenCard, HavenButton)
│   ├── common/              # Shared data constants, data models (e.g. Session, SeedType)
│   └── util/                # Extension functions, notification helpers, timer managers
├── feature/
│   ├── home/                # Welcome screen, daily status widgets, quick focus start
│   ├── dashboard/           # Weekly/monthly progress statistics, bar/doughnut growth charts
│   ├── timer/               # Focused time ticking interface, interactive growing animations
│   ├── forest/              # The Android viewport container hosting the embedded Unity activity
│   ├── profile/             # User milestones, level progress, settings shortcut
│   └── settings/            # Notification switches, break interval timings, account management
├── navigation/              # Navigation host graphs, string-based route management
└── ui/
    └── theme/               # Material 3 theme configuration (Color, Type, Theme tokens)
```

### 3.2 Key Dependencies
* **Jetpack Compose BOM (2026.06.01)**: Version-aligns Compose libraries (UI, Material 3, Tooling).
* **Navigation Compose (2.9.8)**: Handles screen routing and transitions.
* **Lifecycle Runtime & ViewModel (2.11.0)**: Supports architecture separation by storing UI state.
* **Coil 3 (3.5.0)**: Handles asynchronous image caching and loading for assets.

---

## 4. Game Client: Unity (`Haven-Game`)

* **Repository Location**: `d:\Unity Project\Haven-Game`
* **Engine version**: Unity 6000.3.12f1
* **Rendering Pipeline**: Universal Render Pipeline (URP) 17.3.0
* **Camera Style**: Fixed-angle Orthographic / Isometric Projection

### 4.1 Asset Structure
```
d:\Unity Project\Haven-Game\Assets\
├── Art/                     # Texture maps, materials, environment assets
├── Animations/              # Tree growing transitions, wind sway cycles, animal behaviors
├── Audio/                   # Nature-inspired ambient loop files, button tap tones
├── Materials/               # Custom URP shaders and material mappings
├── Models/                  # 3D low-poly models (trees, plants, stones, cozy animals)
├── Plugins/                 # Android support files for the Unity-Android library bridge
├── Prefabs/                 # Preconfigured GameObjects for spawning (e.g., PineTree, OakTree, Fox, Deer)
├── Resources/               # Dynamically loaded runtime configurations
├── Scenes/                  # Main scene definition files (e.g., MainForest.unity)
├── Scripts/
│   ├── Runtime/             # Game cycle managers, UaaL messaging, initialization scripts
│   ├── Editor/              # Developer helper tools, custom inspectors
│   └── Systems/
│       ├── Tree/            # Tree spawning, age management, growth animations
│       ├── Forest/          # Tile/Grid system, soil status, forest rendering
│       ├── Animals/         # Animal spawning, pathfinding (AI Navigation), idle behaviors
│       ├── Camera/          # Drag-to-pan limits, zoom controls (Cinemachine)
│       └── UI/              # Light developer-only overlay UI for testing gameplay
└── Settings/                # Project rendering profiles and URP setups
```

### 4.2 Key Packages & Features
* **Universal Render Pipeline (URP)**: Configured for performance-optimized, low-poly mobile rendering.
* **Cinemachine (3.1.7)**: Handles isometric camera framing, smooth target tracking, and pan limits.
* **AI Navigation (2.0.11)**: Manages navmesh data dynamically as tiles and trees expand, allowing animals to navigate the forest cleanly.
* **Visual Effect Graph & Timeline (17.3.0 / 1.8.11)**: Powers weather effects (ambient fog, falling leaves) and cinematic sequences during milestones.
* **glTFast (6.19.0)**: Enables fast runtime loading of glTF/GLB models if user customization assets are retrieved dynamically.

---

## 5. Core Gameplay & Focus Loop

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│ 1. Choose Seed  │ ────> │ 2. Choose Time  │ ────> │ 3. Device Use   │
│ Select tree type│       │ Select focus min│       │ Start focus mod │
└─────────────────┘       └─────────────────┘       └─────────────────┘
                                                             │
                                                             ▼
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│ 6. Tree Spawns  │ <──── │ 5. Take Break   │ <──── │ 4. Focus Comp.  │
│ 3D Forest update│       │ Complete eye-ex │       │ App notifications│
└─────────────────┘       └─────────────────┘       └─────────────────┘
```

1. **Select Seed**: The user chooses a seed from their collection (e.g., Pine, Birch, Oak). Different seeds require different focus times and provide different forest vibes.
2. **Select Focus Time**: The user sets a timer (typically 20 to 60 minutes).
3. **Focus Mode**: The user works on their tasks. The Android app tracks focus time. If the user exits the app, the focus session fails.
4. **Focus Completion**: Once the timer ends, the app rings/notifies the user.
5. **Take an Eye-Care Break**: Before receiving the reward, the user is encouraged to complete a 5-minute break activity (e.g., a "20-20-20 rule" exercise: look at something 20 feet away for 20 seconds).
6. **Reward and Growth**: On successful break completion, the tree matures. Android instructs Unity to permanently add this tree to the user's 3D forest scene.
7. **Forest Population**: As the user builds a streak, new tile blocks unlock and cozy animals (e.g., deer, rabbits, foxes) begin wandering in, reinforcing positive behavior.

---

## 6. Design System & Visual Guidelines

HAVEN uses a unified visual identity designed to feel cozy, inviting, and premium. The core styling tokens must be strictly adhered to across both Android layouts and Unity designs.

### 6.1 Color Palette

```
   FOREST GREEN         LEAF GREEN           SOFT YELLOW          WARM WHITE
  #2E7D32 (Primary)   #81C784 (Secondary)   #FFD54F (Accent)    #FAFAF5 (Neutral)
 ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
 │                 │ │                 │ │                 │ │                 │
 └─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘
```

* **Primary**: `forest_green` (`#2E7D32`) for active states, headers, primary buttons.
* **Secondary**: `leaf_green` (`#81C784`) for supporting badges and interactive accents.
* **Accent**: `soft_yellow` (`#FFD54F`) for stars, streak rewards; `warm_orange` (`#FFB74D`) for warnings; `sky_blue` (`#64B5F6`) for info.
* **Neutral**: `warm_white` (`#FAFAF5`) for app background; `light_gray` (`#F5F5F0`) for floating card backgrounds; `dark_text` (`#1B1B1B`) for body texts.
* **Semantic Error**: Soft coral (`#E57373`) instead of jarring red.
* **Rule**: **Green is always the dominant color.** Screens should feel bright, airy, and warm. Avoid dark themes unless explicitly requested.

### 6.2 Layout, Spacing, and Corner Radii
* **Layout Rule**: Screen padding must be a **minimum of 24dp**. One primary action per screen. Whitespace is active content.
* **Spacing Scale**: `sm` = 8dp, `md` = 16dp, `lg` = 24dp, `xl` = 32dp.
* **Corner Radius Rule**: **No sharp edges anywhere.** 
  * Buttons and input fields: `16dp`
  * Standard cards: `24dp`
  * Large bottom sheets and floating cards: `32dp`
  * Min corner radius: `12dp` (for badges/chips)

### 6.3 Motion, Elevation, and Depth
* **Floating Cards**: Cards must hover over the warm-white background using `medium` elevation (`4dp`) with warm-tinted, diffused shadows.
* **Transitions**: EaseOut curves for entry (300ms), Spring physics for bottom sheets (400ms), and custom slow-cycle Sine loops for idle floating card animations.
* **Scale-Feedback**: Buttons shrink slightly on press (scale to 95% over 200ms) and spring back on release (100% over 150ms).

---

## 7. Development Roadmap & Implementation Tasks

### 7.1 Android Work (Host App)
- [ ] **Navigation Setup**: Build the base `NavHost` in [MainActivity.kt](file:///d:/Haven/app/src/main/java/com/haven/app/MainActivity.kt) using Jetpack Navigation.
- [ ] **Feature Views**: Implement simple, beautiful Compose screens under the `feature/` packages.
- [ ] **Design Theme**: Apply colors, shapes, typography defined in [Theme.kt](file:///d:/Haven/app/src/main/java/com/haven/app/ui/theme/Theme.kt) and [Color.kt](file:///d:/Haven/app/src/main/java/com/haven/app/ui/theme/Color.kt).
- [ ] **Timer System**: Wire up countdown timer functionality with notifications.
- [ ] **Dashboard Stats**: Define in-memory session statistics storage to power the dashboard charts.

### 7.2 Unity Work (Game Client)
- [ ] **3D Environment**: Create a cozy forest environment in a test scene (e.g., [MainForest.unity](file:///d:/Unity Project/Haven-Game/Assets/Scenes/)).
- [ ] **Low-Poly Assets**: Import trees, foliage, and animal models.
- [ ] **Tree Grow Script**: Write a script in `Systems/Tree/` that handles tree spawning and uses Tweening (or procedural scaling) to animate growth.
- [ ] **Animal AI**: Set up NavMesh pathfinding and simple idle roaming behaviors for spawned animals.
- [ ] **Camera Controls**: Connect Cinemachine with standard mobile swipe-to-pan and pinch-to-zoom gestures.

### 7.3 Integration Work (Embedded Player)
- [ ] **Export Unity Project**: Build the Unity game project as an Android archive `.aar` library.
- [ ] **Import into Android Host**: Configure Gradle dependencies to include the compiled game module in the `:app` build directory.
- [ ] **Unity View Wrapper**: Build a custom `AndroidView` class in the `feature/forest/` directory to embed and control the Unity player view directly.
- [ ] **Message Bridge**: Construct the `UnityPlayer` messenger interface to synchronize focus session events.
