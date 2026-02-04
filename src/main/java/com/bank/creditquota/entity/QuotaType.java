package com.bank.creditquota.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class QuotaType {
    private Long id;
    private String quotaTypeName;
    private Integer quotaCategory; // 1-授信额度, 2-产品额度, 3-担保额度, 4-临时额度
    private String description;
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}