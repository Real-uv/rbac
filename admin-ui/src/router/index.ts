import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore, usePermissionStore } from '@/stores'
import { ElMessage } from 'element-plus'
import type { RouteRecordRaw } from 'vue-router'

// 导入布局组件
const Layout = () => import('@/layout/index.vue')

// 导入页面组件
const Login = () => import('@/views/Login.vue')
const Dashboard = () => import('@/views/Dashboard.vue')
const Users = () => import('@/views/system/Users.vue')
const Roles = () => import('@/views/system/Roles.vue')
const Permissions = () => import('@/views/system/Permissions.vue')

// 基础路由（不需要权限验证）
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      title: '登录',
      hidden: true
    }
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在',
      hidden: true
    }
  },
  {
    path: '/',
    name: 'Root',
    redirect: '/dashboard',
    meta: {
      hidden: true
    }
  }
]

// 动态路由（需要权限验证）
export const asyncRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: {
          title: '仪表板',
          icon: 'DataAnalysis',
          affix: true
        }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/users',
    meta: {
      title: '系统管理',
      icon: 'Setting'
    },
    children: [
      {
        path: 'users',
        name: 'SystemUsers',
        component: Users,
        meta: {
          title: '用户管理',
          icon: 'User',
          permissions: ['system:user:list']
        }
      },
      {
        path: 'roles',
        name: 'SystemRoles',
        component: Roles,
        meta: {
          title: '角色管理',
          icon: 'Lock',
          permissions: ['system:role:list']
        }
      },
      {
        path: 'permissions',
        name: 'SystemPermissions',
        component: Permissions,
        meta: {
          title: '权限管理',
          icon: 'Key',
          permissions: ['system:permission:list']
        }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'index',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: {
          title: '个人中心',
          icon: 'User'
        }
      }
    ]
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [...constantRoutes],
  scrollBehavior: () => ({ left: 0, top: 0 })
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const permissionStore = usePermissionStore() // 新增：获取permissionStore

  // 设置页面标题
  if (to.meta?.title) {
    document.title = `${to.meta.title} - RBAC管理系统`
  }

  // 白名单路由（不需要登录）
  const whiteList = ['/login', '/404']

  if (userStore.token) {
    if (to.path === '/login') {
      // 已登录用户访问登录页，重定向到首页
      next({ path: '/' })
    } else {
      // 检查是否已获取用户信息和动态路由
      if (!userStore.userInfo || !userStore.routesGenerated) {
        try {
          // 获取用户信息和权限
          if (!userStore.userInfo) {
            await userStore.getUserInfo()
          }

          // 修改：调用permissionStore.generateRoutes()来生成菜单树
          await permissionStore.generateRoutes()

          // 生成动态路由（保留原有逻辑，但使用permissionStore中的routes）
          const accessRoutes = filterAsyncRoutes(asyncRoutes, userStore.permissions)

          // 添加动态路由
          accessRoutes.forEach(route => {
            router.addRoute(route)
          })

          // 添加404路由（必须在最后）
          router.addRoute({
            path: '/:pathMatch(.*)*',
            redirect: '/404',
            meta: { hidden: true }
          })

          // 标记路由已生成
          userStore.setRoutesGenerated(true)

          // 重新导航到目标路由
          next({ ...to, replace: true })
        } catch (error) {
          // 获取用户信息失败，清除token并跳转到登录页
          userStore.logout()
          ElMessage.error('获取用户信息失败，请重新登录')
          next(`/login?redirect=${to.path}`)
        }
      } else {
        // 已有用户信息，检查路由权限
        if (hasPermission(userStore.permissions, to)) {
          next()
        } else {
          ElMessage.error('您没有访问该页面的权限')
          next('/404')
        }
      }
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

// 路由后置守卫
router.afterEach(() => {
  // 可以在这里添加页面加载完成后的逻辑
})

// 生成动态路由
async function generateRoutes(permissions: string[]): Promise<RouteRecordRaw[]> {
  // 过滤有权限的路由
  const accessedRoutes = filterAsyncRoutes(asyncRoutes, permissions)
  return accessedRoutes
}

// 过滤异步路由
function filterAsyncRoutes(routes: RouteRecordRaw[], permissions: string[]): RouteRecordRaw[] {
  const res: RouteRecordRaw[] = []

  routes.forEach(route => {
    const tmp = { ...route }

    if (hasPermission(permissions, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, permissions)
      }
      res.push(tmp)
    }
  })

  return res
}

// 检查权限
function hasPermission(permissions: string[], route: RouteRecordRaw | any): boolean {
  if (route.meta?.permissions) {
    // 检查是否有任一所需权限
    return route.meta.permissions.some((permission: string) =>
      permissions.includes(permission)
    )
  } else {
    // 没有权限要求的路由默认可访问
    return true
  }
}

// 重置路由
export function resetRouter() {
  const newRouter = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [...constantRoutes],
    scrollBehavior: () => ({ left: 0, top: 0 })
  })

  // 替换路由实例
  ;(router as any).matcher = (newRouter as any).matcher
}

export default router
