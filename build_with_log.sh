#!/bin/bash
# 后台构建脚本 - 适合网络较慢的情况

cd /workspace

export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
export PATH=$JAVA_HOME/bin:$PATH
export ANDROID_HOME=/root/android-sdk
export ANDROID_SDK_ROOT=/root/android-sdk
export PATH=$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH

# 设置超时时间（120秒）
export GRADLE_OPTS="-Dorg.gradle.internal.http.connectionTimeout=120000 -Dorg.gradle.internal.http.socketTimeout=120000"

echo "开始构建项目..."
echo "注意：首次构建需要下载依赖，可能需要 10-30 分钟"
echo "建议在网络稳定的环境下运行"
echo ""

# 运行构建
/workspace/gradle-8.7/bin/gradle assembleDebug \
    --stacktrace \
    --info \
    2>&1 | tee /workspace/build.log

# 检查构建结果
if [ $? -eq 0 ]; then
    echo ""
    echo "================================"
    echo "✅ 构建成功！"
    echo "================================"
    echo "APK 位置: /workspace/app/build/outputs/apk/debug/app-debug.apk"
    ls -lh /workspace/app/build/outputs/apk/debug/app-debug.apk 2>/dev/null || echo "APK 文件不存在"
else
    echo ""
    echo "================================"
    echo "❌ 构建失败"
    echo "================================"
    echo "请查看构建日志: /workspace/build.log"
    echo "常见问题："
    echo "1. 网络超时 - 请确保网络连接稳定"
    echo "2. 依赖下载失败 - 可以重试构建"
    echo "3. 内存不足 - 增加 JVM 内存: export GRADLE_OPTS=\"-Xmx4g ...\""
fi
