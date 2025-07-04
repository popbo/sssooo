<template>
  <el-container class="list">
    <el-aside width="180px">
      <ul class="menu">
        <li :index="item.categoryValue"
            :key="item.categoryValue"
            v-for="item in typeList"
            :class="{'is-active':item.categoryValue===activeName}"
            @click="handleSelect(item.categoryValue)">
          <i class="el-icon-s-order"></i>
          {{item.categoryKey}}
        </li>
      </ul>
    </el-aside>
    <el-container>
      <el-header class="content__header">
        <p class="avue-tip-title">选择下面的方式进行创建</p>
        <div class="content__box content__nav">
          <div class="content_add_import">
          <div class="content__add"
               @click="handleAdd">
            <img :src="`${$router.options.base}img/new-project.png`"
                 height="40px"
                 alt="">
            <p>新建大屏</p>
            </div>
            <div class="import">
              <el-button type="primary" @click="$refs.result.show=true">导入</el-button>
            </div>
          </div>
          <div class="content__page">
            <el-pagination v-if="page.total>0"
                           layout="total, sizes, prev, pager, next, jumper"
                           background
                           size="mini"
                           @size-change="handleSizeChange"
                           @current-change="handleCurrentChange"
                           :page-size="page.size"
                           :current-page.sync="page.page"
                           :total="page.total">
            </el-pagination>
          </div>
        </div>
      </el-header>
      <el-main class="content">
        <div class="content__box">
          <div class="content__item"
               v-for="(item,index) in list"
               :key="index"
               @mouseover="item._menu=true"
               @mouseout="item._menu=false">
            <div class="content__info">
              <img v-if="item.backgroundUrl"
                   :src="item.backgroundUrl"
                   alt="" />
              <div class="content__menu"
                   v-show="item._menu">
                <div class="content__btn"
                     @click="handleEdit(item)">
                  编辑
                </div>
                <div class="content__btn"
                     @click="handleExport(item)">
                  导出
                </div>
              </div>
            </div>
            <div class="content__main">
              <span class="content__name">{{item.title}}</span>
              <div class="content__menulist">
                <div class="content__view">
                  <el-tooltip content="删除">
                    <i class="el-icon-delete"
                       @click="handleDel(item,index)"></i>
                  </el-tooltip>
                  <el-tooltip content="编辑">
                    <i class="el-icon-edit"
                       @click="handleUpdate(item,index)"></i>
                  </el-tooltip>
                  <el-tooltip content="预览">
                    <i class="el-icon-view"
                       @click="handleViews(item,index)"></i>
                  </el-tooltip>
                  <el-tooltip content="复制">
                    <i class="el-icon-copy-document"
                       @click="handleCopy(item,index)"></i>
                  </el-tooltip>
                </div>
                <span class="content__status"
                      :class="{'content__status--active':item.status}">
                  {{item.status?'已发布':'未发布'}}
                </span>
              </div>

            </div>
          </div>
        </div>
      </el-main>
    </el-container>
    <el-dialog title="编辑大屏"
               width="50%"
               :close-on-click-modal="false"
               :visible.sync="box">
      <avue-form :option="option"
                 v-model="form"
                 @submit="handleSave"></avue-form>
    </el-dialog>
    <result ref="result" @importData="importData"></result>
  </el-container>
