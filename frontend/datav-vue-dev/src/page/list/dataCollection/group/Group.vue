<template>
  <el-col class="tree-style">
    <!-- group -->
    <el-col>
      <el-row class="title-css">
        <span class="title-text"> 数据集 </span>
        <el-button
          icon="el-icon-plus"
          type="text"
          size="mini"
          style="float: right"
          @click="add('group')"
        />
      </el-row>
      <el-divider />

      <el-row style="margin-bottom: 10px">
        <el-col :span="16">
          <el-input
            v-model="filterText"
            size="mini"
            placeholder="搜索"
            prefix-icon="el-icon-search"
            clearable
            class="main-area-input"
          />
        </el-col>
        <el-col :span="8">
          <el-dropdown>
            <el-button size="mini" type="primary">
              {{ searchMap[searchType]
              }}<i class="el-icon-arrow-down el-icon--right" />
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="searchTypeClick('all')"
                >全部</el-dropdown-item
              >
              <el-dropdown-item @click.native="searchTypeClick('folder')"
                >目录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-col>
      </el-row>

      <el-col class="custom-tree-container">
        <div class="block">
          <el-tree
            ref="datasetTreeRef"
            :default-expanded-keys="expandedArray"
            :data="tData"
            node-key="id"
            highlight-current
            :expand-on-click-node="true"
            :filter-node-method="filterNode"
            @node-expand="nodeExpand"
            @node-collapse="nodeCollapse"
            @node-click="nodeClick"
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
              <span class="child">
                <span v-if="data.modelInnerType === 'group'" @click.stop>
                  <el-dropdown trigger="click" size="small" @command="clickAdd">
                    <span class="el-dropdown-link">
                      <el-button icon="el-icon-plus" type="text" size="small" />
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item
                        icon="el-icon-folder-add"
                        :command="beforeClickAdd('group', data, node)"
                      >
                        <span style="font-size: 13px">分组</span>
                      </el-dropdown-item>
                      <el-dropdown-item icon="el-icon-circle-plus">
                        <el-dropdown
                          size="small"
                          placement="right-start"
                          @command="clickAddData"
                        >
                          <span class="el-dropdown-link inner-dropdown-menu">
                            <span>
                              <span style="font-size: 13px">添加数据集</span>
                            </span>
                            <i class="el-icon-arrow-right el-icon--right" />
                          </span>
                          <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item
                              :command="beforeClickAddData('db', data)"
                            >
                              <!-- <svg-icon icon-class="ds-db" class="ds-icon-db" /> -->
                              数据库数据集
                            </el-dropdown-item>
                            <el-dropdown-item
                              :command="beforeClickAddData('sql', data)"
                            >
                              <!-- <svg-icon
                                icon-class="ds-sql"
                                class="ds-icon-sql"
                              /> -->
                              SQL 数据集
                            </el-dropdown-item>
                            <!-- :disabled="!kettleRunning" -->
                            <!-- <el-dropdown-item :command="beforeClickAddData('excel',data)" >
                              <svg-icon icon-class="ds-excel" class="ds-icon-excel" />
                              Excel 数据集
                            </el-dropdown-item> -->
                            <el-dropdown-item
                              :command="beforeClickAddData('custom', data)"
                            >
                              <!-- <svg-icon
                                icon-class="ds-custom"
                                class="ds-icon-custom"
                              /> -->
                              自定义数据集
                            </el-dropdown-item>
                            <!-- <el-dropdown-item :command="beforeClickAddData('union',data)">
                              <svg-icon icon-class="ds-union" class="ds-icon-union" />
                              关联数据集
                            </el-dropdown-item> -->
                          </el-dropdown-menu>
                        </el-dropdown>
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </span>
                <span style="margin-left: 12px" @click.stop>
                  <el-dropdown
                    trigger="click"
                    size="small"
                    @command="clickMore"
                  >
                    <span class="el-dropdown-link">
                      <el-button icon="el-icon-more" type="text" size="small" />
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item
                        icon="el-icon-edit-outline"
                        :command="beforeClickMore('rename', data, node)"
                      >
                        重命名
                      </el-dropdown-item>
                      <el-dropdown-item
                        icon="el-icon-right"
                        :command="beforeClickMore('move', data, node)"
                      >
                        移动到
                      </el-dropdown-item>
                      <el-dropdown-item
                        icon="el-icon-delete"
                        :command="beforeClickMore('delete', data, node)"
                      >
                        删除
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </span>
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
                  v-if="
                    data.modelInnerType === 'db' ||
                    data.modelInnerType === 'sql'
                  "
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
              <span class="child">
                <span style="margin-left: 12px" @click.stop>
                  <el-dropdown
                    trigger="click"
                    size="small"
                    @command="clickMore"
                  >
                    <span class="el-dropdown-link">
                      <el-button icon="el-icon-more" type="text" size="small" />
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item
                        icon="el-icon-edit-outline"
                        :command="beforeClickMore('editTable', data, node)"
                      >
                        重命名
                      </el-dropdown-item>
                      <el-dropdown-item
                        icon="el-icon-right"
                        :command="beforeClickMore('moveDs', data, node)"
                      >
                        移动到
                      </el-dropdown-item>
                      <el-dropdown-item
                        icon="el-icon-delete"
                        :command="beforeClickMore('deleteTable', data, node)"
                      >
                        删除
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </span>
              </span>
            </span>
          </el-tree>
        </div>
      </el-col>

      <el-dialog
        v-dialogDrag
        :title="dialogTitle"
        :visible="editGroup"
        :show-close="false"
        width="30%"
      >
        <el-form
          ref="groupForm"
          :model="groupForm"
          :rules="groupFormRules"
          @keypress.enter.native="saveGroup(groupForm)"
        >
          <el-form-item :label="'名称'" prop="name">
            <el-input v-model="groupForm.name" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button size="mini" @click="close()">取消</el-button>
          <el-button type="primary" size="mini" @click="saveGroup(groupForm)"
            >提交
          </el-button>
        </div>
      </el-dialog>
    </el-col>

    <el-dialog
      v-dialogDrag
      :title="'表格'"
      :visible="editTable"
      :show-close="false"
      width="30%"
    >
      <el-form
        ref="tableForm"
        :model="tableForm"
        :rules="tableFormRules"
        @keypress.enter.native="saveTable(tableForm)"
      >
        <el-form-item :label="'名称'" prop="name">
          <el-input v-model="tableForm.name" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="mini" @click="closeTable()">取消</el-button>
        <el-button type="primary" size="mini" @click="saveTable(tableForm)"
          >提交
        </el-button>
      </div>
    </el-dialog>

    <!--移动分组-->
    <el-dialog
      v-dialogDrag
      :title="moveDialogTitle"
      :visible="moveGroup"
      :show-close="false"
      width="30%"
      class="dialog-css"
    >
      <group-move-selector :item="groupForm" @targetGroup="targetGroup" />
      <div slot="footer" class="dialog-footer">
        <el-button size="mini" @click="closeMoveGroup()">取消</el-button>
        <el-button
          :disabled="groupMoveConfirmDisabled"
          type="primary"
          size="mini"
          @click="saveMoveGroup(tGroup)"
          >提交
        </el-button>
      </div>
    </el-dialog>

    <!--移动数据集-->
    <el-dialog
      v-dialogDrag
      :title="moveDialogTitle"
      :visible="moveDs"
      :show-close="false"
      width="30%"
      class="dialog-css"
    >
      <ds-move-selector :item="dsForm" @targetDs="targetDs" />
      <div slot="footer" class="dialog-footer">
        <el-button size="mini" @click="closeMoveDs()">取消</el-button>
        <el-button
          :disabled="dsMoveConfirmDisabled"
          type="primary"
          size="mini"
          @click="saveMoveDs(tDs)"
          >提交
        </el-button>
      </div>
    </el-dialog>
  </el-col>
