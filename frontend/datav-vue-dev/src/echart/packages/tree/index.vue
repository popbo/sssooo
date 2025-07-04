<template>
  <div :class="b()" :style="styleSizeName">
    <div style="height: 100%" :class="option.nodeCheak ? '' : 'nodeCheakTree'">
      <el-input
        v-if="this.option.showSelect"
        placeholder="输入搜索的内容..."
        v-model="filterText"
        style="height: 13%"
      >
      </el-input>
      <el-tree
        v-if="treeFalge"
        class="filter-tree"
        :data="this.option.onlyRoot ? rootData : dataChart"
        :props="defaultProps"
        node-key="id"
        show-checkbox
        :default-expanded-keys="treeKeys"
        :filter-node-method="filterNode"
        @check-change="handleNodeClick"
        :render-content="renderFun"
        ref="tree"
        :style="[styleName, styleAlgin]"
      >
      </el-tree>
    </div>
  </div>
</template>

<script>
// this.option.onlyRoot ? rootData :
import create from '../../create'
export default create({
  name: 'tree',
  inject: ['main'],
  data() {
    return {
      treeFalge: true,
      rootData: [],
      treeNum: 1,
      treeKeys: [],
      filterText: '',
      defaultProps: {
        children: 'children',
        label: 'label',
      },
    }
  },
  created() {},

  computed: {
    x2() {
      return this.option.gridX2 || 20
    },
    styleName() {
      const obj = Object.assign(
        {
          // nodeCheak: this.option.nodeCheak,
          // showSelect: this.option.showSelect,
          // onlyRoot: this.option.onlyRoot,
          borderColor: this.option.borderColor || '#309EF8',
          borderStyle: this.option.borderStyle || 'solid',
          borderWidth: this.setPx(this.option.borderWidth || 0),
          background: this.option.bgColor,
          height: '87%',
          overflow: 'auto',
        }
        // (() => {
        //   this.treeKeys = [];
        //   this.getValue(this.option.treeNum, this.data);
        // })()
      )
      return obj
    },
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val)
    },
    dataChart: {
      handler() {
        if (this.option.onlyRoot) {
          this.rootNode(this.dataChart)
        }
      },
      deep: true,
    },
    ignoreCase: {
      handler() {
        this.filterNode()
      },
      deep: true,
    },
    'option.treeNum': {
      handler(val) {
        if (this.$route.name === 'build') {
          this.treeFalge = false
          this.treeNum = 1
          this.treeKeys = []
          this.getValue(val, this.data)
          setTimeout(() => {
            this.treeFalge = true
          })
        }
        if (this.$route.name === 'view' || this.$route.name === 'share' ) {
          this.treeNum = 1
          this.treeKeys = []
          this.getValue(val, this.data)
        }
      },
      deep: true,
      immediate: true,
    },
  },

  methods: {
    renderFun(h, { node, data, store }) {
      return h(
        'span',
        {
          style: {
            width: '100%',
            textAlign: this.option.flexAlign || 'left',
            fontFamily: this.option.fontFamily,
            fontWeight: this.option.fontWeight,
            fontSize: this.setPx(this.option.fontSize || 16),
            paddingRight: '10px',
            color: this.option.color,
          },
        },
        node.label
      )
    },
    filterNode(value, data) {
      if (!value) {
        return true
      } else {
        if (this.option.ignoreCase) {
          return data.label.indexOf(value) !== -1
        } else {
          return data.label.toUpperCase().indexOf(value.toUpperCase()) !== -1
        }
      }
    },
    rootNode(dataChart) {
      this.rootData = []
      let a = {
        label: 'root',
        id: 'root',
        children: [],
      }
      a['children'] = dataChart
      this.rootData.push(a)
    },

    handleNodeClick(data, checked, node) {
      if (this.option.nodeCheak == false) {
        if (checked === true) {
          this.checkedId = data.id
          this.$refs.tree.setCheckedKeys([data.id])
        } else {
          if (this.checkedId == data.id) {
            this.$refs.tree.setCheckedKeys([data.id])
          }
        }
      }
    },
    //获取层级
    getValue(num, a) {
      if (this.option.onlyRoot) {
        this.treeNum = this.treeNum + 1
        if (num < 1) {
          this.treeKeys = []
        } else if (num < 2 && num >= 1) {
          this.treeKeys = ['root']
        } else {
          for (let i = 0; i < a.length; i++) {
            this.treeKeys.push(a[i].id)
            if (this.treeNum < num) {
              if (a[i].children) {
                this.getValue(num, a[i].children)
              }
            }
          }
        }
      } else {
        this.treeNum = this.treeNum + 1
        if (num < 1) {
          this.treeKeys = []
        } else {
          for (let i = 0; i < a.length; i++) {
            this.treeKeys.push(a[i].id)
            if (this.treeNum <= num) {
              if (a[i].children) {
                this.getValue(num, a[i].children)
              }
            }
          }
        }
      }
    },
    capsValue(boolean) {
      this.caps = boolean
    },
  },
})
</script>
<style scoped lang="scss">
/deep/input {
  background-color: #263e58 !important;
  border-color: #2c7bbf !important;
  color: #2c7bbf !important;
  height: 100%;
}

.nodeCheakTree {
  /deep/.el-tree-node {
    .is-leaf + .el-checkbox .el-checkbox__inner {
      display: inline-block;
    }
    .el-checkbox .el-checkbox__inner {
      display: none;
    }
  }
}
// 不可全选样式
/deep/.el-tree-node:hover > .el-tree-node__content {
  background-color: #263e58 !important;
}
/deep/.el-tree-node:focus > .el-tree-node__content {
  background-color: #263e58 !important;
}
// /deep/ .el-checkbox__input .el-checkbox__inner{
//    background-color:  #fff !important
// }
/deep/.el-checkbox__input.is-focus .el-checkbox__inner {
  background-color: #e68f00 !important;
}
</style>
