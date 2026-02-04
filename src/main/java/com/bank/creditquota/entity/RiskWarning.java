package com.bank.creditquota.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 风险预警实体
 */
@Data
public class RiskWarning {
    private Long warningId;              // 预警ID
    private String customerId;           // 客户ID
    private String warningType;          // 预警类型
    private String warningCode;          // 预警代码
    private String warningTitle;         // 预警标题
    private String warningContent;       // 预警内容
    private String riskLevel;            // 风险等级
    private String warningStatus;        // 预警状态
    private String handler;              // 处理人
    private LocalDateTime handleTime;    // 处理时间
    private String handleResult;         // 处理结果
    private LocalDateTime warningDate;   // 预警日期
    private LocalDateTime resolveDate;   // 解除日期
    private String createdBy;            // 创建人
    private LocalDateTime createdTime;   // 创建时间
    private String updatedBy;            // 更新人
    private LocalDateTime updatedTime;   // 更新时间
}