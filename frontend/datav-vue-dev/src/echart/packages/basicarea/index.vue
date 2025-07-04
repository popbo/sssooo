<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'

export default create({
  name: 'basicarea',
  computed: {
    x2() {
      return this.option.gridX2 ?? 70
    },
  },
  // watch: {
  //   'option.xAxisName'(newVal) {
  //     let width = this.option.xAxisNameSize
  //     if (newVal) {
  //       const needWidth = newVal.length * width
  //       if (needWidth > 60) {
  //         this.option.gridX2 = needWidth + 15
  //       }
  //     }
  //   },
  // },
  methods: {
    updateChart() {
      const optionData = this.deepClone(this.dataChart)
      let colorList = [];
      if(this.option.relatyBarColor){
        colorList.push(this.option.relatyBarColor[0].color1)
      }
      if(this.option.calculateBarColor){
        colorList.push(this.option.calculateBarColor[0].color1)
      }
      const option = {
        title: this.ishasprop(
          this.option.titleShow,
          {
            text: this.option.title,
            subtext: this.option.subtext || '',
            textStyle: {
              color: this.option.titleColor || '#333',
              fontSize: this.option.titleFontSize ?? 16,
            },
            left: this.option.titlePostion || 'auto',
            subtextStyle: {
              color: this.option.subTitleColor || '#aaa',
              fontSize: this.option.subTitleFontSize ?? 14,
            },
          },
          {}
        ),
        tooltip: (() => {
          return Object.assign(
            (() => {
              if (this.formatter) {
                return {
                  formatter: name => {
                    return this.formatter(name, this.dataChart)
                  },
                }
              }
              return {}
            })(),
            {
              backgroundColor: this.option.tipBackgroundColor,
              borderColor: this.option.tipBorderColor,
              borderWidth: this.option.tipBorder,
              trigger: 'axis',
              textStyle: {
                fontSize: this.option.tipFontSize,
                color: this.option.tipColor ?? '#fff',
              },
              show:this.option.tipShow,
            }
          )
        })(),
        grid: {
          left: this.option.gridX ?? 20,
          top: this.option.gridY ?? 60,
          right: this.x2 ?? 70,
          bottom: this.option.gridY2 ?? 60,
        },
        legend: {
          itemWidth: this.option.legendWidth ?? 25,
          itemHeight: this.option.legendHeight ?? 14,
          show: this.vaildData(this.option.legend, false),
          orient: this.option.legendOrient || 'horizontal',
          x: this.option.legendPostion || 'right',
          top: 0,
          right: this.x2,
          textStyle: {
            fontSize: this.option.legendFontSize ?? 12,
            fontFamily: this.option.legendFontFamily,
          },
          data: (() => {
            return (optionData.series || []).map((ele, index) => {
              return {
                name: ele.name,
                textStyle: {
                  // borderColor: this.getColor(index, true),//写错位置了
                  color: colorList[index] || "#fff",
                },
              }
            })
          })(),
        },
        xAxis: {
          //设置了两边的留白
          boundaryGap: false,
          type: this.option.category ? 'value' : 'category',
          name: this.option.xAxisName,
          nameTextStyle: {
            fontSize: this.option.xAxisNameSize ?? 14,
          },
          axisLine: {
            lineStyle: {
              color: this.option.lineColor || '#333',
              opacity: 0.3,
            },
          },
          axisTick: {
            lineStyle: {
              color: '#fff',
              opacity: 0.3,
            },
          },

          data: optionData.categories || [],
          inverse: this.vaildData(this.option.xAxisInverse, false),
          show: this.vaildData(this.option.xAxisShow, true),
          splitLine: {
            show: this.vaildData(this.option.xAxisSplitLineShow, false),
            lineStyle: {
              color: this.vaildData(
                this.option.xAxisSplitLineTypeColor,
                '#fff'
              ),
              opacity: 0.1,
              width: 1,
              type: this.vaildData(this.option.xAxisSplitLineType, false),
            },
          },
          axisLabel: {
            interval: this.option.xAxisinterval || 'auto',
            rotate: this.option.xAxisRotate,
            textStyle: {
              color: this.option.nameColor || '#333',
              fontSize: this.option.xNameFontSize ?? 14,
              fontFamily: 'SourceHanSansCN-Regular',
            },
          },
        },
        yAxis: {
          type: this.option.category ? 'category' : 'value',
          name: this.option.yAxisName,
          nameTextStyle: {
            fontSize: this.option.yAxisNameSize ?? 14,
            fontFamily: 'SourceHanSansCN-Regular',
          },
          data: optionData.categories || [],
          axisLabel: {
            textStyle: {
              color: this.option.nameColor || '#333',
              fontSize: this.option.yNameFontSize ?? 14,
              fontFamily: 'SourceHanSansCN-Regular',
            },
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: this.option.lineColor || '#333',
              opacity: 0.3,
            },
          },
          axisTick: {
            lineStyle: {
              color: '#fff',
              opacity: 0.3,
            },
          },
          inverse: this.vaildData(this.option.yAxisInverse, false),
          show: this.vaildData(this.option.yAxisShow, true),
          splitLine: {
            show: this.vaildData(this.option.yAxisSplitLineShow, true),
            lineStyle: {
              color: this.vaildData(
                this.option.yAxisSplitLineTypeColor,
                '#fff'
              ),
              opacity: 0.1,
              width: 1,
              type: this.vaildData(this.option.yAxisSplitLineType, false),
            },
          },
        },
        series: (() => {
          // 对数据进行的前端处理  根据实际数据将预测数据进行改变 实际处理过的数据预测改为"-"
          for (let i = 0; i < optionData.series[0].data.length - 1; i++) {
            optionData.series[1].data.splice(i, 1, '-')
          }
          const list = (optionData.series || []).map((ele, index, arr) => {
            return Object.assign(ele, {
              type: 'line',
              smooth: this.vaildData(
                this.option.whichActive[index].smooth,
                true
              ),
              symbol: 'circle',
              showSymbol: this.option.whichActive[index].showSymbol,
              symbolSize: this.option.symbolSize || 10,
              areaStyle: (() => {
                if (this.option.whichActive[index].areaStyle) {
                  let newColor = ''
                  if (index === 0) {
                    if (this.option.relatyBarColor) {
                      let relatyBarColor = []
                      if (this.option.relatyBarColor[0].color1) {
                        relatyBarColor.push({
                          offset: 0,
                          color: this.option.relatyBarColor[0].color1,
                        })
                      }
                      if (this.option.relatyBarColor[0].color2) {
                        relatyBarColor.push({
                          offset: 1,
                          color: this.option.relatyBarColor[0].color2,
                        })
                      }
                      newColor = new echarts.graphic.LinearGradient(
                        0,
                        0,
                        0,
                        1,
                        relatyBarColor
                      )
                    }
                  }
                  if (index === 1) {
                    if (this.option.calculateBarColor) {
                      let calculateBarColor = []
                      if (this.option.calculateBarColor[0].color1) {
                        calculateBarColor.push({
                          offset: 0,
                          color: this.option.calculateBarColor[0].color1,
                        })
                      }
                      if (this.option.calculateBarColor[0].color2) {
                        calculateBarColor.push({
                          offset: 1,
                          color: this.option.calculateBarColor[0].color2,
                        })
                      }
                      newColor = new echarts.graphic.LinearGradient(
                        0,
                        0,
                        0,
                        1,
                        calculateBarColor
                      )
                    }
                  }
                  return {
                    color: newColor,
                    opacity: 0.7,
                  }
                }
              })(),
              lineStyle: {
                width: this.option.whichActive[index].lineWidth || 1,
                type: this.option.whichActive[index].lineStyle.type,
              },
              // 设置圆点的样式
              itemStyle: this.ishasprop(
                !this.switchTheme,
                {
                  color: this.getColor(index),
                },
                {
                  borderColor: '#fff',
                  borderWidth: 4,
                  borderType: 'solid',
                }
              ),
              label: {
                show: this.vaildData(this.option.labelShow, false), //开启显示
                position: 'top', //在上方显示,
                formatter: name => this.getLabelFormatter(name),
                textStyle: {
                  //数值样式
                  fontSize: this.option.labelShowFontSize ?? 14,
                  color: this.option.labelShowColor || '#333',
                  fontWeight: this.option.labelShowFontWeight || 500,
                },
              },
              markLine: {
                symbol: ['none', 'none'],
                label: { show: false },
                data: [{ xAxis: arr[0].data.length - 1 }],
              },
            })
          })
          // console.log(list)
          return list
        })(),
      }
      this.myChart.resize()
      this.myChart.setOption(option, true)
    },
  },
})
</script>
