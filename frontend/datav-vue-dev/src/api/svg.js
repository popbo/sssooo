import { url } from '@/config'
import request from '../axios'

export function getSvgList(type) {
  return request({
    url: url + '/oss/file/' + type + '/svg',
    method: 'get',
  })
}

export function deleteSvg(type, id) {
  return request({
    url: url + '/oss/file/' + type + '/svg',
    method: 'delete',
    data: {
      id,
    },
  })
}
