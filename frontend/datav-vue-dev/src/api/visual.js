import {
  url
} from '@/config';
import {
  config
} from '@/option/config'
import request from '../axios'
import baseConfig from '@/config'
export const getCustomImg = (data) => request({
  url: url + `/oss/file/custom/component/current-component/img`,
  method: 'get',
  params: data
});
export const getCustomFile = (data) => request({
  url: url + `/oss/file/custom/component/current-component/file`,
  method: 'get',
  params: data
});
export const getRemoveVersion = (versionId) => request({
  url: url + `/visual/remove/version`,
  method: 'post',
  params: {
    versionId:versionId
  }
});
export const getEventAdd = (data) => request({
  url: url + `/visual/event/add`,
  method: 'post',
  data: data
});
export const getCurrentImg = (data) => request({
  url: url + `/oss/file/custom/component/currentImg`,
  method: 'get',
  params: data
});
export const getDelCurrentImg = (data) => request({
  url: url + `/oss/file/custom/component/del/currentImg`,
  method: 'post',
  params: data
});
export const getUpdateVersion = (data) => request({
  url: url + `/visual/update/version`,
  method: 'post',
  data: data
});
export const getProjectTemplate = (configId) => request({
  url: url + `/visual/project/template/${configId}`,
  method: 'get',
});
export const getImgNames = (data) => request({
  url: url + `/visual/project/get/img-names`,
  method: 'get',
  params: data
});
export const getProjectEditType = (data) => request({
  url: url + `/visual/project/editType`,
  method: 'get',
  params: data
});

export const getProjecTarget = (data) => request({
  url: url + `/visual/project/target`,
  method: 'get',
  params: data
});

export const getProjecConfig = (data) => request({
  url: url + `/visual/project/config`,
  method: 'get',
  params: data
});

export const getProjecReplaceAll = (data) => request({
  url: url + `/visual/project/replaceAll`,
  method: 'post',
  data: data
});

export const getProjeQueryAll = (data) => request({
  url: url + `/visual/project/query/replace`,
  method: 'post',
  data: data
});

export const getList = (params) => request({
  url: url + '/visual/list',
  method: 'get',
  params: params
});



export const copyObj = (id) => request({
  url: url + '/visual/copy',
  method: 'post',
  params: {
    id: id
  }
});

export const getCategory = (params) => request({
  url: url + '/category/list',
  method: 'get',
  params: params
});

export const getObj = (id) => request({
  url: url + '/visual/detail',
  method: 'get',
  params: {
    id
  }
});
export const getExportJson = (id) => request({
  url: url + '/visual/export',
  method: 'get',
  params: {
    id
  }
});
export const getExportFull = (id) => request({
  url: url + '/visual/full/export',
  method: 'get',
  responseType: 'blob',
  headers:{ 'Content-Type': 'application/json; application/octet-stream'},
  params: {
    id
  }
});
export const viewGetObj = (id) => request({
  url: url + '/visual/share/detail',
  method: 'post',
  headers: {
    'data': id.replaceAll(baseConfig.SHARESECRETKEY, '/'),
    'Content-Type': 'application/json'
  },
  data: id.replaceAll(baseConfig.SHARESECRETKEY, '/')
});
// 添加预览计时接口，点击预览按钮时，告诉后台预览开始
export const startView = (id) => request({
  url: url + '/visual/view-init/' + id,
  method: 'get',
});
// 查询是否预览页面已经超时
export const queryIsOverTime = (id) => request({
  url: url + '/visual/view-get/' + id,
  method: 'get',
});
export const shareGetObj = (path) => request({
  url: url + '/release/detail',
  method: 'get',
  params: {
    path
  }
});
export const uploadImg = (file) => request({
  url: url + '/visual/put-file',
  method: 'post',
  data: 'file=' + file,
  headers: {
    "Content-Type": "multipart/form-data"
  }
});

export const addObj = (data) => request({
  url: url + '/visual/save',
  method: 'post',
  data:data
});

export const updateComponent = (data) => request({
  url: url + '/visual/update',
  method: 'post',
  data: data
});

export const updateObj = (data) => request({
  url: url + '/visual/update',
  method: 'post',
  data: {
    "visual": {
      "id": data.id,
      "editPassword": data.editPassword,
      "category": data.category,
      "backgroundType": data.backgroundType,
      "title": data.title,
      "backgroundId": data.backgroundId
    },
    config:{
      version:data.version,
      versionRemark:data.versionRemark
    }
  }
});

export const delObj = (id) => request({
  url: url + '/visual/remove',
  method: 'post',
  params: {
    ids: id
  }
});
export const exportObj = (ids) => request({
  url: url + '/visual/batch/export',
  method: 'get',
  params: {
    ids
  }
});
export const lightExportObj = (ids) => request({
  url: url + '/visual/light-weight/export',
  method: 'get',
  params: {
    ids
  }
});
export const uploadCss = (name, style) => request({
  url: url + '/custom-cache/kv',
  method: 'post',
  data: {
    key: name,
    value: style
  }
});
export const obtainCss = (name) => request({
  url: url + '/custom-cache/kv',
  method: 'get',
  params: {
    key: name,
  }
});
export const importAddVersionObj = (data) => request({
  url: url + '/visual/import',
  method: 'post',
  data
});
export const importFull = (data) => request({
  url: url + '/visual/full/import/'+data.category+'/'+data.visualName,
  method: 'post',
  headers: {
    'Content-Type': 'multipart/form-data'
  },
  data:data['file']
});
export const importAddObj = (data) => request({
  url: url + '/visual/save',
  method: 'post',
  data
});
export const templateUpdateObj = (data) => request({
  url: url + '/visual/update',
  method: 'post',
  data
});
export const AddNewVersion = (data) => request({
  url: url + '/visual/save/version',
  method: 'post',
  data
});
export const getVersionData = (id,version) => request({
  url: url + '/visual/detail/'+version,
  params:{
    id
  },
  headers: { 'Content-Type': 'application/json;charset=UTF-8' }
});
//获取当前大屏下的分类
export const getCurrentCate = (id) => request({
  url: url + '/visual/pageParamsList/'+ id,
  method: 'get',
});
//获取当前大屏下的版本
export const getCurrentVersion = (id) => request({
  url: url + '/visual/query-version/' + id,
  method: 'post',
});
export const getExcel = (data) => request({
  url: url + '/oss/level2/excel/export',
  method: 'post',
  data:data
});