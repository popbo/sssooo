export const topBarRouter = [
  {
    path: "/list",
    meta: {
      title: "大屏管理",
    },
    component: () => import(/* webpackChunkName: "page" */ "@/page/list/newIndex.vue")
  },
  // {
  //   path: "/map",
  //   meta: {
  //     title: "地图管理",
  //   },
  //   component: () => import(/* webpackChunkName: "page" */ "@/page/list/map.vue")
  // },
  {
    path: "/category",
    meta: {
      title: "分类管理",
    },
    component: () => import(/* webpackChunkName: "page" */ "@/page/list/category.vue")
  },
  // {
  //   path: "/cockpit",
  //   meta: {
  //     title: "ai驾驶舱",
  //   },
  //   component: () => import(/* webpackChunkName: "page" */ "@/page/list/cockpit.vue")
  // },
  {
    path: "/datasource",
    meta: {
      title: "数据源管理",
    },
    component: () => import(/* webpackChunkName: "page" */ "@/page/list/datasource/DsMain.vue")
  },
  {
    path: "/dataCollection",
    meta: {
      title: "数据集管理",
    },
    component: () => import(/* webpackChunkName: "page" */ "@/page/list/dataCollection/index.vue")
  },
  {
    path: "/resourceManage",
    meta: {
      title: "资源管理",
    },
    component: () => import(/* webpackChunkName: "page" */ "@/page/list/resourceManage/index.vue")
  },
  // {
  //   path: "/uploadfont",
  //   meta: {
  //     title: "上传本地字体",
  //   },
  //   component: () => import(/* webpackChunkName: "page" */ "@/page/list/uploadfont.vue")
  // },
  // {
  //   path: "/apidocument",
  //   meta: {
  //     title: "接口文档",
  //   },
  // },
]