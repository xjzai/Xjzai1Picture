<template>
  <a-modal v-model:visible="visible" title="批量删除图片" :footer="false" @cancel="closeModal">
    <a-typography-paragraph type="secondary">* 只对当前页面的图片生效</a-typography-paragraph>
    <!-- 确认按钮 -->
    <a-button type="primary" @click="handleSubmit" danger>确认删除</a-button>
    <a-button type="primary" @click="closeModal">取消删除</a-button>
  </a-modal>
</template>

<script setup lang="ts">
import { defineExpose, defineProps, ref, withDefaults } from 'vue'
import { deletePictureByBatchUsingPost } from '@/api/pictureController'
import { message } from 'ant-design-vue'

// 定义组件属性类型
interface Props {
  pictureList: API.PictureVO[]
  spaceId: number
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
const handleSubmit = async (values: any) => {
  if (!props.pictureList) {
    return
  }
  const res = await deletePictureByBatchUsingPost({
    pictureIdList: props.pictureList.map((picture) => picture.id),
    spaceId: props.spaceId,
    ...values,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success('操作成功')
    closeModal()
    props.onSuccess?.()
  } else {
    message.error('操作失败，' + res.data.message)
  }
}
</script>
