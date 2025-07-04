<template>
  <div :class="b()" :style="styleSizeName">
    <div :ref="id" :style="styleChartName"></div>
  </div>
</template>
<script>
import create from '../../create'
import findTree from "xe-utils/findTree";
export default create({
  name: 'relationshipDiagram',
  data() {
    return {
      dataList:[],
      links:[]
    }
  },
  created() {
  
  },
  methods: {
    updateChart(){
      let datas = []
      let links = []
      this.dataList = []
      this.links = [];
      let symbolSize 
      if(this.option.type==='circle'){
        symbolSize = this.option.width
      }else{
        symbolSize = [this.option.rectWidth,this.option.rectHeight]
      }
      let styleData = {
        symbolSize: symbolSize,
          symbol:this.option.type,
          itemStyle: {
            normal: {
              borderColor: this.option.borderColor || '#30c0fa',
              borderWidth: this.option.boderWidth,
              // shadowBlur: 20,
              // shadowColor: "#04f2a7",
              color: this.option.backgroundColor || '#309ef8', // 背景颜色
              borderRadius:50
            },
          },
          label: {
            normal: {
              fontSize: this.option.fontSize,
              color: this.option.color,
              fontFamily:this.option.fontFamily,
              fontWeight:this.option.fontWeight
            },
          },
      }
      if(this.dataChart.name){
        this.dataList.push(Object.assign({name:this.dataChart.name},styleData))
      }
      this.getDataList(this.dataChart)
      this.getLinksList(this.dataChart,[this.dataChart])
      this.dataList.forEach((item,index)=>{
        if(index>0){
          if(this.option.list.length===0){
            item = Object.assign(item,styleData)
          }else{
            if(item.level){
              if(item.level<=this.option.list.length+1){
                item = Object.assign(item,this.getDataStyle(item.level-2))
              }else{
                item = Object.assign(item,this.getDataStyle(this.option.list.length-1))
              }
            }else{
              item = Object.assign(item,this.getDataStyle(this.option.list.length-1))
            }
          }
        }
      })
      datas = this.dataList
      links = this.links
      console.log(this.dataList,this.links,123)
      let option = {
        series: [
          {
            type: "graph",
            layout: "force",
            focusNodeAdjacency: true,
            roam: false,
            draggable: false,
            force: {
              initLayout:'circular',
              repulsion: 850,
              edgeLength: 100,
              gravity: 0.1 ,
              layoutAnimation: true ,
            },
            edgeLabel: {
              normal: {
                show: this.option.characters,
                // textStyle: {
                //   fontSize: 12,
                //   color:'red'
                // },
                formatter: "{c}",
              },
            },
            label: {
              normal: {
                show: true,
              },
            },
            data: datas,
            links: links,
          },
        ],
      };
      this.myChart.resize()
      this.myChart.setOption(option, true)
    },
    getDataList(list){
      if(list.children){
        if(list.children.length>0){
          list.children.forEach(item=>{
            this.dataList.push({
              name:item.name,
              level:item.level,
            })
            this.getDataList(item)
          })
        }
      }
    },
    getLinksList(list,data){
      if(list.children){
        if(list.children.length>0){
          list.children.forEach(itemt=>{
            let pidList = findTree(data,(item)=> item.id===itemt.id)
            console.log(pidList,5555)
            if(pidList){
              this.links.push({
                source:pidList.parent.name,
                target:pidList.item.name,
                value: pidList.item.association || '',
                lineStyle:{
                    color:this.option.lineColor || '#fff',
                    width:this.option.lineWidth,
                    type:this.option.linestyle,
                    curveness: this.option.lineShape,
                    opacity: 0.8,
                  },
                  label:{
                    show:this.option.characters,
                    normal: {
                      fontSize: this.option.charactersFontSize,
                      color:this.option.charactersColor || '#fff',
                      fontFamily:this.option.charactersFontFamily,
                      fontWeight:this.option.charactersFontWeight
                    },
                  }
              })
            }
            this.getLinksList(itemt,data)
          })
        }
      }
    },
    getDataStyle(index){
      let symbolSize
      if(this.option.list[index].type==='circle'){
        symbolSize = this.option.list[index].width
      }else{
        symbolSize = [this.option.list[index].rectWidth,this.option.list[index].rectHeight]
      }
      return {
        symbolSize: symbolSize,
        symbol:this.option.list[index].type,
        itemStyle: {
          normal: {
            borderColor: this.option.list[index].borderColor || '#30c0fa',
            borderWidth: this.option.list[index].boderWidth,
            color: this.option.list[index].backgroundColor || '#309ef8', // 背景颜色
            borderRadius: this.option.list[index].type==='rect'?this.option.list[index].fillet:0
          },
        },
        label: {
          normal: {
            fontSize: this.option.list[index].fontSize,
            color: this.option.list[index].color,
            fontFamily:this.option.list[index].fontFamily,
            fontWeight:this.option.list[index].fontWeight
          },
        },
      }
    }
  },
})
</script>
<style lang="scss" scoped>
</style>
