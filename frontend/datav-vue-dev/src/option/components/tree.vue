<!-- 选项卡配置 -->
<template>
  <div>
    <el-form-item label="多选">
      <avue-switch v-model="main.activeOption.nodeCheak"></avue-switch>
    </el-form-item>
    <el-form-item label="搜索">
      <avue-switch v-model="main.activeOption.showSelect"></avue-switch>
    </el-form-item>
    <el-form-item label="大小写敏感">
      <avue-switch v-model="main.activeOption.ignoreCase"></avue-switch>
    </el-form-item>
    <el-form-item label="单一根节点">
      <avue-switch v-model="main.activeOption.onlyRoot"></avue-switch>
    </el-form-item>
    <el-form-item label="默认展开层级">
      <avue-input v-model="main.activeOption.treeNum"></avue-input>
    </el-form-item>
    <!-- <el-form-item label="默认值">
      <el-input-number v-model="main.activeOption.treeNum" :min="0"></el-input-number>
    </el-form-item> -->
    <el-form-item label="默认字体颜色">
      <avue-input-color v-model="main.activeOption.color"></avue-input-color>
    </el-form-item>
    <el-form-item label="字体大小">
      <el-input-number
        v-model="main.activeOption.fontSize"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="文字粗细">
      <avue-select
        v-model="main.activeOption.fontWeight"
        :dic="dicOption.fontWeight"
      >
      </avue-select>
    </el-form-item>
    <el-form-item label="字体位置">
      <el-select v-model="main.activeOption.flexAlign">
        <el-option
          v-for="(item, index) in dicOption.textAlign"
          :key="index"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="背景颜色">
      <avue-input-color v-model="main.activeOption.bgColor"></avue-input-color>
    </el-form-item>
    <el-form-item label="边框颜色">
      <avue-input-color
        v-model="main.activeOption.borderColor"
      ></avue-input-color>
    </el-form-item>

    <el-form-item label="边框粗细">
      <div style="display: flex">
        <el-input-number
          v-model="main.activeOption.borderWidth"
          controls-position="right"
          min="0"
        ></el-input-number>
        <i style="margin-left: 5px">px</i>
      </div>
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
