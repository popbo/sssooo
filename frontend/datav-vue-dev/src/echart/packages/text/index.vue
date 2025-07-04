<template>
  <div :class="b()" ref="main" @click="handleClick">
    <div ref="box" :class="b('box')">
      <div
        class="svg-box-before"
        v-if="isUseSvg && svgConObj.location === 'wzq'"
        v-html="svgWithStyle"
        :class="[{ blink: isBlink }]"
      ></div>
      <!-- 文本框内的字体颜色既可以在配置里面配置 也可以在条件里面配置 当找到满足的条件conTextColor就会返回一个字体颜色覆盖掉前面设置的 -->
      <div v-if="option.link && option.linkHref">
        <a
          ref="text"
          :class="[b('text'), { blink: isBlink }]"
          v-if="!isHide"
          :href="linkHref"
          :style="[styleName, styleSizeName, conTextColor, slectStyle]"
          :target="linkTarget"
        >
          <span class="show-space" ref="textRef">{{ dataChart.value }}</span>
          <span
            v-if="option.underline"
            class="under-line"
            :style="lineStyle"
          ></span>
        </a>
      </div>
      <div v-else>
        <div
          ref="text"
          :class="[b('text'), { blink: isBlink }]"
          v-if="!isHide"
          :style="[styleName, styleSizeName, conTextColor, slectStyle]"
        >
          <span class="show-space" ref="textRef">{{ dataChart.value }}</span>
          <span
            v-if="option.underline"
            class="under-line"
            :style="lineStyle"
          ></span>
          <span v-show="option.unitFlage" :style="unitStyle">{{option.unitText}}</span>
        </div>
      </div>
      <div
        class="svg-box-after"
        v-if="isUseSvg && svgConObj.location === 'wzh'"
        v-html="svgWithStyle"
        :class="[{ blink: isBlink }]"
      ></div>
    </div>
  </div>
</template>

