<template>
  <div :class="b()" :style="[contentStyleName, contentBaseStyle]">
    <div
      v-for="(item, index) in cloneData"
      :class="b('item')"
      @change="handleChange"
      :key="index"
      @click="handleClick(item, index)"
      @dblclick="handleDblClick(item, index)"
      :style="[itemBaseStyle, itemStyleName, activeItemStyle(index)]"
    >
      <el-image
        v-show="option.hasIcon && option.iconPosition !== 'right'"
        :style="[iconStyleName, leftImg]"
        :src="option.iconBackgroundImage"
      >
        <div slot="error" class="image-slot">
          <i class="el-icon-picture-outline"></i></div
      ></el-image>
      <p :style="[textStyle, textUnderlineStyle(index + 1)]">
        {{ item.label }}
      </p>
      <el-image
        v-show="option.hasIcon && option.iconPosition === 'right'"
        :style="[iconStyleName, rightImg]"
        :src="option.iconBackgroundImage"
        ><div slot="error" class="image-slot">
          <i class="el-icon-picture-outline"></i>
        </div>
      </el-image>
    </div>
  </div>
</template>

<script>
import create from '../../create'
import { deepClone, setPx } from '../../util'
export default create({
  name: 'universalTabs',
  data() {
    return {
      cloneData:[],
      rotationTimer: null,
      activeItem: null,
    }
  },
  watch: {
    dataChart: {
      handler(val) {
        this.cloneData = deepClone(val)
        if(this.option.isFaist){
          if(this.cloneData.length>0){
            this.updateClick(this.cloneData[0], 'clickFormatter')
          }
        }
      },
      deep: true,
    },
    'option.isFaist':{
      handler(val){
        if(val){
          this.activeItem = 0;
          if(this.cloneData.length>0){
            this.updateClick(this.cloneData[0], 'clickFormatter')
          }
        }else{
          this.activeItem = null
        }
      },
      deep:true,
      immediate: true,
    },
    'option.isRotation': {
      handler(newVal) {
        newVal ? this.autoRotation() : clearInterval(this.rotationTimer)
      },
      immediate: true,
    },
    'option.width'(newVal) {
      if (newVal) {
        this.component.width = newVal * 1.04 * this.cloneData.length
      }
    },
    'option.rotationTime'() {
      this.autoRotation()
    },
  },
  computed: {
    // cloneData() {
    //   if(this.option.isFaist){
    //     console.log('this.cloneData',this.dataChart)
    //     debugger
    //     if(this.dataChart.length>0){
    //       console.log('this.cloneData[0]',this.cloneData[0])
    //       debugger
    //       this.updateClick(this.cloneData[0], 'clickFormatter')
    //     }
    //   }
    //   return deepClone(this.dataChart)
    // },

    textStyle() {
      return {
        border: 'none',
      }
    },
    // 容器基础样式
    contentBaseStyle() {
      return Object.assign({
        display: 'flex',
        justifyContent: 'space-between',
        border: '2px solid rgba(256 ,256,256,0.2)',
        alignItem: 'center',
      })
    },
    // 容器配置样式
    contentStyleName() {
      return Object.assign({
        backgroundColor: this.option.backgroundColor || 'rgba(0,0,0,0.2)',
        backgroundImage: this.option.empBackgroundImage
          ? `url(${this.option.empBackgroundImage})`
          : '',
        // width: `${setPx(this.option.width || '')} !important`,
      })
    },

    //选项配置样式
    itemStyleName() {
      let styleObj = {}
      Object.assign(styleObj, {
        padding: '5px 15px',
        margin: '2px 0px',
        fontSize: setPx(this.option.fontSize),
        border: '2px solid rgba(256,256,256,0.6)',
        color: this.option.color || '#fff',
        textAlign: this.option.textAlign,
        borderRadius: setPx(this.option.radius),
        fontWeight: this.option.fontWeight,
      })
      return styleObj
    },
    // 选项基础样式
    itemBaseStyle() {
      return Object.assign({
        display: 'flex !important',
        alignItem: 'center',
        whiteSpace: 'nowrap',
        border: '2px solid rgba(256 ,256,256,0.6)',
        justifyContent: this.option.alignment,
        alignItems: 'center',
        // width: this.option.width || `${100 / this.cloneData.length - 2}%`,
        width: this.option.width
          ? setPx(this.option.width)
          : '' || `${100 / this.cloneData.length - 2}%`,
        // minWidth: '100px',
      })
    },
    leftImg() {
      return { marginRight: `${setPx(this.option.iconInterval)}` }
    },
    rightImg() {
      return { marginLeft: `${setPx(this.option.iconInterval)}` }
    },
    iconStyleName() {
      return Object.assign({
        width: setPx(this.option.iconSize),
        height: setPx(this.option.iconSize),
      })
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
  methods: {
    // 自动轮播
    autoRotation() {
      if (!!this.rotationTimer) {
        clearInterval(this.rotationTimer)
      }

      this.rotationTimer = setInterval(() => {
        if(this.activeItem===null){
          this.activeItem = 0;
        }else{
          if (this.activeItem === this.cloneData.length-1) {
            this.activeItem = 0
          }else{
            this.activeItem++
          }
        }
      }, this.option.rotationTime)
    },
    // 下划线样式
    textUnderlineStyle(index) {
      let underlineColor = this.option.underlineColor || '#fff'
      let underlineWeight = this.option.underlineWeight || 2
      let underlineRadius = this.option.underlineRadius || 0
      let position = this.option.linePosition
      const styleObj = {}

      // 边框样式
      styleObj[position] = `${setPx(underlineWeight)} solid ${underlineColor}`
      // 圆角大小
      const currentRadius = this.getUnderlineRadius(
        this.option.linePosition,
        underlineRadius
      )

      if (this.option.hasUnderline) {
        return { ...styleObj, ...currentRadius }
      }
      return { border: 'none' }
    },
    //下划线圆角
    getUnderlineRadius(position, underlineRadius) {
      switch (position) {
        case 'borderLeft':
          return {
            borderTopLeftRadius: setPx(underlineRadius),
            borderBottomLeftRadius: setPx(underlineRadius),
          }
        case 'borderRight':
          return {
            borderBottomRightRadius: setPx(underlineRadius),
            borderTopRightRadius: setPx(underlineRadius),
          }
        case 'borderTop':
          return {
            borderTopLeftRadius: setPx(underlineRadius),
            borderTopRightRadius: setPx(underlineRadius),
          }
        default:
          return {
            borderBottomLeftRadius: setPx(underlineRadius),
            borderBottomRightRadius: setPx(underlineRadius),
          }
      }
    },
    activeItemStyle(item) {
      if (item === this.activeItem) {
        return {
          color: this.option.selectedColor || '#fff',
          border: '4px solid rgba(102, 177, 255,1.0)',
          backgroundColor:
            this.option.selectedBgColor || 'rgba(102, 177, 255,0.2)',
          fontSize: `${setPx(
            this.option.selectedFontSize || this.option.fontSize
          )} !important`,
        }
      }
    },
    styleIconBgName(item) {
      if (item.icon) {
        return {
          backgroundImage: `url(${item.icon})`,
          backgroundSize: '100% 100%',
        }
      }
    },
    styleIconActiveName(item) {
      if (this.activeItem == item.value && item.empIcon) {
        return {
          backgroundImage: `url(${item.empIcon})`,
          backgroundSize: '100% 100%',
        }
      }
    },
    styleActiveName(item) {
      if (this.activeItem == item.value) {
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
            color: this.option.empColor,
          }
        )
      }
    },

    handleClick(item, index, target = false) {
      this.activeItem = index
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
      this.activeItem = index
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
})
</script>
