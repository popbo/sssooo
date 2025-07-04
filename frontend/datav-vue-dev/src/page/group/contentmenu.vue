<template>
  <div>
    <div
      class="contentmenu"
      ref="contentmenuRef"
      v-show="contentMenu"
      @click="contentMenu = false"
      :style="styleName"
    >
      <div class="contentmenu__item" @click="handleLock()">
        <i class="el-icon-close"></i
        >{{ contain.activeObj.lock ? '解锁' : '锁定' }}
      </div>
      <div
        class="contentmenu__item"
        @click="handleBreakUp"
        v-if="contain.activeFolder"
      >
        <i class="el-icon-folder-delete"></i>解散分组
      </div>
      <div
        class="contentmenu__item"
        @click="handleCompose()"
        v-if="!contain.activeFolder"
      >
        <i class="el-icon-folder-delete"></i>组合分组
      </div>
      <!-- <div class="contentmenu__item" @click="handleLogout()" v-if="contain.isFolder"> <i class="el-icon-close"></i>解散分组
    </div>
    <div class="contentmenu__item" @click="handleCompose()" v-if="!contain.isFolder"> <i class="el-icon-close"></i>组合分组
    </div> -->
      <div class="contentmenu__item" @click="handleDel()">
        <i class="el-icon-close"></i>删除图层
      </div>
      <div class="contentmenu__item" @click="handleCopy()">
        <i class="el-icon-document"></i>复制图层
      </div>
      <div class="contentmenu__item" @click="handleTop()">
        <i class="el-icon-arrow-up"></i>置顶图层
      </div>
      <div class="contentmenu__item" @click="handleBottom()">
        <i class="el-icon-arrow-down"></i>置底图层
      </div>
      <div class="contentmenu__item" @click="handleStepTop()">
        <i class="el-icon-arrow-up"></i>上移一层
      </div>
      <div class="contentmenu__item" @click="handleStepBottom()">
        <i class="el-icon-arrow-down"></i>下移一层
      </div>
      <div class="contentmenu__item" @click="handleCollection">
        <i class="el-icon-star-on"></i>收藏
      </div>
      <div class="contentmenu__item">
        <small style="font-size: 10px">Tip:按住Ctrl可以选择多个图层</small>
      </div>
      <div class="contentmenu__item">
        <small style="font-size: 10px">Tip:按空格可以拖拽画布</small>
      </div>
    </div>
    <collectLayer ref="collectRef" :type="'add'"></collectLayer>
  </div>
</template>

