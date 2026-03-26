<template>
  <a-modal v-model:visible="visible" title="Confirm Delete" :footer="false" @cancel="closeModal">
    <!-- 确认按钮 -->
    <a-button type="primary" @click="doDelete" danger>Confirm Delete</a-button>
    <a-button type="primary" @click="closeModal">Cancel</a-button>
  </a-modal>
</template>

<script setup lang="ts">
import { defineExpose, defineProps, ref, withDefaults } from 'vue'
import { deletePictureByBatchUsingPost, deletePictureUsingPost } from '@/api/pictureController'
import { message } from 'ant-design-vue'

// 定义组件属性类型
interface Props {
  obj: any
  onSuccess: () => void
}

// 给组件指定初始值
const props = withDefaults(defineProps<Props>(), {})

// 控制弹窗可见性
const visible = ref(false)

// 打开弹窗
const openModal = () => {
  visible.value = true
}

// 关闭弹窗
const closeModal = () => {
  visible.value = false
}

// 暴露函数给父组件
defineExpose({
  openModal,
})

// 提交表单时处理

const doDelete = async () => {
  const id = props.obj.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('Deleted successfully')
    closeModal()
    // 让外层刷新
    props.onSuccess?.()
  } else {
    message.error('Delete failed')
  }
}
</script>
