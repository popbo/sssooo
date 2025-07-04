<template>
<el-row>
    <el-form ref="form" :model="fieldForm" size="mini" class="row-style">
      <el-form-item>
        <span style="width: 60px;font-size: 12px">字段名</span>
        <el-input v-model="fieldForm.name" size="mini" placeholder="请输入名称" />
      </el-form-item>
    </el-form>

    <el-row style="height: 420px;">
      <el-col :span="14" style="height: 100%">
        <el-row>
          <el-row>
            <span>字段表达式</span>
            <codemirror
              ref="myCm"
              v-model="fieldForm.originName"
              class="codemirror"
              :options="cmOption"
              @ready="onCmReady"
              @focus="onCmFocus"
              @input="onCmCodeChange"
            />
          </el-row>
          <el-row style="margin-top: 10px;">
            <el-form ref="form" :model="fieldForm" size="mini" class="row-style">
              <el-form-item>
                <span style="width: 80px;font-size: 12px">数据类型</span>
                <el-radio-group v-model="fieldForm.groupType" size="mini">
                  <el-radio-button label="d">维度</el-radio-button>
                  <el-radio-button label="q">指标</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <span style="width: 80px;font-size: 12px">字段类型</span>
                <el-select v-model="fieldForm.deType" size="mini">
                  <el-option
                    v-for="item in fields"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                    <span style="float: left">
                      <svg-icon v-if="item.value === 0" icon-class="field_text" class="field-icon-text" />
                      <svg-icon v-if="item.value === 1" icon-class="field_time" class="field-icon-time" />
                      <svg-icon v-if="item.value === 2 || item.value === 3" icon-class="field_value" class="field-icon-value" />
                      <svg-icon v-if="item.value === 5" icon-class="field_location" class="field-icon-location" />
                    </span>
                    <span style="float: left;font-size: 12px">{{ item.label }}</span>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-form>
          </el-row>
        </el-row>
      </el-col>
      <el-col :span="10" style="height: 100%;border-left: 1px solid #393b4a;">
        <el-col :span="12" style="height: 100%" class="padding-lr">
          <span>
            点击引用字段
            <el-tooltip class="item" effect="dark" placement="bottom">
              <div slot="content">
                引用字段以 "[" 开始， "]" 结束
                <br>
                请勿修改引用内容，否则将引用失败
                <br>
                若输入与引用字段相同格式的内容，将被当作引用字段处理
              </div>
              <i class="el-icon-info" style="cursor: pointer;" />
            </el-tooltip>
          </span>
          <el-input
            v-model="searchField"
            size="mini"
            placeholder="搜索"
            prefix-icon="el-icon-search"
            clearable
          />
          <div class="field-height">
            <span>维度</span>
            <draggable
              v-model="dimensionData"
              :options="{group:{name: 'drag',pull:'clone'},sort: true}"
              animation="300"
              class="drag-list"
              :disabled="true"
            >
              <transition-group>
                <span v-for="item in dimensionData" :key="item.id" class="item-dimension" :title="item.name" @click="insertFieldToCodeMirror('['+item.id+']')">
                  <svg-icon v-if="item.deType === 0" icon-class="field_text" class="field-icon-text" />
                  <svg-icon v-if="item.deType === 1" icon-class="field_time" class="field-icon-time" />
                  <svg-icon v-if="item.deType === 2 || item.deType === 3" icon-class="field_value" class="field-icon-value" />
                  <svg-icon v-if="item.deType === 5" icon-class="field_location" class="field-icon-location" />
                  {{ item.name }}
                </span>
              </transition-group>
            </draggable>
          </div>
          <div class="field-height">
            <span>指标</span>
            <draggable
              v-model="quotaData"
              :options="{group:{name: 'drag',pull:'clone'},sort: true}"
              animation="300"
              class="drag-list"
              :disabled="true"
            >
              <transition-group>
                <span v-for="item in quotaData" :key="item.id" class="item-quota" :title="item.name" @click="insertFieldToCodeMirror('['+item.id+']')">
                  <svg-icon v-if="item.deType === 0" icon-class="field_text" class="field-icon-text" />
                  <svg-icon v-if="item.deType === 1" icon-class="field_time" class="field-icon-time" />
                  <svg-icon v-if="item.deType === 2 || item.deType === 3" icon-class="field_value" class="field-icon-value" />
                  <svg-icon v-if="item.deType === 5" icon-class="field_location" class="field-icon-location" />
                  <span>{{ item.name }}</span>
                </span>
              </transition-group>
            </draggable>
          </div>
        </el-col>
        <el-col :span="12" style="height: 100%" class="padding-lr">
          <span>
            点击引用函数
            <el-tooltip class="item" effect="dark" placement="bottom">
              <div slot="content">
                使用数据集对应数据库类型所支持的函数，语法同对应数据库
                <br>
                如日期格式化：MySQL使用DATE_FORMAT(date,format)；Oracle使用TO_DATE(X,[,fmt])
                <br>
                非直连模式数据集，使用Doris数据库函数，可参考Doris官网 http://doris.apache.org/master/zh-CN/
              </div>
              <i class="el-icon-info" style="cursor: pointer;" />
            </el-tooltip>
          </span>
          <el-input
            v-model="searchFunction"
            size="mini"
            placeholder="搜索"
            prefix-icon="el-icon-search"
            clearable
          />
          <el-row class="function-height">
            <el-popover
              v-for="(item,index) in functionData"
              :key="index"
              class="function-pop"
              placement="right"
              width="200"
              trigger="hover"
              :open-delay="500"
            >
              <p class="pop-title">{{ item.name }}</p>
              <p class="pop-info">{{ item.func }}</p>
              <p class="pop-info">{{ item.desc }}</p>
              <span slot="reference" class="function-style" @click="insertParamToCodeMirror(item.func)">{{ item.func }}</span>
            </el-popover>
          </el-row>
        </el-col>
      </el-col>
    </el-row>

    <el-row>
      <div class="dialog-button">
        <el-button size="mini" @click="closeCalcField">取消</el-button>
        <el-button :disabled="!fieldForm.name || !fieldForm.originName" type="primary" size="mini" @click="saveCalcField">确认</el-button>
      </div>
    </el-row>
  </el-row>
