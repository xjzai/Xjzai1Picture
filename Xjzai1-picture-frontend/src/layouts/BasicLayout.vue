<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout>
        <a-layout-sider
          v-if="loginUserStore.loginUser.id"
          class="sider"
          width="200"
          breakpoint="lg"
          collapsed-width="0"
        >
          <GlobalSider />
        </a-layout-sider>
        <a-layout-content class="content">
          <router-view />
        </a-layout-content>
      </a-layout>
      <a-layout-footer class="footer">
        <a href="http://www.xjzai1.fun/" target="_blank">xjzai1个人网站 by xjzai1</a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>
<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalSider from '@/components/GlobalSider.vue'
import { useLoginUserStore } from '@/stores/user'

const loginUserStore = useLoginUserStore()
</script>
<style scoped>
#basicLayout .header {
  padding-inline: 20px;
  margin-bottom: 1px;
  color: unset;
  background: white;
}

#basicLayout .content {
  background: linear-gradient(to right, #fefefe, #fff);
  margin-bottom: 28px;
  padding: 28px;
}

#basicLayout .footer {
  background: #efefef;
  padding: 16px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}

#basicLayout .sider {
  background: #fff;
  padding-top: 20px;
  border-right: 0.5px solid #eee;
  width: 200px;
}

#basicLayout :deep(.ant-menu-root) {
  border-bottom: none !important;
  border-inline-end: none !important;
}
</style>
