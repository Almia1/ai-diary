import os
import json
import sys
import re

# ========================================
# 彻底禁用代理设置 - 必须在导入其他库之前
# ========================================
def disable_proxies():
    # 清除所有代理相关的环境变量
    proxy_keys = [
        'HTTP_PROXY', 'HTTPS_PROXY', 'http_proxy', 'https_proxy',
        'ALL_PROXY', 'all_proxy', 'FTP_PROXY', 'ftp_proxy',
        'SOCKS_PROXY', 'socks_proxy', 'NO_PROXY', 'no_proxy',
        'REQUESTS_CA_BUNDLE', 'CURL_CA_BUNDLE'
    ]
    
    for key in proxy_keys:
        if key in os.environ:
            del os.environ[key]
        if key.lower() in os.environ:
            del os.environ[key.lower()]
    
    # 设置 NO_PROXY 为 *
    os.environ['NO_PROXY'] = '*'
    os.environ['no_proxy'] = '*'
    
    # 清空代理设置
    os.environ['HTTP_PROXY'] = ''
    os.environ['HTTPS_PROXY'] = ''
    os.environ['http_proxy'] = ''
    os.environ['https_proxy'] = ''

# 执行代理禁用
disable_proxies()

# 修复控制台编码问题
if sys.platform == 'win32':
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8', errors='replace')

# 现在导入其他库
import requests

# 配置 requests 库完全不使用代理
def configure_requests():
    # 禁用 requests 的自动代理检测
    requests.utils.default_user_agent = lambda: 'Python/3.8'
    
    # 创建一个不使用代理的 session 类
    original_get_adapter = requests.sessions.Session.get_adapter
    
    def no_proxy_get_adapter(self, url):
        adapter = original_get_adapter(self, url)
        adapter.proxy_manager = {}
        return adapter
    
    requests.sessions.Session.get_adapter = no_proxy_get_adapter

configure_requests()

from dashscope import MultiModalConversation

# 你的阿里云API Key
API_KEY = "sk-5f9e17ec553942baa29732066071f74a"

def extract_json(text):
    """从文本中提取 JSON 数组"""
    # 先清理 markdown 代码块标记
    text = text.replace("```json", "").replace("```", "").strip()
    
    # 尝试找到第一个 [ 到最后一个 ] 之间的内容
    try:
        json_start = text.find('[')
        json_end = text.rfind(']')
        
        if json_start != -1 and json_end != -1 and json_end > json_start:
            json_str = text[json_start:json_end+1]
            return json.loads(json_str)
    except:
        pass
    
    # 如果上面方法失败，尝试用正则表达式提取
    try:
        pattern = r'\[[\s\S]*\]'
        match = re.search(pattern, text)
        if match:
            return json.loads(match.group(0))
    except:
        pass
    
    return None

def recognize_bill(img_path):
    try:
        if not os.path.exists(img_path):
            print(f"错误：图片文件不存在 - {img_path}")
            return None

        # 使用文件路径格式而不是直接传递字节
        messages = [
            {
                "role": "user",
                "content": [
                    {"image": f"file://{os.path.abspath(img_path)}"},
                    {"text": "请识别这张微信或支付宝账单，提取交易记录。只返回JSON数组，不要任何其他文字说明。JSON格式：[{\"交易时间\": \"\", \"交易类型\": \"\", \"交易金额\": \"\", \"交易对方\": \"\", \"备注\": \"\", \"支付渠道\": \"\"}]"}
                ]
            }
        ]

        response = MultiModalConversation.call(
            model="qwen-vl-plus",
            messages=messages,
            api_key=API_KEY
        )

        if response.status_code == 200:
            content = response.output.choices[0].message.content
            text = content[0]["text"]
            
            # 尝试清理和提取 JSON
            result = extract_json(text)
            
            if result:
                return result
            else:
                print("无法从返回内容中提取 JSON")
                return None
        else:
            print("调用失败：", response.message)
            return None

    except Exception as e:
        print("异常信息：", e)
        import traceback
        traceback.print_exc()
        return None

if __name__ == "__main__":
    # 检查项目目录下的图片文件
    print("当前目录下的图片文件：")
    img_files = []
    for file in os.listdir('.'):
        if file.lower().endswith(('.jpg', '.jpeg', '.png', '.bmp')):
            print(f"  - {file}")
            img_files.append(file)
    
    if img_files:
        # 询问用户要识别哪个图片
        print("\n请选择要识别的图片：")
        for i, file in enumerate(img_files, 1):
            print(f"  {i}. {file}")
        print(f"  {len(img_files)+1}. 识别所有图片")
        
        try:
            choice = input(f"\n请输入选项 (1-{len(img_files)+1})，或直接回车默认识别第一个：").strip()
            
            if not choice:
                choice = "1"
            
            choice_num = int(choice)
            
            if 1 <= choice_num <= len(img_files):
                # 识别单个图片
                img_path = img_files[choice_num - 1]
                print(f"\n正在识别：{img_path}")
                result = recognize_bill(img_path)
                if result:
                    # 保存结果到文件（UTF-8 编码）
                    output_file = f"result_{os.path.splitext(img_path)[0]}.json"
                    with open(output_file, 'w', encoding='utf-8') as f:
                        json.dump(result, f, indent=2, ensure_ascii=False)
                    
                    print(f"\n识别完成！结果已保存到：{output_file}")
                    print("\n识别结果：")
                    print(json.dumps(result, indent=2, ensure_ascii=False))
            
            elif choice_num == len(img_files) + 1:
                # 识别所有图片
                all_results = []
                for img_path in img_files:
                    print(f"\n正在识别：{img_path}")
                    result = recognize_bill(img_path)
                    if result:
                        # 添加来源文件名
                        for item in result:
                            item['_来源'] = img_path
                        all_results.extend(result)
                        
                        # 保存单个文件的结果
                        output_file = f"result_{os.path.splitext(img_path)[0]}.json"
                        with open(output_file, 'w', encoding='utf-8') as f:
                            json.dump(result, f, indent=2, ensure_ascii=False)
                        print(f"结果已保存到：{output_file}")
                
                # 保存所有结果到一个文件
                if all_results:
                    output_file = "all_bills_result.json"
                    with open(output_file, 'w', encoding='utf-8') as f:
                        json.dump(all_results, f, indent=2, ensure_ascii=False)
                    
                    print(f"\n全部识别完成！共 {len(all_results)} 条记录")
                    print(f"合并结果已保存到：{output_file}")
                    print("\n所有识别结果：")
                    print(json.dumps(all_results, indent=2, ensure_ascii=False))
            
            else:
                print("无效选项！")
        
        except ValueError:
            print("输入无效，默认识别第一个图片...")
            img_path = img_files[0]
            print(f"\n正在识别：{img_path}")
            result = recognize_bill(img_path)
            if result:
                output_file = f"result_{os.path.splitext(img_path)[0]}.json"
                with open(output_file, 'w', encoding='utf-8') as f:
                    json.dump(result, f, indent=2, ensure_ascii=False)
                
                print(f"\n识别完成！结果已保存到：{output_file}")
                print("\n识别结果：")
                print(json.dumps(result, indent=2, ensure_ascii=False))
    
    else:
        print("\n未找到任何图片文件！请将账单截图放在此目录中。")
