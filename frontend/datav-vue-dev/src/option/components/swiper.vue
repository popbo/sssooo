<!-- 轮播配置 -->
<template>
  <div>
    <el-form-item label="类型">
      <avue-radio
        v-model="main.activeOption.type"
        :dic="dicOption.swiperType"
      ></avue-radio>
    </el-form-item>
    <el-form-item label="轮播时间">
      <avue-input v-model="main.activeOption.interval"></avue-input>
    </el-form-item>
    <el-form-item label="显示选择器">
      <avue-switch v-model="showIndicator"></avue-switch>
    </el-form-item>
    <el-form-item label="选择器样式" v-show="showIndicator">
      <avue-select
        v-model="main.activeOption.indicator"
        :dic="dicOption.swiperIndicator"
      >
      </avue-select>
    </el-form-item>
    <el-form-item label="图片透明度">
      <el-input-number
        v-model="main.activeOption.opacity"
        controls-position="right"
        :min="0"
        :max="100"
      />
    </el-form-item>
    <el-form-item label="显示标题">
      <avue-switch v-model="main.activeOption.showTitle"></avue-switch>
    </el-form-item>
    <el-form-item label="显示标题背景" v-show="main.activeOption.showTitle">
      <avue-switch
        v-model="main.activeOption.showTitleBackground"
      ></avue-switch>
    </el-form-item>
    <el-form-item label="标题位置" v-show="main.activeOption.showTitle">
      <avue-select
        v-model="main.activeOption.titlePosition"
        :dic="dicOption.textAlign"
      >
      </avue-select>
    </el-form-item>
    <el-form-item label="标题字体颜色" v-show="main.activeOption.showTitle">
      <avue-input-color
        v-model="main.activeOption.textColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="标题字体设置" v-show="main.activeOption.showTitle">
      <el-input-number
        v-model="main.activeOption.titleFontSize"
        controls-position="right"
        :min="0"
        :max="200"
      />
      <avue-select
        v-model="main.activeOption.titleTextFamily"
        :dic="allFontFamilyArr"
      >
      </avue-select>
    </el-form-item>
    <el-form-item
      label="标题背景颜色"
      v-show="
        main.activeOption.showTitle && main.activeOption.showTitleBackground
      "
    >
      <avue-input-color
        v-model="main.activeOption.titleBackgroundColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item
      label="标题背景透明度"
      v-show="
        main.activeOption.showTitle && main.activeOption.showTitleBackground
      "
    >
      <el-input-number
        v-model="main.activeOption.titleOpcity"
        controls-position="right"
        :min="0"
        :max="100"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="视频自动播放">
      <avue-switch v-model="main.activeOption.autoplay"></avue-switch>
    </el-form-item>
    <el-form-item label="视频控制台">
      <avue-switch v-model="main.activeOption.controls"></avue-switch>
    </el-form-item>
    <el-form-item label="视频循环">
      <avue-switch v-model="main.activeOption.loop"></avue-switch>
    </el-form-item>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  mixins: [optionsGetFont],
  data() {
    return {
      dicOption: dicOption,
      // showIndicator: true,
    }
  },
  inject: ['main'],
  created() {
    console.log(this.main)
  },
  // watch: {
  //   showIndicator(newValue) {
  //     if (!newValue) {
  //       this.main.activeOption.indicator = 'none'
  //     }
  //   },
  // },
  computed: {
    showIndicator: {
      get() {
        return this.main.activeOption.showIndicator
      },
      set(newVal) {
        this.main.activeOption.showIndicator = newVal
        if (!newVal) {
          this.main.activeOption.indicator = 'none'
        } else {
          this.main.activeOption.indicator = 'inside'
        }
      },
    },
  },
}
</script>
