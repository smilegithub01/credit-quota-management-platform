package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.QuotaUsageDetail;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface QuotaUsageDetailMapper {
    int insert(QuotaUsageDetail record);
    int updateById(QuotaUsageDetail record);
    int deleteById(Long detailId);
    QuotaUsageDetail selectById(Long detailId);
    List<QuotaUsageDetail> selectAll();
    List<QuotaUsageDetail> selectByQuotaId(@Param("quotaId") Long quotaId);
    List<QuotaUsageDetail> selectByCustomerId(@Param("customerId") String customerId);
    List<QuotaUsageDetail> selectByUsageId(@Param("usageId") String usageId);
    List<QuotaUsageDetail> selectByTransactionType(@Param("transactionType") String transactionType);
}