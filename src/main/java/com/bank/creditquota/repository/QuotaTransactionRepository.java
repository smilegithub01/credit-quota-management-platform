package com.bank.creditquota.repository;

import com.bank.creditquota.entity.QuotaTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Mapper
public interface QuotaTransactionRepository {
    int insert(QuotaTransaction quotaTransaction);
    
    int updateById(QuotaTransaction quotaTransaction);
    
    int deleteById(Long id);
    
    QuotaTransaction selectById(Long id);
    
    List<QuotaTransaction> selectAll();
    
    List<QuotaTransaction> findByCustomerId(@Param("customerId") String customerId);
    
    List<QuotaTransaction> findByCustomerQuotaId(@Param("customerQuotaId") Long customerQuotaId);
    
    List<QuotaTransaction> findByTransactionId(@Param("transactionId") String transactionId);
}