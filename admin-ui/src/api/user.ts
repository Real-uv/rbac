import request from '@/utils/request'
import type { User, UserQuery, UserForm, PageResponse } from '@/types'

/**
 * 用户管理API
 */
export class UserApi {
  /**
   * 分页查询用户列表
   */
  static getUsers(params: UserQuery) {
    return request.get<PageResponse<User>>('/system/users', params)
  }
  
  /**
   * 根据ID获取用户详情
   */
  static getUserById(id: number) {
    return request.get<User>(`/system/users/${id}`)
  }
  
  /**
   * 创建用户
   */
  static createUser(data: UserForm) {
    return request.post<User>('/system/users', data)
  }
  
  /**
   * 更新用户
   */
  static updateUser(id: number, data: UserForm) {
    return request.put<User>(`/system/users/${id}`, data)
  }
  
  /**
   * 删除用户
   */
  static deleteUser(id: number) {
    return request.delete(`/system/users/${id}`)
  }
  
  /**
   * 批量删除用户
   */
  static batchDeleteUsers(ids: number[]) {
    return request.delete('/system/users/batch', { ids })
  }
  
  /**
   * 启用/禁用用户
   */
  static toggleUserStatus(id: number, status: number) {
    return request.put(`/system/users/${id}/status`, { status })
  }
  
  /**
   * 重置用户密码
   */
  static resetPassword(id: number, newPassword: string) {
    return request.put(`/system/users/${id}/reset-password`, { newPassword })
  }
  
  /**
   * 分配用户角色
   */
  static assignRoles(userId: number, roleIds: number[]) {
    return request.post(`/system/users/${userId}/roles`, { roleIds })
  }
  
  /**
   * 获取用户角色列表
   */
  static getUserRoles(userId: number) {
    return request.get<number[]>(`/system/users/${userId}/roles`)
  }
  
  /**
   * 导出用户数据
   */
  static exportUsers(params: UserQuery) {
    return request.download('/system/users/export', params, '用户数据.xlsx')
  }
  
  /**
   * 导入用户数据
   */
  static importUsers(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.upload('/system/users/import', formData)
  }
  
  /**
   * 检查用户名是否存在
   */
  static checkUsername(username: string, excludeId?: number) {
    return request.get<boolean>('/system/users/check-username', {
      username,
      excludeId
    })
  }
  
  /**
   * 检查邮箱是否存在
   */
  static checkEmail(email: string, excludeId?: number) {
    return request.get<boolean>('/system/users/check-email', {
      email,
      excludeId
    })
  }
  
  /**
   * 检查手机号是否存在
   */
  static checkPhone(phone: string, excludeId?: number) {
    return request.get<boolean>('/system/users/check-phone', {
      phone,
      excludeId
    })
  }
}