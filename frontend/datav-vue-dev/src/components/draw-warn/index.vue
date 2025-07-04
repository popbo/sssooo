<template>
  <div class="draw-condition-box">
    <div class="mybtn-box">
      <el-button
        plain
        icon="el-icon-plus"
        class="addBtn myBtn"
        @click="addCondition"
        >添加渲染条件</el-button
      >
    </div>
    <div>
      <el-collapse v-if="drawConditionListCopy.length > 0">
        <el-collapse-item
          v-for="(f_item, f_index) in drawConditionListCopy"
          :key="f_item.conditionId"
        >
          <template slot="title">
            <!-- 条件字段 -->
            <span class="tj" v-if="!f_item.isEdite">{{
              f_item.conditionName
            }}</span>
            <!-- 编辑条件名称输入框 -->
            <el-input
              v-else
              v-model="f_item.conditionName"
              @focus.stop="handleFocus"
              @blur.stop="handleBlur(f_item)"
              :ref="f_item.conditionId"
            ></el-input>
            <!-- 编辑图标 -->
            <i class="el-icon-edit" @click.stop="editClick(f_item)"></i>
            <div style="flex: 1 1 0%"></div>
            <!-- 删除图标 -->
            <i
              class="el-icon-delete"
              @click.stop="deleteCondition(f_index)"
            ></i>
          </template>
          <el-form
            :ref="'form' + f_item.conditionId"
            :model="f_item"
            label-width="90px"
          >
            <el-form-item label="判断类型">
              <el-radio v-model="f_item.logic" label="and"
                >满足全部条件</el-radio
              >
              <el-radio v-model="f_item.logic" label="or"
                >满足任意条件</el-radio
              >
            </el-form-item>
            <el-form-item label="类型">
              <el-select
                v-model="f_item.conditionType"
                placeholder="请选择条件类型"
                size="mini"
              >
                <el-option label="字段" value="zd"></el-option>
                <el-option label="自定义条件" value="zdtj"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              label="设置条件"
              prop="enterValue"
              :key="s_index"
              :rules="{
                required: true,
                message: '字段名不能为空',
                trigger: 'change',
              }"
            >
              <!-- <el-input v-model="item.fieldId" class="set-condition-input" @focus="anothHandleFocus" @blur="anothhandleBlur"></el-input> -->
              <template v-if="isTable">
                <el-select
                  v-model="f_item.enterValue"
                  class="set-condition-select-ziduan"
                  placeholder="请选择输入值"
                  clearable
                  size="mini"
                >
                  <el-option
                    v-for="option in enterValueArr"
                    :label="option.name"
                    :key="option.id"
                    :value="option.id"
                  ></el-option>
                </el-select>
              </template>
              <template v-else>
                <el-select
                  v-model="f_item.enterValue"
                  class="set-condition-select-ziduan"
                  placeholder="请选择输入值"
                  clearable
                  size="mini"
                >
                  <el-option label="当前值" value="dqz"></el-option>
                </el-select>
              </template>

              <i
                class="el-icon-circle-plus-outline"
                @click="addTerm(f_item)"
              ></i>
            </el-form-item>
            <el-form-item
              :label="'表达式' + (s_index + 1)"
              v-for="(s_item, s_index) in f_item.filter"
              :prop="'filter.' + s_index + '.value'"
              :key="s_index"
              :rules="{
                required: true,
                message: '表达式值不能为空',
                trigger: 'change',
              }"
            >
              <el-select
                v-model="s_item.term"
                class="set-condition-select"
                size="mini"
              >
                <el-option
                  v-for="termItem in termArr"
                  :key="termItem.value"
                  :label="termItem.label"
                  :value="termItem.value"
                >
                </el-option>
              </el-select>
              <el-input
                v-model="s_item.value"
                :class="isShowUnit?'set-condition-input':'input-set'"
                size="mini"
              ></el-input>
              <el-input
                v-show="isShowUnit"
                v-model="s_item.unit"
                class="set-condition-unit"
                placeholder="单位"
                size="mini"
              ></el-input>
              <el-tooltip
                v-show="isShowUnit"
                class="item engineering-unit"
                effect="dark"
                content="返回数据有时候带有文本单位时需填写单位才能成功筛选无单位可不填"
                placement="top-start"
                style="font-size: 14px;"
              >
                <i class="el-icon-info"></i>
              </el-tooltip>
              <i
                class="el-icon-delete"
                style="cursor: pointer;font-size: 16px;"
                @click.stop="deleteTerm(f_item.filter, s_index)"
              ></i>
            </el-form-item>
            <el-divider></el-divider>
            <template v-if="isText || isTable">
              <el-form-item
                label="渲染样式："
                label-width="100px"
              ></el-form-item>
              <el-form-item label="样式：">
                <el-select
                  v-model="f_item.contentStyle"
                  @change="handleContentStyleChange($event, f_item)"
                  size="mini"
                >
                  <el-option
                    v-for="item in contentStyleOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </el-form-item>
              <!--表格开关 -->
              <el-form-item label="开关状态" v-if="isTable && f_item.contentStyle==='swh'">
                <el-select v-model="f_item.switch" size="mini">
                  <el-option
                    v-for="option in tableSwitch"
                    :label="option.name"
                    :key="option.id"
                    :value="option.id"
                  ></el-option>
                </el-select>
              </el-form-item>
              <!--表格开关  -->
              <el-form-item label="渲染对象" v-if="isTable && f_item.contentStyle==='swh'">
                <el-select v-model="f_item.drawObjSwitch" size="mini">
                  <el-option
                    v-for="option in enterValueArr"
                    :label="option.name"
                    :key="option.id"
                    :value="option.id"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="渲染对象" v-if="isTable && f_item.contentStyle!=='swh' ">
                <el-select v-model="f_item.drawObj" size="mini">
                  <el-option label="整行" value="allRow"></el-option>
                  <el-option
                    v-for="option in enterValueArr"
                    :label="option.name"
                    :key="option.id"
                    :value="option.id"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="图标:" v-if="f_item.contentStyle === 'svg'">
                <template v-if="f_item.svgObj">
                  <div
                    v-html="f_item.svgObj.link"
                    class="div-svg"
                    @click="handleChooseSvgClick(f_item)"
                  ></div>
                  <el-button
                    plain
                    icon="el-icon-edit"
                    class="myBtn"
                    size="mini"
                    @click="setSvgStyle(f_item)"
                    >设置图标</el-button
                  >
                </template>
              </el-form-item>
              <el-form-item
                label="图标位置:"
                v-if="f_item.contentStyle === 'svg'"
              >
                <el-select v-model="f_item.svgConObj.location" size="mini">
                  <el-option label="文字前" value="wzq"></el-option>
                  <el-option label="文字后" value="wzh"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="颜色:" v-if="f_item.contentStyle === 'text'">
                <el-color-picker
                  v-model="f_item.textColor"
                  size="mini"
                ></el-color-picker>
                <el-input
                  v-model="f_item.textColor"
                  class="color_input"
                  size="mini"
                ></el-input>
              </el-form-item>
              <el-form-item
                label="字体大小"
                v-if="f_item.contentStyle === 'text' && isTable"
              >
                <avue-input-number size="mini" v-model="f_item.fontSize">
                </avue-input-number>
              </el-form-item>
              <el-form-item
                label="背景颜色:"
                v-if="f_item.contentStyle === 'bgc'"
              >
                <el-color-picker
                  v-model="f_item.bgColor"
                  size="mini"
                ></el-color-picker>
                <el-input
                  v-model="f_item.bgColor"
                  class="color_input"
                  size="mini"
                ></el-input>
              </el-form-item>
              <el-form-item label="动画:" v-if="f_item.contentStyle!=='swh'">
                <el-checkbox-group v-model="f_item.animationType">
                  <el-checkbox label="隐藏内容" name="type"></el-checkbox>
                  <el-checkbox label="闪烁" name="type"></el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </template>
            <template v-if="isImg">
              <el-form-item label="图片地址">
                <img
                  v-if="f_item.imgUrl"
                  :src="f_item.imgUrl"
                  alt=""
                  width="100%"
                />
                <el-input v-model="f_item.imgUrl" size="mini">
                  <div
                    @click="contain.handleOpenImg(f_item, 'warnDraw')"
                    slot="append"
                    class="openimg_box"
                  >
                    <i class="iconfont icon-img"></i>
                  </div>
                </el-input>
              </el-form-item>
            </template>
            <template v-if="isStraightLine || isCustomSegments">
              <el-form-item
                label="颜色:"
                v-if="
                  f_item.contentStyle === 'sharpLine' ||
                  f_item.contentStyle === 'customSegments'
                "
              >
                <el-color-picker
                  v-model="f_item.bodyColor"
                  size="mini"
                ></el-color-picker>
                <el-input
                  v-model="f_item.bodyColor"
                  class="color_input"
                  size="mini"
                ></el-input>
              </el-form-item>
            </template>
            <template v-if="isBar">
              <el-form-item
                label="渲染样式："
                label-width="100px"
              ></el-form-item>
              <el-form-item
                label="颜色渐变"
                label-width="100px"
              >
              <el-switch
                v-model="f_item.switch"
               >
              </el-switch>
              </el-form-item>
              <el-form-item
                v-show="f_item.switch"
                label="柱体颜色1："
                label-width="100px"
              >
              <avue-input-color
                type="textarea"
                v-model="f_item.barColor1"
              ></avue-input-color>
              </el-form-item>
              <el-form-item
                v-show="f_item.switch"
                label="柱体颜色2："
                label-width="100px"
              >
              <avue-input-color
                type="textarea"
                v-model="f_item.barColor2"
              ></avue-input-color>
              </el-form-item>
              <el-form-item
                v-show="!f_item.switch"
                label="柱体颜色："
                label-width="100px"
              >
              <avue-input-color
                type="textarea"
                v-model="f_item.barColor"
              ></avue-input-color>
              </el-form-item>
            </template>
          </el-form>
        </el-collapse-item>
      </el-collapse>
    </div>
    <div class="mybtn-box" v-if="drawConditionListCopy.length > 0">
      <el-button
        plain
        icon="el-icon-upload"
        class="saveBtn myBtn"
        @click="saveData"
        >保存</el-button
      >
    </div>
    <!-- 图标选择对话框 开始 -->
    <el-dialog title="选择图标" :visible.sync="svgDialogVisible" width="30%">
      <svgDiglog ref="svgDiglog" @on-chooseSvg="handleChooseSvg"></svgDiglog>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelSvg">取 消</el-button>
        <el-button type="primary" @click="confirmSvgObj">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 图标选择对话框 结束 -->
    <!-- 图表样式对话框 开始 -->
    <el-dialog
      title="图标样式编辑"
      :visible.sync="svgStyleDialogVisible"
      width="30%"
    >
      <el-form :model="svgStyleForm" ref="svgStyleForm" :rules="rules">
        <el-form-item label="颜色：" label-width="80px" prop="svgColor">
          <el-color-picker v-model="svgStyleForm.svgColor"></el-color-picker>
          <el-input
            v-model="svgStyleForm.svgColor"
            class="color_input"
          ></el-input>
        </el-form-item>
        <el-form-item label="大小：" label-width="80px" prop="svgSize">
          <el-input
            v-model="svgStyleForm.svgSize"
            class="color_input"
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelSetSvgStyle">取 消</el-button>
        <el-button type="primary" @click="confirmSetSvgStyle">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 图表样式对话框 结束 -->
  </div>
