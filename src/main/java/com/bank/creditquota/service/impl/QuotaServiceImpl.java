package com.bank.creditquota.service.impl;

import com.bank.creditquota.dto.QuotaRequestDTO;
import com.bank.creditquota.dto.QuotaResponseDTO;
import com.bank.creditqua.repository.CustomerQuotaRepository;
import com.bank.creditquota.entity.CustomerQuota;
import com.bank.creditquota.entity.QuotaTransaction;
import com.bank.creditquota.entity.QuotaTransaction.TransactionType;
import com.bank.creditquota.entity.QuotaTransaction.TransactionStatus;
import com.bank.creditquota.service.QuotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class QuotaServiceImpl implements QuotaService {

    @Autowired
    private CustomerQuotaRepository customerQuotaRepository;

    @Override
    public boolean checkQuotaAvailability(String customerId, Long quotaTypeId, BigDecimal amount) {
        var customerQuotaOpt = customerQuotaRepository.findByCustomerIdAndQuotaTypeId(customerId, quotaTypeId);
        if (customerQuotaOpt.isPresent()) {
            CustomerQuota customerQuota = customerQuotaOpt.get();
            return customerQuota.getAvailableAmount().compareTo(amount) >= 0;
        }
        return false;
    }

    @Override
    public QuotaResponseDTO consumeQuota(QuotaRequestDTO request) {
        var response = new QuotaResponseDTO();
        try {
            var customerQuotaOpt = customerQuotaRepository.findByCustomerIdAndQuotaTypeId(
                request.getCustomerId(), request.getQuotaTypeId());
            
            if (!customerQuotaOpt.isPresent()) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }
            
            CustomerQuota customerQuota = customerQuotaOpt.get();
            
            if (customerQuota.getAvailableAmount().compareTo(request.getAmount()) < 0) {
                response.setSuccess(false);
                response.setMessage("额度不足");
                return response;
            }
            
            // 更新额度
            customerQuota.setUsedAmount(customerQuota.getUsedAmount().add(request.getAmount()));
            customerQuota.setAvailableAmount(customerQuota.getAvailableAmount().subtract(request.getAmount()));
            customerQuota.setUpdatedAt(LocalDateTime.now());
            
            customerQuotaRepository.save(customerQuota);
            
            // 记录交易
            createTransaction(request, customerQuota, TransactionType.CONSUMPTION, 
                             customerQuota.getAvailableAmount().add(request.getAmount()), 
                             customerQuota.getAvailableAmount());
            
            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setStatus(customerQuota.getStatus().name());
            response.setSuccess(true);
            response.setMessage("额度占用成功");
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度占用失败: " + e.getMessage());
        }
        return response;
    }

    @Override
    public QuotaResponseDTO releaseQuota(QuotaRequestDTO request) {
        var response = new QuotaResponseDTO();
        try {
            var customerQuotaOpt = customerQuotaRepository.findByCustomerIdAndQuotaTypeId(
                request.getCustomerId(), request.getQuotaTypeId());
                
            if (!customerQuotaOpt.isPresent()) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }
            
            CustomerQuota customerQuota = customerQuotaOpt.get();
            
            // 更新额度
            customerQuota.setUsedAmount(customerQuota.getUsedAmount().subtract(request.getAmount()));
            customerQuota.setAvailableAmount(customerQuota.getAvailableAmount().add(request.getAmount()));
            customerQuota.setUpdatedAt(LocalDateTime.now());
            
            customerQuotaRepository.save(customerQuota);
            
            // 记录交易
            createTransaction(request, customerQuota, TransactionType.RELEASE, 
                             customerQuota.getAvailableAmount().subtract(request.getAmount()), 
                             customerQuota.getAvailableAmount());
            
            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setStatus(customerQuota.getStatus().name());
            response.setSuccess(true);
            response.setMessage("额度释放成功");
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度释放失败: " + e.getMessage());
        }
        return response;
    }

    @Override
    public QuotaResponseDTO adjustQuota(QuotaRequestDTO request) {
        var response = new QuotaResponseDTO();
        try {
            var customerQuotaOpt = customerQuotaRepository.findByCustomerIdAndQuotaTypeId(
                request.getCustomerId(), request.getQuotaTypeId());
                
            if (!customerQuotaOpt.isPresent()) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }
            
            CustomerQuota customerQuota = customerQuotaOpt.get();
            
            // 调整总额度
            BigDecimal oldTotal = customerQuota.getTotalAmount();
            customerQuota.setTotalAmount(oldTotal.add(request.getAmount()));
            
            // 调整可用额度
            customerQuota.setAvailableAmount(customerQuota.getAvailableAmount().add(request.getAmount()));
            
            customerQuota.setUpdatedAt(LocalDateTime.now());
            
            customerQuotaRepository.save(customerQuota);
            
            // 记录交易
            createTransaction(request, customerQuota, TransactionType.ADJUSTMENT, 
                             oldTotal, customerQuota.getTotalAmount());
            
            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setStatus(customerQuota.getStatus().name());
            response.setSuccess(true);
            response.setMessage("额度调整成功");
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度调整失败: " + e.getMessage());
        }
        return response;
    }

    @Override
    public QuotaResponseDTO freezeQuota(QuotaRequestDTO request) {
        var response = new QuotaResponseDTO();
        try {
            var customerQuotaOpt = customerQuotaRepository.findByCustomerIdAndQuotaTypeId(
                request.getCustomerId(), request.getQuotaTypeId());
                
            if (!customerQuotaOpt.isPresent()) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }
            
            CustomerQuota customerQuota = customerQuotaOpt.get();
            
            if (customerQuota.getAvailableAmount().compareTo(request.getAmount()) < 0) {
                response.setSuccess(false);
                response.setMessage("可用额度不足，无法冻结");
                return response;
            }
            
            // 更新额度
            customerQuota.setAvailableAmount(customerQuota.getAvailableAmount().subtract(request.getAmount()));
            customerQuota.setFrozenAmount(customerQuota.getFrozenAmount().add(request.getAmount()));
            customerQuota.setUpdatedAt(LocalDateTime.now());
            
            customerQuotaRepository.save(customerQuota);
            
            // 记录交易
            createTransaction(request, customerQuota, TransactionType.FROZEN, 
                             customerQuota.getAvailableAmount().add(request.getAmount()), 
                             customerQuota.getAvailableAmount());
            
            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setFrozenAmount(customerQuota.getFrozenAmount());
            response.setStatus(customerQuota.getStatus().name());
            response.setSuccess(true);
            response.setMessage("额度冻结成功");
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度冻结失败: " + e.getMessage());
        }
        return response;
    }

    @Override
    public QuotaResponseDTO unfreezeQuota(QuotaRequestDTO request) {
        var response = new QuotaResponseDTO();
        try {
            var customerQuotaOpt = customerQuotaRepository.findByCustomerIdAndQuotaTypeId(
                request.getCustomerId(), request.getQuotaTypeId());
                
            if (!customerQuotaOpt.isPresent()) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }
            
            CustomerQuota customerQuota = customerQuotaOpt.get();
            
            if (customerQuota.getFrozenAmount().compareTo(request.getAmount()) < 0) {
                response.setSuccess(false);
                response.setMessage("冻结额度不足，无法解冻");
                return response;
            }
            
            // 更新额度
            customerQuota.setAvailableAmount(customerQuota.getAvailableAmount().add(request.getAmount()));
            customerQuota.setFrozenAmount(customerQuota.getFrozenAmount().subtract(request.getAmount()));
            customerQuota.setUpdatedAt(LocalDateTime.now());
            
            customerQuotaRepository.save(customerQuota);
            
            // 记录交易
            createTransaction(request, customerQuota, TransactionType.UNFROZEN, 
                             customerQuota.getFrozenAmount().add(request.getAmount()), 
                             customerQuota.getFrozenAmount());
            
            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setFrozenAmount(customerQuota.getFrozenAmount());
            response.setStatus(customerQuota.getStatus().name());
            response.setSuccess(true);
            response.setMessage("额度解冻成功");
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度解冻失败: " + e.getMessage());
        }
        return response;
    }

    @Override
    public QuotaResponseDTO getCustomerQuota(String customerId, Long quotaTypeId) {
        var response = new QuotaResponseDTO();
        try {
            var customerQuotaOpt = customerQuotaRepository.findByCustomerIdAndQuotaTypeId(customerId, quotaTypeId);
            
            if (!customerQuotaOpt.isPresent()) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }
            
            CustomerQuota customerQuota = customerQuotaOpt.get();
            
            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setFrozenAmount(customerQuota.getFrozenAmount());
            response.setStatus(customerQuota.getStatus().name());
            response.setSuccess(true);
            response.setMessage("获取客户额度信息成功");
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("获取客户额度信息失败: " + e.getMessage());
        }
        return response;
    }

    @Override
    public QuotaResponseDTO getCustomerTotalQuota(String customerId) {
        var response = new QuotaResponseDTO();
        try {
            // 这里可以调用repository的方法来获取客户总额度信息
            var quotas = customerQuotaRepository.findByCustomerId(customerId);
            
            if (quotas.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("客户无额度记录");
                return response;
            }
            
            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal usedAmount = BigDecimal.ZERO;
            BigDecimal availableAmount = BigDecimal.ZERO;
            BigDecimal frozenAmount = BigDecimal.ZERO;
            
            for (CustomerQuota quota : quotas) {
                totalAmount = totalAmount.add(quota.getTotalAmount());
                usedAmount = usedAmount.add(quota.getUsedAmount());
                availableAmount = availableAmount.add(quota.getAvailableAmount());
                frozenAmount = frozenAmount.add(quota.getFrozenAmount());
            }
            
            response.setCustomerId(customerId);
            response.setTotalAmount(totalAmount);
            response.setUsedAmount(usedAmount);
            response.setAvailableAmount(availableAmount);
            response.setFrozenAmount(frozenAmount);
            response.setStatus("ACTIVE");
            response.setSuccess(true);
            response.setMessage("获取客户总额度信息成功");
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("获取客户总额度信息失败: " + e.getMessage());
        }
        return response;
    }

    @Override
    public QuotaResponseDTO allocateQuota(QuotaRequestDTO request) {
        var response = new QuotaResponseDTO();
        try {
            // 检查是否已存在相同的额度类型
            var existingQuotaOpt = customerQuotaRepository.findByCustomerIdAndQuotaTypeId(
                request.getCustomerId(), request.getQuotaTypeId());
                
            if (existingQuotaOpt.isPresent()) {
                response.setSuccess(false);
                response.setMessage("客户已存在相同类型的额度");
                return response;
            }
            
            // 创建新的额度记录
            CustomerQuota customerQuota = new CustomerQuota();
            customerQuota.setCustomerId(request.getCustomerId());
            customerQuota.setQuotaTypeId(request.getQuotaTypeId());
            customerQuota.setTotalAmount(request.getAmount());
            customerQuota.setUsedAmount(BigDecimal.ZERO);
            customerQuota.setAvailableAmount(request.getAmount());
            customerQuota.setFrozenAmount(BigDecimal.ZERO);
            customerQuota.setEffectiveDate(LocalDateTime.now());
            customerQuota.setCreatedAt(LocalDateTime.now());
            customerQuota.setUpdatedAt(LocalDateTime.now());
            
            customerQuotaRepository.save(customerQuota);
            
            // 记录交易
            createTransaction(request, customerQuota, TransactionType.ALLOCATION, 
                             BigDecimal.ZERO, customerQuota.getTotalAmount());
            
            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setStatus(customerQuota.getStatus().name());
            response.setSuccess(true);
            response.setMessage("额度分配成功");
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度分配失败: " + e.getMessage());
        }
        return response;
    }

    @Override
    public QuotaResponseDTO recoverQuota(QuotaRequestDTO request) {
        var response = new QuotaResponseDTO();
        try {
            var customerQuotaOpt = customerQuotaRepository.findByCustomerIdAndQuotaTypeId(
                request.getCustomerId(), request.getQuotaTypeId());
                
            if (!customerQuotaOpt.isPresent()) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }
            
            CustomerQuota customerQuota = customerQuotaOpt.get();
            
            // 回收额度：减少总额度
            BigDecimal oldTotal = customerQuota.getTotalAmount();
            customerQuota.setTotalAmount(oldTotal.subtract(request.getAmount()));
            
            // 相应地调整可用额度
            customerQuota.setAvailableAmount(customerQuota.getAvailableAmount().subtract(request.getAmount()));
            
            customerQuota.setUpdatedAt(LocalDateTime.now());
            
            customerQuotaRepository.save(customerQuota);
            
            // 记录交易
            createTransaction(request, customerQuota, TransactionType.RECOVERY, 
                             oldTotal, customerQuota.getTotalAmount());
            
            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setStatus(customerQuota.getStatus().name());
            response.setSuccess(true);
            response.setMessage("额度回收成功");
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度回收失败: " + e.getMessage());
        }
        return response;
    }
    
    private void createTransaction(QuotaRequestDTO request, CustomerQuota customerQuota, 
                                  TransactionType type, BigDecimal beforeBalance, BigDecimal afterBalance) {
        // 在实际实现中，这里会创建QuotaTransaction记录
        // 为简化演示，这里只记录基本交易信息
    }
}