import Vue from 'vue'
// import ElementUI from "element-ui";
import Avue from '@smallwei/avue'
import '@smallwei/avue/lib/index.css'
import axios from './axios'
import dataV from '@jiaminghi/data-view'
import filter from '@/filter/filter'
import router from './router.js'
import App from './App.vue'
// import "./styles/common.scss";
// import "@/utils/es6";
import '@/utils/es6.min'
import '@/mock/'
import store from '@/store'
// 处理svg
import '@/icons'
import '@/newIcon/iconfont.css'
//导入主题文件
import '@/theme/index.js'
// 若是没有开启Devtools工具，在开发环境中开启，在生产环境中关闭
// 引入自定义指令文件
import directives from './directive'
import '@/styles/front.css'
import UmyUi from 'umy-ui'
import 'umy-ui/lib/theme-chalk/index.css' // 引入样式
// umy-ui的样式会对全局样式产生影响，这项目写的有问题，明明已经在vue.config.js里面写了无论在生产环境还是开发环境忽略对element的打包
// 还在这里引入element的引入并且这个通过npm下载的element的版本和public\index.html里面引入的还不是一个版本
// 这里引入一下element的样式是因为不再次引入一遍"umy-ui/lib/theme-chalk/index.css"会覆盖public\index.html里面引入的element的css
import 'element-ui/lib/theme-chalk/index.css'
import './styles/common.scss'
import highcharts from 'highcharts'
import highcharts3d from 'highcharts/highcharts-3d'
import ElementResizeDetectorMaker from 'element-resize-detector'
import * as echarts from 'echarts';
Vue.prototype.$echarts = echarts
Vue.prototype.$erd = ElementResizeDetectorMaker()

Vue.prototype.$setSessionItem = function (key, newVal) {
  // 创建 StorageEvent 事件
  let newStorageEvent = document.createEvent("StorageEvent");
  const storage = {
    setItem: function (k, val) {
      window.sessionStorage.setItem(k, val);

      // 初始化 StorageEvent 事件
      newStorageEvent.initStorageEvent(
        "setItem", // 事件别名
        false,
        false,
        k,
        null,
        val,
        null,
        null
      );

      // 派发事件
      window.dispatchEvent(newStorageEvent);
    },
  };
  return storage.setItem(key, newVal);
};
Vue.prototype.$loadJsCss = function(type,url){
  return new Promise((resolve,reject)=>{
    let load = null
    if(type==='js'){
      load = document.createElement('script');
      load.type = "text/javascript";
      load.src= url;
      document.body.appendChild(load);
      load.onload = ()=>{
        resolve('加载成功');
      }
      load.onerror = ()=>{
        reject('加载失败');
      }
    } 
    if(type==='css'){
      load = document.createElement('link');
      load.href = url;
      load.rel = 'stylesheet';
      load.type = 'text/css';
      document.head.appendChild(load);
      load.onload = ()=>{
        resolve('加载成功');
      }
      load.onerror = ()=>{
        reject('加载失败');
      }
    }
  })
}
highcharts3d(highcharts)
Vue.use(UmyUi)
// import message from './utils/message'
import message from './utils/message.min.js'
Vue.use(message)
if (process.env.NODE_ENV == 'development') {
  Vue.config.devtools = true
} else {
  Vue.config.devtools = false
}
Vue.config.productionTip = false
window.axios = axios
Vue.use(Avue, {
  size: 'mini',
})
Vue.use(directives)
Vue.prototype.$website = window.$website
// Vue.use(ElementUI);
Vue.use(dataV)
// Vue.use(Vuex)
Vue.use(filter)
new Vue({
  router,
  store,
  render: h => h(App),
  beforeCreate() {
    Vue.prototype.$bus = this
    window.$busNew = this
  },
}).$mount('#app')
