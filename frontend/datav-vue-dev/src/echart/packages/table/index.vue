<template>
  <div
    :class="[b(), option.isOverflowEllipsis === 0 ? 'ol-wordwarpNormal' : 'wordwarpNormal']"
    :style="{
      '--headerBorderColor':option.headerBorderColor || '',
      '--headerBorderSize': option.headerBorder?option.headerBorderSize+'px': 0 + 'px',
      '--headerFontSize':option.headerFontSize+'px'|| 0,
      '--bodyFontSize':option.bodyFontSize+'px',
      '--lineHeight': option.tdLineHeight + 'px',
      '--headerHeight': option.headerHeight + 'px',
      '--headerLineHeight': option.headerLineHeight + 'px',
      '--paginationFontSize': option.paginationFontSize
        ? option.paginationFontSize + 'px'
        : '14px',
      '--paginationColor': option.paginationColor || '#fff',
      '--otherPgColor': option.paginationColor || '#606266',
      '--paginationPosition': option.paginationPosition || 'flex-end',
      '--pagiTextScale': pagiTextScale,
      '--headerFontWeight': option.headerFontWeight,
      '--choiceColor': option.choiceColor,
      '--choiceBacolor': option.choiceBacolor,
      '--choiceBorder': option.choiceBorder + 'px',
      '--choiceBorderColor': option.choiceBorderColor,
      '--paginationBcolor': option.paginationBcolor,
      '--paginationBorder': option.paginationBorder + 'px',
      '--paginationBorderColor': option.paginationBorderColor,
      '--paginationScolor': option.paginationScolor,
      '--paginationIconcolor':option.paginationIconcolor,
      '--paginationIconBcolor':option.paginationIconBcolor,
      '--paginationXbcolor':option.paginationXbcolor,
      '--paginationMargin':option.paginationMargin+'px',
      '--paginationMarginTop':option.paginationMarginTop+'px',
      '--paginationSborderColor':option.paginationSborderColor,
      '--paginationNormal':option.paginationNormal,
      '--paginationWidth':option.paginationWidth + 'px',
      '--paginationHidth':option.paginationHidth + 'px',
      '--paginationIconFiont':option.paginationIconFiont + 'px',
      '--xuanWidth':option.xuanWidth + 'px',
      '--xuanHeight':option.xuanHeight + 'px',
      '--xanWidth':option.xanWidth + 'px',
      '--xanHeight':option.xanHight + 'px',
    }"
  >
    <el-table
      ref="table"
      @cell-click="cellClick"
      @cell-dblclick="cellDblClick"
      @selection-change="selectionChange"
      :data="filterTableData"
      :height="height"
      :cell-style="cellStyle"
      :row-style="rowStyle"
      :header-row-style="headerRowStyle"
      :header-cell-style="headerCellStyle"
      :show-header="!option.showHeader"
      :style="tableStyle"
    >
      <!-- :data="
        option.showPagination
          ? tableData.slice(startIndex, endIndex)
          : tableData
      " -->
      <!-- <el-table-column type="index" label="#" header-align="center" align="center" v-if="option.index" width="60">
        <span slot-scope="{$index}">{{$index+1}}</span>
      </el-table-column> -->
      <el-table-column
        v-if="option.multipleChoice"
        type="selection"
        width="55">
      </el-table-column>
      <el-table-column
        type="index"
        :label="option.nameProportion"
        v-if="option.startSequence"
        :width="option.widthProportion * 6.9"
      >
        <template slot-scope="{ row, $index }">
          <span class="img-proportion" :style="imgProportion" v-if="option.imgType===2">
            {{ row.serialNumber }}
          </span>
          <span
            v-else
            :class="[{ blink }]"
            :style="[seqCellStyle(row, $index), seqTextSizeStyle]"
            >{{ row.serialNumber }}</span
          >
        </template>
      </el-table-column>
      <template v-for="(item, index) in option.column">
        <!-- <template v-for="(item, index) in column"> -->
        <table-column
            v-if="item.children && item.children.length"
            :key="index"
            :coloumn-header="item"
            :component="component"
            :option="option"
            :tableOptions="tableOptions"
            :switchFalge="switchFalge"
            :drawConditionList="drawConditionList"
            @changetrainstation="changetrainstation"
            class="more-header"
        ></table-column>
        <template v-else>
          <el-table-column
            v-if="item.hide !== true"
            :show-overflow-tooltip="item.prop===option.currentColom&&option.tableSelect?false:true"
            :key="index"
            :prop="item.prop"
            :label="item.label"
            :width="
              item.width ? (item.width * component.width).toFixed(2) / 100 : ''
            "
          >
            <template slot-scope="{ row, column }">
              <span
                v-if="
                  row.extendObj.isUseSvg &&
                  row.extendObj.svgObj.location === 'wzq'
                "
                :class="[{ blink: row.extendObj.isBlink }]"
                v-html="row.extendObj.svgObj.link"
              >
              </span>
              <span
                v-if="row.extendObj.isShowText&&item.prop!==option.currentColom&&getSwitch(row,column,true)"
                :class="[{ blink: row.extendObj.isBlink }]"
                >{{ row[column.property] }}</span
              >
              <!--  <span
                v-if="row.extendObj.isShowText"
                :class="[{ blink: row.extendObj.isBlink }]"
                >{{ row[column.property] }}</span
              > -->
              <span
                v-if="row.extendObj.isShowText&&item.prop===option.currentColom&&!option.tableSelect&&getSwitch(row,column,true)"
                :class="[{ blink: row.extendObj.isBlink }]"
                >
                {{ row[column.property] }}
              </span>
              <span
                v-if="
                  row.extendObj.isUseSvg &&
                  row.extendObj.svgObj.location === 'wzh'
                "
                :class="[{ blink: row.extendObj.isBlink }]"
                v-html="row.extendObj.svgObj.link"
              ></span>
              <el-switch
                v-if="getSwitch(row,column)"
                class="table-switch"
                :style="tableSwitch"
                v-model="row.switchFalge"
                width="50"
                @change="e => changetrainstation(e, row)"
                active-color="#3a89fe "
                inactive-color="#4d4f59 ">
              </el-switch>
              <el-select v-model="row[option.currentColom]" placeholder="请选择" v-if="item.prop===option.currentColom&&option.tableSelect">
                <el-option
                  v-for="item in tableOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
        </template>
      </template>
      <el-table-column
        v-if="option.openOperation &&filterTableData.length>0"
        :label="option.operateText"
        :width="option.operaColomnWidth * 6.9"
      >
        <template slot-scope="scope">
         <div :class="option.buttonArrangement==='transverse'?'button-arrangement':''">
          <div v-for="(item,index) in option.buttonList" :key="index" class="button-list">
            <button :style="buttonStyle(item,index)" @click.stop="handleClick(scope.row,item)">
              {{ item.buttonContext }}
            </button>
          </div>
         </div>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-box" v-show="option.showPagination">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[5, 10,15,20,25,30]"
        :page-size="pageSize"
        :layout=option.paginationType
        :total="pageTotal"
        :popper-class="id"
      >
      </el-pagination>
    </div>
  </div>
