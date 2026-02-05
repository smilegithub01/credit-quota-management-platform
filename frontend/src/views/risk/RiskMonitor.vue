<template>
  <div class="risk-monitor-container">
    <div class="search-area">
      <el-form :model="searchForm" inline label-width="100px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item :label="$t('message.risk.customer')">
              <el-input 
                v-model="searchForm.customerId" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.risk.customer')" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('message.risk.indexType')">
              <el-select 
                v-model="searchForm.indexType" 
                :placeholder="$t('message.common.pleaseSelect') + $t('message.risk.indexType')"
                clearable
              >
                <el-option 
                  v-for="item in indexTypeOptions" 
                  :key="item.value" 
                  :label="item.label" 
                  :value="item.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item>
              <el-button type="primary" @click="handleSearch">{{ $t('message.common.search') }}</el-button>
              <el-button @click="handleReset">{{ $t('message.common.reset') }}</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="top-operation">
      <el-button type="primary" @click="handleCreateIndex">
        <el-icon><Plus /></el-icon>
        {{ $t('message.risk.createIndex') }}
      </el-button>
      <el-button @click="handleCheckAll">
        <el-icon><Refresh /></el-icon>
        {{ $t('message.risk.checkAll') }}
      </el-button>
    </div>

    <div class="dashboard-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon low-risk">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-number">{{ summary.lowRiskCount }}</div>
                <div class="card-label">{{ $t('message.risk.lowRisk') }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon medium-risk">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-number">{{ summary.mediumRiskCount }}</div>
                <div class="card-label">{{ $t('message.risk.mediumRisk') }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon high-risk">
                <el-icon><CircleClose /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-number">{{ summary.highRiskCount }}</div>
                <div class="card-label">{{ $t('message.risk.highRisk') }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="dashboard-card">
            <div class="card-content">
              <div class="card-icon critical-risk">
                <el-icon><CircleCloseFilled /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-number">{{ summary.criticalRiskCount }}</div>
                <div class="card-label">{{ $t('message.risk.criticalRisk') }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="table-area">
      <el-table 
        :data="monitorList" 
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="indexId" :label="$t('message.risk.indexId')" width="120" />
        <el-table-column prop="customerId" :label="$t('message.risk.customer')" width="120" />
        <el-table-column prop="customerName" :label="$t('message.risk.customerName')" width="120" />
        <el-table-column prop="indexType" :label="$t('message.risk.indexType')" width="150">
          <template #default="{ row }">
            {{ getIndexTypeLabel(row.indexType) }}
          </template>
        </el-table-column>
        <el-table-column prop="indexName" :label="$t('message.risk.indexName')" width="150" />
        <el-table-column prop="currentValue" :label="$t('message.risk.currentValue')" width="120" />
        <el-table-column prop="threshold" :label="$t('message.risk.threshold')" width="120" />
        <el-table-column prop="riskLevel" :label="$t('message.risk.riskLevel')" width="120">
          <template #default="{ row }">
            <el-tag :type="getRiskLevelTagType(row.riskLevel)">
              {{ getRiskLevelLabel(row.riskLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastCheckTime" :label="$t('message.risk.lastCheckTime')" width="150" />
        <el-table-column :label="$t('message.common.operate')" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">{{ $t('message.common.view') }}</el-button>
            <el-button size="small" type="primary" @click="handleCheck(row)">{{ $t('message.risk.check') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-area">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRiskMonitoringIndex, checkRiskIndex } from '@/api/risk'

export default {
  name: 'RiskMonitor',
  setup() {
    // 搜索表单
    const searchForm = reactive({
      customerId: '',
      indexType: ''
    })
    
    // 监控列表
    const monitorList = ref([])
    
    // 加载状态
    const loading = ref(false)
    
    // 分页信息
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })
    
    // 汇总信息
    const summary = reactive({
      lowRiskCount: 0,
      mediumRiskCount: 0,
      highRiskCount: 0,
      criticalRiskCount: 0
    })
    
    // 指标类型选项
    const indexTypeOptions = [
      { value: 'LOAN_TO_VALUE', label: '贷款价值比' },
      { value: 'DEBT_TO_ASSET_RATIO', label: '资产负债率' },
      { value: 'CURRENT_RATIO', label: '流动比率' },
      { value: 'QUICK_RATIO', label: '速动比率' },
      { value: 'INTEREST_COVERAGE_RATIO', label: '利息保障倍数' },
      { value: 'DEBT_SERVICE_RATIO', label: '债务偿付比率' }
    ]
    
    // 获取监控列表
    const getRiskMonitorData = async () => {
      loading.value = true
      try {
        const params = {
          ...searchForm,
          pageNum: pagination.currentPage,
          pageSize: pagination.pageSize
        }
        const response = await getRiskMonitoringIndex(params)
        monitorList.value = response.data.list || []
        pagination.total = response.data.total || 0
        
        // 更新汇总信息
        updateSummary(monitorList.value)
      } catch (error) {
        ElMessage.error('获取风险监控列表失败: ' + error.message)
      } finally {
        loading.value = false
      }
    }
    
    // 更新汇总信息
    const updateSummary = (list) => {
      summary.lowRiskCount = list.filter(item => item.riskLevel === 'LOW').length
      summary.mediumRiskCount = list.filter(item => item.riskLevel === 'MEDIUM').length
      summary.highRiskCount = list.filter(item => item.riskLevel === 'HIGH').length
      summary.criticalRiskCount = list.filter(item => item.riskLevel === 'CRITICAL').length
    }
    
    // 搜索处理
    const handleSearch = () => {
      pagination.currentPage = 1
      getRiskMonitorData()
    }
    
    // 重置搜索
    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      pagination.currentPage = 1
      getRiskMonitorData()
    }
    
    // 查看详情
    const handleView = (row) => {
      ElMessage.info(`${$t('message.risk.viewDetail')} ${row.indexId}`)
    }
    
    // 检查指标
    const handleCheck = async (row) => {
      try {
        await checkRiskIndex(row.customerId)
        ElMessage.success($t('message.risk.checkSuccess'))
        getRiskMonitorData() // 刷新列表
      } catch (error) {
        ElMessage.error('检查失败: ' + error.message)
      }
    }
    
    // 检查所有
    const handleCheckAll = async () => {
      try {
        // 这里通常会调用批量检查接口
        ElMessage.success($t('message.risk.checkAllSuccess'))
        getRiskMonitorData() // 刷新列表
      } catch (error) {
        ElMessage.error('批量检查失败: ' + error.message)
      }
    }
    
    // 创建指标
    const handleCreateIndex = () => {
      ElMessage.success($t('message.risk.createIndexSuccess'))
    }
    
    // 分页大小变化
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.currentPage = 1
      getRiskMonitorData()
    }
    
    // 当前页变化
    const handleCurrentChange = (page) => {
      pagination.currentPage = page
      getRiskMonitorData()
    }
    
    // 获取指标类型标签
    const getIndexTypeLabel = (type) => {
      const option = indexTypeOptions.find(item => item.value === type)
      return option ? option.label : type
    }
    
    // 获取风险等级标签类型
    const getRiskLevelTagType = (level) => {
      switch (level) {
        case 'LOW': return 'success'
        case 'MEDIUM': return 'warning'
        case 'HIGH': return 'danger'
        case 'CRITICAL': return 'info'
        default: return 'info'
      }
    }
    
    // 获取风险等级标签
    const getRiskLevelLabel = (level) => {
      switch (level) {
        case 'LOW': return '低风险'
        case 'MEDIUM': return '中风险'
        case 'HIGH': return '高风险'
        case 'CRITICAL': return '危急风险'
        default: return level
      }
    }
    
    // 初始化数据
    onMounted(() => {
      getRiskMonitorData()
    })
    
    return {
      searchForm,
      monitorList,
      loading,
      pagination,
      summary,
      indexTypeOptions,
      handleSearch,
      handleReset,
      handleView,
      handleCheck,
      handleCheckAll,
      handleCreateIndex,
      handleSizeChange,
      handleCurrentChange,
      getIndexTypeLabel,
      getRiskLevelTagType,
      getRiskLevelLabel
    }
  }
}
</script>

<style lang="scss" scoped>
.risk-monitor-container {
  .search-area {
    background: #fff;
    padding: 20px;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
  }

  .top-operation {
    margin-bottom: 20px;
  }

  .dashboard-cards {
    margin-bottom: 20px;
  }

  .dashboard-card {
    height: 100px;
    cursor: pointer;

    .card-content {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .card-icon {
        width: 50px;
        height: 50px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;

        &.low-risk {
          background-color: #e8f5e9;
          color: #4caf50;
        }

        &.medium-risk {
          background-color: #fff3e0;
          color: #ff9800;
        }

        &.high-risk {
          background-color: #ffebee;
          color: #f44336;
        }

        &.critical-risk {
          background-color: #f3e5f5;
          color: #9c27b0;
        }
      }

      .card-info {
        text-align: right;

        .card-number {
          font-size: 24px;
          font-weight: bold;
          color: #333;
        }

        .card-label {
          font-size: 14px;
          color: #666;
          margin-top: 4px;
        }
      }
    }
  }

  .table-area {
    background: #fff;
    padding: 20px;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }

  .pagination-area {
    margin-top: 20px;
    text-align: right;
  }
}
</style>