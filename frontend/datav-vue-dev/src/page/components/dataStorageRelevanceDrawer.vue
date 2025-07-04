<template>
  <el-drawer
    title="组件关联配置"
    :visible.sync="dataStorageRelevanceVisible"
    :direction="direction"
    :before-close="handleClose"
    @open="handleOpen"
  >
    <div class="data_storage_box">
      <!-- <div class="preview_btn_box">
        <el-button type="primary" icon="el-icon-view" size="mini" @click="previewData">预览</el-button>
      </div> -->
      <div>
        <span class="mode-text">配置方式</span>
        <!-- <el-select v-model="showMode" placeholder="请选择" size="mini">
          <el-option
            v-for="item in optionList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select> -->
        <el-radio-group v-model="showMode" size="mini" style="width: 200px">
          <el-radio
            v-for="item in optionList"
            :key="item.value"
            :label="item.value"
            :disabled="item.value !== hasLock"
          >
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </div>
      <p class="title">
        <span class="title_text">数据预览</span>
      </p>
      <div class="table_box" v-if="showMode === 'table'">
        <table border="1" v-if="previewTableData">
          <thead class="thead_box">
            <tr>
              <th>序号</th>
              <th
                v-for="(itemCharcode, index) in previewTableData.column"
                :key="itemCharcode.prop"
              >
                {{ index | numToCharCode }}
              </th>
            </tr>
            <tr>
              <th>0</th>
              <th
                class="th dimensionMTh"
                v-for="itemColumn in previewTableData.column"
                :key="itemColumn.prop"
                :title="itemColumn.label"
              >
                <span class="dimensionMSpan">{{ itemColumn.label }}</span>
              </th>
            </tr>
          </thead>
          <tbody class="tbody_box">
            <tr
              v-for="(item_f_data, f_index) in previewTableData.data"
              :key="f_index"
            >
              <th>{{ f_index + 1 }}</th>
              <template v-if="Array.isArray(item_f_data)">
                <td
                  v-for="(item_s_data, s_index) in item_f_data"
                  :key="s_index"
                  @click="handleTdClick(f_index, s_index)"
                >
                  {{ item_s_data }}
                </td>
              </template>
              <template v-else>
                <td
                  v-for="(item_s_data, s_key, s_index) in item_f_data"
                  :key="s_index"
                  @click="newhandleTdClick(f_index, s_index, s_key)"
                >
                  {{ item_s_data }}
                </td>
              </template>
              <!-- <td
                v-for="(item_s_data, s_index) in item_f_data"
                :key="s_index"
                @click="handleTdClick(f_index, s_index)"
              >
                {{ item_s_data }}
              </td> -->
            </tr>
          </tbody>
        </table>
        <p v-else class="tips_p">暂无数据</p>
      </div>
      <div class="tree_box" v-else>
        <el-tree
          :data="treeData"
          :props="defaultProps"
          @node-click="handleNodeClick"
        >
        <!-- 悬停显示tooltip -->
        <div slot-scope="{ node }" class="tree-node">
            <span class="over-ellipsis" :title="node.label">
                {{ node.label }}
            </span>
        </div>
      </el-tree>
      </div>
      <p class="title">
        <span class="title_text">组件配置</span>
        <span class="title_tool"
          ><i @click="addComRelevance" class="el-icon-circle-plus-outline"></i
        ></span>
      </p>
      <div class="com_relevance_box">
        <el-table :data="showTable" style="width: 100%">
          <el-table-column prop="date" label="组件名称" width="180">
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.index"
                :ref="scope.row.id"
                placeholder="请选择"
                size="mini"
                filterable
              >
                <!-- 这里的value这么写是想让如果文本框或者图片是来自tips组件那么就要把他父亲的tips的index携带上进行查找 -->
                <el-option
                  v-for="item in onlyTextAndImgList"
                  :key="item.index"
                  :label="item.name"
                  :value="
                    item.parentTipsIndex
                      ? item.index + '&&' + item.parentTipsIndex
                      : item.index
                  "
                  :disabled="getDisabled(item)"
                >
                </el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="绑定关系" width="180">
            <template slot-scope="scope">
              <el-input
                placeholder="请输入内容"
                v-model="scope.row.dataValue"
                :readonly="true"
                size="mini"
                @focus="chooseValueInput(scope.row)"
                @blur="handleBlur($event)"
              >
              </el-input>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <!-- <el-button type="danger" icon="el-icon-delete" circle size="mini" @click="deleteComRelevance(scope.$index, scope.row)"></el-button> -->
              <i
                @click="deleteComRelevance(scope.$index, scope.row)"
                class="el-icon-delete"
              ></i>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="page">
        <el-pagination
          background
          layout="prev, pager, next"
          :current-page="currentPage"
          :page-size="pageSize"
          @current-change="currentChange"
          :total="relevanceTableData.length || 0">
        </el-pagination>
      </div>
      <div class="save_box">
        <el-button type="primary" @click="saveComRelevance">保存</el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script>
