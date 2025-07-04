<template>
  <div class="logEdit">
    <div class="logEdit-text">
      <div class="heard">
        <div class="log-name">日志内容</div>
        <div class="log-close" @click="closeLog">
          <i class="el-icon-close"></i>
        </div>
      </div>
      <div class="paramJSON_edit_box">
        <monaco-editor
          v-model="value"
          :height="365"
          ref="editor"
        ></monaco-editor>
      </div>
      <div class="foot">
        <el-button size="small" type="primary" @click="handleOk">确定</el-button>
      </div>
    </div>
  </div>
</template>
<script>
import MonacoEditor from '@/page/components/editor'
export default {
  name:'logEdit',
  data(){
    return {
      value:''
    }
  },
  props: {
    evnetData: {
      type: Object,
      default: ()=>{
        return {}
      }
    }
  },
  components:{
    MonacoEditor
  },
  created(){
    this.value = this.evnetData.logContent
  },
  methods:{
    closeLog(){
      this.$emit('closeLog')
    },
    handleOk(){
      this.$emit('OkLog', this.value)
    }
  }
}
</script>
<style lang="scss" scoped>
.logEdit{
  position: fixed;
  top: 0px;
  left: 0px;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  z-index: 10000;
  .logEdit-text{
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%,-50%);
    width: 700px;
    height: 500px;
    background: #232630;
    .heard{
      height: 45px;
      border-bottom: 1px solid #363841;
      display: flex;
      justify-content: space-between;
      .log-name{
        padding-left: 20px;
        font-size: 16px;
        color: #fff;
        line-height: 45px;
      }
      .log-close{
        font-size: 16px;
        line-height: 45px;
        color: #fff;
        padding-right: 20px;
        cursor: pointer;
      }
    }
    .paramJSON_edit_box{
      width: 100%;
      padding: 0 10px 20px;
      margin-top: 20px;
    }
    .foot{
      padding-right: 15px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>