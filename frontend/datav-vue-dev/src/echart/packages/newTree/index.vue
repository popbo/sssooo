<template>
  <div :class="b()"
  :style="{
     '--fontSize':option.fontSize+'px',
     '--jiaofontSize':option.jiaofontSize+'px',
     '--jiaoHeight':(option.jiaofontSize*0.6)+'px',
     '--jiaoColor':option.jiaoColor || '#C0C4CC',
     '--svgSize':option.initialStatesObj.svgSize + 'px',
  }">
    <div :class="option.jiaoStyle===2?'new-b-svg':(option.jiaoStyle===3?'rectangle-svg':'old-b-svg')">
      <el-tree
        ref="newTree"
        :data="dataChart"
        node-key="id"
        :accordion='true'
        :props="defaultProps"
        @node-click="handleNodeClick"
        :default-expanded-keys="expandedArray"
        :expand-on-click-node="true"
        :render-content="renderFun"
        :class="'newTree' + this.id.substring(0, 8)"
        :highlight-current='true'
        :current-node-key="currentNodekey"
        @node-expand="nodeExpand"
        @node-collapse="nodeCollapse"
      >
      </el-tree>
    </div>
  </div>
</template>

<script>
import create from '../../create'
import findTree from "xe-utils/findTree";
// import { EventBus } from '@/bus.js'
export default create({
  name: 'newTree',
  data() {
    return {
      defaultProps: {
        children: 'children',
        label: 'label',
      },
      currentNodekey:'',
      expandedArray: [],
      flage:true
    }
  },
  props:{
    id:String,
    option: Object,
    component:Object,
    upDateObj:Object
  },
  mounted() {
    this.createNewTreeStyle()
    if(this.flage){
      this.getSelet(this.$attrs.comParams)
    }
  },
  watch:{
    option:{
      handler(){
        if(this.$route.name==='build'){
          this.createNewTreeStyle()
        }
      },
      deep:true
    },
  },
  methods: {
    getSelet(deviceId){
      setTimeout(() => {
        this.$refs.newTree.setCheckedKeys([deviceId])
        this.$refs.newTree.setCurrentKey(deviceId)
        this.currentNodekey = deviceId;
        // console.log(8888,this.$attrs.comParams)
        let node = this.$refs.newTree.getCheckedNodes()[0]
        let pid,data
        if(node){
          let nwepid = ''
          if(deviceId){
            if(deviceId){
              let pidList = findTree(this.dataChart,(item)=> item.id===deviceId)
              if(pidList){
                if(pidList.parent){
                  if(pidList.parent.id){
                    nwepid = pidList.parent.id
                  }
                }
              }
            }
          }
          if(node.children){
            if(node.children.length > 0 ) {
              pid = node.children[0].pid || nwepid
              data = node.children[0]
            } else {
              pid = node.pid || nwepid
              data = node
            }
          }else{
            pid = node.pid || nwepid
            data = node
          }
        }
        if(data){
          this.expandedArray.push(pid);
          this.updateClick(data, 'initFinishFormatter')
        }
      },0)
    },
    nodeExpand(data) {
      console.log(6666,data)
      if (data.id) {
        this.expandedArray.push(data.id);
      }
    },
    nodeCollapse(data) {
      if (data.id) {
        this.expandedArray.splice(this.expandedArray.indexOf(data.id), 1);
      }
    },
    handleNodeClick(data, node) {
      console.log('node点击', node)
      if (!(data.children && data.children.length > 0)) {
        console.log('data==>', data)
        this.updateClick(data, 'clickFormatter')
      }
    },
    renderFun(h, { node, data, store }) {
      console.log(999,node)
      let svgStyle = ''
      if(this.option.initialStatesObj){
        let source = this.option.initialStatesObj.link
        let svgSize =this.option.initialStatesObj.svgSize + 'px'
        let svgColor = this.option.initialStatesObj.svgColor
        let style = ` style = "width: ${svgSize} ; height: ${svgSize}; fill: ${svgColor}"`
        let reg = /((?<=<svg))/g
        let reg1 = /fill=\"(\S)*\"/g // 去除掉path标签中的fill,要不然无法改色
        let source1 = source.replace(reg1, 'fill')
        svgStyle = source1.replace(reg, style)
      }
      return h(
        'span',{
          style:{
            width: '100%',
          }
        },[h('span',{
          domProps:{
            innerHTML:svgStyle
          },
          class:{
            'new-t-svg':true,
            'show-t-svg':this.option.jiaoStyle!==2
          },
          style:{
            display:node.childNodes.length===0?'none':'inline-block',
            position: 'relative',
            left: `-${this.option.initialStatesObj.svgSize}px`,
            verticalAlign: 'middle',
          },
          on:{
            click() {
              console.log("dsdsdsdsd")
            },
          }
        }),h('span',{
          style: {
            display: 'inline-block',
            height: this.setPx(this.option.titleHeight || 69),
            width: '100%',
            lineHeight: this.setPx(this.option.titleHeight || 69),
            textAlign: this.option.titlePosition || 'left',
            paddingLeft: this.option.jiaoStyle===2?this.setPx(this.option.titlePaddingLeft+8 || 8):this.setPx(this.option.titlePaddingLeft || 0),
            fontFamily: this.option.fontFamily,
            fontSize: this.setPx(this.option.fontSize || 16),
            paddingRight: '10px',
            marginLeft: this.option.jiaoStyle===2?(-this.option.initialStatesObj.svgSize) + 'px':0
          },
        }, node.label)]
      )
    },
    createNewTreeStyle() {
      let styleText = `
        .newTree${this.id.substring(0, 8)} {
          color: ${this.option.color || '#FBFBFB'}
        }
        .newTree${this.id.substring(
          0,
          8
        )} .el-tree-node .el-tree-node__content { 
          background-color: ${this.option.externalBgc || '#2c4162'} !important;
          }
        .newTree${this.id.substring(
          0,
          8
        )} .el-tree-node .el-tree-node__children .el-tree-node__content  { 
          background-color: ${this.option.interiorBgc || '#1c2b3c'} !important; 
        }
        .newTree${this.id.substring(
          0,
          8
        )} .el-tree-node.is-current>.el-tree-node__content {
          color: ${this.option.currentColor || '#409EFF'}
        }
        `
      //追加css
      let styleTag = document.createElement('style')
      styleTag.type = 'text/css'
      styleTag.id = 'newTree' + this.id.substring(0, 8)
      styleTag.innerHTML = styleText
      document.getElementsByTagName('head')[0].appendChild(styleTag)
    },
  },
})
</script>

