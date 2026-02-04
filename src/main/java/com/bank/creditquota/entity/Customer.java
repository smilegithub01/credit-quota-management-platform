package com.bank.creditquota.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Customer {
    private String customerId;
    private String customerName;
    private Integer customerType; // 1-集团客户, 2-成员单位, 3-单一客户
    private String parentCustomerId; // 父客户ID，用于集团客户结构
    private String customerLevel; // 客户等级
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}