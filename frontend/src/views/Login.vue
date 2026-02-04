<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="login-header">
        <h2>{{ $t('message.login.title') }}</h2>
      </div>
      
      <el-form 
        ref="loginFormRef"
        :model="loginForm" 
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            :placeholder="$t('message.login.username')"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            :placeholder="$t('message.login.password')"
            size="large"
            type="password"
            show-password
            prefix-icon="Lock"
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="loginForm.remember">{{ $t('message.login.remember') }}</el-checkbox>
          <el-link type="primary" href="#" class="forgot-password">
            {{ $t('message.login.forgotPassword') }}
          </el-link>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-button"
          >
            {{ $t('message.login.loginButton') }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <el-link href="#">{{ $t('message.login.register') }}</el-link>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const store = useStore()
    
    // 登录表单数据
    const loginForm = reactive({
      username: '',
      password: '',
      remember: false
    })
    
    // 表单验证规则
    const loginRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '长度在3到20个字符', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
      ]
    }
    
    // 加载状态
    const loading = ref(false)
    const loginFormRef = ref(null)
    
    // 处理登录
    const handleLogin = async () => {
      // 验证表单
      if (!loginFormRef.value) return
      
      const valid = await loginFormRef.value.validate().catch(() => false)
      if (!valid) return
      
      // 调用登录方法
      loading.value = true
      try {
        const result = await store.dispatch('login', {
          username: loginForm.username,
          password: loginForm.password
        })
        
        if (result.success) {
          ElMessage.success('登录成功')
          
          // 记住密码逻辑
          if (loginForm.remember) {
            localStorage.setItem('username', loginForm.username)
          }
          
          // 跳转到首页
          router.push({ path: '/' })
        } else {
          ElMessage.error(result.error || '登录失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
    
    return {
      loginForm,
      loginRules,
      loading,
      loginFormRef,
      handleLogin
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #74b9ff, #0984e3);
  
  .login-card {
    width: 400px;
    padding: 20px;
    
    .login-header {
      text-align: center;
      margin-bottom: 30px;
      
      h2 {
        margin: 0;
        color: #333;
      }
    }
    
    .login-form {
      :deep(.el-form-item__content) {
        display: block;
      }
      
      .forgot-password {
        float: right;
        margin-top: 5px;
      }
      
      .login-button {
        width: 100%;
        height: 45px;
        font-size: 16px;
      }
    }
    
    .login-footer {
      text-align: center;
      margin-top: 20px;
    }
  }
}

@media (max-width: 480px) {
  .login-container {
    .login-card {
      width: 90%;
      margin: 0 20px;
    }
  }
}
</style>