<style scoped lang="scss">
.avue-echart-newTree {
  overflow-y: scroll;
}
.old-b-svg{
  /deep/ .el-tree-node__expand-icon{
    position: relative;
    z-index: 1000;
    font-size: var(--jiaofontSize);
    color: var(--jiaoColor);
  }
}
/deep/ .show-t-svg{
  display: none !important;
}
.new-b-svg{
  /deep/.el-tree-node__expand-icon{
    position: relative;
    z-index: 1000;
    width: var(--svgSize);
    height:var(--svgSize);
    margin-left: 8px;
    background: transparent;
  }
  /deep/.el-icon-caret-right:before{
    content:''
  }
}
.rectangle-svg{
  /deep/.el-tree-node__expand-icon{
    width: var(--jiaofontSize);
    height:var(--jiaoHeight);
    background: var(--jiaoColor);
    margin-left: 8px;
    margin-right: 8px;
    padding: 0;
  }
  /deep/.el-icon-caret-right:before{
    content:''
  }
}
/deep/.is-leaf{
  color: transparent !important;
  cursor: default;
}
/deep/.expanded+span{
  .new-t-svg{
    transform: rotate(90deg);
    transition: transform .3s ease-in-out,-webkit-transform .3s ease-in-out;
  }
}
/deep/ .el-tree {
  .el-tree-node {
    .el-tree-node__content {
      height: unset;
      // background-color: #2c4162;
    }
    // .el-tree-node__children {
    //   .el-tree-node__content {
    //     background-color: #1c2b3c;
    //   }
    // }
  }
}
</style>
