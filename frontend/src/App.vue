<template>
  <div id="app">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <h1 class="logo">银行信贷额度管控平台</h1>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleUserCommand">
            <span class="el-dropdown-link">
              {{ userInfo.username }}
              <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-container>
        <!-- 侧边栏导航 -->
        <el-aside width="200px" class="sidebar">
          <el-menu
            :default-active="$route.path"
            :router="true"
            class="menu"
            :collapse="isCollapse"
          >
            <el-sub-menu index="1">
              <template #title>
                <el-icon><user /></el-icon>
                <span>客户管理</span>
              </template>
              <el-menu-item index="/customer/list">客户列表</el-menu-item>
              <el-menu-item index="/customer/group">集团关系</el-menu-item>
              <el-menu-item index="/customer/affiliate">关联方管理</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="2">
              <template #title>
                <el-icon><coin /></el-icon>
                <span>额度管理</span>
              </template>
              <el-menu-item index="/quota/list">额度查询</el-menu-item>
              <el-menu-item index="/quota/application">额度申请</el-menu-item>
              <el-menu-item index="/quota/usage">额度使用</el-menu-item>
              <el-menu-item index="/quota/adjustment">额度调整</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="3">
              <template #title>
                <el-icon><warning-filled /></el-icon>
                <span>风险管理</span>
              </template>
              <el-menu-item index="/risk/monitor">风险监控</el-menu-item>
              <el-menu-item index="/risk/warning">风险预警</el-menu-item>
              <el-menu-item index="/risk/index">风险指标</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="4">
              <template #title>
                <el-icon><document-checked /></el-icon>
                <span>审批流程</span>
              </template>
              <el-menu-item index="/approval/list">审批列表</el-menu-item>
              <el-menu-item index="/approval/pending">待审批</el-menu-item>
              <el-menu-item index="/approval/history">审批历史</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="5">
              <template #title>
                <el-icon><setting /></el-icon>
                <span>系统管理</span>
              </template>
              <el-menu-item index="/system/users">用户管理</el-menu-item>
              <el-menu-item index="/system/roles">角色管理</el-menu-item>
              <el-menu-item index="/system/config">系统配置</el-menu-item>
              <el-menu-item index="/system/logs">操作日志</el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-aside>

        <!-- 主内容区域 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const store = useStore()
    const isCollapse = ref(false)
    
    // 用户信息
    const userInfo = reactive({
      username: 'admin'
    })

    // 处理用户下拉菜单命令
    const handleUserCommand = (command) => {
      switch(command) {
        case 'profile':
          router.push('/profile')
          break
        case 'settings':
          router.push('/settings')
          break
        case 'logout':
          handleLogout()
          break
      }
    }

    // 登出处理
    const handleLogout = () => {
      // 清除用户信息和token
      store.commit('CLEAR_USER_INFO')
      router.push('/login')
    }

    // 页面加载完成后初始化
    onMounted(() => {
      // 获取用户信息
      // const user = store.state.user
      // if (!user || !user.token) {
      //   router.push('/login')
      // }
    })

    return {
      isCollapse,
      userInfo,
      handleUserCommand,
      handleLogout
    }
  }
}
</script>

<style lang="scss">
#app {
  height: 100vh;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #409EFF;
  color: white;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);

  .header-left {
    .logo {
      margin: 0;
      font-size: 1.5rem;
    }
  }

  .header-right {
    .el-dropdown-link {
      cursor: pointer;
      color: white;
      display: flex;
      align-items: center;
      
      &:hover {
        color: #ffd04b;
      }
    }
  }
}

.sidebar {
  background-color: #f5f5f5;
  border-right: 1px solid #dcdfe6;
}

.menu {
  border-right: none;
  height: 100%;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>