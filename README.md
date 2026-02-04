# 银行信贷额度管控平台

基于Spring Boot的银行信贷额度管控平台，实现额度管理、分配、使用、监控、风险控制等功能。

## 项目特性

- 高性能额度管控引擎
- 实时额度查询与占用
- 分布式锁保障并发安全
- 完整的额度交易记录
- 灵活的额度类型管理
- 支持集团额度与单一客户额度层级管理
- 额度启用/停用功能
- 风险监控与预警机制
- 审批流程管理
- 用信申请管理
- 客户关联方管理
- 额度预占（预留）机制

## 核心功能

### 额度管理
- **额度类型管理**（授信额度、产品额度、担保额度、临时额度）
- **额度分配与调整**
- **额度占用与释放**
- **额度冻结与解冻**
- **额度回收**
- **额度启用/停用**
- **集团额度管理**（支持集团额度与成员额度的层级关系）
- **额度预占机制**（支持业务预约和锁定）
- **额度实时监控**

### 风险管理
- **风险监控指标管理**（支持多种风险指标的实时监控）
- **风险预警管理**（自动触发预警并支持人工处理）
- **风险分级管理**（支持低、中、高、危急四级风险等级）
- **风险处置跟踪**（风险事件的全程跟踪管理）

### 审批流程
- **审批流程管理**（完整的审批流程生命周期管理）
- **审批节点管理**（支持多级审批节点）
- **审批进度跟踪**（实时审批进度跟踪）
- **审批结果管理**（审批结果记录和统计）

### 用信管理
- **用信申请管理**（完整的用信申请流程）
- **额度使用明细**（详细的额度使用记录）
- **资金用途跟踪**（资金使用情况跟踪）

### 客户管理
- **客户信息管理**（客户基本信息管理）
- **客户关联方管理**（客户关联方信息管理）
- **客户等级管理**（客户等级分类管理）

## 数据库设计

### 表层级结构

系统采用分层架构设计：

1. **客户层级（Customer Layer）**
   - t_customer_info - 客户基础信息表
   - t_group_relationship - 集团客户关系表
   - t_customer_affiliate - 客户关联方表

2. **额度管理层（Quota Management Layer）**
   - t_credit_quota - 授信额度表
   - t_credit_application - 授信申请表
   - t_usage_application - 用信申请表
   - t_quota_usage_detail - 额度使用明细表

3. **风险监控层（Risk Monitoring Layer）**
   - t_risk_monitoring_index - 风险监控指标表
   - t_risk_warning - 风险预警表

4. **流程管理层（Process Management Layer）**
   - t_approval_process - 审批流程表
   - t_approval_node - 审批节点表

5. **系统配置层（System Configuration Layer）**
   - t_sys_param_config - 系统参数配置表

### 主要外键关系

- t_customer_info ←→ t_group_relationship (parent_customer_id/child_customer_id)
- t_customer_info ←→ t_customer_affiliate (customer_id/affiliate_id)
- t_customer_info ←→ t_credit_application (customer_id)
- t_customer_info ←→ t_credit_quota (customer_id)
- t_customer_info ←→ t_usage_application (customer_id)
- t_customer_info ←→ t_quota_usage_detail (customer_id)
- t_customer_info ←→ t_risk_monitoring_index (customer_id)
- t_customer_info ←→ t_risk_warning (customer_id)
- t_credit_quota ←→ t_quota_usage_detail (quota_id)
- t_credit_quota ←→ t_usage_application (quota_id)
- t_credit_quota ←→ t_credit_quota (parent_quota_id) - 自关联
- t_credit_application ←→ t_usage_application (application_id)
- t_approval_process ←→ t_approval_node (process_id)

### 业务流程关系

```
客户申请授信 → t_credit_application
              ↓
    审批通过 → t_credit_quota (创建额度)
              ↓
    客户用信 → t_usage_application
              ↓
    占用额度 → t_quota_usage_detail (记录占用明细)
              ↓
    风险监控 → t_risk_monitoring_index (持续监控)
              ↓
    预警触发 → t_risk_warning (生成预警)
```

