<template>
  <ErrorHandler ref="errorHandler" />
  
  <router-view v-slot="{ Component, route }">
    <component 
      :is="route.meta.layout || 'DefaultLayout'" 
      v-if="shouldUseLayout(route)"
    >
      <component :is="Component" />
    </component>
    <component :is="Component" v-else />
  </router-view>
</template>

<script setup>
import { ref, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useUserStore } from './store/user'
import DefaultLayout from './layouts/DefaultLayout.vue'

// 注册布局组件
const layouts = {
  DefaultLayout: markRaw(DefaultLayout)
}

const router = useRouter()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

// 判断是否应该使用布局
const shouldUseLayout = (route) => {
  return !route.meta.noLayout && route.path !== '/' && route.path !== '/login' && route.path !== '/register'
}
</script>

<style>
/* 页面过渡效果 */
.page-enter-active,
.page-leave-active {
  transition: opacity 0.3s;
}

.page-enter-from,
.page-leave-to {
  opacity: 0;
}
</style>
