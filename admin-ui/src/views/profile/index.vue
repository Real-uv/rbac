<template>
  <div class="profile-page">
    <div class="profile-container">
      <!-- 用户信息卡片 -->
      <el-card class="profile-card" shadow="hover">
        <div class="profile-header">
          <div class="avatar-section">
            <el-avatar :size="100" :src="userInfo.avatar" :icon="UserFilled" />
            <el-button type="primary" size="small" class="upload-btn" @click="handleAvatarUpload">
              <el-icon><Camera /></el-icon>
              更换头像
            </el-button>
          </div>
          <div class="user-details">
            <h2 class="user-name">{{ userInfo.nickname || userInfo.username }}</h2>
            <p class="user-title">{{ userInfo.roles?.[0]?.name || '普通用户' }}</p>
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-value">{{ userInfo.loginCount || 0 }}</span>
                <span class="stat-label">登录次数</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ formatDate(userInfo.lastLoginTime) }}</span>
                <span class="stat-label">最后登录</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ formatDate(userInfo.createdAt) }}</span>
                <span class="stat-label">注册时间</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 功能区域 -->
      <div class="profile-content">
        <el-row :gutter="20">
          <!-- 基本信息 -->
          <el-col :xs="24" :lg="16">
            <el-card class="info-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <span>基本信息</span>
                  <el-button type="primary" size="small" @click="editMode = !editMode">
                    <el-icon><Edit /></el-icon>
                    {{ editMode ? '取消编辑' : '编辑信息' }}
                  </el-button>
                </div>
              </template>

              <el-form
                ref="formRef"
                :model="formData"
                :rules="formRules"
                label-width="100px"
                :disabled="!editMode"
              >
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="用户名" prop="username">
                      <el-input v-model="formData.username" disabled />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="昵称" prop="nickname">
                      <el-input v-model="formData.nickname" placeholder="请输入昵称" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="邮箱" prop="email">
                      <el-input v-model="formData.email" placeholder="请输入邮箱" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="手机号" prop="phone">
                      <el-input v-model="formData.phone" placeholder="请输入手机号" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="性别" prop="gender">
                      <el-select v-model="formData.gender" placeholder="请选择性别">
                        <el-option label="男" value="1" />
                        <el-option label="女" value="2" />
                        <el-option label="保密" value="0" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="生日" prop="birthday">
                      <el-date-picker
                        v-model="formData.birthday"
                        type="date"
                        placeholder="请选择生日"
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item label="个人简介" prop="bio">
                  <el-input
                    v-model="formData.bio"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入个人简介"
                  />
                </el-form-item>

                <el-form-item v-if="editMode">
                  <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
                    保存修改
                  </el-button>
                  <el-button @click="resetForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-col>

          <!-- 侧边栏 -->
          <el-col :xs="24" :lg="8">
            <!-- 安全设置 -->
            <el-card class="security-card" shadow="hover">
              <template #header>
                <span>安全设置</span>
              </template>
              <div class="security-list">
                <div class="security-item" @click="handleChangePassword">
                  <div class="security-icon">
                    <el-icon><Lock /></el-icon>
                  </div>
                  <div class="security-content">
                    <div class="security-title">修改密码</div>
                    <div class="security-desc">定期更换密码保护账户安全</div>
                  </div>
                  <el-icon class="security-arrow"><ArrowRight /></el-icon>
                </div>
                
                <div class="security-item" @click="handleBindPhone">
                  <div class="security-icon">
                    <el-icon><Phone /></el-icon>
                  </div>
                  <div class="security-content">
                    <div class="security-title">手机绑定</div>
                    <div class="security-desc">
                      {{ userInfo.phone ? '已绑定' : '未绑定' }}
                    </div>
                  </div>
                  <el-icon class="security-arrow"><ArrowRight /></el-icon>
                </div>
                
                <div class="security-item" @click="handleBindEmail">
                  <div class="security-icon">
                    <el-icon><Message /></el-icon>
                  </div>
                  <div class="security-content">
                    <div class="security-title">邮箱绑定</div>
                    <div class="security-desc">
                      {{ userInfo.email ? '已绑定' : '未绑定' }}
                    </div>
                  </div>
                  <el-icon class="security-arrow"><ArrowRight /></el-icon>
                </div>
              </div>
            </el-card>

            <!-- 我的角色 -->
            <el-card class="roles-card" shadow="hover">
              <template #header>
                <span>我的角色</span>
              </template>
              <div class="roles-list">
                <el-tag
                  v-for="role in userInfo.roles"
                  :key="role.id"
                  type="primary"
                  size="large"
                  class="role-tag"
                >
                  {{ role.name }}
                </el-tag>
                <div v-if="!userInfo.roles?.length" class="no-roles">
                  暂无角色
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px">
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请确认新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="passwordLoading" @click="handlePasswordSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 头像上传 -->
    <input
      ref="avatarInputRef"
      type="file"
      accept="image/*"
      style="display: none"
      @change="handleAvatarChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores'
import { userApi } from '@/api'
import { DateUtils, ValidateUtils } from '@/utils'
import {
  UserFilled,
  Camera,
  Edit,
  Lock,
  Phone,
  Message,
  ArrowRight
} from '@element-plus/icons-vue'

const userStore = useUserStore()

// 表单引用
const formRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()
const avatarInputRef = ref<HTMLInputElement>()

// 状态
const editMode = ref(false)
const submitLoading = ref(false)
const passwordDialogVisible = ref(false)
const passwordLoading = ref(false)

// 用户信息
const userInfo = computed(() => userStore.userInfo || {})

