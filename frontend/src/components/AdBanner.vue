<template>
  <div class="ad-container" v-if="adData && showAd">
    <div class="ad-label">赞助商</div>
    
    <!-- 横幅广告 -->
    <div class="banner-ad" v-if="adData.type === 'BANNER' && adData.imageUrl">
      <a :href="adData.linkUrl" target="_blank" rel="noopener" @click="recordClick">
        <img :src="adData.imageUrl" :alt="adData.title" />
        <div class="ad-text">{{ adData.title }}</div>
      </a>
    </div>

    <!-- Google AdSense 广告 -->
    <div class="google-ad" v-if="adData.type === 'GOOGLE'">
      <ins class="adsbygoogle"
           style="display:block"
           data-ad-client="ca-pub-xxxxxxxxxxxxxxxx"
           data-ad-slot="xxxxxxxxxx"
           data-ad-format="auto"
           data-full-width-responsive="true"></ins>
    </div>

    <!-- 百度联盟广告 -->
    <div class="baidu-ad" v-if="adData.type === 'BAIDU' && adData.baiduCproId">
      <div :id="'baidu_ad_' + adData.baiduCproId"></div>
    </div>

    <!-- 腾讯优量汇广告 -->
    <div class="tencent-ad" v-if="adData.type === 'TENCENT' && adData.tencentAppId">
      <div :id="'tencent_ad_' + adData.tencentPlacementId"></div>
    </div>

    <!-- 字节跳动穿山甲广告 -->
    <div class="bytedance-ad" v-if="adData.type === 'BYTEDANCE' && adData.bytedanceAppId">
      <div :id="'bytedance_ad_' + adData.bytedanceSlotId"></div>
    </div>

    <!-- 阿里妈妈广告 -->
    <div class="alimama-ad" v-if="adData.type === 'ALIMAMA' && adData.alimamaPid">
      <div :id="'alimama_ad_' + adData.alimamaPid"></div>
    </div>

    <!-- 360广告 -->
    <div class="qihoo-ad" v-if="adData.type === 'QIHOO' && adData.qihooPosId">
      <div :id="'qihoo_ad_' + adData.qihooPosId"></div>
    </div>

    <!-- 搜狗广告 -->
    <div class="sogou-ad" v-if="adData.type === 'SOGOU' && adData.sogouAppId">
      <div :id="'sogou_ad_' + adData.sogouAppId"></div>
    </div>

    <!-- 京东联盟广告 -->
    <div class="jd-ad" v-if="adData.type === 'JD' && adData.jdUnionId">
      <div :id="'jd_ad_' + adData.jdUnionId"></div>
    </div>

    <!-- 拼多多推广广告 -->
    <div class="pdd-ad" v-if="adData.type === 'PDD' && adData.pddAppKey">
      <div :id="'pdd_ad_' + adData.pddAppKey"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'

const props = defineProps({
  // 是否显示广告
  showAd: {
    type: Boolean,
    default: true
  }
})

const adData = ref(null)

// 获取广告数据
const loadAdData = async () => {
  try {
    const response = await fetch('/api/ad/current')
    if (response.ok) {
      const result = await response.json()
      if (result.code === 200 && result.data) {
        adData.value = result.data
        // 根据广告类型加载相应的脚本
        loadAdScript(result.data)
      }
    }
  } catch (error) {
    console.error('加载广告数据失败:', error)
  }
}

// 记录广告点击
const recordClick = async () => {
  if (adData.value && adData.value.id) {
    try {
      await fetch(`/api/ad/click/${adData.value.id}`, {
        method: 'POST'
      })
      console.log('广告点击已记录')
    } catch (error) {
      console.error('记录广告点击失败:', error)
    }
  }
}

// 根据广告类型加载相应的脚本
const loadAdScript = (ad) => {
  if (!ad) return

  switch (ad.type) {
    case 'GOOGLE':
      loadGoogleAdSense()
      break
    case 'BAIDU':
      loadBaiduAd(ad)
      break
    case 'TENCENT':
      loadTencentAd(ad)
      break
    case 'BYTEDANCE':
      loadBytedanceAd(ad)
      break
    case 'ALIMAMA':
      loadAlimamaAd(ad)
      break
    case 'QIHOO':
      loadQihooAd(ad)
      break
    case 'SOGOU':
      loadSogouAd(ad)
      break
    case 'JD':
      loadJdAd(ad)
      break
    case 'PDD':
      loadPddAd(ad)
      break
  }
}

// 加载Google AdSense
const loadGoogleAdSense = () => {
  if (!document.querySelector('script[src*="adsbygoogle"]')) {
    const script = document.createElement('script')
    script.async = true
    script.src = 'https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js'
    script.setAttribute('data-ad-client', 'ca-pub-xxxxxxxxxxxxxxxx')
    document.head.appendChild(script)
    
    setTimeout(() => {
      if (window.adsbygoogle) {
        window.adsbygoogle.push({})
      }
    }, 100)
  }
}

// 加载百度联盟广告
const loadBaiduAd = (ad) => {
  const elementId = 'baidu_ad_' + ad.baiduCproId
  
  setTimeout(() => {
    const container = document.getElementById(elementId)
    if (container && !container.innerHTML) {
      // 创建百度联盟广告代码
      const adScript = document.createElement('script')
      adScript.innerHTML = `
        document.write('<a target="_blank" href="//www.baidu.com/s?wd=百度联盟"><img width="${ad.width || 300}" height="${ad.height || 100}" src="//dss1.bdstatic.com/6OF1bjeh1BF3odCf/it/u=${ad.baiduCproId}&fm=26&gp=0.jpg" /></a>');
      `
      container.appendChild(adScript)
    }
  }, 100)
}

