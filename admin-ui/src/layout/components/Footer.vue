<template>
  <div class="layout-footer">
    <div class="footer-content">
      <span>© 2024 RBAC Admin System. All rights reserved.</span>
      <span class="footer-links">
        <el-link type="primary" :underline="false">帮助文档</el-link>
        <el-divider direction="vertical" />
        <el-link type="primary" :underline="false">意见反馈</el-link>
        <el-divider direction="vertical" />
        <el-link type="primary" :underline="false">关于我们</el-link>
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
// Footer 组件不需要额外的逻辑
</script>

<style lang="scss" scoped>
.layout-footer {
  background: var(--el-bg-color);
  border-top: 1px solid var(--el-border-color-light);
  padding: 16px 24px;
  
  .footer-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: var(--el-text-color-regular);
    font-size: 14px;
    
    .footer-links {
      display: flex;
      align-items: center;
      
      .el-link {
        font-size: 14px;
      }
    }
  }
}

@media (max-width: 768px) {
  .layout-footer {
    .footer-content {
      flex-direction: column;
      gap: 8px;
      text-align: center;
    }
  }
}
</style>