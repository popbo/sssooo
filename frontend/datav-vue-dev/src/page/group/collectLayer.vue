<template>
  <!-- 点击收藏显示弹窗 -->
    <el-dialog
      :title="type==='edit'?'编辑收藏组件':'组件收藏'"
      :visible.sync="collectionDialog"
      :modal="false"
      width="800px"
      @close="resetForm">
      <el-form ref="form" :model="form" :rules="rules" class="collectionForm">
        <el-form-item label="收藏组件名称" prop="name">
          <el-input v-model="form.name" @blur="nameValidEvent" clearable></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description"></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="classify">
          <el-select :title="form.classify" v-model="form.classify"  filterable allow-create @change="selectType">
            <el-option  v-for="(item,index) in classifyList"
              :key="item.name+index"
              :label="item.name"
              :value="item.name"
              :title="item.name">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="缩略图" class="thum_form_item">
          <el-radio-group v-model="form.isDefault" @change="selectThumPic">
            <div class="thum_item">
              <el-radio label="0">默认图片</el-radio>
              <div class="imgs_box">
                <img :src="defaultPicPath" alt="">
              </div>
            </div>
            <div class="thum_item">
              <el-radio label="1">自定义</el-radio>
              <div class="imgs_box">
                <el-upload class="upload-demo"
                  :on-success="handleCoverImageSuccess"
                  :show-file-list="false"
                  :action="url+'/oss/file/custom/component'"
                  :headers="headersObj"
                  accept=".jpg,png,.gif,.jpeg,.JPG,.PNG,.GIF,.JPEG">
                    <img v-if="imageUrl" :src="imageUrl">
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                  </el-upload>
              </div>
            </div>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="resetForm">取 消</el-button>
        <el-button type="primary" @click="collectionBtn">{{type==='edit'?'确定':'收藏'}}</el-button>
      </span>
    </el-dialog>
