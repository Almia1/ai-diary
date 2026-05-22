#!/bin/bash

echo "========================================"
echo "📱 记账笔记 - 构建状态报告"
echo "========================================"
echo ""
date
echo ""

# 检查 Gradle 进程
if pgrep -f "gradle" > /dev/null; then
    echo "✅ Gradle 构建正在进行中..."
    echo ""
    
    # 检查缓存大小
    if [ -d "$HOME/.gradle/caches" ]; then
        CACHE_SIZE=$(du -sh $HOME/.gradle/caches 2>/dev/null | cut -f1)
        echo "📥 已下载缓存: $CACHE_SIZE"
    fi
    
    # 检查 build 目录
    if [ -d "/workspace/app/build" ]; then
        echo "📂 构建目录已创建"
        
        # 检查是否有编译的文件
        CLASS_COUNT=$(find /workspace/app/build -name "*.class" 2>/dev/null | wc -l)
        if [ "$CLASS_COUNT" -gt 0 ]; then
            echo "📦 已编译 $CLASS_COUNT 个类"
        fi
        
        # 检查是否有 APK
        if [ -f "/workspace/app/build/outputs/apk/debug/app-debug.apk" ]; then
            APK_SIZE=$(du -h /workspace/app/build/outputs/apk/debug/app-debug.apk | cut -f1)
            echo ""
            echo "🎉 构建成功！APK 已生成！"
            echo "📦 位置: /workspace/app/build/outputs/apk/debug/app-debug.apk"
            echo "📏 大小: $APK_SIZE"
        fi
    fi
else
    echo "⚠️  未检测到 Gradle 进程"
    echo ""
    echo "检查是否有构建日志..."
    if [ -f "/workspace/build_detailed.log" ]; then
        LOG_LINES=$(wc -l /workspace/build_detailed.log | cut -d' ' -f1)
        echo "📝 构建日志有 $LOG_LINES 行"
        echo ""
        echo "最后20行日志："
        echo "-------------------------"
        tail -20 /workspace/build_detailed.log
    fi
fi

echo ""
echo "========================================"
echo "💡 下一步："
echo "========================================"
echo ""
echo "1. 如果构建正在运行：请耐心等待..."
echo "2. 如果构建已完成：查看 APK 是否生成"
echo "3. 如果构建失败：运行 /workspace/build_full.sh 重试"
echo ""
echo "运行此命令再次检查状态："
echo "  /workspace/status.sh"
echo ""
