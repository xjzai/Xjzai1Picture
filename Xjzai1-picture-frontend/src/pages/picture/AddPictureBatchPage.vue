<template>
<div id="addPictureBatchPage">
<h2 style="margin-bottom: 16px">Batch Create Pictures</h2>
<a-form layout="vertical" :model="formData" @finish="handleSubmit">
  <a-form-item label="Keyword" name="searchText">
    <a-input v-model:value="formData.searchText" placeholder="Please enter keyword" />
  </a-form-item>
  <a-form-item label="Count" name="count">
    <a-input-number
      v-model:value="formData.count"
      placeholder="Please enter count"
      style="min-width: 180px"
      :min="1"
      :max="30"
      allow-clear
    />
  </a-form-item>
  <a-form-item label="Name Prefix" name="namePrefix">
    <a-input v-model:value="formData.namePrefix" placeholder="Please enter name prefix. Sequence numbers will be auto-generated" />
  </a-form-item>
  <a-form-item>
    <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">
      Execute Task
    </a-button>
  </a-form-item>
</a-form>
</div>
</template>
<script setup lang="ts">

import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import router from '@/router'
import { uploadPictureByBatchUsingPost } from '@/api/pictureController'

const formData = reactive<API.PictureUploadByBatchRequest>({
  count: 10,
})
const loading = ref(false)


const handleSubmit = async (values: any) => {
  loading.value = true;
  const res = await uploadPictureByBatchUsingPost({
    ...formData,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success(`Creation successful, ${res.data.data} items`)
    router.push({
      path: '/',
    })
  } else {
    message.error('Creation failed, ' + res.data.message + ', ' + res.data.description)
  }
  loading.value = false;
}

</script>
<style scoped>
#addPictureBatchPage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
