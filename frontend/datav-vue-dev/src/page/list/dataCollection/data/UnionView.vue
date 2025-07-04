<template>
  <el-row>
    <el-button type="primary" v-if="hasDataPermission('manage',param.privileges)" size="mini" icon="el-icon-circle-plus-outline" @click="showUnionEdit">新建关联</el-button>
    <el-row>
      <el-table
        size="mini"
        :data="unionData"
        :height="height"
        border
        style="width: 100%;margin-top: 10px;"
      >
        <el-table-column
          prop="sourceTableName"
          label="关联表"
        />
        <el-table-column
          prop="sourceTableFieldName"
          label="关联字段"
        />
        <el-table-column
          prop="sourceUnionRelation"
          label="关联关系"
        >
          <template slot-scope="scope">
            <span style="font-size: 12px;">
              <span v-if="scope.row.sourceUnionRelation === '1:N'">左连接</span>
              <span v-if="scope.row.sourceUnionRelation === 'N:1'">右连接</span>
              <span v-if="scope.row.sourceUnionRelation === '1:1'">内连接</span>
              <span v-if="scope.row.sourceUnionRelation === 'N:N'">全连接</span>
            </span>
          </template>
        </el-table-column>
        <el-table-column
          prop="targetTableName"
          label="被关联表"
        />
        <el-table-column
          prop="targetTableFieldName"
          label="被关联字段"
        />
        <el-table-column
          align="left"
          label="操作"
        >
          <template slot-scope="scope">
            <el-button v-if="hasDataPermission('manage',param.privileges)" type="text" size="mini" @click="edit(scope.row)">编辑</el-button>
            <el-button v-if="hasDataPermission('manage',param.privileges)" type="text" size="mini" @click="deleteUnion(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-row>

    <el-dialog
      v-dialogDrag
      title="关联设置"
      :visible="editUnion"
      :show-close="false"
      width="600px"
      class="dialog-css"
    >
      <el-row style="display: flex;align-items: center;justify-content: center;">
        <el-col :span="6">
          <p class="table-name-css" :title="table.name">{{ table.name }}</p>
          <el-select v-model="union.sourceTableFieldId" placeholder="请选择关联字段" filterable clearable size="mini">
            <el-option
              v-for="item in sourceFieldOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <span>
                <span v-if="item.deType === 0">
                  <svg-icon v-if="item.deType === 0" icon-class="field_text" class="field-icon-text" />
                </span>
                <span v-if="item.deType === 1">
                  <svg-icon v-if="item.deType === 1" icon-class="field_time" class="field-icon-time" />
                </span>
                <span v-if="item.deType === 2 || item.deType === 3">
                  <svg-icon v-if="item.deType === 2 || item.deType === 3" icon-class="field_value" class="field-icon-value" />
                </span>
                <span v-if="item.deType === 5">
                  <svg-icon v-if="item.deType === 5" icon-class="field_location" class="field-icon-location" />
                </span>
              </span>
              <span>
                {{ item.name }}
              </span>
            </el-option>
          </el-select>
        </el-col>

        <el-col :span="6">
          <el-radio-group v-model="union.sourceUnionRelation" size="mini" style="display: block;width: 100%;text-align: center;">
            <el-radio class="union-relation-css" label="1:N">左连接</el-radio>
            <el-radio class="union-relation-css" label="N:1">右连接</el-radio>
            <el-radio class="union-relation-css" label="1:1">内连接</el-radio>
            <!--            <el-radio class="union-relation-css" label="N:N">{{ $t('dataset.full_join') }}</el-radio>-->
          </el-radio-group>
        </el-col>

        <el-col :span="6">
          <el-popover
            ref="targetTable"
            placement="bottom"
            width="500"
            trigger="click"
          >
            <dataset-group-selector-tree :fix-height="true" show-mode="union" :table="table" :custom-type="customType" :mode="table.mode" @getTable="getTable" />
            <el-button slot="reference" size="mini" style="width: 100%;">
              <p class="table-name-css" :title="targetTable.name || '请选择关联表'">{{ targetTable.name || '请选择关联表' }}</p>
            </el-button>
          </el-popover>

          <el-select v-model="union.targetTableFieldId" placeholder="请选择关联字段" filterable clearable size="mini">
            <el-option
              v-for="item in targetFieldOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <span>
                <span v-if="item.deType === 0">
                  <svg-icon v-if="item.deType === 0" icon-class="field_text" class="field-icon-text" />
                </span>
                <span v-if="item.deType === 1">
                  <svg-icon v-if="item.deType === 1" icon-class="field_time" class="field-icon-time" />
                </span>
                <span v-if="item.deType === 2 || item.deType === 3">
                  <svg-icon v-if="item.deType === 2 || item.deType === 3" icon-class="field_value" class="field-icon-value" />
                </span>
                <span v-if="item.deType === 5">
                  <svg-icon v-if="item.deType === 5" icon-class="field_location" class="field-icon-location" />
                </span>
              </span>
              <span>
                {{ item.name }}
              </span>
            </el-option>
          </el-select>
        </el-col>
      </el-row>

      <div slot="footer" class="dialog-footer">
        <el-button size="mini" @click="closeUnion">取消</el-button>
        <el-button type="primary" size="mini" @click="saveUnion">确认</el-button>
      </div>
    </el-dialog>
  </el-row>
