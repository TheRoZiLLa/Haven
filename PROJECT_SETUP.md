# HAVEN — Project Setup

## Repositories

| Project | Path | Tech |
|---|---|---|
| Haven (Android) | `d:\Haven` | Kotlin 2.4.10, Compose BOM 2026.06.01, Material 3 |
| Haven-Game (Unity) | `d:\Unity Project\Haven-Game` | Unity 6000.3.12f1, URP 17.3.0 |

## Android Dependencies

| Library | Version | Purpose |
|---|---|---|
| AGP | 9.2.1 | Android Gradle Plugin |
| Kotlin | 2.4.10 | Language |
| Gradle | 9.4.1 | Build system |
| Compose BOM | 2026.06.01 | Compose version alignment |
| Material 3 | BOM-managed | Design system |
| Navigation Compose | 2.9.8 | Screen navigation |
| Lifecycle Runtime | 2.11.0 | Lifecycle-aware state |
| Lifecycle ViewModel | 2.11.0 | ViewModel + Compose |
| Activity Compose | 1.13.0 | Activity integration |
| Core KTX | 1.19.0 | Kotlin extensions |
| Coil 3 | 3.5.0 | Image loading |
| Material Icons Extended | BOM-managed | Full icon set |

## Android SDK

| Setting | Value |
|---|---|
| compileSdk | 37 |
| minSdk | 26 |
| targetSdk | 37 |
| Java | 11 |
| JDK (Gradle daemon) | 21 |

## Unity Packages

| Package | Version |
|---|---|
| Universal Render Pipeline | 17.3.0 |
| Input System | 1.19.0 |
| Cinemachine | 3.1.7 |
| ProBuilder | 6.1.2 |
| AI Navigation | 2.0.11 |
| Visual Effect Graph | 17.3.0 |
| Timeline | 1.8.11 |
| glTFast | 6.19.0 |
| Unity MCP | Git (main) |

## Folder Structure

See [ARCHITECTURE.md](docs/ARCHITECTURE.md) for complete folder structure.

## Deferred (Add When Needed)

| Library | When to add |
|---|---|
| Kotlin Serialization | When type-safe navigation routes are needed |
| DataStore | When persistent storage is needed |
| Hilt / Koin | When DI complexity warrants it |
| Room | When structured data storage is needed |
| Firebase | When cloud features are needed |

## Remaining TODOs

- [ ] Wire up Navigation Compose host in MainActivity
- [ ] Create screen composables for each feature
- [ ] Implement Material 3 theme with Haven color palette
- [ ] Create ForestScene in Unity
- [ ] Set up Unity as a Library integration
- [ ] Implement timer logic
- [ ] Design dashboard layout
- [ ] Create forest 3D environment