</template>
<script>
import create from '../../create'
import { deepClone, setPx } from '../../util'
import TableColumn from "./tableColumn.vue";
import { EventBus } from '@/bus.js'
import { uploadCss, obtainCss } from '@/api/visual.js'
// import { color } from 'highcharts';
export default create({
  name: 'table',
  data() {
    return {
      switchFalge:false,
      value11:false,
      tableOptions:[],
      input:'',
      headerHeight: '',
      cHeaderHeight:'',
      headerFontSize:'',
      // height: '',
      scrollCheck: '',
      currentPage: 1,
      pageTotal: this.option.count,
      pageNum: 1,
      pageSize: 5,
      startIndex: 0,
      endIndex: 0,
      tableData: [],
      pageScrollTimer: null,
      timeoutTimer: '',
    }
  },
  components:{
    TableColumn
  },
  watch: {
    dataChart:{
      handler(val){
        if(val.tableOptions){
          this.tableOptions = val.tableOptions;
        }
      },
      deep:true
    },
    'option.buttonArrangement'(val){
        if(this.option.buttonList.length>0){
          let widthData = 0
          this.option.buttonList.forEach(item=>{
             widthData = (item.buttonWidth+12)+widthData
          })
          if(val === 'transverse'){
            this.option.operaColomnWidth = parseInt(widthData/6.9) < 16 ? 16 : parseInt(widthData/6.9)
          }else{
            this.option.operaColomnWidth = 16
          }
        }
    },
    // 'option.buttonList':{
    //   handler(val) {
    //   if(val.length>0){
    //       let widthData = 0
    //       val.forEach(item=>{
    //          widthData = (item.buttonWidth+12)+widthData
    //       })
    //       if(this.option.buttonArrangement === 'transverse'){
    //         this.option.operaColomnWidth = parseInt(widthData/6.9) < 16 ? 16 : parseInt(widthData/6.9)
    //       }else{
    //         this.option.operaColomnWidth = 16
    //       }
    //   }
    //   },
    //   deep:true
    // },
    'option.animationModel'(newVal) {
      this.setTime()
      this.option.scroll = true
    },
    scrollSpeed() {
      this.setTime()
    },
    // 监听是否滚动
    'option.scroll'(newVal) {
      if(!newVal){
        this.option.showPagination = true
      }
      this.setTime()
      // if (!newVal) {
      //   this.option.showPagination = true
      // }
    },
    // 监听是否分页
    'option.showPagination'(newVal) {
      if (newVal && this.option.animationModel === 'byItem') {
        this.option.scroll = false
      }
    },
    //监听滚动时间变化
    'option.rotationTime'() {
      this.setTime()
      this.option.scroll = true
    },
    'option.showHeader': {
      handler(newValue) {
        if (newValue) {
          // 如果是true表示隐藏了表头，所以高度设置为0
          this.headerHeight = 0
        } else {
          this.headerHeight = 48
        }
      },
    },
    // 'option.isGridlines': {
    //   handler() {
    //     this.$nextTick(() => {
    //       this.headerHeight = this.$refs.table.$refs.headerWrapper
    //         ? parseInt(this.$refs.table.$refs.headerWrapper.clientHeight)
    //         : 0
    //     })
    //   },
    //   deep: true,
    //   immediate: false,
    // },
    'option.borderXWidth': {
      handler() {
        this.$nextTick(() => {
          this.headerHeight = this.$refs.table.$refs.headerWrapper
            ? parseInt(this.$refs.table.$refs.headerWrapper.clientHeight)
            : 0
        })
      },
      deep: true,
      immediate: false,
    },
    // 表格数据变化时通知功能按钮组件
    tableData: {
      handler(newValue) {
        // console.log('表格数据发生变化', newValue)
        this.$bus.$emit('dataChange')
      },
      deep: true,
    },
    'option.paginationSelectBcolor':{
      handler(){
        if(this.$route.name === 'build'){
          this.css()
        }
      },
      deep:true
    },
    'option.paginationSelectFonit':{
      handler(){
        if(this.$route.name === 'build'){
          this.css()
        }
      },
      deep:true
    },
    'option.paginationSelectColor':{
      handler(){
        if(this.$route.name === 'build'){
          this.css()
        }
      },
      deep:true
    },
    'option.paginationSelectScolor':{
      handler(){
        if(this.$route.name === 'build'){
          this.css()
        }
      },
      deep:true
    },
    'option.paginationSelectHcolor':{
      handler(){
        if(this.$route.name === 'build'){
          this.css()
        }
      },
      deep:true
    }
  },
  beforeDestroy() {
    //销毁
    this.$bus.$off('dataChange') //$off解绑当前组件所用到的事件
  },
  computed: {
    imgProportion(){
      return {
        display: 'inline-block',
        width: this.option.imgWidth + 'px',
        height: this.option.imgHeight + 'px',
        background: `url(${this.option.value}) no-repeat`,
        backgroundSize: '100% 100%',
        fontSize:this.option.fontSize + 'px',
        color:this.option.textColor,
        textAlign: 'center',
        lineHeight: this.option.imgHeight + 'px',
      }
    },
    // 数据过滤
    filterTableData() {
      this.$nextTick(() => {
          this.headerHeight = this.$refs.table.$refs.headerWrapper
            ? parseInt(this.$refs.table.$refs.headerWrapper.clientHeight)
            : 0
      })
      let cloneTableData = deepClone(this.tableData)
      // if (this.option.startRank) {
      //   cloneTableData.sort((a, b) => a.id - b.id)
      // }
      if (this.option.showPagination) {
        if (Array.isArray(cloneTableData)) {
          cloneTableData = cloneTableData.slice(this.startIndex, this.endIndex)
        } else {
          this.$message.error('数据处理可能有问题')
          cloneTableData = []
        }
      }
      if(cloneTableData.length>0){
        cloneTableData.forEach((item,index)=>{
          item.serialNumber = index+1
        })
      }
      return cloneTableData
    },
    iconColors() {
      return this.option.iconColor
    },
    //icon背景颜色组
    currentBgColorArray() {
      let arr = []
      for (let i = 0; i < this.filterTableData.length; i++) {
        if (i === this.iconColors.length) {
          i = 0
        }
        arr.push(this.iconColors[i])
        if (arr.length === this.filterTableData.length) {
          return arr
        }
      }
    },
    scrollTime() {
      return this.option.scrollTime
    },
    scrollSpeed() {
      return this.option.scrollSpeed || 1
    },
    scroll() {
      return this.option.scroll
    },
    cellHeight() {
      // if(this.option.cellHeight){
      //   return this.option.cellHeight
      // }
      this.cHeaderHeight = this.option.headerHeight;
      this.headerFontSize = this.option.headerFontSize;
      this.headerHeight = this.$refs.table.$refs.headerWrapper
        ? parseInt(this.$refs.table.$refs.headerWrapper.clientHeight)
        : 0
      let height 
      if(this.option.showPagination){
        height =  ( parseInt(this.height - this.headerHeight) / this.pageSize)
        // if((this.pageTotal/this.pageSize)<=1 ){
        //   height = ( parseInt(this.height - this.headerHeight) / this.pageTotal)
        // }else{
        //   height =  ( parseInt(this.height - this.headerHeight) / this.pageSize)
        // }
      }else{
        height = parseInt((this.height - this.headerHeight) / this.option.count)
      }
      this.$forceUpdate()
      return height
    },
    rotationTime() {
      return this.option.rotationTime
    },
    animationModel() {
      return this.option.animationModel
    },
    tableStyle() {
      return {
        '--xBorderWidth': this.option.isGridlines
          ? this.setPx(this.option.borderXWidth)
          : 0,
        '--xBorderColor':
          this.option.borderXWidth > 0 && this.option.borderXColor
            ? this.option.borderXColor
            : '#00c6ff',
        '--yBorderWidth': this.option.isGridlines
          ? this.setPx(this.option.borderYWidth)
          : 0,
        '--yBorderColor':
          this.option.borderYWidth > 0 && this.option.borderYColor
            ? this.option.borderYColor
            : '#00c6ff',
        '--borderCollasp': this.option.isGridlines ? 'collapse' : '',
        '--borderXStyle':
          this.option.isGridlines && this.option.borderXWidth > 0
            ? 'solid'
            : '',
        '--borderYStyle':
          this.option.isGridlines && this.option.borderYWidth > 0
            ? 'solid'
            : '',
        // '--tableBodyHeight':this.setPx(this.height - this.headerHeight)+'!important'
      }
    },
    seqTextSizeStyle() {
      const { fontSize } = this.option
      if (fontSize >= 12) {
        return { fontSize: setPx(fontSize) }
      } else {
        return {
          fontSize: `12px`,
          transform: `scale(${fontSize / 12})`,
        }
      }
    },
    pagiTextScale() {
      const { paginationFontSize } = this.option
      return paginationFontSize < 12 ? paginationFontSize / 12 : 1
    },
    tableSwitch(){
      return {
        display: 'inline-block',
        height: '100%',
        width: 'auto !important' 
      }
    }
  },
  props: {
    option: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  mounted() {
    EventBus.$on('tableSwitchDrawWarn',()=>{
      this.switchFalge = true;
    })
    this.css()
  },
  created() {
    this.pageSize = this.option.pageSize;
    this.endIndex = this.pageSize
    this.$nextTick(() => {
      // this.height = parseInt(this.$el.clientHeight)
      this.headerHeight = this.$refs.table.$refs.headerWrapper
        ? parseInt(this.$refs.table.$refs.headerWrapper.clientHeight)
        : 0
      setTimeout(() => {
        this.setTime()
      }, this.scrollTime)
    })
  },
  methods: {
    selectionChange(data){
      this.option.selectTableData = data
    },
    changetrainstation(e,row) {
      if (e) {
        this.updateClick(row, 'openTableClickFormatter')
      } else {
        this.updateClick(row, 'closeTableClickFormatter')
      }
    },
    getSwitch(row,column,other){
      let falge = false;
      let switchData = []
      this.drawConditionList.forEach(item=>{
        if(item.contentStyle==='swh'){
          switchData.push(item)
        }
      })
      let property = column.property
      for (let key in row) {
        let coincidentindex = this.parseCondition(
          switchData,
          key,
          row[key]
        )
        if (coincidentindex !== undefined) {
          let obj = switchData[coincidentindex]
          if(property === obj.drawObjSwitch){
            falge = true;
            if(row.switchFalge===undefined){
              if(obj.switch==='sk'){
                this.$set(row,'switchFalge',true)
              }
              if(obj.switch==='sg'){
                this.$set(row,'switchFalge',false)
              }
            }else{
              if(this.switchFalge){
                if(obj.switch==='sk'){
                  this.$set(row,'switchFalge',true)
                }
                if(obj.switch==='sg'){
                  this.$set(row,'switchFalge',false)
                }
              }
            }
          }
        }
      }
      if(other){
        falge = !falge
      }
      return falge
    },
    buttonStyle(item,index) {
      let marginBottom  = 0
      let marginTop  = 0
      let marginRight = 0
      if(this.option.buttonArrangement==='transverse'){
        marginBottom = 0;
        marginTop = 0;
        marginRight = 6 + 'px'
      }else{
        marginBottom = this.option.buttonList.length>0?'10px':0
        marginTop = index===0&&this.option.buttonList.length>0?'10px':0
        marginRight = 0;
      }
      let parms = {
          width: this.setPx(item.buttonWidth || 56),
          height: this.setPx(item.buttonHeight || 30),
          color: item.buttonColor || '#fff',
          fontSize: this.setPx(item.buttonFontSize || 14),
          borderWidth: this.setPx(item.buttonBorderWidth ?? 0),
          borderStyle: 'solid',
          borderColor: item.buttonBorderColor,
          marginBottom: marginBottom,
          marginTop: marginTop,
          marginRight: marginRight,
          borderRadius: item.borderWidth + 'px',
          cursor: 'pointer',
      }
      if(item.backgroundGradient){
        parms = Object.assign(parms,{
          background: `linear-gradient(to bottom, ${item.bgColor1}, ${item.bgColor2})`,
        })
      }else{
        parms = Object.assign(parms,{
          backgroundColor: item.buttonBgc,
        })
      }
      return parms
    },
    setTime() {
      clearInterval(this.scrollCheck)
      clearInterval(this.pageScrollTimer)
      if (this.scroll && this.animationModel === 'byItem') {
        this.option.showPagination = false
        this.$nextTick(() => {
          this.scrollCheck = setInterval(()=>{
           this.filterTableData.push(this.filterTableData.shift())
          },this.rotationTime )
        })
      } else if (this.scroll && this.animationModel === 'byPage') {
        this.option.showPagination = true
        // const pageCount = Math.ceil(this.pageTotal / this.pageSize)
        this.currentPage = 1
        this.pageScrollTimer = setInterval(() => {
          const pageCount = Math.ceil(this.pageTotal / this.pageSize)
          // console.log(Math.ceil(this.pageTotal / this.pageSize),pageCount,888)
          if (this.currentPage == pageCount) {
            this.currentPage = 0
          }
          this.currentPage = this.currentPage + 1
          this.$nextTick(() => {
            this.handleCurrentChange(this.currentPage)
          })
        }, this.rotationTime + 200)
        // this.scrollControl()
      }
      // else {
      //   // divData.scrollTop = divData.clientHeight
      // }
    },
    //序列icon样式
    seqCellStyle(item, $index) {
      // let index = this.option.colorFollowRank ? Number(item.id - 1) : $index
      let currentColor = {}
      const { color1, color2 } = this.currentBgColorArray[$index]
      // 如果只有一个颜色,则不使用渐变色样式
      if (!color1 || !color2) {
        currentColor = !color1
          ? { backgroundColor: color2 }
          : { backgroundColor: color1 }
      } else {
        currentColor = {
          backgroundImage: `linear-gradient(to right, ${this.currentBgColorArray[$index].color1} ,  ${this.currentBgColorArray[$index].color2})`,
        }
      }
      return {
        display: 'inline-block',
        textAlign: 'center',
        lineHeight: setPx((this.option.sequenceProportion / 100) * 50),
        width: setPx((this.option.sequenceProportion / 100) * 50),
        height: setPx((this.option.sequenceProportion / 100) * 50),
        borderRadius: '50%',
        color: this.option.textColor,
        // fontSize: setPx(this.option.fontSize),
        opacity: (this.currentBgColorArray[$index].opacity / 100).toFixed(1),
        ...currentColor,
      }
    },

    cellClick(row, column, cell, event) {
      if(this.getSwitch(row, column)){
        return false
      }
      this.updateClick(row, 'clickFormatter')
      this.clickFormatter &&
        this.clickFormatter(
          {
            type: column,
            item: row,
            data: this.tableData,
          },
          this.getItemRefs()
        )
    },
    cellDblClick(row, column, cell, event) {
      if(this.getSwitch(row, column)){
        return false
      }
      this.updateClick(row, 'dblClickFormatter')
      this.dblClickFormatter &&
        this.dblClickFormatter(
          {
            type: column,
            item: row,
            data: this.tableData,
          },
          this.getItemRefs()
        )
    },

    cellStyle({ row, column, rowIndex, columnIndex }) {
      row.extendObj = {
        isShowText: true,
        isUseSvg: false,
        isBlink: false,
        svgObj: {},
      }
      let styleObj = {
        padding: 0,
        height: this.setPx(this.cellHeight),
        fontSize: this.setPx(this.option.bodyFontSize),
        color: this.option.bodyColor,
        textAlign:
          column.type == 'index' ? 'center' : this.option.bodyTextAlign,
      }
      // if (this.isSql) {
      let property = column.property
      for (let key in row) {
        let coincidentindex = this.parseCondition(
          this.drawConditionList,
          key,
          row[key]
        )
        if (coincidentindex !== undefined) {
          let obj = this.drawConditionList[coincidentindex]
          if (obj.drawObj === 'allRow') {
            this.addExtendObj(row, obj, styleObj)
          } else if (property === obj.drawObj) {
            console.log(row[property],column,5555)
            row.extendObj.isShowText = false;
            this.addExtendObj(row, obj, styleObj)
          }
        }
        // }
      }
      return styleObj
    },
    addExtendObj(row, obj, styleObj) {
      row.extendObj.isShowText = !obj.animationType.includes('隐藏内容')
      row.extendObj.isBlink = obj.animationType.includes('闪烁')
      if (obj.contentStyle === 'text') {
        styleObj.color = obj.textColor
        styleObj.fontSize = this.setPx(obj.fontSize)
      } else if (obj.contentStyle === 'bgc') {
        styleObj.backgroundColor = obj.bgColor
      } else if (obj.contentStyle === 'svg') {
        let source = obj.svgObj.link
        let svgSize = obj.svgConObj.svgSize + 'px'
        let svgColor = obj.svgConObj.svgColor

        let style = ` style = "width: ${svgSize} ; height: ${svgSize}; fill: ${svgColor}"`
        let reg = /((?<=<svg))/g
        let reg1 = /fill=\"(\S)*\"/g // 去除掉path标签中的fill,要不然无法改色
        let source1 = source.replace(reg1, 'fill')
        row.extendObj.svgObj.link = source1.replace(reg, style)
        row.extendObj.isUseSvg = true
        row.extendObj.svgObj.location = obj.svgConObj.location
      }
    },
    rowStyle({ row, rowIndex }) {
      // console.log(row,8888)
      return {
        backgroundColor:
          rowIndex % 2 == 0 ? this.option.nthColor : this.option.othColor,
      }
    },
    headerRowStyle() {
      return {
        backgroundColor: this.option.headerBackground,
        height: this.option.headerHeight+'px'
        
      }
    },
    headerCellStyle({ row, column, rowIndex, columnIndex }) {
      let headerBorderSize = this.option.headerBorder ? this.option.headerBorderSize+'px':0+'px'
      return {
        fontSize: this.setPx(this.option.headerFontSize),
        backgroundColor: this.option.headerBackground,
        color: this.option.headerColor,
        height: '100%',
        textAlign:
          column.type == 'index' ? 'center' : this.option.headerTextAlign,
        fontFamily: 'SourceHanSansCN-Regular',
        borderLeft: columnIndex!==0?`${headerBorderSize} solid ${this.option.headerBorderColor}`:0,
        boxSizing: 'border-box'
      }
      
    },
    handleSizeChange(value) {
      this.option.pageSize = value;
      this.pageSize = value
      this.option.count = value
      this.startIndex = (this.pageNum - 1) * this.pageSize
      this.endIndex = this.pageNum * this.pageSize
    },
    handleCurrentChange(value) {
      this.pageNum = value
      this.startIndex = (this.pageNum - 1) * this.pageSize
      this.endIndex = this.pageNum * this.pageSize
    },
    parseCondition(arr, key, value) {
      // 思路是在arr条件中每个条件项都是或的关系，因此要筛选出那个dataChart.value满足的条件
      // coincidentindex保存满足的条件的index值
      let coincidentindex
      // 首先判断是否配置了渲染条件，如果配置了那么渲染条件的数组长度大于0
      if (arr.length > 0) {
        for (let i = 0; i < arr.length; i++) {
          let enterValue = arr[i].enterValue // 表达式的输入值是否是dataChart.value
          if (key === enterValue) {
            let filter = arr[i].filter
            if (filter.length > 0) {
              let logic = arr[i].logic // 条件分支之间的关系是且还是或  and 且  or 或
              let filterResult = this.handleFilter(
                filter,
                logic,
                enterValue,
                value
              )
              if (filterResult) {
                coincidentindex = i
                break
              }
            }
          }
          // 首先判断一下条件项有没有配置分支条件，即filter数组长度是否大于0
        }
        return coincidentindex
      } else {
        // 没有配置渲染条件数组
      }
    },
    handleFilter(filter, logic, enterValue, chartValue) {
      const isAllNumber = function (chartValue, itemValue) {
        let num1 = Number(chartValue)
        let num2 = Number(itemValue)
        return !isNaN(num1) && !isNaN(num2)
      }
      // let reg = /-?\d+(\.\d+)?/
      // if(chartValue.match(reg)){
      //   chartValue = chartValue.match(reg)[0]
      // }
      const switchFnc = function (item) {
        let result
        switch (item.term) {
          case 'eq': // '='
            if(Object.prototype.toString.call(chartValue)=="[object String]"){
              chartValue = chartValue.replace(item.unit, "")
            }
            result = chartValue == item.value
            break
          case 'ne': // '!='
            if(Object.prototype.toString.call(chartValue)=="[object String]"){
              chartValue = chartValue.replace(item.unit, "")
            }
            result = chartValue !== item.value
            break
          case 'lt': // '<' 如果是小于号，那么要看比较的两个值是否都是数字,这个比较只限于数字的比较
            if(Object.prototype.toString.call(chartValue)=="[object String]"){
              chartValue = chartValue.replace(item.unit, "")
            }
            if (isAllNumber(chartValue, item.value)) {
              result = Number(chartValue) < Number(item.value)
            } else {
              result = false
            }
            break
          case 'le': // '<='
            if(Object.prototype.toString.call(chartValue)=="[object String]"){
              chartValue = chartValue.replace(item.unit, "")
            }
            if (isAllNumber(chartValue, item.value)) {
              result = Number(chartValue) <= Number(item.value)
            } else {
              result = false
            }
            break
          case 'gt': // '>'
            if(Object.prototype.toString.call(chartValue)=="[object String]"){
              chartValue = chartValue.replace(item.unit, "")
            }
            if (isAllNumber(chartValue, item.value)) {
              console.log('chartValue',chartValue)
              result = Number(chartValue) > Number(item.value)
            } else {
              result = false
            }
            break
          case 'ge': // '>='
            if(Object.prototype.toString.call(chartValue)=="[object String]"){
              chartValue = chartValue.replace(item.unit, "")
            }
            if (isAllNumber(chartValue, item.value)) {
              result = Number(chartValue) >= Number(item.value)
            } else {
              result = false
            }
            break
        }
        return result
      }
      // 当条件分支间的关系为且
      if (logic === 'and') {
        // 如果为且的话那么就要求filter的每一项都返回true,那么可以选择数组的every方法
        const allResult = filter.every(item => {
          return switchFnc(item)
        })
        return allResult
      } else if (logic === 'or') {
        // 当条件分支间的关系为否
        const allResult = filter.some(item => {
          return switchFnc(item)
        })
        return allResult
      }
    },
    handleClick(row,item) {
      let bottonType = {
        tableButtonId:item.tableButtonId,
        tableButtonContext:item.buttonContext
      }
      let prams = {...row,bottonType}
      this.updateClick(prams, 'columnClickFormatter')
    },
    async css(){
      let id = this.id.substring(4, this.id.length)
      let styles = `.${this.id}{
        background-color: ${this.option.paginationSelectBcolor};
      }
      .${this.id} .el-select-dropdown__item{
        font-size: ${this.option.paginationSelectFonit}px;
        color: ${this.option.paginationSelectColor};
      }
      .${this.id} .el-select-dropdown__item.hover{
        background-color: ${this.option.paginationSelectHcolor}
      }
      .${this.id} .el-select-dropdown__item.selected{
        background-color: ${this.option.paginationSelectScolor}
      }
      .${this.id} .popper__arrow::after{
        border-bottom-color: ${this.option.paginationSelectBcolor} !important
      }
      `
      //提交最新修改CSS
      await uploadCss(id, styles)
      //获取最新css
      obtainCss(id).then(resq => {
        var style = document.createElement('style')
        style.type = 'text/css'
        style.innerHTML = `${resq.data.data}`
        document.getElementsByTagName('head')[0].appendChild(style)
      })
    }
  },
})
</script>
<style scoped lang="scss">
.pagination-box {
  margin-top: var(--paginationMarginTop);
  display: flex;
  justify-content: flex-end;
}
.button-list{
  position: relative;
}
.img-proportion{
  position: relative;
  .img-name{
    position: absolute;
  }
}
.button-arrangement{
  display: flex;
}
/deep/.el-checkbox__inner{
  width: 16px;
  height: 16px;
  border: var(--choiceBorder) solid var(--choiceBorderColor);
}
/deep/.el-table__empty-text{
  font-size: 32px;
}
/deep/.el-checkbox__inner::after{
  height: 8px;
  left: 5px;
}
/deep/ .el-checkbox__input.is-checked .el-checkbox__inner, .el-checkbox__input.is-indeterminate .el-checkbox__inner{
  border: 1px solid var(--choiceBorderColor);
  background-color: var(--choiceColor) !important;
}
/deep/.el-checkbox__input{
  .el-checkbox__inner{
    background-color: var(--choiceBacolor) !important;
  }
}
.table-switch{
  /deep/.el-switch__core{
    height: 20px !important;
  }
  /deep/.el-switch__core:after{
    height: 19px !important;;
    width: 19px !important;;
  }
}
/deep/.el-table--border::after, .el-table--group::after, .el-table::before{
  background-color: transparent !important;
}
/deep/.el-table--border, .el-table--group{
  border: 0px solid transparent !important;
}
/deep/.el-table__header{
   .cell{
    line-height: var(--headerFontSize);
    height: 100%;
    // overflow: visible !important
  }
}
/deep/.el-table__header-wrapper{
  th{
    padding:0px;
    // padding-left: 0px;
    // padding-right: 0px;
  }
}
// /deep/.el-table td,.el-table th{
//   padding: 0px !important;
// }
/deep/.el-table--border th{
  border-left: var(--headerBorderSize) solid var(--headerBorderColor) !important;
  border-collapse:collapse;
  border-right: 0px;
}
/deep/.el-table__header-wrapper{
  tr{
    &:first-child{
      th{
        &:first-child{
          border-left: 0px !important;
        }
      }
    }
  }
}

/deep/.el-table--border th,.el-table__fixed-right-patch {
    border-bottom: var(--headerBorderSize) solid var(--headerBorderColor);
    border-collapse:collapse;
}
.el-table {
  /deep/ .el-table__body {
    // //-webkit-border-horizontal-spacing: 13px;  // 水平间距
    // -webkit-border-vertical-spacing: 4px; // 垂直间距
    width: calc(100% - 5px)!important;
    box-sizing: border-box;
    .el-table__row {
      td {
        box-sizing: border-box;
        .cell{
         span{
          line-height: var(--lineHeight);
         }
        }
      }
    }
  }
}
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

.blink {
  animation: twinkling 2.2s ease-in-out infinite;
}
.wordwarpNormal {
  .el-table {
    /deep/ .cell.el-tooltip {
      white-space: normal;
    }
  }
  
}
.ol-wordwarpNormal{
  /deep/.el-table .cell{
    word-break: keep-all;
  }
}
.el-table {
  // /deep/.el-table__body{
  //   height: 100% !important;
  // }
  /deep/.el-table__header {
    border-collapse: var(--borderCollasp);
    width: 100% !important;
    //-webkit-border-vertical-spacing: 0px;
    //-webkit-border-horizontal-spacing: 0px;
  }
  /deep/ .el-table__body-wrapper {
   // width: calc(100% - 5px) !important;
  }
  /deep/.el-table__body{
    .has-gutter {
      // line-height: var(--headerHeight) !important;
      th.is-leaf {
        box-sizing: border-box;
        border-right-width: var(--yBorderWidth);
        border-right-color: var(--yBorderColor);
        border-bottom-width: var(--xBorderWidth);
        border-bottom-color: var(--xBorderColor);
        border-bottom-style: var(--borderXStyle);
        border-right-style: var(--borderYStyle);
      }
    }
    .el-table__row {
      td {
        box-sizing: border-box;
        border-right-width: var(--yBorderWidth);
        border-right-color: var(--yBorderColor);
        border-bottom-width: var(--xBorderWidth);
        border-bottom-color: var(--xBorderColor);
        border-bottom-style: var(--borderXStyle);
        border-right-style: var(--borderYStyle);
        border-top: none;
        border-left: none;
        div {
          width: 100% !important;
        }
      }
    }
  }
 
  /deep/ .el-table__header-wrapper {
    display: flex;
    align-items: center;
    // height: var(--headerHeight) !important;
  }
  /deep/ .el-table__header tr,
  .el-table__header th {
    padding: 0;
    // height: var(--headerHeight) !important;
    .cell {
      // line-height: var(--headerLineHeight) !important;
    }
  } //表头
}
::v-deep .el-pager li {
  display: inline-block;
  width: var(--paginationWidth);
  height: var(--paginationHidth);
  text-align: center;
  line-height: var(--paginationHidth);
  padding: 0px !important;
  min-width:30px !important;
  font-weight:var(--paginationNormal);
  background-color: var(--paginationBcolor) !important;
  color: var(--paginationColor) !important;
  font-size: var(--paginationFontSize) !important;
  margin-right: var(--paginationMargin);
  border: var(--paginationBorder) solid var(--paginationBorderColor) !important;
  // transform: scale(var(--pagiTextScale));
}
::v-deep .el-pager li:hover {
  background-color: var(--paginationXbcolor) !important;
}
::v-deep .el-pager li.active {
  color: var(--paginationScolor) !important;
  cursor: pointer !important;
  border: var(--paginationBorder) solid var(--paginationSborderColor) !important;
}
.el-pagination {
  transform: scale(var(--pagiTextScale));
  display: flex;
  align-items: center;
}
::v-deep .el-pagination__total {
  color: var(--paginationColor) !important;
  font-size: var(--paginationFontSize) !important;
}
::v-deep .el-input__inner {
  color: var(--paginationColor) !important;
  font-size: var(--paginationFontSize) !important;
}
::v-deep .el-pagination button {
  width: var(--paginationWidth);
  height: var(--paginationHidth);
  text-align: center;
  line-height: var(--paginationHidth);
  padding: 0px !important;
  min-width:30px !important;
  font-weight:var(--paginationNormal);
  background-color: var(--paginationBcolor) !important;
  color: var(--paginationIconcolor) !important;;
  margin-right: var(--paginationMargin);
  border: var(--paginationBorder) solid var(--paginationBorderColor) !important;
}
::v-deep .el-pagination button:disabled {
  background-color: var(--paginationBcolor) !important;
  margin-right: var(--paginationMargin);
  color:var(--paginationIconcolor) !important;
  border: var(--paginationBorder) solid var(--paginationBorderColor) !important;
}
::v-deep .el-pagination button:hover {
  background-color: var(--paginationIconBcolor)!important;
}
::v-deep .pagination-box {
  justify-content: var(--paginationPosition) !important;
}
::v-deep .el-pagination__jump {
  color: var(--otherPgColor) !important;
  font-size: var(--paginationFontSize) !important;
}
::v-deep .el-table__header th .cell {
  font-weight: var(--headerFontWeight);
}
::v-deep .el-pagination .el-pagination__sizes .el-select .el-input input{
  background-color:var(--paginationBcolor) !important;
  border: var(--paginationBorder) solid var(--paginationBorderColor) !important;
  height: var(--xuanHeight) !important ;
}
::v-deep .el-select .el-input .el-select__caret{
  color:var(--paginationColor) !important;
}
::v-deep  .el-pagination .btn-next .el-icon, .el-pagination .btn-prev .el-icon{
  font-size: var(--paginationIconFiont) !important;
}
::v-deep .el-icon-arrow-left{
  font-size: var(--paginationIconFiont) !important;
}
/deep/.el-pagination .el-select .el-input{
  width: var(--xuanWidth) !important;
}
/deep/.el-pagination__sizes{
  height: auto !important ;
}
/deep/.el-pagination__editor.el-input{
  width: var(--xanWidth) !important;
  height: auto !important;
}
/deep/.el-pagination__editor.el-input .el-input__inner{
  background-color: var(--paginationBcolor) !important;
  height: var(--xanHeight) !important;;
}
/deep/.el-pagination__jump{
  height: auto !important;
}
</style>
