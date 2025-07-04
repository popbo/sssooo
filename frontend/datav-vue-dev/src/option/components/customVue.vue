<!-- 选项卡配置 -->
<template>
  <div>
    <el-form-item label="vue代码">
      <el-button type="primary" @click="openCustom">编辑代码</el-button>
      <input type="file" accept=".vue" id="uploadVue" style="display: none;"/>
      <el-button style="margin-left: 15px;" type="primary" @click="exportVue">上传代码</el-button>
    </el-form-item>
    <el-form-item label="引用文件">
      <el-button type="primary" @click="openImgfile">文件</el-button>
    </el-form-item>
    <el-form-item label="引用图片">
      <el-button type="primary" @click="openImgFlage">图片</el-button>
    </el-form-item>
    <el-form-item label="操作说明">
      <div class="illustrate-bt" @click="illustrate=true" title="点击打开">?</div>
      <!-- <el-button type="primary" @click="illustrate=true">说明</el-button> -->
    </el-form-item>
    <div class="custom-file" v-if="fileFlage">
        <div class="custom-text">
          <div class="heard">
            <div class="log-name">文件</div>
            <div class="log-close" @click="fileFlage=false">
              <i class="el-icon-close"></i>
            </div>
          </div>
          <div class="export-file">
            <input type="file" id="uploadFile" style="display: none;"/>
            <el-button size="mini" type="primary" @click="exportText"><i class="el-icon-upload"></i>文件上传</el-button>
          </div>
          <div class="file-list">
            <div class="file-d" v-for="(item,index) in flieList" :key="index">
              <div class="delet" title="删除" @click="deletFlie(item)">
                <i class="el-icon-close"></i>
              </div>
              <div class="name">
              <span>名称：</span>
              <span class="s-name">{{item.namePrefix}}</span>
            </div>
              <div class="adress">
                <span>地址：</span>
                <el-input :id="'copyFile'+index" style="width: 245px;" size="mini" v-model="item.link" readonly></el-input>
                <i @click="copyFile(index)" title="复制" class="el-icon-document-copy copy-f"></i>
              </div>
            </div>
          </div>
        </div>
    </div>
    <div class="img-file" v-if="imgFlage">
        <div class="custom-text">
          <div class="heard">
            <div class="log-name">图片</div>
            <div class="log-close" @click="imgFlage=false">
              <i class="el-icon-close"></i>
            </div>
          </div>
          <div class="export-file">
            <input type="file" accept=".jpg,.jpeg,.png,.GIF,.JPG,.PNG" id="uploadImg" style="display: none;"/>
            <el-button size="mini" type="primary" @click="exportImg"><i class="el-icon-upload"></i>图片上传</el-button>
          </div>
          <div class="file-list">
            <div class="file-c" v-for="(item,index) in imgList" :key="index">
              <div class="delet" title="删除"  @click="delteImg(item)">
                <i class="el-icon-close"></i>
              </div>
              <div class="img">
                <img :src="item.link" alt="">
              </div>
              <div class="adress">
                <el-input :id="'copyImg'+index" style="width: 280px;" size="mini" v-model="item.link" readonly></el-input>
                <i @click="copyImg(index)" title="复制" class="el-icon-document-copy copy-f"></i>
              </div>
            </div>
          </div>
        </div>
    </div>
    <div class="custom-code" v-if="customFlage">
      <div class="custom-text">
        <div class="heard">
          <div class="log-name">vue代码</div>
          <div class="log-close" @click="closeLog">
            <i class="el-icon-close"></i>
          </div>
        </div>
        <div class="paramJSON_edit_box">
          <monaco-editor
            v-model="code"
            :height="610"
            ref="editor"
          ></monaco-editor>
        </div>
        <div class="foot">
          <el-button size="small" type="primary" @click="handleOk">确定</el-button>
        </div>
      </div>
    </div>
    <div class="illustrate" v-if="illustrate">
     <div class="illustrate-text">
       <div class="heard">
          <div class="log-name">Vue自定义组件说明</div>
          <div class="log-close" @click="closeIll">
            <i class="el-icon-close"></i>
          </div>
        </div>
        <div class="paramJSON_edit_box">
          <monaco-editor
            v-model="illustrateTest"
            :height="450"
            :disabled="true"
            ref="editor"
          ></monaco-editor>
        </div>
     </div>
    </div>
  </div>
