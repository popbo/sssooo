import vue from '@vitejs/plugin-vue';
import formidable from 'formidable';
import fs from 'fs';
import { join, resolve } from 'path';
import {
  AntDesignVueResolver,
  ElementPlusResolver,
} from 'unplugin-vue-components/resolvers';
import Components from 'unplugin-vue-components/vite';
import { Plugin, ViteDevServer, defineConfig, loadEnv } from 'vite';
import monacoEditorPlugin from 'vite-plugin-monaco-editor';
import styleImport, {
  AndDesignVueResolve,
  ElementPlusResolve,
} from 'vite-plugin-style-import';
const config = loadEnv('base', './');
console.log("config1", config);
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    monacoEditorPlugin({}),
    Components({
      resolvers: [AntDesignVueResolver(), ElementPlusResolver()],
    }),
    styleImport({
      resolves: [AndDesignVueResolve(), ElementPlusResolve()],
      libs: [
        // 如果没有你需要的resolve，可以在lib内直接写，也可以给我们提供PR
        {
          libraryName: 'ant-design-vue',
          esModule: true,
          resolveStyle: (name) => {
            return `ant-design-vue/es/${name}/style/index`;
          },
        },
      ],
    }),
    fileList(),
    // viteMockServe({
    //   mockPath:'./src/mock/',
    //   lockEnabled:true,
    //   suppportTs:true,
    //   logger:true
    // }),  
  ],
  // TODO: 等到最后正式上线关掉这个
  // build: {
  //   sourcemap: true
  // },
  resolve: {
    alias: {
      '@': resolve(__dirname, './src'),
      // '@topometa2d': resolve(__dirname, '../meta2d.js/packages'),
      // '@topometa2d-components': resolve(
      //   __dirname,
      //   '../meta2d-components/packages'
      // )
    },
  }, //测试图纸id 637c748d0e1f0ecb106538f0
  server: {
    proxy: {
	  //--------------------------------
	  // '/images': 'http://localhost:8080/',
	  // '/image': 'http://localhost:8080/',
    // '/api/device/data': {
    //   target:'http://rap2api.taobao.org/app/mock/311085',//要配置到
    //   changeOrigin:true,//跨域
    // },
	  // '/api': 'http://localhost:8080/',
	  // '/data': 'http://localhost:8080/',
	  // '/account': 'http://localhost:8080/',
	  // '/file': 'http://localhost:8080/',
    '/api/device/data': {
      target:'http://rap2api.taobao.org/app/mock/311085',
      changeOrigin: true,//跨域
    },
	  '/images': config.VITE_BASIC_API,
	  '/image': config.VITE_BASIC_API,
	  '/api': config.VITE_BASIC_API,
	  '/data': config.VITE_BASIC_API,
	  '/account': config.VITE_BASIC_API,
	  '/file': config.VITE_BASIC_API,
	  //--------------------------------
      '/ISAPI': 'http://192.168.31.198',
      '/webSocketVideoCtrlProxy': {
        target: 'ws://192.168.31.64:7681',
        changeOrigin: true,
        ws: true,
        rewrite: (path) => {
          return path.replace(/^\/webSocketVideoCtrlProxy/, '/101');
        },
      },
    },
  },
  css: {
    preprocessorOptions: {
      less: {
        modifyVars: {
          // 'primary-color': '#1890ff'
          // "link-color": "#fb8501",
          // "table-row-hover-bg": "#fb850103",
          // "tree-node-selected-bg": "#fb850100",
        },
        javascriptEnabled: true,
      },
    },
  },
});

function fileList(): Plugin {
  return {
    name: 'vite-plugin-svg-png-files',
    configureServer(server: ViteDevServer) {
      server.middlewares.use((req, res, next) => {
        let url = req.url as string;
        if ((url.startsWith('/2d/svg/') || url.startsWith('/2d/png/') && url.endsWith('/'))) {
          url = url.substring(3);
        }
        if ((url.startsWith('/svg/') || url.startsWith('/png/')) && url.endsWith('/')) {
          const pwd = decodeURI(join(__dirname, 'public', url));
          const files = fs.readdirSync(pwd, {
            withFileTypes: true,
          });

          const list: {
            name: string;
            type?: string;
          }[] = [];
          for (const item of files) {
            if (item.isDirectory()) {
              list.push({ name: item.name, type: 'directory' });
            } else {
              list.push({ name: item.name });
            }
          }
          res.end(JSON.stringify(list));
        } else if (url === '/img' && req.method === 'POST') {
          const form = formidable({
            uploadDir: decodeURI(join(__dirname, 'public', '/img')),
            keepExtensions: true,
          });
          form.parse(req, (err, fields, files) => {
            if (!err) {
              res.end(
                JSON.stringify({ url: '/img/' + files.file.newFilename })
              );
            }
          });
        } else {
          next();
        }
      });
    },
  };
}
