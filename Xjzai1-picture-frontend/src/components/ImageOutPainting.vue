<template>
  <a-modal
    class="image-out-painting"
    v-model:visible="visible"
    title="AI Outpainting"
    :footer="false"
    @cancel="closeModal"
  >
    <a-row gutter="16">
      <a-col span="12">
        <h4>Original Image</h4>
        <img :src="picture?.url" :alt="picture?.name" style="max-width: 100%" />
      </a-col>
      <a-col span="12">
        <h4>Outpainting Result</h4>
        <img
          v-if="resultImageUrl"
          :src="resultImageUrl"
          :alt="picture?.name"
          style="max-width: 100%"
        />
      </a-col>
    </a-row>
    <div style="margin-bottom: 16px" />
    <a-flex gap="16" justify="center">
      <a-button type="primary" :loading="!!taskId" ghost
                @click="createTask">
        Generate Image
      </a-button>
      <a-button type="primary" v-if="resultImageUrl"
                :loading="uploadLoading"
                @click="handleUpload">
        Apply Result
      </a-button>
    </a-flex>

  </a-modal>
</template>

<script setup lang="ts">
import { onUnmounted, ref } from 'vue'
import {
  createPictureOutPaintingTaskUsingPost, getPictureOutPaintingTaskUsingGet,
  uploadPictureByUrlUsingPost,
  uploadPictureUsingPost
} from '@/api/pictureController'
import { message } from 'ant-design-vue'

interface Props {
  picture?: API.PictureVO
  spaceId?: number
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()

// 是否可见
const visible = ref(false)

// 打开弹窗
const openModal = () => {
  visible.value = true
}

// 关闭弹窗
const closeModal = () => {
  visible.value = false
}
const resultImageUrl = ref<string>()

// 任务 id
let taskId = ref<string>()

/**
 * 创建任务
 */
const createTask = async () => {
  if (!props.picture?.id) {
    return
  }
  const res = await createPictureOutPaintingTaskUsingPost({
    pictureId: props.picture.id,
    // 可以根据需要设置扩图参数
    parameters: {
      xScale: 2,
      yScale: 2,
    },
  })
  if (res.data.code === 0 && res.data.data) {
    message.success('Task created successfully. Please wait and do not leave this page.')
    console.log(res.data.data.output.taskId)
    taskId.value = res.data.data.output.taskId
    // 开启轮询
    startPolling()
  } else {
    message.error('Failed to create task, ' + res.data.message)
  }
}
// 轮询定时器
let pollingTimer: NodeJS.Timeout = null

// 清理轮询定时器
const clearPolling = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
    taskId.value = null
  }
}

// 开始轮询
const startPolling = () => {
  if (!taskId.value) return

  pollingTimer = setInterval(async () => {
    try {
      const res = await getPictureOutPaintingTaskUsingGet({
        taskId: taskId.value,
      })
      if (res.data.code === 0 && res.data.data) {
        const taskResult = res.data.data.output
        if (taskResult.taskStatus === 'SUCCEEDED') {
          message.success('Outpainting task succeeded')
          resultImageUrl.value = taskResult.outputImageUrl
          clearPolling()
        } else if (taskResult.taskStatus === 'FAILED') {
          message.error('Outpainting task failed')
          clearPolling()
        }
      }
    } catch (error) {
      console.error('Failed to poll task status', error)
      message.error('Failed to check task status. Please try again later.')
      clearPolling()
    }
  }, 3000) // 每隔 3 秒轮询一次
}

// 清理定时器，避免内存泄漏
onUnmounted(() => {
  clearPolling()
})
const uploadLoading = ref<boolean>(false)

const handleUpload = async () => {
  uploadLoading.value = true
  try {
    const params: API.PictureUploadRequest = {
      fileUrl: resultImageUrl.value,
      spaceId: props.spaceId,
    }
    if (props.picture) {
      params.id = props.picture.id
    }
    const res = await uploadPictureByUrlUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('Picture uploaded successfully')
      // 将上传成功的图片信息传递给父组件
      props.onSuccess?.(res.data.data)
      // 关闭弹窗
      closeModal()
    } else {
      message.error('Picture upload failed, ' + res.data.message)
    }
  } catch (error) {
    message.error('Picture upload failed')
  } finally {
    uploadLoading.value = false
  }
}

// 暴露函数给父组件
defineExpose({
  openModal,
})
</script>

<style scoped>
.image-out-painting {
  text-align: center;
}
</style>
