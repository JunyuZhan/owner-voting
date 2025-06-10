import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import OwnerVerify from '../views/OwnerVerify.vue'
import VoteList from '../views/VoteList.vue'
import VoteDetail from '../views/VoteDetail.vue'
import AnnouncementList from '../views/AnnouncementList.vue'
import AnnouncementDetail from '../views/AnnouncementDetail.vue'
import Login from '../views/Login.vue'
import SuggestionList from '../views/SuggestionList.vue'
import SuggestionDetail from '../views/SuggestionDetail.vue'
import SuggestionAdd from '../views/SuggestionAdd.vue'
import PersonalCenter from '../views/PersonalCenter.vue'
import Register from '../views/Register.vue'
import CommunitySelect from '../views/CommunitySelect.vue'
import HouseRegister from '../views/HouseRegister.vue'
import HouseManagement from '../views/HouseManagement.vue'

// Admin页面
import AdminLayout from '../views/admin/AdminLayout.vue'
import AdminVoteList from '../views/admin/AdminVoteList.vue'
import AdminAnnouncementList from '../views/admin/AdminAnnouncementList.vue'
import AdminOwnerList from '../views/admin/AdminOwnerList.vue'
import AdminSuggestionList from '../views/admin/AdminSuggestionList.vue'
import AdminLogList from '../views/admin/AdminLogList.vue'
import AdminDashboard from '../views/admin/AdminDashboard.vue'
import CommunityManagement from '../views/admin/CommunityManagement.vue'
import AdminProfile from '../views/admin/AdminProfile.vue'
import AdManagement from '../views/admin/AdManagement.vue'
import CommunityApplications from '../views/admin/CommunityApplications.vue'
import CommunityAdminApplication from '../views/CommunityAdminApplication.vue'
import CommunityAdminApplicationManagement from '../views/admin/CommunityAdminApplicationManagement.vue'

const routes = [
  { 
    path: '/', 
    component: Home, 
    meta: { 
      title: '首页',
      noLayout: true // 首页不使用默认布局
    } 
  },
  { 
    path: '/login', 
    component: Login, 
    meta: {
      title: '登录',
      noLayout: true 
    } 
  },
  { 
    path: '/register', 
    component: Register, 
    meta: {
      title: '注册',
      noLayout: true
    } 
  },
  { 
    path: '/community-select', 
    component: CommunitySelect, 
    meta: { 
      title: '选择小区',
      requiresOwner: true 
    } 
  },
  { 
    path: '/community-admin-application', 
    component: CommunityAdminApplication, 
    meta: { 
      title: '申请小区管理员',
      noLayout: true 
    } 
  },
  { 
    path: '/owner/verify', 
    component: OwnerVerify,
    meta: { title: '业主认证' }
  },
  { 
    path: '/votes', 
    component: VoteList,
    meta: { title: '投票列表' }
  },
  { 
    path: '/votes/:id', 
    component: VoteDetail,
    meta: { title: '投票详情' }
  },
  { 
    path: '/announcements', 
    component: AnnouncementList,
    meta: { title: '公告列表' }
  },
  { 
    path: '/announcements/:id', 
    component: AnnouncementDetail,
    meta: { title: '公告详情' }
  },
  { 
    path: '/suggestions', 
    component: SuggestionList,
    meta: { title: '建议列表' }
  },
  { 
    path: '/suggestions/add', 
    component: SuggestionAdd,
    meta: { title: '提交建议' }
  },
  { 
    path: '/suggestions/:id', 
    component: SuggestionDetail,
    meta: { title: '建议详情' }
  },
  { 
    path: '/personal-center', 
    component: PersonalCenter,
    meta: { 
      title: '个人中心',
      requiresOwner: true 
    }
  },
  { 
    path: '/house-register', 
    component: HouseRegister,
    meta: { 
      title: '房屋注册',
      requiresOwner: true 
    }
  },
  { 
    path: '/house-management', 
    component: HouseManagement,
    meta: { 
      title: '房屋管理',
      requiresOwner: true 
    }
  },
  
  // 管理员路由
  { 
    path: '/admin', 
    component: AdminLayout,
    redirect: '/admin/dashboard',
    meta: { 
      requiresAdmin: true 
    },
    children: [
      { 
        path: 'dashboard', 
        component: AdminDashboard,
        meta: { title: '控制台' }
      },
      { 
        path: 'votes', 
        component: AdminVoteList,
        meta: { title: '投票管理' }
      },
      { 
        path: 'announcements', 
        component: AdminAnnouncementList,
        meta: { title: '公告管理' }
      },
      { 
        path: 'owners', 
        component: AdminOwnerList,
        meta: { title: '业主管理' }
      },
      { 
        path: 'suggestions', 
        component: AdminSuggestionList,
        meta: { title: '建议管理' }
      },
      { 
        path: 'logs', 
        component: AdminLogList,
        meta: { title: '日志管理' }
      },
      { 
        path: 'communities', 
        component: CommunityManagement,
        meta: { 
          title: '小区管理',
          requiresSystemAdmin: true // 仅系统管理员可访问
        }
      },
      { 
        path: 'community-applications', 
        component: CommunityApplications,
        meta: { title: '业主申请审核' }
      },
      { 
        path: 'admin-applications', 
        component: CommunityAdminApplicationManagement,
        meta: { 
          title: '管理员申请审核',
          requiresSystemAdmin: true // 仅系统管理员可访问
        }
      },
      { 
        path: 'profile', 
        component: AdminProfile,
        meta: { title: '个人设置' }
      },
      { 
        path: 'ads', 
        component: AdManagement,
        meta: { 
          title: '广告管理',
          requiresSystemAdmin: true // 仅系统管理员可访问
        }
      }
    ] 
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 路由守卫：未登录跳转登录页，非管理员不能访问/admin
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 业主投票系统` : '业主投票系统'
  
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  const currentCommunity = localStorage.getItem('currentCommunity')
  
  // 未登录检查
  if (to.path !== '/' && to.path !== '/login' && to.path !== '/register' && !token) {
    next('/login')
    return
  }
  
  // 管理员路由检查
  if (to.meta.requiresAdmin) {
    const role = user.role
    if (role === 'SYSTEM_ADMIN' || role === 'COMMUNITY_ADMIN' || role === 'OPERATOR') {
      // 检查是否需要系统管理员权限
      if (to.meta.requiresSystemAdmin && role !== 'SYSTEM_ADMIN') {
        next('/admin/dashboard') // 重定向到仪表盘
      } else {
        next()
      }
    } else {
      next('/')
    }
    return
  }
  
  // 业主路由检查
  if (to.meta.requiresOwner && token) {
    // 业主必须先选择小区（除了小区选择页面本身）
    if (to.path !== '/community-select' && !currentCommunity && user.role !== 'SYSTEM_ADMIN' && user.role !== 'COMMUNITY_ADMIN') {
      next('/community-select')
      return
    }
  }
  
  next()
})

export default router 