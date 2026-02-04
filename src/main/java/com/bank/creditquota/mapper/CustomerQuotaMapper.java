package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.CustomerQuota;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CustomerQuotaMapper {
    int insert(CustomerQuota customerQuota);
    
    int updateById(CustomerQuota customerQuota);
    
    int deleteById(Long id);
    
    CustomerQuota selectById(Long id);
    
    List<CustomerQuota> selectAll();
    
    List<CustomerQuota> selectByCustomerId(String customerId);
    
    List<CustomerQuota> selectByQuotaTypeId(Long quotaTypeId);
    
    CustomerQuota selectByCustomerIdAndQuotaType(@Param("customerId") String customerId, 
                                                @Param("quotaTypeId") Long quotaTypeId);
    
    int updateAvailableAmount(@Param("id") Long id, 
                              @Param("amount") BigDecimal amount);
    
    int updateUsedAmount(@Param("id") Long id, 
                         @Param("amount") BigDecimal amount);
    
    int updateStatus(@Param("id") Long id, 
                     @Param("status") Integer status);
}