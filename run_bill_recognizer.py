import os
import sys
import subprocess

# 准备干净的环境变量
env = os.environ.copy()
keys_to_remove = []
for key in env.keys():
    if 'proxy' in key.lower():
        keys_to_remove.append(key)

for key in keys_to_remove:
    del env[key]

env['NO_PROXY'] = '*'
env['HTTP_PROXY'] = ''
env['HTTPS_PROXY'] = ''

# 运行主脚本
result = subprocess.run(
    [sys.executable, 'main.py'],
    env=env,
    cwd=os.path.dirname(os.path.abspath(__file__))
)

sys.exit(result.returncode)
