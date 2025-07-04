<template>
  <div class="head">
    <div class="head_actions head_left">
      <div class="new_head_info">
        <i class="el-icon-data-analysis" style="margin-right: 2px"></i>
        <span>可视化设计-{{ contain.visual.title }}</span>
      </div>
      <div
        class="head_btn"
        :class="{ 'head_btn--active': contain.menuShow }"
        @click="handleFlag('menuShow')"
      >
        <el-tooltip effect="dark" content="图层" placement="top">
          <i class="iconfont icon-tuceng"></i>
        </el-tooltip>
        <span>图层</span>
      </div>
      <!-- 添加一个组件的展示与否的按钮 开始 -->
      <!-- <div class="head_btn" :class="{'head_btn--active':contain.componentShow}" @click="handleFlag('componentShow')">
        <el-tooltip effect="dark" content="组件" placement="top">6
          <i class="iconfont icon-zujian"></i>
        </el-tooltip>
        <span>组件</span>
      </div> -->
      <!-- 添加一个组件的展示与否的按钮 结束 -->
      <div
        class="head_btn"
        :class="{ 'head_btn--active': contain.paramsShow }"
        @click="handleFlag('paramsShow')"
      >
        <el-tooltip effect="dark" content="操作" placement="top">
          <i class="iconfont icon-caozuojilu"></i>
        </el-tooltip>
        <span>操作</span>
      </div>
    </div>
    <div class="head_actions head_tool" v-show="!showTips">
      <div
        class="head_btn"
        :disabled="!contain.canUndo"
        @click="contain.editorUndo"
      >
        <el-tooltip effect="dark" content="撤销" placement="top">
          <i class="iconfont icon-chexiao"></i>
        </el-tooltip>
        <span>撤销</span>
      </div>
      <div
        class="head_btn"
        :disabled="!contain.canRedo"
        @click="contain.editorRedo"
      >
        <el-tooltip effect="dark" content="重做" placement="top">
          <i class="iconfont icon-zhongzuo"></i>
        </el-tooltip>
        <span>重做</span>
      </div>
      <!-- <div class="head_btn" @click="handleToolClick('Top')">
        <el-tooltip effect="dark" content="置顶" placement="top">
          <el-button type="text" icon="iconfont icon-tucengzhiding" class="tool_btn" :disabled="!contain.activeIndex"></el-button>
        </el-tooltip>
      </div>
      <div class="head_btn" @click="handleToolClick('Bottom')">
        <el-tooltip effect="dark" content="置底" placement="top">
          <el-button type="text" icon="iconfont icon-tucengzhidi" class="tool_btn" :disabled="!contain.activeIndex"></el-button>
        </el-tooltip>
      </div> -->
      <!-- <div class="head_btn" @click="handleToolClick('StepTop')">
        <el-tooltip effect="dark" content="上移" placement="top">
           <el-button type="text" icon="iconfont icon-tucengshangyi" class="tool_btn" :disabled="!contain.activeIndex"></el-button>
          
        </el-tooltip>
      </div>
      <div class="head_btn" @click="handleToolClick('StepBottom')">
        <el-tooltip effect="dark" content="下移" placement="top">
          <el-button type="text" icon="iconfont icon-tucengxiayi" class="tool_btn" :disabled="!contain.activeIndex"></el-button>
          
        </el-tooltip>
      </div> -->
      <div
        class="head_btn"
        :disabled="isLockUse"
        v-if="!contain.activeObj.lock"
        @click="handleToolClick('lock')"
        :class="contain.activeObj.lock ? 'lock_btn_use' : ''"
      >
        <el-tooltip effect="dark" content="锁定" placement="top">
          <i class="iconfont icon-suoding"></i>
        </el-tooltip>
        <span>锁定</span>
      </div>
      <div
        class="head_btn"
        v-else
        :disabled="isJiesuoUse"
        @click="handleToolClick('lock')"
        :class="contain.activeObj.lock ? '' : 'lock_btn_use'"
      >
        <el-tooltip effect="dark" content="解锁" placement="top">
          <i class="iconfont icon-jiesuo"></i>
        </el-tooltip>
        <span>解锁</span>
      </div>
      <!-- <div>
        <span>对齐线</span>
        <el-switch v-model="alignLineEnable" @change='changeStatus'>
        </el-switch>
      </div> -->
    </div>
    <div class="head_actions head_tool" v-show="showTips">
      <div class="head_btn">
        <el-tooltip effect="dark" content="返回" placement="top" class="pad0">
          <el-button
            @click="closeTips"
            type="text"
            icon="el-icon-back"
            :disabled="!showTips"
            class="tool_btn"
          ></el-button>
        </el-tooltip>
        <!-- <el-button type="text"  @click='closeTips' icon="iconfont el-icon-back" :disabled="!showTips" class="tool_btn"></el-button> -->
      </div>
    </div>
    <!-- <div class="head_info">
      <i class="el-icon-data-analysis"></i>
      <span>可视化设计-{{contain.config.name}}</span>
    </div> -->

    <div class="head_actions" style="width: auto">
      <div class="head_select">
        <el-select
          v-model="versionValue"
          placeholder="版本记录"
          :popper-class="'head_select_el-select-dropdown'"
          @change="applyVersionSure(versionValue)"
        >
          <el-option
            v-for="item in versionList"
            :key="item.visualId"
            :label="item.version"
            :value="item.version"
          >
            <el-popover
              placement="right-start"
              width="190"
              popper-class="versionListDetail"
              trigger="hover"
              :offset="-12"
              :visible-arrow="false"
            >
              <p>详细信息</p>
              <i class="el-icon-edit v-edit" @click.stop="getOpenEditVersionVisible(item)" v-show="item.version!=='v1'"></i>
              <el-divider></el-divider>
              <div class="content_box">
                <div>版本号：{{ item.version }}</div>
                <div>修改时间：{{ item.createTime }}</div>
                <div>更新内容：{{ item.versionRemark }}</div>
              </div>
              <div slot="reference">
                {{ item.version }}
              </div>
              <i class="el-icon-delete v-delete" v-show="item.version!=='v1'&&versionValue!==item.version" slot="reference" @click.stop="getVdelete(item)"></i>
            </el-popover>
          </el-option>
        </el-select>
      </div>
      <div class="head_btn" style="width: 110px">
        <el-tooltip effect="dark" content="另存为" placement="top">
          <i class="iconfont icon-baocun"></i>
        </el-tooltip>
        <el-popover
          placement="bottom"
          width="160"
          :visible-arrow="false"
          v-model="saveAsVisible"
        >
          <ul class="saveAsUl">
            <li @click="saveTemplateVisible = true">模板</li>
            <el-divider></el-divider>
            <li @click="getOpenSaveNewVersionVisible">新版本</li>
          </ul>
          <span slot="reference">另存为</span>
        </el-popover>
      </div>
      <div style="display: flex" v-show="!showTips">
        <div class="head_btn" @click="handleBuild">
          <el-tooltip effect="dark" content="保存" placement="top">
            <i class="iconfont icon-baocun"></i>
          </el-tooltip>
          <span>保存</span>
        </div>
        <!-- <div class="head_btn" @click="handleImg">
        <el-tooltip effect="dark" content="导出图片" placement="top">
          <i class="el-icon-camera"></i>
        </el-tooltip>
      </div> -->
        <!-- <div class="head_btn" @click="handleView" v-show="contain.menuFlag">
        <el-tooltip effect="dark" content="预览" placement="top">
          <i class="iconfont icon-view"></i>
        </el-tooltip>
      </div>
      <div class="head_btn" @click="handleReset" v-show="!contain.menuFlag">
        <el-tooltip effect="dark" content="还原" placement="top">
          <i class="iconfont icon-reset"></i>
        </el-tooltip>
      </div> -->
        <!-- <div class="head_btn" @click="$refs.result.show=true">
        <el-tooltip effect="dark" content="导入导出" placement="top">
          <i class="el-icon-download"></i>
        </el-tooltip>
      </div> -->
        <!-- <div class="head_btn" @click="handleShare">
        <el-tooltip effect="dark" content="分享" placement="top">
          <i class="el-icon-share"></i>
        </el-tooltip>
      </div> -->
        <div class="head_btn" @click="handleShare">
          <el-tooltip effect="dark" content="预览" placement="top">
            <i class="iconfont icon-view"></i>
          </el-tooltip>
          <span>预览</span>
        </div>
        <div class="head_btn" @click="handleRelease">
          <el-tooltip effect="dark" content="发布" placement="top">
            <i class="el-icon-s-promotion"></i>
          </el-tooltip>
          <span>发布</span>
        </div>
        <div class="head_btn" @click="handleExit">
          <el-tooltip effect="dark" content="退出" placement="top">
            <i class="iconfont icon-guanbi"></i>
          </el-tooltip>
          <span>退出</span>
        </div>
      </div>
      <!-- <div class="head_btn" :disabled='!contain.canUndo' @click="contain.editorUndo">
        <el-tooltip effect="dark" content="撤销" placement="top">
          <i class="nav__icon el-icon-arrow-left"></i>
        </el-tooltip>
      </div>
      <div class="head_btn" :disabled='!contain.canRedo' @click="contain.editorRedo">
        <el-tooltip effect="dark" content="重做" placement="top">
          <i class="nav__icon el-icon-arrow-right"></i>
        </el-tooltip>
      </div> -->
    </div>
    <result ref="result"></result>
    <el-dialog
      title="保存为模板"
      :visible.sync="saveTemplateVisible"
      :modal="false"
      width="800px"
      @close="resetForm"
    >
      <el-form ref="form" :model="form" :rules="rules">
        <el-form-item label="收藏模板名称" prop="name">
          <el-input v-model.trim="form.name" clearable></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="resetForm">取 消</el-button>
        <el-button type="primary" @click="saveAsTemplate">确定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :title="saveNewVersionTitle"
      :visible.sync="saveNewVersionVisible"
      :modal="false"
      width="800px"
      @close="resetVersionForm"
    >
      <el-form ref="versionForm" :model="versionForm" :rules="versionFormRules">
        <el-form-item label="版本号" prop="version">
          <el-input v-model.trim="versionForm.version" clearable></el-input>
        </el-form-item>
        <el-form-item label="版本更新内容" prop="versionRemark">
          <el-input
            v-model.trim="versionForm.versionRemark"
            clearable
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="resetVersionForm">取 消</el-button>
        <el-button type="primary" @click="saveAsNewVersion" :disabled="isLock"
          >确定</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import result from './result'