<script>
// import { createFile, uuid } from '@/utils/utils'
import { createFile, uuid } from '@/utils/utils.min.js'
import collectLayer from '@/page/group/collectLayer'
// import { List } from 'echarts'
import findTree from "xe-utils/findTree";
import uniq from "xe-utils/uniq";
export default {
  name: 'contentmenu',
  inject: ['contain'],
  components: {
    collectLayer,
  },
  data() {
    return {
      activeList: [],
      contentMenu: false,
      contentMenuX: 0,
      contentMenuY: 0,
      boxHeight: 0,
    }
  },
  computed: {
    styleName() {
      let pos = {}
      const h = window.innerHeight
      if (this.contentMenuY + this.boxHeight > h) {
        pos = {
          left: this.setPx(this.contentMenuX),
          bottom: this.setPx(10),
        }
      } else {
        pos = {
          left: this.setPx(this.contentMenuX),
          top: this.setPx(this.contentMenuY),
        }
      }
      return pos
    },
  },
  methods: {
    show(X = 0, Y = 0) {
      this.contentMenuX = X
      this.contentMenuY = Y
      this.contentMenu = true
      this.$nextTick(() => {
        this.boxHeight = this.$refs.contentmenuRef.offsetHeight
      })
    },
    hide() {
      this.contentMenuX = 0
      this.contentMenuY = 0
      this.contentMenu = false
    },
    handleStepBottom() {
      this.handleCommon(false, true)
    },
    handleStepTop() {
      this.handleCommon(true, true)
    },
    // 收藏
    handleCollection() {
      this.$refs.collectRef.openDialog()
    },
    //文件夹成组逻辑
    handleCompose() {
      let list = this.contain.active
      this.$confirm(`是否组合所选择的图层?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          let floder = createFile()
          //查找到每个组件调用核心方法就行组合操作
          //寻找父类
          let params = findTree(this.contain.nav,(item)=> item.index===list[0])
          if(params){
            if(params.parent){
              list.forEach(ele => {
                let item = findTree(this.contain.nav,(item)=> item.index===ele)
                if(item){
                  if(item.parent){
                    item.parent.children.splice(item.index, 1)
                  }else{
                    this.contain.nav.splice(item.index, 1)
                  }
                  floder.children.push(item.item)
                }
              })
              params.parent.children.push(floder)
            }else{
              list.forEach(ele => {
                let item = findTree(this.contain.nav,(item)=> item.index===ele)
                if(item){
                  if(item.parent){
                    item.parent.children.splice(item.index, 1)
                  }else{
                    this.contain.nav.splice(item.index, 1)
                  }
                    floder.children.push(item.item)
                }
              })
              this.contain.nav.push(floder)
            }
          }
        })
        .catch(() => {})
    },
    //文件夹解散逻辑
    handleLogout() {
      let ele = this.contain.activeObj
      this.$confirm(`是否解散${ele.name}图层?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          //查找到文件夹调用核心方法nav去操作
          const params = this.contain.findnav(ele.index, true)
          const list = this.deepClone(params.obj.children)
          params.parent.splice(params.count, 1)
          list.forEach(ele => {
            params.parent.push(ele)
          })
          this.contain.handleInitActive()
        })
        .catch(() => {})
    },
    // 打散文件夹的方法
    handleBreakUp() {
      let ele = this.contain.activeFolder
      this.$confirm(`是否解散${ele.name}图层?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          //查找到文件夹调用核心方法nav去操作
          const params = this.contain.findnav(ele.index, true)
          const list = this.deepClone(params.obj.children)
          params.parent.splice(params.count, 1)
          list.forEach(ele => {
            params.parent.push(ele)
          })
          this.contain.handleInitActive()
        })
        .catch(() => {})
    },
    //删除组件的方法
    handleDel() {
      this.$confirm(`是否删除所选图层?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          if (this.contain.showTips) {
            const tipsObj = this.contain.findlist(this.contain.tipsIndex) || {}
            this.deleteItem(tipsObj.list)
          } else {
            // 不传入list那么list就是undefined，那么就会使用findnav的参数默认值也即nav数组
            this.deleteItem()
          }
          this.contain.handleInitActive()
          // this.$nextTick(() => {
          //   const refList =
          //     this.$parent.$refs.container.$refs.subgroup.$children
          //   console.log('build==>refList', refList)
          // })
        })
        .catch(() => {})
    },
    /**
     * @list list表示要在哪个数组查找要删除的对象，如果是build编辑页面，那么就是this.contain.nav 如果是在tips编辑页面那就是tips组件的list
     */
    deleteItem(list) {
      const params = this.contain.findnav(this.contain.active[0], true, list)
      this.contain.active.forEach(ele => {
        const item = this.contain.findnav(ele, true, list)
        if (Array.isArray(params.parent)) {
          params.parent.splice(item.count, 1)
        } else {
          params.parent.children.splice(item.count, 1)
        }
      })
    },
    //复制组件的方法
    handleCopy() {
      if(this.$store.state.showTips){
        this.$emit('showTipsHandleCopy')
      }else{
        //寻找父类
        let nav = this.deepClone(this.contain.nav)
        let list = this.deepClone(this.contain.active)
        let active = []
        list.forEach(ele=>{
          let newItem = findTree(nav,(item)=> item.index===ele) // 保存一份数据处理当有文件夹时数据重复问题
          let item = findTree(this.contain.nav,(item)=> item.index===ele)
          if(newItem){
            if(newItem.parent){
              newItem.parent.children.splice(newItem.index, 1)
              newItem.item.index = uuid()
              if(newItem.item.children){
                this.getChildren(newItem.item.children,active)
              }else{
                active.push(newItem.item.index)
              }
              item.parent.children.unshift(newItem.item)
            }else{
              nav.splice(newItem.index, 1)
              newItem.item.index = uuid()
              if(newItem.item.children){
                this.getChildren(newItem.item.children,active)
              }else{
                active.push(newItem.item.index)
              }
              this.contain.nav.unshift(newItem.item)
            }
          }
        })
        this.$nextTick(()=>{
          active = uniq(active)
          this.contain.active = active;
          this.contain.activeIndex = null
        })
        // this.contain.handleInitActive()
      }
      
    },
    getChildren(list,active) {
      if (Array.isArray(list)) {
        list.forEach(item => {
          item.index = uuid()
          active.push(item.index)
          if (item.children) {
            this.getChildren(item.children,active)
          }
        })
      }
    },
    // 图层的上下移动方法
    handleCommon(first = false, step = false) {
      // first 用来判断图层是上移还是下移 true --> 上移  false --> 下移
      // step 用来判断是只移动一步还是直接置顶/置底 true --> 只移动一步  false ---> 置顶/置底
      // 交换数组元素
      // var swapItems = function (arr, index1, index2) {
      //   arr[index1] = arr.splice(index2, 1, arr[index1])[0]
      //   return arr
      // }
      // console.log(this.contain.active,88888)
      let nav = this.contain.showTips ? this.contain.navTips : this.contain.nav
      // let newNav = this.deepClone(nav)
      let active = this.contain.active
      let activeSelect = []
      active.forEach(aitem=>{
      let pidList = findTree(nav,(item)=> item.index===aitem)
        activeSelect.push(pidList)
      })
      let navSelect = this.deepClone(activeSelect)
      if(step){
        if(first){
          navSelect.sort((a, b) => {
            return a.index - b.index
          })
          navSelect.forEach(bitem=>{
            let pid = findTree(nav,(item)=> item.index===bitem.item.index)
            if(pid.parent){
              if(pid.index-1>=0){
                pid.parent.children.splice(pid.index,1)
                pid.parent.children.splice(pid.index-1,0,pid.item)
              }
            }else{
              if(pid.index-1>=0){
                nav.splice(pid.index,1)
                nav.splice(pid.index-1,0,pid.item)
              }
            }
          })
        }else{
          navSelect.sort((a, b) => {
            return b.index - a.index
          })
          navSelect.forEach(bitem=>{
            let pid = findTree(nav,(item)=> item.index===bitem.item.index)
            if(pid.parent){
              if(pid.index + 1 <= pid.parent.children.length){
                pid.parent.children.splice(pid.index,1)
                pid.parent.children.splice(pid.index+1,0,pid.item)
              }
            }else{
              if(pid.index + 1<= nav.length){
                nav.splice(pid.index,1)
                nav.splice(pid.index+1,0,pid.item)
              }
            }
          })
        }
      }else{
        if(first){
          navSelect.sort((a, b) => {
            return b.index - a.index
          })
          navSelect.forEach(aitem=>{
            let pid = findTree(nav,(item)=> item.index===aitem.item.index)
            if(pid.parent){
              pid.parent.children.splice(pid.index,1)
              pid.parent.children.unshift(pid.item)
            }else{
              nav.splice(pid.index,1)
              nav.unshift(pid.item)
            }
          })
        }else{
          navSelect.sort((a, b) => {
            return a.index - b.index
          })
          navSelect.forEach(aitem=>{
            let pid = findTree(nav,(item)=> item.index===aitem.item.index)
            if(pid.parent){
              pid.parent.children.splice(pid.index,1)
              pid.parent.children.push(pid.item)
            }else{
              nav.splice(pid.index,1)
              nav.push(pid.item)
            }
          })
        }
      }
    },
    handleTop() {
      this.handleCommon(true)
    },
    handleBottom() {
      this.handleCommon()
    },
    handleLock() {
      console.log(this.contain.activeObj,55555)
      this.contain.activeObj.lock = !this.contain.activeObj.lock
      this.contain.active = []
      this.contain.activeIndex = null
    },
  },
}
</script>

<style>
.contentmenu {
  width: 200px;
  position: fixed;
  z-index: 99999;
  list-style: none;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  padding: 0;
  background: #27343e;
  color: #bcc9d4;
}
.contentmenu__item {
  z-index: 10000;
  list-style: none;
  padding: 8px 12px;
  cursor: pointer;
  position: relative;
  font-size: 12px;
}
.contentmenu__item:hover {
  background-color: rgba(0, 192, 222, 0.1);
}
.contentmenu__item i {
  margin-right: 5px;
}
.contentmenu__item :first-child {
  padding-top: 5px;
}
</style>
