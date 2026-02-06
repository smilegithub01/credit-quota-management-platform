import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/customer',
    name: 'customer',
    component: () => import('../views/CustomerView.vue')
  },
  {
    path: '/quota',
    name: 'quota',
    component: () => import('../views/QuotaView.vue')
  },
  {
    path: '/risk',
    name: 'risk',
    component: () => import('../views/RiskView.vue')
  },
  {
    path: '/approval',
    name: 'approval',
    component: () => import('../views/ApprovalView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router