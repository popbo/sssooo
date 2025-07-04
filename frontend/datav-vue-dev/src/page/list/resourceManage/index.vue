<template>
  <el-tabs class="resource_main" v-model="activeName">
    <!-- <el-tab-pane label="主题管理" name="first">主题管理</el-tab-pane> -->
    <el-tab-pane label="字体管理" name="second">
      <FontManage></FontManage>
    </el-tab-pane>
    <el-tab-pane label="图片管理" name="third">
      <!-- <ImgList></ImgList> -->
      <imageManage></imageManage>
    </el-tab-pane>
    <el-tab-pane label="模板管理" name="four">
      <templatelist></templatelist>
    </el-tab-pane>
    <el-tab-pane label="收藏组件管理" name="five">
      <collectComponentManage></collectComponentManage>
    </el-tab-pane>
  </el-tabs>
</template>
<script>
import FontManage from './uploadfont'
// import ImgList from './imglist'
import templatelist from './templatelist'
import imageManage from './imageManage'
import collectComponentManage from './collectComponentManage'
  export default {
    components:{
      FontManage,
      // ImgList,
      templatelist,
      imageManage,
      collectComponentManage
    },
    data() {
      return {
        activeName: 'second'
      };
    },
  };
</script>
<style lang="scss" scoped>
.resource_main{
  height:100%;
  /deep/ .el-tabs__nav-wrap{
    &:after{
      background-color:#293241
    }
  }
  /deep/ .el-tabs__nav-scroll{
    padding: 0 50px;
    .el-tabs__item{
      font-size: 16px;
      color: #bfbfbf;
      height: 50px;
      line-height: 50px;
      &.is-active{
        color: #409EFF;
      }
    }
  }
  /deep/ .el-tabs__header {
    height: 50px;
  }
  /deep/ .el-tabs__content{
    height: calc(100% - 65px);
    // margin-top:30px;
    padding: 0 25px;
  }
}
/deep/ .el-tab-pane{
  height: 100%;
  color: #bfbfbf;
}
</style>