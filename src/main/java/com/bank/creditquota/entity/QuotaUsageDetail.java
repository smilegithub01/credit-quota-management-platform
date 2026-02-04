package com.bank.creditquota.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 额度使用明细实体
 */
@Data
public class QuotaUsageDetail {
    private Long detailId;                 // 明细ID
    private Long quotaId;                  // 额度ID
    private String customerId;             // 客户ID
    private String usageId;                // 用信申请ID
    private String transactionType;        // 交易类型
    private BigDecimal transactionAmount;  // 交易金额
    private BigDecimal beforeBalance;      // 交易前余额
    private BigDecimal afterBalance;       // 交易后余额
    private String businessType;           // 业务类型
    private String businessRefNo;          // 业务参考号
    private String operator;               // 操作员
    private LocalDateTime transactionTime; // 交易时间
    private String remark;                 // 备注
    private String createdBy;              // 创建人
    private LocalDateTime createdTime;     // 创建时间
    private String updatedBy;              // 更新人
    private LocalDateTime updatedTime;     // 更新时间
}