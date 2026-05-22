#!/bin/bash

echo "========================================"
echo "开始构建项目（详细日志）"
echo "========================================"
echo ""

cd /workspace

# 设置环境变量
export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
export PATH=$JAVA_HOME/bin:$PATH
export ANDROID_HOME=/root/android-sdk
export ANDROID_SDK_ROOT=/root/android-sdk
export PATH=$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH

# 增加超时和内存
export GRADLE_OPTS="-Dorg.gradle.internal.http.connectionTimeout=300000 -Dorg.gradle.internal.http.socketTimeout=300000 -Xmx4096m"

echo "步骤 1：检查网络连接..."
# 测试是否能连接到 Google Maven
if curl -L --max-time 30 -o /dev/null -w "%{http_code}" "https://maven.google.com" 2>/dev/null | grep -q "200\|301\|302\|307"; then
    echo "✅ 网络连接正常"
else
    echo "⚠️  可能有网络限制，但继续尝试..."
fi
echo ""

echo "步骤 2：开始 Gradle 构建..."
echo "这可能需要 10-30 分钟，请耐心等待..."
echo ""
date

# 运行构建并显示进度
/workspace/gradle-8.7/bin/gradle \
    --build-cache \
    --no-daemon \
    --console=plain \
    :app:assembleDebug 2>&1 | tee /workspace/build_detailed.log

EXIT_CODE=${PIPESTATUS[0]}

echo ""
date
echo ""

if [ $EXIT_CODE -eq 0 ]; then
    echo "========================================"
    echo "✅ 构建成功！"
    echo "========================================"
    if [ -f "/workspace/app/build/outputs/apk/debug/app-debug.apk" ]; then
        APK_PATH="/workspace/app/build/outputs/apk/debug/app-debug.apk"
        APK_SIZE=$(du -h $APK_PATH | cut -f1)
        echo ""
        echo "🎉 APK 文件已生成！"
        echo "📦 路径：$APK_PATH"
        echo "📏 大小：$APK_SIZE"
        echo ""
        echo "下一步："
        echo "1. 将 APK 传输到 Android 设备"
        echo "2. 安装并测试应用"
        echo ""
    else
        echo "⚠️  APK 未找到，请检查构建日志"
    fi
else
    echo "========================================"
    echo "❌ 构建失败！退出码：$EXIT_CODE"
    echo "========================================"
    echo ""
    echo "查看详细日志："
    echo "  tail -100 /workspace/build_detailed.log"
    echo ""
fi
