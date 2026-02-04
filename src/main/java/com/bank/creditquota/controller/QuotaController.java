package com.bank.creditquota.controller;

import com.bank.creditquota.dto.QuotaRequestDTO;
import com.bank.creditquota.dto.QuotaResponseDTO;
import com.bank.creditquota.service.QuotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quota")
public class QuotaController {

    @Autowired
    private QuotaService quotaService;

    @PostMapping("/check")
    public ResponseEntity<Boolean> checkQuota(@RequestParam String customerId, 
                                             @RequestParam Long quotaTypeId, 
                                             @RequestParam java.math.BigDecimal amount) {
        boolean available = quotaService.checkQuotaAvailability(customerId, quotaTypeId, amount);
        return ResponseEntity.ok(available);
    }

    @PostMapping("/consume")
    public ResponseEntity<QuotaResponseDTO> consumeQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = quotaService.consumeQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/release")
    public ResponseEntity<QuotaResponseDTO> releaseQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = quotaService.releaseQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/adjust")
    public ResponseEntity<QuotaResponseDTO> adjustQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = quotaService.adjustQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/freeze")
    public ResponseEntity<QuotaResponseDTO> freezeQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = quotaService.freezeQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unfreeze")
    public ResponseEntity<QuotaResponseDTO> unfreezeQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = quotaService.unfreezeQuota(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}/quota/{quotaTypeId}")
    public ResponseEntity<QuotaResponseDTO> getCustomerQuota(@PathVariable String customerId, 
                                                            @PathVariable Long quotaTypeId) {
        QuotaResponseDTO response = quotaService.getCustomerQuota(customerId, quotaTypeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}/total")
    public ResponseEntity<QuotaResponseDTO> getCustomerTotalQuota(@PathVariable String customerId) {
        QuotaResponseDTO response = quotaService.getCustomerTotalQuota(customerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/allocate")
    public ResponseEntity<QuotaResponseDTO> allocateQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = quotaService.allocateQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recover")
    public ResponseEntity<QuotaResponseDTO> recoverQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = quotaService.recoverQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/enable")
    public ResponseEntity<QuotaResponseDTO> enableQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = quotaService.enableQuota(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/disable")
    public ResponseEntity<QuotaResponseDTO> disableQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = quotaService.disableQuota(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<QuotaResponseDTO> getGroupQuota(@PathVariable String groupId) {
        QuotaResponseDTO response = quotaService.getGroupQuota(groupId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/group/{groupId}/members")
    public ResponseEntity<QuotaResponseDTO> getGroupMembersQuota(@PathVariable String groupId) {
        QuotaResponseDTO response = quotaService.getGroupMembersQuota(groupId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/distribute-group")
    public ResponseEntity<QuotaResponseDTO> distributeGroupQuota(@RequestBody QuotaRequestDTO request) {
        QuotaResponseDTO response = quotaService.distributeGroupQuota(request);
        return ResponseEntity.ok(response);
    }
}