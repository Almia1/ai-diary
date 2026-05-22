#!/bin/bash
# 环境检查脚本

echo "================================"
echo "记账笔记 - 环境检查"
echo "================================"
echo ""

# 检查 Java
echo "1. 检查 Java 环境..."
export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
export PATH=$JAVA_HOME/bin:$PATH
if java -version 2>&1 | grep -q "17"; then
    echo "   ✅ Java 17 已安装"
    java -version 2>&1 | head -1
else
    echo "   ❌ Java 17 未找到"
    java -version 2>&1 | head -1
fi
echo ""

# 检查 Android SDK
echo "2. 检查 Android SDK..."
if [ -d "$HOME/android-sdk" ]; then
    echo "   ✅ Android SDK 已安装: $HOME/android-sdk"
    if [ -d "$HOME/android-sdk/platforms/android-34" ]; then
        echo "   ✅ Android 34 平台已安装"
    else
        echo "   ⚠️  Android 34 平台未安装"
    fi
    if [ -d "$HOME/android-sdk/build-tools" ]; then
        echo "   ✅ Build Tools 已安装"
    else
        echo "   ⚠️  Build Tools 未安装"
    fi
else
    echo "   ❌ Android SDK 未找到"
fi
echo ""

# 检查 Gradle
echo "3. 检查 Gradle..."
if [ -f "/workspace/gradle-8.7/bin/gradle" ]; then
    echo "   ✅ Gradle 8.7 已安装"
    /workspace/gradle-8.7/bin/gradle --version | head -3
else
    echo "   ❌ Gradle 未找到"
fi
echo ""

# 检查项目文件
echo "4. 检查项目文件..."
files_to_check=(
    "/workspace/app/src/main/java/com/diarybook/util/BillOcrRecognizer.kt"
    "/workspace/app/src/main/java/com/diarybook/ui/screen/OcrRecognitionScreen.kt"
    "/workspace/app/src/main/java/com/diarybook/data/model/BillRecognitionResult.kt"
    "/workspace/app/build.gradle.kts"
    "/workspace/local.properties"
)

all_present=true
for file in "${files_to_check[@]}"; do
    if [ -f "$file" ]; then
        echo "   ✅ $(basename $file)"
    else
        echo "   ❌ $(basename $file) - 缺失"
        all_present=false
    fi
done
echo ""

# 检查环境变量文件
echo "5. 检查配置文件..."
if [ -f "/workspace/local.properties" ]; then
    echo "   ✅ local.properties 已创建"
    echo "   SDK 路径: $(grep sdk.dir /workspace/local.properties)"
else
    echo "   ❌ local.properties 缺失"
fi

if [ -f "/workspace/set_env.sh" ]; then
    echo "   ✅ set_env.sh 已创建"
else
    echo "   ❌ set_env.sh 缺失"
fi

if [ -f "/workspace/build.sh" ]; then
    echo "   ✅ build.sh 已创建"
else
    echo "   ❌ build.sh 缺失"
fi
echo ""

# 总结
echo "================================"
echo "环境状态总结"
echo "================================"
if java -version 2>&1 | grep -q "17" && [ -d "$HOME/android-sdk" ] && [ -f "/workspace/gradle-8.7/bin/gradle" ]; then
    echo "✅ 环境配置基本完成！"
    echo ""
    echo "下一步："
    echo "1. 设置环境变量: source /workspace/set_env.sh"
    echo "2. 构建项目: /workspace/build.sh"
    echo "3. 等待依赖下载完成（首次构建需要网络）"
else
    echo "⚠️  环境配置不完整，请检查上述问题"
fi
echo ""
