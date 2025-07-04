import { url } from "@/config";
import request from "../axios";
export const login = function () {
  return request({
    url: url + '/api/auth/login',
    method: 'post'
  })
}