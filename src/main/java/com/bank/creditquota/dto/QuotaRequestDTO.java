package com.bank.creditquota.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class QuotaRequestDTO {
    private String customerId;
    private Long quotaTypeId;
    private BigDecimal amount;
    private String referenceId;
    private String remarks;
}