</template>
<script>
import { getList, updateObj, delObj, getCategory, copyObj } from '@/api/visual';
import { getExportJson,importAddVersionObj } from '@/api/visual'
// import { viewHtml } from '@/utils/html'
// import { viewHtml } from '@/utils/html.min.js'
import crypto from '@/utils/crypto.min.js'
import config from '@/config'
import result from './result';
export default {
  name: "list",
  components: {
    result
  },
  data () {
    return {
      typeList: [],
      box: false,
      option: {
        submitText: '修改大屏',
        column: [{
          label: '分组',
          prop: 'category',
          span: 24,
          labelWidth: 100,
          type: 'select',
          dicUrl: this.$website.url + '/category/list',
          props: {
            label: 'categoryKey',
            value: 'categoryValue',
          },
          rules: [{
            required: true,
            message: "请选择分组",
            trigger: "blur"
          }]
        }, {
          label: '大屏名称',
          span: 24,
          labelWidth: 100,
          prop: 'title',
          rules: [{
            required: true,
            message: "请输入大屏名称",
            trigger: "blur"
          }]
        }, {
          label: '密码',
          span: 24,
          type: 'password',
          labelWidth: 100,
          prop: 'password',
        }, {
          label: '发布状态',
          prop: 'status',
          span: 24,
          labelWidth: 100,
          type: 'select',
          dicData: [{
            label: '未发布',
            value: 0
          }, {
            label: '已发布',
            value: 1
          }]
        }]
      },
      page: {
        page: 1,
        size: 50,
        total: 0,
      },
      form: {},
      activeName: '',
      list: [],
    }
  },
  created () {
    this.getCategory()
  },
  methods: {
    handleSelect (key) {
      this.activeName = key;
      console.log('this.activeName',this.activeName)
      this.page.page = 1;
      this.getList();
    },
    vaildData (id) {
      const list = [];
      for (var i = 0; i < 20; i++) {
        list.push(i)
      }
      return list.includes(id)
    },
    getCategory () {
      getCategory().then(res => {
        const data = res.data.data;
        this.typeList = data;
        this.activeName = (data[0] || {}).categoryValue;
        this.getList();
      })
    },
    handleExport (item) {
      getExportJson(item.id).then(res => {
        const data = res.data.data;
        console.log('data导出',data)
        let allVersion=data
        // let mode = {
        //   detail: JSON.parse(data.config.detail),
        //   component: JSON.parse(data.config.component),
        //   bgImage:item.backgroundUrl,
        //   version:data.config.version,
        //   versionRemark:data.config.versionRemark
        // }
        var zip = new window.JSZip();
        // console.log("mode",mode)
        zip.file("view.json", `${JSON.stringify(allVersion, null, 4)}`);
        // zip.file('index.html', viewHtml);
        zip.generateAsync({ type: "blob" })
          .then(function (content) {
            console.log('content',content)
             saveAs(content, '下载.zip')
            // location.href = "data:application/zip;base64," + content;
          });
      })
    },
    handleCopy (item) {
      if (this.$website.isDemo) {
        this.$message.error(this.$website.isDemoTip)
        return
      }
      this.$confirm('确认复制当前大屏', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        copyObj(item.id).then((res) => {
          this.$message.success('复制成功');
          const id = res.data.data;
          this.handleEdit({ id })
        })
      }).catch(() => {

      });
    },
    handleDel (item, index) {
      if (this.vaildData(index) && this.$website.isDemo) {
        this.$message.error(this.$website.isDemoTip)
        return false;
      }
      this.$confirm('是否确认永久删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delObj(item.id).then(() => {
          this.list.splice(index, 1)
          this.$message.success('删除成功')
        })
      }).catch(() => {

      });
    },
    handleAdd () {
      this.$router.push({
        path: '/create', query: {
          category: this.activeName
        }
      })
    },
    handleUpdate (item, index) {
      if (this.vaildData(Number(index)) && this.$website.isDemo) {
        this.$message.error(this.$website.isDemoTip)
        return false;
      }
      this.form = item
      this.form.category = this.form.category + '';
      this.box = true;
    },
    handleEdit (item) {
      let routeUrl = this.$router.resolve({
        path: '/build/' + item.id
      })
      window.open(routeUrl.href, '_blank');
    },
    handleViews (item) {
      const id = crypto.encrypt(item.id).replaceAll('/', config.SHARESECRETKEY)
      let routeUrl = this.$router.resolve({
        path: '/view/' + id
      })
      window.open(routeUrl.href, '_blank');
    },
    handleSave (form, done) {
      updateObj({
        id: this.form.id,
        category: this.form.category,
        password: this.form.password,
        status: this.form.status,
        title: this.form.title
      }).then(() => {
        done();
        this.$message.success('修改成功');
        this.getList();

      })
    },
    handleCurrentChange (val) {
      this.page.page = val;
      this.getList();
    },
    handleSizeChange (val) {
      this.page.size = val;
      this.getList();
    },
    getList (category) {
      this.list = []
      getList({
        category: category || this.activeName,
        current: this.page.page,
        size: this.page.size,
      }).then(res => {
        const data = res.data.data;
        this.page.total = data.total;
        this.list = data.records
        this.initData();
      })
    },
    //导入数据
    importData(content){
      console.log('activeName',content)
      const importParams=content
      // const importParams={
      //   config:{
      //     component:JSON.stringify(content.component),
      //     detail:JSON.stringify(content.detail),
      //     version:content.version,
      //     versionRemark:content.versionRemark
      //   },
      //   visual:{
      //     backgroundUrl:content.bgImage,
      //     title:content.detail.name,
      //     category:this.activeName
      //   }
      // }
      importAddVersionObj(importParams).then(res=>{
        if(res.data.success){
          this.getList()
        }
      })
    },
    initData () {
      this.list.forEach((ele, index) => {
        this.$set(this.list[index], '_menu', false)
      })
    }
  }
}
</script>
<style lang="scss">
@import "@/styles/list.scss";
</style>
