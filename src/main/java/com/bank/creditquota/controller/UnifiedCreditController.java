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
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class UnifiedCreditController {

    @Autowired
    private UnifiedCreditService unifiedCreditService;

    // 客户管理接口
    @PostMapping("/customers")
    public ResponseEntity<CustomerInfo> createCustomer(@RequestBody CustomerInfo customerInfo) {
        customerInfo.setCreatedTime(java.time.LocalDateTime.now());
        boolean result = unifiedCreditService.addCustomerInfo(customerInfo);
        if (result) {
            return ResponseEntity.ok(customerInfo);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/customers/{customerId}")
    public ResponseEntity<CustomerInfo> updateCustomer(
            @PathVariable String customerId,
            @RequestBody CustomerInfo customerInfo) {
        customerInfo.setCustomerId(customerId);
        customerInfo.setUpdatedTime(java.time.LocalDateTime.now());
        boolean result = unifiedCreditService.updateCustomerInfo(customerInfo);
        if (result) {
            return ResponseEntity.ok(customerInfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerInfo> getCustomer(@PathVariable String customerId) {
        CustomerInfo customerInfo = unifiedCreditService.getCustomerInfo(customerId);
        if (customerInfo != null) {
            return ResponseEntity.ok(customerInfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerInfo>> getCustomers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<CustomerInfo> customerList = unifiedCreditService.getCustomerList(pageNum, pageSize);
        return ResponseEntity.ok(customerList);
    }

    // 集团关系管理接口
    @PostMapping("/customers/{customerId}/groups")
    public ResponseEntity<GroupRelationship> createGroupRelationship(
            @PathVariable String customerId,
            @RequestBody GroupRelationship groupRelationship) {
        groupRelationship.setParentCustomerId(customerId);
        groupRelationship.setCreatedTime(java.time.LocalDateTime.now());
        boolean result = unifiedCreditService.addGroupRelationship(groupRelationship);
        if (result) {
            return ResponseEntity.ok(groupRelationship);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/group-relationships/{relationshipId}")
    public ResponseEntity<GroupRelationship> updateGroupRelationship(
            @PathVariable Long relationshipId,
            @RequestBody GroupRelationship groupRelationship) {
        groupRelationship.setUpdatedTime(java.time.LocalDateTime.now());
        boolean result = unifiedCreditService.updateGroupRelationship(groupRelationship);
        if (result) {
            return ResponseEntity.ok(groupRelationship);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{customerId}/children")
    public ResponseEntity<List<GroupRelationship>> getChildRelationships(@PathVariable String customerId) {
        List<GroupRelationship> relationships = unifiedCreditService.getGroupRelationshipByParent(customerId);
        return ResponseEntity.ok(relationships);
    }

    @GetMapping("/customers/{customerId}/parents")
    public ResponseEntity<List<GroupRelationship>> getParentRelationships(@PathVariable String customerId) {
        List<GroupRelationship> relationships = unifiedCreditService.getGroupRelationshipByChild(customerId);
        return ResponseEntity.ok(relationships);
    }

    // 客户关联方管理接口
    @PostMapping("/customers/{customerId}/affiliates")
    public ResponseEntity<CustomerAffiliate> createCustomerAffiliate(
            @PathVariable String customerId,
            @RequestBody CustomerAffiliate customerAffiliate) {
        customerAffiliate.setCustomerId(customerId);
        customerAffiliate.setCreatedTime(java.time.LocalDateTime.now());
        boolean result = unifiedCreditService.addCustomerAffiliate(customerAffiliate);
        if (result) {
            return ResponseEntity.ok(customerAffiliate);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/customer-affiliates/{affiliateId}")
    public ResponseEntity<CustomerAffiliate> updateCustomerAffiliate(
            @PathVariable Long affiliateId,
            @RequestBody CustomerAffiliate customerAffiliate) {
        customerAffiliate.setUpdatedTime(java.time.LocalDateTime.now());
        boolean result = unifiedCreditService.updateCustomerAffiliate(customerAffiliate);
        if (result) {
            return ResponseEntity.ok(customerAffiliate);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{customerId}/affiliates")
    public ResponseEntity<List<CustomerAffiliate>> getCustomerAffiliates(@PathVariable String customerId) {
        List<CustomerAffiliate> affiliates = unifiedCreditService.getCustomerAffiliatesByCustomerId(customerId);
        return ResponseEntity.ok(affiliates);
    }

    // 授信申请管理接口
    @PostMapping("/customers/{customerId}/applications")
    public ResponseEntity<String> createCreditApplication(
            @PathVariable String customerId,
            @RequestBody CreditApplication creditApplication) {
        creditApplication.setCustomerId(customerId);
        String applicationId = unifiedCreditService.submitCreditApplication(creditApplication);
        if (applicationId != null && !applicationId.isEmpty()) {
            return ResponseEntity.ok(applicationId);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/credit-applications/{applicationId}")
    public ResponseEntity<CreditApplication> updateCreditApplication(
            @PathVariable String applicationId,
            @RequestBody CreditApplication creditApplication) {
        creditApplication.setApplicationId(applicationId);
        boolean result = unifiedCreditService.updateCreditApplication(creditApplication);
        if (result) {
            return ResponseEntity.ok(creditApplication);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/credit-applications/{applicationId}")
    public ResponseEntity<CreditApplication> getCreditApplication(@PathVariable String applicationId) {
        CreditApplication application = unifiedCreditService.getCreditApplication(applicationId);
        if (application != null) {
            return ResponseEntity.ok(application);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{customerId}/applications")
    public ResponseEntity<List<CreditApplication>> getCreditApplicationsByCustomer(@PathVariable String customerId) {
        List<CreditApplication> applications = unifiedCreditService.getCreditApplicationsByCustomer(customerId);
        return ResponseEntity.ok(applications);
    }

    // 用信申请管理接口
    @PostMapping("/usage-applications")
    public ResponseEntity<String> createUsageApplication(@RequestBody UsageApplication usageApplication) {
        String usageId = unifiedCreditService.submitUsageApplication(usageApplication);
        if (usageId != null && !usageId.isEmpty()) {
            return ResponseEntity.ok(usageId);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/usage-applications/{usageId}")
    public ResponseEntity<UsageApplication> updateUsageApplication(
            @PathVariable String usageId,
            @RequestBody UsageApplication usageApplication) {
        usageApplication.setUsageId(usageId);
        boolean result = unifiedCreditService.updateUsageApplication(usageApplication);
        if (result) {
            return ResponseEntity.ok(usageApplication);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usage-applications/{usageId}")
    public ResponseEntity<UsageApplication> getUsageApplication(@PathVariable String usageId) {
        UsageApplication usageApplication = unifiedCreditService.getUsageApplication(usageId);
        if (usageApplication != null) {
            return ResponseEntity.ok(usageApplication);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{customerId}/usage-applications")
    public ResponseEntity<List<UsageApplication>> getUsageApplicationsByCustomer(@PathVariable String customerId) {
        List<UsageApplication> applications = unifiedCreditService.getUsageApplicationsByCustomer(customerId);
        return ResponseEntity.ok(applications);
    }

    // 额度管理接口
    @PostMapping("/customers/{customerId}/quotas")
    public ResponseEntity<CreditQuota> createCreditQuota(
            @PathVariable String customerId,
            @RequestBody CreditQuota creditQuota) {
        creditQuota.setCustomerId(customerId);
        boolean result = unifiedCreditService.approveCreditQuota(creditQuota);
        if (result) {
            return ResponseEntity.ok(creditQuota);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/quotas/{quotaId}")
    public ResponseEntity<CreditQuota> updateCreditQuota(
            @PathVariable Long quotaId,
            @RequestBody CreditQuota creditQuota) {
        creditQuota.setQuotaId(quotaId);
        boolean result = unifiedCreditService.updateCreditQuota(creditQuota);
        if (result) {
            return ResponseEntity.ok(creditQuota);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/quotas/{quotaId}")
    public ResponseEntity<CreditQuota> getCreditQuota(@PathVariable Long quotaId) {
        CreditQuota creditQuota = unifiedCreditService.getCreditQuota(quotaId);
        if (creditQuota != null) {
            return ResponseEntity.ok(creditQuota);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{customerId}/quotas/{quotaType}")
    public ResponseEntity<CreditQuota> getCreditQuotaByCustomerAndType(
            @PathVariable String customerId,
            @PathVariable String quotaType) {
        CreditQuota creditQuota = unifiedCreditService.getCreditQuotaByCustomerAndType(customerId, quotaType);
        if (creditQuota != null) {
            return ResponseEntity.ok(creditQuota);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{customerId}/quotas")
    public ResponseEntity<List<CreditQuota>> getCreditQuotasByCustomer(@PathVariable String customerId) {
        List<CreditQuota> creditQuotas = unifiedCreditService.getCreditQuotasByCustomer(customerId);
        return ResponseEntity.ok(creditQuotas);
    }

    // 额度使用明细接口
    @PostMapping("/quotas/{quotaId}/usage-details")
    public ResponseEntity<QuotaUsageDetail> createQuotaUsageDetail(
            @PathVariable Long quotaId,
            @RequestBody QuotaUsageDetail quotaUsageDetail) {
        quotaUsageDetail.setQuotaId(quotaId);
        boolean result = unifiedCreditService.recordQuotaUsageDetail(quotaUsageDetail);
        if (result) {
            return ResponseEntity.ok(quotaUsageDetail);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/quotas/{quotaId}/usage-details")
    public ResponseEntity<List<QuotaUsageDetail>> getQuotaUsageDetailsByQuotaId(@PathVariable Long quotaId) {
        List<QuotaUsageDetail> details = unifiedCreditService.getQuotaUsageDetailsByQuotaId(quotaId);
        return ResponseEntity.ok(details);
    }

    // 额度管控接口
    @PostMapping("/quotas/{quotaId}/occupy")
    public ResponseEntity<QuotaResponseDTO> occupyQuota(
            @PathVariable Long quotaId,
            @RequestBody QuotaRequestDTO request) {
        request.setQuotaId(quotaId);
        QuotaResponseDTO response = unifiedCreditService.occupyQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quotas/{quotaId}/release")
    public ResponseEntity<QuotaResponseDTO> releaseQuota(
            @PathVariable Long quotaId,
            @RequestBody QuotaRequestDTO request) {
        request.setQuotaId(quotaId);
        QuotaResponseDTO response = unifiedCreditService.releaseQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quotas/{quotaId}/pre-occupy")
    public ResponseEntity<QuotaResponseDTO> preOccupyQuota(
            @PathVariable Long quotaId,
            @RequestBody QuotaRequestDTO request) {
        request.setQuotaId(quotaId);
        QuotaResponseDTO response = unifiedCreditService.preOccupyQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quotas/{quotaId}/cancel-pre-occupy")
    public ResponseEntity<QuotaResponseDTO> cancelPreOccupiedQuota(
            @PathVariable Long quotaId,
            @RequestBody QuotaRequestDTO request) {
        request.setQuotaId(quotaId);
        QuotaResponseDTO response = unifiedCreditService.cancelPreOccupiedQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quotas/{quotaId}/freeze")
    public ResponseEntity<QuotaResponseDTO> freezeQuota(
            @PathVariable Long quotaId,
            @RequestBody QuotaRequestDTO request) {
        request.setQuotaId(quotaId);
        QuotaResponseDTO response = unifiedCreditService.freezeQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quotas/{quotaId}/unfreeze")
    public ResponseEntity<QuotaResponseDTO> unfreezeQuota(
            @PathVariable Long quotaId,
            @RequestBody QuotaRequestDTO request) {
        request.setQuotaId(quotaId);
        QuotaResponseDTO response = unifiedCreditService.unfreezeQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quotas/{quotaId}/enable")
    public ResponseEntity<QuotaResponseDTO> enableQuota(
            @PathVariable Long quotaId,
            @RequestBody QuotaRequestDTO request) {
        request.setQuotaId(quotaId);
        QuotaResponseDTO response = unifiedCreditService.enableQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quotas/{quotaId}/disable")
    public ResponseEntity<QuotaResponseDTO> disableQuota(
            @PathVariable Long quotaId,
            @RequestBody QuotaRequestDTO request) {
        request.setQuotaId(quotaId);
        QuotaResponseDTO response = unifiedCreditService.disableQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quotas/{quotaId}/adjust")
    public ResponseEntity<QuotaResponseDTO> adjustQuota(
            @PathVariable Long quotaId,
            @RequestBody QuotaRequestDTO request) {
        request.setQuotaId(quotaId);
        QuotaResponseDTO response = unifiedCreditService.adjustQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quotas/{quotaId}/distribute")
    public ResponseEntity<QuotaResponseDTO> distributeGroupQuota(
            @PathVariable Long quotaId,
            @RequestBody QuotaRequestDTO request) {
        request.setQuotaId(quotaId);
        QuotaResponseDTO response = unifiedCreditService.distributeGroupQuota(request);
        return ResponseEntity.ok(response);
    }

    // 额度查询接口
    @GetMapping("/customers/{customerId}/quota-summary")
    public ResponseEntity<QuotaResponseDTO> getCustomerTotalQuota(@PathVariable String customerId) {
        QuotaResponseDTO response = unifiedCreditService.getCustomerTotalQuota(customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/groups/{groupId}/quota-summary")
    public ResponseEntity<QuotaResponseDTO> getGroupTotalQuota(@PathVariable String groupId) {
        QuotaResponseDTO response = unifiedCreditService.getGroupTotalQuota(groupId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/groups/{groupId}/member-quotas")
    public ResponseEntity<QuotaResponseDTO> getGroupMembersQuota(@PathVariable String groupId) {
        QuotaResponseDTO response = unifiedCreditService.getGroupMembersQuota(groupId);
        return ResponseEntity.ok(response);
    }

    // 风险监控接口
    @PostMapping("/customers/{customerId}/risk-indexes")
    public ResponseEntity<RiskMonitoringIndex> createRiskMonitoringIndex(
            @PathVariable String customerId,
            @RequestBody RiskMonitoringIndex riskMonitoringIndex) {
        riskMonitoringIndex.setCustomerId(customerId);
        boolean result = unifiedCreditService.addRiskMonitoringIndex(riskMonitoringIndex);
        if (result) {
            return ResponseEntity.ok(riskMonitoringIndex);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/risk-indexes/{indexId}")
    public ResponseEntity<RiskMonitoringIndex> updateRiskMonitoringIndex(
            @PathVariable Long indexId,
            @RequestBody RiskMonitoringIndex riskMonitoringIndex) {
        boolean result = unifiedCreditService.updateRiskMonitoringIndex(riskMonitoringIndex);
        if (result) {
            return ResponseEntity.ok(riskMonitoringIndex);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{customerId}/risk-indexes")
    public ResponseEntity<List<RiskMonitoringIndex>> getRiskMonitoringIndicesByCustomerId(@PathVariable String customerId) {
        List<RiskMonitoringIndex> indices = unifiedCreditService.getRiskMonitoringIndicesByCustomerId(customerId);
        return ResponseEntity.ok(indices);
    }

    @GetMapping("/customers/{customerId}/risk-check")
    public ResponseEntity<Boolean> checkCustomerRiskIndices(@PathVariable String customerId) {
        boolean result = unifiedCreditService.checkCustomerRiskIndices(customerId);
        return ResponseEntity.ok(result);
    }

    // 风险预警接口
    @PostMapping("/customers/{customerId}/risk-warnings")
    public ResponseEntity<RiskWarning> createRiskWarning(
            @PathVariable String customerId,
            @RequestBody RiskWarning riskWarning) {
        riskWarning.setCustomerId(customerId);
        boolean result = unifiedCreditService.addRiskWarning(riskWarning);
        if (result) {
            return ResponseEntity.ok(riskWarning);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/risk-warnings/{warningId}")
    public ResponseEntity<RiskWarning> updateRiskWarning(
            @PathVariable Long warningId,
            @RequestBody RiskWarning riskWarning) {
        boolean result = unifiedCreditService.updateRiskWarning(riskWarning);
        if (result) {
            return ResponseEntity.ok(riskWarning);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{customerId}/risk-warnings")
    public ResponseEntity<List<RiskWarning>> getRiskWarningsByCustomerId(@PathVariable String customerId) {
        List<RiskWarning> warnings = unifiedCreditService.getRiskWarningsByCustomerId(customerId);
        return ResponseEntity.ok(warnings);
    }

    @PutMapping("/risk-warnings/{warningId}/handle")
    public ResponseEntity<Boolean> handleRiskWarning(
            @PathVariable Long warningId,
            @RequestParam String handler,
            @RequestParam String handleResult) {
        boolean result = unifiedCreditService.handleRiskWarning(warningId, handler, handleResult);
        return ResponseEntity.ok(result);
    }

    // 审批流程接口
    @PostMapping("/approval-processes")
    public ResponseEntity<String> createApprovalProcess(@RequestBody ApprovalProcess approvalProcess) {
        String processId = unifiedCreditService.startApprovalProcess(approvalProcess);
        if (processId != null && !processId.isEmpty()) {
            return ResponseEntity.ok(processId);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/approval-processes/{processId}")
    public ResponseEntity<ApprovalProcess> updateApprovalProcess(
            @PathVariable String processId,
            @RequestBody ApprovalProcess approvalProcess) {
        approvalProcess.setProcessId(processId);
        boolean result = unifiedCreditService.updateApprovalProcess(approvalProcess);
        if (result) {
            return ResponseEntity.ok(approvalProcess);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/approval-processes/{processId}")
    public ResponseEntity<ApprovalProcess> getApprovalProcess(@PathVariable String processId) {
        ApprovalProcess process = unifiedCreditService.getApprovalProcess(processId);
        if (process != null) {
            return ResponseEntity.ok(process);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/approval-nodes/{nodeId}/complete")
    public ResponseEntity<ApprovalNode> completeApprovalNode(
            @PathVariable Long nodeId,
            @RequestBody ApprovalNode approvalNode) {
        approvalNode.setNodeId(nodeId);
        boolean result = unifiedCreditService.completeApprovalNode(approvalNode);
        if (result) {
            return ResponseEntity.ok(approvalNode);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/approval-processes/{processId}/nodes")
    public ResponseEntity<List<ApprovalNode>> getApprovalNodesByProcessId(@PathVariable String processId) {
        List<ApprovalNode> nodes = unifiedCreditService.getApprovalNodesByProcessId(processId);
        return ResponseEntity.ok(nodes);
    }
}