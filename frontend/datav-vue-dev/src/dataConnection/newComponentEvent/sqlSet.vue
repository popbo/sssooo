<template>
  <div>
    <el-form-item label="维度">
      <el-select v-model="sqlForm.fieldType" class="set-condition-select-ziduan" placeholder="请选择" clearable size="mini">
        <el-option v-for="fieldTypeItem in fieldTypeList" :label="fieldTypeItem.label" :value="fieldTypeItem.value" :key="fieldTypeItem.value">
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="设置字段">
      <el-select v-model="sqlForm.fieldId" class="set-condition-select-ziduan" placeholder="请选择字段名" clearable size="mini">
        <el-option v-for="dimensionItem in fieldListData" :label="dimensionItem.name" :value="dimensionItem.id" :key="dimensionItem.id">
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label=" 参数传递类型">
      <el-select v-model="sqlForm.paramsType" class="set-condition-select-ziduan" placeholder="请选择字段名" clearable size="mini">
        <el-option v-for="paramsTypeItem in paramsTypeArr" :label="paramsTypeItem.label" :value="paramsTypeItem.value" :key="paramsTypeItem.value">
        </el-option>
      </el-select>
    </el-form-item>
  </div>
</template>

<script>
import { fieldListDQ } from '@/api/dataCollection'
export default {
  props: {
    sqlChildItem: {
      type: Object,
      default: function () {
        return {}
      },
    },
    paramsTypeArr: {
      type: Array,
      default: function() {
        return []
      }
    }
  },
  data() {
    return {
      sqlForm: {},
      fieldTypeList: [
        { label: '度量值', value: 1 },
        { label: '维度值', value: 2 },
      ],
      fieldListData: [],
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
    }
  },
  created() {
    console.log('调用了')
    this.sqlForm = this.deepClone(this.sqlChildItem)
    fieldListDQ(this.sqlChildItem.sqlId).then((response) => {
      let tableFields = response.data.data
      this.fieldListData = tableFields.dimensionList.concat(tableFields.quotaList)
    })
  },
  watch: {
    sqlForm: {
      handler(newVal) {
        this.$emit('updateSqlChildItem', this.deepClone(newVal))
      },
      deep: true
    }
  },
  methods: {},
}
</script>

<style scoped lang='less'>
</style>
