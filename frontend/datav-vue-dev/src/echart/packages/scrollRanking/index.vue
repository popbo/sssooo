<template>
  <div
    :class="b()"
    class="scrollRanking"
    @click="handleClick"
    @dblclick="handleDbClick"
    :style="{
      '--gradientColorOne': option.gradientColorOne || '#04a0ec',
      '--gradientColorTwo': option.gradientColorTwo || '#55bcd3',
      '--iconSpacing': option.iconSpacing + 'px',
      height: this.component.height + 'px',
    }"
  >
    <div class="swiper-container" ref="scrollRankingRef">
      <div
        class="swiper-wrapper"
        ref="scrollBox"
        :class="{ anim: animate == true }"
      >
        <div
          class="swiper-slide"
          ref="slide1"
          v-for="(item, index) in scrollData"
          :key="index"
          :style="{ height: swiperHight + 'px' }"
        >
          <div
            class="scroll-text"
            :style="{ width: option.progressWidth + 'px' }"
          >
            <div class="text-one">
              <div
                class="text-img"
                v-show="option.icon"
                :class="option.iconDirection === 2 ? 'text-img1' : ''"
                :style="{
                  width: option.iconSize + 'px',
                  height: option.iconSize + 'px',
                }"
              >
                <img :src="option.value" alt="" />
              </div>
              <div
                class="text-f"
                v-show="!option.theme"
                :style="{
                  fontSize: option.themeFsize + 'px',
                  color: option.themeColor || '#fff',
                }"
              >
                {{ item.label }}
              </div>
            </div>
            <div
              class="text-two"
              :style="{
                fontSize: option.numericalFsize + 'px',
                color: option.numericalColor || '#fff',
              }"
            >
              <div class="text-number">{{ item.value }}</div>
              <div
                class="text-company"
                :style="{
                  fontSize: option.numericalFsize + 'px',
                }"
              >
                {{ option.numericalunit }}
              </div>
            </div>
          </div>
          <div
            ref="progress"
            class="scroll-one"
            :style="{
              width: option.progressWidth + 'px',
              height: option.progressHeight + 'px',
              backgroundColor: option.progressBackground || '#363C51',
            }"
          >
            <div
              class="scroll-two"
              :class="option.gradient ? 'scroll-gradient' : ''"
              :style="{
                width: item.value + '%',
                backgroundColor: option.progressProspect || '#04a0ec',
              }"
            ></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import create from '../../create'
