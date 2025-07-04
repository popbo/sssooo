<!-- 选项卡配置 -->
<template>
  <div>
    <!-- <el-form-item label="按钮内容">
      <el-input v-model="main.activeOption.buttonContent"/>
    </el-form-item>
    <el-form-item label="按钮字体粗细">
        <avue-select
          v-model="main.activeOption.buttonFontWeight"
          :dic="dicOption.fontWeight"
        >
        </avue-select>
    </el-form-item>
    <el-form-item label="按钮字体大小">
      <div style="display: flex;">
        <el-input-number
          v-model="main.activeOption.buttonFontSize"
          controls-position="right"
          :min="0"
          :max="200"
        />
        <span style="margin-left: 4px;">px</span>
      </div>
    </el-form-item>
    <el-form-item label="按钮背景颜色">
      <avue-input-color v-model="main.activeOption.buttonBackground"></avue-input-color>
    </el-form-item>
    <el-form-item label="按钮字体颜色">
      <avue-input-color v-model="main.activeOption.buttonColor"></avue-input-color>
    </el-form-item> -->
    <el-form-item label="边框颜色">
      <avue-input-color v-model="main.activeOption.borderColor"></avue-input-color>
    </el-form-item>
    <el-form-item label="背景颜色">
      <avue-input-color v-model="main.activeOption.inputBackground"></avue-input-color>
    </el-form-item>
    <el-form-item label="输入字体颜色">
      <avue-input-color v-model="main.activeOption.inputColor"></avue-input-color>
    </el-form-item>
    <el-form-item label="输入字体大小">
      <div  style="display: flex;">
        <el-input-number
          v-model="main.activeOption.inputFontSize"
          controls-position="right"
          :min="0"
          :max="200"
        />
        <span style="margin-left: 4px;">px</span>
      </div>
    </el-form-item>
    <el-form-item label="输入字体粗细">
        <avue-select
          v-model="main.activeOption.inputFontWeight"
          :dic="dicOption.fontWeight"
        >
        </avue-select>
    </el-form-item>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  data() {
    return {
      dicOption: dicOption,
      methodList: [
        {
          label: 'GET',
          value: 'GET',
        },
        {
          label: 'POST',
          value: 'POST',
        },
      ],
      directionList: [
        {
          label: '横向',
          value: 1,
        },
        {
          label: '纵向',
          value: 2,
        },
      ],
      authList: [
        {
          label: 'no-auth',
          value: 'no-auth',
        },
        {
          label: 'Basic Auth',
          value: 'Basic Auth',
        },
      ],
    }
  },
  mixins: [optionsGetFont],
  inject: ['main'],
  methods: {
  },
  computed: {},
}
</script>
<style scoped>
.font-d {
  display: flex;
}
.font-a {
  width: 50%;
}
.font-b {
  width: 50%;
}
.spacing {
  display: flex;
  width: 60%;
}
.spacing-text {
  display: flex;
  margin-bottom: 15px;
}
</style>