import { uuid } from '@/utils/utils.min.js'
import indexOf from "xe-utils/indexOf";
import _ from 'lodash';
let createInstance = function () {
  return {
    index: '',
    dataValue: '',
    dataPosition: {
      row: '',
      column: '',
    },
    id: uuid(),
  }
}
export default {
  name: 'dataStorageRelevanceDrawer',
  props: {
    dataStorageRelevanceVisible: {
      type: Boolean,
      default: false,
    },
    activeIndex: {
      default: '',
    },
    childList: {
      type: Array,
      default: function () {
        return []
      },
    },
  },
  data() {
    return {
      pageSize:5,
      currentPage:1,
      isShowTooltip:false,
      direction: 'rtl',
      previewTableData: null,
      relevanceTableData: [createInstance()],
      showTable:[],
      childProps: {
        label: 'name',
        value: 'index',
      },
      currentRow: null,
      currentActiveObj: null,
      optionList: [
        {
          value: 'table',
          label: '表格',
        },
        {
          value: 'tree',
          label: '层级树',
        },
      ],
      showMode: 'table',
      treeData: [],
      defaultProps: {
        children: 'children',
        label: 'label',
      },
      hasLock: '',
      visibleOptions:[],
    }
  },

  created() {},
  mounted() {},
  filters: {
    numToCharCode(num) {
      let name = ''
      function getColumnName(num){
        if (num < 0) return '';
        // 65 是 'A' 的 ASCII 码
        const remainder = num % 26;
        const char = String.fromCharCode(65 + remainder);
        // 计算前导部分（如果有）
        const quotient = Math.floor(num / 26) - 1;
        if (quotient >= 0) {
          return getColumnName(quotient) + char;
        } else {
          return char;
        }
      }
      name = getColumnName(num)
      return name
    },
  },
  watch: {
    activeIndex: {
      handler(newVal, oldVal) {
        this.currentActiveObj = this.$parent.showTips
          ? this.$parent.findTipsList(newVal)
          : this.$parent.findlist(newVal)
      },
      immediate: true,
    },
  },
  computed: {
    onlyTextAndImgList() {
      let result = []
      function traverse(items) {
        items.forEach(item => {
          // 检查当前对象的component.prop是否符合条件
          if (item.component && (item.component.prop === 'text' || item.component.prop === 'img')) {
            result.push(item);
          }
          // 检查是否有list属性并递归遍历
          if (item.list && Array.isArray(item.list)) {
            traverse(item.list);
          }
        });
      }
      traverse(this.childList)
      return result
    },
  },
  methods: {
    getColumnName(num) {
      if (num < 0) return '';
      // 65 是 'A' 的 ASCII 码
      const remainder = num % 26;
      const char = String.fromCharCode(65 + remainder);
      // 计算前导部分（如果有）
      const quotient = Math.floor(num / 26) - 1;
      if (quotient >= 0) {
        return this.getColumnName(quotient) + char;
      } else {
        return char;
      }
    },
    currentChange(currentPage){
      this.currentPage = currentPage
      let startPage =  (this.currentPage-1)*this.pageSize
      let endPage = (this.currentPage-1)*this.pageSize + this.pageSize
      this.showTable = this.relevanceTableData.slice(startPage,endPage)
    },
    getDisabled(itema){
      let list = this.childList.filter(item => item.component && (item.component.prop === 'dataStorage'&&item.index!==this.currentActiveObj.index))
      let result = []
      list.forEach(item=>{
        if(item.comRelavanceList){
          result = result.concat(item.comRelavanceList)
        }
      })
      let sList = this.relevanceTableData.concat(result)
      let id = itema.parentTipsIndex ? itema.index + '&&' + itema.parentTipsIndex:itema.index
      let found = sList.some(item => item.index.includes(id));
      return found
    },
    handleClose(done) {
      this.$confirm(
        '检测到未保存的内容，是否在离开页面前保存修改？',
        '确认信息',
        {
          distinguishCancelAndClose: true,
          confirmButtonText: '保存',
          cancelButtonText: '放弃修改',
          closeOnClickModal: false,
          closeOnPressEscape: false,
        }
      )
        .then(() => {
          this.saveComRelevance()
        })
        .catch(action => {
          // 选择放弃修改前要把本来当前数据源组件的comRelavanceList中的每一项重新设置一遍，因为可能没有增加，但是删除了某一项
          // 如果不重新设置一遍那么这个组件还在与这个数据源组件关联着，导致另外一个数据源组件又可以关联该组件
          if (this.currentActiveObj.comRelavanceList) {
            this.distributeToRelevanceCom(
              this.currentActiveObj.comRelavanceList,
              true
            )
          }
          // 然后还要把当前新添加的组件关联项解除关联因为放弃了保存，如果不解除，下个数据源组件就无法选择
          this.relevanceTableData.forEach(item_a => {
            // 因为有的组件关联项会选择组件名称，有的没有选择，没有选择组件名称的index为空字符串
            // 选择了的要去和当前数据源组件的comRelavanceList查找出哪些是新添加的
            if (item_a.index) {
              let result = this.currentActiveObj.comRelavanceList.findIndex(
                item_b => item_a.index === item_b.index
              )
              // 如果没有找到说明是新添加的
              if (result == -1) {
                let relevanceCom =
                  item_a.index.split('&&').length > 1
                    ? this.$parent.findTipsList(...item_a.index.split('&&'))
                    : this.$parent.findlist(item_a.index)
                // 解除组件的绑定状态
                this.relieveRelevanceCom(relevanceCom)
              }
            }
          })
          this.$emit('update:dataStorageRelevanceVisible', false)
          done()
        })
    },
    handleOpen() {
      const itemData = this.$store.state.dataSourceList.find(
        item => item.id === this.activeIndex
      )
      // 表格数据型
      if (
        Object.prototype.toString.call(itemData) === '[object Object]' &&
        !!itemData.column
      ) {
        this.toggleLock('table')
        let data =  this.deepClone(itemData.data)
        let column = this.deepClone(itemData.column)
        let props = column.map(col => col.prop);
        let filteredData = data.map(item => _.pick(item, props));
        let tableData = {
          column:column,
          data:filteredData
        }
        this.previewTableData = this.deepClone(tableData)
        if(this.previewTableData.column){
          if(this.previewTableData.column.length===0){
            this.previewTableData = null
          }
        }
      }
      // 树形数据型
      else {
        this.toggleLock('tree')
        this.treeData = []
        if(itemData.treeData){
          this.treeData = itemData.treeData
        }
      }
      if (this.currentActiveObj.comRelavanceList) {
        this.relevanceTableData = this.deepClone(
          this.currentActiveObj.comRelavanceList
        )
      } else {
        this.relevanceTableData = [createInstance()]
      }
      // console.log('this.relevanceTableData',this.relevanceTableData)
      if (
        Object.prototype.toString.call(itemData) === '[object Object]' &&
        !!itemData.column
      ){
        if(this.relevanceTableData.length>0){
          let b
          this.relevanceTableData.forEach(item=>{
            if(!this.isRealNum(item.dataPosition.column)){
              b = this.getIindex(item.dataPosition.column)
              let name = this.getColumnName(b) + (item.dataPosition.row+1)
              item.dataValue = name
            }
          })
        }
      }
      this.currentPage = 1
      let startPage =  (this.currentPage-1)*this.pageSize
      let endPage = (this.currentPage-1)*this.pageSize + this.pageSize
      this.showTable = this.relevanceTableData.slice(startPage,endPage)
    },
    isRealNum(val){
    // isNaN()函数 把空串 空格 以及NUll 按照0来处理 所以先去除，
      if(val === "" || val ==null){
        return false;
      }
      if(!isNaN(val)){
        return true; 
      }else{ 
        return false; 
      } 
    },
    getIindex(value){
      let a 
      if(this.previewTableData.column){
        let lsit = this.previewTableData.column.map(item=>{
          return item.prop
        })
        if(lsit){
          a = indexOf(lsit,value)
        }
      }
      return a
    },
    newhandleTdClick(row, column, label){
      if (this.currentRow) {
        let name = this.getColumnName(column) + (row+1)
        const position = name
        this.currentRow.dataValue = position
        this.currentRow.dataPosition.row = row
        this.currentRow.dataPosition.column = label
        // [this.currentRow.dataPosition.row,  this.currentRow.dataPosition.column] = [row, column]
      }
    },
    handleTdClick(row, column) {
      // 判断配置组件表格是否选中了某一个绑定关系输入框
      if (this.currentRow) {
        let name = this.getColumnName(column) + (row+1)
        this.currentRow.dataValue = name
        this.currentRow.dataPosition.row = row
        this.currentRow.dataPosition.column = column
        // this.currentRow.dataPosition.column = label
        // [this.currentRow.dataPosition.row,  this.currentRow.dataPosition.column] = [row, column]
      }
    },
    // 点击表格中选择绑定关系的输入框
    chooseValueInput(row) {
      console.log('this.currentRow',row)
      this.currentRow = row
    },
    // 处理绑定关系的输入框失焦
    handleBlur(e) {},
    // 点击添加组件配置按钮
    addComRelevance() {
      this.relevanceTableData.push(createInstance())
      let startPage =  (this.currentPage-1)*this.pageSize
      let endPage = (this.currentPage-1)*this.pageSize + this.pageSize
      this.showTable = this.relevanceTableData.slice(startPage,endPage)
    },
    // 点击删除组件配置按钮
    deleteComRelevance(index, row) {
      //如果已经选择了组件名称，那么就要把这个组件的dataType设为0，且要把hasRelevance状态变为false
      if (row.index) {
        console.log(row.index)
        let relevanceCom =
          row.index.split('&&').length > 1
            ? this.$parent.findTipsList(...row.index.split('&&'))
            : this.$parent.findlist(row.index)
        // 把父tips删掉或者里面的某个组件删掉都会有问题
        if (JSON.stringify(relevanceCom) !== '{}' && relevanceCom !== '') {
          // 解除组件的绑定状态
          this.relieveRelevanceCom(relevanceCom)
        }
      }
      const indexToRemove = this.relevanceTableData.findIndex(item => item.index === row.index);
      if (indexToRemove !== -1) {
        this.relevanceTableData.splice(indexToRemove, 1);
      }
      let startPage =  (this.currentPage-1)*this.pageSize
      let endPage = (this.currentPage-1)*this.pageSize + this.pageSize
      if(startPage>=this.relevanceTableData.length){
        this.currentPage = this.currentPage-1
        if(this.currentPage===0){
          this.currentPage = 1
        }
        startPage =  (this.currentPage-1)*this.pageSize
        endPage = (this.currentPage-1)*this.pageSize + this.pageSize
      }
      this.showTable = this.relevanceTableData.slice(startPage,endPage)
    },
    // 点击保存按钮
    saveComRelevance() {
      // console.log('this.relevanceTableData', this.relevanceTableData)
      // 如果当前选择的数据源组件未保存
      this.currentActiveObj.comRelavanceList = this.relevanceTableData
      this.distributeToRelevanceCom(this.relevanceTableData, false)
      this.$emit('update:dataStorageRelevanceVisible', false)
      this.$message.success('已保存')
    },
    // 数据源组件把关联的组件的相关数据给到每一个组件
    distributeToRelevanceCom(list, isSetHasRelevance) {
      // console.log('list', list, 'isSetHasRelevance', isSetHasRelevance)
      list.forEach(item => {
        let relevanceCom =
          item.index.split('&&').length > 1
            ? this.$parent.findTipsList(...item.index.split('&&'))
            : this.$parent.findlist(item.index)
        relevanceCom.dataType = 4
        if (isSetHasRelevance) {
          relevanceCom.hasRelevance = true
        }
        if (this.showMode === 'tree') {
          console.log('item', item)
          relevanceCom.dataPosition = {
            isTreeData: true,
            ...item.treeData,
            dataStorageId: this.activeIndex,
          }
        } else {
          relevanceCom.dataPosition = {
            isTreeData: false,
            ...item.dataPosition,
            dataStorageId: this.activeIndex,
          }
        }
      })
    },
    // 解除组件的绑定状态
    relieveRelevanceCom(com) {
      com.dataType = 0
      com.hasRelevance = false
      com.dataPosition = {}
    },
    handleNodeClick(data) {
      if (this.showMode === 'table') {
        if (!data.children) {
          const { row, column } = data
          this.handleTdClick(row, column)
        }
      } else if (this.showMode === 'tree') {
        if (!data.children) {
          const { id, label } = data
          if (this.currentRow) {
            this.currentRow.dataValue = id
            this.currentRow.isTree = true
            this.currentRow.treeData = { id, label }
          }
        }
      }
    },
    // 将数据转化为树形
    toggleTreeData(data) {
      return data.column.map((columnItem, columnIndex) => ({
        id: uuid(),
        label: columnItem.label,
        children: (() =>
          data.data.map((rowItem, rowIndex) => ({
            id: uuid(),
            label: rowItem[columnIndex],
            column: columnIndex,
            row: rowIndex,
          })))(),
      }))
    },
    // 锁定
    toggleLock(status) {
      this.showMode = status
      this.hasLock = status
    },
  },
}
</script>

