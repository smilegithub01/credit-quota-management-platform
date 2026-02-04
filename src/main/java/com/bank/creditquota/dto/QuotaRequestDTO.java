package com.bank.creditquota.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class QuotaRequestDTO {
    private String customerId;
    private String quotaType;  // 修改为String类型以匹配新的数据库结构
    private Long quotaTypeId;  // 保留兼容性
    private BigDecimal amount;
    private String referenceId;
    private String remarks;
    private String quotaLevel; // 额度层级
    private String currency;   // 币种
}