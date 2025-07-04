<template>
  <div class="login-container">
    <div class="logo">
      <img src="~@/assets/logo_yw.png">
    </div>
    <div class="login-bg">
      <el-form :model="form" :rules="rules" ref="form" class="login-form" label-position="left" label-width="80px">
        <div class="project-title">可视化平台</div>
        <el-form-item class="login-form-username" label="用户名" prop="username">
          <el-input placeholder="请输入用户名" v-model="form.username" name="username" @keyup.enter.native="login" auto-complete="on"></el-input>
        </el-form-item>
        <el-form-item class="login-form-pwd" label="密码" prop="password">
          <el-input type="password" auto-complete="new-password" placeholder="请输入密码" v-model="form.password" @keyup.enter.native="login"></el-input>
          <!-- <a href="javascript:void(0);" class="forget-pwd" @click="$router.push({ path: '/login/resetPasswordRequest' })">忘记密码?</a> -->
        </el-form-item>
        <el-form-item class="login-form-btn">
          <el-button type="primary" :loading="loading" @click="login" >登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { getUser } from '@/api/login'
import { mapMutations } from 'vuex'
import {
  Message
} from 'element-ui'
export default {
  data() {
    return {
      form: {
        username: '',
        password: '',
      },
      rules: {
        username: { required: true, message: '用户名不能为空', trigger: 'change' },
        password: { required: true, message: '密码不能为空', trigger: 'change' },
      },
      loading: false,
      userToken: '',
    }
  },
  methods: {
    ...mapMutations(['changeLogin', 'userInfo']),
    login() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) return false
        this.loading = true
        getUser(this.form).then((res) => {
          if (res.data.success) {
            this.userToken = res.data.data.token
            this.changeLogin({ Authorization: this.userToken })
            this.userInfo({ username: this.form.username })
            console.log('hhhh', this.$route.query.redirect)
            this.$router.push(this.$route.query.redirect  || '/')
            this.loading = false
          } else {
            this.loading = false
            this.$message.error(res.data.msg)
          }
        }).catch(()=>{
          this.loading = false
        })
        
      })
    },
    closeDialog() {
      this.$store.commit('logout')
    },
  },
  watch:{
    $route:{
      handler(val) {
        if(val.params.redirect==='loginAgain'){
          Message({
            message:'请重新登录',
            type:'error'
          })
        }
      },
      deep:true,
      immediate:true
    },
  },
  created() {
    this.closeDialog()
  },
}
</script>

<style scoped lang='scss'>
.login-container {
  width: 100%;
  height: 100%;
  position: relative;
  background: url('~@/assets/login_bg_all.png');
  .logo {
    position: absolute;
    left: 30px;
    top: 20px;
    width: 161px;
    height: 40px;
    img {
      width: 100%;
    }
  }
  .login-bg {
    position: absolute;
    width: 1600px;
    height: 764px;
    background-color: #ffffff;
    box-shadow: 0px 0px 44px 0px rgba(0, 0, 0, 0.09);
    border-radius: 40px;
    left: 160px;
    top: 87px;
    background-image: url('~@/assets/login_bg_middle.png');
    background-repeat: no-repeat;
    .login-form {
      position: absolute;
      left: 1108px;
      top: 169px;
      width: 383px;
      height: 405px;
      .project-title {
        width: 250px;
        height: 48px;
        line-height: 48px;
        font-family: SourceHanSansCN-Bold;
        font-size: 48px;
        font-weight: normal;
        margin-bottom: 73px;
      }
      /deep/ .el-form-item {
        margin-bottom: 29px;
        .el-form-item__label {
          display: block;
          width: 100% !important;
          height: 18px;
          line-height: 18px;
          margin-bottom: 10px;
          padding-left: 0;
          font-family: SourceHanSansCN-Regular;
          font-size: 18px;
          color: #333333;
          &::before {
            content: '';
          }
        }
        .el-form-item__content {
          height: 100%;
          width: 100%;
          margin-left: 0 !important;
          padding-right: 0;
        }
        .el-input__inner {
          height: 52px;
          background-color: #f8fafb !important;
          color: #1c1c1c !important;
          font-size: 18px;
          border: 1px solid #d8d8d8 !important;
          border-radius: 4px;
        }
        input::-webkit-input-placeholder {
          color: #888888;
        }
        input::-moz-input-placeholder {
          color: #888888;
        }
        input::-ms-input-placeholder {
          color: #888888;
        }
        .forget-pwd {
          font-size: 16px;
          font-family: PingFangSC-Medium;
          color: #939393;
          line-height: 22px;
          float: right;
          margin-top: 14px;
        }
        &.login-form-btn {
          height: 52px;
          margin-top: 45px;
          .el-button {
            height: 100%;
            width: 100%;
            border-color: #6993ff;
            background-color: #409eff;
            border-radius: 4px;
            padding: 11px 90px;
            span {
              color: #fff;
              font-family: SourceHanSansCN-Regular;
              font-size: 24px;
            }
          }
        }
      }
    }
  }
}
</style>
