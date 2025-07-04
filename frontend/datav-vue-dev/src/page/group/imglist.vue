<template>
  <el-dialog
    title="图库"
    width="80%"
    :close-on-click-modal="false"
    :visible.sync="imgVisible"
  >
    <div style="margin: 0 auto">
      <el-upload
        class="upload-demo"
        :on-success="onSuccess"
        :show-file-list="false"
        :action="url + '/oss/file/custom/component'"
        :headers="headers"
        :data="uploadIds"
        multiple
        list-type="picture"
      >
        <el-button size="small" icon="el-icon-upload" type="primary"
          >点击上传</el-button
        >
      </el-upload>
    </div>
    <el-scrollbar class="imgList">
      <div class="img-type"   v-for="(item, index) in imgList" :key="index">
        <img
          :src="item.value"
          :style="styleName"
          @click="handleSetimg(item.value)"
        />
        <div class="dele-list" v-show="item.id" @click.stop="delteImg(item)">
          <i class="el-icon-close"></i>
        </div>
      </div>
    </el-scrollbar>
  </el-dialog>
</template>

<script>
import { imgOption } from '@/option/config'
import { url } from '@/config'
import {
  getCurrentImg,
  getDelCurrentImg,
} from '@/api/visual'
export default {
  inject: ['main'],
  data() {
    return {
      imgList:[],
      imgVisible: false,
      imgObj: '',
      type: '',
      imgActive: 0,
      imgOption: imgOption,
      imgTabs: [],
      url: '',
      headers: {
        Authorization: '',
      },
      uploadIds: {
        visualId: '',
        configId: '',
      },
    }
  },
  computed: {
    styleName() {
      if (this.type === 'background') {
        return {
          width: '200px',
        }
      }
      return {}
    },
  },
  watch: {
    type: {
      handler() {
        if (this.type === 'background') {
          this.imgActive = 0
        } else if (this.type == 'border') {
          this.imgActive = 1
        } else {
          this.imgActive = 2
        }
      },
      immediate: true,
    },
    imgVisible(newVal) {
      if (newVal) {
        this.addUploadIds()
        this.getCurrentImg()
      }
    },
  },
  created() {
    this.headers.Authorization = localStorage.getItem('Authorization')
    this.url = url
  },
  methods: {
    delteImg(item){
      getDelCurrentImg({id:item.id}).then(res=>{
        console.log(res)
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
    },
    getCurrentImg(){
      getCurrentImg(this.uploadIds).then(res=>{
        if(res.data.data){
          let imgList = [];
          res.data.data.forEach(item=>{
            imgList.push({
              label: item.link || '',
              value: item.link || '',
              id: item.id
            })
          })
          this.imgList = [...imgList,...this.imgOption[this.imgActive]]
        }
        // console.log(this.imgList,666)
      })
    },
    onSuccess(res) {
      if(res.data.link){
        this.$message.success('上传成功')
        this.getCurrentImg()
      }
      // const url = res.data.link
      // this.imgOption[this.imgActive].unshift({
      //   label: url,
      //   value: url,
      // })
    },
    openImg(item, type) {
      this.type = type
      this.imgObj = item
      this.imgVisible = true
    },
    handleSetimg(item) {
      this.imgVisible = false
      console.log(item, this.imgObj, this.type)
      // this.$emit('change', item, this.imgObj);
      if (this.type === 'warnDraw') {
        this.imgObj.imgUrl = item
      } else {
        this.$emit('change', item, this.imgObj, this.type)
      }
    },
    addUploadIds() {
      if (this.main?.obj?.config) {
        const { visualId, id } = this.main.obj.config
        this.uploadIds = {
          visualId: visualId,
          configId: id,
        }
      }
    },
  },
}
</script>
<style scoped>
.dele-list{
  position: absolute;
  top: 4px;
  right: 0;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #3a89fe;
  text-align: center;
  line-height: 20px;
  color: #fff;
  cursor: pointer;
  display: none;
}
.img-type{
  position: relative;
  display: inline-block;
}
.img-type:hover .dele-list{
  display: block;
}
</style>
