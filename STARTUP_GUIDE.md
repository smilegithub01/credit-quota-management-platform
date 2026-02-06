# 银行信贷额度管控平台 - 启动验证指南

## 项目概述
这是一个基于Spring Boot的银行信贷额度管控平台，实现了完整的额度管理、风险控制、审批流程等功能。

## 系统要求

### 必需组件
1. **Java 17+** - 运行Spring Boot应用
2. **Maven 3.6+** - 项目构建工具
3. **MySQL 8.0+** - 主数据库
4. **Redis 6.0+** - 分布式缓存和锁

### 可选组件
1. **Postman/Insomnia** - API测试工具
2. **Docker** - 容器化部署（可选）

## 启动步骤

### 第一步：环境准备

#### 1. 安装Java 17+
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# 验证安装
java -version
```

#### 2. 安装Maven
```bash
# Ubuntu/Debian
sudo apt install maven

# 验证安装
mvn -version
```

#### 3. 安装MySQL 8.0+
```bash
# Ubuntu/Debian
sudo apt install mysql-server

# 启动MySQL
sudo systemctl start mysql
sudo systemctl enable mysql

# 安全配置（可选）
sudo mysql_secure_installation
```

#### 4. 安装Redis
```bash
# Ubuntu/Debian
sudo apt install redis-server

# 启动Redis
sudo systemctl start redis
sudo systemctl enable redis

# 验证Redis是否运行
redis-cli ping
```

### 第二步：数据库配置

#### 1. 创建数据库
```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE credit_quota_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2. 执行数据库初始化脚本
```bash
# 进入项目目录
cd ~/workspace/credit-quota-management-platform

# 执行SQL脚本
mysql -u root -p credit_quota_db < src/main/resources/db/schema.sql
```

#### 3. 配置数据库连接
编辑 `src/main/resources/application.properties`：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/credit_quota_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 第三步：项目构建

#### 1. 下载依赖
```bash
cd ~/workspace/credit-quota-management-platform
mvn clean compile
```

#### 2. 打包项目
```bash
mvn clean package -DskipTests
```

### 第四步：启动应用

#### 1. 直接启动
```bash
mvn spring-boot:run
```

#### 2. 或者使用打包后的JAR
```bash
java -jar target/credit-quota-management-platform-1.0.0.jar
```

### 第五步：验证启动

#### 1. 检查应用状态
应用默认运行在 `http://localhost:8080`

#### 2. 访问健康检查端点
```bash
curl http://localhost:8080/actuator/health
```

预期响应：
```json
{
  "status": "UP"
}
```

#### 3. 访问API文档端点
```bash
curl http://localhost:8080/actuator/info
```

## API测试验证

### 1. 客户管理测试

#### 创建客户
```bash
curl -X POST http://localhost:8080/api/unified-credit/customer \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST001",
    "customerName": "测试客户",
    "customerType": "ENTERPRISE",
    "customerLevel": "A",
    "legalRepresentative": "张三",
    "phone": "13800138001",
    "status": "NORMAL"
  }'
```

#### 查询客户
```bash
curl http://localhost:8080/api/unified-credit/customer/CUST001
```

### 2. 额度管理测试

#### 创建授信额度
```bash
curl -X POST http://localhost:8080/api/unified-credit/credit-quota \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST001",
    "quotaType": "CREDIT",
    "totalQuota": 1000000.00,
    "currency": "CNY",
    "quotaLevel": "GROUP",
    "effectiveDate": "2026-02-06",
    "expireDate": "2027-02-06",
    "quotaStatus": "ACTIVE"
  }'
```

#### 占用额度
```bash
curl -X POST http://localhost:8080/api/unified-credit/quota/occupy \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST001",
    "quotaType": "CREDIT",
    "amount": 50000.00,
    "referenceId": "TXN001"
  }'
```

#### 查询额度
```bash
curl http://localhost:8080/api/unified-credit/credit-quota/customer/CUST001
```

