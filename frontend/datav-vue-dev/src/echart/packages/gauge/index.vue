<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'
let _this
export default create({
  name: 'gauge',
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
        grid: {
          left: this.option.gridX ?? 20,
          top: this.option.gridY ?? 60,
          right: this.x2??20,
          bottom: this.option.gridY2 ?? 60,
        },
        series: [
          {
            type: 'gauge',
            startAngle: 210,
            endAngle: -30,
            axisLine: {
              roundCap: true,
              lineStyle: {
                color: this.getColorGauge(this.option.axisLineColor, true),
                width: this.option.lineSize ?? 5,
              },
            },
            splitLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
            axisLabel: {
              show: false,
            },
          },
          {
            name: '业务指标',
            type: 'gauge',
            startAngle: 210,
            endAngle: -30,
            detail: {
              color: '#fff',
              fontSize: this.option.valueFontSize ?? 30,
              fontFamily:this.option.fontFamily,
              formatter: function (value) {
                const percentValue = ((value / _this.option.max) * 100).toFixed()
                return percentValue + '%'
              },
            },
            min: this.option.min,
            max: this.option.max,
            splitNumber: 20,
            axisLine: {
              show: false,
              roundCap: true,
              lineStyle: {
                color: this.getColorGauge(this.option.axisTickColor, false),
                width: this.option.lineSize || 5,
              },
            },
            splitLine: {
              show: true,
              length: 20,
              distance: 0,
              lineStyle: {
                color: 'auto',
                width: 3,
              },
            },
            axisLabel: {
              color: '#AAC6E2',
              show: this.vaildData(this.option.axisLabelShow, true),
              fontSize: this.option.axisLabelFontSize ?? 25,
            },
            axisTick: {
              length: 10,
              distance: 0,
              lineStyle: {
                color: 'auto',
              },
            },
            pointer: {
              itemStyle: {
                color: {
                  type: 'linear',
                  x: 0,
                  y: 0,
                  x2: 0,
                  y2: 1,
                  colorStops: [
                    {
                      offset: 0,
                      color: this.option.pointerColor, // 0% 处的颜色
                    },
                    // {
                    //   offset: 0.1,
                    //   color: 'rgba(48, 158, 248, 0.8)', // 0% 处的颜色
                    // },
                    {
                      offset: 1,
                      color: this.option.pointerColor, // 100% 处的颜色
                    },
                  ],
                  global: false, // 缺省为 false
                },
              },
            },
            anchor: {
              show: true,
              showAbove: true,
              icon: 'circle',
              size: 10,
              itemStyle: {
                color: '#333',
                borderWidth: 2,
              },
            },
            title: {
              color: this.option.nameColor || '#fff',
              fontSize: this.option.nameFontSize ?? 20,
              show: true
            },
            data: [optionData],
          },
        ],
      }
      this.myChart.resize()
      this.myChart.setOption(option, true)
    },
    getColorGauge(colors, isGradient) {
      if (isGradient) {
        return colors.map((ele) => {
          return [
            Number(ele.postion),
            {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                {
                  offset: 0,
                  color: ele.color2,
                },
                {
                  offset: 1,
                  color: ele.color1,
                },
              ],
              global: false,
            },
          ]
        })
      } else {
        return colors.map(ele => {
          return [Number(ele.postion), ele.color1 ]
        })
      }
    },
  },
})
</script>
