import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import AddPicturePage from '@/pages/picture/AddPicturePage.vue'
import PictureManagePage from '@/pages/admin/PictureManagePage.vue'
import PictureDetailPage from '@/pages/picture/PictureDetailPage.vue'
import AddPictureBatchPage from '@/pages/picture/AddPictureBatchPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: '主页',
      component: HomePage,
    },
    {
      path: '/user/login',
      name: '用户登录',
      component: UserLoginPage,
    },
    {
      path: '/user/register',
      name: '用户注册',
      component: UserRegisterPage,
    },
    {
      path: '/admin/userManage',
      name: '用户管理',
      component: UserManagePage,
    },
    {
      path: '/admin/pictureManage',
      name: '图片管理',
      component: PictureManagePage,
    },
    {
      path: '/picture/addPicture',
      name: '图片上传',
      component: AddPicturePage,
    },
    {
      path: '/picture/addPicture/batch',
      name: '批量创建图片',
      component: AddPictureBatchPage,
    },
    {
      path: '/picture/:id',
      name: '图片详情',
      component: PictureDetailPage,
      props: true,
    }
  ],
})

export default router
