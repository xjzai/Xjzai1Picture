<template>
  <div id="userManagePage">
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="Account">
        <a-input v-model:value="searchParams.userAccount" placeholder="Enter account" />
      </a-form-item>
      <a-form-item label="Username">
        <a-input v-model:value="searchParams.userName" placeholder="Enter username" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">Search</a-button>
      </a-form-item>
    </a-form>
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">Admin</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">User</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button danger @click="doDelete(record.id)">Delete</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script setup lang="ts">
import { SmileOutlined, DownOutlined } from '@ant-design/icons-vue'
import { deleteUserUsingPost, listUserVoByPageUsingPost } from '@/api/userController'
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

const columns = [
  {
    title: 'User ID',
    dataIndex: 'id',
  },
  {
    title: 'Account',
    dataIndex: 'userAccount',
  },
  {
    title: 'Username',
    dataIndex: 'userName',
  },
  {
    title: 'Avatar',
    dataIndex: 'userAvatar',
  },
  {
    title: 'Profile',
    dataIndex: 'userProfile',
  },
  {
    title: 'Role',
    dataIndex: 'userRole',
  },
  {
    title: 'Create Time',
    dataIndex: 'createTime',
  },
  {
    title: 'Actions',
    key: 'action',
  },
]

// 数据
const dataList = ref([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  const res = await listUserVoByPageUsingPost({
    ...searchParams,
  })
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('Failed to fetch data, ' + res.data.message + ', ' + res.data.description)
  }
  // console.log(searchParams);
}

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total) => `Total ${total} items`,
  }
})

// 表格变化处理
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}
// 获取数据
const doSearch = () => {
  // 重置页码
  searchParams.current = 1
  fetchData()
}

// 删除数据
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteUserUsingPost({ id })
  if (res.data.code === 0) {
    message.success('Deleted successfully')
    // 刷新数据
    await fetchData()
  } else {
    message.error('Delete failed')
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})
</script>

<style scoped></style>