</template>

<script>
import draggable from 'vuedraggable'
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
import { post } from '@/api/dataCollection'

export default {
  name: 'CalcFieldEdit',
  components: { codemirror, draggable },
  props: {
    param: {
      type: Object,
      required: true
    },
    tableFields: {
      type: Object,
      required: true
    },
    field: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      fieldForm: {
        id: null,
        name: '',
        groupType: 'd',
        deType: 0,
        originName: '',
        tableId: this.param.id,
        checked: 1,
        columnIndex: this.tableFields.dimensionList.length + this.tableFields.quotaList.length,
        size: 0,
        extField: 2
      },
      cmOption: {
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
      fields: [
        { label: '文本', value: 0 },
        { label: '时间', value: 1 },
        { label: '数值', value: 2 },
        { label: '数值' + '(' + '小数' + ')', value: 3 },
        { label: '地理位置', value: 5 }
      ],
      functions: [],
      searchField: '',
      searchFunction: '',
      dimensionData: [],
      quotaData: [],
      functionData: []
    }
  },
  computed: {
    codemirror() {
      return this.$refs.myCm.codemirror
    }
  },
  watch: {
    'param': function() {
      this.initFunctions()
    },
    'field': {
      handler: function() {
        this.initField()
      },
      deep: true
    },
    'tableFields': function() {
      this.dimensionData = JSON.parse(JSON.stringify(this.tableFields.dimensionList)).filter(ele => ele.extField === 0)
      this.quotaData = JSON.parse(JSON.stringify(this.tableFields.quotaList)).filter(ele => ele.extField === 0)
    },
    'searchField': function(val) {
      if (val && val !== '') {
        this.dimensionData = JSON.parse(JSON.stringify(this.tableFields.dimensionList.filter(ele => ele.name.toLocaleLowerCase().includes(val.toLocaleLowerCase()) && ele.extField === 0)))
        this.quotaData = JSON.parse(JSON.stringify(this.tableFields.quotaList.filter(ele => ele.name.toLocaleLowerCase().includes(val.toLocaleLowerCase()) && ele.extField === 0)))
      } else {
        this.dimensionData = JSON.parse(JSON.stringify(this.tableFields.dimensionList)).filter(ele => ele.extField === 0)
        this.quotaData = JSON.parse(JSON.stringify(this.tableFields.quotaList)).filter(ele => ele.extField === 0)
      }
    },
    'searchFunction': function(val) {
      if (val && val !== '') {
        this.functionData = JSON.parse(JSON.stringify(this.functions.filter(ele => { return ele.func.toLocaleLowerCase().includes(val.toLocaleLowerCase()) })))
      } else {
        this.functionData = JSON.parse(JSON.stringify(this.functions))
      }
    }
  },
  mounted() {
    console.log('calcField')
    this.$refs.myCm.codemirror.on('keypress', () => {
      this.$refs.myCm.codemirror.showHint()
    })
    this.initFunctions()
    this.initField()
    this.dimensionData = JSON.parse(JSON.stringify(this.tableFields.dimensionList)).filter(ele => ele.extField === 0)
    this.quotaData = JSON.parse(JSON.stringify(this.tableFields.quotaList)).filter(ele => ele.extField === 0)
  },
  methods: {
    onCmReady(cm) {
      this.codemirror.setSize('-webkit-fill-available', 'auto')
    },
    onCmFocus(cm) {
      // console.log('the editor is focus!', cm)
    },
    onCmCodeChange(newCode) {
      // console.log(newCode)
      this.fieldForm.originName = newCode
    },
    insertParamToCodeMirror(param) {
      const pos1 = this.$refs.myCm.codemirror.getCursor()
      const pos2 = {}
      pos2.line = pos1.line
      pos2.ch = pos1.ch
      this.$refs.myCm.codemirror.replaceRange(param, pos2)
    },
    insertFieldToCodeMirror(param) {
      const pos1 = this.$refs.myCm.codemirror.getCursor()
      const pos2 = {}
      pos2.line = pos1.line
      pos2.ch = pos1.ch
      this.$refs.myCm.codemirror.replaceRange(param, pos2)
    },

    initFunctions() {
      post('/dataset/function/listByTableId/' + this.param.id, null).then(response => {
        this.functions = response.data.data
        this.functionData = JSON.parse(JSON.stringify(this.functions))
      })
    },

    initField() {
      if (this.field.id) {
        this.fieldForm = JSON.parse(JSON.stringify(this.field))
      } else {
        this.fieldForm = JSON.parse(JSON.stringify(this.fieldForm))
      }
    },

    closeCalcField() {
      this.resetField()
      this.$emit('onEditClose', {})
    },

    saveCalcField() {
      if (this.fieldForm.name && this.fieldForm.name.length > 50) {
        this.$message.error('字段名不能超过50个字符')
        return
      }
      if (!this.fieldForm.id) {
        this.fieldForm.type = this.fieldForm.deType
        this.fieldForm.deExtractType = this.fieldForm.deType
        this.fieldForm.tableId = this.param.id
        this.fieldForm.columnIndex = this.tableFields.dimensionList.length + this.tableFields.quotaList.length
      }
      post('/dataset/field/save', this.fieldForm).then(response => {
        this.closeCalcField()
      })
    },

    resetField() {
      this.fieldForm = {
        id: null,
        name: '',
        groupType: 'd',
        deType: 0,
        originName: '',
        tableId: this.param.id,
        checked: 1,
        columnIndex: this.tableFields.dimensionList.length + this.tableFields.quotaList.length,
        size: 0,
        extField: 2
      }
      this.dimensionData = []
      this.quotaData = []
      this.searchField = ''
      this.searchFunction = ''
    }
  }
}
</script>

