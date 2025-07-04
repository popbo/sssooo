<template>
  <el-dialog title="模型"
             width="80%"
             :close-on-click-modal="false"
              v-loading="loading" 
              element-loading-text="导入中..."
             :visible.sync="imgVisible">
    <div class="click-add">
      <el-button size="small"
          icon="el-icon-upload"
          type="primary" @click="getAdd">点击上传</el-button>
    </div>
    <div class="model-imgList">
      <div class="model-list" @click="handleSetimg(item)"
          v-for="(item,index) in modelType" :key="index">
        <img :src="item.img || giveImg" />
        <div class="name" :title="item.name">{{item.name}}</div>
      </div>
    </div>
    <div class="add-model" v-show="addFlage">
      <div class="model-top">
        <div class="model-name">
          <div>
            添加模型
          </div>
          <div style="cursor: pointer;" @click="closeAdd">
            <i class="el-icon-close"></i>
          </div>
        </div>
        <div class="top-test">
          <div class="name">模型：</div>
          <div class="psd-button">
            <input type="file" accept=".glb" id='modelFile' style="display: none;" />
            <div class="psd-export" @click="modelExport">
              <span>上传</span>
            </div>
          </div>
          <div class="export-name" v-show="modelname">
            <span>{{modelname}}</span>
            <i class="el-icon-circle-close" @click="delModel"></i>
          </div>
        </div>
        <div class="top-test">
          <div class="name">模型图片：</div>
          <div class="psd-button">
            <input type="file" id='imgFile' style="display: none;" />
            <div class="psd-export" @click="imgExport">
              <span>上传</span>
            </div>
          </div>
          <div class="export-name" v-show="imgname">
            <span>{{imgname}}</span>
            <i class="el-icon-circle-close" @click="delImg"></i>
          </div>
        </div>
        <div class="model-b">
          <el-button size='small' type="primary" @click="ok">确定</el-button>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { url } from '@/config'
export default {
  data () {
    return {
      addFlage:false,
      loading:false,
      giveImg: require('@/assets/threeModel.png'),
      name:'',
      modelfile:'',
      modelname:'',
      imgFile:'',
      imgname:'',
      imgVisible: false,
      modelType:[
      // {
      //   name:'颠三倒四多',
      //   glb:'/model/gelikaiguan.glb',
      //   img:'/model/gelikaiguan.png'
      // },{
      //   name:'颠三倒四多',
      //   glb:'/model/monkey.glb',
      //   img:'/model/monkey.png'
      // }
      ],
    }
  },
  inject: ['main'],
  methods: {
    delModel() {
      this.modelfile=''
      this.modelname=''
    },
    delImg() {
      this.imgFile=''
      this.imgname=''
    },
    getAdd() {
      this.addFlage = true;
    },
    closeAdd() {
      this.modelfile=''
      this.modelname=''
      this.imgFile=''
      this.imgname=''
      this.addFlage = false
    },
    modelExport() {
      let uploadFile = document.getElementById('modelFile')
      let self = this;
      uploadFile.click();
      function readerFile(e){
        self.modelfile = e.target.files[0];
        console.log('self.modelfile',self.modelfile)
        uploadFile.value = ''
        uploadFile.removeEventListener('change',readerFile,false);
        self.modelname = self.modelfile.name
      }
      uploadFile.addEventListener('change',readerFile,false);
    },
    imgExport() {
      let uploadFile = document.getElementById('imgFile')
      let self = this;
      uploadFile.click();
      function readerFile(e){
        self.imgFile = e.target.files[0];
        if (!/^image\/bmp|jpeg|jpg|png$/.test(self.imgFile.type)){
          return self.$message.error('请上传图片！')
        }
        uploadFile.value = ''
        uploadFile.removeEventListener('change',readerFile,false);
        self.imgname = self.imgFile.name
      }
      uploadFile.addEventListener('change',readerFile,false);
    },
    ok() {
      if(this.modelfile===''){
        return this.$message.error('模型不能为空！')
      }
      if(this.imgFile===''){
        return this.$message.error('模型图片不能为空！')
      }
      this.addFlage = false;
      let formData = new FormData();
      formData.append('glbFile',this.modelfile);
      formData.append('imgFile',this.imgFile);
      this.loading = true;
      axios
      .post(url + '/oss/file/3D/custom/component', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }).then(res=>{
        if(res.data.code==200){
          this.loading = false;
          this.modelfile='';
          this.modelname='';
          this.imgFile='';
          this.imgname='';
          this.$message.success('导入成功!');
          this.getList();
        }else{
          this.loading = false;
          this.modelfile='';
          this.modelname='';
          this.imgFile='';
          this.imgname='';
          this.$message.error('导入失败!');
        }
      }).catch(()=>{
        this.loading = false;
        this.modelfile='';
        this.modelname='';
        this.imgFile='';
        this.imgname='';
        this.$message.error('导入失败!');
      })
    },
    getList() {
      axios
      .get(url + `/oss/file/3D/custom/component`, {
      }).then(res=>{
        if(res.data.code==200){
          this.modelType = res.data.data;
        }
      })
    },
    openImg () {
      this.loading = false;
      this.imgVisible = true;
      this.modelfile='';
      this.modelname='';
      this.imgFile='';
      this.imgname='';
      this.getList();
    },
    handleSetimg (item) {
      this.imgVisible = false;
      console.log('this.dataChart',this.main.activeObj.data)
      this.main.activeObj.data.model = item.glb;
      // this.main.activeObj.data.img = item.img;
    }
  }
}
</script>
<style lang="scss" scoped>
.click-add{
  position: relative;
  left: 10px;
}
/deep/.el-dialog__body{
  padding: 20px 20px;
}
.model-imgList{
  display: flex;
  height: 350px;
  flex-wrap: wrap;
  overflow-y: auto;
  .model-list{
    width: 100px;
    height: 125px;
    margin: 20px 10px;
  }
  img{
    width: 100px;
    height: 100px;
    cursor: pointer;
  }
  .name{
    color: #fff;
    font-size: 12px;
    width: 100%;
    text-align: center;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
.add-model{
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 100%;
  background-color: rgba(144, 146, 152, 0.3);
}
.model-top{
  position: absolute;
  top:50%;
  left: 50%;
  transform: translate(-50%,-50%);
  width: 330px;
  padding:10px;
  background: #232630;
  border: 1px solid #232630;
  .model-name{
    display: flex;
    justify-content: space-between;
    font-size: 16px;
    color: #fff;
    margin-bottom: 10px;
  }
  .top-test{
    display: flex;
    margin-bottom: 4px;
    padding-left: 10px;
    .name{
      width: 75px;
      font-size: 14px;
      padding-top: 8px;
      color: #fff;
    }
    .psd-button{
      .psd-export{
        width:80px;
        height:30px;
        line-height:30px;
        font-size: 14px;
        text-align: center;
        color: #2c88d4;
        border: 1px solid #2c88d4;
        background-color: #1c354d;
        margin-top: 4px;
        cursor: pointer;
      }
    }
    .export-name{
      position: relative;
      margin-left: 5px;
      margin-top: 10px;
      color: #fff;
      font-size: 12px;
      span{
        display: inline-block;
        max-width: 130px;
        // white-space: nowrap;
        overflow: hidden;
        // text-overflow: ellipsis;
      }
      .el-icon-circle-close{
        position: absolute;
        top: -3px;
        color: red;
        font-size: 14px;
        z-index: 1000;
        cursor: pointer;
      }
    }
  }
  .model-b{
    margin-left: 250px;
  }
}
</style>