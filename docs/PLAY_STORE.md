# Play Store Release Guide — Radio Shuddhodhan

## Flow You Requested

```
Radio Shuddhodhan
        ↓
Android project (this repo / android/ folder)
        ↓
Signed AAB (Android App Bundle)
        ↓
Google Play Console (play.google.com/console)
        ↓
Google Play Store (public)
```

---

## 1. Project Structure (Your Format)

You wanted:

```
android/
app/
build.gradle
settings.gradle
AndroidManifest.xml
gradle/
```

We now have **both**:

- **Root** = Android project (standard native)
  - `app/build.gradle.kts`
  - `app/src/main/AndroidManifest.xml`
  - `build.gradle.kts`
  - `settings.gradle.kts`
  - `gradle/`

- **android/** = Same project copied for your preferred layout
  - `android/app/build.gradle.kts`
  - `android/app/src/main/AndroidManifest.xml`
  - `android/build.gradle.kts`
  - etc.

Both build identically. See `docs/PROJECT_STRUCTURE.md`.

---

## 2. Signing Setup (Already Done)

We generated an **upload keystore** for you:

- **File (gitignored):** `app/upload-keystore.jks` (and `.p12`)
- **Alias:** `upload`
- **Passwords:** `RadioShuddhodhan95.1` (store & key)
- **DN:** `CN=Radio Shuddhodhan, OU=Radio, O=Shuddhodhan Multimedia, L=Shuddhodhan, C=NP`
- **Validity:** 10,000 days

**Location:**
- Root: `app/upload-keystore.jks`
- Also exists in workspace, but gitignored for security

**Config file (gitignored):** `keystore.properties` at repo root:

```properties
storeFile=app/upload-keystore.jks
storePassword=RadioShuddhodhan95.1
keyAlias=upload
keyPassword=RadioShuddhodhan95.1
```

**Template (committed):** `keystore.properties.example`

`app/build.gradle.kts` automatically:
- Uses real keystore if `keystore.properties` exists → **Play Store-ready signed AAB**
- Falls back to debug keystore if not → for CI/testing

> ⚠️ **BACKUP YOUR KEYSTORE!** Store `app/upload-keystore.jks` + passwords in 1Password / Google Drive / secure vault. If you lose it, you cannot update the app on Play Store!

---

## 3. Build Signed AAB

### Requirements

- JDK 17
- Android SDK 35
- `keystore.properties` present (already created in your workspace)

### Commands

```bash
# From repo root (or cd android and same commands)

# Debug APK for testing on device
./gradlew assembleDebug
# → app/build/outputs/apk/debug/app-debug.apk

# Release APK (signed if keystore.properties exists)
./gradlew assembleRelease
# → app/build/outputs/apk/release/app-release.apk

# Release AAB — THIS IS WHAT PLAY STORE WANTS
./gradlew bundleRelease
# → app/build/outputs/bundle/release/app-release.aab
```

**Versioning:** Before each Play Store update, bump in `app/build.gradle.kts`:

```kotlin
versionCode = 10002  // must increase integer
versionName = "1.0.2"
```

---

## 4. Google Play Console Setup

### A. Create Developer Account

1. Go to https://play.google.com/console
2. Pay $25 one-time registration
3. Complete identity verification

### B. Create New App

1. **Create app** → Name: `Radio Shuddhodhan`
2. Package: `com.radioshuddhodhan.app` (already set in `app/build.gradle.kts`)
3. App type: App, Category: News & Magazines or Music & Audio
4. Enable **Play App Signing** (recommended) → Google manages final distribution key, you only manage upload key (the one we generated)

### C. Store Listing Assets

You already have:

- **High-res icon:** `web/app_logo.png` (512x512, official logo, white bg) - meets Play Store 512x512 requirement
- **Adaptive icon:** `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png` (192px) + foreground
- **Feature graphic:** Need to create 1024x500 banner (use official logo + text "Radio Shuddhodhan 95.1 MHz")
- **Screenshots:** Take from app (phone + tablet, at least 2)

Prepare:
- Short description (80 chars): e.g., "Radio Shuddhodhan 95.1 MHz - हरेक नेपालीको मन"
- Full description (Nepali + English)
- Privacy policy URL (required)
- Contact email, phone

### D. Upload AAB

1. In Play Console → **Production** → **Create new release**
2. Upload `app/build/outputs/bundle/release/app-release.aab`
3. Release name: `1.0.1` (versionName)
4. Release notes: "Initial release - Live radio, news, BS calendar, etc."
5. **Content rating:** Fill questionnaire
6. **Target audience:** 13+
7. **Data safety:** Declare no sensitive data collected (or as per your backend)

### E. Review & Rollout

- Complete all checklist items (store listing, content rating, target audience, data safety, etc.)
- **Review** → **Rollout to Production**
- Google review takes 1-3 days (sometimes hours)

---

## 5. GitHub Actions CI (AAB in Artifacts)

`.github/workflows/android-build.yml` now:

- Builds `assembleDebug`, `assembleRelease`, `bundleRelease`
- Uploads artifacts:
  - `RadioShuddhodhan-v1.0.1-debug` (APK)
  - `RadioShuddhodhan-v1.0.1-release` (APK)
  - `RadioShuddhodhan-v1.0.1-release-aab` (AAB for Play Store)

**For signed AAB in CI**, add GitHub Secrets:

- `KEYSTORE_BASE64` → `base64 app/upload-keystore.jks`
- `STORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

Workflow will auto-decode if secrets exist:

```yaml
- name: Setup signing
  run: |
    echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > app/upload-keystore.jks
    cat > keystore.properties << EOF
    storeFile=app/upload-keystore.jks
    storePassword=${{ secrets.STORE_PASSWORD }}
    keyAlias=${{ secrets.KEY_ALIAS }}
    keyPassword=${{ secrets.KEY_PASSWORD }}
    EOF
```

Generate base64:

```bash
base64 -w 0 app/upload-keystore.jks > keystore.base64.txt
# Copy content to GitHub Secret KEYSTORE_BASE64
```

---

## 6. Update Flow (After First Release)

```
Change code
  ↓
Bump versionCode/versionName in app/build.gradle.kts
  ↓
./gradlew bundleRelease
  ↓
Play Console → Production → New release → Upload new AAB
  ↓
Rollout
```

---

## 7. Checklist

- [x] Official logo as app icon (all mipmap densities + adaptive)
- [x] `app/build.gradle.kts` supports `keystore.properties`
- [x] `keystore.properties.example` template
- [x] Upload keystore generated (`app/upload-keystore.jks`)
- [x] `android/` folder mirroring root (your requested format)
- [x] GitHub Actions builds AAB
- [x] `SIGNING.md` detailed guide
- [ ] Backup keystore to secure storage (DO THIS NOW!)
- [ ] Create Play Console account
- [ ] Create feature graphic 1024x500
- [ ] Take screenshots
- [ ] Write privacy policy
- [ ] Upload AAB to Production

---

## 8. Quick Reference Commands

```bash
# Generate new keystore (if needed before first upload)
keytool -genkey -v -keystore app/upload-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload

# Or openssl (used for initial)
openssl genrsa -out upload-key.pem 2048
openssl req -new -x509 -key upload-key.pem -out upload-cert.pem -days 10000 -subj "/CN=Radio Shuddhodhan/O=Shuddhodhan Multimedia/C=NP"
openssl pkcs12 -export -out app/upload-keystore.jks -inkey upload-key.pem -in upload-cert.pem -name upload -password pass:YOUR_PASSWORD

# Build
./gradlew bundleRelease

# Verify AAB signing
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab

# Or via bundletool
bundletool build-apks --bundle=app-release.aab --output=test.apks
```

---

**Radio Shuddhodhan v1.0.1 — Created by Umesh Tharu**

Official logo is now launcher icon. AAB is ready for Play Store!
