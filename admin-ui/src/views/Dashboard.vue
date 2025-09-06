<template>
  <div class="dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h1 class="welcome-title">
          {{ getGreeting() }}，{{ userStore.nickname || userStore.username }}！
        </h1>
        <p class="welcome-subtitle">
          今天是 {{ DateUtils.format(new Date(), 'YYYY年MM月DD日 dddd') }}，祝您工作愉快！
        </p>
      </div>
      <div class="welcome-actions">
        <el-button type="primary" :icon="Plus" @click="handleQuickAction('user')">
          新增用户
        </el-button>
        <el-button type="success" :icon="Setting" @click="handleQuickAction('role')">
          角色管理
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card" v-for="stat in stats" :key="stat.key">
        <div class="stat-icon" :style="{ backgroundColor: stat.color }">
          <el-icon :size="24">
            <component :is="stat.icon" />
          </el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-trend" :class="stat.trend > 0 ? 'positive' : 'negative'">
            <el-icon>
              <ArrowUp v-if="stat.trend > 0" />
              <ArrowDown v-else />
            </el-icon>
            {{ Math.abs(stat.trend) }}%
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>用户增长趋势</span>
                <el-button-group size="small">
                  <el-button 
                    v-for="period in ['7天', '30天', '90天']" 
                    :key="period"
                    :type="selectedPeriod === period ? 'primary' : 'default'"
                    @click="selectedPeriod = period"
                  >
                    {{ period }}
                  </el-button>
                </el-button-group>
              </div>
            </template>
            <div ref="userChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <span>权限分布</span>
            </template>
            <div ref="permissionChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 活动日志 -->
    <div class="activity-section">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="16">
          <el-card class="activity-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>最近活动</span>
                <el-link type="primary" :underline="false" @click="$router.push('/logs')">
                  查看全部
                </el-link>
              </div>
            </template>
            <div class="activity-list">
              <div 
                v-for="activity in activities" 
                :key="activity.id"
                class="activity-item"
              >
                <div class="activity-avatar">
                  <el-avatar :size="32" :src="activity.avatar" :icon="UserFilled" />
                </div>
                <div class="activity-content">
                  <div class="activity-text">
                    <span class="activity-user">{{ activity.username }}</span>
                    <span class="activity-action">{{ activity.action }}</span>
                    <span class="activity-target">{{ activity.target }}</span>
                  </div>
                  <div class="activity-time">{{ DateUtils.fromNow(activity.createdAt) }}</div>
                </div>
                <div class="activity-status">
                  <el-tag 
                    :type="activity.status === 'success' ? 'success' : 'danger'"
                    size="small"
                  >
                    {{ activity.status === 'success' ? '成功' : '失败' }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :xs="24" :lg="8">
          <el-card class="quick-actions-card" shadow="hover">
            <template #header>
              <span>快捷操作</span>
            </template>
            <div class="quick-actions">
              <div 
                v-for="action in quickActions" 
                :key="action.key"
                class="quick-action-item"
                @click="handleQuickAction(action.key)"
              >
                <div class="action-icon">
                  <el-icon :size="20">
                    <component :is="action.icon" />
                  </el-icon>
                </div>
                <div class="action-content">
                  <div class="action-title">{{ action.title }}</div>
                  <div class="action-desc">{{ action.description }}</div>
                </div>
                <el-icon class="action-arrow">
                  <ArrowRight />
                </el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores'
import { DateUtils } from '@/utils'
import * as echarts from 'echarts'
import {
  Plus,
  Setting,
  User,
  UserFilled,
  Lock,
  Key,
  ArrowUp,
  ArrowDown,
  ArrowRight,
  DataAnalysis,
  Document,
  Bell
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 图表引用
const userChartRef = ref<HTMLElement>()
const permissionChartRef = ref<HTMLElement>()

// 选中的时间周期
const selectedPeriod = ref('7天')

// 统计数据
const stats = reactive([
  {
    key: 'users',
    label: '总用户数',
    value: '1,234',
    trend: 12.5,
    color: '#409EFF',
    icon: User
  },
  {
    key: 'roles',
    label: '角色数量',
    value: '56',
    trend: 8.2,
    color: '#67C23A',
    icon: Lock
  },
  {
    key: 'permissions',
    label: '权限数量',
    value: '189',
    trend: -2.1,
    color: '#E6A23C',
    icon: Key
  },
  {
    key: 'online',
    label: '在线用户',
    value: '89',
    trend: 15.3,
    color: '#F56C6C',
    icon: DataAnalysis
  }
])

// 活动日志
const activities = reactive([
  {
    id: 1,
    username: '张三',
    action: '创建了用户',
    target: '李四',
    status: 'success',
    avatar: '',
    createdAt: new Date(Date.now() - 5 * 60 * 1000)
  },
  {
    id: 2,
    username: '王五',
    action: '修改了角色',
    target: '管理员',
    status: 'success',
    avatar: '',
    createdAt: new Date(Date.now() - 15 * 60 * 1000)
  },
  {
    id: 3,
    username: '赵六',
    action: '删除了权限',
    target: '用户管理',
    status: 'failure',
    avatar: '',
    createdAt: new Date(Date.now() - 30 * 60 * 1000)
  },
  {
    id: 4,
    username: '孙七',
    action: '登录系统',
    target: '',
    status: 'success',
    avatar: '',
    createdAt: new Date(Date.now() - 45 * 60 * 1000)
  },
  {
    id: 5,
    username: '周八',
    action: '分配了角色',
    target: '普通用户',
    status: 'success',
    avatar: '',
    createdAt: new Date(Date.now() - 60 * 60 * 1000)
  }
])

// 快捷操作
const quickActions = reactive([
  {
    key: 'user',
    title: '用户管理',
    description: '添加、编辑用户信息',
    icon: User
  },
  {
    key: 'role',
    title: '角色管理',
    description: '配置角色权限',
    icon: Lock
  },
  {
    key: 'permission',
    title: '权限管理',
    description: '管理系统权限',
    icon: Key
  },
  {
    key: 'log',
    title: '操作日志',
    description: '查看系统日志',
    icon: Document
  },
  {
    key: 'notification',
    title: '消息通知',
    description: '发送系统通知',
    icon: Bell
  }
])

// 获取问候语
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 17) return '下午好'
  if (hour < 19) return '傍晚好'
  return '晚上好'
}

