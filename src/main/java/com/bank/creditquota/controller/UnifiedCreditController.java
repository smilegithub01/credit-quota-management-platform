package com.bank.creditquota.controller;

import com.bank.creditquota.dto.QuotaRequestDTO;
import com.bank.creditquota.dto.QuotaResponseDTO;
import com.bank.creditquota.entity.*;
import com.bank.creditquota.service.UnifiedCreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unified-credit")
public class UnifiedCreditController {

    @Autowired
    private UnifiedCreditService unifiedCreditService;

    // 客户管理相关接口
    @PostMapping("/customer")
    public ResponseEntity<Boolean> addCustomerInfo(@RequestBody CustomerInfo customerInfo) {
        boolean result = unifiedCreditService.addCustomerInfo(customerInfo);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/customer")
    public ResponseEntity<Boolean> updateCustomerInfo(@RequestBody CustomerInfo customerInfo) {
        boolean result = unifiedCreditService.updateCustomerInfo(customerInfo);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CustomerInfo> getCustomerInfo(@PathVariable String customerId) {
        CustomerInfo customerInfo = unifiedCreditService.getCustomerInfo(customerId);
        return ResponseEntity.ok(customerInfo);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerInfo>> getCustomerList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<CustomerInfo> customerList = unifiedCreditService.getCustomerList(pageNum, pageSize);
        return ResponseEntity.ok(customerList);
    }

    // 集团关系管理相关接口
    @PostMapping("/group-relationship")
    public ResponseEntity<Boolean> addGroupRelationship(@RequestBody GroupRelationship groupRelationship) {
        boolean result = unifiedCreditService.addGroupRelationship(groupRelationship);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/group-relationship")
    public ResponseEntity<Boolean> updateGroupRelationship(@RequestBody GroupRelationship groupRelationship) {
        boolean result = unifiedCreditService.updateGroupRelationship(groupRelationship);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/group-relationship/parent/{parentCustomerId}")
    public ResponseEntity<List<GroupRelationship>> getGroupRelationshipByParent(@PathVariable String parentCustomerId) {
        List<GroupRelationship> relationships = unifiedCreditService.getGroupRelationshipByParent(parentCustomerId);
        return ResponseEntity.ok(relationships);
    }

    @GetMapping("/group-relationship/child/{childCustomerId}")
    public ResponseEntity<List<GroupRelationship>> getGroupRelationshipByChild(@PathVariable String childCustomerId) {
        List<GroupRelationship> relationships = unifiedCreditService.getGroupRelationshipByChild(childCustomerId);
        return ResponseEntity.ok(relationships);
    }

    // 客户关联方管理相关接口
    @PostMapping("/customer-affiliate")
    public ResponseEntity<Boolean> addCustomerAffiliate(@RequestBody CustomerAffiliate customerAffiliate) {
        boolean result = unifiedCreditService.addCustomerAffiliate(customerAffiliate);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/customer-affiliate")
    public ResponseEntity<Boolean> updateCustomerAffiliate(@RequestBody CustomerAffiliate customerAffiliate) {
        boolean result = unifiedCreditService.updateCustomerAffiliate(customerAffiliate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/customer-affiliate/customer/{customerId}")
    public ResponseEntity<List<CustomerAffiliate>> getCustomerAffiliatesByCustomerId(@PathVariable String customerId) {
        List<CustomerAffiliate> affiliates = unifiedCreditService.getCustomerAffiliatesByCustomerId(customerId);
        return ResponseEntity.ok(affiliates);
    }

    // 授信申请相关接口
    @PostMapping("/credit-application")
    public ResponseEntity<String> submitCreditApplication(@RequestBody CreditApplication creditApplication) {
        String applicationId = unifiedCreditService.submitCreditApplication(creditApplication);
        return ResponseEntity.ok(applicationId);
    }

    @PutMapping("/credit-application")
    public ResponseEntity<Boolean> updateCreditApplication(@RequestBody CreditApplication creditApplication) {
        boolean result = unifiedCreditService.updateCreditApplication(creditApplication);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/credit-application/{applicationId}")
    public ResponseEntity<CreditApplication> getCreditApplication(@PathVariable String applicationId) {
        CreditApplication application = unifiedCreditService.getCreditApplication(applicationId);
        return ResponseEntity.ok(application);
    }

    @GetMapping("/credit-application/customer/{customerId}")
    public ResponseEntity<List<CreditApplication>> getCreditApplicationsByCustomer(@PathVariable String customerId) {
        List<CreditApplication> applications = unifiedCreditService.getCreditApplicationsByCustomer(customerId);
        return ResponseEntity.ok(applications);
    }

    // 用信申请相关接口
    @PostMapping("/usage-application")
    public ResponseEntity<String> submitUsageApplication(@RequestBody UsageApplication usageApplication) {
        String usageId = unifiedCreditService.submitUsageApplication(usageApplication);
        return ResponseEntity.ok(usageId);
    }

    @PutMapping("/usage-application")
    public ResponseEntity<Boolean> updateUsageApplication(@RequestBody UsageApplication usageApplication) {
        boolean result = unifiedCreditService.updateUsageApplication(usageApplication);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/usage-application/{usageId}")
    public ResponseEntity<UsageApplication> getUsageApplication(@PathVariable String usageId) {
        UsageApplication usageApplication = unifiedCreditService.getUsageApplication(usageId);
        return ResponseEntity.ok(usageApplication);
    }

    @GetMapping("/usage-application/customer/{customerId}")
    public ResponseEntity<List<UsageApplication>> getUsageApplicationsByCustomer(@PathVariable String customerId) {
        List<UsageApplication> applications = unifiedCreditService.getUsageApplicationsByCustomer(customerId);
        return ResponseEntity.ok(applications);
    }

    // 额度管理相关接口
    @PostMapping("/credit-quota")
    public ResponseEntity<Boolean> approveCreditQuota(@RequestBody CreditQuota creditQuota) {
        boolean result = unifiedCreditService.approveCreditQuota(creditQuota);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/credit-quota")
    public ResponseEntity<Boolean> updateCreditQuota(@RequestBody CreditQuota creditQuota) {
        boolean result = unifiedCreditService.updateCreditQuota(creditQuota);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/credit-quota/{quotaId}")
    public ResponseEntity<CreditQuota> getCreditQuota(@PathVariable Long quotaId) {
        CreditQuota creditQuota = unifiedCreditService.getCreditQuota(quotaId);
        return ResponseEntity.ok(creditQuota);
    }

    @GetMapping("/credit-quota/customer/{customerId}/type/{quotaType}")
    public ResponseEntity<CreditQuota> getCreditQuotaByCustomerAndType(
            @PathVariable String customerId, 
            @PathVariable String quotaType) {
        CreditQuota creditQuota = unifiedCreditService.getCreditQuotaByCustomerAndType(customerId, quotaType);
        return ResponseEntity.ok(creditQuota);
    }

    @GetMapping("/credit-quota/customer/{customerId}")
    public ResponseEntity<List<CreditQuota>> getCreditQuotasByCustomer(@PathVariable String customerId) {
        List<CreditQuota> creditQuotas = unifiedCreditService.getCreditQuotasByCustomer(customerId);
        return ResponseEntity.ok(creditQuotas);
    }

    // 额度使用明细相关接口
    @PostMapping("/quota-usage-detail")
    public ResponseEntity<Boolean> recordQuotaUsageDetail(@RequestBody QuotaUsageDetail quotaUsageDetail) {
        boolean result = unifiedCreditService.recordQuotaUsageDetail(quotaUsageDetail);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/quota-usage-detail/quota/{quotaId}")
    public ResponseEntity<List<QuotaUsageDetail>> getQuotaUsageDetailsByQuotaId(@PathVariable Long quotaId) {
        List<QuotaUsageDetail> details = unifiedCreditService.getQuotaUsageDetailsByQuotaId(quotaId);
        return ResponseEntity.ok(details);
    }

    // 统一额度管控相关接口
    @PostMapping("/quota/check")
    public ResponseEntity<Boolean> checkQuota(@RequestParam String customerId, 
                                             @RequestParam String quotaType, 
                                             @RequestParam java.math.BigDecimal amount) {
        boolean available = unifiedCreditService.checkQuotaAvailability(customerId, quotaType, amount);
        return ResponseEntity.ok(available);
    }

    @PostMapping("/quota/occupy")
    public ResponseEntity<QuotaResponseDTO> occupyQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = unifiedCreditService.occupyQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quota/release")
    public ResponseEntity<QuotaResponseDTO> releaseQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = unifiedCreditService.releaseQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quota/pre-occupy")
    public ResponseEntity<QuotaResponseDTO> preOccupyQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = unifiedCreditService.preOccupyQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quota/cancel-pre-occupy")
    public ResponseEntity<QuotaResponseDTO> cancelPreOccupiedQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = unifiedCreditService.cancelPreOccupiedQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quota/freeze")
    public ResponseEntity<QuotaResponseDTO> freezeQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = unifiedCreditService.freezeQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quota/unfreeze")
    public ResponseEntity<QuotaResponseDTO> unfreezeQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = unifiedCreditService.unfreezeQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quota/enable")
    public ResponseEntity<QuotaResponseDTO> enableQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = unifiedCreditService.enableQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quota/disable")
    public ResponseEntity<QuotaResponseDTO> disableQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = unifiedCreditService.disableQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quota/adjust")
    public ResponseEntity<QuotaResponseDTO> adjustQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = unifiedCreditService.adjustQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quota/distribute-group")
    public ResponseEntity<QuotaResponseDTO> distributeGroupQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = unifiedCreditService.distributeGroupQuota(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/quota/customer/{customerId}/total")
    public ResponseEntity<QuotaResponseDTO> getCustomerTotalQuota(@PathVariable String customerId) {
        QuotaResponseDTO response = unifiedCreditService.getCustomerTotalQuota(customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/quota/group/{groupId}/total")
    public ResponseEntity<QuotaResponseDTO> getGroupTotalQuota(@PathVariable String groupId) {
        QuotaResponseDTO response = unifiedCreditService.getGroupTotalQuota(groupId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/quota/group/{groupId}/members")
    public ResponseEntity<QuotaResponseDTO> getGroupMembersQuota(@PathVariable String groupId) {
        QuotaResponseDTO response = unifiedCreditService.getGroupMembersQuota(groupId);
        return ResponseEntity.ok(response);
    }

    // 风险监控相关接口
    @PostMapping("/risk-monitoring-index")
    public ResponseEntity<Boolean> addRiskMonitoringIndex(@RequestBody RiskMonitoringIndex riskMonitoringIndex) {
        boolean result = unifiedCreditService.addRiskMonitoringIndex(riskMonitoringIndex);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/risk-monitoring-index")
    public ResponseEntity<Boolean> updateRiskMonitoringIndex(@RequestBody RiskMonitoringIndex riskMonitoringIndex) {
        boolean result = unifiedCreditService.updateRiskMonitoringIndex(riskMonitoringIndex);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/risk-monitoring-index/customer/{customerId}")
    public ResponseEntity<List<RiskMonitoringIndex>> getRiskMonitoringIndicesByCustomerId(@PathVariable String customerId) {
        List<RiskMonitoringIndex> indices = unifiedCreditService.getRiskMonitoringIndicesByCustomerId(customerId);
        return ResponseEntity.ok(indices);
    }

    @GetMapping("/risk-monitoring-index/customer/{customerId}/check")
    public ResponseEntity<Boolean> checkCustomerRiskIndices(@PathVariable String customerId) {
        boolean result = unifiedCreditService.checkCustomerRiskIndices(customerId);
        return ResponseEntity.ok(result);
    }

    // 风险预警相关接口
    @PostMapping("/risk-warning")
    public ResponseEntity<Boolean> addRiskWarning(@RequestBody RiskWarning riskWarning) {
        boolean result = unifiedCreditService.addRiskWarning(riskWarning);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/risk-warning")
    public ResponseEntity<Boolean> updateRiskWarning(@RequestBody RiskWarning riskWarning) {
        boolean result = unifiedCreditService.updateRiskWarning(rWarning);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/risk-warning/customer/{customerId}")
    public ResponseEntity<List<RiskWarning>> getRiskWarningsByCustomerId(@PathVariable String customerId) {
        List<RiskWarning> warnings = unifiedCreditService.getRiskWarningsByCustomerId(customerId);
        return ResponseEntity.ok(warnings);
    }

    @PutMapping("/risk-warning/{warningId}/handle")
    public ResponseEntity<Boolean> handleRiskWarning(
            @PathVariable Long warningId,
            @RequestParam String handler,
            @RequestParam String handleResult) {
        boolean result = unifiedCreditService.handleRiskWarning(warningId, handler, handleResult);
        return ResponseEntity.ok(result);
    }

    // 审批流程相关接口
    @PostMapping("/approval-process")
    public ResponseEntity<String> startApprovalProcess(@RequestBody ApprovalProcess approvalProcess) {
        String processId = unifiedCreditService.startApprovalProcess(approvalProcess);
        return ResponseEntity.ok(processId);
    }

    @PutMapping("/approval-process")
    public ResponseEntity<Boolean> updateApprovalProcess(@RequestBody ApprovalProcess approvalProcess) {
        boolean result = unifiedCreditService.updateApprovalProcess(approvalProcess);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/approval-process/{processId}")
    public ResponseEntity<ApprovalProcess> getApprovalProcess(@PathVariable String processId) {
        ApprovalProcess process = unifiedCreditService.getApprovalProcess(processId);
        return ResponseEntity.ok(process);
    }

    @PutMapping("/approval-node/{nodeId}/complete")
    public ResponseEntity<Boolean> completeApprovalNode(
            @PathVariable Long nodeId,
            @RequestBody ApprovalNode approvalNode) {
        approvalNode.setNodeId(nodeId);
        boolean result = unifiedCreditService.completeApprovalNode(approvalNode);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/approval-node/process/{processId}")
    public ResponseEntity<List<ApprovalNode>> getApprovalNodesByProcessId(@PathVariable String processId) {
        List<ApprovalNode> nodes = unifiedCreditService.getApprovalNodesByProcessId(processId);
        return ResponseEntity.ok(nodes);
    }
}