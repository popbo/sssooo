<template>
  <div class="middle">
    <div v-show="isMouseBox" class="mouse-box">
      <div
        class="content"
        id="mouse-content"
        :style="contentStyle"
        style="background-color: transparent"
      >
        <div
          class="container"
          :style="styleName"
          id="mouse-container"
          ref="mouseBox"
        ></div>
      </div>
    </div>
    <div id="wrapper" class="wrapper" @mousedown="contain.handleMouseDown">
      <div
        v-if="isShowContainer"
        class="content"
        id="content"
        ref="content"
        :style="contentStyle"
        @dragover="dragOver"
        @drop="dropToAddCom"
      >
        <!-- 弹框 -->
        <bulletBox
          v-if="bulletBoxFalge && $route.name === 'view' || bulletBoxFalge && $route.name === 'share' "
          :bulletBoxData="bulletBoxData"
          @closeBulletBox="closeBulletBox"
        ></bulletBox>
        <div
          class="container"
          :style="styleName"
          id="container"
          ref="container"
        >
          <AlignLine></AlignLine>
          <div
            class="check-mask"
            v-show="mask_positionList.is_show_mask"
            :style="
              'width:' +
              mask_width +
              'left:' +
              mask_left +
              'height:' +
              mask_height +
              'top:' +
              mask_top
            "
          ></div>
          <!-- 拖动组件的时候背景的网格线 -->
          <div
            class="grade"
            v-if="gradeFlag || contain.config.gradeShow"
            :style="gradeLenStyle"
          ></div>
          <!-- 拖动组件的时候背景的网格线 -->
          <subgroup
            ref="subgroup"
            :nav="contain.list"
            :upDateObj="upDateObj"
          ></subgroup>
        </div>
      </div>
      <div class="unable-see-box" v-if="isShowUnrelease">
        <div class="unable-see-content">
          <img src="@/assets/unable_see.png" alt="" />
          <p>您访问的可视化应用不存在或已被删除</p>
          <p>
            您访问的可视化应用不存在或已被删除，请确认访问的可视化应用是否存在
          </p>
        </div>
      </div>
      <div class="overtime-box" v-if="isShowOvertime">
        <div class="overtime-content">
          <img src="@/assets/overTime.png" alt="" />
          <p>您的预览时间结束</p>
          <p>如需继续预览，请在编辑器中重新点击预览</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import subgroup from './subgroup'
