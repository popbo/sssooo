<template>
  <el-row style="height: 100%; overflow-y: hidden; width: 100%">
    <!--    <span v-show="false">{{ tableRefresh }}</span>-->
    <el-row style="height: 26px">
      <span class="title-text" style="line-height: 26px; color: #bfbfbf">
        {{ table.name }}
      </span>
      <el-popover
        placement="right-start"
        width="400"
        trigger="click"
        @show="showTab"
        @hide="hideTab"
      >
        <dataset-chart-detail
          type="dataset"
          :data="table"
          :tab-status="tabStatus"
        />
        <!--        <svg-icon slot="reference" class="title-text" icon-class="more_v" style="cursor: pointer;" />-->
        <i
          slot="reference"
          class="el-icon-warning icon-class"
          style="margin-left: 4px; cursor: pointer; font-size: 14px"
        />
      </el-popover>
      <el-row
        v-if="hasDataPermission('manage', param.privileges)"
        style="float: right"
      >
        <el-dropdown
          v-if="table.type === 'excel'"
          style="margin-right: 10px"
          size="small"
          trigger="click"
          @command="clickEditExcel"
        >
          <el-button size="mini"> 编辑Excel </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item :command="beforeEditExcel('0')">
              替换
            </el-dropdown-item>
            <el-dropdown-item :command="beforeEditExcel('1')">
              追加
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-button
          v-if="table.type === 'custom'"
          size="mini"
          @click="editCustom"
        >
          编辑自定义数据集
        </el-button>
        <el-button v-if="table.type === 'sql'" size="mini" @click="editSql">
          编辑 SQL 数据集
        </el-button>
        <el-button v-if="table.type === 'union'" size="mini" @click="editUnion">
          编辑关联数据集
        </el-button>
        <!--        <el-button size="mini" @click="edit">-->
        <!--          {{ $t('dataset.edit_field') }}-->
        <!--        </el-button>-->
        <!--        <el-button size="mini" type="primary" @click="createChart">-->
        <!--          {{$t('dataset.create_view')}}-->
        <!--        </el-button>-->
      </el-row>
    </el-row>
    <el-divider />

    <el-tabs v-model="tabActive" @tab-click="tabClick">
      <el-tab-pane label="数据预览" name="dataPreview">
        <tab-data-preview
          :param="param"
          :table="table"
          :fields="fields"
          :data="data"
          :page="page"
          :form="tableViewRowForm"
          @reSearch="reSearch"
        />
      </el-tab-pane>
      <el-tab-pane label="字段管理" name="fieldEdit">
        <field-edit :param="param" :table="table" />
      </el-tab-pane>
      <el-tab-pane
        v-if="
          table.type !== 'custom' && !(table.type === 'sql' && table.mode === 0)
        "
        label="数据关联"
        name="joinView"
      >
        <union-view :param="param" :table="table" />
      </el-tab-pane>
      <el-tab-pane
        v-if="
          table.mode === 1 &&
          (table.type === 'excel' ||
            table.type === 'db' ||
            table.type === 'sql')
        "
        label="更新信息"
        name="updateInfo"
      >
        <update-info
          v-if="tabActive == 'updateInfo'"
          :param="param"
          :table="table"
        />
      </el-tab-pane>
      <el-tab-pane :lazy="true" label="行权限" name="rowPermissions">
        <rowPermissions
          v-if="tabActive == 'rowPermissions'"
          :param="param"
        ></rowPermissions>
      </el-tab-pane>
      <el-tab-pane :lazy="true" label="列权限" name="columnPermissions">
        <columnPermissions
          v-if="tabActive == 'columnPermissions'"
          :param="param"
        ></columnPermissions>
      </el-tab-pane>
    </el-tabs>
  </el-row>
</template>