<style scoped lang="scss">
@import '~@/styles/buildVariables.scss';
/deep/ .el-drawer {
  background-color: $bgc2;
  .el-drawer__header {
    color: #fff;
    margin-bottom: 15px;
  }
  .el-drawer__body {
    background-color: $bgc1;
    padding-top: 20px;
  }
}
/deep/.el-tree-node:focus>.el-tree-node__content{
  background: #232630;
}
.tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  /* padding-right: 8px; */
}
.over-ellipsis {
  display: block;
  // width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  -webkit-line-clamp: 1;
}
.data_storage_box {
  width: 100%;
  height: 100%;
  padding: 0 20px;
  // background-color: pink;
  .preview_btn_box {
    margin-bottom: 10px;
  }
  .table_box {
    width: 100%;
    height: 300px;
    overflow-y: scroll;
    table {
      width: 100%;
      min-height: 300px;
      border: 1px solid #e8e2e2;
      border-top: none;
      border-left: none;
      border-collapse: collapse;
      border-spacing: 0;
      background-color: #fff;
      .thead_box {
        background-color: $bgc6;
        color: $fontc1;
        border: solid 1px $borderc1;
      }
      .tbody_box {
        background-color: $bgc5;
        color: $fontc1;
        border: solid 1px $borderc1;
      }
      .serial_number_th {
        width: 80px;
      }
      th {
        padding: 5px;
        font-size: 14px;
        text-align: center;
        // font-family: 微软雅黑;
        // font: 12px/1.5 微软雅黑;
      }
      td {
        cursor: pointer;
        text-align: center;
        white-space: nowrap;
      }
    }
  }
  .tree_box {
    width: 100%;
    height: 300px;
    overflow-y: scroll;
  }
}

