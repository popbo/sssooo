import store  from '@/store/index'
import { Meta2d, Event, EventAction, Pen ,BindId} from '@topometa2d/core';
declare const meta2d: Meta2d;
let   httpTimer: any;
let httpTimerList:any[]=[]
const token =localStorage.getItem('token')
// 多个http并行请求
const connectHttpfix=(https)=>{
// 关闭之前的连接todo
  closeHttpFix()
// 获取多个配值对象
  if(https){
   https.forEach((item, index) => {
        if (item.http) {
          httpTimerList[index] = setInterval(async () => {
            // 默认每一秒请求一次
            const res: Response = await fetch(item.http, {
              headers: item.httpHeaders,
            });
            if (res.ok) {
              const data = await res.text();

              //8.14 如有响应数据判断是否含有function且不为空
              let ret:any=data
              if(item.responseData && isFunction(item.responseData)){
                try {
                  // 8.14当responseData处理有结果就取其结果,没有取后者
                  ret = funEval(item.responseData)(data)||data
                } catch (error) {
                  console.log('数据格式有误', error);
                  return
                }
              }

              socketCallback(ret,{
                name:'http',
                id:item.id
              });
            }
          }, item.httpTimeInterval || 1000);
        }
      });
  }
}
// 8.14构造响应数据处理函数  
const funEval = (a) => new Function("return " + a + ";")()
// 8.14检验字符串中是否又function 关键字
const isFunction = (f)=>{
    try{
      return /^\s*\bfunction\b/.test(f);
    }catch(x){
      return false;
    }
}

const socketCallback=(message: string|object, obj,topic = '')=>{
    // this.store.emitter.emit('socket', { message, topic });
    // 把数据存到vuex中，而且是以键值对的方式
    if(obj.name==='http') { 
        let data: any;
      if (message.constructor === Object || message.constructor === Array) {
        data = message;
      } else if (typeof message === 'string') {
        try {
        data = JSON.parse(message);
      } catch (error) {
        console.warn('Invalid socket data:', data, error);
      }
      } else {
        return;
      }
      if (!data) {
        return;
      }
      if (!Array.isArray(data)) {
        data = [data];
      }
      let arr=[]
      if(data[0].children) {
        arr=extractLeavesWithData(data)  //处理data剥壳
      }else{
        arr=[...data]
      }
      // 存入vuex中
       store.commit('souceDate/setHttp',{
        id:obj.id,
        data:[...arr],
       })      

    }
    // TODO 2、如果是rtbd 把数据保存到vuex的RealTimeDataBases中
    // 把数据存到vuex中，而且是以键值对的方式
    if(obj.name==='RealTimeDataBase') { 
        let data: any;
      if (message.constructor === Object || message.constructor === Array) {
        data = message;
      } else if (typeof message === 'string') {
        try {
        data = JSON.parse(message);
      } catch (error) {
        console.warn('Invalid socket data:', data, error);
      }
      } else {
        return;
      }
      if (!data) {
        return;
      }
      if (!Array.isArray(data)) {
        data = [data];
      }
      // 存入vuex中
       store.commit('souceDate/setRealTimeDataBase',{
        id:obj.id,
        data
       })      

    }
    if (obj.name === 'TDengine') {
       let data: any;
       if (message.constructor === Object || message.constructor === Array) {
         data = message;
       } else if (typeof message === 'string') {
         try {
           data = JSON.parse(message);
         } catch (error) {
           console.warn('Invalid socket data:', data, error);
         }
       } else {
         return;
       }
       if (!data) {
         return;
       }
       if (!Array.isArray(data)) {
         data = [data];
       }
       let arr = [];
       if (data[0].children) {
         arr = extractLeavesWithData(data); //处理data剥壳
       } else {
         arr = [...data];
       }
       // 存入vuex中
       store.commit('souceDate/setTDengine', {
         id: obj.id,
         data: [...arr],
       });
    }
}

  // 处理data 获取所有有value值得子叶--》生成数组[{id:xxx,value:xxxx}]
  function extractLeavesWithData(tree) {
    let leaves = [];
    function traverse(node) {
      if (node.children && node.children.length > 0) {
        for (let child of node.children) {
          traverse(child);
        }
      } else {
        if (node.hasOwnProperty('value')) {
          leaves.push({ dataId: node.id, value: node.value });
        }
      }
    }
    for (let node of tree) {
      traverse(node);
    }
    return leaves;
  }

const closeHttpFix=()=>{
   clearInterval(httpTimer);
    httpTimer = undefined;
    httpTimerList &&
    httpTimerList.forEach((_httpTimer) => {
        clearInterval(_httpTimer);
        _httpTimer = undefined;
      });
}

