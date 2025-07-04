<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'

export default create({
  name: 'biaxlinebar',
  computed: {
    x2() {
      return this.option.gridX2 ?? 58
    },
  },
  data(){
    return {
      clearLoop:null,
    }
  },
  methods: {
    updateChart() {
      const chooseLabelPosition = ele => {
        if (ele.type === 'bar') {
          return this.option.category ? 'top' : 'left'
        } else {
          return this.option.category ? 'bottom' : 'top'
        }
      }
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
      this.option.TOPN != 0 &&
        optionData.categories.splices(this.option.TOPN, this.option.Ns) &&
        optionData.series[0].data.splices(this.option.TOPN, this.option.Ns)
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
        // tooltip: this.ishasprop(
        //   this.formatter,
        //   {
        //     formatter: name => {
        //       return this.formatter(name, this.dataChart)
        //     },
        //   },
        //   {
        //     textStyle: {
        //       fontSize: this.option.tipFontSize,
        //       color: this.option.tipColor || '#fff',
        //     },
        //   }
        // ),
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
          },
          backgroundColor: this.option.tipBackgroundColor,
          borderColor: this.option.tipBorderColor,
          borderWidth: this.option.tipBorder,
          textStyle: {
            fontSize: this.option.tipFontSize || 14,
            color: this.option.tipColor || '#fff',
          },
        },
        grid: {
          left: this.option.gridX ?? 90,
          top: this.option.gridY ?? 60,
          right: this.x2 ?? 58,
          bottom: this.option.gridY2 ?? 60,
        },
        legend: {
          itemWidth: this.option.legendWidth ?? 25,
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
            fontFamily: this.option.legendFontFamily,
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
        xAxis: {
          type: this.option.category ? 'value' : 'category',
          name: this.option.xAxisName,
          position: "left",
          alignTicks: true,
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
            // 刻度线的设置
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
            rotate: this.option.xAxisRotate || 0,
            textStyle: {
              color: this.option.nameColor || '#333',
              fontSize: this.option.xNameFontSize ?? 14,
              fontFamily: 'SourceHanSansCN-Regular',
            },
          },
        },
        yAxis: [{
          type: this.option.category ? 'category' : 'value',
          name: this.option.yLaxisName,
          nameTextStyle: {
            fontSize: this.option.yLaxisNameSize ?? 14,
            fontFamily: 'SourceHanSansCN-Regular',
            color:this.option.yLaxisLabelColor,
          },
          data: optionData.categories || [],
          axisLabel: {
            textStyle: {
              color:this.option.yLaxisLabelColor,
              fontSize: this.option.yLameFontSize ?? 14,
              fontFamily: 'SourceHanSansCN-Regular',
            },
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: this.option.yLlineColor || '#fff',
              opacity: 0.3,
              width: 1,
            },
          },
          axisTick: {
            show: true,
            length: 7,
            lineStyle: {
              color: '#fff',
              opacity: 0.3,
              width: 1,
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
        },{
          type: this.option.category ? 'category' : 'value',
          name: this.option.yRaxisName,
          osition: "right",
          alignTicks: true,
          nameTextStyle: {
            fontSize: this.option.yRaxisNameSize ?? 14,
            fontFamily: 'SourceHanSansCN-Regular',
            color:this.option.yRaxisLabelColor,
          },
          data: optionData.categories || [],
          axisLabel: {
            textStyle: {
              color:this.option.yRaxisLabelColor,
              fontSize: this.option.yRameFontSize ?? 14,
              fontFamily: 'SourceHanSansCN-Regular',
            },
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: this.option.yRlineColor || '#fff',
              opacity: 0.3,
              width: 1,
            },
          },
          axisTick: {
            show: true,
            length: 7,
            lineStyle: {
              color: '#fff',
              opacity: 0.3,
              width: 1,
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
        },],
        series: (() => {
          //const barColor = this.option.barColor || [];
          const list = (optionData.series || []).map((ele, index) => {
            // console.log('数据聚聚聚聚聚聚聚聚=====》', ele, chooseLabelPosition)
            return Object.assign(ele, {
              //type: "bar",
              stack: ele.stack,
              barWidth: this.option.barWidth ?? 16,
              smooth: this.vaildData(this.option.smooth, true),
              symbol: 'circle',
              symbolSize: this.option.symbolSize ?? 11,
              showSymbol: this.option.showSymbol,
              barGap: this.option.barGap + `%`,
              barMinHeight: this.option.barMinHeight || 0,
              itemStyle: this.ishasprop(
                !this.switchTheme,
                {
                  color: this.getColor(index),
                },
                { barBorderRadius: this.option.barRadius || 0 }
              ),
              yAxisIndex: index>1?1:index,
              lineStyle: {
                width: this.option.lineWidth || 1,
              },
              label: {
                show: this.vaildData(this.option.labelShow, false), //开启显示
                position: chooseLabelPosition(ele), //在上方显示,
                rotate: this.option.xLableRotate || 0,
                formatter: name => this.getLabelFormatter(name),
                textStyle: {
                  //数值样式
                  fontSize: this.option.labelShowFontSize ?? 14,
                  color: this.option.labelShowColor || '#fff',
                  fontWeight: this.option.labelShowFontWeight || 500,
                  fontFamily: 'SourceHanSansCN-Regular',
                },
              },
            })
          })
          // console.log(list);
          return list
        })(),
      }
      this.myChart.resize()
      this.myChart.setOption(option, true)
      this.clearLoop && this.clearLoop()
      if(this.option.tipCarousel){
        this.clearLoop = (tools.loopShowTooltip(this.myChart, option, {loopSeries: true,interval: this.option.tipCarouselTime})).clearLoop;
      }
      // console.log();
    },
  },
})
</script>
