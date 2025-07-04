<template>
  <div>
    <el-dialog
      width="30%"
      :title="isEdit ? '编辑驱动' : '添加驱动'"
      :visible.sync="addDialogVisible"
    >
      <el-form
        label-position="left"
        label-width="100px"
        :model="newDriveForm"
        :rules="addDriveRules"
      >
        <el-form-item label="驱动名称" prop="name">
          <el-input v-if="isEdit" v-model="driveForm.name"></el-input>
          <el-input v-else v-model="newDriveForm.name"></el-input>
        </el-form-item>
        <el-form-item label="驱动类型" prop="type">
          <el-select
            v-if="isEdit"
            v-model="driveForm.type"
            placeholder="请选择驱动类型"
            class="select-width"
            disabled
          >
            <el-option
              v-for="item in allTypes"
              :key="item.name"
              :label="item.label"
              :value="item.name"
            />
          </el-select>
          <el-select
            v-else
            v-model="newDriveForm.type"
            placeholder="请选择驱动类型"
            class="select-width"
          >
            <el-option
              v-for="item in allTypes"
              :key="item.name"
              :label="item.label"
              :value="item.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-if="isEdit"
            type="textarea"
            placeholder="请输入"
            maxlength="200"
            v-model="driveForm.description"
          ></el-input>
          <el-input
            v-else
            type="textarea"
            placeholder="请输入"
            maxlength="200"
            v-model="newDriveForm.description"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save()">确定</el-button>
      </div>
    </el-dialog>
    <el-dialog
      :visible.sync="dMVisible"
      width="100%"
      :before-close="handleClose"
      top="56px"
      :fullscreen="true"
      :show-close="false"
    >
      <div slot="title" class="dialog-nav">
        <div class="nav-left">
          <el-button
            icon="el-icon-arrow-left"
            class="back-arrow"
            @click="handleClose"
          />
          <span>驱动管理</span>
        </div>
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="mini"
          class="add-drive"
          @click="addDrive"
          >添加驱动</el-button
        >
      </div>
      <el-container class="outsider-contain">
        <el-aside width="15%">
          <el-form @submit.native.prevent>
            <el-form-item class="form-item">
              <el-input
                v-model="nodeKey"
                size="mini"
                :placeholder="'搜索'"
                prefix-icon="el-icon-search"
                clearable
                class="main-area-input"
              />
            </el-form-item>
          </el-form>
          <div>
            <el-tree
              :data="treeData"
              node-key="name"
              :props="defaultProps"
              @node-click="handleNodeClick"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              ref="drivesTree"
            >
              <span class="custom-tree-node item-father" slot-scope="{ data }">
                <span class="item-text">
                  <i class="el-icon-folder" v-show="!data.id" />
                  <span style="margin-left: 5px">{{ data.name }}</span>
                </span>
                <span class="item-child" @click.stop>
                  <span class="add-item" v-if="data.id">
                    <el-dropdown size="small" trigger="click">
                      <span class="el-dropdown-link">
                        <el-button
                          icon="el-icon-more"
                          type="text"
                          size="small"
                        />
                      </span>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item
                          icon="el-icon-copy-document"
                          @click.native="modifyDrive(data)"
                          >编辑</el-dropdown-item
                        >
                        <el-dropdown-item
                          icon="el-icon-delete"
                          @click.native="deleteDrive(data)"
                          >删除</el-dropdown-item
                        >
                      </el-dropdown-menu>
                    </el-dropdown>
                  </span>
                  <span class="handle-item" v-else>
                    <span class="el-dropdown-link">
                      <el-button
                        icon="el-icon-plus"
                        type="text"
                        size="small"
                        @click="addDrive(data)"
                      />
                    </span>
                  </span>
                </span>
              </span>
            </el-tree>
          </div>
        </el-aside>
        <el-main>
          <div class="no-drive" v-if="!isChildNode">
            <img
              src="data:image/png;base64,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"
              ondragstart="return false"
              style="width: 125px; height: 125px"
            />
            <span class="no-drive-text">请在左侧选择驱动</span>
          </div>
          <div class="drive-detail" v-else>
            <p>{{ driveForm.name }}</p>
            <div class="row-rules">
              <span>驱动文件</span>
            </div>
            <div class="drive-form">
              <el-form
                label-position="top"
                label-width="80px"
                :model="formLabelAlign"
              >
                <el-form-item required>
                  <span slot="label" style="font-size: 14px">驱动类</span>
                  <el-input v-model="driveForm.driverClass"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-upload
                    class="upload-demo"
                    accept=".jar"
                    action=""
                    :http-request="uploadDrive"
                    :limit="1"
                    :file-list="fileList"
                    list-type="picture"
                    :on-remove="deleteJar"
                    :on-exceed="handleExeed"
                  >
                    <el-button
                      size="medium"
                      icon="el-icon-upload2"
                      type="primary"
                      >上传文件</el-button
                    >
                    <div slot="tip" class="el-upload__tip">
                      仅支持上传JAR格式的文件
                    </div>
                  </el-upload>
                </el-form-item>
              </el-form>
            </div>
          </div>
          <div class="form-footer" v-if="isChildNode">
            <el-button type="primary" @click="updateCurrentDrive(driveForm)"
              >保存</el-button
            >
          </div>
        </el-main>
      </el-container>
    </el-dialog>
  </div>
