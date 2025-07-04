<template>
  <div>
    <el-form ref="form" :model="form" :label-width="labelWidth" :rules="rules">
      <el-form-item label="数据集" prop="id">
        <select-tree
          @chooseDataColection="hadleChooseDataColection"
          :sqlId="sqlId"
        ></select-tree>
      </el-form-item>
      <!-- 交叉表请求参数不同 重新 -->
      <el-form-item
        label="行表头"
        prop="rowHead"
        v-if="activeObj.component.prop == 'crosstable'"
      >
        <el-select
          v-model="form.rowHead"
          @change="selectDimensionField"
          placeholder="请选择行表头"
          multiple
          :multiple-limit="dimensionFieldMultipleNum"
          clearable
          size="mini"
        >
          <el-option
            v-for="item in tableFields.dimensionListData"
            :label="item.name"
            :value="item.id"
            :key="item.id"
          ></el-option>
        </el-select>
        <el-button
          :disabled="form.dimensionField.length === 0"
          type="text"
          class="sort-set-btn"
          icon="el-icon-setting"
          @click="
            sortSet('维度', form.dimensionField, tableFields.dimensionListData)
          "
        ></el-button>
      </el-form-item>
      <el-form-item
        label="列表头"
        prop="colHead"
        v-if="activeObj.component.prop == 'crosstable'"
      >
        <el-select
          v-model="form.colHead"
          @change="selectDimensionField"
          placeholder="请选择列表头"
          multiple
          :multiple-limit="dimensionFieldMultipleNum"
          clearable
          size="mini"
        >
          <el-option
            v-for="item in tableFields.dimensionListData"
            :label="item.name"
            :value="item.id"
            :key="item.id"
          ></el-option>
        </el-select>
        <el-button
          :disabled="form.dimensionField.length === 0"
          type="text"
          class="sort-set-btn"
          icon="el-icon-setting"
          @click="
            sortSet('维度', form.dimensionField, tableFields.dimensionListData)
          "
        ></el-button>
      </el-form-item>
      <el-form-item
        label="标额"
        prop="target"
        v-if="activeObj.component.prop == 'crosstable'"
      >
        <el-select
          v-model="form.target"
          placeholder="请选择标额"
          multiple
          :multiple-limit="measureFieldMultipleNum"
          clearable
          size="mini"
        >
          <el-option
            v-for="item in tableFields.quotaListData"
            :label="item.name"
            :value="item.id"
            :key="item.id"
          ></el-option>
        </el-select>
        <el-button
          :disabled="form.measureField.length === 0"
          type="text"
          class="sort-set-btn"
          icon="el-icon-setting"
          @click="sortSet('标额', form.measureField, tableFields.quotaListData)"
        ></el-button>
      </el-form-item>
      <el-form-item
        label="维度"
        prop="dimensionField"
        v-if="isShowDimensionField"
      >
        <el-select
          v-model="form.dimensionField"
          @change="selectDimensionField"
          placeholder="请选择维度"
          multiple
          :multiple-limit="dimensionFieldMultipleNum"
          clearable
          size="mini"
        >
          <el-option
            v-for="item in tableFields.dimensionListData"
            :label="item.name"
            :value="item.id"
            :key="item.id"
          ></el-option>
        </el-select>
        <el-button
          :disabled="form.dimensionField.length === 0"
          type="text"
          class="sort-set-btn"
          icon="el-icon-setting"
          @click="
            sortSet('维度', form.dimensionField, tableFields.dimensionListData)
          "
        ></el-button>
      </el-form-item>
      <el-form-item
        label="标题"
        prop="titleField"
        v-if="activeObj.component.prop === 'eventRankingList'"
      >
        <el-select
          v-model="form.titleField"
          placeholder="请选择标题"
          multiple
          :multiple-limit="dimensionFieldMultipleNum"
          clearable
          size="mini"
          @change="sameSelectVal"
        >
          <el-option
            v-for="item in tableFields.dimensionListData"
            :label="item.name"
            :value="item.id"
            :key="item.id"
            :disabled="item.isDisabled"
          ></el-option>
        </el-select>
        <el-button
          :disabled="form.titleField.length === 0"
          type="text"
          class="sort-set-btn"
          icon="el-icon-setting"
          @click="
            sortSet('标题', form.titleField, tableFields.dimensionListData)
          "
        ></el-button>
      </el-form-item>
      <el-form-item
        label="内容"
        prop="contentField"
        v-if="activeObj.component.prop === 'eventRankingList' ||activeObj.component.prop === 'textRankingList'"
      >
        <el-select
          v-model="form.contentField"
          placeholder="请选择内容"
          multiple
          :multiple-limit="dimensionFieldMultipleNum"
          clearable
          size="mini"
          @change="sameSelectVal"
        >
          <el-option
            v-for="item in tableFields.dimensionListData"
            :label="item.name"
            :value="item.id"
            :key="item.id"
            :disabled="item.isDisabled"
          ></el-option>
        </el-select>
        <el-button
          :disabled="form.contentField.length === 0"
          type="text"
          class="sort-set-btn"
          icon="el-icon-setting"
          @click="
            sortSet('内容', form.contentField, tableFields.dimensionListData)
          "
        ></el-button>
      </el-form-item>
      <el-form-item
        label="时间"
        prop="timeField"
        v-if="activeObj.component.prop === 'eventRankingList'"
      >
        <el-select
          v-model="form.timeField"
          placeholder="请选择时间"
          multiple
          :multiple-limit="dimensionFieldMultipleNum"
          clearable
          size="mini"
          @change="sameSelectVal"
        >
          <el-option
            v-for="item in tableFields.dimensionListData"
            :label="item.name"
            :value="item.id"
            :key="item.id"
            :disabled="item.isDisabled"
          ></el-option>
        </el-select>
        <el-button
          :disabled="form.timeField.length === 0"
          type="text"
          class="sort-set-btn"
          icon="el-icon-setting"
          @click="
            sortSet('时间', form.timeField, tableFields.dimensionListData)
          "
        ></el-button>
      </el-form-item>
      <el-form-item
        :label="measureFieldLabel"
        prop="measureField"
        v-if="isShowMeasureFieldLabel"
      >
        <el-select
          v-model="form.measureField"
          @change="selectMeasureField"
          placeholder="请选择度量"
          multiple
          :multiple-limit="measureFieldMultipleNum"
          clearable
          size="mini"
        >
          <el-option
            v-for="item in tableFields.quotaListData"
            :label="item.name"
            :value="item.id"
            :key="item.id"
          ></el-option>
        </el-select>
        <el-button
          :disabled="form.measureField.length === 0"
          type="text"
          class="sort-set-btn"
          icon="el-icon-setting"
          @click="sortSet('度量', form.measureField, tableFields.quotaListData)"
        ></el-button>
      </el-form-item>
      <el-form-item
        label="维度/度量"
        label-width="110px"
        prop="dimensionField"
        v-if="activeObj.component.prop === 'text'"
      >
        <el-select
          v-model="form.dimensionField"
          placeholder="请选择"
          multiple
          :multiple-limit="measureFieldMultipleNum"
          clearable
          size="mini"
        >
          <el-option
            v-for="item in fieldListData"
            :label="item.name"
            :value="item.id"
            :key="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        label="图例"
        v-if="
          activeObj.component.prop === 'bar' ||
          activeObj.component.prop === 'line' ||
          activeObj.component.prop === 'line-bar' ||
          activeObj.component.prop === 'newPictorialBar'
        "
      >
        <el-select
          v-model="form.legendField"
          placeholder="请选择图例"
          multiple
          clearable
          size="mini"
          :multiple-limit="legendFieldMultipleNum"
        >
          <el-option
            v-for="item in tableFields.dimensionListData"
            :label="item.name"
            :value="item.id"
            :key="item.id"
          ></el-option>
        </el-select>
        <el-button
          :disabled="form.legendField.length === 0"
          type="text"
          class="sort-set-btn"
          icon="el-icon-setting"
          @click="
            sortSet('图例', form.legendField, tableFields.dimensionListData)
          "
        ></el-button>
      </el-form-item>
      <!-- 这里没有和图例共用一个的原因是虽然都是绑定legendField，但是下拉框绑定的数组为了可读性故重写一个表单项目 -->
      <el-form-item
        label="度量实际值"
        v-if="activeObj.component.prop === 'radar'"
      >
        <el-select
          v-model="form.legendField"
          placeholder="请选择实际值"
          multiple
          clearable
          :multiple-limit="legendFieldMultipleNum"
          size="mini"
        >
          <el-option
            v-for="item in tableFields.quotaListData"
            :label="item.name"
            :value="item.id"
            :key="item.id"
          ></el-option>
        </el-select>
        <el-button
          :disabled="form.legendField.length === 0"
          type="text"
          class="sort-set-btn"
          icon="el-icon-setting"
          @click="
            sortSet(
              '度量实际值',
              form.legendField,
              tableFields.dimensionListData
            )
          "
        ></el-button>
      </el-form-item>
    </el-form>
    <el-divider></el-divider>
    <div class="data-flow-wp">
      <div class="ds-line">
        <div class="ds-title">
          <span class="ds-type-text">数据源数据</span>
          <el-button
            size="mini"
            class="myBtn previewBtn"
            plain
            @click="previewData"
            >数据预览</el-button
          >
        </div>
      </div>
      <div class="ds-line mt5">
        <span class="ds-type-text">数据响应结果 ( 只读 ) </span>
      </div>
      <div class="ds-dots">
        <span class="ds-dot active"></span>
        <span class="ds-dot"></span>
        <span class="ds-dot"></span>
      </div>
    </div>
    <div class="data-response">
      <monaco-editor
        v-model="previewJSON"
        :height="200"
        v-if="tabsActive === '1'"
      ></monaco-editor>
    </div>
    <el-button
      plain
      icon="el-icon-refresh-left"
      class="refreshBtn myBtn"
      @click="handleRes"
      >刷新数据</el-button
    >
    <el-dialog
      title="提示"
      :visible.sync="setSortDialogVisible"
      width="30%"
      :before-close="handleClose"
      destroy-on-close
    >
      <el-form
        :model="replacefieldConditionForm"
        size="mini"
        label-width="90px"
        v-if="JSON.stringify(replacefieldConditionForm) != '{}'"
        style="maxHeight: 500px;overflow-y: auto;"
      >
        <div
          class="box-card"
          v-for="(item, index) in replacefieldConditionForm.sortConditionS"
          :key="index"
          :inline="true"
        >
          <span>{{ replacefieldConditionForm.sortOfKind + (index + 1) }}</span>
          <el-form-item label="显示:">
            <el-input v-model="item.fieldName" disabled></el-input>
          </el-form-item>
          <div style="display: flex">
            <el-form-item label="排序">
              <el-select v-model="item.sortType" placeholder="请选择活动区域">
                <el-option label="正序" value="ASC"></el-option>
                <el-option label="倒序" value="DESC"></el-option>
                <el-option label="无" value="UNSORT"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              label="颗粒化"
              v-if="
                item.deType===1 &&
                (activeObj.component.prop == 'bar' ||
                  activeObj.component.prop == 'line' ||
                  activeObj.component.prop == 'table')
              "
            >
              <el-select v-model="item.granular" placeholder="请选择活动区域">
                <el-option label="年" value="year"></el-option>
                <el-option label="季" value="quarter"></el-option>
                <el-option label="月" value="month"></el-option>
                <el-option label="周" value="week"></el-option>
                <el-option label="日" value="day"></el-option>
                <el-option label="时" value="hours"></el-option>
                <el-option label="分" value="minutes"></el-option>
                <el-option label="秒" value="seconds"></el-option>
              </el-select>
            </el-form-item>
          </div>
          <el-form-item label="优先级:">
            <el-input v-model="item.priority"></el-input>
          </el-form-item>
          <el-form-item label="格式转义:" v-if="main.activeObj.component.name==='table'">
            <el-switch v-model="item.transferred"> </el-switch>
            <div v-if="item.transferred" class="transferred">
              <el-table :data="item.rule" border>
                <el-table-column label="原值">
                  <template slot-scope="scoped">
                    <el-input
                      v-model="scoped.row.originalValue"
                      placeholder=""
                      size="mini"
                    ></el-input>
                  </template>
                </el-table-column>
                <el-table-column label="转义值">
                  <template slot-scope="scoped">
                    <el-input
                      v-model="scoped.row.transferredValue"
                      placeholder=""
                      size="mini"
                    ></el-input>
                  </template>
                </el-table-column>
                <el-table-column label="操作">
                  <template slot-scope="scoped">
                    <el-button
                      type="danger"
                      icon="el-icon-delete"
                      circle
                      @click="handledel(scoped.$index, item)"
                    ></el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-button
                type="primary"
                size="default"
                icon="el-icon-circle-plus-outline"
                style="width: 100%"
                @click="handleAdd(item.rule)"
              ></el-button>
            </div>
          </el-form-item>
          <div v-if="main.activeObj.component.name==='table'&& replacefieldConditionForm.sortOfKind==='维度'" class="dimension-list">
            <el-form-item label="设为主维度:">
              <el-switch v-model="item.dimension" :disabled="item.dimensionDisabled"></el-switch>
            </el-form-item>
            <el-form-item label="绑定子维度:" v-if="item.dimension">
              <el-select v-model="item.dimensionList" @change="dimensionChange" multiple @visible-change="visibleChange($event,item.fieldId)" placeholder="请选择">
                <el-option
                  v-for="itemOption in dimensionData"
                  :key="itemOption.id"
                  :label="itemOption.name"
                  :value="itemOption.id">
                </el-option>
              </el-select>
            </el-form-item>
          </div>
        </div>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button
          @click="
            () => {
              setSortDialogVisible = false
            }
          "
          >取 消</el-button
        >
        <el-button type="primary" @click="setSortConfirm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import MonacoEditor from '@/page/components/editor'
