package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.CreditQuota;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CreditQuotaMapper {
    int insert(CreditQuota creditQuota);
    
    int updateById(CreditQuota creditQuota);
    
    int deleteById(Long quotaId);
    
    CreditQuota selectById(Long quotaId);
    
    List<CreditQuota> selectAll();
    
    List<CreditQuota> selectByCustomerId(@Param("customerId") String customerId);
    
    List<CreditQuota> selectByQuotaType(@Param("quotaType") String quotaType);
    
    List<CreditQuota> selectByStatus(@Param("status") String status);
    
    CreditQuota selectByCustomerIdAndQuotaType(@Param("customerId") String customerId, 
                                              @Param("quotaType") String quotaType);
    
    int updateAvailableQuota(@Param("quotaId") Long quotaId, 
                            @Param("amount") BigDecimal amount);
    
    int updateUsedQuota(@Param("quotaId") Long quotaId, 
                       @Param("amount") BigDecimal amount);
    
    int updateQuotaStatus(@Param("quotaId") Long quotaId, 
                         @Param("status") String status);
}