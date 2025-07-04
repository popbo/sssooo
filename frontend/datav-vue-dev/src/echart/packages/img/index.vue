<template>
  <div
    :class="b()"
    :style="styleSizeName"
    @click="handleClick"
    @dblclick="handleDblClick"
    class="img-list"
  >
    <div
      :style="{ '--rotateAngle': option.rotateAngle + 'deg' || 0 }"
      class="list-border"
    >
      <template v-if="!option.dynamicSwitch">
        <img
          :style="[
            styleImgName,
            styleSizeName,
            animationStyle,
            sizeLockStyle,
            borderStyle,
          ]"
          :src="dataChart.value"
          class="img-rotate"
          :class="b({ rotate: rotate })"
          draggable="false"
        />
      </template>
      <template v-else-if="option.dynamicSwitch && dynamicSrc">
        <img
          :style="[
            styleImgName,
            styleSizeName,
            animationStyle,
            sizeLockStyle,
            borderStyle,
          ]"
          :src="dynamicSrc"
          class="img-rotate"
          :class="b({ rotate: rotate })"
          draggable="false"
        />
      </template>
    </div>
  </div>
</template>

<script>
import create from '../../create'
import parseCondition from '@/mixins/parseCondition.js'
import { setPx } from '../../util'
export default create({
  mixins: [parseCondition],
  name: 'img',
  data() {
    return {
      sizeLockStyle: {
        width: '100% !important',
        height: '100% !important',
      },
      borderStyle: {},
    }
  },
  created() {},
  computed: {
    styleImgName() {
      return Object.assign(
        (() => {
          if (this.rotate) {
            return {
              animationDuration: this.duration / 1000 + 's',
            }
          }
          return {}
        })(),
        {
          opacity: this.option.opacity ?? 1, // 将逻辑或改为空值合并运算符，解决透明度为0但是求值会为1
        }
      )
    },
    duration() {
      return this.option.duration || 3000
    },
    rotate() {
      return this.option.rotate
    },
    dynamicSrc() {
      if (this.coincidentCondition) {
        return this.coincidentCondition.imgUrl
      } else {
        return ''
      }
    },
    animationStyle() {
      if (this.option.animationType === 'none') return {}
      let baseAnimationObj = {
        animationTimingFunction: 'linear',
        animationDelay: '0s',
        animationDirection: 'normal',
        animationFillMode: 'forwards',
        animationPlayState: 'running',
        animationIterationCount: 'infinite',
      }
      let dynamicAnimationObj = {
        animationName: 'img' + this.id.substring(0, 8),
      }
      if (this.option.animationType === 'rotate') {
        dynamicAnimationObj.animationDuration = this.option.rotateTime + 'ms'
      } else if (this.option.animationType === 'blink') {
        dynamicAnimationObj.animationDuration = this.option.blinkTime
          ? this.option.blinkTime + 2000 + 'ms'
          : this.option.blinkTime + 'ms'
      } else if (this.option.animationType === 'jump') {
        dynamicAnimationObj.animationDuration = this.option.jumpTime + 'ms'
      }
      return Object.assign(baseAnimationObj, dynamicAnimationObj)
    },
  },
  watch: {
    'option.sizeLock': {
      handler() {
        this.getsizeLockStyle()
      },
      deep: true,
      immediate: true,
    },
    'option.sizeLockObject': {
      handler() {
        this.getsizeLockStyle()
      },
      deep: true,
      immediate: true,
    },
    component: {
      handler(val) {
        if (this.option.sizeLock) {
          this.option.sizeLockObject.width = val.width
          this.option.sizeLockObject.height = val.height
        }
      },
      deep: true,
    },
  },
  mounted() {
    window.sessionStorage.removeItem('imgBorderTypeChange')
    window.addEventListener('setItem', () => {
      if (window.sessionStorage.getItem('imgBorderTypeChange')) {
        let type = window.sessionStorage.getItem('imgBorderTypeChange')
        if (this.option.clickBorder) {
          if (type === `${this.id}-border`) {
            this.borderStyle = {
              border: `${this.option.border}px solid ${this.option.borderColor}`,
              boxShadow: `0px 0px ${this.option.borderShadow}px ${this.option.borderShadowColor}`,
            }
          } else {
            this.borderStyle = {}
          }
        } else {
          this.borderStyle = {}
        }
      } else {
        this.borderStyle = {}
      }
    })
    this.createAnimationName()
  },
  methods: {
    getsizeLockStyle() {
      if (this.option.sizeLock) {
        this.sizeLockStyle = {
          width: `${setPx(this.option.sizeLockObject.width)} !important`,
          height: `${setPx(this.option.sizeLockObject.height)} !important`,
        }
        this.component.width = this.option.sizeLockObject.width
        this.component.height = this.option.sizeLockObject.height
      } else {
        // this.getComponentData()
        this.sizeLockStyle = {
          width: '100% !important',
          height: '100% !important',
        }
      }
    },
    getComponentData() {
      if (this.option.dynamicSwitch) {
        if (this.dynamicSrc) {
          this.setImgNaturalSize(this.dynamicSrc)
        }
      } else {
        if (this.dataChart.value) {
          this.setImgNaturalSize(this.dataChart.value)
        }
      }
    },
    setImgNaturalSize(src) {
      let img = new Image()
      img.src = src
      if (img.complete) {
        this.component.width = width
        this.component.height = height
      } else {
        img.onload = () => {
          this.component.width = width
          this.component.height = height
        }
      }
    },
    handleClick() {
      this.$setSessionItem('imgBorderTypeChange', `${this.id}-border`)
      this.clickFormatter &&
        this.clickFormatter(
          {
            data: this.dataChart,
          },
          this.getItemRefs()
        )
      this.updateClick(
        {
          value: this.dataChart.value,
          comParams: this.$attrs.comParams,
          comName: this.$attrs.name,
        },
        'clickFormatter'
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
    setImgNaturalSize(src) {
      let img = new Image()
      img.src = src
      if (img.complete) {
        this.component.width = img.width
        this.component.height = img.height
      } else {
        img.onload = () => {
          this.component.width = img.width
          this.component.height = img.height
        }
      }
    },
    createAnimationName() {
      // 如果是在预览页面那么就要生成相应的styel标签插入到head标签中
      if (
        this.$route.path.includes('view') ||
        this.$route.path.includes('share')
      ) {
        let keyframes
        //追加css
        // var style = document.createElement('style')
        // style.type = 'text/css'
        // style.innerHTML = `${resq.data.data}`
        // document.getElementsByTagName('head')[0].appendChild(style)
        if (this.option.animationType === 'rotate') {
          keyframes = `
        @keyframes img${this.id.substring(0, 8)} {
          0% {
             transform: rotateX(${
               this.option.angleOfInclination + 'deg'
             }) rotateZ(0);
         }
         100% {
             transform: rotateX(${
               this.option.angleOfInclination + 'deg'
             }) rotateZ(${
            this.option.rotateDirection === 'clockwise' ? '360deg' : '-360deg'
          });
         }
        }
        `
        } else if (this.option.animationType === 'blink') {
          keyframes = `
        @keyframes img${this.id.substring(0, 8)} {
          0% {
              opacity: 1;
          }
          50% {
              opacity: 0.1;
          }
          100% {
              opacity: 1;
          }
        }
        `
        } else if (this.option.animationType === 'jump') {
          if (this.option.jumpDirection === 'upDown') {
            keyframes = `
        @keyframes img${this.id.substring(0, 8)} {
          0% {
            transform: translateY(${-this.option.jumpStart + 'px'});
        }
        50% {
            transform: translateY(${-this.option.jumpEnd + 'px'});
        }
        100% {
            transform: translateY(${-this.option.jumpStart + 'px'});
        }
        }
        `
          } else {
            keyframes = `
        @keyframes img${this.id.substring(0, 8)} {
          0% {
            transform: translateX(${-this.option.jumpStart + 'px'});
        }
        50% {
            transform: translateX(${-this.option.jumpEnd + 'px'});
        }
        100% {
            transform: translateX(${-this.option.jumpStart + 'px'});
        }
        }
        `
          }
        }
        let styleTag = document.createElement('style')
        styleTag.type = 'text/css'
        styleTag.id = 'img' + this.id.substring(0, 8)
        styleTag.innerHTML = keyframes
        document.getElementsByTagName('head')[0].appendChild(styleTag)
      }
    },
  },
  beforeDestroy() {
    // 移除对 sessionStorage 的监听
    window.removeEventListener('setItem', () => {})
    if (window.sessionStorage.getItem('imgBorderTypeChange')) {
      window.sessionStorage.removeItem('imgBorderTypeChange')
    }
  },
})
</script>
<style lang="scss" scoped>
.list-border {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
}
.img-rotate {
  transform: rotate(var(--rotateAngle));
  box-sizing: border-box;
}
.img-list {
  box-sizing: border-box;
}
</style>
