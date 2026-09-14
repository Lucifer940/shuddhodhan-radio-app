# Project Structure — Requested Format

You asked for:

```
android/
app/
build.gradle
settings.gradle
AndroidManifest.xml
gradle/
```

This repo now supports **BOTH** layouts:

## Option 1: Root is Android project (current, for CI)

This is standard native Android:

```
shuddhodhan-radio-app/          ← repo root = Android project root
├── app/
│   ├── build.gradle.kts        ← app-level config (applicationId, versionCode, signing)
│   ├── proguard-rules.pro
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/radioshuddhodhan/app/...
│           └── res/
│               ├── mipmap-*/ic_launcher.png (official logo, white bg)
│               ├── mipmap-*/ic_launcher_foreground.png (adaptive)
│               └── drawable-nodpi/app_logo.png (512px official logo)
├── build.gradle.kts            ← project-level
├── settings.gradle.kts
├── gradle.properties
├── gradle/wrapper/
├── gradlew
├── gradlew.bat
├── keystore.properties.example
└── web/                        ← PWA preview (optional)
```

Build from root:

```bash
./gradlew assembleDebug
./gradlew bundleRelease  # AAB for Play Store
```

## Option 2: android/ folder (your requested format)

For React Native / Flutter style familiarity, we also have:

```
shuddhodhan-radio-app/
└── android/                    ← same Android project, duplicated for your format
    ├── app/
    │   ├── build.gradle.kts
    │   └── src/main/AndroidManifest.xml
    ├── build.gradle.kts
    ├── settings.gradle.kts
    ├── gradle.properties
    ├── gradle/wrapper/
    ├── gradlew
    └── README.md
```

Build from android/:

```bash
cd android
./gradlew bundleRelease
```

Both produce same outputs:
- `app/build/outputs/apk/release/app-release.apk`
- `app/build/outputs/bundle/release/app-release.aab` ← **for Play Store**

## Why both?

- **Root** keeps GitHub Actions working (`./gradlew` at root)
- **android/** matches your requested `android/ app/ build.gradle settings.gradle AndroidManifest.xml gradle/` format

You can use either. For Play Store docs, we refer to root, but android/ works identically.

## Key Files

| File | Purpose |
|------|---------|
| `app/build.gradle.kts` | VersionCode (10001), VersionName (1.0.1), signingConfig logic |
| `app/src/main/AndroidManifest.xml` | Permissions, icon `@mipmap/ic_launcher`, services |
| `app/src/main/res/mipmap-*/` | Launcher icons - official logo (Buddha + 95.1) on white |
| `keystore.properties.example` | Template for Play Store signing |
| `SIGNING.md` | How to generate keystore & AAB |

## Next: Play Store Flow

See `docs/PLAY_STORE.md` for:

```
Radio Shuddhodhan
      ↓
Android project
      ↓
Signed AAB (bundleRelease)
      ↓
Google Play Console
      ↓
Google Play Store
```
