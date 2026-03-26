<template>
  <div id="spaceManagePage">
    <a-flex justify="space-between">
      <h2>Space Management</h2>
      <a-space>
        <a-button type="primary" href="/space/addSpace" target="_blank">+ Create Space</a-button>
        <a-button type="primary" ghost href="/space/analyze?queryPublic=1" target="_blank">
          Analyze Public Gallery
        </a-button>
        <a-button type="primary" ghost href="/space/analyze?queryAll=1" target="_blank">
          Analyze All Spaces
        </a-button>
      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px" />
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="Space Name" name="spaceName">
        <a-input v-model:value="searchParams.spaceName" placeholder="Enter space name" allow-clear />
      </a-form-item>
      <a-form-item label="Space Level" name="spaceLevel">
        <a-select
          v-model:value="searchParams.spaceLevel"
          :options="SPACE_LEVEL_OPTIONS"
          placeholder="Enter space level"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="Space Type" name="spaceType">
        <a-select
          v-model:value="searchParams.spaceType"
          :options="SPACE_TYPE_OPTIONS"
          placeholder="Enter space type"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="User ID" name="userId">
        <a-input v-model:value="searchParams.userId" placeholder="Enter user id" allow-clear />
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
        <!-- 空间级别 -->
        <template v-if="column.dataIndex === 'spaceLevel'">
          <a-tag>{{ SPACE_LEVEL_MAP[record.spaceLevel] }}</a-tag>
        </template>
        <!-- 空间类别 -->
        <template v-if="column.dataIndex === 'spaceType'">
          <a-tag>{{ SPACE_TYPE_MAP[record.spaceType] }}</a-tag>
        </template>
        <!-- 使用情况 -->
        <template v-if="column.dataIndex === 'spaceUseInfo'">
          <div>Size: {{ formatSize(record.totalSize) }} / {{ formatSize(record.maxSize) }}</div>
          <div>Count: {{ record.totalCount }} / {{ record.maxCount }}</div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button type="link" :href="`/space/analyze?spaceId=${record.id}`" target="_blank">
              Analyze
            </a-button>
            <a-button type="link" @click="doUpdate(record.id)">Edit</a-button>
            <a-button type="link" danger @click="doDelete(record.id)">Delete</a-button>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script setup lang="ts">
import { deleteSpaceUsingPost, listSpaceByPageUsingPost } from '@/api/spaceController'
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { useRouter } from 'vue-router'
import {
  SPACE_LEVEL_OPTIONS,
  SPACE_LEVEL_ENUM,
  SPACE_LEVEL_MAP,
  SPACE_TYPE_OPTIONS,
  SPACE_TYPE_MAP
} from '@/constants/space'
import { formatSize } from '../../utils'
import { PIC_REVIEW_STATUS_ENUM } from '@/constants/picture'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: 'Space Name',
    dataIndex: 'spaceName',
  },
  {
    title: 'Space Level',
    dataIndex: 'spaceLevel',
  },
  {
    title: 'Space Type',
    dataIndex: 'spaceType',
  },
  {
    title: 'Usage',
    dataIndex: 'spaceUseInfo',
  },
  {
    title: 'User ID',
    dataIndex: 'userId',
    width: 80,
  },
  {
    title: 'Created At',
    dataIndex: 'createTime',
  },
  {
    title: 'Edited At',
    dataIndex: 'editTime',
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
const searchParams = reactive<API.SpaceQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'create_time',
  sortOrder: 'descend',
})

// 获取数据
const fetchData = async () => {
  const res = await listSpaceByPageUsingPost({
    ...searchParams,
  })
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
    // console.log(dataList.value);
  } else {
    message.error('Failed to fetch data, ' + res.data.message + ', ' + res.data.description)
  }
}

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total) => `共 ${total} 条`,
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
  const res = await deleteSpaceUsingPost({ id })
  if (res.data.code === 0) {
    message.success('Deleted successfully')
    // 刷新数据
    await fetchData()
  } else {
    message.error('Delete failed')
  }
}

const router = useRouter()

// 编辑空间
const doUpdate = (id: string) => {
  router
    .push({
      path: '/space/addSpace',
      query: {
        id: id,
      },
    })
    .then(() => {
      // console.log('Navigation successful')
    })
    .catch((err) => {
      // console.error('Navigation failed', err)
    })
}

const handleReview = async (record: API.Space, reviewStatus: number) => {
  const reviewMessage =
    reviewStatus === PIC_REVIEW_STATUS_ENUM.PASS ? 'Admin approved' : 'Admin rejected'
  const res = await doSpaceReviewUsingPost({
    id: record.id,
    reviewStatus,
    reviewMessage,
  })
  if (res.data.code === 0) {
    message.success('Review operation successful')
    // 重新获取列表
    fetchData()
  } else {
    message.error('Review operation failed, ' + res.data.message + ', ' + res.data.description)
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})
</script>

<style scoped></style>
