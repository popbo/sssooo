<!-- 地图配置 -->
<template>
  <div>
    <el-form-item label="开启轮播">
      <avue-switch v-model="main.activeOption.banner"></avue-switch>
    </el-form-item>
    <template v-if="main.activeOption.banner">
      <el-form-item label="轮播时间">
        <avue-input v-model="main.activeOption.bannerTime"></avue-input>
      </el-form-item>
    </template>
    <template v-if="main.activeOption.type === 0">
      <el-form-item label="地图选择">
        <avue-select
          :dic="main.DIC.MAP"
          v-model="main.activeOption.mapData"
          placeholder="请选择地图"
        ></avue-select>
      </el-form-item>
      <el-form-item label="地图比例">
        <el-input-number
          v-model="main.activeOption.zoom"
          controls-position="right"
          :min="0"
          :max="5"
          :step="0.1"
        ></el-input-number>
      </el-form-item>
      <el-form-item label="字体大小">
        <el-input-number
          v-model="main.activeOption.fontSize"
          controls-position="right"
          :min="0"
        />
      </el-form-item>
      <el-form-item label="字体高亮颜色">
        <avue-input-color
          v-model="main.activeOption.empColor"
        ></avue-input-color>
      </el-form-item>
      <el-form-item label="字体颜色">
        <avue-input-color v-model="main.activeOption.color"></avue-input-color>
      </el-form-item>
      <el-form-item label="区域线">
        <el-input-number
          v-model="main.activeOption.borderWidth"
          controls-position="right"
          :min="0"
        />
      </el-form-item>
      <el-form-item label="区域颜色">
        <avue-input-color
          v-model="main.activeOption.areaColor"
        ></avue-input-color>
      </el-form-item>
      <el-form-item label="区域高亮颜色">
        <avue-input-color
          v-model="main.activeOption.empAreaColor"
        ></avue-input-color>
      </el-form-item>
      <el-form-item label="边框颜色">
        <avue-input-color
          v-model="main.activeOption.borderColor"
        ></avue-input-color>
      </el-form-item>
    </template>
    <template v-if="main.activeOption.type === 1">
      <el-form-item label="图片地址">
        <avue-input v-model="main.activeOption.img"></avue-input>
      </el-form-item>
      <el-form-item label="地图比例">
        <el-input-number
          v-model="main.activeOption.scale"
          controls-position="right"
          :max="300"
          :min="0"
        ></el-input-number>
      </el-form-item>
    </template>
  </div>
</template>

<script>
export default {
  inject: ['main'],
}
</script>

<style></style>
