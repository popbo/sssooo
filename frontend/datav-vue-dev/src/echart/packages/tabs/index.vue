<template>
  <div :class="b()" :style="styleSizeName" ref="main">
    <avue-select
      v-if="isSelect"
      :dic="typeof dataChart === 'string' ? [] : dataChart"
      :style="styleSelectName"
      class="selected"
      v-model="active"
      @change="handleChange"
    ></avue-select>
    <div :class="b('list')" v-else>
      <div
        :class="b('item')"
        :style="[styleName, styleActiveName(item)]"
        v-for="(item, index) in dataChart"
        :key="index"
        @click="handleClick(item, index)"
        @dblclick="handleDblClick(item, index)"
      >
        <div
          v-if="item.icon"
          :class="b('icon')"
          :style="[
            styleIconName,
            styleIconBgName(item),
            styleIconActiveName(item),
          ]"
        ></div>
        <span>{{ item.label }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import create from '../../create'
export default create({
  name: 'tabs',
  data() {
    return {
      active: '',
    }
  },
  watch: {
    dataChart: {
      handler(val) {
        if (val.length !== 0) {
          this.handleClick(val[0], 0, true)
        }
      },
      immediate: true,
    },
  },
  computed: {
    isSelect() {
      return this.type === 'select'
    },
    type() {
      return this.option.type
    },
    paramName() {
      return this.option.paramName
    },
    iconSize() {
      return this.option.iconSize || 20
    },
    styleSelectName() {
      return Object.assign(
        {
          fontSize: this.setPx(this.option.fontSize || 30),
        },
        this.styleSizeName
      )
    },
    styleIconName() {
      return Object.assign({
        marginRight: this.setPx(this.option.iconSplit),
        width: this.setPx(this.option.iconSize),
        height: this.setPx(this.option.iconSize),
      })
    },
    styleName() {
      return Object.assign(
        (() => {
          if (this.option.backgroundImage) {
            return {
              backgroundImage: `url(${this.option.backgroundImage})`,
              backgroundSize: '100% 100%',
            }
          }
          return {}
        })(),
        {
          borderColor: this.option.borderColor || '#fff',
          borderStyle: 'solid',
          borderWidth: this.setPx(this.option.borderWidth || 0),
          margin: `0 ${this.setPx(this.option.split)}`,
          backgroundColor: this.option.backgroundColor,
          fontSize: this.setPx(this.option.fontSize || 30),
          color: this.option.color || '#AAC6E2',
        }
      )
    },
  },
  methods: {
    styleIconBgName(item) {
      if (item.icon) {
        return {
          backgroundImage: `url(${item.icon})`,
          backgroundSize: '100% 100%',
        }
      }
    },
    styleIconActiveName(item) {
      if (this.active == item.value && item.empIcon) {
        return {
          backgroundImage: `url(${item.empIcon})`,
          backgroundSize: '100% 100%',
        }
      }
    },
    styleActiveName(item) {
      if (this.active == item.value) {
        return Object.assign(
          (() => {
            if (this.option.empBackgroundImage) {
              return {
                backgroundImage: `url(${this.option.empBackgroundImage})`,
                backgroundSize: '100% 100%',
              }
            }
            return {}
          })(),
          {
            borderColor: this.option.empBorderColor || '#fff',
            borderStyle: 'solid',
            borderWidth: this.setPx(this.option.empBorderWidth || 0),
            color: this.option.empColor || '#fff',
          }
        )
      }
    },
    handleClick(item, index, target = false) {
      this.active = item.value
      this.updateClick(item, 'clickFormatter')
      this.clickFormatter &&
        this.clickFormatter(
          {
            type: index,
            item: item,
            value: item.value,
            data: this.dataChart,
          },
          this.getItemRefs()
        )
      if (item.href && !target) window.open(item.href, item.target)
    },
    handleChange(val) {
      this.updateClick({ value: val }, 'clickFormatter')
    },
    handleDblClick(item, index, target = false) {
      this.active = item.value
      this.updateClick(item, 'dblClickFormatter')
      this.dblClickFormatter &&
        this.dblClickFormatter(
          {
            type: index,
            item: item,
            value: item.value,
            data: this.dataChart,
          },
          this.getItemRefs()
        )
      if (item.href && !target) window.open(item.href, item.target)
    },
  },
  props: {
    option: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
})
</script>