import SelectTree from './selectTree'
import { fieldListDQ } from '@/api/dataCollection'
import { EventBus } from '@/bus.js'
import { sqlSearch } from '@/api/sqlSearch'
import {
  dimensionFieldRequireList,
  measureFieldRequireList,
} from '@/option/config.js'
import { deepClone } from '../../echart/util'
const createForm = function (id = '') {
  let form = {
    id, // 数据集对象id
    dimensionField: [],
    measureField: [],
    legendField: [],
    titleField: [], //标题
    contentField: [], //内容
    timeField: [], //时间
    colHead: [], // 列表头
    rowHead: [], // 行表头
    target: [], //标额
    fieldCondition: {
      sortConditionS: [],
    }, // 排序设置对象
  }
  // if (activeObj.component.prop === 'table') {
  //   form.dimensionField = []
  //   form.measureField = []
  //   form.legendField = ''
  // } else if (activeObj.component.prop === 'radar') {
  //   form.dimensionField = ''
  //   form.measureField = ''
  //   form.legendField = []
  // } else {
  //   form.dimensionField = ''
  //   form.measureField = ''
  //   form.legendField = ''
  // }
  return form
}
export default {
  name: 'sqlForm',
  inject: ['contain', 'main'],
  props: {
    activeObj: {
      type: Object,
      default: function () {
        return {}
      },
    },
    tableFields: {
      type: Object,
      default: function () {
        return {
          dimensionListData: [],
          quotaListData: [],
        }
      },
    },
    tabsActive: {
      type: String,
      default: '0',
    },
    activeIndex: [null, String],
  },
  data() {
    let checkDataCollectId = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请选择数据集'))
      } else {
        callback()
      }
    }
    let checkDimensionField = (rule, value, callback) => {
      if (value.length === 0) {
        if (
          this.dimensionFieldRequireList.includes(this.activeObj.component.prop)
        ) {
          return callback(new Error('请选择维度'))
        } else {
          callback()
        }
      } else {
        callback()
      }
    }
    let checkMeasureField = (rule, value, callback) => {
      if (value.length === 0) {
        if (
          this.measureFieldRequireList.includes(this.activeObj.component.prop)
        ) {
          return callback(new Error('请选择度量'))
        } else {
          callback()
        }
      } else {
        callback()
      }
    }
    let checkTitleField = (rule, value, callback) => {
      if (value.length === 0) {
        if (this.activeObj.component.prop === 'eventRankingList') {
          return callback(new Error('请选择标题'))
        } else {
          callback()
        }
      } else {
        callback()
      }
    }
    let checkContentField = (rule, value, callback) => {
      if (value.length === 0) {
        if (this.activeObj.component.prop === 'eventRankingList' ||this.activeObj.component.prop === 'textRankingList') {
          return callback(new Error('请选择内容'))
        } else {
          callback()
        }
      } else {
        callback()
      }
    }
    let checkTimeField = (rule, value, callback) => {
      if (value.length === 0) {
        if (this.activeObj.component.prop === 'eventRankingList') {
          return callback(new Error('请选择时间'))
        } else {
          callback()
        }
      } else {
        callback()
      }
    }
    return {
      dimensionDataSelect:[],
      dimensionData:[],
      form: createForm(),
      rules: {
        id: [
          { validator: checkDataCollectId, required: true, trigger: 'change' },
        ],
        dimensionField: [
          {
            validator: checkDimensionField,
            message: '请选择维度',
            trigger: 'change',
          },
        ],
        measureField: [
          {
            validator: checkMeasureField,
            message: '请选择度量',
            trigger: 'change',
          },
        ],
        colHead: [
          { required: true, message: '请选择列表头', trigger: 'change' },
        ],
        rowHead: [
          { required: true, message: '请选择行表头', trigger: 'change' },
        ],
        target: [{ required: true, message: '请选择标额', trigger: 'change' }],
        titleField: [
          {
            validator: checkTitleField,
            required: true,
            trigger: 'change',
          },
        ],
        contentField: [
          {
            validator: checkContentField,
            required: true,
            trigger: 'change',
          },
        ],
        timeField: [
          {
            validator: checkTimeField,
            required: true,
            trigger: 'change',
          },
        ],
      },
      sqlId: '', // 选中的数据集的id
      previewJSON: '',
      measureFieldLabel: '度量',
      labelWidth: '80px',
      dimensionFieldMultipleNum: 1, // 为0的时候不限制选择框的数量，只能选一个的时候改为1，相当于单选
      measureFieldMultipleNum: 1,
      setSortDialogVisible: false,
      fieldConditionForm: {
        sortOfKind: '99999',
        sortConditionS: [],
        tabelSortConditionS:[],
      },
      replacefieldConditionForm: {},
      isTime: false,
      granular: 'day',
      currentObj: null, // 当前选中的组件对象
      dimensionFieldRequireList: dimensionFieldRequireList,
      measureFieldRequireList: measureFieldRequireList,
    }
  },
  watch: {
    activeIndex: {
      handler: function (newVal, oldVal) {
        if (newVal) {
          if (this.contain.showTips) {
            this.currentObj = this.contain.findTipsList(newVal)
          } else {
            this.currentObj = this.contain.findlist(newVal)
          }
          console.log(
            '-----》',
            this.currentObj.sqlData,
            this.activeObj.component.prop
          )
          if (this.currentObj.sqlData && this.currentObj.sqlData.id) {
            this.sqlId = this.currentObj.sqlData.id 
            if (this.activeObj.component.prop === 'eventRankingList'||this.activeObj.component.prop === 'textRankingList') {
              if (this.currentObj.sqlData.dimensionField) {
                this.form.titleField = []
                this.form.contentField = []
                this.form.timeField = []
                const tempDimen =
                  this.currentObj.sqlData.dimensionField.split(',')
                this.form.titleField.push(tempDimen[0])
                this.form.contentField.push(tempDimen[1])
                this.form.timeField.push(tempDimen[2])
              }
            } else {
              this.form.dimensionField = this.currentObj.sqlData.dimensionField
                ? this.currentObj.sqlData.dimensionField.split(',')
                : []
            }

            this.form.measureField = this.currentObj.sqlData.measureField
              ? this.currentObj.sqlData.measureField.split(',')
              : []
            if (this.currentObj.sqlData.legendField) {
              this.form.legendField = this.currentObj.sqlData.legendField
                ? this.currentObj.sqlData.legendField.split(',')
                : []
            }
            if (this.currentObj.sqlData.colHead) {
              this.form.colHead = this.currentObj.sqlData.colHead
                ? this.currentObj.sqlData.colHead.split(',')
                : []
            }
            if (this.currentObj.sqlData.rowHead) {
              this.form.rowHead = this.currentObj.sqlData.rowHead
                ? this.currentObj.sqlData.rowHead.split(',')
                : []
            }
            if (this.currentObj.sqlData.target) {
              this.form.target = this.currentObj.sqlData.target
                ? this.currentObj.sqlData.target.split(',')
                : []
            }
            if (this.currentObj.sqlData.fieldCondition) {
              this.form.fieldCondition = this.deepClone(
                this.currentObj.sqlData.fieldCondition
              ) // 新增排序对象 因为这个传递的是对象所以深拷贝一下
            }
            if(this.currentObj.component.name==="table"){
              if (this.currentObj.option.tabelSortConditionS) {
              this.form.fieldCondition.sortConditionS = this.deepClone(
                this.currentObj.option.tabelSortConditionS
              ) 
            }
            }
          } else {
            console.log('121331321')
            // 如果是未配置过sqlData的就要重置表单
            this.form = createForm()
            this.sqlId = ''
            this.$emit('update:tableFields', {
              dimensionListData: [],
              quotaListData: [],
            })
          }
        }
        this.previewJSON = ''
      },
      deep: true,
      immediate: true,
    },
    // 'form.id': {
    //   handler: function (newVal, oldVal) {
    //     if (newVal && oldVal) {
    //       console.log('触发了')
    //       this.form = createForm(newVal)
    //     }
    //   },
    // },
    'activeObj.component.prop': {
      handler: function (newVal, oldVal) {
        switch (newVal) {
          case 'radar':
            // 如果是雷达图
            this.measureFieldLabel = '度量标准值'
            this.labelWidth = '110px'
            this.legendFieldMultipleNum = 0
            break
          case 'table':
            // 如果是表格，维度和度量要改成多选
            this.dimensionFieldMultipleNum = 0
            this.measureFieldMultipleNum = 0
            break
          case 'crosstable':
            // 如果是表格，维度和度量要改成多选
            this.dimensionFieldMultipleNum = 0
            this.measureFieldMultipleNum = 3
            break
          case 'line-bar':
            // 如果是折柱混合图，维度和度量要改成多选
            this.dimensionFieldMultipleNum = 0
            this.measureFieldMultipleNum = 2
            this.legendFieldMultipleNum = 1
            break
          case 'basicarea':
            // 如果是折柱混合图，维度和度量要改成多选
            this.dimensionFieldMultipleNum = 0
            this.measureFieldMultipleNum = 2
            break
          case 'tree':
            // 如果是树状图，维度和度量要改成多选
            this.dimensionFieldMultipleNum = 0
            this.measureFieldMultipleNum = 2
            break
          case 'newTree':
            // 如果是侧边菜单栏，维度要改成多选
            this.dimensionFieldMultipleNum = 0
            break
          case 'dataStorage':
            // 如果是表格，维度和度量要改成多选
            this.dimensionFieldMultipleNum = 0
            this.measureFieldMultipleNum = 0
            break
          case 'scatter':
            // 如果是表格，维度和度量要改成多选
            this.dimensionFieldMultipleNum = 0
            this.measureFieldMultipleNum = 2
            break
          default:
            // 默认值
            this.measureFieldLabel = '度量'
            this.labelWidth = '80px'
            this.dimensionFieldMultipleNum = 1
            this.measureFieldMultipleNum = 1
            this.legendFieldMultipleNum = 1
        }
      },
      immediate: true,
    },
    setSortDialogVisible: {
      handler(newVal) {
        if (newVal) {
          // 模态框打开时，赋值备份数据
          this.replacefieldConditionForm = deepClone(this.fieldConditionForm)
        } else {
          // console.log('模态框关闭')
        }
      },
    },
  },
  computed: {
    // 如果当前选中的组件是柱状图、雷达图就展示图例选项，此外雷达图的measureField选项，label字段改为度量标准值，legendField选项的label字段改为度量实际值并且由单选改为多选
    // 再写一个measureField选项label动态变化的计算属性
    // measureFieldLabel() {
    //   switch (this.activeObj.component.prop) {
    //     case 'radar':
    //       return '度量标准值'
    //       break
    //     default:
    //       return '度量'
    //   }
    // },
    // 再写一个控制label宽度变化的计算属性
    // labelWidth() {
    //   switch (this.activeObj.component.prop) {
    //     case 'radar':
    //       return '110px'
    //       break
    //     default:
    //       return '80px'
    //   }
    // },
    // 当前组件为表格时，维度dimensionField为可以多选的下拉框
    // dimensionFieldMultipleNum() {
    //   switch (this.activeObj.component.prop) {
    //     case 'table':
    //       return true
    //       break
    //     default:
    //       return falsehandledel
    //   }
    // },
    // 当前组件为表格时，维度measureField为可以多选的下拉框
    // measureFieldMultipleNum() {
    //   switch (this.activeObj.component.prop) {
    //     case 'table':
    //       return true
    //       break
    //     default:
    //       return false
    //   }
    // },
    // 是否显示维度
    isShowDimensionField() {
      let arr = [
        'gauge',
        'text',
        'crosstable',
        'progressView',
        'progressBar',
        // 'ZebraProgressBar',
        'scatter',
        'eventRankingList',
        'textRankingList',
      ] // 在数组中的组件不展示
      let isIncludes = arr.includes(this.activeObj.component.prop)
      if (isIncludes) {
        return false
      } else {
        return true
      }
    },
    // 是否显示度量
    isShowMeasureFieldLabel() {
      let arr = ['text', 'crosstable', 'eventRankingList','textRankingList'] // 在数组中的组件不展示
      let isIncludes = arr.includes(this.activeObj.component.prop)
      if (isIncludes) {
        return false
      } else {
        return true
      }
    },
    // 数组包含了维度和度量
    fieldListData() {
      return this.tableFields.dimensionListData.concat(
        this.tableFields.quotaListData
      )
    },
  },
  created() {
    // console.log('sqlForm')
  },
  mounted() {
    EventBus.$on('refreshData', checkoutResult => {
      this.$refs.form.validate(valid => {
        if (valid) {
          const sqlData = this.handleSqlData()
          this.$emit('on-handleRes', sqlData)
        } else {
          this.$message.error('请填写必填项')
          return false
        }
      })
    })
    EventBus.$on('updatePreviewJSON', previewJSON => {
      this.previewJSON = previewJSON
    })
  },
  destroyed() {
    EventBus.$off()
  },
  methods: {
    dimensionChange() {
      if(this.replacefieldConditionForm.sortConditionS){
        let selectData = []
        this.replacefieldConditionForm.sortConditionS.forEach(item=>{
          if(item.dimensionList.length>0){
              item.dimensionList.forEach(itemList=>{
                selectData.push(itemList)
              })
            }
        })
        this.replacefieldConditionForm.sortConditionS.forEach(item=>{
          item.dimensionDisabled = false;
        })
        for(let i = 0; i < selectData.length; i++){
          for(let b = 0; b < this.replacefieldConditionForm.sortConditionS.length; b++){
            if(selectData[i]===this.replacefieldConditionForm.sortConditionS[b].fieldId){
              this.replacefieldConditionForm.sortConditionS[b].dimensionDisabled = true;
            }
          }
        }
      }
    },
    visibleChange(value,fieldId){
      console.log(value,fieldId)
      if(value){
        let selectList = this.deepClone(this.dimensionDataSelect);
        let selectedType = this.replacefieldConditionForm.sortConditionS || [];
        let selectedData = []
        selectedType.forEach((item)=>{
          if(item.dimension){
            selectedData.push(item.fieldId)
          }
          if(item.fieldId!==fieldId){
            if(item.dimensionList.length>0){
              item.dimensionList.forEach(itemList=>{
                selectedData.push(itemList)
              })
            }
          }
        })
        for(let b = 0 ; b < selectList.length; b++){
          for(let i = 0; i < selectedData.length; i++){
            if(selectList[b].id===selectedData[i]){
              selectList.splice(b,1)
              // selectList[b].disabled = true
            }
          }
        }
        this.dimensionData = this.deepClone(selectList);
      }
    },
    // 处理SelectTree组件选中值后
    hadleChooseDataColection(obj) {
      if (obj) {
        this.form.id = obj.id
        if (obj.isClean) {
          this.form.dimensionField = []
          this.form.measureField = []
          this.form.legendField = []
        }
        fieldListDQ(obj.id).then(response => {
          const resData = response.data.data
          let dimensionListData = this.deepClone(resData.dimensionList)
          let quotaListData = this.deepClone(resData.quotaList)
          // 把这个值传给父组件，因为条件选择那里会用到
          this.$emit('update:tableFields', { dimensionListData, quotaListData })
          // 更新已选维度和度量
          this.updateCheckedDimAndMea({ dimensionListData, quotaListData })
        })
      } else {
        // 如果传递过来的数值是一个null 即清空了数据集选择项 就要置空其他选项
        this.form = createForm()
        // 同时更新父组件的tableFields恢复为初始值
        this.$emit('update:tableFields', {
          dimensionListData: [],
          quotaListData: [],
        })
      }
    },
    //添加表格行
    handleAdd(item) {
      let row = {
        transferredValue: '',
        originalValue: '',
      }
      item.push(row)
    },
    //删除表格行
    handledel(index, item) {
      item.rule.splice(index, 1)
    },
    // 点击刷新按钮
    handleRes() {
      // this.$refs.form.validate((valid) => {
      //   if (valid) {
      //     // 如果sql数据配置表单校验通过,准备好要发送的数据
      //     const sqlData = this.handleSqlData()
      //     // 那么还需判断下当前选中的这个组件是否配置了条件查询
      //     if (this.activeObj.sqlData.whereCustomFilterS && this.activeObj.sqlData.whereCustomFilterS.length > 0) {
      //       // 如果当前组件存在配置条件的数组且长度大于0那么就要通知兄弟组件 condition.vue去校验下表单是否填写完整了,同时把sqlData传递给condition.vue
      //       EventBus.$emit('checkoutFilter', sqlData)
      //     } else {
      //       // 没有的话就直接出发父组件的刷新数据事件就好了
      //       this.$emit('on-handleRes', sqlData)
      //     }
      //   } else {
      //     this.$message.error('请填写必填项')
      //     return false
      //   }
      // })
      // this.fieldConditionForm.sortConditionS.some(
      //   (el) => el.transferred && el.rule.length != 0
      // )

      let cb = sqlData => {
        // console.log('sqlData',sqlData)
        this.$emit('on-handleRes', sqlData)
      }
      this.checkoutForm(cb)
    },
    // 点击数据预览按钮
    previewData() {
      // this.$refs.form.validate((valid) => {
      //   if (valid) {
      //     // 如果sql数据配置表单校验通过,准备好要发送的数据
      //     const sqlData = this.handleSqlData()
      //     // 那么还需判断下当前选中的这个组件是否配置了条件查询
      //     if (this.activeObj.sqlData.whereCustomFilterS && this.activeObj.sqlData.whereCustomFilterS.length > 0) {
      //       // 如果当前组件存在配置条件的数组且长度大于0那么就要通知兄弟组件 condition.vue去校验下表单是否填写完整了,同时把sqlData传递给condition.vue
      //       EventBus.$emit('checkoutFilter', sqlData)
      //     } else {
      //       // 没有的话就直接出发父组件的刷新数据事件就好了
      //       const type = this.activeObj.component.prop
      //       sqlSearch(type, sqlData).then((res) => {
      //           this.previewJSON = res.data
      //         });
      //     }
      //   } else {
      //     this.$message.error('请填写必填项')
      //     return false
      //   }
      // })
      let cb = sqlData => {
        const type = this.activeObj.component.prop
        sqlSearch(type, sqlData).then(res => {
          this.previewJSON = res.data
          console.log(res.data)
        })
      }
      this.checkoutForm(cb, true)
    },
    // 校验表单的方法 因为数据预览 和 刷新数据都要用到所以写一个公共的
    checkoutForm(cb, isPreview = false) {
      this.$refs.form.validate(valid => {
        if (valid) {
          // 如果sql数据配置表单校验通过,准备好要发送的数据

          const sqlData = this.handleSqlData()
          console.log('我进来了', sqlData, this.activeObj)
          // 那么还需判断下当前选中的这个组件是否配置了条件查询
          // 2022/3/24发现的新问题如果当前图表配置了条件查询就不会调用传进来的cb函数，因此这个公共方法还需要添加一个参数以表明是否是数据预览，isPreview
          if (
            this.activeObj.sqlData.whereCustomFilterS &&
            this.activeObj.sqlData.whereCustomFilterS.length > 0
          ) {
            // 如果当前组件存在配置条件的数组且长度大于0那么就要通知兄弟组件 condition.vue去校验下表单是否填写完整了,同时把sqlData传递给condition.vue
            EventBus.$emit('checkoutFilter', { sqlData, isPreview })
          } else {
            cb(sqlData)
          }
        } else {
          this.$message.error('请填写必填项')
          return false
        }
      })
    },
    // checkoutForm(cb) {
    //   this.$refs.form.validate((valid) => {
    //     if (valid) {
    //       cb()
    //     } else {
    //       this.$message.error('请填写必填项')
    //       return false
    //     }
    //   })
    // },
    handleSqlData() {
      let sqlData = {
        id: '',
        dimensionField: [],
        colHead: [],
        rowHead: [],
        target: [],
        measureField: [],
        fieldCondition: {
          dateFormatConditionS: [],
          sortConditionS: [],
        },
      }
      sqlData.id = this.form.id
      if(this.activeObj.component.prop==='table'){
       
          let tableHeadList = this.form.fieldCondition.sortConditionS || []
          let tableHead = {
            tableHead:[]
          }
          let child = []
          tableHeadList.forEach(item=>{
            if(item.dimension){
              if(item.dimensionList.length>0){
                item.dimensionList.forEach(itemChild=>{
                  child.push({
                    head:itemChild
                  })
                })
              }
              tableHead.tableHead.push({
                head:item.fieldId,
                child:child,
              })
            }
          })
          sqlData = Object.assign(sqlData,tableHead)
      }
      if (this.activeObj.component.prop === 'eventRankingList'||this.activeObj.component.prop === 'textRankingList') {
        console.log('我是事件排序')
        const tempA = this.form.titleField.concat(
          this.form.contentField,
          this.form.timeField
        )
        sqlData.dimensionField = tempA.join()
      } else {
        sqlData.dimensionField = this.form.dimensionField.join()
      }
      sqlData.measureField = this.form.measureField.join()
      sqlData.colHead = this.form.colHead.join()
      sqlData.rowHead = this.form.rowHead.join()
      sqlData.target = this.form.target.join()
      if(this.activeObj.component.prop==='table'){
          let tabelSortConditionS = this.deepClone(this.form.fieldCondition.sortConditionS)
          this.main.activeObj.option.tabelSortConditionS = tabelSortConditionS
        }
      // sortConditionS中排序方式为无的项不上传
      sqlData.fieldCondition.sortConditionS =
        this.form.fieldCondition.sortConditionS.filter(
          item => item.sortType !== 'UNSORT'
        )
      // 颗粒化
      this.form.fieldCondition.sortConditionS.forEach(granularItem=>{
        if(granularItem.deType===1){
          sqlData.fieldCondition.dateFormatConditionS.push({
            fieldId: granularItem.fieldId,
            format: this.getIsTime(granularItem.granular).format,
            granularity: this.getIsTime(granularItem.granular).granularity,
          })
        }
      })
      switch (this.activeObj.component.prop) {
        case 'bar':
        case 'line':
        case 'line-bar':
        case 'biaxlinebar':
        case 'radar': // 因为当当前所选组件为雷达图的时候this.form.legendField是一个数组，但后台始终需要的是字符串
        case 'newPictorialBar':
          sqlData.legendField = this.form.legendField.join()
          break
        default:
          break
      }
      return sqlData
    },
    getIsTime(granular){
      let format, granularity
      if(granular === 'year'){
        format ='%Y年';
        granularity = 'year';
      }else if(granular === 'quarter'){
        format ='第%s季度';
        granularity = 'quarter';
      }else if(granular === 'month'){
        format ='%Y年%m月';
        granularity = 'month';
      }else if(granular === 'week'){
        format ='%Y年%m月第%w周';
        granularity = 'week';
      }else if(granular === 'day'){
        format ='%Y年%m月%d日';
        granularity = 'day';
      }else if(granular === 'hours'){
        format ='%Y年%m月%d日 %h时';
        granularity = 'hour';
      }else if(granular === 'minutes'){
        format ='%Y年%m月%d日 %h:%m';
        granularity = 'minute';
      }else if(granular === 'seconds'){
        format ='%Y年%m月%d日 %h:%m:%ss';
        granularity = 'second';
      }else{
        format = '';
        granularity = '';
      }
      return {
        format:format,
        granularity:granularity
      }
    },
    // 点击维度 度量 图例旁的设置按钮
    sortSet(sortOfKind, field, fieldList) {
      this.setSortDialogVisible = true;
      let dimensionData = field.map(id=>{
         return fieldList.find(fieldListItem=>{
            return fieldListItem.id === id
          }) 
      })
      this.dimensionData = this.deepClone(dimensionData)
      this.dimensionDataSelect = this.deepClone(dimensionData)
      this.fieldConditionForm.sortOfKind = sortOfKind;
      // 获取颗粒化数据
      let dateFormatConditionS  = []
      if (this.currentObj.sqlData.fieldCondition) {
        if(this.currentObj.sqlData.fieldCondition.dateFormatConditionS){
          dateFormatConditionS =this.deepClone(this.currentObj.sqlData.fieldCondition.dateFormatConditionS) 
        }
      }
      // 根据多选框传过来的id数组来生成fieldConditionForm的sortConditionS数组
      this.fieldConditionForm.sortConditionS = field.map(id => {
        // 拿到id后去fieldList中查找名字
        let fieldName = fieldList.find(fieldListItem => {
          return fieldListItem.id === id
        }).name
        let deType = fieldList.find(fieldListItem => {
          return fieldListItem.id === id
        }).deType
        let granular = dateFormatConditionS.find(granularItem=>{
          return granularItem.fieldId === id
        })
        // 拿到id后去form的fieldCondition中的sortConditionS数组中查找之前保存过的与之相对应的项
        let targetArr = this.form.fieldCondition.sortConditionS
        let sortConditionItem = targetArr.find(conditionItem => {
          return (
            conditionItem.fieldId === id &&
            conditionItem.kind === sortOfKind
          )
        })
        if (sortConditionItem) {
          // 如果找到了
          let sortConditionItemCopy = this.deepClone(sortConditionItem)
          console.log('sortConditionItem',sortConditionItem)
          sortConditionItemCopy.fieldName = fieldName
          sortConditionItemCopy.deType = deType;
          return sortConditionItemCopy
        } else {
          // 如果没找到
          let parms = {
              fieldId: id,
              fieldName: fieldName,
              deType: deType,
              sortType: 'UNSORT',
              granular: granular ? granular.granularity : 'day',
              priority: '1',
              kind: sortOfKind,
          }
          if(this.main.activeObj.component.name==='table'){
            let tableParms = {
              transferred: false,//格式转义
              rule: [],
              dimensionDisabled:false,
              dimension:false, //设为主维度
              dimensionList:[],// 绑定子维
            }
            parms = Object.assign(tableParms,parms)
          }
          return parms
        }
      })
      console.log('this.fieldConditionForm.sortConditionS',this.fieldConditionForm.sortConditionS)
    },
    // 点击设置排序的对话框的确定按钮
    setSortConfirm() {
      // console.log(this.fieldConditionForm, this.previewJSON)
      this.fieldConditionForm = deepClone(this.replacefieldConditionForm)
      this.$nextTick(() => {
        this.fieldConditionForm.sortConditionS.forEach(item => {
          let sortConditionItem = this.form.fieldCondition.sortConditionS.find(
            conditionItem => {
              return conditionItem.fieldId === item.fieldId
            }
          )
          if (sortConditionItem) {
            // 如果找到了，说明之前保存设置过排序，那么就用现在的值覆盖之前的值
            Object.assign(sortConditionItem, item)
          } else {
            // 没有的话就把该项添加进去
            this.form.fieldCondition.sortConditionS.push(item)
          }
        })
        this.setSortDialogVisible = false
      })
    },
    //是否为时间类型
    selectDimensionField() {
      console.log('触发了')
      // let ids = this.form.dimensionField[0]
      // if (ids) {
      //   let { deType, deExtractType } = this.tableFields.dimensionListData.find(
      //     item => item.id == ids
      //   )
      //   console.log(deExtractType)
      //   // deType === 1 && deExtractType === 1
      //   //   ? (this.isTime = true)
      //   //   : (this.isTime = false)
      //   // deType:1 打开颗粒化,deExtractType没有用
      //   this.isTime = deType === 1
      //   console.log(this.time, this.activeObj.component.prop)
      // }
      this.updateCheckedDimAndMea(this.tableFields)
    },
    selectMeasureField() {
      this.updateCheckedDimAndMea(this.tableFields)
    },
    // 更新已选维度度量
    updateCheckedDimAndMea({ dimensionListData, quotaListData }) {
      if (this.activeObj.component.prop === 'table') {
        let arr = []
        const checkedDimAndMeaArr = this.form.dimensionField.concat(
          this.form.measureField
        )
        checkedDimAndMeaArr.forEach(id => {
          let option = dimensionListData
            .concat(quotaListData)
            .find(item => item.id === id)
          if (option) arr.push(option)
        })
        EventBus.$emit('updateCheckedDimAndMea', arr)
        this.$emit('updateCheckedDimAndMea', arr)
      }
    },
    sameSelectVal() {
      //标题，内容，时间不能相同
      console.log('数据', this.tableFields.dimensionListData)
      this.tableFields.dimensionListData =
        this.tableFields.dimensionListData.map(item => {
          if (
            item.id === this.form.titleField[0] ||
            item.id === this.form.contentField[0] ||
            item.id === this.form.timeField[0]
          ) {
            return {
              ...item,
              isDisabled: true,
            }
          } else {
            return {
              ...item,
              isDisabled: false,
            }
          }
        })
    },
  },
  components: {
    SelectTree,
    MonacoEditor,
  },
}
</script>

