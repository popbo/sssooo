<template>
    <el-container
      class="cockpit"
      v-loading="loading"
      element-loading-background="rgba(0, 0, 0, 0.8)"
    >
      <el-container>
        <el-header class="content__header">
          <el-button
            size="mini"
            icon="el-icon-plus"
            class="btn-build"
            @click="handleAdd"
            type="primary"
            >新建大屏</el-button
          >
          <p class="p-sum">
            全部大屏<span>{{ page.total }}</span
            >个
          </p>
          <div class="filter-box" v-show="!batchEdit">
            <el-input
              placeholder="请输入内容"
              v-model="serachValue"
              @input="handleSearch"
              class="input-serach"
              size="mini"
              width="175px"
            ></el-input>
            <el-select
              v-model="oderTime"
              placeholder="选择排序方式"
              class="select-sort"
              size="mini"
              @change="handleOrderChange"
            >
              <el-option
                v-for="item in timeFilterOpt"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
            <el-button
              type="info"
              class="btn-batch edit-btn"
              size="mini"
              @click="handleBatchEdit"
              >批量编辑</el-button
            >
          </div>
          <div class="batch-operation-box" v-show="batchEdit">
            <el-button
              type="primary"
              class="btn-batch"
              size="mini"
              @click="checkAll"
              >全选</el-button
            >
            <el-button
              type="info"
              class="btn-batch"
              size="mini"
              @click="handleBatchExport"
              >导出</el-button
            >
            <el-button
              type="danger"
              class="btn-batch"
              size="mini"
              @click="handleBatchDelete"
              >删除</el-button
            >
            <el-button
              type="info"
              class="btn-batch cancel-btn"
              size="mini"
              @click="cancelBatchEdit"
              >取消操作</el-button
            >
          </div>
        </el-header>
        <el-main class="content">
          <div class="content__box">
            <div
              class="content__item"
              v-for="(item, index) in list"
              :key="index"
              @mouseover="item._menu = true"
              @mouseout="item._menu = false"
            >
              <div class="content__info">
                <template v-if="item.backgroundType === 'DEFAULT'">
                  <img :src="imgFn(item.release.isRelease)" alt="" />
                </template>
                <template v-else>
                  <img :src="item.backgroundUrl" alt="" />
                </template>
                <!-- 鼠标移入浮层 -->
                <div class="content__menu" v-show="item._menu && !batchEdit">
                  <div class="top-btn">
                    <el-tooltip content="发布">
                      <i
                        class="el-icon-s-promotion"
                        style="color: #fff"
                        @click="handleRelease(item)"
                      ></i>
                    </el-tooltip>
                    <el-tooltip content="预览">
                      <i
                        class="el-icon-view"
                        style="color: #fff"
                        @click="handleViews(item, index)"
                      ></i>
                    </el-tooltip>
                  </div>
                  <div class="middle-btn">
                    <div class="edit-btn">
                      <el-button type="primary" @click="handleEdit(item)"
                        >编辑</el-button
                      >
                    </div>
                    <div class="icon-btn">
                      <el-tooltip content="删除">
                        <i
                          class="el-icon-delete"
                          style="color: #fff"
                          @click="handleDel(item, index)"
                        ></i>
                      </el-tooltip>
                      <el-tooltip content="编辑">
                        <i
                          class="el-icon-edit"
                          style="color: #fff"
                          @click="handleUpdate(item, index)"
                        ></i>
                      </el-tooltip>
                      <el-tooltip content="复制">
                        <i
                          class="el-icon-copy-document"
                          style="color: #fff"
                          @click="handleCopy(item, index)"
                        ></i>
                      </el-tooltip>
                      <el-tooltip content="导出">
                        <i
                          class="el-icon-upload2"
                          style="color: #fff"
                          @click="handleExport(item)"
                        ></i>
                      </el-tooltip>
                    </div>
                  </div>
                </div>
                <!-- 选择图标 -->
                <div class="content__check" v-show="batchEdit">
                  <img
                    v-show="!item._check"
                    src="@/assets/unselect.png"
                    alt=""
                    @click="item._check = !item._check"
                  />
                  <img
                    v-show="item._check"
                    src="@/assets/select.png"
                    alt=""
                    @click="item._check = !item._check"
                  />
                </div>
              </div>
              <div class="content__title">
                <span>{{ item.title }}</span>
                <span>{{ item.release.isRelease ? '已发布' : '未发布' }}</span>
                <span
                  class="status-light"
                  :class="
                    item.release.isRelease ? 'release-color' : 'unrelease-color'
                  "
                ></span>
              </div>
            </div>
          </div>
        </el-main>
        <el-footer class="content__footer">
          <div class="content__page">
            <el-pagination
              v-if="page.total > 0"
              layout="prev, pager, next,sizes, jumper"
              background
              size="mini"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :page-size="page.size"
              :current-page.sync="page.page"
              :total="page.total"
            >
            </el-pagination>
          </div>
        </el-footer>
      </el-container>
      <el-dialog
        title="编辑大屏"
        width="30%"
        :close-on-click-modal="false"
        :visible.sync="box"
      >
        <el-form
          :model="form"
          :rules="formRules"
          label-width="100px"
          ref="editFrom"
        >
          <el-form-item label="分类" prop="category">
            <el-select
              v-model="form.category"
              placeholder="请选择活动区域"
              size="mini"
            >
              <el-option
                v-for="item in typeList"
                :key="item.id"
                :label="item.categoryKey"
                :value="item.categoryValue"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="大屏名称" prop="title">
            <el-input v-model="form.title" size="mini"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="editPassword">
            <el-input
              type="password"
              v-model.trim="form.editPassword"
              autocomplete="off"
              size="mini"
              show-password
            ></el-input>
          </el-form-item>
          <el-form-item label="封面类型">
            <el-radio-group v-model="form.backgroundType">
              <el-radio label="DEFAULT">大屏默认封面</el-radio>
              <el-radio label="THUMBNAIL">页面截图</el-radio>
              <el-radio label="CUSTOM">自定义封面</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            label="上传封面"
            v-if="form.backgroundType === 'CUSTOM'"
            prop="backgroundUrl"
            key="backgroundUrl"
            :rules="formRules.backgroundType"
          >
            <el-upload
              :action="
                url + '/oss/file/custom/thumbnail/' + this.currentEditScreenId
              "
              :headers="headersObj"
              :data="{ visualId: currentEditScreenId }"
              :on-success="handleCoverImageSuccess"
              :show-file-list="false"
            >
              <img
                v-if="form.backgroundUrl && !startWithTHUMBNAIL.isTHUMBNAIL"
                :src="form.backgroundUrl"
              />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
          <el-form-item class="btn-form-item">
            <el-button type="primary" @click="handleSave" size="mini"
              >修改大屏</el-button
            >
            <el-button @click="resetForm('editFrom')" size="mini">清空</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
      <exportItem ref="exportItem" @exportData="exportData"></exportItem>
      <release-dialog
        ref="releaseDialog"
        :releaseDialogVisible.sync="releaseDialogVisible"
        :releaseInfo.sync="releaseInfo"
        @releaseCbFn="handleReleaseCbFn"
        @cancelReleaseCbFn="handleCancelReleaseCbFn"
        :isOutRelease="true"
      ></release-dialog>
      <!-- 新建 -->
      <el-dialog
        v-dialogDrag
        title="新建ai驾驶舱大屏"
        :visible="addCockpit"
        class="new-cockpit"
        @close="close"
        width="650px"
    >
        <div class="add-cockpit">
            <div>
                <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
                    <el-form-item label="大屏名称" prop="name">
                        <el-input v-model="ruleForm.name" size="small"></el-input>
                    </el-form-item>
                    <el-form-item label="大屏主题" prop="theme">
                        <el-select v-model="ruleForm.theme" placeholder="请选择"  size="small">
                            <el-option
                            v-for="item in themeList"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="链接" prop="link">
                        <el-input v-model="ruleForm.link" size="small"></el-input>
                    </el-form-item>
                    <el-form-item style="margin-top: 60px;">
                        <el-button type="primary"  size="small" style="width: 100px; margin-right: 120px;" @click="submitForm('ruleForm')">确定</el-button>
                        <el-button @click="close"  size="small"  style="width: 100px;" >取消</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
      </el-dialog>
    </el-container>
  </template>
  <script>
  import {
    getList,
    updateObj,
    delObj,
    getCategory,
    copyObj,
    startView,
    exportObj,
  } from '@/api/visual'
  import { getReleaseInfo, saveReleaseInfo, closeRelease } from '@/api/share.js'
  import crypto from '@/utils/crypto.min.js'
  import config from '@/config'
  import exportItem from './exportItem'
  import _ from 'lodash'
  import releaseDialog from '@/page/components/releaseDialog.vue'
  import { url } from '@/config.js'
  export default {
    name: 'list',
    data() {
      return {
        addCockpit:false,
        themeList:[{
            label:'主题一',
            value:0
        },{
            label:'主题二',
            value:1
        }],
        ruleForm:{
            name:'',
            theme:'',
            link:''
        },
        rules:{
            name: [
                { required: true, message: '请输入大屏名称', trigger: 'blur' },
            ],
            theme: [
                { required: true, message: '请输入大屏主题', trigger: 'blur' },
            ],
            link: [
                { required: true, message: '请输入链接', trigger: 'blur' },
            ],
        },
        typeList: [],
        box: false,
        page: {
          page: 1,
          size: 20,
          total: 0,
        },
        form: {},
        formRules: {
          category: [
            { required: true, message: '请选择活动区域', trigger: 'change' },
          ],
          title: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
          backgroundUrl: [
            { required: true, message: '请上传自定义封面', trigger: 'blur' },
          ],
        },
        activeName: '',
        list: [],
        timeFilterOpt: [
          {
            value: 'create_time',
            label: '按创建时间排序',
          },
          {
            value: 'update_time',
            label: '按修改时间排序',
          },
          {
            value: 'title',
            label: '按大屏名称排序',
          },
        ],
        batchEdit: false,
        serachValue: '',
        oderTime: 'create_time',
        releaseInfo: {
          originalUrl: '',
          isCustom: '',
          isEnc: '',
          isRelease: '',
          path: '',
          psw: '',
        },
        releaseDialogVisible: false,
        visualId: '',
        configId: '',
        headersObj: {
          Authorization: localStorage.getItem('Authorization'),
        },
        currentEditScreenId: '',
        url,
        backgroundId: '',
        fileList: [],
        exportItemId: '',
        startWithTHUMBNAIL: {
          isTHUMBNAIL: '',
          url: '',
        },
        loading: false,
      }
    },
    components: {
      releaseDialog,
      exportItem,
    },
    created() {
      if (window.sessionStorage.getItem('returnGoList')) {
        let category = window.sessionStorage.getItem('returnGoList')
        this.activeName = category
        window.sessionStorage.removeItem('returnGoList')
      }
      this.getCategory()
    },
    watch: {
      box(newVal) {
        if (newVal) {
          console.log('编辑大屏打开===========>', this.form)
          // 若打开编辑大屏弹框时背景类型为页面截图，则保存一份地址并标记
          if (this.form.backgroundType === 'THUMBNAIL') {
            this.startWithTHUMBNAIL = {
              isTHUMBNAIL: true,
              url: this.form.backgroundUrl,
            }
          }
        } else {
          this.startWithTHUMBNAIL = {
            isTHUMBNAIL: '',
            url: '',
          }
        }
      },
    },
    methods: {
      handleSelect(key) {
        this.activeName = key
        console.log('this.activeName', this.activeName)
        this.page.page = 1
        this.getList()
      },
      vaildData(id) {
        const list = []
        for (var i = 0; i < 20; i++) {
          list.push(i)
        }
        return list.includes(id)
      },
      getCategory() {
        getCategory().then(res => {
          const data = res.data.data
          this.typeList = data
          if (!this.activeName) {
            try{
              this.activeName = (data[0] || {}).categoryValue
            }catch(err){
              console.log(err)
            }
          }
          this.getList()
        })
      },
      handleExport(item) {
        this.exportItemId = item.id
        this.$refs.exportItem.exportDialogShow = true
        // getExportJson(item.id).then(res => {
        //   const data = res.data.data
        //   console.log('data导出', data)
        //   let allVersion = data
        //   // let mode = {
        //   //   detail: JSON.parse(data.config.detail),
        //   //   component: JSON.parse(data.config.component),
        //   //   bgImage:item.backgroundUrl,
        //   //   version:data.config.version,
        //   //   versionRemark:data.config.versionRemark
        //   // }
        //   var zip = new window.JSZip()
        //   // console.log("mode",mode)
        //   zip.file('view.json', `${JSON.stringify(allVersion, null, 4)}`)
        //   // zip.file('index.html', viewHtml);
        //   zip.generateAsync({ type: 'blob' }).then(function (content) {
        //     console.log('content', content)
        //     saveAs(content, '下载.zip')
        //     // location.href = "data:application/zip;base64," + content;
        //   })
        // })
      },
      exportData(type) {
        let consturl
        if (type === '2') {
          consturl = url + '/visual/full/export?id=' + this.exportItemId
        } else {
          consturl = url + '/visual/export?id=' + this.exportItemId
        }
        this.loading = true
        this.$nextTick(() => {
          try {
            axios.get(consturl).then(res => {
              if (res.data.data) {
                console.log(res.data)
                window.location.href = res.data.data
              } else {
                this.$message.error('导出失败')
              }
            })
            this.loading = false
          } catch (error) {
            this.loading = false
            this.$message.error('导出失败')
          }
        })
      },
      handleCopy(item) {
        if (this.$website.isDemo) {
          this.$message.error(this.$website.isDemoTip)
          return
        }
        this.$confirm('确认复制当前大屏', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            copyObj(item.id).then(res => {
              this.$message.success('复制成功')
  
              const id = res.data.data
              this.handleEdit({ id })
              this.getCategory()
            })
          })
          .catch(() => {})
      },
      handleDel(item, index) {
        if (this.vaildData(index) && this.$website.isDemo) {
          this.$message.error(this.$website.isDemoTip)
          return false
        }
        this.$confirm('是否确认永久删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }).then(() => {
            delObj(item.id).then(res => {
              if (res.data.code === 200) {
                this.list.splice(index, 1)
                this.$message.success('删除成功')
                this.getCategory()
              } else {
                this.$message.error('删除失败')
              }
            })
        }).catch(() => {})
      },
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
            if (valid) {
                this.addCockpit = false;
            } 
        });
      },
      handleAdd(){
            this.ruleForm={
                name:'',
                theme:'',
                link:''
            }
            this.addCockpit = true;
        },
        close(){
            this.addCockpit = false;
        },
      handleUpdate(item, index) {
        if (this.vaildData(Number(index)) && this.$website.isDemo) {
          this.$message.error(this.$website.isDemoTip)
          return false
        }
        // this.fileList = []
        this.form = this.deepClone(item)
        this.form.category = this.form.category + ''
        this.currentEditScreenId = item.id
        this.backgroundId = item.backgroundId
        this.box = true
      },
      handleEdit(item) {
        let routeUrl = this.$router.resolve({
          path: '/build/' + item.id,
        })
        window.open(routeUrl.href, '_blank')
      },
      async handleViews(item) {
        const id = crypto.encrypt(item.id).replaceAll('/', config.SHARESECRETKEY)
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
      },
      handleSave(form) {
        if (
          this.form.backgroundType === 'CUSTOM' &&
          this.form.backgroundUrl == this.startWithTHUMBNAIL.url
        ) {
          this.form.backgroundUrl = ''
        }
        this.$refs.editFrom.validate(valid => {
          if (valid) {
            try {
              updateObj({
                id: this.form.id,
                category: this.form.category,
                editPassword: this.form.editPassword,
                title: this.form.title,
                backgroundType: this.form.backgroundType,
                backgroundId: this.backgroundId,
              }).then(res => {
                if (res.data.success) {
                  this.$message.success('修改成功')
                  this.box = false
                  this.getList()
                } else {
                  this.$message.error(res.data.msg)
                }
              })
            } catch (error) {
              this.$message.error(error)
            }
          } else {
            this.$message.warning('表单校验失败，请检查表单')
          }
        })
      },
      handleCurrentChange(val) {
        this.page.page = val
        this.getList()
      },
      handleSizeChange(val) {
        this.page.size = val
        this.getList()
      },
      getList(category) {
        this.list = []
        getList({
          category: category || this.activeName,
          current: this.page.page,
          size: this.page.size,
          title: this.serachValue,
          order: this.oderTime === 'list' ? 'create_time' : this.oderTime,
        }).then(res => {
          const data = res.data.data
          this.page.total = data.total
          if (this.oderTime === 'title') {
            // 对返回数据进行处理，分别放入数字、字母、中文数组
            // 1.数字,2.字母，3.中文
            let numberData = [],
              letterData = [],
              ChineserData = [],
              otherData = []
            data.records.forEach(item => {
              const firstWord = item.title.toString().slice(0, 1)
              if (this.isNumber(firstWord)) {
                numberData.push(item)
              } else if (this.isLetter(firstWord)) {
                letterData.push(this.dataSort(item))
              } else if (this.isChinese(firstWord)) {
                ChineserData.push(this.dataSort(item))
              } else {
                otherData.push(item)
              }
            })
            // 分别对各类数组进行排序
            this.dataSort(numberData, 'number')
            this.dataSort(letterData, 'letter')
            this.dataSort(ChineserData, 'Chinese')
            // console.log(
            //   'numberData',
            //   numberData,
            //   'letterData',
            //   letterData,
            //   'ChineserData',
            //   ChineserData,
            //   'otherData',
            //   otherData
            // )
            this.list = [
              ...numberData,
              ...letterData,
              ...ChineserData,
              ...otherData,
            ]
          } else {
            this.list = data.records
          }
          // console.log('列表数据', this.list)
          this.initData()
        })
      },
      initData() {
        this.list.forEach((ele, index) => {
          this.$set(this.list[index], '_menu', false)
          this.$set(this.list[index], '_check', false)
        })
      },
      // 点击批量编辑
      handleBatchEdit() {
        this.batchEdit = true
      },
      // 点击取消操作
      cancelBatchEdit() {
        // 切换两组按钮的控制值
        this.batchEdit = false
        // 把每一项的选中值 _check改为false
        this.list.forEach((ele, index) => {
          ele._check = false
        })
      },
      // 点击全选按钮
      checkAll() {
        // 把每一项的选中值 _check改为false
        this.list.forEach((ele, index) => {
          ele._check = true
        })
      },
      // 改变大屏排序
      handleOrderChange() {
        this.getList()
      },
      // 输入框搜索大屏名称
      handleSearch: _.debounce(function () {
        this.getList()
      }, 0.5 * 1000),
      // 批量删除操作
      handleBatchDelete() {
        this.$confirm('是否确认批量删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            const checkIdsArr = []
            this.list.map(ele => {
              if (ele._check) {
                checkIdsArr.push(ele.id)
              }
            })
            delObj(checkIdsArr.join(',')).then(() => {
              this.getList()
            })
          })
          .catch(() => {})
      },
      // 批量导出
      handleBatchExport() {
        this.$confirm('是否确认批量导出?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }).then(() => {
          const checkIdsArr = []
          this.list.map(ele => {
            if (ele._check) {
              checkIdsArr.push(ele.id)
            }
          })
          if (checkIdsArr.length === 0) {
            return this.$message.warning('请选择要导出的大屏！')
          }
          this.loading = true
          this.$nextTick(() => {
            try {
              exportObj(checkIdsArr.join(',')).then(res => {
                console.log('批量导出结果', res)
                if (res.data.data) {
                  window.location.href = res.data.data
                  this.getList()
                } else {
                  this.$message.error('导出失败')
                }
                this.loading = false
              })
            } catch (error) {
              this.loading = false
              this.$message.error('导出失败')
            }
          })
        })
      },
      // 处理发布之后的回调函数
      async handleReleaseCbFn(infoData) {
        try {
          infoData.id = this.visualId
          const res = await saveReleaseInfo(infoData)
          if (res.data.success) {
            this.$refs.releaseDialog.$refs.releaseForm.resetFields()
            // 关闭表单
            this.releaseDialogVisible = false
            // const openlink = window.location.origin + '/share/' + infoData.path
            // window.open(openlink, '_blank')
            this.$message.success('发布成功')
            this.getList()
          } else {
            this.$message.error(res.data.msg)
          }
        } catch (error) {
          console.log(error)
        }
      },
      async handleRelease(item) {
        // this.releaseDialogVisible = true
        this.visualId = item.id
        const id = crypto.encrypt(item.id).replaceAll('/', config.SHARESECRETKEY) // .replace('/', '^JsH$71b1TFg#kUN')
        let routeUrl = this.$router.resolve({
          path: '/share/' + id,
        })
        try {
          const res = await getReleaseInfo(item.id)
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
            releaseInfo.id = item.id
            releaseInfo.version = data.version
            this.releaseInfo = releaseInfo
            this.releaseDialogVisible = true
          } else {
            this.$message.error(res.data.msg)
          }
        } catch (error) {
          console.log(error)
        }
      },
      async handleCancelReleaseCbFn() {
        try {
          const res = await closeRelease({
            id: this.visualId,
            isRelease: 0,
          })
          if (res.data.success) {
            this.$message.success('已取消发布')
            this.getList()
          }
        } catch (error) {
          console.log(error)
        }
      },
      imgFn(isRelease) {
        if (isRelease) {
          let img = require('@/assets/mrfm_yfb.png')
          return img
        } else {
          return require('@/assets/mrfm.png')
        }
      },
      handleRemove(file, fileList) {
        console.log(file, fileList)
      },
      handleCoverImageSuccess(res) {
        if (res.success) {
          this.$message.success('上传成功')
          this.startWithTHUMBNAIL.isTHUMBNAIL = false
          this.backgroundId = res.data.id
          this.form.backgroundUrl = res.data.link
        } else {
          this.$message.error(res.msg)
        }
      },
      resetForm(formName) {
        //只清空标题和密码
        this.form.title = ''
        this.form.editPassword = ''
      },
      // 判断数据类型
      isChinese(temp) {
        const re = /[^\u4E00-\u9FA5]/
        return re.test(temp) ? false : true
      },
      isNumber(temp) {
        return !isNaN(parseFloat(temp)) && isFinite(temp)
      },
      isLetter(temp) {
        const p = /[a-z]/i
        return p.test(temp)
      },
      // 对列表排序
      dataSort(val, type) {
        switch (type) {
          case 'number':
            return val.sort(
              (a, b) => a['title'].slice(0, 1) - b['title'].slice(0, 1)
            )
          case 'letter':
            return val.sort((a, b) =>
              (a['title'] + '').localeCompare(b['title'] + '')
            )
          case 'Chinese':
            return val.sort((a, b) => a['title'].localeCompare(b['title']))
          default:
            return val
        }
      },
    },
  }
  </script>
  <style lang="scss">
  @import '@/styles/cockpit.scss';
  </style>
  <style lang="scss" scoped>
   .new-cockpit{
    /deep/.el-dialog__body{
      padding: 30px 70px;
      box-sizing: border-box;
    }
    /deep/.el-dialog__header{
        padding: 15px 20px 10px;
        border-bottom: 1px solid #333;
    }
    .add-cockpit{
        height: 280px;
    }
  }
  </style>