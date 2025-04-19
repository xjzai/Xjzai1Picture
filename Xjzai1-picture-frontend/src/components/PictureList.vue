<template>
  <div class="picture-list" style="margin-top: 10px">
    <!--    <button @click="new">点击传递数据</button>-->
    <!-- 图片列表 -->
    <a-list
      :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
      :data-source="dataList"
      :loading="loading"
    >
      <ShareModal ref="shareModalRef" :link="shareLink" />
      <template #renderItem="{ item: picture }">
        <a-list-item style="padding: 0">
          <!-- 单张图片 -->
          <a-card hoverable @click="doClickPicture(picture)">
            <template #cover>
              <img
                style="height: 180px; object-fit: cover"
                :alt="picture.name"
                :src="picture.thumbnailUrl ?? picture.url"
                loading="lazy"
              />
            </template>
            <a-card-meta :title="picture.name">
              <template #description>
                <a-flex>
                  <a-tag color="green">
                    {{ picture.category ?? '默认' }}
                  </a-tag>
                  <a-tag v-for="tag in picture.tags" :key="tag">
                    {{ tag }}
                  </a-tag>
                </a-flex>
              </template>
            </a-card-meta>
            <template v-if="showOp" #actions>
              <a-space @click="(e) => doSearch(picture, e)">
                <SearchOutlined />
              </a-space>
              <a-space @click="(e) => doShare(picture, e)">
                <ShareAltOutlined />
              </a-space>
              <a-space
                v-if="canEdit"
                @click="(e) => doEdit(picture, e)">
                <EditOutlined />
              </a-space>
              <a-space
                v-if="canDelete"
                @click="(e) => doDelete(picture, e)">
                <DeleteOutlined />
              </a-space>
              <a-space @click="(e) => doCheck(picture, e)">
                <a-checkbox
                  v-model:checked="picture.checked"
                  @change="doChange(picture)"
                ></a-checkbox>
              </a-space>
            </template>
          </a-card>
        </a-list-item>
      </template>
    </a-list>
    <DeleteConfirmModal ref="deleteConfirmModalRef" :on-success="onDeleteConfirmSuccess" :obj="deletePicture"/>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import {
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ShareAltOutlined,
} from '@ant-design/icons-vue'
import { deletePictureUsingPost } from '@/api/pictureController'
import { message } from 'ant-design-vue'
import ShareModal from '@/components/ShareModal.vue'
import { ref } from 'vue'
import BatchDeletePictureModal from '@/components/BatchDeletePictureModal.vue'
import DeletePictureModal from '@/components/DeleteConfirmModal.vue'
import DeleteConfirmModal from '@/components/DeleteConfirmModal.vue'

interface Props {
  dataList?: API.PictureVO[]
  loading?: boolean
  showOp?: boolean
  onReload?: () => void
  canEdit?: boolean
  canDelete?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  dataList: () => [],
  loading: false,
  showOp: false,
  canEdit: false,
  canDelete: false,
})

// 跳转至图片详情
const router = useRouter()
// todo 添加spaceId
const doClickPicture = (picture) => {
  console.log(picture)
  router.push({
    path: `/picture/${picture.id}/${picture.spaceId}`,
  })
}

// 编辑
const doEdit = (picture, e) => {
  e.stopPropagation()
  router.push({
    path: '/picture/addPicture',
    query: {
      id: picture.id,
      spaceId: picture.spaceId,
    },
  })
}

// 搜索
const doSearch = (picture, e) => {
  e.stopPropagation()
  window.open(`/picture/searchPicture?pictureId=${picture.id}`)
}

// 分享弹窗引用
const shareModalRef = ref()
// 分享链接
const shareLink = ref<string>()

// 分享
const doShare = (picture: API.PictureVO, e: Event) => {
  e.stopPropagation()
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${picture.id}`
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }
}

// 多选框模块
const doCheck = (picture: API.PictureVO, e: Event) => {
  e.stopPropagation()
}

const newPicture = ref()

const doChange = (picture: API.PictureVO[]) => {
  if (picture.checked) {
    newPicture.value = JSON.parse(JSON.stringify(picture))
    newPicture.value.checked = true
    emit('newPicture', newPicture.value)
  } else {
    newPicture.value = JSON.parse(JSON.stringify(picture))
    newPicture.value.checked = false
    emit('newPicture', newPicture.value)
  }
}
// 定义 emits
const emit = defineEmits<{
  (e: 'newPicture', newPicture: API.PictureVO[]): void,
}>()

// 删除确认模块
const deleteConfirmModalRef = ref()
const deletePicture = ref();

// 批量编辑成功后，刷新数据
const onDeleteConfirmSuccess = () => {
  deletePicture.value = '';
  // 让外层刷新
  props?.onReload()
}

// 打开批量编辑弹窗
const doDelete = (picture: API.PictureVO, e: Event) => {
  e.stopPropagation()
  deletePicture.value = picture;
  if (deleteConfirmModalRef.value) {
    deleteConfirmModalRef.value.openModal()
  }
}

</script>

<style scoped></style>