import { config as defaultConfig } from '@/option/config'
import { getObj, viewGetObj, shareGetObj, queryIsOverTime } from '@/api/visual'
import Vue from 'vue'
// import { uuid } from '@/utils/utils';
import { uuid } from '@/utils/utils.min.js'
// import crypto from '@/utils/crypto';
// import crypto from '@/utils/crypto.min.js'
import AlignLine from '@/components/alignLine/align-line'
import { EventBus } from '@/bus.js'
import baseList from '@/option/base'
import deepMerge from 'deepmerge'
import bulletBox from './bulletBox'
Vue.prototype.$website = window.$website
export default {
  name: 'contents',
  inject: ['contain'],
  props: {
    option: Object,
    props: {
      type: Object,
      default: () => {
        return {}
      },
    },
    wscale: Number,
  },
  provide() {
    return {
      contain: this.contain,
      container: this,
    }
  },
  components: {
    subgroup,
    AlignLine,
    bulletBox,
  },
  data() {
    return {
      bulletBoxFalge: false,
      bulletBoxData: '',
      baseList: baseList,
      isMouseBox: false,
      selcetNavData: [],
      selcetNav: [],
      contentStyle: {},
      selectCount: {
        x1: null,
        x2: null,
        y1: null,
        y2: null,
        widthMaxObj: null,
      },
      scale: 1,
      gradeFlag: false,
      isShowContainer: true,
      setIntervalId: '',
      mask_positionList: {
        is_show_mask: false,
        box_screen_left: 0,
        box_screen_top: 0,
        start_x: 0,
        start_y: 0,
        end_x: 0,
        end_y: 0,
      },
      upDateObj: {},
    }
  },
  computed: {
    stepScale() {
      let scale = Number(100 / (this.scale * this.wscale)).toFixed(2)
      return scale
    },
    //计算中央可视化大屏比例
    styleName() {
      this.setResize()
      const scale = this.contain.config.scale
      let widthVal = scale / 100 + 0.001
      // 在build情况下scale
      if(this.$route.name === 'build'){
        widthVal = 1 + 0.001
      }
      // const heightVal =
      //   document.body.clientHeight / this.contain.config.height - 0.002
      return Object.assign(
        {
          transform: `scale(${widthVal}, ${widthVal})`,
          width: this.setPx(this.contain.config.width),
          height: this.setPx(this.contain.config.height),
          backgroundColor: this.contain.config.backgroundColor,
          filter: this.setFilter(this.contain.config.filter),
        },
        (() => {
          if (this.contain.config.backgroundImage) {
            return {
              background: `url(${this.contain.config.backgroundImage}) 0% 0% / 100% 100% ${this.contain.config.backgroundColor}`,
            }
          }
          return { background: this.contain.config.backgroundColor }
        })(),
        (() => {
          if (this.contain.config.filter.show) {
            return {
              filter: this.setFilter(this.contain.config.filter),
            }
          } else {
            return {
              filter: 'unset',
            }
          }
          return
        })()
      )
    },
    gradeLenStyle() {
      return {
        backgroundSize: `${this.setPx(
          this.contain.config.gradeLen
        )} ${this.setPx(this.contain.config.gradeLen)},${this.setPx(
          this.contain.config.gradeLen
        )} ${this.setPx(this.contain.config.gradeLen)}`,
      }
    },
    isBuild() {
      return this.$route ? this.$route.name === 'build' : false
    },
    isShowUnrelease() {
      const isShare = this.$route.path.includes('share')
      return !this.isShowContainer && isShare
    },
    isShowOvertime() {
      const isView = this.$route.path.includes('view')
      return !this.isShowContainer && isView
    },
    mask_width() {
      return `${Math.abs(
        this.mask_positionList.end_x - this.mask_positionList.start_x
      )}px;`
    },
    mask_height() {
      return `${Math.abs(
        this.mask_positionList.end_y - this.mask_positionList.start_y
      )}px;`
    },
    mask_left() {
      return `${
        Math.min(this.mask_positionList.start_x, this.mask_positionList.end_x) -
        this.mask_positionList.box_screen_left
      }px;`
    },
    mask_top() {
      return `${
        Math.min(this.mask_positionList.start_y, this.mask_positionList.end_y) -
        this.mask_positionList.box_screen_top
      }px;`
    },
  },
  watch: {
    mask_positionList: {
      handler: function (val) {
        this.selcetNavData = []
        this.contain.active = []
        let leftMin = Math.min(val.start_x, val.end_x)
        let leftMax = Math.max(val.start_x, val.end_x)
        let topMin = Math.min(val.start_y, val.end_y)
        let topMax = Math.max(val.start_y, val.end_y)
        // console.log('val',val,)
        if (this.$route.name === 'build') {
          let selcetNav = []
          if (this.contain.nav.length > 0) {
            this.contain.nav.forEach(item => {
              this.getSelcetNav(item)
            })
            this.selcetNavData.forEach(item => {
              let Xb2 = leftMax
              let Xb1 = leftMin
              let Xa2 = item.left + item.component.width
              let Xa1 = item.left
              let Yb2 = topMax
              let Yb1 = topMin
              let Ya2 = item.top + item.component.height
              let Ya1 = item.top
              if (
                Math.abs(Xb2 + Xb1 - Xa2 - Xa1) <= Xa2 - Xa1 + Xb2 - Xb1 &&
                Math.abs(Yb2 + Yb1 - Ya2 - Ya1) <= Ya2 - Ya1 + Yb2 - Yb1
              ) {
                selcetNav.push(item.index)
              }
            })
            this.selcetNav = []
            selcetNav.forEach(a => {
              let check = this.selcetNav.every(function (b) {
                return a !== b
              })
              check ? this.selcetNav.push(a) : ''
            })
            this.contain.active = this.selcetNav
          }
        }
      },
      deep: true,
    },
  },
  created() {},
  mounted() {
    this.initData()
    this.initFun()
    EventBus.$on('tableColumnClickFormatter', parms => {
      this.bulletBoxFalge = true
      this.bulletBoxData = parms
    })
    // this.listen();
    this.$bus.$on('getNewVersion', versionData => {
      const verConfig = versionData.config
      const verContain = {
        config: JSON.parse(verConfig.detail) || {},
        component: JSON.parse(verConfig.component) || [],
      }
      const versionInfoToggle = {
        version: verConfig.version,
        versionRemark: verConfig.versionRemark,
        visualId: verConfig.visualId,
      }
      this.initsqlDataFn(verContain.component)
      this.contain.config = Object.assign({}, defaultConfig, verContain.config)
      this.contain.nav = verContain.component
      this.contain.visual = versionData.visual
      this.contain.versionInfo = versionInfoToggle
      this.contain.obj = versionData
      const width = this.isBuild
        ? this.contain.contentWidth
        : document.body.clientWidth
      this.setScale(width)
    })
    window.addEventListener(
      'message',
      e => {
        // 通过origin对消息进行过滤，避免遭到XSS攻击
        console.log('发布页面接收到iframe的消息了', e)
        if (e.srcElement.name === 'iframe') {
          let postMessageData = e.data // 从iframe传过来的数据
          let obj = this.makeUpDateObj(postMessageData)
          console.log('obj=====', obj)
          EventBus.$emit('paramsAccept', obj)
        }
      },
      false
    )
  },
  methods: {
    initFun() {
      ;['handleRefresh', 'getDragObj'].forEach(ele => {
        this[ele] = this.$refs.subgroup[ele]
      })
    },
    closeBulletBox() {
      this.bulletBoxFalge = false
    },
    getSelcetNav(item) {
      if (item.children) {
        item.children.forEach(data => {
          this.getSelcetNav(data)
        })
      } else {
        this.selcetNavData.push(item)
      }
    },
    getBaseList(value) {
      let option = {}
      for (let i = 0; i < this.baseList.length; i++) {
        if (this.baseList[i].children) {
          for (let b = 0; b < this.baseList[i].children.length; b++) {
            if (this.baseList[i].children[b].option) {
              if (this.baseList[i].children[b].option.component) {
                if (
                  this.baseList[i].children[b].option.component.prop === value
                ) {
                  option = this.baseList[i].children[b].option || {}
                }
              }
            }
          }
        }
      }
      return option
    },
    getObjList(obj,newObj) {
      if (Object.prototype.toString.call(obj) === '[object Object]') {
        for (let key in obj) {
          if (obj.hasOwnProperty(key)) {
            if (Object.prototype.toString.call(obj[key]) === '[object Array]') {
              if(newObj[key]){
                obj[key] = []
              }
            }
            this.getObjList(obj[key])
          }
        }
      }
    },
    // 深层拷贝
    getSumComponent(list) {
      let sumOption = {}
      let sumOptionTwo = {}
      try {
        list.forEach(item => {
          if (item.component) {
            if (item.component.prop) {
              if (this.getBaseList(item.component.prop).option) {
                sumOption = this.getBaseList(item.component.prop).option || {}
                sumOptionTwo = this.deepClone(sumOption)
                this.getObjList(sumOptionTwo,item.option)
              }
              if (item.option) {
                item.option = deepMerge(sumOptionTwo, item.option)
              } else {
                item.option = this.deepClone(sumOption)
              }
            }
          }
          if (item.children) {
            if (item.children.length > 0) {
              this.getSumComponent(item.children)
            }
          }
          if(item.list){
            if (item.list.length > 0) {
              this.getSumComponent(item.list)
            }
          }
        })
      } catch (error) {
        console.log(error)
      }
    },
    //初始化数据
    initData() {
      const id = this.$route ? this.$route.params.id : this.props.id
      // 这个就是再给build.vue组件添加属性this.contain就是指的build.vue这个组件实例
      this.contain.id = id
      this.contain.contentWidth = this.$refs.content.offsetWidth // 这个宽度是继承自父元素的宽度，在div未设置宽度的情况下继承父元素的宽度，详情见build.vue的canvasStyle的计算属性
      const width = this.isBuild
        ? this.contain.contentWidth
        : document.body.clientWidth
      let config
      // console.log(this.isBuild,document.body.clientWidth,99999)
      const callback = () => {
        //赋值属性
        let mark = this.contain.config.mark
        if (mark.show && !this.isBuild) {
          this.watermark(
            Object.assign(mark, {
              fontSize: mark.fontSize + 'px',
            })
          )
        }
        this.calcData()
        this.setScale(width)
        // 如果是在预览或者发布页面那么要看下地址后面是否携带了查询参数
        if (
          this.$route.meta.isNeedCheckParams &&
          JSON.stringify(this.$route.query) !== '{}'
        ) {
          this.upDateObj = this.makeUpDateObj(this.$route.query)
        }
      }
      if (id) {
        // const loading = this.$loading({
        //   lock: true,
        //   text: '正在加载中，请稍后',
        //   spinner: 'loading',
        //   background: 'rgba(0, 0, 0, 0.7)',
        // });
        this.getMethod(id)
          .then(res => {
            if (res.data.success) {
              const data = res.data.data
              // console.log('data',JSON.parse(data.config.component))
              // base.js和返回的值进行合并
              if (data) {
                let sumComponent = JSON.parse(data.config.component) || []
                this.getSumComponent(sumComponent)
                data.config.component = JSON.stringify(sumComponent)
              }
              // console.log('data.list',JSON.parse(data.config.component))
              this.$bus.$emit('detailData', data)
              this.contain.obj = data
              config = data.config
              const contain = {
                config: JSON.parse(config.detail) || {},
                component: JSON.parse(config.component) || [],
              }
              const versionInfo = {
                version: config.version,
                versionRemark: config.versionRemark,
                visualId: config.visualId,
              }
              this.initsqlDataFn(contain.component)
              this.contain.config = Object.assign(
                {},
                defaultConfig,
                contain.config
              )
              // 处理以前没有存储multiparameterList
              if (!this.contain.config.multiparameterList) {
                if (this.contain.config.pageParamsList) {
                  let multiparameterList = this.deepClone(
                    this.contain.config.pageParamsList
                  )
                  if (multiparameterList.length > 0) {
                    multiparameterList.forEach(item => {
                      item.paramsValue = ''
                    })
                  }
                  this.contain.config = Object.assign(this.contain.config, {
                    multiparameterList: multiparameterList,
                  })
                }
              }
              this.contain.nav = contain.component
              this.contain.visual = data.visual
              this.contain.versionInfo = versionInfo
              //添加水印。只有查看页面生效
              if (this.$route.meta && this.$route.meta.isNeedCheckPsw) {
                let password = ''
                if (this.$route.meta.pswStr === 'psw') {
                  password =
                    this.contain.visual.release[this.$route.meta.pswStr]
                } else {
                  password = this.contain.visual[this.$route.meta.pswStr]
                }
                if (!this.validatenull(password)) {
                  const regPasseord = '^' + password + '$'
                  this.$prompt('请输入密码', '提示', {
                    confirmButtonText: '确定',
                    showCancelButton: false,
                    showClose: false,
                    closeOnClickModal: false,
                    closeOnPressEscape: false,
                    inputPattern: new RegExp(regPasseord),
                    inputErrorMessage: '密码不正确，请重新输入',
                  }).then(() => {
                    callback()
                  })
                } else {
                  callback()
                }
              } else {
                callback()
              }
              this.isShowContainer = true
              if (this.$route.path.includes('view')) {
                this.setIntervalId = setInterval(async () => {
                  const isOverTime = await this.queryIsOverTime(id)
                  if (isOverTime) {
                    this.isShowContainer = false
                    clearInterval(this.setIntervalId)
                  }
                }, 60 * 1000)
              }
              // loading.close();
            } else {
              this.isShowContainer = false
            }
          })
          .catch(err => {
            // 如果发生预览超时也会走这里
            console.log(err)
            // loading.close();
            this.isShowContainer = false
          })
      } else if (this.option) {
        config = this.option
        this.contain.config = config.detail || {}
        this.contain.nav = config.component || []
        callback()
      } else {
        this.setScale(width)
      }
    },
    async getMethod(id) {
      if (this.$route.path.includes('view')) {
        // 先进行从预览页面是否超时预览
        // 如果超时的话就return 一个拒绝状态的promise
        // 如果没有超时就执行获取大屏信息的接口
        const isOverTime = await this.queryIsOverTime(id)
        console.log('isOverTime==>', isOverTime)
        return isOverTime ? Promise.reject() : viewGetObj(id)
      } else if (this.$route.path.includes('share')) {
        return shareGetObj(id)
      } else {
        return getObj(id)
      }
    },
    //计算比例
    setScale(width) {
      this.contain.config.scale = (width / this.contain.config.width) * 100
      this.scale = this.contain.config.scale
      this.setResize()
    },
    //适配尺寸
    setResize() {
      if(this.$route.name === 'view' || this.$route.name === 'share'){
        this.contentStyle = {
          width: this.setPx(
            (this.contain.config.scale * this.contain.config.width) / 100
          ),
          height: this.setPx(
            (this.contain.config.scale * this.contain.config.height) / 100
          ),
        }
      }
      if(this.$route.name === 'build'){
        this.contentStyle = {
          width: this.setPx(
             this.contain.config.width
          ),
          height: this.setPx(
            this.contain.config.height
          ),
        }
      }
    },
    calcData() {
      if (!this.contain.config.mark) this.contain.config.mark = {}
      if (!this.contain.config.query) this.contain.config.query = {}
    },
    handlePostionSelect(postion) {
      this.handleCalcPostionSelect()
      const { x1, x2, y1, y2, widthMaxObj } = this.selectCount
      if (postion === 'left') {
        this.handleMoveSelectList(x1, undefined, true, postion)
      } else if (postion === 'center') {
        this.handleMoveSelectList(
          widthMaxObj.left + widthMaxObj.width / 2,
          undefined,
          true,
          postion
        )
      } else if (postion === 'right') {
        this.handleMoveSelectList(x2, undefined, true, postion)
      } else if (postion === 'top') {
        this.handleMoveSelectList(undefined, y1, true, postion)
      } else if (postion === 'middle') {
        this.handleMoveSelectList(undefined, y1 + (y2 - y1) / 2, true, postion)
      } else if (postion === 'bottom') {
        this.handleMoveSelectList(undefined, y2, true, postion)
      }
    },
    handleMoveSelectList(left, top, type, postion) {
      const activeCopy = this.deepClone(this.contain.active)
      this.contain.active = []
      activeCopy.forEach(ele => {
        ele = this.contain.findlist(ele)
        const ele_component = ele.component
        //水平情况
        if (left) {
          let baseLeft = Number(type ? left : (ele.left + left).toFixed(2))
          if (postion === 'right') {
            baseLeft = baseLeft - ele_component.width
          } else if (postion === 'center') {
            baseLeft = left - ele_component.width / 2
          }
          this.$set(ele, 'left', baseLeft)
        }
        //垂直情况
        if (top) {
          let baseTop = Number(type ? top : (ele.top + top).toFixed(2))
          if (postion === 'bottom') {
            baseTop = baseTop - ele_component.height
          } else if (postion === 'middle') {
            baseTop = top - ele_component.height / 2
          }
          this.$set(ele, 'top', baseTop)
        }
      })
      // this.contain.active = activeCopy
    },
    //计算多选状态下的最大边界值
    handleCalcPostionSelect() {
      // 以画布的左顶点作为XY坐标系的原点，水平方向向右为X的正方向，垂直方向向下为Y的正方向
      // x1表示X轴上最小的点，x2表示X轴上最大的点，y1表示Y轴上最小的点，y2表示Y轴上最大的点
      // widthMaxObj记录多选的组件宽度最大的组件
      // 假设多选的数组中的第一个组件的四个点的x,y值映射到XY轴即为X轴上最小的点，X轴上最大的点，Y轴上最小的点，Y轴上最大的点
      const firstComponent = this.contain.findlist(this.contain.active[0])
      const { left, top, width, height } = this.getSizeFun(firstComponent)
      this.selectCount = {
        x1: left,
        x2: left + width,
        y1: top,
        y2: top + height,
        widthMaxObj: {
          left,
          top,
          width,
          height,
        },
      }
      for (let i = 1; i < this.contain.active.length; i++) {
        const selectComponnet = this.contain.findlist(this.contain.active[i])
        const sizeObj = this.getSizeFun(selectComponnet)
        const { left, top, width, height } = sizeObj
        if (this.selectCount.x1 > left) this.selectCount.x1 = left
        if (this.selectCount.x2 < left + width)
          this.selectCount.x2 = left + width
        if (this.selectCount.y1 > top) this.selectCount.y1 = top
        if (this.selectCount.y2 < top + height)
          this.selectCount.y2 = top + height
        if (this.selectCount.widthMaxObj.width < width) {
          Object.assign(this.selectCount.widthMaxObj, sizeObj)
        } else if (
          this.selectCount.widthMaxObj.width === width &&
          this.selectCount.widthMaxObj.top > top
        ) {
          // 如果宽度相同，取高度最高者
          Object.assign(this.selectCount.widthMaxObj, sizeObj)
        }
      }
    },
    getSizeFun(obj) {
      return {
        left: obj.left,
        top: obj.top,
        width: obj.component.width,
        height: obj.component.height,
      }
    },
    dragOver(ev) {
      ev.preventDefault()
      ev.stopPropagation()
      ev.dataTransfer.dropEffect = 'copy'
    },
    dropToAddCom(event) {
      event.preventDefault()
      try {
        const objString = event.dataTransfer.getData('text')
        const obj = JSON.parse(objString)
        //如果是收藏的拖拽，拖拽出来是成组的
        if (obj.type && obj.type === 'collect') {
          const collectItem = {
            isname: false,
            menu: false,
            name: obj.name,
            index: uuid(),
          }
          obj.dragDataTempArr.forEach(x => {
            x.index = uuid()
          })
          collectItem.children = obj.dragDataTempArr
          this.contain.nav.unshift(collectItem)
        } else {
          obj.left = event.offsetX - (obj.component.width / 2).toFixed()
          obj.top = event.offsetY - (obj.component.height / 2).toFixed()
          // 为了对齐线添加两个属性实时top 和 实时  left
          obj.intimeTop = event.offsetX - (obj.component.width / 2).toFixed()
          obj.intimeLeft = event.offsetY - (obj.component.height / 2).toFixed()
          obj.index = uuid()
          // 全局配置
          if(obj.option){
            if(obj.option.barColor){
              if(this.contain.config.overallSituationColor){
                if(this.contain.config.overallSituationColor.length>0){
                  obj.option.barColor = this.deepClone(this.contain.config.overallSituationColor)
                }
              }
            }
            // 象形图时
            if(obj.option.pictorialBarColor){
              if(this.contain.config.overallSituationColor){
                if(this.contain.config.overallSituationColor.length>0){
                  obj.option.pictorialBarColor = this.deepClone(this.contain.config.overallSituationColor)
                }
              }
            }
          }
          this.contain.nav.unshift(obj)
        }
      } catch {}
    },
    // 初始化时清除sqlData下的数据
    initsqlDataFn(component = []) {
      // console.log("component==>",component)
      for (var i = 0; i < component.length; i++) {
        let sqlData = component[i].sqlData
        if (sqlData && sqlData.linkCustomFilterS) {
          sqlData.linkCustomFilterS = {}
        }
        // if (sqlData&&sqlData['2ecbaad3-0080-487d-b345-d83d290cb048']) {
        //   delete sqlData['2ecbaad3-0080-487d-b345-d83d290cb048']
        // }
      }
    },
    setFilter(cssFilter) {
      return `hue-rotate(${cssFilter.hueRotate}deg) saturate(${
        100 + cssFilter.saturate
      }%) brightness(${100 + cssFilter.brightness}%) contrast(${
        100 + cssFilter.contrast
      }%) opacity(${cssFilter.opacity}%) grayscale(${cssFilter.grayscale}%)`
    },
    /**
     * @param time  number  如果要定时五秒，那么就输入5
     */
    async queryIsOverTime(id) {
      try {
        const res = await queryIsOverTime(id)
        if (res.data.success) {
          return false
        } else {
          return true
        }
      } catch (error) {
        console.log(error)
      }
    },
    //鼠标按下事件
    handleMouseDown(event) {
      if (this.$route.name === 'view') {
        return false
      }
      event.stopPropagation()
      this.isMouseBox = true
      this.mask_positionList.is_show_mask = true
      console.log('event==>', event)
      this.mask_positionList.start_x = event.offsetX
      this.mask_positionList.start_y = event.offsetY
      this.mask_positionList.end_x = event.offsetX
      this.mask_positionList.end_y = event.offsetY
      this.$refs.mouseBox.addEventListener('mousemove', this.handleMouseMove) //监听鼠标移动事件
      this.$refs.mouseBox.addEventListener('mouseup', this.handleMouseUp) //监听鼠标抬起事件
    },
    handleMouseMove(event) {
      event.stopPropagation()
      this.mask_positionList.end_x = event.offsetX
      this.mask_positionList.end_y = event.offsetY
      // console.log('mousemoveEvent==>', event)
    },
    handleMouseUp() {
      // event.stopPropagation()
      this.$refs.mouseBox.removeEventListener('mousemove', this.handleMouseMove)
      this.$refs.mouseBox.removeEventListener('mouseup', this.handleMouseUp)
      this.mask_positionList.is_show_mask = false
      this.isMouseBox = false
      // handleDomSelect();
      // resSetXY();
    },
    resSetXY() {
      this.mask_positionList.start_x = 0
      this.mask_positionList.start_y = 0
      this.mask_positionList.end_x = 0
      this.mask_positionList.end_y = 0
    },
    getChildData(children,postData){
      let parms = {}
      if(children.length>0){
        children.forEach(item=>{
          if(item.paramName){
            if(postData){
              if(postData[item.keyName]!==undefined){
                parms[item.paramName]= postData[item.keyName] || ''
              }
            }
          }
        })
      }
      return parms
    },
    getSqlChildData(children,postData){
      let parms = []
      if(children.length>0){
        children.forEach(item=>{
          if(item.paramsType){
            if(postData){
              if(postData[item.paramsType]!==undefined){
                parms.push({
                  value: postData[item.paramsType] || '',
                  fieldId: item.fieldId,
                  term: postData.termParams ? postData.termParams : 'eq',
                })
              }
            }
          }
        })
      }
      return parms
    },
    // 分发参数接受事件
    makeUpDateObj(postData) {
      let { eventList } = this.contain.config
      let upDateObj = {}
      if (!eventList) {
        return upDateObj
      }
      let freshEventList = eventList.filter(item=>{
        return item.eventAction === 1
      });
      let child = []
      let sqlChild = [];
      let staticChild = [];
      freshEventList.forEach(item=>{
        child = [...child,...item.child]
        sqlChild = [...sqlChild,...item.sqlChild]
        staticChild = [...staticChild,...item.staticChild]
      })
      if(child.length>0){
          let testOld = []; 
          let childData = []
          let newObj  = {}
          child.forEach(el => {
            let oldObj = { index: el.index, children: [] }
            let txtObj = { keyName: el.keyName, paramName: el.paramName };
            oldObj.children.push(txtObj);
            testOld.push(oldObj);
          });
          testOld.forEach((c,i)=>{
            if(!newObj[c.index]){
              childData.push(c)
              newObj[c.index] = true
            }else{
              childData.forEach(el=>{
                if(el.index === testOld[i].index){
                  el.children = [...el.children, ...testOld[i].children]
                }
              })
            }
          })
          childData.forEach(item=>{
            upDateObj[item.index] = {
              p: this.getChildData(item.children,postData)
            }
          })
      }
      if(sqlChild.length>0){
          let testOld = []; 
          let sqlChildData = []
          let newObj  = {}
          sqlChild.forEach(el => {
            let oldObj = { index: el.index, children: [] }
            let txtObj = { fieldId: el.fieldId, fieldType: el.fieldType, paramsType: el.paramsType, sqlId: el.sqlId };
            oldObj.children.push(txtObj);
            testOld.push(oldObj);
          });
          testOld.forEach((c,i)=>{
            if(!newObj[c.index]){
              sqlChildData.push(c)
              newObj[c.index] = true
            }else{
              sqlChildData.forEach(el=>{
                if(el.index === testOld[i].index){
                  el.children = [...el.children, ...testOld[i].children]
                }
              })
            }
          })
          console.log(testOld,555)
          sqlChildData.forEach(item=>{
            upDateObj[item.index] = {
              p: this.getSqlChildData(item.children,postData)
            }
          })
      }
      if(staticChild.length){
        staticChild.forEach(s => {
          upDateObj[s.index] = {
              p: postData,
          } 
        })
      }
      return upDateObj
    },
  },
}
</script>

