<template>
  <div class="condition-box">
    <!-- <div class="judge-box">
      <el-row :gutter="24">
        <el-col :span="7">判断类型</el-col>
        <el-col :span="17">
          <el-radio v-model="radio" label="AND">满足全部条件</el-radio>
          <el-radio v-model="radio" label="OR">满足任意条件</el-radio>
        </el-col>
      </el-row>
    </div> -->
    <div class="mybtn-box">
      <el-button plain icon="el-icon-plus" class="addBtn" @click="addCondition">添加条件</el-button>
    </div>
    <div>
      <el-collapse v-if="conditionArrCopy.length > 0">
        <el-collapse-item v-for="(f_item, f_index) in conditionArrCopy" :key="f_item.conditionId">
          <template slot="title">
            <!-- 条件字段 -->
            <span class="tj" v-if="!f_item.isEdite">{{ f_item.conditionName }}</span>
            <!-- 编辑条件名称输入框 -->
            <el-input v-else v-model="f_item.conditionName" :ref="f_item.conditionId" @focus.stop="handleFocus" @blur.stop="handleBlur(f_item)" size="mini"></el-input>
            <!-- 编辑图标 -->
            <i class="el-icon-edit" @click.stop="editClick(f_item)"></i>
            <!-- 未保存提示字段 -->
            <!-- <span class="unsave" v-if="!item.isSave">
              <i class="circle-red"></i>
              未保存
            </span> -->
            <!-- 占位 -->
            <div style="flex: 1 1 0%;"></div>
            <!-- 删除图标 -->
            <i class="el-icon-delete" @click.stop="deleteCondition(f_index)"></i>
          </template>
          <el-form :ref="'form' + f_item.conditionId" :model="f_item" label-width="90px">
            <el-form-item label="判断类型">
              <el-radio v-model="f_item.logic" label="and">满足全部条件</el-radio>
              <el-radio v-model="f_item.logic" label="or">满足任意条件</el-radio>
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="f_item.conditionType" placeholder="请选择条件类型" size="mini">
                <el-option label="字段" value="zd"></el-option>
                <el-option label="自定义条件" value="zdtj"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="设置条件" prop="field.id" :key="s_index" :rules="{required: true, message: '字段名不能为空', trigger: 'change'}">
              <!-- <el-input v-model="item.fieldId" class="set-condition-input" @focus="anothHandleFocus" @blur="anothhandleBlur"></el-input> -->
              <el-select v-model="f_item.field.id" class="set-condition-select-ziduan" placeholder="请选择字段名" clearable size="mini">
                <el-option v-for="dimensionItem in fieldListData" :label="dimensionItem.name" :value="dimensionItem.id" :key='dimensionItem.id'>
                </el-option>
              </el-select>
              <i class="el-icon-circle-plus-outline" @click="addTerm(f_item)"></i>
            </el-form-item>
            <el-form-item :label="'表达式' + (s_index + 1)" v-for="(s_item, s_index) in f_item.filter" :prop="'filter.' + s_index + '.value'" :key="s_index" :rules="{required: true, message: '表达式值不能为空', trigger: 'change'}">
              <el-select v-model="s_item.term" class="set-condition-select" size="mini">
                <el-option v-for="termItem in termArr" :key="termItem.value" :label="termItem.label" :value="termItem.value">
                </el-option>
              </el-select>
              <el-input v-model="s_item.value" class="set-condition-input" size="mini"></el-input>
              <i class="el-icon-delete" style="cursor: pointer" @click.stop="deleteTerm(f_item.filter, s_index)"></i>
            </el-form-item>
          </el-form>
        </el-collapse-item>
      </el-collapse>
    </div>
    <div class="mybtn-box" v-if="conditionArrCopy.length > 0">
      <el-button plain icon="el-icon-refresh-left" class="refreshBtn" @click="refreshData">刷新数据</el-button>
    </div>
  </div>
</template>

