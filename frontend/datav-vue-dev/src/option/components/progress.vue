<!-- 进度条配置 -->
<template>
  <div>
    <el-form-item label="类型">
      <avue-radio v-model="main.activeOption.type" :dic="dicOption.line">
      </avue-radio>
    </el-form-item>
    <el-form-item label="间距">
      <el-input-number
        controls-position="right"
        v-model="main.activeOption.split"
        :max="200"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="边框颜色">
      <avue-input-color
        v-model="main.activeOption.borderColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="边框大小">
      <el-input-number
        controls-position="right"
        v-model="main.activeOption.strokeWidth"
        :max="50"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="字体大小">
      <el-input-number
        v-model="main.activeOption.fontSize"
        controls-position="right"
        :max="200"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="字体颜色">
      <avue-input-color v-model="main.activeOption.color"></avue-input-color>
    </el-form-item>
    <el-form-item label="文字粗细">
      <avue-select
        v-model="main.activeOption.FontWeight"
        :dic="dicOption.fontWeight"
      >
      </avue-select>
    </el-form-item>
    <el-form-item label="前缀字体大小">
      <el-input-number
        controls-position="right"
        v-model="main.activeOption.suffixFontSize"
        :max="200"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="前缀字体颜色">
      <avue-input-color
        v-model="main.activeOption.suffixColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="前缀文字粗细">
      <avue-select
        v-model="main.activeOption.suffixFontWeight"
        :dic="dicOption.fontWeight"
      >
      </avue-select>
    </el-form-item>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
export default {
  data() {
    return {
      dicOption: dicOption,
    }
  },
  inject: ['main'],
}
</script>

<style></style>
