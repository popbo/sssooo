<template>
  <div
    class="customSegments"
    :class="[{ cursorBuild: isBuild }, b()]"
    @click="handleClick"
    @dblclick="handleDblClick"
    ref="lineSvg"
  >
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
          :id="endId"
          shape-rendering="geometricPrecision"
          markerUnits="userSpaceOnUse"
          :markerWidth="endNodeSize"
          :markerHeight="endNodeSize"
          viewBox="0 0 12 12"
          refX="6"
          refY="6"
          orient="auto"
        >
          <path
            d="M2,2 L10,6 L2,10 L6,6 L2,2"
            :style="`fill: ${
              endNodeSolid ? endNodeColor : 'none'
            }; stroke: ${endNodeColor}`"
          ></path>
        </marker>
        <marker
          :id="startId"
          shape-rendering="geometricPrecision"
          markerUnits="userSpaceOnUse"
          :markerWidth="startNodeSize"
          :markerHeight="startNodeSize"
          viewBox="-12 -12 12 12"
          refX="-6"
          refY="-6"
          orient="auto"
        >
          <path
            transform="rotate(180)"
            d="M2,2 L10,6 L2,10 L6,6 L2,2"
            :style="`fill: ${
              startNodeSolid ? startNodeColor : 'none'
            }; stroke: ${startNodeColor}`"
          ></path>
        </marker>
        <marker
          :id="startCricleId"
          shape-rendering="geometricPrecision"
          markerUnits="userSpaceOnUse"
          :markerWidth="startNodeSize"
          :markerHeight="startNodeSize"
          :viewBox="`0 0 ${startNodeSize * 2 + 4} ${startNodeSize * 2 + 4}`"
          :refX="startNodeSize + 2"
          :refY="startNodeSize + 2"
          orient="auto"
        >
          <circle
            :cx="startNodeSize + 2"
            :cy="startNodeSize + 2"
            :r="startNodeSize"
            :style="`fill: ${
              startNodeSolid ? startNodeColor : 'none'
            }; stroke: ${startNodeColor}`"
          />
        </marker>
        <marker
          :id="endCricleId"
          shape-rendering="geometricPrecision"
          markerUnits="userSpaceOnUse"
          :markerWidth="endNodeSize"
          :markerHeight="endNodeSize"
          :viewBox="`0 0 ${endNodeSize * 2 + 4} ${endNodeSize * 2 + 4}`"
          :refX="endNodeSize + 2"
          :refY="endNodeSize + 2"
          orient="auto"
        >
          <circle
            :cx="endNodeSize + 2"
            :cy="endNodeSize + 2"
            :r="endNodeSize"
            :style="`fill: ${
              endNodeSolid ? endNodeColor : 'none'
            }; stroke: ${endNodeColor}`"
          />
        </marker>
        <marker
          :id="midCricleId"
          shape-rendering="geometricPrecision"
          markerUnits="userSpaceOnUse"
          :markerWidth="midNodeSize"
          :markerHeight="midNodeSize"
          :viewBox="`0 0 ${midNodeSize * 2 + 4} ${midNodeSize * 2 + 4}`"
          :refX="midNodeSize + 2"
          :refY="midNodeSize + 2"
          orient="auto"
        >
          <circle
            :cx="midNodeSize + 2"
            :cy="midNodeSize + 2"
            :r="midNodeSize"
            :style="`fill: ${
              midNodeSolid ? midNodeColor : 'none'
            }; stroke: ${midNodeColor}`"
          />
        </marker>
      </defs>
      <polyline
        :points="pointsStr"
        :style="`stroke-width: ${option.borderWidth};fill: none;stroke: ${lineColor}`"
        :stroke-dasharray="isStroke"
        :marker-start="`url(#${objStartId})`"
        :marker-mid="`url(#${objMidId})`"
        :marker-end="`url(#${objEndId})`"
      ></polyline>
      <polyline
        v-if="selectStatus && isBuild"
        :points="pointsTepmStr"
        :style="`stroke-width: ${option.borderWidth};fill: ${lineColor};stroke: ${lineColor}`"
        :stroke-dasharray="isStroke"
      ></polyline>
    </svg>
    <div
      class="pointBox"
      v-if="isBuild"
      @click="addPoint($event)"
      @mousemove.stop="MouseOverFn($event)"
    >
      <div
        v-for="(item, index) in pointList"
        :key="index"
        :style="{ left: item[0] - 2 + 'px', top: item[1] - 2 + 'px' }"
        class="itemBox"
      >
        <div class="deletePoint" @click.stop="deletePoint(index)">
          <i class="el-icon-circle-close"></i>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import create from '../../create'
  import { uuid } from '@/utils/utils.min.js'
  import components from '@/components/';
  import parseCondition from '@/mixins/parseCondition.js'
  // import G6 from '@antv/g6'
  export default create({
    mixins: [parseCondition],
    name: 'customSegments',
    inject: ['contain', 'container'],
    provide() {
      return {
        contain: this.contain,
        container: this.container,
      };
    },
    props: {
      option: Object,
      component: Object,
      pointList: Object
    },
    data() {
      return {
        isStroke: '',
        arrowWidth: 6,
        startId: '',
        startCricleId: "",
        endCricleId: "",
        endId: '',
        objStartId: '',
        objEndId: '',
        pointsTepm: [],
        pointsTepmStr: '',
        midCricleId: '',
      }
    },
    created() {
      this.startId = 'arrow_startblin_' + uuid()
      this.endId = 'arrowblin_' + uuid()
      this.startCricleId = 'cricle_startblin_' + uuid()
      this.endCricleId = 'cricle_endblin_' + uuid()
      this.midCricleId = 'cricle_midblin_' + uuid()
      document.addEventListener('keyup', this.escEvent)

    },
    mounted() {
      document.addEventListener('mousemove', this.escEvent)
    },
    computed: {
      svgWidth() {
        return this.component.width
      },
      svgHeight() {
        return this.component.height
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
      pointsStr() {
        return this.pointList.join(' ') || '';
      },
      // pointsTepmStr() {
      //   console.log("pointsTepmStr",(this.pointList.slice(-1).concat(this.pointsTepm)).join(' '))
      //   return (this.pointList.slice(-1, 1).concat(this.pointsTepm)).join(' ');
      // },
      selectStatus() {
        return this.option.selectStatus
      },
      isBuild() {
        return this.$route.name === 'build'
      },
      startNodeColor() {
        return this.option.startNodeColor
      },
      startNodeSolid() {
        let type = false;
        switch (this.option.startNodeType) {
          case 'none': {
            type = false;
            this.objStartId = ''
            break;
          }
          case 'hollowArrows': {
            type = false;
            this.objStartId = this.startId
            break;
          }
          case 'solidArrows': {
            type = true;
            this.objStartId = this.startId
            break;
          }
          case 'hollowCircles': {
            type = false;
            this.objStartId = this.startCricleId
            break;
          }
          case 'solidCircles': {
            type = true;
            this.objStartId = this.startCricleId
            break;
          }
          default: {
            type = false;
            this.objStartId = ''
            break;
          }
        }
        return type
      },
      startNodeType() {
        let type = 'none';
        switch (this.option.startNodeType) {
          case 'none': {
            type = 'none';
            break;
          }
          case 'hollowArrows': {
            type = 'none';
            break;
          }
          case 'solidArrows': {
            type = 'none';
            break;
          }
          case 'hollowCircles': {
            type = 'none';
            break;
          }
          case 'solidCircles': {
            type = 'none';
            break;
          }
          default: {
            type = 'none';
            break;
          }
        }
        return type
      },
      startNodeSize() {
        return this.option.startNodeSize
      },
      hasStartNode() {
        let type = false;
        switch (this.option.startNodeType) {
          case 'none': {
            type = false;
            break;
          }
          case 'hollowArrows': {
            type = true;
            break;
          }
          case 'solidArrows': {
            type = true;
            break;
          }
          case 'hollowCircles': {
            type = true;
            break;
          }
          case 'solidCircles': {
            type = true;
            break;
          }
          default: {
            type = false;
            break;
          }
        }
        return type
      },
      endNodeColor() {
        return this.option.endNodeColor
      },
      endNodeSolid() {
        let type = false;
        switch (this.option.endNodeType) {
          case 'none': {
            type = false;
            this.objEndId = ''
            break;
          }
          case 'hollowArrows': {
            type = false;
            this.objEndId = this.endId
            break;
          }
          case 'solidArrows': {
            type = true;
            this.objEndId = this.endId
            break;
          }
          case 'hollowCircles': {
            type = false;
            this.objEndId = this.endCricleId
            break;
          }
          case 'solidCircles': {
            type = true;
            this.objEndId = this.endCricleId
            break;
          }
          default: {
            type = false;
            this.objEndId = ''
            break;
          }
        }
        return type
      },
      endNodeType() {
        let type = 'none';
        switch (this.option.endNodeType) {
          case 'none': {
            type = 'none';
            break;
          }
          case 'hollowArrows': {
            type = 'none';
            break;
          }
          case 'solidArrows': {
            type = 'none';
            break;
          }
          case 'hollowCircles': {
            type = 'none';
            break;
          }
          case 'solidCircles': {
            type = 'none';
            break;
          }
          default: {
            type = 'none';
            break;
          }
        }
        return type
      },
      endNodeSize() {
        return this.option.endNodeSize
      },
      hasEndNode() {
        let type = false;
        switch (this.option.endNodeType) {
          case 'none': {
            type = false;
            break;
          }
          case 'hollowArrows': {
            type = true;
            break;
          }
          case 'solidArrows': {
            type = true;
            break;
          }
          case 'hollowCircles': {
            type = true;
            break;
          }
          case 'solidCircles': {
            type = true;
            break;
          }
          default: {
            type = false;
            break;
          }
        }
        return type
      },
      midNodeColor() {
        return this.option.midNodeColor
      },
      midNodeSolid() {
        let type = false;
        switch (this.option.midNodeType) {
          case 'none': {
            type = false;
            this.objMidId = ''
            break;
          }
          case 'hollowCircles': {
            type = false;
            this.objMidId = this.midCricleId
            break;
          }
          case 'solidCircles': {
            type = true;
            this.objMidId = this.midCricleId
            break;
          }
          default: {
            type = false;
            this.objMidId = ''
            break;
          }
        }
        return type
      },
      midNodeType() {
        let type = 'none';
        switch (this.option.midNodeType) {
          case 'none': {
            type = 'none';
            break;
          }
          case 'hollowCircles': {
            type = 'none';
            break;
          }
          case 'solidCircles': {
            type = 'none';
            break;
          }
          default: {
            type = 'none';
            break;
          }
        }
        return type
      },
      midNodeSize() {
        return this.option.midNodeSize
      },
    },
    components: components,
    methods: {
      addPoint(e) {
        if (this.selectStatus) {
          if (e.offsetX && e.offsetY) {
            this.pointList.push([e.offsetX, e.offsetY])
          }
        }
        console.log("eee>", this.pointList)
      },
      deletePoint(index) {
        this.pointList.splice(index, 1)
      },
      escFn(val) {
        console.log("escFn==>", val)
      },
      escEvent() {
        if (window?.event?.keyCode == 27) this.closeComp()
      },
      closeComp() {
        this.option.selectStatus = false
      },
      MouseOverFn(e) {
        console.log("eeee=>", e.target.className, e)
        if (this.selectStatus && e.target.className == 'pointBox') {
          if (e.offsetX && e.offsetY) {
            this.pointsTepm.splice(0, 2, e.offsetX, e.offsetY)
            this.pointsTepmStr = (this.pointList.slice(-1).concat(this.pointsTepm)).join(' ')
          }
        }

      },
      handleClick() {
        if(this.$route.name === 'build'){
          return false
        }
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
          },
          'clickFormatter'
        )
      },
      handleDblClick() {
        if(this.$route.name === 'build'){
          return false
        }
        this.dblClickFormatter &&
          this.dblClickFormatter(
            {
              data: this.dataChart,
            },
            this.getItemRefs()
          )
      },
    },
    watch: {
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
    },
    beforeDestroy() {
      document.removeEventListener('keyup', this.escEvent)
    },
  })
</script>
<style lang="scss" scoped>
.avue-echart-sharpLine {
  position: relative;
}
.lineSvg {
  /* margin: 200px 300px; */
  position: absolute;
  /* border:1px solid #000 */
}
.customSegments {
  position: relative;
  height: 100%;
}
.cursorBuild {
  cursor: crosshair;
}
.pointBox {
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  z-index: 1;
}
.itemBox {
  width: 4px;
  height: 4px;
  position: absolute;
  border: 1px solid #3a89fe;
  &:hover {
    .deletePoint {
      display: block;
    }
  }
}
.deletePoint {
  display: none;
  position: absolute;
  width: 8px;
  height: 8px;
  top: -8px;
  right: -8px;
  color: #3a89fe;
  .el-icon-circle-close {
    font-size: 8px;
    position: absolute;
  }
}
</style>
<style lang="scss">
</style>

