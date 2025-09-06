import dayjs from 'dayjs'
import CryptoJS from 'crypto-js'

/**
 * 日期时间工具
 */
export class DateUtils {
  /**
   * 格式化日期
   */
  static format(date: string | Date, format = 'YYYY-MM-DD HH:mm:ss'): string {
    return dayjs(date).format(format)
  }
  
  /**
   * 获取相对时间
   */
  static fromNow(date: string | Date): string {
    return dayjs(date).fromNow()
  }
  
  /**
   * 获取今天开始时间
   */
  static startOfToday(): string {
    return dayjs().startOf('day').format('YYYY-MM-DD HH:mm:ss')
  }
  
  /**
   * 获取今天结束时间
   */
  static endOfToday(): string {
    return dayjs().endOf('day').format('YYYY-MM-DD HH:mm:ss')
  }
  
  /**
   * 获取本周开始时间
   */
  static startOfWeek(): string {
    return dayjs().startOf('week').format('YYYY-MM-DD HH:mm:ss')
  }
  
  /**
   * 获取本月开始时间
   */
  static startOfMonth(): string {
    return dayjs().startOf('month').format('YYYY-MM-DD HH:mm:ss')
  }
}

/**
 * 字符串工具
 */
export class StringUtils {
  /**
   * 首字母大写
   */
  static capitalize(str: string): string {
    return str.charAt(0).toUpperCase() + str.slice(1)
  }
  
