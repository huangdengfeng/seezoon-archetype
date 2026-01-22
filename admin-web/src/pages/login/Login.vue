<template>
    <div class="login-container">
        <div class="login-wrapper">
            <div class="login-box">
            <div class="login-header">
                    <img class="logo" src="/logo.svg" alt="logo" />
                    <h1 class="title">后台管理系统</h1>
            </div>
                <t-form ref="form" :data="formData" :rules="rules" :colon="false" :label-width="0" @submit="onSubmit" class="login-form">
                    <t-form-item name="username">
                        <t-input 
                            v-model="formData.username" 
                            size="large"
                            clearable 
                            placeholder="请输入账号"
                            :autocomplete="'username'"
                        >
                            <template #prefix-icon>
                                <desktop-icon />
                            </template>
                        </t-input>
                    </t-form-item>

                    <t-form-item name="password">
                        <t-input 
                            v-model="formData.password" 
                            type="password" 
                            size="large"
                            clearable 
                            placeholder="请输入密码"
                            :autocomplete="'current-password'"
                        >
                            <template #prefix-icon>
                                <lock-on-icon />
                            </template>
                        </t-input>
                    </t-form-item>

                    <t-form-item>
                        <t-button theme="primary" type="submit" size="large" block :loading="loading">登录</t-button>
                    </t-form-item>
                </t-form>
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { DesktopIcon, LockOnIcon } from 'tdesign-icons-vue-next'
import { MessagePlugin } from 'tdesign-vue-next'
import request from '@/utils/request'
import { setToken } from '@/utils/auth'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const router = useRouter()
const loading = ref(false)

// 禁用页面滚动并重置 margin/padding
onMounted(() => {
    document.body.style.overflow = 'hidden'
    document.body.style.margin = '0'
    document.body.style.padding = '0'
    document.documentElement.style.margin = '0'
    document.documentElement.style.padding = '0'
})

onUnmounted(() => {
    document.body.style.overflow = ''
    document.body.style.margin = ''
    document.body.style.padding = ''
    document.documentElement.style.margin = ''
    document.documentElement.style.padding = ''
})

const formData = reactive({
    username: 'admin',
    password: '123456'
})

const rules = {
    username: [
        { required: true, message: '请输入账号' },
        { whitespace: true, message: '账号不能为空白字符' }
    ],
    password: [
        { required: true, message: '请输入密码' },
        { whitespace: true, message: '密码不能为空白字符' },
        { min: 6, message: '密码不能少于6位' }
    ]
}

const onSubmit = async ({ validateResult }) => {
    if (validateResult === true) {
        loading.value = true
        try {
        const { accessToken } = await request.post('/login/user_passwd', {
            username: formData.username,
            password: formData.password
        });
        setToken(accessToken);
        await userStore.getUserInfo()
        MessagePlugin.success('登录成功');
        router.push('/');
        } finally {
            loading.value = false
        }
    }
}
</script>

<style scoped>
.login-container {
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f0f2f5;
    position: relative;
    overflow: hidden;
    margin: 0;
    padding: 0;
}

.login-wrapper {
    position: relative;
    z-index: 1;
    width: 100%;
    max-width: 400px;
    padding: 0 20px;
}

.login-box {
    background: #ffffff;
    border-radius: 12px;
    padding: 48px 40px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

.login-header {
    text-align: center;
    margin-bottom: 40px;
}

.logo {
    height: 64px;
    width: auto;
    margin-bottom: 16px;
    display: block;
    margin-left: auto;
    margin-right: auto;
}

.title {
    font-size: 28px;
    font-weight: 600;
    color: #1a1a1a;
    margin: 0;
    letter-spacing: -0.5px;
}

.login-form {
    margin-top: 0;
}

.login-form :deep(.t-form-item) {
    margin-bottom: 24px;
}

.login-form :deep(.t-form-item:last-child) {
    margin-bottom: 0;
    margin-top: 8px;
}

.login-form :deep(.t-input) {
    border-radius: 6px;
}

.login-form :deep(.t-button) {
    border-radius: 6px;
    font-size: 16px;
    font-weight: 500;
    height: 48px;
    margin-top: 8px;
}

@media (max-width: 480px) {
    .login-box {
        padding: 32px 24px;
    }
    
    .title {
        font-size: 24px;
    }
    
    .logo {
        height: 48px;
    }
}
</style>