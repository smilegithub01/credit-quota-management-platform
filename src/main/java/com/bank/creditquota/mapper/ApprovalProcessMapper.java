package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.ApprovalProcess;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ApprovalProcessMapper {
    int insert(ApprovalProcess record);
    int updateById(ApprovalProcess record);
    int deleteById(String processId);
    ApprovalProcess selectById(String processId);
    List<ApprovalProcess> selectAll();
    List<ApprovalProcess> selectByBusinessType(@Param("businessType") String businessType);
    List<ApprovalProcess> selectByBusinessId(@Param("businessId") String businessId);
    List<ApprovalProcess> selectByProcessStatus(@Param("processStatus") String processStatus);
    List<ApprovalProcess> selectByCurrentAssignee(@Param("currentAssignee") String currentAssignee);
}