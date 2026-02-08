# GameCenter — Requirements Document

## 1. Overview

GameCenter is an Android TV game store application. Users interact via remote control or gamepad to browse, search, download, install, and play games. The app supports both native APK games and H5 web games.

- Package: `com.kgzn.gamecenter`
- Platform: Android TV (Leanback)
- Min SDK: 29 (Android 10)
- Target SDK: 36
- Design resolution: 960×540dp, landscape only

---

## 2. Functional Requirements

### 2.1 Home Screen

| ID | Requirement |
|----|-------------|
| FR-HOME-01 | Display dynamic category tabs fetched from server (`ContentConfig` API) |
| FR-HOME-02 | Support vertical pager switching between category pages |
| FR-HOME-03 | First page is "My" page showing recent play records (max 10) |
| FR-HOME-04 | "My" page provides quick access: gamepad management, download manager, uninstall manager, settings, about |
| FR-HOME-05 | Category pages display banners and game lists composed of `Component` + `Resource` |

### 2.2 Game Details

| ID | Requirement |
|----|-------------|
| FR-DETAIL-01 | Display game info: title, description, tags, control method (gamepad/remote), background image, related recommendations |
| FR-DETAIL-02 | APK games: support download → install → launch flow |
| FR-DETAIL-03 | APK games: support pause, resume, cancel download |
| FR-DETAIL-04 | APK games: detect version updates and prompt user |
| FR-DETAIL-05 | H5 games: open directly in built-in WebView |
| FR-DETAIL-06 | Detect Google Play dependency; show dialog if required but not installed |
| FR-DETAIL-07 | Support Deep Link: `gamecenter://details?configId=&dataId=&contentType=&dataType=` |
| FR-DETAIL-08 | Record play history to local database and report events via EventReportSdk |

### 2.3 Search

| ID | Requirement |
|----|-------------|
| FR-SEARCH-01 | Keyword search with debounce delay |
| FR-SEARCH-02 | Call `/appapi/search2` API and display results |
| FR-SEARCH-03 | Tap result navigates to game details |

### 2.4 Download Manager

| ID | Requirement |
|----|-------------|
| FR-DL-01 | Display all download tasks sorted by add time (descending) |
| FR-DL-02 | Support operations: pause, resume, delete, install completed games |
| FR-DL-03 | Download states: Added → Downloading → Completed / Paused / Error |
| FR-DL-04 | Max 5 concurrent downloads; excess tasks auto-pause oldest |
| FR-DL-05 | Optional auto-install after download completion (configurable) |
| FR-DL-06 | Optional auto-cleanup of APK after install (configurable) |

### 2.5 Install & Uninstall

| ID | Requirement |
|----|-------------|
| FR-INST-01 | Silent install using `PackageInstaller` API (system signature) |
| FR-INST-02 | Broadcast install/uninstall status via `InstallEvents` event flow |
| FR-INST-03 | Uninstall management page lists installed games with batch uninstall |

### 2.6 Gamepad Management

| ID | Requirement |
|----|-------------|
| FR-INPUT-01 | Guide users through USB and Bluetooth gamepad connection |
| FR-INPUT-02 | Display connected devices with disconnect option |
| FR-INPUT-03 | Require Bluetooth permissions: `BLUETOOTH`, `BLUETOOTH_CONNECT`, `BLUETOOTH_PRIVILEGED` |

### 2.7 Settings

| ID | Requirement |
|----|-------------|
| FR-SET-01 | Toggle: auto-install after download (default: on) |
| FR-SET-02 | Toggle: auto-cleanup APK after install (default: on) |
| FR-SET-03 | Persist settings via DataStore Preferences |

### 2.8 WebView

| ID | Requirement |
|----|-------------|
| FR-WEB-01 | Built-in browser for H5 games and external links |
| FR-WEB-02 | Intercept http/https links via Intent filter |

### 2.9 Internationalization

| ID | Requirement |
|----|-------------|
| FR-I18N-01 | Support 21 languages: zh-CN, zh-TW, en, ja, ko, ar, de, es, fr, id, it, he, nl, pl, pt, ru, th, uk, uz, vi, fa |

---

## 3. Non-Functional Requirements

### 3.1 Performance

| ID | Requirement |
|----|-------------|
| NFR-PERF-01 | Download engine supports multi-threaded chunked download with resume |
| NFR-PERF-02 | Max 5 concurrent download tasks with global throttling |
| NFR-PERF-03 | Search input debounce to reduce API calls |
| NFR-PERF-04 | Disk space check before download (reserve 50MB minimum) |

### 3.2 Reliability

| ID | Requirement |
|----|-------------|
| NFR-REL-01 | Download supports breakpoint resume across app restarts |
| NFR-REL-02 | Auto-refresh expired download URLs |
| NFR-REL-03 | Network retry with configurable retry interceptor |
| NFR-REL-04 | Offline detection with graceful degradation |

### 3.3 Security

| ID | Requirement |
|----|-------------|
| NFR-SEC-01 | System signature app (`android.uid.system`) for silent install/uninstall |
| NFR-SEC-02 | Token-based API authentication |

### 3.4 Usability

| ID | Requirement |
|----|-------------|
| NFR-USE-01 | Full D-Pad / remote control navigation (TV optimized) |
| NFR-USE-02 | Gamepad support for all screens |
| NFR-USE-03 | Landscape-only, fixed 960×540dp design resolution |

### 3.5 Maintainability

| ID | Requirement |
|----|-------------|
| NFR-MAIN-01 | MVVM architecture with Hilt dependency injection |
| NFR-MAIN-02 | Reactive data flow: Kotlin Flow → StateFlow → Compose |
| NFR-MAIN-03 | Type-safe navigation routes |
| NFR-MAIN-04 | Modular DI: separate modules for network, database, download, install, settings |

---

## 4. API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/contentconfigapi/getContentConfig` | POST | Get home page categories and content config |
| `/contentconfigapi/getInfo` | POST | Get game detail info |
| `/appapi/search2` | POST | Search games by keyword |
| `/contentconfigapi/getDownloadUrl` | POST | Get download URL for a game |

Base URL: `appstore.intelligen.ltd:8084`

---

## 5. Deep Link Specification

| Scheme | Host | Parameters | Description |
|--------|------|------------|-------------|
| `gamecenter` | `details` | `configId`, `dataId`, `contentType`, `dataType` | Open game details |
| `http` / `https` | `*` | — | Open in built-in WebView |
