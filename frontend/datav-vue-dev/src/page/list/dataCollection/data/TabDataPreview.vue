<template>
  <el-col>
    <el-row>
      <el-col style="width: 300px;">
        <el-form ref="form" :model="form" size="mini" class="row-style">
          <el-form-item>
            <span class="title-text" style="width: 100px;">显示行</span>
            <el-input v-model="form.row" class="main-area-input">
              <el-button type="primary" slot="append" size="mini" icon="el-icon-search" @click="reSearch" />
            </el-input>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
    <ux-grid
      ref="plxTable"
      size="mini"
      style="width: 100%;"
      :height="height"
      :checkbox-config="{highlight: true}"
      :width-resize="true"
    >
      <ux-table-column
        v-for="field in fields"
        :key="field.id"
        min-width="200px"
        :field="field.dataeaseName"
        :resizable="true"
      >
        <template slot="header">
          <svg-icon v-if="field.deType === 0" icon-class="field_text" class="field-icon-text" />
          <svg-icon v-if="field.deType === 1" icon-class="field_time" class="field-icon-time" />
          <svg-icon v-if="field.deType === 2 || field.deType === 3" icon-class="field_value" class="field-icon-value" />
          <svg-icon v-if="field.deType === 5" icon-class="field_location" class="field-icon-location" />
          <span>{{ field.name }}</span>
        </template>
      </ux-table-column>
    </ux-grid>
    <el-row style="margin-top: 4px;">
      <span v-if="table.type === 'excel' || table.type === 'custom' || table.type === 'union'" class="table-count">
        <span v-if="page.total <= currentPage.show">
          共
          <span class="span-number">{{ page.total }}</span>
          条数据
        </span>
        <span v-if="page.total > currentPage.show">
          显示
          <span class="span-number">{{ currentPage.show }}</span>
          条数据
          ，共
          <span class="span-number">{{ page.total }}</span>
          条数据
        </span>
      </span>
      <span v-if="table.type === 'db' || table.type === 'sql'" class="table-count">
        显示
        <span class="span-number">{{ page.total }}</span>
        条数据
      </span>
      <el-pagination
        :current-page="currentPage.page"
        :page-sizes="[100]"
        :page-size="currentPage.pageSize"
        :pager-count="5"
        layout="sizes, prev, pager, next"
        :total="currentPage.show"
        @current-change="pageChange"
      />
    </el-row>
  </el-col>
</template>

<script>
export default {
  name: 'TabDataPreview',
  props: {
    table: {
      type: Object,
      required: true
    },
    param: {
      type: Object,
      required: true
    },
    fields: {
      type: Array,
      required: true
    },
    data: {
      type: Array,
      required: true
    },
    form: {
      type: Object,
      required: true
    },
    page: {
      type: Object,
      required: false
    }
  },
  data() {
    return {
      height: 500,
      currentPage: {
        page: 1,
        pageSize: 100,
        show: parseInt(this.form.row)
      }
    }
  },
  computed: {
  },
  watch: {
    data() {
      const datas = this.data
      this.$refs.plxTable.reloadData(datas)
    },
    page() {
      if (this.page.total < parseInt(this.form.row)) {
        this.currentPage.show = this.page.total
      } else {
        this.currentPage.show = parseInt(this.form.row)
      }
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      this.calHeight()
    },
    calHeight() {
      const that = this
      setTimeout(function() {
        const currentHeight = document.documentElement.clientHeight
        that.height = currentHeight - 56 - 30 - 26 - 25 - 55 - 38 - 28 - 10
      }, 10)
    },
    reSearch() {
      if (!this.form.row ||
          this.form.row === '' ||
          this.form.row.length > 4 ||
          isNaN(Number(this.form.row)) ||
          String(this.form.row).includes('.') ||
          parseInt(this.form.row) < 1) {
        this.$message({
          message: '请输入5位以内的正整数',
          type: 'error',
          showClose: true
        })
        return
      }
      this.currentPage.show = parseInt(this.form.row)
      this.currentPage.page = 1
      this.$emit('reSearch', { form: this.form, page: this.currentPage })
    },
    pageChange(val) {
      this.currentPage.page = val
      // console.log(this.currentPage)
      this.$emit('reSearch', { form: this.form, page: this.currentPage })
    }
  }
}
</script>

<style lang="scss" scoped>
.title-text{
  color:#bfbfbf ;
}
  .row-style>>>.el-form-item__label{
    font-size: 12px;
  }
  .row-style>>>.el-form-item--mini.el-form-item{
    margin-bottom: 10px;
  }
  .row-style>>>.el-form-item__content{
    display: flex;
    flex-direction: row;
    width: 250px;
  }
  .row-style{
    /deep/.el-form-item__content{
      display: flex;
      flex-direction: row;
      width: 250px;
    }
    /deep/ .el-form-item--mini.el-form-item{
      margin-bottom: 10px;
    }
    /deep/ .el-form-item__label{
      font-size: 12px;
    }
  }
  .el-pagination{
    float: right;
  }
  span{
    font-size: 12px;
  }
  .span-number{
    color: #0a7be0;
  }
  .table-count{
    color: #bfbfbf;
  }
  /deep/ .el-input-group__append, .el-input-group__prepend {
    border-color: #409EFF !important;
    color: #fff !important;
    background-color: #409EFF !important;
  }
  /deep/ .elx-table--header,/deep/ .elx-table--body,/deep/ .body--wrapper{
    background-color: #171c22;
    color: #bfbfbf;
  }
  /deep/ .elx-header--column{
    background: #222933;
  }
 /deep/ .elx-table--header-border-line{
   border-bottom: 1px solid #293241!important;
 }

 /deep/.elx-table.border--full {
   .elx-body--column,.elx-header--column {
     background-image: linear-gradient(#293241,#293241),linear-gradient(#293241,#293241);
   }
 }
 /deep/.elx-table .elx-table--border-line{
   border: 1px solid #293241;
 }
 /deep/.elx-table{
   .elx-body--row.row--checked,.elx-body--row.row--current,.elx-body--row.row--radio,.elx-body--row.row--hover{
     background-color: #222933;
   }
 }
 /deep/ .el-pagination button:disabled{
  background-color: #393b4a;
  color: #55575c;
 }
 /deep/ .el-pagination .btn-next, .el-pagination .btn-prev{
  background: center center no-repeat #393b4a !important;
  color: #b3b4b7;
 }
 /deep/.el-pagination .btn-prev{
  background: center center no-repeat #393b4a !important;
  color: #b3b4b7;
 }
 /deep/ .el-pager li{
  background-color: #393b4a;
  border: 1px solid #393b4a;
 }
 /deep/ .el-pagination{
  color: #b3b4b7;
 }
 /deep/.el-pager li.btn-quicknext, .el-pager li.btn-quickprev{
  color: #b3b4b7;
 }
</style>
