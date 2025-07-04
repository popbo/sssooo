<!-- 图片框配置 -->
<template>
  <div>
    <el-form-item label="背景色">
      <avue-input-color
        v-model="main.activeOption.backgroundColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="图片透明度">
      <el-input-number
        v-model="main.activeOption.opacity"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="图片地址">
      <img
        v-if="main.activeObj.data"
        :src="main.activeObj.data"
        alt=""
        width="100%"
      />
      <el-input v-model="main.activeObj.data">
        <div
          @click="main.handleOpenImg('activeObj.data', 'border')"
          slot="append"
        >
          <i class="iconfont icon-img"></i>
        </div>
      </el-input>
    </el-form-item>
  </div>
</template>

<script>
export default {
  inject: ['main'],
}
</script>

<style></style>
