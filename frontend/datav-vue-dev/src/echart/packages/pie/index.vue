<template>
  <div :class="b()" :style="styleSizeName">
    <div
      :class="b('title')"
      v-html="titleFormatter && titleFormatter(dataChart)"
    ></div>
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'
export default create({
  data() {
    return {
      autoTimer: null,
    }
  },
  name: 'pie',
  computed: {
    labelShow() {
      return this.vaildData(this.option.labelShow, false)
    },
    x2() {
      return this.option.gridX2 || 50
    },
    fontSize() {
      return this.option.fontSize ?? 14
    },
  },
  methods: {
    updateChart() {
      // 清除定时器
      if (!this.option.showAnimation) {
        clearInterval(this.autoTimer)
      }
      const optionData = this.deepClone(this.dataChart) || []
      let legendPostionX = ''
      let legendPostionY = ''
      if(this.option.numericalControl){
        legendPostionX = this.option.numberX + "%"
        legendPostionY = this.option.numberY + "%"
      }else{
        legendPostionX = this.option.legendPostionX || 'right';
        legendPostionY = this.option.legendPostionY || 'top';
      }
      const option = {
        calculable: false,
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
              textStyle: {
                fontSize: this.option.tipFontSize,
                color: this.option.tipColor ?? '#bbb',
              },
              show:this.option.tipShow
            }
          )
        })(),
        grid: {
          left: this.option.gridX ?? 0,
          top: this.option.gridY ?? '15%',
          right: this.x2 ?? 50,
          bottom: this.option.gridY2 ?? 20,
        },
        legend: {
          show: this.vaildData(this.option.legend, false),
          orient: this.option.legendOrient || 'vertical',
          x: legendPostionX,
          y: legendPostionY,
          // padding: [
          //   5, // 上
          //   20, // 右
          //   5, // 下
          //   20, // 左
          // ], // 这个图例位置生效后 top 和 right 就不能生效了
          // top: 'center',
          // right: this.x2,
          itemGap: 18,
          itemWidth: this.option.legendWidth ?? 14, //设置图例的宽
          itemHeight: this.option.legendHeight ?? 14, // 设置图例的高
          textStyle: {
            fontSize: this.option.legendFontSize ?? 15,
            fontFamily: this.option.legendFontFamily,
            rich:{
              a:{
                padding: [2, 0, 0, 0],
                fontSize: this.option.legendFontSize ?? 15,
              },
              b:{
                padding: [0, 0, 0,this.option.unitWidth],
                fontSize: this.option.legendFontSize ?? 15,
                fontFamily: this.option.unitFontFamily,
              },
              c:{
                padding: [this.option.belowWidth, 0, 0, 2],
                fontSize: this.option.unitFontSize ?? 15,
                fontFamily: this.option.belowFontFamily,
              }
            }
          },
          formatter:(name)=>{
            console.log(name,optionData,1111)
            if(this.option.numberUnit){
              let value = ''
              optionData.forEach(item=>{
                if(item.name===name){
                  value = item.value
                }
              })
              let unitText = this.option.unitText || ''
              return `{a|${name}} {b|${value}}{c|${unitText}}`
            }
            return name
          },
          data: (() => {
            return (Array.isArray(optionData) ? optionData : []).map(
              (ele, index) => {
                return {
                  name: ele.name,
                  textStyle: this.ishasprop(
                    !this.switchTheme,
                    {
                      // color: this.getColor(index,true)
                      color: this.newGetColor(index,this.option.barColor),
                    },
                    {}
                  ),
                }
              }
            )
          })(),
        },
        series: (() => {
          const list = [
            {
              type: 'pie',
              roseType: this.option.roseType ? 'radius' : '',
              radius: this.setRadius(),
              center: [`${this.option.leftWdidth}%`, '50%'],
              animationType: 'scale',
              animationEasing: 'elasticOut',
              animationDelay: function (idx) {
                return Math.random() * 200
              },
              label: {
                show: this.labelShow,
                position: 'outside',
                formatter: '{value|{c}}\n{label|{b}}',
                rich: {
                  value: {
                    padding: 5,
                    align: 'center',
                    verticalAlign: 'middle',
                    fontFamily: 'SourceHanSansCN-Bold',
                    fontSize: this.option.labelShowFontSize ?? 26,
                    color: this.option.labelShowColor
                      ? this.option.labelShowColor
                      : 'inherit',
                    fontWeight: this.option.labelShowFontWeight
                      ? this.option.labelShowFontWeight
                      : 'normal',
                  },
                  label: {
                    align: 'center',
                    verticalAlign: 'middle',
                    fontFamily: this.option.fontFamily,
                    fontSize: this.option.labelShowFontSize ?? 18,
                    color: this.option.labelShowColor
                      ? this.option.labelShowColor
                      : 'inherit',
                    fontWeight: this.option.labelShowFontWeight
                      ? this.option.labelShowFontWeight
                      : 'normal',
                  },
                },
              },
              labelLine:{
                length: this.option.labelLine1,
                length2: this.option.labelLine2,
              },
              data: (() => {
                let list = optionData
                if (this.option.notCount) {
                  list = list.filter(ele => {
                    if (ele.value !== 0 && ele.value) {
                      return true
                    }
                  })
                }
                if (this.option.sort) {
                  list.sort(function (a, b) {
                    return a.value - b.value
                  })
                }
                return list
              })(),
              itemStyle: this.ishasprop(
                !this.switchTheme,
                {
                  // color: this.getColor(params.dataIndex)
                  // color: params => new echarts.graphic.LinearGradient(0, 1, 0, 0, [{
                  //     offset: 0,
                  //     color: this.yuanNewGetColor(params.dataIndex).color1
                  // }, {
                  //     offset: 1,
                  //     color: this.yuanNewGetColor(params.dataIndex).color2 || ''
                  // }]),
                  color: params => this.newGetColor(params.dataIndex,this.option.barColor),
                },
                {
                  // emphasis: {
                  //   shadowBlur: 10,
                  //   shadowOffsetX: 0,
                  //   shadowColor: "rgba(0, 0, 0, 0.5)"
                  // }
                  borderColor: this.option.boderColor,
                  borderWidth: this.option.boderWidth,
                  borderType: 'solid',
                }
              ),
            },
          ]
          let parms =  {
              radius: this.withinRadius(),
              center: [`${this.option.leftWdidth}%`, '50%'],
              type: 'pie',
              hoverAnimation: false,
              label: {
                  normal: {
                      show: false
                  },
                  emphasis: {
                      show: false
                  }
              },
              labelLine: {
                  normal: {
                      show: false
                  },
                  emphasis: {
                      show: false
                  }
              },
              animation: false,
              tooltip: {
                  show: true
              },
              data: (() => {
                let list = optionData
                if (this.option.notCount) {
                  list = list.filter(ele => {
                    if (ele.value !== 0 && ele.value) {
                      return true
                    }
                  })
                }
                if (this.option.sort) {
                  list.sort(function (a, b) {
                    return a.value - b.value
                  })
                }
                return list
              })(),
              itemStyle:{
                borderColor: this.option.boderColor,
                borderWidth: this.option.boderWidth,
                borderType: 'solid',
                color: params => this.newGetColor(params.dataIndex,this.option.withinBarColor),
              }
          }
          if(this.option.within){
            list.push(parms)
          }
          return list
        })(),
        animation: true,
      }
      // 取消上一个的高亮
      this.myChart.dispatchAction({
        type: 'downplay',
        seriesIndex: 0,
        dataIndex: this.currentIndex,
      })
      this.myChart.resize()
      this.myChart.setOption(option, true)
      // // this.getDefaultSelected(this.myChart)
      if (this.option.showAnimation) {
        this.autoActiveRing(
          this.myChart,
          option,
          this.option.animationInterval,
          this
        )
      }
    },
    // 该函数未支持渐变色
    newGetColor(index,barColorList) {
      const barColor = barColorList || []
      const length = barColor.length
      let color
      if (index < length) {
        // 如果数据的dataIndex没有超过颜色数组的长度
        color = barColor[index].color1
      } else {
        // 如果超过就对颜色数组长度取余数循环使用
        let indexOfRemainder = index % length
        color = barColor[indexOfRemainder].color1 || ''
      }
      return color
    },
    yuanNewGetColor(index) {
      const barColor = this.option.barColor || []
      console.log('this.option.barColor',this.option.barColor)
      const length = barColor.length
      let color
      if (index < length) {
        // 如果数据的dataIndex没有超过颜色数组的长度
        color = {
          color1:barColor[index].color1,
          color2:barColor[index].color2 || barColor[index].color1
        }
      } else {
        // 如果超过就对颜色数组长度取余数循环使用
        let indexOfRemainder = index % length
        color = {
          color1:barColor[indexOfRemainder].color1,
          color2:barColor[indexOfRemainder].color2 || barColor[indexOfRemainder].color1
        }
      }
      return color
    },
    autoActiveRing(myChart, option, animationInterval, _that) {
      _that.currentIndex = -1
      if (this.autoTimer) {
        clearInterval(this.autoTimer)
      }
      this.autoTimer = setInterval(function () {
        var dataLen = option.series[0].data.length
        // 取消之前高亮的图形
        myChart.dispatchAction({
          type: 'downplay',
          seriesIndex: 0,
          dataIndex: _that.currentIndex,
        })
        _that.currentIndex = (_that.currentIndex + 1) % dataLen
        // 高亮当前图形
        myChart.dispatchAction({
          type: 'highlight',
          seriesIndex: 0,
          dataIndex: _that.currentIndex,
        })
      }, animationInterval)
    },
    getDefaultSelected(myChart) {
      let index = 0
      myChart.dispatchAction({
        type: 'highlight',
        seriesIndex: 0,
        dataIndex: 0,
      })
      myChart.on('mouseover', e => {
        if (e.dataIndex !== index) {
          myChart.dispatchAction({
            type: 'downplay',
            seriesIndex: 0,
            dataIndex: index,
          })
        }
      })
      myChart.on('mouseout', e => {
        index = e.dataIndex
        myChart.dispatchAction({
          type: 'highlight',
          seriesIndex: 0,
          dataIndex: e.dataIndex,
        })
      })
    },
    withinRadius() {
        return [
          this.option.withinRadius,
          this.option.withinRadius + this.option.withinAnnulusWidth,
        ]
    },
    setRadius() {
      if (this.option.isAnnulus && this.option.annulusWidth) {
        return [
          this.option.radius,
          this.option.radius + this.option.annulusWidth,
        ]
      } else {
        return this.option.radius
      }
    },
  },
  beforeDestroy() {
    // 清除滚动定时器
    if (this.autoTimer) {
      clearInterval(this.autoTimer)
    }
  },
})
</script>
