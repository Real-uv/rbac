<template>
  <div class="header">
    <!-- 左侧区域 -->
    <div class="header-left">
      <!-- 菜单折叠按钮 -->
      <div class="header-trigger" @click="appStore.toggleSidebar">
        <el-icon>
          <Fold v-if="!appStore.sidebarCollapsed" />
          <Expand v-else />
        </el-icon>
      </div>
      
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" class="header-breadcrumb">
        <el-breadcrumb-item
          v-for="(item, index) in appStore.breadcrumbs"
          :key="index"
          :to="item.path"
        >
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <!-- 右侧区域 -->
    <div class="header-right">
      <!-- 全屏按钮 -->
      <div class="header-action" @click="toggleFullscreen">
        <el-tooltip content="全屏" placement="bottom">
          <el-icon>
            <FullScreen v-if="!isFullscreen" />
            <Aim v-else />
          </el-icon>
        </el-tooltip>
      </div>
      
      <!-- 主题切换 -->
      <div class="header-action" @click="appStore.toggleTheme">
        <el-tooltip :content="appStore.isDark ? '切换到亮色主题' : '切换到暗色主题'" placement="bottom">
          <el-icon>
            <Sunny v-if="appStore.isDark" />
            <Moon v-else />
          </el-icon>
        </el-tooltip>
      </div>
      
      <!-- 消息通知 -->
      <div class="header-action">
        <el-badge :value="notificationCount" :hidden="notificationCount === 0">
          <el-tooltip content="消息通知" placement="bottom">
            <el-icon>
              <Bell />
            </el-icon>
          </el-tooltip>
        </el-badge>
      </div>
      
      <!-- 用户信息 -->
      <el-dropdown class="header-user" @command="handleUserCommand">
        <div class="user-info">
          <el-avatar 
            :size="32" 
            :src="userStore.avatar" 
            :icon="UserFilled"
          />
          <span class="user-name">{{ userStore.nickname || userStore.username }}</span>
          <el-icon class="user-arrow">
            <ArrowDown />
          </el-icon>
        </div>
        
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              个人中心
            </el-dropdown-item>
            <el-dropdown-item command="settings">
              <el-icon><Setting /></el-icon>
              系统设置
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore, useUserStore } from '@/stores'
import { DomUtils } from '@/utils'
import { ElMessageBox } from 'element-plus'
import {
  Fold,
  Expand,
  FullScreen,
  Aim,
  Sunny,
  Moon,
  Bell,
  UserFilled,
  User,
  Setting,
  SwitchButton,
  ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

// 全屏状态
const isFullscreen = ref(false)

// 通知数量
const notificationCount = ref(0)

// 切换全屏
const toggleFullscreen = () => {
  if (isFullscreen.value) {
    DomUtils.exitFullscreen()
  } else {
    DomUtils.requestFullscreen()
  }
}

// 监听全屏状态变化
const handleFullscreenChange = () => {
  isFullscreen.value = DomUtils.isFullscreen()
}

// 处理用户下拉菜单命令
const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      // 打开设置对话框
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 处理退出登录
const handleLogout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    userStore.logout()
    router.push('/login')
  })
}

onMounted(() => {
  document.addEventListener('fullscreenchange', handleFullscreenChange)
  document.addEventListener('webkitfullscreenchange', handleFullscreenChange)
  document.addEventListener('mozfullscreenchange', handleFullscreenChange)
  document.addEventListener('MSFullscreenChange', handleFullscreenChange)
})

onUnmounted(() => {
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
  document.removeEventListener('webkitfullscreenchange', handleFullscreenChange)
  document.removeEventListener('mozfullscreenchange', handleFullscreenChange)
  document.removeEventListener('MSFullscreenChange', handleFullscreenChange)
})
</script>

<style lang="scss" scoped>
.header {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background-color: var(--bg-color);
  border-bottom: 1px solid var(--border-lighter);
}

.header-left {
  display: flex;
  align-items: center;
  flex: 1;
}

.header-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  cursor: pointer;
  border-radius: var(--border-radius-base);
  transition: background-color 0.3s ease;
  
  &:hover {
    background-color: var(--bg-color-overlay);
  }
  
  .el-icon {
    font-size: 18px;
    color: var(--text-primary);
  }
}

.header-breadcrumb {
  margin-left: 16px;
  
  :deep(.el-breadcrumb__item) {
    .el-breadcrumb__inner {
      color: var(--text-regular);
      font-weight: 400;
      
      &:hover {
        color: var(--primary-color);
      }
    }
    
    &:last-child {
      .el-breadcrumb__inner {
        color: var(--text-primary);
        font-weight: 500;
      }
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-action {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  cursor: pointer;
  border-radius: var(--border-radius-base);
  transition: background-color 0.3s ease;
  
  &:hover {
    background-color: var(--bg-color-overlay);
  }
  
  .el-icon {
    font-size: 18px;
    color: var(--text-primary);
  }
}

.header-user {
  cursor: pointer;
  
  .user-info {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    border-radius: var(--border-radius-base);
    transition: background-color 0.3s ease;
    
    &:hover {
      background-color: var(--bg-color-overlay);
    }
  }
  
  .user-name {
    margin: 0 8px;
    font-size: var(--font-size-base);
    color: var(--text-primary);
    white-space: nowrap;
    
    @media (max-width: 768px) {
      display: none;
    }
  }
  
  .user-arrow {
    font-size: 12px;
    color: var(--text-secondary);
    transition: transform 0.3s ease;
  }
}

:deep(.el-dropdown-menu) {
  .el-dropdown-menu__item {
    display: flex;
    align-items: center;
    
    .el-icon {
      margin-right: 8px;
      font-size: 16px;
    }
  }
}
</style>