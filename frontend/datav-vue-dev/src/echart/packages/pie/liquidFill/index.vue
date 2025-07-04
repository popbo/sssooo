<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>
<script>
import create from '../../create'
import components from '@/components/'
import common from '@/config'
import { getFunction } from '@/utils/utils.min.js'
import * as echarts from 'echarts';
import 'echarts-liquidfill'; // 引入水球图的组件
export default create({
  name: 'liquidFill',
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
    }
  },
  props: {
    option: Object,
    component: Object,
  },
  methods: {
    updateChart() {
      let myChart = echarts.init(this.$refs[this.id])
      let value =  this.dataChart.value || 0;
      let number = this.option.number
      let FillData = []
      for(let i = 0;i < number; i++){
        FillData.push(value)
      }
      const option = {
        // backgroundColor: '#0F224C',
        series: [
            {
              type: 'liquidFill',
              radius: '99%',
              shape: this.option.liquidFillType,
              center: ['50%', '50%'],
              color: [
                  {
                      type: 'linear',
                      x: 0,
                      y: 0,
                      x2: 0,
                      y2: 1,
                      colorStops: [
                          {
                              offset: 0,
                              color: 'red',
                          },
                          {
                              offset: 1,
                              color: '#2ca3e2',
                          },
                      ],
                      globalCoord: false,
                  },
                ],
                data:FillData, // data个数代表波浪数
                backgroundStyle: {
                    borderWidth: 1,
                    color: 'transparent',
                },
                label:{
                  normal: {
                    textStyle: {
                      fontSize: 16,
                      color: this.option.fontColor || '#fff',
                    },
                  },
                },
                outline: {
                    show: this.option.borderWidth<=0?false:true,
                    borderDistance: 0,
                    itemStyle: {
                        borderWidth: this.option.borderWidth || 1,
                        borderColor: this.option.borderColor || '#fff',
                    },
                },
            },
        ],
      }
      myChart.resize()
      myChart.setOption(option, true)
    },
  },
  components: components,
  beforeDestroy() {},
})
</script>
<style lang="scss" scoped>
/deep/.el-switch__core:after {
  content: attr(data-attr);
  background-color: var(--closeKrtMsgBorderColor) !important;
  display: inline-block;
  color: var(--closeTitleColor);
  font-size: var(--closeTitleFont);
  font-family: var(--closeTitleFamily);
  text-align: center;
  line-height: 22px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 22px !important;
  height: 22px !important;
  top: 1px !important;
}

/deep/.el-switch.is-checked .el-switch__core:after {
  content: attr(data-attr1);
  color: var(--openTitleColor);
  font-size: var(--openTitleFont);
  font-family: var(--openTitleFamily);
  background-color: var(--openKrtMsgBorderColor) !important;
}
/deep/ .el-switch.is-checked .el-switch__core::after {
  margin-left: -22.5px;
}
/deep/ .el-switch__core {
  background-color: var(--inactiveColor) !important;
  border: var(--closeBorderNumber) solid var(--closeBorderColor) !important;
  height: 26px !important;
  border-radius: 25px;
}
/deep/.el-switch.is-checked .el-switch__core {
  background-color: var(--openColor) !important;
  border: var(--openBorderNumber) solid var(--openBorderColor) !important;
}
.switchListBox-longitudinal {
  /deep/.el-switch__core:after {
    transform: rotate(90deg);
  }
}
.switchListBox {
  width: 100%;
  height: 100%;
  display: flex;
  // flex-direction: row;
  // flex-wrap: wrap;
  // justify-content: space-between;
  align-content: flex-start;
  .switchListBox-content {
    position: relative;
    text-align: center;
    align-items: center;
  }
  &-title {
    color: #fff;
    text-align: center;
  }
  &-switch {
    transform: rotate(-90deg) translateX(-30%);
    // height: 52px!important;
  }
  .switchListBox-title {
    margin-top: 42px;
  }
}
.switchListBox-transverse {
  display: flex;
  height: 30px;
  .switchListBox-switch {
    transform: rotate(0deg);
    // height: 52px!important;
  }
  .switchListBox-title {
    position: relative;
    height: 30px;
    line-height: 30px;
    left: 7px;
    margin-top: 0;
  }
}
</style>
