<template>
  <div class="system-config-container">
    <div class="search-area">
      <el-form :model="searchForm" inline label-width="100px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item :label="$t('message.system.configKey')">
              <el-input 
                v-model="searchForm.configKey" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.system.configKey')" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('message.system.configType')">
              <el-select 
                v-model="searchForm.configType" 
                :placeholder="$t('message.common.pleaseSelect') + $t('message.system.configType')"
                clearable
              >
                <el-option 
                  v-for="item in configTypeOptions" 
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
      <el-button @click="handleImport">
        <el-icon><Upload /></el-icon>
        {{ $t('message.common.import') }}
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        {{ $t('message.common.export') }}
      </el-button>
    </div>

    <div class="table-area">
      <el-table 
        :data="configList" 
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="configId" :label="$t('message.system.configId')" width="120" />
        <el-table-column prop="configKey" :label="$t('message.system.configKey')" width="200" />
        <el-table-column prop="configValue" :label="$t('message.system.configValue')" width="200" show-overflow-tooltip />
        <el-table-column prop="configType" :label="$t('message.system.configType')" width="120">
          <template #default="{ row }">
            {{ getConfigTypeLabel(row.configType) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" :label="$t('message.system.description')" width="200" show-overflow-tooltip />
        <el-table-column prop="status" :label="$t('message.system.status')" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="'ACTIVE'"
              :inactive-value="'INACTIVE'"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" :label="$t('message.system.createTime')" width="150" />
        <el-table-column prop="updateTime" :label="$t('message.system.updateTime')" width="150" />
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

    <!-- 配置详情/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogType === 'create' ? $t('message.system.addConfig') : $t('message.system.editConfig')" 
      width="50%"
    >
      <el-form 
        :model="currentConfig" 
        :rules="configRules" 
        ref="configFormRef"
        label-width="120px"
      >
        <el-form-item :label="$t('message.system.configKey')" prop="configKey">
          <el-input 
            v-model="currentConfig.configKey" 
            :placeholder="$t('message.common.pleaseInput') + $t('message.system.configKey')"
            :disabled="dialogType === 'edit'"
          />
        </el-form-item>
        
        <el-form-item :label="$t('message.system.configValue')" prop="configValue">
          <el-input 
            v-model="currentConfig.configValue" 
            :placeholder="$t('message.common.pleaseInput') + $t('message.system.configValue')"
            type="textarea"
            :rows="4"
          />
        </el-form-item>
        
        <el-form-item :label="$t('message.system.configType')" prop="configType">
          <el-select 
            v-model="currentConfig.configType" 
            :placeholder="$t('message.common.pleaseSelect') + $t('message.system.configType')"
            style="width: 100%"
          >
            <el-option 
              v-for="item in configTypeOptions" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item :label="$t('message.system.description')">
          <el-input 
            v-model="currentConfig.description" 
            :placeholder="$t('message.common.pleaseInput') + $t('message.system.description')"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        
        <el-form-item :label="$t('message.system.status')">
          <el-select 
            v-model="currentConfig.status" 
            :placeholder="$t('message.common.pleaseSelect') + $t('message.system.status')"
          >
            <el-option label="激活" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">{{ $t('message.common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSave">{{ $t('message.common.save') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSysConfigList, createSysConfig, updateSysConfig, deleteSysConfig, updateSysConfigStatus } from '@/api/system'

export default {
  name: 'SystemConfig',
  setup() {
    // 搜索表单
    const searchForm = reactive({
      configKey: '',
      configType: ''
    })
    
    // 配置列表
    const configList = ref([])
    
    // 加载状态
    const loading = ref(false)
    
    // 分页信息
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })
    
    // 对话框相关
    const dialogVisible = ref(false)
    const dialogType = ref('create') // 'create' or 'edit'
    const currentConfig = ref({
      configId: null,
      configKey: '',
      configValue: '',
      configType: '',
      description: '',
      status: 'ACTIVE'
    })
    const configFormRef = ref(null)
    
    // 配置类型选项
    const configTypeOptions = [
      { value: 'BASIC', label: '基础配置' },
      { value: 'BUSINESS', label: '业务配置' },
      { value: 'RISK', label: '风控配置' },
      { value: 'NOTIFICATION', label: '通知配置' },
      { value: 'SECURITY', label: '安全配置' },
      { value: 'AUDIT', label: '审计配置' }
    ]
    
    // 表单验证规则
    const configRules = {
      configKey: [
        { required: true, message: '请输入配置键名', trigger: 'blur' },
        { min: 3, max: 50, message: '配置键名长度在3-50个字符之间', trigger: 'blur' }
      ],
      configValue: [
        { required: true, message: '请输入配置值', trigger: 'blur' }
      ],
      configType: [
        { required: true, message: '请选择配置类型', trigger: 'change' }
      ]
    }
    
    // 获取配置列表
    const getSysConfigListData = async () => {
      loading.value = true
      try {
        const params = {
          ...searchForm,
          pageNum: pagination.currentPage,
          pageSize: pagination.pageSize
        }
        const response = await getSysConfigList(params)
        configList.value = response.data.list || []
        pagination.total = response.data.total || 0
      } catch (error) {
        ElMessage.error('获取系统配置列表失败: ' + error.message)
      } finally {
        loading.value = false
      }
    }
    
    // 搜索处理
    const handleSearch = () => {
      pagination.currentPage = 1
      getSysConfigListData()
    }
    
    // 重置搜索
    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      pagination.currentPage = 1
      getSysConfigListData()
    }
    
    // 添加配置
    const handleCreate = () => {
      currentConfig.value = {
        configId: null,
        configKey: '',
        configValue: '',
        configType: '',
        description: '',
        status: 'ACTIVE'
      }
      dialogType.value = 'create'
      dialogVisible.value = true
    }
    
    // 编辑配置
    const handleEdit = (row) => {
      currentConfig.value = { ...row }
      dialogType.value = 'edit'
      dialogVisible.value = true
    }
    
    // 查看配置
    const handleView = (row) => {
      currentConfig.value = { ...row }
      dialogType.value = 'view'
      dialogVisible.value = true
    }
    
    // 删除配置
    const handleDelete = (row) => {
      ElMessageBox.confirm(
        `${$t('message.system.confirmDelete')} "${row.configKey}"?`,
        $t('message.system.deleteConfig'),
        {
          confirmButtonText: $t('message.common.confirm'),
          cancelButtonText: $t('message.common.cancel'),
          type: 'warning'
        }
      ).then(async () => {
        try {
          await deleteSysConfig(row.configId)
          ElMessage.success($t('message.system.deleteSuccess'))
          getSysConfigListData() // 刷新列表
        } catch (error) {
          ElMessage.error('删除配置失败: ' + error.message)
        }
      }).catch(() => {
        // 取消删除
      })
    }
    
    // 保存配置
    const handleSave = async () => {
      try {
        await configFormRef.value.validate()
        
        if (dialogType.value === 'create') {
          await createSysConfig(currentConfig.value)
          ElMessage.success($t('message.system.createSuccess'))
        } else {
          await updateSysConfig(currentConfig.value.configId, currentConfig.value)
          ElMessage.success($t('message.system.updateSuccess'))
        }
        
        dialogVisible.value = false
        getSysConfigListData() // 刷新列表
      } catch (error) {
        ElMessage.error('保存配置失败: ' + error.message)
      }
    }
    
    // 状态变更
    const handleStatusChange = async (row) => {
      try {
        await updateSysConfigStatus(row.configId, { status: row.status })
        ElMessage.success('状态更新成功')
      } catch (error) {
        ElMessage.error('状态更新失败: ' + error.message)
        // 如果失败，恢复原状态
        row.status = row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
      }
    }
    
    // 导入配置
    const handleImport = () => {
      ElMessage.success($t('message.common.importSuccess'))
    }
    
    // 导出配置
    const handleExport = () => {
      ElMessage.success($t('message.common.exportSuccess'))
    }
    
    // 分页大小变化
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.currentPage = 1
      getSysConfigListData()
    }
    
    // 当前页变化
    const handleCurrentChange = (page) => {
      pagination.currentPage = page
      getSysConfigListData()
    }
    
    // 获取配置类型标签
    const getConfigTypeLabel = (type) => {
      const option = configTypeOptions.find(item => item.value === type)
      return option ? option.label : type
    }
    
    // 初始化数据
    onMounted(() => {
      getSysConfigListData()
    })
    
    return {
      searchForm,
      configList,
      loading,
      pagination,
      dialogVisible,
      dialogType,
      currentConfig,
      configFormRef,
      configTypeOptions,
      configRules,
      handleSearch,
      handleReset,
      handleCreate,
      handleEdit,
      handleView,
      handleDelete,
      handleSave,
      handleStatusChange,
      handleImport,
      handleExport,
      handleSizeChange,
      handleCurrentChange,
      getConfigTypeLabel
    }
  }
}
</script>

<style lang="scss" scoped>
.system-config-container {
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