<script>
import create from '../../create'
import parseCondition from '@/mixins/parseCondition.js'
import findTree from 'xe-utils/findTree'
export default create({
  mixins: [parseCondition],
  inject: ['contain'],
  name: 'text',
  data() {
    return {
      check: '',
      date: new Date(),
      left: 0,
      slectStyle: {},
      lineStyle: {},
      selectType: '',
    }
  },
  props: {
    option: {
      type: Object,
      default: () => {
        return {}
      },
    },
    data: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  computed: {
    scroll() {
      return this.vaildData(this.option.scroll, false)
    },
    linkHref() {
      if (this.option.link) {
        return this.option.linkHref || '#'
      } else {
        return '#'
      }
    },
    linkTarget() {
      return this.option.linkTarget || '_self'
    },
    step() {
      return this.option.step || 5
    },
    speed() {
      return this.option.speed || 100
    },
    lineHeight() {
      return this.option.lineHeight || 40
    },
    fontSize() {
      return this.option.fontSize || 0
    },
    split() {
      return this.option.split
    },
    textWidth() {
      const textLen = (this.dataChart.value || '').length
      return textLen * this.fontSize
    },
    textShadow() {
      if (JSON.stringify(this.option.textShadow) !== '{}') {
        let { hShadow, vShadow, blur, color } = this.option.textShadow
        const newTextShadow = `${hShadow || 0}px ${vShadow || 0}px ${
          blur || 0
        }px ${color || '#fff'}`
        return !hShadow && !vShadow && !blur ? '' : newTextShadow
      } else {
        return '0px 0px 0px #fff'
      }
    },
    styleName() {
      let parms = {
        position: 'relative',
        width: this.scroll ? this.setPx(this.textWidth) : 'auto',
        transform: 'translateX(' + this.left + 'px)',
        textAlign: this.option.textAlign,
        letterSpacing: this.setPx(this.split),
        textIndent: this.setPx(this.split),
        fontWeight: this.option.fontWeight || 'normal',
        fontFamily: this.option.fontFamily,
        fontSize: this.fontSize + 'px',
        lineHeight: this.lineHeight + 'px',
        color: this.option.color || '#333',
        textShadow: this.textShadow,
        overflowY: 'scroll',
        borderRadius: this.option.borderWidth + 'px',
        fontStyle: this.option.fontStyle ? 'oblique' : 'normal',
      }
      if (this.option.backgroundImg) {
        return Object.assign(parms, {
          background: `url(${this.option.backgroundImg}) 0% 0% / 100% 100% no-repeat`,
        })
      }
      return Object.assign(parms, {
        backgroundColor: this.option.backgroundColor,
      })
    },
    unitStyle(){
      return {
        display: 'inline-block',
        position: 'relative',
        top:this.option.unitTop + 'px',
        left:this.option.unitLeft + 'px',
        fontSize:this.option.unitFontSize + 'px',
        color:this.option.unitColor,
        fontWeight:this.option.unitFontWeight,
        fontFamily:this.option.unitFontFamily
      }
    },
    // 再根据计算出的coincidentCondition来计算是否带有闪烁效果
    isBlink() {
      if (this.coincidentCondition) {
        return this.coincidentCondition.animationType.includes('闪烁')
      } else {
        return false
      }
    },
    // 再根据计算出的coincidentCondition来计算是否隐藏文字内容
    isHide() {
      if (this.coincidentCondition) {
        return this.coincidentCondition.animationType.includes('隐藏内容')
      } else {
        return false
      }
    },
    // 条件设置中文字的颜色
    conTextColor() {
      if (this.coincidentCondition) {
        return { color: this.coincidentCondition.textColor }
      } else {
        return {}
      }
    },
    // 根据计算出的coincidentCondition来判断是否使用图标
    isUseSvg() {
      if (this.coincidentCondition) {
        return this.coincidentCondition.contentStyle === 'svg'
      } else {
        return false
      }
    },
    // 根据计算出的coincidentCondition来计算出svg配置对象
    svgConObj() {
      if (
        this.coincidentCondition &&
        this.coincidentCondition.contentStyle === 'svg'
      ) {
        return {
          ...this.coincidentCondition.svgConObj,
          ...this.coincidentCondition.svgObj,
        }
      } else {
        return null
      }
    },
    // 根据svgConObj计算出svg
    svgWithStyle() {
      if (this.svgConObj) {
        let source = this.svgConObj.link
        let svgSize = this.svgConObj.svgSize + 'px'
        let svgColor = this.svgConObj.svgColor
        let style = ` style = "width: ${svgSize} ; height: ${svgSize}; fill: ${svgColor}"`
        let reg = /((?<=<svg))/g
        let reg1 = /fill=\"(\S)*\"/g // 去除掉path标签中的fill,要不然无法改色
        let source1 = source.replace(reg1, 'fill')
        return source1.replace(reg, style)
      } else {
        return ''
      }
    },
  },
  watch: {
    '$store.state.textSelectTypeChange': {
     handler(newVal) {
        let seltype = this.selectType + 'textSelectTypeChange'
        this.slectStyle = {}
        this.lineStyle = {
          height: 0,
        }
        if(newVal[seltype]){
          let s = newVal[seltype]
          if(s ===`${this.id}-select`){
            if (this.option.selectImg) {
              this.slectStyle = {
                background: `url(${this.option.selectImg}) 0% 0% / 100% 100% no-repeat`,
                color: this.option.selectColor,
              }
            } else {
              this.slectStyle = {
                backgroundColor: this.option.selectBackground,
                color: this.option.selectColor,
              }
            }
            this.lineStyle = {
              height: this.option.underlineHeight + 'px',
              background: this.option.underlineColor,
            }
            if(this.option.cancelClickSelect){
              let time = this.option.cancelTime || 0
              setTimeout(()=>{
                let newTextSelectTypeChange = {
                }
                newTextSelectTypeChange[seltype] = ''
                this.$store.commit('intTextSelectTypeChange', newTextSelectTypeChange)
              },time)
            }
          }
        }
      },
      deep: true, 
      immediate: true 
    },
    scroll() {
      this.move()
    },
    speed() {
      this.move()
    },
    'dataChart.value': {
      handler(newValue, oldValue) {
        // 因为在common.js中dataChart的value并不存在，是一个undefined,所以第一次一定会触发这个事件，那么当
        if (oldValue !== undefined) {
          // this.updateData()
          this.updateClick({ value: newValue }, 'contentChangeFormatter')
        }
      },
    },
  },
  created() {
    // this.updateClick({ value: this.data.value }, 'initFormatter')
    setInterval(() => {
      this.date = new Date()
    }, 1000)
    if (!this.option.textShadow) {
      this.$set(this.option, 'textShadow', {
        hShadow: 0,
        vShadow: 0,
        blur: 0,
        color: '#fff',
      })
    }
  },
  mounted() {
    this.move()
  },
  beforeDestroy() {
    clearInterval(this.check)
  },
  methods: {
    handleClick() {
      let id = ''
      if (Object.prototype.toString.call(this.id) == '[object String]') {
        id = this.id.replace('list', '')
      }
      let seltype = findTree(this.contain.nav, item => item.index === id, {
        children: 'list',
      })
      if (seltype) {
        if (seltype.parent) {
          if (seltype.parent.index) {
            this.selectType = seltype.parent.index
          }
        }
      }
      let type = this.selectType + 'textSelectTypeChange'
      if(this.option.clickSelect){
        let oldTextSelectTypeChange = this.$store.state.textSelectTypeChange
        let newTextSelectTypeChange = {
        }
        newTextSelectTypeChange[type] = `${this.id}-select`
        let textSelectTypeChange = Object.assign(oldTextSelectTypeChange,newTextSelectTypeChange)
        this.$store.commit('intTextSelectTypeChange', textSelectTypeChange)
      }
      this.updateClick(
        {
          value: this.dataChart.value,
          comParams: this.$attrs.comParams,
        },
        'clickFormatter'
      )
      this.clickFormatter &&
        this.clickFormatter(
          {
            data: this.dataChart,
          },
          this.getItemRefs()
        )
    },
    move() {
      clearInterval(this.check)
      if (this.scroll) {
        this.check = setInterval(() => {
          if (this.left < -(this.textWidth + this.$refs.textRef?.offsetLeft)) {
            this.left = this.width
          }
          this.left = this.left - this.step
        }, this.speed)
      } else {
        this.left = 0
      }
    },
  },
})
</script>
<style lang="scss">
@keyframes twinkling {
  0% {
    opacity: 0.2;
    filter: alpha(opacity=20);
    transform: scale(1);
  }

  50% {
    opacity: 0.5;
    filter: alpha(opacity=50);
    transform: scale(1.12);
  }

  100% {
    opacity: 0.2;
    filter: alpha(opacity=20);
    transform: scale(1);
  }
}
.under-line {
  position: absolute;
  bottom: 0px;
  left: 0px;
  width: 100%;
}
.blink {
  animation: twinkling 2.2s ease-in-out infinite;
}
.hide {
  display: none;
}
// .svg-box-before, .svg-box-after {
//   width: 50px;
//   height: 50px;
// }

.avue-echart-text__box {
  display: flex;
  // .avue-echart-text__text {
  //   text-align: left !important;
  // }
}
// .show-space {
//   white-space: pre;
// }
</style>
