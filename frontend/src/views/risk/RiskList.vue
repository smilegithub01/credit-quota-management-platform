<template>
  <div class="risk-management-container">
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
            <el-form-item :label="$t('message.risk.riskLevel')">
              <el-select 
                v-model="searchForm.riskLevel" 
                :placeholder="$t('message.common.pleaseSelect') + $t('message.risk.riskLevel')"
                clearable
              >
                <el-option 
                  v-for="item in riskLevelOptions" 
                  :key="item.value" 
                  :label="item.label" 
                  :value="item.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('message.risk.riskType')">
              <el-input 
                v-model="searchForm.riskType" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.risk.riskType')" 
              />
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
      <el-button type="primary" @click="handleCreateWarning">
        <el-icon><Plus /></el-icon>
        {{ $t('message.risk.createWarning') }}
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        {{ $t('message.common.export') }}
      </el-button>
    </div>

    <div class="table-area">
      <el-table 
        :data="riskList" 
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="warningId" :label="$t('message.risk.warningId')" width="120" />
        <el-table-column prop="customerId" :label="$t('message.risk.customer')" width="120" />
        <el-table-column prop="customerName" :label="$t('message.risk.customerName')" width="120" />
        <el-table-column prop="riskType" :label="$t('message.risk.riskType')" width="120" />
        <el-table-column prop="riskLevel" :label="$t('message.risk.riskLevel')" width="120">
          <template #default="{ row }">
            <el-tag :type="getRiskLevelTagType(row.riskLevel)">
              {{ getRiskLevelLabel(row.riskLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warningContent" :label="$t('message.risk.warningContent')" width="200" show-overflow-tooltip />
        <el-table-column prop="warningTime" :label="$t('message.risk.warningTime')" width="150" />
        <el-table-column prop="status" :label="$t('message.risk.status')" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('message.common.operate')" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">{{ $t('message.common.view') }}</el-button>
            <el-button size="small" type="primary" @click="handleHandle(row)" :disabled="row.status !== 'PENDING'">{{ $t('message.risk.handle') }}</el-button>
            <el-button size="small" @click="handleFollowUp(row)" :disabled="row.status === 'PENDING'">{{ $t('message.risk.followUp') }}</el-button>
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

    <!-- 风险详情对话框 -->
    <el-dialog v-model="detailDialogVisible" :title="$t('message.risk.detail')" width="60%">
      <div v-if="currentRisk" class="risk-detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item :label="$t('message.risk.warningId')">
            {{ currentRisk.warningId }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.risk.customer')">
            {{ currentRisk.customerName }} ({{ currentRisk.customerId }})
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.risk.riskType')">
            {{ currentRisk.riskType }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.risk.riskLevel')">
            <el-tag :type="getRiskLevelTagType(currentRisk.riskLevel)">
              {{ getRiskLevelLabel(currentRisk.riskLevel) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.risk.warningContent')" :span="2">
            {{ currentRisk.warningContent }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.risk.warningTime')">
            {{ currentRisk.warningTime }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.risk.status')">
            <el-tag :type="getStatusTagType(currentRisk.status)">
              {{ getStatusLabel(currentRisk.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.risk.handleResult')" :span="2">
            {{ currentRisk.handleResult || $t('message.risk.noHandleResult') }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.risk.handleTime')" :span="2">
            {{ currentRisk.handleTime || $t('message.risk.notHandled') }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">{{ $t('message.common.close') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRiskWarningList } from '@/api/risk'

export default {
  name: 'RiskManagement',
  setup() {
    // 搜索表单
    const searchForm = reactive({
      customerId: '',
      riskLevel: '',
      riskType: ''
    })
    
    // 风险列表
    const riskList = ref([])
    
    // 加载状态
    const loading = ref(false)
    
    // 分页信息
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })
    
    // 当前选中的风险
    const currentRisk = ref(null)
    
    // 详情对话框可见性
    const detailDialogVisible = ref(false)
    
    // 风险等级选项
    const riskLevelOptions = [
      { value: 'LOW', label: '低风险' },
      { value: 'MEDIUM', label: '中风险' },
      { value: 'HIGH', label: '高风险' },
      { value: 'CRITICAL', label: '危急风险' }
    ]
    
    // 风险状态选项
    const statusOptions = [
      { value: 'PENDING', label: '待处理' },
      { value: 'HANDLED', label: '已处理' },
      { value: 'IGNORED', label: '已忽略' },
      { value: 'RESOLVED', label: '已解决' }
    ]
    
    // 获取风险列表
    const getRiskListData = async () => {
      loading.value = true
      try {
        const params = {
          ...searchForm,
          pageNum: pagination.currentPage,
          pageSize: pagination.pageSize
        }
        const response = await getRiskWarningList(params)
        riskList.value = response.data.list || []
        pagination.total = response.data.total || 0
      } catch (error) {
        ElMessage.error('获取风险列表失败: ' + error.message)
      } finally {
        loading.value = false
      }
    }
    
    // 搜索处理
    const handleSearch = () => {
      pagination.currentPage = 1
      getRiskListData()
    }
    
    // 重置搜索
    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      pagination.currentPage = 1
      getRiskListData()
    }
    
    // 查看风险详情
    const handleView = (row) => {
      currentRisk.value = row
      detailDialogVisible.value = true
    }
    
    // 处理风险
    const handleHandle = (row) => {
      ElMessageBox.confirm(
        `${$t('message.risk.confirmHandle')} "${row.warningContent}"?`,
        $t('message.risk.handleWarning'),
        {
          confirmButtonText: $t('message.common.confirm'),
          cancelButtonText: $t('message.common.cancel'),
          type: 'warning'
        }
      ).then(() => {
        ElMessage.success($t('message.risk.handleSuccess'))
        // 实际应用中这里会调用API更新风险状态
        getRiskListData() // 刷新列表
      }).catch(() => {
        // 取消操作
      })
    }
    
    // 跟踪处理
    const handleFollowUp = (row) => {
      ElMessage.info(`${$t('message.risk.followUp')} ${row.warningId}`)
    }
    
    // 创建预警
    const handleCreateWarning = () => {
      ElMessage.success($t('message.risk.createWarningSuccess'))
    }
    
    // 导出数据
    const handleExport = () => {
      ElMessage.success($t('message.common.exportSuccess'))
    }
    
    // 分页大小变化
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.currentPage = 1
      getRiskListData()
    }
    
    // 当前页变化
    const handleCurrentChange = (page) => {
      pagination.currentPage = page
      getRiskListData()
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
      const option = riskLevelOptions.find(item => item.value === level)
      return option ? option.label : level
    }
    
    // 获取状态标签类型
    const getStatusTagType = (status) => {
      switch (status) {
        case 'PENDING': return 'warning'
        case 'HANDLED': return 'info'
        case 'IGNORED': return 'info'
        case 'RESOLVED': return 'success'
        default: return 'info'
      }
    }
    
    // 获取状态标签
    const getStatusLabel = (status) => {
      const option = statusOptions.find(item => item.value === status)
      return option ? option.label : status
    }
    
    // 初始化数据
    onMounted(() => {
      getRiskListData()
    })
    
    return {
      searchForm,
      riskList,
      loading,
      pagination,
      currentRisk,
      detailDialogVisible,
      riskLevelOptions,
      statusOptions,
      handleSearch,
      handleReset,
      handleView,
      handleHandle,
      handleFollowUp,
      handleCreateWarning,
      handleExport,
      handleSizeChange,
      handleCurrentChange,
      getRiskLevelTagType,
      getRiskLevelLabel,
      getStatusTagType,
      getStatusLabel
    }
  }
}
</script>

<style lang="scss" scoped>
.risk-management-container {
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

  .risk-detail-content {
    .el-descriptions {
      margin-top: 10px;
    }
  }
}
</style>