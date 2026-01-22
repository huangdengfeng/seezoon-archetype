import axios from 'axios'
import {MessagePlugin} from 'tdesign-vue-next'
import {getToken, removeToken} from '@/utils/auth'
import router from '@/router'

const {VITE_API_BASE_URL, VITE_API_PREFIX} = import.meta.env

const request = axios.create({
  baseURL: VITE_API_BASE_URL + VITE_API_PREFIX,
  timeout: 6000
})

// 请求拦截器
request.interceptors.request.use(
    (config) => {
      const token = getToken()
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    (error) => {
      return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response) => {
      // const data = response.data
      // if (data.code !== 0) {
      //   MessagePlugin.error(data.msg)
      //   return Promise.reject(new Error(data.msg))
      // }
      return response.data;
    },
    (error) => {
      console.log(error)
      if (error.response?.status === 401) {
        removeToken()
        router.push('/login')
      } else if (error.response?.status === 403) {
        MessagePlugin.error('对不起，您没有访问权限')
      } else if (error.response?.status === 500) {
        MessagePlugin.error('服务器错误，请稍后重试')
      } else {
        if (error.response?.data) {
          MessagePlugin.error(error.response.data.title)
        } else {
          MessagePlugin.error(error.message || '请求失败')
        }
      }
      return Promise.reject(error)
    }
)

export default request