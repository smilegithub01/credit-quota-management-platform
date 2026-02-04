package com.bank.creditquota.service.impl;

import com.bank.creditquota.dto.QuotaRequestDTO;
import com.bank.creditquota.dto.QuotaResponseDTO;
import com.bank.creditquota.entity.Customer;
import com.bank.creditquota.entity.CustomerQuota;
import com.bank.creditquota.entity.QuotaTransaction;
import com.bank.creditquota.mapper.CustomerMapper;
import com.bank.creditquota.mapper.CustomerQuotaMapper;
import com.bank.creditquota.mapper.QuotaTransactionMapper;
import com.bank.creditquota.service.QuotaService;
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
public class QuotaServiceImpl implements QuotaService {

    @Autowired
    private CustomerQuotaMapper customerQuotaMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private QuotaTransactionMapper quotaTransactionMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public boolean checkQuotaAvailability(String customerId, Long quotaTypeId, BigDecimal amount) {
        CustomerQuota customerQuota = customerQuotaMapper.selectByCustomerIdAndQuotaType(customerId, quotaTypeId);
        if (customerQuota != null) {
            // 检查额度是否启用
            if (customerQuota.getStatus() == 0) {
                return false; // 额度未启用
            }
            // 检查是否在有效期内
            if (customerQuota.getEffectiveDate() != null && customerQuota.getEffectiveDate().isAfter(LocalDateTime.now())) {
                return false; // 未到生效日期
            }
            if (customerQuota.getExpireDate() != null && customerQuota.getExpireDate().isBefore(LocalDateTime.now())) {
                return false; // 已过期
            }
            return customerQuota.getAvailableAmount().compareTo(amount) >= 0;
        }
        return false;
    }

