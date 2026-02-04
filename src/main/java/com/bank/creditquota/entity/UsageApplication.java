package com.bank.creditquota.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用信申请实体
 */
@Data
public class UsageApplication {
    private String usageId;               // 用信申请ID
    private String applicationId;         // 授信申请ID
    private String customerId;            // 客户ID
    private Long quotaId;                 // 额度ID
    private String productType;           // 产品类型
    private BigDecimal usageAmount;     // 用信金额
    private String currency;              // 币种
    private Integer usageTerm;            // 用信期限(天)
    private BigDecimal interestRate;      // 利率(%)
    private String repaymentMethod;       // 还款方式
    private String guaranteeMethod;       // 担保方式
    private String fundUsage;             // 资金用途
    private LocalDateTime applicationDate; // 申请日期
    private String status;                // 状态
    private String contractNo;            // 合同编号
    private String loanNoteNo;            // 借据编号
    private LocalDateTime disbursementDate; // 放款日期
    private LocalDateTime maturityDate;     // 到期日期
    private String createdBy;             // 创建人
    private LocalDateTime createdTime;    // 创建时间
    private String updatedBy;             // 更新人
    private LocalDateTime updatedTime;    // 更新时间
}