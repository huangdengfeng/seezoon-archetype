<template>
  <Layout/>
</template>

<script setup>
import {onMounted} from 'vue'
import {useUserStore} from '@/store/user'
import {getToken} from '@/utils/auth'
import Layout from "@/components/layout/Layout.vue";

const userStore = useUserStore()

// 避免刷新页面后未加载用户信息
const initUserInfo = async () => {
  const token = getToken()
  if (token) {
    await userStore.getUserInfo()
  }
}
onMounted(initUserInfo)

</script>
