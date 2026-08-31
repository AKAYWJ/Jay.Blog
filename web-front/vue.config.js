module.exports = {
  devServer: {
    port: 8081,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8086',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  },
  productionSourceMap: false,
  lintOnSave: false
}