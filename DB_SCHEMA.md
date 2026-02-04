# 银行信贷额度管控平台 - 数据库设计文档

## 1. 数据库概述

本数据库设计遵循银行业务规范，支持完整的额度管控、风险管理、审批流程等功能。

## 2. 数据库表结构

### 2.1 客户信息表 (t_customer_info)
| 字段名 | 类型 | 是否为空 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| customer_id | VARCHAR(64) | NOT NULL | | 客户ID |
| customer_name | VARCHAR(200) | NOT NULL | | 客户名称 |
| customer_type | ENUM | NOT NULL | | 客户类型(ENTERPRISE,INDIVIDUAL,FINANCIAL,GOVERNMENT) |
| customer_level | VARCHAR(20) | | 'A' | 客户等级 |
| business_license_no | VARCHAR(30) | | | 营业执照号 |
| organization_code | VARCHAR(20) | | | 组织机构代码 |
| tax_register_no | VARCHAR(30) | | | 税务登记号 |
| registered_capital | DECIMAL(18,2) | | | 注册资本 |
| registered_date | DATE | | | 注册日期 |
| legal_representative | VARCHAR(100) | | | 法定代表人 |
| business_scope | TEXT | | | 经营范围 |
| industry_type | VARCHAR(50) | | | 所属行业 |
| province | VARCHAR(50) | | | 所在省份 |
| city | VARCHAR(50) | | | 所在城市 |
| address | VARCHAR(500) | | | 详细地址 |
| phone | VARCHAR(50) | | | 联系电话 |
| status | ENUM | | 'NORMAL' | 客户状态(NORMAL,FROZEN,CANCELLED) |
| created_by | VARCHAR(64) | | | 创建人 |
| created_time | TIMESTAMP | | CURRENT_TIMESTAMP | 创建时间 |
| updated_by | VARCHAR(64) | | | 更新人 |
| updated_time | TIMESTAMP | | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.2 集团客户关系表 (t_group_relationship)
| 字段名 | 类型 | 是否为空 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| id | BIGINT | NOT NULL | AUTO_INCREMENT | 主键ID |
| parent_customer_id | VARCHAR(64) | NOT NULL | | 父客户ID |
| child_customer_id | VARCHAR(64) | NOT NULL | | 子客户ID |
| relationship_type | ENUM | NOT NULL | | 关系类型(CONTROL,INFLUENCE,AFFILIATE) |
| control_ratio | DECIMAL(5,2) | | | 控制比例(%) |
| relationship_desc | VARCHAR(500) | | | 关系描述 |
| effective_date | DATE | NOT NULL | | 生效日期 |
| expire_date | DATE | | | 失效日期 |
| status | ENUM | | 'ACTIVE' | 关系状态(ACTIVE,INACTIVE) |
| created_by | VARCHAR(64) | | | 创建人 |
| created_time | TIMESTAMP | | CURRENT_TIMESTAMP | 创建时间 |
| updated_by | VARCHAR(64) | | | 更新人 |
| updated_time | TIMESTAMP | | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.3 客户关联方表 (t_customer_affiliate)
| 字段名 | 类型 | 是否为空 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| id | BIGINT | NOT NULL | AUTO_INCREMENT | 主键ID |
| customer_id | VARCHAR(64) | NOT NULL | | 客户ID |
| affiliate_type | ENUM | NOT NULL | | 关联方类型(SHAREHOLDER,CONTROLLER,GUARANTOR,RELATED_PERSON) |
| affiliate_id | VARCHAR(64) | | | 关联方客户ID |
| affiliate_name | VARCHAR(200) | NOT NULL | | 关联方名称 |
| affiliate_identity | VARCHAR(100) | | | 关联方身份 |
| relationship_desc | VARCHAR(500) | | | 关系描述 |
| relationship_ratio | DECIMAL(5,2) | | | 关联比例(%) |
| status | ENUM | | 'ACTIVE' | 状态(ACTIVE,INACTIVE) |
| created_by | VARCHAR(64) | | | 创建人 |
| created_time | TIMESTAMP | | CURRENT_TIMESTAMP | 创建时间 |
| updated_by | VARCHAR(64) | | | 更新人 |
| updated_time | TIMESTAMP | | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.4 授信申请表 (t_credit_application)
| 字段名 | 类型 | 是否为空 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| application_id | VARCHAR(64) | NOT NULL | | 申请ID |
| customer_id | VARCHAR(64) | NOT NULL | | 客户ID |
| application_type | ENUM | NOT NULL | | 申请类型(NEW,RENEWAL,ADJUSTMENT) |
| application_amount | DECIMAL(18,2) | NOT NULL | | 申请金额 |
| currency | VARCHAR(10) | | 'CNY' | 币种 |
| application_purpose | VARCHAR(500) | | | 申请用途 |
| guarantee_method | VARCHAR(100) | | | 担保方式 |
| term_months | INT | | | 期限(月) |
| application_date | DATE | NOT NULL | | 申请日期 |
| applicant | VARCHAR(100) | | | 申请人 |
| department | VARCHAR(100) | | | 申请部门 |
| status | ENUM | | 'SUBMITTED' | 申请状态(DRAFT,SUBMITTED,IN_REVIEW,APPROVED,REJECTED,CANCELLED) |
| current_approver | VARCHAR(64) | | | 当前审批人 |
| current_approve_node | VARCHAR(100) | | | 当前审批节点 |
| approve_result | ENUM | | 'PENDING' | 审批结果(PENDING,APPROVED,REJECTED) |
| approve_advice | TEXT | | | 审批意见 |
| approve_date | TIMESTAMP | | | 审批完成日期 |
| created_by | VARCHAR(64) | | | 创建人 |
| created_time | TIMESTAMP | | CURRENT_TIMESTAMP | 创建时间 |
| updated_by | VARCHAR(64) | | | 更新人 |
| updated_time | TIMESTAMP | | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.5 授信额度表 (t_credit_quota)
| 字段名 | 类型 | 是否为空 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| quota_id | BIGINT | NOT NULL | AUTO_INCREMENT | 额度ID |
| customer_id | VARCHAR(64) | NOT NULL | | 客户ID |
| quota_type | VARCHAR(50) | NOT NULL | | 额度类型 |
| quota_subtype | VARCHAR(50) | | | 额度子类型 |
| total_quota | DECIMAL(18,2) | NOT NULL | | 总额度 |
| used_quota | DECIMAL(18,2) | | 0.00 | 已用额度 |
| available_quota | DECIMAL(18,2) | | | 可用额度(计算字段) |
| frozen_quota | DECIMAL(18,2) | | 0.00 | 冻结额度 |
| currency | VARCHAR(10) | | 'CNY' | 币种 |
| quota_level | ENUM | | 'CUSTOMER' | 额度层级(GROUP,CUSTOMER,PRODUCT) |
| parent_quota_id | BIGINT | | | 父额度ID |
| effective_date | DATE | NOT NULL | | 生效日期 |
| expire_date | DATE | NOT NULL | | 到期日期 |
| quota_status | ENUM | | 'ACTIVE' | 额度状态(ACTIVE,INACTIVE,FROZEN,EXPIRED) |
| guarantee_info | TEXT | | | 担保信息 |
| risk_control_measures | TEXT | | | 风险控制措施 |
| quota_manager | VARCHAR(64) | | | 额度管理员 |
| created_by | VARCHAR(64) | | | 创建人 |
| created_time | TIMESTAMP | | CURRENT_TIMESTAMP | 创建时间 |
| updated_by | VARCHAR(64) | | | 更新人 |
| updated_time | TIMESTAMP | | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.6 用信申请表 (t_usage_application)
| 字段名 | 类型 | 是否为空 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| usage_id | VARCHAR(64) | NOT NULL | | 用信申请ID |
| application_id | VARCHAR(64) | | | 授信申请ID |
| customer_id | VARCHAR(64) | NOT NULL | | 客户ID |
| quota_id | BIGINT | NOT NULL | | 额度ID |
| product_type | VARCHAR(50) | NOT NULL | | 产品类型 |
| usage_amount | DECIMAL(18,2) | NOT NULL | | 用信金额 |
| currency | VARCHAR(10) | | 'CNY' | 币种 |
| usage_term | INT | | | 用信期限(天) |
| interest_rate | DECIMAL(8,4) | | | 利率(%) |
| repayment_method | VARCHAR(50) | | | 还款方式 |
| guarantee_method | VARCHAR(100) | | | 担保方式 |
| fund_usage | VARCHAR(500) | | | 资金用途 |
| application_date | DATE | NOT NULL | | 申请日期 |
| status | ENUM | | 'SUBMITTED' | 状态(DRAFT,SUBMITTED,IN_REVIEW,APPROVED,REJECTED,CANCELLED,EXECUTED) |
| contract_no | VARCHAR(100) | | | 合同编号 |
| loan_note_no | VARCHAR(100) | | | 借据编号 |
| disbursement_date | DATE | | | 放款日期 |
| maturity_date | DATE | | | 到期日期 |
| created_by | VARCHAR(64) | | | 创建人 |
| created_time | TIMESTAMP | | CURRENT_TIMESTAMP | 创建时间 |
| updated_by | VARCHAR(64) | | | 更新人 |
| updated_time | TIMESTAMP | | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.7 额度使用明细表 (t_quota_usage_detail)
| 字段名 | 类型 | 是否为空 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| detail_id | BIGINT | NOT NULL | AUTO_INCREMENT | 明细ID |
| quota_id | BIGINT | NOT NULL | | 额度ID |
| customer_id | VARCHAR(64) | NOT NULL | | 客户ID |
| usage_id | VARCHAR(64) | | | 用信申请ID |
| transaction_type | ENUM | NOT NULL | | 交易类型(OCCUPY,RELEASE,FREEZE,UNFREEZE,ADJUST) |
| transaction_amount | DECIMAL(18,2) | NOT NULL | | 交易金额 |
| before_balance | DECIMAL(18,2) | NOT NULL | | 交易前余额 |
| after_balance | DECIMAL(18,2) | NOT NULL | | 交易后余额 |
| business_type | VARCHAR(50) | | | 业务类型 |
| business_ref_no | VARCHAR(100) | | | 业务参考号 |
| operator | VARCHAR(64) | | | 操作员 |
| transaction_time | TIMESTAMP | | CURRENT_TIMESTAMP | 交易时间 |
| remark | TEXT | | | 备注 |
| created_by | VARCHAR(64) | | | 创建人 |
| created_time | TIMESTAMP | | CURRENT_TIMESTAMP | 创建时间 |
| updated_by | VARCHAR(64) | | | 更新人 |
| updated_time | TIMESTAMP | | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.8 风险监控指标表 (t_risk_monitoring_index)
| 字段名 | 类型 | 是否为空 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| index_id | BIGINT | NOT NULL | AUTO_INCREMENT | 指标ID |
| customer_id | VARCHAR(64) | NOT NULL | | 客户ID |
| index_type | VARCHAR(50) | NOT NULL | | 指标类型 |
| index_code | VARCHAR(50) | NOT NULL | | 指标代码 |
| index_name | VARCHAR(200) | NOT NULL | | 指标名称 |
| index_value | DECIMAL(18,4) | | | 指标值 |
| index_unit | VARCHAR(20) | | | 指标单位 |
| threshold_value | DECIMAL(18,4) | | | 阈值 |
| risk_level | ENUM | | | 风险等级(LOW,MEDIUM,HIGH,CRITICAL) |
| calc_date | DATE | NOT NULL | | 计算日期 |
| calc_method | VARCHAR(200) | | | 计算方法 |
| data_source | VARCHAR(100) | | | 数据来源 |
| created_by | VARCHAR(64) | | | 创建人 |
| created_time | TIMESTAMP | | CURRENT_TIMESTAMP | 创建时间 |

