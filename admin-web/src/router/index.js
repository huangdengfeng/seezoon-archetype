import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/sys/user',
      children: [
        {
          path: '/sys/user',
          name: 'SysUser',
          component: () => import('@/pages/sys/user/index.vue')
        },
        {
          path: '/sys/user/center',
          name: 'SysUserCenter',
          component: () => import('@/pages/sys/user/Center.vue')
        },
        {
          path: '/sys/role',
          name: 'SysRole',
          component: () => import('@/pages/sys/role/index.vue')
        }
      ]
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/pages/login/Login.vue')
    },
    // 添加404路由
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/pages/error/404.vue')
    }
  ]
})

export default router