### 性能优化

- 已在关键字段上建立索引
- 额度使用明细表支持按时间分区
- 采用Redis分布式锁保证并发安全

## 技术栈

- Spring Boot 3.2+
- MyBatis 3.0+
- MySQL 8.0+
- Redis (分布式缓存与锁)
- Redisson (分布式锁)
- Lombok
- Maven

## 数据库表结构

- **t_customer_info** - 客户信息表
- **t_group_relationship** - 集团客户关系表
- **t_customer_affiliate** - 客户关联方表
- **t_credit_application** - 授信申请表
- **t_credit_quota** - 授信额度表
- **t_usage_application** - 用信申请表
- **t_quota_usage_detail** - 额度使用明细表
- **t_risk_monitoring_index** - 风险监控指标表
- **t_risk_warning** - 风险预警表
- **t_approval_process** - 审批流程表
- **t_approval_node** - 审批节点表
- **t_sys_param_config** - 系统参数配置表

## API 接口

### 客户管理接口
- `POST /api/unified-credit/customer` - 新增客户信息
- `PUT /api/unified-credit/customer` - 更新客户信息
- `GET /api/unified-credit/customer/{customerId}` - 查询客户信息
- `GET /api/unified-credit/customers` - 查询客户列表

### 集团关系管理接口
- `POST /api/unified-credit/group-relationship` - 新增集团关系
- `PUT /api/unified-credit/group-relationship` - 更新集团关系
- `GET /api/unified-credit/group-relationship/parent/{parentCustomerId}` - 查询集团下级关系
- `GET /api/unified-credit/group-relationship/child/{childCustomerId}` - 查询集团上级关系

### 客户关联方管理接口
- `POST /api/unified-credit/customer-affiliate` - 新增客户关联方
- `PUT /api/unified-credit/customer-affiliate` - 更新客户关联方
- `GET /api/unified-credit/customer-affiliate/customer/{customerId}` - 查询客户关联方

### 授信申请管理接口
- `POST /api/unified-credit/credit-application` - 提交授信申请
- `PUT /api/unified-credit/credit-application` - 更新授信申请
- `GET /api/unified-credit/credit-application/{applicationId}` - 查询授信申请
- `GET /api/unified-credit/credit-application/customer/{customerId}` - 查询客户授信申请列表

### 用信申请管理接口
- `POST /api/unified-credit/usage-application` - 提交用信申请
- `PUT /api/unified-credit/usage-application` - 更新用信申请
- `GET /api/unified-credit/usage-application/{usageId}` - 查询用信申请
- `GET /api/unified-credit/usage-application/customer/{customerId}` - 查询客户用信申请列表

### 额度管理接口
- `POST /api/unified-credit/credit-quota` - 核定授信额度
- `PUT /api/unified-credit/credit-quota` - 更新授信额度
- `GET /api/unified-credit/credit-quota/{quotaId}` - 查询授信额度
- `GET /api/unified-credit/credit-quota/customer/{customerId}/type/{quotaType}` - 根据客户和额度类型查询
- `GET /api/unified-credit/credit-quota/customer/{customerId}` - 查询客户所有额度

### 额度使用明细接口
- `POST /api/unified-credit/quota-usage-detail` - 记录额度使用明细
- `GET /api/unified-credit/quota-usage-detail/quota/{quotaId}` - 查询额度使用明细

### 额度管控接口
- `POST /api/unified-credit/quota/check` - 检查额度是否充足
- `POST /api/unified-credit/quota/occupy` - 占用额度
- `POST /api/unified-credit/quota/release` - 释放额度
- `POST /api/unified-credit/quota/pre-occupy` - 额度预占
- `POST /api/unified-credit/quota/cancel-pre-occupy` - 取消预占
- `POST /api/unified-credit/quota/freeze` - 冻结额度
- `POST /api/unified-credit/quota/unfreeze` - 解冻额度
- `POST /api/unified-credit/quota/enable` - 启用额度
- `POST /api/unified-credit/quota/disable` - 停用额度
- `POST /api/unified-credit/quota/adjust` - 额度调整
- `POST /api/unified-credit/quota/distribute-group` - 集团额度分配

