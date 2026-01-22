import { useUserStore } from '@/store/user'

export const checkPermission = (permissions) => {
  const userStore = useUserStore()
  return Array.isArray(permissions) 
    ? permissions.some(permission => userStore.userInfo?.permissions?.includes(permission))
    : userStore.userInfo?.permissions?.includes(permissions)
}

export const auth = {
  mounted(el, binding) {
    const hasPermission = checkPermission(binding.value)
    if (!hasPermission) {
      el.parentNode?.removeChild(el)
    }
  }
}

