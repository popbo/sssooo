<template>
  <div class="tipsBox" ref="tipsBox" :class="{ bg: !list || list.length == 0 }">
    <div class="closeImgBox" v-if="isShowCloseImg">
      <img src="~@/assets/close.png" alt="" @click="closeTips" />
    </div>
    <div class="comBox">
      <template v-for="item in list">
        <component
          v-if="!contain.showTips"
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
          style="position: absolute"
          :style="{
            top: `${item.top}px`,
            left: `${item.left}px`,
            width: `${item.component.width}px`,
            height: `${item.component.height}px`,
            'z-index': `${item.zIndex}`,
          }"
        />
      </template>
    </div>
  </div>
</template>
<script>
import create from '../../create'
import components from '@/components/'
import common from '@/config'
import { getFunction } from '@/utils/utils.min.js'
import { EventBus } from '@/bus.js'
export default create({
  name: 'tips',
  inject: ['contain', 'container'],
  provide() {
    return {
      contain: this.contain,
      container: this.container,
    }
  },
  data() {
    return {
      common: common,
      getFunction: getFunction,
      eventInfo: {},
      contentStyle: {},
      styleName: {},
      // isShowClose: false,
    }
  },
  props: {
    option: Object,
    component: Object,
    list: Array,
  },
  created(){
  },
  computed: {
    currentObj() {
      let obj = {}
      for (let j = 0; j < this.contain.nav.length; j++) {
        if (this.$attrs.index === this.contain.nav[j].index) {
          obj = this.contain.nav[j]
          break
        }
      }
      return obj
    },
    // 如果弹窗处在build页面那么是不需要显示关闭图片
    // isShowCloseImg() {
    //   if (this.$store.state.showCloseImg) {
    //     return true
    //   }
    //   if (this.$route.path.includes('build')) {
    //     return false
    //   } else {
    //     return true
    //   }
    // },
    isShowCloseImg: {
      get() {
        return this.option.isShowClose
      },
      set(newVal) {
        this.option.isShowClose = newVal
      },
    },
  },
  watch: {
    'currentObj.display': {
      handler(newVal, oldVal) {
        console.log(newVal, oldVal)
        if (this.contain.menuFlag == true) {
          return false
        }
        // if (this.option.showPosition == 2 && newVal == false) {
        //   this.transformPosition()
        // }
      },
      // deep: true,
    },
   '$attrs.display':{
      handler(){
        let id = this.id.replace('list','')
        let textSession = id + 'textSelectTypeChange'
        window.sessionStorage.removeItem(textSession)
      },
      deep:true
   } 
  },
  methods: {
    bodyCloseMenus(e) {
      //  记录点击信息
      this.eventInfo = e
      //  如果在修改页面 所有事件不执行
      if (this.contain.menuFlag == true) {
        return false
      }
      // 点击空白处隐藏
      // console.log('this.currentObj', this.currentObj)
      // if (e.target._prevClass === 'container' && !this.currentObj.display)
      if (!this.currentObj.display) {
        if (this.option.showBySpace) {
          this.$set(this.currentObj, 'display', '')
          this.currentObj.display = true
        }
      }
    },
    closeTips() {
      this.$set(this.currentObj, 'display', '')
      this.currentObj.display = true
    },
    transformPosition() {
      // console.log('eventInfo==>', this.eventInfo);
      // console.log('this.currentObj==>', this.currentObj);
      let { left, top } = this.calcPositionFn(
        { left: this.eventInfo.clientX, top: this.eventInfo.clientY },
        this.currentObj.component,
        20
      )
      //  偏移量为
      // console.log({ left, top });
      this.currentObj.top = top + 40
      this.currentObj.left = left + 40
    },
    calcPositionFn(ePosition, objSize, offsetVal) {
      /**
       * ePosition 鼠标点击位置
       * objSize 组件尺寸
       * offsetVal  偏移量
       */
      console.log('objSize==>', objSize)
      console.log('ePosition==>', ePosition)
      const contentWidth = parseFloat(this.contentStyle.width) // 获取当前id="content"的dev的盒子的宽度
      const contentHeight = parseFloat(this.contentStyle.height) // 获取当前id="content"的dev的盒子的高度
      const configWidth = parseFloat(this.styleName.width) // 获取大屏的原始配置宽度
      const configHeight = parseFloat(this.styleName.height) // 获取大屏的原始配置高度
      const scale = contentWidth / configWidth // 计算缩放比例
      console.log('scale==>', scale)
      // 如果大屏进行了缩放那么弹窗的宽高 和 偏移距离也应该按缩放比例计算后参与计算
      const objSizeWidthScale = objSize.width * scale
      const objSizeHeightScale = objSize.height * scale
      const offsetValScale = offsetVal * scale
      let returnVal = { top: 0, left: 0 }
      if (ePosition.left - offsetValScale > objSizeWidthScale) {
        // 左侧
        console.log('左侧')
        returnVal.left =
          (ePosition.left - offsetValScale - objSizeWidthScale) / scale
      } else if (
        contentWidth - ePosition.left - offsetValScale >
        objSizeWidthScale
      ) {
        // 右侧
        console.log('右侧')
        returnVal.left = (ePosition.left + offsetValScale) / scale
      }
      if (ePosition.top - offsetValScale > objSizeHeightScale) {
        //  上方
        console.log('上方')
        returnVal.top =
          (ePosition.top - offsetValScale - objSizeHeightScale) / scale
      } else if (
        contentHeight - ePosition.top - offsetValScale >
        objSizeHeightScale
      ) {
        //  下方
        console.log('下方')
        returnVal.top = (ePosition.top + offsetValScale) / scale
      }
      console.log('returnVal==>', returnVal)
      return returnVal
    },
  },
  mounted() {
    EventBus.$on('tipsTransformPosition',()=>{
      this.transformPosition()
    })
    document.addEventListener('click', this.bodyCloseMenus)
    console.log(this.$route.path)
    this.contentStyle = this.contain.$refs.container.contentStyle
    this.styleName = this.contain.$refs.container.styleName
  },
  components: components,
  beforeDestroy() {
    document.removeEventListener('click', this.bodyCloseMenus)
  },
})
</script>
<style lang="scss" scoped>
// .tipsBox {
//   position: absolute;
//   border: 1px solid red;
// }
.bg {
  background: url('~@/assets/imgborder.png') no-repeat center;
}
.comBox {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: auto;
}
.closeImgBox {
  position: absolute;
  right: -40px;
  top: -40px;
}
// .innerBox{
//   height: 100%;
//   width: 100%;
//   position: relative;
// }
</style>
