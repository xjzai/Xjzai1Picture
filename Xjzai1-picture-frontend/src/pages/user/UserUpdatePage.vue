<template>
  <div id="userUpdatePage">
    <h2 style="margin-bottom: 16px">个人信息修改</h2>
    <a-form layout="vertical" :model="userForm" @finish="handleSubmit">
      <a-form-item label="头像" name="userAvatar">
        <a-input v-model:value="userForm.userAvatar" placeholder="请输入头像url" />
      </a-form-item>
      <a-form-item label="用户名" name="userName">
        <a-input v-model:value="userForm.userName" placeholder="请输入用户名" />
      </a-form-item>
      <a-form-item label="个人简介" name="userProfile">
        <a-textarea
          v-model:value="userForm.userProfile"
          placeholder="请输入个人简介"
          :autoSize="{ minRows: 5, maxRows: 5 }"
          allowClear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%"> 提交</a-button>
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
  console.log(userForm)
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
    message.success('修改成功')
    router.back()
  } else {
    message.error('修改失败，' + res.data.message)
  }
}
</script>
<style scoped>
#userUpdatePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
