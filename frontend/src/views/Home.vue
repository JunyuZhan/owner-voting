<template>
  <nav class="navbar">
    <div class="nav-container">
      <a href="#" class="nav-logo">
        <div class="logo-icon">社</div>
        <span>业主自治</span>
      </a>
      <ul class="nav-menu">
        <li><a href="#">首页</a></li>
        <li><a href="#">投票</a></li>
        <li><a href="#">公告</a></li>
        <li><a href="#">建议</a></li>
        <li><a href="#">关于</a></li>
      </ul>
    </div>
  </nav>
  <main class="main-content">
    <section class="hero-section">
      <div class="hero-content">
        <div class="hero-logo">🏠</div>
        <h1 class="hero-title">业主线上投票与自治系统</h1>
        <p class="hero-subtitle">让小区治理更高效、更透明、更有温度</p>
        <a href="#" class="hero-button" @click="handleLogin">立即登录体验</a>
      </div>
    </section>
    <section class="features-section">
      <div class="features-container">
        <h2 class="features-title">为现代社区治理而生</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon vote">
              <svg width="36" height="36" fill="none" viewBox="0 0 36 36">
                <path d="M12 18l4 4 8-8" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3 class="feature-title">实名投票</h3>
            <p class="feature-description">业主实名参与，结果可追溯，杜绝刷票，确保每一票都真实有效</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon communication">
              <svg width="36" height="36" fill="none" viewBox="0 0 36 36">
                <path d="M8 12a4 4 0 014-4h12a4 4 0 014 4v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-8z" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M16 16h4" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
              </svg>
            </div>
            <h3 class="feature-title">高效沟通</h3>
            <p class="feature-description">公告、建议、投票一站式，信息触达每一位业主，沟通零障碍</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon security">
              <svg width="36" height="36" fill="none" viewBox="0 0 36 36">
                <path d="M18 8l-6 4v8a6 6 0 0012 0v-8l-6-4z" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M15 18l2 2 4-4" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3 class="feature-title">数据安全</h3>
            <p class="feature-description">多级权限、日志审计，保障数据隐私与安全，让您安心使用</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon analytics">
              <svg width="36" height="36" fill="none" viewBox="0 0 36 36">
                <rect x="8" y="20" width="4" height="8" rx="2" fill="currentColor"/>
                <rect x="16" y="16" width="4" height="12" rx="2" fill="currentColor"/>
                <rect x="24" y="12" width="4" height="16" rx="2" fill="currentColor"/>
              </svg>
            </div>
            <h3 class="feature-title">智能统计</h3>
            <p class="feature-description">投票、建议、业主等数据一目了然，智能分析辅助决策</p>
          </div>
        </div>
      </div>
    </section>
    <section class="testimonial-section">
      <div class="testimonial-container">
        <div class="testimonial-quote">自从用了这个系统，业主参与度提升了，管理也更轻松了！</div>
        <div class="testimonial-author">—— 某小区业委会主任</div>
      </div>
    </section>
  </main>
  <footer class="footer">
    © 2024 业主自治系统 · 让每一位业主都能参与社区治理
  </footer>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

function handleLogin() {
  router.push('/login')
}

onMounted(() => {
  // 平滑滚动和动画效果
  const featureCards = document.querySelectorAll('.feature-card');
  const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
  };
  const observer = new window.IntersectionObserver(function(entries) {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.style.opacity = '1';
        entry.target.style.transform = 'translateY(0)';
      }
    });
  }, observerOptions);
  featureCards.forEach(card => {
    observer.observe(card);
  });

  // 导航栏滚动效果
  let lastScrollTop = 0;
  const navbar = document.querySelector('.navbar');
  window.addEventListener('scroll', function() {
    let scrollTop = window.pageYOffset || document.documentElement.scrollTop;
    if (scrollTop > 100) {
      navbar.style.background = 'rgba(255, 255, 255, 0.9)';
    } else {
      navbar.style.background = 'rgba(255, 255, 255, 0.72)';
    }
    lastScrollTop = scrollTop <= 0 ? 0 : scrollTop;
  });

  // 特性卡片悬停效果
  featureCards.forEach(card => {
    card.addEventListener('mouseenter', function() {
      this.style.transform = 'translateY(-8px) scale(1.02)';
    });
    card.addEventListener('mouseleave', function() {
      this.style.transform = 'translateY(0) scale(1)';
    });
  });
});
</script>