### 额度查询接口
- `GET /api/unified-credit/quota/customer/{customerId}/total` - 获取客户总额度信息
- `GET /api/unified-credit/quota/group/{groupId}/total` - 获取集团总额度信息
- `GET /api/unified-credit/quota/group/{groupId}/members` - 获取集团成员额度汇总

### 风险监控接口
- `POST /api/unified-credit/risk-monitoring-index` - 新增风险监控指标
- `PUT /api/unified-credit/risk-monitoring-index` - 更新风险监控指标
- `GET /api/unified-credit/risk-monitoring-index/customer/{customerId}` - 查询客户风险监控指标
- `GET /api/unified-credit/risk-monitoring-index/customer/{customerId}/check` - 检查客户风险指标

### 风险预警接口
- `POST /api/unified-credit/risk-warning` - 新增风险预警
- `PUT /api/unified-credit/risk-warning` - 更新风险预警
- `GET /api/unified-credit/risk-warning/customer/{customerId}` - 查询客户风险预警
- `PUT /api/unified-credit/risk-warning/{warningId}/handle` - 处理风险预警

### 审批流程接口
- `POST /api/unified-credit/approval-process` - 启动审批流程
- `PUT /api/unified-credit/approval-process` - 更新审批流程
- `GET /api/unified-credit/approval-process/{processId}` - 查询审批流程
- `PUT /api/unified-credit/approval-node/{nodeId}/complete` - 完成审批节点
- `GET /api/unified-credit/approval-node/process/{processId}` - 查询审批节点

## 额度管控平台设计说明

此项目根据银行信贷额度管控需求设计，重点关注额度管理的核心功能：

1. **额度类型管理** - 支持多种额度类型（授信、产品、担保、临时）
2. **实时额度控制** - 高性能的额度查询与占用释放
3. **额度分配与调剂** - 灵活的额度分配策略和调剂机制
4. **额度动态调整** - 支持自动和手动额度调整
5. **高并发处理** - 基于Redis的分布式锁机制
6. **交易记录** - 完整的额度变动记录
7. **风险控制** - 多维度的风险控制和预警机制
8. **集团额度管理** - 支持集团额度与成员额度的层级关系管理
9. **额度启停控制** - 支持额度的启用和停用功能
10. **额度预占机制** - 支持业务预约和锁定功能
11. **审批流程管理** - 完整的审批流程生命周期管理
12. **用信申请管理** - 完整的用信申请流程管理
13. **客户关联方管理** - 客户关联方信息管理
14. **风险监控预警** - 实时风险监控和预警机制

## 项目架构

本项目采用分层微服务架构设计，主要包括以下几个层面：

### 系统架构图
```
┌─────────────────────────────────────────────────────────────┐
│                        表现层 (Presentation Layer)          │
│                     (Controller/Rest API)                   │
├─────────────────────────────────────────────────────────────┤
│                        业务逻辑层 (Business Logic Layer)    │
│                    (Service/Business Logic)                 │
├─────────────────────────────────────────────────────────────┤
│                        数据访问层 (Data Access Layer)       │
│                      (Repository/DAO/MyBatis)               │
├─────────────────────────────────────────────────────────────┤
│                        数据存储层 (Data Storage Layer)      │
│                    (MySQL/Redis/External Services)          │
└─────────────────────────────────────────────────────────────┘
```

### 各层详细设计
请参阅 [LAYER_DESIGN_DOCUMENTATION.md](LAYER_DESIGN_DOCUMENTATION.md) 了解各层的详细设计说明。

### 微服务模块
- 统一额度管控服务
- 风险监控服务
- 审批流程服务
- 客户信息服务

## 部署说明

1. 确保安装了Java 17+, Maven 3.6+, MySQL 8.0+, Redis
2. 配置application.properties中的数据库连接信息
3. 执行数据库初始化脚本
4. 运行 `mvn spring-boot:run` 启动应用