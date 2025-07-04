<template>
  <div class="tipsHolder" ref="tipsHolder">
    <div class="comBox" ref="comBox" :style="getStyle"
    v-click-outside='getOut'
    >
      <template v-for="item in listData">
        <div :key="item.index"  
          :class="{'lockedStyle':item.lock,['box-avue-echart-'+item.component.name]:true}">
          <avue-draggable
            v-bind="item"
            :scale="container.stepScale"
            :disabled="!contain.menuFlag"
            :step="container.stepScale"
            :width="item.component.width"
            :height="item.component.height"
            class="tipsHolder-show"
            :class="{
              'tipsHolder-list':item.component.prop==='sharpLine',
              'tipsHolder-move': item.moveLineFlage 
            }"
            :ref="common.DEAFNAME + item.index"
            :id="common.DEAFNAME + item.index"
            :active-flag="holderActiveSelect.includes(item.index)"
            v-show="!item.display"
            @move="handleMove"
            @over="handleOver"
            @focus="handleFocus"
            @blur="handleBlur"
            @out="handleOut"
          >
          <component
            v-show="!item.display"
            :key="item.index"
            :ref="common.NAME + item.index"
            :id="common.NAME + item.index"
            :is="common.COMPNAME + item.component.name"
            v-bind="item"
            :data-formatter="getFunction(item.dataFormatter)"
            :click-formatter="getFunction(item.clickFormatter, true)"
            :dbl-click-formatter="getFunction(item.dblClickFormatter, true)"
            :echart-formatter="getFunction(item.echartFormatter)"
            :label-formatter="getFunction(item.labelFormatter)"
            :styles-formatter="getFunction(item.stylesFormatter)"
            :formatter="getFunction(item.formatter)"
            :data-query="getFunction(item.dataQuery)"
            :data-header="getFunction(item.dataHeader)"
            :websocket-header="getFunction(item.websocketHeader)"
            :websocket-query="getFunction(item.websocketQuery)"
            :sql-formatter="sqlFormatter"
            :width="item.component.width"
            :height="item.component.height"
            :disabled="!contain.menuFlag"
            :scale="container.stepScale"
            title=""
            :selectedActive="holderActiveSelect.includes(item.index)"
          />
          </avue-draggable>
        </div>
      </template>
    </div>
  </div>
