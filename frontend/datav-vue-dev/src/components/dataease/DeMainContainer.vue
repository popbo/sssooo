<template>
  <el-main class="ms-main-container">
    <slot />
  </el-main>
</template>

<script>
export default {
  name: 'DeMainContainer',
}
</script>

<style scoped>
.ms-main-container {
  padding: 10px;
  height: calc(100vh - 227px);
  /* background-color: var(--SiderBG, #fff); */
}
</style>
