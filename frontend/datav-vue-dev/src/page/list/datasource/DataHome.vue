<template>
  <el-col style="height: 100%;">
    <el-row style="height: 100%;" class="custom-position">
      请从左侧选择数据源
    </el-row>
  </el-col>
</template>

<script>
export default {
  name: 'DataHome'
}
</script>

<style scoped>
.custom-position {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  flex-flow: row nowrap;
  color: #9ea6b2;
}
</style>
