<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'
export default create({
  name: 'line',
  computed: {
    x2() {
      return this.option.gridX2 ?? 60
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
      let handleIcon =
        'path://M-9.35,34.56V42m0-40V9.5m-2,0h4a2,2,0,0,1,2,2v21a2,2,0,0,1-2,2h-4a2,2,0,0,1-2-2v-21A2,2,0,0,1-11.35,9.5Z'
      if (this.option.handleStyle === 2) {
        handleIcon =
          'path://M512,512m-448,0a448,448,0,1,0,896,0a448,448,0,1,0,-896,0Z'
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
                color: this.option.tipColor || '#fff',
              },
              show:this.option.tipShow
            }
          )
        })(),
        grid: {
          left: this.option.gridX ?? 20,
          top: this.option.gridY ?? 60,
          right: this.x2 ?? 60,
          bottom: this.option.gridY2 ?? 60,
        },
        legend: {
          icon: this.option.iconType || 'circle',
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
                  color: this.getColor(index, true),
                },
              }
            })
          })(),
        },
        xAxis: [
          {
            type: this.option.category ? 'value' : 'category',
            name: this.option.xAxisName,
            boundaryGap:!this.option.axleX,
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
        ],
        yAxis:  {
          type: this.option.category ? 'category' : 'value',
          name: this.option.yAxisName,
          boundaryGap:!this.option.axleX,
          nameTextStyle: {
            fontSize: this.option.yAxisNameSize ?? 14,
            fontFamily: 'SourceHanSansCN-Regular',
          },
          data: optionData.categories || [],
          axisLabel: {
            textStyle: {
              color: this.option.nameColor || '#fff',
              fontSize: this.option.yNameFontSize ?? 14,
              fontFamily: 'SourceHanSansCN-Regular',
            },
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: this.option.lineColor || '#fff',
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
        },
        series: (() => {
          let list = (optionData.series || []).map((ele, index) => {
            return Object.assign(ele, {
              type: 'line',
              smooth: this.vaildData(this.option.smooth, true),
              symbol: 'circle',
              symbolSize: this.option.symbolSize ?? 11,
              showSymbol: this.option.showSymbol,
              areaStyle: (() => {
                if (this.option.areaStyle) {
                  return {
                    opacity: 0.7,
                  }
                }
              })(),
              lineStyle: {
                width: this.option.lineWidth || 1,
              },
              // 设置圆点的样式
              itemStyle: this.ishasprop(
                !this.switchTheme,
                {
                  color: this.getColor(index),
                },
                {
                  borderColor: '#fff',
                  borderWidth: 0,
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
            })
          },{
          })
          if(this.option.animation && !this.option.sliding){
            let listAnimation = []
            if(optionData.series){
              if(optionData.series.length>0){
                optionData.series.forEach(item=>{
                  if(!item.smooth){
                    listAnimation.push({
                    showSymbol: false,
                    name: item.name,
                    type: "lines",
                    polyline: true,
                    coordinateSystem: "cartesian2d",
                    zlevel: 1,
                    effect: {
                      show: true,
                      smooth: true,
                      period: this.option.animationTime/1000,
                      symbolSize: this.option.animationSize,
                    },
                    lineStyle: {
                      color: this.option.animationColor,
                      width: 1,
                      opacity: 0,
                      curveness: 0,
                      cap: "round",
                    },
                      data: this.getAnimation(optionData.categories,item.data)
                    })
                  }
                })
                list=[...list,...listAnimation]
              }
            }
          }
          return list
        })(),
      }
      if(this.option.category ){
        let endValue = {}
        if(this.option.sliding){
          endValue = {
            endValue: this.option.slidingNumber - 1,
          }
        }
        option.dataZoom = [
            {
              type: 'slider',
              show: this.option.sliding,
              yAxisIndex: [0,1],
              // filterMode: 'empty',
              // start: 0,
              startValue: 0,
              fillerColor: this.option.slidingColor,
              // height:'100%',
              width: this.option.slidingHeight,
              right: this.option.slidingBottom,
              backgroundColor: this.option.slidingBarColor,
              // end: 100,
              // endValue: this.option.slidingNumber - 1,
              // minValueSpan:2,
              // maxValueSpan:2,
              handleSize: this.option.handleFont + '%',
              handleIcon: handleIcon,
              borderColor: 'transparent',
              borderRadius: 0,
              showDetail: this.option.slidingDrag,
              handleStyle: {
                //手柄的样式
                color: this.option.handleColor,
                shadowColor: this.option.handleColor,
                shadowBlur: 10,
              },
              dataBackground: {
                lineStyle: {
                  color: 'transparent',
                },
                areaStyle: {
                  color: 'transparent',
                },
              },
              brushSelect: false,
              textStyle: {
                //手柄文字的样式
                color: this.option.slidingFontColor,
                fontSize: this.option.slidingFont,
              },
            },
          // {
          //     type: 'inside',
          //     xAxisIndex: 0,
          //     filterMode: 'empty',
          //     bottom: 0,
          //     start: 0,
          //     end: 100
          // }
        ]
        option.dataZoom[0] = Object.assign(option.dataZoom[0],endValue)
      }else{
        let endValue = {}
        if(this.option.sliding){
          endValue = {
            endValue: this.option.slidingNumber - 1,
          }
        }
        option.dataZoom = [
            {
              type: 'slider',
              show: this.option.sliding,
              // xAxisIndex: 0,
              // filterMode: 'empty',
              // start: 0,
              startValue: 0,
              fillerColor: this.option.slidingColor,
              height: this.option.slidingHeight,
              bottom: this.option.slidingBottom,
              backgroundColor: this.option.slidingBarColor,
              // end: 100,
              // endValue: this.option.slidingNumber - 1,
              // minValueSpan:2,
              // maxValueSpan:2,
              handleSize: this.option.handleFont + '%',
              handleIcon: handleIcon,
              borderColor: 'transparent',
              borderRadius: 0,
              showDetail: this.option.slidingDrag,
              handleStyle: {
                //手柄的样式
                color: this.option.handleColor,
                shadowColor: this.option.handleColor,
                shadowBlur: 10,
              },
              dataBackground: {
                lineStyle: {
                  color: 'transparent',
                },
                areaStyle: {
                  color: 'transparent',
                },
              },
              brushSelect: false,
              textStyle: {
                //手柄文字的样式
                color: this.option.slidingFontColor,
                fontSize: this.option.slidingFont,
              },
            },
          // {
          //     type: 'inside',
          //     xAxisIndex: 0,
          //     filterMode: 'empty',
          //     bottom: 0,
          //     start: 0,
          //     end: 100
          // }
        ]
        option.dataZoom[0] = Object.assign(option.dataZoom[0],endValue)
      }
      this.myChart.resize()
      this.myChart.setOption(option, true)
      this.clearLoop && this.clearLoop()
      if(this.option.sliding){
        this.option.tipCarousel = false;
        this.option.animation = false;
      }
      if(this.option.smooth){
        this.option.animation = false;
      }
      if(this.option.tipCarousel && !this.option.sliding){
        this.clearLoop = (tools.loopShowTooltip(this.myChart, option, {loopSeries: true,interval: this.option.tipCarouselTime})).clearLoop;
      }
    },
    getAnimation(name,data){
      let datacoords = [
        {
          coords: [],
        },
      ];
      if(this.option.category){
        for (let i = 0; i < name.length; i++) {
          datacoords[0].coords.push([data[i],[i]]);
          // datacoords.push([
          //     {
          //       coord: [i, data[i]],
          //     },
          //     {
          //       coord: [i + 1, data[i + 1]],
          //     },
          // ]);
        }
      }else{
        for (let i = 0; i < name.length; i++) {
          datacoords[0].coords.push([[i],data[i]]);
          // datacoords.push([
          //     {
          //       coord: [i, data[i]],
          //     },
          //     {
          //       coord: [i + 1, data[i + 1]],
          //     },
          // ]);
        }
      }
      return datacoords
    },
  },
})
</script>
