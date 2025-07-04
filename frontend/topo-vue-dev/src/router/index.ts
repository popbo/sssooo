import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  { path: '/', component: () => import('@/views/Home.vue') },
  {
    path: '/preview',
    component: () => import('@/views/Preview.vue'),
  },
  {
    path: '/release',
    component: () => import('@/views/Release.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
