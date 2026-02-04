package com.bank.creditquota.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quota_types")
@Data
public class QuotaType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String quotaTypeName;
    
    @Enumerated(EnumType.STRING)
    private QuotaCategory category;
    
    private String description;
    
    private Boolean isActive = true;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    public enum QuotaCategory {
        CREDIT("授信额度"),
        PRODUCT("产品额度"),
        COLLATERAL("担保额度"),
        TEMPORARY("临时额度");
        
        private final String displayName;
        
        QuotaCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}