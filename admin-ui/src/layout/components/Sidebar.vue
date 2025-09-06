<template>
  <div class="sidebar">
    <!-- Logo区域 -->
    <div class="sidebar-logo" @click="$router.push('/')">
      <img 
        v-if="!appStore.sidebarCollapsed" 
        src=""
        alt="Logo" 
        class="logo-image"
      >
      <img 
        v-else 
        src=""
        alt="Logo" 
        class="logo-mini"
      >
      <span v-if="!appStore.sidebarCollapsed" class="logo-title">
        {{ $env.VITE_APP_LOGO_TITLE }}
      </span>
    </div>
    
    <!-- 菜单区域 -->
    <div class="sidebar-menu">
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        :unique-opened="true"
        router
        background-color="transparent"
        text-color="var(--text-primary)"
        active-text-color="var(--primary-color)"
      >
        <SidebarItem
          v-for="route in permissionStore.menuTree"
          :key="route.id"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore, usePermissionStore } from '@/stores'
import SidebarItem from './SidebarItem.vue'

const route = useRoute()
const appStore = useAppStore()
const permissionStore = usePermissionStore()

// 当前激活的菜单
const activeMenu = computed(() => {
  const { path } = route
  return path
})
</script>

<style lang="scss" scoped>
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-color);
}

.sidebar-logo {
  height: var(--header-height);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  border-bottom: 1px solid var(--border-lighter);
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background-color: var(--bg-color-overlay);
  }
  
  .logo-image,
  .logo-mini {
    height: 32px;
    width: auto;
  }
  
  .logo-title {
    margin-left: 12px;
    font-size: var(--font-size-large);
    font-weight: 600;
    color: var(--primary-color);
    white-space: nowrap;
  }
}

.sidebar-menu {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  
  :deep(.el-menu) {
    border-right: none;
    
    .el-menu-item,
    .el-sub-menu__title {
      height: 48px;
      line-height: 48px;
      padding: 0 16px !important;
      
      .el-icon {
        margin-right: 8px;
        font-size: 16px;
      }
      
      &:hover {
        background-color: var(--bg-color-overlay) !important;
        color: var(--primary-color) !important;
      }
    }
    
    .el-menu-item.is-active {
      background-color: var(--primary-color) !important;
      color: #fff !important;
      position: relative;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 3px;
        background-color: #fff;
      }
    }
    
    .el-sub-menu {
      .el-sub-menu__title {
        &:hover {
          background-color: var(--bg-color-overlay) !important;
          color: var(--primary-color) !important;
        }
      }
      
      .el-menu {
        background-color: var(--bg-color-page);
        
        .el-menu-item {
          padding-left: 48px !important;
          
          &:hover {
            background-color: var(--bg-color-overlay) !important;
          }
          
          &.is-active {
            background-color: var(--primary-light) !important;
            color: #fff !important;
          }
        }
      }
    }
  }
}

// 折叠状态下的样式调整
:deep(.el-menu--collapse) {
  .el-menu-item,
  .el-sub-menu__title {
    padding: 0 16px !important;
    text-align: center;
    
    .el-icon {
      margin-right: 0;
    }
    
    span {
      display: none;
    }
  }
  
  .el-sub-menu {
    .el-sub-menu__title {
      .el-sub-menu__icon-arrow {
        display: none;
      }
    }
  }
}
</style>