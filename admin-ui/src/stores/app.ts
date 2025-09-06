import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 侧边栏状态
  const sidebarCollapsed = ref<boolean>(
    localStorage.getItem('sidebar_collapsed') === 'true'
  )
  
  // 设备类型
  const device = ref<'desktop' | 'tablet' | 'mobile'>('desktop')
  
  // 主题模式
  const isDark = ref<boolean>(
    localStorage.getItem('theme_mode') === 'dark'
  )
  
  // 语言设置
  const language = ref<string>(
    localStorage.getItem('language') || 'zh-CN'
  )
  
  // 页面加载状态
  const pageLoading = ref<boolean>(false)
  
  // 全局加载状态
  const globalLoading = ref<boolean>(false)
  
  // 面包屑导航
  const breadcrumbs = ref<Array<{
    title: string
    path?: string
  }>>([])
  
  // 标签页
  const tabs = ref<Array<{
    name: string
    title: string
    path: string
    closable: boolean
  }>>([])
  
  // 当前激活的标签页
  const activeTab = ref<string>('')
  
  /**
   * 切换侧边栏
   */
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
    localStorage.setItem('sidebar_collapsed', sidebarCollapsed.value.toString())
  }
  
  /**
   * 设置设备类型
   */
  const setDevice = (deviceType: 'desktop' | 'tablet' | 'mobile') => {
    device.value = deviceType
    
    // 移动端自动收起侧边栏
    if (deviceType === 'mobile') {
      sidebarCollapsed.value = true
    }
  }
  
  /**
   * 切换主题
   */
  const toggleTheme = () => {
    isDark.value = !isDark.value
    localStorage.setItem('theme_mode', isDark.value ? 'dark' : 'light')
    
    // 更新HTML类名
    const html = document.documentElement
    if (isDark.value) {
      html.classList.add('dark')
    } else {
      html.classList.remove('dark')
    }
  }
  
  /**
   * 设置语言
   */
  const setLanguage = (lang: string) => {
    language.value = lang
    localStorage.setItem('language', lang)
  }
  
  /**
   * 设置页面加载状态
   */
  const setPageLoading = (loading: boolean) => {
    pageLoading.value = loading
  }
  
  /**
   * 设置全局加载状态
   */
  const setGlobalLoading = (loading: boolean) => {
    globalLoading.value = loading
  }
  
  /**
   * 设置面包屑
   */
  const setBreadcrumbs = (crumbs: Array<{ title: string; path?: string }>) => {
    breadcrumbs.value = crumbs
  }
  
  /**
   * 添加标签页
   */
  const addTab = (tab: {
    name: string
    title: string
    path: string
    closable?: boolean
  }) => {
    const existingTab = tabs.value.find(t => t.name === tab.name)
    if (!existingTab) {
      tabs.value.push({
        ...tab,
        closable: tab.closable !== false
      })
    }
    activeTab.value = tab.name
  }
  
  /**
   * 移除标签页
   */
  const removeTab = (tabName: string) => {
    const index = tabs.value.findIndex(tab => tab.name === tabName)
    if (index > -1) {
      tabs.value.splice(index, 1)
      
      // 如果关闭的是当前激活的标签页，切换到其他标签页
      if (activeTab.value === tabName && tabs.value.length > 0) {
        const newIndex = index >= tabs.value.length ? tabs.value.length - 1 : index
        activeTab.value = tabs.value[newIndex].name
      }
    }
  }
  
  /**
   * 设置激活的标签页
   */
  const setActiveTab = (tabName: string) => {
    activeTab.value = tabName
  }
  
  /**
   * 关闭其他标签页
   */
  const closeOtherTabs = (keepTabName: string) => {
    tabs.value = tabs.value.filter(tab => tab.name === keepTabName || !tab.closable)
    activeTab.value = keepTabName
  }
  
  /**
   * 关闭所有标签页
   */
  const closeAllTabs = () => {
    tabs.value = tabs.value.filter(tab => !tab.closable)
    if (tabs.value.length > 0) {
      activeTab.value = tabs.value[0].name
    } else {
      activeTab.value = ''
    }
  }
  
  /**
   * 初始化应用设置
   */
  const initApp = () => {
    // 初始化主题
    const html = document.documentElement
    if (isDark.value) {
      html.classList.add('dark')
    } else {
      html.classList.remove('dark')
    }
    
    // 监听窗口大小变化
    const handleResize = () => {
      const width = window.innerWidth
      if (width < 768) {
        setDevice('mobile')
      } else if (width < 1024) {
        setDevice('tablet')
      } else {
        setDevice('desktop')
      }
    }
    
    handleResize()
    window.addEventListener('resize', handleResize)
    
    return () => {
      window.removeEventListener('resize', handleResize)
    }
  }
  
  return {
    // 状态
    sidebarCollapsed,
    device,
    isDark,
    language,
    pageLoading,
    globalLoading,
    breadcrumbs,
    tabs,
    activeTab,
    
    // 方法
    toggleSidebar,
    setDevice,
    toggleTheme,
    setLanguage,
    setPageLoading,
    setGlobalLoading,
    setBreadcrumbs,
    addTab,
    removeTab,
    setActiveTab,
    closeOtherTabs,
    closeAllTabs,
    initApp
  }
})