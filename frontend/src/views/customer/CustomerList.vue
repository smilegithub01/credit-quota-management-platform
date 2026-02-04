<template>
  <div class="customer-list-container">
    <div class="search-area">
      <el-form :model="searchForm" inline label-width="80px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item :label="$t('message.customer.customerName')">
              <el-input 
                v-model="searchForm.customerName" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.customer.customerName')" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('message.customer.customerType')">
              <el-select 
                v-model="searchForm.customerType" 
                :placeholder="$t('message.common.pleaseSelect') + $t('message.customer.customerType')"
                clearable
              >
                <el-option 
                  v-for="item in customerTypeOptions" 
                  :key="item.value" 
                  :label="item.label" 
                  :value="item.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('message.customer.status')">
              <el-select 
                v-model="searchForm.status" 
                :placeholder="$t('message.common.pleaseSelect') + $t('message.customer.status')"
                clearable
              >
                <el-option 
                  v-for="item in customerStatusOptions" 
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
        :data="customerList" 
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="customerId" :label="$t('message.customer.customerName')" width="200" />
        <el-table-column prop="customerName" :label="$t('message.customer.customerName')" width="200" />
        <el-table-column prop="customerType" :label="$t('message.customer.customerType')" width="120">
          <template #default="{ row }">
            {{ getCustomerTypeLabel(row.customerType) }}
          </template>
        </el-table-column>
        <el-table-column prop="customerLevel" :label="$t('message.customer.customerLevel')" width="100" />
        <el-table-column prop="registeredCapital" :label="$t('message.customer.registeredCapital')" width="150">
          <template #default="{ row }">
            ¥{{ formatCurrency(row.registeredCapital) }}
          </template>
        </el-table-column>
        <el-table-column prop="legalRepresentative" :label="$t('message.customer.legalRepresentative')" width="120" />
        <el-table-column prop="status" :label="$t('message.customer.status')" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" :label="$t('message.common.createTime')" width="180" />
        <el-table-column :label="$t('message.common.operate')" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">{{ $t('message.common.view') }}</el-button>
            <el-button size="small" type="primary" @click="handleEdit(row)">{{ $t('message.common.edit') }}</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">{{ $t('message.common.delete') }}</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomerList } from '@/api/customer'

export default {
  name: 'CustomerList',
  setup() {
    const router = useRouter()
    
    // 搜索表单
    const searchForm = reactive({
      customerName: '',
      customerType: '',
      status: ''
    })
    
    // 客户列表
    const customerList = ref([])
    
    // 加载状态
    const loading = ref(false)
    
    // 分页信息
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })
    
    // 客户类型选项
    const customerTypeOptions = [
      { value: 'ENTERPRISE', label: '企业' },
      { value: 'INDIVIDUAL', label: '个人' },
      { value: 'FINANCIAL', label: '金融机构' },
      { value: 'GOVERNMENT', label: '政府机构' }
    ]
    
    // 客户状态选项
    const customerStatusOptions = [
      { value: 'NORMAL', label: '正常' },
      { value: 'FROZEN', label: '冻结' },
      { value: 'CANCELLED', label: '注销' }
    ]
    
    // 获取客户列表
    const getCustomerListData = async () => {
      loading.value = true
      try {
        const params = {
          ...searchForm,
          pageNum: pagination.currentPage,
          pageSize: pagination.pageSize
        }
        const response = await getCustomerList(params)
        customerList.value = response.data.list || []
        pagination.total = response.data.total || 0
      } catch (error) {
        ElMessage.error('获取客户列表失败: ' + error.message)
      } finally {
        loading.value = false
      }
    }
    
    // 搜索处理
    const handleSearch = () => {
      pagination.currentPage = 1
      getCustomerListData()
    }
    
    // 重置搜索
    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      pagination.currentPage = 1
      getCustomerListData()
    }
    
    // 创建客户
    const handleCreate = () => {
      router.push('/customer/create')
    }
    
    // 查看客户
    const handleView = (row) => {
      router.push(`/customer/${row.customerId}/view`)
    }
    
    // 编辑客户
    const handleEdit = (row) => {
      router.push(`/customer/${row.customerId}/edit`)
    }
    
    // 删除客户
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除客户 "${row.customerName}" 吗？`,
          '删除确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // TODO: 实际删除API调用
        ElMessage.success('删除成功')
        getCustomerListData() // 刷新列表
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败: ' + error.message)
        }
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
      getCustomerListData()
    }
    
    // 当前页变化
    const handleCurrentChange = (page) => {
      pagination.currentPage = page
      getCustomerListData()
    }
    
    // 获取客户类型标签
    const getCustomerTypeLabel = (type) => {
      const option = customerTypeOptions.find(item => item.value === type)
      return option ? option.label : type
    }
    
    // 获取状态标签类型
    const getStatusTagType = (status) => {
      switch (status) {
        case 'NORMAL': return 'success'
        case 'FROZEN': return 'warning'
        case 'CANCELLED': return 'danger'
        default: return 'info'
      }
    }
    
    // 获取状态标签
    const getStatusLabel = (status) => {
      const option = customerStatusOptions.find(item => item.value === status)
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
      getCustomerListData()
    })
    
    return {
      searchForm,
      customerList,
      loading,
      pagination,
      customerTypeOptions,
      customerStatusOptions,
      handleSearch,
      handleReset,
      handleCreate,
      handleView,
      handleEdit,
      handleDelete,
      handleExport,
      handleSizeChange,
      handleCurrentChange,
      getCustomerTypeLabel,
      getStatusTagType,
      getStatusLabel,
      formatCurrency
    }
  }
}
</script>

<style lang="scss" scoped>
.customer-list-container {
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