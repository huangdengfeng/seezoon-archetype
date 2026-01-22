import {createApp} from 'vue'
import {createPinia} from 'pinia'
import App from './App.vue'
import router from './router'
import TDesign from 'tdesign-vue-next'
import 'tdesign-vue-next/es/style/index.css'
// 自定义按钮权限指令
import {auth} from '@/directives/auth'
// Store持久化存储
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import './style/index.css'

const app = createApp(App)
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)
app.use(router)
app.use(TDesign)
app.mount('#app')
app.directive('auth', auth)
