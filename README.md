# GameCenter

An Android TV game center application that allows users to browse, search, download, install, and play games using a remote control or gamepad. Supports both native APK games and H5 web games.

## Features

- **Home** — Dynamic category tabs fetched from server, recent play history, quick access to settings and management
- **Game Details** — Game info display with download/install/play flow for APK games, WebView launch for H5 games, Deep Link support (`gamecenter://details`)
- **Search** — Keyword search with debounce, results navigate to game details
- **Download Manager** — Multi-threaded chunked download engine with pause/resume/cancel, up to 5 concurrent tasks, auto-install on completion
- **Install/Uninstall** — Silent install/uninstall via system signature (`android.uid.system`), batch uninstall support
- **Gamepad Management** — USB and Bluetooth controller connection guide
- **Settings** — Auto-install after download, auto-cleanup after install (DataStore Preferences)
- **WebView** — Built-in browser for H5 games and external links
- **Internationalization** — 21 languages including Chinese, English, Japanese, Korean, Arabic, and more

## Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin 2.2.0 |
| UI | Jetpack Compose + AndroidX TV Material 1.0.1 |
| Navigation | Navigation Compose 2.9.3 (Type-Safe Routes) |
| Networking | Retrofit 3.0.0 + OkHttp 5.1.0 |
| Image Loading | Coil 3.3.0 (SVG, OkHttp backend) |
| Database | Room 2.7.2 |
| Preferences | DataStore 1.1.4 |
| DI | Hilt 2.57.2 + KSP |
| Serialization | Kotlinx Serialization 1.9.0 + Gson |
| Async | Kotlin Coroutines + Flow |
| FP | Arrow Core 2.1.2 |
| Build | Gradle 8.10.1 + AGP 8.10.1 |

## Architecture

```
┌──────────────────────────────────────────┐
│              UI Layer                     │
│  Compose Screens + TV Material + Nav      │
├──────────────────────────────────────────┤
│           ViewModel Layer                 │
│  @HiltViewModel + StateFlow + SharedFlow  │
├──────────────────────────────────────────┤
│         Domain / Feature Layer            │
│  DownloadManager, InstallManager,         │
│  SettingsManager, NetworkMonitor          │
├──────────────────────────────────────────┤
│             Data Layer                    │
│  AppApi → Retrofit, Room, DataStore       │
├──────────────────────────────────────────┤
│        Dependency Injection (Hilt)        │
└──────────────────────────────────────────┘
```

## Project Structure

```
com.kgzn.gamecenter/
├── data/           → API models, remote/local data sources, repository, DI modules
├── db/             → Room database, DAOs (DownloadItem, PlayRecord)
├── designsystem/   → Reusable Compose components, theme, modifiers
├── feature/
│   ├── downloader/ → Multi-threaded chunked download engine
│   ├── installer/  → Silent APK install/uninstall
│   ├── network/    → Connectivity monitoring
│   └── settings/   → User preferences management
└── ui/
    ├── home/           → Home screen with category browsing
    ├── gamedetails/    → Game detail & download/install flow
    ├── search/         → Game search
    ├── downloader/     → Download management UI
    ├── uninstaller/    → Batch uninstall UI
    ├── settings/       → Settings UI
    ├── input/          → Gamepad management
    ├── web/            → Built-in WebView
    └── about/          → About screen
```

## Build

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires system platform signing key)
./gradlew assembleRelease
```

Requirements:
- Android Studio Ladybug or later
- JDK 17+
- Android SDK 36
- Min SDK 29 (Android 10)

## Configuration

- Design resolution: 960×540dp (landscape)
- System app with `android.uid.system` shared UID
- Deep Link schemes: `gamecenter://`, `http://`, `https://`

## License

Proprietary — All rights reserved.
