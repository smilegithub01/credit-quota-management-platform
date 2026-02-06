package com.bank.creditquota.repository;

import com.bank.creditquota.entity.CustomerQuota;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@Mapper
public interface CustomerQuotaRepository {
    int insert(CustomerQuota customerQuota);
    
    int updateById(CustomerQuota customerQuota);
    
    int deleteById(Long id);
    
    CustomerQuota selectById(Long id);
    
    List<CustomerQuota> selectAll();
    
    List<CustomerQuota> findByCustomerId(String customerId);
    
    Optional<CustomerQuota> findByCustomerIdAndQuotaTypeId(String customerId, Long quotaTypeId);
    
    BigDecimal getTotalQuotaByCustomer(@Param("customerId") String customerId);
    
    BigDecimal getAvailableQuotaByCustomer(@Param("customerId") String customerId);
}