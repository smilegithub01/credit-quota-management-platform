import request from '@/utils/request'

// 风险管理相关API

// 获取风险监控指标
export function getRiskMonitoringIndex(customerId) {
  return request({
    url: `/risk-monitoring-index/customer/${customerId}`,
    method: 'get'
  })
}

// 检查客户风险指标
export function checkRiskIndex(customerId) {
  return request({
    url: `/risk-monitoring-index/customer/${customerId}/check`,
    method: 'get'
  })
}

// 创建风险监控指标
export function createRiskMonitoringIndex(data) {
  return request({
    url: '/risk-monitoring-index',
    method: 'post',
    data
  })
}

// 更新风险监控指标
export function updateRiskMonitoringIndex(data) {
  return request({
    url: '/risk-monitoring-index',
    method: 'put',
    data
  })
}

// 获取风险预警列表
export function getRiskWarningList(params) {
  return request({
    url: '/risk-warning',
    method: 'get',
    params
  })
}

// 获取客户风险预警
export function getRiskWarningByCustomer(customerId) {
  return request({
    url: `/risk-warning/customer/${customerId}`,
    method: 'get'
  })
}

// 创建风险预警
export function createRiskWarning(data) {
  return request({
    url: '/risk-warning',
    method: 'post',
    data
  })
}

// 更新风险预警
export function updateRiskWarning(data) {
  return request({
    url: '/risk-warning',
    method: 'put',
    data
  })
}

// 处理风险预警
export function handleRiskWarning(warningId, data) {
  return request({
    url: `/risk-warning/${warningId}/handle`,
    method: 'put',
    data
  })
}

// 获取风险指标类型列表
export function getRiskIndexTypes() {
  return request({
    url: '/risk-monitoring-index/types',
    method: 'get'
  })
}

// 获取风险预警类型列表
export function getRiskWarningTypes() {
  return request({
    url: '/risk-warning/types',
    method: 'get'
  })
}