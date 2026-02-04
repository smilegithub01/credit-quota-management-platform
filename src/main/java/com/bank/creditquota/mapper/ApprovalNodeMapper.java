package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.ApprovalNode;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ApprovalNodeMapper {
    int insert(ApprovalNode record);
    int updateById(ApprovalNode record);
    int deleteById(Long nodeId);
    ApprovalNode selectById(Long nodeId);
    List<ApprovalNode> selectAll();
    List<ApprovalNode> selectByProcessId(@Param("processId") String processId);
    List<ApprovalNode> selectByNodeStatus(@Param("nodeStatus") String nodeStatus);
    List<ApprovalNode> selectByAssigneeId(@Param("assigneeId") String assigneeId);
    List<ApprovalNode> selectByApproveResult(@Param("approveResult") String approveResult);
}