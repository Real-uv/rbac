import request from '@/utils/request'
import type { Role, RoleQuery, RoleForm, PageResponse } from '@/types'

/**
 * 角色管理API
 */
export class RoleApi {
  /**
   * 分页查询角色列表
   */
  static getRoles(params: RoleQuery) {
    return request.get<PageResponse<Role>>('/system/roles', params)
  }
  
  /**
   * 获取所有角色列表（不分页）
   */
  static getAllRoles() {
    return request.get<Role[]>('/system/roles/all')
  }
  
  /**
   * 根据ID获取角色详情
   */
  static getRoleById(id: number) {
    return request.get<Role>(`/system/roles/${id}`)
  }
  
  /**
   * 创建角色
   */
  static createRole(data: RoleForm) {
    return request.post<Role>('/system/roles', data)
  }
  
  /**
   * 更新角色
   */
  static updateRole(id: number, data: RoleForm) {
    return request.put<Role>(`/system/roles/${id}`, data)
  }
  
  /**
   * 删除角色
   */
  static deleteRole(id: number) {
    return request.delete(`/system/roles/${id}`)
  }
  
  /**
   * 批量删除角色
   */
  static batchDeleteRoles(ids: number[]) {
    return request.delete('/system/roles/batch', { ids })
  }
  
  /**
   * 启用/禁用角色
   */
  static toggleRoleStatus(id: number, status: number) {
    return request.put(`/system/roles/${id}/status`, { status })
  }
  
  /**
   * 分配角色权限
   */
  static assignPermissions(roleId: number, permissionIds: number[]) {
    return request.post(`/system/roles/${roleId}/permissions`, { permissionIds })
  }
  
  /**
   * 获取角色权限列表
   */
  static getRolePermissions(roleId: number) {
    return request.get<number[]>(`/system/roles/${roleId}/permissions`)
  }
  
  /**
   * 检查角色名称是否存在
   */
  static checkRoleName(roleName: string, excludeId?: number) {
    return request.get<boolean>('/system/roles/check-name', {
      roleName,
      excludeId
    })
  }
  
  /**
   * 检查角色编码是否存在
   */
  static checkRoleCode(roleCode: string, excludeId?: number) {
    return request.get<boolean>('/system/roles/check-code', {
      roleCode,
      excludeId
    })
  }
  
  /**
   * 获取角色使用统计
   */
  static getRoleStats(roleId: number) {
    return request.get<{
      userCount: number
      permissionCount: number
      lastAssignTime: string
    }>(`/system/roles/${roleId}/stats`)
  }
}