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

## Project Structure (Your Requested Format)

You asked for:

```
android/
app/
build.gradle
settings.gradle
AndroidManifest.xml
gradle/
```

This repo now supports **BOTH**:

**Root = Android project (standard):**
```
./
├── app/build.gradle.kts
├── app/src/main/AndroidManifest.xml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
└── gradlew
```

**android/ = Same project (your format):**
```
android/
├── app/build.gradle.kts
├── app/src/main/AndroidManifest.xml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
└── gradlew
```

Both build the same app. See `docs/PROJECT_STRUCTURE.md`.

## Build

Requirements: JDK 17, Android SDK 35.

```bash
./gradlew assembleDebug        # debug APK
./gradlew testDebugUnitTest    # unit tests (BS calendar verification)
./gradlew assembleRelease      # release APK (signed if keystore.properties exists)
./gradlew bundleRelease        # release AAB (for Play Store) ← USE THIS FOR PLAY STORE
```

APKs and AABs are also built by GitHub Actions on every push (see
`.github/workflows/android-build.yml`) and attached as artifacts.

## Play Store Flow

```
Radio Shuddhodhan
        ↓
Android project (root or android/ folder)
        ↓
./gradlew bundleRelease → app-release.aab (signed)
        ↓
Google Play Console (play.google.com/console)
        ↓
Google Play Store (public)
```

**Full guide:** [`docs/PLAY_STORE.md`](docs/PLAY_STORE.md) — signing, AAB generation, Console upload, GitHub Secrets setup.

**Signing:** Upload keystore generated at `app/upload-keystore.jks` (gitignored). See `SIGNING.md` and `keystore.properties.example`.

```
# Quick AAB build
./gradlew bundleRelease
# Output: app/build/outputs/bundle/release/app-release.aab
```

## Documentation

- [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) — app architecture
- [`docs/BACKEND_API.md`](docs/BACKEND_API.md) — full backend API contract,
  push (FCM) setup and the secure AI proxy design
- [`docs/PROJECT_STRUCTURE.md`](docs/PROJECT_STRUCTURE.md) — requested `android/ app/ build.gradle ...` format (both root and android/ folder)
- [`docs/PLAY_STORE.md`](docs/PLAY_STORE.md) — full Play Store flow: `Radio Shuddhodhan → Android → Signed AAB → Console → Store`
- [`SIGNING.md`](SIGNING.md) — keystore generation, `bundleRelease`, GitHub Secrets for CI

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
