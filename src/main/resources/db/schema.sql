-- 银行信贷额度管控平台数据库初始化脚本 (MySQL)

-- 创建数据库
CREATE DATABASE IF NOT EXISTS credit_quota_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE credit_quota_db;

-- 客户表
CREATE TABLE customers (
    customer_id VARCHAR(64) PRIMARY KEY COMMENT '客户ID',
    customer_name VARCHAR(255) NOT NULL COMMENT '客户名称',
    customer_type INT DEFAULT 2 COMMENT '客户类型: 1-集团客户, 2-成员单位, 3-单一客户',
    parent_customer_id VARCHAR(64) COMMENT '父客户ID，用于集团客户结构',
    customer_level VARCHAR(50) COMMENT '客户等级',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_customer_id (parent_customer_id),
    INDEX idx_customer_type (customer_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- 额度类型表
CREATE TABLE quota_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    quota_type_name VARCHAR(100) UNIQUE NOT NULL COMMENT '额度类型名称',
    quota_category INT NOT NULL COMMENT '额度类别: 1-授信额度, 2-产品额度, 3-担保额度, 4-临时额度',
    description TEXT COMMENT '描述',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_quota_type_name (quota_type_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='额度类型表';

-- 客户额度表
CREATE TABLE customer_quotas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    customer_id VARCHAR(64) NOT NULL COMMENT '客户ID',
    quota_type_id BIGINT NOT NULL COMMENT '额度类型ID',
    total_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '总额度',
    used_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '已用额度',
    available_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '可用额度',
    frozen_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '冻结额度',
    status INT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    quota_level INT DEFAULT 2 COMMENT '额度层级: 1-集团额度, 2-客户额度',
    parent_quota_id VARCHAR(64) COMMENT '父额度ID（用于集团额度分解）',
    effective_date DATETIME COMMENT '生效日期',
    expire_date DATETIME COMMENT '到期日期',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (quota_type_id) REFERENCES quota_types(id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_quota_type_id (quota_type_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户额度表';

-- 额度交易记录表
CREATE TABLE quota_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    transaction_id VARCHAR(64) UNIQUE NOT NULL COMMENT '交易ID',
    customer_id VARCHAR(64) NOT NULL COMMENT '客户ID',
    customer_quota_id BIGINT NOT NULL COMMENT '客户额度ID',
    transaction_type INT NOT NULL COMMENT '交易类型: 1-分配, 2-占用, 3-释放, 4-调整, 5-冻结, 6-解冻, 7-回收, 8-启用, 9-停用',
    amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '交易金额',
    before_balance DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '交易前余额',
    after_balance DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '交易后余额',
    status INT DEFAULT 1 COMMENT '状态: 1-成功, 2-失败, 3-处理中',
    reference_id VARCHAR(64) COMMENT '关联业务ID',
    remarks TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    processed_at DATETIME COMMENT '处理时间',
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (customer_quota_id) REFERENCES customer_quotas(id),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_customer_quota_id (customer_quota_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='额度交易记录表';

-- 初始化额度类型数据
INSERT INTO quota_types (quota_type_name, quota_category, description, is_active) VALUES
('综合授信额度', 1, '客户综合授信额度', TRUE),
('流动资金贷款额度', 2, '流动资金贷款专项额度', TRUE),
('固定资产贷款额度', 2, '固定资产贷款专项额度', TRUE),
('贸易融资额度', 2, '贸易融资专项额度', TRUE),
('担保额度', 3, '基于抵押物的担保额度', TRUE),
('临时调节额度', 4, '临时性调节额度', TRUE);

-- 初始化示例客户数据
INSERT INTO customers (customer_id, customer_name, customer_type, parent_customer_id, customer_level, is_active) VALUES
('GRP001', 'ABC集团', 1, NULL, 'VIP', TRUE),
('CUST001', 'ABC集团子公司1', 2, 'GRP001', 'A', TRUE),
('CUST002', 'ABC集团子公司2', 2, 'GRP001', 'A', TRUE),
('CUST003', '单一客户张三', 3, NULL, 'B', TRUE);

-- 初始化示例额度数据
INSERT INTO customer_quotas (customer_id, quota_type_id, total_amount, used_amount, available_amount, frozen_amount, status, quota_level, effective_date, expire_date) VALUES
('GRP001', 1, 10000000.00, 3000000.00, 6500000.00, 500000.00, 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR)),
('CUST001', 1, 5000000.00, 2000000.00, 3000000.00, 0.00, 1, 2, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR)),
('CUST002', 1, 3000000.00, 1000000.00, 2000000.00, 0.00, 1, 2, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR)),
('CUST003', 1, 1000000.00, 0.00, 1000000.00, 0.00, 1, 2, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR));