<template>
  <div class="userDetailPage">
    <a-row :gutter="[16, 16]" position="center">
      <!-- 图片信息区 -->
      <a-col >
        <a-card title="User Information">
          <a-descriptions :column="1">
            <a-descriptions-item label="Avatar">
              <a-space>
                <a-avatar :size="300" :src="user.userAvatar" />
              </a-space>
            </a-descriptions-item>
            <a-descriptions-item label="Username">
              {{ user.userName ?? 'Anonymous User' }}
            </a-descriptions-item>
            <a-descriptions-item label="Account">
              {{ user.userAccount }}
            </a-descriptions-item>
            <a-descriptions-item label="ID">
              {{ user.id }}
            </a-descriptions-item>
            <a-descriptions-item label="Profile">
              {{ user.userProfile ?? 'No profile yet' }}
            </a-descriptions-item>
          </a-descriptions>
          <a-space wrap>
            <a-button type="default" @click="doEdit">
              Edit
              <template #icon>
                <EditOutlined />
              </template>
            </a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
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
import { getLoginUserUsingGet } from '@/api/userController'

const user = ref<API.UserVo>({})

// 获取图片详情
const fetchUserDetail = async () => {
  try {
    const res = await getLoginUserUsingGet({
    })
    if (res.data.code === 0 && res.data.data) {
      user.value = res.data.data
      // console.log(res.data.data)
    } else {
      message.error('Failed to get user details, ' + res.data.message + ', ' + res.data.description)
    }
  } catch (e: any) {
    message.error('Failed to get user details: ' + e.message)
  }
}

// 编辑
const doEdit = () => {
  router.push({
    path: '/user/update',
    query: {
      userId: user.value.id,
      userName: user.value.userName,
      userAccount: user.value.userAccount,
      userProfile: user.value.userProfile,
      userAvatar: user.value.userAvatar,
    },
  })
}

onMounted(() => {
  fetchUserDetail()
  // console.log(picture.value.permissionList)
})

</script>
<style scoped>
.userDetailPage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
