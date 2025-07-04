<template>
  <div>
    <el-form-item label="数据总值">
      <el-input-number
        v-model="main.activeOption.maxValue"
        controls-position="right"
        :min="0"
      />
    </el-form-item>

    <el-form-item label="外部背景颜色" :label-width="labelWidth">
      <avue-input-color
        v-model="main.activeOption.backgroundColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="进度条高度" :label-width="labelWidth">
      <el-input-number
        v-model="main.activeOption.barWidth"
        controls-position="right"
        :min="0"
      />
    </el-form-item>

    <el-form-item label="默认字体颜色" :label-width="labelWidth">
      <avue-input-color v-model="main.activeOption.color"></avue-input-color>
    </el-form-item>
    <el-form-item label="字体大小" :label-width="labelWidth">
      <el-input-number
        v-model="main.activeOption.fontSize"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="字体系列" :label-width="labelWidth">
      <el-select v-model="main.activeOption.fontFamily">
        <el-option
          v-for="(item, index) in allFontFamilyArr"
          :key="index"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="提示框是否显示" :label-width="labelWidth">
      <el-switch v-model="main.activeOption.tipsIsShow"></el-switch>
    </el-form-item>
    <template v-if="main.activeOption.tipsIsShow">
      <el-form-item label="提示框边框颜色" :label-width="labelWidth">
        <avue-input-color
          v-model="main.activeOption.borderColor"
        ></avue-input-color>
      </el-form-item>
      <el-form-item label="提示框边框宽度" :label-width="labelWidth">
        <el-input-number
          v-model="main.activeOption.borderWidth"
          controls-position="right"
          :min="0"
        />
      </el-form-item>
      <el-form-item label="提示框内边距" :label-width="labelWidth">
        <el-input-number
          v-model="main.activeOption.tipsPadding"
          controls-position="right"
          :min="0"
        />
      </el-form-item>
    </template>
    <el-collapse>
        <el-collapse-item title="内部进度条颜色" name="1">
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
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  inject: ['main'],
  mixins: [optionsGetFont],
  data() {
    return {
      labelWidth: '120px',
    }
  },

  created() {},

  methods: {},
}
</script>

<style scoped lang="scss">
.radius_input {
  width: 120px !important;
}
.radius_span {
  padding-left: 10px;
  color: #fff;
}
</style>
