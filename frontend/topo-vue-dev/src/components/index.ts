import type { App } from 'vue';

import Meta2d from '@/components/Meta2d.vue';
import Preview from '@/views/Preview.vue';

import '@/styles/index.scss';
import { t } from '@/i18n/i18n';

const install = (app: App) => {
  app.component('meta2d-vue', Meta2d);
  app.component('meta2d-vue-preview', Preview);

  app.config.globalProperties.t = t;
};
export default install;
