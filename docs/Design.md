# GameCenter — Architecture & Design Document

## 1. System Overview

GameCenter is an Android TV game store built with Jetpack Compose and a layered MVVM architecture. The app runs as a system-signed application with elevated privileges for silent APK management.

```
┌──────────────────────────────────────────────────┐
│                    UI Layer                       │
│   Jetpack Compose + TV Material + Navigation      │
├──────────────────────────────────────────────────┤
│                ViewModel Layer                    │
│   @HiltViewModel + StateFlow + SharedFlow         │
├──────────────────────────────────────────────────┤
│             Domain / Feature Layer                │
│   DownloadManager, InstallManager,                │
│   SettingsManager, NetworkMonitor                 │
├──────────────────────────────────────────────────┤
│                  Data Layer                       │
│   AppApi (Retrofit), Room, DataStore              │
├──────────────────────────────────────────────────┤
│           Dependency Injection (Hilt)             │
└──────────────────────────────────────────────────┘
```

---

## 2. Module Design

### 2.1 Data Layer

Responsible for all data access — remote API, local database, and preferences.

```
data/
├── remote/
│   ├── ApiService.kt              → Retrofit interface
│   ├── AppApiImpl.kt              → AppApi implementation (remote)
│   ├── ApiResponse.kt             → Generic API response wrapper
│   ├── ApiException.kt            → API error handling
│   ├── interceptor/
│   │   └── RetryInterceptor.java  → OkHttp retry interceptor
│   └── request/
│       └── Token.kt               → Auth token model
├── local/
│   ├── LocalAppApiImpl.kt         → AppApi implementation (preview/test)
│   └── LocalDataProvider.kt       → Mock data provider
├── repository/
│   └── GameRepository.kt          → Repository wrapping AppApi
├── di/
│   └── DataModule.kt              → Hilt module: AppApi binding
└── model/
    ├── ContentConfig.kt           → Home page config model
    ├── Component.kt               → UI component model
    ├── Resource.kt                → Game resource model
    ├── Info.kt                    → Game detail model
    ├── Search2.kt                 → Search result model
    └── InfoParam.kt               → Request parameter model
```

Key design decisions:
- `AppApi` interface abstracts remote vs local data sources
- `AppApiImpl` uses Retrofit 3.0 + OkHttp 5.1 with coroutine support
- `LocalAppApiImpl` provides mock data for Compose previews and testing
- `RetryInterceptor` handles transient network failures

### 2.2 Database Layer

Room database for persistent local storage.

```
db/
├── AppDatabase.kt                 → Room database definition
├── di/
│   └── DatabaseModule.kt          → Hilt module: DB + DAOs
└── playrecord/
    ├── PlayRecord.kt              → Play history entity
    └── PlayRecordDao.kt           → Play history DAO
```

Download items are stored separately by the download engine's own persistence layer (`IDownloadListDb`, `IDownloadPartListDb`).

### 2.3 Feature Layer

Self-contained feature modules with their own DI.

#### Download Engine

The most complex module — a custom multi-threaded chunked download engine.

```
feature/downloader/
├── DownloadManager.kt             → Central download orchestrator
├── DownloadJob.kt                 → Single download task
├── PartDownloader.kt              → Chunk downloader (per-part)
├── Part.kt                        → Download chunk metadata
├── DownloadDestination.kt         → File write target
├── DownloadMonitor.kt             → Global download state observer
├── DownloadSettings.kt            → Download configuration
├── Throttler.kt                   → Global bandwidth limiter
├── connection/
│   ├── Connection.kt              → Connection abstraction
│   ├── DownloaderClient.kt        → Client interface
│   └── OkHttpDownloaderClient.kt  → OkHttp implementation
├── db/
│   ├── IDownloadListDb.kt         → Task persistence interface
│   └── IDownloadPartListDb.kt     → Chunk persistence interface
└── di/
    └── DownloaderModule.kt        → Hilt module
```

Capabilities:
- Multi-threaded chunked download with breakpoint resume
- Max 5 concurrent tasks (oldest auto-paused on overflow)
- Global bandwidth throttling
- Duplicate file policy: overwrite / rename / abort
- Auto-refresh expired download URLs
- Disk space pre-check (50MB reserve)
- Sparse file allocation
- OkHttp connection pool with no concurrency limit

#### Install Manager

```
feature/installer/
├── InstallManager.kt              → Silent install/uninstall via PackageInstaller
├── InstallReceiver.kt             → Broadcast receiver for install events
├── InstallEvents.kt               → SharedFlow event bus
└── di/
    └── InstallModule.kt           → Hilt module
```

Uses `android.uid.system` shared UID for silent operations without user confirmation.

#### Network Monitor

```
feature/network/
├── NetworkMonitor.kt                        → Interface
├── ConnectivityManagerNetworkMonitor.kt      → Implementation
└── di/
    └── NetworkModule.kt                     → Hilt module
```

#### Settings Manager

```
feature/settings/
├── SettingsManager.kt             → DataStore Preferences wrapper
└── di/
    └── SettingsModule.kt          → Hilt module
```

### 2.4 UI Layer

Each screen follows the pattern: `Screen.kt` + `ViewModel.kt` + `Navigation.kt`.

