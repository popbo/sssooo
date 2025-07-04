<template>
  <div :class="b()">
    <svg width="100%" height="100%" class="progressSvg" v-if="isShowProgress">
      <circle :r="circleRadius" :cy="positionCoordinate.cy" :cx="positionCoordinate.cx" :stroke-width="option.strokeWidth" :stroke="option.strokeBgColor" stroke-linejoin="round" stroke-linecap="round" fill="none"></circle>
      <circle :style="animationStyle" :r="circleRadius" :cy="positionCoordinate.cy" :cx="positionCoordinate.cx" :stroke-width="option.strokeWidth" :stroke="option.strokeProgressColor" stroke-linejoin="round" stroke-linecap="round" fill="none" stroke-dashoffset="0px" :stroke-dasharray="strokeDasharray"></circle>
    </svg>
    <!-- <el-button type="primary" @mousedown.native="handleMouseDown" @mouseup.native="handleMouseUp" class="changanbtn">长按三秒</el-button> -->
    <div class="custom_svg_box" @mousedown="handleMouseDown" @mouseup="handleMouseUp" @touchstart="handleMouseDown" @touchend="handleMouseUp">
      <div class="svg-box-init" v-show="isInit && option.hasState" v-html="initSvgwithStyle"></div>
      <div class="svg-box-finish" v-show="isFinish && option.hasState" v-html="finishSvgwithStyle"></div>
      <transition name="fade">
        <div class="svg-box-tips" v-show="isTips && option.hasTips">
          <svg t="1653011506802" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3698" xmlns:xlink="http://www.w3.org/1999/xlink" width="20" height="20">
            <defs></defs>
            <path d="M361.2672 903.61856c95.744-133.89824 194.82624-252.74368 268.43136-336.40448 43.45856-49.39776 84.2752-94.3104 123.35104-134.73792 35.34848-36.57728 70.41024-70.10304 104.09984-101.66272 57.4464-53.80096 124.60032-113.39776 166.83008-138.91584l-53.92384-71.55712c-78.19264 48.9472-153.45664 101.25312-211.57888 141.4144-33.87392 23.42912-65.29024 45.91616-94.9248 67.35872-29.34784 21.23776-59.61728 44.56448-91.56608 69.0176-55.43936 42.47552-126.68928 99.57376-195.6864 158.57664l-173.11744-163.59424L0 553.43104l361.2672 350.18752z m0 0" fill="#FFF" p-id="3699"></path>
          </svg>
        </div>
      </transition>
    </div>
  </div>
