<template>
  <div class="admin-layout">
    <el-container style="height: 100vh">
      <el-aside width="200px">
        <el-menu :default-active="activeMenu" router>
          <el-menu-item index="/admin/votes">投票管理</el-menu-item>
          <el-menu-item index="/admin/announcements">公告管理</el-menu-item>
          <el-menu-item index="/admin/owners">业主管理</el-menu-item>
          <el-menu-item index="/admin/suggestions">建议管理</el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header style="background:#f5f5f5; text-align:right; padding:0 20px;">
          <span>管理员后台</span>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}
</style> 