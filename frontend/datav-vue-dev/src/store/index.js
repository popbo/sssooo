import Vue from 'vue'
import Vuex from 'vuex'
import mutations from './mutations'
import actions from './actions'

Vue.use(Vuex)

const state = {
  alignLine: {
    enable: false,
    top: 0,
    bottom: 0,
    left: 0,
    right: 0,
    vertical: 0,
    horizontal: 0,
    topShow: false,
    bottomShow: false,
    leftShow: false,
    rightShow: false,
    verticalShow: false,
    horizontalShow: false,
  },
  canvas: {
    scale: 0.2,
    width: 1920,
    height: 1080,
  },
  showTips: false,
  tipsIndex: '',
  dataSourceList: [], // 存储数据源组件获取到的数据
  Authorization: '',
  showCloseImg: false,
  textSelectTypeChange:{}
}

export default new Vuex.Store({
  state,
  actions,
  mutations,
  modules: {},
})
