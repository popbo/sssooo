<template>
  <div :class="b()">
    <button
      @mouseenter="handleEnter"
      @mouseleave="handleleave"
      @blur="handleBlur"
      @focus="handleFocus"
      @click="handleClick"
      @dblclick="handleDbClick"
      v-show="this.option.visible"
      :style="[styleName, hoverStyle, focusStyle]"
      :disabled="!this.option.enable"
      :class="this.option.enable ? '' : 'is-disabled'"
    >
      {{ dataChart.value }}
    </button>
  </div>
</template>

<script>
import create from '../../create'
export default create({
  name: 'button',
  data() {
    return {
      hoverStyle: {},
      focusStyle: {},
    }
  },
  created() {},
  methods: {
    handleEnter() {
      if (this.option.hoverIsUse) {
        this.hoverStyle = {
          color: this.option.hoverStyle.color,
          fontSize: this.setPx(this.option.hoverStyle.fontSize || 30),
          background: this.option.hoverStyle.bgColor,
          borderColor: this.option.hoverStyle.borderColor,
        }
      }
    },
    handleleave() {
      if (this.option.hoverIsUse) {
        this.hoverStyle = {}
      }
    },
    handleFocus() {
      if (this.option.focusIsUse) {
        this.focusStyle = {
          color: this.option.focusStyle.color,
          fontSize: this.setPx(this.option.focusStyle.fontSize || 30),
          background: this.option.focusStyle.bgColor,
          borderColor: this.option.focusStyle.borderColor,
        }
      }
    },
    handleBlur() {
      if (this.option.focusIsUse) {
        this.focusStyle = {}
      }
    },
    handleClick() {
      this.updateClick(
        {
          value: this.dataChart.value,
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
    handleDbClick() {
      this.updateClick(
        {
          value: this.dataChart.value,
        },
        'dblClickFormatter'
      )
      this.clickFormatter &&
        this.clickFormatter(
          {
            data: this.dataChart,
          },
          this.getItemRefs()
        )
    },
  },
  computed: {
    styleName() {
      const obj = {
        borderColor: this.option.borderColor || '#309EF8',
        borderStyle: this.option.borderStyle || 'solid',
        borderWidth: this.setPx(this.option.borderWidth || 0),
        borderRadius: this.setPx(this.option.borderRadius || 4),
        backgroundColor: this.option.isGradient
          ? ''
          : this.option.bgColor || '#264767',
        // background: `linear-gradient(0deg, ${this.option.bgColor1} 0%, ${this.option.bgColor2} 100%)`,
        fontSize: this.setPx(this.option.fontSize || 30),
        fontFamily: this.option.fontFamily,
        textAlign: this.option.textAlign,
        color: this.option.color || '#fff',
      }
      let bgPrams = {}
      if (this.option.backgroundImage) {
        bgPrams = {
          backgroundSize: '100% 100%',
          backgroundImage: `url(${this.option.backgroundImage})`,
          backgroundColor: this.option.bgColor || '#264767',
        }
      } else {
        if (this.option.isGradient) {
          bgPrams = {
            background: `linear-gradient(0deg, ${this.option.bgColor1} 0%, ${this.option.bgColor2} 100%)`,
          }
        } else {
          bgPrams = {
            // background: this.option.bgColor || "#309EB6",
          }
        }
      }
      let parms = Object.assign(obj, bgPrams)
      return parms
    },
  },
})
</script>

<style scoped lang="scss">
button {
  width: 100%;
  height: 100%;
  cursor: pointer;
}
button.is-disabled {
  cursor: not-allowed;
  opacity: 0.5;
}
</style>
