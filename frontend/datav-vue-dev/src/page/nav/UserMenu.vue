<template>
  <div class="user-container">
    <el-dropdown trigger="click" @command="handleCommand" placement="bottom">
      <div class="user_info">
        <svg-icon icon-class="user-circle" class="user_icon"></svg-icon>
        <div class="account">
          <span class="username">{{ userInfo }}</span>
        </div>
      </div>

      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item command="logout">退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
export default {
  computed: {
    userInfo(){
      return localStorage.getItem('userInfo')
    },
    authority () {
      return this.userType[this.$store.getters.userInfo.authority]
    }
  },
  methods: {
    async handleCommand (command) {
      if (command === 'logout') {
        localStorage.removeItem('dataset-tree')
        this.$router.push({ path: '/login' })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.user-container {
  height: 56px;
  float: right;
  padding-right:30px;
  color: #fff;
  cursor: pointer;
  .user_box, .account {
    float: left;
  }
  .el-dropdown {
    height: 100%;
    // width: 40px;
    display: flex;
    align-items: center;
    .user_info{
      width: 100%;
      .user_icon{
        width: 24px;
        height: 24px;
        margin-right: 4px;
        float: left;
        margin-top: 18px;
      }
    }
  }
  .account {
    margin-left: 4px;
    .username, .user-author {
      float: left;
      width: 100%;
      line-height: 1;
      font-size: 16px;
      color: #e5f4ff;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    .username {
      // margin: 9px 0 9px 0;
      line-height: 60px;
    }
  }
}
.aboutProject {
  /deep/ .icloud-dialog__body {
    padding: 30px 30px 10px 30px !important;
    .el-form-item {
      &:nth-child(1), &:nth-child(2) {
        .el-form-item__content {
          font-size: 20px;
        }
      }
      &:nth-child(2) {
        padding-bottom: 40px;
        font-weight: bolder;
      }
    }
  }
}
</style>
