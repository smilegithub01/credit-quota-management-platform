package com.bank.creditquota.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerQuota {
    private Long id;
    private String customerId;      // 客户ID
    private Long quotaTypeId;       // 额度类型ID
    private BigDecimal totalAmount; // 总额度
    private BigDecimal usedAmount;  // 已用额度
    private BigDecimal availableAmount; // 可用额度
    private BigDecimal frozenAmount;    // 冻结额度
    private Integer status;             // 状态：1-启用, 0-禁用
    private Integer quotaLevel;         // 额度层级：1-集团额度, 2-客户额度
    private String parentQuotaId;       // 父额度ID（用于集团额度分解）
    private LocalDateTime effectiveDate; // 生效日期
    private LocalDateTime expireDate;    // 到期日期
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}