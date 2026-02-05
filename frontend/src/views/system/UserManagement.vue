<template>
  <div class="user-management-container">
    <div class="search-area">
      <el-form :model="searchForm" inline label-width="100px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item :label="$t('message.system.user.username')">
              <el-input 
                v-model="searchForm.username" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.system.user.username')" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('message.system.user.department')">
              <el-input 
                v-model="searchForm.department" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.system.user.department')" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('message.system.user.status')">
              <el-select 
                v-model="searchForm.status" 
                :placeholder="$t('message.common.pleaseSelect') + $t('message.system.user.status')"
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
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        {{ $t('message.common.add') }}
      </el-button>
      <el-button @click="handleBatchEnable">
        <el-icon><Unlock /></el-icon>
        {{ $t('message.common.enable') }}
      </el-button>
      <el-button @click="handleBatchDisable">
        <el-icon><Lock /></el-icon>
        {{ $t('message.common.disable') }}
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
        :data="userList" 
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="userId" :label="$t('message.system.user.userId')" width="100" />
        <el-table-column prop="username" :label="$t('message.system.user.username')" width="120" />
        <el-table-column prop="realName" :label="$t('message.system.user.realName')" width="120" />
        <el-table-column prop="email" :label="$t('message.system.user.email')" width="180" />
        <el-table-column prop="phone" :label="$t('message.system.user.phone')" width="130" />
        <el-table-column prop="department" :label="$t('message.system.user.department')" width="150" />
        <el-table-column prop="position" :label="$t('message.system.user.position')" width="120" />
        <el-table-column prop="roleNames" :label="$t('message.system.user.roles')" width="150">
          <template #default="{ row }">
            <el-tag 
              v-for="role in row.roleNames.split(',')" 
              :key="role" 
              size="small" 
              style="margin-right: 4px;"
            >
              {{ role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" :label="$t('message.system.user.status')" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" :label="$t('message.system.user.createTime')" width="150" />
        <el-table-column :label="$t('message.common.operate')" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">{{ $t('message.common.view') }}</el-button>
            <el-button size="small" type="primary" @click="handleEdit(row)">{{ $t('message.common.edit') }}</el-button>
            <el-dropdown split-button type="primary" size="small" @click="handleResetPassword(row)">
              {{ $t('message.system.user.resetPassword') }}
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleChangeRole(row)">{{ $t('message.system.user.changeRole') }}</el-dropdown-item>
                  <el-dropdown-item @click="handleUpdatePermission(row)">{{ $t('message.system.user.updatePermission') }}</el-dropdown-item>
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

    <!-- 用户详情/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogType === 'create' ? $t('message.system.user.addUser') : $t('message.system.user.editUser')" 
      width="50%"
    >
      <el-form 
        :model="currentUser" 
        :rules="userRules" 
        ref="userFormRef"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="$t('message.system.user.username')" prop="username">
              <el-input 
                v-model="currentUser.username" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.system.user.username')"
                :disabled="dialogType === 'edit'"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('message.system.user.realName')" prop="realName">
              <el-input 
                v-model="currentUser.realName" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.system.user.realName')"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="$t('message.system.user.email')" prop="email">
              <el-input 
                v-model="currentUser.email" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.system.user.email')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('message.system.user.phone')" prop="phone">
              <el-input 
                v-model="currentUser.phone" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.system.user.phone')"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="$t('message.system.user.department')" prop="department">
              <el-input 
                v-model="currentUser.department" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.system.user.department')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('message.system.user.position')" prop="position">
              <el-input 
                v-model="currentUser.position" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.system.user.position')"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="$t('message.system.user.status')" prop="status">
              <el-select 
                v-model="currentUser.status" 
                :placeholder="$t('message.common.pleaseSelect') + $t('message.system.user.status')"
                style="width: 100%"
              >
                <el-option label="激活" value="ACTIVE" />
                <el-option label="停用" value="INACTIVE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('message.system.user.roles')">
              <el-select 
                v-model="currentUser.roleIds" 
                multiple 
                :placeholder="$t('message.common.pleaseSelect') + $t('message.system.user.roles')"
                style="width: 100%"
              >
                <el-option 
                  v-for="role in roleOptions" 
                  :key="role.value" 
                  :label="role.label" 
                  :value="role.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item v-if="dialogType === 'create'" :label="$t('message.system.user.password')" prop="password">
          <el-input 
            v-model="currentUser.password" 
            type="password"
            show-password
            :placeholder="$t('message.common.pleaseInput') + $t('message.system.user.password')"
          />
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
import { getUserList, createUser, updateUser, deleteUser, resetUserPassword } from '@/api/system'

export default {
  name: 'UserManagement',
  setup() {
    // 搜索表单
    const searchForm = reactive({
      username: '',
      department: '',
      status: ''
    })
    
    // 用户列表
    const userList = ref([])
    
    // 加载状态
    const loading = ref(false)
    
    // 分页信息
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })
    
    // 选中的用户
    const selectedUsers = ref([])
    
    // 对话框相关
    const dialogVisible = ref(false)
    const dialogType = ref('create') // 'create' or 'edit'
    const currentUser = ref({
      userId: null,
      username: '',
      realName: '',
      email: '',
      phone: '',
      department: '',
      position: '',
      status: 'ACTIVE',
      roleIds: [],
      password: ''
    })
    const userFormRef = ref(null)
    
    // 状态选项
    const statusOptions = [
      { value: 'ACTIVE', label: '激活' },
      { value: 'INACTIVE', label: '停用' }
    ]
    
    // 角色选项
    const roleOptions = [
      { value: 1, label: '系统管理员' },
      { value: 2, label: '风险管理员' },
      { value: 3, label: '审批员' },
      { value: 4, label: '客户经理' },
      { value: 5, label: '普通用户' }
    ]
    
    // 表单验证规则
    const userRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在3-20个字符之间', trigger: 'blur' }
      ],
      realName: [
        { required: true, message: '请输入真实姓名', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱地址', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入手机号码', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
      ],
      status: [
        { required: true, message: '请选择用户状态', trigger: 'change' }
      ]
    }
    
    // 获取用户列表
    const getUserListData = async () => {
      loading.value = true
      try {
        const params = {
          ...searchForm,
          pageNum: pagination.currentPage,
          pageSize: pagination.pageSize
        }
        const response = await getUserList(params)
        userList.value = response.data.list || []
        pagination.total = response.data.total || 0
      } catch (error) {
        ElMessage.error('获取用户列表失败: ' + error.message)
      } finally {
        loading.value = false
      }
    }
    
    // 搜索处理
    const handleSearch = () => {
      pagination.currentPage = 1
      getUserListData()
    }
    
    // 重置搜索
    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      pagination.currentPage = 1
      getUserListData()
    }
    
    // 选择变化处理
    const handleSelectionChange = (users) => {
      selectedUsers.value = users
    }
    
    // 添加用户
    const handleCreate = () => {
      currentUser.value = {
        userId: null,
        username: '',
        realName: '',
        email: '',
        phone: '',
        department: '',
        position: '',
        status: 'ACTIVE',
        roleIds: [],
        password: ''
      }
      dialogType.value = 'create'
      dialogVisible.value = true
    }
    
    // 编辑用户
    const handleEdit = (row) => {
      currentUser.value = { ...row, roleIds: row.roleIds || [] }
      dialogType.value = 'edit'
      dialogVisible.value = true
    }
    
    // 查看用户
    const handleView = (row) => {
      currentUser.value = { ...row, roleIds: row.roleIds || [] }
      dialogType.value = 'view'
      dialogVisible.value = true
    }
    
    // 删除用户
    const handleDelete = (row) => {
      ElMessageBox.confirm(
        `${$t('message.system.user.confirmDelete')} "${row.realName}"?`,
        $t('message.system.user.deleteUser'),
        {
          confirmButtonText: $t('message.common.confirm'),
          cancelButtonText: $t('message.common.cancel'),
          type: 'warning'
        }
      ).then(async () => {
        try {
          await deleteUser(row.userId)
          ElMessage.success($t('message.system.user.deleteSuccess'))
          getUserListData() // 刷新列表
        } catch (error) {
          ElMessage.error('删除用户失败: ' + error.message)
        }
      }).catch(() => {
        // 取消删除
      })
    }
    
    // 保存用户
    const handleSave = async () => {
      try {
        await userFormRef.value.validate()
        
        if (dialogType.value === 'create') {
          await createUser(currentUser.value)
          ElMessage.success($t('message.system.user.createSuccess'))
        } else {
          await updateUser(currentUser.value.userId, currentUser.value)
          ElMessage.success($t('message.system.user.updateSuccess'))
        }
        
        dialogVisible.value = false
        getUserListData() // 刷新列表
      } catch (error) {
        ElMessage.error('保存用户失败: ' + error.message)
      }
    }
    
    // 重置密码
    const handleResetPassword = (row) => {
      ElMessageBox.prompt($t('message.system.user.enterNewPassword'), $t('message.system.user.resetPassword'), {
        confirmButtonText: $t('message.common.confirm'),
        cancelButtonText: $t('message.common.cancel'),
        inputPattern: /^.{6,20}$/,
        inputErrorMessage: $t('message.system.user.passwordRule')
      }).then(async ({ value }) => {
        try {
          await resetUserPassword(row.userId, { newPassword: value })
          ElMessage.success($t('message.system.user.resetPasswordSuccess'))
        } catch (error) {
          ElMessage.error('重置密码失败: ' + error.message)
        }
      }).catch(() => {
        // 取消操作
      })
    }
    
    // 批量启用
    const handleBatchEnable = () => {
      if (selectedUsers.value.length === 0) {
        ElMessage.warning($t('message.system.user.selectUserFirst'))
        return
      }
      
      ElMessageBox.confirm(
        $t('message.system.user.confirmBatchEnable'),
        $t('message.system.user.batchEnable'),
        {
          confirmButtonText: $t('message.common.confirm'),
          cancelButtonText: $t('message.common.cancel'),
          type: 'warning'
        }
      ).then(() => {
        ElMessage.success($t('message.system.user.batchEnableSuccess'))
      }).catch(() => {
        // 取消操作
      })
    }
    
    // 批量禁用
    const handleBatchDisable = () => {
      if (selectedUsers.value.length === 0) {
        ElMessage.warning($t('message.system.user.selectUserFirst'))
        return
      }
      
      ElMessageBox.confirm(
        $t('message.system.user.confirmBatchDisable'),
        $t('message.system.user.batchDisable'),
        {
          confirmButtonText: $t('message.common.confirm'),
          cancelButtonText: $t('message.common.cancel'),
          type: 'warning'
        }
      ).then(() => {
        ElMessage.success($t('message.system.user.batchDisableSuccess'))
      }).catch(() => {
        // 取消操作
      })
    }
    
    // 导入用户
    const handleImport = () => {
      ElMessage.success($t('message.common.importSuccess'))
    }
    
    // 导出用户
    const handleExport = () => {
      ElMessage.success($t('message.common.exportSuccess'))
    }
    
    // 分页大小变化
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.currentPage = 1
      getUserListData()
    }
    
    // 当前页变化
    const handleCurrentChange = (page) => {
      pagination.currentPage = page
      getUserListData()
    }
    
    // 获取状态标签类型
    const getStatusTagType = (status) => {
      switch (status) {
        case 'ACTIVE': return 'success'
        case 'INACTIVE': return 'info'
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
      getUserListData()
    })
    
    return {
      searchForm,
      userList,
      loading,
      pagination,
      selectedUsers,
      dialogVisible,
      dialogType,
      currentUser,
      userFormRef,
      statusOptions,
      roleOptions,
      userRules,
      handleSearch,
      handleReset,
      handleSelectionChange,
      handleCreate,
      handleEdit,
      handleView,
      handleDelete,
      handleSave,
      handleResetPassword,
      handleBatchEnable,
      handleBatchDisable,
      handleImport,
      handleExport,
      handleSizeChange,
      handleCurrentChange,
      getStatusTagType,
      getStatusLabel
    }
  }
}
</script>

<style lang="scss" scoped>
.user-management-container {
  .search-area {
    background: #fff;
    padding: 20px;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
  }

  .top-operation {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
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