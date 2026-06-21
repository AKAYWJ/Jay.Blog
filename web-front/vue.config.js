module.exports = {
  devServer: {
    port: 8081,
    open: true,
    proxy: {
      '/api': {
        // target: 'http://47.95.199.158:8086/',
        target: 'http://localhost:8086',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      },
      // 新增：上传接口单独代理
      '/upload': {
        // target: 'http://47.95.199.158:8086/',
        target: 'http://localhost:8086',
        changeOrigin: true,
        // 路径不需要重写，后端本身就是 /upload/cover
        pathRewrite: {}
      }
    }
  },
  productionSourceMap: false,
  lintOnSave: false
}