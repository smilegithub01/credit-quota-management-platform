<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ customerCount }}</div>
            <div class="stat-label">客户总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ quotaTotal }}</div>
            <div class="stat-label">总额度(万元)</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ riskCount }}</div>
            <div class="stat-label">风险预警</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ approvalCount }}</div>
            <div class="stat-label">待审批</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <el-button-group>
            <el-button type="primary" @click="$router.push('/customer')">客户管理</el-button>
            <el-button type="success" @click="$router.push('/quota')">额度管理</el-button>
            <el-button type="warning" @click="$router.push('/risk')">风险监控</el-button>
            <el-button type="info" @click="$router.push('/approval')">审批流程</el-button>
          </el-button-group>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统状态</span>
            </div>
          </template>
          <div class="status-info">
            <p>系统运行正常</p>
            <p>API服务: <el-tag type="success">正常</el-tag></p>
            <p>数据库: <el-tag type="success">正常</el-tag></p>
            <p>Redis: <el-tag type="success">正常</el-tag></p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from 'axios'

export default {
  name: 'HomeView',
  setup() {
    const customerCount = ref(0)
    const quotaTotal = ref(0)
    const riskCount = ref(0)
    const approvalCount = ref(0)

    const fetchData = async () => {
      try {
        // 这里可以调用API获取统计数据
        // const response = await axios.get('/api/unified-credit/dashboard')
        // customerCount.value = response.data.customerCount
        // quotaTotal.value = response.data.quotaTotal
        // riskCount.value = response.data.riskCount
        // approvalCount.value = response.data.approvalCount
        
        // 模拟数据
        customerCount.value = 156
        quotaTotal.value = 12500
        riskCount.value = 3
        approvalCount.value = 7
      } catch (error) {
        console.error('获取数据失败:', error)
      }
    }

    onMounted(() => {
      fetchData()
    })

    return {
      customerCount,
      quotaTotal,
      riskCount,
      approvalCount
    }
  }
}
</script>

<style scoped>
.stat-card {
  text-align: center;
}

.stat-content {
  padding: 20px 0;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.card-header {
  font-weight: bold;
}

.status-info p {
  margin: 8px 0;
}
</style>