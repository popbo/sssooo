<!-- 选项卡配置 -->
<template>
  <div>
    <el-form-item label="水位图形状">
      <avue-select
        :dic="directionList"
        v-model="main.activeOption.liquidFillType"
      ></avue-select>
    </el-form-item>
    <el-form-item label="边框颜色">
      <avue-input-color
        v-model="main.activeOption.borderColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="边框粗细">
      <el-input-number
        v-model="main.activeOption.borderWidth"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="字体颜色">
      <avue-input-color
        v-model="main.activeOption.fontColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="字体大小">
      <el-input-number
        v-model="main.activeOption.fontSize"
        controls-position="right"
        :min="1"
      />
    </el-form-item>
    <el-form-item label="波浪数量">
      <el-input-number
        v-model="main.activeOption.xNumber"
        controls-position="right"
        :min="1"
      />
    </el-form-item>
    <el-form-item label="水波数量">
      <el-input-number
        v-model="main.activeOption.yNumber"
        controls-position="right"
        :min="1"
      />
    </el-form-item>
    <el-form-item label="波浪高度">
      <el-input-number
        v-model="main.activeOption.height"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="波浪透明度">
      <el-input-number
        v-model="main.activeOption.opacity"
        controls-position="right"
        :min="0"
        :max="1"
        :step="0.1"
      />
    </el-form-item>
    <el-collapse>
      <!-- <el-collapse-item title="自定义配色">
        <avue-crud
          :option="colorOption"
          :data="main.activeOption.barColor"
          @row-save="rowSave"
          @row-del="rowDel"
          @row-update="rowUpdate"
        ></avue-crud>
      </el-collapse-item> -->
      <el-collapse-item title="波浪颜色" name="1">
        <el-form-item label="是否渐变">
          <el-switch v-model="main.activeOption.isGradient"></el-switch>
        </el-form-item>
        <template v-if="main.activeOption.isGradient">
          <el-form-item label="颜色一">
            <avue-input-color
              v-model="main.activeOption.bgColor1"
            ></avue-input-color>
          </el-form-item>
          <el-form-item label="颜色二">
            <avue-input-color
              v-model="main.activeOption.bgColor2"
            ></avue-input-color>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="颜色">
            <avue-input-color
              v-model="main.activeOption.bgColor"
            ></avue-input-color>
          </el-form-item>
        </template>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import { dicOption, colorOption } from '@/option/config'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  data() {
    return {
      dicOption: dicOption,
      colorOption: colorOption,
      directionList: [
        {
          label: '圆形水位图',
          value: 'circle',
        },
        {
          label: '矩形水位图',
          value: 'rect',
        },
        {
          label: '圆角水位图',
          value: 'roundRect',
        },
      ],
      sliderOption: {
        minValue: 0,
        maxValue: 1,
        step: 0.1,
      },
    }
  },
  mixins: [optionsGetFont],
  inject: ['main'],
  methods: {
    rowSave(row, done) {
      this.main.activeOption.barColor.push(row)
      done()
    },
    rowDel(row, index) {
      if (this.main.activeOption.barColor.length > 1) {
        this.main.activeOption.barColor.splice(index, 1)
      } else {
        this.$message.error('请至少保留一项')
      }
    },
    rowUpdate(row, index, done) {
      this.main.activeOption.barColor.splice(index, 1, row)
      done()
    },
  },
  computed: {},
}
</script>
<style lang="scss" scoped>
/deep/ .avue-crud__header {
  background-color: transparent;
}
</style>
