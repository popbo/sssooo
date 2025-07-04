<template>
  <div class="queryBox" :style="{
    '--buttonFontWeight':option.buttonFontWeight,
    '--buttonFontSize':option.buttonFontSize +'px',
    '--buttonBackground':option.buttonBackground,
    '--buttonColor':option.buttonColor,
    '--borderColor':option.borderColor,
    '--inputBackground':option.inputBackground,
    '--inputColor':option.inputColor,
    '--inputFontSize':option.inputFontSize + 'px',
    '--inputFontWeight':option.inputFontWeight,
  }">
  <div class="input-queryBox">
    <el-input v-model="inputQueryBox" placeholder="" @blur="queryButton">
    </el-input>
    <!-- <div class="search-input" :style="{fontSize:option.inputFontSize + 'px'}">
      <i class="el-icon-search" @click="queryButton"></i>
    </div> -->
  </div>
  <!-- <div class="button-q">
    <el-button class="button-queryBox" @click="queryButton">{{option.buttonContent}}</el-button>
  </div> -->
  </div>
</template>
<script>
import create from '../../create'
import components from '@/components/'
import common from '@/config'
import { getFunction } from '@/utils/utils.min.js'
export default create({
  name: 'queryBox',
  inject: ['contain', 'container'],
  provide() {
    return {
      contain: this.contain,
      container: this.container,
    }
  },
  data() {
    return {
      common: common,
      getFunction: getFunction,
      inputQueryBox:''
    }
  },
  props: {
    option: Object,
    component: Object,
  },
  methods: {
    queryButton() {
      this.updateClick({value:this.inputQueryBox,label:this.inputQueryBox,termParams:'LIKE'}, 'clickFormatter')
    },
  },
  components: components,
})
</script>
<style lang="scss" scoped>
  .queryBox{
    display: flex;
    height: 100%;
    width: 100%;
    .input-queryBox{
      display: flex;
      flex: 1;
      height: 100%;
      border: 1px solid var(--borderColor) !important;
      background-color: var(--inputBackground) !important;
      color: var(--inputColor) !important;
      overflow: hidden;
      .search-input{
        position: relative;
        display:flex;
        justify-content:center;
        align-items:center;
        height: 100%;
        background-color: var(--inputBackground) !important;
      }
    }
    /deep/.el-input{
      height: 100% !important;
    }
    /deep/.el-input__inner{
      color: var(--inputColor) !important;
      font-size: var(--inputFontSize) !important;
      font-weight: var(--inputFontWeight);
      height: 100% !important;
      background-color: var(--inputBackground) !important;
    }
    /deep/.el-input__inner, .el-textarea__inner, .el-input-group__append, .el-input-group__prepend{
      border: 0px solid var(--borderColor) !important;
    }
    .button-q{
      height: 100%;
    }
    .button-queryBox{
      margin-left: 4px;
      height: 100%;
      min-width: 130px;
    }
    /deep/.el-button{
      font-size: var(--buttonFontSize);
      font-weight: var(--buttonFontWeight);
      background-color: var(--buttonBackground);
      color: var(--buttonColor);
      border: 0px;
      // overflow: hidden;
    }
  }
</style>
