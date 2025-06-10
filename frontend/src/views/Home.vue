<template>
  <nav class="navbar">
    <div class="nav-container">
      <a href="#" class="nav-logo">
        <div class="logo-icon">社</div>
        <span>业主自治</span>
      </a>
      <ul class="nav-menu">
        <li><a href="#communities">小区</a></li>
        <li><a href="#votes">投票</a></li>
        <li><a href="#announcements">公告</a></li>
        <li><a href="#about">关于</a></li>
        <li><a href="#" @click="goToApplication">申请管理</a></li>
      </ul>
      <div class="nav-actions">
        <button class="login-btn" @click="handleLogin">登录</button>
      </div>
    </div>
  </nav>
  
  <main class="main-content">
    <!-- 首页横幅 -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="hero-logo">🏠</div>
        <h1 class="hero-title">业主线上投票与自治系统</h1>
        <p class="hero-subtitle">让小区治理更高效、更透明、更有温度</p>
        <div class="hero-stats">
          <div class="stat-item">
            <div class="stat-number">{{ totalCommunities }}</div>
            <div class="stat-label">接入小区</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ totalVotes }}</div>
            <div class="stat-label">累计投票</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ totalOwners }}</div>
            <div class="stat-label">注册业主</div>
          </div>
        </div>
        <div class="hero-buttons">
          <button class="hero-button primary" @click="handleLogin">立即登录参与</button>
          <button class="hero-button secondary" @click="goToApplication">申请成为管理员</button>
        </div>
      </div>
    </section>

    <!-- 小区列表 -->
    <section id="communities" class="communities-section">
      <div class="section-container">
        <h2 class="section-title">已接入小区</h2>
        <div class="communities-grid" v-loading="loadingCommunities">
          <div v-for="community in communities" :key="community.id" class="community-card">
            <div class="community-header">
              <h3 class="community-name">{{ community.name }}</h3>
              <span class="community-status">运行中</span>
            </div>
            <p class="community-address">📍 {{ community.address }}</p>
            <p class="community-description">{{ community.description }}</p>
            <div class="community-stats">
              <span class="stat">{{ community.voteCount || 0 }} 次投票</span>
              <span class="stat">{{ community.announcementCount || 0 }} 个公告</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 最新投票结果 -->
    <section id="votes" class="votes-section">
      <div class="section-container">
        <h2 class="section-title">最新投票结果</h2>
        <div class="votes-grid" v-loading="loadingVotes">
          <div v-for="vote in publicVotes" :key="vote.id" class="vote-card">
            <div class="vote-header">
              <h3 class="vote-title">{{ vote.title }}</h3>
              <span class="vote-status completed">已结束</span>
            </div>
            <p class="vote-community">{{ vote.communityName }}</p>
            <p class="vote-description">{{ vote.description }}</p>
            <div class="vote-result">
              <div class="result-stats">
                <span>参与率: {{ vote.participationRate }}%</span>
                <span>总票数: {{ vote.totalVotes }}</span>
              </div>
              <div class="winning-option">
                胜出选项: {{ vote.winningOption }}
              </div>
            </div>
            <div class="vote-time">
              结束时间: {{ formatDate(vote.endTime) }}
            </div>
          </div>
        </div>
        <div class="section-footer">
          <button class="view-more-btn" @click="handleLogin">登录查看更多投票</button>
        </div>
      </div>
    </section>

    <!-- 公告信息 -->
    <section id="announcements" class="announcements-section">
      <div class="section-container">
        <h2 class="section-title">公开公告</h2>
        <div class="announcements-list" v-loading="loadingAnnouncements">
          <div v-for="announcement in publicAnnouncements" :key="announcement.id" class="announcement-card">
            <div class="announcement-header">
              <h3 class="announcement-title">{{ announcement.title }}</h3>
              <span class="announcement-type">{{ getAnnouncementTypeText(announcement.type) }}</span>
            </div>
            <p class="announcement-community">{{ announcement.communityName }}</p>
            <p class="announcement-content">{{ announcement.content.substring(0, 200) }}...</p>
            <div class="announcement-footer">
              <span class="announcement-time">{{ formatDate(announcement.publishedAt) }}</span>
              <button class="read-more-btn" @click="handleLogin">登录查看详情</button>
            </div>
          </div>
        </div>
        <div class="section-footer">
          <button class="view-more-btn" @click="handleLogin">登录查看更多公告</button>
        </div>
      </div>
    </section>

    <!-- 广告位 -->
    <section class="ad-section">
      <div class="section-container">
        <AdBanner 
          :banner-ad="currentAd?.type === 'banner' ? currentAd : null"
          :baidu-config="currentAd?.type === 'baidu' ? currentAd.config : null"
          :show-ad="showAdvertisement && currentAd"
        />
      </div>
    </section>

    <!-- 系统特色 -->
    <section class="features-section">
      <div class="section-container">
        <h2 class="section-title">为现代社区治理而生</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon vote">🗳️</div>
            <h3 class="feature-title">透明投票</h3>
            <p class="feature-description">实名认证，结果公开，每一票都可追溯</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon communication">📢</div>
            <h3 class="feature-title">高效沟通</h3>
            <p class="feature-description">公告、建议、投票一站式，信息及时触达</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon security">🔒</div>
            <h3 class="feature-title">数据安全</h3>
            <p class="feature-description">多级权限保护，确保个人隐私安全</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon analytics">📊</div>
            <h3 class="feature-title">智能统计</h3>
            <p class="feature-description">数据分析可视化，决策更科学</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 关于系统 -->
    <section id="about" class="about-section">
      <div class="section-container">
        <h2 class="section-title">关于系统</h2>
        <div class="about-content">
          <p>业主投票与自治系统致力于为现代住宅小区提供数字化治理解决方案。通过在线投票、公告发布、意见收集等功能，促进业主参与社区事务，提升治理效率和透明度。</p>
          <div class="about-features">
            <div class="about-feature">
              <strong>实名认证</strong> - 确保每位参与者身份真实
            </div>
            <div class="about-feature">
              <strong>移动友好</strong> - 随时随地参与社区治理
            </div>
            <div class="about-feature">
              <strong>数据安全</strong> - 严格保护用户隐私
            </div>
            <div class="about-feature">
              <strong>结果公开</strong> - 投票结果实时公布
            </div>
          </div>
        </div>
      </div>
    </section>
  </main>
  
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-section">
        <h4>联系我们</h4>
        <p>邮箱: support@community-voting.com</p>
        <p>电话: 400-123-4567</p>
      </div>
      <div class="footer-section">
        <h4>帮助中心</h4>
        <p>使用指南</p>
        <p>常见问题</p>
      </div>
      <div class="footer-section">
        <h4>法律信息</h4>
        <p>隐私政策</p>
        <p>使用条款</p>
      </div>
    </div>
    <div class="footer-bottom">
      © 2024 业主自治系统 · 让每一位业主都能参与社区治理
    </div>
  </footer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AdBanner from '@/components/AdBanner.vue'

