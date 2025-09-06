<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAppStore } from '@/stores'

const appStore = useAppStore()

onMounted(() => {
  // 初始化应用设置
  appStore.initApp()
})
</script>

<style lang="scss">
#app {
  height: 100vh;
  overflow: hidden;
}
</style>
