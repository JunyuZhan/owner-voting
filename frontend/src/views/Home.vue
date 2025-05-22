<template>
  <div class="home-bg">
    <div class="hero">
      <svg class="hero-bg-left" width="320" height="320" viewBox="0 0 320 320" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="160" cy="160" r="160" fill="url(#leftGradient)" fill-opacity="0.18"/>
        <defs>
          <radialGradient id="leftGradient" cx="0.5" cy="0.5" r="0.5" fx="0.3" fy="0.3">
            <stop stop-color="#4F8FFF"/>
            <stop offset="1" stop-color="#fff" stop-opacity="0"/>
          </radialGradient>
        </defs>
      </svg>
      <svg class="hero-bg-right" width="320" height="320" viewBox="0 0 320 320" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="160" cy="160" r="160" fill="url(#rightGradient)" fill-opacity="0.18"/>
        <defs>
          <radialGradient id="rightGradient" cx="0.5" cy="0.5" r="0.5" fx="0.7" fy="0.7">
            <stop stop-color="#00C48C"/>
            <stop offset="1" stop-color="#fff" stop-opacity="0"/>
          </radialGradient>
        </defs>
      </svg>
      <div class="hero-inner">
        <div class="hero-content">
          <img src="https://dummyimage.com/128x128/4f8fff/fff.png&text=LOGO" alt="logo" class="logo" />
          <h1>业主线上投票与自治系统</h1>
          <p class="subtitle">让小区治理更高效、更透明、更有温度</p>
          <el-button type="primary" size="large" class="cta-btn" @click="goLogin">立即登录体验</el-button>
        </div>
        <img class="hero-illustration" src="https://assets-global.website-files.com/5d9bc5d562ffc2869b73b2f8/63e3e2e2e2e2e2e2e2e2e2e2_community-illustration.svg" alt="社区插画" />
      </div>
    </div>
    <div class="features">
      <div class="feature-card" v-for="f in features" :key="f.title">
        <div class="icon" v-html="f.icon"></div>
        <h3>{{ f.title }}</h3>
        <p>{{ f.desc }}</p>
      </div>
    </div>
    <div class="testimonial">
      <div class="testimonial-content">
        <div class="quote">“自从用了这个系统，业主参与度提升了，管理也更轻松了！”</div>
        <div class="author">—— 某小区业委会主任</div>
      </div>
    </div>
    <footer class="footer">
      <span>© 2024 业主自治系统</span>
      <span style="margin-left: 16px; color: #aaa;">让每一位业主都能参与社区治理</span>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
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
.home-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e0e7ef 100%);
  width: 100%;
  min-width: 0;
  overflow-x: hidden;
}
.hero {
  width: 100%;
  background: linear-gradient(90deg, #4f8fff 0%, #00c48c 100%);
  color: #fff;
  position: relative;
  border-radius: 0 0 24px 24px;
  padding: 64px 0 32px 0;
  overflow: hidden;
}
.hero-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
}
.hero-content {
  z-index: 2;
  max-width: 480px;
  text-align: left;
}
.logo {
  width: 56px;
  margin-bottom: 16px;
}
.hero h1 {
  font-size: 2.6em;
  font-weight: 700;
  margin-bottom: 12px;
}
.subtitle {
  font-size: 1.25em;
  margin-bottom: 28px;
  color: #e0f2ff;
}
.cta-btn {
  font-size: 1.1em;
  padding: 12px 36px;
  border-radius: 24px;
  box-shadow: 0 4px 16px rgba(79,143,255,0.18);
  transition: background 0.2s, box-shadow 0.2s;
}
.cta-btn:hover {
  background: #005be4;
  box-shadow: 0 6px 24px rgba(79,143,255,0.28);
}
.hero-illustration {
  width: 340px;
  margin-left: 48px;
  z-index: 1;
  filter: drop-shadow(0 8px 32px rgba(0,0,0,0.08));
  display: none;
}
@media (min-width: 900px) {
  .hero-illustration {
    display: block;
  }
}
.features {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 32px;
  margin: 48px 0 0 0;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}
.feature-card {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 32px 24px 24px 24px;
  width: 240px;
  text-align: center;
  transition: box-shadow 0.2s, transform 0.2s;
}
.feature-card:hover {
  box-shadow: 0 8px 32px rgba(79,143,255,0.13);
  transform: translateY(-4px) scale(1.03);
}
.icon {
  margin-bottom: 16px;
}
.feature-card h3 {
  font-size: 1.18em;
  margin-bottom: 8px;
  color: #222;
}
.feature-card p {
  color: #666;
  font-size: 1em;
}
.testimonial {
  margin: 64px 0 0 0;
  display: flex;
  justify-content: center;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}
.testimonial-content {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 32px 40px;
  max-width: 520px;
  text-align: center;
}
.quote {
  font-size: 1.18em;
  color: #4f8fff;
  margin-bottom: 12px;
  font-style: italic;
}
.author {
  color: #888;
  font-size: 1em;
}
.footer {
  margin: 64px 0 0 0;
  padding: 24px 0 16px 0;
  text-align: center;
  color: #888;
  font-size: 1em;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}
.hero-bg-left {
  position: absolute;
  left: -120px;
  top: -80px;
  z-index: 0;
}
.hero-bg-right {
  position: absolute;
  right: -120px;
  top: 40px;
  z-index: 0;
}
.hero-content, .hero-illustration {
  position: relative;
  z-index: 1;
}
</style> 