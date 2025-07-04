<template>
  <el-col>
    <el-row>
      <el-row style="height: 26px;" class="title-text">
        <span style="line-height: 26px;color:#bfbfbf">
          {{ param.tableId?'编辑 SQL 数据集':'添加 SQL 数据集' }}
        </span>
        <el-row style="float: right">
          <el-button size="mini" @click="cancel">
            取消
          </el-button>
          <el-button size="mini" type="primary" @click="save">
            确认
          </el-button>
        </el-row>
      </el-row>
      <el-divider />
      <el-row>
        <el-form :inline="true">
          <el-form-item class="form-item">
            <el-select v-model="dataSource" filterable placeholder="'请选择数据源'" size="mini" @change="changeDatasource()">
              <el-option
                v-for="item in options"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item class="form-item">
            <el-input v-model="name" size="mini" placeholder="名称" />
          </el-form-item>
          <el-form-item v-if="!param.tableId" class="form-item">
            <el-select v-model="mode" filterable :placeholder="'连接模式'" size="mini">
              <el-option label="直连" value="0" />
              <el-option label="定时同步" value="1" :disabled="!kettleRunning || selectedDatasource.type==='es' || selectedDatasource.type==='ck'|| selectedDatasource.type==='mongo'|| selectedDatasource.type==='redshift' || selectedDatasource.type==='hive'" />
            </el-select>
          </el-form-item>

          <el-form-item v-if="mode === '1'" class="form-item">
            <el-select v-model="syncType" filterable :placeholder="'连接模式'" size="mini">
              <el-option label="'立即更新'" value="sync_now" />
              <el-option label="稍后同步" value="sync_latter" />
            </el-select>
          </el-form-item>
        </el-form>
      </el-row>
      <el-row>
        <el-col style="min-width: 200px;">
          <codemirror
            ref="myCm"
            v-model="sql"
            class="codemirror"
            :options="sqlOption"
            @ready="onCmReady"
            @focus="onCmFocus"
            @input="onCmCodeChange"
          />
        </el-col>
      </el-row>
      <el-row style="margin-top: 10px;">
        <el-card class="box-card dataPreview" shadow="never">
          <div slot="header" class="clearfix">
            <span style="color:#bfbfbf">数据预览</span>
            <el-button style="float: right; padding: 3px 0" type="text" size="mini" @click="getSQLPreview">预览</el-button>
          </div>
          <div class="text item">
            <ux-grid
              ref="plxTable"
              size="mini"
              style="width: 100%;"
              :height="height"
              :checkbox-config="{highlight: true}"
              :width-resize="true"
            >
              <ux-table-column
                v-for="field in fields"
                :key="field.fieldName"
                min-width="200px"
                :field="field.fieldName"
                :title="field.remarks"
                :resizable="true"
              />
            </ux-grid>
          </div>
          <span class="table-count">
            显示
            <span class="span-number">1000</span>
            条数据
          </span>
        </el-card>
      </el-row>
    </el-row>
  </el-col>
</template>

<script>
// import { post, listDatasource, isKettleRunning } from '@/api/dataset/dataset'
import { listDatasource, post, getTable } from '@/api/dataCollection'
import { codemirror } from 'vue-codemirror'
// import { getTable } from '@/api/dataset/dataset'
// 核心样式
import 'codemirror/lib/codemirror.css'
// 引入主题后还需要在 options 中指定主题才会生效
import 'codemirror/theme/blackboard.css'
import 'codemirror/mode/sql/sql.js'
// require active-line.js
import 'codemirror/addon/selection/active-line.js'
// closebrackets
import 'codemirror/addon/edit/closebrackets.js'
// keyMap
import 'codemirror/mode/clike/clike.js'
import 'codemirror/addon/edit/matchbrackets.js'
import 'codemirror/addon/comment/comment.js'
import 'codemirror/addon/dialog/dialog.js'
import 'codemirror/addon/dialog/dialog.css'
import 'codemirror/addon/search/searchcursor.js'
import 'codemirror/addon/search/search.js'
import 'codemirror/keymap/emacs.js'
// 引入代码自动提示插件
import 'codemirror/addon/hint/show-hint.css'
import 'codemirror/addon/hint/sql-hint'
import 'codemirror/addon/hint/show-hint'

