import request from '@/utils/request'
import type { Permission, PermissionQuery, PermissionForm } from '@/types'

/**
 * 权限管理API
 */
export class PermissionApi {
  /**
   * 获取权限树形列表
   */
  static getPermissionTree(params?: PermissionQuery) {
    return request.get<Permission[]>('/system/permissions/tree', params)
  }

  /**
   * 获取权限列表（平铺）
   */
  static getPermissions(params?: PermissionQuery) {
    return request.get<Permission[]>('/system/permissions', params)
  }

  /**
   * 根据ID获取权限详情
   */
  static getPermissionById(id: number) {
    return request.get<Permission>(`/system/permissions/${id}`)
  }

  /**
   * 创建权限
   */
  static createPermission(data: PermissionForm) {
    return request.post<Permission>('/system/permissions', data)
  }

  /**
   * 更新权限
   */
  static updatePermission(id: number, data: PermissionForm) {
    return request.put<Permission>(`/system/permissions/${id}`, data)
  }

  /**
   * 删除权限
   */
  static deletePermission(id: number) {
    return request.delete(`/system/permissions/${id}`)
  }

  /**
   * 启用/禁用权限
   */
  static togglePermissionStatus(id: number, status: number) {
    return request.put(`/system/permissions/${id}/status`, { status })
  }

  /**
   * 检查权限名称是否存在
   */
  static checkPermissionName(permissionName: string, excludeId?: number) {
    return request.get<boolean>('/system/permissions/check-name', {
      permissionName,
      excludeId
    })
  }

  /**
   * 检查权限编码是否存在
   */
  static checkPermissionCode(permissionCode: string, excludeId?: number) {
    return request.get<boolean>('/system/permissions/check-code', {
      permissionCode,
      excludeId
    })
  }

  /**
   * 获取菜单权限树（用于前端路由）
   */
  static getMenuTree() {
    return request.get<Permission[]>('/system/permissions/tree')
  }

  /**
   * 获取按钮权限列表
   */
  static getButtonPermissions(menuId?: number) {
    return request.get<Permission[]>('/system/permissions/buttons', { menuId })
  }
}
