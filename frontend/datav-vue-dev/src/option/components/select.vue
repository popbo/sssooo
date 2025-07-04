<!-- 选项卡配置 -->
<template>
  <div>
    <el-form-item label="显示全部">
      <avue-switch v-model="main.activeOption.showSelectAll"></avue-switch>
    </el-form-item>
    <el-form-item label="全部内容显示">
      <avue-input v-model="main.activeOption.selectAllText"></avue-input>
    </el-form-item>
    <el-form-item label="可搜索">
      <avue-switch v-model="main.activeOption.isFilterable"></avue-switch>
    </el-form-item>
    <el-form-item label="大小写敏感">
      <avue-switch v-model="main.activeOption.ignoreCase"></avue-switch>
    </el-form-item>
    <el-form-item label="多选">
      <avue-switch v-model="main.activeOption.isMultiple"></avue-switch>
    </el-form-item>
    <el-form-item label="默认字体颜色" :label-width="labelWidth">
      <avue-input-color
        v-model="main.activeOption.defaultFontColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="移入字体颜色" :label-width="labelWidth">
      <avue-input-color
        v-model="main.activeOption.hoverFontColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="选中字体颜色" :label-width="labelWidth">
      <avue-input-color
        v-model="main.activeOption.selectedFontColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="字体大小" :label-width="labelWidth">
      <el-input-number
        v-model="main.activeOption.fontSize"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="背景颜色" :label-width="labelWidth">
      <avue-input-color v-model="main.activeOption.bgColor"></avue-input-color>
    </el-form-item>
    <el-form-item label="背景图片">
      <div style="position: relative">
        <img
          v-if="main.activeOption.backgroundImage"
          :src="main.activeOption.backgroundImage"
          alt=""
          width="80px"
          height="100%"
        />
        <div
          v-show="main.activeOption.backgroundImage"
          class="close"
          @click="delImg"
        >
          <i class="el-icon-circle-close" style="color: red"></i>
        </div>
      </div>
      <el-input v-model="main.activeOption.backgroundImage">
        <div
          @click="main.handleOpenImg('activeOption.backgroundImage', 'border')"
          slot="append"
        >
          <i class="iconfont icon-img"></i>
        </div>
      </el-input>
    </el-form-item>
    <el-form-item label="移入背景颜色" :label-width="labelWidth">
      <avue-input-color
        v-model="main.activeOption.selectedBgColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="边框颜色" :label-width="labelWidth">
      <avue-input-color
        v-model="main.activeOption.borderColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="边框大小" :label-width="labelWidth">
      <el-input-number
        :min="0"
        :max="50"
        v-model="main.activeOption.borderSize"
        controls-position="right"
      ></el-input-number>
    </el-form-item>
    <el-collapse accordion>
      <el-collapse-item title="默认显示值设置">
        <el-form-item label="默认显示值">
          <avue-input v-model="main.activeOption.defaultShow"></avue-input>
        </el-form-item>
        <el-form-item label="是否使用第一项为默认值">
          <avue-switch v-model="main.activeOption.useFirstOption"></avue-switch>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="角标设置">
        <el-form-item label="角标颜色">
          <avue-input-color
            v-model="main.activeOption.subscriptColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="角标大小">
          <el-input-number
            v-model="main.activeOption.subscriptFontSize"
            controls-position="right"
            :min="0"
          ></el-input-number>
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
      labelWidth: '110px',
    }
  },
  inject: ['main'],
  methods: {
    delImg() {
      this.main.activeOption.backgroundImage = ''
    },
  },
}
</script>
<style lang="scss" scoped>
.close {
  position: absolute;
  top: -10px;
  left: 75px;
  font-size: 14px;
  cursor: pointer;
}
</style>