<script>
import { post } from '@/api/dataCollection'
import TabDataPreview from './TabDataPreview'
import UpdateInfo from './UpdateInfo'
import DatasetChartDetail from '../common/DatasetChartDetail'
import UnionView from './UnionView'
import FieldEdit from './FieldEdit'
import columnPermissions from './columnPermissions'
import rowPermissions from './rowPermissions'
export default {
  name: 'ViewTable',
  components: {
    FieldEdit,
    UnionView,
    DatasetChartDetail,
    UpdateInfo,
    TabDataPreview,
    rowPermissions,
    columnPermissions,
  },
  props: {
    param: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      table: {
        name: '',
      },
      fields: [],
      data: [],
      page: {
        page: 1,
        pageSize: 100,
        show: 1000,
      },
      tabActive: 'dataPreview',
      tableViewRowForm: {
        row: 1000,
      },
      tabStatus: false,
    }
  },
  computed: {
    // tableRefresh() {
    //   this.initTable(this.param)
    //   return this.$store.state.dataset.table
    // }
  },
  watch: {
    param: function () {
      this.tabActive = 'dataPreview'
      this.initTable(this.param.id)
    },
  },
  created() {},
  mounted() {
    this.initTable(this.param.id)
  },
  methods: {
    hasDataPermission() {
      return true
    },
    initTable(id) {
      this.resetPage()
      this.tableViewRowForm.row = 1000
      if (id !== null) {
        this.fields = []
        this.data = []
        post('/dataset/table/getWithPermission/' + id, null)
          .then(response => {
            this.table = response.data.data
            this.initPreviewData(this.page)
          })
          .catch(res => {
            this.$emit('switchComponent', { name: '' })
          })
      }
    },

    initPreviewData(page) {
      if (this.table.id) {
        this.table.row = this.tableViewRowForm.row
        post(
          '/dataset/table/getPreviewData/' + page.page + '/' + page.pageSize,
          this.table,
          true,
          30000
        )
          .then(response => {
            if (response.data.code === 400) {
              this.$error(response.data.msg, 5000)
            } else {
              this.fields = response.data?.data?.fields
              this.data = response.data?.data?.data
              this.page = response.data?.data?.page

              console.log('res', response)
              if (response.data.status === 'warnning') {
                this.$warning(response.data.msg, 3000)
              }
              if (response.data.status === 'error') {
                this.$error(response.data.msg, 3000)
              }
            }
          })
          .catch(response => {
            // if (response.code === 400) {
            //   this.$warning(response.msg, 3000)
            // }
            this.fields = []
            this.data = []
            this.page = {
              page: 1,
              pageSize: 100,
              show: 0,
            }
          })
      }
    },

    edit() {
      this.$emit('switchComponent', {
        name: 'FieldEdit',
        param: { table: this.table },
      })
    },

    editSql() {
      this.$emit('switchComponent', {
        name: 'AddSQL',
        param: {
          id: this.table.sceneId,
          tableId: this.table.id,
          table: this.table,
        },
      })
    },
    editCustom() {
      this.$emit('switchComponent', {
        name: 'AddCustom',
        param: {
          id: this.table.sceneId,
          tableId: this.table.id,
          table: this.table,
        },
      })
    },
    editUnion() {
      this.$emit('switchComponent', {
        name: 'AddUnion',
        param: {
          id: this.table.sceneId,
          tableId: this.table.id,
          table: this.table,
        },
      })
    },

    reSearch(val) {
      this.tableViewRowForm = val.form
      this.initPreviewData(val.page)
    },

    showTab() {
      this.tabStatus = true
    },
    hideTab() {
      this.tabStatus = false
    },

    clickEditExcel(param) {
      // console.log(param);
      switch (param.type) {
        case '0':
          this.$emit('switchComponent', {
            name: 'AddExcel',
            param: {
              id: this.table.sceneId,
              tableId: this.table.id,
              editType: 0,
              table: this.table,
            },
          })
          break
        case '1':
          this.$emit('switchComponent', {
            name: 'AddExcel',
            param: {
              id: this.table.sceneId,
              tableId: this.table.id,
              editType: 1,
              table: this.table,
            },
          })
          break
      }
    },

    beforeEditExcel(type) {
      return {
        type: type,
      }
    },
    msg2Current(sourceParam) {
      this.tabActive = 'updateInfo'
      this.table.msgTaskId = sourceParam.taskId
    },

    resetPage() {
      this.page = {
        page: 1,
        pageSize: 100,
        show: 1000,
      }
    },

    tabClick() {
      if (this.tabActive === 'dataPreview') {
        this.initTable(this.param.id)
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.el-divider--horizontal {
  margin: 12px 0;
  background-color: #293241;
}

.form-item {
  margin-bottom: 6px;
}
.icon-class {
  color: #6c6c6c;
}
.blackTheme .icon-class {
  color: #cccccc;
}
.el-tabs {
  /deep/ .el-tabs__nav-wrap {
    &:after {
      background-color: #293241;
    }
    .el-tabs__item {
      color: #bfbfbf;
      height: 50px;
      line-height: 50px;
    }
  }
}
/deep/.el-table {
  background-color: #171c22;
  tr {
    background-color: #171c22;
    th {
      background-color: #171c22;
      &.is-leaf {
        border-bottom: 1px solid #293241;
      }
    }
    td {
      border-bottom: 1px solid #293241;
    }
  }
}
/deep/ .el-table--border {
  td,
  th {
    border-right: 1px solid #293241;
  }
}
/deep/.el-table__body-wrapper
  .el-table--border.is-scrolling-left
  ~ .el-table__fixed {
  border-right: 1px solid #293241;
}
/deep/.el-table--border::after,
/deep/.el-table--group::after,
/deep/.el-table::before {
  background-color: #293241;
}
/deep/.el-table--border,
/deep/ .el-table--group {
  border: 1px solid #293241;
}
/deep/.el-table--enable-row-hover .el-table__body tr:hover > td {
  background-color: #293241;
}
</style>
