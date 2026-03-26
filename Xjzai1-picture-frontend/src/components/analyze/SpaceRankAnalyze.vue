<template>
  <div class="space-rank-analyze">
    <a-card title="Space Usage Ranking">
      <v-chart :option="options" style="height: 320px" />
    </a-card>
  </div>
</template>
<script setup lang="ts">
import VChart from "vue-echarts";
import "echarts";
import { computed, ref, watchEffect } from 'vue'
import { getSpaceRankAnalyzeUsingPost } from '@/api/spaceAnalyzeController'
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
const dataList = ref<API.SpaceRankAnalyzeResponse[]>([])
const loading = ref(true)

const fetchData = async () => {
  loading.value = true
  const res = await getSpaceRankAnalyzeUsingPost({
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
  const spaceNames = dataList.value.map((item) => item.spaceName)
  const usageData = dataList.value.map((item) => (item.totalSize / (1024 * 1024)).toFixed(2)) // 转为 MB

  return {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: spaceNames,
    },
    yAxis: {
      type: 'value',
      name: 'Space Usage (MB)',
    },
    series: [
      {
        name: 'Space Usage (MB)',
        type: 'bar',
        data: usageData,
        itemStyle: {
          color: '#5470C6', // 自定义柱状图颜色
        },
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
