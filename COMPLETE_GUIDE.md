# 记账笔记 - 构建和运行指南

## 📋 当前状态
✅ 环境配置完成
- Java 17 已安装
- Android SDK 34 已安装
- Gradle 8.7 已配置
- 本地配置已设置好

✅ 代码已完整
- OCR 识别功能已添加
- 所有源文件已就位
- 项目配置已更新

## 🚀 构建项目

### 方式 1：使用简化构建脚本（推荐）
```bash
/workspace/simple_build.sh
```

### 方式 2：使用详细构建脚本
```bash
/workspace/build_full.sh
```

### 方式 3：直接使用 Gradle
```bash
# 设置环境变量
export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
export PATH=$JAVA_HOME/bin:$PATH
export ANDROID_HOME=/root/android-sdk
export ANDROID_SDK_ROOT=/root/android-sdk
export PATH=$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH

# 运行构建
cd /workspace
./gradle-8.7/bin/gradle :app:assembleDebug
```

## 📊 监控构建进度

### 查看构建状态
```bash
/workspace/status.sh
```

### 查看构建日志
```bash
/workspace/check_logs.sh
```

## ⏱️ 首次构建时间

首次构建需要下载大量依赖（约 500MB+），可能需要：
- 良好网络：10-20 分钟
- 普通网络：20-40 分钟
- 较慢网络：可能需要多次重试

## 📦 构建输出

成功后，APK 位置：
```
/workspace/app/build/outputs/apk/debug/app-debug.apk
```

## 📱 运行应用

### 方法 1：在 Android 设备上安装
1. 找到生成的 APK：`/workspace/app/build/outputs/apk/debug/app-debug.apk`
2. 将 APK 传输到 Android 设备
3. 在设备上安装 APK
4. 打开应用测试

### 方法 2：使用 ADB 安装（如果有连接的设备）
```bash
# 确保设置了环境变量
export ANDROID_HOME=/root/android-sdk
export PATH=$ANDROID_HOME/platform-tools:$PATH

# 安装 APK
adb install -r /workspace/app/build/outputs/apk/debug/app-debug.apk
```

## 🔍 测试 OCR 功能

构建成功后：
1. 打开记账笔记应用
2. 点击首页右上角的 📷 相机图标
3. 选择账单截图或拍照
4. 查看识别结果并确认保存

## ❓ 常见问题

### Q：构建卡住不动？
A：很可能是在下载依赖。请耐心等待，或者查看网络连接。

### Q：构建失败怎么办？
A：
1. 检查网络连接
2. 重新运行构建脚本
3. 查看详细日志：`tail -100 /workspace/build_detailed.log`

### Q：找不到 JDK？
A：运行：
```bash
export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
export PATH=$JAVA_HOME/bin:$PATH
java -version
```

### Q：找不到 Android SDK？
A：检查 `/workspace/local.properties` 是否存在，内容应为：
```
sdk.dir=/root/android-sdk
```

## 📄 快速命令索引

```bash
# 检查环境
/workspace/check_env.sh

# 检查构建状态
/workspace/status.sh

# 查看日志
/workspace/check_logs.sh

# 开始构建
/workspace/simple_build.sh

# 或者用详细构建
/workspace/build_full.sh
```

## 🎉 成功标志

看到以下信息即为构建成功：
```
BUILD SUCCESSFUL in XXXs
```
并且能在 `/workspace/app/build/outputs/apk/debug/` 找到 `app-debug.apk`。

祝构建顺利！🚀