</template>
<script>
import create from '../../create'
import { uploadCss, obtainCss } from '@/api/visual.js'
export default create({
  name: 'delayImg',
  data() {
    return {
      timeObj: null,
      isShowProgress: false,
      isInit: true,
      isTips: false,
      isFinish: false,
    }
  },
  computed: {
    positionCoordinate() {
      let cy = (this.component.height / 2).toFixed()
      let cx = (this.component.width / 2).toFixed()
      return {
        cy,
        cx,
      }
    },
    circleRadius() {
      // 拿到宽高的最小值作为圆的的直径
      let minValue = Math.min(this.component.height, this.component.width) - this.option.strokeWidth
      return (minValue / 2).toFixed()
    },
    strokeDasharray() {
      return this.setPx((this.circleRadius * 2 * Math.PI).toFixed())
    },
    initSvgwithStyle() {
      if (this.option.initialStatesObj.svgSource) {
        let source = this.option.initialStatesObj.svgSource
        let svgSize = this.option.initialStatesObj.svgSize + 'px'
        let svgColor = this.option.initialStatesObj.svgColor
        let style = ` style = "width: ${svgSize} ; height: ${svgSize}; fill: ${svgColor}"`
        let reg = /((?<=<svg))/g
        let reg1 = /fill=\"(\S)*\"/g // 去除掉path标签中的fill,要不然无法改色
        let source1 = source.replace(reg1, 'fill')
        return source1.replace(reg, style)
      } else {
        return ''
      }
    },
    finishSvgwithStyle() {
      if (this.option.finishStatesObj.svgSource) {
        let source = this.option.finishStatesObj.svgSource
        let svgSize = this.option.finishStatesObj.svgSize + 'px'
        let svgColor = this.option.finishStatesObj.svgColor
        let style = ` style = "width: ${svgSize} ; height: ${svgSize}; fill: ${svgColor}"`
        let reg = /((?<=<svg))/g
        let reg1 = /fill=\"(\S)*\"/g // 去除掉path标签中的fill,要不然无法改色
        let source1 = source.replace(reg1, 'fill')
        return source1.replace(reg, style)
      } else {
        return ''
      }
    },
    animationStyle() {
      console.log('计算')
      return {
        // animation: `rotate${this.id.substring(0, 8)} ${(this.option.animationTime ? this.option.animationTime : 3) * 1000}ms linear both;`,
        // width: '50px'
        animationTimingFunction: 'linear',
        animationDelay: '0s',
        animationDirection: 'normal',
        animationFillMode: 'forwards',
        animationPlayState: 'running',
        animationName: 'rotate' + this.id.substring(0, 8),
        animationDuration: (this.option.animationTime ? this.option.animationTime : 3) * 1000 + 'ms',
      }
    },
  },
  created() {},
  mounted() {
    this.createAnimationName()
  },
  methods: {
    handleMouseDown(e) {
      // 只监听鼠标左键的点击
      if (e.button === 0) {
        console.log('触发了按下')
        this.isShowProgress = true
        this.timeObj = setTimeout(() => {
          // 隐藏状态1图标
          this.isInit = !this.isInit
          // 出现Tips图标
          this.isTips = true
          if (this.option.hasTips) {
            setTimeout(() => {
              // 隐藏Tips图标
              this.isTips = false
              // 显示状态2图标、
              this.isFinish = !this.isFinish
              this.updateClick('', 'clickFormatter')
            }, 1 * 1000)
          } else {
            this.updateClick('', 'clickFormatter')
            this.isFinish = !this.isFinish
          }
          this.isShowProgress = false
        }, this.option.animationTime * 1000)
      }
    },
    handleMouseUp(e) {
      // 只监听鼠标左键的点击
      if (e.button === 0) {
        console.log('触发了松开')
        this.isShowProgress = false
        clearTimeout(this.timeObj)
      }
    },
    createAnimationName() {
      if (this.$route.path.includes('view') || this.$route.path.includes('share')) {
        let keyframes = `
        @keyframes rotate${this.id.substring(0, 8)} {
          from {
            stroke-dashoffset: ${this.strokeDasharray};
          }
          to {
            stroke-dashoffset: 0px;
          }
        }
        `
        //追加css
        let styleTag = document.createElement('style')
        styleTag.type = 'text/css'
        styleTag.id = 'img' + this.id.substring(0, 8)
        styleTag.innerHTML = keyframes
        document.getElementsByTagName('head')[0].appendChild(styleTag)
      }
    },
  },
})
</script>
<style lang='scss'>
.avue-echart-delayImg {
  position: relative;
  // border: 1px solid yellow;
}
.progressSvg {
  // position: absolute;
  // top: 50%;
  // left: 50%;
  transform: rotate(-90deg);
}
.custom_svg_box {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 2;
}
.svg-box- {
  &tips,
  &init,
  &finish {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 3;
    display: flex;
    justify-content: center;
    align-items: center;
  }
}
// .progress {
//   animation: rotate 5 * 1000ms linear both;
// }
// @keyframes rotate {
//   from {
//     stroke-dashoffset: 440px;
//   }
//   to {
//     stroke-dashoffset: 0px;
//   }
// }
// .slide-fade-enter-active {
//   transition: all 0.8s ease;
// }
// .slide-fade-leave-active {
//   transition: all 0.8s cubic-bezier(1, 0.5, 0.8, 1);
// }
// .slide-fade-enter, .slide-fade-leave-to
// /* .slide-fade-leave-active for below version 2.1.8 */ {
//   transform: translateX(10px);
//   opacity: 0;
// }
.fade-enter-active,
.fade-leave-active {
  transition: opacity 1.5s;
}
.fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */ {
  // opacity: 0;
  transition: opacity 0.5s;
}
</style>