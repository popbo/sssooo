<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'
let _this
export default create({
  name: 'pictorialbar',
  created() {
    _this = this
  },
  methods: {
    hexToRgb(val,transparency=1) {   //HEX十六进制颜色值转换为RGB(A)颜色值
      // 16进制颜色值的正则
      var reg = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/;
      // 把颜色值变成小写
      var color = val.toLowerCase();
      var result = '';
      if (reg.test(color)) {
          // 如果只有三位的值，需变成六位，如：#fff => #ffffff
          if (color.length === 4) {
              var colorNew = "#";
              for (var i = 1; i < 4; i += 1) {
                  colorNew += color.slice(i, i + 1).concat(color.slice(i, i + 1));
              }
              color = colorNew;
          }
          // 处理六位的颜色值，转为RGB
          var colorChange = [];
          for (var i = 1; i < 7; i += 2) {
              colorChange.push(parseInt("0x" + color.slice(i, i + 2)));
          }
          result = "rgba(" + colorChange.join(",") + ',' + transparency + ")";
          return { rgb: result, r: colorChange[0], g: colorChange[1], b: colorChange[2] };
      } else {
          result = val;
          let r = this.rgbaNum(result,0);
          let g = this.rgbaNum(result,1);
          let b = this.rgbaNum(result,2);
          if(!r){
            return ''
          }
          return { rgb: `rgba(${r},${g},${b},${transparency})` };
      }
    },
    rgbaNum(rgba, index) {
      let val = rgba.match(/(\d(\.\d+)?)+/g);
      if(val){
        return val[index] || '';
      }
      return val;
    },
    updateChart() {
      let self = this
      const optionData = this.deepClone(this.dataChart)
      // const symbol = this.validatenull(this.option.symbol) ? '' : 'image://' + this.option.symbol
      const color = this.option.color || '#fff'
      // const fontSize = this.option.fontSize || 20
      const pictorialBarColor = this.option.pictorialBarColor
      var maxData = 0
      // function symbol() {
      //   if (_this.option.geometricOrImage === 'geometric') {
      //     return _this.option.symbol
      //   } else {
      //     return _this.validatenull(_this.option.symbol)
      //       ? ''
      //       : 'image://' + _this.option.symbol
      //   }
      // }
      optionData.forEach(ele => {
        if (ele.value > maxData) maxData = ele.value
      })
      const option = {
        // tooltip: (() => {
        //   return Object.assign(
        //     (() => {
        //       if (this.formatter) {
        //         return {
        //           formatter: name => {
        //             return this.formatter(name, this.dataChart)
        //           },
        //         }
        //       }
        //       return {}
        //     })(),
        //     {
        //       textStyle: {
        //         fontSize: this.option.tipFontSize,
        //         color: this.option.tipColor || '#fff',
        //       },
        //     }
        //   )
        // })(),
        tooltip: {
          show:this.option.tipShow,
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
          },
          backgroundColor: this.option.tipBackgroundColor,
          borderColor: this.option.tipBorderColor,
          borderWidth: this.option.tipBorder,
          textStyle: {
            fontSize: this.option.tipFontSize ?? 14,
            color: this.option.tipColor || '#fff',
          },
          formatter:(parms)=>{
           return parms[0].name + '<br/>' + parms[0].marker + parms[0].value
          }
        },
        xAxis: {
          show: this.vaildData(this.option.xAxisShow, true),
          max: maxData,
          splitLine: { show: false },
          offset: 10,
          axisTick: { show: false },
          axisLine: { show: false },
          axisLabel: {
            margin: 10,
            textStyle: {
              color: this.option.nameColor || '#333',
              fontSize: this.option.xNameFontSize ?? 14,
              fontFamily: 'SourceHanSansCN-Normal',
            },
          },
        },
        yAxis: {
          data: (() => {
            return (Array.isArray(optionData) ? optionData : []).map(ele => {
              return ele.name
            })
          })(),
          show: this.vaildData(this.option.yAxisShow, true),
          inverse: true,
          axisTick: { show: false },
          axisLine: { show: false },
          axisLabel: {
            margin: 10,
            textStyle: {
              color: this.option.nameColor || '#333',
              fontSize: this.option.yNameFontSize ?? 14,
              fontFamily: 'SourceHanSansCN-Normal',
            },
          },
        },
        grid: {
          top: this.option.gridY ?? 10,
          bottom: this.option.gridY2 ?? 10,
          left: this.option.gridX ?? 70,
          right: this.option.gridX2 ?? 90,
        },
        series: [
          {
            type: 'pictorialBar',
            itemStyle: {
              normal: {
                color(params) {
                  let color
                  // if (params.dataIndex == 4) {
                  //   color = '#00D68A'
                  // } else if (params.dataIndex == 3) {
                  //   color = '#BAE6FF'
                  // } else if (params.dataIndex == 2) {
                  //   color = '#2F54EB'
                  // } else if (params.dataIndex == 1) {
                  //   color = '#1EE7E7'
                  // } else {
                  //   color = '#309EF8'
                  // }

                  // color = pictorialBarColor[params.dataIndex] ? pictorialBarColor[params.dataIndex].color1 : '#309EF8'
                  // return color
                  const length = pictorialBarColor.length
                  if (params.dataIndex < length) {
                    // 如果数据的dataIndex没有超过颜色数组的长度
                    color = pictorialBarColor[params.dataIndex].color1
                  } else {
                    // 如果超过就对颜色数组长度取余数循环使用
                    let index = params.dataIndex % length
                    color = pictorialBarColor[index].color1
                  }
                  return color
                },
              },
            },
            symbol:
              this.option.geometricOrImage === 'geometric'
                ? this.option.symbol
                : 'image://' + this.option.imgSymbol,
            // symbol: symbol(),
            symbolRepeat: 'fixed',
            symbolClip: true,
            symbolSize: this.option.symbolSize || [10, 30],
            symbolMargin: [3, 1],
            symbolBoundingData: maxData,
            data: (() => {
              return (Array.isArray(optionData) ? optionData : []).map(ele => {
                return ele.value
              })
            })(),
          },
          {
            type: 'pictorialBar',
            itemStyle: {
              normal: {
                color(params) {
                  let color
                  // if (params.dataIndex == 4) {
                  //   color = '#00D68A'
                  // } else if (params.dataIndex == 3) {
                  //   color = '#BAE6FF'
                  // } else if (params.dataIndex == 2) {
                  //   color = '#2F54EB'
                  // } else if (params.dataIndex == 1) {
                  //   color = '#1EE7E7'
                  // } else {
                  //   color = '#309EF8'
                  // }
                  // color = pictorialBarColor[params.dataIndex] ? pictorialBarColor[params.dataIndex].color1 : '#309EF8'
                  // return color
                  const length = pictorialBarColor.length
                  if (params.dataIndex < length) {
                    // 如果数据的dataIndex没有超过颜色数组的长度
                    color = pictorialBarColor[params.dataIndex].color1
                  } else {
                    // 如果超过就对颜色数组长度取余数循环使用
                    let index = params.dataIndex % length
                    color = pictorialBarColor[index].color1
                  }
                  return self.hexToRgb(color,0.2).rgb
                },
              },
            },
            label: {
              normal: {
                show: true,
                position: 'right',
                offset: [15, 0],
                textStyle: {
                  color: color,
                  fontSize: this.option.fontSize ?? 20,
                },
              },
            },
            animationDuration: 0,
            symbol:
              this.option.geometricOrImage === 'geometric'
                ? this.option.symbol
                : 'image://' + this.option.imgSymbol,
            // symbol: symbol(),
            symbolRepeat: 'fixed',
            symbolSize: this.option.symbolSize || [10, 30],
            // symbolSize: [10, 30],
            symbolMargin: [3, 1],
            symbolBoundingData: maxData,
            data: (() => {
              return (Array.isArray(optionData) ? optionData : []).map(ele => {
                return ele.value
              })
            })(),
          },
        ],
      }

      this.myChart.resize()
      this.myChart.setOption(option, true)
    },
  },
})
</script>
