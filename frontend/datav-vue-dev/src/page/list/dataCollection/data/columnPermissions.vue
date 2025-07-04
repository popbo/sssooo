<template>
  <div>
    <el-button
      type="primary"
      icon="el-icon-plus"
      size="mini"
      @click="addColumnPermission"
      >添加</el-button
    >
    <el-table :data="columnPermissionTableData" style="width: 100%" size="mini">
      <el-table-column prop="typeName" label="类型"> </el-table-column>
      <el-table-column prop="name" label="名称"> </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button
            type="primary"
            icon="el-icon-edit"
            circle
            size="mini"
            @click="editColumnPermission(scope.row)"
          ></el-button>
          <el-button
            type="danger"
            icon="el-icon-delete"
            circle
            size="mini"
            @click="deleteColumPermission(scope.row)"
          ></el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 列权限设置表单 -->
    <el-dialog
      :title="isEdit ? '编辑列权限' : '添加列权限'"
      :visible.sync="dialogFormVisible"
      width="40%"
      @close="handleDialogClose"
      :close-on-press-escape="false"
    >
      <el-form
        :model="columnPermissionform"
        ref="refColumnPermissionform"
        :inline="true"
      >
        <el-form-item label="禁用">
          <el-switch v-model="columnPermissionform.enable"> </el-switch>
        </el-form-item>
        <el-form-item
          label="类型"
          prop="authTargetType"
          :rules="[
            { required: true, message: '请选择类型', trigger: 'change' },
          ]"
        >
          <el-select
            v-model="columnPermissionform.authTargetType"
            placeholder="请选择类型"
            size="mini"
            @change="handleAuthTargetTypeChange"
          >
            <el-option label="组织" value="dept"></el-option>
            <el-option label="角色" value="role"></el-option>
            <el-option label="用户" value="user"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          label="名称"
          prop="authTargetId"
          :rules="[
            { required: true, message: '请选择名称', trigger: 'change' },
          ]"
        >
          <el-cascader
            v-if="columnPermissionform.authTargetType === 'dept'"
            :options="authTargetList"
            v-model="columnPermissionform.authTargetId"
            :props="organizationListProps"
            size="mini"
          ></el-cascader>
          <el-select
            v-else
            v-model="columnPermissionform.authTargetId"
            placeholder="请选择名称"
            size="mini"
          >
            <el-option
              v-for="item in authTargetList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-table
        :data="tableData"
        ref="multipleTable"
        style="width: 100%"
        size="mini"
      >
        <el-table-column width="50px">
          <template slot="header" slot-scope="scope">
            <el-checkbox
              :indeterminate="isIndeterminate"
              v-model="checkAll"
              @change="handleCheckAllChange"
            ></el-checkbox>
          </template>
          <template slot-scope="scope">
            <el-checkbox
              v-model="scope.row.selected"
              @change="handleCheckOneChange"
            ></el-checkbox>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="类型"> </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-radio-group v-model="scope.row.opt">
              <el-radio :disabled="!scope.row.selected" label="Prohibit"
                >禁用</el-radio
              >
              <el-radio :disabled="!scope.row.selected" label="Desensitization"
                >脱敏</el-radio
              >
            </el-radio-group>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" size="mini"
          >取 消</el-button
        >
        <el-button type="primary" @click="saveColumnPermission" size="mini"
          >确 定</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getallOrganizations,
  getAllRoles,
  getAllUsers,
  addColumnPermission,
  getAllColumnPermissions,
  updateColumnPermission,
  deleteColumPermission,
} from '@/api/login.js'
import { fieldListDQ } from '@/api/dataCollection'
export default {
  name: 'columnPermissions',
  props: {
    param: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      columnPermissionTableData: [],
      dialogFormVisible: false,
      formLabelWidth: '90px',
      columnPermissionform: {
        enable: false,
        authTargetType: '',
        authTargetId: '',
      },
      organizationList: [],
      rolesList: [],
      usersList: [],
      organizationListProps: {
        label: 'name',
        value: 'deptId',
      },
      tableData: [],
      initTableData: [],
      fieldListData: [],
      checkAll: false,
      isIndeterminate: false,
      isEdit: false,
    }
  },
  computed: {
    // 根据类型选择的不同变换不同的名称列表
    authTargetList() {
      let authTargetType = this.columnPermissionform.authTargetType
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
  watch: {
    param: function () {
      this.initField()
      this.getAllRowPermissionList()
    },
  },
  created() {
    this.getAllRowPermissionList()
    this.getOrganizationList()
    this.getRolesList()
    this.getUsersList()
    this.initField()
  },
  methods: {
    // 获取所有行权限
    async getAllRowPermissionList() {
      try {
        const res = await getAllColumnPermissions({
          datasetId: this.param.id,
        })
        if (res.data.success) {
          this.columnPermissionTableData = res.data.data
        }
      } catch (err) {
        console.log(err)
      }
    },
    addColumnPermission() {
      this.isEdit = false
      this.tableData = this.deepClone(this.initTableData)
      ;(this.checkAll = false),
        (this.isIndeterminate = false),
        (this.dialogFormVisible = true)
    },
    // 所选类型发生改变时清空名称字段的值
    handleAuthTargetTypeChange() {
      this.columnPermissionform.authTargetId = ''
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
          this.rolesList = data.map(item => {
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
          this.usersList = data.map(item => {
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
        this.fieldListData = tableFields.dimensionList.concat(
          tableFields.quotaList
        )
        this.initTableData = this.fieldListData.map(item => {
          return {
            id: item.id,
            name: item.name,
            selected: false,
            opt: 'Prohibit',
          }
        })
      } catch (err) {
        console.log(err)
      }
    },
    handleCheckAllChange(val) {
      this.isIndeterminate = false
      this.tableData.forEach(item => {
        item.selected = val
      })
    },
    handleCheckOneChange() {
      let totalCount = this.tableData.length
      let someStatusCount = 0
      this.tableData.forEach(item => {
        if (item.selected === true) {
          someStatusCount++
        }
      })
      this.checkAll = totalCount === someStatusCount
      this.isIndeterminate = someStatusCount > 0 && someStatusCount < totalCount
    },
    // 保存新增的列权限
    saveColumnPermission() {
      this.$refs.refColumnPermissionform.validate(async valid => {
        if (valid) {
          try {
            const { authTargetId, authTargetType, enable, id } =
              this.columnPermissionform
            const data = {
              id,
              // authTargetId: JSON.stringify(authTargetId),
              authTargetId: String(authTargetId),
              authTargetType,
              datasetId: this.param.id,
              permissions: JSON.stringify({
                enable,
                columns: this.tableData,
              }),
            }
            const res = this.isEdit
              ? await updateColumnPermission(data)
              : await addColumnPermission(data)
            if (res.data.success) {
              this.$message.success('保存成功')
              this.dialogFormVisible = false
              this.getAllRowPermissionList()
            } else {
              this.$message.error(res.data.msg)
            }
          } catch (err) {
            console.log(err)
          }
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    // 点击列权限列表的编辑按钮
    editColumnPermission(row) {
      this.isEdit = true
      let permissions = JSON.parse(row.permissions)
      this.$nextTick(() => {
        this.columnPermissionform.authTargetType = row.authTargetType
        this.columnPermissionform.authTargetId = JSON.parse(row.authTargetId)
        this.columnPermissionform.enable = permissions.enable
        this.columnPermissionform.id = row.id
        this.tableData = permissions.columns
        this.handleCheckOneChange()
      })
      this.dialogFormVisible = true
    },
    // 点击删除列权限按钮
    deleteColumPermission(row) {
      this.$confirm('此操作将删除此条列权限, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await deleteColumPermission({
            id: row.id,
          })
          if (res.data.success) {
            this.$message.success('删除成功')
            this.getAllRowPermissionList()
          } else {
            this.$message.error(res.data.msg)
          }
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除',
          })
        })
    },
    handleDialogClose() {
      this.$refs.refColumnPermissionform.resetFields()
    },
  },
}
</script>

<style scoped lang="scss">
.el-radio {
  width: unset;
}
</style>
