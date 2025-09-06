<template>
  <div v-if="!item.hidden">
    <!-- 有子菜单的情况 -->
    <el-sub-menu
        v-if="hasChildren"
        :index="item.path"
    >
      <template #title>
        <el-icon v-if="item.icon">
          <component :is="item.icon" />
        </el-icon>
        <span>{{ item.title }}</span>
      </template>

      <SidebarItem
          v-for="child in item.children"
          :key="child.id"
          :item="child"
          :base-path="resolvePath(child.path)"
      />
    </el-sub-menu>

    <!-- 没有子菜单的情况 -->
    <el-menu-item
        v-else
        :index="resolvePath(item.path)"
    >
      <el-icon v-if="item.icon">
        <component :is="item.icon" />
      </el-icon>
      <template #title>
        <span>{{ item.title }}</span>
      </template>
    </el-menu-item>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Menu } from '@/types'

// 递归组件需要显式注册自身
import SidebarItem from './SidebarItem.vue'

interface Props {
  item: Menu
  basePath?: string
}

const props = withDefaults(defineProps<Props>(), {
  basePath: ''
})

// 是否有子菜单
const hasChildren = computed(() => {
  return props.item.children && props.item.children.length > 0
})

// 解析路径
const resolvePath = (routePath: string) => {
  if (routePath.startsWith('/')) {
    return routePath
  }

  if (props.basePath) {
    return `${props.basePath}/${routePath}`.replace(/\/+/g, '/')
  }

  return routePath
}
</script>
