import { computed } from 'vue';
import {
  connectHttpfix,
  connectRealTimeDataBasefix,
  closeRealTimeDataBasesFix,
  closeTDenginesFix,
  connectTDenginesfix,
} from '@/utils/soucerequest';
import Bus from '@/components/common/bus';


function pollDate(data, meta2d,store) {
  // 重写数据动态变化
  const bindDate = computed(() => {
    return store.getters['souceDate/getBindDate'];
  });
  // 创建一个动态监听数组，
  Bus.$on('selectDataScouse', (str) => {
    poll(); //通知轮询
  });

  // 给绑定的数据集合赋值 先遍历图元，在遍历每个图元内的各个属性挨个赋值
  function getsocueDate() {
    // 赋值操作
    bindDate.value.forEach((pen) => {
      pen.data?.forEach((formItem) => {
        // console.log(formItem, 'formItem');
        if ('dataIds' in formItem) {
          // 对应绑定的数据池中取数据，根据协议id和协议类型定位那个数据池
          let value: any;
          const {
            http,
            dataId,
            communicationProtocolId,
            communicationProtocolName,
          } = formItem.dataIds;
          // TODO，在souceDate.ts文件中，使用getDataPool
          value = store.getters['souceDate/getDataPool'](
            communicationProtocolId,
            dataId,
            communicationProtocolName
          );

          // 赋值 当存在formItem.dataIds时才赋值
          if (value) {
            //如果value不存在不赋值
            meta2d.setValue({
              id: pen.id, //图元id
              [formItem.key]: value.value,
            });
          }
        } else {
          // 增添了属性但是没有绑定设备id也即没有tag和设备相对应
          return;
        }
      });
    });
    // 实时数据库的赋值操作
  }

  // 绑定后就去 如果vuex的http有值且bindDate也有值就去一直轮询，没有值的话轮询查找http是有值60秒就停止
  function poll() {
    // 节流多次绑定重置60秒轮询----->是否需要对每个http返回的数据集都去轮询
    let timers = 60;
    let timer = null; //重复绑定清楚定时器
    timer = setInterval(() => {
      if (
        store.state.souceDate.http.length !== 0 ||
        store.state.souceDate.RealTimeDataBase.length !== 0 ||
        store.state.souceDate.TDengine.length !== 0
      ) {
        timers = 60;
        getsocueDate(); //赋值操作
      } else {
        timers--;
        if (timers === 0) {
          clearInterval(timer); //清除定时器
          timer = null;
        }
      }
    }, 3000);
  }

  closeRealTimeDataBasesFix(); //初始化关闭各个请求
  if (
    data &&
    (data as any).RealTimeDataBase &&
    (data as any).RealTimeDataBase.length > 0
  )
    connectRealTimeDataBasefix((data as any).RealTimeDataBase);
  if (data && data.https && data.https.length > 0) connectHttpfix(data.https); //启动http请求
 closeTDenginesFix();
 if (data && (data as any).TDengine && (data as any).TDengine.length > 0)
   connectTDenginesfix((data as any).RealTimeDataBase);
  // 绑定信息初始化到Vuex中bindDate数据
  if (data && data.pens && data.pens.length > 0) {
    data.pens.forEach((item) => {
      if (!item.form) return; //如果不存在绑定信息就退出当前循环
      store.commit('souceDate/setBindDate', {
        id: item.id, //图元id
        data: item.form, //设备id,属性，tag等等信息
      });
    });
    // 触发Date中的轮询拿值
    poll(); //通知轮询
  }
}
export {
 pollDate
};
