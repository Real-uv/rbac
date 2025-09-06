// 通用类型定义

// API响应基础类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 分页响应类型
export interface PageResponse<T = any> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 分页查询参数
export interface PageQuery {
  current: number
  size: number
  [key: string]: any
}

// 用户相关类型
export interface User {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar?: string
  status: number
  createTime: string
  updateTime: string
  roles?: Role[]
}

// 角色类型
export interface Role {
  id: number
  roleName: string
  roleCode: string
  description: string
  status: number
  createTime: string
  updateTime: string
  permissions?: Permission[]
}

// 权限类型
export interface Permission {
  id: number
  permissionName: string
  permissionCode: string
  type: number
  parentId: number
  path?: string
  component?: string
  icon?: string
  sort: number
  status: number
  createTime: string
  updateTime: string
  children?: Permission[]
}

// 登录请求类型
export interface LoginRequest {
  username: string
  password: string
  captcha: string
  captchaKey: string
  rememberMe?: boolean
}

// 登录响应类型
export interface LoginResponse {
  token: string
  refreshToken: string
  user: User
  permissions: string[]
}

// 用户查询参数
export interface UserQuery extends PageQuery {
  username?: string
  nickname?: string
  email?: string
  phone?: string
  status?: number
  startTime?: string
  endTime?: string
}

// 用户创建/更新参数
export interface UserForm {
  id?: number
  username: string
  nickname: string
  email: string
  phone: string
  password?: string
  status: number
  roleIds: number[]
}

// 角色查询参数
export interface RoleQuery extends PageQuery {
  roleName?: string
  roleCode?: string
  status?: number
}

// 角色创建/更新参数
export interface RoleForm {
  id?: number
  roleName: string
  roleCode: string
  description: string
  status: number
  permissionIds: number[]
}

// 权限查询参数
export interface PermissionQuery {
  permissionName?: string
  type?: number
  status?: number
}

// 权限创建/更新参数
export interface PermissionForm {
  id?: number
  permissionName: string
  permissionCode: string
  type: number
  parentId: number
  path?: string
  component?: string
  icon?: string
  sort: number
  status: number
}

// 操作日志类型
export interface OperationLog {
  id: number
  userId: number
  username: string
  operation: string
  method: string
  params: string
  result: string
  ip: string
  location: string
  userAgent: string
  executeTime: number
  createTime: string
}

// 登录日志类型
export interface LoginLog {
  id: number
  userId: number
  username: string
  ip: string
  location: string
  userAgent: string
  status: number
  message: string
  createTime: string
}

// 系统信息类型
export interface SystemInfo {
  cpu: {
    name: string
    usage: number
  }
  memory: {
    total: number
    used: number
    free: number
    usage: number
  }
  disk: {
    total: number
    used: number
    free: number
    usage: number
  }
  jvm: {
    version: string
    totalMemory: number
    usedMemory: number
    freeMemory: number
    usage: number
  }
}

// 在线用户类型
export interface OnlineUser {
  token: string
  userId: number
  username: string
  nickname: string
  ip: string
  location: string
  browser: string
  os: string
  loginTime: string
  lastAccessTime: string
}

// 菜单类型
export interface Menu {
  id: number
  title: string
  path: string
  component?: string
  icon?: string
  sort: number
  hidden: boolean
  children?: Menu[]
}

// 路由元信息
export interface RouteMeta {
  title: string
  icon?: string
  hidden?: boolean
  keepAlive?: boolean
  permissions?: string[]
}

// 表格列配置
export interface TableColumn {
  prop: string
  label: string
  width?: number
  minWidth?: number
  fixed?: boolean | string
  sortable?: boolean
  formatter?: (row: any, column: any, cellValue: any) => string
}

// 表单规则
export interface FormRule {
  required?: boolean
  message?: string
  trigger?: string | string[]
  min?: number
  max?: number
  pattern?: RegExp
  validator?: (rule: any, value: any, callback: any) => void
}

// 字典类型
export interface DictItem {
  label: string
  value: any
  color?: string
  disabled?: boolean
}

// 上传文件类型
export interface UploadFile {
  name: string
  url: string
  size: number
  type: string
}

// 统计数据类型
export interface Statistics {
  userCount: number
  roleCount: number
  permissionCount: number
  onlineUserCount: number
  todayLoginCount: number
  totalLoginCount: number
}

// 图表数据类型
export interface ChartData {
  name: string
  value: number
}

// 导出状态枚举
export enum ExportStatus {
  PENDING = 0,
  SUCCESS = 1,
  FAILED = 2
}

// 用户状态枚举
export enum UserStatus {
  DISABLED = 0,
  ENABLED = 1
}

// 角色状态枚举
export enum RoleStatus {
  DISABLED = 0,
  ENABLED = 1
}

// 权限类型枚举
export enum PermissionType {
  MENU = 1,
  BUTTON = 2
}

// 权限状态枚举
export enum PermissionStatus {
  DISABLED = 0,
  ENABLED = 1
}