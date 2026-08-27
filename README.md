# MusicDownloader Version 1.0.4

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

## 1.0.4 网易云下载修复

本版本重点修复网易云下载生成 0 字节文件的问题。

- 下载公式改为 `https://music.163.com/song/media/outer/url?id=歌曲ID.mp3`。
- 保留 HTTP 重定向跟随，用于获取网易云实际 `music.126.net` 音频地址。
- 检查响应类型，避免把 HTML/JSON 错误页面保存成 `.mp3`。
- 下载失败或出现异常时删除 MediaStore 中的空/残留文件。
- 下载完成前不会把临时文件标记为公开可见。
- 显示实际下载字节数。