</template>

<script>
import {
  getDrivesList,
  getDrivesDetails,
  upDrive,
  saveDrive,
  updateDrive,
  removeDrive,
  removeDriveJar,
} from '@/api/newdb.js'
export default {
  props: ['dMVisible'],
  data() {
    return {
      isEdit: false,
      dialogVisible: true,
      addDialogVisible: false,
      nodeKey: '',
      treeData: [],
      isChildNode: false,
      driveForm: {
        description: '',
        driverClass: '',
        id: '',
        jarPath: '',
        name: '',
        type: '',
      },
      newDriveForm: {
        description: '',
        driverClass: '',
        id: '',
        jarPath: '',
        name: '',
        type: '',
      },
      defaultProps: {
        children: 'children',
        label: 'name',
      },
      fileList: [
        // { name: '测试文件名.jar', url: require('@/assets/svg/jar.svg') },
      ],
      allTypes: [
        {
          name: 'mysql',
          label: 'MySQL',
          type: 'jdbc',
          extraParams:
            'serverTimezone=UTC&characterEncoding=UTF-8&connectTimeout=5000&useSSL=false&allowPublicKeyRetrieval=true',
        },
        { name: 'db2', lable: 'Db2', type: 'jdbc', extraParams: '' },
        { name: 'hive', label: 'Apache Hive', type: 'jdbc', extraParams: '' },
        { name: 'oracle', label: 'Oracle', type: 'jdbc' },
        {
          name: 'sqlServer',
          label: 'SQL Server',
          type: 'jdbc',
          extraParams: '',
        },
        { name: 'pg', label: 'PostgreSQL', type: 'jdbc', extraParams: '' },
        { name: 'ck', label: 'ClickHouse', type: 'jdbc', extraParams: '' },
        {
          name: 'gbase',
          label: 'gbase',
          type: 'jdbc',
          extraParams:
            'GBASEDBTSERVER={server};SQLMODE=GBase;DB_LOCALE=zh_CN.57372',
        },
        {
          name: 'dm',
          label: 'dm',
          type: 'jdbc',
          extraParams: '',
        },
        {
          name: 'kingbase',
          label: 'kingbase',
          type: 'jdbc',
          extraParams: '',
        },
      ],
      addDriveRules: {
        name: [
          { required: true, message: '请输入', trigger: 'blur' },
          { min: 1, max: 20, message: '名称长度不符合要求', trigger: 'blur' },
        ],
        type: [{ required: true, message: '请选择', trigger: 'blur' }],
      },
    }
  },
  created() {
    this.queryDbDrivesList()
  },
  watch: {
    nodeKey(val) {
      this.$refs.drivesTree.filter(val)
    },
  },
  methods: {
    //获取数据库驱动列表
    async queryDbDrivesList() {
      try {
        const res = await getDrivesList()
        // console.log('获取列表', res)
        if (res.status === 200) {
          const treeData = this.buildTree(res.data.data)
          this.treeData = treeData
          this.isChildNode = false
        } else {
          this.$message('更新驱动列表失败')
        }
      } catch (error) {}
    },
    //将后端数据重组成树形
    buildTree(array) {
      const types = {}
      const newArr = []
      for (let index = 0; index < array.length; index++) {
        const element = array[index]
        if (!(element.type in types)) {
          types[element.type] = []
          newArr.push({
            name: element.type,
            children: types[element.type],
          })
        }
        types[element.type].push(element)
      }
      return newArr
    },
    handleNodeClick(obj, node, event) {
      // console.log('点击子节点', obj)
      // this.driveForm = obj
      if (!obj.id) {
        return (this.isChildNode = false)
      }
      this.isChildNode = true
      this.getDriveInfo(obj.id)
    },
    addDrive(data) {
      if (data) {
        // console.log('当前data', data)
        this.newDriveForm.type = data.name
      }
      this.addDialogVisible = true
    },
    handleClose() {
      this.$emit('update:dMVisible', false)
    },
    filterNode(value, data) {
      if (!value) return true
      return data.name.indexOf(value) !== -1
    },
    async uploadDrive(params) {
      // console.log('上传的文件', params)
      const filename = params.file.name
      const form = new FormData()
      form.append('file', params.file)
      form.append('id', this.driveForm.id)
      try {
        const res = await upDrive(form)
        // console.log('上传结果', res)
        if (res.status === 200) {
          this.$message.success('驱动上传成功')
          const newForm = { ...this.driveForm, jarPath: res.data.data.jarPtah }
          this.updateCurrentDrive(newForm)
          this.fileList = [
            {
              name: filename,
              url: require('@/assets/svg/jar.svg'),
            },
          ]
        }
      } catch (error) {}
    },
    // 获取某个节点的驱动详情
    async getDriveInfo(id) {
      this.fileList = []
      try {
        const res = await getDrivesDetails(id)
        this.driveForm = res.data?.data
        // console.log('节点详情', res)
        const filename = this.driveForm.jarFileName
        if (filename) {
          this.fileList = [
            {
              name: filename,
              url: require('@/assets/svg/jar.svg'),
            },
          ]
        }
      } catch (error) {}
    },
    save() {
      this.isEdit
        ? this.updateCurrentDrive(this.driveForm)
        : this.addNewDrive(this.newDriveForm)
    },
    // 添加驱动
    async addNewDrive(datasourceDriver) {
      try {
        const res = await saveDrive(datasourceDriver)
        console.log('添加新驱动结果', res)
        if (res.status === 200) {
          this.$message.success('添加新驱动成功')
          await this.queryDbDrivesList()
          this.resetNewForm()
        } else {
          this.fileList = []
          this.$message.error('添加驱动失败')
        }
      } catch (error) {
      } finally {
        this.addDialogVisible = false
      }
    },
    // 更新驱动
    async updateCurrentDrive(form) {
      const { id } = form
      try {
        const res = await updateDrive(form)
        // console.log('更新后台表单结果', res)
        if (res.status === 200) {
          this.$message.success('更新驱动成功')
          await this.getDriveInfo(id)
          this.addDialogVisible = false
        } else {
          this.$message.error('更新驱动失败')
        }
      } catch (error) {}
    },
    //删除驱动
    async deleteDrive(form) {
      // console.log('删除驱动获取的表单数据', form)
      try {
        const res = await removeDrive(form)
        // console.log('添加新驱动结果', res)
        if (res.status === 200) {
          this.$message.success('删除驱动成功')
          await this.queryDbDrivesList()
        } else {
          this.$message.error('删除驱动失败')
        }
      } catch (error) {}
    },
    //删除jar包
    async deleteJar(file, fileList) {
      // console.log('删除jar包', this.driveForm)
      const { jarPath, id } = this.driveForm
      const cloneFileList = [{ ...this.fileList[0] }]
      try {
        const res = await removeDriveJar(jarPath.toString())
        // console.log('添加新驱动结果', res)
        if (res.status === 200) {
          this.$message.success('删除驱动jar包成功')
          this.fileList = []
          this.getDriveInfo(id)
        } else {
          this.$message.error('删除驱动jar包失败')
          this.fileList = cloneFileList
        }
      } catch (error) {}
    },
    handleExeed(files, fileList) {
      // console.log('文件超过最大值', fileList)
      this.$message.warning('上传驱动超过最大数量', 2000)
    },
    modifyDrive(data) {
      this.isEdit = true
      this.driveForm = data
      // console.log('data', data)
      this.addDialogVisible = true
    },
    resetNewForm() {
      this.newDriveForm = {
        description: '',
        driverClass: '',
        id: '',
        jarPath: '',
        name: '',
        type: '',
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.dialog-nav {
  width: 100%;
  height: 50px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  .nav-left {
    display: flex;
    align-items: center;
  }
}

::v-deep .el-main {
  border-left: 1px solid rgba($color: #bfbfbf, $alpha: 0.2);
  position: relative;
  overflow: hidden;
}
.form-footer {
  position: absolute;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  bottom: 0;
  left: 10px;
  width: 100%;
  height: 10%;
  border-top: 1px solid rgba($color: #bfbfbf, $alpha: 0.5);
  padding-right: 20px;
}
::v-deep .outsider-contain {
  height: 100%;
}
.back-arrow {
  border: none;
  font-weight: 700;
}
.add-drive {
  height: 32px !important;
  margin: 0 20px;
}
.no-drive {
  height: 100% !important;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.drive-detail {
  > p {
    font-size: 16px;
    font-weight: 500;
    color: #fff;
  }
  .row-rules {
    display: flex;
    align-items: center;
    position: relative;
    font-family: PingFang SC;
    font-size: 14px;
    font-weight: 500;
    line-height: 22px;
    padding-left: 10px;
    margin: 24px 0 16px 0;
  }
  .row-rules:before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    -webkit-transform: translateY(-50%);
    transform: translateY(-50%);
    height: 14px;
    width: 2px;
    background: #3370ff;
  }
  .drive-form {
    width: 30%;
  }
}

.no-drive-text {
  text-align: center;
  margin: 10px 0;
}
.item-father {
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-width: 90%;
  min-height: 21px;
  font-size: 14px;
  padding: 0 8px;
  .item-child {
    visibility: hidden;
  }
}
.item-father:hover .item-child {
  visibility: visible;
}

// ::v-deep .el-dialog__header {
//   padding: 10px 0;
// }
::v-deep .el-dialog__body {
  padding: 10px 20px;
  // height: 100vh;
}
.el-tree {
  background-color: transparent;
}
.search-input {
  padding: 12px 0;
}

.custom-tree-container {
  margin-top: 10px;
  .block {
    margin: 0;
  }
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
.el-tree-node:focus > .el-tree-node__content {
  background-color: #192639;
}

.el-tree {
  /deep/ .el-tree-node__content {
    background-color: transparent;
    &:hover {
      background-color: #192639;
      // color: #fff;
    }
  }
}
/deep/ .el-tree .el-tree-node.is-current > .el-tree-node__content {
  background-color: #192639;
}
::v-deep .custom-tree-node-list {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 8px;
  color: #bfbfbf;
}
.item-text {
  font-size: 16px;
}
.tree-list > .el-tree-node__expand-icon.is-leaf {
  display: none;
}
.el-upload__tip {
  font-size: 16px;
}
.el-aside {
  padding: 0 10px;
}
.el-container .outsider-contain {
  height: 90vh;
}
</style>
