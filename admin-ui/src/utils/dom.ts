/**
 * DOM 工具类
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
   * 获取元素的位置信息
   */
  static getElementPosition(element: Element) {
    const rect = element.getBoundingClientRect()
    return {
      top: rect.top + window.scrollY,
      left: rect.left + window.scrollX,
      width: rect.width,
      height: rect.height
    }
  }
  
  /**
   * 检查元素是否在视口内
   */
  static isElementInViewport(element: Element): boolean {
    const rect = element.getBoundingClientRect()
    return (
      rect.top >= 0 &&
      rect.left >= 0 &&
      rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
      rect.right <= (window.innerWidth || document.documentElement.clientWidth)
    )
  }
  
  /**
   * 滚动到指定元素
   */
  static scrollToElement(element: Element, options?: ScrollIntoViewOptions): void {
    element.scrollIntoView({
      behavior: 'smooth',
      block: 'center',
      inline: 'nearest',
      ...options
    })
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
  
  /**
   * 防抖函数
   */
  static debounce<T extends (...args: any[]) => any>(
    func: T,
    wait: number,
    immediate = false
  ): (...args: Parameters<T>) => void {
    let timeout: NodeJS.Timeout | null = null
    
    return function executedFunction(...args: Parameters<T>) {
      const later = () => {
        timeout = null
        if (!immediate) func(...args)
      }
      
      const callNow = immediate && !timeout
      
      if (timeout) clearTimeout(timeout)
      timeout = setTimeout(later, wait)
      
      if (callNow) func(...args)
    }
  }
  
  /**
   * 节流函数
   */
  static throttle<T extends (...args: any[]) => any>(
    func: T,
    limit: number
  ): (...args: Parameters<T>) => void {
    let inThrottle: boolean
    
    return function executedFunction(...args: Parameters<T>) {
      if (!inThrottle) {
        func.apply(this, args)
        inThrottle = true
        setTimeout(() => inThrottle = false, limit)
      }
    }
  }
  
  /**
   * 添加事件监听器（支持多个事件）
   */
  static addEventListener(
    element: Element | Window | Document,
    events: string | string[],
    handler: EventListener,
    options?: boolean | AddEventListenerOptions
  ): () => void {
    const eventList = Array.isArray(events) ? events : [events]
    
    eventList.forEach(event => {
      element.addEventListener(event, handler, options)
    })
    
    // 返回移除监听器的函数
    return () => {
      eventList.forEach(event => {
        element.removeEventListener(event, handler, options)
      })
    }
  }
  
  /**
   * 获取CSS变量值
   */
  static getCSSVariable(name: string, element?: Element): string {
    const el = element || document.documentElement
    return getComputedStyle(el).getPropertyValue(name).trim()
  }
  
  /**
   * 设置CSS变量值
   */
  static setCSSVariable(name: string, value: string, element?: Element): void {
    const el = element || document.documentElement
    ;(el as HTMLElement).style.setProperty(name, value)
  }
}