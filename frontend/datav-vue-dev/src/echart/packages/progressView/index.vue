<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'
let _this
export default create({
  name: 'progressView',
  computed: {
    radiusArr() {
      if (this.option.radiusIspercentage) {
        // 如果使用百分比作为单位
        let insideRadius = this.option.insideRadius + '%'
        let outsideRadius = this.option.outsideRadius + '%'
        return [insideRadius, outsideRadius]
      } else {
        return [this.option.insideRadius, this.option.outsideRadius]
      }
    },
    seriesData() {
      let zeroObj = {
        //画中间的图标
        name: '',
        value: 0,
        label: {
          position: 'inside',
          backgroundColor: this.option.circleColor,
          borderRadius: this.option.circleSize,
          // padding: this.option.circleSize, // 可以控制圆的大小
          width: this.option.circleSize,
          height: this.option.circleSize,
          // borderWidth: 0,
          // borderColor: '#11C7EE',
        },
      }
      let realValueObj = {
        name: '',
        value: this.dataChart.value,
        itemStyle: {
          show: true,
          color: this.bgcolorIsGradient(),
        },
      }
      let residueValueObj = {
        //画剩余的刻度圆环
        name: '',
        value: (this.option.maxValue || 100) - this.dataChart.value,
        itemStyle: {
          color: this.option.backgroundColor,
        },
      }
      if (this.option.direction === 'clockwise') {
        return [realValueObj, zeroObj, residueValueObj]
      } else {
        return [residueValueObj, zeroObj, realValueObj]
      }
    },
  },
  created() {
    _this = this
  },
  methods: {
    updateChart() {
      // const optionData = this.deepClone(this.dataChart)
      let marginsTop = '45%';
      let marginsLfet = 'center';
      if(this.option.margins){
        marginsTop =  this.option.marginsTop+"%";
        marginsLfet = this.option.marginsLfet+"%";
      }
      const option = {
        // backgroundColor: '#ccc',
        title: {
          text:
            (
              this.option.maxValue &&
              (this.dataChart.value / this.option.maxValue) * 100
            ).toFixed() + '%',
          x: marginsLfet,
          y: marginsTop,
          textStyle: {
            color: this.option.color || '#fff',
            fontSize: this.option.fontSize ?? 36,
            fontFamily:this.option.fontFamily
          },
          subtextStyle: {
            color: '#999',
            fontSize: 16,
          },
        },
        color: ['#fff', 'rgba(255,255,255,.4)', 'transparent'],
        series: [
          {
            type: 'pie',
            startAngle: 90,
            center: ['50%', '50%'],
            radius: this.radiusArr,
            hoverAnimation: false,
            labelLine: {
              show: false,
            },
            data: this.seriesData,
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
