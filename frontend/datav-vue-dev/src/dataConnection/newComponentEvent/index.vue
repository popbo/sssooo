<template>
  <div class="eventlist-box">
    <div class="mybtn-box">
      <el-button plain icon="el-icon-plus" class="addBtn" @click="addCondition"
        >添加事件</el-button
      >
    </div>
    <div>
      <el-collapse v-if="eventListCopy.length > 0" @change="collapseChange">
        <el-collapse-item
          v-for="(f_item, f_index) in eventListCopy"
          :key="f_item.eventId"
        >
          <template slot="title">
            <!-- 条件字段 -->
            <span class="tj" v-if="!f_item.isEdite">{{
              f_item.eventName
            }}</span>
            <!-- 编辑条件名称输入框 -->
            <el-input
              v-else
              v-model="f_item.eventName"
              :ref="f_item.eventId"
              @focus.stop="handleFocus"
              @blur.stop="handleBlur(f_item)"
              size="mini"
            ></el-input>
            <!-- 编辑图标 -->
            <i class="el-icon-edit" @click.stop="editClick(f_item)"></i>
            <!-- 占位 -->
            <div style="flex: 1 1 0%"></div>
            <!-- 删除图标 -->
            <i
              class="el-icon-delete"
              @click.stop="deleteCondition(f_index)"
            ></i>
          </template>
          <el-form
            :ref="'form' + f_item.eventId"
            :model="f_item"
            label-width="120px"
          >
            <el-form-item label="开放接口" v-if="activeIndex">
              <el-switch
                v-model="f_item.openInterface"
                @change="switchOpenInterface(f_item, $event)"
              >
              </el-switch>
            </el-form-item>
            <el-form-item v-if="f_item.openInterface" label="接口类型">
              <el-select
                v-model="f_item.openInterfaceType"
                @change="openInterfaceTypeChange(f_item, $event)"
                size="mini"
              >
                <el-option
                  v-for="(Item, index) in openInterfaceTypeList"
                  :label="Item.label"
                  :value="Item.value"
                  :key="index"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="接口地址" v-if="f_item.openInterface">
              <avue-input
                v-model="f_item.openInterfaceAdress"
                size="mini"
                readonly
              ></avue-input>
            </el-form-item>
            <el-form-item label="日志" v-if="activeIndex && getTypeEventAction(f_item.eventAction)">
              <el-switch
                v-model="f_item.log"
                @change="logChange(f_item, $event)"
              >
              </el-switch>
              <el-button v-if="f_item.log" @click="openLog(f_item)" style="margin-left: 20px;" type="primary" plain size="mini">编辑日志内容</el-button>
            </el-form-item>
            <!-- <div class="log-style" v-if="f_item.log">
              <div class="params_title">日志内容</div>
              <div class="paramJSON_edit_box">
                  <monaco-editor
                    v-model="f_item.logContent"
                    :height="200"
                    ref="editor"
                  ></monaco-editor>
              </div>
            </div> -->
            <el-form-item label="事件类型">
              <el-select
                v-model="f_item.eventType"
                class="set-condition-select-ziduan"
                placeholder="请选择事件类型"
                clearable
                size="mini"
              >
                <el-option
                  v-for="eventTypeItem in eventTypeArr"
                  :label="eventTypeItem.label"
                  :value="eventTypeItem.value"
                  :key="eventTypeItem.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              label="配置按钮"
              v-if="f_item.eventType === 'columnClickFormatter'"
            >
              <el-select
                v-model="f_item.buttonType"
                class="set-condition-select-ziduan"
                placeholder="请选择配置按钮"
                clearable
                size="mini"
              >
                <el-option
                  v-for="eventTypeItem in buttonEventList"
                  :label="eventTypeItem.label"
                  :value="eventTypeItem.value"
                  :key="eventTypeItem.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="事件行为">
              <el-select
                v-model="f_item.eventAction"
                class="set-condition-select-ziduan"
                placeholder="请选择事件行为"
                clearable
                size="mini"
                @change="handleEventActionChange(f_item, $event)"
              >
                <el-option
                  v-for="eventActionItem in eventActionArr"
                  :label="eventActionItem.label"
                  :value="eventActionItem.value"
                  :key="eventActionItem.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <div v-if="f_item.eventAction === 6">
              <el-form-item label="url地址">
                <avue-input v-model="f_item.urlAdress" size="mini"></avue-input>
              </el-form-item>
              <el-form-item label="弹框尺寸" class="bullet-box">
                <el-input-number
                  :min="0"
                  controls-position="right"
                  style="width: 90px"
                  v-model="f_item.urlWidth"
                  size="mini"
                ></el-input-number>
                <span>*</span>
                <el-input-number
                  :min="0"
                  controls-position="right"
                  style="width: 90px"
                  v-model="f_item.urlHeight"
                  size="mini"
                ></el-input-number>
              </el-form-item>
              <div
                style="
                  color: #859094;
                  margin: 10px 30px;
                  padding-bottom: 15px;
                  border-bottom: 1px solid #393b4a;
                "
              >
                <span>参数配置</span>
                <i
                  style="float: right; padding: 3px 0"
                  class="el-icon-circle-plus-outline"
                  @click="urlAddItem(f_item)"
                ></i>
              </div>
              <el-row :gutter="20" style="color: #859094; padding: 10px 30px">
                <el-col :span="10">参数名</el-col>
                <el-col :span="10">参数值</el-col>
              </el-row>
              <div style="padding: 10px 30px">
                <el-row
                  :gutter="20"
                  style="margin-bottom: 10px"
                  v-for="(item, index) in f_item.urlParameter"
                  :key="index"
                >
                  <el-col :span="10">
                    <avue-input
                      v-model="item.urlParameterName"
                      size="mini"
                    ></avue-input>
                  </el-col>
                  <el-col :span="10">
                    <el-select
                      v-model="item.urlParameterValue"
                      class="set-condition-select-table"
                      placeholder="请选择"
                      clearable
                      width="125px"
                      size="mini"
                    >
                      <el-option
                        v-for="eventTypeItem in tableParameter"
                        :label="eventTypeItem.label"
                        :value="eventTypeItem.value"
                        :key="eventTypeItem.value"
                      >
                      </el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="4">
                    <i
                      class="el-icon-delete"
                      style="float: right; padding: 3px 0"
                      @click="urlDeleteTerm(f_item, index)"
                    ></i>
                  </el-col>
                </el-row>
              </div>
            </div>
            <el-form-item
              label="子类"
              v-if="
                [1, 2, 3, 9].includes(f_item.eventAction) &&
                f_item.eventAction !== 6
              "
            >
              <el-select
                v-model="f_item.linkComIndexArr"
                class="set-condition-select-ziduan"
                placeholder="请选择关联组件"
                clearable
                size="mini"
                multiple
                @remove-tag="handleRemoveLinkComItem"
                @change="handleLinkComItemChange(f_item, $event)"
                filterable
              >
                <el-option
                  v-for="linkComItem in allComList"
                  :label="linkComItem.name"
                  :value="linkComItem.index"
                  :key="linkComItem.index"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <tempalte
              v-if="f_item.eventAction === 1 || f_item.eventAction === 9"
            >
              <!-- API联动 -->
              <tempalte
                v-for="(apiChildItem, apiChildIndex) in f_item.child"
                :key="apiChildIndex"
              >
                <div class="comName">
                  {{ apiChildItem.index | transformName }}API联动
                </div>
                <el-form-item label="参数名称">
                  <el-input
                    v-model="apiChildItem.paramName"
                    class="set-condition-select-ziduan"
                    size="mini"
                  ></el-input>
                </el-form-item>
                <el-form-item label="参数值" v-if="f_item.eventAction === 9">
                  <el-select v-model="apiChildItem.keyName" size="mini">
                    <el-option
                      v-for="option in paramsTypeArr"
                      :label="option.label"
                      :key="option.value"
                      :value="option.value"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="键值名称" v-else>
                  <el-input
                    v-model="apiChildItem.keyName"
                    class="set-condition-select-ziduan"
                    size="mini"
                  ></el-input>
                </el-form-item>
              </tempalte>
              <!-- SQL联动 -->
              <tempalte
                v-for="(sqlChildItem, sqlChildIndex) in f_item.sqlChild"
                :key="sqlChildIndex"
              >
                <div class="comName">
                  {{ sqlChildItem.index | transformName }}SQL联动
                </div>
                <sqlSet
                  :sqlChildItem="sqlChildItem"
                  :paramsTypeArr="paramsTypeArr"
                  @updateSqlChildItem="Object.assign(sqlChildItem, $event)"
                ></sqlSet>
              </tempalte>
              <!-- websocket联动 -->
              <tempalte
                v-for="(
                  websocketChildItem, websocketChildIndex
                ) in f_item.websocketChild"
                :key="websocketChildIndex"
              >
                <div class="comName">
                  {{ websocketChildItem.index | transformName }}websocket联动
                </div>
                <div class="params_title">编辑参数</div>
                <div class="paramJSON_edit_box">
                  <monaco-editor
                    v-model="websocketChildItem.paramFun"
                    :height="200"
                    ref="editor"
                  ></monaco-editor>
                </div>
              </tempalte>
            </tempalte>
            <template v-if="f_item.eventAction === 4">
              <el-form-item label="请求类型">
                <avue-select
                  v-model="f_item.serverChild.method"
                  :dic="methodList"
                  size="mini"
                >
                </avue-select>
              </el-form-item>
              <el-form-item label="请求地址">
                <avue-input v-model="f_item.serverChild.url" size="mini">
                </avue-input>
              </el-form-item>
              <el-form-item label="认证方式">
                <el-select
                  v-model="f_item.serverChild.authenticationMethod"
                  placeholder="请选择"
                  size="mini"
                >
                  <el-option
                    v-for="(item, index) in authenticationArr"
                    :key="index"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </el-form-item>
              <template
                v-if="f_item.serverChild.authenticationMethod === 'basicAuth'"
              >
                <el-form-item label="认证地址">
                  <avue-input
                    v-model="
                      f_item.serverChild.authenticationForm.authenticationUrl
                    "
                    size="mini"
                  ></avue-input>
                </el-form-item>
                <el-form-item label="用户名">
                  <avue-input
                    v-model="f_item.serverChild.authenticationForm.userName"
                    size="mini"
                  ></avue-input>
                </el-form-item>
                <el-form-item label="密码">
                  <avue-input
                    v-model="f_item.serverChild.authenticationForm.password"
                    size="mini"
                  ></avue-input>
                </el-form-item>
              </template>
              <div class="params_title">编辑请求头</div>
              <div class="paramJSON_edit_box">
                <monaco-editor
                  v-model="f_item.serverChild.paramHeader"
                  :height="100"
                  ref="editor"
                ></monaco-editor>
              </div>
              <div class="params_title">编辑参数</div>
              <div class="paramJSON_edit_box">
                <monaco-editor
                  v-model="f_item.serverChild.paramFun"
                  :height="200"
                  ref="editor"
                ></monaco-editor>
              </div>
            </template>
            <template v-if="f_item.eventAction === 5">
              <el-form-item label="动作">
                <avue-select
                  v-model="paramTransValue"
                  :dic="paramtransfer"
                  size="mini"
                >
                </avue-select>
              </el-form-item>
              <el-form-item label="选择页面">
                <!-- <div style="color:#fff">{{f_item}}</div> -->
                <el-select
                  v-model="f_item.currenScreen"
                  size="mini"
                  @change="selectScreen($event, f_item)"
                >
                  <el-option
                    v-for="option in screeCateList"
                    :label="option.title"
                    :key="option.id"
                    :value="{ value: option.id, option: option }"
                  ></el-option>
                </el-select>
              </el-form-item>
              <div
                style="
                  color: #859094;
                  margin: 10px 30px;
                  padding-bottom: 15px;
                  border-bottom: 1px solid #393b4a;
                "
              >
                <span>参数配置</span>
                <i
                  style="float: right; padding: 3px 0"
                  class="el-icon-circle-plus-outline"
                  @click="addItem(f_item)"
                ></i>
              </div>
              <el-row :gutter="20" style="color: #859094; padding: 10px 30px">
                <el-col :span="10">参数名</el-col>
                <el-col :span="10">参数值</el-col>
              </el-row>
              <div style="padding: 10px 30px">
                <el-row
                  :gutter="20"
                  style="margin-bottom: 10px"
                  v-for="(item, index) in f_item.paramNameValList"
                  :key="index"
                >
                  <el-col :span="10">
                    <el-select v-model="item.currenScreenName" size="mini">
                      <el-option
                        v-for="option in f_item.currentScreeParamList"
                        :label="option.paramsKey"
                        :key="option.paramsValue"
                        :value="option.paramsKey"
                      ></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="10">
                    <avue-select
                      v-model="item.currentScreenVal"
                      :dic="currentScreeParamValList"
                      size="mini"
                    >
                    </avue-select>
                  </el-col>
                  <el-col :span="4">
                    <i
                      class="el-icon-delete"
                      style="float: right; padding: 3px 0"
                      @click="deleteTerm(f_item, index)"
                    ></i>
                  </el-col>
                </el-row>
              </div>
            </template>
            <template v-if="f_item.eventAction === 7">
              <el-form-item label="js脚本" class="javascript-lable">
                <el-tooltip
                      class="item-javascript"
                      effect="dark"
                      content="如果想js脚本里获取想对应的传值, jsCodeDataList里有想对应的值。"
                      placement="top-start"
                    >
                    <i class="el-icon-info"></i>
                  </el-tooltip>
                <div class="paramJSON_edit_box Js-code">
                  <monaco-editor
                    v-model="f_item.JScode"
                    :height="200"
                    ref="editor"
                    language="javascript"
                    theme="vs-dark"
                  ></monaco-editor>
                </div>
              </el-form-item>
            </template>
            <template v-if="f_item.eventAction === 8">
              <el-form-item label="请求关闭">
                <el-switch
                  size="mini"
                  v-model="f_item.settingMultiparameter.interfaceSwitch"
                ></el-switch>
              </el-form-item>
              <el-form-item label="参数名">
                <el-select
                  v-model="f_item.settingMultiparameter.lableName"
                  size="mini"
                >
                  <el-option
                    v-for="option in setParamsTypeArr"
                    :label="option.label"
                    :key="option.value"
                    :value="option.value"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="参数值">
                <el-select
                  v-model="f_item.settingMultiparameter.valueData"
                  size="mini"
                >
                  <el-option
                    v-for="option in paramsTypeArr"
                    :label="option.label"
                    :key="option.value"
                    :value="option.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </template>
          </el-form>
        </el-collapse-item>
      </el-collapse>
    </div>
    <logEdit :evnetData="evnetData" @OkLog="OkLog" v-if="logfalge" @closeLog="closeLog"></logEdit>
  </div>
