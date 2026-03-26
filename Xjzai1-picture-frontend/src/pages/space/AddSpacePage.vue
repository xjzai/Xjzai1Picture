<template>
  <div id="addSpacePage">
    <h2 style="margin-bottom: 16px">
      {{ route.query.id ? 'Edit Space' : 'Create Space' }}
    </h2>
    <a-form layout="vertical" :model="spaceForm" @finish="handleSubmit">
      <a-form-item label="Space Name" name="spaceName">
        <a-input v-model:value="spaceForm.spaceName" placeholder="Please enter space name" allow-clear />
      </a-form-item>
      <a-form-item label="Space Level" name="spaceLevel">
        <a-select
          v-model:value="spaceForm.spaceLevel"
          :options="SPACE_LEVEL_OPTIONS"
          placeholder="Please enter space level"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <!--      <a-form-item>-->
      <!--        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">-->
      <!--          提交-->
      <!--        </a-button>-->
      <!--      </a-form-item>-->
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">
          {{ route.query.id ? 'Edit' : 'Create' }}{{ SPACE_TYPE_MAP[spaceType] }}
        </a-button>
      </a-form-item>
    </a-form>
    <a-card title="Space Level Introduction">
      <a-typography-paragraph>
        * Currently only the Basic plan is supported. If you need to upgrade, please contact
        <a href="http://www.xjzai1.top" target="_blank">xjzai1</a>。
      </a-typography-paragraph>
      <a-typography-paragraph v-for="spaceLevel in spaceLevelList">
        {{ spaceLevel.text }}: Max size {{ formatSize(spaceLevel.maxSize) }}, Max count {{ spaceLevel.maxCount }}
      </a-typography-paragraph>
    </a-card>
  </div>
</template>
<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import {
  addSpaceUsingPost,
  editSpaceUsingPost, getSpaceVoByIdUsingGet,
  listSpaceLevelUsingGet, updateSpaceUsingPost
} from '@/api/spaceController'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { SPACE_LEVEL_ENUM, SPACE_LEVEL_OPTIONS, SPACE_TYPE_ENUM, SPACE_TYPE_MAP } from '@/constants/space'
import { formatSize } from '../../utils'

const space = ref<API.SpaceVo>()
const spaceForm = reactive<API.SpaceAddRequest | API.SpaceUpdateRequest>({
  spaceName: '',
  spaceLevel: SPACE_LEVEL_ENUM.COMMON,
})
const loading = ref(false)

const router = useRouter()
const route = useRoute();

const oldSpace = ref<API.SpaceVO>()

// 获取老数据
const getOldSpace = async () => {
  // 获取数据
  const id = route.query?.id
  if (id) {
    const res = await getSpaceVoByIdUsingGet({
      id: id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      oldSpace.value = data
      spaceForm.spaceName = data.spaceName
      spaceForm.spaceLevel = data.spaceLevel
    }
  }
}


/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const spaceId = oldSpace.value?.id
  loading.value = true
  let res
  // 更新
  if (spaceId) {
    res = await updateSpaceUsingPost({
      id: spaceId,
      ...spaceForm,
    })
  } else {
    // 创建
    res = await addSpaceUsingPost({
      ...spaceForm,
      spaceType: spaceType.value
    })
  }
  if (res.data.code === 0 && res.data.data) {
    message.success('Operation successful')
    let path = `/space/${spaceId ?? res.data.data}`
    router.push({
      path,
    })
  } else {
    message.error('Operation failed, ' + res.data.message + ', ' + res.data.description);
  }
  loading.value = false
}

const spaceLevelList = ref<API.SpaceLevel[]>([])

// 获取空间级别
const fetchSpaceLevelList = async () => {
  const res = await listSpaceLevelUsingGet()
  if (res.data.code === 0 && res.data.data) {
    spaceLevelList.value = res.data.data
  } else {
    message.error('Failed to load space levels, ' + res.data.message + ', ' + res.data.description)
  }
}

// 空间类别
const spaceType = computed(() => {
  if (route.query?.type) {
    return Number(route.query.type)
  }
  return SPACE_TYPE_ENUM.PRIVATE
})



onMounted(() => {
  fetchSpaceLevelList();
  getOldSpace();
})
</script>
<style scoped>
#addSpacePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
