<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'
export default create({
  name: 'newPictorialBar',
  data() {
    return {
      clearLoop:null
    }
  },
  methods: {
    updateChart() {
      let optionData = this.deepClone(this.dataChart)
      // let symbol = () => {
      //   if (this.option.geometricOrImage === 'geometric') {
      //     return this.option.symbol
      //   } else {
      //     return this.validatenull(this.option.symbol)
      //       ? ''
      //       : 'image://' + this.option.symbol
      //   }
      // }
      console.log('optionData-->', this.option.xAxisSplitLineColor)
      const labelSetting = {
        show: this.vaildData(this.option.labelShow, false),
        position: 'top',
        textStyle: {
          //数值样式
          fontSize: this.option.labelShowFontSize ?? 14,
          color: this.option.labelShowColor || '#333',
          fontWeight: this.option.labelShowFontWeight || 500,
          fontFamily: 'SourceHanSansCN-Regular',
        },
      }
      const option = {
        title: {
          // text: 'Vehicles in X City',
        },
        legend: {
          itemWidth: this.option.legendWidth ?? 14,
          itemHeight: this.option.legendHeight ?? 14,
          show: this.vaildData(this.option.legend, false),
          orient: this.option.legendOrient || 'vertical',
          x: this.option.legendPostion || 'right',
          top: 0,
          right: this.x2,
          align: 'left', // 图例的文字在左边还是右边
          textStyle: {
            fontSize: this.option.legendFontSize ?? 12,
            color: '#fff',
          },
          data: (() => {
            return (optionData.series || []).map((ele, index) => {
              return {
                name: ele.name,
                textStyle: this.ishasprop(
                  !this.switchTheme,
                  {
                    color: this.getColor(index, true),
                  },
                  {}
                ),
              }
            })
          })(),
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
          },
          backgroundColor: this.option.tipBackgroundColor,
          borderColor: this.option.tipBorderColor,
          borderWidth: this.option.tipBorder,
          textStyle: {
            fontSize: this.option.tipFontSize ?? 14,
            color: this.option.tipColor || '#fff',
          },
          show:this.option.tipShow,
        },
        // grid: {
        //   height: Number(this.option.barSplit) * 10,
        //   left: this.option.gridX || 100,
        //   top: this.option.gridY || 60,
        //   right: this.option.gridX2 || 60,
        //   bottom: this.option.gridY2 || 60,
        // },
        grid: (() => {
          // let obj = this.option.category === 'x_category' ? { width: Number(this.option.barSplit) * 10 } : {height: Number(this.option.barSplit) * 10}
          return Object.assign(
            {
              left: this.option.gridX ?? 100,
              top: this.option.gridY ?? 60,
              right: this.option.gridX2 ?? 60,
              bottom: this.option.gridY2 ?? 60,
            }
            // obj
          )
        })(),
        yAxis: {
          show: !this.option.yAxisShow,
          type: this.option.category === 'y_category' ? 'category' : 'value',
          data: optionData.categories || [],
          axisPointer: {
            label: {
              show: true,
              margin: 30,
            },
          },
          axisLabel: {
            fontSize: this.setPx(this.option.yAxisLabelFontSize ?? 14),
            color: this.option.nameColor || '#aac6e2',
          },
          axisLine: {
            show: !this.option.yAxisLineShow,
            lineStyle: {
              color: this.option.lineColor || '#65686f',
            },
          },
          axisTick: {
            show: true,
            length: 8,
            lineStyle: {
              opacity: 0.3,
              width: 1.5,
            },
          },
          splitLine: {
            show: this.option.yAxisSplitLineShow,
            lineStyle: {
              type: this.option.yAxisSplitLineType,
              width: this.option.yAxisSplitLineWidth ?? 0,
              color: this.option.yAxisSplitLineColor ?? '#fff',
            },
          },
        },
        xAxis: {
          show: !this.option.xAxisShow,
          type: this.option.category === 'x_category' ? 'category' : 'value',
          data: optionData.categories || [],
          axisLabel: {
            fontSize: this.setPx(this.option.xAxisLabelFontSize ?? 14),
            color: this.option.nameColor || '#aac6e2',
          },
          axisLine: {
            show: !this.option.xAxisLineShow,
            lineStyle: {
              color: this.option.lineColor || '#65686f',
            },
          },
          axisTick: {
            // 坐标轴X轴刻度线的设置
            show: true,
            length: 8,
            lineStyle: {
              opacity: 0.3,
              width: 1.5,
            },
          },
          splitLine: {
            show: this.option.xAxisSplitLineShow,
            lineStyle: {
              type: this.option.xAxisSplitLineType,
              width: this.option.xAxisSplitLineWidth || 1,
              color: this.option.xAxisSplitLineColor || '#65686f',
            },
          },
        },
        series: (() => {
          let getbarGap = this.option.barSplit - 100
          const list = (optionData.series || []).map((ele, index) => {
            return Object.assign(ele, {
              type: 'pictorialBar',
              symbol:
                this.option.geometricOrImage === 'geometric'
                  ? this.option.symbol
                  : 'image://' + this.option.imgSymbol,
              symbolRepeat: true,
              symbolClip: true,
              label: labelSetting,
              symbolSize: this.option.symbolSize || [20, 5],
              symbolMargin: this.option.symbolSplit,
              barGap: `${getbarGap}%`,
              z: 100,
              itemStyle: {
                color: this.getColor(index),
              },
            })
          })
          return list
        })(),
      }
      // console.log('option--->9999', option.xAxis.splitLine)
      this.myChart.resize()
      this.myChart.setOption(option, true)
      this.clearLoop && this.clearLoop()
      if(this.option.tipCarousel){
        this.clearLoop = (tools.loopShowTooltip(this.myChart, option, {loopSeries: true,interval: this.option.tipCarouselTime})).clearLoop;
      }
    },
  },
})
</script>

<style scoped lang="scss"></style>
