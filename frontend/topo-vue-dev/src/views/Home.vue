<template>
  <div class="Home">
    <Meta2d />
  </div>
  <PassWord v-model:dialogVisible="dialogVisible" :password="password"></PassWord>
</template>

<script lang="ts">
import { defineComponent, ref, watchEffect,computed } from 'vue';
import Meta2d from '@/components/Meta2d.vue';
import { useRoute, useRouter } from 'vue-router';
import localforage from 'localforage';
import { useStore } from 'vuex';
import { init, setToken, handleVarBind } from '@/services/url';
import { localMeta2dDataName } from '@/components/utils';
import axios from 'axios';
import { connectHttpfix ,connectRealTimeDataBasefix,closeRealTimeDataBasesFix, connectTDenginesfix, closeTDenginesFix} from '@/utils/soucerequest';
import Bus from '@/components/common/bus';
import PassWord from './PassWord.vue';
import { message } from 'ant-design-vue';
import {showWindowHref} from '@/components/utils'
import { watch } from 'fs';
declare const meta2d: any;
declare const window: any;

export default defineComponent({
  name: 'Home',
  components: {
    Meta2d,
    PassWord
  },
  setup: () => {
    const route = useRoute();
    const router = useRouter();
    const store = useStore();
    const dialogVisible= ref<boolean>()//是否开启密码输入弹窗
    const password=ref<string>(null)

    watchEffect(async () => {
      // TODO: 考虑重复的执行是否合理
      if (route.path === '/') {
        const configObj = window ? window.MyGlobalConfigObject : null;
        const visual_url = configObj ? configObj.visual_url : null;
        const linkSearch=showWindowHref()
        let linkParams={
          headtitle:'未命名图纸'
        }
        if(route.query.username){
          Object.assign(linkParams,{username:route.query.username})
        }
        if(route.query?.id){
          linkParams = await axios.post(`/data/topo2d/topo/editor?id=${linkSearch['id']}`);
        }
        let reqUrl = null;
        if(linkParams['headtitle']){
          document.title='组态设计_'+linkParams['headtitle']
        }
        
        if (linkParams['username']) {
          //从可视化进入
          //reqUrl = '/account/cas/login?account=' + route.query.username;
          reqUrl = '/account/get/token?account=' + linkParams['username'];
        } else if (linkParams['ticket']) {
          //从统一认证进入
          //reqUrl = '/account/cas/login?ticket=' + route.query.ticket + '&service=' + route.query.service;
        } else {
          console.log("location.href: ", location.href);
          if (location.href.endsWith("/2d/")) {//组态原首页
            console.warn('请从可视化系统进入访问。');
            localStorage.removeItem('token');
            localStorage.removeItem('username');
            if (visual_url) {  //跳转可视化登录，调试先不打开
              window.location.href = visual_url;
            }
            return;
          }
        }
        const queryUserName = linkParams['username'];
        const localSavedToken = localStorage.getItem('token');
        const localSavedUser = localStorage.getItem('username');
        const noTokenCondition = !localSavedToken && !localSavedUser && reqUrl;
        const hasTokenUserDiffCondition = localSavedToken && localSavedUser && reqUrl && (localSavedUser != queryUserName);
        if (noTokenCondition || hasTokenUserDiffCondition) {
          const ret: any = await axios.post(reqUrl);
          if (ret && ret.token && ret.username) {
            console.log('getToken成功--->', ret.username);
            localStorage.setItem('token', ret.token);
            localStorage.setItem('username', ret.username);
            location.reload();
          } else {
            console.log('getToken失败');
            localStorage.removeItem('token');
            localStorage.removeItem('username');
            if (visual_url) {
              window.location.href = visual_url;
            }
          }
          return;
        } else {
          console.warn("Home.vue localStorage中username,token: ", localStorage.getItem('username'), localStorage.getItem('token'));
        }
        //  设置 token
        // if (route.query.token) { //影响跳转问题
        //   const newQuery = setToken(route.query, store);
        //   router.replace({
        //     path: '/',
        //     query: newQuery,
        //   });
        //   return;
        // }
       
        
        const data = await init(linkParams);
        if(data){
          let payload=data.categoryValue? data.categoryValue: route.query.folderType
          store.commit('user/setProfile', payload)
          
        }
        await handleVarBind(linkParams, data);
        // 判断是否有密码，有弹出弹框，没有不做处理
        if(!(data as any).password) {
          dialogVisible.value=false
        }else{
          dialogVisible.value=true
          password.value=(data as any).password
        }
        meta2d.open(data);
        //⇡⇡⇡刷新打开图纸 解决当刷新后不同配置图元的数据完全相同变化问题
        meta2d.closeHttp();// 关闭刷新自带的http连接，然后打开自己定义的http连接方式
        closeRealTimeDataBasesFix()//初始化关闭各个请求
        closeTDenginesFix()
        if (data && (data as any).RealTimeDataBase && (data as any).RealTimeDataBase.length > 0) connectRealTimeDataBasefix((data as any).RealTimeDataBase)
        
        if(data&&data.https&&data.https.length>0) connectHttpfix(data.https)//启动http请求
        
        if (data && (data as any).TDengine && (data as any).TDengine.length > 0) connectTDenginesfix((data as any).TDengine)
        // 绑定信息初始化到Vuex中bindDate数据
        if(data&&data.pens&&data.pens.length>0){
          data.pens.forEach(item=>{
            if(!item.form) return  //如果不存在绑定信息就退出当前循环
            store.commit('souceDate/setBindDate', {
               id: item.id, //图元id
               data: item.form, //设备id,属性，tag等等信息
            })
          })
          // 触发Date中的轮询拿值
          poll(); //通知轮询
        }
        if (!data.pens || data.pens.length === 0) {
          meta2d.setDatabyOptions(meta2d.store.options);
        }
        localforage.removeItem(localMeta2dDataName);

        setTimeout(() => {
          meta2d.render();
          message.destroy()// 关闭所有的Message提示
        }, 2000);
      }
    });
    
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
          if ('dataIds' in formItem) {
            // 对应绑定的数据池中取数据，根据协议id和协议类型定位那个数据池
            let value: any;
            const { http, dataId,communicationProtocolId,communicationProtocolName } = formItem.dataIds;
            // TODO，在souceDate.ts文件中，使用getDataPool
            value = store.getters['souceDate/getDataPool'](communicationProtocolId, dataId,communicationProtocolName);
            // 赋值 当存在formItem.dataIds时才赋值
            if (value !== null && value !== undefined) {
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
        if (store.state.souceDate.http.length !== 0||store.state.souceDate.RealTimeDataBase.length !== 0|| store.state.souceDate.TDengine.length !== 0) {
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

    return {
      dialogVisible,//展示密码弹窗
      password
    };
  },
});
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.Home {
  height: 100vh;
  position: relative;
}
</style>
