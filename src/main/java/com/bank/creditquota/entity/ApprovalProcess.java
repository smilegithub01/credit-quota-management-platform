package com.bank.creditquota.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审批流程实体
 */
@Data
public class ApprovalProcess {
    private String processId;              // 流程ID
    private String businessType;          // 业务类型
    private String businessId;            // 业务ID
    private String processDefinitionId;   // 流程定义ID
    private String processName;           // 流程名称
    private String currentNode;           // 当前节点
    private String currentAssignee;       // 当前处理人
    private String processStatus;         // 流程状态
    private String priority;              // 优先级
    private LocalDateTime startTime;      // 开始时间
    private LocalDateTime endTime;        // 结束时间
    private String createdBy;             // 发起人
    private LocalDateTime createdTime;    // 发起时间
    private String updatedBy;             // 更新人
    private LocalDateTime updatedTime;    // 更新时间
}