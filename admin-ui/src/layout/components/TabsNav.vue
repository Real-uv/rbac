<template>
  <div class="tabs-nav" v-if="appStore.tabs.length > 0">
    <el-tabs
      v-model="appStore.activeTab"
      type="card"
      closable
      @tab-remove="removeTab"
      @tab-click="handleTabClick"
    >
      <el-tab-pane
        v-for="tab in appStore.tabs"
        :key="tab.name"
        :label="tab.title"
        :name="tab.name"
        :closable="tab.closable"
      />
    </el-tabs>
    
    <div class="tabs-actions">
      <el-dropdown @command="handleCommand">
        <el-button size="small" type="text">
          <el-icon><ArrowDown /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="closeOthers">关闭其他</el-dropdown-item>
            <el-dropdown-item command="closeAll">关闭所有</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'

const appStore = useAppStore()
const router = useRouter()

// 移除标签页
const removeTab = (tabName: string) => {
  const removedTab = appStore.tabs.find(tab => tab.name === tabName)
  appStore.removeTab(tabName)
  
  // 如果移除的是当前激活的标签页，需要跳转到新的激活标签页
  if (removedTab && appStore.activeTab === tabName && appStore.tabs.length > 0) {
    const activeTab = appStore.tabs.find(tab => tab.name === appStore.activeTab)
    if (activeTab) {
      router.push(activeTab.path)
    }
  }
}

// 点击标签页
const handleTabClick = (tab: any) => {
  const targetTab = appStore.tabs.find(t => t.name === tab.paneName)
  if (targetTab) {
    router.push(targetTab.path)
  }
}

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'closeOthers':
      appStore.closeOtherTabs(appStore.activeTab)
      break
    case 'closeAll':
      appStore.closeAllTabs()
      if (appStore.tabs.length === 0) {
        router.push('/')
      }
      break
  }
}
</script>

<style lang="scss" scoped>
.tabs-nav {
  display: flex;
  align-items: center;
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-light);
  padding: 0 16px;
  
  :deep(.el-tabs) {
    flex: 1;
    
    .el-tabs__header {
      margin: 0;
      border-bottom: none;
    }
    
    .el-tabs__nav-wrap {
      &::after {
        display: none;
      }
    }
    
    .el-tabs__item {
      border: 1px solid var(--el-border-color-light);
      border-radius: 4px 4px 0 0;
      margin-right: 4px;
      
      &.is-active {
        background: var(--el-color-primary);
        color: white;
        border-color: var(--el-color-primary);
      }
    }
  }
  
  .tabs-actions {
    margin-left: 8px;
  }
}
</style>