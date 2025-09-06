import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { AuthApi } from '@/api'
import type { User, LoginRequest, LoginResponse } from '@/types'
import { ElMessage } from 'element-plus'
import Cookies from 'js-cookie'

const TOKEN_KEY = 'rbac_token'
const REFRESH_TOKEN_KEY = 'rbac_refresh_token'
const USER_INFO_KEY = 'rbac_user_info'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(Cookies.get(TOKEN_KEY) || '')
  const refreshToken = ref<string>(Cookies.get(REFRESH_TOKEN_KEY) || '')
  const userInfo = ref<User | null>(
    localStorage.getItem(USER_INFO_KEY)
      ? JSON.parse(localStorage.getItem(USER_INFO_KEY)!)
      : null
  )
  const permissions = ref<string[]>([])
  const routesGenerated = ref<boolean>(false)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userId = computed(() => userInfo.value?.id)
  const username = computed(() => userInfo.value?.username)
  const nickname = computed(() => userInfo.value?.nickname)
  const avatar = computed(() => userInfo.value?.avatar)
  const roles = computed(() => userInfo.value?.roles || [])

  /**
   * 登录
   */
  const login = async (loginData: LoginRequest): Promise<LoginResponse> => {
    try {
      const response = await AuthApi.login(loginData)

      // 保存token
      setToken(response.token, response.refreshToken)

      // 保存用户信息
      setUserInfo(response.user)

      // 保存权限
      permissions.value = response.permissions

      ElMessage.success('登录成功')
      return response
    } catch (error) {
      throw error
    }
  }

  /**
   * 获取验证码
   */
  const getCaptcha = async () => {
    return AuthApi.getCaptcha()
  }

  /**
   * 登出
   */
  const logout = async () => {
    try {
      if (token.value) {
        await AuthApi.logout()
      }
    } catch (error) {
      console.error('登出请求失败:', error)
    } finally {
      // 清除本地数据
      clearUserData()
      ElMessage.success('已退出登录')
    }
  }

  /**
   * 获取用户信息
   */
  const getUserInfo = async () => {
    try {
      const user = await AuthApi.getCurrentUser()
      setUserInfo(user)

      const perms = await AuthApi.getCurrentUserPermissions()
      permissions.value = perms

      return user
    } catch (error) {
      // 获取用户信息失败，清除登录状态
      clearUserData()
      throw error
    }
  }

  /**
   * 刷新Token
   */
  const refreshAccessToken = async () => {
    try {
      if (!refreshToken.value) {
        throw new Error('No refresh token')
      }

      const response = await AuthApi.refreshToken(refreshToken.value)
      setToken(response.token, response.refreshToken)

      return response.token
    } catch (error) {
      // 刷新失败，清除登录状态
      clearUserData()
      throw error
    }
  }

  /**
   * 更新用户信息
   */
  const updateUserInfo = async (data: Partial<User>) => {
    try {
      const updatedUser = await AuthApi.updateProfile(data)
      setUserInfo(updatedUser)
      ElMessage.success('个人信息更新成功')
      return updatedUser
    } catch (error) {
      throw error
    }
  }

  /**
   * 修改密码
   */
  const changePassword = async (data: {
    oldPassword: string
    newPassword: string
    confirmPassword: string
  }) => {
    try {
      await AuthApi.changePassword(data)
      ElMessage.success('密码修改成功，请重新登录')
      // 修改密码后需要重新登录
      logout()
    } catch (error) {
      throw error
    }
  }

  /**
   * 检查权限
   */
  const hasPermission = (permission: string): boolean => {
    return permissions.value.includes(permission)
  }

  /**
   * 检查角色
   */
  const hasRole = (roleCode: string): boolean => {
    return roles.value.some(role => role.roleCode === roleCode)
  }

  /**
   * 检查是否为超级管理员
   */
  const isSuperAdmin = computed(() => {
    return hasRole('SUPER_ADMIN')
  })

  /**
   * 设置Token
   */
  const setToken = (accessToken: string, refreshTokenValue: string) => {
    token.value = accessToken
    refreshToken.value = refreshTokenValue

    // 保存到Cookie
    Cookies.set(TOKEN_KEY, accessToken, { expires: 7 })
    Cookies.set(REFRESH_TOKEN_KEY, refreshTokenValue, { expires: 30 })
  }

  /**
   * 设置用户信息
   */
  const setUserInfo = (user: User) => {
    userInfo.value = user
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(user))
  }

  /**
   * 设置路由生成状态
   */
  const setRoutesGenerated = (generated: boolean) => {
    routesGenerated.value = generated
  }

  /**
   * 清除用户数据
   */
  const clearUserData = () => {
    token.value = ''
    refreshToken.value = ''
    userInfo.value = null
    permissions.value = []
    routesGenerated.value = false

    // 清除本地存储
    Cookies.remove(TOKEN_KEY)
    Cookies.remove(REFRESH_TOKEN_KEY)
    localStorage.removeItem(USER_INFO_KEY)
  }

  return {
    // 状态
    token,
    refreshToken,
    userInfo,
    permissions,
    routesGenerated,

    // 计算属性
    isLoggedIn,
    userId,
    username,
    nickname,
    avatar,
    roles,
    isSuperAdmin,

    // 方法
    login,
    logout,
    getCaptcha,
    getUserInfo,
    refreshAccessToken,
    updateUserInfo,
    changePassword,
    hasPermission,
    hasRole,
    setToken,
    setUserInfo,
    setRoutesGenerated,
    clearUserData
  }
})
