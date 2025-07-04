<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'
import highcharts from 'highcharts'
// import columnPermissionsVue from '../../../page/list/dataCollection/data/columnPermissions.vue'
let _this
export default create({
  name: 'stereoscopicPie',
  data() {
    return {}
  },

  created() {
    _this = this
  },

  methods: {
    updateChart() {
      let each = highcharts.each,
      round = Math.round,
      cos = Math.cos,
      sin = Math.sin,
      deg2rad = Math.deg2rad;
      highcharts.wrap(highcharts.seriesTypes.pie.prototype, 'translate', function(proceed) {
      proceed.apply(this, [].slice.call(arguments, 1));
      // Do not do this if the chart is not 3D
      if(!this.chart.is3d()) {
        return;
      }
      let series = this,
        chart = series.chart,
        options = chart.options,
        seriesOptions = series.options,
        depth = seriesOptions.depth || 0,
        options3d = options.chart.options3d,
        alpha = options3d.alpha,
        beta = options3d.beta,
        z = seriesOptions.stacking ? (seriesOptions.stack || 0) * depth : series._i * depth;
      z += depth / 2;
      if(seriesOptions.grouping !== false) {
        z = 0;
      }
      each(series.data, function(point) {
        var shapeArgs = point.shapeArgs,
          angle;
        point.shapeType = 'arc3d';
        var ran = point.options.h;
        shapeArgs.z = z;
        shapeArgs.depth = depth * 0.75 + ran;
        shapeArgs.alpha = alpha;
        shapeArgs.beta = beta;
        shapeArgs.center = series.center;
        shapeArgs.ran = ran;
        angle = (shapeArgs.end + shapeArgs.start) / 2;
        point.slicedTranslation = {
          translateX: round(cos(angle) * seriesOptions.slicedOffset * cos(alpha * deg2rad)),
          translateY: round(sin(angle) * seriesOptions.slicedOffset * cos(alpha * deg2rad))
        };
      });
    });
    (function(H) {
      H.wrap(highcharts.SVGRenderer.prototype, 'arc3dPath', function(proceed) {
        // Run original proceed method
        var ret = proceed.apply(this, [].slice.call(arguments, 1));
        ret.zTop = (ret.zOut + 0.5) / 100;
        return ret;
      });
    }(highcharts));
      const optionData = this.deepClone(this.dataChart) || []
      // highChart是使用y属性来计算数值的，所以给每一项添加一个属性y
      optionData.forEach((item)=> {
        item.y = item.value,
        item.h = item.h || 0
      })
      let option = {
        chart: {
          animation: false,
          backgroundColor: 'none',
          type: 'pie', //饼图
          margin: [0, 0, 0, 0],
          options3d: {
            enabled: true, //使用3d功能
            alpha: 58, //延y轴向内的倾斜角度
            beta: 0,
          },
          events: {
            load: function () {
              var each = highcharts.each,
                points = this.series[0].points
              each(points, function (p, i) {
                console.log('pppppppp', p)
                p.graphic.attr({
                  translateY: -p.shapeArgs.ran || 0,
                })
                p.graphic.side1.attr({
                  translateY: -p.shapeArgs.ran || 0,
                })
                p.graphic.side2.attr({
                  translateY: -p.shapeArgs.ran || 0,
                })
              })
            },
          },
        },
        legend: {
          enabled: this.option.legend, // 关闭图例
          align: this.option.legendHorizontal, //水平方向位置
          verticalAlign: this.option.legendVertical, //垂直方向位置
          layout: this.option.legendOrient,
          symbolWidth: this.option.legendWidth ?? 14,
          symbolHeight: this.option.legendHeight ?? 14,
          symbolRadius: 0, // 图例标志的边框圆角。默认为 symbolHeight值的一半。设置为0显示为矩形
          itemMarginBottom: 8,
          useHTML: true,
          // labelFormat: '{name}&nbsp;&nbsp;&nbsp;&nbsp;{y}',
          labelFormatter: function () {
            return (
              '<div style="height: 14px;line-height: 14px">' +
              this.name +
              ':&nbsp;&nbsp;' +
              this.y +
              '</div>'
            )
          },
          itemStyle: {
            color: this.option.legendColor || '#fff',
            fontSize: this.setPx(this.option.legendFontSize ?? 12),
          },
        },
        tooltip: {
          backgroundColor:this.option.backgroundColor,
          borderColor:this.option.borderColor,
          borderWidth:this.option.border,
          headerFormat: '<b>{point.key}</b><br>',
          style: {
            color: this.option.tipColor,
            fontSize: this.option.tipFontSize + 'px',
          },
        },
        title: {
          enabled: false,
          text: this.option.isShowTitle ? this.option.titleText : undefined,
          align: this.option.textAlign,
          style: {
            color: this.option.titleTextColor
              ? this.option.titleTextColor
              : '#fff',
            fontSize: this.setPx(this.option.titleTextFontSize ?? 16),
          },
        },
        subtitle: {
          text: '',
        },
        plotOptions: {
          pie: {
            allowPointSelect: false, // 禁用点击
            cursor: 'pointer',
            depth: this.option.depth ?? 50,
            showInLegend: true,
            size:
              (this.option.isAnnulus
                ? this.option.outsideRadius
                : this.option.radius) * 2, // 外圈直径大小
            innerSize:
              (this.option.isAnnulus ? this.option.insideRadius : 0) * 2, // 内圈直径大小
            center: ['50%', '50%'],
            colors: this.newGetColor(),
            dataLabels: {
              useHTML: true,
              enabled: this.option.labelShow, //是否显示饼图的线形tip
              distance: 5,
              borderColor: '#007acc',
              align: 'center',
              // verticalAlign: 'top',
              position: 'right',
              format: '{point.y}',
              color: this.option.labelShowColor
                ? this.option.labelShowColor
                : '#ffffff',
              style: {
                textOutline: 'none',
                fontSize: this.setPx(this.option.labelShowFontSize ?? 16),
                fontWeight: this.option.labelShowFontWeight,
              },
            },
            states: {
              inactive: {
                enabled: false,
              },
            },
            point: {
              events: {
                click: e => {
                  console.log('click', e.point.options)
                  this.updateClick(e.point.options, 'clickFormatter')
                },
              },
            },
          },
        },
        credits: {
          enabled: false, // 禁用版权信息
        },
        series: [
          {
            type: 'pie',
            name: '数量',
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
          },
        ],
      }
      console.log(option,33333)
      const chart = highcharts.chart(this.$refs[this.id], option)
    },
    // 该函数未支持渐变色
    newGetColor() {
      const barColor = this.deepClone(this.option.barColor) || []
      const data = this.dataChart || []
      const colorlength = barColor.length
      const datalength = data.length
      const diffValue = datalength - colorlength
      if (diffValue > 0) {
        for (let i = 0; i < diffValue; i++) {
          let indexOfRemainder = i % colorlength
          barColor.push(barColor[indexOfRemainder])
        }
      }
      return barColor.map(item => item.color1)
    },
  },
})
</script>

<style scoped lang="scss"></style>
