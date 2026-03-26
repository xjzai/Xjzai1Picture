import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import AddPicturePage from '@/pages/picture/AddPicturePage.vue'
import PictureManagePage from '@/pages/admin/PictureManagePage.vue'
import PictureDetailPage from '@/pages/picture/PictureDetailPage.vue'
import AddPictureBatchPage from '@/pages/picture/AddPictureBatchPage.vue'
import SpaceManagePage from '@/pages/admin/SpaceManagePage.vue'
import AddSpacePage from '@/pages/space/AddSpacePage.vue'
import MySpacePage from '@/pages/space/MySpacePage.vue'
import SpaceDetailPage from '@/pages/space/SpaceDetailPage.vue'
import SearchPicturePage from '@/pages/picture/SearchPicturePage.vue'
import SpaceAnalyzePage from '@/pages/space/SpaceAnalyzePage.vue'
import SpaceUserManagePage from '@/pages/admin/SpaceUserManagePage.vue'
import UserUpdatePage from '@/pages/user/UserUpdatePage.vue'
import UserDetailPage from '@/pages/user/UserDetailPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: HomePage,
    },
    {
      path: '/user/login',
      name: 'User Login',
      component: UserLoginPage,
    },
    {
      path: '/user/register',
      name: 'User Registration',
      component: UserRegisterPage,
    },
    {
      path: '/user/update',
      name: 'Update Account Info',
      component: UserUpdatePage,
    },
    {
      path: '/user/detail',
      name: 'Personal Info',
      component: UserDetailPage,
    },
    {
      path: '/admin/userManage',
      name: 'User Management',
      component: UserManagePage,
    },
    {
      path: '/admin/pictureManage',
      name: 'Picture Management',
      component: PictureManagePage,
    },
    {
      path: '/admin/spaceManage',
      name: 'Space Management',
      component: SpaceManagePage,
    },
    {
      path: '/picture/addPicture',
      name: 'Upload Picture',
      component: AddPicturePage,
    },
    {
      path: '/space/addSpace',
      name: 'Create Space',
      component: AddSpacePage,
    },
    {
      path: '/picture/addPicture/batch',
      name: 'Batch Create Pictures',
      component: AddPictureBatchPage,
    },
    {
      path: '/picture/:id/:spaceId',
      name: 'Picture Detail',
      component: PictureDetailPage,
      props: true,
    },
    {
      path: '/space/:id',
      name: 'Space Detail',
      component: SpaceDetailPage,
      props: true,
    },
    {
      path: '/space/mySpace',
      name: 'My Spaces',
      component: MySpacePage,
    },
    {
      path: '/picture/searchPicture',
      name: 'Picture Search',
      component: SearchPicturePage,
    },
    {
      path: '/space/analyze',
      name: 'Space Analytics',
      component: SpaceAnalyzePage,
    },
    {
      path: '/space/spaceUserManage/:id',
      name: 'Space Member Management',
      component: SpaceUserManagePage,
      props: true,
    },

  ],
})

export default router
