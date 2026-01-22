import {defineStore} from 'pinia'
import request from '@/utils/request'

const initUserInfo = {
  username: '',
  name: '',
  mobile: '',
  email: '',
  photo: '',
  roles: [],
  roleNames: [],
  permissions: [],
}
export const useUserStore = defineStore('user', {
  state: () => ({
    initied: false,
    userInfo: initUserInfo
  }),
  actions: {
    async getUserInfo() {
      const data = await request.get('/sys/user/my_info')
      this.userInfo = data
      this.initied = true
      return data
    },

    clearUserInfo() {
      this.userInfo = { ...initUserInfo }
      this.initied = false
    }
  },
  persist: {
    key: 'user-store',
    storage: sessionStorage,
  }
})