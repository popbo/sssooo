<template>
  <div class="componentSql-box">
    <el-form :ref="'form' + componentId" :model="form" label-width="110px">
      <el-form-item label="设置字段" prop="fieldId" :key="s_index" :rules="{ required: true, message: '字段名不能为空', trigger: 'change' }">
        <el-select v-model="form.fieldId" class="set-condition-select-ziduan" placeholder="请选择字段名" clearable>
          <el-option v-for="dimensionItem in fieldListData" :label="dimensionItem.name" :value="dimensionItem.id" :key="dimensionItem.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label=" 参数传递类型">
        <el-select v-model="form.paramsType" class="set-condition-select-ziduan" placeholder="请选择字段名" clearable>
          <el-option v-for="paramsTypeItem in paramsTypeArr" :label="paramsTypeItem.label" :value="paramsTypeItem.value" :key="paramsTypeItem.value">
          </el-option>
        </el-select>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { fieldListDQ } from '@/api/dataCollection'
export default {
  name: 'componentSql',
  props: {
    activeObj: {
      type: Object,
      default: function () {
        return {}
      },
    },
    componentId: {
      type: String,
      default: '',
    },
    value: {
      type: String,
      default: '',
    },
    paramsType: {
      type: String,
      default: '',
    },
    childList: {
      type: Array,
      default: function () {
        return []
      },
    },
  },
  data() {
    return {
      form: {
        fieldId: '',
        paramsType: 'bindValue',
      },
      fieldListData: [],
      paramsTypeArr: [
        {
          label: '当前绑定值',
          value: 'bindValue',
        },
        {
          label: '图层参数值',
          value: 'comName',
        },
      ],
    }
  },
  watch: {
    childList: {
      handler(val, oldVal) {
        if (val.length > 0 && val[0].dataType == 2 && val[0].sqlData && val[0].sqlData.id) {
          if (oldVal && oldVal.length > 0 && oldVal[0].dataType == 2 && oldVal[0].sqlData && oldVal[0].sqlData.id) {
            if (oldVal[0].sqlData.id != val[0].sqlData.id) {
              this.hadleChooseDataColection(val[0].sqlData.id)
            }
          } else {
            this.hadleChooseDataColection(val[0].sqlData.id)
          }
        }
      },
      deep: true,
      immediate: true,
    },
    'form.fieldId': {
      handler(val) {
        this.$emit('input', val)
      },
    },
    'form.paramsType': {
      handler(val) {
        this.$emit('update:paramsType', val)
      },
    },
    value: {
      handler(val) {
        this.form.fieldId = val
      },
      immediate: true,
    },
    paramsType: {
      handler(val) {
        this.form.paramsType = val
      },
      immediate: true,
    },
  },
  created() {},
  mounted() {},
  destroyed() {},
  computed: {
    // fieldListData() {
    //   return this.tableFields.dimensionList.concat(this.tableFields.quotaListData);
    // },
  },
  methods: {
    hadleChooseDataColection(id) {
      if (id) {
        fieldListDQ(id).then((response) => {
          let tableFields = response.data.data
          this.fieldListData = tableFields.dimensionList.concat(tableFields.quotaList)
        })
      }
    },
  },
}
</script>

<style scoped lang="scss">
.componentSql-box {
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
</style>
