import { url } from '@/config'
// import crypto from "@/utils/crypto";
import crypto from '@/utils/crypto.min.js'
import request from '../axios'
export const getDataSourceTypeList = function () {
  return request({
    url: url + '/db/db-type',
    method: 'get',
  })
}
// 添加数据源
export const addDBsource = function (data) {
  return request({
    url: url + '/db/save',
    method: 'post',
    data,
  })
}
// 更新数据源
export const updataDBsource = function (data) {
  return request({
    url: url + '/db/update',
    method: 'post',
    data,
  })
}
// 获取数据源列表
export const getDBsourceList = function () {
  return request({
    url: url + '/datasource/list',
    method: 'get',
  })
}
// 删除数据源
export function delDs(id) {
  return request({
    url: url + '/datasource/delete/' + id,
    loading: true,
    method: 'post',
  })
}
// 校验数据源
export const validateDBsource = function (data) {
  return request({
    url: url + '/db/db-test',
    method: 'post',
    headers: {
      data: crypto.encrypt(JSON.stringify(data)),
      'Content-Type': 'application/json',
    },
    data: crypto.encrypt(JSON.stringify(data)),
  })
}

export function addDs(data) {
  return request({
    url: url + '/datasource/add/',
    method: 'post',
    loading: true,
    headers: {
      data: crypto.encrypt(JSON.stringify(data)),
      'Content-Type': 'application/json',
    },
    data: crypto.encrypt(JSON.stringify(data)),
  })
}

export function editDs(data) {
  return request({
    url: url + '/datasource/update/',
    method: 'post',
    loading: true,
    headers: {
      data: crypto.encrypt(JSON.stringify(data)),
      'Content-Type': 'application/json',
    },
    data: crypto.encrypt(JSON.stringify(data)),
  })
}

export function getSchema(data) {
  return request({
    url: url + '/datasource/getSchema/',
    method: 'post',
    loading: true,
    data,
  })
}
export function validateDs(data) {
  return request({
    url: url + '/datasource/validate/',
    method: 'post',
    loading: true,
    data,
  })
}

export function validateDsById(datasourceId) {
  return request({
    url: url + '/datasource/validate/' + datasourceId,
    method: 'get',
    loading: true,
  })
}

//获取数据库驱动列表
export function getDrivesList() {
  return request({
    url: url + '/datasource/drivers',
    method: 'get',
  })
}
//通过数据源驱动id获取数据库驱动详情
export function getDrivesDetails(id) {
  return request({
    url: url + `/datasource/driver/query/byid/${id}`,
    method: 'get',
  })
}
//通过驱动类型获取数据库驱动列表
export function getDrivesDetailsByType(type) {
  return request({
    url: url + `/datasource/driver/query/bytype/${type}`,
    method: 'get',
  })
}
// 上传驱动
export function upDrive(data) {
  return request({
    url: url + '/datasource/driver/upload',
    method: 'post',
    loading: true,
    data,
  })
}
// 保存驱动
export function saveDrive(data) {
  return request({
    url: url + '/datasource/driver/save',
    method: 'put',
    loading: true,
    data,
  })
}
//更新驱动
export function updateDrive(data) {
  return request({
    url: url + '/datasource/driver/update',
    method: 'post',
    loading: true,
    data,
  })
}
//删除驱动
export function removeDrive(data) {
  return request({
    url: url + '/datasource/driver/del',
    method: 'delete',
    loading: true,
    data,
  })
}
//删除驱动jar
export function removeDriveJar(data) {
  return request({
    url: url + '/datasource/driver/del',
    method: 'post',
    loading: true,
    data: {
      jarPath: data,
    },
  })
}
