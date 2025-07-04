import { setPx, getUrlParams, validatenull } from './util'
import config from './config'
import baseConfig from '@/config'
// import crypto from '@/utils/crypto';
import crypto from '@/utils/crypto.min.js'
// import { funEval, uuid } from '@/utils/utils';
import { uuid, getFunction } from '@/utils/utils.min.js'
import COMMON from '@/config'
import { sqlSearch } from '@/api/sqlSearch'
// import token from './token'
import { EventBus } from '@/bus.js'
import { deviceIdList } from './deviceIdList.js'
import findTree from "xe-utils/findTree";
import { getEventAdd} from '@/api/visual'
import isPlainObject from "xe-utils/isPlainObject";
import isArray from "xe-utils/isArray";
export default (() => {
  return {
    inject: ['contain'],
    props: {
      stylesFormatter: Function,
      dataFormatter: Function,
      titleFormatter: Function,
      labelFormatter: Function,
      clickFormatter: Function,
      dblClickFormatter: Function,
      sqlFormatter: Function,
      formatter: Function,
      echartFormatter: Function,
      dataQuery: Function,
      dataHeader: Function,
      websocketHeader: Function,
      websocketQuery: Function,
      width: {
        type: [Number, String],
        default: 600,
      },
      height: {
        type: [Number, String],
        default: 600,
      },
      theme: {
        type: String,
      },
      child: {
        type: Object,
        default: () => {
          return {}
        },
      },
      sqlChild: {
        type: Object,
        default: () => {
          return {
            filterObj: {},
          }
        },
      },
      sql: {
        type: String,
      },
      // 接受传递过来的sqlData数据
      sqlData: {
        type: Object,
        default: () => {
          return {}
        },
      },
      // 接受传递过来的sqlData数据
      drawConditionList: {
        type: Array,
        default: () => {
          return []
        },
      },
      time: {
        type: Number,
        default: 0,
      },
      url: {
        type: String,
      },
      webSocketUrl: {
        type: String,
      },
      authenticationMethod: {
        type: String,
      },
      apiAuthenticationForm: {
        type: Object,
        default: () => {
          return {}
        },
      },
      apiAuthenticationMethod: {
        type: String,
      },
      authenticationForm: {
        type: Object,
        default: () => {
          return {}
        },
      },
      disabled: {
        type: Boolean,
        default: true,
      },
      dataType: {
        type: Number,
        default: 0,
      },
      dataMethod: {
        type: String,
        default: 'get',
      },
      id: {
        type: String,
        default: 'main_' + uuid(),
      },
      data: {
        type: [Object, String, Array],
      },
      component: {
        type: Object,
        default: () => {
          return {}
        },
      },
      option: {
        type: Object,
        default: () => {
          return {}
        },
      },
      eventList: {
        type: Array,
        default: () => {
          return []
        },
      },
      // index: { // 组件的id
      //   type: String,
      // },
      dataPosition: {
        type: Object,
        default: () => {
          return {}
        },
      },
      upDateObj: {
        type: Object,
        default: () => {
          return {}
        },
      },
    },

    data() {
      return {
        navWholeList:[],
        subgrouprefList:{},
        itemRefsresult:{},
        combined:{},
        dynamicQuery: {},
        propQuery: {},
        headerHeight: '',
        checkChart: '',
        myChart: '',
        dataChart: [],
        dataAxios: {},
        dataUrl: '',
        dataWebSocketUrl: '',
        key: false,
        isChart: false,
        isHighChart: false,
        styles: {},
        appendCheck: {},
        appendObj: {},
        appendList: [],
        // lockReconnect: false,
        websocket: null,
        // timeout: 120000,
        // timeoutObj: null,
        // serverTimeoutObj: null,
      }
    },
    watch: {
      dataAppend(val) {
        console.log('dataAppend')
        this.appendObj = {}
        this.appendList = []
        if (!val) {
          this.appendCheck = clearInterval(this.appendCheck)
        } else {
          this.dataChart = []
        }
        this.updateData()
      },
      echartFormatter() {
        this.updateChart && this.updateChart()
      },
      url: {
        handler(val) {
          this.dataUrl = val || ''
        },
        deep: true,
        immediate: true,
      },
      webSocketUrl: {
        handler(val) {
          this.dataWebSocketUrl = val || ''
        },
        deep: true,
        immediate: true,
      },
      data: {
        handler() {
          console.log('这里触发了2')
          this.updateData()
        },
        deep: true,
      },
      width() {
        // console.log('width==>', width)
        this.updateData()
      },
      height() {
        this.updateData()
      },
      theme() {
        // 这三句一句都不能少
        this.myChart.dispose()
        this.init()
        this.updateData()
      },
      option: {
        handler(newVal, oldVal) {
          // console.log('option-new', newVal)
          // console.log('option-old', oldVal)
          if (this.myChart && this.isChart) {
            this.updateData()
          }
          if (this.isHighChart) {
            this.updateData()
          }
        },
        deep: true,
        immediate: true,
      },
      // dataSource: {
      //   handler(newVal, oldVal) {
      //     // console.log(this)
      //     this.updateData()
      //   }
      // },
      valueFromDataSource: {
        handler(newVal, oldVal) {
          if (newVal !== oldVal) {
            this.updateData()
          }
        },
      },
    },
    computed: {
      count() {
        return this.option.count
      },
      dataAppend() {
        return this.option.dataAppend
      },
      dataChartLen() {
        return (this.dataChart || []).length
      },
      switchTheme() {
        return this.vaildData(this.option.switchTheme, false)
      },
      name() {
        const result = this.$el.className.replace(config.name, '')
        return result
      },
      minWidth() {
        const val = this.option.minWidth
        if (val > this.width) return val
      },
      isApi() {
        return this.dataType === 1
      },
      isSql() {
        return this.dataType === 2
      },
      // 文字组件新增websocket
      isWebSocket() {
        return this.dataType === 3
      },
      // 文字和图片新增数据源组件数据来源
      isDataFromDataStorage() {
        return this.dataType == 4
      },
      style() {
        return this.component.style || {}
      },
      styleChartName() {
        const obj = {
          width: setPx(this.minWidth || this.width),
          height: setPx(this.height),
        }
        return obj
      },
      styleSizeName() {
        return Object.assign(
          {
            width: setPx(this.width),
            height: setPx(this.height),
          },
          (() => {
            if (this.minWidth) {
              return {
                overflowX: 'auto',
                overflowY: 'hidden',
              }
            }
            return {}
          })(),
          this.styles
        )
      },
      dataSource() {
        if (this.dataPosition.dataStorageId) {
          return this.$store.state.dataSourceList.find(
            item => item.id === this.dataPosition.dataStorageId
          )
        }
      },
      // 写一个计算属性去计算dataSource给组件的值
      valueFromDataSource() {
        if (this.dataSource) {
          if (this.dataSource.treeData) {
            let parms = findTree(this.dataSource.treeData,(item)=> item.id===this.dataPosition.id)
            if(parms){
              if(parms.item){
                if(parms.item.value){
                  return parms.item.value
                }
                return parms.item.label || ''
              }
            }
            return ''
          } else {
            return this.dataSource.data[this.dataPosition.row][
              this.dataPosition.column
            ]
          }
        }
      },
    },
    created() {
    },
    mounted() {
      window.addEventListener('message',this.getIframeData,false);
      this.init()
      // console.log('upDateObj-->', this.upDateObj)
      if (
        this.$route.meta.isNeedCheckParams &&
        JSON.stringify(this.$route.query) !== '{}'
      ) {
        if (this.component.name === 'newTree') {
          this.flage = false
          let id = this.id.replace('list', '')
          if (this.upDateObj[id]) {
            let param = this.upDateObj[id].p
            this.updateData().then(() => {
              this.getSelet(param.deviceId)
            })
          } else {
            this.updateData()
          }
        } else {
          let id = this.id.replace('list', '')
          this.upDateObj[id]
            ? this.updateData(this.upDateObj[id].p)
            : this.updateData()
        }
      } else {
        this.updateData()
      }
      EventBus.$on('paramsAccept', obj => {
        if (this.component.name === 'newTree') {
          this.flage = false
          let id = this.id.replace('list', '')
          let param = obj[id].p
          this.updateData().then(() => {
            this.getSelet(param.deviceId)
          })
        } else {
          console.log('组件接收到下发的消息了', obj)
          let id = this.id.replace('list', '')
          obj[id] && this.updateData(obj[id].p)
        }
      })
      // 只有在预览时执行
      if (this.$route.name === 'view' || this.$route.name === 'share') {
        if (this.component.name === 'text') {
          this.updateClick({ value: this.data.value }, 'initFormatter')
        } else if (this.component.name === 'atepicker') {
          let myDate = new Date()
          let year = myDate.getFullYear() //获取完整的年份(4位,1970-????)
          let month = myDate.getMonth() + 1 //获取当前月份(0-11,0代表1月)
          if (month < 10) {
            month = `0${month}`
          }
          let date = myDate.getDate() //获取当前日(1-31)
          if (date < 10) {
            date = `0${date}`
          }
          this.updateClick(
            { name: `${year}-${month}-${date}`, termParams: 'eq' },
            'initFormatter'
          )
        } else {
          this.updateClick({ value: '' }, 'initFormatter')
        }
      }
      //事件开放接口
      EventBus.$on('openInterfaceWebsocket', openInterface => {
        this.updateClick('', '', openInterface)
      })
    },
    methods: {
     isJSON(str) {
        try {
          JSON.parse(str);
          return true;
        } catch (e) {
          return false;
        }
      },
      getIframeData(e){
        if(e.data){
          let parms = e.data
          if(this.isJSON(e.data)){
            parms = JSON.parse(e.data)
            this.customVueClick(e.data)
          }
        }
      },
      getEventAdd(data){
        getEventAdd(data).then(res=>{
          if(res.data){
            if(res.data.code===200){
              console.log('添加日志成功')
            }
          }
        })
      },
      init() {
        const main = this.$refs[this.id]
        if (main) {
          // 判断是否图表去初始化
          this.isChart = config.echart.includes(this.name)
          this.isHighChart = config.highChart.includes(this.name)
          if (this.isChart) this.myChart = window.echarts.init(main, this.theme)
          if (this.name == 'datav') {
            this.isChart = true
          }
        }
      },
      getItemRefs() {
        let parent = this.$parent
        this.getSubgroup(parent)
        let refList = this.subgrouprefList.$refs
        this.getItemRefsresult(refList)
        // console.log('result',this.itemRefsresult)
        return this.itemRefsresult
      },
      getItemRefsresult(refList) {
        Object.keys(refList).forEach(ele => {
            if (ele.indexOf(COMMON.NAME) !== -1) {
              if(refList[ele][0]){
                this.itemRefsresult[ele.replace(COMMON.NAME, '')] = refList[ele][0]
              }
              if(refList[ele][0]){
                if(refList[ele][0].$refs){
                  let refList_child = refList[ele][0].$refs
                  Object.keys(refList_child).forEach(ele_s => {
                    if (ele_s.indexOf(COMMON.NAME) !== -1) {
                      if(refList_child[ele_s][0]){
                        this.itemRefsresult[ele_s.replace(COMMON.NAME, '')] = refList_child[ele_s][0]
                      }
                    }
                    if(refList_child[ele_s][0]){
                      if(refList_child[ele_s][0].$refs){
                        if(Object.keys(refList_child[ele_s][0].$refs).length>0){
                          this.getItemRefsresult(refList_child[ele_s][0].$refs)
                        }
                      }
                    }
                  })
                }
              }
            }
        })
      },
      getSubgroup(parent) {
        if(parent.$refs){
          if(parent.$refs.subgroup!==undefined){
            this.subgrouprefList = parent.$refs.subgroup
          }else{
            parent = parent.$parent
            this.getSubgroup(parent)
          }
        }
      },
      getNavParent(parent){
        if(parent.nav){
          this.navWholeList = parent.nav
        }else{
          let nweParent = parent.$parent
          this.getNavParent(nweParent)
        }
      },
      customVueClick(params){
        let parent = this.$parent
        this.getNavParent(parent)
        let sumList = [];
        let parentList = []
        let nav = this.navWholeList
        const detailList = item =>{
          if(item.list){
            item.list.forEach(ele => {
              sumList.push(ele)
              detailList(ele)
            })
          }
        }
        if(nav){
          for(let i = 0; i < nav.length; i++){
            sumList.push(nav[i])
            detailList(nav[i])
          }
        }
        parentList = sumList
        if(params){
          if(isPlainObject(params)){
            if(params.id){
              if(params.eventAction===1){
                let refList = this.getItemRefs() 
                Object.keys(refList).forEach(ele => {
                  if (params.id === ele) {
                    if(params.p){
                      refList[ele].updateData(params.p)
                    }else{
                      refList[ele].updateData()
                    }
                  }
                })
              }
              if(params.eventAction===2){
                for (let j = 0; j < parentList.length; j++) {
                  if (params.id === parentList[j].index) {
                    // 如果没有在配置页面触发该组件的隐藏按钮，那么display属性就不是一个响应式的属性，所以要先把他变为响应式
                    try{
                      if(parentList[j].component.prop==='tips'){
                        this.$setSessionItem('imgBorderTypeChange',false)
                      }
                    }catch(err){
                      console.log(err)
                    }
                    setTimeout(() => {
                      this.$set(parentList[j], 'display', '')
                      parentList[j].display = true
                    }, 100)
                  }
                }
              }
              if(params.eventAction===3){
                for (let j = 0; j < parentList.length; j++) {
                  if (params.id === parentList[j].index) {
                    setTimeout(() => {
                      try{
                        if(parentList[j].component.prop==='tips'){
                          if(parentList[j].option.showPosition == 2){
                            EventBus.$emit('tipsTransformPosition')
                          }
                        }
                      }catch(err){
                        console.log(err)
                      }
                      console.log('parentList[j]==>', parentList[j])
                      parentList[j].display = false
                    }, 100)
                  }
                }
              }
            }
          }
          if(isArray(params)){
            let refList = this.getItemRefs() 
            params.forEach(item=>{
              if(item.id){
                if(item.eventAction===1){
                  Object.keys(refList).forEach(ele => {
                    if (item.id === ele) {
                      if(item.p){
                        refList[ele].updateData(item.p)
                      }else{
                        refList[ele].updateData()
                      }
                    }
                  })
                }
                if(item.eventAction===2){
                  for (let j = 0; j < parentList.length; j++) {
                    if (item.id === parentList[j].index) {
                      // 如果没有在配置页面触发该组件的隐藏按钮，那么display属性就不是一个响应式的属性，所以要先把他变为响应式
                      try{
                        if(parentList[j].component.prop==='tips'){
                          this.$setSessionItem('imgBorderTypeChange',false)
                        }
                      }catch(err){
                        console.log(err)
                      }
                      setTimeout(() => {
                        this.$set(parentList[j], 'display', '')
                        parentList[j].display = true
                      }, 100)
                    }
                  }
                }
                if(item.eventAction===3){
                  for (let j = 0; j < parentList.length; j++) {
                    if (item.id === parentList[j].index) {
                      setTimeout(() => {
                        try{
                          if(parentList[j].component.prop==='tips'){
                            if(parentList[j].option.showPosition == 2){
                              EventBus.$emit('tipsTransformPosition')
                            }
                          }
                        }catch(err){
                          console.log(err)
                        }
                        console.log('parentList[j]==>', parentList[j])
                        parentList[j].display = false
                      }, 100)
                    }
                  }
                }
              }
            })
          }
        }
      },
      updateClick(params, eventType, openInterface) {
        // console.log('params',params)
        // 传图层名称、参数
        params = Object.assign(params,{
          comParams: this.$attrs.comParams || '',
          comName:this.$attrs.name || '',
          comId:this.id || ''
        })
        let refList = this.getItemRefs()
        // let nav = this.$attrs.parentTipsIndex?this.$parent.$parent.$parent.nav: this.$parent.$parent.nav
        let parent = this.$parent
        this.getNavParent(parent)
        let sumList = [];
        let parentList = []
        let nav = this.navWholeList
        const detailList = item =>{
          if(item.list){
            item.list.forEach(ele => {
              sumList.push(ele)
              detailList(ele)
            })
          }
        }
        if(nav){
          for(let i = 0; i < nav.length; i++){
            sumList.push(nav[i])
            detailList(nav[i])
          }
        }
        // 内部框架
        let setDomModule = {
          eventType:eventType,
          eventList:this.eventList,
          refList:refList,
          params:params,
          assemblyType:this.component.prop
        }
        EventBus.$emit('setDomModule',setDomModule)
        parentList = sumList;
        // if (this.$attrs.parentTipsIndex) {
        //   parentList = nav.find(
        //     item => item.index === this.$attrs.parentTipsIndex
        //   ).list
        // } else {
        //   parentList = nav
        // }
        let eventListArr
        if (openInterface) {
          if (openInterface.isWebsocket) {
            eventListArr = this.eventList.filter(
              item =>
                item.eventId === openInterface.eventId && item.openInterface
            )
          } else {
            eventListArr = this.eventList.filter(
              item => item.eventType === eventType
            )
          }
        } else {
          eventListArr = this.eventList.filter(
            item => item.eventType === eventType
          )
        }
        // 表格操作事件
        if(eventType==='columnClickFormatter'){
          eventListArr = eventListArr.filter(item=>{
            return item.buttonType === params.bottonType.tableButtonId
          })
        }
        // console.log('eventListArr',eventListArr)
        if (eventListArr) {
          for (let i = 0; i < eventListArr.length; i++) {
            let itemObj = eventListArr[i]
            switch (itemObj.eventAction) {
              case 1: {
                if (itemObj.child.length > 0) {
                  console.log('api')
                  const apiChild = itemObj.child
                  apiChild.forEach(apiChildItem => {
                    // keyName并不是参数的key值，是从发起事件的组件传过来的params中取哪个属性的值
                    let {
                      index: linkComIndex,
                      paramName,
                      keyName,
                    } = apiChildItem
                    if (
                      !(
                        validatenull(linkComIndex) &&
                        validatenull(paramName) &&
                        validatenull(keyName)
                      )
                    ) {
                      let p = {}
                      p[paramName] = params.value
                      if (apiChildItem.keyName) {
                         p[paramName] = params[apiChildItem.keyName]
                      }
                      Object.keys(refList).forEach(ele => {
                        if (linkComIndex === ele) {
                          console.log('p==>', p)
                          refList[ele].updateData(p)
                        }
                      })
                    }
                  })
                }
                if (itemObj.sqlChild.length > 0) {
                  const sqlChild = itemObj.sqlChild
                  // console.log('apipppp',sqlChild)
                  sqlChild.forEach(sqlChildItem => {
                    let {
                      index: linkComIndex,
                      fieldType,
                      fieldId,
                      paramsType,
                    } = sqlChildItem
                    if (
                      !(
                        validatenull(linkComIndex) &&
                        validatenull(fieldType) &&
                        validatenull(fieldId)
                      )
                    ) {
                      let p
                      // 判断是否是时间控件
                      if (this.component.prop==='atepicker') {
                        switch (params.isInterval) {
                          case true:
                            let time =  params.value.split(',')
                            p = [
                              { term: 'ge', value: time[0], fieldId },
                              { term: 'le', value: time[0], fieldId },
                            ]
                            break
                          case false:
                            p = [
                              { term: 'eq', value: params.value, fieldId },
                            ]
                            break
                        }
                      } else {
                        p = {}
                        p.term = params.termParams ? params.termParams : 'eq'
                        if (fieldType == 1) {
                          if (paramsType === 'bindValue') {
                            p.value = params.value || params[fieldId]
                          } else {
                            p.value = params.comParams || params[fieldId]
                          }
                        } else {
                          p.value = params.name || params.label || params[fieldId]
                        }
                        p.fieldId = fieldId
                      }
                      console.log('refList______',p)
                      Object.keys(refList).forEach(ele => {
                        if (linkComIndex === ele) {
                          console.log('p==>', p)
                          refList[ele].updateData(p)
                        }
                      })
                    }
                  })
                }
                if (itemObj.websocketChild.length > 0) {
                  console.log('有websocketChild要刷新')
                  const websocketChild = itemObj.websocketChild
                  websocketChild.forEach(websocketChilditem => {
                    let linkComIndex = websocketChilditem.index
                    let dataQueryFun = getFunction(websocketChilditem.paramFun) // 先把保存的json字符串转为函数
                    let dataQuery =
                      typeof dataQueryFun === 'function' && dataQueryFun() // 再自调用函数返回真正的参数对象
                    function cb(obj) {
                      let valueTypeArr = obj.valueType.split('.')
                      let key1 = valueTypeArr[0]
                      let key2 = valueTypeArr[1]
                      return refList[obj.index][key1][key2]
                    }
                    const dataQueryParsed = this.traverseObj(dataQuery, cb)
                    if (
                      !(
                        validatenull(linkComIndex) &&
                        validatenull(dataQueryParsed)
                      )
                    ) {
                      Object.keys(refList).forEach(ele => {
                        if (linkComIndex === ele) {
                          console.log('p==>', params)
                          refList[ele].updateData(params)
                        }
                      })
                    }
                  })
                }
                if (itemObj.staticChild.length > 0) {
                  let staticChild = itemObj.staticChild
                  staticChild.forEach(staticChildItem => {
                    let linkComIndex = staticChildItem.index
                    if (!validatenull(linkComIndex)) {
                      Object.keys(refList).forEach(ele => {
                        if (linkComIndex === ele) {
                          console.log('p==>', params)
                          refList[ele].updateData(params)
                        }
                      })
                    }
                  })
                }
                // 日志开发
                if(itemObj.log){
                  let logContentQueryFun = getFunction(itemObj.logContent) 
                  let logContent = params
                  let logContentQuery =
                      typeof  logContentQueryFun === 'function' && logContentQueryFun(logContent)
                  const { visualId, id } = this.contain.obj.config
                  let componetId = this.id.replace('list', '')
                  let eventParms = {
                    visualId: visualId,
                    configId: id,
                    componentId:componetId,
                    eventId:itemObj.eventId,
                    detail:JSON.stringify(logContentQuery)
                  }
                  console.log(eventParms,'日志开发')
                  this.getEventAdd(eventParms)
                }
                break
              }
              case 2: {
                // 隐藏组件
                let hideIndexList = itemObj.showHideChild.index
                for (let k = 0; k < hideIndexList.length; k++) {
                  for (let j = 0; j < parentList.length; j++) {
                    if (hideIndexList[k] === parentList[j].index) {
                      // 如果没有在配置页面触发该组件的隐藏按钮，那么display属性就不是一个响应式的属性，所以要先把他变为响应式
                      try{
                        if(parentList[j].component.prop==='tips'){
                          this.$setSessionItem('imgBorderTypeChange',false)
                        }
                      }catch(err){
                        console.log(err)
                      }
                      setTimeout(() => {
                        this.$set(parentList[j], 'display', '')
                        parentList[j].display = true
                      }, 100)
                    }
                  }
                }

                // Object.keys(refList).forEach((ele) => {
                //   if (hideIndexList.includes(ele)){
                //     // console.log("refList[ele]==>",ele,refList[ele])
                //     // refList[ele].display = true;

                //   }
                // });
                // for(let j=0;j<this.list.length;j++){
                //   if (hideIndexList.includes(this.list[j].index)){
                //     // console.log("refList[ele]",this)
                //     this.list[j].display = true;
                //   }
                // }
                break
              }
              case 3: {
                console.log('组件显示')
                let hideIndexList = itemObj.showHideChild.index
                for (let k = 0; k < hideIndexList.length; k++) {
                  for (let j = 0; j < parentList.length; j++) {
                    if (hideIndexList[k] === parentList[j].index) {
                      setTimeout(() => {
                        try{
                          if(parentList[j].component.prop==='tips'){
                            if(parentList[j].option.showPosition == 2){
                              EventBus.$emit('tipsTransformPosition')
                            }
                          }
                        }catch(err){
                          console.log(err)
                        }
                        console.log('parentList[j]==>', parentList[j])
                        parentList[j].display = false
                      }, 100)
                    }
                  }
                }
                // 显示组件
                break
              }
              // 调用服务
              case 4: {
                console.log('调用服务')
                // 当前组件事件列表的
                let serverChild = itemObj.serverChild
                let result = getUrlParams(serverChild.url) // 将url字符串进行拆分，如果字符传中含有请求参数
                let url = result.url // 只保留请求地址的url
                if (this.validatenull(url)) return // 通过this.validatenull调用的validatenull是avue库的一个api它可以用来判断对象、数组、字符串是否为空
                function cb(obj) {
                  let valueTypeArr = obj.valueType.split('.')
                  let key1 = valueTypeArr[0]
                  let key2 = valueTypeArr[1]
                  return refList[obj.index][key1][key2]
                }
                function parseUrl(paramsValue) {
                  let reg = /\{.{1,}\}/g
                  const objString = paramsValue.match(reg)
                    ? paramsValue.match(reg)[0]
                    : ''
                  if (!objString) return
                  console.log('objString==>', objString)
                  let value = cb(JSON.parse(objString))
                  url = paramsValue.replace(reg, '') + value
                }
                parseUrl(result.url)
                let dataQueryFun = getFunction(serverChild.paramFun) // 先把保存的json字符串转为函数
                // // 将请求参数转换为请求对象
                // // serverChild.params.forEach((item) => {
                // //   if (item.val.indexOf('{') > -1) {
                // //     item.val = JSON.parse(item.val)
                // //   }
                // //   dataQuery[item.name] = item.val;
                // // });
                let dataQuery
                if (eventType === 'openClickFormatter') {
                  if(params.deviceId){
                    let deviceIdType = deviceIdList.filter(item => {
                      if (item === params.deviceId) {
                        return item
                      }
                    })
                    if (deviceIdType.length > 0) {
                      url = url + '/' + 'L01-S01-PSD'
                      let openParms = {
                        event: {
                          closeKey: 'DO001',
                          closeValue: '1',
                          openKey: 'DO000',
                          openValue: '1',
                        },
                      }
                      dataQuery =
                        typeof dataQueryFun === 'function' &&
                        dataQueryFun(openParms)
                    } else {
                      if (params.deviceId === 'L06-S01-GOK62501') {
                        url = url + '/' + 'L01-S01-AGM1'
                        let openParms = {
                          event: {
                            closeKey: 'DO000',
                            closeValue: '0',
                            openKey: 'DO000',
                            openValue: '1',
                          },
                        }
                        dataQuery =
                          typeof dataQueryFun === 'function' &&
                          dataQueryFun(openParms)
                      } else {
                        url = url + '/' + params.deviceId
                        dataQuery =
                          typeof dataQueryFun === 'function' &&
                          dataQueryFun(params) // 再自调用函数返回真正的参数对象
                      }
                    }
                  }else{
                    dataQuery =
                    typeof dataQueryFun === 'function' &&
                    dataQueryFun(params) // 再自调用函数返回真正的参数对象
                  }
                } else if (eventType === 'closeClickFormatter') {
                  if(params.deviceId){
                    let deviceIdType = deviceIdList.filter(item => {
                      if (item === params.deviceId) {
                        return item
                      }
                    })
                    if (deviceIdType.length > 0) {
                      url = url + '/' + 'L01-S01-PSD'
                      let openParms = {
                        event: {
                          closeKey: 'DO001',
                          closeValue: '1',
                          openKey: 'DO000',
                          openValue: '1',
                        },
                      }
                      dataQuery =
                        typeof dataQueryFun === 'function' &&
                        dataQueryFun(openParms)
                    } else {
                      if (params.deviceId === 'L06-S01-GOK62501') {
                        url = url + '/' + 'L01-S01-AGM1'
                        let closeParms = {
                          event: {
                            closeKey: 'DO000',
                            closeValue: '0',
                            openKey: 'DO000',
                            openValue: '1',
                          },
                        }
                        dataQuery =
                          typeof dataQueryFun === 'function' &&
                          dataQueryFun(closeParms)
                      } else {
                        url = url + '/' + params.deviceId
                        dataQuery =
                          typeof dataQueryFun === 'function' &&
                          dataQueryFun(params) // 再自调用函数返回真正的参数对象
                      }
                    }
                  }else{
                    dataQuery =
                    typeof dataQueryFun === 'function' &&
                    dataQueryFun(params) // 再自调用函数返回真正的参数对象
                  }
                } else {
                  dataQuery =
                    typeof dataQueryFun === 'function' && dataQueryFun(params) // 再自调用函数返回真正的参数对象
                }
                const dataQueryParsed = this.traverseObj(dataQuery, cb)
                // 日志开发
                if(itemObj.log){
                  let logContentQueryFun = getFunction(itemObj.logContent) 
                  let logContent =  Object.assign(
                    params,
                    result.params,
                    dataQueryParsed
                  )
                  let logContentQuery =
                      typeof  logContentQueryFun === 'function' && logContentQueryFun(logContent)
                  const { visualId, id } = this.contain.obj.config
                  let componetId = this.id.replace('list', '')
                  let eventParms = {
                    visualId: visualId,
                    configId: id,
                    componentId:componetId,
                    eventId:itemObj.eventId,
                    detail:JSON.stringify(logContentQuery)
                  }
                  this.getEventAdd(eventParms)
                }
                function sendRequest(headers = {}) {
                  if (serverChild.method === 'GET') {
                    const paramsObj = Object.assign(
                      result.params,
                      dataQueryParsed
                    )
                    axios({
                      method: 'GET',
                      url,
                      noVisualization:true, 
                      params: paramsObj,
                      headers:headers,
                    })
                  } else if (serverChild.method === 'POST') {
                    axios({
                      method: 'POST',
                      url,
                      noVisualization:true, 
                      params: result.params,
                      data: dataQueryParsed,
                      headers:headers,
                    })
                  }
                }
                // get请求和post请求对于请求参数的放置位置不同所以需要分开处理
                if (serverChild.authenticationMethod === 'noAuth') {
                  let paramHeader = getFunction(serverChild.paramHeader)
                  let headers =
                    typeof paramHeader === 'function' &&
                  paramHeader()
                  sendRequest(headers)
                } else {
                  this.getToken(serverChild.authenticationForm).then(res => {
                    let newToken = {}
                    if(res.data){
                      newToken = res.data
                    }
                    let paramHeader = getFunction(serverChild.paramHeader)
                    let headers =
                      typeof paramHeader === 'function' &&
                    paramHeader(newToken)
                    sendRequest(headers)
                    // let token = 'Bearer ' + res.data.token
                    // sendRequest({ headers: { 'X-Authorization': token } })
                  })
                }
                break
              }
              case 5: {
                console.log('页面联动',itemObj)
                let iframeUrlIndex = []
                // itemObj.currenScreen.value crypto.encrypt(itemObj.currenScreen.value).replaceAll('/', baseConfig.SHARESECRETKEY)
                nav.forEach(ele => {
                  if(ele.name === 'iframe'){
                    if(ele.data.value.indexOf(crypto.encrypt(itemObj.currenScreen.value).replaceAll('/', baseConfig.SHARESECRETKEY))>0){
                      iframeUrlIndex.push(ele.index)
                    }else{
                      if(itemObj.currenScreen){
                        if(itemObj.currenScreen.option){
                          if(itemObj.currenScreen.option.path){
                            if(ele.data.value.indexOf(itemObj.currenScreen.option.path)>0){
                              iframeUrlIndex.push(ele.index)
                            }
                          }
                        }
                      }
                    }
                  }
                })
                if (
                  iframeUrlIndex.length > 0 &&
                  itemObj.paramNameValList.length > 0
                ) {
                  let paramVal = {}
                  itemObj.paramNameValList.forEach(item => {
                    paramVal[item.currenScreenName] = params[item.currentScreenVal]
                    if (params.termParams) {
                      // 如果是下拉框可能多选 多选情况下会配置termParams参数告诉后台sql的查询方式 所以应该要带上
                      paramVal.termParams = params.termParams
                    }
                  })
                  iframeUrlIndex.forEach(ele => {
                    refList[ele], refList[ele].postData(paramVal)
                    // console.log('refList[ele].$refs',refList[ele],refList[ele].postData(paramVal))
                  })
                }
                break
              }
              case 6: {
                if (eventListArr.length > 0) {
                  let tableIndex = eventListArr.length - 1
                  let urlParameterValue =
                    eventListArr[tableIndex].urlParameter || []
                  let tableParams = {
                    urlAdress: eventListArr[tableIndex].urlAdress || '',
                    urlHeight: eventListArr[tableIndex].urlHeight || '',
                    urlWidth: eventListArr[tableIndex].urlWidth || '',
                  }
                  let newUrlParameterValue = []
                  console.log(urlParameterValue, tableParams, 9999)
                  if (
                    Object.prototype.toString
                      .call(urlParameterValue)
                      .slice(8, -1) === 'Array'
                  ) {
                    urlParameterValue.forEach(item => {
                      newUrlParameterValue.push({
                        urlParameterName: item.urlParameterName,
                        urlParameterValue: params[item.urlParameterValue] || '',
                      })
                    })
                    tableParams.urlParameterValue = newUrlParameterValue
                  }
                  EventBus.$emit('tableColumnClickFormatter', tableParams)
                }
                break
              }
              case 7: {
                if (itemObj.JScode) {
                  let jsCodeDataList = params
                  let textContent = `function(jsCodeDataList){${itemObj.JScode}}`
                  let f = getFunction(textContent)
                  typeof f === 'function' && f(jsCodeDataList)
                  if(itemObj.log){
                    let logContentQueryFun = getFunction(itemObj.logContent) 
                    let logContent
                    logContent = Object.assign(datalist)
                    let logContentQuery =
                        typeof  logContentQueryFun === 'function' && logContentQueryFun(logContent)
                    const { visualId, id } = this.contain.obj.config
                    let componetId = this.id.replace('list', '')
                    let eventParms = {
                      visualId: visualId,
                      configId: id,
                      componentId:componetId,
                      eventId:itemObj.eventId,
                      detail: JSON.stringify(logContentQuery)
                    }
                    this.getEventAdd(eventParms)
                  }
                }
                break
              }
              case 8: {
                if(itemObj.settingMultiparameter){
                  if(itemObj.settingMultiparameter.lableName){
                    if(this.contain.config.multiparameterList){
                      this.contain.config.multiparameterList.forEach(item=>{
                        if(item.paramsKey===itemObj.settingMultiparameter.lableName){
                          if(params.termParams){
                            item.termParams = params.termParams
                          }else{
                            item.termParams = "eq"
                          }
                          if(itemObj.settingMultiparameter.valueData==='comName'){
                            // 图层参数值
                            item.paramsValue = this.$attrs.comParams || ''
                          }else{
                            if(this.component.prop==='atepicker'){
                              item.paramsValue = params.value.split(',')
                            }else{
                              // 当前值
                              item.paramsValue = params.value || params.label
                            }
                          }
                        }
                      })
                    }
                  }
                }
                let child = [];
                let sqlChild = [];
                let websocketChild = []
                let settingEventList = this.contain.config.eventList.filter(item=>{
                  return item.eventAction === 9
                });
                settingEventList.forEach(item=>{
                  child = [...item.child,...child]
                  sqlChild = [...item.sqlChild,...sqlChild]
                  websocketChild = [...item.websocketChild,...websocketChild]
                })
                let upDateObj = {}
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
                      p: this.getChildData(item.children)
                    }
                  })
                }
                if(sqlChild.length>0){
                  // console.log(sqlChild,9999)
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
                  sqlChildData.forEach(item=>{
                    upDateObj[item.index] = {
                      p: this.getSqlChildData(item.children,params)
                    }
                  })
                //  console.log(sqlChildData,'sqlChildData')
                }
                if(websocketChild.length>0){
                  console.log('111')
                }
                // 日志开发
                if(itemObj.log){
                  let logContentQueryFun = getFunction(itemObj.logContent) 
                  let logContent
                  Object.keys(upDateObj).forEach(ele => {
                    logContent = Object.assign(upDateObj[ele].p,params)
                  })
                  let logContentQuery =
                      typeof  logContentQueryFun === 'function' && logContentQueryFun(logContent)
                  const { visualId, id } = this.contain.obj.config
                  let componetId = this.id.replace('list', '')
                  let eventParms = {
                    visualId: visualId,
                    configId: id,
                    componentId:componetId,
                    eventId:itemObj.eventId,
                    detail: JSON.stringify(logContentQuery)
                  }
                  this.getEventAdd(eventParms)
                }
                if(itemObj.settingMultiparameter.interfaceSwitch){
                  return false
                }
                Object.keys(upDateObj).forEach(ele => {
                  refList[ele].updateData(upDateObj[ele].p)
                })
                break
              }
            }
          }
        }
      },
      getChildData(children){
        let parms = {}
        if(children.length>0){
          children.forEach(item=>{
            if(item.paramName){
              parms[item.paramName]=this.getSettingMultiparameter(item.keyName,true)
            }
          })
        }
        return parms
      },
      getSqlChildData(children){
        let p = []
        if(children.length>0){
          children.forEach(item=>{
            if(Object.prototype.toString.call(this.getSettingMultiparameter(item.paramsType).paramsValue).slice(8,-1) === 'Array'){
                if(this.getSettingMultiparameter(item.paramsType).paramsValue.length>1){
                  this.getSettingMultiparameter(item.paramsType).paramsValue.forEach((timeItem,index)=>{
                    if(index===0){
                      p.push({
                        term: 'ge', 
                        value: timeItem, 
                        fieldId: item.fieldId
                      })
                    }else{
                      p.push({
                        term: 'le', 
                        value: timeItem, 
                        fieldId: item.fieldId
                      })
                    }
                  })
                }else{
                  this.getSettingMultiparameter(item.paramsType).paramsValue.forEach((timeItem)=>{
                    p.push({
                      term: 'eq', 
                      value: timeItem, 
                      fieldId: item.fieldId
                    })
                  })
                }
              }else{
                p.push({
                  fieldId: item.fieldId,
                  term: this.getSettingMultiparameter(item.paramsType).termParams || 'eq',
                  value: this.getSettingMultiparameter(item.paramsType).paramsValue || ''
                })
            }
          })
        }
        return p
      },
      getSettingMultiparameter(type,aplFlage){
        let multiparameterList = []
        if(this.contain.config.multiparameterList){
          multiparameterList = this.contain.config.multiparameterList
        }
        if(multiparameterList.length>0){
          let parms = multiparameterList.filter(item=>{
              return item.paramsKey === type
          })
          if(aplFlage){
            if(Object.prototype.toString.call(parms[0].paramsValue).slice(8,-1) === 'Array'){
              return parms[0].paramsValue.join(',') || ''
            }
            return parms[0].paramsValue || ''
          }else{
            return parms[0] || ''
          }
        }
        return ''
      },
      updateAppend(result) {
        if (this.validatenull(this.appendObj)) {
          this.appendList = result
          this.appendObj = result[0]
        } else {
          let appendList = []
          for (let i = 0; i < result.length; i++) {
            const ele = result[i]
            if (ele.id === this.appendObj.id) break
            else appendList.push(ele)
          }
          this.appendObj = result[0]
          appendList.reverse().forEach(ele => {
            this.appendList.unshift(ele)
          })
        }
        if (this.validatenull(this.appendCheck)) {
          this.appendCheck = setInterval(() => {
            let length = this.appendList.length - 1
            if (length >= 0) {
              let obj = this.appendList.splice(length, 1)[0]
              this.dataChart.unshift(obj)
              let len = this.dataChart.length
              if (len > this.count) {
                this.appendList.splice(len - 1, 1)
              }
            }
          }, 2000)
        }
      },
      updateUrl(url) {
        this.dataUrl = url
        this.updateData()
      },
      // 更新数据核心方法
      updateData(p = {}) {
        if (this.websocket) {
          this.websocket.close()
          this.websocket = null
        }
        this.dynamicQuery = Object.assign(this.dynamicQuery, p)
        return new Promise((resolve, reject) => {
          this.resetData && this.resetData()
          if (this.key) return
          this.key = true
          const callback = () => {
            this.key = false
            // let updateErr = false
            const bindEvent = () => {
              if (this.isChart) this.updateChart()
              if (this.isHighChart) this.updateChart()
              if (this.myChart) {
                this.bindClick()
                this.bindDblClick()
              }
              if (typeof this.stylesFormatter === 'function') {
                this.styles =
                  this.stylesFormatter(
                    this.dataChart,
                    this.dynamicQuery,
                    this.getItemRefs()
                  ) || {}
              }
              resolve(this.dataChart)
            }

            // 动态数据
            if (this.isApi) {
              const detail = res => {
                // 处理返回的数据
                this.dataAxios = res
                let result = (() => {
                  // 如果在数据处理对话框添加了数据处理函数就会走这里
                  if (typeof this.dataFormatter === 'function') {
                    return this.dataFormatter(
                      res.data,
                      this.dataAxios,
                      this.getItemRefs()
                    )
                  }
                  return res.data || {}
                })()
                if((JSON.stringify(result) == "{}")){
                  result = res.data
                }
                // 延迟效果数据逐步增加
                if (this.dataAppend) {
                  this.updateAppend(result)
                } else {
                  if (this.component.prop === 'dataStorage') {
                    this.dataChart = this.deepClone(result)
                    let dataSource = {
                      id: this.$attrs.index
                    }
                    if(Object.prototype.toString.call(result).slice(8, -1) === 'Array'){
                      let treeData = {
                        treeData: result
                      }
                      dataSource =  Object.assign(dataSource,treeData)
                    }else{
                      dataSource = Object.assign(dataSource,result)
                    }
                    this.$store.commit('SET_DATA_SOURCE_LIST', dataSource)
                  } else if (this.component.prop === 'table') {
                    try{
                      this.pageTotal = result.data.length
                      this.dataChart = this.deepClone(result)
                      this.tableData = this.deepClone(result.data)
                      this.column = this.deepClone(this.dataChart.column)
                      let column = this.deepClone(this.dataChart.column)
                      let optionColumn = this.deepClone(this.option.column)
                      if(this.option.column){
                        this.getOptionTableColumn(column,optionColumn)
                      }
                      this.option.column = this.deepClone(column)
                    }catch(e){
                      console.log(e)
                      return this.$message.error('数据出现问题')
                    }
                  } else if (this.component.prop === 'crosstable') {
                    // 当是交叉表时，进行处理
                    if (!!result.data) {
                      try{
                        this.pageTotal = this.deepClone(result.total)
                        this.dataChart = this.deepClone(result.data)
                        this.option.column = {
                          colHeads: this.deepClone(result.colHeads),
                          fieldS: this.deepClone(result.fieldS),
                          rowHeads: this.deepClone(result.rowHeads),
                        }
                      }catch(e){
                        console.log(e)
                        return this.$message.error('数据出现问题')
                      }
                    } else {
                      this.dataChart = result
                    }
                  } else {
                    this.dataChart = result
                  }
                }
                bindEvent()
              }

              let _this = this
              function cb(obj) {
                let refList = _this.getItemRefs()
                let valueTypeArr = obj.valueType.split('.')
                let key1 = valueTypeArr[0]
                let key2 = valueTypeArr[1]
                return refList[obj.index][key1][key2]
              }
              let result = getUrlParams(this.dataUrl) // dataUrl为计算属性，实际即为父组件传来的url，getUrlParams对url做一个分割，有查询参数就把查询参数提取出来
              let levelData = { // 图层值
                comName:this.$attrs.name,
                comParams:this.$attrs.comParams
              }
              result = Object.assign(result,levelData)
              let url = result.url // 只保留请求地址的url
              if(result.params){
                result.params = {...result.params,...this.dynamicQuery} //合并参数
              }
              if (this.validatenull(url)) return // 通过this.validatenull调用的validatenull是avue库的一个api它可以用来判断对象、数组、字符串是否为空
              let dataQuery =
                typeof this.dataQuery === 'function' && this.dataQuery(result)
              const dataQueryParsed = this.traverseObj(dataQuery, cb)
              let params = Object.assign(
                result.params,
                // dataQuery,
                dataQueryParsed,
                this.propQuery,
                this.dynamicQuery,
              )
              // 当参数处理要作为第一参数时,当dataQueryParsed有visualizationParametersFirst等于1时
              if(dataQueryParsed.visualizationParametersFirst === 1){
                delete dataQueryParsed.visualizationParametersFirst;
                 params = Object.assign(
                  result.params,
                  // dataQuery,
                  this.propQuery,
                  this.dynamicQuery,
                  dataQueryParsed
                )
              }
              if(this.component.prop === 'textRankingList'){
                this.textRankingListLoading = true; //推荐词loading状态
              }
              if (this.apiAuthenticationMethod === 'basicAuth') {
                this.getToken(this.apiAuthenticationForm).then(res => {
                  let newToken = {}
                  if(res.data){
                    newToken = {
                      newToken:res.data
                    }
                  }
                  result = Object.assign(result,newToken)
                  let dataHeader =
                    (typeof this.dataHeader === 'function' &&
                      this.dataHeader(result)) ||
                    {} 
                  // let token = 'Bearer ' + res.data.token
                  // let dataHeaderWithToken = Object.assign({}, dataHeader, {
                  //   'X-Authorization': token,
                  // })
                  this.$axios({
                    method: this.dataMethod,
                    url: url,
                    data: params,
                    params: params,
                    noVisualization:true,
                    headers: dataHeader,
                  }).then(res => {
                    this.textRankingListLoading = false //推荐词loading状态
                    detail(res)
                  }).catch(()=>{
                    this.textRankingListLoading = false //推荐词loading状态
                  })// 这里走的是mockjs的数据所以看不到请求
                })
              } else {
                let dataHeader =
                  (typeof this.dataHeader === 'function' &&
                    this.dataHeader(result)) ||
                  {} 
                this.$axios({
                  method: this.dataMethod,
                  url: url,
                  data: params,
                  noVisualization:true, 
                  headers: dataHeader,
                  params: params,
                }).then(res => {
                  this.textRankingListLoading = false  //推荐词loading状态
                  detail(res)
                }).catch(()=>{
                  this.textRankingListLoading = false //推荐词loading状态
                })// 这里走的是mockjs的数据所以看不到请求
              }
            } else if (this.isSql) {
              console.log('sq的 p----->', p,this.sqlData)
              this.sqldatahandle(p, this.sqlData)
              let type = this.component.prop
              const data = this.sqlData
              // console.log(this.option,'this.currentObj')
              sqlSearch(type, data).then(res => {
                if (res.data.success) {
                  if (type === 'table') {
                    let transferred = this.option.tabelSortConditionS || []
                    this.pageTotal = res.data.data.total
                    if (
                      Array.isArray(transferred) &&
                      transferred.length > 0
                    ) {
                      transferred.forEach(transferredEl => {
                        if (transferredEl.transferred) {
                          transferredEl.rule.forEach(ruleEl => {
                            res.data.data.data.forEach(el => {
                              for (let i in el) {
                                if (
                                  i == transferredEl.fieldId &&
                                  el[i] === ruleEl.originalValue
                                ) {
                                  el[i] = ruleEl.transferredValue
                                }
                              }
                            })
                          })
                        }
                      })
                    }
                    this.dataChart = this.deepClone(res.data.data)
                    this.tableData = this.deepClone(res.data.data.data)
                    this.column = this.deepClone(this.dataChart.column)
                    let column = this.deepClone(this.dataChart.column)
                    let optionColumn = this.deepClone(this.option.column)
                    if(this.option.column){
                      this.getOptionTableColumn(column,optionColumn)
                    }
                    this.option.column = this.deepClone(column)
                  } else if (type === 'crosstable') {
                    this.pageTotal = res.data.data.total
                    this.dataChart = res.data.data.data
                    this.option.column = {
                      colHeads: res.data.data.colHeads,
                      fieldS: res.data.data.fieldS,
                      rowHeads: res.data.data.rowHeads,
                    }
                  } else if (type === 'tree') {
                    this.dataChart = res.data.data.data
                  } else if (type === 'dataStorage') {
                    this.dataChart =  res.data.data
                    // 如果是数据源组件那么要把取得的值存储到vuex的dataSourceList
                    let dataSource = {
                      id: this.$attrs.index
                    }
                    if(Object.prototype.toString.call(this.dataChart).slice(8, -1) === 'Array'){
                      let treeData = {
                        treeData: this.dataChart
                      }
                      dataSource =  Object.assign(dataSource,treeData)
                    }else{
                      this.dataChart = this.sortData(res.data.data)
                      dataSource = Object.assign(dataSource,this.dataChart)
                    }
                    this.$store.commit('SET_DATA_SOURCE_LIST', dataSource)
                  } else if (type === 'text') {
                    let valueStr = ''
                    res.data.data.forEach(item => {
                      valueStr += `${item.value},`
                    })
                    this.dataChart = {
                      value: valueStr.slice(0, -1),
                    }
                  } else {
                    this.dataChart = res.data.data
                    // console.log('--------------------', this.dataChart)
                  }
                } else {
                  this.$message.error(res.data.msg)
                }
                bindEvent()
              })
            } else if (this.isWebSocket) {
              const detail = (res, initData) => {
                // 处理返回的数据
                this.dataAxios = res

                let result = (() => {
                  // 如果在数据处理对话框添加了数据处理函数就会走这里
                  if (typeof this.dataFormatter === 'function') {
                    return this.dataFormatter(
                      res.data,
                      initData,
                      this.dataAxios
                    )
                  }
                  return res.data || {}
                })()
                if((JSON.stringify(result) == "{}")){
                  result = res.data
                }
                if (this.component.prop === 'dataStorage') {
                  let dataSource = {
                    id: this.$attrs.index
                  }
                  if(Object.prototype.toString.call(result).slice(8, -1) === 'Array'){
                    let treeData = {
                      treeData: result
                    }
                    dataSource =  Object.assign(dataSource,treeData)
                  }else{
                    dataSource = Object.assign(dataSource,result)
                  }
                  this.$store.commit('SET_DATA_SOURCE_LIST', dataSource)
                }else if(this.component.prop === 'table'){
                  try{
                    this.pageTotal = result.data.length
                    this.dataChart = this.deepClone(result)
                    this.tableData = this.deepClone(result.data)
                    this.column = this.deepClone(this.dataChart.column)
                    let column = this.deepClone(this.dataChart.column)
                    let optionColumn = this.deepClone(this.option.column)
                    if(this.option.column){
                      this.getOptionTableColumn(column,optionColumn)
                    }
                    this.option.column = this.deepClone(column)
                  }catch(e){
                    console.log(e)
                    return this.$message.error('数据出现问题')
                  }
                }else if(this.component.prop === 'crosstable'){
                   // 当是交叉表时，进行处理
                   if (!!result.data) {
                    try{
                      this.pageTotal = this.deepClone(result.total)
                      this.dataChart = this.deepClone(result.data)
                      this.option.column = {
                        colHeads: this.deepClone(result.colHeads),
                        fieldS: this.deepClone(result.fieldS),
                        rowHeads: this.deepClone(result.rowHeads),
                      }
                    }catch(e){
                      console.log(e)
                      return this.$message.error('数据出现问题')
                    }
                  } else {
                    this.dataChart = result
                  }
                }else{
                  this.dataChart = result
                }
                bindEvent()
              }
              // console.log('实时数据')
              let result = getUrlParams(this.dataWebSocketUrl)
              let url = result.url
              if (this.validatenull(url)) return
              let websocketQuery =
                typeof this.websocketQuery === 'function' &&
                this.websocketQuery(result)
              let websocketHeader =
                (typeof this.websocketHeader === 'function' &&
                  this.websocketHeader(result)) ||
                {}
              console.log(websocketHeader,'websocketHeader')
              Object.assign(websocketQuery, p)
              this.webSocketInit(url, websocketQuery, detail)
            } else if (this.isDataFromDataStorage) {
              if (this.dataSource) {
                // 是树形数据
                if (this.dataSource.treeData) {
                  let parms = findTree(this.dataSource.treeData,(item)=> item.id===this.dataPosition.id)
                  let label = ''
                  if(parms){
                    if(parms.item){
                      if(parms.item.value){
                        label =  parms.item.value
                      }else{
                        label = parms.item.label || ''
                      }
                    }
                  }
                  this.dataChart = {
                    value: label,
                  }
                }else {  // 是表格型数据
                  this.dataChart = {
                    value:
                      this.dataSource.data[this.dataPosition.row][
                        this.dataPosition.column
                      ],
                  }
                }
                bindEvent()
              }
            } else {
              // 有的组件并没有预设置data,所以要判断下
              Object.assign(this.data ? this.data : {}, p)
              // 静态数据
              if (typeof this.dataFormatter === 'function') {
                let result = this.dataFormatter(
                  this.data,
                  this.dynamicQuery,
                  this.getItemRefs()
                )
                if((JSON.stringify(result) == "{}")){
                  this.dataChart = this.deepClone(this.data)
                  this.tableData = this.deepClone(this.data)
                }else{
                  this.dataChart = result
                }
              } else {
                this.dataChart = this.deepClone(this.data)
                this.tableData = this.deepClone(this.data)
              }
              if (this.component.prop === 'dataStorage') {
                let dataSource = {
                  id: this.$attrs.index
                }
                if(Object.prototype.toString.call(this.dataChart).slice(8, -1) === 'Array'){
                  let treeData = {
                    treeData: this.dataChart
                  }
                  dataSource =  Object.assign(dataSource,treeData)
                }else{
                  dataSource = Object.assign(dataSource,this.dataChart)
                }
                this.$store.commit('SET_DATA_SOURCE_LIST', dataSource)
              }
              if (this.component.prop === 'table') {
                this.tableData = this.deepClone(this.dataChart.data)
                this.pageTotal = this.tableData.length
                this.column = this.deepClone(this.dataChart.column)
                let column = this.deepClone(this.dataChart.column)
                let optionColumn = this.deepClone(this.option.column)
                if(this.option.column){
                  this.getOptionTableColumn(column,optionColumn)
                }
                this.option.column = this.deepClone(column)
            
              }
              bindEvent()
            }
          }
          this.$nextTick(() => {
            callback()
            clearInterval(this.checkChart)
            if (this.time !== 0 && this.disabled) {
              this.checkChart = setInterval(() => {
                // this.myChart.clear() // 清空图形，重新绘制
                callback()
              }, this.time)
            }
          })
        })
      },
      // table 多表头处理
      getOptionTableColumn(column,optionColumn){
        if(column){
          if(column.length){
            column.forEach(item=>{
              if(item.prop){
                this.combined = {}
                this.getTableColumn(item.prop,optionColumn)
                if(this.combined.width){
                  item.width=this.combined.width
                }
              }
              if(item.children && item.children.length){
                this.getOptionTableColumn(item.children,optionColumn)
              }
            })
          }
        }
      },
      // table 多表头处理
      getTableColumn(value,arr){
        if(arr.length>0){
          arr.forEach(item =>{
            if(item.prop===value){
              this.combined = item
            }
            if(item.children && item.children.length){
              this.getTableColumn(value,item.children)
            }
          })
        }
      },
      getLabelFormatter(name) {
        if (this.labelFormatter) {
          return this.labelFormatter(name, this.dataChart)
        }
        return name.value
      },
      // 绑定点击事件
      bindClick() {
        this.myChart.off('click')
        this.myChart.on('click', e => {
          console.log('绑定点击事件', e)
          if (e.marker) {
            this.clickFormatter &&
              this.clickFormatter(
                {
                  type: this.name,
                  name: e.name,
                  value: e.value[2] || e.value,
                  data: this.dataChart,
                },
                this.getItemRefs()
              )
          }
          this.updateClick(e, 'clickFormatter')
        })
      },
      // 绑定双击事件
      bindDblClick() {
        this.myChart.off('dblclick')
        this.myChart.on('dblclick', e => {
          // console.log("绑定点击事件",e)
          if (e.marker) {
            this.dblClickFormatter &&
              this.dblClickFormatter(
                {
                  type: this.name,
                  name: e.name,
                  value: e.value[2] || e.value,
                  data: this.dataChart,
                },
                this.getItemRefs()
              )
          }
        })
      },
      // 下面俩都是chart的公共的方法,就放这里面共用F
      getColor(index, first) {
        const barColor = this.option.barColor || []
        if (barColor[index]) {
          const color1 = barColor[index].color1
          const color2 = barColor[index].color2
          const postion = (barColor[index].postion || 0.9) * 0.01
          if (first) return color1
          if (color2) {
            return {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                {
                  offset: 0,
                  color: color1, // 0% 处的颜色
                },
                {
                  offset: postion,
                  color: color2, // 100% 处的颜色
                },
              ],
              global: false, // 缺省为 false
            }
          }
          return color1
        }
      },
      ishasprop(condition, isprop, alwaysObj) {
        return Object.assign(
          (() => {
            return condition ? isprop : {}
          })(),
          alwaysObj
        )
      },
      //  sqldata数据处理函数
      sqldatahandle(params, oldData) {
        if (oldData && oldData.linkCustomFilterS) {
        } else {
          oldData.linkCustomFilterS = {}
        }
        oldData.linkCustomFilterS.filter = []
        //如果是数组，则是时间控件控制数据，直接赋值
        if (Array.isArray(params)) {
          oldData.linkCustomFilterS.filter = params
        } else {
          if (params.fieldId) {
            oldData.linkCustomFilterS[params.fieldId] = params
          }
          for (var key in oldData.linkCustomFilterS) {
            if (key != 'filter' && key != 'undefined') {
              oldData.linkCustomFilterS.filter.push({
                ...oldData.linkCustomFilterS[key],
              })
            }
          }
          // 删除无用参数
          delete oldData.linkCustomFilterS[params.fieldId]
        }
      },
      // websocket初始化
      webSocketInit(url, websocketQuery, detail) {
        let finalUrl
        let lockReconnect = false
        let timeout = 60 * 1000
        let timeoutObj = null
        let serverTimeoutObj = null
        let tt = null
        let _this = this
        let initData = {} // 该数据用来存储第一次websocket发送回来的数据
        // 心跳检测
        const heartCheck = function () {
          // console.log('心跳检测开始');
          timeoutObj && clearTimeout(timeoutObj)
          // serverTimeoutObj && clearTimeout(serverTimeoutObj);
          // 0;
          timeoutObj = setTimeout(() => {
            //发送测试信息，后端收到后，返回一个消息，
            // this.websocket.send("hello");
            console.log('是否连接着')
            // serverTimeoutObj = setTimeout(() => {
            //   _this.websocket.close();
            // }, timeout);
          }, timeout)
        }
        // 重新链接
        const websocketReconnect = function () {
          if (lockReconnect) {
            // 是否已经执行重连
            return
          }
          lockReconnect = true
          //没连接上会一直重连，设置延迟避免请求过多
          tt && clearTimeout(tt)
          tt = setTimeout(() => {
            createWebSocket()
            lockReconnect = false
            console.log('正在重连')
          }, 5 * 1000)
        }
        // 创建websocket实例
        const createWebSocket = function () {
          try {
            if ('WebSocket' in window) {
              if (_this.authenticationMethod === 'noAuth') {
                finalUrl = url
                _this.websocket = new WebSocket(finalUrl)
                extendWebSocket()
              } else {
                _this.getToken(_this.authenticationForm).then(res => {
                  finalUrl = url + '?token=' + res.data.token
                  _this.websocket = new WebSocket(finalUrl)
                  extendWebSocket()
                })
              }
              //url为websocket地址
            } else {
              this.$message.error('该浏览器不支持websocket！');
            }
          } catch (e) {
            console.log('catch==> e')
            websocketReconnect() //尝试重连websocket
          }
        }
        // 用于给websocket实例添加方法
        const extendWebSocket = function () {
          // console.log('_this.websocket===>' + _this.id, _this)
          // 监听链接的打开
          _this.websocket.addEventListener('open', event => {
            const sendMsg = JSON.stringify(websocketQuery)
            console.log('链接打开', sendMsg)
            _this.websocket.send(sendMsg)
            heartCheck()
          })
          //监听后端传递的数据
          _this.websocket.addEventListener('message', event => {
            // console.log('event==>', event)
            detail(event, initData)
            heartCheck()
          })
          // 监听链接的关闭
          _this.websocket.addEventListener('close', event => {
            console.log('连接关闭')
            lockReconnect = false
            // websocketReconnect(); //尝试重连websocket
          })
          // 监听链接发生错误
          _this.websocket.addEventListener('error', event => {
            console.log('链接错误')
            _this.$message.error('实时数据源发生错误')
            _this.dataChart = {
              value: '错误',
            }
          })
        }
        window.onbeforeunload = function () {
          this.websocket.close()
        }
        createWebSocket()
      },
      // 有的认证接口需要获取token
      getToken(authObj) {
        return new Promise((resolve, reject) => {
          try {
            let authenticationUrl = authObj.authenticationUrl
            let userInfo = {
              password: authObj.password,
              username: authObj.userName,
              loginType: 0,
            }
            // 先获取认证的token
            const res = axios({
              method: 'post',
              url: authenticationUrl,
              data: userInfo,
            })
            resolve(res)
          } catch (error) {
            // this.$message.error(error);
            console.log(error)
          }
        })
      },
      // 数据排序
      sortData(res) {
        const newData = []
        for (let i = 0; i < res.data.length; i++) {
          let arr = []
          for (let j = 0; j < res.column.length; j++) {
            let prop = res.column[j].prop
            arr.push(res.data[i][prop])
          }
          newData.push(arr)
        }
        res.data = newData
        return res
      },
      // 这个函数用来递归参数对象哪些字符串值是需要解析的
      traverseObj(obj, cb) {
        let newObj = {}
        let objCopy = this.deepClone(obj)
        for (let key in objCopy) {
          let value = objCopy[key]
          let resultType = typeof value
          let isArray = Array.isArray(value)
          if (isArray) {
            for (let i = 0; i < value.length; i++) {
              value[i] = this.traverseObj(value[i], cb)
              newObj[key] = value
            }
          } else if (!isArray && resultType === 'object') {
            newObj[key] = this.traverseObj(value, cb)
          } else if (resultType === 'string') {
            //如果这个值是一个字符串，那么就要判断这个字符串是否需要解析
            let index = value.indexOf('$')
            if (index === 0) {
              // 如果开头包含$那么就需要解析这个字符串
              newObj[key] = cb(this.parseParams(value))
            } else {
              // 如果开头不包含字符串说明就是一个普通的字符串，那么直接复制即可
              newObj[key] = value
            }
          } else {
            newObj[key] = value
          }
        }
        return newObj
      },
      // 这个函数用来解析事件里面的调用服务的参数是使用其他组件的数据值
      // 例如当传入一个参数name: "${index: '15151515155555', valueType: 'comParams'}"
      // 这个就会解析为去序号为15151515155555的组件里，拿他的comParams属性所对应的值作为name参数 的参数值
      parseParams(paramsValue) {
        // 匹配字符串以$开头的$后面的内容
        const reg = /(?<=(\$)).{1,}/g
        const objString = paramsValue.match(reg)[0]
        return JSON.parse(objString)
      },
    },
    beforeDestroy() {
      clearInterval(this.checkChart)
      window.removeEventListener('message', this.getIframeData, false)
      if (this.websocket) {
        this.websocket.close()
        this.websocket = null
      }
    },
  }
})()