// 表单数据
const formData = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  gender: '0',
  birthday: '',
  bio: ''
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const formRules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { validator: (_, value, callback) => {
      if (value && !ValidateUtils.isEmail(value)) {
        callback(new Error('请输入正确的邮箱格式'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  phone: [
    { validator: (_, value, callback) => {
      if (value && !ValidateUtils.isPhone(value)) {
        callback(new Error('请输入正确的手机号格式'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ]
}

// 密码验证规则
const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (_, value, callback) => {
      if (value !== passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ]
}

// 格式化日期
const formatDate = (date: string | Date) => {
  if (!date) return '未知'
  return DateUtils.format(date, 'YYYY-MM-DD')
}

// 初始化表单数据
const initFormData = () => {
  Object.assign(formData, {
    username: userInfo.value.username || '',
    nickname: userInfo.value.nickname || '',
    email: userInfo.value.email || '',
    phone: userInfo.value.phone || '',
    gender: userInfo.value.gender?.toString() || '0',
    birthday: userInfo.value.birthday || '',
    bio: userInfo.value.bio || ''
  })
}

// 重置表单
const resetForm = () => {
  initFormData()
  formRef.value?.clearValidate()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    submitLoading.value = true
    
    await userApi.updateProfile({
      ...formData,
      gender: parseInt(formData.gender)
    })
    
    // 更新用户信息
    await userStore.getUserInfo()
    
    ElMessage.success('信息更新成功')
    editMode.value = false
  } catch (error: any) {
    ElMessage.error(error.message || '更新失败')
  } finally {
    submitLoading.value = false
  }
}

// 修改密码
const handleChangePassword = () => {
  passwordDialogVisible.value = true
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
}

// 提交密码修改
const handlePasswordSubmit = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    
    passwordLoading.value = true
    
    await userApi.changePassword(passwordForm)
    
    ElMessage.success('密码修改成功')
    passwordDialogVisible.value = false
  } catch (error: any) {
    ElMessage.error(error.message || '密码修改失败')
  } finally {
    passwordLoading.value = false
  }
}

// 绑定手机
const handleBindPhone = () => {
  ElMessage.info('手机绑定功能开发中...')
}

// 绑定邮箱
const handleBindEmail = () => {
  ElMessage.info('邮箱绑定功能开发中...')
}

// 上传头像
const handleAvatarUpload = () => {
  avatarInputRef.value?.click()
}

// 处理头像文件选择
const handleAvatarChange = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (!file) return
  
  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }
  
  // 检查文件大小（2MB）
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }
  
  try {
    const formData = new FormData()
    formData.append('avatar', file)
    
    await userApi.uploadAvatar(formData)
    
    // 更新用户信息
    await userStore.getUserInfo()
    
    ElMessage.success('头像上传成功')
  } catch (error: any) {
    ElMessage.error(error.message || '头像上传失败')
  }
  
  // 清空文件输入
  target.value = ''
}

// 初始化
onMounted(() => {
  initFormData()
})
</script>

<style lang="scss" scoped>
.profile-page {
  padding: 20px;
  background-color: var(--bg-color-page);
  min-height: calc(100vh - 60px);
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-card {
  margin-bottom: 20px;
  
  .profile-header {
    display: flex;
    align-items: center;
    gap: 30px;
    
    @media (max-width: 768px) {
      flex-direction: column;
      text-align: center;
      gap: 20px;
    }
  }
  
  .avatar-section {
    position: relative;
    
    .upload-btn {
      position: absolute;
      bottom: -10px;
      left: 50%;
      transform: translateX(-50%);
    }
  }
  
  .user-details {
    flex: 1;
    
    .user-name {
      font-size: 28px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0 0 8px 0;
    }
    
    .user-title {
      font-size: 16px;
      color: var(--text-secondary);
      margin: 0 0 20px 0;
    }
    
    .user-stats {
      display: flex;
      gap: 40px;
      
      @media (max-width: 768px) {
        justify-content: center;
        gap: 20px;
      }
      
      .stat-item {
        text-align: center;
        
        .stat-value {
          display: block;
          font-size: 20px;
          font-weight: 600;
          color: var(--primary-color);
          margin-bottom: 4px;
        }
        
        .stat-label {
          font-size: 12px;
          color: var(--text-secondary);
        }
      }
    }
  }
}

.profile-content {
  .info-card {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
  
  .security-card,
  .roles-card {
    margin-bottom: 20px;
  }
  
  .security-list {
    .security-item {
      display: flex;
      align-items: center;
      padding: 16px 0;
      cursor: pointer;
      border-bottom: 1px solid var(--border-lighter);
      transition: background-color 0.2s ease;
      
      &:last-child {
        border-bottom: none;
      }
      
      &:hover {
        background-color: var(--bg-color-overlay);
        margin: 0 -16px;
        padding-left: 16px;
        padding-right: 16px;
        border-radius: var(--border-radius-small);
      }
      
      .security-icon {
        width: 40px;
        height: 40px;
        border-radius: 8px;
        background-color: var(--bg-color-overlay);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        color: var(--primary-color);
      }
      
      .security-content {
        flex: 1;
        
        .security-title {
          font-size: 14px;
          font-weight: 500;
          color: var(--text-primary);
          margin-bottom: 2px;
        }
        
        .security-desc {
          font-size: 12px;
          color: var(--text-secondary);
        }
      }
      
      .security-arrow {
        color: var(--text-placeholder);
        font-size: 14px;
      }
    }
  }
  
  .roles-list {
    .role-tag {
      margin-right: 8px;
      margin-bottom: 8px;
    }
    
    .no-roles {
      text-align: center;
      color: var(--text-secondary);
      font-size: 14px;
      padding: 20px 0;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .profile-page {
    padding: 16px;
  }
  
  .profile-content {
    :deep(.el-col) {
      margin-bottom: 20px;
    }
  }
}
</style>