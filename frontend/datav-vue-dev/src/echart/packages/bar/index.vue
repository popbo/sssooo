<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>

<script>
import create from '../../create'
export default create({
  name: 'bar',
  computed: {
    x2() {
      return this.option.gridX2 || 60
    },
  },
  data() {
    return {
      autoTimer: null,
      drainTimer:null,
      selectName:[],
      drainData:[],
      listAnimation:[],
      clearLoop:null
    }
  },
  watch:{
    drawConditionList:{
      handler(){
        this.updateChart()
      },
      deep:true,
    }
  },
  methods: {
    updateChart() {
      // // 清除定时器
      // if (!this.option.showAnimation || this.option.category) {
      //   clearInterval(this.autoTimer)
      // }
      // console.log(this.coincidentCondition,1111)
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
      let iconType = 'rect'
      if (this.option.iconType === 'yangOne') {
        iconType =
          'path://M25.1,27.9H5.09c-1.378,0-2.5-1.121-2.5-2.5V5.42c0.001-1.377,1.123-2.498,2.5-2.498H25.1c1.379,0,2.5,1.122,2.5,2.5V25.4C27.6,26.779,26.479,27.9,25.1,27.9z M5.09,3.922c-0.826,0-1.499,0.672-1.5,1.499V25.4c0,0.827,0.673,1.5,1.5,1.5H25.1c0.827,0,1.5-0.673,1.5-1.5V5.422c0-0.827-0.673-1.5-1.5-1.5H5.09zM11.1,9.417h8c1.104,0,2,0.895,2,2v7.99c0,1.105-0.896,2-2,2h-8c-1.104,0-2-0.895-2-2v-7.99C9.1,10.312,9.996,9.417,11.1,9.417z'
      }
      let handleIcon =
        'path://M-9.35,34.56V42m0-40V9.5m-2,0h4a2,2,0,0,1,2,2v21a2,2,0,0,1-2,2h-4a2,2,0,0,1-2-2v-21A2,2,0,0,1-11.35,9.5Z'
      if (this.option.handleStyle === 2) {
        handleIcon =
          'path://M512,512m-448,0a448,448,0,1,0,896,0a448,448,0,1,0,-896,0Z'
      }
      // 原方法
      // const optionData = this.deepClone(this.dataChart)
      //当数据为一组时调用单组循环配色
      const newList = this.deepClone(this.dataChart)
      let optionData = this.deepClone(this.dataChart)
      if (newList.series?.length === 1) {
        optionData.series[0].data = newList.series[0].data.map(
          (item, index) => {
            let rendering = this.coincidentCondition(item)
            if(rendering){
              if(rendering.switch){
                return {
                  value: item,
                  itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                        color: rendering.barColor1 // 起点颜色
                    }, {
                        offset: 1,
                        color: rendering.barColor2// 终点颜色
                    }])
                  },
                }
              }
              if(rendering.barColor){
                return {
                  value: item,
                  itemStyle: {
                    color: rendering.barColor
                  },
                }
              }
            }
            return {
              value: item,
              itemStyle: {
                color: this.getColor(
                  this.option.barColor.length - 1 >= index
                    ? index
                    : index % this.option.barColor.length
                ),
              },
            }
          }
        )
      }

      this.option.TOPN != 0 &&
        optionData.categories.splices(this.option.TOPN, this.option.Ns) &&
        optionData.series.forEach(element => {
          element.data.splices(this.option.TOPN, this.option.Ns)
        })
      let option = {
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
        //     backgroundColor: '#232630',
        //     textStyle: {
        //       fontSize: this.option.tipFontSize,
        //       color: this.option.tipColor || '#bbb',
        //     },
        //   }
        // ),
        tooltip: {
          show:this.option.tipShow,
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
          },
          backgroundColor: this.option.tipBackgroundColor,
          borderColor: this.option.tipBorderColor,
          borderWidth: this.option.tipBorder,
          formatter:(parms)=>{
            let lsit =  []
            parms.forEach(item=>{
              if(item.seriesName.slice(0,17)!=='liuguangAnimation'){
                lsit.push(item)
              }
            })
            let tData = ''
            lsit.forEach(item=>{
              tData = tData + item.marker + item.seriesName +'&nbsp&nbsp'+item.value + '<br/>'
            })
            let text = ''
            if(lsit.length>0){
              text = lsit[0].name + '<br/>' + tData
            }
           return text
          },
          textStyle: {
            fontSize: this.option.tipFontSize ?? 14,
            color: this.option.tipColor || '#fff',
          },
        },
        grid: {
          left: this.option.gridX ?? 20,
          top: this.option.gridY ?? 60,
          right: this.x2 ?? 60,
          bottom: this.option.gridY2 ?? 60,
        },
        legend: {
          icon: iconType,
          itemWidth: this.option.legendWidth ?? 14,
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
                itemStyle: {
                  color: this.getColor(index, true),
                },
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
        xAxis: [
          {
            type: this.option.category ? 'value' : 'category',
            name: this.option.xAxisName,
            nameTextStyle: {
              fontSize: this.option.xAxisNameSize ?? 14,
              fontFamily: 'SourceHanSansCN-Regular',
            },
            axisLine: {
              // 坐标轴X轴轴线相关设置
              show: true,
              lineStyle: {
                color: this.option.lineColor || '#fff',
                opacity: 0.3,
                width: 1,
              },
            },
            axisTick: {
              // 坐标轴X轴刻度线的设置
              show: true,
              length: 7,
              lineStyle: {
                color: this.option.lineColor || '#fff',
                opacity: 0.3,
                width: 1,
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
                color: this.option.nameColor || '#fff',
                fontSize: this.option.xNameFontSize ?? 14,
                // fontFamily: 'SourceHanSansCN-Regular',
              },
            },
          },
          {
            show: false,
            type: this.option.category ? 'value' : 'category',
            data: optionData.categories || [],
            axisTick: {
                show: false
            }
          }
        ],
        yAxis: [
          {
            type: this.option.category ? 'category' : 'value',
            name: this.option.yAxisName,
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
              // 坐标轴Y轴轴线相关设置
              show: true,
              lineStyle: {
                color: this.option.lineColor || '#fff',
                opacity: 0.3,
                width: 1,
              },
            },
            axisTick: {
              // 坐标轴Y轴刻度线的设置
              show: true,
              length: 7,
              lineStyle: {
                color: this.option.lineColor || '#fff',
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
          {
            show: false,
            type: this.option.category ? 'category' : 'value',
            data: optionData.categories || [],
            axisTick: {
                show: false
            }
          }
        ],
        // aria:{
        //   enabled:true,
        //   decal:{
        //     show:true
        //   }
        // },
        series: (() => {
          // const barColor = this.option.barColor || []
          let list = (optionData.series || []).map((ele, index) => {
            return Object.assign(ele, {
              type: 'bar',
              stack: this.option.isStack ? 'same' : ele.stack,
              barWidth: this.option.barWidth ?? 16,
              barMinHeight: this.option.barMinHeight || 0,
              barGap: this.option.barGap + `%`,
              itemStyle: this.ishasprop(
                !this.switchTheme,
                // 当数据组数大于1时使用原方法
                optionData.series.length === 1
                  ? {}
                  : {
                      // 原方法
                      color:(parms)=>{
                        let rendering = this.coincidentCondition(parms.data)
                        if(rendering){
                          if(rendering.switch){
                            return new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                              offset: 0,
                              color: rendering.barColor1 // 起点颜色
                            },{
                                offset: 1,
                                color: rendering.barColor2// 终点颜色
                            }])
                          }
                          if(rendering.barColor){
                            return rendering.barColor
                          }
                        }
                        return this.getColor(index)
                      }
                    },
                { barBorderRadius: this.option.barRadius || 0 ,
                }
              ),
              label: {
                show: this.vaildData(this.option.labelShow, false), //开启显示
                position: this.option.category ? 'right' : 'top', //在上方显示,
                formatter: name => this.getLabelFormatter(name),
                rotate: this.option.xLableRotate || 0,
                textStyle: {
                  //数值样式
                  fontSize: this.option.labelShowFontSize ?? 14,
                  color: this.option.labelShowColor || '#333',
                  fontWeight: this.option.labelShowFontWeight || 500,
                  fontFamily: 'SourceHanSansCN-Regular',
                },
              },        
            })
          })
          return list
        })(),
      }
      if(this.option.axisFalge&&!this.option.category){
        let axis = {
          min: this.option.axisMin,
          max: this.option.axisMax,
          // splitNumber:this.option.splitNumber,
          interval:this.option.splitNumber,
        }
        if(option.yAxis[0]){
          option.yAxis[0] = Object.assign(option.yAxis[0],axis)
        }
      }
      // 根据当前是否是竖展示提供滚动功能
      // if (this.option.category) {
      //   option.dataZoom = [
      //     //滑动条
      //     {
      //       yAxisIndex: 0, //这里是从y轴的0刻度开始
      //       show: false, //是否显示滑动条，不影响使用
      //       type: 'inside', // 这个 dataZoom 组件是 slider 型 dataZoom 组件
      //       startValue: 0, // 从头开始。
      //       endValue: 3, // 一次性展示几个。
      //     },
      //   ]
      // }
      // if (this.option.category && !!this.option.showAnimation) {
      //   this.autoScroll(
      //     option,
      //     this,
      //     this.option.animationInterval,
      //     this.option.rankCount
      //   )
      // } else {
      //   clearInterval(this.autoTimer)
      //   this.myChart.resize()
      //   this.myChart.setOption(option, true)
      // }
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
              filterMode: 'empty',
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
              filterMode: 'empty',
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
      if(this.drainTimer){
          clearInterval(this.drainTimer);
          this.drainTimer = null;
      }
      let seriesList = []
      this.listAnimation = []
      let count = 1;
      if(option.series){
          if(option.series.length>0){
            seriesList = this.deepClone(option.series)
          }
      }
      if(this.option.animation){
        if(this.option.isStack || seriesList.length==1){
          if(seriesList){
            if(seriesList.length>0){
              this.getAnimationOne(seriesList,optionData)
            }
          }
        }
      }
      option.series = [...seriesList,...this.listAnimation]
      this.myChart.resize()
      this.myChart.setOption(option, true)
      this.clearLoop && this.clearLoop()
      if(this.option.sliding){
        this.option.tipCarousel = false;
        this.option.animation = false;
        if(this.drainTimer){
            clearInterval(this.drainTimer);
            this.drainTimer = null;
        }
      }
      if(this.option.tipCarousel && !this.option.sliding){
        this.clearLoop = (tools.loopShowTooltip(this.myChart, option, {loopSeries: true,interval: this.option.tipCarouselTime})).clearLoop;
      }
      this.myChart.on('legendselectchanged', (params)=> {
        if(this.option.animation && !this.option.sliding){
          if(this.option.isStack){
            option.legend.selected = params.selected
            this.listAnimation = []
            let animationData = []
            seriesList.forEach(item=>{
              if(params.selected[item.name]){
                animationData.push(item)
              }
            })
            this.getAnimationOne(animationData,optionData)
            option.series = [...seriesList,...this.listAnimation]
            this.myChart.setOption(option, true)
          }else{
            if(seriesList.length>1){
              option.legend.selected = params.selected
              this.drainData = []
              let animationData = []
              let newSeries = this.deepClone(seriesList)
              seriesList.forEach(item=>{
                if(params.selected[item.name]){
                  animationData.push(item)
                }
              })
              this.getAnimation(animationData)
              this.drainData.forEach(ietm=>{
                newSeries.push(ietm)
              })
              if(this.drainTimer){
                clearInterval(this.drainTimer);
                this.drainTimer = null;
                this.myChart.dispatchAction({
                  type: 'legendUnSelect',
                  // 图例名称
                  name: 'liuguangAnimation'
                })
              }
              option.series = newSeries
              this.myChart.setOption(option,true);
              this.drainTimer = setInterval(()=>{
                if(count%2 == 0){
                  this.myChart.dispatchAction({
                      type: 'legendSelect',
                      // 图例名称
                      name: 'liuguangAnimation'
                    })
                }else{
                  this.myChart.dispatchAction({
                    type: 'legendUnSelect',
                    // 图例名称
                    name: 'liuguangAnimation'
                  })
                }
                count+=1;
              },this.option.animationTime-100);
            }
          }
        }
      });
      if(this.option.isStack){
        if(this.drainTimer){
          clearInterval(this.drainTimer);
          this.drainTimer = null;
        }
      }else{
        let listBar = []
        option.series.forEach(item=>{
          if(item.type==='bar'){
            listBar.push(item)
          }
        })
        if(listBar.length>1){
          if(this.option.animation){
            if(option.series.length<=1){
              return false
            }
            if(this.drainTimer){
              clearInterval(this.drainTimer);
              this.drainTimer = null;
            }
            this.drainData = []
            this.getAnimation(seriesList);
            this.drainData.forEach(ietm=>{
              option.series.push(ietm)
            })
            this.myChart.setOption(option,true);
            this.drainTimer = setInterval(()=>{
              if(count%2 == 0){
                this.myChart.dispatchAction({
                    type: 'legendSelect',
                    // 图例名称
                    name: 'liuguangAnimation'
                  })
              }else{
                this.myChart.dispatchAction({
                    type: 'legendUnSelect',
                    // 图例名称
                    name: 'liuguangAnimation'
                  })
              }
              count+=1;
            },this.option.animationTime-100);
          }else{
            if(this.drainTimer){
              clearInterval(this.drainTimer);
              this.drainTimer = null;
            }
          }
        }else{
          if(this.drainTimer){
            clearInterval(this.drainTimer);
            this.drainTimer = null;
          }
        }
      }
    },
    // 柱状图滚动
    // autoScroll(option, that, animationInterval, rankCount = 4) {
    //   // 若先前存在定时器先清除之前的定时器
    //   if (!!this.autoTimer) {
    //     clearInterval(this.autoTimer)
    //   }
    //   this.autoTimer = setInterval(function () {
    //     if (option.dataZoom[0].endValue == option.series[0]?.data.length - 1) {
    //       option.dataZoom[0].endValue = rankCount - 1
    //       option.dataZoom[0].startValue = 0
    //     } else {
    //       option.dataZoom[0].endValue = option.dataZoom[0].endValue + 1
    //       option.dataZoom[0].startValue = option.dataZoom[0].startValue + 1
    //     }
    //     that.myChart.setOption(option, true)
    //   }, animationInterval * 2000)
    // },
    getCoordsData(name,list,option,id){
      let startData = []
      if(id>0){
        for(let i = 0; i < id; i++){
          startData.push(option[i].data)
        }
      }
      let coords = []
      // console.log(list,9999)
      if(this.option.category){
        if(option.length>1){
          name.forEach((item,index)=>{
            if(id>0){
              let startIndex = 0
              startData.forEach(item=>{
                startIndex = item[index] + startIndex
              })
              coords.push({
                coords:[[startIndex,item],[(list[index]-4+startIndex),item]]
              })
            }else{
              coords.push({
                coords:[[0,item],[list[index]-4,item]]
              })
            }
          })
        }else{
          name.forEach((item,index)=>{
            coords.push({
                coords:[[0,item],[list[index].value-4,item]]
            })
          })
        }
      }else{
        if(option.length>1){
          name.forEach((item,index)=>{
            if(id>0){
              let startIndex = 0
              startData.forEach(item=>{
                startIndex = item[index] + startIndex
              })
              coords.push({
                coords:[[item,startIndex],[item,(list[index]-4+startIndex)]]
              })
            }else{
              coords.push({
                coords:[[item,0],[item,list[index]-4]]
              })
            }
          })
        }else{
          name.forEach((item,index)=>{
            coords.push({
                coords:[[item,0],[item,list[index].value-4]]
              })
          })
        }
      }
      // console.log('coords',coords)
      return coords
    },
    getAnimation(seriesList){
       if(this.option.category){
              seriesList.forEach(item=>{
                this.drainData.push({
                  type: 'bar',
                  name:'liuguangAnimation',
                  barWidth: this.option.barWidth ?? 16,
                  zlevel: 2,
                  animation: true ,
                  animationDuration: this.option.animationTime,
                  animationEasing: 'cubicOut' ,
                  animationEasing: 'linear',   
                  data:item.data || [],
                  yAxisIndex: 1,
                  itemStyle: {
                      normal: {
                          color: this.$echarts.graphic.LinearGradient(0, 0, 1, 0, [{
                              offset: 0,
                              color: 'rgba(255, 255, 255,0)'
                          }, 
                          { 
                            offset: 0.96, 
                            color: 'rgba(255, 255, 255,0)'
                          },
                          {offset: 1, color: this.option.animationColor}])
                      }
                  },
                })
              })
      }else{
        seriesList.forEach((item)=>{
          this.drainData.push({
            type: 'bar',
            name:'liuguangAnimation',
            barWidth: this.option.barWidth ?? 16,
            zlevel: 2,
            animation: true ,
            animationDuration: this.option.animationTime,
            // animationDurationUpdate: 100,
            animationEasing: 'cubicOut' ,
            animationEasing: 'linear',     
            data:item.data || [],
            xAxisIndex: 1,
            itemStyle: {
                normal: {
                    color: this.$echarts.graphic.LinearGradient(0, 1, 0, 0, [
                    {
                        offset: 1,
                        color: this.option.animationColor
                    }, 
                    { 
                      offset: 0.96, 
                      color: 'rgba(255, 255, 255,0)'
                    },
                    { 
                      offset: 0, 
                      color:'rgba(255, 255, 255,0)'
                    }
                  ])
                }
            },
          })
        })
      }
    },
    getAnimationOne(seriesList,optionData){
      seriesList.forEach((item,index)=>{
        this.listAnimation.push({
          showSymbol: false,
          name: item.name,
          type: "lines",
          polyline: true,
          coordinateSystem: "cartesian2d",
          zlevel: 10,
          effect: {
            show: true,
            smooth: true,
            period: this.option.animationTime/1000,
            symbol: 'rect',
            trailLength: 0.4,
            symbolSize:[this.option.barWidth,0.8]
          },
          lineStyle: {
            color: this.option.animationColor,
            width: 0,
            // opacity: 0,
            // curveness: 0,
            // cap: "round",
          },
          data: this.getCoordsData(optionData.categories,item.data,optionData.series,index)
        })
      })
    },
    coincidentCondition(value){
      const index = this.parseCondition(this.drawConditionList,value)
      return this.drawConditionList[index]
    },
    parseCondition(arr,value) {
      // 思路是在arr条件中每个条件项都是或的关系，因此要筛选出那个dataChart.value满足的条件
      // coincidentindex保存满足的条件的index值
      let coincidentindex
      // 首先判断是否配置了渲染条件，如果配置了那么渲染条件的数组长度大于0
      if (arr.length > 0) {
        for (let i = 0; i < arr.length; i++) {
          // 首先判断一下条件项有没有配置分支条件，即filter数组长度是否大于0
          //
          let filter = arr[i].filter
          if (filter.length > 0) {
            let logic = arr[i].logic // 条件分支之间的关系是且还是或  and 且  or 或
            let enterValue = arr[i].enterValue // 表达式的输入值是否是dataChart.value
            let filterResult = this.handleFilter(filter, logic, enterValue, value)
            if (filterResult) {
              coincidentindex = i
              break
            }
          }
        }
        return coincidentindex
      } else {
        // 没有配置渲染条件数组
      }
    },
    handleFilter(filter, logic, enterValue, chartValue) {
      const isAllNumber = function (chartValue, itemValue) {
        let num1 = Number(chartValue)
        let num2 = Number(itemValue)
        return !isNaN(num1) && !isNaN(num2)
      }
      const switchFnc = function (item) {
        let result
        switch (item.term) {
          case 'eq': // '='
            result = chartValue === item.value
            break
          case 'ne': // '!='
            result = chartValue !== item.value
            break
          case 'lt': // '<' 如果是小于号，那么要看比较的两个值是否都是数字,这个比较只限于数字的比较
            if (isAllNumber(chartValue, item.value)) {
              result = Number(chartValue) < Number(item.value)
            } else {
              result = false
            }
            break
          case 'le': // '<='
            if (isAllNumber(chartValue, item.value)) {
              result = Number(chartValue) <= Number(item.value)
            } else {
              result = false
            }
            break
          case 'gt': // '>'
            if (isAllNumber(chartValue, item.value)) {
              result = Number(chartValue) > Number(item.value)
            } else {
              result = false
            }
            break
          case 'ge': // '>='
            if (isAllNumber(chartValue, item.value)) {
              result = Number(chartValue) >= Number(item.value)
            } else {
              result = false
            }
            break
        }
        return result
      }
      // 先确定下是否是以dataChart.value作为表达式的输入值
      if (enterValue === 'dqz') {
        // 当条件分支间的关系为且
        if (logic === 'and') {
          // 如果为且的话那么就要求filter的每一项都返回true,那么可以选择数组的every方法
          const allResult = filter.every((item) => {
            return switchFnc(item)
          })
          return allResult
        } else if (logic === 'or') { // 当条件分支间的关系为否
          const allResult = filter.some((item) => {
            return switchFnc(item)
          })
          return allResult
        }
      }
    },
  },
  beforeDestroy(){
        if(this.drainTimer){
            clearInterval(this.drainTimer);
            this.drainTimer = null;
        }
    }
})
</script>
