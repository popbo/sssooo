import http from "@/http";


// 传递实时数据库参数
function postRealTimeDataBaseServer(){
    return http({
        url:'/connect/server',
    })
}

// 获取设备树数据
// http://192.168.152.134:8080/meta-server/api/rtdb/device/data/tree?devMainTable=r_s010202eq&devSubTable=r_s010202ai
async function getRealTimeDataBaseServerTreeDate(EquipmentMasterTable,EquipmentSubTable){
    // 查询该协议id下的配置  url
    const res: Response = await http.get(`/api/rtdb/device/data/tree?devMainTable=${EquipmentMasterTable}&devSubTable=${EquipmentSubTable}`);
    return res
}
// 获取设备实时数据

async function getTDengineServerTreeDate() {
  // 查询该协议id下的配置  url
  const res: Response = await http.get(`/api/rtdb/tdengine/device/data/tree`, {
    headers: {
      Authorization: `${localStorage.getItem('token')}`,
    },
  });
  return res;
}
// 下发设备控制指令
function postRealTimeDataBaseCommand(pid){
    return http({
        url:`/device/command/${pid}`,
    })
}
// 组件工程化全局同步
function globalSynchronization(data) {
  return http({
    method: 'post',
    url: `/api/data/engineering/global/synchronization`,
    data,
  });
}

export {
  postRealTimeDataBaseServer,
  postRealTimeDataBaseCommand,
  getRealTimeDataBaseServerTreeDate,
  getTDengineServerTreeDate,
  globalSynchronization,
};