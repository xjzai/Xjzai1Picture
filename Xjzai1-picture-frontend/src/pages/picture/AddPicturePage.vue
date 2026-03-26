<template>
  <div id="addPicturePage">
    <h2 style="margin-bottom: 16px">
      {{ route.query.id ? 'Edit Picture' : 'Create Picture' }}
    </h2>
    <a-typography-paragraph v-if="spaceId != 0" type="secondary">
      Save to Space: <a :href="`/space/${spaceId}`" target="_blank">{{ spaceId }}</a>
    </a-typography-paragraph>
    <!-- 选择上传方式 -->
    <!-- todo 尝试实现上传大文件-->
    <a-tabs v-model:activeKey="uploadType">
      <a-tab-pane key="file" tab="File Upload">
<!--        <PictureUpload :picture="picture" :onSuccess="onSuccess" />-->
        <PictureUpload :picture="picture" :spaceId="spaceId" :onSuccess="onSuccess" />
      </a-tab-pane>
      <a-tab-pane key="url" tab="URL Upload" force-render>
        <UrlPictureUpload :picture="picture" :spaceId="spaceId" :onSuccess="onSuccess" />
      </a-tab-pane>
    </a-tabs>
    <div v-if="picture" class="edit-bar">
      <a-button :icon="h(EditOutlined)" @click="doEditPicture">Edit Picture</a-button>
      <a-button type="primary" ghost :icon="h(FullscreenOutlined)" @click="doImagePainting">
        AI Outpainting
      </a-button>
      <ImageOutPainting
        ref="imageOutPaintingRef"
        :picture="picture"
        :spaceId="spaceId"
        :onSuccess="onImageOutPaintingSuccess"
      />
      <ImageCropper
        ref="imageCropperRef"
        :imageUrl="picture?.url"
        :picture="picture"
        :spaceId="spaceId"
        :space="space"
        :onSuccess="onSuccess"
      />


    </div>
    <a-form v-if="picture" layout="vertical" :model="pictureForm" @finish="handleSubmit">
      <a-form-item label="Name" name="name">
        <a-input v-model:value="pictureForm.name" placeholder="Please enter name" />
      </a-form-item>
      <a-form-item label="Introduction" name="introduction">
        <a-textarea
          v-model:value="pictureForm.introduction"
          placeholder="Please enter introduction"
          :autoSize="{ minRows: 5, maxRows: 5 }"
          allowClear
        />
      </a-form-item>
      <a-form-item label="Category" name="category">
        <a-auto-complete
          v-model:value="pictureForm.category"
          :options="categoryOptions"
          placeholder="Please enter category"
          allowClear
        />
      </a-form-item>
      <a-form-item label="Tags" name="tags">
        <a-select
          v-model:value="pictureForm.tags"
          :options="tagOptions"
          mode="tags"
          placeholder="Please enter tags"
          allowClear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">
          {{ route.query.id ? 'Edit' : 'Create' }}
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script setup lang="ts">
import PictureUpload from '@/components/PictureUpload.vue'
import { computed, onMounted, reactive, ref, h, watchEffect } from 'vue'
import {
  editPictureUsingPost,
  getPictureVoByIdUsingGet,
  listPictureTagCategoryUsingGet,
} from '@/api/pictureController'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import UrlPictureUpload from '@/components/UrlPictureUpload.vue'
import ImageCropper from '@/components/ImageCropper.vue'
import { EditOutlined } from '@ant-design/icons-vue'
import { getSpaceVoByIdUsingGet } from '@/api/spaceController'
import ImageOutPainting from '@/components/ImageOutPainting.vue'

const picture = ref<API.PictureVo>()
const pictureForm = reactive<API.PictureEditRequest>({})
const uploadType = ref<'file' | 'url'>('file')

const onSuccess = (newPicture: API.PictureVo) => {
  picture.value = newPicture
  pictureForm.name = newPicture.name
}

const router = useRouter()

// 空间 id
const spaceId = computed(() => {
  return route.query?.spaceId
})

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const pictureId = picture.value.id
  if (!pictureId) {
    return
  }
  const res = await editPictureUsingPost({
    id: pictureId,
    spaceId: spaceId.value,
    ...values,
  })
  if (res.data.code === 0 && res.data.data) {
    if (route.query.id) {
      message.success('Update successful')
      router.back()
    } else {
      message.success('Created successfully')
      // console.log(spaceId)
      // todo 跳转到图片详情页
      router.push({
        path: `/picture/${pictureId}/${spaceId.value ?? 0}`,
      })
    }
  } else {
    if (route.query.id) {
      message.error('Update failed, ' + res.data.message + ', ' + res.data.description)
    } else {
      message.error('Creation failed, ' + res.data.message + ', ' + res.data.description)
    }
  }
}

const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

// 获取标签和分类选项
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    // 转换成下拉选项组件接受的格式
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('Failed to load options, ' + res.data.message + ', ' + res.data.description)
  }
}

const route = useRoute()

// 获取老数据
const getOldPicture = async () => {
  // 获取数据
  const id = route.query?.id
  const spaceId = route.query?.spaceId
  if (id) {
    const res = await getPictureVoByIdUsingGet({
      id: id,
      spaceId: spaceId ?? undefined,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      picture.value = data
      pictureForm.name = data.name
      pictureForm.introduction = data.introduction
      pictureForm.category = data.category
      pictureForm.tags = data.tags
    }
  }
}

onMounted(() => {
  getOldPicture()
  getTagCategoryOptions()
})

// 图片编辑弹窗引用
const imageCropperRef = ref()

// 编辑图片
const doEditPicture = () => {
  if (imageCropperRef.value) {
    imageCropperRef.value.openModal()
  }
}

// 编辑成功事件
const onCropSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
}

const space = ref<API.SpaceVO>()

// 获取空间信息
const fetchSpace = async () => {
  // 获取数据
  if (spaceId.value) {
    const res = await getSpaceVoByIdUsingGet({
      id: spaceId.value,
    })
    if (res.data.code === 0 && res.data.data) {
      space.value = res.data.data
    }
  }
}

watchEffect(() => {
  fetchSpace()
})

// AI 扩图弹窗引用
const imageOutPaintingRef = ref()

// AI 扩图
const doImagePainting = () => {
  if (imageOutPaintingRef.value) {
    imageOutPaintingRef.value.openModal()
  }
}

// 编辑成功事件
const onImageOutPaintingSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
}


</script>
<style scoped>
#addPicturePage {
  max-width: 720px;
  margin: 0 auto;
}

#addPicturePage .edit-bar {
  text-align: center;
  margin: 16px 0;
}

</style>
