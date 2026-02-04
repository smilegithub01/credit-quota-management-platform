import request from '@/utils/request'

// 客户管理相关API

// 获取客户列表
export function getCustomerList(params) {
  return request({
    url: '/customer',
    method: 'get',
    params
  })
}

// 获取单个客户信息
export function getCustomerById(customerId) {
  return request({
    url: `/customer/${customerId}`,
    method: 'get'
  })
}

// 创建客户
export function createCustomer(data) {
  return request({
    url: '/customer',
    method: 'post',
    data
  })
}

// 更新客户信息
export function updateCustomer(data) {
  return request({
    url: '/customer',
    method: 'put',
    data
  })
}

// 删除客户
export function deleteCustomer(customerId) {
  return request({
    url: `/customer/${customerId}`,
    method: 'delete'
  })
}

// 获取集团关系列表
export function getGroupRelationshipList(parentCustomerId) {
  return request({
    url: `/group-relationship/parent/${parentCustomerId}`,
    method: 'get'
  })
}

// 获取客户关联方列表
export function getCustomerAffiliateList(customerId) {
  return request({
    url: `/customer-affiliate/customer/${customerId}`,
    method: 'get'
  })
}

// 创建集团关系
export function createGroupRelationship(data) {
  return request({
    url: '/group-relationship',
    method: 'post',
    data
  })
}

// 创建客户关联方
export function createCustomerAffiliate(data) {
  return request({
    url: '/customer-affiliate',
    method: 'post',
    data
  })
}