const router = useRouter()

// 响应式数据
const communities = ref([])
const publicVotes = ref([])
const publicAnnouncements = ref([])
const totalCommunities = ref(0)
const totalVotes = ref(0)
const totalOwners = ref(0)
const loadingCommunities = ref(false)
const loadingVotes = ref(false)
const loadingAnnouncements = ref(false)

// 广告相关
const showAdvertisement = ref(true)
const currentAd = ref(null)

// 获取公开数据
const fetchPublicData = async () => {
  try {
    // 获取小区列表
    loadingCommunities.value = true
    const communitiesRes = await fetch('/api/community/public')
    if (communitiesRes.ok) {
      const communitiesData = await communitiesRes.json()
      if (communitiesData.code === 200) {
        communities.value = communitiesData.data.slice(0, 6) // 显示前6个
        totalCommunities.value = communitiesData.data.length
      }
    }
  } catch (error) {
    console.error('获取小区数据失败:', error)
    // 使用模拟数据
    communities.value = [
      {
        id: 1,
        name: '示范小区',
        address: '北京市海淀区中关村大街1号',
        description: '这是一个示范小区，用于系统演示',
        voteCount: 15,
        announcementCount: 8
      },
      {
        id: 2,
        name: '阳光花园',
        address: '北京市朝阳区建国路88号',
        description: '阳光花园是一个现代化住宅小区',
        voteCount: 12,
        announcementCount: 5
      }
    ]
    totalCommunities.value = 2
  } finally {
    loadingCommunities.value = false
  }

  try {
    // 获取公开投票结果
    loadingVotes.value = true
    const votesRes = await fetch('/api/vote-topic/public')
    if (votesRes.ok) {
      const votesData = await votesRes.json()
      if (votesData.code === 200) {
        publicVotes.value = votesData.data.slice(0, 4)
        totalVotes.value = votesData.total || votesData.data.length
      }
    }
  } catch (error) {
    console.error('获取投票数据失败:', error)
    // 使用模拟数据
    publicVotes.value = [
      {
        id: 1,
        title: '小区停车位分配方案投票',
        communityName: '示范小区',
        description: '针对小区停车位紧张问题，提出三种分配方案供业主选择',
        participationRate: 78,
        totalVotes: 156,
        winningOption: '按楼栋分配固定车位',
        endTime: '2024-01-15T18:00:00'
      },
      {
        id: 2,
        title: '物业费调整投票',
        communityName: '阳光花园',
        description: '考虑到物价上涨和服务升级，物业费需要适当调整',
        participationRate: 85,
        totalVotes: 210,
        winningOption: '上调10%，加强安保服务',
        endTime: '2024-01-10T20:00:00'
      }
    ]
    totalVotes.value = 27
  } finally {
    loadingVotes.value = false
  }

  try {
    // 获取公开公告
    loadingAnnouncements.value = true
    const announcementsRes = await fetch('/api/announcement/public')
    if (announcementsRes.ok) {
      const announcementsData = await announcementsRes.json()
      if (announcementsData.code === 200) {
        publicAnnouncements.value = announcementsData.data.slice(0, 5)
      }
    }
  } catch (error) {
    console.error('获取公告数据失败:', error)
    // 使用模拟数据
    publicAnnouncements.value = [
      {
        id: 1,
        title: '春节期间物业服务安排通知',
        type: 'NOTICE',
        communityName: '示范小区',
        content: '尊敬的业主，春节期间（2月9日-2月17日）物业服务安排如下：1. 保安24小时值班；2. 保洁每日上午清理；3. 维修服务电话保持畅通...',
        publishedAt: '2024-02-01T10:00:00'
      },
      {
        id: 2,
        title: '小区绿化改造完工公告',
        type: 'NOTICE',
        communityName: '阳光花园',
        content: '经过一个月的施工，小区绿化改造工程已全面完工。新增绿植覆盖面积200平方米，安装了自动喷灌系统，预计春季将呈现更加美丽的景色...',
        publishedAt: '2024-01-28T14:30:00'
      }
    ]
  } finally {
    loadingAnnouncements.value = false
  }

  // 设置模拟统计数据
  totalOwners.value = 1247

  // 获取广告数据
  try {
    const adRes = await fetch('/api/ad/current')
    if (adRes.ok) {
      const adData = await adRes.json()
      if (adData.code === 200 && adData.data) {
        currentAd.value = adData.data
      }
    }
  } catch (error) {
    console.error('获取广告数据失败:', error)
    // 使用默认广告
    currentAd.value = {
      id: 0,
      title: '物业管理系统专业版',
      image: 'https://via.placeholder.com/300x100?text=物业管理系统',
      link: 'https://example.com'
    }
  }
}

