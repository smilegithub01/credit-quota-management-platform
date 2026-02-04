import { createRouter, createWebHistory } from 'vue-router'

// 路由配置
const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/dashboard/Dashboard.vue'),
    meta: { title: '仪表板' }
  },
  // 客户管理路由
  {
    path: '/customer/list',
    name: 'CustomerList',
    component: () => import('../views/customer/CustomerList.vue'),
    meta: { title: '客户列表' }
  },
  {
    path: '/customer/group',
    name: 'GroupRelationship',
    component: () => import('../views/customer/GroupRelationship.vue'),
    meta: { title: '集团关系' }
  },
  {
    path: '/customer/affiliate',
    name: 'CustomerAffiliate',
    component: () => import('../views/customer/CustomerAffiliate.vue'),
    meta: { title: '关联方管理' }
  },
  {
    path: '/customer/create',
    name: 'CreateCustomer',
    component: () => import('../views/customer/CreateCustomer.vue'),
    meta: { title: '新增客户' }
  },
  {
    path: '/customer/:id/edit',
    name: 'EditCustomer',
    component: () => import('../views/customer/EditCustomer.vue'),
    meta: { title: '编辑客户' },
    props: true
  },
  // 额度管理路由
  {
    path: '/quota/list',
    name: 'QuotaList',
    component: () => import('../views/quota/QuotaList.vue'),
    meta: { title: '额度查询' }
  },
  {
    path: '/quota/application',
    name: 'QuotaApplication',
    component: () => import('../views/quota/QuotaApplication.vue'),
    meta: { title: '额度申请' }
  },
  {
    path: '/quota/usage',
    name: 'QuotaUsage',
    component: () => import('../views/quota/QuotaUsage.vue'),
    meta: { title: '额度使用' }
  },
  {
    path: '/quota/adjustment',
    name: 'QuotaAdjustment',
    component: () => import('../views/quota/QuotaAdjustment.vue'),
    meta: { title: '额度调整' }
  },
  // 风险管理路由
  {
    path: '/risk/monitor',
    name: 'RiskMonitor',
    component: () => import('../views/risk/RiskMonitor.vue'),
    meta: { title: '风险监控' }
  },
  {
    path: '/risk/warning',
    name: 'RiskWarning',
    component: () => import('../views/risk/RiskWarning.vue'),
    meta: { title: '风险预警' }
  },
  {
    path: '/risk/index',
    name: 'RiskIndex',
    component: () => import('../views/risk/RiskIndex.vue'),
    meta: { title: '风险指标' }
  },
  // 审批流程路由
  {
    path: '/approval/list',
    name: 'ApprovalList',
    component: () => import('../views/approval/ApprovalList.vue'),
    meta: { title: '审批列表' }
  },
  {
    path: '/approval/pending',
    name: 'PendingApprovals',
    component: () => import('../views/approval/PendingApprovals.vue'),
    meta: { title: '待审批' }
  },
  {
    path: '/approval/history',
    name: 'ApprovalHistory',
    component: () => import('../views/approval/ApprovalHistory.vue'),
    meta: { title: '审批历史' }
  },
  // 系统管理路由
  {
    path: '/system/users',
    name: 'UserManagement',
    component: () => import('../views/system/UserManagement.vue'),
    meta: { title: '用户管理' }
  },
  {
    path: '/system/roles',
    name: 'RoleManagement',
    component: () => import('../views/system/RoleManagement.vue'),
    meta: { title: '角色管理' }
  },
  {
    path: '/system/config',
    name: 'SystemConfig',
    component: () => import('../views/system/SystemConfig.vue'),
    meta: { title: '系统配置' }
  },
  {
    path: '/system/logs',
    name: 'OperationLogs',
    component: () => import('../views/system/OperationLogs.vue'),
    meta: { title: '操作日志' }
  },
  // 其他页面
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/profile',
    name: 'UserProfile',
    component: () => import('../views/UserProfile.vue'),
    meta: { title: '个人资料' }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('../views/Settings.vue'),
    meta: { title: '系统设置' }
  }
]

// 创建路由器实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 更新页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 银行信贷额度管控平台`
  } else {
    document.title = '银行信贷额度管控平台'
  }
  
  // 检查登录状态
  const token = localStorage.getItem('token')
  if (!token && to.name !== 'Login') {
    next({ name: 'Login' })
  } else if (token && to.name === 'Login') {
    next({ name: 'Dashboard' })
  } else {
    next()
  }
})

export default routes