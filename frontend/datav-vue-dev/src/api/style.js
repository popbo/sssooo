import { url } from "@/config";
import request from "../axios";
export const getFontCssUrl = function () {
  return request({
    url: url + '/oss/file/get/font-css',
    method: 'get'
  })
}