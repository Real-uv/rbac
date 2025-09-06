<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <div 
      class="layout-sidebar"
      :class="{ 'is-collapsed': appStore.sidebarCollapsed }"
    >
      <Sidebar />
    </div>
    
    <!-- 主内容区域 -->
    <div class="layout-main">
      <!-- 顶部导航栏 -->
      <div class="layout-header">
        <Header />
      </div>
      
      <!-- 标签页导航 -->
      <div class="layout-tabs" v-if="appStore.tabs.length > 0">
        <TabsNav />
      </div>
      
      <!-- 页面内容 -->
      <div class="layout-content">
        <router-view v-slot="{ Component, route }">
          <transition name="fade" mode="out-in">
            <keep-alive :include="keepAliveComponents">
              <component :is="Component" :key="route.path" />
            </keep-alive>
          </transition>
        </router-view>
      </div>
      
      <!-- 底部 -->
      <div class="layout-footer">
        <Footer />
      </div>
    </div>
    
    <!-- 移动端遮罩 -->
    <div 
      v-if="appStore.device === 'mobile' && !appStore.sidebarCollapsed"
      class="layout-mask"
      @click="appStore.toggleSidebar"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAppStore } from '@/stores'
import Sidebar from './components/Sidebar.vue'
import Header from './components/Header.vue'
import TabsNav from './components/TabsNav.vue'
import Footer from './components/Footer.vue'

const appStore = useAppStore()

// 需要缓存的组件
const keepAliveComponents = computed(() => {
  return appStore.tabs
    .filter(tab => tab.name)
    .map(tab => tab.name)
})
</script>

<style lang="scss" scoped>
.layout-container {
  display: flex;
  height: 100vh;
  background-color: var(--bg-color-page);
}

.layout-sidebar {
  width: var(--sidebar-width);
  background-color: var(--bg-color);
  box-shadow: var(--box-shadow-base);
  transition: width 0.3s ease;
  z-index: 1001;
  
  &.is-collapsed {
    width: var(--sidebar-collapsed-width);
  }
  
  @media (max-width: 768px) {
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    z-index: 1002;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
    
    &:not(.is-collapsed) {
      transform: translateX(0);
    }
  }
}

.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  
  @media (max-width: 768px) {
    width: 100%;
  }
}

.layout-header {
  height: var(--header-height);
  background-color: var(--bg-color);
  box-shadow: var(--box-shadow-base);
  z-index: 1000;
}

.layout-tabs {
  height: 40px;
  background-color: var(--bg-color);
  border-bottom: 1px solid var(--border-lighter);
}

.layout-content {
  flex: 1;
  overflow: auto;
  background-color: var(--bg-color-page);
}

.layout-footer {
  height: var(--footer-height);
  background-color: var(--bg-color);
  border-top: 1px solid var(--border-lighter);
}

.layout-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.3);
  z-index: 1001;
}

// 页面切换动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>