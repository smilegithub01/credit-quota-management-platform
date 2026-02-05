<template>
  <div class="approval-list-container">
    <div class="search-area">
      <el-form :model="searchForm" inline label-width="100px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item :label="$t('message.approval.applicant')">
              <el-input 
                v-model="searchForm.applicant" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.approval.applicant')" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('message.approval.processType')">
              <el-select 
                v-model="searchForm.processType" 
                :placeholder="$t('message.common.pleaseSelect') + $t('message.approval.processType')"
                clearable
              >
                <el-option 
                  v-for="item in processTypeOptions" 
                  :key="item.value" 
                  :label="item.label" 
                  :value="item.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('message.approval.status')">
              <el-select 
                v-model="searchForm.status" 
                :placeholder="$t('message.common.pleaseSelect') + $t('message.approval.status')"
                clearable
              >
                <el-option 
                  v-for="item in statusOptions" 
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
      <el-button type="primary" @click="handleStartProcess">
        <el-icon><Plus /></el-icon>
        {{ $t('message.approval.startProcess') }}
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        {{ $t('message.common.export') }}
      </el-button>
    </div>

    <div class="table-area">
      <el-table 
        :data="processList" 
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="processId" :label="$t('message.approval.processId')" width="120" />
        <el-table-column prop="processType" :label="$t('message.approval.processType')" width="120">
          <template #default="{ row }">
            {{ getProcessTypeLabel(row.processType) }}
          </template>
        </el-table-column>
        <el-table-column prop="applicant" :label="$t('message.approval.applicant')" width="120" />
        <el-table-column prop="applyTime" :label="$t('message.approval.applyTime')" width="150" />
        <el-table-column prop="currentNode" :label="$t('message.approval.currentNode')" width="150" />
        <el-table-column prop="progress" :label="$t('message.approval.progress')" width="120">
          <template #default="{ row }">
            <el-progress :percentage="row.progress" :stroke-width="16" :show-text="false" />
            <span>{{ row.progress }}%</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" :label="$t('message.approval.status')" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('message.common.operate')" width="250" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">{{ $t('message.common.view') }}</el-button>
            <el-button size="small" type="primary" @click="handleApprove(row)" :disabled="!canApprove(row)">{{ $t('message.approval.approve') }}</el-button>
            <el-button size="small" type="danger" @click="handleReject(row)" :disabled="!canApprove(row)">{{ $t('message.approval.reject') }}</el-button>
            <el-button size="small" @click="handleTrack(row)">{{ $t('message.approval.track') }}</el-button>
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

    <!-- 审批流程详情对话框 -->
    <el-dialog v-model="detailDialogVisible" :title="$t('message.approval.detail')" width="70%">
      <div v-if="currentProcess" class="process-detail-content">
        <el-steps :active="getCurrentStep()" finish-status="success" align-center>
          <el-step 
            v-for="(node, index) in currentProcess.nodes" 
            :key="index"
            :title="node.nodeName"
            :description="getNodeDescription(node)"
          />
        </el-steps>
        
        <el-divider>{{ $t('message.approval.processInfo') }}</el-divider>
        
        <el-descriptions :column="2" border>
          <el-descriptions-item :label="$t('message.approval.processId')">
            {{ currentProcess.processId }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.approval.processType')">
            {{ getProcessTypeLabel(currentProcess.processType) }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.approval.applicant')">
            {{ currentProcess.applicant }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.approval.applyTime')">
            {{ currentProcess.applyTime }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.approval.currentNode')">
            {{ currentProcess.currentNode }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.approval.status')">
            <el-tag :type="getStatusTagType(currentProcess.status)">
              {{ getStatusLabel(currentProcess.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.approval.description')" :span="2">
            {{ currentProcess.description || $t('message.approval.noDescription') }}
          </el-descriptions-item>
        </el-descriptions>
        
        <el-divider>{{ $t('message.approval.nodeDetails') }}</el-divider>
        
        <el-table :data="currentProcess.nodes" style="width: 100%" stripe>
          <el-table-column prop="nodeName" :label="$t('message.approval.nodeName')" width="150" />
          <el-table-column prop="handler" :label="$t('message.approval.handler')" width="120" />
          <el-table-column prop="status" :label="$t('message.approval.status')" width="120">
            <template #default="{ scope }">
              <el-tag :type="getNodeStatusTagType(scope.row.status)">
                {{ getNodeStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="handleTime" :label="$t('message.approval.handleTime')" width="150" />
          <el-table-column prop="result" :label="$t('message.approval.result')" width="120">
            <template #default="{ scope }">
              {{ scope.row.result || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="comment" :label="$t('message.approval.comment')" width="200" show-overflow-tooltip />
        </el-table>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">{{ $t('message.common.close') }}</el-button>
        <el-button 
          type="primary" 
          @click="handleCompleteCurrentNode" 
          :disabled="!canCompleteCurrentNode()"
        >
          {{ $t('message.approval.completeCurrentNode') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getApprovalProcessList } from '@/api/approval'

export default {
  name: 'ApprovalList',
  setup() {
    // 搜索表单
    const searchForm = reactive({
      applicant: '',
      processType: '',
      status: ''
    })
    
    // 流程列表
    const processList = ref([])
    
    // 加载状态
    const loading = ref(false)
    
    // 分页信息
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })
    
    // 当前选中的流程
    const currentProcess = ref(null)
    
    // 详情对话框可见性
    const detailDialogVisible = ref(false)
    
    // 流程类型选项
    const processTypeOptions = [
      { value: 'CREDIT_APPLICATION', label: '授信申请' },
      { value: 'USAGE_APPLICATION', label: '用信申请' },
      { value: 'QUOTA_ADJUSTMENT', label: '额度调整' },
      { value: 'RISK_HANDLING', label: '风险处理' }
    ]
    
    // 状态选项
    const statusOptions = [
      { value: 'PENDING', label: '待处理' },
      { value: 'IN_PROGRESS', label: '处理中' },
      { value: 'APPROVED', label: '已批准' },
      { value: 'REJECTED', label: '已拒绝' },
      { value: 'COMPLETED', label: '已完成' },
      { value: 'CANCELLED', label: '已取消' }
    ]
    
    // 节点状态选项
    const nodeStatusOptions = [
      { value: 'PENDING', label: '待处理' },
      { value: 'PROCESSING', label: '处理中' },
      { value: 'COMPLETED', label: '已完成' },
      { value: 'SKIPPED', label: '已跳过' }
    ]
    
    // 获取流程列表
    const getProcessListData = async () => {
      loading.value = true
      try {
        const params = {
          ...searchForm,
          pageNum: pagination.currentPage,
          pageSize: pagination.pageSize
        }
        const response = await getApprovalProcessList(params)
        processList.value = response.data.list || []
        pagination.total = response.data.total || 0
      } catch (error) {
        ElMessage.error('获取审批流程列表失败: ' + error.message)
      } finally {
        loading.value = false
      }
    }
    
    // 搜索处理
    const handleSearch = () => {
      pagination.currentPage = 1
      getProcessListData()
    }
    
    // 重置搜索
    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      pagination.currentPage = 1
      getProcessListData()
    }
    
    // 查看流程详情
    const handleView = (row) => {
      currentProcess.value = row
      detailDialogVisible.value = true
    }
    
    // 审批通过
    const handleApprove = (row) => {
      ElMessageBox.confirm(
        `${$t('message.approval.confirmApprove')} "${row.description}"?`,
        $t('message.approval.approveTitle'),
        {
          confirmButtonText: $t('message.common.confirm'),
          cancelButtonText: $t('message.common.cancel'),
          type: 'success'
        }
      ).then(() => {
        ElMessage.success($t('message.approval.approveSuccess'))
        // 实际应用中这里会调用API更新审批状态
        getProcessListData() // 刷新列表
      }).catch(() => {
        // 取消操作
      })
    }
    
    // 审批拒绝
    const handleReject = (row) => {
      ElMessageBox.confirm(
        `${$t('message.approval.confirmReject')} "${row.description}"?`,
        $t('message.approval.rejectTitle'),
        {
          confirmButtonText: $t('message.common.confirm'),
          cancelButtonText: $t('message.common.cancel'),
          type: 'error'
        }
      ).then(() => {
        ElMessage.error($t('message.approval.rejectSuccess'))
        // 实际应用中这里会调用API更新审批状态
        getProcessListData() // 刷新列表
      }).catch(() => {
        // 取消操作
      })
    }
    
    // 跟踪流程
    const handleTrack = (row) => {
      ElMessage.info(`${$t('message.approval.tracking')} ${row.processId}`)
    }
    
    // 启动新流程
    const handleStartProcess = () => {
      ElMessage.success($t('message.approval.startProcessSuccess'))
    }
    
    // 导出数据
    const handleExport = () => {
      ElMessage.success($t('message.common.exportSuccess'))
    }
    
    // 分页大小变化
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.currentPage = 1
      getProcessListData()
    }
    
    // 当前页变化
    const handleCurrentChange = (page) => {
      pagination.currentPage = page
      getProcessListData()
    }
    
    // 检查是否可以审批
    const canApprove = (row) => {
      return row.status === 'PENDING' || row.status === 'IN_PROGRESS'
    }
    
    // 获取当前步骤
    const getCurrentStep = () => {
      if (!currentProcess.value || !currentProcess.value.nodes) return 0
      
      let completedNodes = 0
      for (const node of currentProcess.value.nodes) {
        if (node.status === 'COMPLETED') {
          completedNodes++
        } else {
          break
        }
      }
      return completedNodes
    }
    
    // 获取节点描述
    const getNodeDescription = (node) => {
      if (node.handleTime) {
        return `${node.handler} (${node.handleTime})`
      }
      return node.handler || '-'
    }
    
    // 完成当前节点
    const handleCompleteCurrentNode = () => {
      if (!currentProcess.value) return
      
      ElMessageBox.confirm(
        `${$t('message.approval.confirmCompleteNode')} "${currentProcess.value.currentNode}"?`,
        $t('message.approval.completeNodeTitle'),
        {
          confirmButtonText: $t('message.common.confirm'),
          cancelButtonText: $t('message.common.cancel'),
          type: 'warning'
        }
      ).then(() => {
        ElMessage.success($t('message.approval.completeNodeSuccess'))
        detailDialogVisible.value = false
        getProcessListData() // 刷新列表
      }).catch(() => {
        // 取消操作
      })
    }
    
    // 检查是否可以完成当前节点
    const canCompleteCurrentNode = () => {
      if (!currentProcess.value) return false
      return currentProcess.value.status === 'IN_PROGRESS'
    }
    
    // 获取流程类型标签
    const getProcessTypeLabel = (type) => {
      const option = processTypeOptions.find(item => item.value === type)
      return option ? option.label : type
    }
    
    // 获取状态标签类型
    const getStatusTagType = (status) => {
      switch (status) {
        case 'PENDING': return 'warning'
        case 'IN_PROGRESS': return 'primary'
        case 'APPROVED': return 'success'
        case 'REJECTED': return 'danger'
        case 'COMPLETED': return 'success'
        case 'CANCELLED': return 'info'
        default: return 'info'
      }
    }
    
    // 获取状态标签
    const getStatusLabel = (status) => {
      const option = statusOptions.find(item => item.value === status)
      return option ? option.label : status
    }
    
    // 获取节点状态标签类型
    const getNodeStatusTagType = (status) => {
      switch (status) {
        case 'PENDING': return 'warning'
        case 'PROCESSING': return 'primary'
        case 'COMPLETED': return 'success'
        case 'SKIPPED': return 'info'
        default: return 'info'
      }
    }
    
    // 获取节点状态标签
    const getNodeStatusLabel = (status) => {
      const option = nodeStatusOptions.find(item => item.value === status)
      return option ? option.label : status
    }
    
    // 初始化数据
    onMounted(() => {
      getProcessListData()
    })
    
    return {
      searchForm,
      processList,
      loading,
      pagination,
      currentProcess,
      detailDialogVisible,
      processTypeOptions,
      statusOptions,
      nodeStatusOptions,
      handleSearch,
      handleReset,
      handleView,
      handleApprove,
      handleReject,
      handleTrack,
      handleStartProcess,
      handleExport,
      handleSizeChange,
      handleCurrentChange,
      canApprove,
      getCurrentStep,
      getNodeDescription,
      handleCompleteCurrentNode,
      canCompleteCurrentNode,
      getProcessTypeLabel,
      getStatusTagType,
      getStatusLabel,
      getNodeStatusTagType,
      getNodeStatusLabel
    }
  }
}
</script>

<style lang="scss" scoped>
.approval-list-container {
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

  .process-detail-content {
    .el-steps {
      margin-bottom: 20px;
    }
    
    .el-descriptions {
      margin-top: 10px;
      margin-bottom: 20px;
    }
  }
}
</style>