#!/bin/bash

# 银行信贷额度管控平台 - 快速启动脚本

echo "=========================================="
echo "银行信贷额度管控平台 - 启动脚本"
echo "=========================================="

# 检查Java
echo "1. 检查Java环境..."
java -version > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "❌ 未找到Java，请安装Java 17+"
    exit 1
fi
echo "✅ Java环境正常"

# 检查Maven
echo "2. 检查Maven环境..."
mvn -version > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "❌ 未找到Maven，请安装Maven 3.6+"
    exit 1
fi
echo "✅ Maven环境正常"

# 检查MySQL
echo "3. 检查MySQL服务..."
if ! systemctl is-active --quiet mysql; then
    echo "⚠️  MySQL服务未运行，尝试启动..."
    sudo systemctl start mysql
    if [ $? -ne 0 ]; then
        echo "❌ 启动MySQL失败，请手动启动"
        exit 1
    fi
fi
echo "✅ MySQL服务正常"

# 检查Redis
echo "4. 检查Redis服务..."
if ! systemctl is-active --quiet redis; then
    echo "⚠️  Redis服务未运行，尝试启动..."
    sudo systemctl start redis
    if [ $? -ne 0 ]; then
        echo "❌ 启动Redis失败，请手动启动"
        exit 1
    fi
fi
echo "✅ Redis服务正常"

# 检查数据库是否存在
echo "5. 检查数据库..."
mysql -u root -p -e "USE credit_quota_db;" > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "⚠️  数据库不存在，尝试创建..."
    mysql -u root -p -e "CREATE DATABASE credit_quota_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    if [ $? -ne 0 ]; then
        echo "❌ 创建数据库失败"
        exit 1
    fi
    echo "✅ 数据库创建成功"
    
    # 执行初始化脚本
    echo "6. 执行数据库初始化脚本..."
    mysql -u root -p credit_quota_db < src/main/resources/db/schema.sql
    if [ $? -ne 0 ]; then
        echo "❌ 数据库初始化失败"
        exit 1
    fi
    echo "✅ 数据库初始化成功"
else
    echo "✅ 数据库已存在"
fi

# 构建项目
echo "7. 构建项目..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ 项目构建失败"
    exit 1
fi
echo "✅ 项目构建成功"

# 启动应用
echo "8. 启动应用..."
echo "=========================================="
echo "应用启动中..."
echo "访问地址: http://localhost:8080"
echo "健康检查: http://localhost:8080/actuator/health"
echo "=========================================="

java -jar target/credit-quota-management-platform-1.0.0.jar