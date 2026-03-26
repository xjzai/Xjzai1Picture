<template>
  <a-modal v-model:visible="visible" title="Share Picture" :footer="false" @cancel="closeModal">
    <h4>Copy Share Link</h4>
    <a-typography-link copyable>
      <!-- todo 分享页面自己写一个，不然别人没有权限查看分享的页面-->
      {{ link }}
    </a-typography-link>
    <div style="margin-bottom: 16px" />
    <h4>Scan QR Code to View on Mobile</h4>
    <a-qrcode :value="link" />
  </a-modal>
</template>

<script setup lang="ts">
import { defineProps, ref, withDefaults } from 'vue'

/**
 * 定义组件属性类型
 */
interface Props {
  title: string
  link: string
}

/**
 * 给组件指定初始值
 */
const props = withDefaults(defineProps<Props>(), {
  title: () => 'Share',
  link: () => 'https://laoyujianli.com/share/yupi',
})

// 是否可见
const visible = ref(false)

// 打开弹窗
const openModal = () => {
  visible.value = true
}

// 关闭弹窗
const closeModal = () => {
  visible.value = false
}

import { defineExpose } from 'vue'

// 暴露函数给父组件
defineExpose({
  openModal,
})
</script>
