import request from '@/utils/request'

// 系统管理相关API

// 用户管理API

// 获取用户列表
export function getUserList(params) {
  return request({
    url: '/system/users',
    method: 'get',
    params
  })
}

// 获取单个用户信息
export function getUserById(userId) {
  return request({
    url: `/system/users/${userId}`,
    method: 'get'
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/system/users',
    method: 'post',
    data
  })
}

// 更新用户信息
export function updateUser(userId, data) {
  return request({
    url: `/system/users/${userId}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(userId) {
  return request({
    url: `/system/users/${userId}`,
    method: 'delete'
  })
}

// 角色管理API

// 获取角色列表
export function getRoleList(params) {
  return request({
    url: '/system/roles',
    method: 'get',
    params
  })
}

// 获取单个角色信息
export function getRoleById(roleId) {
  return request({
    url: `/system/roles/${roleId}`,
    method: 'get'
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/system/roles',
    method: 'post',
    data
  })
}

// 更新角色信息
export function updateRole(roleId, data) {
  return request({
    url: `/system/roles/${roleId}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(roleId) {
  return request({
    url: `/system/roles/${roleId}`,
    method: 'delete'
  })
}

// 系统配置API

// 获取系统参数配置
export function getSystemConfig(params) {
  return request({
    url: '/system/config',
    method: 'get',
    params
  })
}

// 更新系统参数配置
export function updateSystemConfig(paramId, data) {
  return request({
    url: `/system/config/${paramId}`,
    method: 'put',
    data
  })
}

// 创建系统参数配置
export function createSystemConfig(data) {
  return request({
    url: '/system/config',
    method: 'post',
    data
  })
}

// 删除系统参数配置
export function deleteSystemConfig(paramId) {
  return request({
    url: `/system/config/${paramId}`,
    method: 'delete'
  })
}

// 操作日志API

// 获取操作日志列表
export function getOperationLogList(params) {
  return request({
    url: '/system/logs',
    method: 'get',
    params
  })
}

// 获取操作日志详情
export function getOperationLogById(logId) {
  return request({
    url: `/system/logs/${logId}`,
    method: 'get'
  })
}

// 清空操作日志
export function clearOperationLogs() {
  return request({
    url: '/system/logs/clear',
    method: 'delete'
  })
}

// 登录相关API

// 用户登录
export function login(credentials) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: credentials
  })
}

// 用户登出
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

// 获取当前用户信息
export function getCurrentUserInfo() {
  return request({
    url: '/auth/me',
    method: 'get'
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/auth/password',
    method: 'put',
    data
  })
}