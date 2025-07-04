import { url } from "@/config";
import request from "../axios";
export const getDataColList = function () {
  return request({
    url: url + '/db/model/data-collection',
    method: 'get'
  })
}
// 查询左侧树形菜单的数据集列表
export function queryAuthModel(data, loading = true, timeout = 30000) {
  return request({
    url: url + '/authModel/queryAuthModel',
    method: 'post',
    loading: loading,
    timeout: timeout,
    data
  })
}
// 添加数据集分组
export function addGroup(data) {
  return request({
    url: url +  '/dataset/group/save',
    method: 'post',
    loading: true,
    data
  })
}
// 获取数据库数据集
export function listDatasource() {
  return request({
    url:  url +  '/datasource/list',
    loading: true,
    method: 'get'
  })
}

export function post(newurl, data, showLoading = true, timeout = 20000) {
  return request({
    url: url +  newurl,
    method: 'post',
    loading: showLoading,
    timeout: timeout,
    data
  })
}

// 删除一个数据集
export function delTable(tableId) {
  return request({
    url: url + '/dataset/table/delete/' + tableId,
    loading: true,
    method: 'post'
  })
}
// 删除数据分组
export function delGroup(groupId) {
  return request({
    url: url + '/dataset/group/delete/' + groupId,
    loading: true,
    method: 'post'
  })
}
// 查询分组
export function groupTree(data) {
  return request({
    url: url + '/dataset/group/tree',
    method: 'post',
    loading: true,
    data
  })
}
// 移动数据集时
export function alter(data) {
  return request({
    url: url + '/dataset/table/alter',
    method: 'post',
    loading: true,
    data
  })
}

export function getTable(id, hideMsg = false) {
  return request({
    url: url + '/dataset/table/get/' + id,
    loading: false,
    method: 'post',
    hideMsg: hideMsg
  })
}

export function fieldList(id, showLoading = true) {
  return request({
    url: url + '/dataset/field/list/' + id,
    loading: showLoading,
    method: 'post'
  })
}
export function fieldListDQ(id, showLoading = true) {
  return request({
    url: url + '/dataset/field/listByDQ/' + id,
    loading: showLoading,
    method: 'post'
  })
}

export function getFontList(){
  return request({
    url:url+"/visual/get-otf",
    method: 'get'
  })
}

export function getFontCss(){
  return request({
    url:url+"/visual/get-css",
    method: 'get'
  })
}
export function getBgPic(){
  return request({
    url:url+"/visual/get-back",
    method: 'get'
  })
}
// 获取文件的某一类
export function getFile(source, type, params){
  return request({
    url:url+`/oss/file/page/${source}/${type}`,
    method: 'get',
    params
  })
}
// 修改文件
export function putFile(source, type, data){
  return request({
    url:url+`/oss/file/${source}/${type}`,
    method: 'put',
    data
  })
}
// 删除文件
export function delFile(source, type, data){
  return request({
    url:url+`/oss/file/${source}/${type}`,
    method: 'delete',
    data
  })
}