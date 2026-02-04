package com.bank.creditquota.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class QuotaTransaction {
    private Long id;
    private String transactionId;
    private String customerId;
    private Long customerQuotaId;
    private Integer transactionType;    // 1-分配, 2-占用, 3-释放, 4-调整, 5-冻结, 6-解冻, 7-回收, 8-启用, 9-停用
    private BigDecimal amount;
    private BigDecimal beforeBalance;
    private BigDecimal afterBalance;
    private Integer status;             // 1-成功, 2-失败, 3-处理中
    private String referenceId;         // 关联业务ID
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}