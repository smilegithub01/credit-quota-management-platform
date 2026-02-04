package com.bank.creditquota.service;

import com.bank.creditquota.dto.QuotaRequestDTO;
import com.bank.creditquota.dto.QuotaResponseDTO;
import com.bank.creditquota.entity.CustomerQuota;
import java.math.BigDecimal;

public interface QuotaService {
    /**
     * 检查额度是否充足
     */
    boolean checkQuotaAvailability(String customerId, Long quotaTypeId, BigDecimal amount);
    
    /**
     * 占用额度
     */
    QuotaResponseDTO consumeQuota(QuotaRequestDTO request);
    
    /**
     * 释放额度
     */
    QuotaResponseDTO releaseQuota(QuotaRequestDTO request);
    
    /**
     * 调整额度
     */
    QuotaResponseDTO adjustQuota(QuotaRequestDTO request);
    
    /**
     * 冻结额度
     */
    QuotaResponseDTO freezeQuota(QuotaRequestDTO request);
    
    /**
     * 解冻额度
     */
    QuotaResponseDTO unfreezeQuota(QuotaRequestDTO request);
    
    /**
     * 获取客户额度信息
     */
    QuotaResponseDTO getCustomerQuota(String customerId, Long quotaTypeId);
    
    /**
     * 获取客户总额度信息
     */
    QuotaResponseDTO getCustomerTotalQuota(String customerId);
    
    /**
     * 分配新额度
     */
    QuotaResponseDTO allocateQuota(QuotaRequestDTO request);
    
    /**
     * 回收额度
     */
    QuotaResponseDTO recoverQuota(QuotaRequestDTO request);
    
    /**
     * 启用额度
     */
    QuotaResponseDTO enableQuota(QuotaRequestDTO request);
    
    /**
     * 停用额度
     */
    QuotaResponseDTO disableQuota(QuotaRequestDTO request);
    
    /**
     * 获取集团额度信息
     */
    QuotaResponseDTO getGroupQuota(String groupId);
    
    /**
     * 获取集团下所有成员额度汇总
     */
    QuotaResponseDTO getGroupMembersQuota(String groupId);
    
    /**
     * 额度层级分配（集团额度向下分解）
     */
    QuotaResponseDTO distributeGroupQuota(QuotaRequestDTO request);
}