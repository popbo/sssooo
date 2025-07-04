<template>
  <div class="svg-list-box">
    <el-tabs v-model="activeName" @tab-click="handleClick" stretch>
      <el-tab-pane label="全部" name="first">
        <!-- <el-image v-for="url in urls" :key="url" :src="url" lazy></el-image> -->
        <div class="all-img-box">
          <ul>
            <li
              v-for="(item, index) in svgSystemList"
              :key="index"
              @click="choseSvg(item, index)"
            >
              <div
                class="svg-box"
                :class="currentIndex === index ? 'svg-box-live' : ''"
                v-html="item.link"
              ></div>
              <p :title="item.namePrefix">{{ item.namePrefix }}</p>
            </li>
          </ul>
        </div>
      </el-tab-pane>
      <el-tab-pane label="自定义" name="second">
        <div class="all-img-box">
          <ul>
            <li>
              <div class="svg-box">
                <el-upload
                  class="avatar-uploader"
                  accept=".svg"
                  :http-request="httpRequest"
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                >
                  <i class="el-icon-plus avatar-uploader-icon"></i>
                </el-upload>
              </div>
            </li>
            <li
              v-for="(item, index) in svgCustomList"
              :key="index"
              @click="choseSvg(item, index)"
            >
              <div
                class="svg-box"
                :class="currentIndex === index ? 'customIconSelect' : ''"
              >
                <div v-html="item.link"></div>
                <span
                  class="delete-btn"
                  v-if="currentIndex === index ? true : false"
                  @click="delSvg(item.id)"
                  >x</span
                >
              </div>
              <p :title="item.namePrefix">{{ item.namePrefix }}</p>
            </li>
          </ul>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { getSvgList, deleteSvg } from '@/api/svg.js'
import { url } from '@/config'
export default {
  name: 'svgDialog',
  inject: ['main'],
  data() {
    return {
      activeName: 'first',
      svgSystemList: [],
      svgCustomList: [],
      currentIndex: Number,
      dialogImageUrl: '',
      dialogVisible: false,
      uploadSvgUrl: url + '/visual/put-svg/custom',
      uploadIds: {
        visualId: '',
        configId: '',
      },
    }
  },

  created() {
    this.getSvgSystemList()
    this.getCustomList()
    this.getIds()
  },

  methods: {
    handleClick(tab, event) {
      // console.log(tab, event)
    },
    // 获取系统svg图标列表
    async getSvgSystemList() {
      try {
        const res = await getSvgList('system')
        if (res && res.data && res.data.success) {
          this.svgSystemList = res.data.data
        } else {
          this.$message.error('获取系统svg失败')
        }
      } catch (error) {}
    },
    // 获取用户自定义图标列表
    async getCustomList() {
      try {
        const res = await getSvgList('custom')
        if (res && res.data && res.data.success) {
          this.svgCustomList = res.data.data
          console.log(this.svgCustomList)
        } else {
          this.$message.error('获取自定义svg失败')
        }
      } catch (error) {}
    },
    // 点击svg时
    choseSvg(item, index) {
      this.currentIndex = index
      this.$emit('on-chooseSvg', item)
    },
    handleAvatarSuccess(res, file) {
      // this.imageUrl = URL.createObjectURL(file.raw)
      console.log(res)
      if (res.success) {
        this.getCustomList()
      }
    },
    beforeAvatarUpload(file) {
      const isSvg = file.type === 'image/svg+xml'
      if (!isSvg) {
        this.$message.error('请上传SVG格式的文件')
      }
      return isSvg
    },
    httpRequest(param) {
      const { visualId, configId } = this.uploadIds
      const formData = new FormData()
      formData.append('file', param.file)
      formData.append('fileName', this.input)
      formData.append('visualId', visualId)
      formData.append('configId', configId)
      axios
        .post(url + '/oss/file/custom/svg', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
            Authorization: localStorage.getItem('Authorization'),
          },
        })
        .then(res => {
          if (res.status === 200) {
            this.$message.success('上传文件成功!')
            this.getCustomList()
          }
        })
        .catch(err => {
          this.$message.error('上传失败!')
        })
    },
    getIds() {
      if (
        this.main?.obj?.config &&
        JSON.stringify(this.main.obj.config) !== '{}'
      ) {
        const { visualId, id } = this.main.obj.config
        this.uploadIds = {
          visualId: visualId,
          configId: id,
        }
      }
    },
    async delSvg(id) {
      try {
        const res = await deleteSvg('custom', id)
        if (res.data.code === 200) {
          this.$message.success('删除文件成功')
          this.getCustomList()
        } else {
          this.$message.error('删除文件失败')
        }
      } catch (error) {
        this.$message.error('删除文件失败')
      }
    },
  },
  filters: {
    getFileName(file) {
      const arr = file.split('.')
      return arr[0]
    },
  },
}
</script>

<style lang="scss">
.svg-list-box {
  .el-tabs__item,
  .is-top {
    color: #bcc9d4 !important;
  }
  .all-img-box {
    width: 100%;
    ul {
      width: 100%;
      height: 300px;
      margin-top: 10px;
      overflow-y: scroll;
      background-color: #24262f;
      li {
        float: left;
        display: flex;
        width: 20%;
        height: 100px;
        // background-color: violet;
        justify-content: center;
        flex-flow: column;
        cursor: pointer;
        .svg-box {
          width: 70px;
          height: 70px;
          margin: 0 auto;
          // background-color: turquoise;
          position: relative;
          svg {
            width: 50px;
            height: 50px;
            position: absolute;
            left: 10px;
            top: 10px;
          }
        }
        .svg-box-live {
          background-image: url('~@/assets/checkedSvgBG.png');
          background-repeat: no-repeat;
          background-size: 70px 70px;
        }
        .customIconSelect {
          border: 2px solid #409eff;
          background-image: none !important;
        }
        .delete-btn {
          display: block !important;
          color: #fff;
          background-color: #409eff;
          width: 16px;
          height: 16px;
          border-radius: 50%;
          text-align: center;
          line-height: 14px;
          position: absolute;
          right: -10px;
          top: -6px;
        }
        p {
          text-align: center;
          /* color: #383838; */
          padding: 0 10px;
          margin: 0;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }
  }
}
</style>
<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 70px;
  height: 70px;
  line-height: 70px;
  text-align: center;
}
.avatar {
  width: 70px;
  height: 70px;
  display: block;
}
</style>
