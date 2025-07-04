<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'
export default create({
  name: 'PeakBar',
  data() {
    return {
      clearLoop:null
    }
  },
  computed: {
    x2() {
      return this.option.gridX2 ?? 60
    },
  },
  created() {},

  methods: {
    updateChart() {
      Array.prototype.splices = function (order, top) {
        if (order > 0) {
          for (let i = 0; i < this.length - top; i++) {
            this.pop()
            i--
          }
          return this
        } else {
          for (let i = 0; i < this.length - top; i++) {
            this.shift()
            i--
          }
          return this
        }
      }
      const optionData = this.deepClone(this.dataChart)
      // console.log('56666', optionData.series)
      // this.option.TOPN != 0 &&
      // optionData.categories.splices(this.option.TOPN, this.option.Ns) &&
      // optionData.series[0].data.splices(this.option.TOPN, this.option.Ns)
      this.option.TOPN != 0 &&
        optionData.categories.splices(this.option.TOPN, this.option.Ns) &&
        optionData.series.forEach(ele => {
          ele.data.splices(this.option.TOPN, this.option.Ns)
        })
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
        tooltip: this.ishasprop(
          this.formatter,
          {
            formatter: name => {
              return this.formatter(name, this.dataChart)
            },
          },
          {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow',
            },
            backgroundColor: this.option.tipBackgroundColor,
            borderColor: this.option.tipBorderColor,
            borderWidth: this.option.tipBorder,
            textStyle: {
              fontSize: this.option.tipFontSize,
              color: this.option.tipColor || '#bbb',
            },
            show:this.option.tipShow,
          }
        ),
        grid: {
          left: this.option.gridX ?? 20,
          top: this.option.gridY ?? 60,
          right: this.x2 ?? 60,
          bottom: this.option.gridY2 ?? 60,
        },
        legend: {
          itemWidth: this.option.legendWidth ?? 20,
          itemHeight: this.option.legendHeight ?? 20,
          show: this.vaildData(this.option.legend, false),
          orient: this.option.legendOrient || 'vertical',
          x: this.option.legendPostion || 'right',
          top: 0,
          right: this.x2,
          align: 'left', // 图例的文字在左边还是右边
          textStyle: {
            fontSize: this.option.legendFontSize ?? 12,
            color: '#fff',
            fontFamily: this.option.legendFontFamily,
          },
          // itemStyle: {
          //   color: this.getColor(0),
          // },
          data: (() => {
            return (optionData.series || []).map((ele, index) => {
              return {
                name: ele.name || 'Type' + index,
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
        xAxis: {
          type: 'category',
          data: optionData.categories || [],
          name: this.option.xAxisName,
          nameTextStyle: {
            fontSize: this.option.xAxisNameSize ?? 14,
          },
          axisLine: {
            // X轴的相关设置
            show: true,
            lineStyle: {
              width: 1,
              color: this.option.lineColor || '#333',
              opacity: 0.3,
            },
          },
          axisTick: {
            // X轴的刻度线设置
            show: true,
            lineStyle: {
              width: 1,
              color: '#FFFFFF',
              opacity: 0.3,
            },
          },
          inverse: this.vaildData(this.option.xAxisInverse, false), // 是否开启轴的反转
          show: this.vaildData(this.option.xAxisShow, true), // 是否显示X轴
          splitLine: {
            // X轴的网格线即竖直方向的网格线
            show: this.vaildData(this.option.xAxisSplitLineShow, false),
            lineStyle: {
              color: this.vaildData(this.option.xAxisSplitLineTypeColor, false),
              opacity: 0.1,
              width: 3,
              type: this.vaildData(this.option.xAxisSplitLineType, false),
            },
          },
          axisLabel: {
            // X轴的标签间距
            interval: this.option.xAxisinterval || 'auto',
            rotate: this.option.xAxisRotate || 0,
            textStyle: {
              color: this.option.nameColor || '#333',
              fontSize: this.option.xNameFontSize ?? 14,
              fontFamily: 'SourceHanSansCN-Regular',
            },
          },
        },
        yAxis: {
          type: 'value',
          name: this.option.yAxisName,
          nameTextStyle: {
            fontSize: this.option.yAxisNameSize ?? 14,
            fontFamily: 'SourceHanSansCN-Regular',
          },
          inverse: this.vaildData(this.option.yAxisInverse, false),
          show: this.vaildData(this.option.yAxisShow, true),
          axisLine: {
            show: true,
            lineStyle: {
              width: 1,
              color: this.option.lineColor || '#333',
              opacity: 0.3,
            },
          },
          axisLabel: {
            color: this.option.nameColor || '#333',
            fontSize: this.option.yNameFontSize ?? 14,
          },
          splitLine: {
            show: this.vaildData(this.option.yAxisSplitLineShow, true),
            lineStyle: {
              color: this.vaildData(this.option.yAxisSplitLineTypeColor, false),
              opacity: 0.1,
              width: 1,
              type: this.vaildData(this.option.yAxisSplitLineType, false),
            },
          },
          axisTick: {
            show: true,
            lineStyle: {
              width: 1,
              color: '#FFFFFF',
              opacity: 0.3,
            },
          },
        },
        series: (() => {
          const list = []
          optionData.series.forEach((ele, index) => {
            ele.name = ele.name ? ele.name : 'Type' + index
            list.push(
              Object.assign(ele, {
                type: 'pictorialBar',
                barGap: this.option.barGap + '%',
                barCategoryGap: this.option.barCategoryGap + '%',
                barMinHeight: 10,
                symbol:
                  this.option.cylinderStyle === 'triangle'
                    ? 'path://M0,20 L10,20 L5,10 L0,20 z'
                    : 'path://M0,10 L10,10 C5.5,10 5.5,5 5,0 C4.5,5 4.5,10 0,10 z',
                itemStyle: {
                  color: this.getColor(index)
                    ? this.getColor(index)
                    : '#AAC6E2',
                },
                label: {
                  show: this.vaildData(this.option.labelShow, false), //开启显示
                  position: this.option.category ? 'right' : 'top', //在上方显示,
                  textStyle: {
                    //数值样式
                    fontSize: this.option.labelShowFontSize ?? 14,
                    color: this.option.labelShowColor || '#333',
                    fontWeight: this.option.labelShowFontWeight || 500,
                    fontFamily: 'SourceHanSansCN-Regular',
                  },
                  formatter: e => {
                    return e.value
                  },
                },
                data: ele.data,
              })
            )
          })
          return list
        })(),
      }
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