// 加载腾讯优量汇广告
const loadTencentAd = (ad) => {
  const elementId = 'tencent_ad_' + ad.tencentPlacementId
  
  setTimeout(() => {
    const container = document.getElementById(elementId)
    if (container && !container.innerHTML) {
      container.innerHTML = `
        <div style="width: ${ad.width || 300}px; height: ${ad.height || 100}px; background: linear-gradient(45deg, #00d4ff, #004bff); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; cursor: pointer;" onclick="window.open('https://e.qq.com/', '_blank')">
          腾讯优量汇广告位
        </div>
      `
    }
  }, 100)
}

// 加载字节跳动穿山甲广告
const loadBytedanceAd = (ad) => {
  const elementId = 'bytedance_ad_' + ad.bytedanceSlotId
  
  setTimeout(() => {
    const container = document.getElementById(elementId)
    if (container && !container.innerHTML) {
      // 模拟穿山甲广告展示
      container.innerHTML = `
        <div style="width: ${ad.width || 300}px; height: ${ad.height || 100}px; background: linear-gradient(45deg, #ff4757, #ff6348); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; cursor: pointer;" onclick="window.open('https://www.pangle.cn/', '_blank')">
          穿山甲广告位
        </div>
      `
    }
  }, 100)
}

// 加载阿里妈妈广告
const loadAlimamaAd = (ad) => {
  const elementId = 'alimama_ad_' + ad.alimamaPid
  
  setTimeout(() => {
    const container = document.getElementById(elementId)
    if (container && !container.innerHTML) {
      // 模拟阿里妈妈广告展示
      container.innerHTML = `
        <div style="width: ${ad.width || 300}px; height: ${ad.height || 100}px; background: linear-gradient(45deg, #ff9800, #ff5722); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; cursor: pointer;" onclick="window.open('https://www.alimama.com/', '_blank')">
          阿里妈妈广告位
        </div>
      `
    }
  }, 100)
}

// 加载360广告
const loadQihooAd = (ad) => {
  const elementId = 'qihoo_ad_' + ad.qihooPosId
  
  setTimeout(() => {
    const container = document.getElementById(elementId)
    if (container && !container.innerHTML) {
      // 模拟360广告展示
      container.innerHTML = `
        <div style="width: ${ad.width || 300}px; height: ${ad.height || 100}px; background: linear-gradient(45deg, #4caf50, #2196f3); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; cursor: pointer;" onclick="window.open('https://union.360.cn/', '_blank')">
          360广告位
        </div>
      `
    }
  }, 100)
}

// 加载搜狗广告
const loadSogouAd = (ad) => {
  const elementId = 'sogou_ad_' + ad.sogouAppId
  
  setTimeout(() => {
    const container = document.getElementById(elementId)
    if (container && !container.innerHTML) {
      // 模拟搜狗广告展示
      container.innerHTML = `
        <div style="width: ${ad.width || 300}px; height: ${ad.height || 100}px; background: linear-gradient(45deg, #9c27b0, #e91e63); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; cursor: pointer;" onclick="window.open('https://fuwu.sogou.com/', '_blank')">
          搜狗广告位
        </div>
      `
    }
  }, 100)
}

// 加载京东联盟广告
const loadJdAd = (ad) => {
  const elementId = 'jd_ad_' + ad.jdUnionId
  
  setTimeout(() => {
    const container = document.getElementById(elementId)
    if (container && !container.innerHTML) {
      // 模拟京东联盟广告展示
      container.innerHTML = `
        <div style="width: ${ad.width || 300}px; height: ${ad.height || 100}px; background: linear-gradient(45deg, #e53935, #d32f2f); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; cursor: pointer;" onclick="window.open('https://union.jd.com/', '_blank')">
          京东联盟广告位
        </div>
      `
    }
  }, 100)
}

// 加载拼多多推广广告
const loadPddAd = (ad) => {
  const elementId = 'pdd_ad_' + ad.pddAppKey
  
  setTimeout(() => {
    const container = document.getElementById(elementId)
    if (container && !container.innerHTML) {
      // 模拟拼多多推广广告展示
      container.innerHTML = `
        <div style="width: ${ad.width || 300}px; height: ${ad.height || 100}px; background: linear-gradient(45deg, #ff5722, #ff9800); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; cursor: pointer;" onclick="window.open('https://jinbao.pinduoduo.com/', '_blank')">
          拼多多推广位
        </div>
      `
    }
  }, 100)
}

onMounted(() => {
  loadAdData()
})

// 监听showAd属性变化
watch(() => props.showAd, (newVal) => {
  if (newVal && !adData.value) {
    loadAdData()
  }
})
</script>

<style scoped>
.ad-container {
  margin: 20px 0;
  text-align: center;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  overflow: hidden;
}

.ad-label {
  font-size: 12px;
  color: #6c757d;
  padding: 4px 8px;
  background: #e9ecef;
  text-align: center;
}

.google-ad,
.baidu-ad,
.tencent-ad,
.bytedance-ad,
.alimama-ad,
.qihoo-ad,
.sogou-ad,
.jd-ad,
.pdd-ad {
  padding: 10px;
  text-align: center;
}

.banner-ad a {
  display: block;
  text-decoration: none;
  color: inherit;
  padding: 15px;
  transition: background-color 0.2s;
}

.banner-ad a:hover {
  background-color: #f0f0f0;
}

.banner-ad img {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin-bottom: 10px;
}

.ad-text {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ad-container {
    margin: 15px 0;
  }
  
  .banner-ad a {
    padding: 10px;
  }
}
</style> 