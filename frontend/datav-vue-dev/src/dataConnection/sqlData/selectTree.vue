<!-- 树状选择器 -->
<template>
  <el-popover
    ref="popover"
    placement="bottom-start"
    trigger="click"
    @show="onShowPopover"
    @hide="onHidePopover"
  >
    <el-tree
      class="select-tree"
      ref="datasetTreeRef"
      :default-expanded-keys="expandedArray"
      :data="tData"
      node-key="id"
      highlight-current
      :expand-on-click-node="true"
      :filter-node-method="filterNode"
      @node-click="nodeClick"
      @node-expand="nodeExpand"
      @node-collapse="nodeCollapse"
    >
      <span
        v-if="data.modelInnerType === 'group'"
        slot-scope="{ node, data }"
        class="custom-tree-node father"
      >
        <span style="display: flex; flex: 1; width: 0">
          <span>
            <i class="el-icon-folder" />
          </span>
          <span
            style="
              margin-left: 6px;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            "
            :title="data.name"
            >{{ data.name }}</span
          >
        </span>
      </span>
      <span
        v-else
        slot-scope="{ node, data }"
        class="custom-tree-node-list father"
      >
        <span style="display: flex; flex: 1; width: 0">
          <span>
            <svg-icon
              v-if="data.modelInnerType === 'db'"
              icon-class="ds-db"
              class="ds-icon-db"
            />
            <svg-icon
              v-if="data.modelInnerType === 'sql'"
              icon-class="ds-sql"
              class="ds-icon-sql"
            />
            <svg-icon
              v-if="data.modelInnerType === 'excel'"
              icon-class="ds-excel"
              class="ds-icon-excel"
            />
            <svg-icon
              v-if="data.modelInnerType === 'custom'"
              icon-class="ds-custom"
              class="ds-icon-custom"
            />
            <svg-icon
              v-if="data.modelInnerType === 'union'"
              icon-class="ds-union"
              class="ds-icon-union"
            />
          </span>
          <span
            v-if="data.modelInnerType === 'db' || data.modelInnerType === 'sql'"
          >
            <span v-if="data.mode === 0" style="margin-left: 6px"
              ><i class="el-icon-s-operation"
            /></span>
            <span v-if="data.mode === 1" style="margin-left: 6px"
              ><i class="el-icon-alarm-clock"
            /></span>
          </span>
          <span
            style="
              margin-left: 6px;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            "
            :title="data.name"
            >{{ data.name }}</span
          >
        </span>
      </span>
    </el-tree>
    <el-input
      slot="reference"
      ref="input"
      v-model="labelModel"
      clearable
      :style="`width: ${width}px`"
      :class="{ rotate: showStatus }"
      suffix-icon="el-icon-arrow-down"
      :placeholder="placeholder"
    >
    </el-input>
  </el-popover>
</template>

