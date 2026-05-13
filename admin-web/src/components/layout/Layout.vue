<template>
  <t-layout style="min-height: 100vh;">
    <t-header style="border-bottom: 1px solid var(--td-component-border)">
      <t-head-menu height="120px">
        <template #logo>
          <a href="/">
            <img alt="logo" class="logo"
                 src="https://www.tencent.com/img/index/menu_logo_hover.png"
                 width="136"/>
          </a>
        </template>
        <template #operations>
          <t-space>
            <t-dropdown trigger="click">
              <t-space>
                <t-button variant="text">
                  <t-icon name="user" size="20" style="margin-right: 8px"/>
                  {{ userStore.userInfo?.username || '' }} {{ userStore.userInfo?.name || '' }}
                  <template #suffix>
                    <t-icon name="chevron-down" size="16"/>
                  </template>
                </t-button>
              </t-space>
              <t-dropdown-menu>
                <t-dropdown-item @click="router.push('/sys/user/center')">个人信息</t-dropdown-item>
                <t-dropdown-item @click="handleLogout">退出登录</t-dropdown-item>
              </t-dropdown-menu>
            </t-dropdown>
          </t-space>
        </template>
      </t-head-menu>
    </t-header>
    <t-layout style="flex: 1;">
      <t-aside
          style="border-right: 1px solid var(--td-component-border);border-top:1px solid var(--td-component-border) ">
        <t-menu theme="light" value="user-manage">
          <t-submenu v-auth="['sys']" title="系统管理" value="system">
            <template #icon>
              <t-icon name="setting"/>
            </template>
            <t-menu-item v-auth="['sys:user']" to="/sys/user">
              <template #icon>
                <t-icon name="user"/>
              </template>
              用户管理
            </t-menu-item>
            <t-menu-item v-auth="['sys:role']" to="/sys/role">
              <template #icon>
                <t-icon name="usergroup"/>
              </template>
              角色管理
            </t-menu-item>
          </t-submenu>
        </t-menu>
      </t-aside>
      <t-layout style="flex: 1;">
        <t-content style="padding: 10px; flex: 1;">
          <router-view/>
        </t-content>
        <t-footer
            style="text-align: center; padding: 14px 0; font-size: 12px; color: var(--td-text-color-placeholder); background-color: var(--td-bg-color-page); position: sticky; bottom: 0; z-index: 10;">
          Copyright © 2024-{{ new Date().getFullYear() }} Seezoon. All rights reserved.
        </t-footer>
      </t-layout>
    </t-layout>
  </t-layout>
</template>
<script setup>
import {useUserStore} from '@/store/user'
import {useRouter} from 'vue-router'
import {removeToken} from '@/utils/auth'
import request from '@/utils/request'

const userStore = useUserStore()
const router = useRouter()

const handleLogout = async () => {
  try {
    // 调用后台 logout 接口
    await request.post('/sys/user/logout')
  } finally {
    // 清除 token 和用户信息
    removeToken()
    userStore.clearUserInfo()
    router.push('/login')
  }
}
</script>