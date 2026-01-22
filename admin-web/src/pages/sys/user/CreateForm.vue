<template>
  <t-dialog v-model:visible="visible" :close-on-overlay-click="false" :destroy-on-close="true"
            :footer="false"
            header="添加用户" scroll-to-first-error="smooth" width="720">
    <template #body>
      <t-form ref="form" :data="data" :rules="formRules" @submit="save">
        <t-row :gutter="[0, 20]">
          <t-col :span="6">
            <t-form-item label="姓名" name="name">
              <t-input v-model="data.name" :maxcharacter="20" placeholder="请输入姓名"></t-input>
            </t-form-item>
          </t-col>
          <t-col :span="6"></t-col>
          <t-col :span="6">
            <t-form-item label="用户名" name="userName">
              <t-input v-model="data.userName" :maxcharacter="20"
                       placeholder="用于登录，不能重复"></t-input>
            </t-form-item>
          </t-col>
          <t-col :span="6">
            <t-form-item label="密码" name="password">
              <t-input v-model="data.password" :maxcharacter="20" placeholder="请输入密码"
                       type="password"></t-input>
            </t-form-item>
          </t-col>
          <t-col :span="6">
            <t-form-item label="手机号" name="mobile">
              <t-input v-model="data.mobile" :maxcharacter="20" placeholder=""></t-input>
            </t-form-item>
          </t-col>
          <t-col :span="6">
            <t-form-item label="邮箱" name="email">
              <t-input v-model="data.email" :maxcharacter="50" placeholder=""></t-input>
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="角色" name="roles">
              <t-checkbox-group v-model="data.roles" :options="roles"></t-checkbox-group>
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="data.remark" :maxcharacter="100"
                          placeholder="请输入备注"></t-textarea>
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item>
              <t-space>
                <t-button variant="outline" @click="close()">取消</t-button>
                <t-button theme="primary" type="submit">保存</t-button>
              </t-space>
            </t-form-item>
          </t-col>
        </t-row>
      </t-form>
    </template>
  </t-dialog>
</template>

<script setup>
import {reactive, ref} from 'vue'
import {MessagePlugin} from 'tdesign-vue-next'
import request from '@/utils/request'

const INIT_DATA = {
  userName: null,
  name: null,
  mobile: null,
  email: null,
  password: null,
  remark: null,
  roles: [],
}

const checkUserName = (userName) => {
  return new Promise((resolve) => {
    if (!userName) {
      resolve(true)
    } else {
      request.post('/sys/user/page', {userName}).then(({total}) => {
        if (total !== 0) {
          resolve({result: false, message: '用户名已存在'})
        } else {
          resolve(true)
        }
      })
    }
  })
}

const formRules = {
  name: [
    {required: true},
    {whitespace: true}
  ],
  userName: [
    {required: true},
    {whitespace: true},
    {min: 5, message: '至少5个字符'},
    {
      validator: checkUserName,
      trigger: 'blur',
    }
  ],
  password: [
    {required: true},
    {whitespace: true},
    {min: 6, message: '至少6个字符'}
  ],
  mobile: [
    {whitespace: true}
  ],
  email: [
    {whitespace: true},
    {email: true, message: '请输入正确邮箱地址'}
  ]
}

const visible = ref(false)
const data = reactive({...INIT_DATA})
const roles = ref([])
const form = ref(null)
const emit = defineEmits(['refresh'])

const open = () => {
  visible.value = true
  Object.assign(data, INIT_DATA)
}

const close = () => {
  visible.value = false
}

defineExpose({
  open
})

const save = async ({validateResult}) => {
  if (validateResult !== true) {
    return
  }
  await request.post('/sys/user/create', data)
  emit('refresh')
  MessagePlugin.success('添加成功')
  close()
}

// 获取角色列表
request.post('/sys/role/list').then((data) => {
  roles.value = data.map((item) => ({
    label: item.name,
    value: item.code,  // 使用角色代码而不是ID
  }))
})
</script>

