<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'
export default create({
  name: 'ZebraProgressBar',
  computed: {},
  created() {
    // _this = this
  },
  methods: {
    updateChart() {
      
      const optionData = this.deepClone(this.dataChart)
      const option = {
        grid: {
          left: '2%',
          right: '2%',
          bottom: '2%',
          top: '2%',
          containLabel: true,
        },
        xAxis: {
            max: this.option.maxValue,
            splitLine: {
                show: false
            },
            axisLine: {
                show: false
            },
            axisLabel: {
                show: false
            },
            axisTick: {
                show: false
            },
        },
        yAxis: [{
                axisTick:'none',
                axisLine:'none',
                offset:'27',
                axisLabel: {
                        textStyle: {
                            color: '#ffffff',
                            fontSize:'16',
                        }
                    },
                data: []
            }, {
                axisTick:'none',
                axisLine:'none',
                axisLabel: {
                        textStyle: {
                            color: '#ffffff',
                            fontSize:'16',
                        }
                    },
                data: []
            },{
               
                axisLine:{
                  lineStyle:{
                    color:'rgba(0,0,0,0)'
                  }
                },
                data: [],
        }],
        series: [
        {
            // 内
            type: "bar",
            yAxisIndex: 0,
            barWidth: this.option.barWidth,
            silent: true,
            itemStyle: {
              color: this.bgcolorIsForeGradient
            },
            data: [optionData],
            z: 2,
            animationEasing: "elasticOut"
        },
        {
            name: '白框',
            type: 'bar',
            yAxisIndex: 1,
            barGap: '-100%',
            data: [this.option.maxValue-0.5],
            barWidth: this.option.barWidth+10,
            itemStyle: {
                normal: {
                    color: 'transparent',
                    barBorderRadius: 5,
                }
            },
            z: 1
        }, {
            name: "外框",
            type: "bar",
            yAxisIndex: 2,
            barGap: "-100%", // 设置外框粗细
            data: [this.option.maxValue],
            barWidth: this.option.barWidth+15,
            itemStyle: {
                normal: {
                  color:'transparent',
                  barBorderColor: this.option.borderColor,
                  barBorderRadius: 5,
                  barBorderWidth: this.option.borderWidth
                }
            },
            z: 3
        },
        {
            // 分隔 背景的分隔
            type: "pictorialBar",
            itemStyle: {
                normal: {
                  color: this.option.backgroundColor
                }
            },
            symbolRepeat: "fixed",
            symbolMargin: this.option.symbolMargin,
            symbol: "rect",
            symbolClip: true,
            symbolSize: [this.option.symbolSizeX, this.option.barWidth],
            symbolPosition: "start",
            symbolOffset: [0, 0],
            // symbolBoundingData: this.total,
            data: [this.option.maxValue],
            z: 2,
            animationEasing: "elasticOut",
        },
        {
            // 分隔  做数据上的分割
            type: "pictorialBar",
            itemStyle: {
                normal: {
                    color: "#07314a"
                }
            },
            symbolRepeat: "fixed",
            symbolMargin: this.option.symbolMargin,
            symbol: "rect",
            symbolClip: true,
            symbolSize: [this.option.symbolSizeX, this.option.barWidth],
            symbolPosition: "start",
            symbolOffset: [0, 0],
            symbolBoundingData: this.total,
            data: [optionData.value],
            z: 3,
            animationEasing: "elasticOut",


        },
        
         {
            type: 'scatter',
            name: '条形',
            symbol: 'roundRect',
            symbolSize: [7,this.option.barWidth-10],
            symbolOffset: [3, 0],
            symbolKeepAspect: true,
            itemStyle: {
                normal: {
                    color: this.option.borderColor
                }
            },
            data: [this.option.maxValue],
        }

    ],
      }
      this.myChart.resize()
      this.myChart.setOption(option, true)
    },
    bgcolorIsForeGradient() {
      if (this.option.isGradient) {
        return {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 1,
                y2: 0,
                colorStops: [{
                    offset: 0,
                    color: this.option.foreBgColor1 // 0% 处的颜色
                }, {
                    offset: 1,
                    color: this.option.foreBgColor2 // 100% 处的颜色
                }],
                globalCoord: false // 缺省为 false
            } //底色
      } else {
        return this.option.foreBgColor
      }
    },
  },
})
</script>
