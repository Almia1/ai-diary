#!/bin/bash

echo "========================================"
echo "📄 构建日志检查"
echo "========================================"
echo ""

if [ -f "/workspace/build_detailed.log" ]; then
    LOG_SIZE=$(du -h /workspace/build_detailed.log | cut -f1)
    LOG_LINES=$(wc -l /workspace/build_detailed.log | cut -d' ' -f1)
    echo "📝 构建日志：$LOG_LINES 行，$LOG_SIZE"
    echo ""
    echo "-------------------------"
    echo "最后50行日志"
    echo "-------------------------"
    tail -50 /workspace/build_detailed.log
else
    echo "❌ 构建日志文件不存在"
fi

echo ""
echo "========================================"
echo "📁 检查 build 目录"
echo "========================================"
if [ -d "/workspace/app/build" ]; then
    echo "✅ build 目录存在"
    echo ""
    echo "目录结构："
    ls -la /workspace/app/build 2>/dev/null || echo "无法列出目录"
    
    if [ -d "/workspace/app/build/intermediates" ]; then
        echo ""
        echo "📦 找到中间文件"
        ls -la /workspace/app/build/intermediates | head -20
    fi
else
    echo "❌ build 目录不存在"
fi
