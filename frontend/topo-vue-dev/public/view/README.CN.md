# meta2d-view

meta2d 预览页.

# 如何使用

## 下载为html

### 解压 zip 运行

- 安装依赖包

```
npm install
npm start

// or

yarn
yarn start
```

- 本地运行

```
npm install -g http-server
http-server
```

### 在浏览器中打开

http://localhost:8080
缺省打开 index.html 平级目录下 data.json。

或

http://localhost:8080?id=1  
在 index.html 平级目录下必须存在 1.json。

.json 文件为编辑器中“下载为 JSON 文件”




## 下载为Vue2

- 将zip文件解压;
- 将vue2/meta2d.vue放到 vue2项目 的views文件夹（作为一个独立的页面）;
- 配置meta2d页面路由;
- 将data.json文件放到 vue2项目 公共资源目录下public/json/;
- 将js、image和png文件夹放到 vue2项目 public下;
- 参考index.html文件，将script标签引入资源的代码复制到 vue2项目 的index.html中;
- ①参考package.json文件，添加meta2d.js依赖并安装;②将node_modules/meta2d.js下的meta2d.js文件移动到目录public/js下;③在index.html中引入该文件;
- 

## 下载为Vue3

- 将zip文件解压;
- 将vue3/meta2d.vue放到 vue3项目 的views文件夹（作为一个独立的页面）;
- 在 vue3项目 中配置meta2d页面路由,如下：

```ts
  {
    path: '/meta2d',
    component: () => import('@/views/Meta2d.vue'),
  }
```

- 将data.json文件放到 vue3项目 公共资源目录下public/json/;
- 将js、image和png文件夹放到 vue3项目 public下;
- 参考index.html文件，将script标签引入资源的代码复制到 vue3项目 的index.html中;
- ①参考package.json文件，添加meta2d.js依赖并安装;②将node_modules/meta2d.js下的meta2d.js文件移动到目录public/js下;③在index.html中引入该文件;


## 下载为React

- 将zip文件解压;
- 将react文件夹下的文件放到 react项目 的views文件夹（作为一个独立的页面）;
- 配置mata2d页面路由;
- 将data.json文件放到 react项目 公共资源目录下public/json下;
- 将js、image和png文件夹放到 react项目 public下;
- 参考index.html文件，将script标签引入资源的代码复制到 react项目 的index.html中;
- ①参考package.json文件，添加meta2d.js依赖并安装;②将node_modules/meta2d.js下的meta2d.js文件移动到目录public/js下;③在index.html中引入该文件;


# 生产环境

用 nginx 静态代理即可，例如：

```
server {
    listen       80;
    server_name  域名;

    #access_log  /var/log/nginx/host.access.log  main;

    root /root/web/静态文件夹名;

    location / {
        index index.html;
        rewrite ^/.*/$ / last;
        rewrite ^([^.]*[^/])$ $1/ permanent;
    }
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }
}
```
