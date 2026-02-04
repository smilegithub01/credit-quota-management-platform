import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import { createStore } from 'vuex'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import routes from './router'
import store from './store'
import i18n from './i18n'
import 'normalize.css'
import './styles/index.scss'

// 创建Vue应用
const app = createApp(App)

// 注册所有Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 创建路由器
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 使用插件
app.use(router)
app.use(store)
app.use(i18n)
app.use(ElementPlus)

// 挂载应用
app.mount('#app')