<template>
  <div>
    <template v-for="item in nav">
      <div
        :key="item.index"
        v-if="!item.children"
        @contextmenu.prevent="contain.handleContextMenu && contain.handleContextMenu($event, item)"
        :class="{'lockedStyle':item.lock,['box-avue-echart-'+item.component.name]:true}"
        @click="handelFatherClick(item)"
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
          @out="handleOut"
        >
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
            :selectedActive="contain.active.includes(item.index)"
            @lineMovePosi="moveLinePosi"
            :upDateObj='upDateObj'
          />
          <div class="error-title-box"></div>
        </avue-draggable>
        <subgroup v-if="item.children" :nav="item.children"></subgroup>
      </div>
    </template>
  </div>
</template>

<script>
  //注册自定义组件
  import components from '@/components/';
  // import crypto from '@/utils/crypto';
  // import crypto from '@/utils/crypto.min.js';
  import Vue from 'vue';
  import { dynamicSql } from '@/api/db';
  // import { getFunction } from '@/utils/utils';
  import { getFunction } from '@/utils/utils.min.js';
  import common from '@/config';
  import echartComponents from '../../echart/'; // 引入组件菜单栏中的各个组件
  import calcfn from '@/components/alignLine/calcfn.js';
  // import { EventBus } from '@/bus.js'
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
    name: 'subgroup',
    inject: ['contain', 'container'],
    provide() {
      return {
        contain: this.contain,
        container: this.container,
      };
    },
    components: components,
    props: {
      nav: {
        type: Array,
        default: () => {
          return [];
        },
      },
      upDateObj: {
        type: Object,
        default: () => {
          return {}
        }
      }
    },
    data() {
      return {
        nestLeft:0,
        nestTop:0,
        sqlFormatter: dynamicSql,
        common: common,
        activeObjCopy: null,
        refsList:{}
      };
    },
    created() {
      this.init();
      // console.log("nav==>",this.nav)
    },
    mounted(){
     
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
      init() {
        // 注册每一个组件
        Object.keys(echartComponents).forEach((ele) => {
          let component = echartComponents[ele];
          Vue.component(component.name, component);
        });
        this.getFunction = getFunction;
      },
      getItemObj() {
        if(this.$refs[this.common.NAME + this.contain.activeObj.index]){
          if(this.$refs[this.common.NAME + this.contain.activeObj.index].length!==0){
            return this.$refs[this.common.NAME + this.contain.activeObj.index][0] || null;
          }
        }
        // 获取所有的节点
        let ref = this.$refs
        this.getRefsList(ref)
        if(this.refsList[this.contain.activeObj.index]){
          return this.refsList[this.contain.activeObj.index] || null;
        }
        return null
      },
      // 获取所有的节点
      getRefsList(refList){
        Object.keys(refList).forEach(ele => {
            if (ele.indexOf(this.common.NAME) !== -1) {
              if(refList[ele][0]){
                this.refsList[ele.replace(this.common.NAME, '')] = refList[ele][0]
              }
              if(refList[ele][0]){
                if(refList[ele][0].$refs){
                  let refList_child = refList[ele][0].$refs
                  Object.keys(refList_child).forEach(ele_s => {
                    if (ele_s.indexOf(this.common.NAME) !== -1) {
                      if(refList_child[ele_s][0]){
                        this.refsList[ele_s.replace(this.common.NAME, '')] = refList_child[ele_s][0]
                      }
                    }
                    if(refList_child[ele_s][0]){
                      if(refList_child[ele_s][0].$refs){
                        if(Object.keys(refList_child[ele_s][0].$refs).length>0){
                          this.getRefsList(refList_child[ele_s][0].$refs)
                        }
                      }
                    }
                  })
                }
              }
            }
        })
      },
      //刷新数据
      handleRefresh() {
        if(this.getItemObj()){
          return this.getItemObj().updateData();
        }
        return null
      },
      //获取对象
      getDragObj(val) {
        return this.$refs[`${this.common.DEAFNAME}${val}`];
      },
      handleMove({ index, left, top }) {
        this.contain.moveFlage = false;
        // 点击撤销和重做不要执行下面函数
        if( this.contain.moveNoFlage ){
          return;
        }
        if (this.contain.activeIndex !== index) return;
        this.contain.activeList.forEach((item) => {
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
        if (this.$store.state.alignLine.enable && this.activeObjCopy) {
          this.setAlignLineInfo({ index, left, top });
        }
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
      // 当点击了某一个图表时触发的事件
      handleFocus({ index }) {
        this.contain.moveFlage = true;
        this.contain.moveNoFlage = false;
        // 如果之前点击过文件夹，再点了单个组件那么就要置空activeFolder
        if(this.contain.activeFolder) this.contain.activeFolder = null
        // 把选中组件的index值赋值给build组件的data里的activeIndex
        this.contain.activeIndex = index;
        // 深拷贝当前选中的图表 深拷贝的原因是在图表移动的过程中需要实时的计算他的intimeTop 和 intimeLeft 值
        // 但是之前的写法是直接给选中图表对象的activeObj的intimeTop 和 intimeLeft赋值，这就导致撤销事件会在图表移动过多出现报错
        // 因为在mixins index.js的watch中深度监听了nav数组，所以activeObj的改变会一直触发监听，导致不停往数组historyCache中push
        // 所以改为深拷贝选中图表
        // 并且当同时选中两个及以上的时候是不需要对齐线的所以也就不需要在深拷贝
        if (this.contain.active.length < 2) {
          this.activeObjCopy = this.deepClone(this.contain.activeObj)
        }
        // 给container组件的data里的gradeFlag赋值
        this.container.gradeFlag = true;
        this.contain.selectNav(index);
      },
      handleBlur({ index, left, top, width, height }) {
        this.contain.moveNoFlage = false;
        this.contain.moveFlage = true;
        // 失焦的时候还原为原来的null值
        this.activeObjCopy = null
        if (index !== this.contain.activeIndex) return;
        this.container.gradeFlag = false;
        this.$set(this.contain.activeObj.component, 'width', width);
        this.$set(this.contain.activeObj.component, 'height', height);
        this.$set(this.contain.activeObj, 'left', Math.round(left));
        this.$set(this.contain.activeObj, 'top', Math.round(top));
        this.$set(this.contain.activeObj, 'intimeLeft', Math.round(left));
        this.$set(this.contain.activeObj, 'intimeTop', Math.round(top));
        let opt = Object.assign({}, this.$store.state.alignLine, initialAlignLine);
        this.$store.commit('SET_ALIGNLINE_INFO', opt);
        // 容器代码
        let storeObj = this.deepClone(this.contain.activeObj)
        let dropXa1 = storeObj.left;
        let dropXa2 = storeObj.left + storeObj.component.width;
        let dropYa1 = storeObj.top
        let dropYa2 = storeObj.top + storeObj.component.height;
        let active = []
        this.contain.active.forEach(ele => {
          const item = this.contain.findnav(ele, true)
          const obj = this.deepClone(item.obj)
          if(item){
            active.push(item.obj)
          }
          if (obj.children) {
            this.getChildren(obj.children)
          }
        })
        // // 关闭容器多层嵌套
        // if(storeObj.component.name==='tipsHolder'){
        //   return false
        // }
        let pushList  = null
        this.nav.forEach(item=>{
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
              active.forEach( item_f=>{
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
          // console.log(pushList,'pushList')
          this.deleteItem()
          this.contain.handleInitActive()
          active.forEach(item_t=>{
            if(item_t.zIndex < 2){
              item_t.zIndex = 2;
            }
            pushList.list.push(item_t)
          })
        }
      },
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
      deleteItem() {
        const params = this.contain.findnav(this.contain.active[0], true,)
        this.contain.active.forEach(ele => {
          const item = this.contain.findnav(ele, true,)
          if (Array.isArray(params.parent)) {
            params.parent.splice(item.count, 1)
          } else {
            params.parent.children.splice(item.count, 1)
          }
        })
      },
      setAlignLineInfo({ index, left, top }) {
        // console.log('this.contain==>', this.contain);
        if (this.contain.active.length > 1) {
          let opt = Object.assign({}, this.$store.state.alignLine, initialAlignLine);
          this.$store.commit('SET_ALIGNLINE_INFO', opt);
        } else {
          // 因为有的图表会分组，所以要把分组的遍历出来
          let navFlat = []
          this.contain.nav.forEach(f_item => {
            if (f_item.children) {
              f_item.children.forEach(s_item => {
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
            return item.index != index;
          });
          let opt = Object.assign({}, this.$store.state.alignLine, initialAlignLine);
          for (let i = 0; i < com.length; i++) {
            if (calcfn.isIntersectToTop(this.activeObjCopy, com[i])) {
              opt.top = this.activeObjCopy.intimeTop;
              opt.topShow = true
            }
            if (calcfn.isIntersectToBottom(this.activeObjCopy, com[i])) {
              opt.bottom = this.activeObjCopy.intimeTop + this.activeObjCopy.component.height + 24;
              opt.bottomShow = true
            }
            if (calcfn.isIntersectToLeft(this.activeObjCopy, com[i])) {
              opt.left = this.activeObjCopy.intimeLeft;
              opt.leftShow = true
            }
            if (calcfn.isIntersectToRight(this.activeObjCopy, com[i])) {
              opt.right = this.activeObjCopy.intimeLeft + this.activeObjCopy.component.width + 24;
              opt.rightShow = true
            }
            if (calcfn.isIntersectToVertical(this.activeObjCopy, com[i])) {
              opt.vertical = this.activeObjCopy.intimeLeft + com[i].component.width / 2 + 12;
              opt.verticalShow = true
            }
            if (calcfn.isIntersectToHorizontal(this.activeObjCopy, com[i])) {
              opt.horizontal = this.activeObjCopy.intimeTop + com[i].component.height / 2 + 12;
              opt.horizontalShow = true
            }
          }
          
          // console.log('this.activeObjCopy', this.activeObjCopy);
          this.$store.commit('SET_ALIGNLINE_INFO', opt);
        }
      },
      handelFatherClick(item) {
        if(item.lock) {
          this.contain.activeIndex = item.index;
          if(this.$route.name === 'view') return // 因为build和view页面都共用了该组件，但是view没有this.contain.selectNav该方法
          this.contain.selectNav(item.index);
        } else {
          return
        }
      },
      moveLinePosi(index,top,left){
        console.log('位置',index,top,left)
        // const refLine=this.common.DEAFNAME + index
        if (this.contain.activeIndex !== index) return;
        this.contain.activeList.forEach((item) => {
          item.left =  left;
          item.top =  top;
          item.intimetop=item.top
          item.intimeleft=item.left
        });
      }
    },
  };
</script>
<style lang="scss">
.lockedStyle {
  .avue-draggable--active {
    border-color: red;
  }
}
// .sharpLineAvueDrag{
//   border: none;
//   .avue-draggable__range{
//     border:none;
//     background: transparent;
//   }
// }
.sharpLineAvueDrag .avue-draggable__mask{
  display: none;
}
.tipsHolder-show{
  .avue-draggable__mask{
      display: block;
  }
  // .avue-draggable__line{
  //   display:  none;
  // }
}
.tipsHolder-list{
  .avue-draggable__mask{
      display: none;
  }
}
// .tipsHolder-move{
//   .avue-draggable__line{
//     display:  block;
//   }
// }
</style>