<script>
import { queryAuthModel } from "@/api/dataCollection.js";
export default {
  name: "selectTree",
  props: {
    sqlId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      // 树状菜单显示状态
      showStatus: false,
      // 菜单宽度
      treeWidth: "auto",
      // 输入框显示值
      labelModel: "",
      // 实际请求传值
      valueModel: "0",
      props: {
        parent: "parentId", // 父级唯一标识
        value: "id", // 唯一标识
        label: "label", // 标签显示
        children: "children", // 子级
      },
      tData: [],
      expandedArray: [], // 默认展开的节点的 key 的数组
    };
  },
  watch: {
    labelModel(val) {
      if (!val) {
        // 如果清空了数据集的值，也要把父组件中form的dataCollectObj的值变为null，要不然无法校验
        this.$emit("chooseDataColection", null);
        this.$refs.datasetTreeRef.setCheckedKeys([]);
        this.$refs.datasetTreeRef.setCurrentKey(null)
      }
    },
    // 监听父组件传递过来的sqlId
    // sqlId(newVal, oldVal) {
    //   console.log('newValue==>', newVal)
    //   console.log('oldValue==>', oldVal)
    //   if (newVal === '') {
    //     this.labelModel = ''
    //     this.$refs.datasetTreeRef.setCheckedKeys([])
    //   } else {
    //     this.$refs.datasetTreeRef.setCheckedKeys([newVal])
    //     const node = this.$refs.datasetTreeRef.getCheckedNodes()[0]
    //     console.log(node)
    //     this.nodeClick(node)
    //   }
    // },
    sqlId: {
      handler(newVal, oldVal) {
        if (newVal === "") {
          this.labelModel = "";
          this.$refs.datasetTreeRef.setCheckedKeys([]);
          this.$refs.datasetTreeRef.setCurrentKey(null)
        } else {
          this.$refs.datasetTreeRef.setCheckedKeys([newVal]);
          this.$refs.datasetTreeRef.setCurrentKey(newVal)
          // 当只有一个子时，选中会返回父元素
          const listNode = this.$refs.datasetTreeRef.getCheckedNodes();
          const node = listNode[listNode.length-1];
          // 默认展开该节点属于的父节点
          this.expandedArray.push(node.pid);
          if (!node.children) {
            this.labelModel = node[this.props.label];
            this.valueModel = node[this.props.value];
            // 传递给父组件
            // this.$emit('chooseDataColection', node)
            // 代码改进，因为并不需要整个数据集对象，所以把数据集的id传过去就可以了
            this.$emit("chooseDataColection", {id: node.id, isClean: false});
            this.onCloseTree();
          }
        }
      },
    },
  },

  created() {
    this.$nextTick(() => {
      this.treeWidth = `${
        (this.width || this.$refs.input.$refs.input.clientWidth) - 24
      }px`;
    });
  },
  mounted() {
    this.treeNode(true).then(() => {
      this.$nextTick(() => {
        if (this.sqlId) {
          this.$refs.datasetTreeRef.setCheckedKeys([this.sqlId]);
          this.$refs.datasetTreeRef.setCurrentKey(this.sqlId)
          // 当只有一个子时，选中会返回父元素
          const listNode = this.$refs.datasetTreeRef.getCheckedNodes();
          const node = listNode[listNode.length-1];
          // 默认展开该节点属于的父节点
          this.expandedArray.push(node.pid);
          if (!node.children) {
            this.labelModel = node[this.props.label];
            this.valueModel = node[this.props.value];
            // 传递给父组件
            // this.$emit('chooseDataColection', node)
            // 代码改进，因为并不需要整个数据集对象，所以把数据集的id传过去就可以了
            this.$emit("chooseDataColection", {id: node.id, isClean: false});
            this.onCloseTree();
          }
        }
      });
    });
  },
  methods: {
    nodeExpand(data) {
      console.log("chufale  nodeExpand", data);
      if (data.id) {
        this.expandedArray.push(data.id);
      }
    },
    nodeCollapse(data) {
      if (data.id) {
        this.expandedArray.splice(this.expandedArray.indexOf(data.id), 1);
      }
    },
    // 单击节点
    nodeClick(node) {
      if (!node.children) {
        this.labelModel = node[this.props.label];
        this.valueModel = node[this.props.value];
        // 传递给父组件
        // this.$emit('chooseDataColection', node)
        // 代码改进，因为并不需要整个数据集对象，所以把数据集的id传过去就可以了
        this.$emit("chooseDataColection", {id: node.id, isClean: true});
        this.onCloseTree();
      }
    },
    // 隐藏树状菜单
    onCloseTree() {
      this.$refs.popover.showPopper = false;
    },
    // 显示时触发
    onShowPopover() {
      this.showStatus = true;
      this.$refs.datasetTreeRef.filter(false);
    },
    // 隐藏时触发
    onHidePopover() {
      this.showStatus = false;
      this.$emit("selected", this.valueModel);
    },
    // 树节点过滤方法
    filterNode(query, data) {
      if (!query) return true;
      return data[this.props.label].indexOf(query) !== -1;
    },
    treeNode(cache) {
      return new Promise((resolve, reject) => {
        const modelInfo = localStorage.getItem("dataset-tree");
        const userCache = modelInfo && modelInfo != "undefined" && cache;
        if (userCache) {
          this.tData = JSON.parse(modelInfo);
          resolve();
        }
        queryAuthModel({ modelType: "dataset" }, !userCache).then((res) => {
          localStorage.setItem("dataset-tree", JSON.stringify(res.data.data));
          if (!userCache) {
            this.tData = res.data.data;
            resolve();
          }
        });
      });
    },
    // 根据已有的sqlId查询出数据中对应的节点
  },
};
</script>

<style scoped lang="scss">
.el-popover__reference {
  /deep/ .el-input__inner {
    height: 28px;
    line-height: 28px;
  }
}
/deep/.el-popover__reference-wrapper {
  .el-input__inner {
    height: 28px;
    line-height: 28px;
  }
}
.el-input.el-input--suffix {
  cursor: pointer;
  overflow: hidden;
}
.el-input.el-input--suffix.rotate .el-input__suffix {
  transform: rotate(180deg);
}
.select-tree {
  min-width: 280px;
  max-height: 350px;
  overflow-y: scroll;
}
/* 菜单滚动条 */
.select-tree::-webkit-scrollbar {
  z-index: 11;
  width: 6px;
}
.select-tree::-webkit-scrollbar-track,
.select-tree::-webkit-scrollbar-corner {
  background: #fff;
}
.select-tree::-webkit-scrollbar-thumb {
  border-radius: 5px;
  width: 6px;
  background: #b4bccc;
}
.select-tree::-webkit-scrollbar-track-piece {
  background: #fff;
  width: 6px;
}
.tree-list >>> .el-tree-node__expand-icon.is-leaf {
  display: none;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.custom-tree-node-list {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding: 0 8px;
}
.father .child {
  visibility: hidden;
}
.father:hover .child {
  visibility: visible;
}
.tree-style {
  padding: 10px 15px;
  height: 100%;
  overflow-y: auto;
}
</style>
