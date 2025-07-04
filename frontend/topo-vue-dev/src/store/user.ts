import axios from '@/http';
import { deleteCookie, setCookie } from '@/services/cookie';
import { User, isVip } from '@/services/user';

const getRootDomain = () => {
  let domain = '';
  const domainItems = document.domain.split('.');
  if (
    domainItems.length < 3 ||
    (domainItems.length === 4 &&
      +domainItems[0] > 0 &&
      +domainItems[1] > 0 &&
      +domainItems[2] > 0 &&
      +domainItems[3] > 0)
  ) {
    domain = '';
  } else if (
    document.domain.endsWith('.com.cn') ||
    document.domain.endsWith('.org.cn')
  ) {
    domain = domainItems.slice(-3).join('.');
  } else {
    domain = domainItems.slice(-2).join('.');
  }

  return domain;
};

const user = {
  namespaced: true,
  state: () => ({
    profile: null,

    //通信数据
    HTTPDate:[],
    websocketDate:[],
    MQTTDate:[],
    RealTimeDataBase:[],
    TDengine:[],
    // 数据选项下的数据
    treeDataId:[],
  }),
  mutations: {
    profile(state: any, payload: any) {
      state.profile = payload;
      if (payload.token !== undefined && payload.token !== null) {
        setCookie('token', payload.token);
      }
    },
    setProfile(state: any, payload: any) {
      if (state.profile) {state.profile.categoryValue = payload};
    },
    setComponent(state: any, payload: any) { //判断是否'我的组件'
      if (state.profile) {state.profile.component = payload};
    },
    logout(state: any) {
      state.profile = null;
      localStorage.removeItem('token');
      const domain = getRootDomain();
      if (domain) {
        deleteCookie('token', {
          path: '/',
          domain: 'unittec.com',
        });
      }
      // TODO: 目前设置一个伪退出，即不退出页面
      // setCookie('token', '');
    },

    // 存储通信数据
    setHTTPDate(state,payload){
      state.HTTPDate=[...payload]
    },
    setwebsocketDate(state,payload){
      state.websocketDate=[...payload]
    },
    setMQTTDate(state,payload){
      state.MQTTDate=[...payload]
    },
    setRealTimeDataBase(state,payload){
      state.RealTimeDataBase=[...payload]
    },
    setTDengine(state,payload){
      state.TDengine=[...payload]
    },
    // 删除数据
    delHTTPDate(state,id){
      state.HTTPDate.splice(id,1)
    }, 
     delwebsocketDate(state,id){
      state.websocketDate.splice(id,1)
    }, 
     delMQTTDate(state,id){
      state.MQTTDate.splice(id,1)
    },
     delRealTimeDataBase(state,id){
      state.RealTimeDataBase.splice(id,1)
    },
     delTDengine(state,id){
      state.TDengine.splice(id,1)
    },


    // 存储树形的数据
    setTreeDataId(state,paylaod){
        state.treeDataId=[...paylaod]
    }
  },
  actions: {
    async profile({ commit }: any) {
      const href=window.location.href
      if(!href.includes('/release')){
        const user: User = await axios.get('/account/profile');
        // 判断是否是 VIP
        user.isVip = isVip(user); // 为 user 配置一个属性，判断他是否是 vip
        commit('profile', user);
      }
    },
  },
  getters: {
    getTreeDataId:(state)=>(id)=>{
      return state.treeDataId.find(item=>item.dataId===id)
    }

  },
};

export default user;
