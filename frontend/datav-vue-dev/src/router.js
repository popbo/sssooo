import Router from "vue-router";
import Vue from "vue";
import { topBarRouter } from '@/router/topBarRouter'
import { casLogin } from '@/api/login.js'
import store from '@/store'
Vue.use(Router);
const vueRouter = new Router({
  mode: "history",
  base: process.env.VUE_APP_PATH,
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import("@/page/login/index.vue"),
      meta: {
        anonymous: true
      }
    },
    {
      path: "/",
      name: "home",
      redirect: '/list',
      component: () => import(/* webpackChunkName: "page" */ "@/page/home"),
      children: topBarRouter
    },
    {
      path: "/create",
      name: "create",
      component: () => import(/* webpackChunkName: "page" */ "@/page/create"),
    },
    {
      path: "/build/:id",
      name: "build",
      component: () => import(/* webpackChunkName: "page" */ "@/page/build"),
      meta: {
        isNeedCheckPsw: true, // 编辑页面 发布页面都需要查验密码
        pswStr: 'editPassword',
        isNeedCheckParams: false
      }
    },
    {
      path: "/view/:id",
      name: "view",
      component: () => import(/* webpackChunkName: "page" */ "@/page/view"),
      meta: {
        isNeedCheckPsw: false,
        isNeedCheckParams: true
      }
    },
    {
      path: "/share/:id+",
      name: "share",
      component: () => import(/* webpackChunkName: "page" */ "@/page/share"),
      meta: {
        isNeedCheckPsw: true,
        pswStr: 'psw',
        isNeedCheckParams: true
      }
    },
    
  ],
});
const ssoRedirect = () => {
  if (window.ssoConfig.mode == "cas") {
    let service = window.location.href;
    localStorage.setItem("service", service);
    window.location.href = window.ssoConfig.url + "?service=" + service;
  }
};
vueRouter.beforeEach((to, from, next) => {
  // 1.当前为单点登录模式，且要路由到login页面，则做单点登录重定向；
  // 因为后台可能会把点击一个项目时的路由部署为login页面所以应该重定向
  if (to.name == 'login' && window.ssoConfig.mode) {
    // 单点登录时如果已经携带了code或者ticket时，如果是/login路径需要跳转到默认页面（解决反复跳转的问题）
    if ((window.ssoConfig.mode == "cas") && (to.query.code || to.query.ticket)) {
      window.location.href = window.location.href.replace("/login", "")
      return
    }
    else {
      // 这个一般是路由到login页面并且还是cas但是没有携带ticket,就要跳转到总登陆页面
      ssoRedirect();
    }
  }
  if (to.matched.some(record => !record.meta.anonymous)) { // 非login页面
    if (window.ssoConfig.mode == "cas") { //非login页面但模式为cas,需要用ticket去做登录
      let ticket = to.query.ticket;
      if (ticket) {
        casLogin({ticket}).then(res => {
          if (res.data.success) {
            const data = res.data.data
            store.commit('changeLogin', { Authorization: data.token })
            next()
          } 
        }).catch(err => {
          
        })
      } else { //非login页面但模式为cas,之前已经用ticket去做登录了，直接放行
        next()
      }
    } else { // 非login页面且不是单点登录直接放行
      next()
    }
  } else {
    // 这个一般是login页面会进到这里并且cas为空字符串，只有login的路由配置了meta.anonymous为true
    next();
  }
  // if (to.path === '/login') {
  //   next();
  // } else {
  //   let token = localStorage.getItem('Authorization');
  //   if (token === null || token === '') {
  //     next({path:'/login',query: {redirect: to.fullPath}})
  //   } else {
  //     next();
  //   }
  // }
});
export default vueRouter;
