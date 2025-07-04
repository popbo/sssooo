<template>
  <div>
    <div class="nav-item">
      <div>
        <el-upload style="width:28%;padding:4px" class="upload-demo" ref="upload" accept=".fon,.ttf,.ttc,.FON,TTF,TTC,.otf,.OTF" :on-change="handleChange" :file-list="fileList" :auto-upload="false" :http-request="httpRequest">
          <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
          <el-button style="margin-left: 10px" size="small" type="success" @click="submitUpload">上传到服务器</el-button>
        </el-upload>
      </div>
      <el-scrollbar class="font_box" v-loading="loading" element-loading-text="资源加载中" element-loading-spinner="el-icon-loading" element-loading-background="rgba(0, 0, 0, 0.8)">
        <ul>
          <li v-for="(item,index) in allFont" :key="item.label+index">
            <div class="operation_btn" v-show="index>dicOption.fontFamily.length-1">
              <i class="icon el-icon-edit" @click="handleEdit(index, item)"></i>
              <i class="icon el-icon-delete" @click="handleDelete(index, item)"></i>
            </div>
            {{item.label}}
          </li>
        </ul>
      </el-scrollbar>
      <!-- 编辑弹窗 -->
      <el-dialog :visible.sync="dialogTableVisible" width="35%">
        <el-form label-width="128px" class="demo-ruleForm">
          <el-form-item label="本地字体名称">
            <el-input v-model="fontLable" />
          </el-form-item>
        </el-form>
        <span class="dialog-footer" style="text-align: center; display: block">
          <el-button @click="dialogTableVisible=false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import { dicOption } from '@/option/config'
import { getFontList, getFile, putFile, delFile } from '@/api/dataCollection'
import { url } from '@/config'
import { mapMutations } from 'vuex'
import { myStorage } from '@/utils/storage.js'
export default {
  data() {
    return {
      dialogTableVisible: false,
      dicOption: dicOption,
      fileList: [],
      input: '',
      fontLable: '',
      oldName: '',
      allFont: [],
      updataFont: [],
      headersObj: {
        Authorization: localStorage.getItem('Authorization'),
      },
      loading: false,
      id: '',
    }
  },
  created() {
    let windowsHeight = document.documentElement.clientHeight
    this.tableHeight = windowsHeight - 150
    console.log('ddddd', this.dicOption.fontFamily)
    this.getFontAllList()
  },
  methods: {
    ...mapMutations(['SET_ALL_FONTFAMILY_ARR']),
    getFontAllList() {
      this.loading = true
      getFile('custom', 'font').then((res) => {
        if (res.data.success) {
          this.loading = false
          this.updataFont = []
          res.data.data.records.forEach((el) => {
            this.updataFont.push({
              label: el.namePrefix,
              value: el.namePrefix,
              id: el.id
            })
          })
          this.allFont = this.dicOption.fontFamily.concat(this.updataFont)
          myStorage.set('allFontFamilyArr', this.allFont)
          // this.SET_ALL_FONTFAMILY_ARR(this.deepClone(this.allFont))
        } else {
          this.loading = false
          this.$message.error('获取字体资源失败')
        }
      })
    },
    // 修改打开模态框
    handleEdit(index, row) {
      this.oldName = row.label
      this.fontLable = row.label
      this.id = row.id
      this.dialogTableVisible = true
    },
    handleChange(file,fileList){
      this.fileList = fileList
      // console.log(file,7777)
    },
    // 删除
    handleDelete(index, row) {
      this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          const data = {
            id: row.id,
          }
          delFile('custom', 'font', data).then((res) => {
            if (res.data && res.data.code == 200) {
              this.$message({ type: 'success', message: '删除成功!' })
              this.getFontAllList()
            }
          })
        })
        .catch(() => {
          this.$message({ type: 'info', message: '已取消删除' })
        })
    },
    //修改提交
    save() {
      const isSameName = this.dicOption.fontFamily.some((x) => {
        return x.label === this.fontLable
      })
      if (isSameName) {
        this.$message.error('此字体名称已存在，请更换其他名称')
        return false
      }
      const data = {
        namePrefix: this.fontLable,
        id: this.id,
      }
      putFile('custom', 'font', data)
        .then((res) => {
          if (res.status === 200) {
            this.getFontAllList()
            this.$message.success('修改成功!')
          }
        })
        .catch((err) => {
          this.$message.error('修改失败!')
        })
      this.dialogTableVisible = false
    },
    httpRequest(param) {
      const formData = new FormData()
      formData.append('file', param.file)
      formData.append('fileName', this.input)
      formData.append('visualId', 0)
      formData.append('configId', 0)
      axios
        .post(url + '/oss/file/custom/font', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
            Authorization: localStorage.getItem('Authorization'),
          },
        })
        .then((res) => {
          if (res.status === 200) {
            this.$refs.upload.clearFiles()
            this.$message.success('上传文件成功!')
            this.getFontAllList()
          }
        })
        .catch((err) => {
          this.$message.error('上传失败!')
        })
    },
    submitUpload() {
      if(this.fileList.length===0){
        return this.$message.warning('请先上传文件成功!')
      }
      this.$refs.upload.submit()
    },
  },
}
</script>
<style lang="scss" scoped>
.el-scrollbar {
  height: calc(100vh - 200px);
}
.font_box ul {
  margin-top: 30px;
  flex-flow: row wrap;
  display: flex;
}
.font_box li {
  position: relative;
  height: 130px;
  width: 12.74%;
  border: 1px solid #394659;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 20px 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  background: #222933;
  color: #c0bebf;
  &:hover {
    .operation_btn {
      display: block;
    }
  }
  .operation_btn {
    position: absolute;
    right: 10px;
    top: 8px;
    display: none;
    .icon {
      font-size: 18px;
    }
    .el-icon-edit {
      margin-right: 10px;
    }
  }
}
.el-input {
  margin-top: 3px;
  margin-right: 10px;
  /deep/ .el-input__inner {
    background: #fff !important;
    border: 1px solid #dcdfe6 !important;
    height: 32px;
    line-height: 32px;
    color: #606266 !important;
  }
}
</style>