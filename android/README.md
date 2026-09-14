# Android Project - Radio Shuddhodhan

This folder contains the Android project for Radio Shuddhodhan.

## Structure (as requested)

```
android/
├── app/
│   ├── build.gradle.kts          # App-level Gradle config (applicationId, version, signing)
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/com/radioshuddhodhan/app/...
│   │       └── res/
│   │           ├── mipmap-*/ic_launcher.png (official logo)
│   │           └── drawable-nodpi/app_logo.png
│   └── proguard-rules.pro
├── build.gradle.kts              # Project-level Gradle
├── settings.gradle.kts
├── gradle.properties
├── gradle/
│   └── wrapper/
├── gradlew
└── gradlew.bat
```

The root of the repo also has the same structure (for CI compatibility). Both locations build the same app:

```bash
# From repo root
./gradlew bundleRelease

# From android/ folder
cd android
./gradlew bundleRelease
```

## Build Outputs

- APK: `app/build/outputs/apk/release/app-release.apk`
- AAB (for Play Store): `app/build/outputs/bundle/release/app-release.aab`

## Signing

See `SIGNING.md` and `keystore.properties.example` for Play Store signing setup.

## Play Store Flow

```
Radio Shuddhodhan
      ↓
Android project (android/ or root)
      ↓
./gradlew bundleRelease → app-release.aab (signed)
      ↓
Google Play Console (play.google.com/console)
      ↓
Google Play Store
```

