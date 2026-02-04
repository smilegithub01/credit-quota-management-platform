package com.bank.creditquota.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 风险监控指标实体
 */
@Data
public class RiskMonitoringIndex {
    private Long indexId;              // 指标ID
    private String customerId;         // 客户ID
    private String indexType;          // 指标类型
    private String indexCode;          // 指标代码
    private String indexName;          // 指标名称
    private BigDecimal indexValue;     // 指标值
    private String indexUnit;          // 指标单位
    private BigDecimal thresholdValue; // 阈值
    private String riskLevel;          // 风险等级
    private LocalDateTime calcDate;    // 计算日期
    private String calcMethod;         // 计算方法
    private String dataSource;         // 数据来源
    private String createdBy;          // 创建人
    private LocalDateTime createdTime; // 创建时间
}