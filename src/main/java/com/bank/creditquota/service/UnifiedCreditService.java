package com.bank.creditquota.service;

import com.bank.creditquota.dto.QuotaRequestDTO;
import com.bank.creditquota.dto.QuotaResponseDTO;
import com.bank.creditquota.entity.*;
import java.math.BigDecimal;
import java.util.List;

public interface UnifiedCreditService {
    // 客户信息管理
    /**
     * 创建客户
     */
    CustomerInfo createCustomer(CustomerInfo customerInfo);
    
    /**
     * 更新客户
     */
    CustomerInfo updateCustomer(CustomerInfo customerInfo);
    
    /**
     * 根据ID获取客户
     */
    CustomerInfo getCustomerById(String customerId);
    
    /**
     * 获取所有客户（分页）
     */
    List<CustomerInfo> getAllCustomers(int pageNum, int pageSize);

    // 集团客户关系管理
    /**
     * 创建集团关系
     */
    GroupRelationship createGroupRelationship(GroupRelationship groupRelationship);
    
    /**
     * 更新集团关系
     */
    GroupRelationship updateGroupRelationship(GroupRelationship groupRelationship);
    
    /**
     * 根据父客户ID获取集团关系
     */
    List<GroupRelationship> getGroupRelationshipsByParent(String parentCustomerId);
    
    /**
     * 根据子客户ID获取集团关系
     */
    List<GroupRelationship> getGroupRelationshipsByChild(String childCustomerId);

    // 客户关联方管理
    /**
     * 创建客户关联方
     */
    CustomerAffiliate createCustomerAffiliate(CustomerAffiliate customerAffiliate);
    
    /**
     * 更新客户关联方
     */
    CustomerAffiliate updateCustomerAffiliate(CustomerAffiliate customerAffiliate);
    
    /**
     * 根据客户ID获取关联方
     */
    List<CustomerAffiliate> getCustomerAffiliates(String customerId);

    // 授信申请管理
    /**
     * 创建授信申请
     */
    CreditApplication createCreditApplication(CreditApplication creditApplication);
    
    /**
     * 更新授信申请
     */
    CreditApplication updateCreditApplication(CreditApplication creditApplication);
    
    /**
     * 根据ID获取授信申请
     */
    CreditApplication getCreditApplicationById(String applicationId);
    
    /**
     * 根据客户ID获取授信申请列表
     */
    List<CreditApplication> getCreditApplicationsByCustomer(String customerId);

    // 用信申请管理
    /**
     * 创建用信申请
     */
    UsageApplication createUsageApplication(UsageApplication usageApplication);
    
    /**
     * 更新用信申请
     */
    UsageApplication updateUsageApplication(UsageApplication usageApplication);
    
    /**
     * 根据ID获取用信申请
     */
    UsageApplication getUsageApplicationById(String usageId);
    
    /**
     * 根据客户ID获取用信申请列表
     */
    List<UsageApplication> getUsageApplicationsByCustomer(String customerId);

    // 额度管理
    /**
     * 创建授信额度
     */
    CreditQuota createCreditQuota(CreditQuota creditQuota);
    
    /**
     * 更新授信额度
     */
    CreditQuota updateCreditQuota(CreditQuota creditQuota);
    
    /**
     * 根据ID获取授信额度
     */
    CreditQuota getCreditQuotaById(Long quotaId);
    
    /**
     * 根据客户和额度类型获取额度
     */
    CreditQuota getCreditQuotaByCustomerAndType(String customerId, String quotaType);
    
    /**
     * 根据客户ID获取额度列表
     */
    List<CreditQuota> getCreditQuotasByCustomer(String customerId);

    // 额度使用明细管理
    /**
     * 创建额度使用明细
     */
    QuotaUsageDetail createQuotaUsageDetail(QuotaUsageDetail quotaUsageDetail);
    
