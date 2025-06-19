
<script setup>
import Header from './components/Header.vue'
import { RouterView } from 'vue-router'
</script>

<template>
  <div class="app-container">
    <Header />           <!-- ✅ 全局 Header -->
    <main class="page-content">
      <RouterView />     <!-- ✅ 這裡會根據路由動態切換內容 -->
    </main>
  </div>
</template>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.page-content {
  flex: 1;
  padding: 20px;
  background-color: #f9f9f9;
}
</style>
