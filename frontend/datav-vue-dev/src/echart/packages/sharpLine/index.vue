<template>
  <div :class="b()" ref="lineSvg">
    <!-- <div style="color:#fff">{{option.defaultArrowSet==='back'||option.defaultArrowSet==='both'}}</div> -->
    <svg
      xmlns="http://www.w3.org/2000/svg"
      version="1.1"
      class="lineSvg"
      :width="svgWidth"
      :height="svgHeight"
      :viewBox="`${Math.min(option.rectStartX, option.rectEndX)} ${Math.min(
        option.rectStartY,
        option.rectEndY
      )} ${this.svgWidth} ${this.svgHeight}`"
      shape-rendering="geometricPrecision"
    >
      <defs>
        <marker
          v-show="
            option.defaultArrowSet === 'back' ||
            option.defaultArrowSet === 'both'
          "
          :id="endId"
          shape-rendering="geometricPrecision"
          markerUnits="strokeWidth"
          :markerWidth="arrowWidth"
          :markerHeight="arrowWidth"
          viewBox="0 0 12 12"
          refX="6"
          refY="6"
          orient="auto"
        >
          <path
            d="M2,2 L10,6 L2,10 L6,6 L2,2"
            :style="`fill: ${lineColor}; stroke: ${lineColor}`"
          ></path>
        </marker>
        <marker
          v-show="
            option.defaultArrowSet === 'front' ||
            option.defaultArrowSet === 'both'
          "
          :id="startId"
          shape-rendering="geometricPrecision"
          markerUnits="strokeWidth"
          :markerWidth="arrowWidth"
          :markerHeight="arrowWidth"
          viewBox="-12 -12 12 12"
          refX="-6"
          refY="-6"
          orient="auto"
        >
          <path
            transform="rotate(180)"
            d="M2,2 L10,6 L2,10 L6,6 L2,2"
            :style="`fill: ${lineColor}; stroke: ${lineColor}`"
          ></path>
        </marker>
      </defs>
      <rect
        v-show="isSelect"
        id="startPoint"
        :x="option.rectStartX"
        :y="option.rectStartY"
        :width="lineThink"
        :height="lineThink"
        :style="`fill:#008ED5;stroke-width:0;stroke:#008ED5;fill-opacity:1;stroke-opacity:1;cursor: w-resize;`"
        @mousedown.stop="
          nodeMove('rectStartX', 'rectStartY', 'rectEndX', 'rectEndY', $event)
        "
      ></rect>
      <rect
        v-show="isSelect"
        id="endPoint"
        :x="option.rectEndX"
        :y="option.rectEndY"
        :width="lineThink"
        :height="lineThink"
        :style="`fill:#008ED5;stroke-width:0;stroke:#008ED5;fill-opacity:1;stroke-opacity:1;cursor: w-resize;`"
        @mousedown.stop="
          nodeMove('rectEndX', 'rectEndY', 'rectStartX', 'rectStartY', $event)
        "
      ></rect>
      <line
        :x1="option.rectStartX + lineThink / 2"
        :y1="option.rectStartY + lineThink / 2"
        :x2="option.rectEndX + lineThink / 2"
        :y2="option.rectEndY + lineThink / 2"
        :marker-start="leftArrow"
        :marker-end="rightArrow"
        :style="`stroke-width: ${option.borderWidth};stroke: ${lineColor}`"
        :stroke-dasharray="isStroke"
      ></line>
    </svg>
  </div>
</template>