### 2.9 风险预警表 (t_risk_warning)
| 字段名 | 类型 | 是否为空 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| warning_id | BIGINT | NOT NULL | AUTO_INCREMENT | 预警ID |
| customer_id | VARCHAR(64) | NOT NULL | | 客户ID |
| warning_type | VARCHAR(50) | NOT NULL | | 预警类型 |
| warning_code | VARCHAR(50) | NOT NULL | | 预警代码 |
| warning_title | VARCHAR(200) | NOT NULL | | 预警标题 |
| warning_content | TEXT | NOT NULL | | 预警内容 |
| risk_level | ENUM | NOT NULL | | 风险等级(LOW,MEDIUM,HIGH,CRITICAL) |
| warning_status | ENUM | | 'UNHANDLED' | 预警状态(UNHANDLED,HANDLING,HANDLED,IGNORED) |
| handler | VARCHAR(64) | | | 处理人 |
| handle_time | TIMESTAMP | | | 处理时间 |
| handle_result | TEXT | | | 处理结果 |
| warning_date | TIMESTAMP | | CURRENT_TIMESTAMP | 预警日期 |
| resolve_date | TIMESTAMP | | | 解除日期 |
| created_by | VARCHAR(64) | | | 创建人 |
| created_time | TIMESTAMP | | CURRENT_TIMESTAMP | 创建时间 |
| updated_by | VARCHAR(64) | | | 更新人 |
| updated_time | TIMESTAMP | | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.10 审批流程表 (t_approval_process)
| 字段名 | 类型 | 是否为空 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| process_id | VARCHAR(64) | NOT NULL | | 流程ID |
| business_type | VARCHAR(50) | NOT NULL | | 业务类型 |
| business_id | VARCHAR(64) | NOT NULL | | 业务ID |
| process_definition_id | VARCHAR(100) | NOT NULL | | 流程定义ID |
| process_name | VARCHAR(200) | NOT NULL | | 流程名称 |
| current_node | VARCHAR(100) | | | 当前节点 |
| current_assignee | VARCHAR(64) | | | 当前处理人 |
| process_status | ENUM | | 'RUNNING' | 流程状态(RUNNING,COMPLETED,TERMINATED) |
| priority | ENUM | | 'NORMAL' | 优先级(LOW,NORMAL,HIGH,URGENT) |
| start_time | TIMESTAMP | | CURRENT_TIMESTAMP | 开始时间 |
| end_time | TIMESTAMP | | | 结束时间 |
| created_by | VARCHAR(64) | | | 发起人 |
| created_time | TIMESTAMP | | CURRENT_TIMESTAMP | 发起时间 |
| updated_by | VARCHAR(64) | | | 更新人 |
| updated_time | TIMESTAMP | | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.11 审批节点表 (t_approval_node)
| 字段名 | 类型 | 是否为空 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| node_id | BIGINT | NOT NULL | AUTO_INCREMENT | 节点ID |
| process_id | VARCHAR(64) | NOT NULL | | 流程ID |
| node_name | VARCHAR(100) | NOT NULL | | 节点名称 |
| node_type | VARCHAR(50) | NOT NULL | | 节点类型 |
| assignee_type | ENUM | | | 处理人类型(USER,ROLE,DEPARTMENT) |
| assignee_id | VARCHAR(64) | | | 处理人ID |
| assignee_name | VARCHAR(100) | | | 处理人姓名 |
| node_status | ENUM | | 'PENDING' | 节点状态(PENDING,PROCESSING,COMPLETED,REJECTED) |
| approve_result | ENUM | | 'PENDING' | 审批结果(PENDING,APPROVED,REJECTED,TRANSFERRED) |
| approve_opinion | TEXT | | | 审批意见 |
| start_time | TIMESTAMP | | | 开始时间 |
| end_time | TIMESTAMP | | | 结束时间 |
| duration_minutes | INT | | | 处理时长(分钟) |
| created_time | TIMESTAMP | | CURRENT_TIMESTAMP | 创建时间 |
| updated_time | TIMESTAMP | | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.12 系统参数配置表 (t_sys_param_config)
| 字段名 | 类型 | 是否为空 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| param_id | VARCHAR(64) | NOT NULL | | 参数ID |
| param_group | VARCHAR(50) | NOT NULL | | 参数组 |
| param_code | VARCHAR(50) | NOT NULL | | 参数代码 |
| param_name | VARCHAR(200) | NOT NULL | | 参数名称 |
| param_value | TEXT | | | 参数值 |
| param_desc | VARCHAR(500) | | | 参数描述 |
| data_type | ENUM | | 'STRING' | 数据类型(STRING,NUMBER,DATE,JSON) |
| status | ENUM | | 'ACTIVE' | 状态(ACTIVE,INACTIVE) |
| sort_order | INT | | 0 | 排序 |
| created_by | VARCHAR(64) | | | 创建人 |
| created_time | TIMESTAMP | | CURRENT_TIMESTAMP | 创建时间 |
| updated_by | VARCHAR(64) | | | 更新人 |
| updated_time | TIMESTAMP | | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

