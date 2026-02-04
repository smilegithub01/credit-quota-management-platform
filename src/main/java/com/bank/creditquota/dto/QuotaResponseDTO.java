package com.bank.creditquota.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class QuotaResponseDTO {
    private String customerId;
    private Long quotaTypeId;
    private String quotaTypeName;
    private BigDecimal totalAmount;
    private BigDecimal usedAmount;
    private BigDecimal availableAmount;
    private BigDecimal frozenAmount;
    private String status;
    private String message;
    private boolean success;
}