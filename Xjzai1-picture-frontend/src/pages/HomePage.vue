<template>
  <div id="homePage">
    <!-- 搜索框 -->
    <div class="search-bar">
      <a-input-search
        placeholder="Search in the large photo collection"
        v-model:value="searchParams.searchText"
        enter-button="Search"
        size="large"
        @search="doSearch"
      />
    </div>

    <!-- 分类 + 标签 -->
    <a-tabs v-model:activeKey="selectedCategory" @change="doSearch">
      <a-tab-pane key="all" tab="All" />
      <a-tab-pane v-for="category in categoryList" :key="category" :tab="category" />
    </a-tabs>
    <div class="tag-bar">
      <span style="margin-right: 8px">Tags:</span>
      <a-space :size="[0, 8]" wrap>
        <a-checkable-tag
          v-for="(tag, index) in tagList"
          :key="tag"
          v-model:checked="selectedTagList[index]"
          @change="doSearch"
        >
          {{ tag }}
        </a-checkable-tag>
      </a-space>
    </div>

    <!-- 图片列表 -->
    <PictureList :dataList="dataList" :loading="loading" />
    <a-pagination
      style="text-align: right"
      v-model:current="searchParams.current"
      v-model:pageSize="searchParams.pageSize"
      :total="total"
      @change="onPageChange"
    />
    <!--    <div style="margin-top: 10px">-->
    <!--      <a-list-->
    <!--        :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"-->
    <!--        :data-source="dataList"-->
    <!--        :pagination="pagination"-->
    <!--        :loading="loading"-->
    <!--      >-->
    <!--        <template #renderItem="{ item: picture }">-->
    <!--          <a-list-item style="padding: 0">-->
    <!--            &lt;!&ndash; 单张图片 &ndash;&gt;-->
    <!--            <a-card hoverable @click="doClickPicture(picture)">-->
    <!--              <template #cover>-->
    <!--                <img-->
    <!--                  style="height: 180px; object-fit: cover"-->
    <!--                  :alt="picture.name"-->
    <!--                  :src="picture.thumbnailUrl ?? picture.url"-->
    <!--                  loading="lazy"-->
    <!--                />-->
    <!--              </template>-->
    <!--              <a-card-meta :title="picture.name">-->
    <!--                <template #description>-->
    <!--                  <a-flex>-->
    <!--                    <a-tag color="green">-->
    <!--                      {{ picture.category ?? '默认' }}-->
    <!--                    </a-tag>-->
    <!--                    <a-tag v-for="tag in picture.tags" :key="tag">-->
    <!--                      {{ tag }}-->
    <!--                    </a-tag>-->
    <!--                  </a-flex>-->
    <!--                </template>-->
    <!--              </a-card-meta>-->
    <!--            </a-card>-->
    <!--          </a-list-item>-->
    <!--        </template>-->
    <!--      </a-list>-->
    <!--    </div>-->
  </div>
</template>

<script setup lang="ts">
// 数据
import { computed, onMounted, reactive, ref } from 'vue'
import {
  listPictureTagCategoryUsingGet,
  listPictureVoByPageUsingPost,
  listPictureVoByPageWithCacheUsingPost,
} from '@/api/pictureController'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import PictureList from '@/components/PictureList.vue'

const dataList = ref([])
const total = ref(0)
const loading = ref(true)

// 搜索条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'create_time',
  sortOrder: 'descend',
})

const onPageChange = (page, pageSize) => {
  searchParams.current = page;
  searchParams.pageSize = pageSize;
  fetchData();
}

// 分页参数
// const pagination = computed(() => {
//   return {
//     current: searchParams.current ?? 1,
//     pageSize: searchParams.pageSize ?? 10,
//     total: total.value,
//     // 切换页号时，会修改搜索参数并获取数据
//     onChange: (page, pageSize) => {
//       searchParams.current = page
//       searchParams.pageSize = pageSize
//       fetchData()
//     },
//   }
// })

// 获取数据
const fetchData = async () => {
  loading.value = true
  // 转换搜索参数
  const params = {
    ...searchParams,
    tags: [],
  }
  if (selectedCategory.value !== 'all') {
    params.category = selectedCategory.value
  }
  selectedTagList.value.forEach((useTag, index) => {
    if (useTag) {
      params.tags.push(tagList.value[index])
    }
  })
  // console.log(params)
  const res = await listPictureVoByPageUsingPost(params)
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('Failed to fetch data, ' + res.data.message + ', ' + res.data.description)
  }
  loading.value = false
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
  getTagCategoryOptions()
})

const doSearch = () => {
  // 重置搜索条件
  searchParams.current = 1
  fetchData()
}

const categoryList = ref<string[]>([])
const selectedCategory = ref<string>('all')
const tagList = ref<string[]>([])
const selectedTagList = ref<string[]>([])

// 获取标签和分类选项
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    // 转换成下拉选项组件接受的格式
    categoryList.value = res.data.data.categoryList ?? []
    tagList.value = res.data.data.tagList ?? []
  } else {
    message.error('Failed to load categories and tags, ' + res.data.message + ', ' + res.data.description)
  }
}

const router = useRouter()
// 跳转至图片详情
// const doClickPicture = (picture) => {
//   router.push({
//     path: `/picture/${picture.id}`,
//   })
// }

</script>

<style scoped>
#homePage .search-bar {
  max-width: 480px;
  margin: 0 auto 16px;
}
</style>