## 3. 索引设计

### 3.1 主要索引
- t_customer_info: customer_id (主键), idx_customer_name, idx_customer_type, idx_status
- t_group_relationship: id (主键), uk_parent_child, idx_parent_customer, idx_child_customer
- t_customer_affiliate: id (主键), idx_customer_id, idx_affiliate_id, idx_affiliate_type
- t_credit_application: application_id (主键), idx_customer_id, idx_status, idx_application_date
- t_credit_quota: quota_id (主键), idx_customer_id, idx_quota_type, idx_quota_status, idx_expire_date
- t_usage_application: usage_id (主键), idx_customer_id, idx_quota_id, idx_product_type, idx_status, idx_disbursement_date
- t_quota_usage_detail: detail_id (主键), idx_quota_id, idx_customer_id, idx_usage_id, idx_transaction_time, idx_transaction_type
- t_risk_monitoring_index: index_id (主键), idx_customer_id, idx_index_code, idx_calc_date, idx_risk_level
- t_risk_warning: warning_id (主键), idx_customer_id, idx_warning_type, idx_warning_status, idx_warning_date
- t_approval_process: process_id (主键), idx_business_id, idx_process_status, idx_current_assignee
- t_approval_node: node_id (主键), idx_process_id, idx_node_status, idx_assignee_id, idx_approve_result

