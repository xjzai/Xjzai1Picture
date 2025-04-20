<template>
  <div class="pictureDetailPage">
    <a-row :gutter="[16, 16]">
      <!-- 图片展示区 -->
      <a-col :sm="24" :md="16" :xl="18">
        <a-card title="图片预览">
          <a-image style="max-height: 600px; object-fit: contain" :src="picture.url" />
        </a-card>
      </a-col>
      <!-- 图片信息区 -->
      <a-col :sm="24" :md="8" :xl="6">
        <a-card title="图片信息">
          <a-descriptions :column="1">
            <a-descriptions-item label="作者">
              <a-space>
                <a-avatar :size="24" :src="picture.user?.userAvatar" />
                <div>{{ picture.user?.userName }}</div>
              </a-space>
            </a-descriptions-item>
            <a-descriptions-item label="名称">
              {{ picture.name ?? '未命名' }}
            </a-descriptions-item>
            <a-descriptions-item label="简介">
              {{ picture.introduction ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="分类">
              {{ picture.category ?? '默认' }}
            </a-descriptions-item>
            <a-descriptions-item label="标签">
              <a-tag v-for="tag in picture.tags" :key="tag">
                {{ tag }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="格式">
              {{ picture.pictureFormat ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽度">
              {{ picture.pictureWidth ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="高度">
              {{ picture.pictureHeight ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽高比">
              {{ picture.pictureScale ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="大小">
              {{ formatSize(picture.pictureSize) }}
            </a-descriptions-item>
            <a-descriptions-item label="主色调">
              <a-space>
                {{ picture.pictureColor ?? '-' }}
                <div
                  v-if="picture.pictureColor"
                  :style="{
                    backgroundColor: toHexColor(picture.pictureColor),
                    width: '16px',
                    height: '16px',
                  }"
                />
              </a-space>
            </a-descriptions-item>
          </a-descriptions>
          <a-space wrap>
            <a-button type="primary" @click="doDownload">
              免费下载
              <template #icon>
                <DownloadOutlined />
              </template>
            </a-button>
            <a-button type="primary" ghost @click="doShare">
              分享
              <template #icon>
                <ShareAltOutlined />
              </template>
            </a-button>
            <a-button v-if="canEdit" type="default" @click="doEdit">
              编辑
              <template #icon>
                <EditOutlined />
              </template>
            </a-button>
            <a-button v-if="canDelete" danger @click="doDelete">
              删除
              <template #icon>
                <DeleteOutlined />
              </template>
            </a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
    <ShareModal ref="shareModalRef" :link="shareLink" />
  </div>
</template>
<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { deletePictureUsingPost, getPictureVoByIdUsingGet } from '@/api/pictureController'
import { message } from 'ant-design-vue'
import { downloadImage, formatSize } from '../../utils'
import {
  EditOutlined,
  DeleteOutlined,
  DownloadOutlined,
  ShareAltOutlined,
} from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/user'
import router from '@/router'
import ShareModal from '@/components/ShareModal.vue'
import { SPACE_PERMISSION_ENUM } from '@/constants/space'

const props = defineProps<{
  id: string | number,
  spaceId: string | number
}>()
const picture = ref<API.PictureVo>({})

// 获取图片详情
const fetchPictureDetail = async () => {
  console.log(props)
  try {
    const res = await getPictureVoByIdUsingGet({
      id: props.id,
      spaceId: props.spaceId ?? undefined,
    })
    if (res.data.code === 0 && res.data.data) {
      picture.value = res.data.data
      console.log(res.data.data)
    } else {
      message.error('获取图片详情失败，' + res.data.message)
    }
  } catch (e: any) {
    message.error('获取图片详情失败：' + e.message)
  }
}

// const loginUserStore = useLoginUserStore()
// 是否具有编辑权限, 已使用新的鉴权逻辑
// const canEdit = computed(() => {
//   const loginUser = loginUserStore.loginUser
//   // 未登录不可编辑
//   if (!loginUser.id) {
//     return false
//   }
//   // 仅本人或管理员可编辑
//   const user = picture.value.user || {}
//   return loginUser.id === user.id || loginUser.userRole === 'admin'
// })

// 编辑
const doEdit = () => {
  // router.push('/picture/addPicture?id=' + picture.value.id)
  router.push({
    path: '/picture/addPicture',
    query: {
      id: picture.value.id,
      spaceId: picture.value.spaceId,
    },
  })
}
// 删除
const doDelete = async () => {
  const id = picture.value.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
  } else {
    message.error('删除失败')
  }
}

// 处理下载
const doDownload = () => {
  downloadImage(picture.value.originalUrl)
  console.log(picture.value.permissionList)
}

onMounted(() => {
  fetchPictureDetail()
  // console.log(picture.value.permissionList)
})

const toHexColor = (input) => {
  // 去掉 0x 前缀
  const colorValue = input.startsWith('0x') ? input.slice(2) : input

  // 将剩余部分解析为十六进制数，再转成 6 位十六进制字符串
  const hexColor = parseInt(colorValue, 16).toString(16).padStart(6, '0')

  // 返回标准 #RRGGBB 格式
  return `#${hexColor}`
}

// 分享弹窗引用
const shareModalRef = ref()
// 分享链接
const shareLink = ref<string>()

// 分享
const doShare = () => {
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${picture.value.id}`
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }
}

// 通用权限检查函数
function createPermissionChecker(permission: string) {
  return computed(() => {
    return (picture.value.permissionList ?? []).includes(permission)
  })
}

// 定义权限检查
const canEdit = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_EDIT)
const canDelete = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_DELETE)

</script>
<style scoped></style>
