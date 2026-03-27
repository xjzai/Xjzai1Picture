<template>
  <div class="icp">
    {{ `© ${year} ${author} ` }}<a href="http://beian.miit.gov.cn/" target="_blank">{{ record }}</a>
  </div>
</template>

<script setup>
let year = new Date().getFullYear();        // 一般都是最新的一年
let author = 'Liu Jiaxu';				// 作者名
let record = 'ICP No. 2026009013-1';			// 备案号
</script>

<style>
.icp {
  background: #efefef;
  //background-image: url("@/assets/background.png");
  padding: 16px;
  position: fixed;
  bottom: 10px;
  left: 0;
  right: 0;
  text-align: center;
  //background-color: rgba(36, 36, 44);
  //position: absolute;
  //bottom: 0;
  //right: 0;
  width: 100%;
  height: 36px;
  white-space: pre;
  //text-align: center;
  color: rgb(142, 142, 142);
  z-index: 1000;
}

.icp>a {
  color: rgb(142, 142, 142);
  text-decoration: none;
}

.icp>a:hover {
  color: rgb(255, 255, 255);
  text-decoration: none;
}
</style>
