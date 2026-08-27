# MusicDownloader Version 1.0

This is the corrected Android project after first-device UI testing.

Fixes in this revision:
- All programmatic dimensions are converted from raw pixels to dp.
- Main pages use a ScrollView and stable app-bar layout.
- The selected app icon is visible in the app bar and About page.
- The user's supplied red avatar is used only as moskao4's circular avatar.
- Icon 06 has an opaque black base so it cannot disappear into a transparent launcher background.
- Nine launcher aliases remain available; 06 is the default.
- Download status/progress layout is initialized consistently.
- Netease share-text parsing and Download-folder saving remain included.
- GitHub Actions workflow is included at `.github/workflows/build-apk.yml`.

## GitHub build
Upload the contents of this directory to the repository root, then use Actions -> Build APK.
