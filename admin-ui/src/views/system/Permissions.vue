<template>
  <div class="permissions-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">权限管理</h1>
        <p class="page-description">管理系统权限和菜单结构</p>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Plus" @click="handleAdd">
          新增权限
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="权限名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入权限名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="权限编码">
          <el-input
            v-model="searchForm.code"
            placeholder="请输入权限编码"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="权限类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
            <el-option label="菜单" value="menu" />
            <el-option label="按钮" value="button" />
            <el-option label="接口" value="api" />
          </el-select>
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
          <el-button :icon="Sort" @click="toggleExpandAll">
            {{ isExpandAll ? '收起全部' : '展开全部' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 权限树表格 -->
    <el-card class="table-card" shadow="never">
      <div class="table-header">
        <div class="table-title">权限列表</div>
        <div class="table-actions">
          <el-button
            type="danger"
            :icon="Delete"
            :disabled="!selectedIds.length"
            @click="handleBatchDelete"
          >
            批量删除
          </el-button>
          <el-button :icon="Download" @click="handleExport">
            导出权限
          </el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="isExpandAll"
        @selection-change="handleSelectionChange"
        stripe
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="权限名称" min-width="200">
          <template #default="{ row }">
            <div class="permission-name">
              <el-icon v-if="row.icon" class="permission-icon">
                <component :is="row.icon" />
              </el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="权限编码" min-width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getPermissionTypeTag(row.type)" size="small">
              {{ getPermissionTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" min-width="150" show-overflow-tooltip />
        <el-table-column prop="component" label="组件路径" min-width="150" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :icon="Plus"
              @click="handleAddChild(row)"
            >
              新增
            </el-button>
            <el-button
              type="warning"
              size="small"
              :icon="Edit"
              @click="handleEdit(row)"
            >
              编辑
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
    </el-card>

    <!-- 权限表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="权限名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入权限名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限编码" prop="code">
              <el-input v-model="formData.code" placeholder="请输入权限编码" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="权限类型" prop="type">
              <el-select v-model="formData.type" placeholder="请选择类型" @change="handleTypeChange">
                <el-option label="菜单" value="menu" />
                <el-option label="按钮" value="button" />
                <el-option label="接口" value="api" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上级权限" prop="parentId">
              <el-tree-select
                v-model="formData.parentId"
                :data="permissionTreeOptions"
                :props="treeSelectProps"
                placeholder="请选择上级权限"
                clearable
                check-strictly
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="formData.type === 'menu'">
          <el-col :span="12">
            <el-form-item label="路由路径" prop="path">
              <el-input v-model="formData.path" placeholder="请输入路由路径" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组件路径" prop="component">
              <el-input v-model="formData.component" placeholder="请输入组件路径" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="formData.type === 'api'">
          <el-col :span="12">
            <el-form-item label="接口路径" prop="apiPath">
              <el-input v-model="formData.apiPath" placeholder="请输入接口路径" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求方法" prop="method">
              <el-select v-model="formData.method" placeholder="请选择请求方法">
                <el-option label="GET" value="GET" />
                <el-option label="POST" value="POST" />
                <el-option label="PUT" value="PUT" />
                <el-option label="DELETE" value="DELETE" />
                <el-option label="PATCH" value="PATCH" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="图标" prop="icon">
              <el-input v-model="formData.icon" placeholder="请输入图标名称">
                <template #append>
                  <el-button @click="iconDialogVisible = true">选择</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number
                v-model="formData.sort"
                :min="0"
                :max="999"
                controls-position="right"
                placeholder="请输入排序值"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="formData.type === 'menu'">
          <el-col :span="12">
            <el-form-item label="是否隐藏">
              <el-switch v-model="formData.hidden" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否缓存">
              <el-switch v-model="formData.keepAlive" />
            </el-form-item>
          </el-col>
        </el-row>

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
            :rows="3"
            placeholder="请输入权限描述"
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

    <!-- 图标选择对话框 -->
    <el-dialog v-model="iconDialogVisible" title="选择图标" width="800px">
      <div class="icon-selector">
        <div class="icon-search">
          <el-input
            v-model="iconSearchText"
            placeholder="搜索图标"
            :prefix-icon="Search"
            clearable
          />
        </div>
        <div class="icon-grid">
          <div
            v-for="icon in filteredIcons"
            :key="icon"
            class="icon-item"
            :class="{ active: formData.icon === icon }"
            @click="selectIcon(icon)"
          >
            <el-icon>
              <component :is="icon" />
            </el-icon>
            <span class="icon-name">{{ icon }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="iconDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmIcon">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { permissionApi } from '@/api'
import { DateUtils, ArrayUtils } from '@/utils'
import type { Permission, ApiResponse } from '@/types'
import {
  Plus,
  Search,
  Refresh,
  Edit,
  Delete,
  Download,
  Sort,
  Menu,
  Document,
  Setting,
  User,
  Lock,
  Key,
  DataAnalysis,
  Monitor,
  Files,
  Bell,
  Message,
  Calendar,
  Camera,
  Phone,
  Location,
  Star,
  Heart,
  Share,
  Download as DownloadIcon,
  Upload,
  Refresh as RefreshIcon
} from '@element-plus/icons-vue'

// 表单引用
const formRef = ref<FormInstance>()

// 状态
const loading = ref(false)
const dialogVisible = ref(false)
const iconDialogVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const isExpandAll = ref(false)

// 搜索表单
const searchForm = reactive({
  name: '',
  code: '',
  type: '',
  status: ''
})

// 表格数据
const tableData = ref<Permission[]>([])
const selectedIds = ref<number[]>([])
const permissionTreeOptions = ref<Permission[]>([])

// 图标相关
const iconSearchText = ref('')
const selectedIcon = ref('')

// 可选图标列表
const iconList = [
  'Menu', 'Document', 'Setting', 'User', 'Lock', 'Key', 'DataAnalysis',
  'Monitor', 'Files', 'Bell', 'Message', 'Calendar', 'Camera', 'Phone',
  'Location', 'Star', 'Heart', 'Share', 'Download', 'Upload', 'Refresh',
  'Plus', 'Search', 'Edit', 'Delete', 'Sort'
]

// 表单数据
const formData = reactive({
  id: 0,
  name: '',
  code: '',
  type: 'menu',
  parentId: 0,
  path: '',
  component: '',
  apiPath: '',
  method: 'GET',
  icon: '',
  sort: 0,
  hidden: false,
  keepAlive: true,
  status: 1,
  description: ''
})

// 树选择器配置
const treeSelectProps = {
  value: 'id',
  label: 'name',
  children: 'children'
}

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入权限名称', trigger: 'blur' },
    { min: 2, max: 50, message: '权限名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入权限编码', trigger: 'blur' },
    { min: 2, max: 100, message: '权限编码长度在 2 到 100 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9:_-]+$/, message: '权限编码只能包含字母、数字、冒号、下划线和横线', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择权限类型', trigger: 'change' }
  ],
  path: [
    { validator: (_, value, callback) => {
      if (formData.type === 'menu' && !value) {
        callback(new Error('菜单类型必须填写路由路径'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  apiPath: [
    { validator: (_, value, callback) => {
      if (formData.type === 'api' && !value) {
        callback(new Error('接口类型必须填写接口路径'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  method: [
    { validator: (_, value, callback) => {
      if (formData.type === 'api' && !value) {
        callback(new Error('接口类型必须选择请求方法'))
      } else {
        callback()
      }
    }, trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => {
  if (isEdit.value) return '编辑权限'
  return formData.parentId ? '新增子权限' : '新增权限'
})

const filteredIcons = computed(() => {
  if (!iconSearchText.value) return iconList
  return iconList.filter(icon => 
    icon.toLowerCase().includes(iconSearchText.value.toLowerCase())
  )
})

// 获取权限列表
const fetchPermissions = async () => {
  try {
    loading.value = true
    const response = await permissionApi.getPermissionTree(searchForm)
    tableData.value = response.data
    
    // 构建树选择器选项（排除当前编辑的节点）
    buildTreeOptions()
  } catch (error: any) {
    ElMessage.error(error.message || '获取权限列表失败')
  } finally {
    loading.value = false
  }
}

// 构建树选择器选项
const buildTreeOptions = () => {
  const options = ArrayUtils.deepClone(tableData.value)
  // 添加根节点选项
  permissionTreeOptions.value = [
    { id: 0, name: '根权限', children: options }
  ]
}

// 搜索
const handleSearch = () => {
  fetchPermissions()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    code: '',
    type: '',
    status: ''
  })
  handleSearch()
}

// 切换展开/收起
const toggleExpandAll = () => {
  isExpandAll.value = !isExpandAll.value
  // 重新渲染表格以应用展开状态
  fetchPermissions()
}

// 新增权限
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 新增子权限
const handleAddChild = (row: Permission) => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
  formData.parentId = row.id
}

// 编辑权限
const handleEdit = (row: Permission) => {
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    code: row.code,
    type: row.type,
    parentId: row.parentId || 0,
    path: row.path || '',
    component: row.component || '',
    apiPath: row.apiPath || '',
    method: row.method || 'GET',
    icon: row.icon || '',
    sort: row.sort || 0,
    hidden: row.hidden || false,
    keepAlive: row.keepAlive !== false,
    status: row.status,
    description: row.description || ''
  })
}

// 删除权限
const handleDelete = async (row: Permission) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除权限 "${row.name}" 吗？删除后其子权限也将被删除！`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await permissionApi.deletePermission(row.id)
    ElMessage.success('删除成功')
    fetchPermissions()
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
      `确定要删除选中的 ${selectedIds.value.length} 个权限吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await permissionApi.batchDeletePermissions(selectedIds.value)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    fetchPermissions()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

// 状态变更
const handleStatusChange = async (row: Permission) => {
  try {
    await permissionApi.updatePermissionStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error: any) {
    ElMessage.error(error.message || '状态更新失败')
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
  }
}

// 导出权限
const handleExport = async () => {
  try {
    const response = await permissionApi.exportPermissions(searchForm)
    // 处理文件下载
    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `权限列表_${DateUtils.format(new Date(), 'YYYY-MM-DD')}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error: any) {
    ElMessage.error(error.message || '导出失败')
  }
}

// 表格选择变更
const handleSelectionChange = (selection: Permission[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 权限类型变更
const handleTypeChange = (type: string) => {
  // 根据类型清空相关字段
  if (type !== 'menu') {
    formData.path = ''
    formData.component = ''
    formData.hidden = false
    formData.keepAlive = true
  }
  if (type !== 'api') {
    formData.apiPath = ''
    formData.method = 'GET'
  }
}

// 选择图标
const selectIcon = (icon: string) => {
  selectedIcon.value = icon
}

// 确认图标选择
const confirmIcon = () => {
  formData.icon = selectedIcon.value
  iconDialogVisible.value = false
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
      await permissionApi.updatePermission(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await permissionApi.createPermission(formData)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    fetchPermissions()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    id: 0,
    name: '',
    code: '',
    type: 'menu',
    parentId: 0,
    path: '',
    component: '',
    apiPath: '',
    method: 'GET',
    icon: '',
    sort: 0,
    hidden: false,
    keepAlive: true,
    status: 1,
    description: ''
  })
  formRef.value?.resetFields()
}

// 初始化
onMounted(() => {
  fetchPermissions()
})
</script>

<style lang="scss" scoped>
.permissions-page {
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
  
  .permission-name {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .permission-icon {
      font-size: 16px;
      color: var(--primary-color);
    }
  }
}

.icon-selector {
  .icon-search {
    margin-bottom: 20px;
  }
  
  .icon-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 12px;
    max-height: 400px;
    overflow-y: auto;
    
    .icon-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 12px;
      border: 1px solid var(--border-color);
      border-radius: var(--border-radius-base);
      cursor: pointer;
      transition: all 0.2s ease;
      
      &:hover {
        border-color: var(--primary-color);
        background-color: var(--bg-color-overlay);
      }
      
      &.active {
        border-color: var(--primary-color);
        background-color: var(--primary-color-light-9);
        color: var(--primary-color);
      }
      
      .el-icon {
        font-size: 24px;
        margin-bottom: 8px;
      }
      
      .icon-name {
        font-size: 12px;
        text-align: center;
        word-break: break-all;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .permissions-page {
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
  
  .icon-grid {
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  }
}
</style>