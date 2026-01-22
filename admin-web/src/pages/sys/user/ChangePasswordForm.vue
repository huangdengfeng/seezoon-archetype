<template>
  <t-dialog v-model:visible="visible" :close-on-overlay-click="false" :destroy-on-close="true"
            :footer="false"
            header="修改密码" scroll-to-first-error="smooth" width="750">
    <template #body>
      <t-form :data="formData" :rules="formRules" @submit="save">
        <t-row :gutter="[0, 20]">
          <t-col :span="24">
            <t-form-item label="用户">
              <span>{{ formData.name }}({{ formData.userName }})</span>
            </t-form-item>
          </t-col>
          <t-col :span="24">
            <t-form-item label="新密码" name="password">
              <t-input v-model="formData.password" :maxcharacter="20" placeholder="请输入新密码"
                       type="password"></t-input>
            </t-form-item>
          </t-col>
          <t-col :span="24">
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
import {LoadingPlugin, MessagePlugin} from 'tdesign-vue-next'
import request from '@/utils/request'

const formRules = {
  password: [
    {required: true},
    {whitespace: true},
    {min: 6, message: '至少6个字符'}
  ]
}

const visible = ref(false)
const formData = reactive({
  uid: null,
  name: '',
  userName: '',
  password: ''
})
const emit = defineEmits(['refresh'])

const open = (uid, name, userName) => {
  visible.value = true
  Object.assign(formData, {
    uid,
    name,
    userName,
    password: ''
  })
}

const save = async ({validateResult}) => {
  if (validateResult !== true) {
    return
  }
  LoadingPlugin(true)
  try {
    await request.post('/sys/user/change_password', {
      uid: formData.uid,
      newPassword: formData.password
    })
    emit('refresh')
    MessagePlugin.success('修改成功')
    close()
  } finally {
    LoadingPlugin(false)
  }
}

const close = () => {
  visible.value = false
}

defineExpose({
  open
})
</script>

