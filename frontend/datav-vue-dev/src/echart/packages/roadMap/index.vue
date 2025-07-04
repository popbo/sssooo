<template>
  <div :class="b()" :style="styleSizeName">
    <div class="roadMap-s" :style="getMapStyle">
      <div class="move" v-html="getHtml" @click.stop="getLeft" v-show="option.switchFalge"></div>
      <div class="roadMap" :ref="id"></div>
      <div class="move move-right" v-html="getHtml" @click.stop="getRight" v-show="option.switchFalge"></div>
    </div>
  </div>
</template>
<script>
import create from '../../create'
export default create({
  name:'roadMap',
  data(){
    return {
      stations: [],
      branchStations:[],
      bLength:0,
      start:0,
      end:0,
      setData:[],
      linksData:[],
      timer:null,
      bEnd:0,
      nodeSvgType:[
        {
          svg:'<?xml version="1.0" standalone="no"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1693727340817" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="975" xmlns:xlink="http://www.w3.org/1999/xlink" width="200" height="200"><path d="M234.666667 512a277.333333 277.333333 0 1 1 554.666666 0 277.333333 277.333333 0 0 1-554.666666 0zM512 170.666667a341.333333 341.333333 0 1 0 0 682.666666 341.333333 341.333333 0 0 0 0-682.666666z" fill="#fff" p-id="976"></path><path d="M640 512a128 128 0 1 1-256 0 128 128 0 0 1 256 0z" fill="#fff" p-id="977"></path></svg>'
        },
        {
          svg:'<?xml version="1.0" standalone="no"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1700470644611" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5104" xmlns:xlink="http://www.w3.org/1999/xlink" width="200" height="200"><path d="M512 69.479l442.498 442.498-442.498 442.498-442.498-442.498 442.498-442.498z" p-id="5105"></path></svg>'
        },
        {
          svg:'<?xml version="1.0" encoding="utf-8"?><svg version="1.1" id="图层_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px"viewBox="0 0 24 24" style="enable-background:new 0 0 24 24;" xml:space="preserve"><style type="text/css">.st0{fill:#6682AC;}.st1{fill:#FFFFFF;}.st2{fill:#309EF8;}.st3{fill:#6D7E95;}</style><g><path class="st0" d="M12,4c4.4,0,8,3.6,8,8s-3.6,8-8,8s-8-3.6-8-8S7.6,4,12,4 M12,2C6.5,2,2,6.5,2,12s4.5,10,10,10s10-4.5,10-10S17.5,2,12,2L12,2z"/></g></svg>'
        }
      ],
      changeType:[
        {
          svg:'<?xml version="1.0" encoding="utf-8"?><svg version="1.1" id="图层_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px"viewBox="0 0 52 52" style="enable-background:new 0 0 52 52;" xml:space="preserve"><path class="st2" d="M13.2,26l21.2,21.5l4.3-4L21.3,26L38.8,8.5l-4-4C34.7,4.5,13.2,26,13.2,26z"/></svg>'
        },
        {
          svg:'<?xml version="1.0" encoding="utf-8"?><svg version="1.1" id="图层_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px"viewBox="0 0 52 52" style="enable-background:new 0 0 52 52;" xml:space="preserve"><path class="st2" d="M3.2,26l21.2,21.5l4.3-4L11.3,26L28.8,8.5l-4-4C24.7,4.5,3.2,26,3.2,26z"/><path class="st3" d="M23.2,26l16.3,16.5l3.3-3.1L29.4,26l13.4-13.4l-3.1-3.1C39.7,9.5,23.2,26,23.2,26z"/></svg>'
        },
      ]
    }
  },
  methods: {
    getLeft(){
      let max = Math.max(this.bLength,this.stations.length)
      if(max<=this.option.nodenumber){
        return false
      }
      if(this.bLength>length){
        this.bEnd = this.bEnd - 1;
        if(this.bEnd < 0){
          this.bEnd = 0;
          if(this.start===0){
            return false
          }
          this.start = this.start - 1;
        }
        this.updateChart()
      }else{
        this.bEnd = 0;
        this.start = this.start - 1;
        if(this.start<0){
          this.start = 0
          return false
        }
        this.updateChart()
      }
    },
    getRight(){
      let max = Math.max(this.bLength,this.stations.length)
      if(max<=this.option.nodenumber){
        return false
      }
      this.start = this.start + 1;
      let length = this.stations.length
      if(this.start>length-this.option.nodenumber){
        this.start = length-this.option.nodenumber
        if(this.bLength>length){
          this.bEnd = this.bEnd + 1
          if(this.bEnd>this.bLength-length){
            this.bEnd = this.bLength-length
            return false
          }
          this.updateChart()
        }
        return false
      }
      this.updateChart()
    },
    updateChart(){
      console.log(this.dataChart,6666)
      if(this.dataChart.stations){
        this.stations = this.deepClone(this.dataChart.stations)
        this.branchStations = this.deepClone(this.dataChart.branchStations)
      }
      let svg = ''
      let symbolSize = this.option.nodeiconSize
      let symbolColor = this.option.nodeiconColor
      if(this.option.noodeType!==4){
        svg = this.nodeSvgType[this.option.noodeType-1].svg
      }else{
        svg = this.option.nodeiconUrl
      }
      this.getOption()
      let option =  {
          title: {
            text: ''
          },
          tooltip: {
            show:false,
          },
          animation:false,
          animationDurationUpdate: 100,
          animationEasingUpdate: 'quinticInOut',
          // backgroundColor: '#d0d9c2',
          series: [
            {
              type: 'graph',
              layout: 'none',
              left:20,
              right:20,
              top:'middle',
              symbol:this.getSvg(svg),
              symbolSize: symbolSize,//节点大小为17
              roam: true,
              itemStyle: {//给所有节点的类型一个默认样式，特殊的可在其节点下单独修改样式
                normal: {
                  color: symbolColor,//颜色默认白色
                }
              },
              label: {//给所有的节点字体一个默认样式
                show: true,//显示
                position: this.option.fontSizePosition===2?"bottom":"top",//下方显示
                formatter:(params)=> {
                    let text = params.name;
                    let length = text.length;
                    let maxLineLength = 1; // 每行最多显示的字符数
                    let lineCount = Math.ceil(length / maxLineLength); // 计算需要几行
                    var lines = [];
                    for (var i = 0; i < lineCount; i++) {
                        let line = text.substr(i * maxLineLength, maxLineLength);
                        lines.push(line);
                    }
                    let test = [`{a|${lines.join('\n')}}`]
                    if(this.option.vertical){
                      test = [`{a|${text}}`]
                    }
                    return test;
                },
                rich:{
                  a:{
                    fontWeight:this.option.fontWeight,
                    fontFamily:this.option.fontFamily,
                    color:this.option.color,
                    fontSize: this.option.fontSize,//字体样式
                  },
                }
              },
              lineStyle: {//给所有连线一个默认样式
                normal: {
                  width: this.option.lineNumber,
                  color: this.option.lineColor
                }
              },
              data: this.setData,
              links: this.linksData,
            }
          ]
      }
      if(this.option.vertical){
        let parms = {
          rotate: 90,
          align: this.option.fontSizePosition===2?'right':'left',
          verticalAlign:'middle',
        }
        option.series[0].label = Object.assign(option.series[0].label,parms)
      }
      this.myChart.resize()
      this.myChart.setOption(option, true)
      if(this.timer){
        clearInterval(this.timer)
        this.timer = null
      }
      if(this.option.carousel){
        let max = Math.max(this.bLength,this.stations.length)
        if(max<=this.option.nodenumber){
          return false
        }
        this.timer = setInterval(()=>{
          this.start = this.start + 1;
          let length = this.stations.length
          if(this.bLength>length){
            if(this.start>length-this.option.nodenumber){
              this.start = length-this.option.nodenumber
              this.bEnd = this.bEnd + 1
              if(this.bEnd>this.bLength-length){
                this.bEnd = this.bLength-length
                this.start = 0;
                this.bEnd = 0
              }
            }
          }else{
            if(this.start>length-this.option.nodenumber){
              this.start = 0;
              this.bEnd = 0
            }
          }
          this.getOption()
          option.series[0].data = this.setData
          option.series[0].links = this.linksData
          this.myChart.resize()
          this.myChart.setOption(option, true)
        },this.option.time)
      }
    },
    getOption(){
      this.linksData = []
      this.setData = []
      this.end = this.start + this.option.nodenumber
      let datalist = this.stations.slice(this.start,this.end)
      this.getStationsData(datalist,{x:0,y:0})
      this.getlinksData(datalist)
      let branch = datalist.filter(item=>{
        return item.name === this.dataChart.whereBranchStation
      })
      let isBranch = this.stations.findIndex(item=>item.name===this.dataChart.whereBranchStation)
      let branchlist = []
      let qBlist = []
      let oneData = this.stations.findIndex(item=>item.name===datalist[0].name)
      let bifurcationNumber = this.option.bifurcationNumber
      if(this.option.bifurcation===1){
        bifurcationNumber =  this.option.bifurcationNumber*-1
      }
      if(oneData<=isBranch){
        if(branch.length>0){
          let params = {
            x:branch[0].x + 50,
            y:branch[0].y + bifurcationNumber
          }
          let L = datalist.length-1
          let branchIndex = datalist.findIndex(item=>item.name===this.dataChart.whereBranchStation)
          let s = L - branchIndex
          branchlist = this.deepClone(this.branchStations)
          let sBlist = branchlist.slice(0,s+this.bEnd)
          qBlist = sBlist
          this.getStationsData(qBlist,params)
          let branchlins = [...branch,...qBlist]
          this.getlinksData(branchlins)
        }
      }else{
        let params = {
          x:0,
          y:bifurcationNumber
        }
        qBlist = this.branchStations.slice(this.start-(isBranch+1)+this.bEnd,this.end-(isBranch+1)+this.bEnd)
        this.getStationsData(qBlist,params)
        this.getlinksData(qBlist)
      }
      this.bLength = isBranch + 1+ this.branchStations.length
      this.setData = [...datalist,...qBlist]
      console.log(this.setData,9999)
    },
    getStationsData(list,parms){
      list.forEach((item,index)=>{
        let params 
        if(this.option.carShow){
          if(item.name === this.dataChart.carPositionName){
            params = this.getCar()
          }else{
            if(this.option.transferShow){
              if(item.transfer){
                params =  this.getTransfer()
              }else{
                params = this.getNode()
              }
            }else{
              params = this.getNode()
            }
          }
        }else{
          if(this.option.transferShow){
            if(item.transfer){
              params =  this.getTransfer()
            }else{
              params = this.getNode()
            }
          }else{
            params = this.getNode()
          }
        }
        item = Object.assign(item,{
          y: 0 + parms.y,
          x: index*50 + parms.x
        },params)
      })
    },
    getlinksData(lits){
      for(let i =0; i < lits.length-1; i++){
        this.linksData.push({
          source: lits[i].name,
          target: lits[i+1].name,
        })
      }
    },
    getSvg(xml){
      let regex = /<path.*?d="(.*?)"/g;
      let match;
      let srcList = [];
      let path = ''
      while (match = regex.exec(xml)) {
        //match返回一个数组，数组中有两个数值
        //第一个是img整个标签字段，第二个是src中匹配的内容
        srcList.push(match[1]);
      }
      srcList.forEach(item=>{
        if(item){
          path = path + item
        }
      })
      if(path!==''){
        return 'path://' + path
      }
      return path
    },
    // 计算出svg
    svgWithStyle(link,svgSizeNew,svgColor) {
      let source = link
      let svgSize = svgSizeNew + 'px'
      let style = ` style = "width: ${svgSize} ; height: ${svgSize}; fill: ${svgColor}"`
      let reg = /((?<=<svg))/g
      let reg1 = /fill=\"(\S)*\"/g // 去除掉path标签中的fill,要不然无法改色
      let source1 = source.replace(reg1, 'fill')
      return source1.replace(reg, style)
    },
    getCar(){
      let parms = {
          symbol:this.getSvg(this.option.cariconUrl),
          symbolSize: this.option.carNumber,
          itemStyle: 
          {//给所有节点的类型一个默认样式，特殊的可在其节点下单独修改样式
            normal: {
              color: this.option.carColor,//颜色默认白色
            }
          },
        }
      return parms
    },
    getNode(){
      let parms = {}
      let svg = ''
      let symbolSize = this.option.nodeiconSize
      let symbolColor = this.option.nodeiconColor
      if(this.option.noodeType!==4){
        svg = this.nodeSvgType[this.option.noodeType-1].svg
      }else{
        svg = this.option.nodeiconUrl
      }
      parms = {
        symbol:this.getSvg(svg),
        symbolSize: symbolSize,
        itemStyle: 
        {//给所有节点的类型一个默认样式，特殊的可在其节点下单独修改样式
          normal: {
            color: symbolColor,//颜色默认白色
          }
        },
      }
      return parms
    },
    getTransfer(){
      let parms = {
          symbol:this.getSvg(this.option.transfericonUrl),
          symbolSize: this.option.transferSize,
          itemStyle: 
          {//给所有节点的类型一个默认样式，特殊的可在其节点下单独修改样式
            normal: {
              color: this.option.transferColor,//颜色默认白色
            }
          },
        }
      return parms
    }
  },
  computed:{
    getHtml(){
      let link = this.changeType[this.option.switchType-1].svg
      let params = this.svgWithStyle(link,this.option.switchNumber,this.option.switchColor)
      return params
    },
    getMapStyle(){
      if(this.option.vertical){
        return {
          transform: `rotateZ(90deg)`
        }
      }
      return {}
    },
  },
  beforeDestroy() {
    if(this.timer){
      clearInterval(this.timer)
      this.timer = null
    }
  },
})
</script>
<style lang="scss" scoped>
.roadMap-s{
  width: 100%;
  height: 100%;
  display: flex;
  padding: 0px 15px;
  justify-content: center;
  align-items: center;
  cursor: default;
  .move{
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .move-right{
    transform: rotateY(180deg);
  }
}
.roadMap{
  flex: 1;
  width: 100%;
  height: 100%;
}
</style>