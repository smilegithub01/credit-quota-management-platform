package com.bank.creditquota.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 集团客户关系实体
 */
@Data
public class GroupRelationship {
    private Long id;                           // 主键ID
    private String parentCustomerId;          // 父客户ID
    private String childCustomerId;           // 子客户ID
    private String relationshipType;          // 关系类型
    private Double controlRatio;              // 控制比例(%)
    private String relationshipDesc;          // 关系描述
    private LocalDateTime effectiveDate;      // 生效日期
    private LocalDateTime expireDate;         // 失效日期
    private String status;                    // 关系状态
    private String createdBy;                 // 创建人
    private LocalDateTime createdTime;        // 创建时间
    private String updatedBy;                 // 更新人
    private LocalDateTime updatedTime;        // 更新时间
}