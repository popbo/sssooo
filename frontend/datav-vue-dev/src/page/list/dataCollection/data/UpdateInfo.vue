<template>
  <el-col>
    <el-row>
      <el-button v-if="hasDataPermission('manage',param.privileges) && table.type !== 'excel'" icon="el-icon-setting" size="mini" @click="showConfig">
        更新设置
      </el-button>
      <el-button icon="el-icon-refresh" size="mini" @click="refreshLog">
        刷新
      </el-button>
    </el-row>
    <el-row style="margin-top: 10px;">
      <el-table
        size="mini"
        :data="taskLogData"
        border
        :height="height"
        style="width: 100%"
      >
        <el-table-column
          prop="name"
          label="任务名称"
        />
        <el-table-column
          prop="startTime"
          label="开始时间"
        >
          <template slot-scope="scope">
            <span>{{ scope.row.startTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="endTime"
          label="结束时间"
        >
          <template slot-scope="scope">
            <span>{{ scope.row.endTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <span v-if="scope.row.status === 'Completed'" style="color: green">成功</span>
            <span v-if="scope.row.status === 'Underway'" class="blue-color">
              <i class="el-icon-loading" />
              执行中
            </span>
            <span v-if="scope.row.status === 'Error'" style="color: red">
              <el-link type="danger" style="font-size: 12px" @click="showErrorMassage(scope.row.info)">失败</el-link>
            </span>
          </template>
        </el-table-column>
      </el-table>
      <el-row style="margin-top: 10px;text-align: right;">
        <el-pagination
          :current-page="page.currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="page.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="page.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </el-row>
    </el-row>

    <el-dialog
      v-dialogDrag
      title="详情"
      :visible="show_error_massage"
      :show-close="false"
      width="50%"
      class="dialog-css"
    >
      <span class="err-msg">{{ error_massage }}
      </span>
      <span slot="footer" class="dialog-footer">
        <el-button size="mini" @click="show_error_massage = false">关闭</el-button>
      </span>
    </el-dialog>

    <el-dialog
      v-dialogDrag
      :title="table.name+' '+'更新设置'"
      :visible="update_setting"
      :show-close="false"
      width="50%"
      class="dialog-css"
    >
      <el-dialog
        v-dialogDrag
        :title="update_task_dialog_title"
        :visible="update_task"
        :show-close="false"
        width="50%"
        class="dialog-css"
        append-to-body
      >
        <el-col>
          <el-form ref="taskForm" :form="taskForm" :model="taskForm" label-width="100px" size="mini" :rules="taskFormRules">
            <el-form-item label="任务名称" prop="name">
              <el-input
                v-model="taskForm.name"
                size="mini"
                style="width: 50%"
                placeholder="任务名称"
              />
            </el-form-item>
            <el-form-item label="更新方式" prop="type">
              <el-select v-model="taskForm.type" size="mini">
                <el-option
                  label="全量更新"
                  value="all_scope"
                />
                <el-option
                  label="增量更新"
                  value="add_scope"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="执行频率" prop="rate">
              <el-select v-model="taskForm.rate" size="mini" @change="onRateChange">
                <el-option
                  label="立即执行"
                  value="SIMPLE"
                />
                <el-option
                  label="简单重复"
                  value="SIMPLE_CRON"
                />
                <el-option
                  label="表达式设定"
                  value="CRON"
                />
              </el-select>
            </el-form-item>

            <el-form-item v-if="taskForm.rate === 'CRON'" label="">
              <el-popover v-model="cronEdit">
                <cron v-model="taskForm.cron" @close="cronEdit = false" />
                <el-input slot="reference" v-model="taskForm.cron" size="mini" style="width: 50%" @click="cronEdit = true" />
              </el-popover>
            </el-form-item>

            <el-form-item v-if="taskForm.rate === 'SIMPLE_CRON'" label="">
              <el-form :inline="true">
                <el-form-item label="每">
                  <el-input v-model="taskForm.extraData.simple_cron_value" size="mini" type="number" min="1" @change="onSimpleCronChange()" />
                </el-form-item>

                <el-form-item class="form-item">
                  <el-select v-model="taskForm.extraData.simple_cron_type" filterable size="mini" @change="onSimpleCronChange()">
                    <el-option label="分 (执行时间：0秒)" value="minute" />
                    <el-option label="时 (执行时间：0分0秒)" value="hour" />
                    <el-option label="日 (执行时间：0时0分0秒)" value="day" />
                  </el-select>
                </el-form-item>
                <el-form-item class="form-item" label="执行一次" />
              </el-form>
            </el-form-item>

            <el-form-item v-if="taskForm.rate !== 'SIMPLE'" label="开始时间" prop="startTime">
              <el-date-picker
                v-model="taskForm.startTime"
                type="datetime"
                placeholder="选择日期时间"
                size="mini"
              />
            </el-form-item>
            <el-form-item v-if="taskForm.rate !== 'SIMPLE'" label="结束时间" prop="end">
              <el-select v-model="taskForm.end" size="mini">
                <el-option
                  label="无限制"
                  value="0"
                />
                <el-option
                  label="设定结束时间"
                  value="1"
                />
              </el-select>
            </el-form-item>
            <el-form-item v-if="taskForm.end === '1'" label="">
              <el-date-picker
                v-model="taskForm.endTime"
                type="datetime"
                placeholder="选择日期时间"
                size="mini"
              />
            </el-form-item>
          </el-form>
        </el-col>
        <div slot="footer" class="dialog-footer">
          <el-button class="dialog_cancel_button" size="mini" @click="closeTask">取消</el-button>
          <el-button type="primary" size="mini" @click="saveTask(taskForm)">确认</el-button>
        </div>
      </el-dialog>
      <el-row>
        <el-button icon="el-icon-plus" size="mini" @click="addTask(undefined)">
          添加任务
        </el-button>
      </el-row>
      <el-row style="margin-top: 10px;">
        <el-table
          border
          size="mini"
          :data="taskData"
          style="width: 100%"
          height="240"
        >
          <el-table-column
            prop="name"
            label="任务名称"
          />
          <el-table-column
            prop="rate"
            label="执行频率"
          >
            <template slot-scope="scope">
              <span v-if="scope.row.rate === 'SIMPLE'">立即执行</span>
              <span v-if="scope.row.rate === 'SIMPLE_CRON'">简单重复</span>
              <span v-if="scope.row.rate === 'CRON'">表达式设定</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="任务状态">
            <template slot-scope="scope">
              <span v-if="scope.row.status === 'Underway'" style="color: green">
                <el-link type="success" style="font-size: 12px" @click="changeTaskStatus(scope.row)">等待执行</el-link>
              </span>
              <span v-if="scope.row.status === 'Stopped'">
                <div style="font-size: 12px">执行结束</div>
              </span>
              <span v-if="scope.row.status === 'Pending'" class="blue-color">
                <el-link type="primary" style="font-size: 12px" @click="changeTaskStatus(scope.row)">暂停</el-link>
              </span>
              <span v-if="scope.row.status === 'Exec'" class="blue-color">
                <i class="el-icon-loading" />
                等待执行
              </span>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
          >
            <template slot-scope="scope" style="float: right">
              <el-button
                size="mini"
                type="primary"
                icon="el-icon-edit"
                circle
                :disabled="scope.row.rate === 'SIMPLE' || scope.row.status === 'Stopped'"
                @click="addTask(scope.row)"
              />
              <el-button
                size="mini"
                type="danger"
                icon="el-icon-delete"
                circle
                @click="deleteTask(scope.row)"
              />
            </template>
          </el-table-column>
        </el-table>
      </el-row>

      <el-divider />

      <el-row style="height: 26px;">
        <el-row>
          <el-col :span="4"><span>增量更新方式:</span></el-col>
          <el-col :span="18">
            <el-radio-group v-model="incrementalUpdateType" size="mini" @change="incrementalUpdateTypeChange">
              <el-radio label="incrementalAdd">增量添加</el-radio>
              <el-radio label="incrementalDelete">增量删除</el-radio>
            </el-radio-group>
          </el-col>
        </el-row>
      </el-row>

      <el-row style="height: 26px;">
        <el-row>
          <el-col :span="4" style="height: 26px;"><span style="display: inline-block;height: 26px;line-height: 26px;">参数:</span></el-col>
          <el-col :span="18">
            <el-button type="text" size="mini" @click="insertParamToCodeMirror('${__last_update_time__}')">上次更新时间</el-button>
            <el-button type="text" size="mini" @click="insertParamToCodeMirror('${__current_update_time__}')">当前更新时间</el-button>
          </el-col>
        </el-row>
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

      <div slot="footer" class="dialog-footer">
        <el-button size="mini" @click="update_setting = false">关闭</el-button>
        <el-button size="mini" type="primary" @click="saveIncrementalConfig">确认</el-button>
      </div>
    </el-dialog>
  </el-col>
</template>

<script>
import { post } from '@/api/dataCollection'
import { codemirror } from 'vue-codemirror'
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
// vue-cron
// import cron from '@/components/cron/cron' , cron

export default {
  name: 'UpdateInfo',
  components: { codemirror },
  props: {
    table: {
      type: Object,
      default: null
    },
    param: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      height: 500,
      update_setting: false,
      update_task: false,
      show_error_massage: false,
      update_task_dialog_title: '',
      error_massage: '',
      taskForm: {
        name: '',
        type: 'all_scope',
        startTime: '',
        rate: 'SIMPLE',
        cron: '',
        endTime: '',
        end: '0',
        extraData: {
          simple_cron_type: 'hour',
          simple_cron_value: 1
        }
      },
      page: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      taskLogData: [],
      taskData: [],
      taskFormRules: {
        name: [
          { required: true, message: '必填', trigger: 'change' },
          { min: 2, max: 50, message: '0-50字符', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '必填', trigger: 'change' }
        ],
        startTime: [
          { required: true, message: '必填', trigger: 'change' }
        ],
        rate: [
          { required: true, message: '必填', trigger: 'change' }
        ],
        end: [
          { required: true, message: '必填', trigger: 'change' }
        ]
      },
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
      incrementalUpdateType: 'incrementalAdd',
      sql: '',
      incrementalConfig: {},
      cronEdit: false,
      lang: this.$store.getters.language === 'en_US' ? 'en' : 'cn',
      taskLastRequestComplete: true,
      taskLogLastRequestComplete: true
    }
  },
  computed: {
    codemirror() {
      return this.$refs.myCm.codemirror
    }
  },
  watch: {
    table: {
      handler() {
        this.listTask()
        this.listTaskLog()
      },
      immediate: true
    }
  },
  mounted() {
    this.calHeight()
  },
  created() {
    this.taskLogTimer = setInterval(() => {
      if (!this.taskLogLastRequestComplete) {
        return
      } else {
        this.taskLogLastRequestComplete = false
      }
      this.listTaskLog(false)
    }, 10000)

    this.taskTimer = setInterval(() => {
      if (!this.taskLastRequestComplete) {
        return
      } else {
        this.taskLastRequestComplete = false
      }
      this.listTask(false)
    }, 10000)
  },
  beforeDestroy() {
    clearInterval(this.taskTimer)
    clearInterval(this.taskLogTimer)
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
    cellStyle({ row, column }) {
      // 状态列字体颜色
      if (row.status === 'Underway' && column === 'status') {
        return 'color: var(--Main, #0000ff)'
      } else if (row.status === 'Completed' && column === 'status') {
        return 'color: green'
      } else if (row.status === 'Error' && column === 'status') {
        return 'color: red'
      }
    },
    incrementalUpdateTypeChange: function() {
      if (this.incrementalUpdateType === 'incrementalAdd') {
        if (this.sql) {
          this.incrementalConfig.incrementalDelete = this.sql
        } else {
          this.incrementalConfig.incrementalDelete = ''
        }
        if (this.incrementalConfig.incrementalAdd) {
          this.sql = this.incrementalConfig.incrementalAdd
        } else {
          this.sql = ''
        }
      }

      if (this.incrementalUpdateType === 'incrementalDelete') {
        if (this.sql) {
          this.incrementalConfig.incrementalAdd = this.sql
        } else {
          this.incrementalConfig.incrementalAdd = ''
        }
        if (this.incrementalConfig.incrementalDelete) {
          this.sql = this.incrementalConfig.incrementalDelete
        } else {
          this.sql = ''
        }
      }
    },
    showConfig() {
      this.update_setting = true
      this.listTask()
      this.getIncrementalConfig()
    },
    refreshLog() {
      this.listTaskLog()
    },
    showErrorMassage(massage) {
      this.show_error_massage = true
      this.error_massage = massage
    },
    addTask(task) {
      if (!task) {
        // add
        this.resetTaskForm()
        this.taskForm.name = this.table.name + ' ' + '更新设置'
        this.taskForm.startTime = new Date()
        this.update_task_dialog_title = '添加任务'
      } else {
        // update
        this.taskForm = JSON.parse(JSON.stringify(task))
        this.taskForm.extraData = JSON.parse(this.taskForm.extraData)
        this.update_task_dialog_title = '编辑任务'
      }
      this.update_task = true
    },
    listTask(loading = true) {
      post('/dataset/task/list', { tableId: this.table.id }, loading).then(response => {
        this.taskData = response.data
        this.taskLastRequestComplete = true
      }).catch(() => {
        this.taskLastRequestComplete = true
      })
    },
    getIncrementalConfig() {
      post('/dataset/table/incrementalConfig', { tableId: this.table.id }).then(response => {
        this.incrementalConfig = response.data
        this.incrementalUpdateType = 'incrementalAdd'
        if (this.incrementalConfig.incrementalAdd) {
          this.sql = this.incrementalConfig.incrementalAdd
        } else {
          this.sql = ''
        }
      })
    },
    saveIncrementalConfig() {
      if (this.incrementalUpdateType === 'incrementalAdd') {
        this.incrementalConfig.incrementalAdd = this.sql
      } else {
        this.incrementalConfig.incrementalDelete = this.sql
      }
      this.incrementalConfig.tableId = this.table.id
      post('/dataset/table/save/incrementalConfig', this.incrementalConfig).then(response => {
        this.$message({
          message: '保存成功',
          type: 'success',
          showClose: true
        })
        this.update_setting = false
      })
    },
    saveTask(task) {
      this.$refs.taskForm.validate(valid => {
        if (valid) {
          if (this.incrementalUpdateType === 'incrementalAdd') {
            this.incrementalConfig.incrementalAdd = this.sql
          } else {
            this.incrementalConfig.incrementalDelete = this.sql
          }
          this.incrementalConfig.tableId = this.table.id
          task.startTime = new Date(task.startTime).getTime()
          task.endTime = new Date(task.endTime).getTime()
          task.tableId = this.table.id
          const form = JSON.parse(JSON.stringify(task))
          form.extraData = JSON.stringify(form.extraData)
          const dataSetTaskRequest = {
            datasetTableTask: form,
            datasetTableIncrementalConfig: this.incrementalConfig
          }
          post('/dataset/task/save', dataSetTaskRequest).then(response => {
            this.$message({
              message: '保存成功',
              type: 'success',
              showClose: true
            })
            this.update_task = false
            this.resetTaskForm()
            this.listTask()
            this.listTaskLog()
          })
        } else {
          return false
        }
      })
    },
    changeTaskStatus(task) {
      const param = task
      param.status = task.status === 'Underway' ? 'Pending' : 'Underway'
      post('/dataset/task/updateStatus', task).then(response => {
        task.status = param.status
        this.$message({
          message: '状态切换成功',
          type: 'success',
          showClose: true
        })
      })
    },
    deleteTask(task) {
      this.$confirm('确认删除', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        post('/dataset/task/delete/' + task.id, null).then(response => {
          this.$message({
            message: '删除成功',
            type: 'success',
            showClose: true
          })
          this.resetTaskForm()
          this.listTask()
          this.listTaskLog()
        })
      }).catch(() => {
      })
    },
    closeTask() {
      this.update_task = false
      this.resetTaskForm()
    },
    onSimpleCronChange() {
      if (this.taskForm.extraData.simple_cron_type === 'minute') {
        if (this.taskForm.extraData.simple_cron_value < 1 || this.taskForm.extraData.simple_cron_value > 59) {
          this.$message({ message: '分钟不能小于1，大于59', type: 'warning', showClose: true })
          this.taskForm.extraData.simple_cron_value = 59
        }
        this.taskForm.cron = '0 0/' + this.taskForm.extraData.simple_cron_value + ' * * * ? *'
        return
      }
      if (this.taskForm.extraData.simple_cron_type === 'hour') {
        if (this.taskForm.extraData.simple_cron_value < 1 || this.taskForm.extraData.simple_cron_value > 23) {
          this.$message({ message: '小时不能小于1，大于23', type: 'warning', showClose: true })
          this.taskForm.extraData.simple_cron_value = 23
        }
        this.taskForm.cron = '0 0 0/' + this.taskForm.extraData.simple_cron_value + ' * * ? *'
        return
      }
      if (this.taskForm.extraData.simple_cron_type === 'day') {
        if (this.taskForm.extraData.simple_cron_value < 1 || this.taskForm.extraData.simple_cron_value > 31) {
          this.$message({ message: '天不能小于1，大于31', type: 'warning', showClose: true })
          this.taskForm.extraData.simple_cron_value = 31
        }
        this.taskForm.cron = '0 0 0 1/' + this.taskForm.extraData.simple_cron_value + ' * ? *'
        return
      }
    },
    onRateChange() {
      if (this.taskForm.rate === 'SIMPLE') {
        this.taskForm.end = '0'
        this.taskForm.endTime = ''
        this.taskForm.cron = ''
      }
      if (this.taskForm.rate === 'SIMPLE_CRON') {
        this.taskForm.cron = '0 0 0/1 *  * ? *'
      }
      if (this.taskForm.rate === 'CRON') {
        this.taskForm.cron = '00 00 * ? * * *'
      }
    },
    listTaskLog(loading = true) {
      const params = { 'conditions': [{ 'field': 'dataset_table_task_log.table_id', 'operator': 'eq', 'value': this.table.id }], 'orders': [] }
      post('/dataset/taskLog/list/' + this.table.type + '/' + this.page.currentPage + '/' + this.page.pageSize, params, loading).then(response => {
        this.taskLogData = response.data.listObject
        this.page.total = response.data.itemCount
        this.taskLogLastRequestComplete = true
      }).catch(() => {
        this.taskLogLastRequestComplete = true
      })
    },
    handleSizeChange(val) {
      this.page.pageSize = val
      this.listTaskLog()
    },
    handleCurrentChange(val) {
      this.page.currentPage = val
      this.listTaskLog()
    },
    resetTaskForm() {
      this.taskForm = {
        name: '',
        type: 'all_scope',
        startTime: '',
        rate: 'SIMPLE',
        endTime: '',
        end: '0',
        extraData: {
          simple_cron_type: 'hour',
          simple_cron_value: 1
        }
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
    insertParamToCodeMirror(param) {
      const pos1 = this.$refs.myCm.codemirror.getCursor()
      const pos2 = {}
      pos2.line = pos1.line
      pos2.ch = pos1.ch
      this.$refs.myCm.codemirror.replaceRange(param, pos2)
    },
    cronChange(val) {
      this.taskForm.cron = val
    }
  }
}
</script>

<style scoped>
  .el-divider--horizontal {
    margin: 12px 0;
  }

  .el-radio{
    margin-right: 10px;
  }
  .el-radio>>>.el-radio__label{
    font-size: 12px;
  }

  .dialog-css >>> .el-dialog__header {
    padding: 20px 20px 0;
  }

  .dialog-css >>> .el-dialog__body {
    padding: 10px 20px 20px;
  }

  .el-form-item {
    margin-bottom: 10px;
  }

  .codemirror {
    height: 100px;
    overflow-y: auto;
  }
  .codemirror >>> .CodeMirror-scroll {
    height: 100px;
    overflow-y: auto;
  }

  .err-msg{
    font-size: 12px;
    word-break:normal;
    width:auto;
    display:block;
    white-space:pre-wrap;
    word-wrap : break-word ;
    overflow: hidden ;
  }

  .blackTheme .dialog_cancel_button {
    background-color: #171b22 !important;
    color: #2681ff !important;
  }

  .span{
    font-size: 12px;
  }
</style>
