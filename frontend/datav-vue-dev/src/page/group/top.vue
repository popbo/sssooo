<template>
  <div class="build-top-nav">
    <p class="tuceng_p" v-show="contain.menuFlag&&contain.menuShow">图层</p>
    <el-menu class="nav"
             mode="horizontal"
             background-color="#212528"
             text-color="#fff"
             active-text-color="#409EFF"
             @mousedown="contain.handleMouseDown">
      <el-submenu :index="index+''"
                  v-for="(item,index) in baseList"
                  :key="index"
                  :popper-class="'drop-list'"
                  >
        <template slot="title">
          <el-tooltip effect="dark"
                      :content="item.label"
                      placement="top">
            <i :class="'nav__icon iconfont '+item.icon"></i>
          </el-tooltip>
          <span class="nav__title">{{ item.label }}</span>
        </template>
        <div :style="{width:item.labelName!=='collectionMenu'?'390px':'309px'}">
          <template v-if="item.labelName!=='collectionMenu'">
          <el-menu-item v-for="(citem,cindex) in item.children"
                        @click="handleAdd(citem.option,true)"
                        :key="cindex"
                        class="menu-inline"
                        :index="`${index}-${cindex}`">
            <div class="usehove"
                 :draggable="true"
                 @dragstart="dragStart($event, citem.option)"
            >
              <img :src="citem.option.img"
                   class="inside-img">
              <div class="bottom-text">{{citem.label}}</div>
            </div>
          </el-menu-item>
          </template>
          <template v-else>
            <div class="top_collect_box">
              <ul class="collectTypeBox" :style="{height:collectListHeight}">
                <li :index="item.id"
                  :key="item.id"
                  v-for="(item,index) in collectTypeList"
                  :class="{'is-active':item.id===activeId}"
                  :title="item.name"
                  @click="handleSelect(item.id)">
                  <div class="collectMenuList" :style="{'z-index':collectTypeList.length-index}">
                  {{item.lessName}}
                  </div>
                </li>
              </ul>
              <div class="collectContent">
              <el-menu-item v-for="(citem,cindex) in item.children"
                        :key="cindex"
                        class="menu-inline menu_collect"
                        :index="`${index}-${cindex}`"
                        handleName="menuitem"
                        @click="handleCollectAdd(citem,$event)"
                        draggable 
                        @dragstart.native="dragCollectStart($event, citem)"
                        @dragend.native="isDrag=false"
                        >
                        <div class="icon_opera" v-show="!isDrag">
                          <i class="el-icon-edit" title="编辑" @click.stop="editCollect(citem)"></i>
                          <i class="el-icon-delete" title="删除" @click.stop="deleteCollect(citem)"></i>
                        </div>
                <div class="usehove">
                <div class="img_box">
                  <img :src="citem.url"
                      class="inside-img">
                </div>
                  <div class="bottom-text" :title="citem.name">{{citem.name}}</div>
                </div>
              </el-menu-item>
              </div>
            </div>
          </template>
        </div>
      </el-submenu>
    </el-menu>
    <p class="set_p" v-show="contain.menuFlag && contain.paramsShow">{{ contain.activeObj.name? contain.activeObj.name : '页面设置'}}</p>
    <collectLayer ref="collectRef" :type="'edit'"></collectLayer>
  </div>
</template>