</template>
<script>
import create from '../../create'
import components from '@/components/'
import common from '@/config'
import { getFunction } from '@/utils/utils.min.js'
// let initialAlignLine = {
//   top: 0,
//   bottom: 0,
//   left: 0,
//   right: 0,
//   vertical: 0,
//   horizontal: 0,
//   topShow: false,
//   bottomShow: false,
//   leftShow: false,
//   rightShow: false,
//   verticalShow: false,
//   horizontalShow: false,
// }
const clickOutside = {
  bind(el, binding) {
    // 在元素上绑定一个点击事件监听器
    el.clickOutsideEvent = function (event) {
      // 检查点击事件是否发生在元素的内部
      if (!(el === event.target || el.contains(event.target))) {
        // 如果点击事件发生在元素的外部，则触发指令绑定的方法,将点击的event数据传过去
        binding.value(event);
      }
    };
    // 在文档上添加点击事件监听器
    document.addEventListener("click", el.clickOutsideEvent);
  },
  unbind(el) {
    // 在元素上解除点击事件监听器
    document.removeEventListener("click", el.clickOutsideEvent);
  },
};
export default create({
  name: 'tipsHolder',
  inject: ['contain', 'container'],
  provide() {
    return {
      contain: this.contain,
      container: this.container,
    }
  },
  directives: {
    "click-outside": clickOutside, // 注册自定义指令
  },
  data() {
    return {
      common: common,
      getFunction: getFunction,
      activeObjCopy:null,
      holderActiveSelect:[],
      isKeysCtrl:false,
      holderActiveList:[],
      nestLeft:0,
      nestTop:0,
    }
  },
  props: {
    option: Object,
    component: Object,
    list: Array,
    left:String,
    top:String,
    lock:Boolean,
    index:String
  },
  watch:{
    holderActiveSelect:{
      handler(val){
        if(val.length>0){
          let par
          for(let i = 0; i < this.listData.length; i++){
            if(val.includes(this.listData[i].index)){
              par = this.common.DEAFNAME + this.listData[i].index
              this.$refs[par][0].setActive(true)
            }else{
              par = this.common.DEAFNAME + this.listData[i].index
              this.$refs[par][0].setActive(false)
            }
          }
        }else{
          let par
          this.listData.forEach(item=>{
            par = this.common.DEAFNAME + item.index
            if(this.$refs[par]){
              this.$refs[par][0].setActive(false)
            }
          })
        }
      },
      deep:true
    },
  },
  computed:{
    listData(){
      this.list.forEach(item=>{
        item = Object.assign(item,{lock:this.lock})
      })
      return this.list
    },
    getStyle(){
      let prams = {}
      if(this.option.backgroundImage){
        prams ={
          background: `url(${this.option.backgroundImage}) 0% 0% / 100% 100%`,
          border: `${this.option.borderNumber}px solid ${this.option.borderColor}`
        }
      }else{
        prams ={
          backgroundColor: this.option.backgroundColor,
          border: `${this.option.borderNumber}px solid ${this.option.borderColor}`,
        }
      }
      let rotate = {
        transform:`perspective(1200px) rotateX(${this.option.rotateX}deg) rotateY(${this.option.rotateY}deg) rotateZ(${this.option.rotateZ}deg)`
      }
      if(this.option.rotateType===1){
        rotate = {}
      }
      if(this.$route.name === 'build'){
        rotate = {}
      }
      return Object.assign(prams,rotate)
    }
  },
  created(){
  },
  methods: {
    isInside(Xa1,Xa2,Ya1,Ya2,leftMin,leftMax,topMin,topMax){
      let Xb2 = leftMax;
      let Xb1 = leftMin;
      let Yb2 = topMax;
      let Yb1 = topMin;
      if (
          Math.abs(Xb2 + Xb1 - Xa2 - Xa1) <= Xa2 - Xa1 + Xb2 - Xb1 &&
          Math.abs(Yb2 + Yb1 - Ya2 - Ya1) <= Ya2 - Ya1 + Yb2 - Yb1
      ) {
          return true
      }
      return false
    },
    getOut(){
      this.holderActiveSelect = [];
      this.holderActiveList = []
    },
    selectNav(item) {
      if (Array.isArray(item)) {
        if(this.isKeysCtrl){
          this.holderActiveSelect = this.holderActiveSelect.concat(item)
        }else{
          if (this.getInclude1(this.holderActiveSelect, item)) {
            const arr = this.holderActiveSelect.filter(x => !item.some(y => y === x))
            this.holderActiveSelect = arr
          } else {
            this.holderActiveSelect  = item
          }
        }
      } else if (this.isKeysCtrl) {
        if (this.holderActiveSelect.length > 0) {
          let falge = true
          this.holderActiveSelect.forEach((aitem, index) => {
            if (aitem === item) {
  
              this.holderActiveSelect.splice(index, 1)
              falge = false
            }
          })
          if (falge) {
            this.holderActiveSelect.push(item)
          }
        } else {
          this.holderActiveSelect.push(item)
        }
      } else if (!this.holderActiveSelect.includes(item)) {
        this.holderActiveSelect = [item]
        this.contain.activeIndex = item
      }
      let active = this.holderActiveSelect.filter((value, index, self) => {
        return self.indexOf(value) === index;
      });
      this.holderActiveSelect = active
      // console.log('this.active',this.active )
    },
    getInclude1(arr1, arr2) {
      let temp = []
      for (const item of arr2) {
        arr1.find(i => i === item) ? temp.push(item) : ''
      }
      return temp.length === arr2.length ? true : false
    },
    handleMove({ index, left, top }){
      // console.log( index, left, top,this.contain.activeObj)
      this.contain.moveFlage = false;
       // 点击撤销和重做不要执行下面函数
      if( this.contain.moveNoFlage ){
        return;
      }
      if (this.contain.activeIndex !== index) return;
      this.holderActiveList.forEach((item) => {
        if (this.contain.activeIndex === item.index) return;
        if (item.lock) return; // 如果是多选的图层已经被锁定了那么不能拖动
        item.left = item.left + left;
        item.top = item.top + top;
      });
      // 给图表的深拷贝对象的实时left 和 top赋值
      if(this.activeObjCopy) {
        this.activeObjCopy.intimeTop = this.activeObjCopy.intimeTop + top
        this.activeObjCopy.intimeLeft = this.activeObjCopy.intimeLeft + left
      }
      // 判断一下是否开启了对齐线，没开启则不需要计算节省性能
      // 并且也应当判断下activeObjCopy是否有值，因为再点击撤销的时候选中的图表处于选中失焦状态，撤销也可能触发移动事件，是null会报错
      // if (this.$store.state.alignLine.enable && this.activeObjCopy) {
      //   this.setAlignLineInfo({ index, left, top });
      // }
    },
    handleOver({ index }) {
      this.contain.moveNoFlage = false;
      this.contain.moveFlage = true;
      this.contain.overactive = index;
    },
    // 新增移出事件
    handleOut() {
      this.contain.moveNoFlage = false;
      this.contain.moveFlage = true;
      this.contain.overactive = ''
    },
    handleFocus({ index }){
      this.contain.moveFlage = true;
      this.contain.moveNoFlage = false;
      this.contain.active = []
      if(this.contain.activeFolder) this.contain.activeFolder = null
        // 把选中组件的index值赋值给build组件的data里的activeIndex
        this.contain.activeIndex = index;
        // 深拷贝当前选中的图表 深拷贝的原因是在图表移动的过程中需要实时的计算他的intimeTop 和 intimeLeft 值
        // 但是之前的写法是直接给选中图表对象的activeObj的intimeTop 和 intimeLeft赋值，这就导致撤销事件会在图表移动过多出现报错
        // 因为在mixins index.js的watch中深度监听了nav数组，所以activeObj的改变会一直触发监听，导致不停往数组historyCache中push
        // 所以改为深拷贝选中图表
        // 并且当同时选中两个及以上的时候是不需要对齐线的所以也就不需要在深拷贝
      if (this.holderActiveSelect.length < 2) {
        this.activeObjCopy = this.deepClone(this.contain.activeObj)
      }
        // 给container组件的data里的gradeFlag赋值
      this.container.gradeFlag = true;
      this.selectNav(index);
      this.holderActiveList = []
      this.holderActiveSelect.forEach(ele => {
        const item = this.contain.findnav(ele, true,this.listData)
        this.holderActiveList.push(item.obj)
      })
      // console.log(this.holderActiveSelect,'this.holderActiveSelect')
      this.contain.active = [index]
    },
    handleBlur({ index, left, top, width, height }){
      this.contain.moveNoFlage = false;
      this.contain.moveFlage = true;
      this.activeObjCopy = null
      if (index !== this.contain.activeIndex) return;
      this.container.gradeFlag = false;
      this.$set(this.contain.activeObj.component, 'width', width);
      this.$set(this.contain.activeObj.component, 'height', height);
      this.$set(this.contain.activeObj, 'left', Math.round(left));
      this.$set(this.contain.activeObj, 'top', Math.round(top));
      this.$set(this.contain.activeObj, 'intimeLeft', Math.round(left));
      this.$set(this.contain.activeObj, 'intimeTop', Math.round(top));
      // let opt = Object.assign({}, this.$store.state.alignLine, initialAlignLine);
      // this.$store.commit('SET_ALIGNLINE_INFO', opt);
      let storeObj = this.deepClone(this.contain.activeObj)
      if(storeObj.top<0 || storeObj.left<0){
        let delList = []
        let isHolder = null
        const getlist = (item)=>{
          if(item.children){
            item.children.forEach(itema=>{
              if(itema.index===this.id.replace('list','')){
                isHolder = item
              }
              getlist(itema)
            })
          }
          if(item.list){
            item.list.forEach(itemb=>{
              if(itemb.index===this.id.replace('list','')){
                isHolder = item
              }
              getlist(itemb)
            })
          }
        }
        this.contain.nav.forEach(item=>{
          getlist(item)
        })
        // console.log(this.holderActiveSelect,'this.holderActiveSelect')
        this.holderActiveSelect.forEach((ele)=>{
          for(let i = 0; i < this.list.length; i++ ){
            if(ele === this.list[i].index){
              delList.push(this.list[i])
              this.list.splice(i,1)
              this.contain.handleInitActive()
            }
          }
        })
        delList.forEach(item=>{
          item.left = this.left + item.left
          item.top = this.top + item.top
          item.intimeLeft = this.top + item.intimeLeft
          item.intimeTop = this.top + item.intimeTop
          if(isHolder){
            if(isHolder.component){
              if(isHolder.component.name==='tipsHolder'){
                isHolder.list.unshift(item)
              }
            }else{
              this.contain.nav.unshift(item)
            }
          }else{
            this.contain.nav.unshift(item)
          }
        })
        return false
      }
      let dropXa1 = storeObj.left;
      let dropXa2 = storeObj.left + storeObj.component.width/2;
      let dropYa1 = storeObj.top
      let dropYa2 = storeObj.top + storeObj.component.height/2
      let active = this.holderActiveList;
      let pushList  = null
      this.list.forEach(item=>{
      if(item.component.name==='tipsHolder' && !item.display && !item.lock){
          let x1 = item.left;
          let y1 = item.top;
          let x2 = item.left + item.component.width;
          let y2 =  item.top + item.component.height;
          let falge = this.isInside(dropXa1,dropXa2,dropYa1,dropYa2,x1,x2,y1,y2)
          if(item.index===storeObj.index){
            falge = false;
          }
          if(falge){
            active.forEach(item_f=>{
              item_f.left = item_f.left -  item.left;
              item_f.top = item_f.top - item.top;
              item_f.intimeLeft = item_f.intimeLeft - item.intimeLeft
              item_f.intimeTop = item_f.intimeTop - item.intimeTop;
              if(item_f.left < 0){
                item_f.left = 0
              }
              if(item_f.left>item.component.width - item_f.component.width){
                item_f.left = item.component.width - item_f.component.width-10
              }
              if(item_f.top < 0){
                item_f.top = 0
              }
              if(item_f.top>item.component.height-item_f.component.height){
                item_f.top = item.component.height-item_f.component.height-10
              }
              if(item_f.intimeLeft < 0){
                item_f.intimeLeft = 0
              }
              if(item_f.intimeLeft>item.component.width - item_f.component.width){
                item_f.intimeLeft = item.component.width - item_f.component.width-10
              }
              if(item_f.intimeTop < 0){
                item_f.intimeTop = 0
              }
              if(item_f.intimeTop>item.component.height-item_f.component.height){
                item_f.intimeTop = item.component.height-item_f.component.height-10
              }
            })
            pushList = item
          }
        }
      })
      if(pushList){
        this.holderActiveSelect.forEach((ele)=>{
          for(let i = 0; i < this.list.length; i++ ){
            if(ele === this.list[i].index){
              this.list.splice(i,1)
              this.contain.handleInitActive()
            }
          }
        })
        active.forEach(item_t=>{
          if(item_t.zIndex < 2){
            item_t.zIndex = 2;
          }
          pushList.list.push(item_t)
        })
      }
    },
    
  },
  mounted() {
    document.addEventListener('keydown',e=>{
      this.isKeysCtrl = e.keyCode === 17
    })
    document.addEventListener('keyup', () => {
      this.isKeysCtrl = false
    })
  },
  components: components,
  beforeDestroy() {
  },
})
</script>
<style lang="scss" scoped>
  .comBox {
    position: relative;
    width: 100%;
    height: 100%;
    overflow: auto;
    .avue-draggable__line{
      display:  none;
    }
  }
</style>
<style lang="scss">
.box-avue-echart-tipsHolder {
  & > .avue-draggable {
    & > .avue-draggable__wrapper {
      & > .avue-draggable__mask {
        display:none;
      }
    }
  }
}
</style>