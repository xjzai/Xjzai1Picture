<template>
  <div id="globalSider">
    <a-menu mode="inline" v-model:selectedKeys="current" :items="menuItems" @click="doMenuClick" class="menu" />
  </div>
</template>
<script lang="ts" setup>
// 菜单列表
import { useRouter } from 'vue-router'
import { computed, h, onMounted, ref, watchEffect } from 'vue'
import { PictureOutlined, UserOutlined, TeamOutlined } from '@ant-design/icons-vue'
import { SPACE_TYPE_ENUM } from '@/constants/space'
import { message } from 'ant-design-vue'
import { listMyTeamSpaceUsingPost } from '@/api/spaceUserController'
import { useLoginUserStore } from '@/stores/user'

const fixedMenuItems = [
  {
    key: '/',
    label: 'Public Gallery',
    icon: () => h(PictureOutlined),
  },
  {
    key: '/space/mySpace',
    label: 'My Spaces',
    icon: () => h(UserOutlined),
  },
  {
    key: '/space/addSpace?type=' + SPACE_TYPE_ENUM.TEAM,
    label: 'Create Team',
    icon: () => h(TeamOutlined),
  },
]

const router = useRouter()

// 当前选中菜单
const current = ref<string[]>([])
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, failure) => {
  current.value = [to.path]
})

// 路由跳转事件
const doMenuClick = ({ key }: { key: string }) => {
  router.push(key)
}

const teamSpaceList = ref<API.SpaceUserVO[]>([])
const menuItems = computed(() => {
  // 没有团队空间，只展示固定菜单
  if (teamSpaceList.value.length < 1) {
    return fixedMenuItems;
  }
  // 展示团队空间分组
  const teamSpaceSubMenus = teamSpaceList.value.map((spaceUser) => {
    const space = spaceUser.space
    return {
      key: '/space/' + spaceUser.spaceId,
      label: space?.spaceName,
    }
  })
  const teamSpaceMenuGroup = {
    type: 'group',
    label: 'My Teams',
    key: 'teamSpace',
    children: teamSpaceSubMenus,
  }
  return [...fixedMenuItems, teamSpaceMenuGroup]
})

// 加载团队空间列表
const fetchTeamSpaceList = async () => {
  const res = await listMyTeamSpaceUsingPost()
  if (res.data.code === 0 && res.data.data) {
    teamSpaceList.value = res.data.data
  } else {
    message.error('Failed to load my team spaces, ' + res.data.message + ', ' + res.data.description)
  }
}

/**
 * 监听变量，改变时触发数据的重新加载
 */
const loginUserStore = useLoginUserStore()

watchEffect(() => {
  // 登录才加载
  if (loginUserStore.loginUser.id) {
    fetchTeamSpaceList()
  }
})
// onMounted(() => {
//   fetchTeamSpaceList();
// })


</script>

<style scoped>
.menu{
  //background-image: url("@/assets/background.png");
}
</style>
