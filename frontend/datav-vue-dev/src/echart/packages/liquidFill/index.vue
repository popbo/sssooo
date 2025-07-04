<template>
  <div
    :class="b()"
    :style="styleSizeName"
    @click="handleClick"
    @dblclick="handleDbClick"
  >
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>
<script>
import create from '../../create'
import components from '@/components/'
import common from '@/config'
import { getFunction } from '@/utils/utils.min.js'
import * as echarts from 'echarts'
import 'echarts-liquidfill' // 引入水球图的组件
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
      let value = this.dataChart.value || 0
      let yNumber = this.option.yNumber
      let FillData = []
      let wavePercent = `${100 / this.option.xNumber}%`
      for (let i = 0; i < yNumber; i++) {
        FillData.push({
          value,
          itemStyle: {
            normal: {
              color: this.waveColor() || '#00BAFF',
            },
          },
        })
      }
      console.log('FillData', FillData)
      const option = {
        // backgroundColor: '#0F224C',
        title: {
          text: Math.round(this.dataChart.value * 100) + '%',
          x: 'center',
          y: 'center',
          textStyle: {
            fontSize: this.option.fontSize ?? 16,
            color: this.option.fontColor || '#fff',
          },
        },
        series: [
          {
            type: 'liquidFill',
            radius: '90%',
            shape: this.option.liquidFillType,
            center: ['50%', '50%'],
            amplitude: `${this.option.height}%`,
            waveLength: wavePercent,
            data: FillData, // data个数代表波浪数
            backgroundStyle: {
              color: 'transparent',
            },
            label: {
              show: false,
              normal: {
                show: false,
              },
            },
            itemStyle: {
              opacity: this.option.opacity, // 波浪颜色透明度
            },
            outline: {
              show: this.option.borderWidth <= 0 ? false : true,
              borderDistance: 8,
              itemStyle: {
                borderWidth: this.option.borderWidth ?? 1,
                borderColor: this.option.borderColor || '#7ec699',
              },
            },
          },
        ],
      }
      myChart.resize()
      myChart.setOption(option, true)
    },
    waveColor() {
      if (this.option.isGradient) {
        if (!this.option.bgColor1 || !this.option.bgColor2) {
          return '#00BAFF'
        } else if (!this.option.bgColor1 && !this.option.bgColor2) {
          return '#00BAFF'
        } else {
          return new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            {
              offset: 0,
              color: this.option.bgColor1,
            },
            {
              offset: 1,
              color: this.option.bgColor2,
            },
          ])
        }
      } else {
        return this.option.bgColor
      }
    },
    handleClick() {
      this.updateClick(
        {
          value: this.dataChart.value,
        },
        'clickFormatter'
      )
      this.clickFormatter &&
        this.clickFormatter(
          {
            data: this.dataChart,
          },
          this.getItemRefs()
        )
    },
    handleDbClick() {
      this.updateClick(
        {
          value: this.dataChart.value,
        },
        'dblClickFormatter'
      )
      this.clickFormatter &&
        this.clickFormatter(
          {
            data: this.dataChart,
          },
          this.getItemRefs()
        )
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
