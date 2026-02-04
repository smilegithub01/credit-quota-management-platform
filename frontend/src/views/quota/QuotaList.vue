<template>
  <div class="quota-list-container">
    <div class="search-area">
      <el-form :model="searchForm" inline label-width="100px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item :label="$t('message.quota.customer')">
              <el-input 
                v-model="searchForm.customerId" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.quota.customer')" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('message.quota.quotaType')">
              <el-input 
                v-model="searchForm.quotaType" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.quota.quotaType')" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('message.quota.quotaStatus')">
              <el-select 
                v-model="searchForm.quotaStatus" 
                :placeholder="$t('message.common.pleaseSelect') + $t('message.quota.quotaStatus')"
                clearable
              >
                <el-option 
                  v-for="item in quotaStatusOptions" 
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
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        {{ $t('message.common.add') }}
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        {{ $t('message.common.export') }}
      </el-button>
    </div>

    <div class="table-area">
      <el-table 
        :data="quotaList" 
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="quotaId" :label="$t('message.quota.quotaList') + 'ID'" width="120" />
        <el-table-column prop="customerId" :label="$t('message.quota.customer')" width="150" />
        <el-table-column prop="quotaType" :label="$t('message.quota.quotaType')" width="120" />
        <el-table-column prop="totalQuota" :label="$t('message.quota.totalQuota')" width="120">
          <template #default="{ row }">
            ¥{{ formatCurrency(row.totalQuota) }}
          </template>
        </el-table-column>
        <el-table-column prop="usedQuota" :label="$t('message.quota.usedQuota')" width="120">
          <template #default="{ row }">
            ¥{{ formatCurrency(row.usedQuota) }}
          </template>
        </el-table-column>
        <el-table-column prop="availableQuota" :label="$t('message.quota.availableQuota')" width="120">
          <template #default="{ row }">
            ¥{{ formatCurrency(row.availableQuota) }}
          </template>
        </el-table-column>
        <el-table-column prop="frozenQuota" :label="$t('message.quota.frozenQuota')" width="120">
          <template #default="{ row }">
            ¥{{ formatCurrency(row.frozenQuota) }}
          </template>
        </el-table-column>
        <el-table-column prop="quotaLevel" :label="$t('message.quota.quotaLevel')" width="120">
          <template #default="{ row }">
            {{ getQuotaLevelLabel(row.quotaLevel) }}
          </template>
        </el-table-column>
        <el-table-column prop="quotaStatus" :label="$t('message.quota.quotaStatus')" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.quotaStatus)">
              {{ getStatusLabel(row.quotaStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="effectiveDate" :label="$t('message.quota.effectiveDate')" width="120" />
        <el-table-column prop="expireDate" :label="$t('message.quota.expireDate')" width="120" />
        <el-table-column :label="$t('message.common.operate')" width="250" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">{{ $t('message.common.view') }}</el-button>
            <el-button size="small" type="primary" @click="handleEdit(row)">{{ $t('message.common.edit') }}</el-button>
            <el-dropdown split-button type="primary" size="small" @click="handleOperate(row, 'adjust')">
              {{ $t('message.quota.quotaAdjustment') }}
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleOperate(row, 'occupy')">{{ $t('message.quota.occupy') }}</el-dropdown-item>
                  <el-dropdown-item @click="handleOperate(row, 'release')">{{ $t('message.quota.release') }}</el-dropdown-item>
                  <el-dropdown-item @click="handleOperate(row, 'freeze')">{{ $t('message.quota.freeze') }}</el-dropdown-item>
                  <el-dropdown-item @click="handleOperate(row, 'unfreeze')">{{ $t('message.quota.unfreeze') }}</el-dropdown-item>
                  <el-dropdown-item @click="handleOperate(row, 'enable')">{{ $t('message.quota.enable') }}</el-dropdown-item>
                  <el-dropdown-item @click="handleOperate(row, 'disable')">{{ $t('message.quota.disable') }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElDialog } from 'element-plus'
import { getQuotaList } from '@/api/quota'

export default {
  name: 'QuotaList',
  setup() {
    const router = useRouter()
    
    // 搜索表单
    const searchForm = reactive({
      customerId: '',
      quotaType: '',
      quotaStatus: ''
    })
    
    // 额度列表
    const quotaList = ref([])
    
    // 加载状态
    const loading = ref(false)
    
    // 分页信息
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })
    
    // 额度状态选项
    const quotaStatusOptions = [
      { value: 'ACTIVE', label: '激活' },
      { value: 'INACTIVE', label: '停用' },
      { value: 'FROZEN', label: '冻结' },
      { value: 'EXPIRED', label: '已过期' }
    ]
    
    // 额度层级选项
    const quotaLevelOptions = [
      { value: 'GROUP', label: '集团' },
      { value: 'CUSTOMER', label: '客户' },
      { value: 'PRODUCT', label: '产品' }
    ]
    
    // 获取额度列表
    const getQuotaListData = async () => {
      loading.value = true
      try {
        const params = {
          ...searchForm,
          pageNum: pagination.currentPage,
          pageSize: pagination.pageSize
        }
        const response = await getQuotaList(params)
        quotaList.value = response.data.list || []
        pagination.total = response.data.total || 0
      } catch (error) {
        ElMessage.error('获取额度列表失败: ' + error.message)
      } finally {
        loading.value = false
      }
    }
    
    // 搜索处理
    const handleSearch = () => {
      pagination.currentPage = 1
      getQuotaListData()
    }
    
    // 重置搜索
    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      pagination.currentPage = 1
      getQuotaListData()
    }
    
    // 创建额度
    const handleCreate = () => {
      ElMessage.success('创建额度功能开发中...')
    }
    
    // 查看额度
    const handleView = (row) => {
      ElMessage.info(`查看额度: ${row.quotaId}`)
    }
    
    // 编辑额度
    const handleEdit = (row) => {
      ElMessage.info(`编辑额度: ${row.quotaId}`)
    }
    
    // 额度操作
    const handleOperate = (row, operation) => {
      switch(operation) {
        case 'adjust':
          ElMessage.info(`调整额度: ${row.quotaId}`)
          break
        case 'occupy':
          ElMessage.info(`占用额度: ${row.quotaId}`)
          break
        case 'release':
          ElMessage.info(`释放额度: ${row.quotaId}`)
          break
        case 'freeze':
          ElMessage.info(`冻结额度: ${row.quotaId}`)
          break
        case 'unfreeze':
          ElMessage.info(`解冻额度: ${row.quotaId}`)
          break
        case 'enable':
          ElMessage.info(`启用额度: ${row.quotaId}`)
          break
        case 'disable':
          ElMessage.info(`停用额度: ${row.quotaId}`)
          break
        default:
          ElMessage.info(`操作额度: ${row.quotaId}`)
      }
    }
    
    // 导出数据
    const handleExport = () => {
      ElMessage.success('导出功能开发中...')
    }
    
    // 分页大小变化
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.currentPage = 1
      getQuotaListData()
    }
    
    // 当前页变化
    const handleCurrentChange = (page) => {
      pagination.currentPage = page
      getQuotaListData()
    }
    
    // 获取额度层级标签
    const getQuotaLevelLabel = (level) => {
      const option = quotaLevelOptions.find(item => item.value === level)
      return option ? option.label : level
    }
    
    // 获取状态标签类型
    const getStatusTagType = (status) => {
      switch (status) {
        case 'ACTIVE': return 'success'
        case 'INACTIVE': return 'info'
        case 'FROZEN': return 'warning'
        case 'EXPIRED': return 'danger'
        default: return 'info'
      }
    }
    
    // 获取状态标签
    const getStatusLabel = (status) => {
      const option = quotaStatusOptions.find(item => item.value === status)
      return option ? option.label : status
    }
    
    // 格式化货币
    const formatCurrency = (value) => {
      if (!value) return '0.00'
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }
    
    // 初始化数据
    onMounted(() => {
      getQuotaListData()
    })
    
    return {
      searchForm,
      quotaList,
      loading,
      pagination,
      quotaStatusOptions,
      quotaLevelOptions,
      handleSearch,
      handleReset,
      handleCreate,
      handleView,
      handleEdit,
      handleOperate,
      handleExport,
      handleSizeChange,
      handleCurrentChange,
      getQuotaLevelLabel,
      getStatusTagType,
      getStatusLabel,
      formatCurrency
    }
  }
}
</script>

<style lang="scss" scoped>
.quota-list-container {
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
}
</style>