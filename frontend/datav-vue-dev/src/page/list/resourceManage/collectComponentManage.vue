<template>
  <div class="imagemanage_box">
    <el-container style="height: 100%">
      <el-aside width="200px">
        <ul class="img_type_menu">
          <li v-for="item in collectNavList" :class="{'is-active': item.id === activeId }" :key="item.id" @click="getCollectConList(item.id)">
            <div class="collectName" :title="item.name">{{item.name}}</div>
            <div class="operation_btn">
              <i class="icon el-icon-edit" @click.stop="editCollectNav(item)"></i>
              <i class="icon el-icon-delete" @click.stop="deleteCollectNav(item)"></i>
            </div>
          </li>
        </ul>
      </el-aside>
      <el-main>
        <collectCompList :collectConList='collectConList' @delectColectConItem="getCollectConList(activeId)"></collectCompList>
      </el-main>
    </el-container>
    <el-dialog
      title="编辑组件收藏分类"
      :visible.sync="collectionCateDialog"
      :modal="false"
      width="800px"
      @close="resetForm">
      <el-form ref="form" :model="form" :rules="rules" class="collectionForm">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" clearable></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="resetForm">取 消</el-button>
        <el-button type="primary" @click="editColleNavSave">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {getCollectType,getCollectBytype,postEditCollectType,postDeleteCollectType} from '@/api/collection'
import collectCompList from './collectCompList'

export default {
  data() {
    return {
      collectNavList:[],
      collectConList:[],
      collectionCateDialog:false,
      activeId:'',
      form:{
        name:"",
        id:""
      },
      rules: {
        name: [
            { required: true, message: '请输入分类名称', trigger: 'change' }
          ],
       },
    }
  },
  components: {
    collectCompList
  },
  created() {
    this.getCollectList()
    this.$bus.$on('submitCollect',()=>{ //编辑收藏弹窗确定保存时传值，重新获取右侧列表
      this.getCollectConList(this.activeId)
    })
  },

  methods: {
    getCollectList(){//获取收藏的列表，默认取第一项
      getCollectType().then(res=>{
        // console.log('获取收藏的',res)
        if(res.data.success&&res.data.data){
            this.collectNavList=res.data.data
            this.activeId = (this.collectNavList[0] || {}).id;
            this.getCollectConList(this.activeId)
        }
      })
    },
    getCollectConList(id){//根据id取值相对应的右侧列表
      this.activeId=id
      getCollectBytype(id).then(res=>{
        this.collectConList=res.data.data
      })
    },
    resetForm(){//清空表单
      this.collectionCateDialog=false
    },
    //编辑左侧导航
    editCollectNav(row){
      this.collectionCateDialog=true
      this.form.name=row.name
      this.form.id=row.id
    },
    editColleNavSave(){ //保存修改收藏分类
      this.$refs.form.validate(async valid => {
        if (!valid) return
        postEditCollectType(this.form).then(res=>{
          if(res.data.success){
            this.$message.success(res.data.msg)
            this.getCollectList()
            this.collectionCateDialog=false
          }else{
            this.$message.error(res.data.msg)
          }
        })
      })
    },
    deleteCollectNav(row){//删除收藏分类
      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
      postDeleteCollectType(row.id).then(res=>{
        if(res.data.success){
          this.$message.success('删除成功')
          this.getCollectList()
        }else{
          this.$message.error(res.data.msg)
        }
      })
      }).catch(() => {
      });
    }
  },
  beforeDestroy() {
    this.$bus.$off('submitCollect')
  }
}
</script>

<style scoped lang='scss'>
.imagemanage_box {
  height: 100%;
  .el-main{
    padding:0 20px 20px;
  }
}
.el-aside {
  padding-right: 10px;
  border-right: 1px solid #293241;
}
.img_type_menu {
  li {
    cursor: pointer;
    height: 40px;
    line-height: 40px;
    padding-left: 20px;
    position: relative;
    
    .collectName{
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      width: 130px;
    }
    &:hover {
      background-color: #3d404d;
      color: #ffffff;
      .operation_btn{
        opacity: 1;
        color:#3a89fe
      }
    }
    .operation_btn{
      position: absolute;
      right: 10px;
      top: 0;
      opacity: 0;
      .el-icon-edit {
        margin-right: 5px;
      }
    }
  }
}
.is-active {
  background-color: #3d404d;
  color: #3a89fe;
}
</style>
