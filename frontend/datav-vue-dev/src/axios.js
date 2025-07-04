import { Loading } from 'element-ui';
import { validatenull } from '@/echart/util'
// import { checkUrl, getFunction } from '@/utils/utils'
import { checkUrl, getFunction } from '@/utils/utils.min.js'
// import {
//   Message
// } from 'element-ui'
import axios from 'axios'
import vueRouter from '@/router'
window.$glob = {
  url: '',
  loading:false,//是否出现加载框
  params: {},
  query: {},
  headers: {},
}
function getGlobParams() {
  var query = window.location.search.substring(1)
  query = query.split('&')
  query.forEach(ele => {
    var pair = ele.split('=')
    window.$glob.params[pair[0]] = pair[1]
  })
}

axios.defaults.timeout = 10000
//返回其他状态吗
axios.defaults.validateStatus = function (status) {
  return status >= 200 && status <= 500 // 默认的
}
//跨域请求，允许保存cookie
let loadingInstance = false;
axios.defaults.withCredentials = true
axios.interceptors.request.use(
  config => {
    if(config.loading){
      window.$glob.loading = config.loading;
      loadingInstance = Loading.service({
        text: '拼命加载中',
        background: 'rgba(0, 0, 0, 0.8)',
        spinner: 'el-icon-loading'
      });
    }
    // 判断是否有自定义的Authorization,有则使用
    if (!config.isToken && localStorage.getItem('Authorization')) {
      config.headers.Authorization =
        config.headers?.Authorization || localStorage.getItem('Authorization')
    }
    if (window.location.href.includes('share')) {
      config.headers.ReleasePath = window.location.href
        .split('share/')[1]
        .replace(/\?.*/, '') // 有可能后面会带查询参数所以要去掉
    }
    getGlobParams()
    if (!checkUrl(config.url)) {
      config.url = window.$glob.url + config.url
    }
    if (!validatenull(window.$glob.header)) {
      if(config.noVisualization){
        let header = getFunction(window.$glob.header)(config.headers)
        config.headers = Object.assign(config.headers, header)
      }
    }
    //获取全局参数
    if (typeof config.data === 'object' && !validatenull(window.$glob.query)) {
      let query = getFunction(window.$glob.query)()
      let data = Object.assign(window.$glob.params, query)
      if(config.noVisualization){
        if (config.method == 'get') {
          config.params = Object.assign(config.params, data)
        } else if (config.method == 'post') {
          config.data = Object.assign(config.data, data)
        }
      }
    }
    // 先注释掉出了问题再回来看，token
    // config.headers.Authorization = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NDI0MDY3ODIsInVzZXJJZCI6MSwidXNlcm5hbWUiOiJhZG1pbiJ9.ruy_QbdCxzYiFjxy508r-SmtDtIvobqxyrsS-LQZeLI'
    return config
  },
  error => {
    return Promise.reject(error)
  }
)
//HTTPrequest拦截
axios.interceptors.response.use(
  config => {
    if(window.$glob.loading){
      if(loadingInstance){
        loadingInstance.close()
      }
    }
    //判断是否为可视化服务接口
    if(!config.config.noVisualization){
      if (config.data.code === 401) {
        localStorage.removeItem('Authorization')
        vueRouter.replace({
          name: 'login',
          params: {
            redirect: 'loginAgain',
          },
        })
        Message({
          message:'请重新登录',
          type:'error'
        })
      }
    }
    return config
  },
  error => {
    if(window.$glob.loading){
      if(loadingInstance){
        loadingInstance.close()
      }
    }
    console.dir(error)
    return Promise.reject(new Error(error))
  }
)

// 当一些接口出现需要权限认证的时候
// const checkAuth =
export default axios