</template>

<script>
// import { loadTable, getScene, addGroup, delGroup, delTable, post, isKettleRunning, alter } from '@/api/dataset/dataset'
import GroupMoveSelector from './GroupMoveSelector'
import DsMoveSelector from './DsMoveSelector'
import {
  queryAuthModel,
  addGroup,
  delGroup,
  delTable,
  alter,
} from '@/api/dataCollection.js'
export default {
  name: 'Group',
  components: { GroupMoveSelector, DsMoveSelector },
  props: {
    saveStatus: {
      type: Object,
      required: false,
      default: null,
    },
  },
  data() {
    return {
      sceneMode: false,
      dialogTitle: '',
      search: '',
      editGroup: false,
      editTable: false,
      tData: [],
      tableData: [],
      tables: [],
      currGroup: {},
      expandedArray: [],
      groupForm: {
        name: '',
        pid: '0',
        level: 0,
        type: '',
        children: [],
        sort: 'type desc,name asc',
      },
      dsForm: {
        name: '',
        pid: '0',
        level: 0,
        type: '',
        children: [],
        sort: 'type desc,name asc',
      },
      tableForm: {
        name: '',
        mode: '',
        sort: 'type asc,create_time desc,name asc',
      },
      groupFormRules: {
        name: [
          { required: true, message: '请输入内容', trigger: 'change' },
          { max: 50, message: '不能超过50字符', trigger: 'change' },
        ],
      },
      tableFormRules: {
        name: [
          { required: true, message: '请输入内容', trigger: 'change' },
          { max: 50, message: '不能超过50字符', trigger: 'change' },
        ],
      },
      moveGroup: false,
      tGroup: {},
      moveDs: false,
      tDs: {},
      groupMoveConfirmDisabled: true,
      dsMoveConfirmDisabled: true,
      moveDialogTitle: '',
      treeProps: {
        label: 'name',
        children: 'children',
        isLeaf: 'isLeaf',
        id: 'id',
        parentId: 'pid',
      },
      isTreeSearch: false,
      kettleRunning: false,
      searchPids: [], // 查询命中的pid
      filterText: '',
      searchType: 'all',
      searchMap: {
        all: '全部',
        folder: '目录',
      },
    }
  },
  computed: {},
  watch: {
    saveStatus() {
      this.treeNode()
    },
    filterText(val) {
      this.searchPids = []
      this.$refs.datasetTreeRef.filter(val)
    },
    searchType(val) {
      this.searchPids = []
      this.$refs.datasetTreeRef.filter(this.filterText)
    },
  },
  created() {
    // this.kettleState()
  },
  mounted() {
    this.treeNode(true)
    this.refresh()
  },
  methods: {
    clickAdd(param) {
      this.add(param.type)
      this.groupForm.pid = param.data.id
      this.groupForm.level = param.data.level + 1
    },

    beforeClickAdd(type, data, node) {
      return {
        type: type,
        data: data,
        node: node,
      }
    },
    kettleState() {
      isKettleRunning().then(res => {
        this.kettleRunning = res.data
      })
    },
    clickMore(param) {
      switch (param.type) {
        case 'rename':
          this.add(param.data.modelInnerType)
          this.groupForm = JSON.parse(JSON.stringify(param.data))
          break
        case 'move':
          this.moveTo(param.data)
          this.groupForm = JSON.parse(JSON.stringify(param.data))
          break
        case 'moveDs':
          this.moveToDs(param.data)
          this.dsForm = JSON.parse(JSON.stringify(param.data))
          break
        case 'delete':
          this.delete(param.data)
          break
        case 'editTable':
          this.editTable = true
          this.tableForm = JSON.parse(JSON.stringify(param.data))
          this.tableForm.mode = this.tableForm.mode + ''
          break
        case 'deleteTable':
          this.deleteTable(param.data)
          break
      }
    },

    beforeClickMore(type, data, node) {
      return {
        type: type,
        data: data,
        node: node,
      }
    },

    add(type) {
      switch (type) {
        case 'group':
          this.dialogTitle = '分组'
          break
        case 'scene':
          this.dialogTitle = '场景'
          break
      }
      this.groupForm.type = type
      this.editGroup = true
    },

    saveGroup(group) {
      this.$refs['groupForm'].validate(valid => {
        if (valid) {
          addGroup(group).then(res => {
            this.close()
            this.$message({
              message: '保存成功',
              type: 'success',
              showClose: true,
            })
            this.expandedArray.push(group.pid)
            this.treeNode()
          })
        } else {
          return false
        }
      })
    },

    saveTable(table) {
      table.mode = parseInt(table.mode)
      const _this = this
      this.$refs['tableForm'].validate(valid => {
        if (valid) {
          table.isRename = true
          alter(table).then(response => {
            this.closeTable()
            this.$message({
              message: '保存成功',
              type: 'success',
              showClose: true,
            })
            _this.expandedArray.push(table.sceneId)
            _this.$refs.datasetTreeRef.setCurrentKey(table.id)
            _this.treeNode()
            // this.$store.dispatch('dataset/setTable', new Date().getTime())
          })
        } else {
          return false
        }
      })
    },

    delete(data) {
      this.$confirm('确认删除', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          delGroup(data.id).then(res => {
            if (res.data.code === 200) {
              this.$message({
                type: 'success',
                message: '删除数据集成功',
                showClose: true,
              })
              this.treeNode()
            } else {
              this.$message({
                type: 'error',
                message:
                  res.data.msg.length > 30 ? '删除数据集失败' : res.data.msg,
                showClose: true,
              })
            }
          })
        })
        .catch(() => {
          this.$message({
            type: 'error',
            message: '删除数据集失败',
            showClose: true,
          })
        })
    },

    deleteTable(data) {
      this.$confirm('确认删除', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          delTable(data.id).then(res => {
            if (res.data.code === 200) {
              this.$message({
                type: 'success',
                message: '删除成功',
                showClose: true,
              })
              this.treeNode()
            } else {
              this.$message.error('删除失败：' + res.data.msg)
            }
            // this.$store.dispatch('dataset/setTable', new Date().getTime())
          })
        })
        .catch(() => {})
    },

    close() {
      this.$refs['groupForm'].resetFields()
      this.editGroup = false
      this.groupForm = {
        name: '',
        pid: '0',
        level: 0,
        type: '',
        children: [],
        sort: 'type desc,name asc',
      }
    },

    closeTable() {
      this.editTable = false
      this.tableForm = {
        name: '',
      }
    },

    treeNode(cache) {
      const modelInfo = localStorage.getItem('dataset-tree')
      // 不存在返回的是一个字符串 undefined
      const userCache = modelInfo && modelInfo != 'undefined' && cache
      if (userCache) {
        this.tData = JSON.parse(modelInfo)
      }
      queryAuthModel({ modelType: 'dataset' }, !userCache).then(res => {
        localStorage.setItem('dataset-tree', JSON.stringify(res.data.data))
        if (!userCache) {
          this.tData = res.data.data
        }
      })
    },

    tableTree() {
      this.tables = []
      this.tableData = []
      if (this.currGroup.id) {
        loadTable({
          sort: 'type asc,create_time desc,name asc',
          sceneId: this.currGroup.id,
        }).then(res => {
          this.tables = res.data
          this.tableData = JSON.parse(JSON.stringify(this.tables))
        })
      }
    },

    nodeClick(data, node) {
      if (data.modelInnerType !== 'group') {
        this.$emit('switchComponent', { name: 'ViewTable', param: data })
      }
    },

    back() {
      this.sceneMode = false
      this.$emit('switchComponent', { name: '' })
    },

    clickAddData(param) {
      this.currGroup = param.data
      // this.$store.dispatch('dataset/setSceneData', this.currGroup.id)
      switch (param.type) {
        case 'db':
          this.addData('AddDB')
          break
        case 'sql':
          this.addData('AddSQL')
          break
        case 'excel':
          this.addData('AddExcel')
          break
        case 'custom':
          this.addData('AddCustom')
          break
        case 'union':
          this.addData('AddUnion')
          break
      }
    },

    beforeClickAddData(type, data) {
      return {
        type: type,
        data: data,
      }
    },

    addData(name) {
      this.$emit('switchComponent', { name: name, param: this.currGroup })
    },

    sceneClick(data, node) {
      this.$emit('switchComponent', { name: 'ViewTable', param: data.id })
    },

    refresh() {
      const path = this.$route.path
      if (path === '/dataset/table') {
        this.sceneMode = true
        const sceneId = this.$store.state.dataset.sceneData
        getScene(sceneId).then(res => {
          this.currGroup = res.data
        })
      }
    },

    nodeExpand(data) {
      if (data.id) {
        this.expandedArray.push(data.id)
      }
    },
    nodeCollapse(data) {
      if (data.id) {
        this.expandedArray.splice(this.expandedArray.indexOf(data.id), 1)
      }
    },

    moveTo(data) {
      this.moveGroup = true
      this.moveDialogTitle =
        '将' +
        (data.name.length > 10 ? data.name.substr(0, 10) + '...' : data.name) +
        '移动到'
    },
    closeMoveGroup() {
      this.moveGroup = false
      this.groupForm = {
        name: '',
        pid: '0',
        level: 0,
        type: '',
        children: [],
        sort: 'type desc,name asc',
      }
    },
    saveMoveGroup() {
      this.groupForm.pid = this.tGroup.id
      addGroup(this.groupForm).then(res => {
        this.closeMoveGroup()
        this.treeNode()
      })
    },
    targetGroup(val) {
      this.tGroup = val
      this.groupMoveConfirmDisabled = false
    },

    moveToDs(data) {
      this.moveDs = true
      this.moveDialogTitle =
        '将' +
        (data.name.length > 10 ? data.name.substr(0, 10) + '...' : data.name) +
        '移动到'
    },
    closeMoveDs() {
      this.moveDs = false
      this.dsForm = {
        name: '',
        pid: '0',
        level: 0,
        type: '',
        children: [],
        sort: 'type desc,name asc',
      }
    },
    saveMoveDs() {
      const newSceneId = this.tDs.id
      this.dsForm.sceneId = newSceneId
      this.dsForm.isRename = true
      alter(this.dsForm).then(res => {
        this.closeMoveDs()
        this.expandedArray.push(newSceneId)
        this.treeNode()
      })
    },
    targetDs(val) {
      this.tDs = val
      if (this.tDs.type === 'group') {
        this.dsMoveConfirmDisabled = false
      } else {
        this.dsMoveConfirmDisabled = false
      }
    },

    loadNode(node, resolve) {
      if (!this.isTreeSearch) {
        if (node.level > 0) {
          this.tables = []
          this.tableData = []
          if (node.data.id) {
            post('/dataset/table/listAndGroup', {
              sort: 'type asc,name asc,create_time desc',
              sceneId: node.data.id,
            }).then(res => {
              this.tables = res.data
              this.tableData = JSON.parse(JSON.stringify(this.tables))
              resolve(this.tableData)
            })
          }
        }
      } else {
        node.data.children ? resolve(node.data.children) : resolve([])
      }
    },

    getTreeData(val) {
      if (val) {
        this.isTreeSearch = true
        this.searchTree(val)
      } else {
        this.isTreeSearch = false
        this.treeNode()
      }
    },
    filterNode(value, data) {
      if (!value) return true
      if (this.searchType === 'folder') {
        if (
          data.modelInnerType === 'group' &&
          data.label.indexOf(value) !== -1
        ) {
          this.searchPids.push(data.id)
          return true
        }
        if (this.searchPids.indexOf(data.pid) !== -1) {
          if (data.modelInnerType === 'group') {
            this.searchPids.push(data.id)
          }
          return true
        }
      } else {
        return data.label.indexOf(value) !== -1
      }
      return false
    },
    searchTypeClick(searchTypeInfo) {
      this.searchType = searchTypeInfo
    },
  },
}
</script>

<style scoped lang="scss">
.el-divider--horizontal {
  margin: 12px 0;
  background-color: #293241;
}

.search-input {
  padding: 12px 0;
}

.custom-tree-container {
  margin-top: 10px;
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
  visibility: hidden;
}
.father:hover .child {
  visibility: visible;
}

.dialog-css >>> .el-dialog__body {
  padding: 10px 20px 20px;
}

.inner-dropdown-menu {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.tree-style {
  padding: 10px 15px;
  height: 100%;
  overflow-y: auto;
}
//   /deep/.el-dialog, .avue-group__item {
//     background: #fff;
// }
.el-tree {
  background: transparent;
  /deep/ .el-tree-node__content {
    color: #bfbfbf;
    background-color: transparent !important;
    &:hover {
      background-color: #293241 !important;
    }
  }
}
/deep/
  .el-tree--highlight-current
  .el-tree-node.is-current
  > .el-tree-node__content {
  background-color: #192639 !important;
}
</style>
