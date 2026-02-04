-- 统一授信系统数据库表设计 (MySQL)

-- 创建数据库
CREATE DATABASE IF NOT EXISTS unified_credit_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE unified_credit_db;

-- 1. 客户信息表
CREATE TABLE t_customer_info (
    customer_id VARCHAR(64) NOT NULL COMMENT '客户ID',
    customer_name VARCHAR(200) NOT NULL COMMENT '客户名称',
    customer_type ENUM('ENTERPRISE', 'INDIVIDUAL', 'FINANCIAL', 'GOVERNMENT') NOT NULL COMMENT '客户类型',
    customer_level VARCHAR(20) DEFAULT 'A' COMMENT '客户等级',
    business_license_no VARCHAR(30) COMMENT '营业执照号',
    organization_code VARCHAR(20) COMMENT '组织机构代码',
    tax_register_no VARCHAR(30) COMMENT '税务登记号',
    registered_capital DECIMAL(18,2) COMMENT '注册资本',
    registered_date DATE COMMENT '注册日期',
    legal_representative VARCHAR(100) COMMENT '法定代表人',
    business_scope TEXT COMMENT '经营范围',
    industry_type VARCHAR(50) COMMENT '所属行业',
    province VARCHAR(50) COMMENT '所在省份',
    city VARCHAR(50) COMMENT '所在城市',
    address VARCHAR(500) COMMENT '详细地址',
    phone VARCHAR(50) COMMENT '联系电话',
    status ENUM('NORMAL', 'FROZEN', 'CANCELLED') DEFAULT 'NORMAL' COMMENT '客户状态',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (customer_id),
    INDEX idx_customer_name (customer_name),
    INDEX idx_customer_type (customer_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户信息表';

-- 2. 集团客户关系表
CREATE TABLE t_group_relationship (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    parent_customer_id VARCHAR(64) NOT NULL COMMENT '父客户ID',
    child_customer_id VARCHAR(64) NOT NULL COMMENT '子客户ID',
    relationship_type ENUM('CONTROL', 'INFLUENCE', 'AFFILIATE') NOT NULL COMMENT '关系类型',
    control_ratio DECIMAL(5,2) COMMENT '控制比例(%)',
    relationship_desc VARCHAR(500) COMMENT '关系描述',
    effective_date DATE NOT NULL COMMENT '生效日期',
    expire_date DATE COMMENT '失效日期',
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE' COMMENT '关系状态',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_parent_child (parent_customer_id, child_customer_id),
    INDEX idx_parent_customer (parent_customer_id),
    INDEX idx_child_customer (child_customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集团客户关系表';

-- 3. 客户关联方表
CREATE TABLE t_customer_affiliate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    customer_id VARCHAR(64) NOT NULL COMMENT '客户ID',
    affiliate_type ENUM('SHAREHOLDER', 'CONTROLLER', 'GUARANTOR', 'RELATED_PERSON') NOT NULL COMMENT '关联方类型',
    affiliate_id VARCHAR(64) COMMENT '关联方客户ID',
    affiliate_name VARCHAR(200) NOT NULL COMMENT '关联方名称',
    affiliate_identity VARCHAR(100) COMMENT '关联方身份(如股东、担保人等)',
    relationship_desc VARCHAR(500) COMMENT '关系描述',
    relationship_ratio DECIMAL(5,2) COMMENT '关联比例(%)',
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE' COMMENT '状态',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_customer_id (customer_id),
    INDEX idx_affiliate_id (affiliate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户关联方表';

-- 4. 授信申请表
CREATE TABLE t_credit_application (
    application_id VARCHAR(64) NOT NULL COMMENT '申请ID',
    customer_id VARCHAR(64) NOT NULL COMMENT '客户ID',
    application_type ENUM('NEW', 'RENEWAL', 'ADJUSTMENT') NOT NULL COMMENT '申请类型',
    application_amount DECIMAL(18,2) NOT NULL COMMENT '申请金额',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '币种',
    application_purpose VARCHAR(500) COMMENT '申请用途',
    guarantee_method VARCHAR(100) COMMENT '担保方式',
    term_months INT COMMENT '期限(月)',
    application_date DATE NOT NULL COMMENT '申请日期',
    applicant VARCHAR(100) COMMENT '申请人',
    department VARCHAR(100) COMMENT '申请部门',
    status ENUM('DRAFT', 'SUBMITTED', 'IN_REVIEW', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'SUBMITTED' COMMENT '申请状态',
    current_approver VARCHAR(64) COMMENT '当前审批人',
    current_approve_node VARCHAR(100) COMMENT '当前审批节点',
    approve_result ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING' COMMENT '审批结果',
    approve_advice TEXT COMMENT '审批意见',
    approve_date TIMESTAMP NULL COMMENT '审批完成日期',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (application_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    INDEX idx_application_date (application_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授信申请表';

-- 5. 授信额度表
CREATE TABLE t_credit_quota (
    quota_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '额度ID',
    customer_id VARCHAR(64) NOT NULL COMMENT '客户ID',
    quota_type VARCHAR(50) NOT NULL COMMENT '额度类型',
    quota_subtype VARCHAR(50) COMMENT '额度子类型',
    total_quota DECIMAL(18,2) NOT NULL COMMENT '总额度',
    used_quota DECIMAL(18,2) DEFAULT 0.00 COMMENT '已用额度',
    available_quota DECIMAL(18,2) GENERATED ALWAYS AS (total_quota - used_quota) STORED COMMENT '可用额度',
    frozen_quota DECIMAL(18,2) DEFAULT 0.00 COMMENT '冻结额度',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '币种',
    quota_level ENUM('GROUP', 'CUSTOMER', 'PRODUCT') DEFAULT 'CUSTOMER' COMMENT '额度层级',
    parent_quota_id BIGINT COMMENT '父额度ID(用于集团额度分解)',
    effective_date DATE NOT NULL COMMENT '生效日期',
    expire_date DATE NOT NULL COMMENT '到期日期',
    quota_status ENUM('ACTIVE', 'INACTIVE', 'FROZEN', 'EXPIRED') DEFAULT 'ACTIVE' COMMENT '额度状态',
    guarantee_info TEXT COMMENT '担保信息',
    risk_control_measures TEXT COMMENT '风险控制措施',
    quota_manager VARCHAR(64) COMMENT '额度管理员',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_customer_id (customer_id),
    INDEX idx_quota_type (quota_type),
    INDEX idx_quota_status (quota_status),
    INDEX idx_expire_date (expire_date),
    UNIQUE KEY uk_customer_quota_type (customer_id, quota_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授信额度表';

-- 6. 用信申请表
CREATE TABLE t_usage_application (
    usage_id VARCHAR(64) NOT NULL COMMENT '用信申请ID',
    application_id VARCHAR(64) COMMENT '授信申请ID',
    customer_id VARCHAR(64) NOT NULL COMMENT '客户ID',
    quota_id BIGINT NOT NULL COMMENT '额度ID',
    product_type VARCHAR(50) NOT NULL COMMENT '产品类型',
    usage_amount DECIMAL(18,2) NOT NULL COMMENT '用信金额',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '币种',
    usage_term INT COMMENT '用信期限(天)',
    interest_rate DECIMAL(8,4) COMMENT '利率(%)',
    repayment_method VARCHAR(50) COMMENT '还款方式',
    guarantee_method VARCHAR(100) COMMENT '担保方式',
    fund_usage VARCHAR(500) COMMENT '资金用途',
    application_date DATE NOT NULL COMMENT '申请日期',
    status ENUM('DRAFT', 'SUBMITTED', 'IN_REVIEW', 'APPROVED', 'REJECTED', 'CANCELLED', 'EXECUTED') DEFAULT 'SUBMITTED' COMMENT '状态',
    contract_no VARCHAR(100) COMMENT '合同编号',
    loan_note_no VARCHAR(100) COMMENT '借据编号',
    disbursement_date DATE COMMENT '放款日期',
    maturity_date DATE COMMENT '到期日期',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (usage_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_quota_id (quota_id),
    INDEX idx_product_type (product_type),
    INDEX idx_status (status),
    INDEX idx_disbursement_date (disbursement_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用信申请表';

-- 7. 额度使用明细表
CREATE TABLE t_quota_usage_detail (
    detail_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    quota_id BIGINT NOT NULL COMMENT '额度ID',
    customer_id VARCHAR(64) NOT NULL COMMENT '客户ID',
    usage_id VARCHAR(64) COMMENT '用信申请ID',
    transaction_type ENUM('OCCUPY', 'RELEASE', 'FREEZE', 'UNFREEZE', 'ADJUST') NOT NULL COMMENT '交易类型',
    transaction_amount DECIMAL(18,2) NOT NULL COMMENT '交易金额',
    before_balance DECIMAL(18,2) NOT NULL COMMENT '交易前余额',
    after_balance DECIMAL(18,2) NOT NULL COMMENT '交易后余额',
    business_type VARCHAR(50) COMMENT '业务类型',
    business_ref_no VARCHAR(100) COMMENT '业务参考号',
    operator VARCHAR(64) COMMENT '操作员',
    transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
    remark TEXT COMMENT '备注',
    INDEX idx_quota_id (quota_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_usage_id (usage_id),
    INDEX idx_transaction_time (transaction_time),
    INDEX idx_transaction_type (transaction_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='额度使用明细表';

-- 8. 风险监控指标表
CREATE TABLE t_risk_monitoring_index (
    index_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '指标ID',
    customer_id VARCHAR(64) NOT NULL COMMENT '客户ID',
    index_type VARCHAR(50) NOT NULL COMMENT '指标类型',
    index_code VARCHAR(50) NOT NULL COMMENT '指标代码',
    index_name VARCHAR(200) NOT NULL COMMENT '指标名称',
    index_value DECIMAL(18,4) COMMENT '指标值',
    index_unit VARCHAR(20) COMMENT '指标单位',
    threshold_value DECIMAL(18,4) COMMENT '阈值',
    risk_level ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') COMMENT '风险等级',
    calc_date DATE NOT NULL COMMENT '计算日期',
    calc_method VARCHAR(200) COMMENT '计算方法',
    data_source VARCHAR(100) COMMENT '数据来源',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_customer_id (customer_id),
    INDEX idx_index_code (index_code),
    INDEX idx_calc_date (calc_date),
    INDEX idx_risk_level (risk_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险监控指标表';

-- 9. 风险预警表
CREATE TABLE t_risk_warning (
    warning_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '预警ID',
    customer_id VARCHAR(64) NOT NULL COMMENT '客户ID',
    warning_type VARCHAR(50) NOT NULL COMMENT '预警类型',
    warning_code VARCHAR(50) NOT NULL COMMENT '预警代码',
    warning_title VARCHAR(200) NOT NULL COMMENT '预警标题',
    warning_content TEXT NOT NULL COMMENT '预警内容',
    risk_level ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL COMMENT '风险等级',
    warning_status ENUM('UNHANDLED', 'HANDLING', 'HANDLED', 'IGNORED') DEFAULT 'UNHANDLED' COMMENT '预警状态',
    handler VARCHAR(64) COMMENT '处理人',
    handle_time TIMESTAMP NULL COMMENT '处理时间',
    handle_result TEXT COMMENT '处理结果',
    warning_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '预警日期',
    resolve_date TIMESTAMP NULL COMMENT '解除日期',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_customer_id (customer_id),
    INDEX idx_warning_type (warning_type),
    INDEX idx_risk_level (risk_level),
    INDEX idx_warning_status (warning_status),
    INDEX idx_warning_date (warning_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险预警表';

-- 10. 审批流程表
CREATE TABLE t_approval_process (
    process_id VARCHAR(64) NOT NULL COMMENT '流程ID',
    business_type VARCHAR(50) NOT NULL COMMENT '业务类型',
    business_id VARCHAR(64) NOT NULL COMMENT '业务ID',
    process_definition_id VARCHAR(100) NOT NULL COMMENT '流程定义ID',
    process_name VARCHAR(200) NOT NULL COMMENT '流程名称',
    current_node VARCHAR(100) COMMENT '当前节点',
    current_assignee VARCHAR(64) COMMENT '当前处理人',
    process_status ENUM('RUNNING', 'COMPLETED', 'TERMINATED') DEFAULT 'RUNNING' COMMENT '流程状态',
    priority ENUM('LOW', 'NORMAL', 'HIGH', 'URGENT') DEFAULT 'NORMAL' COMMENT '优先级',
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    end_time TIMESTAMP NULL COMMENT '结束时间',
    created_by VARCHAR(64) COMMENT '发起人',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '发起时间',
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (process_id),
    INDEX idx_business_id (business_id),
    INDEX idx_process_status (process_status),
    INDEX idx_current_assignee (current_assignee)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批流程表';

-- 11. 审批节点表
CREATE TABLE t_approval_node (
    node_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '节点ID',
    process_id VARCHAR(64) NOT NULL COMMENT '流程ID',
    node_name VARCHAR(100) NOT NULL COMMENT '节点名称',
    node_type VARCHAR(50) NOT NULL COMMENT '节点类型',
    assignee_type ENUM('USER', 'ROLE', 'DEPARTMENT') COMMENT '处理人类型',
    assignee_id VARCHAR(64) COMMENT '处理人ID',
    assignee_name VARCHAR(100) COMMENT '处理人姓名',
    node_status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'REJECTED') DEFAULT 'PENDING' COMMENT '节点状态',
    approve_result ENUM('PENDING', 'APPROVED', 'REJECTED', 'TRANSFERRED') DEFAULT 'PENDING' COMMENT '审批结果',
    approve_opinion TEXT COMMENT '审批意见',
    start_time TIMESTAMP NULL COMMENT '开始时间',
    end_time TIMESTAMP NULL COMMENT '结束时间',
    duration_minutes INT COMMENT '处理时长(分钟)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_process_id (process_id),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_node_status (node_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批节点表';

-- 12. 系统参数配置表
CREATE TABLE t_sys_param_config (
    param_id VARCHAR(64) NOT NULL COMMENT '参数ID',
    param_group VARCHAR(50) NOT NULL COMMENT '参数组',
    param_code VARCHAR(50) NOT NULL COMMENT '参数代码',
    param_name VARCHAR(200) NOT NULL COMMENT '参数名称',
    param_value TEXT COMMENT '参数值',
    param_desc VARCHAR(500) COMMENT '参数描述',
    data_type ENUM('STRING', 'NUMBER', 'DATE', 'JSON') DEFAULT 'STRING' COMMENT '数据类型',
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE' COMMENT '状态',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (param_id),
    UNIQUE KEY uk_param_code (param_code),
    INDEX idx_param_group (param_group),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统参数配置表';

-- 初始化系统参数配置
INSERT INTO t_sys_param_config (param_id, param_group, param_code, param_name, param_value, param_desc, status) VALUES
('QUOTA_CALC_METHOD', 'RISK_CONTROL', 'quota_calculation_method', '额度测算方法', 'BASED_ON_ASSETS', '额度测算方法：基于资产、现金流等', 'ACTIVE'),
('WARN_THRESHOLD_LOW', 'RISK_CONTROL', 'warning_threshold_low', '低风险阈值', '0.6', '风险指标低于此值为低风险', 'ACTIVE'),
('WARN_THRESHOLD_MEDIUM', 'RISK_CONTROL', 'warning_threshold_medium', '中风险阈值', '0.75', '风险指标在此区间为中风险', 'ACTIVE'),
('WARN_THRESHOLD_HIGH', 'RISK_CONTROL', 'warning_threshold_high', '高风险阈值', '0.9', '风险指标高于此值为高风险', 'ACTIVE'),
('QUOTA_EXPIRY_ALERT_DAYS', 'RISK_CONTROL', 'quota_expiry_alert_days', '额度到期提醒天数', '30', '额度到期前多少天开始提醒', 'ACTIVE'),
('GROUP_QUOTA_RATIO_LIMIT', 'RISK_CONTROL', 'group_quota_ratio_limit', '集团额度分配比例限制', '0.8', '集团额度分配给子公司的最高比例', 'ACTIVE');

-- 初始化额度类型数据
INSERT INTO t_sys_param_config (param_id, param_group, param_code, param_name, param_value, param_desc, status) VALUES
('QUOTA_TYPE_1', 'QUOTA_CONFIG', 'CREDIT_LINE', '综合授信额度', '1', '综合授信额度类型', 'ACTIVE'),
('QUOTA_TYPE_2', 'QUOTA_CONFIG', 'WORKING_CAPITAL', '流动资金贷款额度', '2', '流动资金贷款专项额度', 'ACTIVE'),
('QUOTA_TYPE_3', 'QUOTA_CONFIG', 'FIXED_ASSET_LOAN', '固定资产贷款额度', '3', '固定资产贷款专项额度', 'ACTIVE'),
('QUOTA_TYPE_4', 'QUOTA_CONFIG', 'TRADE_FINANCE', '贸易融资额度', '4', '贸易融资专项额度', 'ACTIVE'),
('QUOTA_TYPE_5', 'QUOTA_CONFIG', 'GUARANTEE_QUOTA', '担保额度', '5', '基于抵押物的担保额度', 'ACTIVE');

-- 初始化客户等级数据
INSERT INTO t_sys_param_config (param_id, param_group, param_code, param_name, param_value, param_desc, status) VALUES
('CUST_LEVEL_S', 'CUSTOMER_CONFIG', 'CUSTOMER_LEVEL_S', 'S级客户', 'S', '战略级客户', 'ACTIVE'),
('CUST_LEVEL_A', 'CUSTOMER_CONFIG', 'CUSTOMER_LEVEL_A', 'A级客户', 'A', '优质客户', 'ACTIVE'),
('CUST_LEVEL_B', 'CUSTOMER_CONFIG', 'CUSTOMER_LEVEL_B', 'B级客户', 'B', '一般客户', 'ACTIVE'),
('CUST_LEVEL_C', 'CUSTOMER_CONFIG', 'CUSTOMER_LEVEL_C', 'C级客户', 'C', '关注客户', 'ACTIVE');