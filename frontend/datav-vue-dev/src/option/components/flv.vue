<template>
  <div>
    <el-form-item label="地址">
      <avue-input v-model="main.activeObj.data[0].url"></avue-input>
    </el-form-item>
    <el-form-item label="分屏预览">
      <avue-switch v-model="main.activeOption.isSplitScreen"></avue-switch>
    </el-form-item>
  </div>
</template>

<script>
export default {
  inject: ["main"]
}
</script>

<style>
</style>