</template>

<script>
// import { uuid } from '@/utils/utils'
import { uuid } from '@/utils/utils.min.js'
import { checkMarkSvg } from '@/utils/svgSource.js'
import { EventBus } from '@/bus.js'
import svgDiglog from './svgDialog'
export default {
  name: 'warnDraw',
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
  },
  data() {
    return {
      tableSwitch:[{
        name:'开状态',
        id:'sk'
      },{
        name:'关状态',
        id:'sg'
      }],
      termArr: [
        {
          value: 'eq',
          label: '=',
        },
        {
          value: 'ne',
          label: '!=',
        },
        {
          value: 'lt',
          label: '<',
        },
        {
          value: 'le',
          label: '<=',
        },
        {
          value: 'gt',
          label: '>',
        },
        {
          value: 'ge',
          label: '>=',
        },
        // {
        //   value: 'IN',
        //   label: 'in',
        // },
        // {
        //   value: 'NOT IN',
        //   label: 'not in',
        // },
        // {
        //   value: 'LIKE',
        //   label: 'like',
        // },
        // {
        //   value: 'NOT LIKE',
        //   label: 'not like',
        // },
        // {
        //   value: 'BETWEEN',
        //   label: 'between',
        // },
        // {
        //   value: 'NOT BETWEEN',
        //   label: 'not between',
        // },
      ],
      drawConditionListCopy: [],
      svgStyleDialogVisible: false, // 图标样式对话框
      svgDialogVisible: false, // 图标选择对话框
      currentSvgObj: {}, // 暂存当前选中的图标
      currentConditionItem: {}, // 暂存当前正在操作的条件
      svgStyleForm: {
        svgColor: '#409EFF',
        svgSize: 10,
      },
      rules: {
        svgColor: [
          { required: true, message: '请选择颜色', trigger: 'change' },
        ],
        svgSize: [{ required: true, message: '请设置大小', trigger: 'blur' }],
      },
      currentObj: null, // 当前选中的组件对象
      dimAndMeaOptions: [],
    }
  },
  watch: {
    // 监听当前选中组建的变化,因为直接点击组件进行切换，condition.vue并不会被销毁。因此会保留着上一个组件的条件所以当组件变化的时候要对其做处理
    activeIndex: {
      handler(newValue, oldValue) {
        if (newValue) {
          if (this.contain.tipsIndex) {
            console.log('tips查找')
            this.currentObj = this.contain.findTipsList(newValue)
          } else {
            this.currentObj = this.contain.findlist(newValue)
          }
          console.log('this.activeObj', this.activeObj)
          this.handleActiveObj(this.activeObj)
        }
      },
      immediate: true,
    },
    // 监听条件数组的变化
    // drawConditionListCopy: {
    //   handler(newValue, oldValue) {
    //     console.log('触发了')
    //     this.activeObj.sqlData.whereCustomFilterS = newValue
    //   },
    //   deep: true,
    // },
  },
  created() {
    this.handleActiveObj(this.activeObj)
  },
  mounted() {
    EventBus.$on('updateCheckedDimAndMea', arr => {
      console.log('dimAndMeaOptions==>', arr)
      this.dimAndMeaOptions = arr
    })
  },
  destroyed() {},
  computed: {
    isShowUnit(){
      if (
        this.activeObj &&
        this.activeObj.component &&
        this.activeObj.component.prop === 'table'
      ) {
        return true
      } else {
        return false
      }
    },
    isText() {
      if (
        this.activeObj &&
        this.activeObj.component &&
        this.activeObj.component.prop === 'text'
      ) {
        return true
      } else {
        return false
      }
    },
    isBar() {
      if (
        this.activeObj &&
        this.activeObj.component &&
        this.activeObj.component.prop === 'bar'
      ) {
        return true
      } else {
        return false
      }
    },
    isImg() {
      if (
        this.activeObj &&
        this.activeObj.component &&
        this.activeObj.component.prop === 'img'
      ) {
        return true
      } else {
        return false
      }
    },
    isStraightLine() {
      if (
        this.activeObj &&
        this.activeObj.component &&
        this.activeObj.component.prop === 'sharpLine'
      ) {
        return true
      } else {
        return false
      }
    },
    isCustomSegments() {
      if (
        this.activeObj &&
        this.activeObj.component &&
        this.activeObj.component.prop === 'customSegments'
      ) {
        return true
      } else {
        return false
      }
    },
    isTable() {
      if (
        this.activeObj &&
        this.activeObj.component &&
        this.activeObj.component.prop === 'table'
      ) {
        return true
      } else {
        return false
      }
    },
    contentStyleOptions() {
      if (this.isTable) {
        return [
          { label: '文字', value: 'text' },
          {
            label: '图标',
            value: 'svg',
          },
          {
            label: '背景色',
            value: 'bgc',
          },
          {
            label: '开关',
            value: 'swh',
          },
        ]
      } else {
        return [
          { label: '文字', value: 'text' },
          {
            label: '图标',
            value: 'svg',
          },
        ]
      }
    },
    enterValueArr() {
      if (this.activeObj && this.activeObj.dataType === 2) {
        return this.dimAndMeaOptions
      } else {
        if (
          this.activeObj &&
          this.activeObj.option &&
          this.activeObj.option.column
        ) {
          let tableList = []
          let column = this.activeObj.option.column
          this.getTableParameter(tableList,column);
          return tableList
          // return this.activeObj.option.column.map(item => {
          //   return {
          //     name: item.label,
          //     id: item.prop,
          //   }
          // })
        } else {
          return []
        }
      }
    },
  },
  methods: {
    getTableParameter(tableParameter,list){
      for(let i = 0; i < list.length; i++){
        if(list[i].children && list[i].children.length){
          this.getTableParameter(tableParameter,list[i].children)
        }else{
          tableParameter.push({
            id:list[i].prop,
            name:list[i].label
          })
        }
      }
    },
    editClick(item) {
      item.isEdite = true
      this.$nextTick(() => {
        this.$refs[item.conditionId][0].focus()
      })
    },
    handleBlur(item) {
      item.isEdite = false
    },
    handleFocus() {},
    // 点击添加条件按钮
    addCondition() {
      const length = this.drawConditionListCopy.length
      const newCondition = {
        isEdite: false,
        conditionId: uuid(), // 条件的Id
        conditionName: '条件' + (length + 1), // 条件的名字
        conditionType: 'zd', // 目前没啥用
        enterValue: this.isTable ? '' : 'dqz', // 渲染告警的条件的输入值
        logic: 'and', // 渲染告警的分支条件数组之前每一项是且还是或
        filter: [{ term: 'eq', value: '',unit:'' }], // 渲染告警的分支条件数组
      }
      let contentObj = {}
      if (this.isText) {
        contentObj = {
          contentStyle: 'text', // 渲染告警样式文字或者图标
          textColor: '#fff', // 文字的颜色
          animationType: [], // 是否隐藏文本值 ， 添加闪烁
          svgConObj: {
            // svg图标配置信息对象
            location: 'wzq',
            svgColor: '#409EFF',
            svgSize: 30,
          },
        }
      } else if (this.isTable) {
        contentObj = {
          switch:'',
          drawObjSwitch:'',
          drawObj: 'allRow',
          contentStyle: 'bgc', // 渲染告警样式文字或者图标
          textColor: '#fff', // 文字的颜色
          animationType: [], // 是否隐藏文本值 ， 添加闪烁
          bgColor: '#fff',
          fontSize: 14,
          svgConObj: {
            // svg图标配置信息对象
            location: 'wzq',
            svgColor: '#409EFF',
            svgSize: 30,
          },
        }
      } else if (this.isImg) {
        contentObj = {
          imgUrl: '',
        }
      } else if (this.isStraightLine) {
        contentObj = {
          contentStyle: 'sharpLine', // 渲染告警样式文字或者图标
          bodyColor: 'rgba(46, 138, 229, 1)',
        }
      } else if (this.isCustomSegments) {
        contentObj = {
          contentStyle: 'customSegments', // 渲染告警样式文字或者图标
          bodyColor: 'rgba(46, 138, 229, 1)',
        }
      }else if(this.isBar){
        contentObj={
          switch:false,
          barColor1:'rgba(46, 138, 229, 1)',
          barColor2:'rgba(46, 138, 229, 1)',
          barColor:'rgba(46, 138, 229, 1)'
        }
      }
      this.drawConditionListCopy.push({ ...newCondition, ...contentObj })
    },
    // 点击删除按钮
    deleteCondition(index) {
      this.drawConditionListCopy.splice(index, 1)
      if (this.drawConditionListCopy.length == 0) {
        this.saveData()
      }
    },
    // 写一个首次接收到到activeObj对它进行处理的函数，因为一个刚加进来的组件它的sqlData是没有添加fieldCustomFilterS，而之前保存过的组件可能有可能没有
    handleActiveObj(obj) {
      // 如果之前保存了这个值，拿到后我要自己进行一些处理
      if (obj && obj.drawConditionList) {
        const drawConditionList = obj.drawConditionList
        this.drawConditionListCopy = this.deepClone(drawConditionList)
      }
    },
    // 点击添加表达式
    addTerm(item) {
      if (!item.filter) {
        item.filter = []
      }
      item.filter.push({
        term: 'eq',
        value: '',
      })
    },
    // 点击删除表达式
    deleteTerm(filter, index) {
      filter.splice(index, 1)
    },
    saveData() {
      // 先进行校验
      const checkoutResult = this.checkoutFilter()
      if (checkoutResult) {
        this.currentObj.drawConditionList = this.deepClone(
          this.drawConditionListCopy
        )
        EventBus.$emit('tableSwitchDrawWarn')
        this.$emit('drawWarnSaveData',this.currentObj)
        this.$message.success('保存成功')
      } else {
        this.$message.error('请填写必填项')
      }
    },
    // 校验每个条件是否填写完整
    checkoutFilter() {
      // 对每一个条件对象进行遍历
      return this.drawConditionListCopy.every(item => {
        // 根据条件数组拿到每个条件表单对象
        const form = this.$refs['form' + item.conditionId][0]
        // 声明一个校验值
        let isValid
        form.validate(valid => {
          if (valid) {
            // 如果通过校验
            isValid = true
          } else {
            // 未通过校验
            isValid = false
            return false
          }
        })
        return isValid
      })
    },
    handleContentStyleChange(value, f_item) {
      // 当样式选择框的值选择为图标时弹出图标选择对话框
      if (value === 'svg') {
        this.currentConditionItem = f_item // 暂存现在正在配置图标的条件对象
        this.currentConditionItem.svgObj = {}
        this.currentConditionItem.svgObj.link = checkMarkSvg
        this.svgDialogVisible = true // 打开选择图标对话框
      }
    },
    // 处理选择图标对话框选中一个图标时触发的事件
    handleChooseSvg(choSvgObj) {
      this.currentSvgObj = choSvgObj // 把对话框中选择的图标对象暂存到currentSvgObj中
    },
    // 当点击了选择图标对话框的确定按钮时
    confirmSvgObj() {
      console.log('this.currentSvgObj==>', this.currentSvgObj)
      if (!this.currentSvgObj.link)
        return this.$message.warning({ message: '请选择一个图标' })
      this.currentConditionItem.svgObj = this.currentSvgObj // 当点击了确定时把当前选中的Svg图标对象赋值给当前正在操作的条件对象
      this.$refs.svgDiglog.currentIndex = '' // 置空一下当前选中的图标Index
      this.currentConditionItem = {} // 置空暂存当前操作对象的currentConditionItem
      this.currentSvgObj = {} // 置空暂存当前选中图标对象的currentSvgObj
      this.svgDialogVisible = false
    },
    // 当点击了选择图标对话框的取消按钮时
    cancelSvg() {
      this.$refs.svgDiglog.currentIndex = '' // 置空一下当前选中的
      this.currentConditionItem = {} // 置空暂存当前操作对象的currentConditionItem
      this.currentSvgObj = {} // 置空暂存当前选中图标对象的currentSvgObj
      this.svgDialogVisible = false
    },
    // 当点击了已经回显出来的图标的时候
    handleChooseSvgClick(f_item) {
      this.currentConditionItem = f_item // 暂存现在正在配置图标的条件对象

      this.svgDialogVisible = true // 打开选择图标对话框
    },
    // 当点击了设置图标按钮
    setSvgStyle(f_item) {
      this.currentConditionItem = f_item // 暂存现在正在配置图标的条件对象
      this.svgStyleForm.svgColor = f_item.svgConObj.svgColor
      this.svgStyleForm.svgSize = f_item.svgConObj.svgSize
      this.svgStyleDialogVisible = true // 打开选择图标对话框
    },
    // 当点击设置图标样式对话框的取消按钮时
    cancelSetSvgStyle() {
      this.$refs.svgStyleForm.resetFields()
      this.currentConditionItem = {} // 置空暂存当前操作对象的currentConditionItem
      this.svgStyleDialogVisible = false // 关闭对话框
    },
    // 当点击设置图标样式对话框的确定按钮时
    confirmSetSvgStyle() {
      this.$refs.svgStyleForm.validate(valid => {
        if (valid) {
          // this.currentConditionItem.svgConObj
          Object.assign(this.currentConditionItem.svgConObj, this.svgStyleForm)
          this.$refs.svgStyleForm.resetFields()
          this.currentConditionItem = {} // 置空暂存当前操作对象的currentConditionItem
          this.svgStyleDialogVisible = false // 关闭对话框
        } else {
          return false
        }
      })
    },
  },
  components: {
    svgDiglog,
  },
}
</script>

