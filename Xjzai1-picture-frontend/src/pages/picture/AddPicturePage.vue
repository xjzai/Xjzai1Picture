<template>
  <div class="addPicturePage">
    <PictureUpload :picture="picture" :onSuccess="onSuccess" />
  </div>
</template>
<script setup lang="ts">
import PictureUpload from '@/components/PictureUpload.vue'
import { ref } from 'vue'

const picture = ref<API.PictureVO>()
const onSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
}

</script>
<style>
</style>
