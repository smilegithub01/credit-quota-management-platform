package com.bank.creditquota.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quota_transactions")
@Data
public class QuotaTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String transactionId;
    
    @Column(nullable = false)
    private String customerId;
    
    @Column(nullable = false)
    private Long customerQuotaId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerQuotaId", insertable = false, updatable = false)
    private CustomerQuota customerQuota;
    
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    
    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal beforeBalance;
    
    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal afterBalance;
    
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    
    private String referenceId;
    
    private String remarks;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime processedAt;
    
    public enum TransactionType {
        ALLOCATION("额度分配"),
        CONSUMPTION("额度占用"),
        RELEASE("额度释放"),
        ADJUSTMENT("额度调整"),
        FROZEN("额度冻结"),
        UNFROZEN("额度解冻"),
        RECOVERY("额度回收");
        
        private final String displayName;
        
        TransactionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum TransactionStatus {
        PENDING, SUCCESS, FAILED, CANCELLED
    }
}