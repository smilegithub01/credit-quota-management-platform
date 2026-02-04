import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import store from '../store'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_API_URL || 'http://localhost:8080/api/unified-credit', // API的base_url
  timeout: 15000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    config.headers['Content-Type'] = 'application/json;charset=UTF-8'
    return config
  },
  error => {
    // 对请求错误做些什么
    console.error(error) // for debug
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data

    // 如果自定义code不是200，则判断为错误
    if (res.code !== 200) {
      ElMessage({
        message: res.message || 'Error',
        type: 'error',
        duration: 5 * 1000
      })

      // 50008: 非法的token; 50012: 其他客户端登录了;  50014: Token 过期了;
      if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
        // 重新登录
        ElMessageBox.confirm(
          '你已被登出，可以取消继续留在该页面，或者重新登录',
          '确定登出',
          {
            confirmButtonText: '重新登录',
            cancelButtonText: '取消',
            type: 'warning'
          }
        ).then(() => {
          store.dispatch('logout').then(() => {
            location.reload() // 为了重新实例化vue-router对象 避免bug
          })
        })
      }
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    // 响应错误处理
    console.error(error) // for debug
    let msg = '请求失败'
    
    if (error.response) {
      // 请求已发出，但服务器响应的状态码不在 2xx 范围内
      switch (error.response.status) {
        case 400:
          msg = '请求参数错误'
          break
        case 401:
          msg = '未授权，请重新登录'
          store.dispatch('logout')
          break
        case 403:
          msg = '拒绝访问'
          break
        case 404:
          msg = '请求地址出错'
          break
        case 408:
          msg = '请求超时'
          break
        case 500:
          msg = '服务器内部错误'
          break
        case 501:
          msg = '服务未实现'
          break
        case 502:
          msg = '网关错误'
          break
        case 503:
          msg = '服务不可用'
          break
        case 504:
          msg = '网关超时'
          break
        default:
          msg = `连接错误${error.response.status}`
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      msg = '网络异常，请检查网络连接'
    } else {
      // 其他错误
      msg = error.message || '未知错误'
    }
    
    ElMessage({
      message: msg,
      type: 'error',
      duration: 5 * 1000
    })
    
    return Promise.reject(error)
  }
)

export default service