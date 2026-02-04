package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.QuotaTransaction;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface QuotaTransactionMapper {
    int insert(QuotaTransaction quotaTransaction);
    
    int updateById(QuotaTransaction quotaTransaction);
    
    int deleteById(Long id);
    
    QuotaTransaction selectById(Long id);
    
    List<QuotaTransaction> selectAll();
    
    List<QuotaTransaction> selectByCustomerId(String customerId);
    
    List<QuotaTransaction> selectByCustomerQuotaId(Long customerQuotaId);
    
    List<QuotaTransaction> selectByTransactionId(String transactionId);
}