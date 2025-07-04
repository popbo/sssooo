<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'

export default create({
  name: 'radar',
  x2() {
    return this.option.gridX2 || '80%'
  },
  methods: {
    updateChart() {
      const optionData = this.deepClone(this.dataChart)
      const option = {
        title: this.ishasprop(
          this.option.titleShow,
          {
            text: this.option.title,
            subtext: this.option.subtext || '',
            textStyle: {
              color: this.option.titleColor || '#333',
              fontSize: this.option.titleFontSize ?? 16,
              fontFamily: 'SourceHanSansCN-Normal', // 雷达图的正标题字体
            },
            left: this.option.titlePostion || 'auto',
            subtextStyle: {
              color: this.option.subTitleColor || '#aaa',
              fontSize: this.option.subTitleFontSize ?? 14,
              fontFamily: 'SourceHanSansCN-Normal', // 雷达图的副标题字体
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
              textStyle: {
                fontSize: this.option.tipFontSize ?? 14,
                color: this.option.tipColor || '#fff',
              },
              show:this.option.tipShow,
            }
          )
        })(),

        grid: {
          left: this.option.gridX ?? 20,
          top: this.option.gridY ?? 60,
          right: this.x2 ?? '80%',
          bottom: this.option.gridY2 ?? 60,
        },
        legend: {
          //雷达的legend是单系列的和其他不一样,不能合并
          itemWidth: this.option.legendWidth ?? 25,
          itemHeight: this.option.legendHeight ?? 14,
          show: this.vaildData(this.option.legend, false),
          top: 0,
          x: this.option.legendPostion || 'right',
          right: this.x2,
          orient: this.option.legendOrient || 'horizontal',
          textStyle: {
            fontSize: this.option.legendFontSize ?? 15,
            fontFamily: 'SourceHanSansCN-Normal', // 图例的字体
          },

          data: (() => {
            return (optionData.series[0].data || []).map((ele, index) => {
              return {
                name: ele.name,
                itemStyle: {
                  color: this.getColor(index, false),
                },
                textStyle: this.ishasprop(
                  !this.option.switchTheme,
                  {
                    color: this.getColor(index, true),
                  },

                  {}
                ),
              }
            })
          })(),
        },
        radar: {
          name: {
            fontSize: this.option.radarNameSize ?? 12,
            color: this.option.radarNameColor || '#333',
          },
          // 只有一条数据时，可以使用该方法，显示单数据
          formatter: (a, b) => {
            i++
            return `${a}\n${arr1[i]}`
            // return `{a|${a}}\n{b|${arr1[i]}}`
          },
          indicator: optionData.indicator || [],
          shape: this.option.shape || 'polygon',
          radius: this.option.radius || '75%',
          // axisLine: {
          //   // (圆内的几条直线)坐标轴轴线相关设置
          //   lineStyle: {
          //     color: '#FF9999',
          //     // 坐标轴线线的颜色。
          //     width: 1,
          //     // 坐标轴线线宽。
          //     type: 'solid',
          //     // 坐标轴线线的类型。
          //   },
          // },
          splitLine: {
            // (这里是指所有圆环)坐标轴在 grid 区域中的分隔线。
            lineStyle: {
              color: this.option.lineColor || '#fff',
              // 分隔线颜色
              // width: 1,
              // 分隔线线宽
            },
          },
          splitArea: {
            // 坐标轴在 grid 区域中的分隔区域，默认不显示。
            show: true,
            areaStyle: {
              // 分隔区域的样式设置。
              color: [this.option.externalBgColor],
              // 分隔区域颜色。分隔区域会按数组中颜色的顺序依次循环设置颜色。默认是一个深浅的间隔色。
            },
          },
        },
        series: (() => {
          // const barColor = this.option.barColor || []
          const list = [
            {
              type: 'radar',
              barWidth: this.option.barWidth ?? 16,
              barMinHeight: this.option.barMinHeight || 0,
              itemStyle: {
                barBorderRadius: this.option.barRadius || 0,
              },
              data: (() => {
                return (optionData.series[0].data || []).map((ele, index) => {
                  return {
                    name: ele.name,
                    value: ele.value,
                    label: {
                      show: this.vaildData(this.option.labelShow, false), //开启显示

                      textStyle: {
                        fontSize: this.option.labelShowFontSize ?? 14,
                        color:
                          this.option.labelShowColor || this.getColor(index),
                        fontWeight: this.option.labelShowFontWeight || 500,
                        fontFamily: 'SourceHanSansCN-Regular',
                      },
                    },
                    itemStyle: {
                      color: this.getColor(index),
                    },
                    areaStyle: {
                      color: this.getColor(index),
                      opacity: this.option.areaOpacity ?? 1,
                    },
                    lineStyle: {
                      color: this.getColor(index),
                    },
                  }
                })
              })(),
            },
          ]
          return list
        })(),
      }
      this.myChart.resize()
      this.myChart.setOption(option, true)
    },
  },
})
</script>