</template>

<script>
import { post, fieldList } from '@/api/dataCollection'
import DatasetGroupSelectorTree from '../common/DatasetGroupSelectorTree'

export default {
  name: 'UnionView',
  components: { DatasetGroupSelectorTree },
  props: {
    table: {
      type: Object,
      required: true
    },
    param: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      height: 500,
      union: {
        id: null,
        sourceTableId: this.table.id,
        sourceTableFieldId: '',
        sourceUnionRelation: '',
        targetTableId: '',
        targetTableFieldId: '',
        targetUnionRelation: ''
      },
      unionData: [],
      editUnion: false,
      sourceFieldOption: [],
      targetFieldOption: [],
      targetTable: {},
      customType: ['db', 'sql', 'excel']
    }
  },
  watch: {
    'table': function() {
      this.initUnion()
    }
  },
  mounted() {
    this.calHeight()
    this.initUnion()
  },
  methods: {
    hasDataPermission() {
      return true
    },
    calHeight() {
      const that = this
      setTimeout(function() {
        const currentHeight = document.documentElement.clientHeight
        that.height = currentHeight - 56 - 30 - 26 - 25 - 55 - 38 - 28 - 10
      }, 10)
    },
    initUnion() {
      if (this.table.id) {
        if (this.table.mode === 0) {
          this.customType = ['db']
        } else {
          this.customType = ['db', 'sql', 'excel']
        }
        post('/dataset/union/listByTableId/' + this.table.id, {}).then(response => {
          // console.log(response)
          this.unionData = response.data.data
        })
      }
    },

    showUnionEdit() {
      // 校验同步状态
      // post('/dataset/table/checkDorisTableIsExists/' + this.table.id, {}, true).then(response => {
      //   if (response.data) {
      this.union.sourceTableId = this.table.id
      fieldList(this.table.id).then(response => {
        this.sourceFieldOption = JSON.parse(JSON.stringify(response.data.data)).filter(ele => ele.extField === 0)
      })
      this.editUnion = true
      //   } else {
      //     this.$message({
      //       type: 'error',
      //       message: this.$t('dataset.invalid_table_check'),
      //       showClose: true
      //     })
      //   }
      // })
    },
    saveUnion() {
      // console.log(this.union)
      if (!this.union.sourceTableFieldId || !this.union.sourceUnionRelation || !this.union.targetTableId || !this.union.targetTableFieldId) {
        this.$message({
          type: 'error',
          message: '请正确设置关联关系',
          showClose: true
        })
        return
      }
      this.union.targetUnionRelation = this.union.sourceUnionRelation.split('').reverse().join('')
      post('/dataset/union/save', this.union).then(response => {
        this.$message({
          type: 'success',
          message: '保存成功',
          showClose: true
        })
        this.closeUnion()
        this.initUnion()
      })
    },
    closeUnion() {
      this.editUnion = false
      this.resetUnion()
    },
    resetUnion() {
      this.union = {
        id: null,
        sourceTableId: this.table.id,
        sourceTableFieldId: '',
        sourceUnionRelation: '',
        targetTableId: '',
        targetTableFieldId: '',
        targetUnionRelation: ''
      }
      this.targetTable = {}
      this.targetFieldOption = []
    },

    edit(item) {
      this.union = JSON.parse(JSON.stringify(item))
      this.targetTable.name = this.union.targetTableName
      fieldList(this.union.targetTableId).then(response => {
        this.targetFieldOption = response.data.data
        this.showUnionEdit()
      })
    },
    deleteUnion(item) {
      this.$confirm('确认删除', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        post('/dataset/union/delete/' + item.id, {}).then(response => {
          this.$message({
            type: 'success',
            message: '删除成功',
            showClose: true
          })
          this.initUnion()
        })
      })
    },
    getTable(param) {
      // console.log(param)
      if (param.id === this.table.id) {
        this.$message({
          type: 'error',
          message: '被关联表不能与关联表相同',
          showClose: true
        })
        return
      }
      if (this.table.mode === 0) {
        if (param.dataSourceId !== this.table.dataSourceId) {
          this.$message({
            type: 'error',
            message: '被关联数据集必须与当前数据集的数据源一致',
            showClose: true
          })
          return
        }
      }
      this.targetTable = param
      this.union.targetTableId = param.id
      this.union.targetTableFieldId = ''
      fieldList(param.id).then(response => {
        this.targetFieldOption = JSON.parse(JSON.stringify(response.data.data)).filter(ele => ele.extField === 0)
      })
      this.$refs['targetTable'].doClose()
    }
  }
}
</script>

<style scoped lang="scss">
  .table-name-css{
    margin: 4px 2px;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
  }
  .union-relation-css{
    display: block;
    width: 100%;
    padding: 4px 10px;
  }

  .dialog-css>>>.el-dialog__title {
    font-size: 14px;
  }
  .dialog-css >>> .el-dialog__header {
    padding: 20px 20px 0;
  }
  .dialog-css >>> .el-dialog__body {
    padding: 10px 20px 20px;
  }
  /deep/.el-dialog, .avue-group__item {
    background: #232630;
}
</style>
