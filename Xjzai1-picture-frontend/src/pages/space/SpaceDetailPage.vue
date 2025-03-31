<template>
  <div id="spaceDetailPage">
    <!-- 空间信息 -->
    <a-flex justify="space-between">
      <h2>{{ space.spaceName }}（私有空间）</h2>
      <a-space size="middle">
        <a-button type="primary" :href="`/picture/addPicture?spaceId=${id}`" target="_blank">
          + 创建图片
        </a-button>
        <a-button
          type="primary"
          ghost
          :icon="h(BarChartOutlined)"
          :href="`/space/analyze?spaceId=${id}`"
          target="_blank"
        >
          空间分析
        </a-button>
        <a-button :icon="h(SelectOutlined)" v-model:checked="allChecked" @click="doAllSelect"> 全选图片</a-button>
        <a-button :icon="h(EditOutlined)" @click="doBatchEdit"> 批量编辑</a-button>
        <a-button :icon="h(DeleteOutlined)" @click="doBatchDelete"> 批量删除</a-button>
        <a-tooltip
          :title="`占用空间 ${formatSize(space.totalSize)} / ${formatSize(space.maxSize)}`"
        >
          <a-progress
            type="circle"
            :percent="((space.totalSize * 100) / space.maxSize).toFixed(1)"
            :size="42"
          />
        </a-tooltip>
      </a-space>
    </a-flex>
    <!-- 搜索表单 -->
    <!-- 搜索表单 -->
    <PictureSearchForm :onSearch="onSearch" />
    <div style="margin-bottom: 16px" />
    <!-- 图片列表 -->
    <PictureList :dataList="dataList" :loading="loading" :showOp="true" :onReload="fetchData" @newPicture="getNewPicture"/>
    <a-pagination
      style="text-align: right"
      v-model:current="searchParams.current"
      v-model:pageSize="searchParams.pageSize"
      :total="total"
      :show-total="() => `图片总数 ${total} / ${space.maxCount}`"
      @change="onPageChange"
    />
    <BatchEditPictureModal
      ref="batchEditPictureModalRef"
      :spaceId="id"
      :pictureList="newDataList"
      :onSuccess="onBatchEditPictureSuccess"
    />
    <BatchDeletePictureModal
      ref="batchDeletePictureModalRef"
      :spaceId="id"
      :pictureList="newDataList"
      :onSuccess="onBatchDeletePictureSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import PictureList from '@/components/PictureList.vue'
import { formatSize } from '@/utils'
import { getSpaceVoByIdUsingGet } from '@/api/spaceController'
import { onMounted, reactive, ref, h } from 'vue'
import { message } from 'ant-design-vue'
import { listPictureVoByPageUsingPost } from '@/api/pictureController'
import PictureSearchForm from '@/components/PictureSearchForm.vue'
import BatchEditPictureModal from '@/components/BatchEditPictureModal.vue'
import { DeleteOutlined, EditOutlined, SelectOutlined, BarChartOutlined } from '@ant-design/icons-vue'
import BatchDeletePictureModal from '@/components/BatchDeletePictureModal.vue'

const props = defineProps<{
  id: string | number
}>()
const space = ref<API.SpaceVO>({})

// 获取空间详情
const fetchSpaceDetail = async () => {
  try {
    const res = await getSpaceVoByIdUsingGet({
      id: props.id,
    })
    if (res.data.code === 0 && res.data.data) {
      space.value = res.data.data
    } else {
      message.error('获取空间详情失败，' + res.data.message)
    }
  } catch (e: any) {
    message.error('获取空间详情失败：' + e.message)
  }
}

onMounted(() => {
  fetchSpaceDetail()
})

// 数据
const dataList = ref([])
const total = ref(0)
const loading = ref(true)

// 搜索条件
const searchParams = ref<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'create_time',
  sortOrder: 'descend',
})

// 分页参数
const onPageChange = (page, pageSize) => {
  searchParams.current = page
  searchParams.pageSize = pageSize
  fetchData()
}

// 搜索
const onSearch = (newSearchParams: API.PictureQueryRequest) => {
  searchParams.value = {
    ...searchParams.value,
    ...newSearchParams,
    current: 1,
  }
  fetchData()
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  // 转换搜索参数
  const params = {
    spaceId: props.id,
    ...searchParams.value,
  }
  const res = await listPictureVoByPageUsingPost(params)
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    dataList.value.forEach(item => {
      item.checked = false;
    })
    console.log(dataList.value)
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
  // 重置选中状态
  newDataList.value = []
  console.log(newDataList.value);
  // 重置全选按钮状态
  allChecked.value = false

  loading.value = false
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

// 批量编辑弹窗引用
const batchEditPictureModalRef = ref()

// 批量编辑成功后，刷新数据
const onBatchEditPictureSuccess = () => {
  fetchData()
}

// 打开批量编辑弹窗
const doBatchEdit = () => {
  if (batchEditPictureModalRef.value) {
    batchEditPictureModalRef.value.openModal()
  }
}
// 从子组件传回的值
const newDataList = ref([])

const getNewPicture = (newPicture) => {
  if (newPicture.checked) {
    newDataList.value.push(newPicture)
  } else {
    newDataList.value = newDataList.value.filter(item => item.id !== newPicture.id);
  }
  console.log('接收到子组件的数据:', newDataList.value);
  // message.error("6666");
  // 更新父组件状态等操作
}

// 全选图片
const allChecked = ref(false);
const doAllSelect = () => {
  allChecked.value = !allChecked.value;
  if (allChecked.value) {
    dataList.value.forEach((item) => {
      item.checked = true;
    });
    newDataList.value = dataList.value;
  } else {
    dataList.value.forEach((item) => {
      item.checked = false;
    });
    newDataList.value = [];
  }
}

// 批量删除
// 批量删除弹窗引用
const batchDeletePictureModalRef = ref()

// 批量删除成功后，刷新数据
const onBatchDeletePictureSuccess = () => {
  fetchData()
}

// 打开批量删除弹窗
const doBatchDelete = () => {
  if (batchDeletePictureModalRef.value) {
    batchDeletePictureModalRef.value.openModal()
  }
}
</script>

<style scoped></style>