### 3. 风险监控测试

#### 创建风险指标
```bash
curl -X POST http://localhost:8080/api/unified-credit/risk-monitoring-index \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST001",
    "indexName": "资产负债率",
    "indexValue": 0.65,
    "threshold": 0.70,
    "riskLevel": "MEDIUM",
    "monitoringFrequency": "DAILY"
  }'
```

#### 检查风险指标
```bash
curl http://localhost:8080/api/unified-credit/risk-monitoring-index/customer/CUST001/check
```

### 4. 审批流程测试

#### 启动审批流程
```bash
curl -X POST http://localhost:8080/api/unified-credit/approval-process \
  -H "Content-Type: application/json" \
  -d '{
    "processName": "授信申请审批",
    "applicationId": "APP001",
    "applicant": "CUST001",
    "processType": "CREDIT_APPROVAL",
    "priority": "MEDIUM"
  }'
```

## 常见问题排查

### 1. 数据库连接失败
**问题**：`Communications link failure`
**解决方案**：
- 检查MySQL是否运行：`sudo systemctl status mysql`
- 检查端口：`netstat -tlnp | grep 3306`
- 检查防火墙：`sudo ufw status`

### 2. Redis连接失败
**问题**：`Connection refused`
**解决方案**：
- 检查Redis是否运行：`sudo systemctl status redis`
- 检查端口：`netstat -tlnp | grep 6379`
- 检查配置：`redis-cli config get bind`

### 3. 端口被占用
**问题**：`Port 8080 is already in use`
**解决方案**：
```bash
# 查找占用端口的进程
sudo lsof -i :8080

# 或者修改application.properties中的server.port
```

### 4. Maven依赖下载失败
**问题**：`Could not resolve dependencies`
**解决方案**：
```bash
# 清理本地仓库
mvn clean

# 强制更新依赖
mvn clean compile -U
```

## 项目结构说明

```
credit-quota-management-platform/
├── src/main/java/com/bank/creditquota/
│   ├── controller/          # 控制器层
│   ├── service/            # 服务层
│   ├── entity/             # 实体类
│   ├── mapper/             # MyBatis映射器
│   └── CreditQuotaManagementApplication.java  # 启动类
├── src/main/resources/
│   ├── application.properties  # 配置文件
│   ├── db/schema.sql       # 数据库初始化脚本
│   └── mapper/*.xml        # MyBatis XML映射文件
├── pom.xml                 # Maven配置
└── README.md               # 项目说明
```

## 性能优化建议

### 1. 数据库优化
- 为高频查询字段建立索引
- 考虑分表分库（当数据量超过千万级）
- 使用读写分离

### 2. 缓存优化
- 合理配置Redis缓存策略
- 使用Redis集群提高可用性

### 3. 应用优化
- 配置连接池参数
- 启用异步处理
- 使用消息队列解耦

## 安全建议

### 1. 生产环境配置
- 修改默认端口
- 配置HTTPS
- 设置强密码
- 启用访问控制

### 2. 数据安全
- 定期备份数据库
- 启用数据加密
- 设置访问日志

## 监控与维护

### 1. 应用监控
- 访问 `/actuator/health` 查看健康状态
- 访问 `/actuator/metrics` 查看性能指标
- 访问 `/actuator/info` 查看应用信息

### 2. 日志管理
- 日志路径：`logs/application.log`
- 日志级别：可在 `application.properties` 中配置

## 下一步

1. **功能测试**：使用Postman测试所有API接口
2. **性能测试**：使用JMeter进行压力测试
3. **安全测试**：进行安全漏洞扫描
4. **集成测试**：与前端应用集成测试

## 技术支持

如有问题，请参考：
- Spring Boot官方文档：https://spring.io/projects/spring-boot
- MyBatis官方文档：https://mybatis.org/mybatis-3/
- Redis官方文档：https://redis.io/documentation