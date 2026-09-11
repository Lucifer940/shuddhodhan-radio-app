# Radio Shuddhodhan 🇳🇵📻

**Radio Shuddhodhan v1.0.1** — created by **Umesh Tharu**.

A production-ready native Android app (Kotlin + Jetpack Compose) for the
Radio Shuddhodhan community radio station: live radio streaming with
background playback, news, posts, a verified Bikram Sambat calendar, radio
stations, social feed, helpdesk, notifications and a full administrator
console — all cloud-controllable.

> This repository originally contained only a README describing a
> React Native/Flutter radio app. The project has been rebuilt as the
> native Android application described here (see `docs/`).

## Features

- **Live radio** — Media3/ExoPlayer streaming, background playback,
  lock-screen/notification controls, Bluetooth & headphone support,
  buffering/reconnect status, auto-reconnect with backoff, volume control.
  Stream URL is **admin-configurable, never hard-coded**.
- **Home** — animated splash, live Nepal clock (Asia/Kathmandu, UTC+05:45),
  Bikram Sambat + Gregorian dates, live radio hero, breaking strip,
  announcements, latest news/posts, stations, events, quick shortcuts.
- **News** — categories, breaking/featured, search, bookmarks, share.
- **Calendar** — full BS calendar (verified data, BS 2000–2090), festivals
  and public holidays (BS 2082–2084), admin events, month navigation,
  today button, Nepali/English.
- **Stations** — search, favourites, play; admin CRUD (name, description,
  stream URL, logo, enable/disable, order, featured).
- **Social feed** — admin-configured Facebook / Facebook Live / YouTube /
  website links opened safely (no scraping).
- **Helpdesk** — categorized contact form + admin inbox with replies.
- **Notifications** — channels for breaking/news/events/announcements,
  in-app notification centre, FCM integration point.
- **Admin console** — remote configuration (feature flags, banner, stream,
  programme, contacts), news/posts/stations/events/announcements/social
  management, helpdesk inbox, notification sender, AI news assistant
  (secure backend proxy; no API key in the app).
- **Settings** — Nepali/English, light/dark/system theme, notification
  preferences, auto-reconnect, backend server URL.
- **Responsive** — phones and tablets, light/dark, animated throughout.

## Build

Requirements: JDK 17, Android SDK 35.

```bash
./gradlew assembleDebug        # debug APK
./gradlew testDebugUnitTest    # unit tests (BS calendar verification)
./gradlew assembleRelease      # release APK (debug-signed for testing)
```

APKs are also built by GitHub Actions on every push (see
`.github/workflows/android-build.yml`) and attached as artifacts.

## Documentation

- [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) — app architecture
- [`docs/BACKEND_API.md`](docs/BACKEND_API.md) — full backend API contract,
  push (FCM) setup and the secure AI proxy design

## Backend

The app is fully functional in **demo mode** out of the box (local database
+ on-device admin console). Connect a backend server at
**Settings → Advanced → Backend server** to make it cloud-controlled:
admin changes then reach every user device through sync/push with no APK
update. No secrets (admin password, OpenAI key, database credentials) are
ever stored in the app.

## Credits

- BS calendar data cross-verified from
  [nepali_utils (MIT)](https://github.com/sarbagyastha/nepali_utils) and
  [nepali-datetime (Apache-2.0)](https://github.com/amitgaru/nepali-datetime)
- Festival/holiday data from
  [nepali-calendar-api (MIT)](https://github.com/S4NKALP/nepali-calendar-api)
- Original UI/branding designed for Radio Shuddhodhan.

**Radio Shuddhodhan v1.0.1 — Created by Umesh Tharu**
