<template>
  <div v-if="unionParam.type" style="height:600px;">
    <div class="field-style">
      <div class="fields">
        <p :title="unionParam.parent.currentDs.name">{{ unionParam.parent.currentDs.name }}</p>
        <union-field-list :field-list="parentField" :node="unionParam.parent" @checkedFields="changeParentFields" />
      </div>
      <div class="fields">
        <p :title="unionParam.parent.currentDs.name">{{ unionParam.node.currentDs.name }}</p>
        <union-field-list :field-list="nodeField" :node="unionParam.node" @checkedFields="changeNodeFields" />
      </div>
    </div>
    <el-divider />
    <union-item-edit :parent-field-list="parentField" :node-field-list="nodeField" :union-param="unionParam" />
  </div>
</template>

<script>
// import { post } from '@/api/dataset/dataset'
import { post } from '@/api/dataCollection'
import UnionFieldList from './UnionFieldList'
import UnionItemEdit from './UnionItemEdit'

export default {
  name: 'UnionEdit',
  components: { UnionItemEdit, UnionFieldList },
  props: {
    unionParam: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      parentField: [],
      nodeField: []
    }
  },
  watch: {
    'unionParam': function() {

    }
  },
  mounted() {
    this.getParentFieldList()
    this.getNodeFieldList()
  },
  methods: {
    getParentFieldList() {
      post('/dataset/field/list/' + this.unionParam.parent.currentDs.id, null, true).then(response => {
        this.parentField = JSON.parse(JSON.stringify(response.data)).filter(ele => ele.extField === 0)
      })
    },
    getNodeFieldList() {
      post('/dataset/field/list/' + this.unionParam.node.currentDs.id, null, true).then(response => {
        this.nodeField = JSON.parse(JSON.stringify(response.data)).filter(ele => ele.extField === 0)
      })
    },

    changeParentFields(val) {
      this.unionParam.parent.currentDsField = val
    },
    changeNodeFields(val) {
      this.unionParam.node.currentDsField = val
    }
  }
}
</script>

<style scoped>
.field-style{
  height: 300px;
}
.fields{
  box-sizing: border-box;
  -moz-box-sizing: border-box;
  -webkit-box-sizing: border-box;
  width: 50%;
  float: left;
  padding: 0 6px;
}
p{
  font-size: 14px;
  margin: 6px 0!important;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}
.el-divider--horizontal {
  margin: 12px 0;
}
</style>
