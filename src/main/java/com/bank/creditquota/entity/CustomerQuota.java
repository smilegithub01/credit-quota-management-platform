package com.bank.creditquota.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_quotas")
@Data
public class CustomerQuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String customerId;
    
    @Column(nullable = false)
    private Long quotaTypeId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotaTypeId", insertable = false, updatable = false)
    private QuotaType quotaType;
    
    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal totalAmount;
    
    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal usedAmount = BigDecimal.ZERO;
    
    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal availableAmount;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal frozenAmount = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    private QuotaStatus status = QuotaStatus.ACTIVE;
    
    private LocalDateTime effectiveDate;
    
    private LocalDateTime expireDate;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    public enum QuotaStatus {
        ACTIVE, INACTIVE, FROZEN, EXPIRED
    }
}