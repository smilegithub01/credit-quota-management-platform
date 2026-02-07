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
        CustomerInfo result = unifiedCreditService.createCustomer(customerInfo);
        if (result != null) {
            return ResponseEntity.ok(result);
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
        CustomerInfo result = unifiedCreditService.updateCustomer(customerInfo);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerInfo> getCustomer(@PathVariable String customerId) {
        CustomerInfo customerInfo = unifiedCreditService.getCustomerById(customerId);
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
        List<CustomerInfo> customerList = unifiedCreditService.getAllCustomers(pageNum, pageSize);
        return ResponseEntity.ok(customerList);
    }

    // 集团关系管理接口
    @PostMapping("/customers/{customerId}/groups")
    public ResponseEntity<GroupRelationship> createGroupRelationship(
            @PathVariable String customerId,
            @RequestBody GroupRelationship groupRelationship) {
        groupRelationship.setParentCustomerId(customerId);
        groupRelationship.setCreatedTime(java.time.LocalDateTime.now());
        GroupRelationship result = unifiedCreditService.createGroupRelationship(groupRelationship);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/group-relationships/{relationshipId}")
    public ResponseEntity<GroupRelationship> updateGroupRelationship(
            @PathVariable Long relationshipId,
            @RequestBody GroupRelationship groupRelationship) {
        groupRelationship.setUpdatedTime(java.time.LocalDateTime.now());
        GroupRelationship result = unifiedCreditService.updateGroupRelationship(groupRelationship);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{customerId}/children")
    public ResponseEntity<List<GroupRelationship>> getChildRelationships(@PathVariable String customerId) {
        List<GroupRelationship> relationships = unifiedCreditService.getGroupRelationshipsByParent(customerId);
        return ResponseEntity.ok(relationships);
    }

    @GetMapping("/customers/{customerId}/parents")
    public ResponseEntity<List<GroupRelationship>> getParentRelationships(@PathVariable String customerId) {
        List<GroupRelationship> relationships = unifiedCreditService.getGroupRelationshipsByChild(customerId);
        return ResponseEntity.ok(relationships);
    }

    // 客户关联方管理接口
    @PostMapping("/customers/{customerId}/affiliates")
    public ResponseEntity<CustomerAffiliate> createCustomerAffiliate(
            @PathVariable String customerId,
            @RequestBody CustomerAffiliate customerAffiliate) {
        customerAffiliate.setCustomerId(customerId);
        customerAffiliate.setCreatedTime(java.time.LocalDateTime.now());
        CustomerAffiliate result = unifiedCreditService.createCustomerAffiliate(customerAffiliate);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/customer-affiliates/{affiliateId}")
    public ResponseEntity<CustomerAffiliate> updateCustomerAffiliate(
            @PathVariable Long affiliateId,
            @RequestBody CustomerAffiliate customerAffiliate) {
        customerAffiliate.setUpdatedTime(java.time.LocalDateTime.now());
        CustomerAffiliate result = unifiedCreditService.updateCustomerAffiliate(customerAffiliate);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{customerId}/affiliates")
    public ResponseEntity<List<CustomerAffiliate>> getCustomerAffiliates(@PathVariable String customerId) {
        List<CustomerAffiliate> affiliates = unifiedCreditService.getCustomerAffiliates(customerId);
        return ResponseEntity.ok(affiliates);
    }

    // 授信申请管理接口
    @PostMapping("/customers/{customerId}/applications")
    public ResponseEntity<CreditApplication> createCreditApplication(
            @PathVariable String customerId,
            @RequestBody CreditApplication creditApplication) {
        creditApplication.setCustomerId(customerId);
        CreditApplication result = unifiedCreditService.createCreditApplication(creditApplication);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/credit-applications/{applicationId}")
    public ResponseEntity<CreditApplication> updateCreditApplication(
            @PathVariable String applicationId,
            @RequestBody CreditApplication creditApplication) {
        creditApplication.setApplicationId(applicationId);
        CreditApplication result = unifiedCreditService.updateCreditApplication(creditApplication);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/credit-applications/{applicationId}")
    public ResponseEntity<CreditApplication> getCreditApplication(@PathVariable String applicationId) {
        CreditApplication application = unifiedCreditService.getCreditApplicationById(applicationId);
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
    public ResponseEntity<UsageApplication> createUsageApplication(@RequestBody UsageApplication usageApplication) {
        UsageApplication result = unifiedCreditService.createUsageApplication(usageApplication);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/usage-applications/{usageId}")
    public ResponseEntity<UsageApplication> updateUsageApplication(
            @PathVariable String usageId,
            @RequestBody UsageApplication usageApplication) {
        usageApplication.setUsageId(usageId);
        UsageApplication result = unifiedCreditService.updateUsageApplication(usageApplication);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usage-applications/{usageId}")
    public ResponseEntity<UsageApplication> getUsageApplication(@PathVariable String usageId) {
        UsageApplication usageApplication = unifiedCreditService.getUsageApplicationById(usageId);
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
        CreditQuota result = unifiedCreditService.createCreditQuota(creditQuota);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/quotas/{quotaId}")
    public ResponseEntity<CreditQuota> updateCreditQuota(
            @PathVariable Long quotaId,
            @RequestBody CreditQuota creditQuota) {
        creditQuota.setQuotaId(quotaId);
        CreditQuota result = unifiedCreditService.updateCreditQuota(creditQuota);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/quotas/{quotaId}")
    public ResponseEntity<CreditQuota> getCreditQuota(@PathVariable Long quotaId) {
        CreditQuota creditQuota = unifiedCreditService.getCreditQuotaById(quotaId);
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
        boolean result = unifiedCreditService.createQuotaUsageDetail(quotaUsageDetail);
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
        QuotaResponseDTO response = unifiedCreditService.getTotalQuotaInfo(customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/groups/{groupId}/quota-summary")
    public ResponseEntity<QuotaResponseDTO> getGroupTotalQuota(@PathVariable String groupId) {
        QuotaResponseDTO response = unifiedCreditService.getGroupTotalQuotaInfo(groupId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/groups/{groupId}/member-quotas")
    public ResponseEntity<QuotaResponseDTO> getGroupMembersQuota(@PathVariable String groupId) {
        QuotaResponseDTO response = unifiedCreditService.getGroupMemberQuotas(groupId);
        return ResponseEntity.ok(response);
    }

    // 风险监控接口
    @PostMapping("/customers/{customerId}/risk-indexes")
    public ResponseEntity<RiskMonitoringIndex> createRiskMonitoringIndex(
            @PathVariable String customerId,
            @RequestBody RiskMonitoringIndex riskMonitoringIndex) {
        riskMonitoringIndex.setCustomerId(customerId);
        RiskMonitoringIndex result = unifiedCreditService.createRiskMonitoringIndex(riskMonitoringIndex);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/risk-indexes/{indexId}")
    public ResponseEntity<RiskMonitoringIndex> updateRiskMonitoringIndex(
            @PathVariable Long indexId,
            @RequestBody RiskMonitoringIndex riskMonitoringIndex) {
        RiskMonitoringIndex result = unifiedCreditService.updateRiskMonitoringIndex(riskMonitoringIndex);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{customerId}/risk-indexes")
    public ResponseEntity<List<RiskMonitoringIndex>> getRiskMonitoringIndicesByCustomerId(@PathVariable String customerId) {
        List<RiskMonitoringIndex> indices = unifiedCreditService.getRiskMonitoringIndexesByCustomer(customerId);
        return ResponseEntity.ok(indices);
    }

    @GetMapping("/customers/{customerId}/risk-check")
    public ResponseEntity<List<RiskMonitoringIndex>> checkCustomerRiskIndices(@PathVariable String customerId) {
        List<RiskMonitoringIndex> result = unifiedCreditService.checkRiskIndexesByCustomer(customerId);
        return ResponseEntity.ok(result);
    }

    // 风险预警接口
    @PostMapping("/customers/{customerId}/risk-warnings")
    public ResponseEntity<RiskWarning> createRiskWarning(
            @PathVariable String customerId,
            @RequestBody RiskWarning riskWarning) {
        riskWarning.setCustomerId(customerId);
        RiskWarning result = unifiedCreditService.createRiskWarning(riskWarning);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/risk-warnings/{warningId}")
    public ResponseEntity<RiskWarning> updateRiskWarning(
            @PathVariable Long warningId,
            @RequestBody RiskWarning riskWarning) {
        RiskWarning result = unifiedCreditService.updateRiskWarning(riskWarning);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customers/{customerId}/risk-warnings")
    public ResponseEntity<List<RiskWarning>> getRiskWarningsByCustomerId(@PathVariable String customerId) {
        List<RiskWarning> warnings = unifiedCreditService.getRiskWarningsByCustomer(customerId);
        return ResponseEntity.ok(warnings);
    }

    @PutMapping("/risk-warnings/{warningId}/handle")
    public ResponseEntity<RiskWarning> handleRiskWarning(
            @PathVariable Long warningId,
            @RequestBody RiskWarning riskWarning) {
        RiskWarning result = unifiedCreditService.handleRiskWarning(warningId, riskWarning);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 审批流程接口
    @PostMapping("/approval-processes")
    public ResponseEntity<ApprovalProcess> createApprovalProcess(@RequestBody ApprovalProcess approvalProcess) {
        ApprovalProcess result = unifiedCreditService.createApprovalProcess(approvalProcess);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/approval-processes/{processId}")
    public ResponseEntity<ApprovalProcess> updateApprovalProcess(
            @PathVariable String processId,
            @RequestBody ApprovalProcess approvalProcess) {
        approvalProcess.setProcessId(processId);
        ApprovalProcess result = unifiedCreditService.updateApprovalProcess(approvalProcess);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/approval-processes/{processId}")
    public ResponseEntity<ApprovalProcess> getApprovalProcess(@PathVariable String processId) {
        ApprovalProcess process = unifiedCreditService.getApprovalProcessById(processId);
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
        ApprovalNode result = unifiedCreditService.completeApprovalNode(nodeId, approvalNode);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/approval-processes/{processId}/nodes")
    public ResponseEntity<List<ApprovalNode>> getApprovalNodesByProcessId(@PathVariable String processId) {
        List<ApprovalNode> nodes = unifiedCreditService.getApprovalNodesByProcess(processId);
        return ResponseEntity.ok(nodes);
    }
}