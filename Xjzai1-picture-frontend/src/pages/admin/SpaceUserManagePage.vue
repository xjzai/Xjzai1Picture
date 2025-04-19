<template>
  <div id="spaceManagePage">
    <a-flex justify="space-between">
      <h2>空间用户管理</h2>
    </a-flex>
    <div style="margin-bottom: 16px" />
    <a-form layout="inline" :model="formData" @finish="handleSubmit">
      <a-form-item label="用户 id" name="userId">
        <a-input v-model:value="formData.userId" placeholder="请输入用户 id" allow-clear />
      </a-form-item>
<!--      <a-form-item label="账号" name="userAccount">-->
<!--        <a-input v-model:value="formData.userAccount" placeholder="请输入账号" allow-clear />-->
<!--      </a-form-item>-->
<!--      <a-form-item label="用户名" name="username">-->
<!--        <a-input v-model:value="formData.username" placeholder="请输入用户名" allow-clear />-->
<!--      </a-form-item>-->
      <a-form-item>
        <a-button type="primary" html-type="submit">添加用户</a-button>
      </a-form-item>
    </a-form>
    <a-table :columns="columns" :data-source="dataList">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userInfo'">
          <a-space>
            <a-avatar :src="record.user?.userAvatar" />
            {{ record.user?.userName }}
          </a-space>
        </template>
        <template v-if="column.dataIndex === 'spaceRole'">
          <a-select
            v-model:value="record.spaceRole"
            :options="SPACE_ROLE_OPTIONS"
            @change="(value) => editSpaceRole(value, record)"
          />
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button type="link" danger @click="doDelete(record.id)">删除</a-button>
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
import { SPACE_LEVEL_OPTIONS, SPACE_LEVEL_ENUM, SPACE_LEVEL_MAP, SPACE_ROLE_OPTIONS } from '@/constants/space'
import { formatSize } from '../../utils'
import { PIC_REVIEW_STATUS_ENUM } from '@/constants/picture'
import {
  addSpaceUserUsingPost,
  deleteSpaceUserUsingPost,
  editSpaceUserUsingPost,
  listSpaceUserUsingPost
} from '@/api/spaceUserController'

// 表格列
const columns = [
  {
    title: '用户',
    dataIndex: 'userInfo',
  },
  {
    title: '角色',
    dataIndex: 'spaceRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 定义属性
interface Props {
  id: string
}

const props = defineProps<Props>()

// 数据
const dataList = ref([])

// 获取数据
const fetchData = async () => {
  const spaceId = props.id
  if (!spaceId) {
    return
  }
  const res = await listSpaceUserUsingPost({
    spaceId,
  })
  if (res.data.data) {
    dataList.value = res.data.data ?? []
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

// 编辑空间角色的函数
const editSpaceRole = async (value, record) => {
  const res = await editSpaceUserUsingPost({
    id: record.id,
    spaceRole: value,
  })
  if (res.data.code === 0) {
    message.success('修改成功')
  } else {
    message.error('修改失败，' + res.data.message)
  }
}

// 删除成员的函数
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteSpaceUserUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } else {
    message.error('删除失败')
  }
}

// 编写表单项变量和提交函数
// 添加用户
const formData = reactive<API.SpaceUserAddRequest>({})

const handleSubmit = async () => {
  const spaceId = props.id
  if (!spaceId) {
    return
  }
  const res = await addSpaceUserUsingPost({
    spaceId,
    ...formData,
  })
  if (res.data.code === 0) {
    message.success('添加成功')
    // 刷新数据
    fetchData()
  } else {
    message.error('添加失败，' + res.data.message)
  }
}



//
// // 数据
// const dataList = ref([])
// const total = ref(0)
//
// // 搜索条件
// const searchParams = reactive<API.SpaceQueryRequest>({
//   current: 1,
//   pageSize: 10,
//   sortField: 'create_time',
//   sortOrder: 'descend',
// })
//
// // 获取数据
// const fetchData = async () => {
//   const res = await listSpaceByPageUsingPost({
//     ...searchParams,
//   })
//   if (res.data.data) {
//     dataList.value = res.data.data.records ?? []
//     total.value = res.data.data.total ?? 0
//     console.log(dataList.value);
//   } else {
//     message.error('获取数据失败，' + res.data.message)
//   }
// }
//
// // 分页参数
// const pagination = computed(() => {
//   return {
//     current: searchParams.current ?? 1,
//     pageSize: searchParams.pageSize ?? 10,
//     total: total.value,
//     showSizeChanger: true,
//     showTotal: (total) => `共 ${total} 条`,
//   }
// })
//
// // 表格变化处理
// const doTableChange = (page: any) => {
//   searchParams.current = page.current
//   searchParams.pageSize = page.pageSize
//   fetchData()
// }
// // 获取数据
// const doSearch = () => {
//   // 重置页码
//   searchParams.current = 1
//   fetchData()
// }
//
// // 删除数据
// const doDelete = async (id: string) => {
//   if (!id) {
//     return
//   }
//   const res = await deleteSpaceUsingPost({ id })
//   if (res.data.code === 0) {
//     message.success('删除成功')
//     // 刷新数据
//     await fetchData()
//   } else {
//     message.error('删除失败')
//   }
// }
//
// const router = useRouter()
//
// // 编辑空间
// const doUpdate = (id: string) => {
//   router
//     .push({
//       path: '/space/addSpace',
//       query: {
//         id: id,
//       },
//     })
//     .then(() => {
//       console.log('Navigation successful')
//     })
//     .catch((err) => {
//       console.error('Navigation failed', err)
//     })
// }
//
// const handleReview = async (record: API.Space, reviewStatus: number) => {
//   const reviewMessage =
//     reviewStatus === PIC_REVIEW_STATUS_ENUM.PASS ? '管理员操作通过' : '管理员操作拒绝'
//   const res = await doSpaceReviewUsingPost({
//     id: record.id,
//     reviewStatus,
//     reviewMessage,
//   })
//   if (res.data.code === 0) {
//     message.success('审核操作成功')
//     // 重新获取列表
//     fetchData()
//   } else {
//     message.error('审核操作失败，' + res.data.message)
//   }
// }
//
// // 页面加载时请求一次
// onMounted(() => {
//   fetchData()
// })
</script>

<style scoped></style>