import {
  updateComponent,
  importAddObj,
  AddNewVersion,
  getVersionData,
  getCurrentVersion,
  getRemoveVersion,
  getUpdateVersion
} from '@/api/visual'
// import crypto from '@/utils/crypto'
import crypto from '@/utils/crypto.min.js'
import config, { url } from '@/config'
import { EventBus } from '@/bus.js'
import { dateFormat } from '@/utils/date.js'
import { getReleaseInfo } from '@/api/share.js'
import { startView } from '@/api/visual.js'
export default {
  inject: ['contain'],
  components: {
    result,
  },

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
    isReleaseFlag: Boolean,
  },
  data() {
    return {
      versionValueFalge:false,
      versionId:'',
      saveNewVersionTitle:'保存为新版本',
      saveNewVersionFalge:true,
      alignLineEnable: this.$store.state.alignLine.enable,
      saveAsVisible: false,
      saveTemplateVisible: false,
      saveNewVersionVisible: false,
      versionRecordVisible: false,
      versionList: [],
      visualId: '',
      isLock: false,
      form: {
        name: '',
      },
      rules: {
        name: [
          { required: true, message: '请输入模板的名称', trigger: 'blur' },
        ],
      },
      versionForm: {
        versionRemark: '',
        version: '',
      },
      versionFormRules: {
        versionRemark: [
          { required: true, message: '请输入版本更新内容', trigger: 'blur' },
        ],
        version: [{ required: true, message: '请输入版本号', trigger: 'blur' }],
      },
    }
  },
  computed: {
    isLockUse() {
      if (this.contain.activeIndex) {
        // 如果选中了组件
        // 如果组件当前状态为锁定,禁用
        if (this.contain.activeObj.lock) {
          return true
        } else {
          return false
        }
      } else {
        // 如果没有选中组件 就返回true，禁用该按钮
        return true
      }
    },
    isJiesuoUse() {
      if (this.contain.activeIndex) {
        // 如果选中了组件
        // 如果组件当前状态为锁定那么可以使用
        if (this.contain.activeObj.lock) {
          return false
        } else {
          return true
        }
      } else {
        // 如果没有选中组件 就返回true，禁用该按钮
        return true
      }
    },
    showTips() {
      return this.$store.state.showTips
    },
  },
  mounted() {
    EventBus.$on('release', () => {
      this.handleShare()
    })
    this.visualId = this.$route.params.id
    this.getVersion()
  },
  beforeDestroy() {
    EventBus.$off()
    this.$bus.$off('detailData')
  },
  methods: {
    getOpenSaveNewVersionVisible(){
      this.saveNewVersionVisible = true;
      this.saveNewVersionFalge = true;
      this.saveNewVersionTitle = '保存为新版本'
    },
    getOpenEditVersionVisible(item){
      this.versionForm.version = item.version
      this.versionForm.versionRemark = item.versionRemark
      this.saveNewVersionVisible = true;
      this.saveNewVersionFalge = false;
      this.versionId = item.versionId;
      this.saveNewVersionTitle = '编辑版本信息'
      if(item.version===this.versionValue){
        this.versionValueFalge = true
      }else{
        this.versionValueFalge = false;
      }
    },
    getVdelete(item) {
      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        console.log(item,666)
        getRemoveVersion(item.versionId).then(res=>{
          if(res.data.code==200){
            this.$message.success('删除成功')
            this.getVersion()
          }else{
            this.$message.success('删除失败')
          }
        })
      }).catch(() => {})
    },
    getVersion(falge) {
      getCurrentVersion(this.visualId).then(res => {
        this.versionList = res.data.data
        if(falge&&this.versionValueFalge){
          let selectVersion =  this.versionList.filter(item=>{
            return item.versionId === this.versionId
          })
          if(selectVersion.length>0){
            this.versionValue = selectVersion[0].version
          }
        }
        this.versionList = this.versionList.map(item => {
          return {
            version: item.version,
            versionRemark: item.versionRemark,
            visualId: item.visualId,
            versionId: item.versionId,
            createTime: dateFormat(
              'YYYY/mm/dd HH:MM',
              new Date(item.createTime)
            ),
          }
        })
      })
    },
    handleReset() {
      this.contain.menuFlag = true
      this.contain.setScale(this.contain.contentWidth)
    },
    handleFlag(name) {
      this.contain[name] = !this.contain[name]
      this.$nextTick(() => {
        this.contain.setScale(
          document.getElementsByClassName('section')[0].clientWidth
        )
      })
    },
    handleView() {
      this.contain.menuFlag = false
      this.$nextTick(() => {
        this.contain.handleInitActive()
        this.contain.setScale(document.body.clientWidth)
      })
    },
    updateVersion(data) {
      console.log('data===>', data)
      this.versionValue = data.version
    },
    handleBuild(fn) {
      let isFun = typeof fn === 'function'
      if (!isFun && this.$route.params.id <= 100 && this.$website.isDemo) {
        this.$message.error(this.$website.isDemoTip)
        return
      }
      this.contain.handleInitActive()
      const loading = this.$loading({
        lock: true,
        text: '加载中...',
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
            console.log('formdata', file)
            // axios
            //   .post(url + '/visual/put-file', formdata, {
            //     headers: {
            //       'Content-Type': 'multipart/form-data',
            //     },
            //   })
            axios
              .post(
                url + '/oss/file/custom/thumbnail/' + this.visualId,
                formdata,
                {
                  headers: {
                    'Content-Type': 'multipart/form-data',
                  },
                }
              )
              .then(res => {
                const data = res.data.data
                const url = data.link
                const formdata = {
                  visual: {
                    id: this.contain.visual.id,
                    backgroundUrl: url,
                    title: this.contain.visual.title,
                  },
                  config: {
                    id: this.contain.obj.config.id,
                    detail: JSON.stringify(this.contain.config),
                    component: JSON.stringify(this.contain.nav),
                    version: this.contain.versionInfo.version,
                    versionRemark: this.contain.versionInfo.versionRemark,
                    visualId: this.contain.versionInfo.visualId,
                    backgroundId: data.id,
                  },
                }
                return updateComponent(formdata)
              })
              .then(() => {
                if (!this.isReleaseFlag) {
                  loading.close()
                }
                if (isFun) {
                  fn()
                } else {
                  this.$message.success('大屏配置保存成功')
                }
                this.$emit('update:isReleaseFlag', false)
              })
              .catch(err => {
                this.$emit('update:isReleaseFlag', false)
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
    handleImg() {
      this.$confirm('是否导出大屏图片？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          this.$Screenshot(document.querySelector('#wrapper')).then(canvas => {
            this.downFile(
              canvas.toDataURL('image/jpeg', 0.8),
              new Date().getTime()
            )
            this.$message.success('图片导出成功')
          })
        })
        .catch(() => {})
    },
    handleShare() {
      this.handleBuild(async () => {
        const id = crypto
          .encrypt(this.contain.id)
          .replaceAll('/', config.SHARESECRETKEY) // .replace('/', '^JsH$71b1TFg#kUN')
        let routeUrl = this.$router.resolve({
          path: '/view/' + id,
        })
        try {
          const res = await startView(id)
          if (res.data.success) {
            window.open(routeUrl.href, '_blank')
          }
        } catch (error) {
          console.log(error)
        }
      })
    },
    // 点击新增的发布按钮
    async handleRelease() {
      // this.handleBuild(async () => {

      // })
      const id = crypto
        .encrypt(this.contain.id)
        .replaceAll('/', config.SHARESECRETKEY) // .replace('/', '^JsH$71b1TFg#kUN')
      let routeUrl = this.$router.resolve({
        path: '/share/' + id,
      })
      try {
        const res = await getReleaseInfo(this.contain.id)
        if (res.data.success) {
          const data = res.data.data
          // 添加非自定义的发布链接
          const releaseInfo = {}
          releaseInfo.originalUrl = routeUrl.href
          releaseInfo.isCustom = data.isCustom
          releaseInfo.isEnc = data.isEnc
          releaseInfo.isRelease = data.isRelease
          releaseInfo.path = data.path ? data.path : ''
          releaseInfo.psw = data.psw ? data.psw : ''
          releaseInfo.id = this.visualId
          releaseInfo.version = data.version //记录当前版本
          console.log('releaseInfo', releaseInfo)
          this.$emit('update:releaseInfo', releaseInfo)
          this.$emit('update:releaseDialogVisible', true)
        } else {
          this.$message.error(res.data.msg)
        }
      } catch (error) {}
    },
    // 点击退出页面
    handleExit() {
      this.$confirm('是否退出当前编辑页面？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        this.handleBuild(() => {
          this.$router.replace({ path: '/list' })
        })
      })
    },
    changeStatus(status) {
      let alignLine = {
        enable: status,
        top: 0,
        bottom: 0,
        left: 0,
        right: 0,
        vertical: 0,
        horizontal: 0,
        topShow: false,
        bottomShow: false,
        leftShow: false,
        rightShow: false,
        verticalShow: false,
        horizontalShow: false,
      }
      this.$store.commit('SET_ALIGNLINE_INFO', alignLine)
    },
    // 处理图层的移动事件
    handleToolClick(action) {
      // console.log('chufale')
      // EventBus.$emit('handleToolClick', action)
      //  EventBus.$emit('test', action)
      switch (action) {
        case 'Top':
          this.contain.$refs.contentmenu.handleTop()
          break
        case 'Bottom':
          this.contain.$refs.contentmenu.handleBottom()
          break
        case 'StepTop':
          this.contain.$refs.contentmenu.handleStepTop()
          break
        case 'StepBottom':
          this.contain.$refs.contentmenu.handleStepBottom()
          break
        case 'lock':
          this.contain.$refs.contentmenu.handleLock()
          break
      }
    },
    //关闭tips组件配置
    closeTips() {
      this.$store.commit('SET_SHOW_TIPS', !this.showTips)
      this.$store.commit('SET_TIPS_INDEX', '')
      this.contain.active = [];
    },
    dataURLtoFile(dataurl, filename) {
      var arr = dataurl.split(','),
        mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]),
        n = bstr.length,
        u8arr = new Uint8Array(n)
      while (n--) {
        u8arr[n] = bstr.charCodeAt(n)
      }
      return new File([u8arr], filename, { type: mime })
    },
    resetForm() {
      this.$refs.form.resetFields()
      this.saveTemplateVisible = false
    },
    resetVersionForm() {
      this.$refs.versionForm.resetFields()
      this.saveNewVersionVisible = false
    },
    //保存为模板
    saveAsTemplate() {
      this.$refs.form.validate(valid => {
        if (!valid) return false
        this.$nextTick(() => {
          html2canvas(document.getElementById('content'), {
            useCORS: true,
            backgroundColor: null,
            allowTaint: true,
          }).then(canvas => {
            var file = this.dataURLtoFile(
              canvas.toDataURL('image/jpeg', 0.1),
              new Date().getTime() + '.jpg'
            )
            var formdata = new FormData()
            formdata.append('file', file)
            console.log('formdata', file)
            axios
              .post(
                url + '/oss/file/custom/thumbnail/' + this.visualId,
                formdata,
                {
                  headers: {
                    'Content-Type': 'multipart/form-data',
                  },
                }
              )
              .then(res => {
                if (res.data.success) {
                  const { data } = res.data
                  const backgroundUrl = data.link
                  const backgroundId = data.id
                  const templateParam = {
                    visual: {
                      category: '999999',
                      backgroundUrl,
                      title: this.form.name,
                    },
                    config: {
                      detail: JSON.stringify(this.contain.config),
                      component: JSON.stringify(this.contain.nav),
                      version: this.contain.versionInfo.version,
                      versionRemark: this.contain.versionInfo.versionRemark,
                      visualId: this.contain.versionInfo.visualId,
                      backgroundId,
                    },
                  }
                  importAddObj(templateParam).then(result => {
                    console.log('保存为模板', templateParam, result)
                    if (result.data.success) {
                      this.$message.success('保存成功')
                      this.resetForm()
                    } else {
                      this.$message.error(result.data.msg)
                    }
                  })
                }
              })
          })
        })
        // this.saveTemplate=true
        // const templateParam={
        //   visual: {
        //     category:'999999',
        //   },
        //   config: {
        //     detail: JSON.stringify(this.contain.config),
        //     component: JSON.stringify(this.contain.nav),
        //   },
        // }
        // importAddObj(templateParam).then(res=>{
        //   console.log('保存为模板',this.contain.config)
        //   if(res.data.success){
        //     this.$message.success('保存成功')
        //   }else{
        //     this.$message.error(res.data.msg)
        //   }
        // })
      })
    },
    saveAsNewVersion() {
      this.$refs.versionForm
        .validate(valid => {
          if (!valid) return false
          this.isLock = true
          this.$nextTick(() => {
            html2canvas(document.getElementById('content'), {
              useCORS: true,
              backgroundColor: null,
              allowTaint: true,
            }).then(canvas => {
              var file = this.dataURLtoFile(
                canvas.toDataURL('image/jpeg', 0.1),
                new Date().getTime() + '.jpg'
              )
              var formdata = new FormData()
              formdata.append('file', file)
              console.log('formdata', file)
              axios
                .post(
                  url + '/oss/file/custom/thumbnail/' + this.visualId,
                  formdata,
                  {
                    headers: {
                      'Content-Type': 'multipart/form-data',
                    },
                  }
                )
                .then(res => {
                  if (res.data.success) {
                    console.log('测试保存新版本', this.contain.config)
                    const { data } = res.data
                    const backgroundUrl = data.link
                    const templateParam = {
                      visual: {
                        category: '999999',
                        backgroundUrl,
                        title: this.form.name,
                      },
                      config: {
                        detail: JSON.stringify(this.contain.config),
                        component: JSON.stringify(this.contain.nav),
                        version: this.versionForm.version,
                        versionRemark: this.versionForm.versionRemark,
                        visualId: this.visualId,
                        backgroundId: data.id,
                      },
                    }
                    if(this.saveNewVersionFalge){
                      AddNewVersion(templateParam).then(result => {
                        console.log(result)
                        if (result.data.success) {
                          this.$message.success('保存成功')
                          this.resetVersionForm()
                          this.getVersion()
                        } else {
                          this.$message.error(result.data.msg)
                        }
                      })
                    }else{
                      let parms = {
                        versionId:this.versionId,
                        newVersion:this.versionForm.version,
                        newVersionRemark:this.versionForm.versionRemark
                      }
                      getUpdateVersion(parms).then(res=>{
                        if(res.data.code===200){
                          this.$message.success('修改成功')
                          this.resetVersionForm()
                          this.getVersion(true)
                        }else{
                          this.$message.success('修改失败')
                        }
                      })
                    }
                  }
                  this.isLock = false
                })
                .catch(error => {
                  this.isLock = false
                })
            })
          })
        })
        .then(async _ => {
          try {
            const result = await getVersionData(this.visualId, ver)
            this.$bus.$emit('getNewVersion', result.data.data)
            console.log('最新版本', result.data.data, this.contain.versionInfo)
          } catch (error) {}
        })
        .catch(() => {})
    },
    applyVersionSure(ver) {
      this.$confirm('确认应用' + ver + '版本', {
        confirmButtonText: '是',
        cancelButtonText: '否',
        type: 'warning',
      })
        .then(async _ => {
          try {
            const result = await getVersionData(this.visualId, ver)
            this.$bus.$emit('getNewVersion', result.data.data)
            console.log('最新版本', result.data.data, this.contain.versionInfo)
          } catch (error) {}
        })
        .catch(() => {})
    },
  },
  created() {
    this.$bus.$on('detailData', content => {
      this.versionValue = content.config.version
    })
  },
}
</script>

