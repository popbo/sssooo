import { url } from '@/config';
import request from '../axios'
export const getUser = (params) => request({
  url: url + '/auth/user/login',
  method: 'post',
  data: params,
  isToken: true 
});
export const getUserOut = () => request({
  url: url + '/auth/user/logout',
  method: 'post',
});
// 获取所有组织
export const getallOrganizations = () => request({
  url: url + '/auth/depts',
  method: 'get',
})
// 获取所有角色
export const getAllRoles = () => request({
  url: url + '/auth/roles',
  method: 'get',
})
// 获取所有用户
export const getAllUsers = () => request({
  url: url + '/auth/users',
  method: 'get',
})
// 获取该数据集的所有字段枚举值
export const getAllEnum = (data) => request({
  url: url + '/dataset/table/enumPreview',
  method: 'post',
  data
})
// 获取某一个数据集已经添加的行权限
export const getAllRowPermissions = (data = {}) => request({
  url: url + '/dataset/permission/row/query',
  method: 'post',
  data
})
// 新增一个行权限
export const addRowPermission = (data) => request({
  url: url + '/dataset/permission/row/save',
  method: 'post',
  data
})
// 删除一个行权限
export const deleteRowPermission = (data) => request({
  url: url + '/dataset/permission/row/del',
  method: 'post',
  data
})
// 更新行权限
export const updateRowPermission = (data) => request({
  url: url + '/dataset/permission/row/update',
  method: 'post',
  data
})
// 获取某一个数据集已经添加的列权限
export const getAllColumnPermissions = (data = {}) => request({
  url: url + '/dataset/permission/column/query',
  method: 'post',
  data
})
// 新增一个列权限
export const addColumnPermission = (data) => request({
  url: url + '/dataset/permission/column/save',
  method: 'post',
  data
})
// 删除一个行权限
export const deleteColumPermission = (data) => request({
  url: url + '/dataset/permission/column/del',
  method: 'post',
  data
})
// 更新行权限
export const updateColumnPermission = (data) => request({
  url: url + '/dataset/permission/column/update',
  method: 'post',
  data
})
// cas获取token
export const casLogin = (data) => request({
  url: url + '/auth/cas/login',
  method: 'post',
  data
})
