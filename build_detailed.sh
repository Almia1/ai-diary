#!/bin/bash
cd /workspace

export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
export PATH=$JAVA_HOME/bin:$PATH
export ANDROID_HOME=/root/android-sdk
export ANDROID_SDK_ROOT=/root/android-sdk
export PATH=$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH

# 增加超时时间
export GRADLE_OPTS="-Dorg.gradle.internal.http.connectionTimeout=180000 -Dorg.gradle.internal.http.socketTimeout=180000 -Xmx4096m"

echo "========================================"
echo "记账笔记 - 项目构建"
echo "========================================"
echo ""
echo "步骤 1: 设置环境变量... ✅"
echo ""
echo "步骤 2: 开始构建项目..."
echo "注意：首次构建需要下载依赖，可能需要较长时间"
echo "请耐心等待..."
echo ""

# 开始构建，每 30 秒输出一次状态
/workspace/gradle-8.7/bin/gradle assembleDebug \
    --stacktrace \
    --info \
    --no-daemon \
    2>&1 | tee /workspace/build.log &

BUILD_PID=$!

# 显示构建进度
echo "构建进程已启动，PID: $BUILD_PID"
echo ""

# 等待构建完成
wait $BUILD_PID
EXIT_CODE=$?

echo ""
echo "========================================"
if [ $EXIT_CODE -eq 0 ]; then
    echo "✅ 构建成功！"
    echo "========================================"
    echo ""
    if [ -f "/workspace/app/build/outputs/apk/debug/app-debug.apk" ]; then
        APK_SIZE=$(du -h /workspace/app/build/outputs/apk/debug/app-debug.apk)
        echo "✅ APK 文件已生成！"
        echo "位置: /workspace/app/build/outputs/apk/debug/app-debug.apk"
        echo "大小: $APK_SIZE"
        echo ""
        echo "下一步："
        echo "1. 将 APK 安装到 Android 设备"
        echo "2. 或者使用 adb install /workspace/app/build/outputs/apk/debug/app-debug.apk"
        echo ""
        # 列出构建文件
        echo "构建输出："
        ls -lh /workspace/app/build/outputs/apk/debug/
    else
        echo "⚠️  APK 文件未找到！请检查构建日志。"
    fi
else
    echo "❌ 构建失败"
    echo "========================================"
    echo ""
    echo "退出代码: $EXIT_CODE"
    echo ""
    echo "查看详细日志：/workspace/build.log"
    echo ""
    echo "常见问题："
    echo "1. 网络超时 - 请检查网络连接"
    echo "2. 依赖下载失败 - 可以重试"
    echo "3. 内存不足 - 增加 JVM 内存"
fi
