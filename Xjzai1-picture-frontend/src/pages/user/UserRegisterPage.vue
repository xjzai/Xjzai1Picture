<template>
  <div id="userRegisterPage">
    <h2 class="title">xjz Cloud Gallery - User Registration</h2>
    <div class="desc">Enterprise smart collaborative cloud gallery</div>
    <a-form
      :model="formState"
      name="basic"
      label-align="left"
      autocomplete="off"
      @finish="handleSubmit"
    >
      <a-form-item name="userAccount" :rules="[{ required: true, message: 'Please enter your account' }]">
        <a-input v-model:value="formState.userAccount" placeholder="Please enter your account" />
      </a-form-item>
      <a-form-item name="userName" :rules="[{ required: true, message: 'Please enter your username' }]">
        <a-input v-model:value="formState.userName" placeholder="Please enter your username" />
      </a-form-item>
      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: 'Please enter your password' },
          { min: 8, message: 'Password must be at least 8 characters' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="Please enter your password" />
      </a-form-item>
      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: 'Please enter confirmation password' },
          { min: 8, message: 'Confirmation password must be at least 8 characters' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="Please enter confirmation password" />
      </a-form-item>
      <div class="tips">
        Already have an account?
        <RouterLink to="/user/login">Log in</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">Register</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { userRegisterUsingPost } from '@/api/userController'

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userName: '',
  userPassword: '',
  checkPassword: '',
})
const router = useRouter()

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  // 判断两次输入的密码是否一致
  if (formState.userPassword !== formState.checkPassword) {
    message.error('The two passwords do not match')
    return
  }
  const res = await userRegisterUsingPost(values)
  // 注册成功，跳转到登录页面
  if (res.data.code === 0 && res.data.data) {
    message.success('Registration successful')
    await router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error('Registration failed, ' + res.data.message + ', ' + res.data.description)
  }
}
</script>

<style scoped>
#userRegisterPage {
  max-width: 360px;
  margin: 0 auto;
}

.title {
  text-align: center;
  margin-bottom: 16px;
}

.desc {
  text-align: center;
  color: #bbb;
  margin-bottom: 16px;
}

.tips {
  margin-bottom: 16px;
  color: #bbb;
  font-size: 13px;
  text-align: right;
}
</style>
