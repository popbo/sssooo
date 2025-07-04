<template>
  <el-dialog
    title="发布"
    :visible.sync="releaseDialogVisible"
    width="30%"
    :before-close="handleBeforeClose"
  >
    <el-form
      ref="releaseForm"
      :model="releaseForm"
      label-width="90px"
      size="mini"
      :rules="rules"
    >
      <el-form-item label="是否发布" prop="isRelease">
        <el-switch
          v-model="releaseForm.isRelease"
          :active-value="1"
          :inactive-value="0"
          @change="releaseChange"
        ></el-switch>
      </el-form-item>
      <el-form-item
        v-if="releaseForm.isRelease"
        label="发布版本"
        prop="version"
      >
        <el-select v-model="releaseForm.version" placeholder="请选择发布版本">
          <el-option
            v-for="item in versionList"
            :key="item.visualId"
            :label="item.version"
            :value="item.version"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <template v-if="releaseForm.isRelease">
        <el-form-item label="自定义" prop="isCustom">
          <el-switch
            v-model="releaseForm.isCustom"
            :active-value="1"
            :inactive-value="0"
          ></el-switch>
        </el-form-item>
        <el-form-item
          v-if="!releaseForm.isCustom"
          label="发布链接"
          prop="originalUrl"
        >
          <el-input
            v-if="!releaseForm.isCustom"
            type="textarea"
            v-model="releaseForm.originalUrl"
            class="my-texttarea"
            ref="linkTextarea"
            :disabled="true"
          ></el-input>
          <el-link
            :underline="false"
            @click="copyReleaseLink"
            class="my-texttarea-link"
            >复制</el-link
          >
        </el-form-item>
        <el-form-item v-else label="发布链接" prop="path">
          <span class="location-origin">{{ locationOrigin + '/share/' }}</span>
          <el-input v-model.trim="releaseForm.path" class="my-input"></el-input>
          <el-link
            :underline="false"
            @click="copyReleaseLink"
            class="my-input-link"
            >复制</el-link
          >
        </el-form-item>
        <el-form-item label="分享加密" prop="isEnc">
          <el-switch
            v-model="releaseForm.isEnc"
            :active-value="1"
            :inactive-value="0"
            @change="clearPsw"
          ></el-switch>
        </el-form-item>
        <el-form-item label="密码" v-if="releaseForm.isEnc" prop="psw">
          <el-input
            v-model.trim="releaseForm.psw"
            :type="flag ? 'text' : 'password'"
          >
            <i
              slot="suffix"
              class="el-input__icon"
              :class="flag ? 'el-icon-minus' : 'el-icon-view'"
              style="cursor: pointer"
              @click="flag = !flag"
            ></i>
            <!-- <i slot="suffix" class="el-input__icon el-icon-date"></i> -->
          </el-input>
        </el-form-item>
      </template>
      <template v-else>
        <div class="unrelease-box">
          <img src="@/assets/un_release.png" alt="" />
          <p>发布后，他人可通过共享链接访问项目</p>
        </div>
      </template>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <!-- <el-button plain class="myBtn releaseBtn" @click="release">发布大屏</el-button> -->
      <el-button size="mini" @click="handleClose">取 消</el-button>
      <el-button size="mini" type="primary" @click="release">发 布</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateComponent, getCurrentVersion } from '@/api/visual'