<style lang="scss">
@import '~@/styles/buildVariables.scss';
.head {
  position: relative;
  height: 41px;
  padding-right: 8px;
  display: flex;
  z-index: 100;
  align-items: center;
  user-select: none;
  color: var(--datav-gui-font-color-base);
  border-bottom: var(--datav-border-dark);
  // background: #1d1e1f;
  background: $bgc1;
  &_actions {
    position: absolute;
    top: 0;
    right: 8px;
    // width: 300px;
    width: 350px;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    height: 40px;
  }
  &_left {
    left: 8px;
    width: 400px;
    justify-content: flex-start;
    .head_btn--active {
      background-color: #3a89fe;
      i,
      span {
        color: #fff;
      }
      &:hover {
        background-color: #454851;
        i,
        span {
          color: #fff;
        }
      }
    }
  }
  &_tool {
    width: 500px;
    // left: 260px;
    left: 775px;
    justify-content: start;
  }
  &_btn {
    margin-left: 4px;
    // width: 40px;
    width: 60px;
    height: 24px;
    // line-height: 26px;
    line-height: 24px;
    text-align: center;
    cursor: pointer;
    background: #303640;
    transition: 0.2s;
    i {
      color: #b4b7c1;
    }
    span {
      font-size: 14px;
      padding-left: 5px;
      color: #b4b7c1;
      font-family: 'Microsoft YaHei';
    }
    &:hover {
      background-color: #454851;
      i,
      span {
        color: #fff;
      }
    }
  }
  &_info {
    position: absolute;
    left: 50%;
    top: 0;
    transform: translateX(-50%);
    text-align: center;
    cursor: default;
    font-size: 14px;
    max-width: 500px;
    font-weight: bold;
    color: #fff;
    line-height: 40px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    display: flex;
    align-items: center;
    i {
      margin-right: 8px;
      font-size: 20px;
    }
  }
  &_select {
    .el-select {
      .el-input__inner {
        width: 110px;
        height: 24px;
        line-height: 24px;
        border: 0;
        background-color: #232630 !important;
        text-align: right;
      }
      .el-input__icon {
        line-height: 24px;
      }
    }
  }
  .new_head_info {
    width: 187px;
    text-align: center;
    color: #fff;
    font-family: MicrosoftYaHei;
    font-size: 14px;
  }
}
.el-popover {
  li {
    text-align: center;
    line-height: 30px;
  }
  .el-divider {
    margin: 5px 0;
  }
}
.versionListBox {
  max-height: 200px;
  overflow-y: auto;
}
.versionListDetail {
  padding: 12px 0 23px;
  margin: 0;
  box-shadow: 0px 2px 8px 0px rgba(0, 0, 0, 0.5);
  border-radius: 2px;
  p {
    font-size: 12px;
    padding: 0 0 4px 12px;
  }
  .el-divider {
    margin: 0;
    background-color: #363841;
  }
  .content_box {
    padding: 12px 10px 0 12px;
    font-size: 12px;
    color: #b4b7c1;
    div {
      padding: 6px 0;
    }
  }
}
.versionListDetail[x-placement^='right'] {
  margin: 0;
}
.versionNum {
  overflow: hidden; /*超出的部分隐藏起来。*/
  white-space: nowrap; /*不显示的地方用省略号...代替*/
  text-overflow: ellipsis; /* 支持 IE */
  text-align: left;
}
.head_select_el-select-dropdown {
  background-color: #2d2f38;
  box-shadow: 0px 2px 8px 0px rgba(0, 0, 0, 0.5);
  border-radius: 2px;
  .el-scrollbar {
    .el-scrollbar__view {
      padding: 0;
      .el-select-dropdown__item {
        line-height: 11px;
        .el-popover__reference {
          width: 110px;
          color: #b4b7c1;
          padding-left: 10px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        &:hover {
          .el-popover__reference {
            color: #fff;
          }
        }
      }
    }
  }
  .popper__arrow {
    display: none;
  }
}
.head_select_el-select-dropdown[x-placement^='bottom'] {
  margin-top: 0;
}
</style>
<style>
.head_btn {
  box-shadow: inset 0 0 0 1px rgb(255 235 235 / 10%), 0 0 0 1px #181818;
}
.tool_btn {
  line-height: 0;
}
.lock_btn_use {
  opacity: 0.5;
}
.pad0 {
  padding: 0;
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
.saveAsUl li {
  cursor: pointer;
}
.v-delete{
  position: absolute;
  right: 5px;
  top:12px;
  color: #b4b7c1;
}
.v-edit{
  position: absolute;
  top: 12px;
  right: 12px;
  cursor: pointer;
}
</style>
