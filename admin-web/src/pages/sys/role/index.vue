<template>
  <t-card title="角色管理">
    <t-table 
      :columns="columns" 
      :data="tableData" 
      :loading="tableLoading"
      row-key="code"
    >
      <template #permissions="{ row }">
        <div v-if="row.permissions && row.permissions.length > 0">
          <t-space :size="8" break-line>
            <t-tag 
              v-for="permission in row.permissions" 
              :key="permission.code"
              theme="primary"
              variant="light"
              :title="`${permission.name} (${permission.code})`"
            >
              {{ permission.name }}
            </t-tag>
          </t-space>
          <div class="permission-count">
            共 {{ row.permissions.length }} 个权限
          </div>
        </div>
        <span v-else class="text-placeholder">无权限</span>
      </template>
    </t-table>
  </t-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import request from '@/utils/request'

// 表格列
const columns = [
  {
    colKey: 'code',
    title: '角色代码',
    width: 200,
  },
  {
    colKey: 'name',
    title: '角色名称',
    width: 200,
  },
  {
    colKey: 'permissions',
    title: '拥有权限',
    ellipsis: true,
  },
]

// 表格数据
const tableData = ref([])
const tableLoading = ref(false)

// 查询角色列表
const query = async () => {
  tableLoading.value = true
  try {
    const data = await request.post('/sys/role/list')
    tableData.value = data || []
  } finally {
    tableLoading.value = false
  }
}

onMounted(() => {
  query()
})
</script>

<style scoped>
.text-placeholder {
  color: var(--td-text-color-placeholder);
}

.permission-count {
  margin-top: 8px;
  font-size: 12px;
  color: var(--td-text-color-secondary);
}
</style>

