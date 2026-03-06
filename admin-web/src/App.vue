<template>
  <router-view/>
</template>

<script setup>
import {onMounted} from 'vue'
import {useUserStore} from '@/store/user'
import {getToken} from '@/utils/auth'

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
