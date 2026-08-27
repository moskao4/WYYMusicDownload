# 音乐下载器 v1.0

Android 10+ oriented prototype for the current project.

## Included
- 网易云下载作为默认主页
- 网易云分享文本：解析 `song?id=数字` 与歌曲名
- 网易云 outer URL 下载
- 普通音频 URL 下载
- 下载进度/速度/状态
- 保存到公共 `Download/`
- 下载记录
- 综合搜索页骨架
- QQ 音乐 SongMID 识别页（实际音频地址解析未接入）
- 外观：跟随系统 / 浅色 / 深色 / 炫彩
- 9 个可切换 Launcher 图标，06 为默认
- 关于页：当前应用图标、Version 1.0、moskao4 与 ChatGPT 两行作者信息

## 编译
本目录是标准 Android Gradle 工程。建议 Android Studio + Android SDK 35 打开并运行 `assembleDebug`。

## 本运行环境限制
当前执行环境没有 Android SDK/Gradle，因此本轮无法在这里实际产出 APK；没有伪造 APK 文件。

## Logo
关于页的 ChatGPT/OpenAI 标识应严格按 OpenAI 官方品牌指南使用，不应修改、着色或暗示 OpenAI 背书。见 https://openai.com/zh-Hans-CN/brand/

## 无电脑构建 APK（推荐）
1. 在 GitHub 新建一个空仓库。
2. 上传本工程 `MusicDownloader/` 内的全部文件。
3. 打开仓库的 Actions，运行 `Build APK`。
4. 工作流完成后，在 Artifacts 下载 `MusicDownloader-debug-apk`。
5. 解压后即可得到 `app-debug.apk`，传到 Android 手机上安装。

本工程已附带 `.github/workflows/build-apk.yml`，无需自己写 GitHub Actions。

- `avatar_moskao4.jpg` is the user-provided personal avatar and is used only in the About page.
- About page app icon follows the currently selected launcher icon.