</template>

<script>
// import { uuid } from '@/utils/utils'
import { uuid } from '@/utils/utils.min.js'
// import { EventBus } from '@/bus.js'
// import { sqlSearch } from '@/api/sqlSearch'
import { fieldListDQ } from '@/api/dataCollection'
import MonacoEditor from '@/page/components/editor'
import sqlSet from './sqlSet'
import logEdit from './logEdit'
import { getCurrentCate } from '@/api/visual'
import { url } from '@/config'
let __this = null
let editLogContent = `function(data){
  //data获取相对应一些内容,可通过console.log(data)查看,来配置自己想要的内容
  //可根据自己需求修改 
  return {
    behavior:"查询",
    content:"数据",
    time:"2013-12-12 12:45:00"
  }
}`
export default {
  name: 'newComponentEvent',
  inject: ['contain'],
  props: {
    activeObj: {
      type: Object,
      default: function () {
        return {}
      },
    },
    activeIndex: [null, String],
    tableFields: {
      type: Object,
      default: function () {
        return {
          dimensionListData: [],
          quotaListData: [],
        }
      },
    },
    childList: {
      type: Array,
      default: function () {
        return []
      },
    },
    authenticationArr: {
      type: Array,
      default: function () {
        return []
      },
    },
    tableColomData: {
      type: Array,
      default: function () {
        return []
      },
    },
  },
  data() {
    return {
      logfalge:false,
      evnetData:{},
      buttonEventList: [],
      tableParameter: [],
      openInterfaceTypeList: [
        {
          label: 'API接口',
          value: 'http',
        },
        {
          label: 'Websocket接口',
          value: 'ws',
        },
      ],
      isEdite: false,
      currentValue: '',
      paramTransValue: 1,
      eventListCopy: [],
      eventTypeArr: [
        {
          label: '鼠标左键单击',
          value: 'clickFormatter',
        },
        {
          label: '鼠标左键双击',
          value: 'dblClickFormatter',
        },
        {
          label: '初始化控件',
          value: 'initFormatter',
        },
        {
          label: '渲染完成',
          value: 'initFinishFormatter',
        },
        {
          label: '内容变化',
          value: 'contentChangeFormatter',
        },
      ],
      eventActionArr: [
        { label: '数据刷新', value: 1 },
        { label: '组件隐藏', value: 2 },
        { label: '组件显示', value: 3 },
        { label: '调用服务', value: 4 },
        { label: '⻚⾯联动', value: 5 },
        { label: '脚本调用', value: 7 },
        { label: '设置页面参数值', value: 8 },
      ],
      currentObj: null, // 当前选中的组件对象
      fieldTypeList: [
        { label: '度量值', value: 1 },
        { label: '维度值', value: 2 },
      ],
      // paramsTypeArr: [
      //   {
      //     label: '当前绑定值',
      //     value: 'bindValue',
      //   },
      //   {
      //     label: '图层参数值',
      //     value: 'comName',
      //   },
      // ],
      fieldListData: [],
      methodList: [
        { label: 'GET', value: 'GET' },
        { label: 'POST', value: 'POST' },
      ],
      paramtransfer: [{ label: '参数传递', value: 1 }],
      // currenScreen:'',
      screeCateList: [],
      currentScreeParamList: [],
      currentScreeParamValList: [
        {
          label: '当前值',
          value: 'value',
        },
      ],
      dimAndMeaOptions: [],
    }
  },
  watch: {
    activeIndex: {
      handler(newValue, oldValue) {
        this.currentObj = null
        if (newValue) {
          if (this.contain.tipsIndex) {
            console.log('tips查找')
            this.currentObj = this.contain.findTipsList(newValue)
          } else {
            this.currentObj = this.contain.findlist(newValue)
          }
          this.eventTypeArr = this.eventTypeMap(
            this.activeObj.component.prop,
            this.activeObj
          )
          this.eventActionArr = this.eventActionArrMap(
            this.activeObj.component.prop
          )
        } else {
          this.eventTypeArr = this.eventTypeMap('page')
          this.eventActionArr = this.eventActionArrMap('page')
        }
        let obj = this.currentObj || this.contain.config // 如果没有找到currentObj说明是在build页面添加的事件列表 ，那么就传入build的config这个值
        this.handleActiveObj(obj)
      },
      deep: true,
      immediate: true,
    },
    eventListCopy: {
      handler(newValue, oldValue) {
        if (this.currentObj) {
          this.currentObj.eventList = newValue
        } else {
          this.contain.config.eventList = newValue
        }
      },
      deep: true,
    },
    activeObj: {
      handler(newVal) {
        if (newVal.component) {
          if (newVal.component.prop === 'table') {
            let buttonEventList = []
            let list = newVal.option.buttonList
            list.forEach(item => {
              buttonEventList.push({
                value: item.tableButtonId,
                label: item.buttonContext,
              })
            })
            this.buttonEventList = buttonEventList
            let tableParameter = []
            let list1 = newVal.option.column
            this.getTableParameter(tableParameter, list1)
            this.tableParameter = tableParameter
            this.eventTypeArr = this.eventTypeMap(
              this.activeObj.component.prop,
              this.activeObj
            )
          }
        }
      },
      deep: true,
      immediate: true,
    },
  },
  created() {
    __this = this
  },
  mounted() {
    const id = this.$route ? this.$route.params.id : this.props.id
    console.log('id', id)
    getCurrentCate(id)
      .then(res => {
        if (res.data.success) {
          this.screeCateList = res.data.data
          // console.log('获取分类', this.screeCateList)
        }
      })
      .catch(err => {
        console.log(err)
      })
    if (this.activeObj.component) {
      if (this.activeObj.component.prop) {
        if (this.activeObj.component.prop === 'table') {
          this.currentScreeParamValList = this.tableColomData.map(ele => {
            return {
              label: ele.name,
              value: ele.id,
            }
          })
        }
      }
    }
  },
  destroyed() {},
  computed: {
    allComList() {
      let arr = []
      const detail = list =>{
        list.forEach(item_f=>{
          arr.push(item_f)
          if(item_f.list){
            detail(item_f.list)
          }
          if(item_f.domList){
            detail(item_f.domList)
          }
        })
      }
      detail(this.childList)
      return arr
    },
    paramsTypeArr() {
      console.log('this.contain.config', this.contain.config)
      if (this.currentObj) {
        return [
          {
            label: '当前绑定值',
            value: 'bindValue',
          },
          {
            label: '图层参数值',
            value: 'comName',
          },
        ]
      } else {
        let pageParamsList = this.contain.config.pageParamsList || []
        return pageParamsList.map(item => {
          return {
            label: item.paramsKey,
            value: item.paramsKey,
          }
        })
      }
    },
    setParamsTypeArr() {
      let pageParamsList = this.contain.config.pageParamsList || []
      return pageParamsList.map(item => {
        return {
          label: item.paramsKey,
          value: item.paramsKey,
        }
      })
    },
  },
  filters: {
    // 转义组件名称
    transformName: function (id) {
      // var p = __this.childList.filter((item) => {
      //   return item.index == id;
      // });
      var p = __this.allComList.filter(item => {
        return item.index == id
      })
      if (p.length > 0) {
        return p[0].name
      } else {
        return '未知'
      }
    },
  },
  methods: {
    getTypeEventAction(v){
      let falge = false;
      if(v===1 || v===4  || v===7 || v===8){
        falge = true
      }
      return falge
    },
    openLog(item){
      this.logfalge = true
      this.evnetData = item
    },
    OkLog(logContent){
      this.eventListCopy.forEach(item=>{
        if(item.eventId===this.evnetData.eventId){
          item.logContent = logContent
        }
      })
      this.logfalge = false
    },
    closeLog(){
      this.logfalge = false
    },
    logChange(item,value){
      if(value){
        if(item.logContent===undefined){
          item.logContent = editLogContent
        }
      }
    },
    urlAddItem(item) {
      item.urlParameter.push({
        urlParameterName: '',
        urlParameterValue: '',
      })
    },
    urlDeleteTerm(item, index) {
      // if(item.urlParameter.length<2){
      //   return this.$message.warning('请至少保留一项')
      // }
      item.urlParameter.splice(index, 1)
    },
    getTableParameter(tableParameter, list) {
      for (let i = 0; i < list.length; i++) {
        if (list[i].children && list[i].children.length) {
          this.getTableParameter(tableParameter, list[i].children)
        } else {
          tableParameter.push({
            value: list[i].prop,
            label: list[i].label,
          })
        }
      }
    },
    switchOpenInterface(f_item, value) {
      if (value) {
        axios
          .get(
            url +
              `/api/register?eventId=${f_item.eventId}&type=${f_item.openInterfaceType}`
          )
          .then(res => {
            if (res.data) {
              f_item.openInterfaceAdress = res.data.data
            }
          })
      }
    },
    openInterfaceTypeChange(f_item, value) {
      if (f_item.openInterface) {
        axios
          .get(url + `/api/register?eventId=${f_item.eventId}&type=${value}`)
          .then(res => {
            if (res.data) {
              f_item.openInterfaceAdress = res.data.data
            }
          })
      }
    },
    creatInstance(eventListLength = 1) {
      let parms = {
        log:false,
        logContent:editLogContent,
        openInterface: false,
        openInterfaceType: 'http',
        openInterfaceAdress: '',
        isEdite: false,
        eventName: '事件' + eventListLength, // 事件名称
        eventId: uuid(), // 事件唯一id
        eventType: 'clickFormatter', // 事件类型
        buttonType:
          this.buttonEventList.length > 0 ? this.buttonEventList[0].value : '',
        urlParameter: [
          {
            urlParameterName: '',
            urlParameterValue: '',
          },
        ],
        urlAdress: '',
        urlWidth: 800,
        urlHeight: 500,
        eventAction: 1, // 事件行为
        linkComIndexArr: [], // 子类
        staticChild: [],
        child: [],
        sqlChild: [],
        websocketChild: [],
        serverChild: {
          url: '',
          method: 'GET',
          // params: [],
          paramHeader: 'function(data){\n    return {}\n}', 
          paramFun: 'function(data){\n    return {}\n}', // 按照原参数编辑的方法这样就无需对填写的格式有严格要求，譬如一切字符串都需要双引号
          authenticationMethod: 'noAuth',
          authenticationForm: {
            authenticationUrl: '',
            userName: '',
            password: '',
          },
        },
        showHideChild: {
          index: [],
        },
        JScode: '',
        settingMultiparameter: {
          lableName: '',
          valueData: '',
          interfaceSwitch:false,
        },
      }
      if (this.contain.activeObj.component) {
        if (this.contain.activeObj.component.prop === 'switchMultiple') {
          parms.eventType = 'openClickFormatter'
        }
      }
      if (!this.currentObj) {
        parms.eventType = 'paramsAcceptFormatter'
      }

      return parms
    },
    editClick(item) {
      item.isEdite = true
      this.$nextTick(() => {
        this.$refs[item.eventId][0].focus()
      })
    },
    handleBlur(item) {
      item.isEdite = false
    },
    handleFocus() {},
    // 点击添加条件按钮
    addCondition() {
      const length = this.eventListCopy.length + 1
      const eventObj = this.creatInstance(length)
      this.eventListCopy.push(eventObj)
    },
    // 点击删除按钮
    deleteCondition(index) {
      this.eventListCopy.splice(index, 1)
    },
    // 写一个首次接收到到activeObj对它进行处理的函数，因为一个刚加进来的组件它的sqlData是没有添加fieldCustomFilterS，而之前保存过的组件可能有可能没有
    handleActiveObj(obj) {
      // 如果之前保存了这个值，拿到后我要自己进行一些处理
      if (obj.eventList) {
        const eventList = obj.eventList
        this.eventListCopy = this.deepClone(eventList)
      } else {
        // 没有的话置空一下
        this.eventListCopy = []
      }
    },
    handleEventActionChange(f_item, eventAction) {
      this.handleLinkComItemChange(f_item, f_item.linkComIndexArr)
    },
    handleLinkComItemChange(f_item, linkComArr) {
      // let newObj = {
      //   staticChild: [],
      //   child: [],
      //   sqlChild: [],
      //   websocketChild: [],
      // }
      // Object.assign(f_item, newObj)
      // console.log(f_item)
      // console.log('val==>', linkComArr)
      if (f_item.eventAction === 1 || f_item.eventAction === 9) {
        // 循环val然后根据请求类型分类
        // linkComArr.forEach(linkComItem => {
        //   this.allComList.forEach(comItem => {
        //     if (linkComItem === comItem.index) {
        //       switch (comItem.dataType) {
        //         case 0: // 如果关联的组件数据源是静态数据
        //           f_item.staticChild.push({ index: linkComItem })
        //           break
        //         case 1: // 如果关联的组件数据源是动态api
        //           f_item.child.push({
        //             index: linkComItem,
        //             keyName: '',
        //             paramName: '',
        //           })
        //           break
        //         case 2: // 如果关联的组件数据源是sql数据
        //           f_item.sqlChild.push({
        //             sqlId: comItem.sqlData.id, // 保存该组件查询的sql数据源的id
        //             fieldType: 1,
        //             index: linkComItem,
        //             fieldId: '',
        //             paramsType: '',
        //           })
        //           break
        //         case 3: // 如果关联的组件数据源是sql数据
        //           f_item.websocketChild.push({
        //             index: linkComItem,
        //             paramFun: 'function(data){\n    return {}\n}',
        //           })
        //           break
        //       }
        //     }
        //   })
        // })
        let { staticChild, child, sqlChild, websocketChild } = f_item
        let length =
          staticChild.length +
          child.length +
          sqlChild.length +
          websocketChild.length
        console.log('length-->', length)
        if (length < linkComArr.length) {
          // 说明是增加了一个子类
          linkComArr.forEach(linkComItem => {
            this.allComList.forEach(comItem => {
              if (linkComItem === comItem.index) {
                switch (comItem.dataType) {
                  case 0: // 如果关联的组件数据源是静态数据
                    if (!this.isHasLinkCom(staticChild, linkComItem)) {
                      staticChild.push({ index: linkComItem })
                    }
                    break
                  case 1: // 如果关联的组件数据源是动态api
                    if (!this.isHasLinkCom(child, linkComItem)) {
                      child.push({
                        index: linkComItem,
                        keyName: '',
                        paramName: '',
                      })
                    }
                    break
                  case 2: // 如果关联的组件数据源是sql数据
                    if (!this.isHasLinkCom(sqlChild, linkComItem)) {
                      sqlChild.push({
                        sqlId: comItem.sqlData.id, // 保存该组件查询的sql数据源的id
                        fieldType: 1,
                        index: linkComItem,
                        fieldId: '',
                        paramsType: '',
                      })
                    }
                    break
                  case 3: // 如果关联的组件数据源是sql数据
                    if (!this.isHasLinkCom(websocketChild, linkComItem)) {
                      websocketChild.push({
                        index: linkComItem,
                        paramFun: 'function(data){\n    return {}\n}',
                      })
                    }
                    break
                }
              }
            })
          })
        } else {
          // 说明是移除了一个子类
          console.log('删除')
          f_item.staticChild = staticChild.filter(item =>
            linkComArr.includes(item.index)
          )
          f_item.child = child.filter(item => linkComArr.includes(item.index))
          f_item.sqlChild = sqlChild.filter(item =>
            linkComArr.includes(item.index)
          )
          f_item.websocketChild = websocketChild.filter(item =>
            linkComArr.includes(item.index)
          )
        }
      } else if (f_item.eventAction === 2 || f_item.eventAction === 3) {
        f_item.showHideChild.index = linkComArr
      }
      // console.log('f_item===>添加后', f_item)
    },
    handleRemoveLinkComItem(removeTag) {
      // console.log('removeTag==>', removeTag)
    },
    visibleChange(sqlId, isShow) {
      if (isShow) {
        console.log('sqlId==>', sqlId)
        fieldListDQ(sqlId).then(response => {
          let tableFields = response.data.data
          this.fieldListData = tableFields.dimensionList.concat(
            tableFields.quotaList
          )
        })
      }
    },
    collapseChange() {
      this.$nextTick(() => {
        // this.$refs.editor[index].$refs.monacoEditor.editor && this.$refs.editor[index].$refs.monacoEditor.editor.dispose()
        // this.$refs.editor[index].$refs.monacoEditor.initMonaco()
        if (this.$refs.editor) {
          this.$refs.editor.forEach(item => {
            item.$refs.monacoEditor.editor &&
              item.$refs.monacoEditor.editor.dispose()
            item.$refs.monacoEditor.initMonaco()
          })
        }
      })
    },
    eventTypeMap(prop = 'default', prams) {
      let Obj = {
        switchMultiple: [
          {
            label: '开关组开事件',
            value: 'openClickFormatter',
          },
          {
            label: '开关组关事件',
            value: 'closeClickFormatter',
          },
          {
            label: '初始化控件',
            value: 'initFormatter',
          },
          {
            label: '渲染完成',
            value: 'initFinishFormatter',
          },
          {
            label: '内容变化',
            value: 'contentChangeFormatter',
          },
        ],
        page: [
          {
            label: '参数接收',
            value: 'paramsAcceptFormatter',
          },
        ],
        default: [
          {
            label: '鼠标左键单击',
            value: 'clickFormatter',
          },
          {
            label: '鼠标左键双击',
            value: 'dblClickFormatter',
          },
          {
            label: '初始化控件',
            value: 'initFormatter',
          },
          {
            label: '渲染完成',
            value: 'initFinishFormatter',
          },
          {
            label: '内容变化',
            value: 'contentChangeFormatter',
          },
        ],
      }
      if (prop === 'table') {
        if (prams) {
          let switchData = []
          if(prams.drawConditionList){
            prams.drawConditionList.forEach(item=>{
              if(item.contentStyle==='swh'){
                switchData.push(item)
              }
            })
          }
          // 开关和按钮同时存在时
          if (prams.option.openOperation&&switchData.length>0) {
            Obj[prop] = [
              {
                label: '鼠标左键单击',
                value: 'clickFormatter',
              },
              {
                label: '鼠标左键双击',
                value: 'dblClickFormatter',
              },
              {
                label: '初始化控件',
                value: 'initFormatter',
              },
              {
                label: '开关开事件',
                value: 'openTableClickFormatter',
              },
              {
                label: '开关关事件',
                value: 'closeTableClickFormatter',
              },
              {
                label: '渲染完成',
                value: 'initFinishFormatter',
              },
              {
                label: '内容变化',
                value: 'contentChangeFormatter',
              },
              {
                label: '列单击事件',
                value: 'columnClickFormatter',
              },
            ]
          }
          // 只有按钮事件
          if (prams.option.openOperation&&switchData.length===0) {
            Obj[prop] = [
              {
                label: '鼠标左键单击',
                value: 'clickFormatter',
              },
              {
                label: '鼠标左键双击',
                value: 'dblClickFormatter',
              },
              {
                label: '初始化控件',
                value: 'initFormatter',
              },
              {
                label: '渲染完成',
                value: 'initFinishFormatter',
              },
              {
                label: '内容变化',
                value: 'contentChangeFormatter',
              },
              {
                label: '列单击事件',
                value: 'columnClickFormatter',
              },
            ]
          }
          // 只有开关事件
          if (!prams.option.openOperation&&switchData.length>0) {
            Obj[prop] = [
              {
                label: '鼠标左键单击',
                value: 'clickFormatter',
              },
              {
                label: '鼠标左键双击',
                value: 'dblClickFormatter',
              },
              {
                label: '初始化控件',
                value: 'initFormatter',
              },
              {
                label: '开关开事件',
                value: 'openTableClickFormatter',
              },
              {
                label: '开关关事件',
                value: 'closeTableClickFormatter',
              },
              {
                label: '渲染完成',
                value: 'initFinishFormatter',
              },
              {
                label: '内容变化',
                value: 'contentChangeFormatter',
              },
            ]
          }
        }
      }
      return Obj[prop] || Obj.default
    },
    eventActionArrMap(prop) {
      let Obj = {
        table: [
          { label: '数据刷新', value: 1 },
          { label: '组件隐藏', value: 2 },
          { label: '组件显示', value: 3 },
          { label: '调用服务', value: 4 },
          { label: '⻚⾯联动', value: 5 },
          { label: 'url弹框', value: 6 },
          { label: '脚本调用', value: 7 },
          { label: '设置页面参数值', value: 8 },
        ],
        page: [
          { label: '数据刷新', value: 1 },
          { label: '组件隐藏', value: 2 },
          { label: '组件显示', value: 3 },
          { label: '调用服务', value: 4 },
          { label: '⻚⾯联动', value: 5 },
          { label: '脚本调用', value: 7 },
          { label: '参数传递', value: 9 },
        ],
        default: [
          { label: '数据刷新', value: 1 },
          { label: '组件隐藏', value: 2 },
          { label: '组件显示', value: 3 },
          { label: '调用服务', value: 4 },
          { label: '⻚⾯联动', value: 5 },
          { label: '脚本调用', value: 7 },
          { label: '设置页面参数值', value: 8 },
        ],
      }
      if (Obj[prop]) {
        return Obj[prop]
      }
      return Obj.default
    },
    //选择大屏
    selectScreen(val, f_item) {
      f_item.currentScreeParamList = val.option.pageParamsList
      this.$set(f_item, 'paramNameValList', [
        {
          currenScreenName: '',
          currentScreenVal: '',
        },
      ])
    },
    //添加页面参数
    addItem(f_item) {
      if (!f_item.paramNameValList) {
        this.$set(f_item, 'paramNameValList', [])
      }
      f_item.paramNameValList.push({
        currenScreenName: '',
        currentScreenVal: '',
      })
      console.log('我被点击了', f_item.paramNameValList)
    },
    //删除页面参数
    deleteTerm(f_item, index) {
      if (f_item.paramNameValList.length > 1) {
        f_item.paramNameValList.splice(index, 1)
      } else {
        this.$message.warning('请至少保留一项')
      }
    },
    isHasLinkCom(arr, Linkindex) {
      return arr.find(item => item.index === Linkindex)
    },
  },
  components: {
    MonacoEditor,
    sqlSet,
    logEdit,
  },
}
</script>

