#!/bin/bash
# 云端部署助手脚本

clear
echo "================================================"
echo "    记账笔记 - 云端部署助手"
echo "================================================"
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 检查环境
echo -e "${BLUE}[1/3] 检查环境...${NC}"
if ./check_env.sh | grep -q "环境配置基本完成"; then
    echo -e "${GREEN}✅ 环境检查通过${NC}"
else
    echo -e "${YELLOW}⚠️  环境可能有问题，但继续尝试...${NC}"
fi
echo ""

# 设置环境变量
echo -e "${BLUE}[2/3] 设置环境变量...${NC}"
source /workspace/set_env.sh
echo -e "${GREEN}✅ 环境变量已设置${NC}"
echo ""

# 构建选项菜单
echo -e "${BLUE}[3/3] 选择操作${NC}"
echo ""
echo "请选择您想做什么："
echo ""
echo "1) 检查环境状态"
echo "2) 尝试构建 APK (需要网络)"
echo "3) 查看云端部署方案"
echo "4) 退出"
echo ""
read -p "请输入选项 (1-4): " choice

case $choice in
    1)
        echo ""
        echo -e "${GREEN}=== 环境状态检查 ===${NC}"
        ./check_env.sh
        ;;
        
    2)
        echo ""
        echo -e "${GREEN}=== 开始构建 APK ===${NC}"
        echo "注意：首次构建需要下载依赖，请确保网络连接稳定"
        echo ""
        read -p "是否继续？(y/n): " confirm
        
        if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
            echo ""
            echo "开始构建..."
            echo "构建日志将保存到 build.log"
            echo ""
            
            ./build_with_log.sh
            
            if [ -f "/workspace/app/build/outputs/apk/debug/app-debug.apk" ]; then
                echo ""
                echo -e "${GREEN}========================================${NC}"
                echo -e "${GREEN}🎉 APK 构建成功！${NC}"
                echo -e "${GREEN}========================================${NC}"
                echo ""
                echo "APK 位置:"
                echo "  /workspace/app/build/outputs/apk/debug/app-debug.apk"
                echo ""
                echo "文件信息:"
                ls -lh /workspace/app/build/outputs/apk/debug/app-debug.apk
                echo ""
                echo "下一步："
                echo "1. 上传 APK 到云存储或文件托管服务"
                echo "2. 使用 Appetize.io 在线演示"
                echo "3. 查看 '云端部署指南.md' 获取更多方案"
                echo ""
            else
                echo ""
                echo -e "${RED}❌ 构建可能失败，请检查 build.log${NC}"
                echo ""
                echo "常见问题："
                echo "1. 网络超时 - 请确保网络连接稳定"
                echo "2. 依赖下载失败 - 可以重试构建"
                echo ""
            fi
        else
            echo "已取消构建"
        fi
        ;;
        
    3)
        echo ""
        echo -e "${GREEN}=== 云端部署方案 ===${NC}"
        echo ""
        cat /workspace/云端部署指南.md
        ;;
        
    4)
        echo ""
        echo "再见！"
        exit 0
        ;;
        
    *)
        echo -e "${RED}无效选项${NC}"
        exit 1
        ;;
esac

echo ""
echo "================================================"
echo "如需更多帮助，请查看："
echo "  /workspace/云端部署指南.md"
echo "  /workspace/环境搭建指南.md"
echo "  /workspace/OCR功能集成总结.md"
echo "================================================"
