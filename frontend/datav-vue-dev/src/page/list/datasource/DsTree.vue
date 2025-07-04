<template>
  <el-col class="tree-style">
    <el-col>
      <el-row class="title-css">
        <span class="title-text"> 数据源 </span>
        <div class="set-group">
          <el-button
            icon="el-icon-setting"
            size="mini"
            class="set-btns"
            @click="handleDriveManage"
          />
          <el-button
            v-permission="['datasource:add']"
            icon="el-icon-plus"
            type="text"
            size="mini"
            class="set-btns"
            @click="addFolder"
          />
        </div>
      </el-row>
      <el-divider />
      <el-row>
        <!-- 当表单只有一个input控件时按下enter会触发form表单的提交事件，因此加一个@submit.native.prevent -->
        <el-form @submit.native.prevent>
          <el-form-item class="form-item">
            <el-input
              v-model="key"
              size="mini"
              :placeholder="'搜索'"
              prefix-icon="el-icon-search"
              clearable
              class="main-area-input"
            />
            <!-- <el-input v-model="my" /> -->
          </el-form-item>
        </el-form>
      </el-row>

      <el-col class="custom-tree-container">
        <div class="block">
          <el-tree
            ref="myDsTree"
            :default-expanded-keys="expandedArray"
            :data="tData"
            node-key="id"
            :expand-on-click-node="true"
            :filter-node-method="filterNode"
            @node-click="nodeClick"
          >
            <span slot-scope="{ data }" class="custom-tree-node-list father">
              <span style="display: flex; flex: 1; width: 0">
                <span v-if="data.type === 'folder'">
                  <i class="el-icon-folder" />
                </span>
                <span
                  v-if="data.status === 'Error'"
                  style="
                    margin-left: 6px;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                  "
                >
                  <el-tooltip
                    effect="dark"
                    :content="'datasource.in_valid'"
                    placement="right"
                  >
                    <span>
                      {{ data.name }}
                    </span>
                  </el-tooltip>
                </span>
                <span
                  v-if="data.status !== 'Error'"
                  style="
                    margin-left: 6px;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                  "
                >
                  {{ data.name }}
                </span>
              </span>
              <span class="child">
                <span v-if="data.type === 'folder'" @click.stop>
                  <span class="el-dropdown-link">
                    <el-button
                      icon="el-icon-plus"
                      type="text"
                      size="small"
                      @click="addFolderWithType(data)"
                    />
                  </span>
                </span>
                <span
                  v-if="data.type !== 'folder'"
                  style="margin-left: 12px"
                  @click.stop
                >
                  <el-dropdown size="small" trigger="click">
                    <span class="el-dropdown-link">
                      <!-- <span class="el-icon-s-fold"> </span> -->
                      <el-button icon="el-icon-more" type="text" size="small" />
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item
                        icon="el-icon-copy-document"
                        @click.native="_handleCopy(data)"
                        >复制</el-dropdown-item
                      >
                      <el-dropdown-item
                        icon="el-icon-delete"
                        @click.native="_handleDelete(data)"
                        >删除</el-dropdown-item
                      >
                    </el-dropdown-menu>
                  </el-dropdown>
                  <!-- <span class="el-dropdown-link">
                    <el-button
                      icon="el-icon-delete"
                      type="text"
                      size="small"
                      @click="_handleDelete(data)"
                    />
                  </span> -->
                </span>
              </span>
            </span>
          </el-tree>
        </div>
      </el-col>
    </el-col>
  </el-col>
