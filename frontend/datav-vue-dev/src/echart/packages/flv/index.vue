<template>
  <div class="borderStyle" :class="b()" :style="styleSizeName" @click="handleClick" @dblclick="handleDblClick">
    <div class="splitScreen" v-if="option.isSplitScreen">
      <div style="margin-right:18px;display:inline-block">分屏</div>
      <div class="svg-btn" 
      :class="[option.isActive == 1 ? 'active' : '']" 
      @click="screenNum(1)">
        <img :src="require('@/assets/svg/one.svg')"/>
      </div>
      <div class="svg-btn" 
      :class="[option.isActive == 4 ? 'active' : '']" 
      @click="screenNum(4)">
        <img :src="require('@/assets/svg/four.svg')"/>
      </div>
      <div class="svg-btn"
      :class="[option.isActive == 9 ? 'active' : '']" 
      @click="screenNum(9)">
        <img :src="require('@/assets/svg/nine.svg')"/>
      </div>
    </div>
    <el-carousel :style="styleSizeName" ref="carouselRef" arrow="hover" 
    :autoplay="false" indicator-position="none" :loop="true">
      <el-carousel-item :style="styleSizeName"  v-for="(item,index) in newArr" :key="item">
        <template v-if="item.length===option.isActive">
          <div class="videoBorder" :style="carouselHeight" v-for="ele in option.isActive" :key="ele">
            <video :style="carouselHeight" :id="flvid+item[ele-1].id" muted></video>
          </div>
        </template>
        <template v-else>
          <div class="videoBorder" :style="carouselHeight" v-for="ele in item.length" :key="ele">
            <video :style="carouselHeight" :id="flvid+item[ele-1].id" muted></video>
          </div>
          <div class="noVideo videoBorder" :style="carouselHeight" v-for="n in (option.isActive-item.length)" :key="n">
            无信号
          </div>
        </template>
        
      </el-carousel-item>
    </el-carousel>
    <!-- <video :id="flvid" :style="styleSizeName"></video> -->
  </div>
</template>