    /**
     * 根据额度ID获取使用明细
     */
    List<QuotaUsageDetail> getQuotaUsageDetailsByQuotaId(Long quotaId);

    // 统一额度管控
    /**
     * 检查额度
     */
    boolean checkQuota(QuotaRequestDTO request);
    
    /**
     * 占用额度
     */
    QuotaResponseDTO occupyQuota(QuotaRequestDTO request);
    
    /**
     * 释放额度
     */
    QuotaResponseDTO releaseQuota(QuotaRequestDTO request);
    
    /**
     * 额度预占
     */
    QuotaResponseDTO preOccupyQuota(QuotaRequestDTO request);
    
    /**
     * 取消预占额度
     */
    QuotaResponseDTO cancelPreOccupiedQuota(QuotaRequestDTO request);
    
    /**
     * 冻结额度
     */
    QuotaResponseDTO freezeQuota(QuotaRequestDTO request);
    
    /**
     * 解冻额度
     */
    QuotaResponseDTO unfreezeQuota(QuotaRequestDTO request);
    
    /**
     * 启用额度
     */
    QuotaResponseDTO enableQuota(QuotaRequestDTO request);
    
    /**
     * 停用额度
     */
    QuotaResponseDTO disableQuota(QuotaRequestDTO request);
    
    /**
     * 调整额度
     */
    QuotaResponseDTO adjustQuota(QuotaRequestDTO request);
    
    /**
     * 分配集团额度
     */
    QuotaResponseDTO distributeGroupQuota(QuotaRequestDTO request);
    
    /**
     * 获取客户总额度信息
     */
    QuotaResponseDTO getTotalQuotaInfo(String customerId);
    
    /**
     * 获取集团总额度信息
     */
    QuotaResponseDTO getGroupTotalQuotaInfo(String groupId);
    
    /**
     * 获取集团成员额度
     */
    QuotaResponseDTO getGroupMemberQuotas(String groupId);

    // 风险监控
    /**
     * 创建风险监控指标
     */
    RiskMonitoringIndex createRiskMonitoringIndex(RiskMonitoringIndex riskMonitoringIndex);
    
    /**
     * 更新风险监控指标
     */
    RiskMonitoringIndex updateRiskMonitoringIndex(RiskMonitoringIndex riskMonitoringIndex);
    
    /**
     * 根据客户获取风险监控指标
     */
    List<RiskMonitoringIndex> getRiskMonitoringIndexesByCustomer(String customerId);
    
    /**
     * 检查客户风险指标
     */
    List<RiskMonitoringIndex> checkRiskIndexesByCustomer(String customerId);

    // 风险预警
    /**
     * 创建风险预警
     */
    RiskWarning createRiskWarning(RiskWarning riskWarning);
    
    /**
     * 更新风险预警
     */
    RiskWarning updateRiskWarning(RiskWarning riskWarning);
    
    /**
     * 根据客户获取风险预警
     */
    List<RiskWarning> getRiskWarningsByCustomer(String customerId);
    
    /**
     * 处理风险预警
     */
    RiskWarning handleRiskWarning(Long warningId, RiskWarning riskWarning);

    // 审批流程
    /**
     * 创建审批流程
     */
    ApprovalProcess createApprovalProcess(ApprovalProcess approvalProcess);
    
    /**
     * 更新审批流程
     */
    ApprovalProcess updateApprovalProcess(ApprovalProcess approvalProcess);
    
    /**
     * 根据ID获取审批流程
     */
    ApprovalProcess getApprovalProcessById(String processId);
    
    /**
     * 完成审批节点
     */
    ApprovalNode completeApprovalNode(Long nodeId, ApprovalNode approvalNode);
    
    /**
     * 根据流程ID获取审批节点
     */
    List<ApprovalNode> getApprovalNodesByProcess(String processId);
    
    /**
     * 记录额度使用明细
     */
    QuotaUsageDetail recordQuotaUsageDetail(QuotaUsageDetail quotaUsageDetail);
}