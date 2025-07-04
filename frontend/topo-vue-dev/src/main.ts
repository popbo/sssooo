import { createApp } from 'vue';

import App from './App.vue';
import router from './router/index';
import store from './store';

import format from '@/services/format';
import i18n from './i18n/i18n';

import '@/styles/index.scss';

import '@/http';

const app = createApp(App);
app.use(i18n);
app.use(format);
app.use(router);
app.use(store);
app.mount('#app');

