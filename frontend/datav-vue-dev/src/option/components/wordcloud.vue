<!-- 字符云配置 -->
<template>
  <div>
    <el-form-item label="最小字体">
      <el-input-number
        v-model="main.activeOption.minFontSize"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="最大字体">
      <el-input-number
        v-model="main.activeOption.maxFontSize"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="间距">
      <el-input-number
        v-model="main.activeOption.split"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="旋转">
      <avue-switch v-model="main.activeOption.rotate"></avue-switch>
    </el-form-item>
  </div>
</template>

<script>
export default {
  inject: ['main'],
}
</script>

<style></style>