// 工具函数
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

const getAnnouncementTypeText = (type) => {
  const types = {
    'NOTICE': '通知',
    'FINANCIAL': '财务',
    'VOTE_RESULT': '投票结果',
    'OTHER': '其他'
  }
  return types[type] || '通知'
}

const handleLogin = () => {
  router.push('/login')
}

const goToApplication = () => {
  router.push('/community-admin-application')
}

onMounted(() => {
  fetchPublicData()
  
  // 平滑滚动
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
      e.preventDefault()
      const target = document.querySelector(this.getAttribute('href'))
      if (target) {
        target.scrollIntoView({
          behavior: 'smooth'
        })
      }
    })
  })
})
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'Helvetica Neue', sans-serif;
  line-height: 1.6;
  color: #1d1d1f;
  background: #ffffff;
}

/* 导航栏样式 */
.navbar {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  position: fixed;
  top: 0;
  width: 100%;
  z-index: 1000;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.nav-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  text-decoration: none;
}

.logo-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: linear-gradient(135deg, #007aff, #5856d6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
}

.nav-menu {
  display: flex;
  list-style: none;
  gap: 32px;
}

.nav-menu a {
  color: #1d1d1f;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.nav-menu a:hover {
  color: #007aff;
}

.login-btn {
  background: #007aff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.login-btn:hover {
  background: #0056b3;
}

/* 主要内容区域 */
.main-content {
  margin-top: 60px;
}

/* 首页横幅 */
.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 80px 24px;
  text-align: center;
}

.hero-logo {
  font-size: 64px;
  margin-bottom: 24px;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
}

.hero-subtitle {
  font-size: 20px;
  margin-bottom: 40px;
  opacity: 0.9;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 60px;
  margin-bottom: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 16px;
  opacity: 0.8;
}

