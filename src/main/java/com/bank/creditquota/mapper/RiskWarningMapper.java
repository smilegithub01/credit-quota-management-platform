package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.RiskWarning;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface RiskWarningMapper {
    int insert(RiskWarning record);
    int updateById(RiskWarning record);
    int deleteById(Long warningId);
    RiskWarning selectById(Long warningId);
    List<RiskWarning> selectAll();
    List<RiskWarning> selectByCustomerId(@Param("customerId") String customerId);
    List<RiskWarning> selectByWarningType(@Param("warningType") String warningType);
    List<RiskWarning> selectByWarningStatus(@Param("warningStatus") String warningStatus);
    List<RiskWarning> selectByRiskLevel(@Param("riskLevel") String riskLevel);
}