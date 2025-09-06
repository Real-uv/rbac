import request from '@/utils/request'
import type { 
  SystemInfo, 
  OnlineUser, 
  OperationLog, 
  LoginLog, 
  Statistics,
  PageResponse,
  PageQuery 
} from '@/types'

/**
 * 系统监控API
 */
export class SystemApi {
  /**
   * 获取系统信息
   */
  static getSystemInfo() {
    return request.get<SystemInfo>('/system/monitor/info')
  }
  
  /**
   * 获取在线用户列表
   */
  static getOnlineUsers(params: PageQuery) {
    return request.get<PageResponse<OnlineUser>>('/system/monitor/online-users', params)
  }
  
  /**
   * 强制下线用户
   */
  static forceLogout(token: string) {
    return request.delete(`/system/monitor/online-users/${token}`)
  }
  
  /**
   * 获取系统统计数据
   */
  static getStatistics() {
    return request.get<Statistics>('/system/monitor/statistics')
  }
  
  /**
   * 获取服务器性能数据
   */
  static getPerformanceData() {
    return request.get<{
      cpu: number[]
      memory: number[]
      timestamps: string[]
    }>('/system/monitor/performance')
  }
  
  /**
   * 清理系统缓存
   */
  static clearCache() {
    return request.delete('/system/monitor/cache')
  }
  
  /**
   * 获取缓存信息
   */
  static getCacheInfo() {
    return request.get<{
      redis: {
        version: string
        uptime: number
        connectedClients: number
        usedMemory: number
        maxMemory: number
      }
    }>('/system/monitor/cache/info')
  }
}

/**
 * 日志管理API
 */
export class LogApi {
  /**
   * 分页查询操作日志
   */
  static getOperationLogs(params: PageQuery & {
    username?: string
    operation?: string
    startTime?: string
    endTime?: string
  }) {
    return request.get<PageResponse<OperationLog>>('/system/logs/operation', params)
  }
  
  /**
   * 分页查询登录日志
   */
  static getLoginLogs(params: PageQuery & {
    username?: string
    ip?: string
    status?: number
    startTime?: string
    endTime?: string
  }) {
    return request.get<PageResponse<LoginLog>>('/system/logs/login', params)
  }
  
  /**
   * 删除操作日志
   */
  static deleteOperationLog(id: number) {
    return request.delete(`/system/logs/operation/${id}`)
  }
  
  /**
   * 批量删除操作日志
   */
  static batchDeleteOperationLogs(ids: number[]) {
    return request.delete('/system/logs/operation/batch', { ids })
  }
  
  /**
   * 清空操作日志
   */
  static clearOperationLogs() {
    return request.delete('/system/logs/operation/clear')
  }
  
  /**
   * 删除登录日志
   */
  static deleteLoginLog(id: number) {
    return request.delete(`/system/logs/login/${id}`)
  }
  
  /**
   * 批量删除登录日志
   */
  static batchDeleteLoginLogs(ids: number[]) {
    return request.delete('/system/logs/login/batch', { ids })
  }
  
  /**
   * 清空登录日志
   */
  static clearLoginLogs() {
    return request.delete('/system/logs/login/clear')
  }
  
  /**
   * 导出操作日志
   */
  static exportOperationLogs(params: any) {
    return request.download('/system/logs/operation/export', params, '操作日志.xlsx')
  }
  
  /**
   * 导出登录日志
   */
  static exportLoginLogs(params: any) {
    return request.download('/system/logs/login/export', params, '登录日志.xlsx')
  }
  
  /**
   * 获取日志统计数据
   */
  static getLogStatistics() {
    return request.get<{
      operationCount: number
      loginCount: number
      todayOperationCount: number
      todayLoginCount: number
      recentOperations: Array<{
        date: string
        count: number
      }>
      recentLogins: Array<{
        date: string
        count: number
      }>
    }>('/system/logs/statistics')
  }
}