<!-- 刻度盘配置 -->
<template>
  <div>
    <el-form-item label="刻度线粗度(像素)">
      <el-input-number
        v-model="main.activeOption.lineSize"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="显示刻度值">
      <avue-switch v-model="main.activeOption.axisLabelShow"></avue-switch>
    </el-form-item>
    <el-form-item label="刻度字号">
      <el-input-number
        v-model="main.activeOption.axisLabelFontSize"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <!-- <el-form-item label="指标名称字号">
       <el-input-number
              v-model="main.activeOption.nameFontSize"
              controls-position="right"
              :min="0"
            />
    </el-form-item> -->
    <el-form-item label="指标字号">
      <el-input-number
        v-model="main.activeOption.valueFontSize"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="字体系列">
      <el-select v-model="main.activeOption.fontFamily">
        <el-option
          v-for="(item, index) in allFontFamilyArr"
          :key="index"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="最小值">
      <el-input-number
        v-model="main.activeOption.min"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="最大值">
      <el-input-number
        v-model="main.activeOption.max"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
     <el-form-item label="指针颜色">
      <avue-input-color
        type="textarea"
        v-model="main.activeOption.pointerColor"
      ></avue-input-color>
    </el-form-item>
    <el-collapse>
      <el-collapse-item title="轴线自定义配色">
        <avue-crud
          :option="axisLineColorOption"
          :data="main.activeOption.axisLineColor"
          @row-save="rowSaveWithAxisLineColor"
          @row-del="rowDelWithAxisLineColor"
          @row-update="rowUpdateWithAxisLineColor"
        ></avue-crud>
      </el-collapse-item>
      <el-collapse-item title="刻度线自定义配色">
        <avue-crud
          :option="colorOption2"
          :data="main.activeOption.axisTickColor"
          @row-save="rowSaveWithAxisTickColor"
          @row-del="rowDelWithAxisTickColor"
          @row-update="rowUpdateWithAxisTickColor"
        ></avue-crud>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import { axisLineColorOption, colorOption2 } from '@/option/config'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  mixins: [optionsGetFont],
  inject: ['main'],
  data() {
    return {
      axisLineColorOption: axisLineColorOption,
      colorOption2: colorOption2,
    }
  },
  methods: {
    rowSaveWithAxisLineColor(row, done) {
      this.main.activeOption.axisLineColor.push(row)
      done()
    },
    rowDelWithAxisLineColor(row, index) {
      this.main.activeOption.axisLineColor.splice(index, 1)
    },
    rowUpdateWithAxisLineColor(row, index, done) {
      this.main.activeOption.axisLineColor.splice(index, 1, row)
      done()
    },
    rowSaveWithAxisTickColor(row, done) {
      this.main.activeOption.axisTickColor.push(row)
      done()
    },
    rowDelWithAxisTickColor(row, index) {
      this.main.activeOption.axisTickColor.splice(index, 1)
    },
    rowUpdateWithAxisTickColor(row, index, done) {
      this.main.activeOption.axisTickColor.splice(index, 1, row)
      done()
    },
  },
}
</script>

<style></style>