    @Override
    public QuotaResponseDTO consumeQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            // 使用分布式锁，确保并发安全
            String lockKey = "quota_consume_" + request.getCustomerId() + "_" + request.getQuotaTypeId();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CustomerQuota customerQuota = customerQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaTypeId());

            if (customerQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            // 检查额度是否启用
            if (customerQuota.getStatus() == 0) {
                response.setSuccess(false);
                response.setMessage("额度未启用");
                return response;
            }

            if (customerQuota.getAvailableAmount().compareTo(request.getAmount()) < 0) {
                response.setSuccess(false);
                response.setMessage("额度不足");
                return response;
            }

            // 更新额度
            BigDecimal newUsedAmount = customerQuota.getUsedAmount().add(request.getAmount());
            BigDecimal newAvailableAmount = customerQuota.getAvailableAmount().subtract(request.getAmount());

            customerQuota.setUsedAmount(newUsedAmount);
            customerQuota.setAvailableAmount(newAvailableAmount);
            customerQuota.setUpdatedAt(LocalDateTime.now());

            customerQuotaMapper.updateById(customerQuota);

            // 记录交易
            createTransaction(request, customerQuota.getId(), 2, // 2-占用
                             customerQuota.getAvailableAmount().add(request.getAmount()),
                             customerQuota.getAvailableAmount());

            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setFrozenAmount(customerQuota.getFrozenAmount());
            response.setStatus(customerQuota.getStatus().toString());
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
            String lockKey = "quota_release_" + request.getCustomerId() + "_" + request.getQuotaTypeId();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CustomerQuota customerQuota = customerQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaTypeId());

            if (customerQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            // 更新额度
            BigDecimal newUsedAmount = customerQuota.getUsedAmount().subtract(request.getAmount());
            BigDecimal newAvailableAmount = customerQuota.getAvailableAmount().add(request.getAmount());

            customerQuota.setUsedAmount(newUsedAmount);
            customerQuota.setAvailableAmount(newAvailableAmount);
            customerQuota.setUpdatedAt(LocalDateTime.now());

            customerQuotaMapper.updateById(customerQuota);

            // 记录交易
            createTransaction(request, customerQuota.getId(), 3, // 3-释放
                             customerQuota.getAvailableAmount().subtract(request.getAmount()),
                             customerQuota.getAvailableAmount());

            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setFrozenAmount(customerQuota.getFrozenAmount());
            response.setStatus(customerQuota.getStatus().toString());
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
    public QuotaResponseDTO adjustQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            String lockKey = "quota_adjust_" + request.getCustomerId() + "_" + request.getQuotaTypeId();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CustomerQuota customerQuota = customerQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaTypeId());

            if (customerQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            // 调整总额度
            BigDecimal oldTotal = customerQuota.getTotalAmount();
            BigDecimal newTotal = oldTotal.add(request.getAmount());
            BigDecimal oldAvailable = customerQuota.getAvailableAmount();
            BigDecimal newAvailable = oldAvailable.add(request.getAmount());

            customerQuota.setTotalAmount(newTotal);
            customerQuota.setAvailableAmount(newAvailable);
            customerQuota.setUpdatedAt(LocalDateTime.now());

            customerQuotaMapper.updateById(customerQuota);

            // 记录交易
            createTransaction(request, customerQuota.getId(), 4, // 4-调整
                             oldTotal, newTotal);

            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setFrozenAmount(customerQuota.getFrozenAmount());
            response.setStatus(customerQuota.getStatus().toString());
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
    public QuotaResponseDTO freezeQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            String lockKey = "quota_freeze_" + request.getCustomerId() + "_" + request.getQuotaTypeId();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CustomerQuota customerQuota = customerQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaTypeId());

            if (customerQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            if (customerQuota.getAvailableAmount().compareTo(request.getAmount()) < 0) {
                response.setSuccess(false);
                response.setMessage("可用额度不足，无法冻结");
                return response;
            }

            // 更新额度
            BigDecimal newAvailableAmount = customerQuota.getAvailableAmount().subtract(request.getAmount());
            BigDecimal newFrozenAmount = customerQuota.getFrozenAmount().add(request.getAmount());

            customerQuota.setAvailableAmount(newAvailableAmount);
            customerQuota.setFrozenAmount(newFrozenAmount);
            customerQuota.setUpdatedAt(LocalDateTime.now());

            customerQuotaMapper.updateById(customerQuota);

            // 记录交易
            createTransaction(request, customerQuota.getId(), 5, // 5-冻结
                             customerQuota.getAvailableAmount().add(request.getAmount()),
                             customerQuota.getAvailableAmount());

            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setFrozenAmount(customerQuota.getFrozenAmount());
            response.setStatus(customerQuota.getStatus().toString());
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
            String lockKey = "quota_unfreeze_" + request.getCustomerId() + "_" + request.getQuotaTypeId();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CustomerQuota customerQuota = customerQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaTypeId());

            if (customerQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            if (customerQuota.getFrozenAmount().compareTo(request.getAmount()) < 0) {
                response.setSuccess(false);
                response.setMessage("冻结额度不足，无法解冻");
                return response;
            }

            // 更新额度
            BigDecimal newAvailableAmount = customerQuota.getAvailableAmount().add(request.getAmount());
            BigDecimal newFrozenAmount = customerQuota.getFrozenAmount().subtract(request.getAmount());

            customerQuota.setAvailableAmount(newAvailableAmount);
            customerQuota.setFrozenAmount(newFrozenAmount);
            customerQuota.setUpdatedAt(LocalDateTime.now());

            customerQuotaMapper.updateById(customerQuota);

            // 记录交易
            createTransaction(request, customerQuota.getId(), 6, // 6-解冻
                             customerQuota.getFrozenAmount().add(request.getAmount()),
                             customerQuota.getFrozenAmount());

            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setFrozenAmount(customerQuota.getFrozenAmount());
            response.setStatus(customerQuota.getStatus().toString());
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
    public QuotaResponseDTO getCustomerQuota(String customerId, Long quotaTypeId) {
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            CustomerQuota customerQuota = customerQuotaMapper.selectByCustomerIdAndQuotaType(customerId, quotaTypeId);

            if (customerQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setFrozenAmount(customerQuota.getFrozenAmount());
            response.setStatus(customerQuota.getStatus().toString());
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
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            List<CustomerQuota> quotas = customerQuotaMapper.selectByCustomerId(customerId);

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
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            String lockKey = "quota_allocate_" + request.getCustomerId() + "_" + request.getQuotaTypeId();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            // 检查是否已存在相同的额度类型
            CustomerQuota existingQuota = customerQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaTypeId());

            if (existingQuota != null) {
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
            customerQuota.setAvailableAmount(request.getAmount()); // 新额度全部可用
            customerQuota.setFrozenAmount(BigDecimal.ZERO);
            customerQuota.setStatus(1); // 默认启用
            customerQuota.setQuotaLevel(2); // 2-客户额度，如果是集团则为1
            customerQuota.setEffectiveDate(LocalDateTime.now());
            customerQuota.setCreatedAt(LocalDateTime.now());
            customerQuota.setUpdatedAt(LocalDateTime.now());

            customerQuotaMapper.insert(customerQuota);

            // 记录交易
            createTransaction(request, customerQuota.getId(), 1, // 1-分配
                             BigDecimal.ZERO, customerQuota.getTotalAmount());

            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setFrozenAmount(customerQuota.getFrozenAmount());
            response.setStatus(customerQuota.getStatus().toString());
            response.setSuccess(true);
            response.setMessage("额度分配成功");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度分配失败: " + e.getMessage());
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return response;
    }

    @Override
    public QuotaResponseDTO recoverQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            String lockKey = "quota_recover_" + request.getCustomerId() + "_" + request.getQuotaTypeId();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CustomerQuota customerQuota = customerQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaTypeId());

            if (customerQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            // 回收额度：减少总额度
            BigDecimal oldTotal = customerQuota.getTotalAmount();
            BigDecimal newTotal = oldTotal.subtract(request.getAmount());
            BigDecimal oldAvailable = customerQuota.getAvailableAmount();
            BigDecimal newAvailable = oldAvailable.subtract(request.getAmount());

            if (newTotal.compareTo(BigDecimal.ZERO) < 0 || newAvailable.compareTo(BigDecimal.ZERO) < 0) {
                response.setSuccess(false);
                response.setMessage("回收额度超出可用额度");
                return response;
            }

            customerQuota.setTotalAmount(newTotal);
            customerQuota.setAvailableAmount(newAvailable);
            customerQuota.setUpdatedAt(LocalDateTime.now());

            customerQuotaMapper.updateById(customerQuota);

            // 记录交易
            createTransaction(request, customerQuota.getId(), 7, // 7-回收
                             oldTotal, newTotal);

            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setFrozenAmount(customerQuota.getFrozenAmount());
            response.setStatus(customerQuota.getStatus().toString());
            response.setSuccess(true);
            response.setMessage("额度回收成功");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("额度回收失败: " + e.getMessage());
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
            String lockKey = "quota_enable_" + request.getCustomerId() + "_" + request.getQuotaTypeId();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CustomerQuota customerQuota = customerQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaTypeId());

            if (customerQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            customerQuota.setStatus(1); // 启用额度
            customerQuota.setUpdatedAt(LocalDateTime.now());

            customerQuotaMapper.updateById(customerQuota);

            // 记录交易
            createTransaction(request, customerQuota.getId(), 8, // 8-启用
                             new BigDecimal(customerQuota.getStatus()), new BigDecimal(1));

            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setFrozenAmount(customerQuota.getFrozenAmount());
            response.setStatus(customerQuota.getStatus().toString());
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
            String lockKey = "quota_disable_" + request.getCustomerId() + "_" + request.getQuotaTypeId();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            CustomerQuota customerQuota = customerQuotaMapper.selectByCustomerIdAndQuotaType(
                request.getCustomerId(), request.getQuotaTypeId());

            if (customerQuota == null) {
                response.setSuccess(false);
                response.setMessage("客户额度记录不存在");
                return response;
            }

            customerQuota.setStatus(0); // 停用额度
            customerQuota.setUpdatedAt(LocalDateTime.now());

            customerQuotaMapper.updateById(customerQuota);

            // 记录交易
            createTransaction(request, customerQuota.getId(), 9, // 9-停用
                             new BigDecimal(customerQuota.getStatus()), new BigDecimal(0));

            response.setCustomerId(customerQuota.getCustomerId());
            response.setQuotaTypeId(customerQuota.getQuotaTypeId());
            response.setTotalAmount(customerQuota.getTotalAmount());
            response.setUsedAmount(customerQuota.getUsedAmount());
            response.setAvailableAmount(customerQuota.getAvailableAmount());
            response.setFrozenAmount(customerQuota.getFrozenAmount());
            response.setStatus(customerQuota.getStatus().toString());
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
    public QuotaResponseDTO getGroupQuota(String groupId) {
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            // 查找集团客户下的所有额度记录
            List<CustomerQuota> groupQuotas = customerQuotaMapper.selectByCustomerId(groupId);

            if (groupQuotas.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("集团无额度记录");
                return response;
            }

            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal usedAmount = BigDecimal.ZERO;
            BigDecimal availableAmount = BigDecimal.ZERO;
            BigDecimal frozenAmount = BigDecimal.ZERO;

            // 计算集团总额度（只计算层级为1的集团额度）
            for (CustomerQuota quota : groupQuotas) {
                if (quota.getQuotaLevel() != null && quota.getQuotaLevel() == 1) { // 1-集团额度
                    totalAmount = totalAmount.add(quota.getTotalAmount());
                    usedAmount = usedAmount.add(quota.getUsedAmount());
                    availableAmount = availableAmount.add(quota.getAvailableAmount());
                    frozenAmount = frozenAmount.add(quota.getFrozenAmount());
                }
            }

            response.setCustomerId(groupId);
            response.setTotalAmount(totalAmount);
            response.setUsedAmount(usedAmount);
            response.setAvailableAmount(availableAmount);
            response.setFrozenAmount(frozenAmount);
            response.setStatus("ACTIVE");
            response.setSuccess(true);
            response.setMessage("获取集团额度信息成功");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("获取集团额度信息失败: " + e.getMessage());
        }
        return response;
    }

    @Override
    public QuotaResponseDTO getGroupMembersQuota(String groupId) {
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            // 获取集团下的所有成员
            List<Customer> members = customerMapper.selectByParentId(groupId);

            if (members.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("集团下无成员");
                return response;
            }

            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal usedAmount = BigDecimal.ZERO;
            BigDecimal availableAmount = BigDecimal.ZERO;
            BigDecimal frozenAmount = BigDecimal.ZERO;

            // 计算集团下所有成员的额度总和
            for (Customer member : members) {
                List<CustomerQuota> memberQuotas = customerQuotaMapper.selectByCustomerId(member.getCustomerId());
                for (CustomerQuota quota : memberQuotas) {
                    // 只计算客户层级额度（层级为2）
                    if (quota.getQuotaLevel() != null && quota.getQuotaLevel() == 2) {
                        totalAmount = totalAmount.add(quota.getTotalAmount());
                        usedAmount = usedAmount.add(quota.getUsedAmount());
                        availableAmount = availableAmount.add(quota.getAvailableAmount());
                        frozenAmount = frozenAmount.add(quota.getFrozenAmount());
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

    @Override
    public QuotaResponseDTO distributeGroupQuota(QuotaRequestDTO request) {
        RLock lock = null;
        QuotaResponseDTO response = new QuotaResponseDTO();
        try {
            // 集团额度分配需要特殊的锁策略
            String lockKey = "quota_distribute_group_" + request.getCustomerId();
            lock = redissonClient.getLock(lockKey);
            lock.lock(30, TimeUnit.SECONDS);

            // 首先找到集团的额度
            List<CustomerQuota> groupQuotas = customerQuotaMapper.selectByCustomerId(request.getCustomerId());
            CustomerQuota groupQuota = null;
            
            // 查找对应的集团额度（层级为1）
            for (CustomerQuota quota : groupQuotas) {
                if (quota.getQuotaTypeId().equals(request.getQuotaTypeId()) && 
                    quota.getQuotaLevel() != null && quota.getQuotaLevel() == 1) {
                    groupQuota = quota;
                    break;
                }
            }

            if (groupQuota == null) {
                response.setSuccess(false);
                response.setMessage("集团额度记录不存在");
                return response;
            }

            // 检查集团是否有足够的额度可供分配
            if (groupQuota.getAvailableAmount().compareTo(request.getAmount()) < 0) {
                response.setSuccess(false);
                response.setMessage("集团可用额度不足，无法分配");
                return response;
            }

            // 更新集团额度
            BigDecimal newGroupUsedAmount = groupQuota.getUsedAmount().add(request.getAmount());
            BigDecimal newGroupAvailableAmount = groupQuota.getAvailableAmount().subtract(request.getAmount());

            groupQuota.setUsedAmount(newGroupUsedAmount);
            groupQuota.setAvailableAmount(newGroupAvailableAmount);
            groupQuota.setUpdatedAt(LocalDateTime.now());

            customerQuotaMapper.updateById(groupQuota);

            // 创建子额度记录（分配给具体客户）
            CustomerQuota subQuota = new CustomerQuota();
            subQuota.setCustomerId(request.getReferenceId()); // 分配的目标客户ID
            subQuota.setQuotaTypeId(request.getQuotaTypeId());
            subQuota.setTotalAmount(request.getAmount());
            subQuota.setUsedAmount(BigDecimal.ZERO);
            subQuota.setAvailableAmount(request.getAmount()); // 新分配的额度全部可用
            subQuota.setFrozenAmount(BigDecimal.ZERO);
            subQuota.setStatus(1); // 启用
            subQuota.setQuotaLevel(2); // 2-客户额度
            subQuota.setParentQuotaId(String.valueOf(groupQuota.getId())); // 关联父额度
            subQuota.setEffectiveDate(LocalDateTime.now());
            subQuota.setCreatedAt(LocalDateTime.now());
            subQuota.setUpdatedAt(LocalDateTime.now());

            customerQuotaMapper.insert(subQuota);

            // 记录交易
            createTransaction(request, groupQuota.getId(), 1, // 1-分配
                             groupQuota.getAvailableAmount().add(request.getAmount()),
                             groupQuota.getAvailableAmount());

            response.setCustomerId(request.getCustomerId());
            response.setQuotaTypeId(request.getQuotaTypeId());
            response.setTotalAmount(groupQuota.getTotalAmount());
            response.setUsedAmount(groupQuota.getUsedAmount());
            response.setAvailableAmount(groupQuota.getAvailableAmount());
            response.setFrozenAmount(groupQuota.getFrozenAmount());
            response.setStatus(groupQuota.getStatus().toString());
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

    private void createTransaction(QuotaRequestDTO request, Long customerQuotaId, 
                                  Integer transactionType, BigDecimal beforeBalance, BigDecimal afterBalance) {
        QuotaTransaction transaction = new QuotaTransaction();
        transaction.setTransactionId(UUID.randomUUID().toString().replace("-", ""));
        transaction.setCustomerId(request.getCustomerId());
        transaction.setCustomerQuotaId(customerQuotaId);
        transaction.setTransactionType(transactionType);
        transaction.setAmount(request.getAmount());
        transaction.setBeforeBalance(beforeBalance);
        transaction.setAfterBalance(afterBalance);
        transaction.setStatus(1); // 1-成功
        transaction.setReferenceId(request.getReferenceId());
        transaction.setRemarks(request.getRemarks());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setProcessedAt(LocalDateTime.now());

        quotaTransactionMapper.insert(transaction);
    }
}