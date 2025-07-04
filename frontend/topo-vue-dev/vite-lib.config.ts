import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';
import {
    AntDesignVueResolver,
    ElementPlusResolver,
} from 'unplugin-vue-components/resolvers';
import Components from 'unplugin-vue-components/vite';
import { defineConfig } from 'vite';
import monacoEditorPlugin from 'vite-plugin-monaco-editor';
import styleImport, {
    AndDesignVueResolve,
    ElementPlusResolve,
} from 'vite-plugin-style-import';

import copyPlugin from 'rollup-copy-plugin';

// https://vitejs.dev/config/
export default defineConfig({
  build: {
    sourcemap: true,
    target: 'es2015',
    outDir: 'lib',
    lib: {
      entry: resolve(__dirname, 'src/components/index.ts'),
      name: 'meta2d-vue',
      fileName: (format) => `meta2d-vue.${format}.js`,
      formats: ['es'],
    },
    rollupOptions: {
      external: ['vue', 'vue-router'],
      output: {
        globals: {
          vue: 'Vue',
        },
        format: 'es',
        inlineDynamicImports: true,
      },
    },
  },
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
    copyPlugin({
      './package.lib.json': './dist/package.json',
    }),
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, './src'),
      // '@topometa2d': resolve(__dirname, '../meta2d.js/packages'),
      // '@topometa2d-components': resolve(
      //   __dirname,
      //   '../meta2d-components/packages'
      // )
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
