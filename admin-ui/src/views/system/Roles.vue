<template>
  <div class="roles-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">角色管理</h1>
        <p class="page-description">管理系统角色和权限分配</p>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Plus" @click="handleAdd">
          新增角色
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="角色名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入角色名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input
            v-model="searchForm.code"
            placeholder="请输入角色编码"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <div class="table-header">
        <div class="table-title">角色列表</div>
        <div class="table-actions">
          <el-button
            type="danger"
            :icon="Delete"
            :disabled="!selectedIds.length"
            @click="handleBatchDelete"
          >
            批量删除
          </el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
        stripe
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="角色名称" min-width="120" />
        <el-table-column prop="code" label="角色编码" min-width="120" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="userCount" label="用户数量" width="100" align="center">
          <template #default="{ row }">
            <el-link type="primary" @click="handleViewUsers(row)">
              {{ row.userCount || 0 }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="permissions" label="权限数量" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small">
              {{ row.permissions?.length || 0 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="160">
          <template #default="{ row }">
            {{ DateUtils.format(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :icon="Edit"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="warning"
              size="small"
              :icon="Key"
              @click="handleAssignPermission(row)"
            >
              分配权限
            </el-button>
            <el-button
              type="danger"
              size="small"
              :icon="Delete"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 角色表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入角色名称" />
        </el-form-item>

        <el-form-item label="角色编码" prop="code">
          <el-input
            v-model="formData.code"
            placeholder="请输入角色编码"
            :disabled="isEdit"
          />
        </el-form-item>

        <el-form-item label="排序" prop="sort">
          <el-input-number
            v-model="formData.sort"
            :min="0"
            :max="999"
            controls-position="right"
            placeholder="请输入排序值"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入角色描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="分配权限"
      width="800px"
      :close-on-click-modal="false"
    >
      <div class="permission-assign-content">
        <div class="role-info">
          <div class="role-details">
            <div class="role-name">{{ currentRole?.name }}</div>
            <div class="role-desc">{{ currentRole?.description }}</div>
          </div>
        </div>

        <el-divider />

        <div class="permission-tree-container">
          <div class="tree-header">
            <el-checkbox
              v-model="checkAll"
              :indeterminate="isIndeterminate"
              @change="handleCheckAllChange"
            >
              全选
            </el-checkbox>
            <el-button type="text" @click="expandAll">
              {{ isExpandAll ? '收起全部' : '展开全部' }}
            </el-button>
          </div>

          <el-tree
            ref="permissionTreeRef"
            :data="permissionTree"
            :props="treeProps"
            :default-expand-all="false"
            :check-strictly="false"
            show-checkbox
            node-key="id"
            class="permission-tree"
            @check="handlePermissionCheck"
          >
            <template #default="{ node, data }">
              <div class="tree-node">
                <el-icon v-if="data.icon" class="node-icon">
                  <component :is="data.icon" />
                </el-icon>
                <span class="node-label">{{ node.label }}</span>
                <el-tag v-if="data.type" :type="getPermissionTypeTag(data.type)" size="small">
                  {{ getPermissionTypeText(data.type) }}
                </el-tag>
              </div>
            </template>
          </el-tree>
        </div>
      </div>

      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="permissionSubmitLoading" @click="handlePermissionSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 用户列表对话框 -->
    <el-dialog
      v-model="userDialogVisible"
      :title="`角色用户 - ${currentRole?.name}`"
      width="600px"
    >
      <el-table :data="roleUsers" stripe>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { roleApi, permissionApi, userApi } from '@/api'
import { DateUtils } from '@/utils'
import type { Role, Permission, User, ApiResponse, PaginationParams } from '@/types'
import {
  Plus,
  Search,
  Refresh,
  Edit,
  Delete,
  Key,
  Menu,
  Document,
  Setting
} from '@element-plus/icons-vue'

// 表单引用
const formRef = ref<FormInstance>()
const permissionTreeRef = ref()

// 状态
const loading = ref(false)
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const userDialogVisible = ref(false)
const submitLoading = ref(false)
const permissionSubmitLoading = ref(false)
const isEdit = ref(false)
const checkAll = ref(false)
const isIndeterminate = ref(false)
const isExpandAll = ref(false)

// 搜索表单
const searchForm = reactive({
  name: '',
  code: '',
  status: ''
})

// 分页参数
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 表格数据
const tableData = ref<Role[]>([])
const selectedIds = ref<number[]>([])
const permissionTree = ref<Permission[]>([])
const roleUsers = ref<User[]>([])

// 当前操作的角色
const currentRole = ref<Role | null>(null)

// 表单数据
const formData = reactive({
  id: 0,
  name: '',
  code: '',
  description: '',
  sort: 0,
  status: 1
})

// 树形组件配置
const treeProps = {
  children: 'children',
  label: 'name'
}

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '角色名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { min: 2, max: 50, message: '角色编码长度在 2 到 50 个字符', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '角色编码只能包含大写字母和下划线', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

// 获取角色列表
const fetchRoles = async () => {
  try {
    loading.value = true
    const params: PaginationParams & typeof searchForm = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    
    const response = await roleApi.getRoles(params)
    tableData.value = response.data.records
    pagination.total = response.data.total
  } catch (error: any) {
    ElMessage.error(error.message || '获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 获取权限树
const fetchPermissionTree = async () => {
  try {
    const response = await permissionApi.getPermissionTree()
    permissionTree.value = response.data
  } catch (error: any) {
    ElMessage.error(error.message || '获取权限树失败')
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchRoles()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    code: '',
    status: ''
  })
  handleSearch()
}

// 新增角色
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 编辑角色
const handleEdit = (row: Role) => {
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    code: row.code,
    description: row.description,
    sort: row.sort,
    status: row.status
  })
}

// 删除角色
const handleDelete = async (row: Role) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色 "${row.name}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await roleApi.deleteRole(row.id)
    ElMessage.success('删除成功')
    fetchRoles()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 个角色吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await roleApi.batchDeleteRoles(selectedIds.value)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    fetchRoles()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

// 状态变更
const handleStatusChange = async (row: Role) => {
  try {
    await roleApi.updateRoleStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error: any) {
    ElMessage.error(error.message || '状态更新失败')
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
  }
}

// 分配权限
const handleAssignPermission = async (row: Role) => {
  currentRole.value = row
  permissionDialogVisible.value = true
  
  await nextTick()
  
  // 设置已选中的权限
  if (row.permissions && row.permissions.length > 0) {
    const checkedKeys = row.permissions.map(p => p.id)
    permissionTreeRef.value?.setCheckedKeys(checkedKeys)
  }
  
  updateCheckAllStatus()
}

// 查看角色用户
const handleViewUsers = async (row: Role) => {
  try {
    currentRole.value = row
    const response = await userApi.getUsersByRole(row.id)
    roleUsers.value = response.data
    userDialogVisible.value = true
  } catch (error: any) {
    ElMessage.error(error.message || '获取角色用户失败')
  }
}

// 表格选择变更
const handleSelectionChange = (selection: Role[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 分页大小变更
const handleSizeChange = (size: number) => {
  pagination.size = size
  fetchRoles()
}

// 当前页变更
const handleCurrentChange = (page: number) => {
  pagination.page = page
  fetchRoles()
}

// 全选变更
const handleCheckAllChange = (checked: boolean) => {
  if (checked) {
    permissionTreeRef.value?.setCheckedNodes(getAllPermissionNodes(permissionTree.value))
  } else {
    permissionTreeRef.value?.setCheckedKeys([])
  }
  updateCheckAllStatus()
}

// 权限选择变更
const handlePermissionCheck = () => {
  updateCheckAllStatus()
}

// 展开/收起全部
const expandAll = () => {
  isExpandAll.value = !isExpandAll.value
  const nodes = permissionTreeRef.value?.store.nodesMap
  if (nodes) {
    Object.values(nodes).forEach((node: any) => {
      node.expanded = isExpandAll.value
    })
  }
}

// 更新全选状态
const updateCheckAllStatus = () => {
  const checkedNodes = permissionTreeRef.value?.getCheckedNodes() || []
  const allNodes = getAllPermissionNodes(permissionTree.value)
  
  checkAll.value = checkedNodes.length === allNodes.length
  isIndeterminate.value = checkedNodes.length > 0 && checkedNodes.length < allNodes.length
}

// 获取所有权限节点
const getAllPermissionNodes = (nodes: Permission[]): Permission[] => {
  const result: Permission[] = []
  
  const traverse = (nodeList: Permission[]) => {
    nodeList.forEach(node => {
      result.push(node)
      if (node.children && node.children.length > 0) {
        traverse(node.children)
      }
    })
  }
  
  traverse(nodes)
  return result
}

// 获取权限类型标签
const getPermissionTypeTag = (type: string) => {
  const typeMap: Record<string, string> = {
    menu: 'primary',
    button: 'success',
    api: 'warning'
  }
  return typeMap[type] || 'info'
}

// 获取权限类型文本
const getPermissionTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    menu: '菜单',
    button: '按钮',
    api: '接口'
  }
  return typeMap[type] || '未知'
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    submitLoading.value = true
    
    if (isEdit.value) {
      await roleApi.updateRole(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await roleApi.createRole(formData)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    fetchRoles()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

// 提交权限分配
const handlePermissionSubmit = async () => {
  if (!currentRole.value) return
  
  try {
    permissionSubmitLoading.value = true
    
    const checkedKeys = permissionTreeRef.value?.getCheckedKeys() || []
    await roleApi.assignPermissions(currentRole.value.id, checkedKeys)
    ElMessage.success('权限分配成功')
    
    permissionDialogVisible.value = false
    fetchRoles()
  } catch (error: any) {
    ElMessage.error(error.message || '权限分配失败')
  } finally {
    permissionSubmitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    id: 0,
    name: '',
    code: '',
    description: '',
    sort: 0,
    status: 1
  })
  formRef.value?.resetFields()
}

// 初始化
onMounted(() => {
  fetchRoles()
  fetchPermissionTree()
})
</script>

<style lang="scss" scoped>
.roles-page {
  padding: 20px;
  background-color: var(--bg-color-page);
  min-height: calc(100vh - 60px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  .header-left {
    .page-title {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0 0 4px 0;
    }
    
    .page-description {
      font-size: 14px;
      color: var(--text-secondary);
      margin: 0;
    }
  }
  
  @media (max-width: 768px) {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
}

.search-card {
  margin-bottom: 20px;
  
  .search-form {
    .el-form-item {
      margin-bottom: 0;
    }
  }
}

.table-card {
  .table-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    
    .table-title {
      font-size: 16px;
      font-weight: 500;
      color: var(--text-primary);
    }
    
    .table-actions {
      display: flex;
      gap: 8px;
    }
  }
  
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }
}

.permission-assign-content {
  .role-info {
    .role-details {
      .role-name {
        font-size: 16px;
        font-weight: 500;
        color: var(--text-primary);
        margin-bottom: 4px;
      }
      
      .role-desc {
        font-size: 14px;
        color: var(--text-secondary);
      }
    }
  }
  
  .permission-tree-container {
    .tree-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      padding: 12px;
      background-color: var(--bg-color-overlay);
      border-radius: var(--border-radius-base);
    }
    
    .permission-tree {
      max-height: 400px;
      overflow-y: auto;
      border: 1px solid var(--border-color);
      border-radius: var(--border-radius-base);
      
      .tree-node {
        display: flex;
        align-items: center;
        gap: 8px;
        flex: 1;
        
        .node-icon {
          font-size: 16px;
          color: var(--primary-color);
        }
        
        .node-label {
          flex: 1;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .roles-page {
    padding: 16px;
  }
  
  .search-form {
    .el-form-item {
      width: 100%;
      margin-bottom: 16px;
    }
  }
  
  .table-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start !important;
    
    .table-actions {
      width: 100%;
      justify-content: flex-start;
    }
  }
  
  :deep(.el-table) {
    .el-table__body-wrapper {
      overflow-x: auto;
    }
  }
}
</style>