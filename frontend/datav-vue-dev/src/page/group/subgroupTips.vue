<template>
  <div>
    <div 
    class="bgBox" 
    :style="{width:`${activeObj.component.width}px`,height:`${activeObj.component.height}px`}"
    >
    </div>
    <template v-for="item in nav">
      <div 
      :key="item.index" 
      v-if="!item.children" 
      @contextmenu.prevent="contain.handleContextMenu && contain.handleContextMenu($event, item)" 
      :class="{'lockedStyle':item.lock,['box-avue-echart-'+item.component.name]:true}" @click="handelFatherClick(item)"
      >
        <avue-draggable 
        v-bind="item" 
        :scale="container.stepScale" 
        :disabled="!contain.menuFlag" 
        :step="container.stepScale" 
        :width="item.component.width" 
        :height="item.component.height" 
        :ref="common.DEAFNAME + item.index" 
        :id="common.DEAFNAME + item.index" 
        :class="getTypeCustomSegments(item)"
        :active-flag="contain.active.includes(item.index)" 
        v-show="!item.display"
        @move="handleMove" 
        @over="handleOver" 
        @focus="handleFocus" 
        @blur="handleBlur" 
        @out="handleOut">
          <component 
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
          />
          <div class="error-title-box"></div>
        </avue-draggable>
        <!-- <subgroup :nav="item.children"></subgroup> -->
      </div>
    </template>
  </div>
</template>

