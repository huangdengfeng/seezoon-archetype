<template>
  <t-card title="个人信息">
    <template #actions>
      <t-button theme="primary" @click="showDialog = true">修改密码</t-button>
    </template>
    <div class="user-info-container">
      <div class="avatar-section">
        <t-avatar 
          v-if="myInfo?.photo" 
          :image="myInfo.photo"
          size="120px"
          class="user-avatar"
        />
        <t-avatar 
          v-else 
          size="120px"
          class="user-avatar"
        >
          {{ myInfo?.name?.charAt(0) || 'U' }}
        </t-avatar>
        <div class="user-name">{{ myInfo?.name || '未设置' }}</div>
        <div class="user-username">{{ myInfo?.username || '' }}</div>
      </div>
      <div class="info-section">
        <t-loading :loading="loading" text="加载中...">
          <t-descriptions :column="2" :border="false" v-if="myInfo">
            <t-descriptions-item label="用户名">
              <span class="info-value">{{ myInfo.username || '暂无' }}</span>
            </t-descriptions-item>
            <t-descriptions-item label="姓名">
              <span class="info-value">{{ myInfo.name || '暂无' }}</span>
            </t-descriptions-item>
            <t-descriptions-item label="手机号">
              <span class="info-value">{{ myInfo.mobile || '暂无' }}</span>
            </t-descriptions-item>
            <t-descriptions-item label="邮箱">
              <span class="info-value">{{ myInfo.email || '暂无' }}</span>
            </t-descriptions-item>
            <t-descriptions-item label="角色" :span="2">
              <t-space :size="8" break-line>
                <t-tag
                  v-for="(roleName, index) in myInfo.roleNames"
                  :key="index"
                  theme="primary"
                  variant="light"
                >
                  {{ roleName }}
                </t-tag>
                <span v-if="!myInfo.roleNames || myInfo.roleNames.length === 0" class="text-placeholder">无角色</span>
              </t-space>
            </t-descriptions-item>
          </t-descriptions>
        </t-loading>
      </div>
    </div>

    <!-- 修改密码对话框 -->
    <t-dialog
        :confirm-btn="{ content: '确定', loading: submitting }"
        :visible="showDialog"
        header="修改密码"
        width="600px"
        @close="closeDialog"
        @confirm="handleModifyPassword"
    >
      <template #body>
        <t-form ref="passwordFormRef" :data="passwordForm" :rules="passwordRules"
                style="height: 180px;">
          <t-form-item label="原密码" name="oldPassword">
            <t-input v-model="passwordForm.oldPassword" clearable type="password"/>
          </t-form-item>
          <t-form-item label="新密码" name="newPassword">
            <t-input v-model="passwordForm.newPassword" clearable type="password"/>
          </t-form-item>
          <t-form-item label="确认密码" name="confirmPassword">
            <t-input v-model="passwordForm.confirmPassword" clearable type="password"/>
          </t-form-item>
        </t-form>
      </template>
    </t-dialog>
  </t-card>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {MessagePlugin} from 'tdesign-vue-next'
import request from '@/utils/request'

const showDialog = ref(false)
const submitting = ref(false)
const passwordFormRef = ref(null)
const loading = ref(false)
const myInfo = ref(null)

// 加载我的信息
onMounted(async () => {
  loading.value = true
  try {
    const data = await request.get('/sys/user/my_info')
    myInfo.value = data
  } catch (error) {
    MessagePlugin.error(error?.message || '获取用户信息失败')
  } finally {
    loading.value = false
  }
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [{required: true, message: '请输入原密码'}],
  newPassword: [
    {required: true, message: '请输入新密码'},
    {min: 6, message: '密码长度不能小于6位'}
  ],
  confirmPassword: [
    {required: true, message: '请确认新密码'},
    {
      validator: (val) => val === passwordForm.value.newPassword,
      message: '两次输入的密码不一致'
    }
  ]
}

const closeDialog = () => {
  showDialog.value = false
  passwordFormRef.value.reset();
  passwordFormRef.value.clearValidate()
}

const handleModifyPassword = async () => {
  const validateResult = await passwordFormRef.value.validate()
  if (validateResult === true) {
    submitting.value = true
    try {
      await request.post('/sys/user/change_my_password', {
        oldPassword: passwordForm.value.oldPassword,
        newPassword: passwordForm.value.newPassword
      })
      MessagePlugin.success('密码修改成功')
      closeDialog()
    } finally {
      submitting.value = false
    }
  }
}
</script>

<style scoped>
.user-info-container {
  display: flex;
  gap: 40px;
  padding: 20px 0;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 200px;
}

.user-avatar {
  margin-bottom: 16px;
  border: 2px solid var(--td-component-border);
}

.user-name {
  font-size: 18px;
  font-weight: 500;
  color: var(--td-text-color-primary);
  margin-bottom: 8px;
}

.user-username {
  font-size: 14px;
  color: var(--td-text-color-secondary);
}

.info-section {
  flex: 1;
}

.info-section :deep(.t-descriptions__label) {
  width: 80px;
  min-width: 80px;
  padding-right: 8px;
}

.info-section :deep(.t-descriptions__content) {
  padding-left: 8px;
}

.info-value {
  color: var(--td-text-color-primary);
  font-size: 14px;
}

.text-placeholder {
  color: var(--td-text-color-placeholder);
}

@media (max-width: 768px) {
  .user-info-container {
    flex-direction: column;
    gap: 24px;
  }
  
  .avatar-section {
    width: 100%;
  }
}
</style>