let RealTimeDataBasesTimer: any;
let RealTimeDataBasesTimerList:any[]=[]
// TODO 1、多个realTime同时发送请求建立连接
const connectRealTimeDataBasefix=  (RealTimeDataBases)=>{
// 关闭之前的连接todo
  closeRealTimeDataBasesFix()
// 获取多个配值对象
  if(RealTimeDataBases){
   RealTimeDataBases.forEach( async (item, index) => {
        // 如果存在RealTimeDataBase,post,username,password,EquipmentMasterTable不为空，后续更精确判断这个ip就去发送请求
        if (item.RealTimeDataBase&&item.post&&item.username&&item.password&&item.EquipmentMasterTable) {
          const res: Response = await fetch(`/api/rtdb/connect/server?serverIp=${item.RealTimeDataBase}&serverPort=${item.post}&serverUser=${item.username}&serverPwd=${item.password}&devMainTable=${item.EquipmentMasterTable}&devSubTable=${item.EquipmentSubTable}`);
          const data = await res.text();
          RealTimeDataBasesTimerList[index] = setInterval(async () => {
            if (JSON.parse(data).success) {
            // 默认每3秒请求一次
             const _res: Response = await fetch(`/api/rtdb/device/data?devMainTable=${item.EquipmentMasterTable}&devSubTable=${item.EquipmentSubTable}`)
            if(_res.ok){
             const _data = await _res.text();
              // 数据处理部分
              let ret:any=_data
              if (item.responseData && isFunction(item.responseData)) {
                try {
                  ret =funEval(item.responseData)(_data)||_data
                } catch (error) {
                  console.log('数据格式有误', error);
                  return;
                }
              }
              socketCallback(ret, {
                name: 'RealTimeDataBase',
                id: item.id,
             });
            }
           }
          },item.realTimeInterval || 3000);
        }
      });
  }
}

const closeRealTimeDataBasesFix=()=>{
   clearInterval(RealTimeDataBasesTimer);
    RealTimeDataBasesTimer = undefined;
    RealTimeDataBasesTimerList &&
    RealTimeDataBasesTimerList.forEach((_RealTimeDataBasesTimer) => {
        clearInterval(_RealTimeDataBasesTimer);
        _RealTimeDataBasesTimer = undefined;
      });
}

let TDenginesTimer: any;
let TDengineTimerList: any[] = [];
// TODO 1、多个TDengines同时发送请求建立连接
const connectTDenginesfix = (TDengines) => {
  // 关闭之前的连接todo
  closeTDenginesFix();
  // 获取多个配值对象
  if (TDengines) {
    TDengines.forEach(async (item, index) => {
      // 如果存在RealTimeDataBase,post,username,password,EquipmentMasterTable不为空，后续更精确判断这个ip就去发送请求
      if (
        item.TDengine &&
        item.post &&
        item.username &&
        item.password &&
        item.EquipmentMasterTable
      ) {
        const res: Response = await fetch(
          `/api/rtdb/tdengine/connect/server?serverAddr=${item.TDengine}&serverPort=${item.post}&serverUser=${item.username}&serverPwd=${item.password}&dbName=${item.EquipmentMasterTable}`,
          {
            headers: {
              Authorization: `${token}`,
            },
          }
        );
        const data = await res.text();
        TDengineTimerList[index] = setInterval(async () => {
          if (JSON.parse(data).success) {
            // 默认每3秒请求一次
            const _res: Response = await fetch(
              `/api/rtdb/tdengine/device/data/tree`,
              {
                headers: {
                  Authorization: `${token}`,
                },
              }
            );
            if (_res.ok) {
              const _data = await _res.text();
              // 数据处理部分
              let ret: any = _data;
              socketCallback(ret, {
                name: 'TDengine',
                id: item.id,
              });
            }
          }
        }, item.TDenginenterval || 3000);
      }
    });
  }
};
const closeTDenginesFix = () => {
  clearInterval(TDenginesTimer);
  TDenginesTimer = undefined;
  TDengineTimerList &&
    TDengineTimerList.forEach((_TDenginesTimer) => {
      clearInterval(_TDenginesTimer);
      _TDenginesTimer = undefined;
    });
};

const initBindDatasfix=()=> {
    meta2d.store.bindDatas = {};//Meta2dStore
    meta2d.store.data.pens.forEach((pen) => {
      pen.form?.forEach((formItem) => {//formItem 就是form中各项数组
        let dataIds: BindId[];
        if (formItem.dataIds) {
          if (Array.isArray(formItem.dataIds)) {
            dataIds = formItem.dataIds;
          } else {
            dataIds = [formItem.dataIds]; // 是一个数组
          }
        }
        dataIds?.forEach((item) => {
          if (!meta2d.store.bindDatas[item.dataId]) {
            meta2d.store.bindDatas[item.dataId] = [];
          }
          meta2d.store.bindDatas[item.dataId].push({
            id: pen.id,//图元的id
            formItem,//[]里面是什么dataId是什么
          });
        });
      });
    });
  }

export {
  connectHttpfix,
  initBindDatasfix,
  closeHttpFix,
  connectRealTimeDataBasefix, //实时数据库连接
  closeRealTimeDataBasesFix, //清除实时数据库连接
  connectTDenginesfix,
  closeTDenginesFix,
};