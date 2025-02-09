<template>
<div style="min-height: 200px;">
  888
</div>
  <a-button type="primary"  @click="ceshi">ceshi</a-button>
</template>
<script setup>
import { healthUsingGet } from '@/api/mainController.ts'

const ceshi = () => {
  healthUsingGet().then((data)=> {
    console.log(data);
  })
}
</script>

<style scoped>

</style>
