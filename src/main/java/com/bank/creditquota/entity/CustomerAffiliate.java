package com.bank.creditquota.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户关联方实体
 */
@Data
public class CustomerAffiliate {
    private Long id;                         // 主键ID
    private String customerId;              // 客户ID
    private String affiliateType;           // 关联方类型
    private String affiliateId;             // 关联方客户ID
    private String affiliateName;           // 关联方名称
    private String affiliateIdentity;       // 关联方身份
    private String relationshipDesc;        // 关系描述
    private BigDecimal relationshipRatio;   // 关联比例(%)
    private String status;                  // 状态
    private String createdBy;               // 创建人
    private LocalDateTime createdTime;      // 创建时间
    private String updatedBy;               // 更新人
    private LocalDateTime updatedTime;      // 更新时间
}