.hero-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.hero-button {
  background: white;
  color: #667eea;
  border: none;
  padding: 16px 32px;
  border-radius: 30px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s;
}

.hero-button:hover {
  transform: translateY(-2px);
}

.hero-button.primary {
  background: white;
  color: #667eea;
}

.hero-button.secondary {
  background: white;
  color: #667eea;
}

/* 通用段落样式 */
.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 48px;
  color: #1d1d1f;
}

.section-footer {
  text-align: center;
  margin-top: 40px;
}

.view-more-btn {
  background: #f5f5f7;
  color: #1d1d1f;
  border: none;
  padding: 12px 24px;
  border-radius: 20px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.view-more-btn:hover {
  background: #e5e5e7;
}

/* 小区列表样式 */
.communities-section {
  padding: 80px 0;
  background: #f5f5f7;
}

.communities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 24px;
}

.community-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.community-card:hover {
  transform: translateY(-4px);
}

.community-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.community-name {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
}

.community-status {
  background: #34c759;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.community-address {
  color: #86868b;
  margin-bottom: 12px;
}

.community-description {
  color: #1d1d1f;
  margin-bottom: 16px;
  line-height: 1.5;
}

.community-stats {
  display: flex;
  gap: 16px;
}

.stat {
  background: #f5f5f7;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 14px;
  color: #86868b;
}

/* 投票结果样式 */
.votes-section {
  padding: 80px 0;
}

.votes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.vote-card {
  background: white;
  border: 1px solid #e5e5e7;
  border-radius: 16px;
  padding: 24px;
  transition: box-shadow 0.2s;
}

.vote-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.vote-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.vote-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  flex: 1;
}

.vote-status.completed {
  background: #86868b;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.vote-community {
  color: #007aff;
  font-weight: 500;
  margin-bottom: 12px;
}

.vote-description {
  color: #86868b;
  margin-bottom: 16px;
  line-height: 1.5;
}

.vote-result {
  background: #f5f5f7;
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 12px;
}

.result-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
  color: #86868b;
}

.winning-option {
  font-weight: 600;
  color: #34c759;
}

.vote-time {
  font-size: 14px;
  color: #86868b;
}

/* 公告样式 */
.announcements-section {
  padding: 80px 0;
  background: #f5f5f7;
}

.announcements-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.announcement-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.announcement-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
}

.announcement-type {
  background: #007aff;
  color: white;
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 12px;
}

.announcement-community {
  color: #007aff;
  font-weight: 500;
  margin-bottom: 8px;
}

.announcement-content {
  color: #86868b;
  line-height: 1.5;
  margin-bottom: 12px;
}

.announcement-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.announcement-time {
  font-size: 14px;
  color: #86868b;
}

.read-more-btn {
  background: #007aff;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
  cursor: pointer;
}

/* 特色功能样式 */
.features-section {
  padding: 80px 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 32px;
}

.feature-card {
  text-align: center;
  padding: 32px 20px;
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.feature-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #1d1d1f;
}

.feature-description {
  color: #86868b;
  line-height: 1.5;
}

/* 关于系统样式 */
.about-section {
  padding: 80px 0;
  background: #f5f5f7;
}

.about-content {
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

.about-content p {
  font-size: 18px;
  line-height: 1.6;
  margin-bottom: 32px;
  color: #1d1d1f;
}

.about-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.about-feature {
  background: white;
  padding: 20px;
  border-radius: 12px;
  font-size: 16px;
}

/* 页脚样式 */
.footer {
  background: #1d1d1f;
  color: #86868b;
  padding: 60px 0 20px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 40px;
  margin-bottom: 40px;
}

.footer-section h4 {
  color: white;
  margin-bottom: 16px;
  font-size: 18px;
}

.footer-section p {
  margin-bottom: 8px;
  cursor: pointer;
  transition: color 0.2s;
}

.footer-section p:hover {
  color: #007aff;
}

.footer-bottom {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 24px 0;
  border-top: 1px solid #333;
  text-align: center;
}

/* 广告区域样式 */
.ad-section {
  padding: 40px 0;
  background: #ffffff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .hero-title {
    font-size: 32px;
  }
  
  .hero-subtitle {
    font-size: 18px;
  }
  
  .hero-stats {
    flex-direction: column;
    gap: 20px;
  }
  
  .section-title {
    font-size: 24px;
  }
  
  .communities-grid,
  .votes-grid {
    grid-template-columns: 1fr;
  }
  
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .about-features {
    grid-template-columns: 1fr;
  }
}
</style>