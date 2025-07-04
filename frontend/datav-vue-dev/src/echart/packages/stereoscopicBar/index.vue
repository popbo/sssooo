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
let _this = ''
export default create({
  name: 'stereoscopicBar',
  data() {
    return {}
  },
  computed: {
    x2() {
      return this.option.gridX2 || 20
    },
  },
  created() {
    _this = this
  },

  methods: {
    updateChart() {
      let myChart = this.$echarts.init(this.$refs[this.id])
      if(myChart){
        myChart.clear()
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
        optionData.series.forEach(element => {
          element.data.splices(this.option.TOPN, this.option.Ns)
        })
      const offsetX = 15
      const offsetY = 7.5
      //绘制立方体开始
      // 绘制左侧面
      const CubeLeft = this.$echarts.graphic.extendShape({
        shape: {
          x: 0,
          y: 0,
        },
        buildPath: function (ctx, shape) {
          // 会canvas的应该都能看得懂，shape是从custom传入的
          const xAxisPoint = shape.xAxisPoint
          // console.log(shape);
          const c0 = [shape.x, shape.y]
          const c1 = [shape.x - offsetX, shape.y - offsetY]
          const c2 = [xAxisPoint[0] - offsetX, xAxisPoint[1] - offsetY]
          const c3 = [xAxisPoint[0], xAxisPoint[1]]
          ctx
            .moveTo(c0[0], c0[1])
            .lineTo(c1[0], c1[1])
            .lineTo(c2[0], c2[1])
            .lineTo(c3[0], c3[1])
            .closePath()
        },
      })
      // 绘制右侧面
      const CubeRight = this.$echarts.graphic.extendShape({
        shape: {
          x: 0,
          y: 0,
        },
        buildPath: function (ctx, shape) {
          const xAxisPoint = shape.xAxisPoint
          const c1 = [shape.x, shape.y]
          const c2 = [xAxisPoint[0], xAxisPoint[1]]
          const c3 = [xAxisPoint[0] + offsetX, xAxisPoint[1] - offsetY]
          const c4 = [shape.x + offsetX, shape.y - offsetY]
          ctx
            .moveTo(c1[0], c1[1])
            .lineTo(c2[0], c2[1])
            .lineTo(c3[0], c3[1])
            .lineTo(c4[0], c4[1])
            .closePath()
        },
      })
      // 绘制顶面
      const CubeTop = this.$echarts.graphic.extendShape({
        shape: {
          x: 0,
          y: 0,
        },
        buildPath: function (ctx, shape) {
          const c1 = [shape.x, shape.y]
          const c2 = [shape.x + offsetX, shape.y - offsetY] //右点
          const c3 = [shape.x, shape.y - offsetX]
          const c4 = [shape.x - offsetX, shape.y - offsetY]
          ctx
            .moveTo(c1[0], c1[1])
            .lineTo(c2[0], c2[1])
            .lineTo(c3[0], c3[1])
            .lineTo(c4[0], c4[1])
            .closePath()
        },
      })
      // 注册三个面图形
      this.$echarts.graphic.registerShape('CubeLeft', CubeLeft)
      this.$echarts.graphic.registerShape('CubeRight', CubeRight)
      this.$echarts.graphic.registerShape('CubeTop', CubeTop)
      //绘制立方体结束
      //绘制圆柱体开始
      // 绘制柱子
      const CircularBar = this.$echarts.graphic.extendShape({
        shape: {
          x: 0,
          y: 0,
        },
        buildPath: function (ctx, shape) {
          // 会canvas的应该都能看得懂，shape是从custom传入的
          const xAxisPoint = shape.xAxisPoint
          // console.log(shape);
          const c0 = [shape.x - offsetX, shape.y - offsetY]
          const c1 = [shape.x + offsetX, shape.y - offsetY]
          const c2 = [xAxisPoint[0] + offsetX, xAxisPoint[1] + offsetY - 7]
          const c3 = [xAxisPoint[0] - offsetX, xAxisPoint[1] + offsetY - 7]
          ctx
            .moveTo(c0[0], c0[1])
            .lineTo(c1[0], c1[1])
            .lineTo(c2[0], c2[1])
            .lineTo(c3[0], c3[1])
            .closePath()
        },
      })
      // 绘制顶面
      const CircularTop = this.$echarts.graphic.extendShape({
        shape: {
          x: 0,
          y: 0,
        },
        buildPath: function (ctx, shape) {
          //椭圆 两条贝塞尔曲线拼接
          var k = offsetX / 0.75,
            h = offsetY / 2,
            x = shape.x,
            y = shape.y - offsetY
          ctx
            .moveTo(x, y - h)
            .bezierCurveTo(x + k, y - h, x + k, y + h, x, y + h)
            .bezierCurveTo(x - k, y + h, x - k, y - h, x, y - h)
            .closePath()
        },
      })
      // 绘制底面
      const CircularBottom = this.$echarts.graphic.extendShape({
        shape: {
          x: 0,
          y: 0,
        },
        buildPath: function (ctx, shape) {
          const xAxisPoint = shape.xAxisPoint
          //半个椭圆
          var k = offsetY / 0.75,
            w = offsetX,
            x = shape.x,
            y = xAxisPoint[1]
          ctx
            .moveTo(x - w, y)
            .bezierCurveTo(x - w, y + k, x + w, y + k, x + w, y)
            .closePath()
        },
      })
      //注册圆柱体各个面
      this.$echarts.graphic.registerShape('CircularBar', CircularBar)
      this.$echarts.graphic.registerShape('CircularTop', CircularTop)
      this.$echarts.graphic.registerShape('CircularBottom', CircularBottom)

      //绘制圆柱体结束
      const VALUE = [100, 200, 300, 400, 300, 200, 100]
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
        //     formatter: (name) => {
        //       return this.formatter(name, this.dataChart)
        //     },
        //   },
        //   {
        //     trigger: 'axis',
        //     axisPointer: {
        //       type: 'none', // 去除鼠标移入时的灰色背景
        //     },
        //     textStyle: {
        //       fontSize: this.option.tipFontSize,
        //       color: this.option.tipColor || '#bbb',
        //     },
        //   }
        // ),
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
          },
          show:this.option.tipShow,
          formatter: function (params) {
            const name = params[0].name
            let tolBox = name
            console.log('this', _this)
            params.forEach((ele, index) => {
              const bgColor = _this.getColor(index, true)
                ? _this.getColor(index, true)
                : _this.getColor(0, true)
              const mark = `<span style="display:inline-block;margin-right:4px;border-radius:10px;width:10px;height:10px;background-color:${bgColor}"></span>`
              tolBox =
                tolBox + '<br>' + mark + ele.seriesName + ' : ' + ele.value
            })
            console.log('tolBox', tolBox)
            return tolBox
          },
          axisPointer: {
            type: 'none', // 去除鼠标移入时的灰色背景
          },
          backgroundColor: this.option.tipBackgroundColor,
          borderColor: this.option.tipBorderColor,
          borderWidth: this.option.tipBorder,
          textStyle: {
            fontSize: this.option.tipFontSize,
            color: this.option.tipColor || '#bbb',
          },
        },
        grid: {
          left: this.option.gridX ?? 20,
          top: this.option.gridY ?? 60,
          right: this.x2 ?? 20,
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
                itemStyle: this.ishasprop(
                  !this.switchTheme,
                  {
                    color: this.getColor(index, true)
                      ? this.getColor(index, true)
                      : this.getColor(0, true),
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
              // fontFamily: 'SourceHanSansCN-Regular',
            },
            margin: 20,
          },
        },
        yAxis: {
          type: 'value',
          name: this.option.yAxisName,
          nameTextStyle: {
            fontSize: this.option.yAxisNameSize ?? 14,
            // fontFamily: 'SourceHanSansCN-Regular',
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
          const barColor = this.option.barColor || []
          const list = []
          console.log('optionData.series', optionData.series)
          optionData.series.forEach((ele, index) => {
            ele.name = ele.name ? ele.name : 'Type' + index
            if (this.option.cylinderStyle === 'cube') {
              list.push(
                Object.assign(ele, {
                  type: 'custom',
                  renderItem: (params, api) => {
                    const location = api.coord([api.value(0), api.value(1)])
                    return {
                      type: 'group',
                      x: optionData.series.length===1?'center':-20 + 35 * index,
                      children: [
                        {
                          type: 'CubeLeft',
                          shape: {
                            api,
                            xValue: api.value(0),
                            yValue: api.value(1),
                            x: location[0],
                            y: location[1],
                            xAxisPoint: api.coord([api.value(0), 0]),
                          },
                          style: {
                            fill: this.getColor(index)
                              ? this.getColor(index)
                              : this.getColor(0),
                          },
                        },
                        {
                          type: 'CubeRight',
                          shape: {
                            api,
                            xValue: api.value(0),
                            yValue: api.value(1),
                            x: location[0],
                            y: location[1],
                            xAxisPoint: api.coord([api.value(0), 0]),
                          },
                          style: {
                            fill: this.getColor(index)
                              ? this.getColor(index)
                              : this.getColor(0),
                          },
                        },
                        {
                          type: 'CubeTop',
                          shape: {
                            api,
                            xValue: api.value(0),
                            yValue: api.value(1),
                            x: location[0],
                            y: location[1],
                            xAxisPoint: api.coord([api.value(0), 0]),
                          },
                          style: api.style(),
                        },
                      ],
                    }
                  },
                  data: ele.data,
                  itemStyle: {
                    color: this.getColor(index)
                      ? this.getColor(index)
                      : this.getColor(0),
                  },
                  label: {
                    show: this.option.labelShow,
                    color: this.option.labelShowColor || '#fff',
                    fontWeight: this.option.labelShowFontWeight || 'normal',
                    fontSize: this.option.labelShowFontSize ?? 12,
                    distance: 0,
                    position: 'top',
                  },
                })
              )
            } else {
              list.push(
                Object.assign(ele, {
                  type: 'custom',
                  renderItem: (params, api) => {
                    const location = api.coord([api.value(0), api.value(1)])
                    return {
                      type: 'group',
                      x: optionData.series.length===1?'center':-20 + 35 * index,
                      children: [
                        {
                          type: 'CircularBar',
                          shape: {
                            api,
                            xValue: api.value(0),
                            yValue: api.value(1),
                            x: location[0],
                            y: location[1],
                            xAxisPoint: api.coord([api.value(0), 0]),
                          },
                          style: {
                            fill: this.getColor(index)
                              ? this.getColor(index)
                              : this.getColor(0),
                          },
                        },
                        {
                          type: 'CircularBottom',
                          shape: {
                            api,
                            xValue: api.value(0),
                            yValue: api.value(1),
                            x: location[0],
                            y: location[1],
                            xAxisPoint: api.coord([api.value(0), 0]),
                          },
                          style: {
                            fill:
                              this.getColor(index) &&
                              this.option.barColor[index].color2
                                ? this.option.barColor[index].color2
                                : this.getColor(index) ||
                                  this.option.barColor[0].color2 ||
                                  this.option.barColor[0].color1,
                          },
                        },
                        {
                          type: 'CircularTop',
                          shape: {
                            api,
                            xValue: api.value(0),
                            yValue: api.value(1),
                            x: location[0],
                            y: location[1],
                            xAxisPoint: api.coord([api.value(0), 0]),
                          },
                          style: api.style(),
                        },
                      ],
                    }
                  },
                  data: ele.data,
                  itemStyle: {
                    color: this.getColor(index)
                      ? this.getColor(index)
                      : this.getColor(0),
                  },
                  label: {
                    show: this.option.labelShow,
                    color: this.option.labelShowColor || '#fff',
                    fontWeight: this.option.labelShowFontWeight || 'normal',
                    fontSize: this.option.labelShowFontSize ?? 12,
                    distance: 0,
                    position: 'top',
                  },
                })
              )
            }
          })
          console.log('list', list)
          return list
        })(),
      }
      myChart.resize()
      myChart.setOption(option)
    },
    handleClick() {
      this.updateClick('', 'clickFormatter')
      this.clickFormatter &&
        this.clickFormatter(
          {
            data: this.dataChart,
          },
          this.getItemRefs()
        )
    },
    handleDbClick() {
      this.updateClick('', 'dblClickFormatter')
      this.dblClickFormatter(
        {
          data: this.dataChart,
        },
        this.getItemRefs()
      )
    },
  },
})
</script>

<style scoped lang="scss"></style>