export default {
  name: 'AddSQL',
  components: { codemirror },
  props: {
    param: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      dataSource: '',
      options: [],
      name: '',
      sql: '',
      sqlOption: {
        tabSize: 2,
        styleActiveLine: true,
        lineNumbers: true,
        line: true,
        mode: 'text/x-sql',
        theme: 'blackboard',
        hintOptions: { // 自定义提示选项
          completeSingle: false // 当匹配只有一项的时候是否自动补全
        }
      },
      data: [],
      fields: [],
      mode: '0',
      syncType: 'sync_now',
      height: 500,
      kettleRunning: false,
      selectedDatasource: {}
    }
  },
  computed: {
    codemirror() {
      return this.$refs.myCm.codemirror
    }
  },
  watch: {
    'param.tableId': {
      handler: function() {
        this.resetComponent()
        this.initTableInfo()
      }
    }
  },
  mounted() {
    window.onresize = () => {
      this.calHeight()
    }
    this.calHeight()
    this.initDataSource()
    this.$refs.myCm.codemirror.on('keypress', () => {
      this.$refs.myCm.codemirror.showHint()
    })

    this.initTableInfo()
  },
  created() {
    // this.kettleState()
  },
  methods: {
    kettleState() {
      isKettleRunning().then(res => {
        this.kettleRunning = res.data
      })
    },
    changeDatasource() {
      for (let i = 0; i < this.options.length; i++) {
        if (this.options[i].id === this.dataSource) {
          this.selectedDatasource = this.options[i]
        }
      }
    },
    calHeight() {
      const that = this
      setTimeout(function() {
        const currentHeight = document.documentElement.clientHeight
        that.height = currentHeight - 56 - 30 - 26 - 25 - 43 - 160 - 10 - 37 - 20 - 10 - 16
      }, 10)
    },
    initDataSource() {
      listDatasource().then(response => {
        this.options = response.data.data
      })
    },

    initTableInfo() {
      if (this.param.tableId) {
        getTable(this.param.tableId).then(response => {
          const table = response.data.data
          this.name = table.name
          this.dataSource = table.dataSourceId
          this.mode = table.mode + ''
          this.sql = JSON.parse(table.info.replace(/\n/g, '\\n').replace(/\r/g, '\\r')).sql

          this.getSQLPreview()
        })
      }
    },

    getSQLPreview() {
      if (!this.dataSource || this.datasource === '') {
        this.$message({
          showClose: true,
          message: '请选择数据源',
          type: 'error'
        })
        return
      }
      post('/dataset/table/sqlPreview', {
        dataSourceId: this.dataSource,
        type: 'sql',
        // info: '{"sql":"' + this.sql + '"}',
        info: JSON.stringify({ sql: this.sql.trim() })
      }).then(response => {
        console.log(response)
        this.fields = response.data.data.fields
        this.data = response.data.data.data
        const datas = this.data
        this.$refs.plxTable.reloadData(datas)
      })
    },

    save() {
      if (!this.dataSource || this.datasource === '') {
        this.$message({
          showClose: true,
          message: '请选择数据源',
          type: 'error'
        })
        return
      }
      if (!this.name || this.name === '') {
        this.$message({
          showClose: true,
          message: '请输入名称',
          type: 'error'
        })
        return
      }
      if (this.name.length > 50) {
        this.$message({
          showClose: true,
          message: '数据集名称不能超过50个字符',
          type: 'error'
        })
        return
      }
      const table = {
        id: this.param.tableId,
        name: this.name,
        sceneId: this.param.id,
        dataSourceId: this.dataSource,
        type: 'sql',
        syncType: this.syncType,
        mode: parseInt(this.mode),
        // info: '{"sql":"' + this.sql + '"}',
        info: JSON.stringify({ sql: this.sql.trim() })
      }
      post('/dataset/table/update', table).then(response => {
        // this.$store.dispatch('dataset/setSceneData', new Date().getTime())
        this.$emit('saveSuccess', table)
        this.cancel()
      })
    },

    cancel() {
      // this.dataReset()
      if (this.param.tableId) {
        this.$emit('switchComponent', { name: 'ViewTable', param: this.param.table })
      } else {
        this.$emit('switchComponent', { name: '' })
      }
    },

    showSQL(val) {
      this.sql = val || ''
    },
    onCmReady(cm) {
      this.codemirror.setSize('-webkit-fill-available', 'auto')
    },
    onCmFocus(cm) {
      // console.log('the editor is focus!', cm)
    },
    onCmCodeChange(newCode) {
      // console.log(newCode)
      this.sql = newCode
      this.$emit('codeChange', this.sql)
    },

    resetComponent() {
      this.dataSource = ''
      this.name = ''
      this.sql = ''
      this.data = []
      this.fields = []
      this.mode = '0'
      this.syncType = 'sync_now'
    }
  }
}
</script>

<style scoped>
.el-divider{
  background-color: #293241;
}
  .el-divider--horizontal {
    margin: 12px 0;
  }

  .form-item {
    margin-bottom: 6px;
  }

  .el-checkbox {
    margin-bottom: 14px;
    margin-left: 0;
    margin-right: 14px;
  }

  .el-checkbox.is-bordered + .el-checkbox.is-bordered {
    margin-left: 0;
  }

  .codemirror {
    height: 160px;
    overflow-y: auto;
  }
  .codemirror >>> .CodeMirror-scroll {
    height: 160px;
    overflow-y: auto;
  }

  .dataPreview>>>.el-card__header{
    padding: 6px 8px;
  }

  .dataPreview>>>.el-card__body{
    padding:10px;
  }

  span{
    font-size: 14px;
  }
  .span-number{
    color: #0a7be0;
  }
  .table-count{
    color: #606266;
  }
  .el-card{
    border:1px solid #293241;
  }
  .el-card>>>.el-card__header{
    border-bottom: 1px solid #293241;
  }
  .el-card>>>.elx-table .elx-table--body-wrapper{
    background: transparent;
  }
  .el-card>>>.elx-table .elx-table--border-line{
    border: 1px solid #293241;
  }
    .el-card>>>.elx-table.border--full .elx-body--column,.el-card>>>.elx-table.border--full .elx-header--column{
    background-image: linear-gradient(#293241, #293241), linear-gradient(#293241, #293241);
  }
  .el-card>>>.elx-header--column{
    background: #222933;
  }
  .el-card>>>.elx-table--header-border-line{
    border-bottom: 1px solid #293241;
  }
  .el-card>>>.elx-body--row.row--hover{
    background-color: #222933;
  }
</style>
