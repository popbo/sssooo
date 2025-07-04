<template>
  <div :class="b()" :style="styleSizeName" ref="pro">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'
let _this
export default create({
  name: 'progressBar',
  computed: {},
  created() {
    _this = this
  },
  methods: {
    updateChart() {
      const optionData = this.deepClone(this.dataChart)
      let rightp =
        _this.option.maxValue &&
        (optionData.value / _this.option.maxValue) *
          (this.$refs.pro.offsetWidth - 100)
      if (rightp <= 0) {
        rightp = 10
      }
      if (rightp >= this.$refs.pro.offsetWidth) {
        rightp = this.$refs.pro.offsetWidth - 100
      }
      const option = {
        grid: {
          left: 10,
          right: '2%',
          bottom: '2%',
          top: '2%',
          containLabel: true,
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'none',
          },
          formatter: function (params) {
            return params[0].name + ' : ' + params[0].value
          },
        },
        xAxis: {
          show: false,
          type: 'value',
        },
        yAxis: [
          {
            type: 'category',
            inverse: true,
            axisTick: 'none',
            axisLine: 'none',
            show: false,
            axisLabel: {
              textStyle: {
                color: '#ffffff',
                fontSize: '12',
              },
            },
          },
        ],
        series: [
          {
            name: '值',
            type: 'bar',
            zlevel: 1,
            itemStyle: {
              x: 400,
              normal: {
                show: false,
                barBorderRadius: 30,
                color: this.bgcolorIsGradient(),
              },
            },
            label: {
              position: [rightp, 0],
              show: this.option.tipsIsShow,
              offset: [0, -(this.option.barWidth + 30)],
              // offset: [optionData.value - 40, -(this.option.barWidth + 30)],
              fontSize: this.option.fontSize ?? 24,
              borderWidth: this.option.borderWidth,
              borderColor: this.option.borderColor || '#1EE7E7',
              padding: this.option.tipsPadding,
              borderRadius: 2,
              color: this.option.color || '#FFF',
              fontFamily:this.option.fontFamily,
              formatter: function (params) {
                const percentValue = (
                  _this.option.maxValue &&
                  (params.data / _this.option.maxValue) * 100
                ).toFixed()
                return percentValue + '%'
              },
            },

            barWidth: this.option.barWidth ?? 20,
            data: [optionData.value],
          },
          {
            name: '背景',
            type: 'bar',
            barWidth: this.option.barWidth ?? 20,
            barGap: '-100%',
            data: [this.option.maxValue ?? 100],
            itemStyle: {
              normal: {
                color: this.option.backgroundColor,
                barBorderRadius: 30,
              },
            },
          },
        ],
      }
      this.myChart.resize()
      this.myChart.setOption(option, true)
    },
    bgcolorIsGradient() {
      if (this.option.isGradient) {
        return new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          {
            offset: 0,
            color: this.option.bgColor1,
          },
          {
            offset: 1,
            color: this.option.bgColor2,
          },
        ])
      } else {
        return this.option.bgColor
      }
    },
  },
})
</script>