</template>
<script>
// import {listDatasource, listDatasourceByType, delDs} from '@/api/system/datasource'
import { getDBsourceList, delDs } from '@/api/newdb.js'
import { deepClone } from '../../../echart/util'
export default {
  name: 'DsTree',
  props: {
    datasource: {
      type: Object,
      default: null,
    },
    dMVisible: Boolean,
  },
  data() {
    return {
      expandedArray: [],
      tData: [],
      showSearchInput: false,
      key: '',
      driveManageVisible: false,
    }
  },
  watch: {
    key(val) {
      this.$refs.myDsTree.filter(val)
    },
  },
  created() {},
  mounted() {
    this.queryTreeDatas()
  },
  methods: {
    filterNode(value, data) {
      if (!value) return true
      return data.name.indexOf(value) !== -1
    },
    showSearchWidget() {
      this.showSearchInput = true
    },
    closeSearchWidget() {
      this.key = ''
      this.showSearchInput = false
    },
    async queryTreeDatas() {
      // listDatasource().then((res) => {
      //   this.tData = this.buildTree(res.data)
      // })
      const res = await getDBsourceList()
      const tData = this.buildTree(res.data.data)
      this.tData = tData
    },
    refreshType(datasource) {
      // let typeData = []
      // listDatasourceByType(datasource.type).then((res) => {
      //   typeData = this.buildTree(res.data)
      //   for (let index = 0; index < this.tData.length; index++) {
      //     if (typeData[0].id === this.tData[index].id) {
      //       this.tData[index].children = typeData[0].children
      //     }
      //   }
      // })
      this.queryTreeDatas()
    },
    buildTree(array) {
      const types = {}
      const newArr = []
      for (let index = 0; index < array.length; index++) {
        const element = array[index]
        // debugger;
        if (this.msgNodeId) {
          if (element.id === this.msgNodeId) {
            element.msgNode = true
          }
        }
        if (!(element.type in types)) {
          types[element.type] = []
          // newArr.push(...element, ...{ children: types[element.type] })
          newArr.push({
            id: element.type,
            name: this.transTypeToName(element.type),
            type: 'folder',
            children: types[element.type],
          })
        }
        types[element.type].push(element)
        // newArr.children.push({ id: element.id, label: element.name })
      }
      return newArr
    },

    transTypeToName(type) {
      if (type === 'mysql') {
        return 'MySQL'
      } else if (type === 'sqlServer') {
        return 'SQL Server'
      } else if (type === 'oracle') {
        return 'Oracle'
      } else if (type === 'pg') {
        return 'PostgreSQL'
      } else if (type === 'es') {
        return 'Elasticsearch'
      } else if (type === 'ck') {
        return 'ClickHouse'
      } else if (type === 'mariadb') {
        return 'MariaDB'
      } else if (type === 'ds_doris') {
        return 'Doris'
      } else if (type === 'mongo') {
        return 'MongoDB'
      } else if (type === 'redshift') {
        return 'AWS Redshift'
      } else if (type === 'hive') {
        return 'Apache Hive'
      } else if (type === 'gbase') {
        return 'Gbase'
      } else if (type === 'db2') {
        return 'DB2'
      } else if (type === 'dm') {
        return 'DM'
      } else if (type === 'kingbase') {
        return '人大金仓'
      }else if (type === 'tdengine') {
        return 'TDengine'
      }
    },

    addFolder() {
      this.switchMain('DsForm')
    },
    addFolderWithType(data) {
      this.switchMain('DsForm', { type: data.id })
    },
    nodeClick(node, data) {
      if (node.type === 'folder') return
      this.showInfo(data)
    },

    clickFileMore(param) {
      const { optType, data } = param
      switch (optType) {
        case 'edit':
          this.edit(data)
          break
        case 'delete':
          this._handleDelete(data)
          break
        default:
          break
      }
    },
    beforeClickFile(optType, data, node) {
      return { optType, data, node }
    },
    edit(row) {
      this.switchMain('DsForm', row)
    },
    showInfo(row) {
      const param = { ...row.data, ...{ showModel: 'show' } }
      // console.log(param)
      this.switchMain('DsForm', param)
    },
    _handleDelete(datasource) {
      this.$confirm('确定要删除该数据源吗？', '', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          delDs(datasource.id).then(res => {
            if (res.data.code === 200) {
              this.$message.success('删除成功')
              this.switchMain('DataHome')
              this.refreshType(datasource)
            } else {
              this.$message.error('删除失败：' + res.data.msg)
            }
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: this.$t('commons.delete_cancelled'),
          })
        })
    },
    _handleCopy(dataSource) {
      let copyData = deepClone(dataSource)
      delete copyData.id
      copyData.isCopy = true
      copyData = {
        ...copyData,
        configuration: JSON.parse(copyData.configuration),
      }
      this.switchMain('DsForm', copyData)
    },
    switchMain(component, componentParam) {
      this.$emit('switch-main', {
        component,
        componentParam,
      })
    },
    markInvalid(msgParam) {
      const param = JSON.parse(msgParam)
      const msgNodeId = param.id
      this.msgNodeId = msgNodeId
      this.$nextTick(() => {
        this.tData.forEach(folder => {
          const nodes = folder.children
          nodes.forEach(node => {
            if (node.id === msgNodeId) {
              node.msgNode = true
            }
          })
        })
      })
    },
    hasDataPermission() {
      return true
    },
    handleKeyup() {
      console.log('enter')
    },
    goDriveManage() {},
    handleDriveManage() {
      console.log(111)
      this.$emit('update:dMVisible', true)
    },
  },
}
</script>
<style lang="scss" scoped>
.el-tree {
  background-color: transparent;
}
.el-divider--horizontal {
  margin: 12px 0;
  background-color: #293241;
}