</template>

<script>
import MonacoEditor from '@/page/components/editor'
import request from '@/axios'
import { url } from '@/config'
import {
  getCustomImg,
  getDelCurrentImg,
  getCustomFile,
} from '@/api/visual'
export default {
  data() {
    return {
      flieList:[],
      imgList:[],
      customFlage:false,
      code:'',
      illustrate:false,
      fileFlage:false,
      imgFlage:false,
      illustrateTest:''
    }
  },
  components:{
    MonacoEditor
  },
  created(){
   this.illustrateTest=`
  //props接收customVueData.dataChart数据交互返回的数据值、customVueData.id序号、customVueData.$attrs里有图层名称(name)、图层参数值(comParams)
  props:['customVueData'] 
  created(){
    //this.$emit-触发点击事件和其他可视化组件交互,需要回调click事件
    let parms = [{
      id:'c881c53a-1157-4573-b955-08ecc1b93247',//id-触发对应组件的序号
      eventAction:2,eventAction-触发组件的事件行为, 1数据刷新、2组件隐藏、3组件显示
      p:{  //p-传给数据刷新时接口接收参数
      }
    }]
    // parms数组形式可以同时控制多个组件
    this.$emit('customVueClick',parms)
  },
  //this.$loadJsCss远程加载三方包
  (内网时注意资源地址是否能加载)
  @param {*} type 资源类型js/css
  @param {*} url 三方包的地址
  this.$loadJsCss(type,url).then(res=>{
      console.log(res)
  })
  可以使用this.$bus进行Vue$Bus事件抛出接收来自定义vue组件里彼此联动
  this.$bus.$emit('事件名',{})
  this.$bus.$on('事件名',()=>{
  })
  window.$busNew.$emit('事件名',{})
  window.$busNew.$on('事件名',()=>{
  })
  可以使用element组件
  可以用this.$axios来使用axios调接口
  可以用this.$echarts使用echarts
  注意当在mounted获取不到dom时请使用this.$nextTick
  注意不支持import引用加载。
   `
  },
  inject: ['main'],
  methods: {
    deletFlie(item){
      this.$confirm('是否确认永久删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        getDelCurrentImg({id:item.id}).then(res=>{
          if(res.data){
            if(res.data.code==200){
              this.getCustomFile()
              this.$message.success('删除成功')
            }else{
              this.getCustomFile()
              this.$message.success('删除失败')
            }
          }
        })
      }).catch(() => {})
    },
    copyImg(index){
       //获取input对象
      let input = document.getElementById('copyImg'+index)
      //选中input标签
      input.select()
      //执行复制
      document.execCommand('copy')
      this.$message.success('复制成功!')
    },
    copyFile(index) {
      //获取input对象
      let input = document.getElementById('copyFile'+index)
      //选中input标签
      input.select()
      //执行复制
      document.execCommand('copy')
      this.$message.success('复制成功!')
    },
    exportVue() {
      let uploadFile = document.getElementById('uploadVue')
      let self = this;
      uploadFile.click();
      function readerFile(e){
          let file = e.target.files[0];
          uploadFile.value = ''
          uploadFile.removeEventListener('change',readerFile,false);
          let name = file.name.split('.')
          if(name[1]!=='vue'){
            return self.$message.error('请上传vue文件!');
          }
          // if(file.size>524288000){
          //   return self.$message.error('上传psd文件过大!');
          // }
          let formDate = new FormData();
          formDate.set('file',file)
          request({
            url: `${url}/oss/file/vue`,
            method: 'post',
            headers: {
              'Content-Type': 'multipart/form-data'
            },
            data:formDate
          }).then(res=>{
            if(res.data){
              if(res.data.data){
                self.main.activeOption.customVueData = res.data.data;
                self.$message.success('上传成功');
              }
            }else{
              self.$message.error('上传失败');
            }
          }).catch(()=>{
            self.$message.error('上传失败');
          })
        }
        uploadFile.addEventListener('change',readerFile,false);
    },
    openImgfile(){
      this.fileFlage = true;
      this.getCustomFile()
    },
    exportText() {
      let uploadFile = document.getElementById('uploadFile')
      let self = this;
      uploadFile.click();
      function readerFile(e){
          let file = e.target.files[0];
          uploadFile.value = ''
          uploadFile.removeEventListener('change',readerFile,false);
          if (/\.(jpg|jpeg|png|GIF|JPG|PNG)$/.test(file.name)) { 
            return  self.$message.error('不能上传图片');  
          }
          // if(file.size>524288000){
          //   return self.$message.error('上传psd文件过大!');
          // }
          const { visualId, id } = self.main.obj.config
          let formDate = new FormData();
          formDate.set('file',file)
          formDate.set('visualId',visualId)
          formDate.set('configId',id)
          formDate.set('componentId',self.main.activeObj.index)
          request({
            url: `${url}/oss/file/custom/component`,
            method: 'post',
            headers: {
              'Content-Type': 'multipart/form-data'
            },
            data:formDate
          }).then(res=>{
            if(res.data){
              if(res.data.code==200){
                self.$message.success('上传成功');
                self.openImgfile()
              }
            }else{
              self.$message.error('上传失败');
            }
          }).catch(()=>{
            self.$message.error('上传失败');
          })
        }
        uploadFile.addEventListener('change',readerFile,false);
    },
    openImgFlage(){
      this.imgFlage = true;
      console.log(this.main,6666)
      this.getCurrentImg();
    },
    exportImg(){
      let uploadFile = document.getElementById('uploadImg')
      let self = this;
      uploadFile.click();
      function readerFile(e){
          let file = e.target.files[0];
          uploadFile.value = ''
          uploadFile.removeEventListener('change',readerFile,false);
          if (!/\.(jpg|jpeg|png|GIF|JPG|PNG)$/.test(file.name) ) { 
            return  self.$message.error('请上传图片');  
          }
          // if(file.size>524288000){
          //   return self.$message.error('上传psd文件过大!');
          // }
          const { visualId, id } = self.main.obj.config
          let formDate = new FormData();
          formDate.set('file',file)
          formDate.set('visualId',visualId)
          formDate.set('configId',id)
          formDate.set('componentId',self.main.activeObj.index)
          request({
            url: `${url}/oss/file/custom/component`,
            method: 'post',
            headers: {
              'Content-Type': 'multipart/form-data'
            },
            data:formDate
          }).then(res=>{
            if(res.data){
              if(res.data.code==200){
                self.$message.success('上传成功');
                self.getCurrentImg()
              }
            }else{
              self.$message.error('上传失败');
            }
          }).catch(()=>{
            self.$message.error('上传失败');
          })
        }
        uploadFile.addEventListener('change',readerFile,false);
    },
    delteImg(item){
      this.$confirm('是否确认永久删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        getDelCurrentImg({id:item.id}).then(res=>{
          if(res.data){
            if(res.data.code==200){
              this.getCurrentImg()
              this.$message.success('删除成功')
            }else{
              this.getCurrentImg()
              this.$message.success('删除失败')
            }
          }
        })
      }).catch(() => {})
    },
    getCustomFile(){
      let parms = {
        componentId:this.main.activeObj.index,
      }
      getCustomFile(parms).then(res=>{
        if(res.data.data){
          let flieList = res.data.data;
          this.flieList = flieList
        }
        // console.log(this.imgList,666)
      })
    },
    getCurrentImg(){
      let parms = {
        componentId:this.main.activeObj.index,
      }
      getCustomImg(parms).then(res=>{
        if(res.data.data){
          let imgList = res.data.data;
          this.imgList = imgList
        }
        // console.log(this.imgList,666)
      })
    },
    openCustom(){
      this.code = this.main.activeOption.customVueData;
      this.customFlage = true;
    },
    closeIll(){
      this.illustrate = false;
    },
    closeLog(){
      this.customFlage = false;
    },
    handleOk(){
      this.main.activeOption.customVueData = this.code;
      this.customFlage = false;
    },
  },
  computed: {},
}
</script>
<style lang="scss" scoped>
.illustrate-bt{
  width: 18px;
  height: 18px;
  background: #bcc9d4;
  color: #232630;
  font-size: 12px;
  text-align: center;
  line-height: 18px;
  border-radius: 50%;
  margin-top: 6px;
  cursor: pointer;
}
.illustrate{
  position: fixed;
  top: 0px;
  left: 0px;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  z-index: 1000;
    .illustrate-text{
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%,-50%);
        width: 800px;
        height: 550px;
        background: #232630;
        .heard{
          height: 45px;
          border-bottom: 1px solid #363841;
          display: flex;
          justify-content: space-between;
          .log-name{
            padding-left: 20px;
            font-size: 16px;
            color: #fff;
            line-height: 45px;
          }
          .log-close{
            font-size: 16px;
            line-height: 45px;
            color: #fff;
            padding-right: 20px;
            cursor: pointer;
          }
        }
        .paramJSON_edit_box{
          width: 100%;
          padding: 0 10px 20px;
          margin-top: 20px;
        }
    }
}
.img-file,.custom-file{
  position: fixed;
  top: 0px;
  left: 0px;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  z-index: 1000;
  .custom-text{
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%,-50%);
    width: 1100px;
    height: 600px;
    background: #232630;
    .heard{
      height: 45px;
      border-bottom: 1px solid #363841;
      display: flex;
      justify-content: space-between;
      .log-name{
        padding-left: 20px;
        font-size: 16px;
        color: #fff;
        line-height: 45px;
      }
      .log-close{
        font-size: 16px;
        line-height: 45px;
        color: #fff;
        padding-right: 20px;
        cursor: pointer;
      }
    }
    .export-file{
      margin-top: 20px;
      margin-left: 30px;
    }
    .file-list{
      display: flex;
      margin-top: 15px;
      flex-wrap: wrap;
      padding: 10px 30px;
      box-sizing: border-box;
      height: 460px;
      overflow-y: auto;
      .file-c{
        position: relative;
        width: 330px;
        height: 110px;
        text-align: center;
        border: 1px solid #363841;
        padding: 10px;
        border-radius: 8px;
        margin-right: 15px;
        margin-bottom: 15px;
        img{
          width: 50px;
          height: 50px;
        }
        .name{
          color: #fff;
          font-size: 14px;
        }
        .adress{
          display: flex;
          .copy-f{
            margin-top: 7px;
            margin-left: 5px;
            cursor: pointer;
          }
        }
        .delet{
          display: none;
          position: absolute;
          top: -8px;
          right: -10px;
          width: 20px;
          height: 20px;
          font-size: 14px;
          text-align: center;
          border-radius: 50%;
          line-height: 20px;
          border: 1px solid #3a89fe;
        }
        &:hover{
          border: 1px solid #3a89fe;
          .delet{
            display: block;
            cursor: pointer;
          }
        }
      }
      .file-d{
        position: relative;
        width: 330px;
        height: 110px;
        border: 1px solid #363841;
        padding: 10px;
        border-radius: 8px;
        margin-right: 15px;
        margin-bottom: 15px;
        .name{
          color: #fff;
          font-size: 14px;
          margin-top: 15px;
          .s-name{
            display: inline-block;
            width: 245px;
          }
        }
        .adress{
          display: flex;
          color: #fff;
          font-size: 14px;
          margin-top: 10px;
          .copy-f{
            margin-top: 7px;
            margin-left: 5px;
            cursor: pointer;
          }
        }
        .delet{
          display: none;
          position: absolute;
          top: -8px;
          right: -10px;
          width: 20px;
          height: 20px;
          font-size: 14px;
          text-align: center;
          border-radius: 50%;
          line-height: 20px;
          border: 1px solid #3a89fe;
        }
        &:hover{
          border: 1px solid #3a89fe;
          .delet{
            display: block;
            cursor: pointer;
          }
        }
      }
    }
  }
}
.custom-code{
  position: fixed;
  top: 0px;
  left: 0px;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  z-index: 1000;
  .custom-text{
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%,-50%);
    width: 1000px;
    height: 750px;
    background: #232630;
    .heard{
      height: 45px;
      border-bottom: 1px solid #363841;
      display: flex;
      justify-content: space-between;
      .log-name{
        padding-left: 20px;
        font-size: 16px;
        color: #fff;
        line-height: 45px;
      }
      .log-close{
        font-size: 16px;
        line-height: 45px;
        color: #fff;
        padding-right: 20px;
        cursor: pointer;
      }
    }
    .paramJSON_edit_box{
      width: 100%;
      padding: 0 10px 20px;
      margin-top: 20px;
    }
    .foot{
      padding-right: 15px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
