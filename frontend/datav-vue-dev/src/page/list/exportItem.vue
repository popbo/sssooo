<template>
  <el-dialog 
             title="导出大屏"
             :close-on-click-modal="false"
             :visible.sync="exportDialogShow"
             width="650px"
             @close="resetFormData">
             <div class="radio_box" style="margin-left:60px;">
              <el-radio v-model="exportChoise" label="1">导出大屏数据（适合同网络迁移）</el-radio>
              <br>
              <el-radio v-model="exportChoise" label="2">导出全部文件（适合不同网络迁移）</el-radio>
             </div>
    <span slot="footer"
          class="dialog-footer">
          <el-button
                 size="small"
                 type="danger"
                 @click="resetFormData">取消</el-button>
      <el-button @click="exportData"
                 size="small"
                 type="primary">确定</el-button>
      
    </span>

  </el-dialog>

</template>

<script>
// import MonacoEditor from '@/page/components/editor'
export default {
  components: {  },
  data () {
    return {
      exportDialogShow: false,
      exportChoise:"1",
      json: {}
    }
  },
  methods: {
    exportData () {
      this.$emit('exportData',this.exportChoise)
      this.resetFormData()
    },
    resetFormData(){
      this.exportDialogShow = false;
    }
    // exportData () {
    //   var zip = new window.JSZip();
    //   zip.file("data.txt", this.json);
    //   zip.generateAsync({ type: "base64" })
    //     .then(function (content) {
    //       location.href = "data:application/zip;base64," + content;
    //     });
    // }
  }
}
</script>

<style lang="scss" scoped>
.radio_box{
  /deep/.el-radio{
    display: block;
    //margin-bottom:15px;
  }
}
</style>