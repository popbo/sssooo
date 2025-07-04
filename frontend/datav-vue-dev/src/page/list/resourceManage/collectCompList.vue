<template>
  <div>
    <el-scrollbar class="imgList">
      <ul>
        <li v-for="(item,index) in collectConList" :key="index">
          <div class="operation_btn">
            <i class="icon el-icon-edit" @click="handleEdit(item)"></i>
            <i class="icon el-icon-delete" @click="handleDelete(item)"></i>
          </div>
          <div class="imgBox">
            <img :src="item.url"/>
          </div>
          
          <h4>{{item.name}}</h4>
        </li>
      </ul>
    </el-scrollbar>
    <!-- 编辑弹窗 -->
    <collectLayer ref="collectRef" :type="'edit'"></collectLayer>
  </div>
</template>

<script>
import { postDeleteCollect } from "@/api/collection"
import collectLayer from "@/page/group/collectLayer"
export default {
  props: ['collectConList'],
  components:{
    collectLayer
  },
  methods: {
    // 修改打开模态框
    handleEdit(row) {
      this.$refs.collectRef.openDialog(row)
    },
    // 删除
    //删除收藏列表
    handleDelete(row){
      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
      const delParam={
        ...row,
        detail:JSON.stringify(row.detail)
      }
      postDeleteCollect(delParam).then(res=>{
        if(res.data.success){
          this.$message.success('删除成功')
          this.$emit('delectColectConItem')
        }else{
          this.$message.error(res.data.msg)
        }
      })
      }).catch(() => {
      });
    }
  },
}
</script>

<style lang="scss" scoped>
.el-scrollbar {
  height: calc(100vh - 200px);
}
.imgList li {
  // height: 130px;
  width: 12.74%;
  display: inline-block;
  margin: 0 14px;
  .imgBox{
    border: 1px solid #3a475a;
    width: 100%;
    height: 110px;
    display: flex;
    justify-content: center;
    align-items: center;
    img {
      width: 110px;
      height: 86px;
      max-width: 86px;
      max-height: 86px;
      display: block;
    }
  }
  h4 {
    text-align: center;
    height: 30px;
    line-height: 30px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-weight: 500;
    color: #bfbfbf;
    margin-top: 10px;
  }
  
  &:hover {
    .operation_btn {
      opacity: 1;
    }
    .imgBox {
      border: 1px solid #409eff;
    }
  }
  .operation_btn {
    float: right;
    background: #409eff;
    height: 26px;
    line-height: 26px;
    padding: 0 10px;
    opacity: 0;
    .icon {
      font-size: 18px;
      color: #fff;
    }
    .el-icon-edit {
      margin-right: 10px;
    }
  }
}
</style>