// 处理快捷操作
const handleQuickAction = (key: string) => {
  const routes: Record<string, string> = {
    user: '/system/users',
    role: '/system/roles',
    permission: '/system/permissions',
    log: '/system/logs',
    notification: '/system/notifications'
  }
  
  if (routes[key]) {
    router.push(routes[key])
  }
}

// 初始化用户增长图表
const initUserChart = () => {
  if (!userChartRef.value) return
  
  const chart = echarts.init(userChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增用户',
        type: 'line',
        stack: 'Total',
        smooth: true,
        lineStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        },
        data: [12, 19, 15, 23, 18, 25, 20]
      }
    ]
  }
  
  chart.setOption(option)
  
  // 响应式
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化权限分布图表
const initPermissionChart = () => {
  if (!permissionChartRef.value) return
  
  const chart = echarts.init(permissionChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '权限分布',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 45, name: '用户管理' },
          { value: 32, name: '角色管理' },
          { value: 28, name: '权限管理' },
          { value: 25, name: '系统设置' },
          { value: 18, name: '日志管理' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  chart.setOption(option)
  
  // 响应式
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

onMounted(async () => {
  await nextTick()
  initUserChart()
  initPermissionChart()
})
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;
  background-color: var(--bg-color-page);
  min-height: calc(100vh - 60px);
}

.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 24px;
  background: white;
  border-radius: var(--border-radius-base);
  box-shadow: var(--box-shadow-light);
  
  @media (max-width: 768px) {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
}

.welcome-content {
  .welcome-title {
    font-size: 24px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 8px 0;
  }
  
  .welcome-subtitle {
    font-size: 14px;
    color: var(--text-secondary);
    margin: 0;
  }
}

.welcome-actions {
  display: flex;
  gap: 12px;
  
  @media (max-width: 768px) {
    width: 100%;
    justify-content: center;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 24px;
  background: white;
  border-radius: var(--border-radius-base);
  box-shadow: var(--box-shadow-light);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--box-shadow-base);
  }
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
  
  &.positive {
    color: var(--success-color);
  }
  
  &.negative {
    color: var(--danger-color);
  }
}

.charts-section {
  margin-bottom: 24px;
}

.chart-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .chart-container {
    height: 300px;
  }
}

.activity-section {
  .activity-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}

.activity-list {
  .activity-item {
    display: flex;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid var(--border-lighter);
    
    &:last-child {
      border-bottom: none;
    }
  }
  
  .activity-avatar {
    margin-right: 12px;
  }
  
  .activity-content {
    flex: 1;
  }
  
  .activity-text {
    font-size: 14px;
    color: var(--text-primary);
    margin-bottom: 4px;
    
    .activity-user {
      font-weight: 500;
      color: var(--primary-color);
    }
    
    .activity-action {
      margin: 0 4px;
    }
    
    .activity-target {
      font-weight: 500;
    }
  }
  
  .activity-time {
    font-size: 12px;
    color: var(--text-secondary);
  }
  
  .activity-status {
    margin-left: 12px;
  }
}

.quick-actions-card {
  .quick-actions {
    .quick-action-item {
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
    }
    
    .action-icon {
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
    
    .action-content {
      flex: 1;
    }
    
    .action-title {
      font-size: 14px;
      font-weight: 500;
      color: var(--text-primary);
      margin-bottom: 2px;
    }
    
    .action-desc {
      font-size: 12px;
      color: var(--text-secondary);
    }
    
    .action-arrow {
      color: var(--text-placeholder);
      font-size: 14px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .charts-section {
    :deep(.el-col) {
      margin-bottom: 20px;
    }
  }
}
</style>