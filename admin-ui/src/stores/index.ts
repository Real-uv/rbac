import { createPinia } from 'pinia'

// 创建pinia实例
const pinia = createPinia()

// 导出stores
export { useUserStore } from './user'
export { useAppStore } from './app'
export { usePermissionStore } from './permission'

export default pinia