## 4. 数据库约束

### 4.1 主键约束
- 所有表均设置主键约束

### 4.2 唯一约束
- t_group_relationship: uk_parent_child (parent_customer_id, child_customer_id)
- t_sys_param_config: uk_param_code (param_code)

### 4.3 外键约束
- t_group_relationship: parent_customer_id -> t_customer_info.customer_id
- t_group_relationship: child_customer_id -> t_customer_info.customer_id
- t_customer_affiliate: customer_id -> t_customer_info.customer_id
- t_credit_application: customer_id -> t_customer_info.customer_id
- t_credit_quota: customer_id -> t_customer_info.customer_id
- t_usage_application: customer_id -> t_customer_info.customer_id, quota_id -> t_credit_quota.quota_id
- t_quota_usage_detail: quota_id -> t_credit_quota.quota_id, customer_id -> t_customer_info.customer_id
- t_risk_monitoring_index: customer_id -> t_customer_info.customer_id
- t_risk_warning: customer_id -> t_customer_info.customer_id
- t_approval_process: created_by -> t_customer_info.customer_id
- t_approval_node: process_id -> t_approval_process.process_id

## 5. 初始化数据

### 5.1 系统参数配置
- 额度测算方法配置
- 风险阈值配置
- 额度到期提醒天数配置
- 集团额度分配比例限制配置
- 额度类型配置
- 客户等级配置

## 6. 性能优化建议

1. 对高频查询字段建立索引
2. 定期清理历史数据
3. 使用分区表处理大量交易数据
4. 配置合适的数据库连接池参数
5. 启用查询缓存机制