<script>
//注册自定义组件
import components from '@/components/'
// import crypto from '@/utils/crypto';
import crypto from '@/utils/crypto.min.js'
import Vue from 'vue'
import { dynamicSql } from '@/api/db'
// import { getFunction } from '@/utils/utils';
import { getFunction,uuid } from '@/utils/utils.min.js'
import common from '@/config'
import echartComponents from '../../echart/' // 引入组件菜单栏中的各个组件
import calcfn from '@/components/alignLine/calcfn.js'
let initialAlignLine = {
  top: 0,
  bottom: 0,
  left: 0,
  right: 0,
  vertical: 0,
  horizontal: 0,
  topShow: false,
  bottomShow: false,
  leftShow: false,
  rightShow: false,
  verticalShow: false,
  horizontalShow: false,
}
export default {
  name: 'subgroupTips',
  inject: ['contain', 'container'],
  provide() {
    return {
      contain: this.contain,
      container: this.container,
    }
  },
  components: components,
  props: {
    nav: {
      type: Array,
      default: () => {
        return []
      },
    },
    showTipsCopy:Number,
  },
  computed: {
    tipsIndex() {
      return this.$store.state.tipsIndex
    },
    activeObj() {
      return this.contain.findlist(this.tipsIndex)
    },
    activeListTips(){
      let result = []
      this.contain.active.forEach(ele => {
        const item = this.getFindnavTips(ele, true,this.nav)
        if(item.obj){
          result.push(item.obj)
        }
      })
      return result
    },
  },
  watch:{
    showTipsCopy(){
      //寻找父类
      const params = this.getFindnavTips(this.contain.active[0], true,this.contain.navTips)
      let active = []
      this.contain.active.forEach(ele => {
        const item = this.getFindnavTips(ele, true,this.contain.navTips)
        const obj = this.deepClone(item.obj)
        if(obj){
          if (obj.children) {
            this.getChildren(obj.children)
          }
          obj.index = uuid()
          params.parent.unshift(obj)
          active.unshift(obj.index)
        }
      })
      this.$nextTick(()=>{
        this.contain.active = active;
        this.contain.activeIndex = null
      })
    }
  },
  data() {
    return {
      sqlFormatter: dynamicSql,
      common: common,
      activeObjCopy: null,
      altSelect:[],
    }
  },
  created() {
    this.contain.active = [];
    this.init()
    document.addEventListener('keydown',e=>{
      if(e.keyCode == 65 && e.altKey && e.ctrlKey){
        // console.log(this.$store.state.showTips,this.nav,888)
          if(this.$store.state.showTips){
            this.altSelect = []
            if(this.nav.length>0){
              this.getAltSelect(this.nav)
            }
            this.contain.active = this.altSelect
          }
        }
    })
  },
  methods: {
    // 判断是否显示 avue-draggable__mask
      getTypeCustomSegments(item){
      if(item.component.name==='customSegments'){
        if(item.option){
          if(item.option.selectStatus){
            return 'sharpLineAvueDrag'
          }
        }
      }
      return ''
    },
    getChildren(list) {
      if (Array.isArray(list)) {
        list.forEach(item => {
          item.index = uuid()
          if (item.children) {
            this.getChildren(item.children)
          }
        })
      }
    },
    getAltSelect(list){
      list.forEach(item=>{
        this.altSelect.push(item.index)
        if(item.children){
          this.getAltSelect(item.children)
        }
      })
    },
    getFindnavTips(id, type, navList) {
      //循环处理数据
      let obj = void 0
      // void 0 返回undefined
      let count = 0
      let parent = void 0
      let pcount = 0
      let len = 0
      const detail = (item, list, i, number = 0) => {
        if (!item.children || type) {
          if (id === item.index) {
            obj = item
            len = Array.isArray(list)
              ? list.length - 1
              : list.children.length - 1
            // parent = list;
            // 这行之前的代码会在有分组的时候出现bug
            parent = Array.isArray(list) ? list : list.children
            pcount = number
            count = i
          }
        }
        if (item.children) {
          item.children.forEach((ele, index) => {
            detail(ele, item, index, number + 1)
          })
        }
      }
      // 把this.nav换成navLIst这样就增加了该函数的扩展性，因为新增了弹窗组件，在弹窗的编辑页面也会有复制粘贴功能用到这个方法，
      // 如果把这里单纯的写为this.nav那么只能从在大屏编辑页面下面查找，改为变量后可以接受弹窗组件的list作为查找
      navList.forEach((ele, index) => {
        detail(ele, navList, index)
      })
      return {
        obj,
        count,
        len,
        pcount,
        parent,
      }
    },
    init() {
      // 注册每一个组件
      Object.keys(echartComponents).forEach((ele) => {
        let component = echartComponents[ele]
        Vue.component(component.name, component)
      })
      this.getFunction = getFunction
    },
    getItemObj() {
      return this.$refs[this.common.NAME + this.contain.activeObj.index][0]
    },
    //刷新数据
    handleRefresh() {
      return this.getItemObj().updateData()
    },
    //获取对象
    getDragObj(val) {
      return this.$refs[`${this.common.DEAFNAME}${val}`]
    },
    handleMove({ index, left, top }) {
      if (this.contain.activeIndex !== index) return;
      this.activeListTips.forEach((item) => {
        if (this.contain.activeIndex === item.index) return;
        if (item.lock) return; // 如果是多选的图层已经被锁定了那么不能拖动
        item.left = item.left + left;
        item.top = item.top + top;
      });
      // 给图表的深拷贝对象的实时left 和 top赋值
      if (this.activeObjCopy) {
        this.activeObjCopy.intimeTop = this.activeObjCopy.intimeTop + top
        this.activeObjCopy.intimeLeft = this.activeObjCopy.intimeLeft + left
      }
      // 判断一下是否开启了对齐线，没开启则不需要计算节省性能
      // 并且也应当判断下activeObjCopy是否有值，因为再点击撤销的时候选中的图表处于选中失焦状态，撤销也可能触发移动事件，是null会报错
      if (this.$store.state.alignLine.enable && this.activeObjCopy) {
        this.setAlignLineInfo({ index, left, top })
      }
    },
    handleOver({ index }) {
      this.contain.overactive = index
    },
    // 新增移出事件
    handleOut() {
      this.contain.overactive = ''
    },
    // 当点击了某一个图表时触发的事件
    handleFocus({ index }) {
      // debugger
      // 把选中组件的index值赋值给build组件的data里的activeIndex
      this.contain.activeIndex = index
      // 深拷贝当前选中的图表 深拷贝的原因是在图表移动的过程中需要实时的计算他的intimeTop 和 intimeLeft 值
      // 但是之前的写法是直接给选中图表对象的activeObj的intimeTop 和 intimeLeft赋值，这就导致撤销事件会在图表移动过多出现报错
      // 因为在mixins index.js的watch中深度监听了nav数组，所以activeObj的改变会一直触发监听，导致不停往数组historyCache中push
      // 所以改为深拷贝选中图表
      // 并且当同时选中两个及以上的时候是不需要对齐线的所以也就不需要在深拷贝
      if (this.contain.active.length < 2) {
        this.activeObjCopy = this.deepClone(this.contain.findTipsList(index))
      }
      // 给container组件的data里的gradeFlag赋值
      this.container.gradeFlag = true
      this.contain.selectNav(index)
    },
    handleBlur({ index, left, top, width, height }) {
      // 失焦的时候还原为原来的null值
      // debugger
      this.activeObjCopy = null
      if (index !== this.contain.activeIndex) return
      this.container.gradeFlag = false
      this.$set(this.contain.activeObj.component, 'width', width)
      this.$set(this.contain.activeObj.component, 'height', height)
      this.$set(this.contain.activeObj, 'left', Math.round(left))
      this.$set(this.contain.activeObj, 'top', Math.round(top))
      this.$set(this.contain.activeObj, 'intimeLeft', Math.round(left))
      this.$set(this.contain.activeObj, 'intimeTop', Math.round(top))
      let opt = Object.assign({}, this.$store.state.alignLine, initialAlignLine)
      this.$store.commit('SET_ALIGNLINE_INFO', opt)
    },
    setAlignLineInfo({ index, left, top }) {
      // console.log('this.contain==>', this.contain);
      if (this.contain.active.length > 1) {
        let opt = Object.assign({}, this.$store.state.alignLine, initialAlignLine)
        this.$store.commit('SET_ALIGNLINE_INFO', opt)
      } else {
        // 因为有的图表会分组，所以要把分组的遍历出来
        let navFlat = []
        this.contain.nav.forEach((f_item) => {
          if (f_item.children) {
            f_item.children.forEach((s_item) => {
              navFlat.push(s_item)
            })
          } else {
            navFlat.push(f_item)
          }
        })
        // 不用再查找目标对象，直接使用activeObjCopy即可
        // let target = navFlat.filter((item) => {
        //   return item.index === index;
        // });
        let com = navFlat.filter((item) => {
          return item.index != index
        })
        let opt = Object.assign({}, this.$store.state.alignLine, initialAlignLine)
        for (let i = 0; i < com.length; i++) {
          if (calcfn.isIntersectToTop(this.activeObjCopy, com[i])) {
            opt.top = this.activeObjCopy.intimeTop
            opt.topShow = true
          }
          if (calcfn.isIntersectToBottom(this.activeObjCopy, com[i])) {
            opt.bottom = this.activeObjCopy.intimeTop + this.activeObjCopy.component.height + 24
            opt.bottomShow = true
          }
          if (calcfn.isIntersectToLeft(this.activeObjCopy, com[i])) {
            opt.left = this.activeObjCopy.intimeLeft
            opt.leftShow = true
          }
          if (calcfn.isIntersectToRight(this.activeObjCopy, com[i])) {
            opt.right = this.activeObjCopy.intimeLeft + this.activeObjCopy.component.width + 24
            opt.rightShow = true
          }
          if (calcfn.isIntersectToVertical(this.activeObjCopy, com[i])) {
            opt.vertical = this.activeObjCopy.intimeLeft + com[i].component.width / 2 + 12
            opt.verticalShow = true
          }
          if (calcfn.isIntersectToHorizontal(this.activeObjCopy, com[i])) {
            opt.horizontal = this.activeObjCopy.intimeTop + com[i].component.height / 2 + 12
            opt.horizontalShow = true
          }
        }

        // console.log('this.activeObjCopy', this.activeObjCopy);
        this.$store.commit('SET_ALIGNLINE_INFO', opt)
      }
    },
    handelFatherClick(item) {
      if (item.lock) {
        this.contain.activeIndex = item.index
        this.contain.selectNav(item.index)
      } else {
        return
      }
    },
  },
}
</script>
<style lang="scss">
.lockedStyle {
  .avue-draggable--active {
    border-color: red;
  }
}
.bgBox {
  position: absolute;
  top: 0;
  left: 0;
  border: 1px dashed #fee;
}
</style>
