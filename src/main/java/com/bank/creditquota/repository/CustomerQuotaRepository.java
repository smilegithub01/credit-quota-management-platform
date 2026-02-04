package com.bank.creditquota.repository;

import com.bank.creditquota.entity.CustomerQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerQuotaRepository extends JpaRepository<CustomerQuota, Long> {
    List<CustomerQuota> findByCustomerId(String customerId);
    
    Optional<CustomerQuota> findByCustomerIdAndQuotaTypeId(String customerId, Long quotaTypeId);
    
    @Query("SELECT SUM(cq.totalAmount) FROM CustomerQuota cq WHERE cq.customerId = ?1 AND cq.status = 'ACTIVE'")
    java.math.BigDecimal getTotalQuotaByCustomer(String customerId);
    
    @Query("SELECT SUM(cq.availableAmount) FROM CustomerQuota cq WHERE cq.customerId = ?1 AND cq.status = 'ACTIVE'")
    java.math.BigDecimal getAvailableQuotaByCustomer(String customerId);
}