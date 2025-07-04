<!-- 雷达图配置 -->
<template>
  <div>
    <el-form-item label="字体大小">
      <el-input-number
        v-model="main.activeOption.radarNameSize"
        controls-position="right"
        :min="0"
        :max="200"
      />
    </el-form-item>
    <el-form-item label="字体颜色">
      <avue-input-color v-model="main.activeOption.radarNameColor" :max="200">
      </avue-input-color>
    </el-form-item>
    <el-form-item label="区域透明度">
      <el-input-number
        v-model="main.activeOption.areaOpacity"
        controls-position="right"
        :min="0"
        :max="1"
        :step="0.1"
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
