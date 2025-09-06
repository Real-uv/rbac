<template>
  <div class="login-container">
    <div class="login-box">
      <!-- 登录表单 -->
      <div class="login-form">
        <div class="login-header">
          <h1 class="login-title">RBAC 管理系统</h1>
          <p class="login-subtitle">基于角色的访问控制系统</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form-content"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>

          <el-form-item prop="captcha" v-if="showCaptcha">
            <div class="captcha-container">
              <el-input
                v-model="loginForm.captcha"
                placeholder="请输入验证码"
                size="large"
                :prefix-icon="Key"
                clearable
                class="captcha-input"
              />
              <div class="captcha-image" @click="refreshCaptcha">
                <img :src="captchaUrl" alt="验证码" />
              </div>
            </div>
          </el-form-item>

          <el-form-item>
            <div class="login-options">
              <el-checkbox v-model="loginForm.rememberMe">
                记住我
              </el-checkbox>
              <el-link type="primary" :underline="false">
                忘记密码？
              </el-link>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="login-button"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 其他登录方式 -->
        <div class="login-other">
          <el-divider>其他登录方式</el-divider>
          <div class="social-login">
            <el-button circle :icon="Platform" />
            <el-button circle :icon="ChatDotRound" />
            <el-button circle :icon="Message" />
          </div>
        </div>
      </div>

      <!-- 登录背景 -->
      <div class="login-bg">
        <div class="bg-content">
          <h2>欢迎使用</h2>
          <p>现代化的权限管理系统</p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon><Setting /></el-icon>
              <span>灵活配置</span>
            </div>
            <div class="feature-item">
              <el-icon><DataAnalysis /></el-icon>
              <span>数据分析</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 版权信息 -->
    <div class="login-footer">
      <p>&copy; 2024 RBAC Admin System. All rights reserved.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElNotification, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores'
import { ValidateUtils } from '@/utils'
import {
  User,
  Lock,
  Key,
  Platform,
  ChatDotRound,
  Message,
  Setting,
  DataAnalysis
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 表单引用
const loginFormRef = ref<FormInstance>()

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  captcha: '',
  captchaKey: '',
  rememberMe: false
})

// 表单验证规则
const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码长度为 4 位', trigger: 'blur' }
  ]
}

// 状态
const loading = ref(false)
const showCaptcha = ref(false)
const captchaUrl = ref('')

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()

    loading.value = true

    await userStore.login({
      username: loginForm.username,
      password: loginForm.password,
      captcha: loginForm.captcha,
      captchaKey: loginForm.captchaKey,
      rememberMe: loginForm.rememberMe
    })

    ElNotification({
      title: '登录成功',
      message: `欢迎回来，${userStore.nickname || userStore.username}！`,
      type: 'success',
      duration: 3000
    })

    // 跳转到首页或之前访问的页面 - 添加延迟确保路由守卫正确处理
    setTimeout(() => {
      const redirect = router.currentRoute.value.query.redirect as string
      router.push(redirect || '/')
    }, 100)

  } catch (error: any) {
    ElMessage.error(error.message || '登录失败')

    // 登录失败后显示验证码
    if (!showCaptcha.value) {
      showCaptcha.value = true
      refreshCaptcha()
    } else {
      refreshCaptcha()
    }
  } finally {
    loading.value = false
  }
}

// 刷新验证码
const refreshCaptcha = async () => {
  try {
    const response = await userStore.getCaptcha()
    captchaUrl.value = response.captchaImage
    loginForm.captchaKey = response.captchaKey
  } catch (error: any) {
    ElMessage.error(error.message || '获取验证码失败')
  }
}

// 初始化
onMounted(() => {
  // 如果已经登录，直接跳转
  if (userStore.isLoggedIn) {
    router.push('/')
    return
  }

  // 开发环境下预填充表单
  if (import.meta.env.DEV) {
    loginForm.username = 'admin'
    loginForm.password = 'admin123'
  }
})
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;

  @media (max-width: 768px) {
    flex-direction: column;
    max-width: 400px;
    min-height: auto;
  }
}

.login-form {
  flex: 1;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;

  @media (max-width: 768px) {
    padding: 40px 30px;
  }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-title {
  font-size: 32px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.login-subtitle {
  font-size: 16px;
  color: var(--text-secondary);
  margin: 0;
}

.login-form-content {
  .el-form-item {
    margin-bottom: 24px;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.captcha-container {
  display: flex;
  gap: 12px;

  .captcha-input {
    flex: 1;
  }

  .captcha-image {
    width: 120px;
    height: 40px;
    cursor: pointer;
    border: 1px solid var(--border-color);
    border-radius: var(--border-radius-base);
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
}

.login-other {
  margin-top: 32px;

  .el-divider {
    margin: 24px 0;

    :deep(.el-divider__text) {
      color: var(--text-secondary);
      font-size: 14px;
    }
  }
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 16px;

  .el-button {
    width: 40px;
    height: 40px;

    &:hover {
      transform: translateY(-2px);
    }
  }
}

.login-bg {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.1"/><circle cx="10" cy="60" r="0.5" fill="white" opacity="0.1"/><circle cx="90" cy="40" r="0.5" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  }

  @media (max-width: 768px) {
    display: none;
  }
}

.bg-content {
  text-align: center;
  z-index: 1;
  position: relative;

  h2 {
    font-size: 36px;
    font-weight: 600;
    margin: 0 0 16px 0;
  }

  p {
    font-size: 18px;
    opacity: 0.9;
    margin: 0 0 40px 0;
  }
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;

  .el-icon {
    font-size: 20px;
  }
}

.login-footer {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  text-align: center;

  p {
    margin: 0;
  }
}

// 动画效果
.login-box {
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.feature-item {
  animation: fadeInLeft 0.8s ease-out;

  &:nth-child(2) {
    animation-delay: 0.2s;
  }

  &:nth-child(3) {
    animation-delay: 0.4s;
  }
}

@keyframes fadeInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>
