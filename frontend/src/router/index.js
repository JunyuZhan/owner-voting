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
import AdminLayout from '../views/admin/AdminLayout.vue'
import AdminVoteList from '../views/admin/AdminVoteList.vue'
import AdminAnnouncementList from '../views/admin/AdminAnnouncementList.vue'
import AdminOwnerList from '../views/admin/AdminOwnerList.vue'
import AdminSuggestionList from '../views/admin/AdminSuggestionList.vue'
import AdminLogList from '../views/admin/AdminLogList.vue'
import AdminDashboard from '../views/admin/AdminDashboard.vue'
import Register from '../views/Register.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/owner/verify', component: OwnerVerify },
  { path: '/votes', component: VoteList },
  { path: '/votes/:id', component: VoteDetail },
  { path: '/announcements', component: AnnouncementList },
  { path: '/announcements/:id', component: AnnouncementDetail },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/suggestions', component: SuggestionList },
  { path: '/suggestions/add', component: SuggestionAdd },
  { path: '/suggestions/:id', component: SuggestionDetail },
  { path: '/personal', component: PersonalCenter },
  { path: '/admin', component: AdminLayout, children: [
    { path: '', component: AdminDashboard },
    { path: 'dashboard', component: AdminDashboard },
    { path: 'votes', component: AdminVoteList },
    { path: 'announcements', component: AdminAnnouncementList },
    { path: 'owners', component: AdminOwnerList },
    { path: 'suggestions', component: AdminSuggestionList },
    { path: 'logs', component: AdminLogList }
    // 其他管理页面可继续添加
  ] }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转登录页，非管理员不能访问/admin
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (to.path !== '/' && to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path.startsWith('/admin')) {
    const role = user.role
    if (role === 'SYSTEM_ADMIN' || role === 'COMMUNITY_ADMIN' || role === 'OPERATOR') {
      next()
    } else {
      next('/')
    }
  } else {
    next()
  }
})

export default router 