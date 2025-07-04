<template>
  <div class="layer_box">
    <!-- <el-input
      class="search_input"
      placeholder="请输入图层名称"
      prefix-icon="el-icon-search"
      v-model.trim="serachVal"
      @input="handleInput"
      size="mini">
  </el-input> -->
    <slot name="header"></slot>
    <draggable
      :group="{ name: 'form' }"
      ghost-class="ghost"
      :list="searchNav"
      :animation="300"
      :disabled="canDraggable"
      class="com_list_box"
    >
      <template v-for="item in searchNav">
        <div
          :key="item.index"
          class="menu__folder"
          @click.stop="handleSetActive(item)"
          v-if="item.children"
        >
          <div
            @dblclick="hangeChangeName(item)"
            @contextmenu.prevent="
              contain.handleContextMenu &&
                contain.handleContextMenu($event, item)
            "
            :class="[
              'menu__item--folder',
              {
                'is-active': handleGetActive(item),
                'is-over': contain.overactive === item.index,
              },
            ]"
          >
            <i
              class="iconfont icon-fold"
              @click.stop="openFolder(item)"
              :class="{ 'is-active': item.menu }"
            ></i>
            <i
              class="iconfont icon-folder"
              @click.stop="handleSetActive(item)"
            ></i>
            <input
              type="text"
              @keyup.enter="item.isname = false"
              v-if="item.isname"
              v-model="item.name"
            />
            <span v-else @click.stop="handleSetActive(item)">{{
              item.name
            }}</span>
          </div>
          <div :key="'list' + item.index" class="menu__list" v-show="item.menu">
            <draggable
              :group="{ name: 'form' }"
              ghost-class="ghost"
              :list="item.children"
              :animation="300"
            >
              <template v-for="citem in item.children">
                <div
                  v-if="!citem.children"
                  :key="citem.index"
                  :class="[
                    'menu__item',
                    {
                      'is-active': handleGetActive(citem),
                      'is-over': contain.overactive === citem.index,
                    },
                  ]"
                  @click.stop="handleSetActive(citem)"
                  @contextmenu.prevent="
                    contain.handleContextMenu &&
                      contain.handleContextMenu($event, citem)
                  "
                  @mouseover="contain.overactive = citem.index"
                  @mouseout="contain.overactive = undefined"
                >
                  <span class="menu__icon">
                    <i :class="'iconfont ' + citem.icon"></i>
                  </span>
                  <el-tooltip
                    popper-class="name_tip"
                    effect="dark"
                    :content="citem.name"
                    placement="top-start"
                  >
                    <span
                      class="menu_name"
                      @click.stop="handleSetActive(citem)"
                      >{{ citem.name }}</span
                    >
                  </el-tooltip>
                </div>
                <layer
                  v-else
                  :count="count + 1"
                  :key="citem.index"
                  :nav="[citem]"
                ></layer>
              </template>
            </draggable>
          </div>
        </div>
        <div
          v-else
          :key="item.index"
          @contextmenu.prevent="
            contain.handleContextMenu && contain.handleContextMenu($event, item)
          "
          @click.stop="handleSetActive(item)"
          :class="[
            'menu__item',
            {
              'is-active': handleGetActive(item),
              'is-over': contain.overactive === item.index,
            },
          ]"
          @mouseover="contain.overactive = item.index"
          @mouseout="contain.overactive = undefined"
        >
          <span class="menu__icon">
            <i :class="'iconfont ' + item.icon"></i>
          </span>
          <el-tooltip
            popper-class="name_tip"
            effect="dark"
            :content="item.name"
            placement="top-start"
          >
            <span class="menu_name" @click.stop="handleSetActive(item)">{{
              item.name
            }}</span>
          </el-tooltip>
        </div>
      </template>
    </draggable>
  </div>
</template>

<script>
import vuedraggable from 'vuedraggable'
export default {
  name: 'layer',
  inject: ['contain'],
  provide() {
    return {
      contain: this.contain,
    }
  },
  components: {
    draggable: vuedraggable,
  },
  props: {
    count: {
      type: Number,
      default: 1,
    },
    nav: {
      type: Array,
      default: () => {
        return []
      },
    },
    serachVal: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      list: [],
      selectList:[],
      // serachVal: '',
    }
  },
  computed: {
    searchNav() {
      if (this.serachVal !== '') {
        return this.filterTree(this.nav, this.serachVal)
      } else {
        return this.nav
      }
    },
    canDraggable() {
      if (this.serachVal !== '') {
        return true
      } else {
        return false
      }
    },
  },
  // watch: {
  //   searchNav: {
  //     handler(newValue) {
  //       this.$bus.$emit('componentList', newValue)
  //     },
  //     immediate: true,
  //     deep: true,
  //   },
  // },
  methods: {
    filterTree(arr, keyword, first = true) {
      if (first) {
        //首次传入深度克隆数据防止修改源数据
        arr = JSON.parse(JSON.stringify(arr))
      }
      let emptyArr = []
      for (let item of arr) {
        if (item.name.includes(keyword)) {
          if (
            item.children &&
            Array.isArray(item.children) &&
            item.children.length > 0
          ) {
            item.children = this.filterTree(item.children, keyword, false)
          }
          if (item.children) {
            if (item.children.length > 0) {
              item.menu = true
            }
          }
          emptyArr.push(item)
        } else if (
          item.children &&
          Array.isArray(item.children) &&
          item.children.length > 0
        ) {
          item.children = this.filterTree(item.children, keyword, false)
          if (item.children.length > 0) {
            item.menu = true
            emptyArr.push(item)
          }
        }
      }
      return emptyArr
    },
    handleGetActive(item) {
      return this.contain.active.includes(item.index)
    },
    handleSetActive(item) {
      console.log('文件夹', item)
      if (item.children) {
        this.selectList = []
        this.getListchildren(item.children)
        this.selectList.unshift(item.index)
        this.contain.selectNav(this.selectList)
        this.contain.activeFolder = item
      } else {
        if (this.contain.activeFolder) this.contain.activeFolder = null
        this.contain.selectNav(item.index)
      }
    },
    getListchildren(list){
      list.forEach(item=>{
        this.selectList.push(item.index)
        if(item.children){
          this.getListchildren(item.children)
        }
      })
    },
    hangeChangeName(item) {
      this.$set(item, 'isname', !item.isname)
    },
    openFolder(item) {
      this.$set(item, 'menu', !item.menu)
      item.isname = false
    },
    handleInput(val) {},
  },
}
</script>

<style lang="scss">
.layer_box {
  height: calc(100% - 30px);
}
.com_list_box {
  height: calc(100% - 28px);
  overflow-y: auto;
}
.span-foot {
  flex: 1;
  height: 18px;
}
// .search_input {
//   position: fixed;
// }
.menu_name {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.name_tip {
  max-width: 200px;
}
</style>
