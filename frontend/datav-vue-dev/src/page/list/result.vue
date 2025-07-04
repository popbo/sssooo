<template>
  <el-dialog 
        title="导入大屏"
        :close-on-click-modal="false"
        :visible.sync="show"
        width="650px"
        @close="resetFormData">
            <div class="batch">
              <span>
                批量导入
              </span>
              <el-switch
                v-model="batchValue"
                active-color="#13ce66"
                width="50"
                @change="sChange"
                >
              </el-switch>
            </div>
            <el-form ref="form" :model="form" :rules="rules" :inline="true">
              <el-form-item prop="file">
                <el-upload
                  class="upload-demo"
                  ref="adModel"
                  drag
                  :http-request="httpRequest"
                  :on-exceed="changeUpload"
                  :on-change="fileChange"
                  :on-remove="fileRemove"
                  :file-list="fileList"
                  :limit="limit"
                  accept=".zip">
                  <i class="el-icon-upload"></i>
                  <div class="el-upload__text">
                    将文件拖到此处，或<em>点击上传</em>
                    <br>
                    <span style="color:#b4b7c1">必须是zip文件才能导入</span>  
                  </div>
                </el-upload>
              </el-form-item>
              <el-form-item class="fileNameItem" label="导入大屏名称" prop="visualName" v-if="!batchValue">
                <el-input v-model="form.visualName"></el-input>
              </el-form-item>
            </el-form>
    <!-- <monaco-editor v-model="json"></monaco-editor> -->
    <span slot="footer"
          class="dialog-footer">
          <el-button
                 size="small"
                 type="danger"
                 @click="resetFormData">取消</el-button>
      <el-button @click="importData"
                 size="small"
                 type="primary">确定</el-button>
      
    </span>

  </el-dialog>

</template>

<script>
// import MonacoEditor from '@/page/components/editor'
export default {
  components: {  },
  data () {
    return {
      limit:1,
      show: false,
      batchValue:false,
      fileList:[],
      form:{
        visualName:'',
        file:null,
      },
      param:{
        category:'',
        file:'',
        visualName:''
      },
      rules: {
        visualName: [
            { required: true, message: '请填写导入大屏名称', trigger: 'blur' }
          ],
          file: [
            { required: true, message: '请上传文件', trigger: 'change' }
          ],
      }
    }
  },
  methods: {
    sChange(val) {
      this.$refs.adModel.clearFiles()
      this.form.visualName=""
      this.form.file=null
      this.fileList = [];
      this.$refs.form.clearValidate();
      if(val){
        this.limit = 100
      }else{
        this.limit = 1;
      }
    },
    importData () {
      if(this.batchValue){
        if(this.fileList.length===0){
          return this.$refs.form.validateField('file')
        }
        this.$emit('importDataList',this.fileList)
        this.resetFormData();
        console.log(6666,this.fileList)
      }else{
        this.$refs.form.validate((valid) => {
        if (!valid) return
          console.log('进来了111',this.param)
          this.param.visualName=this.form.visualName
          this.$emit('importData',this.param)
          this.resetFormData()
        });
      }
    },
    resetFormData(){
      this.show = false;
      this.$refs.adModel.clearFiles()
      this.form.visualName=""
      this.form.file=null
      this.fileList = [];
      this.$refs.form.clearValidate();
    },
    httpRequest(param){
      const formData = new FormData()
      formData.append('file', param.file)
      console.log('file',param)
      this.param.file=formData
      this.form.visualName=param.file.name
      this.$refs.form.clearValidate();
    },
    changeUpload(){
      if(!this.batchValue){
        this.$message.warning(`当前限制选择 1 个文件，请删除后继续上传！`)
      }
    },
    fileChange(file, fileList) {
      if(!this.batchValue){
        this.form.file = file
        if (fileList.length !== 0) {
          this.$refs.form.validateField('file')
        }
      }else{
        console.log(7777,file, fileList)
        let existFile = fileList.slice(0, fileList.length - 1).find(f => f.name === file.name)
        if(existFile){
          fileList.pop()
          this.$message.warning(file.name+" 文件已存在！")
        }
        this.$refs.form.clearValidate();
        this.fileList = fileList;
      }
    },
    fileRemove(uploadFile, fileList) {
      if(this.batchValue){
        this.fileList = fileList;
      }
      if (fileList.length === 0) {
          this.form.file = null
          this.$refs.form.validateField('file')
      }
    }
    // exportData () {
    //   var zip = new window.JSZip();
    //   zip.file("data.txt", this.json);
    //   zip.generateAsync({ type: "base64" })
    //     .then(function (content) {
    //       location.href = "data:application/zip;base64," + content;
    //     });
    // }
  }
}
</script>

<style lang="scss" scoped>
.upload-demo{
  /deep/.el-upload{
    width:100%;
    line-height: 25px;
    border-color:#333542;
    .el-upload-dragger{
      width:100%;
      background: #181a24;
      .el-upload__text{
        color:#fff
      }
    }
  }
}
.batch{
  position: relative;
  top: -15px;
  color: #fff;
  span{
    margin-right: 20px;
  }
}
.el-form{
  /deep/.el-form-item{
    width:100%;
    margin-bottom:10px;
  }
  /deep/.el-form-item__content{
    width:100%;
    padding-right: 0px;
  }
  /deep/ .el-form-item__label{
    padding-left: 0;
  }
  /deep/.fileNameItem {
    .el-form-item__content{
      width: calc(100% - 100px);
    }
  }
}
</style>