package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.UsageApplication;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UsageApplicationMapper {
    int insert(UsageApplication record);
    int updateById(UsageApplication record);
    int deleteById(String usageId);
    UsageApplication selectById(String usageId);
    List<UsageApplication> selectAll();
    List<UsageApplication> selectByCustomerId(@Param("customerId") String customerId);
    List<UsageApplication> selectByQuotaId(@Param("quotaId") Long quotaId);
    List<UsageApplication> selectByProductType(@Param("productType") String productType);
    List<UsageApplication> selectByStatus(@Param("status") String status);
    List<UsageApplication> selectByApplicationId(@Param("applicationId") String applicationId);
}