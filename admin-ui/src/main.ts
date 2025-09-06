import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import '@/assets/styles/index.scss'

import App from './App.vue'
import router from './router'
import pinia from './stores'

// 创建应用实例
const app = createApp(App)

// 注册 Pinia
app.use(pinia)

// 注册路由
app.use(router)

// 注册 Element Plus
app.use(ElementPlus)

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
	app.component(key, component)
}

// 全局错误处理
app.config.errorHandler = (err, vm, info) => {
	console.error('Global error:', err, info)
}

// 挂载应用
app.mount('#app')
