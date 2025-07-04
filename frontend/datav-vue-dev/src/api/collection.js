import { url } from "@/config";
import request from "../axios";

export function postCollect(data){//上传接口
  return request({
    url:url+'/visual/save/bookmark',
    method: 'post',
    data
  })
}
export function getCollectBytype(type){//根据类型获取收藏内容
  return request({
    url:url+`/visual/get/bookmarkbytype/${type}`,
  })
}
export function postEditCollect(data){//编辑接口
  return request({
    url:url+'/visual/edit/bookmark',
    method: 'post',
    data
  })
}
export function postDeleteCollect(data){//编辑接口
  return request({
    url:url+'/visual/del/bookmark',
    method: 'post',
    data
  })
}
export function getCollectType(){//获取分类
  return request({
    url:url+'/visual/get/bookmarktype/true'
  })
}
export function postAddCollectType(data){//新增收藏接口
  return request({
    url:url+'/visual/save/bookmarktype',
    method: 'post',
    data
  })
}
export function postEditCollectType(data){//修改收藏接口
  return request({
    url:url+'/visual/edit/bookmarktype',
    method: 'post',
    data
  })
}
export function postDeleteCollectType(id){//删除收藏接口
  return request({
    url:url+'/visual/del/bookmarktype/'+id,
    method: 'post'
  })
}