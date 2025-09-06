import request from '@/utils/request'
import type { LoginRequest, LoginResponse, User } from '@/types'

/**
 * 认证相关API
 */
export class AuthApi {
  /**
   * 获取验证码
   */
  static getCaptcha() {
    return request.get<{ captchaKey: string; captchaImage: string }>('/auth/captcha')
  }
  
  /**
   * 用户登录
   */
  static login(data: LoginRequest) {
    return request.post<LoginResponse>('/auth/login', data)
  }
  
  /**
   * 用户登出
   */
  static logout() {
    return request.post('/auth/logout')
  }
  
  /**
   * 刷新Token
   */
  static refreshToken(refreshToken: string) {
    return request.post<{ token: string; refreshToken: string }>('/auth/refresh', {
      refreshToken
    })
  }
  
  /**
   * 获取当前用户信息
   */
  static getCurrentUser() {
    return request.get<User>('/auth/user/info')
  }
  
  /**
   * 获取当前用户权限
   */
  static getCurrentUserPermissions() {
    return request.get<string[]>('/auth/user/permissions')
  }
  
  /**
   * 修改密码
   */
  static changePassword(data: {
    oldPassword: string
    newPassword: string
    confirmPassword: string
  }) {
    return request.post('/auth/user/change-password', data)
  }
  
  /**
   * 更新用户信息
   */
  static updateProfile(data: Partial<User>) {
    return request.put<User>('/auth/user/profile', data)
  }
}