  /**
   * 驼峰转下划线
   */
  static camelToSnake(str: string): string {
    return str.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`)
  }
  
  /**
   * 下划线转驼峰
   */
  static snakeToCamel(str: string): string {
    return str.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase())
  }
  
  /**
   * 生成随机字符串
   */
  static randomString(length = 8): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
    let result = ''
    for (let i = 0; i < length; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length))
    }
    return result
  }
  
  /**
   * 脱敏处理
   */
  static mask(str: string, start = 3, end = 4, maskChar = '*'): string {
    if (str.length <= start + end) {
      return str
    }
    const maskLength = str.length - start - end
    const mask = maskChar.repeat(maskLength)
    return str.substring(0, start) + mask + str.substring(str.length - end)
  }
}

/**
 * 数字工具
 */
export class NumberUtils {
  /**
   * 格式化文件大小
   */
  static formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 B'
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }
  
  /**
   * 格式化数字（千分位）
   */
  static formatNumber(num: number): string {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
  }
  
  /**
   * 生成随机数
   */
  static random(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min
  }
  
  /**
   * 保留小数位
   */
  static toFixed(num: number, digits = 2): number {
    return parseFloat(num.toFixed(digits))
  }
}

/**
 * 数组工具
 */
export class ArrayUtils {
  /**
   * 数组去重
   */
  static unique<T>(arr: T[]): T[] {
    return [...new Set(arr)]
  }
  
  /**
   * 数组分组
   */
  static groupBy<T>(arr: T[], key: keyof T): Record<string, T[]> {
    return arr.reduce((groups, item) => {
      const group = String(item[key])
      groups[group] = groups[group] || []
      groups[group].push(item)
      return groups
    }, {} as Record<string, T[]>)
  }
  
  /**
   * 数组转树形结构
   */
  static arrayToTree<T extends { id: any; parentId: any; children?: T[] }>(
    arr: T[],
    parentId: any = 0
  ): T[] {
    return arr
      .filter(item => item.parentId === parentId)
      .map(item => ({
        ...item,
        children: this.arrayToTree(arr, item.id)
      }))
  }
  
  /**
   * 树形结构转数组
   */
  static treeToArray<T extends { children?: T[] }>(tree: T[]): T[] {
    const result: T[] = []
    
    const traverse = (nodes: T[]) => {
      nodes.forEach(node => {
        const { children, ...rest } = node
        result.push(rest as T)
        if (children && children.length > 0) {
          traverse(children)
        }
      })
    }
    
    traverse(tree)
    return result
  }
}

/**
 * 对象工具
 */
export class ObjectUtils {
  /**
   * 深拷贝
   */
  static deepClone<T>(obj: T): T {
    if (obj === null || typeof obj !== 'object') {
      return obj
    }
    
    if (obj instanceof Date) {
      return new Date(obj.getTime()) as unknown as T
    }
    
    if (obj instanceof Array) {
      return obj.map(item => this.deepClone(item)) as unknown as T
    }
    
    if (typeof obj === 'object') {
      const cloned = {} as T
      Object.keys(obj).forEach(key => {
        cloned[key as keyof T] = this.deepClone(obj[key as keyof T])
      })
      return cloned
    }
    
    return obj
  }
  
  /**
   * 移除对象中的空值
   */
  static removeEmpty(obj: Record<string, any>): Record<string, any> {
    const result: Record<string, any> = {}
    
    Object.keys(obj).forEach(key => {
      const value = obj[key]
      if (value !== null && value !== undefined && value !== '') {
        result[key] = value
      }
    })
    
    return result
  }
  
  /**
   * 对象属性重命名
   */
  static renameKeys(obj: Record<string, any>, keyMap: Record<string, string>): Record<string, any> {
    const result: Record<string, any> = {}
    
    Object.keys(obj).forEach(key => {
      const newKey = keyMap[key] || key
      result[newKey] = obj[key]
    })
    
    return result
  }
}

/**
 * 验证工具
 */
export class ValidateUtils {
  /**
   * 验证邮箱
   */
  static isEmail(email: string): boolean {
    const reg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
    return reg.test(email)
  }
  
  /**
   * 验证手机号
   */
  static isPhone(phone: string): boolean {
    const reg = /^1[3-9]\d{9}$/
    return reg.test(phone)
  }
  
  /**
   * 验证身份证号
   */
  static isIdCard(idCard: string): boolean {
    const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
    return reg.test(idCard)
  }
  
  /**
   * 验证URL
   */
  static isUrl(url: string): boolean {
    const reg = /^https?:\/\/.+/
    return reg.test(url)
  }
  
  /**
   * 验证IP地址
   */
  static isIP(ip: string): boolean {
    const reg = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/
    return reg.test(ip)
  }
  
  /**
   * 验证密码强度
   */
  static checkPasswordStrength(password: string): {
    score: number
    level: 'weak' | 'medium' | 'strong'
    suggestions: string[]
  } {
    let score = 0
    const suggestions: string[] = []
    
    // 长度检查
    if (password.length >= 8) {
      score += 1
    } else {
      suggestions.push('密码长度至少8位')
    }
    
    // 包含小写字母
    if (/[a-z]/.test(password)) {
      score += 1
    } else {
      suggestions.push('包含小写字母')
    }
    
    // 包含大写字母
    if (/[A-Z]/.test(password)) {
      score += 1
    } else {
      suggestions.push('包含大写字母')
    }
    
    // 包含数字
    if (/\d/.test(password)) {
      score += 1
    } else {
      suggestions.push('包含数字')
    }
    
    // 包含特殊字符
    if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
      score += 1
    } else {
      suggestions.push('包含特殊字符')
    }
    
    let level: 'weak' | 'medium' | 'strong'
    if (score <= 2) {
      level = 'weak'
    } else if (score <= 3) {
      level = 'medium'
    } else {
      level = 'strong'
    }
    
    return { score, level, suggestions }
  }
}

/**
 * 加密工具
 */
export class CryptoUtils {
  private static readonly SECRET_KEY = 'rbac-admin-secret-key'
  
  /**
   * AES加密
   */
  static encrypt(text: string): string {
    return CryptoJS.AES.encrypt(text, this.SECRET_KEY).toString()
  }
  
  /**
   * AES解密
   */
  static decrypt(cipherText: string): string {
    const bytes = CryptoJS.AES.decrypt(cipherText, this.SECRET_KEY)
    return bytes.toString(CryptoJS.enc.Utf8)
  }
  
  /**
   * MD5加密
   */
  static md5(text: string): string {
    return CryptoJS.MD5(text).toString()
  }
  
  /**
   * Base64编码
   */
  static base64Encode(text: string): string {
    return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(text))
  }
  
  /**
   * Base64解码
   */
  static base64Decode(base64: string): string {
    return CryptoJS.enc.Base64.parse(base64).toString(CryptoJS.enc.Utf8)
  }
}

/**
 * 存储工具
 */
export class StorageUtils {
  /**
   * 设置localStorage
   */
  static setLocal(key: string, value: any): void {
    try {
      localStorage.setItem(key, JSON.stringify(value))
    } catch (error) {
      console.error('设置localStorage失败:', error)
    }
  }
  
  /**
   * 获取localStorage
   */
  static getLocal<T>(key: string): T | null {
    try {
      const value = localStorage.getItem(key)
      return value ? JSON.parse(value) : null
    } catch (error) {
      console.error('获取localStorage失败:', error)
      return null
    }
  }
  
  /**
   * 删除localStorage
   */
  static removeLocal(key: string): void {
    try {
      localStorage.removeItem(key)
    } catch (error) {
      console.error('删除localStorage失败:', error)
    }
  }
  
  /**
   * 设置sessionStorage
   */
  static setSession(key: string, value: any): void {
    try {
      sessionStorage.setItem(key, JSON.stringify(value))
    } catch (error) {
      console.error('设置sessionStorage失败:', error)
    }
  }
  
  /**
   * 获取sessionStorage
   */
  static getSession<T>(key: string): T | null {
    try {
      const value = sessionStorage.getItem(key)
      return value ? JSON.parse(value) : null
    } catch (error) {
      console.error('获取sessionStorage失败:', error)
      return null
    }
  }
  
  /**
   * 删除sessionStorage
   */
  static removeSession(key: string): void {
    try {
      sessionStorage.removeItem(key)
    } catch (error) {
      console.error('删除sessionStorage失败:', error)
    }
  }
}

/**
 * DOM工具
 */
export class DomUtils {
  /**
   * 请求全屏
   */
  static requestFullscreen(element?: Element): Promise<void> {
    const el = element || document.documentElement
    
    if (el.requestFullscreen) {
      return el.requestFullscreen()
    } else if ((el as any).webkitRequestFullscreen) {
      return (el as any).webkitRequestFullscreen()
    } else if ((el as any).mozRequestFullScreen) {
      return (el as any).mozRequestFullScreen()
    } else if ((el as any).msRequestFullscreen) {
      return (el as any).msRequestFullscreen()
    }
    
    return Promise.reject(new Error('Fullscreen API is not supported'))
  }
  
  /**
   * 退出全屏
   */
  static exitFullscreen(): Promise<void> {
    if (document.exitFullscreen) {
      return document.exitFullscreen()
    } else if ((document as any).webkitExitFullscreen) {
      return (document as any).webkitExitFullscreen()
    } else if ((document as any).mozCancelFullScreen) {
      return (document as any).mozCancelFullScreen()
    } else if ((document as any).msExitFullscreen) {
      return (document as any).msExitFullscreen()
    }
    
    return Promise.reject(new Error('Fullscreen API is not supported'))
  }
  
  /**
   * 检查是否处于全屏状态
   */
  static isFullscreen(): boolean {
    return !!(
      document.fullscreenElement ||
      (document as any).webkitFullscreenElement ||
      (document as any).mozFullScreenElement ||
      (document as any).msFullscreenElement
    )
  }
  
  /**
   * 复制文本到剪贴板
   */
  static async copyToClipboard(text: string): Promise<void> {
    if (navigator.clipboard && window.isSecureContext) {
      return navigator.clipboard.writeText(text)
    } else {
      // 降级方案
      const textArea = document.createElement('textarea')
      textArea.value = text
      textArea.style.position = 'fixed'
      textArea.style.left = '-999999px'
      textArea.style.top = '-999999px'
      document.body.appendChild(textArea)
      textArea.focus()
      textArea.select()
      
      return new Promise((resolve, reject) => {
        if (document.execCommand('copy')) {
          resolve()
        } else {
          reject(new Error('Copy to clipboard failed'))
        }
        document.body.removeChild(textArea)
      })
    }
  }
  
  /**
   * 下载文件
   */
  static downloadFile(url: string, filename?: string): void {
    const link = document.createElement('a')
    link.href = url
    if (filename) {
      link.download = filename
    }
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
  
  /**
   * 下载Blob数据
   */
  static downloadBlob(blob: Blob, filename: string): void {
    const url = URL.createObjectURL(blob)
    this.downloadFile(url, filename)
    URL.revokeObjectURL(url)
  }
  
  /**
   * 获取设备类型
   */
  static getDeviceType(): 'mobile' | 'tablet' | 'desktop' {
    const width = window.innerWidth
    if (width < 768) {
      return 'mobile'
    } else if (width < 1024) {
      return 'tablet'
    } else {
      return 'desktop'
    }
  }
  
  /**
   * 检查是否为移动设备
   */
  static isMobile(): boolean {
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
  }
}