import { url } from '@/config'
export default {
  name: 'releaseDialog',
  inject: ['contain'],
  props: {
    releaseDialogVisible: {
      type: Boolean,
      default: false,
    },
    releaseInfo: {
      type: Object,
      default: function () {
        return {}
      },
    },
    isOutRelease: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    releaseInfo: {
      handler(newVal, oldVal) {
        this.$nextTick(() => {
          this.releaseForm = this.deepClone(newVal)
          this.releaseForm.originalUrl =
            this.locationOrigin + newVal.originalUrl
        })
        this.currentId = this.releaseInfo.id
        this.getVersion(this.currentId)
      },
      deep: true,
    },
  },
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else if (value.length < 6 || value.length > 16) {
        callback(new Error('密码长度为6~16位'))
      } else {
        let reg = /^[^\u4e00-\u9fa5 ]{6,16}$/
        let result = reg.test(value)
        if (result) {
          callback()
        } else {
          callback(new Error('密码中不能包含中文和空格'))
        }
      }
    }
    return {
      releaseForm: {
        isRelease: true,
        version: '',
        releaseLink: '',
        isEncrypt: false,
        password: '',
      },
      versionList: [],
      currentId: '',
      flag: false,
      rules: {
        psw: [{ validator: validatePass, trigger: 'blur' }],
        // version: [{ required: true, message: '请选择发布版本', trigger: 'change' }],
        path: [
          { required: true, message: '请输入自定义链接', trigger: 'blur' },
        ],
      },
      locationOrigin: window.location.origin,
    }
  },
  methods: {
    handleBeforeClose(done) {
      this.$emit('update:releaseDialogVisible', false)
      // this.$emit('update:releaseLink', '')
      this.$refs.releaseForm.resetFields()
      done()
    },
    clearPsw() {
      //当取消分享加密时密码置为空
      if (!this.releaseForm.isEnc) {
        this.releaseForm.psw = ''
      }
    },
    handleClose() {
      this.$emit('update:releaseDialogVisible', false)
      // this.$emit('update:releaseLink', '')
      this.$refs.releaseForm.resetFields()
    },
    copyReleaseLink() {
      const input = document.createElement('input')
      if (this.releaseForm.isCustom) {
        input.value = this.locationOrigin + '/share/' + this.releaseForm.path
      } else {
        input.value = this.releaseForm.originalUrl
      }
      document.body.appendChild(input)
      input.select()
      document.execCommand('copy')
      document.body.removeChild(input)
      this.$message.success('复制成功')
    },
    save(fn) {
      let isFun = typeof fn === 'function'
      if (!isFun && this.$route.params.id <= 100 && this.$website.isDemo) {
        this.$message.error(this.$website.isDemoTip)
        return
      }
      this.contain.handleInitActive()
      const loading = this.$loading({
        lock: true,
        text: '正在保存配置，请稍后',
        spinner: 'loading',
        background: 'rgba(0, 0, 0, 0.7)',
      })
      this.$nextTick(() => {
        html2canvas(document.getElementById('content'), {
          useCORS: true,
          backgroundColor: null,
          allowTaint: true,
        })
          .then(canvas => {
            function dataURLtoFile(dataurl, filename) {
              var arr = dataurl.split(','),
                mime = arr[0].match(/:(.*?);/)[1],
                bstr = atob(arr[1]),
                n = bstr.length,
                u8arr = new Uint8Array(n)
              while (n--) {
                u8arr[n] = bstr.charCodeAt(n)
              }
              return new File([u8arr], filename, { type: mime })
            }
            var file = dataURLtoFile(
              canvas.toDataURL('image/jpeg', 0.1),
              new Date().getTime() + '.jpg'
            )
            var formdata = new FormData()
            formdata.append('file', file)
            axios
              .post(url + '/oss/file/custom/thumbnail', formdata, {
                headers: {
                  'Content-Type': 'multipart/form-data',
                },
              })
              .then(res => {
                const { data } = res.data
                const url = data.link
                const formdata = {
                  visual: {
                    id: this.contain.visual.id,
                    backgroundUrl: url,
                    password: this.releaseForm.password,
                  },
                  config: {
                    id: this.contain.obj.config.id,
                    visualId: this.contain.visual.id,
                    detail: JSON.stringify(this.contain.config),
                    component: JSON.stringify(this.contain.nav),
                    backgroundId: data.id,
                    version: this.contain.versionInfo.version,
                    versionRemark: this.contain.versionInfo.versionRemark,
                  },
                }
                return updateComponent(formdata)
              })
              .then(() => {
                loading.close()
                if (isFun) {
                  fn()
                } else {
                  this.$message.success('大屏配置保存成功')
                }
              })
              .catch(err => {
                console.log(err)
                this.$message.error('大屏配置保存失败，请检查服务端配置')
                loading.close()
              })
          })
          .catch(error => {
            console.log(error)
            loading.close()
            return this.$message.error('大屏保存失败')
          })
      })
    },
    release() {
      const form = this.releaseForm
      if (form.isCustom && !form.path.trim()) {
        return this.$message.warning('请输入自定义路径', 2000)
      } else if (form.isEnc && !form.psw.trim()) {
        return this.$message.warning('请输入分享密码', 2000)
      } else if (!form.version) {
        return this.$message.error('发布版本不能为空', 2000)
      } else {
        this.isOutRelease
          ? this.handleRelease()
          : this.$emit('saveData', this.handleRelease, this)
      }
    },
    handleRelease() {
      this.$refs.releaseForm.validate(async valid => {
        if (valid) {
          const infoData = {
            isCustom: '',
            isEnc: '',
            isRelease: '',
            psw: '',
            version: '',
          }
          for (let key in infoData) {
            infoData[key] = this.releaseForm[key]
          }
          if (infoData.isCustom) {
            infoData.path = this.releaseForm.path
          } else {
            infoData.path = this.releaseForm.originalUrl.split('share/')[1]
          }
          console.log('infoData==>', infoData)
          if (!infoData.version) {
            this.$message.error('请选择发布版本', 2000)
          } else {
            this.$emit('releaseCbFn', infoData)
            // this.save(async () => {
            //   try {
            //     const res = await saveReleaseInfo(infoData)
            //     if (res.data.success) {
            //       this.$refs.releaseForm.resetFields()
            //       // 关闭表单
            //       this.$emit('update:releaseDialogVisible', false)
            //       const openlink = this.locationOrigin + '/share/' + infoData.path
            //       window.open(openlink, '_blank')
            //     } else {
            //       this.$message.error(res.data.msg)
            //     }
            //   } catch (error) {}
            //   // 重置表单
            // })
            // console.log('Tijiao')
            // 调用兄弟组件header的分享方法
          }
        } else {
          return false
        }
      })
    },
    async releaseChange(val) {
      if (val === 0) {
        this.$emit('cancelReleaseCbFn')
      }
    },
    //获取当前大屏的所有版本
    getVersion(id) {
      // console.log('我是id', this.releaseInfo)
      getCurrentVersion(id).then(res => {
        this.versionList = res.data.data
        this.versionList = this.versionList.map(item => {
          return {
            version: item.version,
            visualId: item.visualId,
          }
        })
        if (this.releaseInfo.version) {
          this.releaseForm.version = this.releaseInfo.version
        } else if (this.versionList.length > 0) {
          this.releaseForm.version = this.versionList[0].version
        } else {
          this.releaseForm.version = ''
        }
      })
    },
  },
}
</script>

<style scoped lang="scss">
.is-release-box {
  padding-left: 20px;
}
.releaseBtn {
  width: 200px;
}
/deep/.el-dialog__body {
  padding: 30px 20px 0px;
}
.my-texttarea {
  width: 100%;
  // margin-top: 15vh;
  width: 80%;
  margin-right: 10px;
}
.my-input {
  width: 130px;
  margin-right: 10px;
  margin-left: 10px;
}
.location-origin {
  color: #fff;
}
.my-texttarea-link {
  height: 48px;
  line-height: 48px;
}
.el-loading-spinner .loading {
  display: inline-block;
  width: 100px;
  height: 50px;
  background: url('~@/assets/loading.gif') no-repeat;
}
.el-loading-spinner .el-loading-text {
  font-size: 16px;
  color: #fff;
  font-family: 'SourceHanSansCN-Regular';
}
.unrelease-box {
  padding-bottom: 20px;
  img {
    display: block;
    margin: 0 auto;
  }
  P {
    margin: 10px auto;
    text-align: center;
    color: #fff;
  }
}
</style>
