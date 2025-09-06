<template>
  <div class="error-page">
    <div class="error-container">
      <div class="error-content">
        <!-- 404图标 -->
        <div class="error-icon">
          <div class="number">4</div>
          <div class="ghost">
            <div class="face">
              <div class="eye"></div>
              <div class="eye"></div>
              <div class="mouth"></div>
            </div>
          </div>
          <div class="number">4</div>
        </div>
        
        <!-- 错误信息 -->
        <div class="error-info">
          <h1 class="error-title">页面不存在</h1>
          <p class="error-description">
            抱歉，您访问的页面不存在或已被删除
          </p>
          <p class="error-suggestion">
            请检查URL是否正确，或者返回首页重新开始
          </p>
        </div>
        
        <!-- 操作按钮 -->
        <div class="error-actions">
          <el-button type="primary" size="large" @click="goHome">
            <el-icon><House /></el-icon>
            返回首页
          </el-button>
          <el-button size="large" @click="goBack">
            <el-icon><Back /></el-icon>
            返回上页
          </el-button>
        </div>
      </div>
      
      <!-- 装饰元素 -->
      <div class="decoration">
        <div class="cloud cloud-1"></div>
        <div class="cloud cloud-2"></div>
        <div class="cloud cloud-3"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { House, Back } from '@element-plus/icons-vue'

const router = useRouter()

// 返回首页
const goHome = () => {
  router.push('/')
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}
</script>

<style lang="scss" scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.error-container {
  text-align: center;
  position: relative;
  z-index: 2;
}

.error-content {
  background: white;
  border-radius: 20px;
  padding: 60px 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  max-width: 600px;
  
  @media (max-width: 768px) {
    padding: 40px 30px;
    margin: 0 20px;
  }
}

.error-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
  
  .number {
    font-size: 120px;
    font-weight: 700;
    color: var(--primary-color);
    line-height: 1;
    
    @media (max-width: 768px) {
      font-size: 80px;
    }
  }
  
  .ghost {
    width: 100px;
    height: 120px;
    background: var(--primary-color);
    border-radius: 50px 50px 0 0;
    position: relative;
    margin: 0 20px;
    animation: float 3s ease-in-out infinite;
    
    @media (max-width: 768px) {
      width: 60px;
      height: 80px;
      margin: 0 10px;
    }
    
    &::after {
      content: '';
      position: absolute;
      bottom: -10px;
      left: 0;
      width: 100%;
      height: 20px;
      background: var(--primary-color);
      clip-path: polygon(0 0, 20% 100%, 40% 0, 60% 100%, 80% 0, 100% 100%, 100% 0);
      
      @media (max-width: 768px) {
        height: 15px;
        bottom: -7px;
      }
    }
    
    .face {
      position: absolute;
      top: 30px;
      left: 50%;
      transform: translateX(-50%);
      
      @media (max-width: 768px) {
        top: 20px;
      }
    }
    
    .eye {
      width: 12px;
      height: 12px;
      background: white;
      border-radius: 50%;
      display: inline-block;
      margin: 0 8px;
      animation: blink 4s infinite;
      
      @media (max-width: 768px) {
        width: 8px;
        height: 8px;
        margin: 0 5px;
      }
    }
    
    .mouth {
      width: 20px;
      height: 10px;
      background: white;
      border-radius: 0 0 20px 20px;
      margin: 10px auto 0;
      
      @media (max-width: 768px) {
        width: 15px;
        height: 8px;
        margin-top: 8px;
      }
    }
  }
}

.error-info {
  margin-bottom: 40px;
  
  .error-title {
    font-size: 32px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 16px 0;
    
    @media (max-width: 768px) {
      font-size: 24px;
    }
  }
  
  .error-description {
    font-size: 16px;
    color: var(--text-regular);
    margin: 0 0 12px 0;
    line-height: 1.6;
  }
  
  .error-suggestion {
    font-size: 14px;
    color: var(--text-secondary);
    margin: 0;
    line-height: 1.6;
  }
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  
  @media (max-width: 768px) {
    flex-direction: column;
    align-items: center;
    
    .el-button {
      width: 200px;
    }
  }
}

.decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  
  .cloud {
    position: absolute;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50px;
    
    &::before,
    &::after {
      content: '';
      position: absolute;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 50px;
    }
  }
  
  .cloud-1 {
    width: 100px;
    height: 40px;
    top: 20%;
    left: 10%;
    animation: drift 20s infinite linear;
    
    &::before {
      width: 50px;
      height: 50px;
      top: -25px;
      left: 10px;
    }
    
    &::after {
      width: 60px;
      height: 20px;
      top: -10px;
      right: 10px;
    }
  }
  
  .cloud-2 {
    width: 80px;
    height: 30px;
    top: 60%;
    right: 15%;
    animation: drift 25s infinite linear reverse;
    
    &::before {
      width: 40px;
      height: 40px;
      top: -20px;
      left: 15px;
    }
    
    &::after {
      width: 50px;
      height: 15px;
      top: -8px;
      right: 8px;
    }
  }
  
  .cloud-3 {
    width: 120px;
    height: 50px;
    top: 80%;
    left: 70%;
    animation: drift 30s infinite linear;
    
    &::before {
      width: 60px;
      height: 60px;
      top: -30px;
      left: 20px;
    }
    
    &::after {
      width: 70px;
      height: 25px;
      top: -12px;
      right: 15px;
    }
  }
}

// 动画
@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

@keyframes blink {
  0%, 90%, 100% {
    transform: scaleY(1);
  }
  95% {
    transform: scaleY(0.1);
  }
}

@keyframes drift {
  0% {
    transform: translateX(-100px);
  }
  100% {
    transform: translateX(calc(100vw + 100px));
  }
}

// 页面进入动画
.error-content {
  animation: slideUp 0.8s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>