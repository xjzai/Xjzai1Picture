<template>
  <div class="space-user-analyze">
    <a-card title="User Upload Analysis">
      <v-chart :option="options" style="height: 320px" />
      <template #extra>
        <a-space>
          <a-segmented v-model:value="timeDimension" :options="timeDimensionOptions" />
          <a-input-search v-if="!spaceId" placeholder="Please enter user id" enter-button="Search user" @search="doSearch" />
        </a-space>
      </template>
    </a-card>
  </div>
</template>
<script setup lang="ts">
import VChart from "vue-echarts";
import "echarts";
import { computed, ref, watchEffect } from 'vue'
import { message } from 'ant-design-vue'
import { getSpaceUserAnalyzeUsingPost } from '@/api/spaceAnalyzeController'


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
const dataList = ref<API.SpaceUserAnalyzeResponse[]>([])
const loading = ref(true)


const fetchData = async () => {
  loading.value = true
  const res = await getSpaceUserAnalyzeUsingPost({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
    timeDimension: timeDimension.value,
    userId: userId.value,
  })
  // console.log(options.value);
  if (res.data.code === 0) {
    dataList.value = res.data.data ?? []
  } else {
    message.error('Failed to fetch data, ' + res.data.message + ', ' + res.data.description)
  }
  loading.value = false
}

const options = computed(() => {
  const periods = dataList.value.map((item) => item.period) // 时间区间
  const counts = dataList.value.map((item) => item.count) // 上传数量

  return {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: periods, name: 'Time Range' },
    yAxis: { type: 'value', name: 'Upload Count' },
    series: [
      {
        name: 'Upload Count',
        type: 'line',
        data: counts,
        smooth: true, // 平滑折线
        emphasis: {
          focus: 'series',
        },
      },
    ],
  }
})


const userId = ref<string>()
const timeDimension = ref<string>('day')
const timeDimensionOptions = [
  {
    label: 'Day',
    value: 'day',
  },
  {
    label: 'Week',
    value: 'week',
  },
  {
    label: 'Month',
    value: 'month',
  },
]

const doSearch = (value: string) => {
  userId.value = value
}



/**
 * 监听变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  fetchData()
})



</script>
<style scoped>

</style>
