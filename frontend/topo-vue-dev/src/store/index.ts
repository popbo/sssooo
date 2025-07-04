import { createStore } from 'vuex';

import i18n from './i18n';
import user from './user';
import souceDate from './souceDate'
const store = createStore({
  modules: {
    i18n,
    user,
    souceDate
  },
  state: () => ({}),
  mutations: {}
});

export default store;
