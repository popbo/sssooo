<template>
  <div class="position-box">
    <div class="tipBox">
      <span>按住空格拖动画布 </span>
      <span>{{ contain.config.width }}×{{ contain.config.height }}</span>
      <span>
        <el-input
          class="scale-input"
          size="mini"
          v-model="scaleNumber"
          @change="handleScaleChange"
        ></el-input>
      </span>
      <span>%</span>
    </div>
    <div style="flex: 1 1 0"></div>
    <!-- <el-form  label-position="left" size="mini" :inline="true">
      <el-form-item label="X位置">
        <avue-input-number v-model="activeObj.left" v-enter-number class="my_input"></avue-input-number>
      </el-form-item>
      <el-form-item label="Y位置">
        <avue-input-number v-model="activeObj.top" v-enter-number class="my_input"></avue-input-number>
      </el-form-item>
      <el-form-item label="宽度">
        <avue-input-number v-model="activeComponent.width" v-enter-number class="my_input"></avue-input-number>
      </el-form-item>
      <el-form-item label="高度">
        <avue-input-number v-model="activeComponent.height" v-enter-number class="my_input"></avue-input-number>
      </el-form-item>
    </el-form> -->
    <div class="dataBox">
      <div class="dataBox_cell">
        <span>X位置:</span>
        <avue-input-number
          v-model="activeObj.left"
          v-enter-number
          class="my_input"
          size="mini"
        ></avue-input-number>
      </div>
      <div class="dataBox_cell">
        <span>Y位置:</span>
        <avue-input-number
          v-model="activeObj.top"
          v-enter-number
          class="my_input"
          size="mini"
        ></avue-input-number>
      </div>
      <div class="dataBox_cell">
        <span>宽度:</span>
        <avue-input-number
          v-model="activeComponent.width"
          v-enter-number
          class="my_input"
          size="mini"
        ></avue-input-number>
      </div>
      <div class="dataBox_cell">
        <span>高度:</span>
        <avue-input-number
          v-model="activeComponent.height"
          v-enter-number
          class="my_input"
          size="mini"
        ></avue-input-number>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'position',
  inject: ['contain'],
  props: {
    wscale: Number,
    activeObj: {
      type: Object,
      default: function () {
        return {}
      },
    },
    activeComponent: {
      type: Object,
      default: function () {
        return {}
      },
    },
  },
  data() {
    return {
      scaleNumber: '',
    }
  },
  watch:{
    wscale:{
      handler(val){
        this.scaleNumber = (val*100).toFixed()
      },
    },
    deep:true,
  },
  created() {
  },
  methods: {
    handleScaleChange(inputVal) {
      const result = inputVal / 100
      this.$emit('update:wscale', result)
    },
  },
}
</script>

<style scoped lang="scss">
@import '~@/styles/buildVariables.scss';
.position-box {
  width: 100%;
  height: 48px;
  background-color: $bgc1;
  position: absolute;
  bottom: 5px;
  display: flex;
  padding-left: 40px;
  .scale-input {
    width: 60px;
    margin-left: 10px;
  }
  .tipBox {
    display: flex;
    height: 48px;
    line-height: 48px;
    color: #fff;
  }
  .my_input {
    width: 7vw;
  }
  .dataBox {
    display: flex;
    height: 48px;
    align-items: center;
    .dataBox_cell {
      display: flex;
      height: 48px;
      align-items: center;
      span {
        color: #fff;
        font-size: 12px;
        padding-left: 1vw;
      }
    }
  }
}
</style>
