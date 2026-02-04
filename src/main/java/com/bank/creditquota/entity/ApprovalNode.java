package com.bank.creditquota.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审批节点实体
 */
@Data
public class ApprovalNode {
    private Long nodeId;                   // 节点ID
    private String processId;             // 流程ID
    private String nodeName;              // 节点名称
    private String nodeType;              // 节点类型
    private String assigneeType;          // 处理人类型
    private String assigneeId;            // 处理人ID
    private String assigneeName;          // 处理人姓名
    private String nodeStatus;            // 节点状态
    private String approveResult;         // 审批结果
    private String approveOpinion;        // 审批意见
    private LocalDateTime startTime;      // 开始时间
    private LocalDateTime endTime;        // 结束时间
    private Integer durationMinutes;      // 处理时长(分钟)
    private LocalDateTime createdTime;    // 创建时间
    private LocalDateTime updatedTime;    // 更新时间
}