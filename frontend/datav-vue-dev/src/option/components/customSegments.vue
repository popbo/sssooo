<!--折线图配置 -->
<template>
  <div>
    <el-form-item label="显示样式">
      <avue-select
        v-model="main.activeOption.defaultStyle"
        :dic="dicOption.sharpStyle"
      >
      </avue-select>
    </el-form-item>
    <el-form-item label="线条颜色">
      <avue-input-color
        type="textarea"
        v-model="main.activeOption.bodyColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="线条粗细">
      <el-input-number
        v-model="main.activeOption.borderWidth"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="选点状态">
      <avue-switch v-model="main.activeOption.selectStatus"> </avue-switch>
    </el-form-item>
    <el-collapse accordion>
      <el-collapse-item title="节点设置">
        <el-form-item label="首节点样式">
          <avue-select
            v-model="main.activeOption.startNodeType"
            :dic="dicOption.customSegmentsNodeStyle"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="首节点大小">
          <el-input-number
            :min="0"
            v-model="main.activeOption.startNodeSize"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="首节点颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.startNodeColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="尾节点样式">
          <avue-select
            v-model="main.activeOption.endNodeType"
            :dic="dicOption.customSegmentsNodeStyle"
          ></avue-select>
        </el-form-item>
        <el-form-item label="尾节点大小">
          <el-input-number
            :min="0"
            v-model="main.activeOption.endNodeSize"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="尾节点颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.endNodeColor"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="拐点设置">
        <el-form-item label="拐点样式">
          <avue-select
            v-model="main.activeOption.midNodeType"
            :dic="dicOption.customSegmentsMidNodeStyle"
          ></avue-select>
        </el-form-item>
        <el-form-item label="拐点大小">
          <el-input-number
            v-model="main.activeOption.midNodeSize"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="拐点颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.midNodeColor"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
export default {
  inject: ['main'],
  data() {
    return {
      dicOption: dicOption,
      min: 1,
    }
  },
  created() {
    console.log('mainmainmain=>', this.main)
  },
  methods: {},
  watch: {
    route: {
      handler() {},
    },
  },
  beforeDestroy() {},
}
</script>

<style lang="scss" scoped>
.build {
  .sharpLine {
    .el-input-number {
      width: 80%;
    }
  }
}
</style>