</template>
<script>
import { postCollect,postEditCollect,getCollectType,postAddCollectType} from "@/api/collection"
import { getObj } from '@/api/visual';
import { url } from '@/config.js'
import findTree from "xe-utils/findTree";
import isUndefined from "xe-utils/isUndefined";
export default{
  name: 'collectLayer',
  inject: ['contain'],
  props:['type'],
  data(){
    return {
      collectionDialog:false,
      imageUrl: '',
      defaultPicPath:require('@/assets/folderStar.png'),
      isSameName:false,
      collectList:[],//获取收藏列表
      detail:'',//编辑时的传值
      configId:'',
      classifyList:[],
      selectTypeLable:'分类',
      headersObj:{
        Authorization:localStorage.getItem('Authorization')
      },
      form: {
          name: '',
          description:'',
          isDefault:'0',
          classify:"",
          typeId:'',
          id:'',
          url:require('@/assets/folderStar.png')
      },
       rules: {
        name: [
            { required: true, message: '请输入收藏组件的名称', trigger: 'blur' }
          ],
        classify: [
            { required: true, message: '请选择分类', trigger: 'change' }
          ],
       },
       url: ''
    }
  },
  created(){
    this.url = url
  //   this.getCollectTypeList()
  },
  methods:{
    getCollectTypeList(){
      //获取所有分类
      return new Promise((resolve, reject) => {
        getCollectType().then(res=>{
          if(res.data.success&&res.data.data){
              this.classifyList=res.data.data
          }
          resolve("success");
        })
      })
  },
   async openDialog(item){
      this.collectionDialog=true
      await this.getCollectTypeList()
      console.log('this.form',this.form,this.classifyList)
      if(this.type==='edit'){
        for (const key in this.form) {
          this.form[key]=item[key]
        }
        if(this.form.isDefault=='true'||this.form.isDefault=='1'){
          this.form.isDefault='1'
          this.imageUrl=this.form.url
        }else{
          this.imageUrl=''
          this.form.isDefault='0'
        }
        this.detail=item.detail
        this.form.classify=this.classifyList.filter(x=>x.id===item.typeId)[0].name
      }
    },
    // 上传封面图
    handleCoverImageSuccess(res){
      if(res.success){
        this.$message.success('上传成功')
        this.imageUrl = res.data.link;
        this.form.isDefault='1'
        this.selectThumPic('1');
      }else{
        this.$message.error(res.msg)
      }
    },
    //选择封面图
    selectThumPic(val){
      if(val==='0'){
        this.form.url=this.defaultPicPath
      }else{
        this.form.url=this.imageUrl
      }
    },
    //确认收藏
    collectionBtn(){
       this.$refs.form.validate(async valid => {
          if (!valid) return
            if(this.contain){
              const {data} = await getObj(this.contain.id)
            if(data.data){
              this.configId = data.data.config.id
            }else{
              return
            }
          }
          this.form.typeId=this.classifyList.filter(x=>x.name===this.form.classify)[0].id
          if(this.type==='edit'){
            const editCollectParam={
              ...this.form,
              detail:this.detail
            }
            postEditCollect(editCollectParam).then(res=>{
              if(res.data.success){
                this.$message.success('修改成功')
                this.resetForm()
                this.$bus.$emit('submitCollect')
              }else{
                this.$message.error(res.data.msg)
              }
            })
          }else{
            let selectData = []
            if(this.contain.active.length===1){
              let pidList = findTree(this.contain.nav,(item)=> item.index===this.contain.active[0])
              if(pidList){
                if(isUndefined(pidList.item.menu) && !pidList.item.children){
                  selectData.push(pidList.item)
                }else{
                  this.getChildlist(pidList.item.children,selectData)
                }
              }
                // console.log(pidList,66666)
            }else{
              this.contain.active.forEach(i=>{
                let pidList = findTree(this.contain.nav,(item)=> item.index===i)
                if(pidList){
                  if(isUndefined(pidList.item.menu) && !pidList.item.children){
                    selectData.push(pidList.item)
                  }
                }
              })
            }
            selectData = Array.from(new Set(selectData));//点击多次会取到多个相同的参数，所以去重
            // console.log(selectData,'selectData')
            const collectSureParam={
                ...this.form,
                detail:JSON.stringify(selectData)
            }
            postCollect(collectSureParam).then(res=>{
              if(res.data.success){
                this.resetForm()
                this.$message.success('收藏成功')
                this.$bus.$emit('submitCollect')
              }else{
                this.$message.error(res.data.msg)
              }
            })
         }
        });
    },
    getChildlist(list,newArr) {
      list.forEach(item=>{
        if(!item.menu){
          newArr.push(item)
        }
        if(item.children&&item.children.length>0){
          this.getChildlist(item.children,newArr)
        }
      })
    },
    //验证是否有同名项
    nameValidEvent(){
      this.isSameName=this.collectList.some(x=>x.name===this.form.name)
      if(this.isSameName){
        this.$message.error('当前组件名已存在，请更换')
      }
    },
    resetForm() {
      this.$refs.form.resetFields();
      this.form.isDefault='0'
      this.imageUrl=''
      this.selectThumPic('0')
      this.form.classify=''
      this.collectionDialog = false
    },
    async selectType(val){
      const isHasVal=this.classifyList.filter(x=>x.name===val)
      if(isHasVal.length===0){
      await postAddCollectType({name:val})
      this.getCollectTypeList()
      // this.form.typeId=this.classifyList.filter(x=>x.name===this.form.classify)[0].id
    }
    console.log('分类的结果',this.form.classify)
    }
  }
}
</script>
<style scoped>
.thum_form_item .el-radio-group{
  display: flex;
  width: 100%;
}
.thum_item{
  width: 276px;
  margin-right: 30px;
}
.collectionForm /deep/ .imgs_box{
  width: 100%;
  height: 170px;
  border:1px solid #859094;
  margin-top:20px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.collectionForm /deep/ .imgs_box img{
  max-width: 274px;
  max-height:168px;
}
.collectionForm /deep/ .el-form-item__content{
  padding-right: 0;
}
.collectionForm /deep/ .el-form-item__label{
  padding-left:0
}
.collectionForm /deep/ .el-upload.el-upload--text{
    width: 274px;
    height: 168px;
    display: flex;
    justify-content: center;
    align-items: center;
}
.collectionForm /deep/ .upload-demo{
  width: 274px;
    height: 168px;
}
</style>