```
ui/
├── GcApp.kt                      → Root composable, NavHost, scaffold
├── GcAppState.kt                 → Minimal app state (nav, snackbar, offline)
├── home/
│   ├── HomeScreen.kt
│   ├── HomeViewModel.kt          → @HiltViewModel
│   └── HomeNavigation.kt
├── gamedetails/
│   ├── GameDetailsScreen.kt
│   ├── GameDetailsViewModel.kt   → @HiltViewModel + SavedStateHandle
│   └── GameDetailsNavigation.kt
├── search/
│   ├── SearchScreen.kt
│   ├── SearchViewModel.kt        → @HiltViewModel
│   └── SearchNavigation.kt
├── downloader/
│   ├── DownloaderScreen.kt
│   ├── DownloaderViewModel.kt    → @HiltViewModel
│   └── DownloaderNavigation.kt
├── uninstaller/
│   ├── UninstallerScreen.kt
│   ├── UninstallerViewModel.kt   → @HiltViewModel
│   └── UninstallerNavigation.kt
├── settings/
│   ├── SettingsScreen.kt
│   ├── SettingsViewModel.kt      → @HiltViewModel
│   └── SettingsNavigation.kt
├── input/                         → Gamepad management
├── web/                           → Built-in WebView
└── about/                         → About screen
```

---

## 3. Data Flow

```
Server API (appstore.intelligen.ltd:8084)
    │
    ├── /contentconfigapi/getContentConfig
    ├── /contentconfigapi/getInfo
    ├── /appapi/search2
    └── /contentconfigapi/getDownloadUrl
    │
    ▼
AppApiImpl (Retrofit + OkHttp)
    │
    ▼
Repository / Feature Manager
    │
    ▼
ViewModel (Flow → StateFlow)
    │
    ▼
Compose UI (collectAsStateWithLifecycle)
```

All data flows are reactive using Kotlin Flow. ViewModels expose `StateFlow` for UI state and `SharedFlow` for one-shot events (navigation, snackbar messages).

---

## 4. Navigation Design

Type-safe navigation using Navigation Compose with `@Serializable` route classes.

```
NavHost(startDestination = HomeRoute)
├── HomeRoute              → Home (categories + "My" page)
├── GameDetailsRoute       → Game detail (download/install/play)
│   params: configId, dataId, contentType, dataType
├── SearchRoute            → Search
├── DownloaderRoute        → Download manager
├── UninstallerRoute       → Uninstall manager
├── InputRoute             → Gamepad management
├── SettingsRoute          → Settings
├── AboutRoute             → About
└── WebRoute               → Built-in browser
    params: url
```

Navigation events from ViewModels use `SharedFlow<NavigationEvent>` collected in the Navigation composable, keeping ViewModels free of `NavController` references.

---

## 5. Dependency Injection

All DI is managed by Hilt with the following modules:

| Module | Scope | Provides |
|--------|-------|----------|
| DataModule | Singleton | `AppApi` (bound to `AppApiImpl`) |
| DatabaseModule | Singleton | `AppDatabase`, `IDownloadListDb`, `PlayRecordDao` |
| DownloaderModule | Singleton | `DownloadManager`, `DownloadMonitor`, `DownloaderClient`, `DownloadSettings` |
| InstallModule | Singleton | `InstallManager` |
| NetworkModule | Singleton | `NetworkMonitor` (bound to `ConnectivityManagerNetworkMonitor`) |
| SettingsModule | Singleton | `SettingsManager` |
| DispatchersModule | — | `@IoDispatcher CoroutineDispatcher` |

ViewModels use `@HiltViewModel` with constructor injection and `SavedStateHandle` for navigation arguments.

---

## 6. GcAppState Design

After refactoring, `GcAppState` is minimal — only global UI concerns:

```kotlin
@Stable
class GcAppState(
    val navController: NavHostController,
    val snackbarHostState: SnackbarHostState,
    val isOffline: Boolean,
    val playRecords: List<PlayRecord>,
)
```

All business logic dependencies are injected directly into ViewModels via Hilt, eliminating the previous "god object" pattern.

---

## 7. Download Engine Design

```
DownloadManager
├── DownloadJob (per task)
│   ├── PartDownloader[] (per chunk)
│   │   └── Connection (OkHttp)
│   ├── Part[] (chunk metadata)
│   └── DownloadDestination (file I/O)
├── DownloadMonitor (state observation)
├── IDownloadListDb (task persistence)
├── IDownloadPartListDb (chunk persistence)
└── Throttler (bandwidth control)
```

State machine per task:
```
Added → Downloading → Completed
                   → Paused (user or auto)
                   → Error (network/disk)
```

---

## 8. Event Reporting

Integrated `EventReportSdk` (proprietary AAR) for user behavior tracking:
- Game play events
- Download events
- Install events
- App lifecycle events

Configured via AndroidManifest metadata (`ProductAppID`, `keyevent`).

---

## 9. Refactoring Roadmap

### Phase 1 — Foundation (Completed)
- ✅ Migrate all ViewModels to `@HiltViewModel`
- ✅ Simplify `GcAppState` (remove business dependencies)
- ✅ Create `GameRepository`
- ✅ Create `SettingsViewModel`, `UninstallerViewModel`
- ✅ `GameDetailsViewModel` → `@HiltViewModel` + `SavedStateHandle`

### Phase 2 — Domain Layer (Planned)
- Introduce Repository interface + implementation separation
- Introduce UseCase pattern
- Unified `UiState` sealed classes per screen

### Phase 3 — Code Quality (Planned)
- Remove AndroidAutoSize, use Compose native adaptation
- Migrate from Gson to Kotlinx Serialization only
- Unified error handling layer
- Unit test coverage
