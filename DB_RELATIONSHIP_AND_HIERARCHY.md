# 银行信贷额度管控平台 - 数据库表关系与层级说明文档

## 1. 系统概述

本系统是一个完整的银行信贷额度管控平台，采用分层架构设计，涵盖客户管理、集团关系、额度管控、风险监控、审批流程等多个维度。

## 2. 表层级结构

### 2.1 客户层级（Customer Layer）

#### 2.1.1 核心客户表
- **t_customer_info** - 客户基础信息表
  - 最顶层的客户实体，存储客户基本信息
  - 包含客户类型、等级、资质等关键信息

#### 2.1.2 集团关系表
- **t_group_relationship** - 集团客户关系表
  - 建立父子客户关系，形成集团层级结构
  - 支持多层级集团关系管理
  - 与t_customer_info建立外键关系

#### 2.1.3 关联方表
- **t_customer_affiliate** - 客户关联方表
  - 管理客户的关联方信息（股东、担保人、关联企业等）
  - 与t_customer_info建立外键关系

### 2.2 额度管理层（Quota Management Layer）

#### 2.2.1 额度定义表
- **t_credit_quota** - 授信额度表
  - 核心额度表，存储客户的具体授信额度
  - 与t_customer_info建立外键关系
  - 支持父子额度关系（parent_quota_id）

#### 2.2.2 额度申请表
- **t_credit_application** - 授信申请表
  - 管理额度申请流程
  - 与t_customer_info建立外键关系

#### 2.2.3 用信申请表
- **t_usage_application** - 用信申请表
  - 管理具体的用信申请
  - 与t_customer_info和t_credit_quota建立外键关系

#### 2.2.4 额度使用明细表
- **t_quota_usage_detail** - 额度使用明细表
  - 记录所有额度变动操作
  - 与t_credit_quota和t_customer_info建立外键关系

### 2.3 风险监控层（Risk Monitoring Layer）

#### 2.3.1 风险指标表
- **t_risk_monitoring_index** - 风险监控指标表
  - 存储各类风险指标数据
  - 与t_customer_info建立外键关系

#### 2.3.2 风险预警表
- **t_risk_warning** - 风险预警表
  - 管理风险预警信息
  - 与t_customer_info建立外键关系

### 2.4 流程管理层（Process Management Layer）

#### 2.4.1 审批流程表
- **t_approval_process** - 审批流程表
  - 管理各类业务的审批流程
  - 与具体业务表建立关联

#### 2.4.2 审批节点表
- **t_approval_node** - 审批节点表
  - 管理审批流程中的各个节点
  - 与t_approval_process建立外键关系

### 2.5 系统配置层（System Configuration Layer）

#### 2.5.1 参数配置表
- **t_sys_param_config** - 系统参数配置表
  - 管理系统所有配置参数
  - 不与其他业务表建立外键关系

## 3. 表关系详解

### 3.1 主要外键关系

```
t_customer_info (1) ←→ (N) t_group_relationship (parent_customer_id)
t_customer_info (1) ←→ (N) t_group_relationship (child_customer_id)
t_customer_info (1) ←→ (N) t_customer_affiliate (customer_id)
t_customer_info (1) ←→ (N) t_customer_affiliate (affiliate_id)
t_customer_info (1) ←→ (N) t_credit_application (customer_id)
t_customer_info (1) ←→ (N) t_credit_quota (customer_id)
t_customer_info (1) ←→ (N) t_usage_application (customer_id)
t_customer_info (1) ←→ (N) t_quota_usage_detail (customer_id)
t_customer_info (1) ←→ (N) t_risk_monitoring_index (customer_id)
t_customer_info (1) ←→ (N) t_risk_warning (customer_id)

t_credit_quota (1) ←→ (N) t_quota_usage_detail (quota_id)
t_credit_quota (1) ←→ (N) t_usage_application (quota_id)
t_credit_quota (1) ←→ (N) t_credit_quota (parent_quota_id) - 自关联

t_credit_application (1) ←→ (N) t_usage_application (application_id)

t_approval_process (1) ←→ (N) t_approval_node (process_id)
```

### 3.2 业务流程关系

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

## 4. 层级架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    系统配置层                               │
│                t_sys_param_config                           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   风险监控层                                │
│     t_risk_monitoring_index  ←→  t_risk_warning           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   流程管理层                                │
│         t_approval_process  ←→  t_approval_node            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   额度管理层                                │
│  t_credit_application → t_credit_quota → t_usage_application│
│                            │              ↓                │
│                            └→ t_quota_usage_detail         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   客户层级管理                              │
│  t_customer_info → t_group_relationship → t_customer_affiliate│
└─────────────────────────────────────────────────────────────┘
```

## 5. 数据流说明

### 5.1 客户创建流程
1. 在t_customer_info中创建客户基础信息
2. 如需建立集团关系，在t_group_relationship中建立关系
3. 如需管理关联方，在t_customer_affiliate中记录关联信息

### 5.2 额度申请流程
1. 客户提交授信申请 → t_credit_application
2. 审批通过后创建额度 → t_credit_quota
3. 额度变更操作 → t_quota_usage_detail

### 5.3 用信流程
1. 客户提交用信申请 → t_usage_application
2. 占用对应额度 → 更新t_credit_quota和t_quota_usage_detail
3. 生成风险指标 → t_risk_monitoring_index

### 5.4 风险管理流程
1. 系统监控风险指标 → t_risk_monitoring_index
2. 触发预警条件 → t_risk_warning
3. 审批人员处理预警 → t_approval_process/t_approval_node

## 6. 关键约束说明

### 6.1 数据完整性约束
- t_customer_info.customer_id: 主键，唯一标识客户
- t_credit_quota.customer_id: 外键，引用t_customer_info
- t_quota_usage_detail.quota_id: 外键，引用t_credit_quota
- t_group_relationship.parent_customer_id: 外键，引用t_customer_info

### 6.2 业务规则约束
- t_credit_quota.total_quota >= used_quota + frozen_quota
- t_credit_quota.available_quota = total_quota - used_quota - frozen_quota
- t_group_relationship的有效期必须在合理范围内
- t_credit_quota的到期日期必须晚于生效日期

### 6.3 状态流转约束
- t_credit_application.status: DRAFT → SUBMITTED → IN_REVIEW → APPROVED/REJECTED
- t_credit_quota.quota_status: ACTIVE → INACTIVE/FROZEN → ACTIVE
- t_risk_warning.warning_status: UNHANDLED → HANDLING → HANDLED/IGNORED/RESOLVED

## 7. 性能优化建议

### 7.1 索引优化
- t_customer_info: customer_name, customer_type, status
- t_credit_quota: customer_id, quota_type, quota_status, expire_date
- t_quota_usage_detail: quota_id, customer_id, transaction_time, transaction_type
- t_risk_warning: customer_id, risk_level, warning_status, warning_date

### 7.2 分区策略
- t_quota_usage_detail按transaction_time进行分区
- t_risk_warning按warning_date进行分区
- t_approval_node按created_time进行分区

## 8. 扩展性考虑

### 8.1 水平扩展
- 客户表可通过customer_id进行分片
- 额度表可通过customer_id进行分片
- 明细表可通过quota_id进行分片

### 8.2 功能扩展
- 新增额度类型可在t_sys_param_config中配置
- 新增审批流程可在t_approval_process中定义
- 新增风险指标可在t_risk_monitoring_index中扩展

这个数据库设计支持完整的银行信贷额度管控业务，具有良好的层级结构和清晰的关系，能够满足复杂的业务需求。