<script>
// import { uuid } from '@/utils/utils'
import { uuid } from '@/utils/utils.min.js'
import { getCollectBytype,postDeleteCollect,getCollectType } from "@/api/collection"
// import { getObj } from '@/api/visual';
import baseList from '@/option/base'
import collectLayer from "@/page/group/collectLayer"
export default {
  inject: ["contain"],
  provide () {
    return {
      contain: this.contain
    };
  },
  components:{
    collectLayer
  },
  data () {
    return {
      baseList: baseList,
      visualId:'',
      configId:'',
      collectTypeList:[],
      activeId:'',
      isDrag:false
    }
  },
  async created(){
    // this.visualId=this.$route.params.id
    // const {data} = await getObj(this.visualId)
    // if(data.data){
    //   this.configId=data.data.config.id
    // }
    this.getCollectTypeList()
  },
  computed:{
    collectListHeight(){
      const h=window.innerHeight;
      const relH=(this.collectTypeList.length-1)*84+58+36
      let finH=''
      if(relH>=h){
        finH=h+'px'
      }else{
        finH=relH+'px'
      }
      return finH
    }
  },
  methods: {
    handleCollectAdd(item,e){
      if(e.$el.getAttribute('handleName')==='menuitem'){
        let collectRender={
          menu:false,
          isname:false
        }
        let collectionTemp=[] //临时数据
        const detailData=JSON.parse(item.detail)
        detailData.forEach(ele=>{
          let collectObj = this.deepClone(ele);
          collectObj.index = uuid();
          collectionTemp.push(collectObj)
          // this.contain.nav.unshift(collectObj);
        })
        collectRender.children=collectionTemp;
        collectRender.name=item.name
        collectRender.index=uuid()
        this.contain.nav.unshift(collectRender);
        console.log('nav',this.contain)
      }
      
    },
    handleAdd (option, first = false) {
      let obj = this.deepClone(option);
      obj.left = 0;
      obj.top = 0
      obj.index = uuid();
      // 全局配置
      if(obj.option){
        if(obj.option.barColor){
          if(this.contain.config.overallSituationColor){
            if(this.contain.config.overallSituationColor.length>0){
              obj.option.barColor = this.deepClone(this.contain.config.overallSituationColor)
            }
          }
        }
        // 象形图时
        if(obj.option.pictorialBarColor){
          if(this.contain.config.overallSituationColor){
            if(this.contain.config.overallSituationColor.length>0){
              obj.option.pictorialBarColor = this.deepClone(this.contain.config.overallSituationColor)
            }
          }
        }
      }
      if(this.contain.showTips) {
        
        // 如果现在处于弹框的编辑页面，那么要首先找到该弹窗组件对象
        let tipsObj = this.contain.findlist(this.contain.tipsIndex) || {}
        //  记录父亲组件tips的index值
        this.$set(obj, 'parentTipsIndex', this.contain.tipsIndex)
        tipsObj.list.unshift(obj);
        // this.contain.$refs.editTips.tipsNavList = this.contain.findlist(this.contain.tipsIndex).list
      } else {
        if (first) {
        this.contain.nav.unshift(obj);
      } else {
        this.contain.nav.push(obj);
      }
      // console.log('的点点滴滴',first,this.contain.nav)
    }
    },
    dragCollectStart(ev, item) {
      this.isDrag=true
      const dragDataTemp={
        type:'collect',
        name:item.name,
        dragDataTempArr:[]
      }
      const detailDataJson=JSON.parse(item.detail)
      console.log('拖拽的数据',item.detail)
      detailDataJson.forEach(ele=>{
        let collectObj = this.deepClone(ele);
        dragDataTemp.dragDataTempArr.push(collectObj);
      })
      ev.dataTransfer.setData('text', JSON.stringify(dragDataTemp))
      // let obj = this.deepClone(option)
    },
    dragStart(ev, option) {
      let obj = this.deepClone(option)
      // console.log('拖拽',obj,JSON.stringify(obj))
      ev.dataTransfer.setData('text', JSON.stringify(obj))
    },
    //获取收藏的数据列表
    getColletData(){
      getCollectBytype(this.activeId).then(res=>{
       const info=res.data
      //  console.log('获取收藏的数据列表',info)
        if(info.success){
          this.baseList.forEach(x => {
            if(x.labelName==='collectionMenu'){
              x.children=info.data
            }
          });
        }
      })
    },
    //编辑收藏列表
    editCollect(row){
      this.$refs.collectRef.openDialog(row)
    },
    //删除收藏列表
    deleteCollect(row){
      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
      const delParam={
        ...row,
        detail:JSON.stringify(row.detail)
      }
      console.log('删除',delParam)
      postDeleteCollect(delParam).then(res=>{
        if(res.data.success){
          this.$message.success('删除成功')
          this.getColletData()
        }else{
          this.$message.error(res.data.msg)
        }
      })
      }).catch(() => {
      });
    },
    //获取收藏列表分类
    async getCollectTypeList(){
      const {data} = await getCollectType()
      if(data.success){
        this.collectTypeList=data.data
        this.activeId = (this.collectTypeList[0] || {}).id;
        // console.log('dddddd',this.activeId)
        this.getColletData()
    }
      // console.log('获取收藏列表分类',this.collectTypeList)
  },
    //点击事件
    handleSelect(key){
      this.activeId = key;
    this.getColletData()
    }
  },
  mounted(){
    this.$bus.$on('submitCollect',()=>{
      this.getColletData()
      this.getCollectTypeList()
    })
  },
  beforeDestroy() {
    this.$bus.$off('submitCollect')
  }
}
</script>

