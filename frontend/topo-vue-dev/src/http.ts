import { message } from 'ant-design-vue';
import axios from 'axios';

import { t } from '@/i18n/i18n';
import { getCookie } from './services/cookie';
declare const window: any;

// axios 配置
axios.defaults.timeout = 15000;
axios.defaults.withCredentials = false;


// http request 拦截器
axios.interceptors.request.use(
  (config) => {
    if (
      !config.url.startsWith('/svg') &&
      !config.url.startsWith('/png') &&
      !config.url.startsWith('/img') &&
      !config.url.startsWith('/view') &&
      // !config.url.startsWith('/images') &&
      !config.url.startsWith('/image/materials') &&
      !config.url.startsWith('/2d/svg') &&
      !config.url.startsWith('/2d/png') &&
      !config.url.startsWith('/api')
    ) {
      if (!config.url.endsWith('/config.json')) {
        config.baseURL = '/api';
      } 
    }
    if (
      config.url.startsWith('/image') &&
      config.params &&
      config.params.isZip
    ) {
      config.baseURL = '';
      delete config.params.isZip;
    }
    const token = getCookie('token') || localStorage.getItem('token') || '';
    config.headers.Authorization = token;
    return config;
  },
  (err) => Promise.reject(err)
);

// http response 拦截器
axios.interceptors.response.use(
  (response) => {
    if (response && response.data && response.data.error) {
      // message.error(response.data.error,100);
      if(response.data.error!=='文件夹已经存在') {
      message.error(response.data.error,3)
    }
    }
    if (response) {
      return response.data;
    }
    return null;
  },
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          console.log("401未鉴权");
          const localSavedToken = localStorage.getItem('token');
          const localSavedUser = localStorage.getItem('username');
          if (localSavedToken && localSavedUser)  {
            //message.error(t('401未鉴权 need skip visual_url'));
            localStorage.removeItem('token');
            localStorage.removeItem('username');
            const configObj = window ? window.MyGlobalConfigObject : null;
            const visual_url = configObj ? configObj.visual_url : null;
            if (visual_url) {
              window.location.href = visual_url;
            }
          } else {
            console.warn("http.ts localStorage中username,token: ", localStorage.getItem('username'), localStorage.getItem('token'));
          }
          break;
        case 403:
          message.error(error.response.data.error);
          break;
        case 404:
          console.log("访问数据不存在，请检查后重试！");
          //message.error(t('访问数据不存在，请检查后重试！'));
          break;
        case 500:
          message.error(t('请求服务错误，请稍后重试'));
          break;
        case 504:
          message.error(t('网络超时，请检测你的网络'));
          break;
        default:
          console.log("未知网络错误" + error.response.status);
          //message.error(t('未知网络错误'));
          break;
      }
    }

    return Promise.reject(error.response ? error.response.data : error).catch((e) => {});
  }
);

export default axios;
