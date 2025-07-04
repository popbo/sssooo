<!-- 复选框配置 -->
<template>
  <div style="position: relative">
    <custom-option
      v-if="customOptionShow"
      @customChange="changeCustom"
      @handleData="handleData"
      @selectedChange="handleSelected"
      :activeComponent="currentComponent"
      :dataList.sync="dataList"
      :radioOption="main.activeOption"
    />
    <el-form-item label="扩展方向">
      <avue-select
        :dic="dicOption.lineDirection"
        v-model="main.activeOption.extenDirection"
      ></avue-select>
    </el-form-item>
    <el-form-item label="自定义选项" v-if="main.activeObj.dataType === 0">
      <el-button type="primary" @click="changeCustom(true)"
        >自定义选项</el-button
      >
    </el-form-item>
    <el-form-item label="字体大小">
      <el-input-number
        v-model="main.activeOption.fontSize"
        style="width: 90px"
        controls-position="right"
        :min="0"
      ></el-input-number>
      <i style="margin-left: 5px">px</i>
    </el-form-item>
    <el-form-item label="字体粗细">
      <avue-select
        v-model="main.activeOption.fontWeight"
        :dic="dicOption.fontWeight"
      >
      </avue-select>
    </el-form-item>
    <el-form-item label="字体对齐">
      <avue-select
        v-model="main.activeOption.textAlign"
        :dic="dicOption.textAlign"
      >
      </avue-select>
    </el-form-item>
    <el-form-item label="选中颜色">
      <div style="display: flex; align-items: center">
        <el-color-picker
          v-model="main.activeOption.selectedColor"
          size="mini"
          style="margin-right: 10px"
        />
        <avue-input
          v-model="main.activeOption.selectedColor"
          disabled
          style="width: 90px"
        />
      </div>
    </el-form-item>
    <el-form-item label="选项背景颜色">
      <div style="display: flex; align-items: center">
        <el-color-picker
          v-model="main.activeOption.optionBgColor"
          size="mini"
          style="margin-right: 10px"
        />
        <avue-input
          v-model="main.activeOption.optionBgColor"
          disabled
          style="width: 90px"
        />
      </div>
    </el-form-item>
    <el-form-item label="按钮边框颜色">
      <div style="display: flex; align-items: center">
        <el-color-picker
          v-model="main.activeOption.btnBorderColor"
          size="mini"
          style="margin-right: 10px"
        />
        <avue-input
          v-model="main.activeOption.btnBorderColor"
          disabled
          style="width: 90px"
        />
      </div>
    </el-form-item>
    <el-form-item label="选项字体颜色">
      <div style="display: flex; align-items: center">
        <el-color-picker
          v-model="main.activeOption.optionColor"
          size="mini"
          style="margin-right: 10px"
        />
        <avue-input
          v-model="main.activeOption.optionColor"
          disabled
          style="width: 90px"
        />
      </div>
    </el-form-item>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
import CustomOption from '@/components/custom-option'
export default {
  data() {
    return {
      dicOption: dicOption,
      customOptionShow: false,
      currentComponent: '',
    }
  },
  inject: ['main'],
  components: { 'custom-option': CustomOption },
  methods: {
    changeCustom(val) {
      this.customOptionShow = val
    },
    handleSelected(val) {
      // console.log('接收到单选框最新选中值', val)
      this.main.activeOption.selected = val
    },
  },
  computed: {
    dataList: {
      get() {
        return this.main.activeObj.data
      },
      set(val) {
        this.main.activeObj.data = val
      },
    },
  },
  created() {
    this.currentComponent = this.main.activeComponent.name
  },
  watch: {
    'main.activeObj.index'() {
      this.customOptionShow = false
    },
  },
}
</script>

<style lang="scss" scoped></style>
