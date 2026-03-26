<template>
  <div class="picture-search-form">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="Keyword" name="searchText">
        <a-input
          v-model:value="searchParams.searchText"
          placeholder="Search by name and introduction"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="Category" name="category">
        <a-auto-complete
          v-model:value="searchParams.category"
          style="min-width: 180px"
          :options="categoryOptions"
          placeholder="Please enter category"
          allowClear
        />
      </a-form-item>
      <a-form-item label="Tags" name="tags">
        <a-select
          v-model:value="searchParams.tags"
          style="min-width: 180px"
          :options="tagOptions"
          mode="tags"
          placeholder="Please enter tags"
          allowClear
        />
      </a-form-item>
      <a-form-item label="Date" name="">
        <a-range-picker
          style="width: 400px"
          show-time
          v-model:value="dateRange"
          :placeholder="['Start date', 'End date']"
          format="YYYY/MM/DD HH:mm:ss"
          :presets="rangePresets"
          @change="onRangeChange"
        />
      </a-form-item>
      <a-form-item label="Name" name="name">
        <a-input v-model:value="searchParams.name" placeholder="Please enter name" allow-clear />
      </a-form-item>
      <a-form-item label="Introduction" name="introduction">
        <a-input
          v-model:value="searchParams.introduction"
          placeholder="Please enter introduction"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="Width" name="picWidth">
        <a-input-number v-model:value="searchParams.picWidth" />
      </a-form-item>
      <a-form-item label="Height" name="picHeight">
        <a-input-number v-model:value="searchParams.picHeight" />
      </a-form-item>
      <a-form-item label="Format" name="picFormat">
        <a-input v-model:value="searchParams.picFormat" placeholder="Please enter format" allow-clear />
      </a-form-item>
      <a-form-item label="Color">
        <color-picker format="hex" @pureColorChange="onColorChange" />
      </a-form-item>
      <a-form-item>
        <a-space>
          <a-button type="primary" html-type="submit" style="width: 96px">Search</a-button>
          <a-button html-type="reset" @click="doClear">Reset</a-button>
        </a-space>
      </a-form-item>

    </a-form>
  </div>

</template>
<script lang="ts" setup>
import { message } from 'ant-design-vue'
import PictureQueryRequest = API.PictureQueryRequest
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { listPictureTagCategoryUsingGet } from '@/api/pictureController'
import { ColorPicker } from 'vue3-colorpicker'
import "vue3-colorpicker/style.css"

interface Props {
  onSearch?: (searchParams: PictureQueryRequest) => void
}

const props = defineProps<Props>()

// 搜索条件
const searchParams = reactive<API.PictureQueryRequest>({})

// 获取数据
const doSearch = () => {
  // console.log(searchParams);
  props.onSearch?.(searchParams)
}

const dateRange = ref<[]>([])

/**
 * 日期范围更改时触发
 * @param dates
 * @param dateStrings
 */
const onRangeChange = (dates: any[], dateStrings: string[]) => {
  if (dates.length < 2) {
    searchParams.startEditTime = undefined
    searchParams.endEditTime = undefined
  } else {
    searchParams.startEditTime = dates[0].toDate()
    searchParams.endEditTime = dates[1].toDate()
    // console.log(searchParams.startEditTime)
  }
}

const rangePresets = ref([
  { label: 'Last 7 days', value: [dayjs().add(-7, 'd'), dayjs()] },
  { label: 'Last 14 days', value: [dayjs().add(-14, 'd'), dayjs()] },
  { label: 'Last 30 days', value: [dayjs().add(-30, 'd'), dayjs()] },
  { label: 'Last 90 days', value: [dayjs().add(-90, 'd'), dayjs()] },
])


const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

// 获取标签和分类选项
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    // 转换成下拉选项组件接受的格式
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('Failed to load options, ' + res.data.message + ', ' + res.data.description)
  }
}

// 清理
const doClear = () => {
  // 先恢复默认颜色
  // searchParams.pictureColor = "#000000"
  // 取消所有对象的值
  Object.keys(searchParams).forEach((key) => {
    searchParams[key] = undefined
  })
  dateRange.value = []
  props.onSearch?.(searchParams)
}


onMounted(() => {
  getTagCategoryOptions()
})

const onColorChange = (color: string) => {
  searchParams.pictureColor = color;
  // console.log(searchParams.pictureColor);

}


</script>

<style scoped>
.picture-search-form .ant-form-item {
  margin-top: 16px;
}

</style>