<script>
import create from '../../create'
import { uuid } from '@/utils/utils.min.js'
import parseCondition from '@/mixins/parseCondition.js'
// import G6 from '@antv/g6'
export default create({
  mixins: [parseCondition],
  name: 'sharpLine',
  inject: ['main'],
  props: ['selectedActive'],
  data() {
    return {
      // lineOption:{
      //   rectStartX: 0,
      //   rectStartY: 0,
      //   rectEndX: 130,
      //   rectEndY: 0,
      // },
      startArrow: '',
      endArrow: '',
      parentWidth: '',
      parentviewBox: '',
      isSelect: false,
      lineThink: 10,
      arrowWidth: 12,
      isStroke: '',
      leftArrow: '',
      rightArrow: '',
      startId: '',
      endId: '',
      lineLength: this.component.width,
    }
  },
  created() {
    console.log('创建组件')
    this.startId = 'arrow_startblin_' + uuid()
    this.endId = 'arrowblin_' + uuid()
    if (this.option.borderWidth >= 1) {
      this.arrowWidth = 6
    }
    if (this.option.borderWidth > 2) {
      this.lineThink = this.option.borderWidth * 5
    }
    if (
      this.option.defaultArrowSet === 'front' ||
      this.option.defaultArrowSet === 'both'
    ) {
      this.leftArrow = 'url(#' + this.startId + ')'
    } else {
      this.leftArrow = ''
    }
    if (
      this.option.defaultArrowSet === 'back' ||
      this.option.defaultArrowSet === 'both'
    ) {
      this.rightArrow = 'url(#' + this.endId + ')'
    } else {
      this.rightArrow = ''
    }

    this.$bus.$on('inputChangeLength', ({ newLength, currentIndex }) => {
      // console.log('新长度', newLength, 'index值', currentIndex)
      // 如果事件发送组件的index与当前id一致，则进行相关处理
      if (currentIndex === this.id.slice(4)) {
        if (this.option.rectStartX === this.option.rectEndX) {
          this.option.rectEndY = newLength
        }
        if (this.option.rectStartY === this.option.rectEndY) {
          this.option.rectEndX = newLength
        }
        const lineAngle = this.angle(
          { x: this.option.rectStartX, y: this.option.rectStartY },
          { x: this.option.rectEndX, y: this.option.rectEndY }
        )
        const LineEndZb = this.calNewPointByAngle2(
          { x: this.option.rectStartX, y: this.option.rectStartY },
          Math.abs(lineAngle),
          newLength
        )
        //当终点坐标x,y都大于起点坐标
        if (
          this.option.rectEndX > this.option.rectStartX &&
          this.option.rectEndY > this.option.rectStartY
        ) {
          this.option.rectEndX = LineEndZb.x
          this.option.rectEndY = LineEndZb.y
        }
        if (
          this.option.rectEndX < this.option.rectStartX &&
          this.option.rectEndY > this.option.rectStartY
        ) {
          this.option.rectEndX = -LineEndZb.x
          this.option.rectEndY = LineEndZb.y
        }
        if (
          this.option.rectEndX < this.option.rectStartX &&
          this.option.rectEndY < this.option.rectStartY
        ) {
          this.option.rectEndX = -LineEndZb.x
          this.option.rectEndY = -LineEndZb.y
        }
        if (
          this.option.rectEndX > this.option.rectStartX &&
          this.option.rectEndY < this.option.rectStartY
        ) {
          this.option.rectEndX = LineEndZb.x
          this.option.rectEndY = -LineEndZb.y
        }
      }
    })
  },
  computed: {
    svgWidth() {
      let width =
        Math.abs(this.option.rectStartX - this.option.rectEndX) + this.lineThink
      this.component.width = width
      return width
    },
    svgHeight() {
      let height =
        Math.abs(this.option.rectStartY - this.option.rectEndY) + this.lineThink
      this.component.height = height
      return height
    },
    viewBox() {
      return (
        this.option.rectStartX +
        ' ' +
        this.option.rectEndY +
        ' ' +
        this.svgWidth +
        ' ' +
        this.svgHeight
      )
    },
    lineColor() {
      if (this.coincidentCondition) {
        return this.coincidentCondition.bodyColor
      } else {
        return this.option.bodyColor
      }
    },
  },
  watch: {
    selectedActive(val) {
      this.isSelect = val
      // this.isSelect(val)
    },
    'option.borderWidth': {
      handler(newval) {
        console.log('进来了')
        if (newval >= 1) {
          this.arrowWidth = 6
        }
        if (newval > 2) {
          this.lineThink = newval * 5
        } else {
          this.lineThink = 10
        }
      },
    },
    'option.defaultStyle': {
      handler(newval) {
        if (newval === 'solid') {
          this.isStroke = ''
        } else {
          this.isStroke = '3,2'
        }
      },
      immediate: true,
    },
    'option.defaultArrowSet': {
      handler(newval) {
        if (newval === 'front' || newval === 'both') {
          this.leftArrow = 'url(#' + this.startId + ')'
        } else {
          this.leftArrow = ''
        }
        if (newval === 'back' || newval === 'both') {
          this.rightArrow = 'url(#' + this.endId + ')'
        } else {
          this.rightArrow = ''
        }
      },
    },
    'option.direction': {
      handler(newval) {
        if (newval === 'transverse') {
          this.option.rectEndX = this.lineLength
          this.option.rectEndY = this.option.rectStartY
        } else {
          this.option.rectEndY = this.lineLength
          this.option.rectEndX = this.option.rectStartX
        }
      },
    },
    'option.width': {
      handler(newval) {
        this.lineLength = newval
      },
    },
  },
  methods: {
    // 流程图节点移动事件
    nodeMove: function (rectX_move, rectY_move, rectX_static, rectY_static) {
      document.onmousemove = e => {
        //鼠标按下并移动的事件
        this.option[rectX_move] += e.movementX
        this.option[rectY_move] += e.movementY
        // 在移动某一个点时要判断移动点在静止点的右侧还是左侧，在静止点的左侧移动就要改变inset的第四个值
        let leftValue = parseFloat(this.$refs.lineSvg.getAttribute('left'))
        let topValue = parseFloat(this.$refs.lineSvg.getAttribute('top'))
        // 拿移动点的值和静止点的x值作比较若小于则说明在静止点的左侧
        // let insetValue = this.$refs.lineSvg.style.inset.split(' ')
        // let leftValue = parseFloat(insetValue[3])
        // let topValue = parseFloat(insetValue[0])
        // 移动点在静止点左上
        if (
          this.option[rectX_move] < this.option[rectX_static] &&
          this.option[rectY_move] < this.option[rectY_static]
        ) {
          console.log(1, topValue)
          // this.$refs.lineSvg.style.setProperty('inset', `${ topValue += e.movementY }px auto auto ${ leftValue  += e.movementX}px`)
          this.$refs.lineSvg.setAttribute('top', `${(topValue += e.movementY)}`)
          this.$refs.lineSvg.setAttribute(
            'left',
            `${(leftValue += e.movementX)}`
          )
        }

        // 移动点在静止点水平或左下
        if (
          this.option[rectX_move] < this.option[rectX_static] &&
          this.option[rectY_move] >= this.option[rectY_static]
        ) {
          console.log(2, topValue)
          // this.$refs.lineSvg.style.setProperty('inset', `${ topValue }px auto auto ${ leftValue  += e.movementX}px`)
          this.$refs.lineSvg.setAttribute('top', `${topValue}`)
          this.$refs.lineSvg.setAttribute(
            'left',
            `${(leftValue += e.movementX)}`
          )
        }
        // 移动点在静止点右上
        if (
          this.option[rectX_move] > this.option[rectX_static] &&
          this.option[rectY_move] < this.option[rectY_static]
        ) {
          console.log(3, topValue)
          // this.$refs.lineSvg.style.setProperty('inset', `${ topValue += e.movementY }px auto auto ${ leftValue }px`)
          this.$refs.lineSvg.setAttribute('top', `${(topValue += e.movementY)}`)
          this.$refs.lineSvg.setAttribute('left', `${leftValue}`)
        }

        // 移动点在静止点右侧且水平
        if (
          this.option[rectX_move] > this.option[rectX_static] &&
          this.option[rectY_move] == this.option[rectY_static]
        ) {
          console.log(5)
          // this.$refs.lineSvg.style.setProperty('inset', `${ topValue }px auto auto ${ leftValue }px`)
          this.$refs.lineSvg.setAttribute('top', `${topValue}`)
          this.$refs.lineSvg.setAttribute('left', `${leftValue}`)
        }
        this.lineLength = parseInt(
          Math.sqrt(
            Math.pow(this.option.rectEndX - this.option.rectStartX, 2) +
              Math.pow(this.option.rectEndY - this.option.rectStartY, 2)
          )
        )
        this.$bus.$emit('lineWidth', this.lineLength)
        const index = this.$refs.lineSvg.getAttribute('index')
        this.$refs.lineSvg.setAttribute('intimetop', topValue)
        this.$refs.lineSvg.setAttribute('intimeleft', leftValue)
        this.$emit('lineMovePosi', index, topValue, leftValue)
      }
      document.onmouseup = () => {
        document.onmousemove = null
        document.onmouseup = null
      }
    },
    angle(start, end) {
      const diff_x = end.x - start.x,
        diff_y = end.y - start.y

      //返回角度,不是弧度
      return (360 * Math.atan(diff_y / diff_x)) / (2 * Math.PI)
    },
    calNewPointByAngle2(startPoint, angle, distance) {
      var endPoint = {}
      // if()
      // 角度转弧度
      var radian = (angle * Math.PI) / 180

      // 计算新坐标(对于无限接近0的数字，此处没有优化)
      endPoint.x = startPoint.x + distance * Math.cos(radian)
      endPoint.y = startPoint.y + distance * Math.sin(radian)

      return endPoint
    },
  },
  beforeDestroy() {
    //收尾操作，销毁
    this.$bus.$off('inputChangeLength') //$off解绑当前组件所用到的事件
  },
})
</script>
<style lang="scss" scoped>
/deep/ .avue-draggable__mask {
  display: none;
}
.avue-echart-sharpLine {
  position: relative;
}
.lineSvg {
  /* margin: 200px 300px; */
  position: absolute;
  /* border:1px solid #000 */
}
</style>
<style lang="scss">
.box-avue-echart-sharpLine {
  & > .avue-draggable {
    & > .avue-draggable__wrapper {
      & > .avue-draggable__mask {
        display:none;
      }
    }
  }
}
</style>