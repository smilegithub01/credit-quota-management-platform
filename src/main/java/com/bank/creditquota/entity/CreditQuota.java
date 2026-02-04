package com.bank.creditquota.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 授信额度实体
 */
@Data
public class CreditQuota {
    private Long quotaId;                      // 额度ID
    private String customerId;                 // 客户ID
    private String quotaType;                  // 额度类型
    private String quotaSubtype;              // 额度子类型
    private BigDecimal totalQuota;            // 总额度
    private BigDecimal usedQuota;             // 已用额度
    private BigDecimal availableQuota;        // 可用额度
    private BigDecimal frozenQuota;           // 冻结额度
    private String currency;                   // 币种
    private String quotaLevel;                // 额度层级
    private Long parentQuotaId;               // 父额度ID
    private LocalDateTime effectiveDate;       // 生效日期
    private LocalDateTime expireDate;          // 到期日期
    private String quotaStatus;               // 额度状态
    private String guaranteeInfo;             // 担保信息
    private String riskControlMeasures;       // 风险控制措施
    private String quotaManager;              // 额度管理员
    private String createdBy;                 // 创建人
    private LocalDateTime createdTime;        // 创建时间
    private String updatedBy;                 // 更新人
    private LocalDateTime updatedTime;        // 更新时间
}