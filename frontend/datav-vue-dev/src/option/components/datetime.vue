<!-- 实时时间配置 -->
<template>
  <div>
    <el-collapse accordion>
      <el-collapse-item title="时间设置">
        <el-form-item label="时间格式">
          <avue-select
            v-model="main.activeOption.format"
            :dic="dicOption.format"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="时间排列方式" v-show="showStatus !== 1">
          <avue-switch v-model="main.activeOption.isPortait"></avue-switch>
          <span class="details-item">{{
            main.activeOption.isPortait ? '纵向' : '横向'
          }}</span>
        </el-form-item>
        <el-form-item label="水平对齐" v-show="showStatus === 3">
          <avue-select
            v-model="main.activeOption.textAlign"
            :dic="dicOption.textAlign"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="反向排列" v-show="showStatus !== 1">
          <avue-switch v-model="main.activeOption.isReverse"></avue-switch>
        </el-form-item>
        <!-- <el-form-item label="自定义格式">
          <avue-input v-model="main.activeOption.format"> </avue-input>
        </el-form-item> -->
        <el-form-item label="字体间距">
          <el-input-number
            v-model="main.activeOption.split"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.fontSize"
            controls-position="right"
            :min="0"
            :max="200"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="字体背景">
          <avue-input-color
            v-model="main.activeOption.backgroundColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="文字粗细">
          <avue-select
            v-model="main.activeOption.fontWeight"
            :dic="dicOption.fontWeight"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color
            v-model="main.activeOption.color"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
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
  computed: {
    showStatus() {
      const format = this.main.activeOption.format
      // 排列方式
      let single = ['yyyy-MM-dd', 'MM-dd', 'hh:mm', 'hh:mm:ss', 'day']
      let combination = ['yyyy-MM-dd hh:mm', 'yyyy-MM-dd hh:mm:ss']
      if (single.includes(format)) return 1
      if (combination.includes(format) && this.main.activeOption.isPortait)
        return 3
      if (combination.includes(format)) return 2
    },
  },
}
</script>

<style>
.details-item {
  display: inline-block;
  font-size: 12px;
  color: #888;
  margin-left: 10px;
}
</style>
