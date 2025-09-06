import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { PermissionApi } from '@/api'
import type { Permission, Menu } from '@/types'
import { useUserStore } from './user'

export const usePermissionStore = defineStore('permission', () => {
  // 状态
  const routes = ref<Permission[]>([])
  const menuTree = ref<Menu[]>([])
  const buttonPermissions = ref<string[]>([])

  // 计算属性
  const hasRoutes = computed(() => routes.value.length > 0)

  /**
   * 生成路由
   */
  const generateRoutes = async () => {
    try {
      const userStore = useUserStore()

      // 如果是超级管理员，获取所有菜单
      if (userStore.isSuperAdmin) {
        const allMenus = await PermissionApi.getMenuTree()
        routes.value = allMenus
        menuTree.value = transformToMenuTree(allMenus)
      } else {
        // 根据用户权限获取菜单
        const userMenus = await PermissionApi.getMenuTree()
        const filteredMenus = filterMenusByPermissions(userMenus, userStore.permissions)
        routes.value = filteredMenus
        menuTree.value = transformToMenuTree(filteredMenus)
      }

      return routes.value
    } catch (error) {
      console.error('生成路由失败:', error)
      throw error
    }
  }

  /**
   * 获取按钮权限
   */
  const getButtonPermissions = async (menuId?: number) => {
    try {
      const buttons = await PermissionApi.getButtonPermissions(menuId)
      buttonPermissions.value = buttons.map(btn => btn.permissionCode)
      return buttonPermissions.value
    } catch (error) {
      console.error('获取按钮权限失败:', error)
      throw error
    }
  }

  /**
   * 检查按钮权限
   */
  const hasButtonPermission = (permission: string): boolean => {
    const userStore = useUserStore()

    // 超级管理员拥有所有权限
    if (userStore.isSuperAdmin) {
      return true
    }

    return userStore.hasPermission(permission)
  }

  /**
   * 根据权限过滤菜单
   */
  const filterMenusByPermissions = (menus: Permission[], permissions: string[]): Permission[] => {
    return menus.filter(menu => {
      // 如果有子菜单，递归过滤
      if (menu.children && menu.children.length > 0) {
        menu.children = filterMenusByPermissions(menu.children, permissions)
        // 如果子菜单被过滤完了，但父菜单有权限，保留父菜单
        return permissions.includes(menu.permissionCode) || menu.children.length > 0
      }

      // 检查是否有权限
      return permissions.includes(menu.permissionCode)
    })
  }

  /**
   * 转换为菜单树结构
   */
  const transformToMenuTree = (permissions: Permission[]): Menu[] => {
    return permissions.map(permission => ({
      id: permission.id,
      title: permission.permissionName,
      path: permission.path || '',
      component: permission.component,
      icon: permission.icon,
      sort: permission.sort,
      hidden: permission.status === 0,
      children: permission.children ? transformToMenuTree(permission.children) : undefined
    }))
  }

  /**
   * 获取面包屑导航
   */
  const getBreadcrumbs = (path: string): Array<{ title: string; path?: string }> => {
    const breadcrumbs: Array<{ title: string; path?: string }> = []

    const findPath = (menus: Menu[], targetPath: string, parents: Menu[] = []): boolean => {
      for (const menu of menus) {
        const currentPath = [...parents, menu]

        if (menu.path === targetPath) {
          // 找到目标路径，构建面包屑
          currentPath.forEach((item, index) => {
            breadcrumbs.push({
              title: item.title,
              path: index === currentPath.length - 1 ? undefined : item.path
            })
          })
          return true
        }

        if (menu.children && menu.children.length > 0) {
          if (findPath(menu.children, targetPath, currentPath)) {
            return true
          }
        }
      }
      return false
    }

    findPath(menuTree.value, path)
    return breadcrumbs
  }

  /**
   * 获取菜单标题
   */
  const getMenuTitle = (path: string): string => {
    const findTitle = (menus: Menu[]): string => {
      for (const menu of menus) {
        if (menu.path === path) {
          return menu.title
        }
        if (menu.children && menu.children.length > 0) {
          const title = findTitle(menu.children)
          if (title) return title
        }
      }
      return ''
    }

    return findTitle(menuTree.value)
  }

  /**
   * 重置权限数据
   */
  const resetPermissions = () => {
    routes.value = []
    menuTree.value = []
    buttonPermissions.value = []
  }

  return {
    // 状态
    routes,
    menuTree,
    buttonPermissions,

    // 计算属性
    hasRoutes,

    // 方法
    generateRoutes,
    transformToMenuTree,
    getButtonPermissions,
    hasButtonPermission,
    getBreadcrumbs,
    getMenuTitle,
    resetPermissions
  }
})
