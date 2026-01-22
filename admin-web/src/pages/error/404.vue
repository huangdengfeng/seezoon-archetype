<template>
  <div class="error-page">
    <t-space direction="vertical" align="center">
      <t-icon name="error" size="48px" />
      <h1>404</h1>
      <h3>页面不存在</h3>
      <p>抱歉，您访问的页面不存在</p>
      <t-button theme="primary" @click="router.push('/')">返回首页</t-button>
    </t-space>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.error-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

h1 {
  font-size: 48px;
  margin: 16px 0;
  color: var(--td-error-color);
}

h3 {
  font-size: 20px;
  margin: 8px 0;
}

p {
  color: var(--td-text-color-secondary);
  margin: 8px 0 24px;
}
</style>