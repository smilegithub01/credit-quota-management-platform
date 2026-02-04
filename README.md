# 银行信贷额度管控平台

基于Spring Boot的银行信贷额度管控平台，实现额度管理、分配、使用、监控等功能。

## 项目特性

- 高性能额度管控引擎
- 实时额度查询与占用
- 分布式锁保障并发安全
- 完整的额度交易记录
- 灵活的额度类型管理
- 支持集团额度与单一客户额度层级管理
- 额度启用/停用功能

## 核心功能

- **额度类型管理**（授信额度、产品额度、担保额度、临时额度）
- **额度分配与调整**
- **额度占用与释放**
- **额度冻结与解冻**
- **额度回收**
- **额度启用/停用**
- **集团额度管理**（支持集团额度与成员额度的层级关系）
- **额度实时监控**

## 技术栈

- Spring Boot 3.2+
- MyBatis 3.0+
- MySQL 8.0+
- Redis (分布式缓存与锁)
- Lombok
- Maven

## 数据库设计

- **customers** - 客户表（支持集团客户、成员单位、单一客户）
- **quota_types** - 额度类型表
- **customer_quotas** - 客户额度表（支持集团额度、客户额度层级）
- **quota_transactions** - 额度交易记录表

## API 接口

### 额度检查
- `POST /api/quota/check` - 检查额度是否充足

### 额度占用与释放
- `POST /api/quota/consume` - 占用额度
- `POST /api/quota/release` - 释放额度

### 额度调整
- `POST /api/quota/adjust` - 调整额度
- `POST /api/quota/allocate` - 分配额度

### 额度冻结与解冻
- `POST /api/quota/freeze` - 冻结额度
- `POST /api/quota/unfreeze` - 解冻额度

### 额度启用/停用
- `POST /api/quota/enable` - 启用额度
- `POST /api/quota/disable` - 停用额度

### 额度回收
- `POST /api/quota/recover` - 回收额度

### 集团额度管理
- `GET /api/quota/group/{groupId}` - 获取集团额度信息
- `GET /api/quota/group/{groupId}/members` - 获取集团成员额度汇总
- `POST /api/quota/distribute-group` - 集团额度向下分解分配

### 额度查询
- `GET /api/quota/customer/{customerId}/quota/{quotaTypeId}` - 获取客户特定额度
- `GET /api/quota/customer/{customerId}/total` - 获取客户总额度

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