import components from '@/components/'
import common from '@/config'
import { getFunction } from '@/utils/utils.min.js'
import _ from 'lodash'
export default create({
  name: 'scrollRanking',
  inject: ['contain', 'container'],
  provide() {
    return {
      contain: this.contain,
      container: this.container,
    }
  },
  data() {
    return {
      // swiperHight: 0,
      animate: false,
      timer1: null,
      common: common,
      getFunction: getFunction,
      scrollData: [],
    }
  },
  props: {
    option: Object,
    component: Object,
  },
  // created() {
  //   // 节流
  //   this.sizeThrottle = _.throttle(this.resizeBorder, 500)
  // },
  watch: {
    dataChart: {
      handler(newVal) {
        this.option.dataLength = newVal.length
        if (this.dataChart.code == 200) {
          this.scrollData = []
          this.scrollData = [...this.dataChart.data]
          // this.$nextTick(() => {
          //   this.swiperHight = this.$refs.slide1[0].clientHeight + 7
          // })
          // console.log('scrollData==>', this.scrollData)
        } else {
          this.scrollData = []
          this.scrollData = [...this.dataChart]
          // this.$nextTick(() => {
          //   this.swiperHight = this.$refs.slide1[0].clientHeight + 7
          // })
          // console.log('scrollData==>', this.scrollData)
        }
        if (this.option.rotation) {
          if (this.scrollData.length > 0) {
            this.cleanTimer()
            if (!this.timer1) {
              this.timer1 = setInterval(this.scroll, this.option.rotationTime)
            }
          }
        } else {
          this.cleanTimer()
        }
      },
      deep: true,
    },
    // option: {
    //   handler() {
    //     this.swiperHight =
    //       this.$refs.slide1 && this.$refs.slide1.length > 0
    //         ? this.$refs.slide1[0].clientHeight + 7
    //         : 0
    //   },
    //   deep: true,
    // },
    'option.rotation': {
      handler(val) {
        if (val) {
          if (this.scrollData.length > 0) {
            this.cleanTimer()
            if (!this.timer1) {
              this.timer1 = setInterval(this.scroll, this.option.rotationTime)
            }
          }
        } else {
          this.cleanTimer()
        }
      },
    },
    'option.rotationTime': {
      handler(newVal) {
        if (!newVal) {
          this.option.rotationTime = 2000
        }
        this.cleanTimer()
        if (!this.timer1 && this.option.rotation) {
          this.timer1 = setInterval(this.scroll, this.option.rotationTime)
        }
      },
    },
    'option.progressWidth'(newVal) {
      this.component.width = newVal + 5
    },
    scrollData(newVal) {
      if (Array.isArray(newVal) && newVal.length > 0) {
        this.option.maxScrollNumber = newVal.length
      } else {
        this.option.maxScrollNumber = 0
      }
    },
    'option.animationModel'(newVal) {
      if (this.scrollData.length > 0) {
        this.cleanTimer()
        if (!this.timer1 && this.option.rotation) {
          this.timer1 = setInterval(this.scroll, this.option.rotationTime)
        }
      }
    },
  },
  // mounted() {
  //   // 监听组件尺寸变化
  //   let _this = this
  //   _this.$erd.listenTo(_this.$refs.scrollRankingRef, el => {
  //     _this.$nextTick(() => {
  //       _this.sizeThrottle(el, _this)
  //     })
  //   })
  //   // this.$nextTick(() => {
  //   //   this.swiperHight = this.$refs.slide1[0].clientHeight + 10
  //   // })
  // },
  computed: {
    rotationNumber() {
      return this.option.rotationNumber
    },
    swiperHight() {
      return this.component.height / this.rotationNumber - 10
    },
  },
  methods: {
    handleClick() {
      this.updateClick('', 'clickFormatter')
    },
    handleDbClick() {
      this.updateClick('', 'dblClickFormatter')
    },

    // resizeBorder(el, _that) {
    //   _that.component.width = el.clientWidth
    //   _that.component.height = el.clientHeight
    // },
    scroll() {
      const scrollBox = this.$refs.scrollBox
      const offset = this.$refs.slide1[0]
      const that = this
      this.option.animationModel === 'byItem'
        ? this.itemScroll(scrollBox, offset, that)
        : this.pageScroll(scrollBox, offset, that)
    },
    // 逐行滚动
    itemScroll(scrollBox, offset, that) {
      const top = offset.clientHeight + 10
      scrollBox.style.marginTop = `-${top}px` //设置style样式 向上移动一个单元格
      this.animate = !this.animate //
      setTimeout(function () {
        const firstItem = that.scrollData.shift() //删除第一个元素
        that.scrollData.push(firstItem) //尾部追加数组的第一个，放到数组最后
        scrollBox.style.marginTop = '0px' //设置style样式 向上移动30px 再回到原位
        that.animate = !that.animate // 这个地方如果不把animate 取反会出现消息回滚的现象，此时把ul 元素的过渡属性取消掉就可以完美实现无缝滚动的效果了
      }, 200)
    },
    // 逐页滚动
    pageScroll(scrollBox, offset, that) {
      const left = offset.clientWidth
      scrollBox.style.marginLeft = `-${left}px`
      this.animate = !this.animate
      setTimeout(function () {
        const itemArr = that.scrollData.splice(0, that.rotationNumber)
        that.scrollData.push(...itemArr)
        scrollBox.style.marginLeft = '0px'
        that.animate = !that.animate
      }, 200)
    },
    cleanTimer() {
      clearInterval(this.timer1)
      this.timer1 = null
    },
  },
  components: components,
  beforeDestroy() {
    this.cleanTimer()
  },
})
</script>
<style lang="scss" scoped>
.scrollRanking {
  .scroll-text {
    width: 100% !important;
    height: 50%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 3px;
    .text-one {
      display: flex;
      align-items: center;
    }
    .text-two {
      display: flex;
      > div {
        padding-right: 2px;
        display: flex;
        align-items: flex-end;
      }
    }
  }
  .text-img {
    margin-right: var(--iconSpacing);
    img {
      width: 100%;
      height: 100%;
    }
  }
  .text-img1 {
    margin-left: var(--iconSpacing);
    order: 2;
  }
  .swiper-container {
    height: 100%;
    overflow: hidden;
    transition: all 1.3s;
  }
  .anim {
    transition: all 1.3s;
  }
  .swiper-slide {
    // min-height: 30px;
    width: 100%;
    margin-bottom: 10px;
    // overflow: hidden;
  }
  .scroll-one {
    position: relative;
    overflow: hidden;
    width: 100% !important;
    .scroll-two {
      position: absolute;
      height: 100%;
    }
  }
  .scroll-gradient {
    background: linear-gradient(
      to right,
      var(--gradientColorOne),
      var(--gradientColorTwo)
    ) !important;
  }
}
</style>
