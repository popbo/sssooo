<template>
<div>
  <el-scrollbar class="imgList">
    <ul>
      <li v-for="(item,index) in templateList" :key="index">
        <div class="operation_btn">
            <i class="icon el-icon-edit" @click="handleEdit(item)"></i>
            <i class="icon el-icon-delete" @click="handleDelete(index, item)"></i>
          </div>
        <img :src="item.backgroundUrl"/>
        <h4>{{item.title}}</h4>
      </li>
    </ul>
  </el-scrollbar>
  <!-- 编辑弹窗 -->
  <el-dialog :visible.sync="dialogTableVisible" width="35%">
    <el-form label-width="128px" class="demo-ruleForm" :model="form" :rules="rules" ref="form">
      <el-form-item label="模板名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
    </el-form>
    <span class="dialog-footer" style="text-align: center; display: block">
      <el-button @click="dialogTableVisible=false">取 消</el-button>
      <el-button type="primary" @click="save">确 定</el-button>
    </span>
  </el-dialog>
</div>
</template>

<script>
import {getList,delObj,templateUpdateObj } from '@/api/visual';
export default {
  data () {
    return {
      dialogTableVisible: false,
      currentTempId:'',
      category:'999999',
      templateList:[],
      form:{
        name:''
      },
      rules: {
          name: [
            { required: true, message: '请输入模板名称', trigger: 'blur' },
          ],
      }
    }
  },
  methods: {
    // 修改打开模态框
    handleEdit(row) {
      console.log('的点点滴滴多多多',row)
      this.currentTempId=row.id
      this.form.name=row.title
      this.dialogTableVisible = true;
    },
    // 删除
    handleDelete(index,item) {
      this.$confirm('是否确认永久删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delObj(item.id).then(() => {
          this.templateList.splice(index, 1)
          this.$message.success('删除成功')
        })
      })
      .catch(() => {
        this.$message({ type: "info", message: "已取消删除" });
      });
    },
    getTemplatePicList(){
      getList({
        category: this.category
      }).then(res=>{
        
        if(res.data.success){
          this.templateList=res.data.data.records
          console.log('模板列表',this.templateList)
        }else{
          this.$message.error(res.data.msg)
        }
      })
    },
    //修改提交
    save() {
      this.$refs.form.validate((valid) => {
          if (!valid) return
          templateUpdateObj({visual:{
            id: this.currentTempId,
            category: this.category,
            title: this.form.name
          }}).then(() => {
            this.$message.success('修改成功');
            this.dialogTableVisible = false;
            this.getTemplatePicList()
          })
        });
    },
  },
  created(){
    this.getTemplatePicList()
  }
}
</script>

<style lang="scss" scoped>
.el-scrollbar{
  height: calc(100vh - 200px);
}
.imgList li{
  // height: 130px;
  width: 12.74%;
  display: inline-block;
  margin:0 14px;
  h4{
    text-align: center;
    height: 30px;
    line-height: 30px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-weight: 500;
    color: #bfbfbf;
    margin-top: 10px;
  }
  img{
    width:100%;
    height: 110px;
    border: 1px solid #3a475a;
    display: block;
  }
  &:hover{
    .operation_btn{
      opacity: 1;
    }
    img{
      border: 1px solid #409eff;
    }
  }
  .operation_btn{
    float: right;
    background: #409eff;
    height: 26px;
    line-height: 26px;
    padding:0 10px;
    opacity: 0;
    &.opacityShow{
      display: none;
    }
    .icon{
      font-size:18px;
      color: #fff;
    }
    .el-icon-edit{
      margin-right: 10px;
    }
  }
}
</style>