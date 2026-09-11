# Radio Shuddhodhan — Architecture

## Overview

Radio Shuddhodhan v1.0.1 is a native Android application (Kotlin + Jetpack
Compose) built as a **single-activity app** with reactive, offline-first
architecture.

```
┌──────────────────────────────────────────────────────────┐
│ UI  — Jetpack Compose, one NavHost, 30+ destinations     │
│      (splash, guide, auth, home, player, news, posts,    │
│       calendar, stations, social, helpdesk, profile,     │
│       settings, about, notifications, admin console)     │
└───────────────┬──────────────────────────────────────────┘
                │ StateFlow / Room Flow (reactive)
┌───────────────▼──────────────────────────────────────────┐
│ ViewModels (per screen, manual DI — no framework)        │
├──────────────────────────────────────────────────────────┤
│ Repositories                                             │
│  ConfigRepository   — remote config, server = truth      │
│  ContentRepository  — news/posts/stations/events/social   │
│  AuthRepository     — local demo + backend accounts       │
│  HelpdeskRepository — tickets + replies                   │
├───────────────┬───────────────────────────┬──────────────┤
│ Room DB       │ Retrofit API client       │ Media3 player│
│ (cache/SoT)   │ (backend integration)     │ (ExoPlayer + │
│               │                           │  MediaSession│
└───────────────┴───────────────────────────┴──────────────┘
```

## Key components

| Component | File(s) | Purpose |
|---|---|---|
| Application + DI container | `RadioApp.kt` | Lazy singletons; notification channels; seeding |
| Live radio service | `audio/RadioPlaybackService.kt` | Media3 `MediaSessionService`: background playback, lock-screen controls, Bluetooth/headphone handling |
| Player connection | `audio/PlayerManager.kt` | `MediaController` wrapper, state flow, auto-reconnect with backoff, volume |
| Sync engine | `sync/SyncManager.kt`, `SyncWorker.kt` | Start/connectivity/15-min sync; instant local updates via Room flows |
| Remote config | `data/repo/ConfigRepository.kt` | Feature flags; server copy always wins |
| BS calendar | `core/nepalidate/BsCalendar.kt` | Verified AD↔BS conversion (2000–2090 BS) |
| Holidays | `core/nepalidate/HolidayData.kt` | Festivals/public holidays BS 2082–2084 (MIT data) |
| Bilingual strings | `core/language/AppStrings.kt` | Nepali/English, runtime switching |
| Push integration | `notifications/*` | Guarded FCM service + registrar + local delivery |
| Navigation | `ui/navigation/` | All routes, animated transitions |

## Data flow (real-time sync)

1. Admin (in-app console or web) changes something → backend.
2. User devices sync (launch / connectivity / 15-min / FCM push trigger).
3. Content lands in **Room**; every screen observes Room `Flow`s.
4. UI updates instantly — no restart, no APK update.

In **demo mode** (no backend configured) the admin console writes directly
to the same Room database, so the identical reactive pipeline is exercised
end-to-end on one device.

## Offline behaviour

- All content is cached in Room; the app opens and reads offline.
- A connectivity observer drives the offline banner and auto re-sync.
- Live radio requires internet (by nature); the player reports connection
  status and retries automatically.

## Security

- No admin credentials, OpenAI keys or server secrets in the APK.
- Admin login: server-side authorization (`/api/v1/admin/login`) or a
  device-local demo PIN (salted SHA-256, clearly labelled).
- AI generation: only through the backend proxy; results require explicit
  administrator approval before publication (saved as drafts).

## Verified calendar data

The BS conversion table is cross-verified against two independent
open-source datasets (see file header) and unit-tested in
`app/src/test/.../BsCalendarTest.kt`, including known festival anchors.

## Original design

The visual design is original: Nepali-flag crimson, Himalayan navy and
broadcast gold; custom logo; no third-party app UI was copied.
