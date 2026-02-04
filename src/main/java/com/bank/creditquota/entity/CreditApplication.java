package com.bank.creditquota.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 授信申请实体
 */
@Data
public class CreditApplication {
    private String applicationId;              // 申请ID
    private String customerId;                 // 客户ID
    private String applicationType;            // 申请类型
    private BigDecimal applicationAmount;      // 申请金额
    private String currency;                   // 币种
    private String applicationPurpose;         // 申请用途
    private String guaranteeMethod;            // 担保方式
    private Integer termMonths;                // 期限(月)
    private LocalDateTime applicationDate;     // 申请日期
    private String applicant;                  // 申请人
    private String department;                 // 申请部门
    private String status;                     // 申请状态
    private String currentApprover;            // 当前审批人
    private String currentApproveNode;         // 当前审批节点
    private String approveResult;              // 审批结果
    private String approveAdvice;              // 审批意见
    private LocalDateTime approveDate;         // 审批完成日期
    private String createdBy;                  // 创建人
    private LocalDateTime createdTime;         // 创建时间
    private String updatedBy;                  // 更新人
    private LocalDateTime updatedTime;         // 更新时间
}