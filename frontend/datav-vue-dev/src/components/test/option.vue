<!-- 自定义配置 -->
<template>
  <div>
    <el-form-item label="字体大小">
      <el-input-number
        v-model="main.activeOption.fontSize"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
  </div>
</template>

<script>
export default {
  inject: ['main'],
}
</script>

<style></style>
