# Radio Shuddhodhan - Play Store Signing Setup

This project is configured to generate a **signed AAB** for Google Play Store.

## Current Keystore (Generated)

- **File:** `app/upload-keystore.jks` (PKCS12 format, also available as .p12)
- **Alias:** `upload`
- **Store Password:** `RadioShuddhodhan95.1`
- **Key Password:** `RadioShuddhodhan95.1`
- **DN:** CN=Radio Shuddhodhan, OU=Radio, O=Shuddhodhan Multimedia, L=Shuddhodhan, C=NP
- **Validity:** 10,000 days

> ⚠️ This keystore is **gitignored** for security. It exists in your local workspace at `app/upload-keystore.jks`.
> **BACK IT UP** securely (Google Drive, 1Password, etc). If you lose it, you cannot update your app on Play Store!

## How Signing Works Now

`app/build.gradle.kts` reads `keystore.properties` at root or app/ folder:

```properties
storeFile=app/upload-keystore.jks
storePassword=...
keyAlias=upload
keyPassword=...
```

- If `keystore.properties` exists → release build uses your upload keystore → produces **Play Store-ready AAB**
- If not exists → release falls back to debug keystore (for CI/testing APKs)

## Generate AAB

```bash
# Debug APK (for testing)
./gradlew assembleDebug

# Release APK (signed if keystore.properties exists)
./gradlew assembleRelease

# Release AAB (for Play Store) - THIS IS WHAT YOU UPLOAD
./gradlew bundleRelease

# Output:
# app/build/outputs/bundle/release/app-release.aab
# app/build/outputs/apk/release/app-release.apk
```

## Play Store Flow

```
Radio Shuddhodhan
      ↓
Android project (this repo)
      ↓
./gradlew bundleRelease → app-release.aab (signed)
      ↓
Google Play Console (play.google.com/console)
      ↓
Google Play Store (public)
```

### Steps to Publish

1. **Create Play Console account** ($25 one-time)
2. **Create new app** in Console: "Radio Shuddhodhan", package `com.radioshuddhodhan.app`
3. **Enable Play App Signing** (recommended) - Google will manage distribution key, you only need upload key
4. **Upload AAB:**
   - Go to Production → Create new release
   - Upload `app/build/outputs/bundle/release/app-release.aab`
   - Fill release notes, content rating, etc.
5. **Store listing:** Add icon (512x512 from `web/app_logo.png`), feature graphic, screenshots, description
6. **Review & rollout**

## GitHub Actions

The workflow `.github/workflows/android-build.yml` builds both debug and release APKs. For signed AAB in CI:

Add these **GitHub Secrets**:
- `KEYSTORE_BASE64` - base64 of your .jks file
- `STORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

Then add a step in workflow to decode keystore and create keystore.properties.

Example workflow addition:

```yaml
- name: Decode keystore
  run: |
    echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > app/upload-keystore.jks
    cat > keystore.properties << EOF
    storeFile=app/upload-keystore.jks
    storePassword=${{ secrets.STORE_PASSWORD }}
    keyAlias=${{ secrets.KEY_ALIAS }}
    keyPassword=${{ secrets.KEY_PASSWORD }}
    EOF

- name: Build signed AAB
  run: ./gradlew bundleRelease
```

## Regenerate Keystore (if needed)

If you need a new keystore (before first Play Store upload):

```bash
# Using keytool (if JDK installed)
keytool -genkey -v -keystore app/upload-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload

# Or using openssl (as we did initially)
openssl genrsa -out upload-key.pem 2048
openssl req -new -x509 -key upload-key.pem -out upload-cert.pem -days 10000 -subj "/CN=Radio Shuddhodhan/O=Shuddhodhan Multimedia/C=NP"
openssl pkcs12 -export -out app/upload-keystore.jks -inkey upload-key.pem -in upload-cert.pem -name upload -password pass:YOUR_PASSWORD
```

Then update `keystore.properties`.

## Important Notes

- **Never commit** `*.jks`, `*.p12`, or `keystore.properties` to git (already gitignored)
- **Backup** your upload keystore in 2+ secure places
- **Versioning:** Increment `versionCode` (10001 → 10002) and `versionName` (1.0.1 → 1.0.2) in `app/build.gradle.kts` for each Play Store update
- **AAB is required** for new apps on Play Store (APK not accepted for new apps since 2021)
- **Icon:** Already set to official logo (512px, white background) - meets Play Store 512x512 requirement

