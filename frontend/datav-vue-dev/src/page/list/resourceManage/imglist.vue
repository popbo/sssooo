<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-upload class="upload-demo" :on-success="handleSuccess" :on-error="handleError"
        :before-upload="beforeUpload" 
        :data="uploadData"
        :show-file-list="false" :action="url+'/oss/file/custom/' + imgType" :headers="headers" accept=".jpg,png,.gif,.jpeg,.JPG,.PNG,.GIF,.JPEG,.svg" multiple list-type="picture">
          <el-button size="small" icon="el-icon-upload" type="primary">点击上传
          </el-button>
        </el-upload>
      </el-col>
      <el-col :span="18">
        <el-pagination layout="total, sizes, prev, pager, next, jumper" background size="mini" @size-change="handleSizeChange" @current-change="handleCurrentChange" :page-size="page.size" :current-page.sync="page.page" :total="page.total" style="text-align:right">
        </el-pagination>
      </el-col>
    </el-row>
    <el-scrollbar class="imgList" v-loading="loading" element-loading-text="资源加载中" element-loading-spinner="el-icon-loading" element-loading-background="rgba(0, 0, 0, 0.8)">
      <ul>
        <li v-for="(item,index) in allBgImg" :key="index">
          <div class="operation_btn" :class="item.showOperate ? '':'opacityShow'">
            <i class="icon el-icon-edit" @click="handleEdit(item)"></i>
            <i class="icon el-icon-delete" @click="handleDelete(index, item)"></i>
          </div>
          <img v-show="imgType !== 'svg'" :src="item.value" />
          <div v-show="imgType === 'svg'" class="svg-box" v-html="item.value" v-cloak>
          </div>
          <h4>{{item.label}}</h4>
        </li>
      </ul>
    </el-scrollbar>
    <!-- 编辑弹窗 -->
    <el-dialog :visible.sync="dialogTableVisible" width="35%">
      <el-form label-width="128px" class="demo-ruleForm">
        <el-form-item label="本地背景图名称">
          <el-input v-model="fontLable" />
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
import { imgOption } from '@/option/config'
import { getBgPic, getFile, putFile, delFile } from '@/api/dataCollection'
import { url } from '@/config.js'
import { mapState } from 'vuex'
export default {
  name: 'imglist',
  props: {
    imgType: {
      type: String,
      default: 'thumbnail',
    },
  },
  data() {
    return {
      uploadData:{
        visualId:0,
        configId:0,
      },
      dialogTableVisible: false,
      fontLable: '',
      oldName: '',
      id: '',
      imgObj: '',
      imgActive: 0,
      imgOption: imgOption,
      imgTabs: [],
      allBgImg: [],
      updataImg: [],
      url: '',
      headers: {
        Authorization: '',
      },
      source: 'custom',
      page: {
        page: 1,
        size: 50,
        total: 0,
      },
      loading: false,
    }
  },
  watch: {
    imgType: {
      handler(newVal) {
        this.page.page=1
        this.getBgPicList(this.source, newVal)
      },
    },
  },
  // computed: {
  //   ...mapState({
  //     Authorization: 'Authorization'
  //   })
  // },
  created() {
    // 因为上传文件的地址也要跟随线上地址，所以引入config.js中的地址，但因为该地址会在模板中使用，所以在创建时要赋值
    this.headers.Authorization = localStorage.getItem('Authorization')
    this.url = url
    this.getBgPicList(this.source, this.imgType)
  },
  methods: {
    // 修改打开模态框
    handleEdit(row) {
      this.id = row.id
      this.fontLable = row.label
      this.oldName = row.label
      this.dialogTableVisible = true
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
          delFile(this.source, this.imgType, data).then((res) => {
            if (res.data && res.data.code == 200) {
              this.$message({ type: 'success', message: '删除成功!' })
              this.getBgPicList(this.source, this.imgType)
            }
          })
        })
        .catch(() => {
          this.$message({ type: 'info', message: '已取消删除' })
        })
    },
    onSuccess(res) {
      const url = res.data.link
      this.imgOption[0].unshift({
        label: url,
        value: url,
      })
    },
    /**
     * @getBgetBgPicList
     * @param {string} source system 系统 custom自定义
     * @param {string} type svg(svg图片),font(字体文件),css(css文件),back(背景图片),thumbnail(大屏封面)系统
     */
    getBgPicList(source, type) {
      this.allBgImg = []
      this.updataImg = []
      this.dialogTableVisible = false
      const params = {
        current: this.page.page,
        size: this.page.size,
      }
      this.loading = true
      getFile(source, type, params).then((res) => {
        this.loading = false
        const data = res.data.data
        data.records.forEach((x) => {
          this.updataImg.push({
            label: x.namePrefix,
            value: x.link,
            showOperate: true,
            id: x.id,
          })
        })
        this.page.total = data.total
        this.allBgImg = this.updataImg
        // if (type === 'back') {
        //   this.allBgImg = this.imgOption[0].concat(this.updataImg)
        //   console.log('获取背景图',this.imgOption[0],this.updataImg)
        //   this.page.total = data.total + this.imgOption[0].length
        // } else {
        //   this.allBgImg = this.updataImg
        // }
      })
    },
    //修改提交
    save() {
      const isSameName = this.allBgImg.some((x) => {
        return x.label === this.fontLable
      })
      if (isSameName) {
        this.$message.error('此背景图名称已存在，请更换其他名称')
        return false
      }
      const data = {
        namePrefix: this.fontLable,
        id: this.id,
      }
      putFile(this.source, this.imgType, data)
        .then((res) => {
          if (res.status === 200) {
            this.getBgPicList(this.source, this.imgType)
            this.$message.success('修改成功!')
          }
        })
        .catch((err) => {
          this.$message.error('修改失败!')
        })
      this.dialogTableVisible = false
    },
    handleSuccess() {
      this.$message.success('上传成功')
      this.getBgPicList(this.source, this.imgType)
    },
    //处理上传失败
    handleError(err){
      this.$message.error(JSON.parse(err.message).msg)
    },
    beforeUpload(file){
      if(file.type.indexOf('image')<0){
        this.$message.error('文件格式错误')
        return false
      }
    },
    handleCurrentChange(val) {
      this.page.page = val
      this.getBgPicList(this.source, this.imgType)
    },
    handleSizeChange(val) {
      this.page.size = val
      this.getBgPicList(this.source, this.imgType)
    },
  },
}
</script>

<style lang="scss" scoped>
.el-scrollbar {
  height: calc(100vh - 200px);
}
.imgList ul {
  margin-top: 10px;
}
.imgList li {
  // height: 130px;
  width: 12.74%;
  display: inline-block;
  margin: 0 14px;
  h4 {
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
  img {
    width: 100%;
    height: 110px;
    border: 1px solid #3a475a;
    display: block;
  }
  &:hover {
    .operation_btn {
      opacity: 1;
    }
    img {
      border: 1px solid #409eff;
    }
  }
  .operation_btn {
    float: right;
    background: #409eff;
    height: 26px;
    line-height: 26px;
    padding: 0 10px;
    opacity: 0;
    &.opacityShow {
      display: none;
    }
    .icon {
      font-size: 18px;
      color: #fff;
    }
    .el-icon-edit {
      margin-right: 10px;
    }
  }
  /deep/.svg-box {
    width: 70px;
    height: 70px;
    margin: 0 auto;
    // background-color: turquoise;
    position: relative;
    svg {
      width: 50px !important;
      height: 50px !important;
      position: absolute;
      left: 10px;
      top: 10px;
    }
  }
}
</style>