<template>
  <t-dialog
    v-model:visible="visible"
    :close-on-overlay-click="false"
    :destroy-on-close="true"
    :footer="false"
    header="用户详细信息"
    width="800px"
  >
    <template #body>
      <t-loading :loading="loading" text="加载中...">
        <t-form :data="userDetail" label-width="120px" v-if="userDetail">
          <t-row :gutter="[0, 20]">
            <t-col :span="12">
              <t-form-item label="用户ID">
                <span>{{ userDetail.uid }}</span>
              </t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item label="用户名">
                <span>{{ userDetail.username }}</span>
              </t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item label="姓名">
                <span>{{ userDetail.name }}</span>
              </t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item label="手机号">
                <span>{{ userDetail.mobile || '暂无' }}</span>
              </t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item label="邮箱">
                <span>{{ userDetail.email || '暂无' }}</span>
              </t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item label="状态">
                <t-tag v-if="userDetail.status === 1" theme="success">正常</t-tag>
                <t-tag v-else-if="userDetail.status === 2" theme="danger">停用</t-tag>
                <t-tag v-else-if="userDetail.status === 3" theme="danger">锁定</t-tag>
              </t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item label="头像">
                <t-avatar v-if="userDetail.photo" :image="userDetail.photo"/>
                <t-avatar v-else>{{ userDetail.name?.charAt(0) }}</t-avatar>
              </t-form-item>
            </t-col>
            <t-col :span="24">
              <t-form-item label="角色">
                <t-space :size="8" break-line>
                  <t-tag
                    v-for="(roleName, index) in roleNameList"
                    :key="index"
                    theme="primary"
                    variant="light"
                  >
                    {{ roleName }}
                  </t-tag>
                  <span v-if="!roleNameList || roleNameList.length === 0" class="text-placeholder">无角色</span>
                </t-space>
              </t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item label="创建时间">
                <span>{{ userDetail.createTime || '暂无' }}</span>
              </t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item label="更新时间">
                <span>{{ userDetail.updateTime || '暂无' }}</span>
              </t-form-item>
            </t-col>
            <t-col :span="24" v-if="userDetail.remark">
              <t-form-item label="备注">
                <span>{{ userDetail.remark }}</span>
              </t-form-item>
            </t-col>
          </t-row>
        </t-form>
      </t-loading>
    </template>
    <template #footer>
      <t-space>
        <t-button theme="primary" @click="close">关闭</t-button>
      </t-space>
    </template>
  </t-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import request from '@/utils/request'

const visible = ref(false)
const loading = ref(false)
const userDetail = ref(null)

const roleNameList = computed(() => {
  return userDetail.value?.roleNames || []
})

const open = async (uid) => {
  visible.value = true
  loading.value = true
  try {
    const data = await request.post('/sys/user/detail', { uid })
    userDetail.value = data
  }  finally {
    loading.value = false
  }
}

const close = () => {
  visible.value = false
  userDetail.value = null
}

defineExpose({
  open
})
</script>

<style scoped>
.text-placeholder {
  color: var(--td-text-color-placeholder);
}
</style>

