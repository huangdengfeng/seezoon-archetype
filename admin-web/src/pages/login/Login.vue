<template>
    <div class="login-container" ref="containerRef" @mousemove="onMouseMove">
        <div class="bg-scan"></div>
        <div class="bg-spotlight"></div>
        <div class="login-wrapper">
            <div class="login-box">
            <div class="login-header">
                    <img class="logo" src="/logo.svg" alt="logo" />
                    <h1 class="title">Seezoon 智能管理平台</h1>
                    <p class="subtitle">Enterprise Management Platform</p>
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
            <div class="login-copyright">
                Copyright © 2024-{{ currentYear }} Seezoon. All rights reserved.
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
const currentYear = new Date().getFullYear()

const containerRef = ref(null)
let rafId = null
let pendingX = 0
let pendingY = 0

const onMouseMove = (e) => {
    pendingX = e.clientX
    pendingY = e.clientY
    if (rafId) return
    rafId = requestAnimationFrame(() => {
        const el = containerRef.value
        if (el) {
            const rect = el.getBoundingClientRect()
            el.style.setProperty('--mx', (pendingX - rect.left) + 'px')
            el.style.setProperty('--my', (pendingY - rect.top) + 'px')
        }
        rafId = null
    })
}

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

    if (rafId) {
        cancelAnimationFrame(rafId)
        rafId = null
    }
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
    background-color: #f5f7fb;
    background-image:
        linear-gradient(rgba(15, 47, 110, 0.045) 1px, transparent 1px),
        linear-gradient(90deg, rgba(15, 47, 110, 0.045) 1px, transparent 1px),
        linear-gradient(135deg, #f5f7fb 0%, #eaf1fb 60%, #e6efff 100%);
    background-size: 44px 44px, 44px 44px, 100% 100%;
    background-position: 0 0, 0 0, 0 0;
    position: relative;
    overflow: hidden;
    margin: 0;
    padding: 20px;
}

.login-container::before,
.login-container::after {
    content: '';
    position: absolute;
    border-radius: 50%;
    filter: blur(90px);
    opacity: 0.5;
    z-index: 0;
    pointer-events: none;
    will-change: transform;
}

.login-container::before {
    width: 460px;
    height: 460px;
    background: radial-gradient(circle, #b9d4ff 0%, rgba(185, 212, 255, 0) 70%);
    top: -140px;
    right: -120px;
    animation: blob-float-a 22s ease-in-out infinite;
}

.login-container::after {
    width: 380px;
    height: 380px;
    background: radial-gradient(circle, #c8eedd 0%, rgba(200, 238, 221, 0) 70%);
    bottom: -120px;
    left: -100px;
    animation: blob-float-b 28s ease-in-out infinite;
}

@keyframes blob-float-a {
    0%, 100% {
        transform: translate(0, 0) scale(1);
    }
    50% {
        transform: translate(-40px, 50px) scale(1.08);
    }
}

@keyframes blob-float-b {
    0%, 100% {
        transform: translate(0, 0) scale(1);
    }
    50% {
        transform: translate(50px, -40px) scale(0.94);
    }
}

.bg-scan {
    position: absolute;
    inset: 0;
    z-index: 0;
    pointer-events: none;
    background: linear-gradient(
        115deg,
        transparent 35%,
        rgba(120, 170, 240, 0.10) 47%,
        rgba(180, 210, 255, 0.32) 50%,
        rgba(120, 170, 240, 0.10) 53%,
        transparent 65%
    );
    background-size: 250% 250%;
    background-position: 0% 0%;
    animation: scan-sweep 9s ease-in-out infinite;
    mix-blend-mode: screen;
}

@keyframes scan-sweep {
    0% {
        background-position: 0% 0%;
    }
    55% {
        background-position: 100% 100%;
    }
    100% {
        background-position: 100% 100%;
    }
}

.bg-spotlight {
    position: absolute;
    inset: 0;
    z-index: 0;
    pointer-events: none;
    background: radial-gradient(
        600px circle at var(--mx, 50%) var(--my, 50%),
        rgba(0, 82, 217, 0.12),
        rgba(0, 82, 217, 0.05) 35%,
        transparent 65%
    );
}

@media (hover: none) {
    .bg-spotlight {
        display: none;
    }
}

@media (prefers-reduced-motion: reduce) {
    .login-container::before,
    .login-container::after,
    .bg-scan {
        animation: none;
    }
    .bg-scan {
        opacity: 0;
    }
}

.login-wrapper {
    position: relative;
    z-index: 1;
    width: 100%;
    max-width: 420px;
}

.login-box {
    background: rgba(255, 255, 255, 0.92);
    backdrop-filter: saturate(180%) blur(16px);
    -webkit-backdrop-filter: saturate(180%) blur(16px);
    border: 1px solid rgba(255, 255, 255, 0.7);
    border-radius: 16px;
    padding: 48px 40px 40px;
    box-shadow:
        0 1px 0 rgba(255, 255, 255, 0.8) inset,
        0 20px 50px -12px rgba(15, 47, 110, 0.18),
        0 8px 20px -8px rgba(15, 47, 110, 0.10);
}

.login-header {
    text-align: center;
    margin-bottom: 36px;
}

.logo {
    height: 56px;
    width: auto;
    margin: 0 auto 16px;
    display: block;
}

.title {
    font-size: 28px;
    font-weight: 700;
    margin: 0;
    letter-spacing: 1px;
    line-height: 1.3;
    background: linear-gradient(120deg, #0a2a66 0%, #0052d9 50%, #4a90e2 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    color: transparent;
}

.subtitle {
    margin: 8px 0 0;
    font-size: 12px;
    font-weight: 500;
    color: #8a94a6;
    letter-spacing: 3px;
    text-transform: uppercase;
}

.login-form :deep(.t-form-item) {
    margin-bottom: 20px;
}

.login-form :deep(.t-form-item:last-child) {
    margin-bottom: 0;
    margin-top: 12px;
}

.login-form :deep(.t-input) {
    border-radius: 8px;
    height: 44px;
    background-color: #f7f9fc;
    border-color: transparent;
    transition: background-color 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}

.login-form :deep(.t-input:hover) {
    background-color: #f0f4fa;
    border-color: transparent;
}

.login-form :deep(.t-is-focused.t-input),
.login-form :deep(.t-input--focused) {
    background-color: #ffffff;
    border-color: var(--td-brand-color, #0052d9);
    box-shadow: 0 0 0 3px rgba(0, 82, 217, 0.12);
}

.login-form :deep(.t-input__prefix .t-icon) {
    color: #8a94a6;
    font-size: 18px;
}

.login-form :deep(.t-button) {
    border-radius: 8px;
    font-size: 16px;
    font-weight: 500;
    height: 46px;
    margin-top: 4px;
    transition: transform 0.15s ease, box-shadow 0.2s ease;
}

.login-form :deep(.t-button:hover:not(.t-is-disabled):not(.t-is-loading)) {
    transform: translateY(-1px);
    box-shadow: 0 10px 20px -8px rgba(0, 82, 217, 0.45);
}

.login-copyright {
    margin-top: 20px;
    text-align: center;
    font-size: 12px;
    color: #8a94a6;
    letter-spacing: 0.3px;
    user-select: none;
}

@media (max-width: 480px) {
    .login-box {
        padding: 36px 24px 28px;
        border-radius: 14px;
    }

    .login-header {
        margin-bottom: 28px;
    }

    .title {
        font-size: 22px;
        letter-spacing: 0.5px;
    }

    .subtitle {
        font-size: 11px;
        letter-spacing: 2px;
    }

    .logo {
        height: 48px;
    }
}
</style>