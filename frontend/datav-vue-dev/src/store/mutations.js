import * as types from './mutation-types'

export default {
  [types.SET_ALIGNLINE_INFO](state, alignLine) {
    state.alignLine = alignLine
  },
  [types.SET_CANVAS_INFO](state, canvas) {
    state.alignLine = canvas
  },
  [types.SET_SHOW_TIPS](state, val) {
    state.showTips = val
  },
  [types.SET_TIPS_INDEX](state, val) {
    state.tipsIndex = val
  },
  [types.SET_DATA_SOURCE_LIST](state, val) {
    // 对传递过来的数据对象进行查找，如果找到，说明是同一个数据源组件传递过来的数据对象，那就要替换，如果没有找到说明是新的数据源组件添加的
    const index = state.dataSourceList.findIndex(item => item.id === val.id)
    if (index > -1) {
      state.dataSourceList.splice(index, 1, val)
    } else {
      state.dataSourceList.push(val)
    }
    // console.log(state.dataSourceList)
  },
  [types.SET_SHOW_CLOSE_IMG](state, val) {
    state.showCloseImg = val
  },
  [types.changeLogin](state, val) {
    state.Authorization = val.Authorization
    localStorage.setItem('Authorization', val.Authorization)
  },
  [types.userInfo](state, val) {
    localStorage.setItem('userInfo', val.username)
  },
  [types.logout]() {
    localStorage.removeItem('Authorization')
    localStorage.removeItem('userInfo')
  },
  intTextSelectTypeChange(state, val){
    state.textSelectTypeChange = {...state.textSelectTypeChange,...val}
  }
}
