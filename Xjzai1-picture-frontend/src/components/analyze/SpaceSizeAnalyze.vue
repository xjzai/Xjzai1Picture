<template>
  <div class="space-size-analyze">
    <a-card title="Space Picture Size Analysis">
      <v-chart :option="options" style="height: 320px; max-width: 100%" :loading="loading" />
    </a-card>
  </div>

</template>
<script setup lang="ts">
import VChart from "vue-echarts";
import "echarts";
import { computed, ref, watchEffect } from 'vue'
import { getSpaceSizeAnalyzeUsingPost } from '@/api/spaceAnalyzeController'
import { message } from 'ant-design-vue'


interface Props {
  queryAll?: boolean
  queryPublic?: boolean
  spaceId?: number
}

const props = withDefaults(defineProps<Props>(), {
  queryAll: false,
  queryPublic: false,
})

// 图表数据
const dataList = ref<API.SpaceSizeAnalyzeResponse[]>([])
const loading = ref(true)

const fetchData = async () => {
  loading.value = true
  const res = await getSpaceSizeAnalyzeUsingPost({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
  })
  if (res.data.code === 0) {
    dataList.value = res.data.data ?? []
  } else {
    message.error('Failed to fetch data, ' + res.data.message + ', ' + res.data.description)
  }
  loading.value = false
}

const options = computed(() => {
  const pieData = dataList.value.map((item) => ({
    name: item.sizeRange,
    value: item.count,
  }))

  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)',
    },
    legend: {
      top: 'bottom',
    },
    series: [
      {
        name: 'Picture Size',
        type: 'pie',
        radius: '50%',
        data: pieData,
      },
    ],
  }
})

/**
 * 监听变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  fetchData()
})


</script>
<style scoped>

</style>
