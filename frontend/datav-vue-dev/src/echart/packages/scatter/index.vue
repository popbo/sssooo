<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'
export default create({
  name: 'scatter', //散点图
  computed: {
    x2() {
      return this.option.gridX2 || 60
    },
  },
  data() {
    return {
      clearLoop:null
    }
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
            },
            left: this.option.titlePostion || 'auto',
            subtextStyle: {
              color: this.option.subTitleColor || '#aaa',
              fontSize: this.option.subTitleFontSize ?? 14,
            },
          },
          {}
        ),
        tooltip: {
          formatter: this.option.formatter || '',
          backgroundColor: this.option.tipBackgroundColor,
          borderColor: this.option.tipBorderColor,
          borderWidth: this.option.tipBorder,
          textStyle: {
            fontSize: this.option.tipFontSize,
            color: this.option.tipColor || '#fff',
          },
          show:this.option.tipShow,
        },
        grid: {
          left: this.option.gridX ?? 50,
          top: this.option.gridY ?? 60,
          right: this.x2??60,
          bottom: this.option.gridY2 ?? 60,
        },
        xAxis: {
          show: this.vaildData(this.option.xAxisShow, true),
          name: this.option.xAxisName,
          nameTextStyle: {
            fontSize: this.option.xAxisNameSize ?? 14,
            fontFamily: 'SourceHanSansCN-Regular',
          },
          inverse: this.vaildData(this.option.xAxisInverse, false),
          splitLine: {
            show: this.vaildData(this.option.xAxisSplitLineShow, false),
            lineStyle: {
              color: this.vaildData(
                this.option.xAxisSplitLineTypeColor,
                '#fff'
              ),
              width: 1,
              type: this.vaildData(this.option.xAxisSplitLineType, false),
            },
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: this.option.lineColor || '#fff',
            },
          },
          splitLine: { show: false },
          splitNumber: this.option.xAxisinterval,
          axisLabel: {
            show: true,
            // interval: this.option.xAxisinterval || 'auto',
            rotate: this.option.xAxisRotate || 0,
            textStyle: {
              color: this.option.nameColor || '#fff',
              fontSize: this.option.xNameFontSize ?? 14,
            },
          },
        },
        yAxis: {
          show: this.vaildData(this.option.yAxisShow, true),
          name: this.option.yAxisName,
          nameTextStyle: {
            fontSize: this.option.yAxisNameSize ?? 14,
            fontFamily: 'SourceHanSansCN-Regular',
          },
          inverse: this.vaildData(this.option.yAxisInverse, false),
          splitLine: {
            show: this.vaildData(this.option.yAxisSplitLineShow, true),
            lineStyle: {
              color: this.vaildData(
                this.option.yAxisSplitLineTypeColor,
                '#fff'
              ),
              type: this.vaildData(this.option.yAxisSplitLineType, false),
            },
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: this.option.lineColor || '#fff',
            },
          },
          axisLabel: {
            show: true,
            textStyle: {
              color: this.option.nameColor || '#fff',
              fontSize: this.option.yNameFontSize ?? 14,
              fontFamily: 'SourceHanSansCN-Regular',
            },
          },
        },
        series: (() => {
          const barColor = this.option.barColor || []
          const list = (optionData || []).map((ele, index) => {
            return Object.assign(ele, {
              type: 'scatter',
              itemStyle: {
                color: this.getColor(index),
              },
              label: {
                show: this.vaildData(this.option.labelShow, false), //开启显示
                position: 'top',
                textStyle: {
                  fontSize: this.option.labelShowFontSize ?? 14,
                  color: this.option.labelShowColor || '#333',
                  fontWeight: this.option.labelShowFontWeight || 500,
                },
              },
            })
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