.com_relevance_box {
  width: 100%;
  height: 308px;
  overflow-y: scroll;
  /deep/ .el-table {
    &::before {
      width: 0;
    }
    th,
    tr {
      background-color: transparent;
    }
    td {
      border: none;
    }
    th.is-leaf {
      border: none;
    }
  }
  /deep/.el-table--enable-row-hover {
    .el-table__body {
      tr {
        &:hover {
          td {
            background-color: $bgc2;
          }
        }
      }
    }
  }
  .el-table,
  .el-table__expanded-cell {
    background-color: transparent;
    border: none;
  }
}
.page{
  width: 100%;
  padding-right: 15px;
  display: flex;
  margin-top: 10px;
  margin-bottom: 20px;
}
::v-deep .el-tree-node.is-current > .el-tree-node__content {
  background-color: transparent !important;
}
::v-deep .el-tree-node__content:hover {
  background-color: transparent !important;
}
.title {
  margin-top: 10px;
  .title_text {
    float: left;
    font-size: 14px;
  }
  .title_tool {
    float: right;

    i {
      cursor: pointer;
      font-size: 18px;
      line-height: 35px;
    }
  }
}
.el-icon-delete {
  font-size: 18px;
  color: $fontc1;
}
.save_box {
  width: 100%;
  margin-top: 100;
  display: flex;
  justify-content: center;
}
.tips_p {
  width: 150px;
  height: 100%;
  line-height: 300px;
  margin: 0 auto;
  text-align: center;
  color: #fff;
  white-space: nowrap;
  letter-spacing: 10px;
}
.mode-text {
  color: #fff;
  margin: 0 20px 0 10px;
  font-size: 14px;
}
::v-deep .el-radio {
  width: 50px !important;
}
</style>
