<template>
  <el-col style="height: 400px;overflow-y: auto;margin-bottom: 10px;">
    <el-tree
      :data="tData"
      node-key="id"
      :expand-on-click-node="false"
      highlight-current
      @node-click="nodeClick"
    >
      <span slot-scope="{ node, data }" :class="treeClass(data,node)">
        <span style="display: flex;flex: 1;width: 0;">
          <span v-if="data.type === 'scene'">
            <svg-icon icon-class="scene" class="ds-icon-scene" />
          </span>
          <span style="margin-left: 6px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;" :title="data.name">{{ data.name }}</span>
        </span>
      </span>
    </el-tree>
  </el-col>
</template>

<script>
// import { groupTree } from '@/api/dataset/dataset'
import { groupTree } from '@/api/dataCollection.js'
export default {
  name: 'GroupMoveSelector',
  props: {
    item: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      tData: [],
      currGroup: '',
      groupForm: {
        name: '',
        pid: '0',
        level: 0,
        type: 'group',
        children: [],
        sort: 'type desc,name asc'
      },
      targetGroup: {}
    }
  },
  watch: {
    'item': function() {
      this.tree(this.groupForm)
    }
  },
  mounted() {
    this.tree(this.groupForm)
  },
  methods: {
    tree(group) {
      groupTree(group).then(res => {
        this.tData = [{
          id: '0',
          name: '数据集分组',
          pid: '0',
          privileges: 'grant,manage,use',
          type: 'group',
          children: res.data.data
        }]
      })
    },
    nodeClick(data, node) {
      this.targetGroup = data
      this.$emit('targetGroup', data)
    },
    treeClass(data, node) {
      if (data.id === this.item.id) {
        node.visible = false
      }
      return 'custom-tree-node'
    }
  }
}
</script>

<style scoped lang="scss">
  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right:8px;
  }
  
</style>
