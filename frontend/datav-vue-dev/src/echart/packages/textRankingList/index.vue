<template>
  <div class="textRankingList" :style = "{
    '--fontSize':option.fontSize + 'px',
    '--fontFamily':option.fontFamily,
    '--color':option.color,
    '--fontWeight':option.fontWeight,
    '--hoverColor':option.hoverColor,
    '--hoverUnderline':option.hoverUnderline?'underline':'none',
    '--lineSpace':option.lineSpace + 'px',
    '--oneColor':option.oneColor ,
    '--oneFontWeight':option.oneFontWeight,
    '--twoColor':option.twoColor,
    '--twoFontWeight':option.twoFontWeight,
    '--threeColor':option.threeColor,
    '--threeFontWeight':option.threeFontWeight,
  }"
  v-loading="textRankingListLoading"
  element-loading-spinner="el-icon-loading"
  element-loading-background="rgba(0, 0, 0, 0)"
  >
  <div class="rankingList">
    <div class="text-list" :style="textList" v-for="rIndex in option.number" :key="rIndex">
      <div class="text-sy" v-for="(item,index) in dataList.slice((rIndex-1)*Math.ceil(dataList.length/option.number),(rIndex-1)*Math.ceil(dataList.length/option.number)+Math.ceil(dataList.length/option.number))" :key="index" @click="getClik(item)">
        <div :ref="'testRefIcon'+id" class="icon-type" v-show="option.isShowIcon" v-html="option.iconUrl"></div>
        <el-tooltip class="item" effect="dark" :content="item.detail" :disabled='item.disabled' placement="top-start">
          <div :ref="'testRef'+id" class="test" :class="'top'+((index+1)+((rIndex-1)*Math.ceil(dataList.length/option.number)))">{{ (index+1)+((rIndex-1)*Math.ceil(dataList.length/option.number))}} : {{item.detail}}</div>
        </el-tooltip>
      </div>
    </div>
  </div>
  </div>
</template>
<script>
import create from '../../create'
export default create({
  name: 'textRankingList',
  data() {
    return {
      textRankingListLoading:false,
      iconUrl:'',
      dataList:[],
      // textList:{},
    }
  },
  created() {
    this.option.iconUrl = this.svgWithStyle(this.option.iconUrl);
  },
  watch: {
    dataChart:{
      handler(val){
        if(Object.prototype.toString.call(val).slice(8,-1) === 'Array'){
          this.dataList = val;
          let dataList = val
          this.$nextTick(()=>{
            let parms = this.$refs['testRef'+this.id]
            let testRefIcon = this.$refs['testRefIcon'+this.id];
            let marge  = 0
            if(this.option.isShowIcon){
              marge = 5
            }
            let width = 0
            dataList.forEach((item,index) => {
              if(parms[index]){
                if(parms[index].clientWidth){
                  width = parms[index].clientWidth + testRefIcon[index].clientWidth + marge
                  // console.log(width,this.component.width/this.rowsNumber,5555)
                }
              }
              // 5 text-list margin-right的距离
              if(width>=Math.round((this.component.width/this.option.number)-5)){
                item = Object.assign(item,{disabled:false})
              }else{
                item = Object.assign(item,{disabled:true})
              }
            });
            this.dataList = dataList
            this.$forceUpdate()
          })
        }
      },
      deep:true
    },
  },
  computed: {
    textList(){
      return {
        width: (this.component.width/this.option.number)-5 + "px"
      }
    }
  },
  methods: {
    svgWithStyle(link){
      let source = link
      let svgSize = this.option.iconSize + 'px'
      let svgColor = this.option.iconColor
      let style = ` style = "width: ${svgSize} ; height: ${svgSize}; fill: ${svgColor}"`
      let reg = /((?<=<svg))/g
      let reg1 = /fill=\"(\S)*\"/g // 去除掉path标签中的fill,要不然无法改色
      let source1 = source.replace(reg1, 'fill')
      return source1.replace(reg, style)
    },
    getClik(item){
      window.parent.postMessage(item, '*');
      this.updateClick(
        item,
        'clickFormatter'
      )
    },
  },
})
</script>
<style lang="scss" scoped>
.text-list{
//  width: 100%;
 height: 100%;
 margin-right: 5px;
}
::-webkit-scrollbar{
  display: none;
}
/deep/.el-loading-spinner{
  font-size: 25px !important;
}
.rankingList{
    display: flex;
    width: 100%;
    height: 100%;
    overflow: auto;
}
.text-sy{
  display: flex;
  width: 100%;
  color: var(--color);
  margin-bottom: var(--lineSpace);
  font-family: var(--fontFamily);
	font-size: var(--fontSize);
	font-weight:var(--fontWeight);
  align-items: center;
  white-space: nowrap;//不支持换行
  overflow: hidden;//隐藏多出部分文字
  text-overflow: ellipsis;//用省略号代替多出部分文字
  /deep/.icon{
    margin-right: 0px;
  }
  .test{
    white-space: nowrap;
    overflow: hidden; 
    text-overflow: ellipsis;
  }
  .icon-type{
    margin-right: 5px;
  }
  .top1{
    font-weight:var(--oneFontWeight);
    color: var(--oneColor);
  }
  .top2{
    font-weight:var(--twoFontWeight);
    color: var(--twoColor);
  }
  .top3{
    font-weight:var(--threeFontWeight);
    color: var(--threeColor);
  }
  .test:hover{
    color: var(--hoverColor);
    text-decoration: var(--hoverUnderline);
  }

}
</style>
