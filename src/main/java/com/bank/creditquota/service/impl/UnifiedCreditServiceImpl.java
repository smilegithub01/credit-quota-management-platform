package com.bank.creditquota.service.impl;

import com.bank.creditquota.dto.QuotaRequestDTO;
import com.bank.creditquota.dto.QuotaResponseDTO;
import com.bank.creditquota.entity.*;
import com.bank.creditquota.mapper.*;
import com.bank.creditquota.service.UnifiedCreditService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UnifiedCreditServiceImpl implements UnifiedCreditService {

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private GroupRelationshipMapper groupRelationshipMapper;

    @Autowired
    private CreditQuotaMapper creditQuotaMapper;

    @Autowired
    private CreditApplicationMapper creditApplicationMapper;

    @Autowired
    private QuotaUsageDetailMapper quotaUsageDetailMapper;

    @Autowired
    private RiskMonitoringIndexMapper riskMonitoringIndexMapper;

    @Autowired
    private RiskWarningMapper riskWarningMapper;

    @Autowired
    private ApprovalProcessMapper approvalProcessMapper;

    @Autowired
    private ApprovalNodeMapper approvalNodeMapper;

    @Autowired
    private UsageApplicationMapper usageApplicationMapper;

    @Autowired
    private CustomerAffiliateMapper customerAffiliateMapper;

    @Autowired
    private RedissonClient redissonClient;

    // 客户管理相关实现
    @Override
    public boolean addCustomerInfo(CustomerInfo customerInfo) {
        try {
            customerInfo.setCreatedTime(LocalDateTime.now());
            customerInfoMapper.insert(customerInfo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCustomerInfo(CustomerInfo customerInfo) {
        try {
            customerInfo.setUpdatedTime(LocalDateTime.now());
            customerInfoMapper.updateById(customerInfo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CustomerInfo getCustomerInfo(String customerId) {
        return customerInfoMapper.selectById(customerId);
    }

    @Override
    public List<CustomerInfo> getCustomerList(int pageNum, int pageSize) {
        // 简化实现，实际应使用分页插件
        return customerInfoMapper.selectAll();
    }

    // 集团关系管理相关实现
    @Override
    public boolean addGroupRelationship(GroupRelationship groupRelationship) {
        try {
            groupRelationship.setCreatedTime(LocalDateTime.now());
            groupRelationshipMapper.insert(groupRelationship);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateGroupRelationship(GroupRelationship groupRelationship) {
        try {
            groupRelationship.setUpdatedTime(LocalDateTime.now());
            groupRelationshipMapper.updateById(groupRelationship);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<GroupRelationship> getGroupRelationshipByParent(String parentCustomerId) {
        return groupRelationshipMapper.selectByParentCustomerId(parentCustomerId);
    }

    @Override
    public List<GroupRelationship> getGroupRelationshipByChild(String childCustomerId) {
        return groupRelationshipMapper.selectByChildCustomerId(childCustomerId);
    }

    // 客户关联方管理相关实现
    @Override
    public boolean addCustomerAffiliate(CustomerAffiliate customerAffiliate) {
        try {
            customerAffiliate.setCreatedTime(LocalDateTime.now());
            customerAffiliateMapper.insert(customerAffiliate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCustomerAffiliate(CustomerAffiliate customerAffiliate) {
        try {
            customerAffiliate.setUpdatedTime(LocalDateTime.now());
            customerAffiliateMapper.updateById(customerAffiliate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<CustomerAffiliate> getCustomerAffiliatesByCustomerId(String customerId) {
        return customerAffiliateMapper.selectByCustomerId(customerId);
    }

    // 用信申请相关实现
    @Override
    public String submitUsageApplication(UsageApplication usageApplication) {
        try {
            String usageId = "USA" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            usageApplication.setUsageId(usageId);
            usageApplication.setCreatedTime(LocalDateTime.now());
            usageApplicationMapper.insert(usageApplication);
            return usageId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateUsageApplication(UsageApplication usageApplication) {
        try {
            usageApplication.setUpdatedTime(LocalDateTime.now());
            usageApplicationMapper.updateById(usageApplication);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public UsageApplication getUsageApplication(String usageId) {
        return usageApplicationMapper.selectById(usageId);
    }

    @Override
    public List<UsageApplication> getUsageApplicationsByCustomer(String customerId) {
        return usageApplicationMapper.selectByCustomerId(customerId);
    }

    // 额度使用明细相关实现
    @Override
    public boolean recordQuotaUsageDetail(QuotaUsageDetail quotaUsageDetail) {
        try {
            quotaUsageDetail.setCreatedTime(LocalDateTime.now());
            quotaUsageDetailMapper.insert(quotaUsageDetail);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<QuotaUsageDetail> getQuotaUsageDetailsByQuotaId(Long quotaId) {
        return quotaUsageDetailMapper.selectByQuotaId(quotaId);
    }

    // 额度预占（预留）功能
    @Override
    public QuotaResponseDTO preOccupyQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            String lockKey = "quota_preoccupy_" + request.getCustomerId() + "_" + request.getQuotaType();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CreditQuota creditQuota = creditQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaType());

            if (creditQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            // 检查额度状态
            if (!"ACTIVE".equals(creditQuota.getQuotaStatus())) {
                response.setSuccess(false);
                response.setMessage("额度未激活");
                return response;
            }

            // 检查可用额度
            if (creditQuota.getAvailableQuota().compareTo(request.getAmount()) < 0) {
                response.setSuccess(false);
                response.setMessage("额度不足");
                return response;
            }

            // 更新额度（仅更新可用额度，不更新已用额度）
            BigDecimal newAvailableQuota = creditQuota.getAvailableQuota().subtract(request.getAmount());
            BigDecimal newFrozenQuota = creditQuota.getFrozenQuota().add(request.getAmount());

            creditQuota.setAvailableQuota(newAvailableQuota);
            creditQuota.setFrozenQuota(newFrozenQuota);
            creditQuota.setUpdatedTime(LocalDateTime.now());

            creditQuotaMapper.updateById(creditQuota);

            // 记录额度使用明细
            QuotaUsageDetail quotaUsageDetail = new QuotaUsageDetail();
            quotaUsageDetail.setQuotaId(creditQuota.getQuotaId());
            quotaUsageDetail.setCustomerId(creditQuota.getCustomerId());
            quotaUsageDetail.setTransactionType("PRE_OCCUPY");
            quotaUsageDetail.setTransactionAmount(request.getAmount());
            quotaUsageDetail.setBeforeBalance(creditQuota.getAvailableQuota().add(request.getAmount()));
            quotaUsageDetail.setAfterBalance(newAvailableQuota);
            quotaUsageDetail.setBusinessType("CREDIT");
            quotaUsageDetail.setOperator(request.getReferenceId());
            quotaUsageDetail.setRemark("额度预占");
            quotaUsageDetail.setCreatedBy(request.getReferenceId());
            recordQuotaUsageDetail(quotaUsageDetail);

            response.setCustomerId(creditQuota.getCustomerId());
            response.setQuotaType(creditQuota.getQuotaType());
            response.setTotalAmount(creditQuota.getTotalQuota());
            response.setUsedAmount(creditQuota.getUsedQuota());
            response.setAvailableAmount(creditQuota.getAvailableQuota());
            response.setFrozenAmount(creditQuota.getFrozenQuota());
            response.setStatus(creditQuota.getQuotaStatus());
            response.setSuccess(true);
            response.setMessage("额度预占成功");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度预占失败: " + e.getMessage());
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return response;
    }

    @Override
    public QuotaResponseDTO cancelPreOccupiedQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            String lockKey = "quota_cancel_preoccupy_" + request.getCustomerId() + "_" + request.getQuotaType();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CreditQuota creditQuota = creditQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaType());

            if (creditQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            // 检查冻结额度是否足够解冻
            if (creditQuota.getFrozenQuota().compareTo(request.getAmount()) < 0) {
                response.setSuccess(false);
                response.setMessage("冻结额度不足，无法取消预占");
                return response;
            }

            // 更新额度（将冻结额度转回可用额度）
            BigDecimal newAvailableQuota = creditQuota.getAvailableQuota().add(request.getAmount());
            BigDecimal newFrozenQuota = creditQuota.getFrozenQuota().subtract(request.getAmount());

            creditQuota.setAvailableQuota(newAvailableQuota);
            creditQuota.setFrozenQuota(newFrozenQuota);
            creditQuota.setUpdatedTime(LocalDateTime.now());

            creditQuotaMapper.updateById(creditQuota);

            // 记录额度使用明细
            QuotaUsageDetail quotaUsageDetail = new QuotaUsageDetail();
            quotaUsageDetail.setQuotaId(creditQuota.getQuotaId());
            quotaUsageDetail.setCustomerId(creditQuota.getCustomerId());
            quotaUsageDetail.setTransactionType("CANCEL_PRE_OCCUPY");
            quotaUsageDetail.setTransactionAmount(request.getAmount());
            quotaUsageDetail.setBeforeBalance(creditQuota.getAvailableQuota().subtract(request.getAmount()));
            quotaUsageDetail.setAfterBalance(newAvailableQuota);
            quotaUsageDetail.setBusinessType("CREDIT");
            quotaUsageDetail.setOperator(request.getReferenceId());
            quotaUsageDetail.setRemark("取消额度预占");
            quotaUsageDetail.setCreatedBy(request.getReferenceId());
            recordQuotaUsageDetail(quotaUsageDetail);

            response.setCustomerId(creditQuota.getCustomerId());
            response.setQuotaType(creditQuota.getQuotaType());
            response.setTotalAmount(creditQuota.getTotalQuota());
            response.setUsedAmount(creditQuota.getUsedQuota());
            response.setAvailableAmount(creditQuota.getAvailableQuota());
            response.setFrozenAmount(creditQuota.getFrozenQuota());
            response.setStatus(creditQuota.getQuotaStatus());
            response.setSuccess(true);
            response.setMessage("取消额度预占成功");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("取消额度预占失败: " + e.getMessage());
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return response;
    }

    // 风险监控相关实现
    @Override
    public boolean addRiskMonitoringIndex(RiskMonitoringIndex riskMonitoringIndex) {
        try {
            riskMonitoringIndex.setCreatedTime(LocalDateTime.now());
            riskMonitoringIndexMapper.insert(riskMonitoringIndex);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateRiskMonitoringIndex(RiskMonitoringIndex riskMonitoringIndex) {
        try {
            riskMonitoringIndexMapper.updateById(riskMonitoringIndex);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<RiskMonitoringIndex> getRiskMonitoringIndicesByCustomerId(String customerId) {
        return riskMonitoringIndexMapper.selectByCustomerId(customerId);
    }

    @Override
    public boolean checkCustomerRiskIndices(String customerId) {
        List<RiskMonitoringIndex> indices = riskMonitoringIndexMapper.selectByCustomerId(customerId);
        for (RiskMonitoringIndex index : indices) {
            if (index.getIndexValue() != null && index.getThresholdValue() != null) {
                // 根据风险等级判断是否超标
                if ("HIGH".equals(index.getRiskLevel()) || "CRITICAL".equals(index.getRiskLevel())) {
                    // 如果是高风险或危急风险，返回false表示风险检查未通过
                    return false;
                }
            }
        }
        return true;
    }

    // 风险预警相关实现
    @Override
    public boolean addRiskWarning(RiskWarning riskWarning) {
        try {
            riskWarning.setCreatedTime(LocalDateTime.now());
            riskWarning.setWarningDate(LocalDateTime.now());
            riskWarningMapper.insert(riskWarning);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateRiskWarning(RiskWarning riskWarning) {
        try {
            riskWarning.setUpdatedTime(LocalDateTime.now());
            riskWarningMapper.updateById(riskWarning);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<RiskWarning> getRiskWarningsByCustomerId(String customerId) {
        return riskWarningMapper.selectByCustomerId(customerId);
    }

    @Override
    public boolean handleRiskWarning(Long warningId, String handler, String handleResult) {
        try {
            RiskWarning warning = riskWarningMapper.selectById(warningId);
            if (warning != null) {
                warning.setHandler(handler);
                warning.setHandleResult(handleResult);
                warning.setHandleTime(LocalDateTime.now());
                warning.setWarningStatus("HANDLED");
                riskWarningMapper.updateById(warning);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 审批流程相关实现
    @Override
    public String startApprovalProcess(ApprovalProcess approvalProcess) {
        try {
            String processId = "PROC" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            approvalProcess.setProcessId(processId);
            approvalProcess.setStartTime(LocalDateTime.now());
            approvalProcess.setProcessStatus("RUNNING");
            approvalProcess.setCreatedTime(LocalDateTime.now());
            approvalProcessMapper.insert(approvalProcess);
            return processId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateApprovalProcess(ApprovalProcess approvalProcess) {
        try {
            approvalProcess.setUpdatedTime(LocalDateTime.now());
            approvalProcessMapper.updateById(approvalProcess);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ApprovalProcess getApprovalProcess(String processId) {
        return approvalProcessMapper.selectById(processId);
    }

    @Override
    public boolean completeApprovalNode(ApprovalNode approvalNode) {
        try {
            approvalNode.setEndTime(LocalDateTime.now());
            approvalNode.setNodeStatus("COMPLETED");
            approvalNode.setUpdatedTime(LocalDateTime.now());
            
            // 计算处理时长（分钟）
            if (approvalNode.getStartTime() != null) {
                long duration = java.time.Duration.between(approvalNode.getStartTime(), LocalDateTime.now()).toMinutes();
                approvalNode.setDurationMinutes((int) duration);
            }
            
            approvalNodeMapper.updateById(approvalNode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ApprovalNode> getApprovalNodesByProcessId(String processId) {
        return approvalNodeMapper.selectByProcessId(processId);
    }

    // 授信申请相关实现
    @Override
    public String submitCreditApplication(CreditApplication creditApplication) {
        try {
            String applicationId = "APP" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            creditApplication.setApplicationId(applicationId);
            creditApplication.setCreatedTime(LocalDateTime.now());
            creditApplicationMapper.insert(creditApplication);
            return applicationId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateCreditApplication(CreditApplication creditApplication) {
        try {
            creditApplication.setUpdatedTime(LocalDateTime.now());
            creditApplicationMapper.updateById(creditApplication);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CreditApplication getCreditApplication(String applicationId) {
        return creditApplicationMapper.selectById(applicationId);
    }

    @Override
    public List<CreditApplication> getCreditApplicationsByCustomer(String customerId) {
        return creditApplicationMapper.selectByCustomerId(customerId);
    }

    // 额度管理相关实现
    @Override
    public boolean approveCreditQuota(CreditQuota creditQuota) {
        try {
            // 设置默认值
            if (creditQuota.getUsedQuota() == null) {
                creditQuota.setUsedQuota(BigDecimal.ZERO);
            }
            if (creditQuota.getFrozenQuota() == null) {
                creditQuota.setFrozenQuota(BigDecimal.ZERO);
            }
            if (creditQuota.getAvailableQuota() == null) {
                creditQuota.setAvailableQuota(creditQuota.getTotalQuota());
            }
            if (creditQuota.getQuotaStatus() == null) {
                creditQuota.setQuotaStatus("ACTIVE");
            }
            
            creditQuota.setCreatedTime(LocalDateTime.now());
            creditQuotaMapper.insert(creditQuota);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCreditQuota(CreditQuota creditQuota) {
        try {
            creditQuota.setUpdatedTime(LocalDateTime.now());
            creditQuotaMapper.updateById(creditQuota);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CreditQuota getCreditQuota(Long quotaId) {
        return creditQuotaMapper.selectById(quotaId);
    }

    @Override
    public CreditQuota getCreditQuotaByCustomerAndType(String customerId, String quotaType) {
        return creditQuotaMapper.selectByCustomerIdAndQuotaType(customerId, quotaType);
    }

    @Override
    public List<CreditQuota> getCreditQuotasByCustomer(String customerId) {
        return creditQuotaMapper.selectByCustomerId(customerId);
    }

    // 统一额度管控相关实现
    @Override
    public boolean checkQuotaAvailability(String customerId, String quotaType, BigDecimal amount) {
        CreditQuota creditQuota = creditQuotaMapper.selectByCustomerIdAndQuotaType(customerId, quotaType);
        if (creditQuota != null) {
            // 检查额度状态
            if (!"ACTIVE".equals(creditQuota.getQuotaStatus())) {
                return false;
            }
            // 检查是否在有效期内
            if (creditQuota.getEffectiveDate() != null && creditQuota.getEffectiveDate().isAfter(LocalDateTime.now())) {
                return false;
            }
            if (creditQuota.getExpireDate() != null && creditQuota.getExpireDate().isBefore(LocalDateTime.now())) {
                return false;
            }
            // 检查可用额度
            return creditQuota.getAvailableQuota().compareTo(amount) >= 0;
        }
        return false;
    }

    @Override
    public QuotaResponseDTO occupyQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            String lockKey = "quota_occupy_" + request.getCustomerId() + "_" + request.getQuotaType();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CreditQuota creditQuota = creditQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaType());

            if (creditQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            // 检查额度状态
            if (!"ACTIVE".equals(creditQuota.getQuotaStatus())) {
                response.setSuccess(false);
                response.setMessage("额度未激活");
                return response;
            }

            // 检查可用额度
            if (creditQuota.getAvailableQuota().compareTo(request.getAmount()) < 0) {
                response.setSuccess(false);
                response.setMessage("额度不足");
                return response;
            }

            // 更新额度
            BigDecimal newUsedQuota = creditQuota.getUsedQuota().add(request.getAmount());
            BigDecimal newAvailableQuota = creditQuota.getAvailableQuota().subtract(request.getAmount());

            creditQuota.setUsedQuota(newUsedQuota);
            creditQuota.setAvailableQuota(newAvailableQuota);
            creditQuota.setUpdatedTime(LocalDateTime.now());

            creditQuotaMapper.updateById(creditQuota);

            // 记录额度使用明细
            QuotaUsageDetail quotaUsageDetail = new QuotaUsageDetail();
            quotaUsageDetail.setQuotaId(creditQuota.getQuotaId());
            quotaUsageDetail.setCustomerId(creditQuota.getCustomerId());
            quotaUsageDetail.setTransactionType("OCCUPY");
            quotaUsageDetail.setTransactionAmount(request.getAmount());
            quotaUsageDetail.setBeforeBalance(creditQuota.getAvailableQuota().add(request.getAmount()));
            quotaUsageDetail.setAfterBalance(newAvailableQuota);
            quotaUsageDetail.setBusinessType("CREDIT");
            quotaUsageDetail.setOperator(request.getReferenceId());
            quotaUsageDetail.setRemark("额度占用");
            quotaUsageDetail.setCreatedBy(request.getReferenceId());
            recordQuotaUsageDetail(quotaUsageDetail);

            response.setCustomerId(creditQuota.getCustomerId());
            response.setQuotaType(creditQuota.getQuotaType());
            response.setTotalAmount(creditQuota.getTotalQuota());
            response.setUsedAmount(creditQuota.getUsedQuota());
            response.setAvailableAmount(creditQuota.getAvailableQuota());
            response.setFrozenAmount(creditQuota.getFrozenQuota());
            response.setStatus(creditQuota.getQuotaStatus());
            response.setSuccess(true);
            response.setMessage("额度占用成功");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度占用失败: " + e.getMessage());
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return response;
    }

    @Override
    public QuotaResponseDTO releaseQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            String lockKey = "quota_release_" + request.getCustomerId() + "_" + request.getQuotaType();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CreditQuota creditQuota = creditQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaType());

            if (creditQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            // 更新额度
            BigDecimal newUsedQuota = creditQuota.getUsedQuota().subtract(request.getAmount());
            BigDecimal newAvailableQuota = creditQuota.getAvailableQuota().add(request.getAmount());

            if (newUsedQuota.compareTo(BigDecimal.ZERO) < 0) {
                response.setSuccess(false);
                response.setMessage("释放额度超过已用额度");
                return response;
            }

            creditQuota.setUsedQuota(newUsedQuota);
            creditQuota.setAvailableQuota(newAvailableQuota);
            creditQuota.setUpdatedTime(LocalDateTime.now());

            creditQuotaMapper.updateById(creditQuota);

            // 记录额度使用明细
            QuotaUsageDetail quotaUsageDetail = new QuotaUsageDetail();
            quotaUsageDetail.setQuotaId(creditQuota.getQuotaId());
            quotaUsageDetail.setCustomerId(creditQuota.getCustomerId());
            quotaUsageDetail.setTransactionType("RELEASE");
            quotaUsageDetail.setTransactionAmount(request.getAmount());
            quotaUsageDetail.setBeforeBalance(creditQuota.getAvailableQuota().subtract(request.getAmount()));
            quotaUsageDetail.setAfterBalance(newAvailableQuota);
            quotaUsageDetail.setBusinessType("CREDIT");
            quotaUsageDetail.setOperator(request.getReferenceId());
            quotaUsageDetail.setRemark("额度释放");
            quotaUsageDetail.setCreatedBy(request.getReferenceId());
            recordQuotaUsageDetail(quotaUsageDetail);

            response.setCustomerId(creditQuota.getCustomerId());
            response.setQuotaType(creditQuota.getQuotaType());
            response.setTotalAmount(creditQuota.getTotalQuota());
            response.setUsedAmount(creditQuota.getUsedQuota());
            response.setAvailableAmount(creditQuota.getAvailableQuota());
            response.setFrozenAmount(creditQuota.getFrozenQuota());
            response.setStatus(creditQuota.getQuotaStatus());
            response.setSuccess(true);
            response.setMessage("额度释放成功");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度释放失败: " + e.getMessage());
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return response;
    }

    @Override
    public QuotaResponseDTO freezeQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            String lockKey = "quota_freeze_" + request.getCustomerId() + "_" + request.getQuotaType();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CreditQuota creditQuota = creditQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaType());

            if (creditQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            // 检查可用额度是否足够冻结
            if (creditQuota.getAvailableQuota().compareTo(request.getAmount()) < 0) {
                response.setSuccess(false);
                response.setMessage("可用额度不足，无法冻结");
                return response;
            }

            // 更新额度
            BigDecimal newAvailableQuota = creditQuota.getAvailableQuota().subtract(request.getAmount());
            BigDecimal newFrozenQuota = creditQuota.getFrozenQuota().add(request.getAmount());

            creditQuota.setAvailableQuota(newAvailableQuota);
            creditQuota.setFrozenQuota(newFrozenQuota);
            creditQuota.setUpdatedTime(LocalDateTime.now());

            creditQuotaMapper.updateById(creditQuota);

            // 记录额度使用明细
            QuotaUsageDetail quotaUsageDetail = new QuotaUsageDetail();
            quotaUsageDetail.setQuotaId(creditQuota.getQuotaId());
            quotaUsageDetail.setCustomerId(creditQuota.getCustomerId());
            quotaUsageDetail.setTransactionType("FREEZE");
            quotaUsageDetail.setTransactionAmount(request.getAmount());
            quotaUsageDetail.setBeforeBalance(creditQuota.getAvailableQuota().add(request.getAmount())); // 冻结前的可用额度
            quotaUsageDetail.setAfterBalance(newAvailableQuota);
            quotaUsageDetail.setBusinessType("CREDIT");
            quotaUsageDetail.setOperator(request.getReferenceId());
            quotaUsageDetail.setRemark("额度冻结");
            quotaUsageDetail.setCreatedBy(request.getReferenceId());
            recordQuotaUsageDetail(quotaUsageDetail);

            response.setCustomerId(creditQuota.getCustomerId());
            response.setQuotaType(creditQuota.getQuotaType());
            response.setTotalAmount(creditQuota.getTotalQuota());
            response.setUsedAmount(creditQuota.getUsedQuota());
            response.setAvailableAmount(creditQuota.getAvailableQuota());
            response.setFrozenAmount(creditQuota.getFrozenQuota());
            response.setStatus(creditQuota.getQuotaStatus());
            response.setSuccess(true);
            response.setMessage("额度冻结成功");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度冻结失败: " + e.getMessage());
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return response;
    }

    @Override
    public QuotaResponseDTO unfreezeQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            String lockKey = "quota_unfreeze_" + request.getCustomerId() + "_" + request.getQuotaType();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CreditQuota creditQuota = creditQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaType());

            if (creditQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            // 检查冻结额度是否足够解冻
            if (creditQuota.getFrozenQuota().compareTo(request.getAmount()) < 0) {
                response.setSuccess(false);
                response.setMessage("冻结额度不足，无法解冻");
                return response;
            }

            // 更新额度
            BigDecimal newAvailableQuota = creditQuota.getAvailableQuota().add(request.getAmount());
            BigDecimal newFrozenQuota = creditQuota.getFrozenQuota().subtract(request.getAmount());

            creditQuota.setAvailableQuota(newAvailableQuota);
            creditQuota.setFrozenQuota(newFrozenQuota);
            creditQuota.setUpdatedTime(LocalDateTime.now());

            creditQuotaMapper.updateById(creditQuota);

            // 记录额度使用明细
            QuotaUsageDetail quotaUsageDetail = new QuotaUsageDetail();
            quotaUsageDetail.setQuotaId(creditQuota.getQuotaId());
            quotaUsageDetail.setCustomerId(creditQuota.getCustomerId());
            quotaUsageDetail.setTransactionType("UNFREEZE");
            quotaUsageDetail.setTransactionAmount(request.getAmount());
            quotaUsageDetail.setBeforeBalance(creditQuota.getAvailableQuota().subtract(request.getAmount())); // 解冻前的可用额度
            quotaUsageDetail.setAfterBalance(newAvailableQuota);
            quotaUsageDetail.setBusinessType("CREDIT");
            quotaUsageDetail.setOperator(request.getReferenceId());
            quotaUsageDetail.setRemark("额度解冻");
            quotaUsageDetail.setCreatedBy(request.getReferenceId());
            recordQuotaUsageDetail(quotaUsageDetail);

            response.setCustomerId(creditQuota.getCustomerId());
            response.setQuotaType(creditQuota.getQuotaType());
            response.setTotalAmount(creditQuota.getTotalQuota());
            response.setUsedAmount(creditQuota.getUsedQuota());
            response.setAvailableAmount(creditQuota.getAvailableQuota());
            response.setFrozenAmount(creditQuota.getFrozenQuota());
            response.setStatus(creditQuota.getQuotaStatus());
            response.setSuccess(true);
            response.setMessage("额度解冻成功");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度解冻失败: " + e.getMessage());
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return response;
    }

    @Override
    public QuotaResponseDTO enableQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            String lockKey = "quota_enable_" + request.getCustomerId() + "_" + request.getQuotaType();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CreditQuota creditQuota = creditQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaType());

            if (creditQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            // 启用额度
            creditQuota.setQuotaStatus("ACTIVE");
            creditQuota.setUpdatedTime(LocalDateTime.now());

            creditQuotaMapper.updateById(creditQuota);

            response.setCustomerId(creditQuota.getCustomerId());
            response.setQuotaType(creditQuota.getQuotaType());
            response.setTotalAmount(creditQuota.getTotalQuota());
            response.setUsedAmount(creditQuota.getUsedQuota());
            response.setAvailableAmount(creditQuota.getAvailableQuota());
            response.setFrozenAmount(creditQuota.getFrozenQuota());
            response.setStatus(creditQuota.getQuotaStatus());
            response.setSuccess(true);
            response.setMessage("额度启用成功");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度启用失败: " + e.getMessage());
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return response;
    }

    @Override
    public QuotaResponseDTO disableQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            String lockKey = "quota_disable_" + request.getCustomerId() + "_" + request.getQuotaType();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CreditQuota creditQuota = creditQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaType());

            if (creditQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            // 停用额度
            creditQuota.setQuotaStatus("INACTIVE");
            creditQuota.setUpdatedTime(LocalDateTime.now());

            creditQuotaMapper.updateById(creditQuota);

            response.setCustomerId(creditQuota.getCustomerId());
            response.setQuotaType(creditQuota.getQuotaType());
            response.setTotalAmount(creditQuota.getTotalQuota());
            response.setUsedAmount(creditQuota.getUsedQuota());
            response.setAvailableAmount(creditQuota.getAvailableQuota());
            response.setFrozenAmount(creditQuota.getFrozenQuota());
            response.setStatus(creditQuota.getQuotaStatus());
            response.setSuccess(true);
            response.setMessage("额度停用成功");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度停用失败: " + e.getMessage());
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return response;
    }

    @Override
    public QuotaResponseDTO adjustQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            String lockKey = "quota_adjust_" + request.getCustomerId() + "_" + request.getQuotaType();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CreditQuota creditQuota = creditQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaType());

            if (creditQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            // 调整额度
            BigDecimal oldTotal = creditQuota.getTotalQuota();
            BigDecimal newTotal = oldTotal.add(request.getAmount());
            BigDecimal oldAvailable = creditQuota.getAvailableQuota();
            BigDecimal newAvailable = oldAvailable.add(request.getAmount());

            if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
                response.setSuccess(false);
                response.setMessage("调整后总额度不能小于0");
                return response;
            }

            creditQuota.setTotalQuota(newTotal);
            creditQuota.setAvailableQuota(newAvailable);
            creditQuota.setUpdatedTime(LocalDateTime.now());

            creditQuotaMapper.updateById(creditQuota);

            // 记录额度使用明细
            QuotaUsageDetail quotaUsageDetail = new QuotaUsageDetail();
            quotaUsageDetail.setQuotaId(creditQuota.getQuotaId());
            quotaUsageDetail.setCustomerId(creditQuota.getCustomerId());
            quotaUsageDetail.setTransactionType("ADJUST");
            quotaUsageDetail.setTransactionAmount(request.getAmount());
            quotaUsageDetail.setBeforeBalance(oldAvailable);
            quotaUsageDetail.setAfterBalance(newAvailable);
            quotaUsageDetail.setBusinessType("CREDIT");
            quotaUsageDetail.setOperator(request.getReferenceId());
            quotaUsageDetail.setRemark("额度调整");
            quotaUsageDetail.setCreatedBy(request.getReferenceId());
            recordQuotaUsageDetail(quotaUsageDetail);

            response.setCustomerId(creditQuota.getCustomerId());
            response.setQuotaType(creditQuota.getQuotaType());
            response.setTotalAmount(creditQuota.getTotalQuota());
            response.setUsedAmount(creditQuota.getUsedQuota());
            response.setAvailableAmount(creditQuota.getAvailableQuota());
            response.setFrozenAmount(creditQuota.getFrozenQuota());
            response.setStatus(creditQuota.getQuotaStatus());
            response.setSuccess(true);
            response.setMessage("额度调整成功");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度调整失败: " + e.getMessage());
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return response;
    }

    @Override
    public QuotaResponseDTO distributeGroupQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            String lockKey = "quota_distribute_group_" + request.getCustomerId();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            // 查找集团额度
            CreditQuota groupQuota = creditQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaType());

            if (groupQuota == null) {
                response.setSuccess(false);
                response.setMessage("集团额度记录不存在");
                return response;
            }

            // 检查集团是否有足够的额度可供分配
            if (groupQuota.getAvailableQuota().compareTo(request.getAmount()) < 0) {
                response.setSuccess(false);
                response.setMessage("集团可用额度不足，无法分配");
                return response;
            }

            // 更新集团额度
            BigDecimal newGroupUsedQuota = groupQuota.getUsedQuota().add(request.getAmount());
            BigDecimal newGroupAvailableQuota = groupQuota.getAvailableQuota().subtract(request.getAmount());

            groupQuota.setUsedQuota(newGroupUsedQuota);
            groupQuota.setAvailableQuota(newGroupAvailableQuota);
            groupQuota.setUpdatedTime(LocalDateTime.now());

            creditQuotaMapper.updateById(groupQuota);

            // 记录集团额度使用明细
            QuotaUsageDetail groupQuotaUsageDetail = new QuotaUsageDetail();
            groupQuotaUsageDetail.setQuotaId(groupQuota.getQuotaId());
            groupQuotaUsageDetail.setCustomerId(groupQuota.getCustomerId());
            groupQuotaUsageDetail.setTransactionType("OCCUPY");
            groupQuotaUsageDetail.setTransactionAmount(request.getAmount());
            groupQuotaUsageDetail.setBeforeBalance(groupQuota.getAvailableQuota().add(request.getAmount()));
            groupQuotaUsageDetail.setAfterBalance(newGroupAvailableQuota);
            groupQuotaUsageDetail.setBusinessType("GROUP_QUOTA_DISTRIBUTE");
            groupQuotaUsageDetail.setOperator(request.getReferenceId());
            groupQuotaUsageDetail.setRemark("集团额度分配-占用集团额度");
            groupQuotaUsageDetail.setCreatedBy(request.getReferenceId());
            recordQuotaUsageDetail(groupQuotaUsageDetail);

            // 创建子客户额度记录
            CreditQuota subQuota = new CreditQuota();
            subQuota.setCustomerId(request.getReferenceId()); // 分配的目标客户ID
            subQuota.setQuotaType(request.getQuotaType());
            subQuota.setTotalQuota(request.getAmount());
            subQuota.setUsedQuota(BigDecimal.ZERO);
            subQuota.setAvailableQuota(request.getAmount()); // 新分配的额度全部可用
            subQuota.setFrozenQuota(BigDecimal.ZERO);
            subQuota.setCurrency(groupQuota.getCurrency());
            subQuota.setQuotaLevel("PRODUCT"); // 子额度层级
            subQuota.setParentQuotaId(groupQuota.getQuotaId()); // 关联父额度
            subQuota.setEffectiveDate(LocalDateTime.now());
            subQuota.setExpireDate(groupQuota.getExpireDate()); // 继承父额度有效期
            subQuota.setQuotaStatus("ACTIVE"); // 启用状态
            subQuota.setGuaranteeInfo(groupQuota.getGuaranteeInfo()); // 继承担保信息
            subQuota.setCreatedBy(request.getReferenceId());
            subQuota.setCreatedTime(LocalDateTime.now());
            subQuota.setUpdatedTime(LocalDateTime.now());

            creditQuotaMapper.insert(subQuota);

            // 记录子客户额度使用明细
            QuotaUsageDetail subQuotaUsageDetail = new QuotaUsageDetail();
            subQuotaUsageDetail.setQuotaId(subQuota.getQuotaId());
            subQuotaUsageDetail.setCustomerId(subQuota.getCustomerId());
            subQuotaUsageDetail.setTransactionType("OCCUPY");
            subQuotaUsageDetail.setTransactionAmount(BigDecimal.ZERO); // 初始分配不占用额度
            subQuotaUsageDetail.setBeforeBalance(subQuota.getAvailableQuota());
            subQuotaUsageDetail.setAfterBalance(subQuota.getAvailableQuota());
            subQuotaUsageDetail.setBusinessType("GROUP_QUOTA_DISTRIBUTE");
            subQuotaUsageDetail.setOperator(request.getReferenceId());
            subQuotaUsageDetail.setRemark("集团额度分配-子客户额度创建");
            subQuotaUsageDetail.setCreatedBy(request.getReferenceId());
            recordQuotaUsageDetail(subQuotaUsageDetail);

            response.setCustomerId(request.getCustomerId());
            response.setQuotaType(request.getQuotaType());
            response.setTotalAmount(groupQuota.getTotalQuota());
            response.setUsedAmount(groupQuota.getUsedQuota());
            response.setAvailableAmount(groupQuota.getAvailableQuota());
            response.setFrozenAmount(groupQuota.getFrozenQuota());
            response.setStatus(groupQuota.getQuotaStatus());
            response.setSuccess(true);
            response.setMessage("集团额度成功分配给子客户");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("集团额度分配失败: " + e.getMessage());
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return response;
    }

    @Override
    public QuotaResponseDTO getCustomerTotalQuota(String customerId) {
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            List<CreditQuota> quotas = creditQuotaMapper.selectByCustomerId(customerId);

            if (quotas.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("客户无额度记录");
                return response;
            }

            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal usedAmount = BigDecimal.ZERO;
            BigDecimal availableAmount = BigDecimal.ZERO;
            BigDecimal frozenAmount = BigDecimal.ZERO;

            for (CreditQuota quota : quotas) {
                if ("ACTIVE".equals(quota.getQuotaStatus())) {
                    totalAmount = totalAmount.add(quota.getTotalQuota());
                    usedAmount = usedAmount.add(quota.getUsedQuota());
                    availableAmount = availableAmount.add(quota.getAvailableQuota());
                    frozenAmount = frozenAmount.add(quota.getFrozenQuota());
                }
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
    public QuotaResponseDTO getGroupTotalQuota(String groupId) {
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            // 获取集团及其所有成员
            List<GroupRelationship> relationships = groupRelationshipMapper.selectByParentCustomerId(groupId);
            
            // 包括集团本身
            List<CreditQuota> groupQuotas = creditQuotaMapper.selectByCustomerId(groupId);

            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal usedAmount = BigDecimal.ZERO;
            BigDecimal availableAmount = BigDecimal.ZERO;
            BigDecimal frozenAmount = BigDecimal.ZERO;

            // 计算集团本身的额度
            for (CreditQuota quota : groupQuotas) {
                if ("ACTIVE".equals(quota.getQuotaStatus()) && "GROUP".equals(quota.getQuotaLevel())) {
                    totalAmount = totalAmount.add(quota.getTotalQuota());
                    usedAmount = usedAmount.add(quota.getUsedQuota());
                    availableAmount = availableAmount.add(quota.getAvailableQuota());
                    frozenAmount = frozenAmount.add(quota.getFrozenQuota());
                }
            }

            // 如果有成员，则加上成员的额度
            if (relationships != null && !relationships.isEmpty()) {
                for (GroupRelationship rel : relationships) {
                    List<CreditQuota> memberQuotas = creditQuotaMapper.selectByCustomerId(rel.getChildCustomerId());
                    for (CreditQuota quota : memberQuotas) {
                        if ("ACTIVE".equals(quota.getQuotaStatus()) && "CUSTOMER".equals(quota.getQuotaLevel())) {
                            totalAmount = totalAmount.add(quota.getTotalQuota());
                            usedAmount = usedAmount.add(quota.getUsedQuota());
                            availableAmount = availableAmount.add(quota.getAvailableQuota());
                            frozenAmount = frozenAmount.add(quota.getFrozenQuota());
                        }
                    }
                }
            }

            response.setCustomerId(groupId);
            response.setTotalAmount(totalAmount);
            response.setUsedAmount(usedAmount);
            response.setAvailableAmount(availableAmount);
            response.setFrozenAmount(frozenAmount);
            response.setStatus("ACTIVE");
            response.setSuccess(true);
            response.setMessage("获取集团总额度信息成功");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("获取集团总额度信息失败: " + e.getMessage());
        }
        return response;
    }

    @Override
    public QuotaResponseDTO getGroupMembersQuota(String groupId) {
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            // 获取集团下所有成员
            List<GroupRelationship> relationships = groupRelationshipMapper.selectByParentCustomerId(groupId);

            if (relationships.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("集团下无成员");
                return response;
            }

            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal usedAmount = BigDecimal.ZERO;
            BigDecimal availableAmount = BigDecimal.ZERO;
            BigDecimal frozenAmount = BigDecimal.ZERO;

            // 计算集团下所有成员的额度总和
            for (GroupRelationship rel : relationships) {
                List<CreditQuota> memberQuotas = creditQuotaMapper.selectByCustomerId(rel.getChildCustomerId());
                for (CreditQuota quota : memberQuotas) {
                    if ("ACTIVE".equals(quota.getQuotaStatus()) && "CUSTOMER".equals(quota.getQuotaLevel())) {
                        totalAmount = totalAmount.add(quota.getTotalQuota());
                        usedAmount = usedAmount.add(quota.getUsedQuota());
                        availableAmount = availableAmount.add(quota.getAvailableQuota());
                        frozenAmount = frozenAmount.add(quota.getFrozenQuota());
                    }
                }
            }

            response.setCustomerId(groupId);
            response.setTotalAmount(totalAmount);
            response.setUsedAmount(usedAmount);
            response.setAvailableAmount(availableAmount);
            response.setFrozenAmount(frozenAmount);
            response.setStatus("ACTIVE");
            response.setSuccess(true);
            response.setMessage("获取集团成员额度汇总成功");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("获取集团成员额度汇总失败: " + e.getMessage());
        }
        return response;
    }
}