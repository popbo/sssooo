import {
  url
} from "@/config";
import request from "../axios";

export function sqlSearch (type, data) {
  return request({
    url: url + '/comment/db/refresh/' + type,
    method: 'post',
    data
  })
}