<style lang="scss">
/* .tipBox{
  height: 40px;
  position: absolute;
  bottom: -40px;
  right: 0;
  color: #bcc9d4;
} */
/* .el-loading-spinner .loading{
    display: inline-block;
    width: 100px;
    height: 50px;
    background: url('~@/assets/loading.gif') no-repeat;
  }
  .el-loading-spinner .el-loading-text{
    font-size: 18px;
    color:#fff;
    font-family: "SourceHanSansCN-Regular";
  } */
.mouse-box {
  position: absolute;
  // left: 0;
  // top: 0;
  box-sizing: border-box;
  width: 100%;
  z-index: 100000;
  .container {
    background: none !important;
  }
}
.unable-see-box {
  height: 100vh;
  position: relative;
  .unable-see-content {
    position: absolute;
    top: 50%;
    left: 50%;
    translate: -50% -50%;
    img {
      display: block;
      margin: 0 auto;
    }
    p {
      &:nth-child(2) {
        margin-top: 60px;
        text-align: center;
        color: #333;
        font-size: 20px;
        font-weight: bold;
      }
      &:nth-child(3) {
        margin-top: 20px;
        text-align: center;
        color: rgba(51, 51, 51, 0.8);
        font-size: 16px;
      }
    }
  }
}
.overtime-box {
  height: 100vh;
  position: relative;
  .overtime-content {
    position: absolute;
    top: 50%;
    left: 50%;
    translate: -50% -50%;
    img {
      display: block;
      margin: 0 auto;
    }
    p {
      &:nth-child(2) {
        margin-top: 60px;
        text-align: center;
        color: #333;
        font-size: 20px;
        font-weight: bold;
      }
      &:nth-child(3) {
        margin-top: 20px;
        text-align: center;
        color: rgba(51, 51, 51, 0.8);
        font-size: 16px;
      }
    }
  }
}
.check-mask {
  position: absolute;
  background: #409eff;
  opacity: 0.4;
  z-index: 99999;
}
</style>