<style scoped>
/* 复制原有 <style> 内容，去除 body、html 标签样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Icons', 'Helvetica Neue', 'Helvetica', 'Arial', sans-serif;
  line-height: 1.6;
  color: #1d1d1f;
  background: #ffffff;
  overflow-x: hidden;
}

.navbar {
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: saturate(180%) blur(20px);
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
  height: 52px;
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
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, #007aff, #5856d6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 16px;
}

.nav-menu {
  display: flex;
  list-style: none;
  gap: 32px;
}

.nav-menu a {
  color: #1d1d1f;
  text-decoration: none;
  font-size: 17px;
  font-weight: 400;
  transition: color 0.3s ease;
}

.nav-menu a:hover {
  color: #007aff;
}

.main-content {
  margin-top: 52px;
}

.hero-section {
  background: linear-gradient(135deg, #007aff 0%, #5856d6 50%, #af52de 100%);
  color: white;
  padding: 120px 24px 160px;
  position: relative;
  overflow: hidden;
  text-align: center;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 30% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
              radial-gradient(circle at 70% 80%, rgba(255, 255, 255, 0.08) 0%, transparent 50%);
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 800px;
  margin: 0 auto;
}

.hero-logo {
  width: 96px;
  height: 96px;
  margin: 0 auto 32px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  font-weight: 700;
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.hero-title {
  font-size: 64px;
  font-weight: 700;
  line-height: 1.1;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #ffffff 0%, rgba(255, 255, 255, 0.8) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 28px;
  font-weight: 400;
  margin-bottom: 48px;
  opacity: 0.9;
  line-height: 1.3;
}

.hero-button {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  padding: 16px 32px;
  border-radius: 980px;
  font-size: 19px;
  font-weight: 600;
  text-decoration: none;
  display: inline-block;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.hero-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.hero-button:hover::before {
  left: 100%;
}

.hero-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.2);
  background: rgba(255, 255, 255, 0.25);
}

.features-section {
  padding: 120px 24px;
  background: #f5f5f7;
  position: relative;
}

.features-container {
  max-width: 1200px;
  margin: 0 auto;
}

.features-title {
  text-align: center;
  font-size: 48px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 80px;
  line-height: 1.1;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 120px;
}

.feature-card {
  background: white;
  border-radius: 24px;
  padding: 48px 32px;
  text-align: center;
  position: relative;
  transition: all 0.4s ease;
  border: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 32px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: white;
  position: relative;
  transition: transform 0.3s ease;
}

.feature-card:hover .feature-icon {
  transform: scale(1.1);
}

.feature-icon.vote {
  background: linear-gradient(135deg, #007aff, #5ac8fa);
}

.feature-icon.communication {
  background: linear-gradient(135deg, #ff9500, #ffcc00);
}

.feature-icon.security {
  background: linear-gradient(135deg, #34c759, #30d158);
}

.feature-icon.analytics {
  background: linear-gradient(135deg, #ff3b30, #ff6961);
}

.feature-title {
  font-size: 24px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 16px;
}

.feature-description {
  font-size: 17px;
  color: #86868b;
  line-height: 1.5;
}

.testimonial-section {
  background: white;
  padding: 80px 24px;
  text-align: center;
  position: relative;
}

.testimonial-container {
  max-width: 800px;
  margin: 0 auto;
}

.testimonial-quote {
  font-size: 32px;
  font-weight: 600;
  color: #1d1d1f;
  line-height: 1.3;
  margin-bottom: 32px;
  position: relative;
}

.testimonial-quote::before {
  content: '"';
  color: #007aff;
  font-size: 64px;
  position: absolute;
  left: -32px;
  top: -16px;
  opacity: 0.3;
}

.testimonial-author {
  font-size: 17px;
  color: #86868b;
  font-weight: 400;
}

.footer {
  background: #1d1d1f;
  color: #86868b;
  text-align: center;
  padding: 48px 24px;
  font-size: 17px;
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  .hero-title {
    font-size: 32px;
    word-break: break-all;
  }
  .hero-subtitle {
    font-size: 16px;
    margin-bottom: 32px;
  }
  .hero-section {
    padding: 40px 8px 60px;
  }
  .features-title {
    font-size: 22px;
    margin-bottom: 32px;
  }
  .features-section {
    padding: 32px 8px;
  }
  .features-grid {
    grid-template-columns: 1fr;
    gap: 12px;
    margin-bottom: 32px;
  }
  .feature-card {
    padding: 16px 8px;
    border-radius: 14px;
  }
  .feature-icon {
    width: 48px;
    height: 48px;
    font-size: 22px;
    margin-bottom: 16px;
    border-radius: 12px;
  }
  .feature-title {
    font-size: 16px;
    margin-bottom: 8px;
  }
  .feature-description {
    font-size: 13px;
  }
  .testimonial-quote {
    font-size: 16px;
    margin-bottom: 16px;
  }
  .testimonial-quote::before {
    font-size: 28px;
    left: -12px;
    top: -6px;
  }
  .testimonial-section {
    padding: 32px 8px;
  }
  .testimonial-author {
    font-size: 13px;
  }
  .footer {
    padding: 16px 8px;
    font-size: 13px;
  }
  .hero-logo {
    width: 48px;
    height: 48px;
    font-size: 24px;
    margin-bottom: 16px;
    border-radius: 12px;
  }
  .hero-button {
    padding: 10px 18px;
    font-size: 15px;
  }
}

@media (max-width: 600px) {
  .main-content {
    margin-top: 44px;
  }
  .nav-container {
    padding: 0 8px;
    height: 44px;
  }
  .navbar {
    height: 44px;
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hero-content > * {
  animation: fadeInUp 0.8s ease-out;
}

.hero-subtitle {
  animation-delay: 0.2s;
}

.hero-button {
  animation-delay: 0.4s;
}

.feature-card {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeInUp 0.6s ease-out forwards;
}

.feature-card:nth-child(1) { animation-delay: 0.1s; }
.feature-card:nth-child(2) { animation-delay: 0.2s; }
.feature-card:nth-child(3) { animation-delay: 0.3s; }
.feature-card:nth-child(4) { animation-delay: 0.4s; }
</style>