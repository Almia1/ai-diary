#!/bin/bash
cd /workspace

echo "========================================"
echo "🚀 简化版构建脚本"
echo "========================================"
echo ""

# 设置环境变量
export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
export PATH=$JAVA_HOME/bin:$PATH
export ANDROID_HOME=/root/android-sdk
export ANDROID_SDK_ROOT=/root/android-sdk
export PATH=$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH
export GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx4g -Dorg.gradle.daemon=false"

echo "✅ Java 版本："
java -version 2>&1
echo ""

echo "✅ Android SDK：$ANDROID_HOME"
echo ""

echo "========================================"
echo "步骤 1：检查 Gradle 配置"
echo "========================================"
echo ""

# 先运行一个简单的 Gradle 命令，检查配置
echo "检查项目配置..."
/workspace/gradle-8.7/bin/gradle --no-daemon help 2>&1 | head -50

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Gradle 配置有问题"
    exit 1
fi

echo ""
echo "========================================"
echo "步骤 2：开始实际构建"
echo "========================================"
echo ""
echo "注意：这将下载依赖，请耐心等待..."
echo ""

# 运行构建
/workspace/gradle-8.7/bin/gradle \
    --no-daemon \
    --stacktrace \
    :app:assembleDebug

BUILD_EXIT=$?

echo ""
echo "========================================"
if [ $BUILD_EXIT -eq 0 ]; then
    echo "✅ 构建成功！"
    echo "========================================"
    echo ""
    if [ -f "/workspace/app/build/outputs/apk/debug/app-debug.apk" ]; then
        APK_SIZE=$(du -h /workspace/app/build/outputs/apk/debug/app-debug.apk)
        echo "🎉 APK 文件："
        echo "   📦 位置：/workspace/app/build/outputs/apk/debug/app-debug.apk"
        echo "   📏 大小：$APK_SIZE"
    else
        echo "⚠️  APK 未找到，但构建成功？"
    fi
else
    echo "❌ 构建失败！退出码：$BUILD_EXIT"
    echo "========================================"
    echo ""
    echo "查看详细日志或重试..."
fi