<script>
// import { uuid } from '@/utils/utils';
import { uuid } from '@/utils/utils.min.js'
import create from '../../create'
import flvjs from 'flv.js'
export default create({
  name: 'flv',
  data() {
    return {
      flvid: 'main_' + uuid(),
      reload: true,
      config: {},
      flvPlayer: [],
      loading: true,
      newArr:[],
      tempArr:[]
    }
  },
  computed: {
    autoplay() {
      return this.option.autoplay
    },
    carouselHeight(){
      console.log('样式',this.component.width)
      if(this.option.isActive===1){
        return {
          width:this.component.width+'px',
          height:this.component.height+'px',
        }
      }else if(this.option.isActive===4){
        return{
          width:parseInt(this.component.width/2-2)+'px',
          height:parseInt(this.component.height/2-2)+'px',
        }
      }
      else if(this.option.isActive===9){
        return {
          width:parseInt(this.component.width/3-2)+'px',
          height:parseInt(this.component.height/3-2)+'px',
        }
      }
    }
  },
  watch: {
    dataChart: {
      handler(newVal, oldVal) {
        this.tempArr=this.dataChart.map(ele=>{
          return {
            id:'main_'+uuid()+ele.name,
            url:ele.url
          }
        })
        console.log('暂存的数组',this.tempArr)
        this.newArr=this.divideGroup(this.tempArr,this.option.isActive)
        console.log('this.newArr',this.newArr)
        this.$nextTick(() => {
          // this.loading = true
          this.flvPlayer=[]
          newVal.forEach((ele,index)=>{
            this.flvPlayer.push(null)
          })
          this.flvPlayer.forEach((ele,index)=>{
            if(ele!==null){
              console.log('我进来了')
              this.destoryVideo()
            }
          })
          this.createVideo()
        })
      },
      deep: true,
    },
    'option.isSplitScreen'(newVal){
      if(!newVal){
        console.log('我进来了')
        this.option.isActive=1
        this.screenNum(this.option.isActive)
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.createVideo()
    })
  },
  beforeDestroy() {
    this.destoryVideo()
  },
  methods: {
    handleClick() {
      this.clickFormatter &&
        this.clickFormatter(
          {
            data: this.dataChart,
          },
          this.getItemRefs()
        )
    },
    handleDblClick() {
      this.dblClickFormatter &&
        this.dblClickFormatter(
          {
            data: this.dataChart,
          },
          this.getItemRefs()
        )
    },
    // 创建和销毁video
    createVideo() {
      if (flvjs.isSupported()) {
        this.$nextTick(() => {
          this.tempArr.forEach((ele,index)=>{
            var videoElement = document.getElementById(this.flvid+ele.id)
            this.flvPlayer[index]=null
            this.flvPlayer[index] = flvjs.createPlayer(
              {
                type: 'flv',
                url: ele.url,
              },
              {
                cors: true, // 是否跨域
                // enableWorker: true, // 是否多线程工作
                enableStashBuffer: false, // 是否启用缓存
                stashInitialSize: 128, // 缓存大小(kb)  默认384kb
                autoCleanupSourceBuffer: true, // 是否自动清理缓存
              }
            )
            // console.log('视频',this.flvPlayer[index])
            this.flvPlayer[index].attachMediaElement(videoElement)
            this.flvPlayer[index].load()
            this.flvPlayer[index].play()
            this.flvPlayer[index].on(flvjs.Events.ERROR, (errType, errDetail) => {
              console.log('errorType:', errType)
              console.log('errorDetail:', errDetail)
            })
          })
          
        })
      }
    },
    destoryVideo() {
      this.tempArr.forEach((ele,index)=>{
        this.flvPlayer[index].pause()
        this.flvPlayer[index].unload()
        this.flvPlayer[index].detachMediaElement()
        this.flvPlayer[index].destroy()
        this.flvPlayer[index] = null
      })
    },
    screenNum(num){
      this.option.isActive=num
      console.log("this.option.screenNum",this.option.screenNum)
      this.newArr=this.divideGroup(this.tempArr,this.option.isActive)
      this.createVideo()
      console.log('this.newArr',this.newArr)
    },
    divideGroup(array = [], subGroupLength = 0){
      let index = 0;
      const newArray = [];
      while (index < array.length) {
        newArray.push(array.slice(index, index += subGroupLength));
      }
      return newArray;
    }
  },
})
</script>
<style scoped lang="scss">
.splitScreen{
  width:100%;
  height: 35px;
  line-height: 35px;
  background: #243244;
  position: absolute;
  top:-35px;
  padding-left:10px;
  color:#fff;
  font-size:14px;
  .svg-btn{
    width:14px;
    display: inline-block;
    overflow: hidden;
    margin-right: 10px;
    vertical-align: middle;
    cursor: pointer;
    &.active{
      img{
        filter: drop-shadow(#409eff 20px 0);
      }
      
    }
    img{
      width: 100%;
      position: relative;
      left: -20px;
      filter: drop-shadow(#295077 20px 0);
    }
  }
}
video{
  object-fit: fill;
}
/deep/.el-carousel{
  .el-carousel__container{
    height: 100%;
  }
  .el-carousel__item{
    display: flex;
    flex-wrap: wrap;
  }
}
.noVideo{
  display: flex;
  justify-content: center;
  align-items: center;
  color: #309ef8;
  font-size: 12px;
  background: #1c1e26;
  box-sizing: content-box;
}
.borderStyle{
  border:2px solid #243244
}
/deep/.el-carousel__arrow{
  border-radius: 50%;
  background: #1e2b3b;
  color:#2f97ed;
}
.videoBorder{
  border-bottom:2px solid #243244;
  border-right: 2px solid #243244;
  box-sizing: content-box;
}
</style>