<script>
// import { uuid } from '@/utils/utils'
import { uuid } from '@/utils/utils.min.js'
import { EventBus } from '@/bus.js'
import { sqlSearch } from '@/api/sqlSearch'
export default {
  name: 'condition',
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
      isEdite: false,
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
        {
          value: 'IN',
          label: 'in',
        },
        {
          value: 'NOT IN',
          label: 'not in',
        },
        {
          value: 'LIKE',
          label: 'like',
        },
        {
          value: 'NOT LIKE',
          label: 'not like',
        },
        {
          value: 'BETWEEN',
          label: 'between',
        },
        {
          value: 'NOT BETWEEN',
          label: 'not between',
        },
      ],
      currentValue: '',
      conditionArrCopy: [],
      currentObj: null, // 当前选中的组件对象
    }
  },
  watch: {
    // 监听当前选中组建的变化,因为直接点击组件进行切换，condition.vue并不会被销毁。因此会保留着上一个组件的条件所以当组件变化的时候要对其做处理
    activeIndex: {
      handler(newValue, oldValue) {
        if (newValue) {
          this.currentObj = this.contain.findlist(newValue)
          this.handleActiveObj(this.activeObj)
        }
      },
      immediate: true,
    },
    // 监听条件数组的变化
    conditionArrCopy: {
      handler(newValue, oldValue) {
        if(this.currentObj){
          if(this.currentObj.sqlData) {
            this.currentObj.sqlData.whereCustomFilterS = newValue
          }
        }
      },
      deep: true,
    },
  },
  created() {
    this.handleActiveObj(this.activeObj)
  },
  mounted() {
    // 监听一下兄弟组件sqlForm组件触发表单校验的命令
    EventBus.$on('checkoutFilter', ({sqlData , isPreview}) => {
      const checkoutResult = this.checkoutFilter()
      if (checkoutResult) {
        if(isPreview) {
          // 如果是条件预览
          const type = this.activeObj.component.prop
          // 把当前图表配置的条件赋值给sqlData
          sqlData.whereCustomFilterS = this.activeObj.sqlData.whereCustomFilterS
          sqlSearch(type, sqlData).then((res) => {
            let previewJSON = res.data
            EventBus.$emit('updatePreviewJSON', previewJSON)
          })
        } else {
          this.$emit('on-handleRes', sqlData)
        }
      } else {
        this.$message.error('条件配置还有未填写的字段')
      }
    })
  },
  destroyed() {
    EventBus.$off()
  },
  computed: {
    fieldListData() {
      return this.tableFields.dimensionListData.concat(this.tableFields.quotaListData)
    },
  },
  methods: {
    editClick(item) {
      item.isEdite = true
      this.$nextTick(() => {
        this.$refs[item.conditionId][0].focus()
      })
    },
    handleBlur(item) {
      item.isEdite = false
    },
    handleFocus() {
    },
    // 点击添加条件按钮
    addCondition() {
      const length = this.conditionArrCopy.length
      const newCondition = {
        isEdite: false,
        conditionId: uuid(),
        conditionName: '条件' + (length + 1),
        conditionType: 'zd',
        logic: 'and',
        field: {
          id: '',
        },
        filter: [
          {
            term: 'eq',
            value: '',
          },
        ],
      }
      this.conditionArrCopy.push(newCondition)
    },
    // 点击删除按钮
    deleteCondition(index) {
      this.conditionArrCopy.splice(index, 1)
    },
    // 写一个首次接收到到activeObj对它进行处理的函数，因为一个刚加进来的组件它的sqlData是没有添加fieldCustomFilterS，而之前保存过的组件可能有可能没有
    handleActiveObj(obj) {
      // 如果之前保存了这个值，拿到后我要自己进行一些处理
      if (obj.sqlData && obj.sqlData.whereCustomFilterS) {
        const fieldCustomFilterSArr = obj.sqlData.whereCustomFilterS
        this.conditionArrCopy = this.deepClone(fieldCustomFilterSArr)
      } else {
        // 没有的话置空一下
        this.conditionArrCopy = []
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
    refreshData() {
      // 先进行校验
      const checkoutResult = this.checkoutFilter()
      if (checkoutResult) {
        EventBus.$emit('refreshData', checkoutResult)
      } else {
        this.$message.error('请填写必填项')
      }
    },
    // 校验每个条件是否填写完整
    checkoutFilter() {
      // 对每一个条件对象进行遍历
      return this.conditionArrCopy.every((item) => {
        // 根据条件数组拿到每个条件表单对象
        const form = this.$refs['form' + item.conditionId][0]
        // 声明一个校验值
        let isValid
        form.validate((valid) => {
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
  },
}
</script>

<style scoped lang='scss'>
.condition-box {
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
.set-condition-select {
  width: 80px;
  margin: 0 10px;
}
.el-icon-circle-plus-outline {
  font-size: 20px !important;
  cursor: pointer;
}
.mybtn- {
}
</style>
