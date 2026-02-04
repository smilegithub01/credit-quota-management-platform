package com.bank.creditquota.repository;

import com.bank.creditquota.entity.QuotaTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuotaTransactionRepository extends JpaRepository<QuotaTransaction, Long> {
    List<QuotaTransaction> findByCustomerId(String customerId);
    
    List<QuotaTransaction> findByCustomerQuotaId(Long customerQuotaId);
    
    List<QuotaTransaction> findByTransactionId(String transactionId);
}