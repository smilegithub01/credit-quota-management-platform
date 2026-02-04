package com.bank.creditquota.service;

import com.bank.creditquota.dto.QuotaRequestDTO;
import com.bank.creditquota.dto.QuotaResponseDTO;
import com.bank.creditquota.entity.*;
import java.math.BigDecimal;
import java.util.List;

public interface UnifiedCreditService {
    // 客户管理相关
    /**
     * 新增客户信息
     */
    boolean addCustomerInfo(CustomerInfo customerInfo);
    
    /**
     * 更新客户信息
     */
    boolean updateCustomerInfo(CustomerInfo customerInfo);
    
    /**
     * 查询客户信息
     */
    CustomerInfo getCustomerInfo(String customerId);
    
    /**
     * 查询客户列表
     */
    List<CustomerInfo> getCustomerList(int pageNum, int pageSize);
    
    // 集团关系管理相关
    /**
     * 新增集团关系
     */
    boolean addGroupRelationship(GroupRelationship groupRelationship);
    
    /**
     * 更新集团关系
     */
    boolean updateGroupRelationship(GroupRelationship groupRelationship);
    
    /**
     * 查询集团关系
     */
    List<GroupRelationship> getGroupRelationshipByParent(String parentCustomerId);
    
    /**
     * 查询集团关系
     */
    List<GroupRelationship> getGroupRelationshipByChild(String childCustomerId);
    
    // 客户关联方管理相关
    /**
     * 新增客户关联方
     */
    boolean addCustomerAffiliate(CustomerAffiliate customerAffiliate);
    
    /**
     * 更新客户关联方
     */
    boolean updateCustomerAffiliate(CustomerAffiliate customerAffiliate);
    
    /**
     * 查询客户关联方
     */
    List<CustomerAffiliate> getCustomerAffiliatesByCustomerId(String customerId);
    
    // 授信申请相关
    /**
     * 提交授信申请
     */
    String submitCreditApplication(CreditApplication creditApplication);
    
    /**
     * 更新授信申请
     */
    boolean updateCreditApplication(CreditApplication creditApplication);
    
    /**
     * 查询授信申请
     */
    CreditApplication getCreditApplication(String applicationId);
    
    /**
     * 查询客户授信申请列表
     */
    List<CreditApplication> getCreditApplicationsByCustomer(String customerId);
    
    // 用信申请相关
    /**
     * 提交用信申请
     */
    String submitUsageApplication(UsageApplication usageApplication);
    
    /**
     * 更新用信申请
     */
    boolean updateUsageApplication(UsageApplication usageApplication);
    
    /**
     * 查询用信申请
     */
    UsageApplication getUsageApplication(String usageId);
    
    /**
     * 查询客户用信申请列表
     */
    List<UsageApplication> getUsageApplicationsByCustomer(String customerId);
    
    // 额度管理相关
    /**
     * 核定授信额度
     */
    boolean approveCreditQuota(CreditQuota creditQuota);
    
    /**
     * 更新授信额度
     */
    boolean updateCreditQuota(CreditQuota creditQuota);
    
    /**
     * 查询授信额度
     */
    CreditQuota getCreditQuota(Long quotaId);
    
    /**
     * 根据客户和额度类型查询
     */
    CreditQuota getCreditQuotaByCustomerAndType(String customerId, String quotaType);
    
    /**
     * 查询客户所有额度
     */
    List<CreditQuota> getCreditQuotasByCustomer(String customerId);
    
    // 额度使用明细相关
    /**
     * 记录额度使用明细
     */
    boolean recordQuotaUsageDetail(QuotaUsageDetail quotaUsageDetail);
    
    /**
     * 查询额度使用明细
     */
    List<QuotaUsageDetail> getQuotaUsageDetailsByQuotaId(Long quotaId);
    
    // 统一额度管控相关
    /**
     * 检查额度是否充足
     */
    boolean checkQuotaAvailability(String customerId, String quotaType, BigDecimal amount);
    
    /**
     * 占用额度
     */
    QuotaResponseDTO occupyQuota(QuotaRequestDTO request);
    
    /**
     * 释放额度
     */
    QuotaResponseDTO releaseQuota(QuotaRequestDTO request);
    
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
     * 额度调整
     */
    QuotaResponseDTO adjustQuota(QuotaRequestDTO request);
    
    /**
     * 额度预占（预留）
     */
    QuotaResponseDTO preOccupyQuota(QuotaRequestDTO request);
    
    /**
     * 取消预占额度
     */
    QuotaResponseDTO cancelPreOccupiedQuota(QuotaRequestDTO request);
    
    /**
     * 集团额度分配
     */
    QuotaResponseDTO distributeGroupQuota(QuotaRequestDTO request);
    
    /**
     * 获取客户总额度信息
     */
    QuotaResponseDTO getCustomerTotalQuota(String customerId);
    
    /**
     * 获取集团总额度信息
     */
    QuotaResponseDTO getGroupTotalQuota(String groupId);
    
    /**
     * 获取集团成员额度汇总
     */
    QuotaResponseDTO getGroupMembersQuota(String groupId);
    
    // 风险监控相关
    /**
     * 新增风险监控指标
     */
    boolean addRiskMonitoringIndex(RiskMonitoringIndex riskMonitoringIndex);
    
    /**
     * 更新风险监控指标
     */
    boolean updateRiskMonitoringIndex(RiskMonitoringIndex riskMonitoringIndex);
    
    /**
     * 查询风险监控指标
     */
    List<RiskMonitoringIndex> getRiskMonitoringIndicesByCustomerId(String customerId);
    
    /**
     * 检查客户风险指标
     */
    boolean checkCustomerRiskIndices(String customerId);
    
    // 风险预警相关
    /**
     * 新增风险预警
     */
    boolean addRiskWarning(RiskWarning riskWarning);
    
    /**
     * 更新风险预警
     */
    boolean updateRiskWarning(RiskWarning riskWarning);
    
    /**
     * 查询风险预警
     */
    List<RiskWarning> getRiskWarningsByCustomerId(String customerId);
    
    /**
     * 处理风险预警
     */
    boolean handleRiskWarning(Long warningId, String handler, String handleResult);
    
    // 审批流程相关
    /**
     * 启动审批流程
     */
    String startApprovalProcess(ApprovalProcess approvalProcess);
    
    /**
     * 更新审批流程
     */
    boolean updateApprovalProcess(ApprovalProcess approvalProcess);
    
    /**
     * 查询审批流程
     */
    ApprovalProcess getApprovalProcess(String processId);
    
    /**
     * 完成审批节点
     */
    boolean completeApprovalNode(ApprovalNode approvalNode);
    
    /**
     * 查询审批节点
     */
    List<ApprovalNode> getApprovalNodesByProcessId(String processId);
}