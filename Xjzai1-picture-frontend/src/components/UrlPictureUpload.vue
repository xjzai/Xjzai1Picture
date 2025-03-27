<template>
  <div class="urlPictureUpload">
    <div class="url-picture-upload">
      <a-input-group compact style="margin-bottom: 16px">
        <a-input v-model:value="fileUrl" style="width: calc(100% - 120px)" placeholder="请输入图片 URL" />
        <a-button type="primary" :loading="loading" @click="handleUpload" style="width: 120px">提交</a-button>
      </a-input-group>
      <div class="img-wrapper">
        <img v-if="picture?.url" :src="picture?.url" alt="avatar" />
      </div>
    </div>

  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons-vue'
import type { UploadProps } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { uploadPictureByUrlUsingPost, uploadPictureUsingPost } from '@/api/pictureController'

interface Props {
  picture?: API.PictureVo
  onSuccess?: (newPicture: API.PictureVo) => void
}

const props = defineProps<Props>()

const loading = ref<boolean>(false)
const fileUrl = ref<string>()

/**
 * 上传
 */
const handleUpload = async () => {
  loading.value = true
  try {
    const params: API.PictureUploadRequest = { fileUrl: fileUrl.value }
    if (props.picture) {
      params.id = props.picture.id
    }
    const res = await uploadPictureByUrlUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('图片上传成功')
      // 将上传成功的图片信息传递给父组件
      props.onSuccess?.(res.data.data)
    } else {
      message.error('图片上传失败，' + res.data.message)
    }
  } catch (error) {
    message.error('图片上传失败')
  } finally {
    loading.value = false
  }
}


const beforeUpload = (file: UploadProps['fileList'][number]) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('不支持上传该各格式图片，推荐使用jpg和png!')
  }
  const isLt30M = file.size / 1024 / 1024 < 30
  if (!isLt30M) {
    message.error('图片必须小于30MB!')
  }
  return isJpgOrPng && isLt15M;
}
</script>
<style scoped>
.url-picture-upload {
  margin-bottom: 16px;
}

.url-picture-upload img {
  max-width: 100%;
  max-height: 480px;
}

.url-picture-upload .img-wrapper {
  text-align: center;
  margin-top: 16px;
}

</style>
