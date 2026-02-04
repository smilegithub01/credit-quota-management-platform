package com.bank.creditquota.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户信息实体
 */
@Data
public class CustomerInfo {
    private String customerId;           // 客户ID
    private String customerName;         // 客户名称
    private String customerType;         // 客户类型
    private String customerLevel;        // 客户等级
    private String businessLicenseNo;    // 营业执照号
    private String organizationCode;     // 组织机构代码
    private String taxRegisterNo;        // 税务登记号
    private BigDecimal registeredCapital; // 注册资本
    private LocalDateTime registeredDate; // 注册日期
    private String legalRepresentative;  // 法定代表人
    private String businessScope;        // 经营范围
    private String industryType;         // 所属行业
    private String province;             // 所在省份
    private String city;                 // 所在城市
    private String address;              // 详细地址
    private String phone;                // 联系电话
    private String status;               // 客户状态
    private String createdBy;            // 创建人
    private LocalDateTime createdTime;   // 创建时间
    private String updatedBy;            // 更新人
    private LocalDateTime updatedTime;   // 更新时间
}