import { url } from "@/config";
import request from "../axios";
export const getReleaseInfo = function (Id) {
  return request({
    url: url + "/release/queryByVisual/" + Id,
    method: "get",
  });
};
export const saveReleaseInfo = function (data) {
  return request({
    url: url + "/release/save",
    method: "post",
    data,
  });
};

export const closeRelease = function (data) {
  return request({
    url: url + "/release/status",
    method: "post",
    data,
  });
}
