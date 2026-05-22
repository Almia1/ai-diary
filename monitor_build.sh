#!/bin/bash
echo "========================================"
echo "构建进度监控"
echo "========================================"
echo ""

# 检查是否有 Gradle 进程
if pgrep -f "gradle" > /dev/null; then
    echo "✅ Gradle 进程正在运行"
    
    # 检查 build 目录
    if [ -d "/workspace/app/build" ]; then
        echo "📂 构建目录已创建"
        
        # 检查中间文件
        if [ -d "/workspace/app/build/intermediates" ]; then
            echo "🔧 中间文件正在生成..."
            
            # 检查是否有已编译的类文件
            CLASS_COUNT=$(find /workspace/app/build -name "*.class" 2>/dev/null | wc -l)
            if [ "$CLASS_COUNT" -gt 0 ]; then
                echo "📦 已编译 $CLASS_COUNT 个类文件"
            fi
        fi
        
        # 检查下载的依赖
        if [ -d "$HOME/.gradle/caches" ]; then
            CACHE_SIZE=$(du -sh $HOME/.gradle/caches 2>/dev/null | cut -f1)
            echo "📥 缓存大小: $CACHE_SIZE"
        fi
    fi
else
    echo "⚠️  未检测到 Gradle 进程"
fi

echo ""
echo "检查 build 输出目录..."
if [ -d "/workspace/app/build/outputs" ]; then
    echo "✅ build/outputs 目录存在"
    ls -la /workspace/app/build/outputs 2>/dev/null || echo "  目录为空"
else
    echo "❌ build/outputs 目录不存在"
fi

echo ""
echo "========================================"
date