<style scoped lang="scss">
.eventlist-box {
  padding-top: 10px;
}
.bullet-box {
  font-size: 16px;
  span {
    font-size: 18px;
    color: #859094;
    margin: 0 3px;
  }
}
.el-radio {
  width: unset;
  margin-right: 16px;
  &:last-child {
    margin-right: 0px;
  }
}
/deep/ .javascript-lable{
  .el-form-item__label{
    width: 106px !important;
  }
}
.item-javascript{
  position: absolute;
  color: #bcc9d4 !important;
  left: -22px;
  top: 14px;
}
// .addBtn {
//   width: 100%;
//   margin: 0 10px;
// }
.mybtn-box {
  width: 100%;
  padding: 20px 10px 20px;
  .addBtn,
  .refreshBtn {
    width: 100%;
    color: #2491f7;
    border-color: #2491f7;
    background-color: transparent;
    &:active {
      color: #1570d1;
      border-color: #1570d1;
      background-color: transparent;
    }
    &:focus {
      background-color: transparent;
    }
    &:hover {
      color: #4fb0ff;
      border-color: #4fb0ff;
      background-color: transparent;
    }
  }
}
/deep/.el-collapse-item__header {
  background-color: #2d2f38;
  border-top: 1px solid#393b4a;
  &:hover {
    .el-icon-edit {
      opacity: 1;
    }
  }
  .tj {
    width: 148px;
  }
  .el-input {
    width: 148px;
    height: 35px;
    line-height: 35px;
    .el-input__inner {
      height: 35px;
      line-height: 35px;
    }
  }
  .el-icon-edit {
    margin-left: 5px;
    opacity: 0;
  }
  .unsave {
    display: flex;
    align-items: center;
    flex: 1;
    margin-left: 6px;
    color: #999;
    .circle-red {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: #ff3c38;
      margin-right: 4px;
    }
  }
}
/deep/.el-collapse-item__wrap {
  background-color: transparent;
}
.set-condition-input {
  width: 70px;
  margin-right: 20px;
}
.set-condition-select-ziduan {
  width: 170px;
  margin: 0 20px 0 0;
}
.set-condition-select-table {
  width: 130px;
  margin: 0 20px 0 0;
}
.set-condition-select {
  width: 80px;
  margin: 0 10px;
}
.el-icon-circle-plus-outline {
  font-size: 20px !important;
  cursor: pointer;
}
.comName {
  padding: 10px 0 0 30px;
  color: #fff;
}
.params_title {
  margin: 0 0 12px 30px;
  color: #fff;
}
.Js-code {
  padding: 0 !important;
  border: 1px solid rgb(45, 83, 125);
  .monaco-editor {
    width: 100%;
    height: 100%;
  }
  &:hover {
    border-color: #2681ff;
  }
}
.log-style{
  // display: flex;
  // .paramJSON_edit_box{
  //  width: 200px;
  // }
}
</style>