<style scoped lang="scss">
.draw-condition-box {
  padding-top: 10px;
}
.el-radio {
  width: unset;
  margin-right: 16px;
  &:last-child {
    margin-right: 0px;
  }
}
// .addBtn {
//   width: 100%;
//   margin: 0 10px;
// }
.mybtn-box {
  width: 100%;
  padding: 20px 10px 20px;
  .addBtn,
  .saveBtn {
    width: 100%;
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
  width: 56px;
  margin-right: 5px;
}
.input-set{
  width: 105px;
  margin-right: 10px;
}
.set-condition-unit{
  width: 60px;
  margin-right: 5px;
}
.set-condition-select-ziduan {
  width: 170px;
  margin: 0 15px 0 0;
}
.set-condition-select {
  width: 65px;
  margin-right: 5px;
}
.el-icon-circle-plus-outline {
  font-size: 20px !important;
  cursor: pointer;
}
.el-divider--horizontal {
  width: 80%;
  margin: 24px 33px 0;
}
.color_input {
  width: 100px;
}
.el-color-picker {
  vertical-align: middle;
}
/deep/.el-form-item__content, .el-dialog .el-form-item__content{
  padding-right: 0;
}
/deep/.div-svg {
  width: 40px;
  height: 40px;
  display: inline-block;
  vertical-align: middle;
  margin-right: 10px;
  cursor: pointer;
  svg {
    width: 38px !important;
    height: 38px !important;
  }
}
</style>
