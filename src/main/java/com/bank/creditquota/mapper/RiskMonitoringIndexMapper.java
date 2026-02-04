package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.RiskMonitoringIndex;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface RiskMonitoringIndexMapper {
    int insert(RiskMonitoringIndex record);
    int updateById(RiskMonitoringIndex record);
    int deleteById(Long indexId);
    RiskMonitoringIndex selectById(Long indexId);
    List<RiskMonitoringIndex> selectAll();
    List<RiskMonitoringIndex> selectByCustomerId(@Param("customerId") String customerId);
    List<RiskMonitoringIndex> selectByIndexType(@Param("indexType") String indexType);
    List<RiskMonitoringIndex> selectByIndexCode(@Param("indexCode") String indexCode);
    List<RiskMonitoringIndex> selectByRiskLevel(@Param("riskLevel") String riskLevel);
}