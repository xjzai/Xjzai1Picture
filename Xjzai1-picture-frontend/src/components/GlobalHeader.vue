<template>
  <div id="globalHeader">
    <a-row :wrap="false">
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="title-bar">
            <img class="logo" src="../assets/logo.jpg" alt="logo" />
            <div class="title">xjz Cloud Gallery</div>
          </div>
        </RouterLink>
      </a-col>
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          @click="doMenuClick"
          class="menu"
        />
      </a-col>
      <a-col flex="120px">
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <a-space wrap :size="8">
                <a-avatar>
                  <template #icon>
                    <img
                      :src="loginUserStore.loginUser.userAvatar ?? 'https://huacheng.gz-cmc.com/upload/news/image/2023/05/26/3e67c105f5ac4a38b45a2c7f0a40688f.jpeg'"
                      alt="Avatar"
                    />
                  </template>
                </a-avatar>
                {{ loginUserStore.loginUser.userName ?? 'Unnamed' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item>
                    <router-link to="/user/detail">
                      <UserOutlined />
                      Profile
                    </router-link>
                  </a-menu-item>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    Logout
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">Login</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { HomeOutlined, LogoutOutlined, UserOutlined } from '@ant-design/icons-vue'
import { computed, h, ref } from 'vue'
import { MenuProps, message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/user'
import { userLogoutUsingPost } from '@/api/userController'

const loginUserStore = useLoginUserStore()

const current = ref<string[]>(['home'])
// 菜单列表
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: 'Home',
    title: 'Home',
  },
  {
    key: '/picture/addPicture',
    label: 'Create Picture',
    title: 'Create Picture',
  },
  {
    key: '/admin/userManage',
    label: 'User Management',
    title: 'User Management',
  },
  {
    key: '/admin/pictureManage',
    label: 'Picture Management',
    title: 'Picture Management',
  },
  {
    key: '/admin/spaceManage',
    label: 'Space Management',
    title: 'Space Management',
  },
  // {
  //   key: 'others',
  //   label: h('a', { href: 'http://www.xjzai1.top', target: '_blank' }, '其他网站'),
  //   title: '其他网站',
  // },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    if (menu.key.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const items = computed<MenuProps['items']>(() => filterMenus(originItems))

const router = useRouter()

router.beforeEach((to, from, next) => {
  current.value = [to.path]
  next()
})

const doMenuClick = ({ key }: { key: string }) => {
  // ant-design中Menu组件的回调函数传来的值
  // router.push({
  //   path: key,
  // })
  router
    .push({
      path: key,
    })
    .then(() => {
      // console.log('Navigation successful')
    })
    .catch((err) => {
      // console.error('Navigation failed', err)
    })
}

// 用户注销
const doLogout = async () => {
  const res = await userLogoutUsingPost()
  // console.log(res)
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: 'Logged out',
    })
    message.success('Logout successful')
    await router.push('/user/login')
  } else {
    message.error('Logout failed, ' + res.data.message + ', ' + res.data.description)
  }
}
</script>
<style scoped>
.title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 16px;
}

.logo {
  height: 48px;
}
.menu {
  //background-image: url("@/assets/background.png");
}
</style>
