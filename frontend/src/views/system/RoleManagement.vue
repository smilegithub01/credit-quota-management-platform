<template>
  <div class="role-management-container">
    <div class="search-area">
      <el-form :model="searchForm" inline label-width="100px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item :label="$t('message.system.role.roleName')">
              <el-input 
                v-model="searchForm.roleName" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.system.role.roleName')" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('message.system.role.roleCode')">
              <el-input 
                v-model="searchForm.roleCode" 
                :placeholder="$t('message.common.pleaseInput') + $t('message.system.role.roleCode')" 
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
        :data="roleList" 
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="roleId" :label="$t('message.system.role.roleId')" width="100" />
        <el-table-column prop="roleName" :label="$t('message.system.role.roleName')" width="150" />
        <el-table-column prop="roleCode" :label="$t('message.system.role.roleCode')" width="150" />
        <el-table-column prop="description" :label="$t('message.system.role.description')" width="200" show-overflow-tooltip />
        <el-table-column prop="permissions" :label="$t('message.system.role.permissions')" width="300">
          <template #default="{ row }">
            <el-tag 
              v-for="permission in row.permissionList.slice(0, 3)" 
              :key="permission" 
              size="small" 
              style="margin-right: 4px; margin-bottom: 4px;"
            >
              {{ permission }}
            </el-tag>
            <el-popover
              v-if="row.permissionList.length > 3"
              placement="top"
              :width="400"
              trigger="hover"
            >
              <template #reference>
                <el-tag size="small" type="info">+{{ row.permissionList.length - 3 }}</el-tag>
              </template>
              <div class="permission-list">
                <el-tag 
                  v-for="permission in row.permissionList" 
                  :key="permission" 
                  size="small" 
                  style="margin-right: 4px; margin-bottom: 4px;"
                >
                  {{ permission }}
                </el-tag>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="userCount" :label="$t('message.system.role.userCount')" width="100" />
        <el-table-column prop="createTime" :label="$t('message.system.role.createTime')" width="150" />
        <el-table-column :label="$t('message.common.operate')" width="250" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">{{ $t('message.common.view') }}</el-button>
            <el-button size="small" type="primary" @click="handleEdit(row)">{{ $t('message.common.edit') }}</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">{{ $t('message.common.delete') }}</el-button>
            <el-dropdown split-button type="primary" size="small" @click="handleAssignPermission(row)">
              {{ $t('message.system.role.assignPermission') }}
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleAssignUser(row)">{{ $t('message.system.role.assignUser') }}</el-dropdown-item>
                  <el-dropdown-item @click="handleCopyRole(row)">{{ $t('message.system.role.copyRole') }}</el-dropdown-item>
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

    <!-- 角色详情/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogType === 'create' ? $t('message.system.role.addRole') : $t('message.system.role.editRole')" 
      width="50%"
    >
      <el-form 
        :model="currentRole" 
        :rules="roleRules" 
        ref="roleFormRef"
        label-width="120px"
      >
        <el-form-item :label="$t('message.system.role.roleName')" prop="roleName">
          <el-input 
            v-model="currentRole.roleName" 
            :placeholder="$t('message.common.pleaseInput') + $t('message.system.role.roleName')"
          />
        </el-form-item>
        
        <el-form-item :label="$t('message.system.role.roleCode')" prop="roleCode">
          <el-input 
            v-model="currentRole.roleCode" 
            :placeholder="$t('message.common.pleaseInput') + $t('message.system.role.roleCode')"
          />
        </el-form-item>
        
        <el-form-item :label="$t('message.system.role.description')">
          <el-input 
            v-model="currentRole.description" 
            :placeholder="$t('message.common.pleaseInput') + $t('message.system.role.description')"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        
        <el-form-item :label="$t('message.system.role.permissions')">
          <el-tree
            ref="permissionTreeRef"
            :data="permissionTreeData"
            show-checkbox
            node-key="id"
            :default-expanded-keys="getAllExpandedKeys()"
            :props="treeProps"
            :default-checked-keys="currentRole.permissionIds"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">{{ $t('message.common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSave">{{ $t('message.common.save') }}</el-button>
      </template>
    </el-dialog>

    <!-- 分配用户对话框 -->
    <el-dialog 
      v-model="assignUserDialogVisible" 
      :title="$t('message.system.role.assignUserToRole')" 
      width="60%"
    >
      <el-transfer
        v-model="assignedUserIds"
        filterable
        :filter-method="filterUserMethod"
        :titles="[$t('message.system.role.allUsers'), $t('message.system.role.assignedUsers')]"
        :button-texts="[$t('message.common.remove'), $t('message.common.add')]"
        :format="{
          noChecked: $t('message.system.role.noSelectedCount'),
          hasChecked: $t('message.system.role.selectedCount')
        }"
        :data="allUsers"
        :props="{
          key: 'userId',
          label: 'realName',
        }"
      />
      <template #footer>
        <el-button @click="assignUserDialogVisible = false">{{ $t('message.common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSaveAssignedUsers">{{ $t('message.common.save') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole, getPermissions, getUsers } from '@/api/system'

export default {
  name: 'RoleManagement',
  setup() {
    // 搜索表单
    const searchForm = reactive({
      roleName: '',
      roleCode: ''
    })
    
    // 角色列表
    const roleList = ref([])
    
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
    const currentRole = ref({
      roleId: null,
      roleName: '',
      roleCode: '',
      description: '',
      permissionIds: []
    })
    const roleFormRef = ref(null)
    const permissionTreeRef = ref(null)
    
    // 分配用户对话框
    const assignUserDialogVisible = ref(false)
    const assignedUserIds = ref([])
    const allUsers = ref([])
    
    // 权限树数据
    const permissionTreeData = ref([
      {
        id: 'dashboard',
        label: '仪表板',
        children: [
          { id: 'dashboard:view', label: '查看仪表板' }
        ]
      },
      {
        id: 'customer',
        label: '客户管理',
        children: [
          { id: 'customer:list', label: '查看客户列表' },
          { id: 'customer:create', label: '创建客户' },
          { id: 'customer:edit', label: '编辑客户' },
          { id: 'customer:delete', label: '删除客户' }
        ]
      },
      {
        id: 'quota',
        label: '额度管理',
        children: [
          { id: 'quota:list', label: '查看额度' },
          { id: 'quota:create', label: '创建额度' },
          { id: 'quota:edit', label: '编辑额度' },
          { id: 'quota:adjust', label: '调整额度' },
          { id: 'quota:occupy', label: '占用额度' },
          { id: 'quota:release', label: '释放额度' }
        ]
      },
      {
        id: 'risk',
        label: '风险管理',
        children: [
          { id: 'risk:monitor', label: '风险监控' },
          { id: 'risk:warning', label: '风险预警' },
          { id: 'risk:handle', label: '处理风险' }
        ]
      },
      {
        id: 'approval',
        label: '审批管理',
        children: [
          { id: 'approval:list', label: '查看审批' },
          { id: 'approval:create', label: '发起审批' },
          { id: 'approval:approve', label: '审批通过' },
          { id: 'approval:reject', label: '审批拒绝' }
        ]
      },
      {
        id: 'system',
        label: '系统管理',
        children: [
          { id: 'system:user', label: '用户管理' },
          { id: 'system:role', label: '角色管理' },
          { id: 'system:config', label: '系统配置' },
          { id: 'system:log', label: '操作日志' }
        ]
      }
    ])
    
    // 树组件属性
    const treeProps = {
      children: 'children',
      label: 'label'
    }
    
    // 表单验证规则
    const roleRules = {
      roleName: [
        { required: true, message: '请输入角色名称', trigger: 'blur' },
        { min: 2, max: 20, message: '角色名称长度在2-20个字符之间', trigger: 'blur' }
      ],
      roleCode: [
        { required: true, message: '请输入角色编码', trigger: 'blur' },
        { pattern: /^[A-Z][A-Z0-9_]*$/, message: '角色编码必须以大写字母开头，只能包含大写字母、数字和下划线', trigger: 'blur' }
      ]
    }
    
    // 获取角色列表
    const getRoleListData = async () => {
      loading.value = true
      try {
        const params = {
          ...searchForm,
          pageNum: pagination.currentPage,
          pageSize: pagination.pageSize
        }
        const response = await getRoleList(params)
        roleList.value = response.data.list || []
        pagination.total = response.data.total || 0
      } catch (error) {
        ElMessage.error('获取角色列表失败: ' + error.message)
      } finally {
        loading.value = false
      }
    }
    
    // 获取权限数据
    const getPermissionData = async () => {
      try {
        // 这里通常是调用API获取权限数据
        // const response = await getPermissions()
        // permissionTreeData.value = response.data
      } catch (error) {
        ElMessage.error('获取权限数据失败: ' + error.message)
      }
    }
    
    // 获取所有用户
    const getAllUsers = async () => {
      try {
        // 这里通常是调用API获取所有用户
        // const response = await getUsers()
        // allUsers.value = response.data
      } catch (error) {
        ElMessage.error('获取用户列表失败: ' + error.message)
      }
    }
    
    // 搜索处理
    const handleSearch = () => {
      pagination.currentPage = 1
      getRoleListData()
    }
    
    // 重置搜索
    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      pagination.currentPage = 1
      getRoleListData()
    }
    
    // 添加角色
    const handleCreate = () => {
      currentRole.value = {
        roleId: null,
        roleName: '',
        roleCode: '',
        description: '',
        permissionIds: []
      }
      dialogType.value = 'create'
      dialogVisible.value = true
      
      // 清空权限选择
      setTimeout(() => {
        if (permissionTreeRef.value) {
          permissionTreeRef.value.setCheckedKeys([])
        }
      }, 0)
    }
    
    // 编辑角色
    const handleEdit = (row) => {
      currentRole.value = { ...row }
      dialogType.value = 'edit'
      dialogVisible.value = true
      
      // 设置权限选择
      setTimeout(() => {
        if (permissionTreeRef.value) {
          permissionTreeRef.value.setCheckedKeys(row.permissionIds || [])
        }
      }, 0)
    }
    
    // 查看角色
    const handleView = (row) => {
      currentRole.value = { ...row }
      dialogType.value = 'view'
      dialogVisible.value = true
      
      // 设置权限选择
      setTimeout(() => {
        if (permissionTreeRef.value) {
          permissionTreeRef.value.setCheckedKeys(row.permissionIds || [])
        }
      }, 0)
    }
    
    // 删除角色
    const handleDelete = (row) => {
      ElMessageBox.confirm(
        `${$t('message.system.role.confirmDelete')} "${row.roleName}"?`,
        $t('message.system.role.deleteRole'),
        {
          confirmButtonText: $t('message.common.confirm'),
          cancelButtonText: $t('message.common.cancel'),
          type: 'warning'
        }
      ).then(async () => {
        try {
          await deleteRole(row.roleId)
          ElMessage.success($t('message.system.role.deleteSuccess'))
          getRoleListData() // 刷新列表
        } catch (error) {
          ElMessage.error('删除角色失败: ' + error.message)
        }
      }).catch(() => {
        // 取消删除
      })
    }
    
    // 保存角色
    const handleSave = async () => {
      try {
        await roleFormRef.value.validate()
        
        // 获取选中的权限ID
        if (permissionTreeRef.value) {
          currentRole.value.permissionIds = permissionTreeRef.value.getCheckedKeys()
        }
        
        if (dialogType.value === 'create') {
          await createRole(currentRole.value)
          ElMessage.success($t('message.system.role.createSuccess'))
        } else {
          await updateRole(currentRole.value.roleId, currentRole.value)
          ElMessage.success($t('message.system.role.updateSuccess'))
        }
        
        dialogVisible.value = false
        getRoleListData() // 刷新列表
      } catch (error) {
        ElMessage.error('保存角色失败: ' + error.message)
      }
    }
    
    // 分配权限
    const handleAssignPermission = (row) => {
      handleEdit(row)
    }
    
    // 分配用户
    const handleAssignUser = async (row) => {
      try {
        await getAllUsers()
        assignedUserIds.value = row.userIds || []
        assignUserDialogVisible.value = true
      } catch (error) {
        ElMessage.error('加载用户数据失败: ' + error.message)
      }
    }
    
    // 保存分配的用户
    const handleSaveAssignedUsers = async () => {
      try {
        // 这里通常是调用API保存用户分配
        ElMessage.success($t('message.system.role.assignUserSuccess'))
        assignUserDialogVisible.value = false
      } catch (error) {
        ElMessage.error('分配用户失败: ' + error.message)
      }
    }
    
    // 复制角色
    const handleCopyRole = (row) => {
      ElMessage.success($t('message.system.role.copyRoleSuccess'))
    }
    
    // 获取所有展开的节点ID
    const getAllExpandedKeys = () => {
      const keys = []
      const traverse = (nodes) => {
        nodes.forEach(node => {
          keys.push(node.id)
          if (node.children) {
            traverse(node.children)
          }
        })
      }
      traverse(permissionTreeData.value)
      return keys
    }
    
    // 过滤用户方法
    const filterUserMethod = (query, item) => {
      return item.realName.toLowerCase().indexOf(query.toLowerCase()) > -1
    }
    
    // 导入角色
    const handleImport = () => {
      ElMessage.success($t('message.common.importSuccess'))
    }
    
    // 导出角色
    const handleExport = () => {
      ElMessage.success($t('message.common.exportSuccess'))
    }
    
    // 分页大小变化
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.currentPage = 1
      getRoleListData()
    }
    
    // 当前页变化
    const handleCurrentChange = (page) => {
      pagination.currentPage = page
      getRoleListData()
    }
    
    // 初始化数据
    onMounted(() => {
      getRoleListData()
      getPermissionData()
    })
    
    return {
      searchForm,
      roleList,
      loading,
      pagination,
      dialogVisible,
      dialogType,
      currentRole,
      roleFormRef,
      permissionTreeRef,
      assignUserDialogVisible,
      assignedUserIds,
      allUsers,
      permissionTreeData,
      treeProps,
      roleRules,
      handleSearch,
      handleReset,
      handleCreate,
      handleEdit,
      handleView,
      handleDelete,
      handleSave,
      handleAssignPermission,
      handleAssignUser,
      handleSaveAssignedUsers,
      handleCopyRole,
      handleImport,
      handleExport,
      handleSizeChange,
      handleCurrentChange,
      getAllExpandedKeys,
      filterUserMethod
    }
  }
}
</script>

<style lang="scss" scoped>
.role-management-container {
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

  .permission-list {
    max-height: 200px;
    overflow-y: auto;
  }
}
</style>