<style scoped lang="scss">

  .row-style /deep/.el-form-item__label{
    font-size: 12px;
  }
  .row-style /deep/.el-form-item--mini.el-form-item{
    margin-bottom: 10px;
  }
  .row-style /deep/.el-form-item__content{
    display: flex;
    flex-direction: row;
    width: 100%;
  }

  .codemirror {
    height: 300px;
    overflow-y: auto;
    font-size: 12px;
  }
  .codemirror /deep/ .CodeMirror-scroll {
    height: 300px;
    overflow-y: auto;
    font-size: 12px;
  }

  .padding-lr {
    padding: 0 4px;
  }
  .field-height{
    height: calc(50% - 25px);
    margin-top: 4px;
  }
  .drag-list {
    height: calc(100% - 26px);
    overflow:auto;
  }
  .item-dimension {
    padding: 2px 10px;
    margin: 2px 2px 0 2px;
    border: solid 1px #393b4a;
    text-align: left;
    color: #fbfbfb;
    /*background-color: rgba(35,46,64,.05);*/
    // background-color: white;
    display: block;
    word-break: break-all;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  .blackTheme .item-dimension {
    border: solid 1px ;
    border-color: #495865;
    color: #F2F6FC;
    background-color: var(--MainBG);
  }

  .item-dimension + .item-dimension {
    margin-top: 2px;
  }

  .item-dimension:hover {
    color: #1890ff;
    background: #e8f4ff;
    border-color: #a3d3ff;
    cursor: pointer;
  }

  .blackTheme .item-dimension:hover {
    /* color: var(--Main); */
    background: var(--ContentBG);
    /* cursor: pointer; */
  }

  .item-quota {
    padding: 2px 10px;
    margin: 2px 2px 0 2px;
    border: solid 1px #393b4a;
    text-align: left;
    color: #fbfbfb;
    /*background-color: rgba(35,46,64,.05);*/
    // background-color: white;
    display: block;
    word-break: break-all;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  .blackTheme .item-quota {

    border: solid 1px ;
    border-color: #495865;
    color: #F2F6FC;
    background-color: var(--MainBG);

  }

  .item-quota + .item-quota {
    margin-top: 2px;
  }

  .item-quota:hover {
    color: #67c23a;
    background: #f0f9eb;
    border-color: #b2d3a3;
    cursor: pointer;
  }

  .blackTheme .item-quota:hover {
    background: var(--ContentBG);
  }

  span{
    font-size: 12px;
  }
  .function-style{
    color: #fbfbfb;
    display: block;
    padding: 2px 4px;
    cursor: pointer;
    margin: 4px 0;
    word-break: break-word;
    border: solid 1px #393b4a;
  }

  .blackTheme .function-style {
    border: solid 1px ;
    border-color: #495865;
    color: #F2F6FC;
    background-color: var(--MainBG);
  }
  .function-style:hover {
    background: #e8f4ff;
    border-color: #a3d3ff;
    color:#1890ff;
    cursor: pointer;
  }
  .blackTheme .function-style:hover {
    color: #1890ff;
    background: var(--ContentBG);
  }
  .function-height{
    height: calc(100% - 50px);
    overflow: auto;
    margin-top: 4px;
  }
  .function-pop/deep/.el-popover{
    padding: 6px!important;
  }
  .pop-title{
    margin: 6px 0 0 0;
    font-size: 14px;
    font-weight: 500;
  }
  .pop-info{
    margin: 6px 0 0 0;
    font-size: 10px;
  }
  .dialog-button{
    float: right;
    margin-top: 10px;
  }
  /deep/ input {
  background-color: #181a24 !important;
  border-color: #393b4a !important;
  color: #bfbfbf !important;
}
 
</style>
