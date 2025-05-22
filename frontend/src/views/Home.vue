<template>
  <div class="home-page">
    <div class="home-hero-bg">
      <div class="home-hero-inner">
        <AppCard class="home-hero-card" :body-style="{padding: '48px 40px 40px 40px'}">
          <div class="home-hero-content">
            <img src="https://dummyimage.com/64x64/4f8fff/fff.png&text=LOGO" alt="logo" class="logo" />
            <div>
              <h1 class="home-title">业主线上投票与自治系统</h1>
              <p class="home-subtitle">让小区治理更高效、更透明、更有温度</p>
              <el-button type="primary" size="large" @click="goLogin" round>立即登录体验</el-button>
            </div>
          </div>
        </AppCard>
      </div>
    </div>
    <AppCard class="home-main-card">
      <div class="home-features">
        <div v-for="f in features" :key="f.title" class="feature-item">
          <div class="icon" v-html="f.icon"></div>
          <h3>{{ f.title }}</h3>
          <p>{{ f.desc }}</p>
        </div>
      </div>
      <div class="home-testimonial">
        <div class="quote">“自从用了这个系统，业主参与度提升了，管理也更轻松了！”</div>
        <div class="author">—— 某小区业委会主任</div>
      </div>
    </AppCard>
    <div class="home-footer">© 2024 业主自治系统 · 让每一位业主都能参与社区治理</div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import AppCard from '../components/AppCard.vue'
const router = useRouter()
const goLogin = () => {
  router.push('/login')
}
const features = [
  {
    title: '实名投票',
    desc: '业主实名参与，结果可追溯，杜绝刷票',
    icon: `<svg width="36" height="36" fill="none" viewBox="0 0 36 36"><circle cx="18" cy="18" r="18" fill="#4F8FFF"/><path d="M12 18l4 4 8-8" stroke="#fff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg>`
  },
  {
    title: '高效沟通',
    desc: '公告、建议、投票一站式，信息触达每一位业主',
    icon: `<svg width="36" height="36" fill="none" viewBox="0 0 36 36"><circle cx="18" cy="18" r="18" fill="#FFB300"/><path d="M12 24v-8a4 4 0 014-4h4a4 4 0 014 4v8" stroke="#fff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg>`
  },
  {
    title: '数据安全',
    desc: '多级权限、日志审计，保障数据隐私与安全',
    icon: `<svg width="36" height="36" fill="none" viewBox="0 0 36 36"><circle cx="18" cy="18" r="18" fill="#00C48C"/><path d="M18 24a6 6 0 100-12 6 6 0 000 12z" stroke="#fff" stroke-width="2.5"/></svg>`
  },
  {
    title: '智能统计',
    desc: '投票、建议、业主等数据一目了然，辅助决策',
    icon: `<svg width="36" height="36" fill="none" viewBox="0 0 36 36"><circle cx="18" cy="18" r="18" fill="#FF647C"/><rect x="12" y="18" width="3" height="6" rx="1.5" fill="#fff"/><rect x="17" y="14" width="3" height="10" rx="1.5" fill="#fff"/><rect x="22" y="10" width="3" height="14" rx="1.5" fill="#fff"/></svg>`
  }
]
</script>

<style scoped>
.home-page {
  width: 100%;
  padding: 0 16px 32px 16px;
}
.home-hero-bg {
  width: 100%;
  min-width: 0;
  background: linear-gradient(90deg, #4f8fff 0%, #00c48c 100%);
  border-radius: 0 0 32px 32px;
  padding-top: 48px;
  padding-bottom: 64px;
  margin-bottom: -64px;
  position: relative;
}
.home-hero-inner {
  width: 100%;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;
}
.home-hero-card {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  box-shadow: var(--el-box-shadow-light);
}
.home-hero-content {
  display: flex;
  align-items: center;
  gap: 32px;
}
.logo {
  width: 64px;
  height: 64px;
  margin-right: 16px;
}
.home-title {
  font-size: 2.2em;
  font-weight: 700;
  margin-bottom: 10px;
  color: #222;
}
.home-subtitle {
  font-size: 1.1em;
  color: #666;
  margin-bottom: 18px;
}
.home-main-card {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto 32px auto;
  padding: 32px 0;
  box-shadow: var(--el-box-shadow-light);
}
.home-features {
  display: flex;
  flex-wrap: wrap;
  gap: 32px;
  justify-content: space-between;
  margin-bottom: 32px;
}
.feature-item {
  flex: 1 1 200px;
  min-width: 220px;
  max-width: 240px;
  text-align: center;
  padding: 24px 12px 18px 12px;
}
.icon {
  margin-bottom: 12px;
}
.home-testimonial {
  text-align: center;
  margin-top: 24px;
}
.home-testimonial .quote {
  color: var(--el-color-primary);
  font-size: 1.1em;
  font-style: italic;
  margin-bottom: 8px;
}
.home-testimonial .author {
  color: #888;
  font-size: 1em;
}
.home-footer {
  text-align: center;
  color: #888;
  font-size: 1em;
  margin-top: 32px;
}
@media (max-width: 900px) {
  .home-hero-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  .home-features {
    flex-direction: column;
    gap: 16px;
  }
  .home-main-card {
    padding: 16px 0;
  }
}
</style> 