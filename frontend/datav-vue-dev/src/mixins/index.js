// 查看页面和编辑页面公用的参数和方法
import common from '@/config'
import { config } from '@/option/config'
import container from '@/page/group/container'
import editTips from '@/page/group/editTips'
import { getList } from '@/api/map'
import { url } from '@/config'
import findTree from "xe-utils/findTree";
export default {
  components: {
    container,
    editTips,
  },
  provide() {
    return {
      main: this,
      contain: this,
    }
  },
  data() {
    return {
      DIC: {
        MAP: [],
      },
      contentWidth: '',
      config: config,
      obj: {},
      id: '',
      visual: {},
      nav: [],
      navTips: [],
      common: common,
      active: [],
      activeIndex: null,
      overactive: '',
      historyCache: [], // 历史操作数据用于undo redo
      currentHistoryIndex: -1, // redo undo 指针
      copyNav: '', // 用于监听去干扰,字符串类型,方便比较,
      configData: ['header', 'query'],
      moveFlage:true,
      moveNoFlage:false
    }
  },
  watch: {
    nav: {
      handler(val, oldval) {
        this.recordMain(val, oldval)
      },
      deep: true,
    },
    config: {
      handler(val) {
        this.configData.concat(['url']).forEach(ele => {
          window.$glob[ele] = val[ele]
        })
      },
      deep: true,
      immediate: true,
    },
  },
  computed: {
    list() {
      let result = []
      //循环处理数据
      const detail = item => {
        if (item.children) {
          item.children.forEach(ele => {
            detail(ele)
          })
        } else {
          result.push(item)
        }
      }
      this.nav.forEach(ele => {
        detail(ele)
      })
      const len = result.length - 1
      result.forEach((ele, index) => {
        ele.zIndex = len - index
      })
      return result
    },
    listOfTips() {
      let result = []
      //循环处理数据
      const detail = item => {
        if (item.children) {
          item.children.forEach(ele => {
            detail(ele)
          })
        } else {
          result.push(item)
        }
      }
      this.navTips.forEach(ele => {
        detail(ele)
      })
      const len = result.length - 1
      result.forEach((ele, index) => {
        ele.zIndex = len - index
      })
      return result
    },
    // 能否撤销
    canUndo() {
      return this.currentHistoryIndex > 0
    },
    canRedo() {
      return this.historyCache.length > this.currentHistoryIndex + 1
    },
    showTips() {
      return this.$store.state.showTips
    },
    tipsIndex() {
      return this.$store.state.tipsIndex
    },
  },
  created() {
    // 此方法中的接口会报错所以先注释掉
    // this.initDic();
  },
  methods: {
    //初始化字典
    initDic() {
      getList({
        current: 1,
        size: 100,
      }).then(res => {
        console.log(res)
        const data = res.data.data

        this.DIC.MAP = data.records.map(ele => {
          return {
            label: ele.name,
            value: url + '/map/data?id=' + ele.id,
          }
        })
      })
    },
    findnav(id, type, navList = this.nav) {
      //循环处理数据
      let obj = void 0
      // void 0 返回undefined
      let count = 0
      let parent = void 0
      let pcount = 0
      let len = 0
      const detail = (item, list, i, number = 0) => {
        if (!item.children || type) {
          if (id === item.index) {
            obj = item
            len = Array.isArray(list)
              ? list.length - 1
              : list.children.length - 1
            // parent = list;
            // 这行之前的代码会在有分组的时候出现bug
            parent = Array.isArray(list) ? list : list.children
            pcount = number
            count = i
          }
        }
        if (item.children) {
          item.children.forEach((ele, index) => {
            detail(ele, item, index, number + 1)
          })
        }
      }
      // 把this.nav换成navLIst这样就增加了该函数的扩展性，因为新增了弹窗组件，在弹窗的编辑页面也会有复制粘贴功能用到这个方法，
      // 如果把这里单纯的写为this.nav那么只能从在大屏编辑页面下面查找，改为变量后可以接受弹窗组件的list作为查找
      navList.forEach((ele, index) => {
        detail(ele, navList, index)
      })
      return {
        obj,
        count,
        len,
        pcount,
        parent,
      }
    },
    // 根据index值查找build页面中的某个组件
    findlist(index) {
      let list = findTree(this.list,(item)=> item.index===index, { children: 'list' })
      if(list){
        return list.item || {}
      }
      return {}
    },
    // 根据index值查找弹窗中的某个子组件
    findTipsList(index, tipsIndex = this.tipsIndex) {
      let postObj = this.list.find(ele => ele.index == tipsIndex) || {}
      // 判断此时tips组件是否打开，是-走postobj寻找index，否走普通流程
      if (Array.isArray(postObj.list) && postObj.list.length > 0) {
        return postObj.list.find(ele => ele.index == index) || {}
      } else {
        return this.list.find(ele => ele.index == index) || {}
      }
    },
    handleInitActive() {
      if (this.active.isNull()) return
      this.active = []
      this.activeIndex = null
    },
    handleMouseDown() {
      this.handleInitActive()
    },
    // =====================历史纪录=======================
    // 监听调用主程序
    recordMain(val, oldval) {
      //分两种情况:
      // 1. 版本在回撤时, 不要走监听(同时啥也没改)
      // 2. 版本回撤改了呢?那也要走监听重新推新的
      // console.log('val===>', val)
      // console.log('copyNav===>', this.copyNav)
      if (JSON.stringify(val) != this.copyNav) {
        // 不一样的东西才值得记录
        // 这里监听不用deep,很多移动操作无法监听,但用了deep杂质很多,随便鼠标点一下就能触发监听
        // 注意不要这么判断 (val !== oldval)  因为很多时候新老值对比是一样的
        let newst = JSON.stringify(val)
        if (newst != this.copyNav) {
          // console.log('我不一样哦');
          if(this.moveFlage){
            this.copyNav = newst
            this.addHistoryCache(val)
          }
        } else {
          // console.log("一模一样,你记录个锤子啊!!");
          return
        }
      }
    },
    /**
     * 新增一条历史纪录
     * @param
     */
    addHistoryCache(val) {
      // console.log('this.currentHistoryIndex===>', this.currentHistoryIndex)
      // console.log('this.historyCache.length===>', this.historyCache.length)
      if (this.currentHistoryIndex + 1 < this.historyCache.length) {
        this.historyCache.splice(this.currentHistoryIndex + 1)
      }
      this.historyCache.push({
        nav: this.deepClone(val),
        // activePageUUID: this.activePageUUID,
        // activeElementUUID: this.activeElementUUID
      })
      // console.log(this.historyCache,6666)
      // 限制undo 纪录步数，最多支持100步操作undo
      if (this.historyCache.length > 100) {
        this.historyCache.splice(0, 50)
      }
      this.currentHistoryIndex++
    },
    editorUndo() {
      if (!this.canUndo) {
        return
      }
      this.currentHistoryIndex--
      this.relapceEditorState()
    },
    editorRedo() {
      if (!this.canRedo) {
        return
      }
      this.currentHistoryIndex++
      this.relapceEditorState()
    },
    /**
     * 更新编辑器项目数据，从history中拿数据替换
     * @param data
     */
    relapceEditorState() {
      const prevState = this.historyCache[this.currentHistoryIndex]
      this.moveNoFlage = true;
      this.nav = this.deepClone(prevState.nav)
      console.log(this.historyCache[this.currentHistoryIndex],7777)
      // 版本回退,本地保存一个,方便后面对比
      this.copyNav = JSON.stringify(prevState.nav)
    },
  },
}
