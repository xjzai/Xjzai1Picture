<template>
  <div id="pictureManagePage">
    <a-flex justify="space-between">
      <h2>Picture Management</h2>
      <a-space>
        <a-button type="primary" href="/picture/addPicture" target="_blank">+ Create Picture</a-button>
        <a-button type="primary" href="/picture/addPicture/batch" target="_blank" ghost
          >+ Batch Create Pictures</a-button
        >
      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px" />
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="Keyword" name="searchText">
        <a-input
          v-model:value="searchParams.searchText"
          placeholder="Search by name and introduction"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="Category" name="category">
        <a-input v-model:value="searchParams.category" placeholder="Enter category" allow-clear />
      </a-form-item>
      <a-form-item label="Tags" name="tags">
        <a-select
          v-model:value="searchParams.tags"
          mode="tags"
          placeholder="Enter tags"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="Review Status" name="reviewStatus">
        <a-select
          v-model:value="searchParams.reviewStatus"
          :options="PIC_REVIEW_STATUS_OPTIONS"
          placeholder="Enter review status"
          style="min-width: 180px"
          allow-clear
        />
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
        <template v-if="column.dataIndex === 'url'">
          <a-image :src="record.thumbnailUrl ?? record.url" :width="120" />
        </template>
        <!-- 标签 -->
        <template v-if="column.dataIndex === 'tags'">
          <a-space wrap>
            <a-tag v-for="tag in JSON.parse(record.tags || '[]')" :key="tag" color="blue"
              >{{ tag }}
            </a-tag>
          </a-space>
        </template>
        <!-- 图片信息 -->
        <template v-if="column.dataIndex === 'picInfo'">
          <div>Format: {{ record.pictureFormat }}</div>
          <div>Width: {{ record.pictureWidth }}</div>
          <div>Height: {{ record.pictureHeight }}</div>
          <div>Aspect Ratio: {{ record.pictureScale }}</div>
          <div>Size: {{ (record.pictureSize / (1024 * 1024)).toFixed(2) }}MB</div>
        </template>
        <!-- 审核信息 -->
        <template v-if="column.dataIndex === 'reviewMessage'">
          <div>Review Status: {{ PIC_REVIEW_STATUS_MAP[record.reviewStatus] }}</div>
          <div>Review Message: {{ record.reviewMessage }}</div>
          <div>Reviewer: {{ record.reviewerId }}</div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button
              v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.PASS"
              type="link"
              @click="handleReview(record, PIC_REVIEW_STATUS_ENUM.PASS)"
            >
              Approve
            </a-button>
            <a-button
              v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.REJECT"
              type="link"
              danger
              @click="handleReview(record, PIC_REVIEW_STATUS_ENUM.REJECT)"
            >
              Reject
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
import { SmileOutlined, DownOutlined } from '@ant-design/icons-vue'
import {
  deletePictureUsingPost,
  doPictureReviewUsingPost,
  listPictureByPageUsingPost,
  listPictureVoByPageUsingPost,
} from '@/api/pictureController'
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { useRouter } from 'vue-router'
import {
  PIC_REVIEW_STATUS_ENUM,
  PIC_REVIEW_STATUS_MAP,
  PIC_REVIEW_STATUS_OPTIONS,
} from '../../constants/picture'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: 'Image',
    dataIndex: 'url',
    width: 150,
  },
  {
    title: 'Name',
    dataIndex: 'name',
    width: 10,
  },
  {
    title: 'Introduction',
    dataIndex: 'introduction',
    width: 150,
    // ellipsis: true,
  },
  {
    title: 'Category',
    dataIndex: 'category',
    width: 100,
  },
  {
    title: 'Tags',
    dataIndex: 'tags',
    width: 150,
  },
  {
    title: 'Picture Info',
    dataIndex: 'picInfo',
    width: 190,
  },
  {
    title: 'User ID',
    dataIndex: 'userId',
    width: 180,
  },
  {
    title: 'Review Message',
    dataIndex: 'reviewMessage',
  },
  {
    title: 'Created At',
    dataIndex: 'createTime',
    width: 200,
  },
  {
    title: 'Edited At',
    dataIndex: 'editTime',
    width: 200,
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
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  const res = await listPictureByPageUsingPost({
    ...searchParams,
  })
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
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
    showTotal: (total) => `Total ${total} records`,
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
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('Deleted successfully')
    // 刷新数据
    await fetchData()
  } else {
    message.error('Delete failed')
  }
}

const router = useRouter()

// 编辑图片
const doUpdate = (id: string) => {
  router
    .push({
      path: '/picture/addPicture',
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

const handleReview = async (record: API.Picture, reviewStatus: number) => {
  const reviewMessage =
    reviewStatus === PIC_REVIEW_STATUS_ENUM.PASS ? 'Admin approved' : 'Admin rejected'
  const res = await doPictureReviewUsingPost({
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
