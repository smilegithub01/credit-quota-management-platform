-- 数据库结构迁移脚本：从原表结构迁移到优化表结构
-- 适用于从使用计算字段的t_credit_quota表迁移到手动维护可用额度的表结构

-- 步骤1: 备份原表（可选但推荐）
-- CREATE TABLE t_credit_quota_backup AS SELECT * FROM t_credit_quota;

-- 步骤2: 修改t_credit_quota表结构
-- 移除原有的计算字段定义，改为普通字段
ALTER TABLE t_credit_quota MODIFY COLUMN available_quota DECIMAL(18,2) DEFAULT 0.00 COMMENT '可用额度(手动维护：总额度-已用额度-冻结额度)';

-- 步骤3: 更新可用额度字段值，使其等于总额度-已用额度-冻结额度
UPDATE t_credit_quota 
SET available_quota = total_quota - used_quota - frozen_quota
WHERE quota_status IN ('ACTIVE', 'INACTIVE', 'FROZEN');

-- 步骤4: 验证数据一致性
SELECT 
    quota_id,
    customer_id,
    total_quota,
    used_quota,
    frozen_quota,
    available_quota,
    (total_quota - used_quota - frozen_quota) as calculated_available,
    CASE 
        WHEN ABS(available_quota - (total_quota - used_quota - frozen_quota)) > 0.01 
        THEN 'ERROR' 
        ELSE 'OK' 
    END as status
FROM t_credit_quota
WHERE ABS(available_quota - (total_quota - used_quota - frozen_quota)) > 0.01;

-- 步骤5: 添加新的交易类型到额度使用明细表（如果还没有的话）
-- 注意：在优化的schema.sql中，我们已经在transaction_type字段中添加了PRE_OCCUPY和CANCEL_PRE_OCCUPY
-- 如果表中已有数据，可能需要修改字段定义
ALTER TABLE t_quota_usage_detail MODIFY COLUMN transaction_type ENUM('OCCUPY', 'RELEASE', 'FREEZE', 'UNFREEZE', 'ADJUST', 'PRE_OCCUPY', 'CANCEL_PRE_OCCUPY') NOT NULL COMMENT '交易类型';

-- 步骤6: 更新风险预警表，添加RESOLVED状态选项（如果还没有的话）
ALTER TABLE t_risk_warning MODIFY COLUMN warning_status ENUM('UNHANDLED', 'HANDLING', 'HANDLED', 'IGNORED', 'RESOLVED') DEFAULT 'UNHANDLED' COMMENT '预警状态';

-- 步骤7: 更新审批节点表，添加更多处理人类型和审批结果选项
ALTER TABLE t_approval_node MODIFY COLUMN assignee_type ENUM('USER', 'ROLE', 'DEPARTMENT', 'POSITION') COMMENT '处理人类型';
ALTER TABLE t_approval_node MODIFY COLUMN approve_result ENUM('PENDING', 'APPROVED', 'REJECTED', 'TRANSFERRED', 'DELEGATED') DEFAULT 'PENDING' COMMENT '审批结果';
ALTER TABLE t_approval_node MODIFY COLUMN node_status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING' COMMENT '节点状态';

-- 步骤8: 更新系统参数配置表，添加BOOLEAN数据类型
ALTER TABLE t_sys_param_config MODIFY COLUMN data_type ENUM('STRING', 'NUMBER', 'DATE', 'JSON', 'BOOLEAN') DEFAULT 'STRING' COMMENT '数据类型';

-- 步骤9: 添加缺失的索引以提高查询性能
ALTER TABLE t_quota_usage_detail ADD COLUMN created_by VARCHAR(64) COMMENT '创建人' AFTER remark;
ALTER TABLE t_quota_usage_detail ADD COLUMN updated_by VARCHAR(64) COMMENT '更新人' AFTER created_by;
ALTER TABLE t_quota_usage_detail ADD COLUMN updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER updated_by;

-- 步骤10: 为新增的字段添加索引
CREATE INDEX idx_quota_usage_detail_created_by ON t_quota_usage_detail(created_by);
CREATE INDEX idx_quota_usage_detail_updated_time ON t_quota_usage_detail(updated_time);

-- 步骤11: 验证迁移结果
SELECT 
    COUNT(*) as total_records,
    SUM(CASE WHEN ABS(available_quota - (total_quota - used_quota - frozen_quota)) <= 0.01 THEN 1 ELSE 0 END) as consistent_records,
    SUM(CASE WHEN ABS(available_quota - (total_quota - used_quota - frozen_quota)) > 0.01 THEN 1 ELSE 0 END) as inconsistent_records
FROM t_credit_quota;

-- 如果inconsistent_records为0，说明迁移成功
-- 如果有不一致的记录，需要手动修正

-- 步骤12: 创建存储过程来自动更新可用额度（可选）
DELIMITER //
CREATE PROCEDURE UpdateAvailableQuota(IN quota_id_param BIGINT)
BEGIN
    UPDATE t_credit_quota 
    SET available_quota = total_quota - used_quota - frozen_quota
    WHERE quota_id = quota_id_param;
END //

CREATE PROCEDURE RefreshAllAvailableQuota()
BEGIN
    UPDATE t_credit_quota 
    SET available_quota = total_quota - used_quota - frozen_quota;
END //
DELIMITER ;

-- 使用示例：
-- CALL UpdateAvailableQuota(1); -- 更新指定额度ID的可用额度
-- CALL RefreshAllAvailableQuota(); -- 更新所有额度的可用额度

-- 迁移完成提示：
-- 1. 所有额度表现在使用手动维护的可用额度字段，不再依赖计算字段
-- 2. 额度使用明细表现在记录创建人和更新人信息
-- 3. 所有枚举字段已扩展以支持更多业务场景
-- 4. 系统现在能够正确跟踪所有额度变更操作