#!/bin/bash

# GitHub仓库推送脚本
# 请将 YOUR_GITHUB_TOKEN 替换为您的实际GitHub Personal Access Token

echo "开始推送银行信贷额度管控平台到GitHub..."

# 设置GitHub令牌
export GITHUB_TOKEN="YOUR_GITHUB_TOKEN"

# 进入项目目录
cd ~/workspace/credit-quota-management-platform

# 设置git配置
git config --local user.name "jenkov"
git config --local user.email "jenkov@example.com"

# 验证GitHub认证
echo "验证GitHub认证..."
if ! gh auth status; then
    echo "GitHub未认证，正在进行认证..."
    # 由于无法交互式认证，我们将使用令牌直接推送
    echo "请先运行: gh auth login (然后按照提示输入令牌)"
    exit 1
else
    echo "GitHub认证成功"
fi

# 创建GitHub仓库
echo "创建GitHub仓库..."
gh repo create jenkov/credit-quota-management-platform --public --description="银行信贷额度管控平台 - 基于Spring Boot实现额度管理、分配、使用、监控等功能"

# 添加远程仓库
git remote add origin https://github.com/jenkov/credit-quota-management-platform.git

# 推送代码
echo "推送代码到GitHub..."
git push -u origin main

echo "项目已成功推送至: https://github.com/jenkov/credit-quota-management-platform"
echo "推送完成！"