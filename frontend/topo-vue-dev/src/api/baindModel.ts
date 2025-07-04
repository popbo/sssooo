import http from "@/http";

// 获取树形数据
function getTreeDate(url){
    return http({
        url,
    })
}

// http://rap2api.taobao.org/app/mock/311085/api/device/data
// 获取查询数据
function getSeachDate(){
    return http({
        url:'/device/data',
    })
}
function getSeachDate2(){
    return http({
        url:'/device/data',
    })
}

export {
    getTreeDate,
    getSeachDate,
    getSeachDate2
}