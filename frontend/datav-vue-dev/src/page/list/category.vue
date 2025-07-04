<template>
  <div class="category">
    <avue-crud
      :option="option"
      v-model="form"
      :page.sync="page"
      @on-load="getList"
      @row-save="rowSave"
      @row-update="rowUpdate"
      @row-del="rowDel"
      :before-open="beforeOpen"
      :data="data"
    ></avue-crud>
  </div>
</template>

<script>
import { getListPage, getObj, addObj, delObj, updateObj } from '@/api/category'
export default {
  data() {
    return {
      form: {},
      data: [],
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      option: {
        index: true,
        align: 'center',
        headerAlign: 'center',
        column: [
          {
            label: '模块名',
            prop: 'categoryKey',
            rules: [
              {
                required: true,
                message: '请输入模块名',
                trigger: 'blur',
              },
              {
                min: 1,
                max: 12,
                message: '模块名长度不能超过12个字符',
                trigger: 'blur',
              },
            ],
          },
          // {
          //   label: '模块值',
          //   prop: 'categoryValue',
          //   rules: [{
          //     required: true,
          //     message: "请输入模块值",
          //     trigger: "blur"
          //   }]
          // }
        ],
      },
    }
  },
  created() {
    // this.getList()
    this.appendFilterStyle()
  },
  methods: {
    vaildData(id) {
      return [0, 1, 2].includes(id)
    },
    beforeOpen(done, type) {
      if (type == 'edit') {
        getObj(this.form.id).then(res => {
          const data = res.data.data
          this.form = data
          done()
        })
      } else {
        done()
      }
    },
    rowDel(row, index) {
      if (this.vaildData(index) && this.$website.isDemo) {
        this.$message.error(this.$website.isDemoTip)
        return false
      }
      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          delObj(row.id).then(res => {
            if (res.data.success) {
              this.$message.success('删除成功')
              this.getList()
            } else {
              this.$message.error(res.data.msg)
            }
          })
        })
        .catch(() => {})
    },
    rowUpdate(row, index, done) {
      if (this.vaildData(index) && this.$website.isDemo) {
        done()
        this.$message.error(this.$website.isDemoTip)
        return false
      }
      updateObj(row).then(() => {
        done()
        this.$message.success('修改成功')
        this.getList()
      })
    },
    rowSave(row, done) {
      addObj(row).then(res => {
        console.log('新增大屏结果', res)
        if (res.status === 200) {
          this.$message.success('新增成功')
          this.getList()
          done()
        } else {
          this.$message.error('新增失败')
          done()
        }
      })
    },
    getList() {
      getListPage({
        current: this.page.currentPage,
        size: this.page.pageSize,
      }).then(res => {
        const data = res.data.data
        this.page.total = data.total
        this.data = data.records
      })
    },
    // 将过滤选择框的背景颜色改为黑色主题
    appendFilterStyle() {
      const styleText = `
      .el-table-filter {
      background-color: #20202b !important;
      border: 1px solid rgba(255, 255, 255, 0.3);
    }
    .el-table-filter__bottom {
      border-top: 1px solid rgba(255, 255, 255, 0.3);
    }`
      const styleTag = document.createElement('style')
      styleTag.type = 'text/css'
      styleTag.innerHTML = styleText
      document.getElementsByTagName('head')[0].appendChild(styleTag)
    },
  },
}
</script>

<style lang="scss" scoped>
.category {
  padding-top: 20px;
  height: 100%;
  overflow-y: auto;
}
/deep/.avue-crud .el-table th {
  border: 1px solid #293241;
}
/deep/.el-table__body {
  border: 1px solid #293241;
  border-right: none;
}
/deep/.el-table tr {
  border: 1px solid #293241;
}
/deep/.avue-crud .el-table td {
  border-bottom: 1px solid #293241;
  border-right: 1px solid #293241;
}
// /deep/.avue-crud .el-table th.is-leaf{
//   border-right: 1px solid #293241;

// }
/deep/ .avue-crud__header {
  background-color: transparent;
}
/deep/ .avue-crud__refreshBtn {
  display: none;
}
</style>
