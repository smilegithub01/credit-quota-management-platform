import request from '@/utils/request'

// 审批流程相关API

// 获取审批流程列表
export function getApprovalProcessList(params) {
  return request({
    url: '/approval-process',
    method: 'get',
    params
  })
}

// 启动审批流程
export function startApprovalProcess(data) {
  return request({
    url: '/approval-process',
    method: 'post',
    data
  })
}

// 获取审批流程详情
export function getApprovalProcessById(processId) {
  return request({
    url: `/approval-process/${processId}`,
    method: 'get'
  })
}

// 更新审批流程
export function updateApprovalProcess(data) {
  return request({
    url: '/approval-process',
    method: 'put',
    data
  })
}

// 获取审批节点列表
export function getApprovalNodeList(processId) {
  return request({
    url: `/approval-node/process/${processId}`,
    method: 'get'
  })
}

// 完成审批节点
export function completeApprovalNode(nodeId, data) {
  return request({
    url: `/approval-node/${nodeId}/complete`,
    method: 'put',
    data
  })
}

// 获取待审批列表
export function getPendingApprovals(userId) {
  return request({
    url: `/approval-process/pending/${userId}`,
    method: 'get'
  })
}

// 获取审批历史
export function getApprovalHistory(params) {
  return request({
    url: '/approval-process/history',
    method: 'get',
    params
  })
}

// 转办审批
export function transferApproval(nodeId, data) {
  return request({
    url: `/approval-node/${nodeId}/transfer`,
    method: 'put',
    data
  })
}

// 获取审批流程类型
export function getApprovalProcessTypes() {
  return request({
    url: '/approval-process/types',
    method: 'get'
  })
}