<style lang='scss'>
@import '~@/styles/buildVariables.scss';
.build-top-nav {
  height: 45px;
  display: flex;
  .set_p, .tuceng_p{
    text-align: center;
    color: $fontc1;
    line-height: 45px;
    background-color: $bgc2;
    font-family: MicrosoftYaHei;
    font-size: 14px;
  }
  .tuceng_p {
    width: 187px;
    border-right: 1px solid $borderc2;
  }
  .set_p {
    width: 340px;
    border-left: 1px solid $borderc2;
  }
  .nav {
    flex: 1;
    border-bottom: 0 !important;
    height: 45px;
    line-height: 45px;
    overflow: hidden;
    background-color: $bgc2 !important;
  }
  .nav__icon {
    margin-right: 5px;
  }
  .nav__title {
    font-family: $fontFamily1;
    font-size: $fontSize1;
    color: $fontc1;
    padding-left: 7px;
    padding-right: 16px;
  }
  .nav .el-submenu .el-submenu__title,
  .nav .el-menu-item {
    height: 45px;
    line-height: 45px;
    font-size: 12px;
  }
  .el-submenu__title {
    width: 131px;
    font-family: $fontFamily1;
    font-size: $fontSize1;
    color: $fontc1;
    background-color: $bgc2 !important;
    &:hover {
      background-color: $bgc4 !important;
      i, span {
        color: #fff;
      }
    }
  }
  .el-menu-item {
    height: 100px !important;
  }
}
.el-menu--horizontal {
  .menu-inline {
    text-align: center;
    display: inline-block !important;
    &.menu_collect{
      .inside-img:hover {
          border-color: transparent;
        }
      // &:hover{
      //   background: #1a1e20;
      //   .icon_opera{
      //     i{
      //        color:#006eff
      //     }
      //   }
      // }
    }
  }
  .bottom-text {
    color: #b1b1b1;
    width: 100%;
    overflow:hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  .inside-img {
    width: 110px;
    height: 70px;
    border: 2px solid transparent;
    box-sizing: border-box;
  }
  .inside-img:hover {
    border-color: #006eff;
  }
  .usehove:hover {
    .bottom-text {
      color: #fff;
    }
  }
}
.icon_opera{
  // position: absolute;
  // right: 0;
  // top: 0;
  text-align: right;
  color: #b1b1b1;
  i{
    color: #b1b1b1;
  }
  i:focus,i:visited,i:active{
    color: #b1b1b1;
  }
}
.top_collect_box{
  display: flex;
  background: #232630;
  .collectTypeBox{
    // margin-top: 10px;
    // height: 300px;
    overflow-y: auto;
    overflow-x: hidden;
    li{
    width:35px;
    height: 84px;
    line-height: 16px;
    // white-space: nowrap; 
    // overflow: hidden;
    text-overflow: ellipsis;
    color: #b4b7c1;
    // padding: 30px 0 30px 10px;
    margin-bottom:1px;
    position: relative;
    cursor: pointer;
    .collectMenuList{
      position: absolute;
      background: url(~@/assets/collect2.png) no-repeat;
      width: 35px;
      height: 115px;
      padding: 24px 8px;
      overflow: hidden;
      text-align: center;
      word-wrap: break-word;
      writing-mode: vertical-lr;
    }
    &:first-child{
      height: 58px;
      .collectMenuList{
        background: url(~@/assets/collect1.png) no-repeat;
        height: 95px;
        padding-top:10px;
      }
    }
    &.is-active,&:hover{
      .collectMenuList{
        background: url(~@/assets/collect2_h.png) no-repeat;
      }
      &:first-child{
        .collectMenuList{
          background: url(~@/assets/collect1_h.png) no-repeat;
        }
      }
    }
  }
  }
  .collectContent{
    width: 293px;
    overflow-y: auto;
    padding: 10px;
    li.el-menu-item{
      position: relative;
      height: auto;
      background: transparent!important;
      padding: 10px;
      width: 50%;
      .img_box{
        width: 106px;
        height: 66px;
        background: #000;
        display: flex;
        justify-content: center;
        align-items: center;
      }
      .usehove{
        .inside-img{
          max-width: 43px;
          max-height:43px;
        }
      }
      .icon_opera{
          position: absolute;
          width: 106px;
          height: 66px;
          display: none;
          justify-content: center;
          align-items: center;
          color: #b1b1b1;
          background: rgba(0,0,0,0.5);
          i{
            color: #b1b1b1;
            font-size: 20px;
            &:hover{
              color:#3a89fe
            }
          }
          i:focus,i:visited,i:active{
            color: #b1b1b1;
          }
        }
      &:hover{
        background: transparent;
        .icon_opera{
          display: flex;
        }
      }
    }
  }
}
.drop-list {
  overflow-y: scroll;
  max-height: 70vh;
}
</style>