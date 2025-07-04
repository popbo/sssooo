<template>
  <div>
    <el-button type="primary" icon="el-icon-plus" size="mini" @click="addRowPermission">添加</el-button>
    <!-- 行权限展示表格 -->
    <el-table :data="rowPermissionTableData" style="width: 100%" size="mini">
      <el-table-column prop="typeName" label="类型" width="180">
      </el-table-column>
      <el-table-column prop="name" label="名称" width="180">
      </el-table-column>
      <el-table-column prop="fieldName" label="字段名">
      </el-table-column>
      <el-table-column prop="showValue" label="值">
        <!-- <template slot-scope="scope">
          {{ scope.row.filter | parseFilter }}
        </template> -->
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button type="primary" icon="el-icon-edit" circle size="mini" @click="editRowPermission(scope.row)"></el-button>
          <el-button type="danger" icon="el-icon-delete" circle size="mini" @click="deleteRowPermission(scope.row)"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 添加/编辑行权限对话框 -->
    <el-dialog :title="isEdit? '编辑行权限' : '添加行权限'" :visible.sync="dialogFormVisible" width='30%' @close='handleDialogClose' :close-on-press-escape='false'>
      <el-form :model="rowPermissionForm" ref="refRowPermissionForm">
        <el-form-item label="类型" :label-width="formLabelWidth" prop="authTargetType" :rules="[{ required: true, message: '请选择类型', trigger: 'change' }]">
          <el-select v-model="rowPermissionForm.authTargetType" placeholder="请选择类型" size="mini" @change="handleAuthTargetTypeChange">
            <el-option label="组织" value="dept"></el-option>
            <el-option label="角色" value="role"></el-option>
            <el-option label="用户" value="user"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="名称" :label-width="formLabelWidth" prop="authTargetId" :rules="[{ required: true, message: '请选择名称', trigger: 'change' }]">
          <el-cascader v-if="rowPermissionForm.authTargetType === 'dept'" :options="authTargetList" v-model="rowPermissionForm.authTargetId" :props="organizationListProps" size="mini"></el-cascader>
          <el-select v-else v-model="rowPermissionForm.authTargetId" placeholder="请选择名称" size="mini">
            <el-option v-for="item in authTargetList" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="字段名" :label-width="formLabelWidth" prop="datasetFieldId" :rules="[{ required: true, message: '请选择字段名', trigger: 'change' }]">
          <el-select v-model="rowPermissionForm.datasetFieldId" placeholder="请选择字段名" size="mini" @change="datasetFieldIdChange">
            <el-option v-for="item in fieldListData" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="" :label-width="formLabelWidth" prop="filterType">
          <el-radio-group v-model="rowPermissionForm.filterType">
            <el-radio label="logic">逻辑条件</el-radio>
            <el-radio label="enum">字段枚举值</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="rowPermissionForm.filterType === 'logic'">
          <el-form-item :label-width="formLabelWidth">
            <el-button type="primary" icon="el-icon-plus" circle size="mini" @click="addFilter"></el-button>
          </el-form-item>
          <el-form-item label="判断类型" :label-width="formLabelWidth" prop="logic">
            <el-radio v-model="rowPermissionForm.logic" label="and">满足全部条件</el-radio>
            <el-radio v-model="rowPermissionForm.logic" label="or">满足任意条件</el-radio>
          </el-form-item>
          <el-form-item :label="'表达式' + (index + 1)" :label-width="formLabelWidth" v-for="(item, index) in rowPermissionForm.filter" :prop="'filter.' + index + '.value'" :key="index" :rules="{required: true, message: '表达式值不能为空', trigger: 'change'}">
            <el-select v-model="item.term" class="set-filter-select" size="mini">
              <el-option v-for="termItem in termArr" :key="termItem.value" :label="termItem.label" :value="termItem.value">
              </el-option>
            </el-select>
            <el-input v-model="item.value" class="set-filter-input" size="mini"></el-input>
            <i class="el-icon-delete" style="cursor: pointer" @click.stop="deleteTerm(rowPermissionForm.filter, index)"></i>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="字段值" :label-width="formLabelWidth" prop="enumCheckField">
            <MultipleSelectCheckbox v-model="rowPermissionForm.enumCheckField" :order="1" placeholder="请选择" collapse-tags :options="enumList" @change="change">
            </MultipleSelectCheckbox>
          </el-form-item>
        </template>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" size="mini">取 消</el-button>
        <el-button type="primary" @click="saveRowPermission" size="mini">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getallOrganizations,
  getAllRoles,
  getAllUsers,
  addRowPermission,
  getAllEnum,
  getAllRowPermissions,
  deleteRowPermission,
  updateRowPermission,
} from '@/api/login.js'
import { fieldListDQ } from '@/api/dataCollection'
import MultipleSelectCheckbox from '@/components/MultipleSelectCheckbox'
let _this
function createRowPermissionForm() {
  return {
    authTargetType: '',
    authTargetId: '',
    datasetFieldId: '',
    filterType: 'logic',
    logic: 'and',
    filter: [
      {
        term: 'eq',
        value: '',
      },
    ],
    enumCheckField: [],
  }
}
export default {
  name: 'rowPermissions',
  props: {
    param: {
      type: Object,
      required: true,
    },
  },
  components: {
    MultipleSelectCheckbox,
  },
  data() {
    return {
      rowPermissionTableData: [],
      isEdit: false,
      dialogFormVisible: false,
      formLabelWidth: '120px',
      rowPermissionForm: createRowPermissionForm(),
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
      termObj: {
        eq: '等于',
        ne: '不等于',
        lt: '小于',
        le: '小于等于',
        gt: '大于',
        ge: '大于等于',
        IN: '包含',
        'NOT IN': '不包含',
        LIKE: 'like',
        'NOT LIKE': 'not like',
        BETWEEN: 'between',
        'NOT BETWEEN': 'not between',
      },
      organizationList: [],
      rolesList: [],
      usersList: [],
      fieldListData: [],
      organizationListProps: {
        label: 'name',
        value: 'deptId',
      },
      value1: ['选项1'],
      enumList: [],
    }
  },
  watch: {
    param: function () {
      this.initField()
      this.getAllRowPermissionList()
    },
  },
  created() {
    _this = this
    this.getAllRowPermissionList()
    this.getOrganizationList()
    this.getRolesList()
    this.getUsersList()
    this.initField()
  },
  mounted() {
    console.log('xuanran')
  },
  computed: {
    // 根据类型选择的不同变换不同的名称列表
    authTargetList() {
      let authTargetType = this.rowPermissionForm.authTargetType
      if (authTargetType === 'dept') {
        return this.organizationList
      } else if (authTargetType === 'role') {
        return this.rolesList
      } else if (authTargetType === 'user') {
        return this.usersList
      } else {
        return []
      }
    },
  },
  // filters: {
  //   parseFilter(filterStr) {
  //     const filter = JSON.parse(filterStr)
  //     let str = ''
  //     filter.forEach((item) => {
  //       const { term, value } = item
  //       str += _this.termObj[term] + ' ' + value + ' '
  //     })
  //     return str
  //   },
  // },
  methods: {
    // 获取所有行权限
    async getAllRowPermissionList() {
      try {
        const res = await getAllRowPermissions({
          datasetId: this.param.id,
        })
        if (res.data.success) {
          this.rowPermissionTableData = this.deepClone(res.data.data)
          this.rowPermissionTableData.forEach((item) => {
            if (item.filterType === 'logic') {
              item.showValue = this.parseFilter(item.filter)
            } else {
              item.showValue = item.enumCheckField
            }
          })
        }
      } catch (err) {
        console.log(err)
      }
    },
    parseFilter(filterStr) {
      const filter = JSON.parse(filterStr)
      let str = ''
      filter.forEach((item) => {
        const { term, value } = item
        str += this.termObj[term] + ' ' + value + ' '
      })
      return str
    },
    parseEnumCheckField(enumCheckFieldStr) {
      const enumCheckField = JSON.parse(enumCheckFieldStr)
      return enumCheckField.join('、')
    },
    // 获取所有组织列表
    async getOrganizationList() {
      try {
        const res = await getallOrganizations()
        if (res.data.success) {
          const data = res.data.data
          this.organizationList = this.deepClone(data)
        }
      } catch (err) {
        console.log(err)
      }
    },
    // 获取所有角色列表
    async getRolesList() {
      try {
        const res = await getAllRoles()
        if (res.data.success) {
          const data = res.data.data
          this.rolesList = data.map((item) => {
            return {
              id: item.roleId,
              name: item.name,
            }
          })
        }
      } catch (err) {
        console.log(err)
      }
    },
    // 获取所有用户列表
    async getUsersList() {
      try {
        const res = await getAllUsers()
        if (res.data.success) {
          const data = res.data.data
          this.usersList = data.map((item) => {
            return {
              id: item.userId,
              name: item.username,
            }
          })
        }
      } catch (err) {
        console.log(err)
      }
    },
    // 获取该数据集的所有字段名
    async initField() {
      try {
        const res = await fieldListDQ(this.param.id)
        let tableFields = res.data.data
        this.fieldListData = tableFields.dimensionList.concat(tableFields.quotaList)
      } catch (err) {
        console.log(err)
      }
    },
    // 获取所有字段枚举值
    async getAllEnum(params) {
      try {
        const res = await getAllEnum(params)
        if (res.data.success) {
          this.enumList = res.data.data.map((item) => {
            return {
              value: item,
              label: item,
            }
          })
        }
      } catch (err) {
        console.log(err)
      }
    },
    // 点击添加行权限
    addRowPermission() {
      this.rowPermissionForm.enumCheckField = []
      this.isEdit = false
      this.dialogFormVisible = true
    },
    // 删除一个逻辑条件
    deleteTerm(filter, index) {
      filter.splice(index, 1)
    },
    // 添加一个逻辑条件
    addFilter() {
      this.rowPermissionForm.filter.push({
        term: 'eq',
        value: '',
      })
    },
    // 所选类型发生改变时清空名称字段的值
    handleAuthTargetTypeChange() {
      this.rowPermissionForm.authTargetId = ''
    },
    // 保存行权限配置的表单发送到后台
    saveRowPermission() {
      this.$refs.refRowPermissionForm.validate(async (valid) => {
        if (valid) {
          try {
            let data = this.deepClone(this.rowPermissionForm)
            data.filter = JSON.stringify(data.filter)
            console.log(data.enumCheckField)
            data.enumCheckField = data.enumCheckField.join(',')
            data.datasetId = this.param.id
            const res = this.isEdit ? await updateRowPermission(data) : await addRowPermission(data)
            if (res.data.success) {
              this.$message.success('保存成功')
              this.dialogFormVisible = false
              this.getAllRowPermissionList()
            } else {
              this.$message.err(res.data.msg)
            }
          } catch (error) {}
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    // 点击编辑操作按钮
    editRowPermission(row) {
      this.isEdit = true
      const cloneRow = this.deepClone(row)
      cloneRow.filter = JSON.parse(cloneRow.filter)
      cloneRow.enumCheckField = cloneRow.enumCheckField.split(',')
      // 把赋值写到nextTick里面的原因是因为如果第一次打开对话框是从编辑打开那么会导致关闭对话框后点新增，form的初始值就被记录为第一次打开时的值
      this.$nextTick(() => {
        for (let key in this.rowPermissionForm) {
          this.rowPermissionForm[key] = cloneRow[key]
          console.log(key, this.rowPermissionForm[key])
        }
        this.rowPermissionForm.id = cloneRow.id
        console.log('this.rowPermissionForm==>', this.rowPermissionForm)
      })
      this.dialogFormVisible = true
    },
    // 点击删除行权限按钮
    deleteRowPermission(row) {
      this.$confirm('此操作将删除此条行权限, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await deleteRowPermission({
            id: row.id,
          })
          if (res.data.success) {
            this.$message.success('删除成功')
            this.getAllRowPermissionList()
          } else {
            this.$message.err(res.data.msg)
          }
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除',
          })
        })
    },
    change() {},
    handleDialogClose() {
      this.$refs.refRowPermissionForm.resetFields()
    },
    datasetFieldIdChange(val) {
      const data = {
        dataSetTableFieldId: val,
        dataSetTableId: this.param.id,
        dataSourceId: this.param.dataSourceId,
      }
      this.getAllEnum(data)
      this.rowPermissionForm.enumCheckField = []
    },
  },
}
</script>

<style scoped lang='scss'>
.el-radio {
  width: unset;
}
.set-filter-select {
  width: 80px;
  margin: 0 10px;
}
.set-filter-input {
  width: 70px;
  margin-right: 20px;
}
</style>
