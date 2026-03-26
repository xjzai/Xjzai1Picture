<template>
  <div id="userUpdatePage">
    <h2 style="margin-bottom: 16px">Edit Personal Information</h2>
    <a-form layout="vertical" :model="userForm" @finish="handleSubmit">
      <a-form-item label="Avatar" name="userAvatar">
        <a-input v-model:value="userForm.userAvatar" placeholder="Please enter avatar URL" />
      </a-form-item>
      <a-form-item label="Username" name="userName">
        <a-input v-model:value="userForm.userName" placeholder="Please enter your username" />
      </a-form-item>
      <a-form-item label="Profile" name="userProfile">
        <a-textarea
          v-model:value="userForm.userProfile"
          placeholder="Please enter your profile"
          :autoSize="{ minRows: 5, maxRows: 5 }"
          allowClear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%"> Submit</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script setup lang="ts">
import PictureUpload from '@/components/PictureUpload.vue'
import { useRoute, useRouter } from 'vue-router'
import { onMounted, reactive, ref } from 'vue'
import { updateUserUsingPost } from '@/api/userController'
import { message } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()
const userForm = reactive<API.UserUpdateRequest>({})
const userId = ref()
//
// const userForm = res<API.UserVo>({})
//
onMounted(() => {
  userId.value = route.query.userId
  userForm.userName = route.query.userName
  userForm.userAvatar = route.query.userAvatar
  userForm.userProfile = route.query.userProfile
  // console.log(userForm)
})

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const res = await updateUserUsingPost({
    id: userId.value,
    ...values,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success('Update successful')
    router.back()
  } else {
    message.error('Update failed, ' + res.data.message + ', ' + res.data.description)
  }
}
</script>
<style scoped>
#userUpdatePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
