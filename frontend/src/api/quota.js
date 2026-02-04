import request from '@/utils/request'

// 额度管理相关API

// 获取额度列表
export function getQuotaList(params) {
  return request({
    url: '/credit-quota',
    method: 'get',
    params
  })
}

// 根据客户ID获取额度
export function getQuotaByCustomer(customerId) {
  return request({
    url: `/credit-quota/customer/${customerId}`,
    method: 'get'
  })
}

// 获取单个额度信息
export function getQuotaById(quotaId) {
  return request({
    url: `/credit-quota/${quotaId}`,
    method: 'get'
  })
}

// 创建额度
export function createQuota(data) {
  return request({
    url: '/credit-quota',
    method: 'post',
    data
  })
}

// 更新额度
export function updateQuota(data) {
  return request({
    url: '/credit-quota',
    method: 'put',
    data
  })
}

// 检查额度是否充足
export function checkQuota(params) {
  return request({
    url: '/credit-quota/check',
    method: 'post',
    params
  })
}

// 占用额度
export function occupyQuota(data) {
  return request({
    url: '/credit-quota/occupy',
    method: 'post',
    data
  })
}

// 释放额度
export function releaseQuota(data) {
  return request({
    url: '/credit-quota/release',
    method: 'post',
    data
  })
}

// 冻结额度
export function freezeQuota(data) {
  return request({
    url: '/credit-quota/freeze',
    method: 'post',
    data
  })
}

// 解冻额度
export function unfreezeQuota(data) {
  return request({
    url: '/credit-quota/unfreeze',
    method: 'post',
    data
  })
}

// 启用额度
export function enableQuota(data) {
  return request({
    url: '/credit-quota/enable',
    method: 'post',
    data
  })
}

// 停用额度
export function disableQuota(data) {
  return request({
    url: '/credit-quota/disable',
    method: 'post',
    data
  })
}

// 额度调整
export function adjustQuota(data) {
  return request({
    url: '/credit-quota/adjust',
    method: 'post',
    data
  })
}

// 集团额度分配
export function distributeGroupQuota(data) {
  return request({
    url: '/credit-quota/distribute-group',
    method: 'post',
    data
  })
}

// 额度预占
export function preOccupyQuota(data) {
  return request({
    url: '/credit-quota/pre-occupy',
    method: 'post',
    data
  })
}

// 取消预占
export function cancelPreOccupyQuota(data) {
  return request({
    url: '/credit-quota/cancel-pre-occupy',
    method: 'post',
    data
  })
}