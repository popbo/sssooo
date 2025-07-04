let scssVariables = require("./src/styles/scss.variables.js");
const path = require("path");
const TerserPlugin = require("terser-webpack-plugin");
function resolve(dir) {
  return path.join(__dirname, dir);
}
module.exports = {
  publicPath: process.env.NODE_ENV === "production" ? "/" : "/",
  lintOnSave: false,
  devServer: {
    port: 8086,
    proxy: {
      "/nvr": {
        target: "http://124.160.68.210:8088",
        changeOrigin: true, //允许跨域
        ws: true,
      },
    },
  },
  css: {
    loaderOptions: {
      scss: {
        prependData:
          '@import "@/styles/echart.scss";\n' +
          Object.keys(scssVariables)
            .map((k) => `\$${k.replace("_", "-")}: "${scssVariables[k]}";`)
            .join("\n"),
      },
    },
  },
  transpileDependencies: ["monaco-editor", "@jiaminghi/data-view"],
  chainWebpack: (config) => {
    //忽略的打包文件
    config.externals({
      vue: "Vue",
      "vue-router": "VueRouter",
      vuex: "Vuex",
      axios: "axios",
      "element-ui": "ELEMENT",
    });
    // 处理迁移过来的svg
    // config.module.rules.delete("svg"); // 删除默认配置中处理svg,
    // const svgRule = config.module.rule('svg')
    // svgRule.uses.clear()
    // config.module
    //   .rule("svg-sprite-loader")
    //   .test(/\.svg$/)
    //   .include.add(resolve("src/icons")) // 处理svg目录
    //   .end()
    //   .use("svg-sprite-loader")
    //   .loader("svg-sprite-loader")
    //   .options({
    //     symbolId: "icon-[name]",
    //   });
  },
  configureWebpack: {
    optimization: {
      minimizer: [
        new TerserPlugin({
          terserOptions: {
            ecma: undefined,
            warnings: false,
            parse: {},
            compress: {
              drop_console: true,
              drop_debugger: false,
              pure_funcs: ["console.log"], // 移除console
            },
          },
        }),
      ],
    },
  },
};