.search-input {
  padding: 12px 0;
}

.custom-tree-container {
  margin-top: 10px;
  .block {
    margin: 0;
  }
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
.el-tree-node:focus > .el-tree-node__content {
  background-color: #192639;
}

.el-tree {
  /deep/ .el-tree-node__content {
    background-color: transparent;
    &:hover {
      background-color: #192639;
      color: #257bf2;
    }
  }
}
/deep/ .el-tree .el-tree-node.is-current > .el-tree-node__content {
  background-color: #192639;
}
.custom-tree-node-list {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding: 0 8px;
  color: #bfbfbf;
}

.tree-list > .el-tree-node__expand-icon.is-leaf {
  display: none;
}

.custom-position {
  flex: 1;
  display: flex;
  align-items: center;
  font-size: 14px;
  flex-flow: row nowrap;
}

.form-item {
  margin-bottom: 0;
}

.title-css {
  height: 26px;
}

.title-text {
  line-height: 26px;
  color: #bfbfbf;
}

.dialog-css > .el-dialog__header {
  padding: 20px 20px 0;
}

.dialog-css > .el-dialog__body {
  padding: 10px 20px 20px;
}

.form-item > .el-form-item__label {
  font-size: 12px;
}

.scene-title {
  width: 100%;
  display: flex;
}

.scene-title-name {
  width: 100%;
  overflow: hidden;
  display: inline-block;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.father .child {
  /*display: none;*/
  visibility: hidden;
}

.father:hover .child {
  /*display: inline;*/
  visibility: visible;
}

.tree-style {
  padding: 10px 15px;
  height: 100%;
  overflow-y: auto;
}

.msg-node-class {
  color: red;

  > i {
    color: red;
  }
}
.set-group {
  display: flex;
  float: right;
}
.set-btns {
  border: none;
}
// .el-divider--horizontal {
//   margin: 12px 0;
//   background-color: #293241;
// }

// .search-input {
//   padding: 12px 0;
// }

// .custom-tree-container {
//   margin-top: 10px;
//   .block {
//     margin: 0;
//   }
// }

// .custom-tree-node {
//   flex: 1;
//   display: flex;
//   align-items: center;
//   justify-content: space-between;
//   font-size: 14px;
//   padding-right: 8px;
// }
// .el-tree-node:focus > .el-tree-node__content {
//   background-color: #192639;
// }

// .el-tree {
//   /deep/ .el-tree-node__content {
//     background-color: transparent;
//     &:hover {
//       background-color: #192639;
//       color: #257bf2;
//     }
//   }
// }
// /deep/ .el-tree .el-tree-node.is-current > .el-tree-node__content {
//   background-color: #192639;
// }
// .custom-tree-node-list {
//   flex: 1;
//   display: flex;
//   align-items: center;
//   justify-content: space-between;
//   font-size: 14px;
//   padding: 0 8px;
//   color: #bfbfbf;
// }

// .tree-list > .el-tree-node__expand-icon.is-leaf {
//   display: none;
// }

// .custom-position {
//   flex: 1;
//   display: flex;
//   align-items: center;
//   font-size: 14px;
//   flex-flow: row nowrap;
// }

// .form-item {
//   margin-bottom: 0;
// }

// .title-css {
//   height: 26px;
// }

// .title-text {
//   line-height: 26px;
//   color: #bfbfbf;
// }

// .dialog-css > .el-dialog__header {
//   padding: 20px 20px 0;
// }

// .dialog-css > .el-dialog__body {
//   padding: 10px 20px 20px;
// }

// .form-item > .el-form-item__label {
//   font-size: 12px;
// }

// .scene-title {
//   width: 100%;
//   display: flex;
// }

// .scene-title-name {
//   width: 100%;
//   overflow: hidden;
//   display: inline-block;
//   white-space: nowrap;
//   text-overflow: ellipsis;
// }

// .father .child {
//   /*display: none;*/
//   visibility: hidden;
// }

// .father:hover .child {
//   /*display: inline;*/
//   visibility: visible;
// }

// .tree-style {
//   padding: 10px 15px;
//   height: 100%;
//   overflow-y: auto;
// }

// .msg-node-class {
//   color: red;

//   > i {
//     color: red;
//   }
// }
// .set-group {
//   display: flex;
//   float: right;
// }
// .set-btns {
//   border: none;
// }
</style>