<style scoped lang="scss">
.refreshBtn {
  width: 80%;
  margin-left: 30px;
}
.previewBtn {
  margin-left: 120px;
}
.el-divider--horizontal {
  width: 80%;
  margin: 0px 33px 0;
}
.data-flow-wp {
  position: relative;
  padding: 10px;
}
.dimension-list{
  /deep/.el-form-item__label{
    width: 100px !important;
  }
}
.ds-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 5px 0 5px 20px;

  &.mt5 {
    margin-top: 5px;
  }

  .ds-type-text {
    display: inline-block;
    padding: 6px;
    font-size: 12px;
    line-height: 12px;
    color: var(--datav-font-color);
    text-align: center;
    background: #39414d;
    border-radius: 1px;
    box-shadow: 0 0 5px -3px #000;
  }

  .ds-action-btn {
    width: 120px;
  }

  .refresh-btn {
    font-size: 16px;
    cursor: pointer;
    transition: color 0.2s;

    &:hover {
      color: var(--datav-main-color);
    }
  }
}

.ds-dots {
  position: absolute;
  top: 25px;
  left: 15px;
  display: flex;
  width: 1px;
  height: 87px;
  background: #4c5768;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
}

.ds-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  background: #7d8081;
  border-radius: 4px;
  box-shadow: 0 0 3px #000;
  transition: background 0.2s;

  &.active {
    background: #2681ff;
  }
}
.data-response {
  padding: 10px;
  padding-top: 0;
}
button.sort-set-btn {
  padding-left: 10px;

  // vertical-align: middle;
  // cursor: pointer;
  // i {
  //   font-size: 18px !important;
  //   color: #2681ff !important;
  // }
}
::v-deep .el-table th,
.el-table tr {
  color: #859094;
  font-size: 12px;
}
::v-deep .el-table th {
  padding: 4px 0;
}
.transferred {
  width: 100%;
  margin: 0 auto;
}
</style>
