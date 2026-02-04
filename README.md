# 银行信贷额度管控平台

基于Spring Boot的银行信贷额度管控平台，实现额度管理、分配、使用、监控等功能。

## 项目特性

- 高性能额度管控引擎
- 实时额度查询与占用
- 分布式锁保障并发安全
- 完整的额度交易记录
- 灵活的额度类型管理

## 核心功能

- 额度类型管理（授信额度、产品额度、担保额度、临时额度）
- 额度分配与调整
- 额度占用与释放
- 额度冻结与解冻
- 额度回收
- 额度实时监控

## 技术栈

- Spring Boot 3.2+
- Spring Data JPA
- PostgreSQL
- Redis (分布式缓存与锁)
- Lombok
- Maven

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

### 额度回收
- `POST /api/quota/recover` - 回收额度

### 额度查询
- `GET /api/quota/customer/{customerId}/quota/{quotaTypeId}` - 获取客户特定额度
- `GET /api/quota/customer/{customerId}/total` - 获取客户总额度