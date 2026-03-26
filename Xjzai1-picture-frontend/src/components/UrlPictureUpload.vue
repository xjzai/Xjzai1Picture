<template>
  <div class="urlPictureUpload">
    <div class="url-picture-upload">
      <a-input-group compact style="margin-bottom: 16px">
        <a-input v-model:value="fileUrl" style="width: calc(100% - 120px)" placeholder="Please enter image URL" />
        <a-button type="primary" :loading="loading" @click="handleUpload" style="width: 120px">Submit</a-button>
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
  spaceId?: number
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
    params.spaceId = props.spaceId;
    if (props.picture) {
      params.id = props.picture.id
    }
    const res = await uploadPictureByUrlUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('Picture uploaded successfully')
      // 将上传成功的图片信息传递给父组件
      props.onSuccess?.(res.data.data)
    } else {
      message.error('Picture upload failed, ' + res.data.message + ', ' + res.data.description)
    }
  } catch (error) {
    message.error('Picture upload failed')
  } finally {
    loading.value = false
  }
}


const beforeUpload = (file: UploadProps['fileList'][number]) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('Unsupported image format. Please use JPG or PNG!')
  }
  const isLt30M = file.size / 1024 / 1024 < 30
  if (!isLt30M) {
    message.error('Image must be smaller than 30MB!')
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
