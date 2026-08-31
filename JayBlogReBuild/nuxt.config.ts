// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },
  css: [
    '~/assets/css/variables.css',
    '~/assets/css/element-override.css',
    '~/assets/css/global.css'
  ],
  modules: [
    '@element-plus/nuxt',
    '@pinia/nuxt',
    'pinia-plugin-persistedstate/nuxt',
    '@vite-pwa/nuxt'
  ],

  // PWA：渐进式 Web 应用，支持离线访问 + 安装到桌面
  pwa: {
    registerType: 'autoUpdate',
    manifest: {
      name: 'Jay.Blog',
      short_name: 'Jay.Blog',
      description: '个人技术博客 · 记录 · 思考 · 分享',
      theme_color: '#5B4FCF',
      background_color: '#ffffff',
      display: 'standalone',
      orientation: 'portrait',
      lang: 'zh-CN',
      start_url: '/',
      scope: '/',
      icons: [
        {
          src: '/favicon.svg',
          sizes: 'any',
          type: 'image/svg+xml',
          purpose: 'any maskable'
        }
      ]
    },
    workbox: {
      // 预缓存：所有静态资源 + 关键页面
      navigateFallback: '/front',
      globPatterns: ['**/*.{js,css,html,svg,png,ico,woff2}'],
      // 登录背景图 5.62 MB，超默认 2 MiB 限制
      maximumFileSizeToCacheInBytes: 8 * 1024 * 1024,
      // API 缓存策略：网络优先，失败回退缓存
      runtimeCaching: [
        {
          urlPattern: ({ url }) => url.pathname.startsWith('/api/article/front/list') ||
                                   url.pathname.startsWith('/api/category/front/list') ||
                                   url.pathname.startsWith('/api/article/front/hot') ||
                                   url.pathname.startsWith('/api/blogConfig/get'),
          handler: 'StaleWhileRevalidate',
          options: {
            cacheName: 'api-public',
            expiration: { maxEntries: 50, maxAgeSeconds: 60 * 10 } // 10 分钟
          }
        },
        {
          urlPattern: ({ url }) => url.pathname.startsWith('/upload/cover/'),
          handler: 'CacheFirst',
          options: {
            cacheName: 'images',
            expiration: { maxEntries: 200, maxAgeSeconds: 60 * 60 * 24 * 30 } // 30 天
          }
        }
      ]
    },
    // 开发模式不启用 PWA，避免 HMR 报错
    devOptions: {
      enabled: false
    },
    // 客户端日志：上线关掉
    logLevel: 'info'
  },
  plugins: [
    '~/plugins/fetch-interceptor.ts'
  ],
  elementPlus: {
    importStyle: 'css'
  },
  nitro: {
    devProxy: {
      // 匹配所有 /api 开头的请求，转发到后端
      '/api': {
        target: 'http://localhost:8086', // 你的后端真实服务地址
        changeOrigin: true, // 跨域核心：修改请求源为后端域名
        // 路径重写：如果后端接口本身不带 /api 前缀，打开下面这行去掉前缀
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },

  runtimeConfig: {
    public: {
      // 客户端用：相对路径走代理 / nginx
      apiPrefix: process.env.NUXT_PUBLIC_API_PREFIX || '/api',
      // SSR 端用：Node 直接打后端完整地址（不能用 /api 因为 Node 端没 devProxy）
      apiBase: process.env.NUXT_PUBLIC_API_BASE || 'http://localhost:8086'
    }
  },

  app: {
    head: {
      // 首屏加载动画 CSS：SSR 阶段就内联进 HTML，首字节即生效
      // 避免 hydration 之前的"白屏闪一下"
      style: [
        {
          innerHTML: `
            /* 首屏 loading：紫粉渐变圆环 + 居中 logo 文字 */
            html, body { margin: 0; padding: 0; background: #fafafa; font-family: system-ui, -apple-system, "Segoe UI", sans-serif; }
            #__nuxt { min-height: 100vh; }
            .app-loading {
              position: fixed; inset: 0; z-index: 99999;
              display: flex; flex-direction: column; align-items: center; justify-content: center;
              background: #fafafa;
              transition: opacity 0.35s ease, visibility 0.35s ease;
            }
            .app-loading.hide { opacity: 0; visibility: hidden; }
            .app-loading-spinner {
              width: 48px; height: 48px;
              border: 3px solid rgba(91, 79, 207, 0.15);
              border-top-color: #5B4FCF;
              border-right-color: #EC4899;
              border-radius: 50%;
              animation: app-loading-spin 0.9s cubic-bezier(0.4, 0, 0.2, 1) infinite;
            }
            @keyframes app-loading-spin {
              to { transform: rotate(360deg); }
            }
            .app-loading-text {
              margin-top: 20px;
              font-size: 13px;
              font-weight: 500;
              color: #5B4FCF;
              letter-spacing: 2px;
              animation: app-loading-pulse 1.4s ease-in-out infinite;
            }
            @keyframes app-loading-pulse {
              0%, 100% { opacity: 0.4; }
              50% { opacity: 1; }
            }
            /* 暗色模式首屏背景 */
            @media (prefers-color-scheme: dark) {
              html, body { background: #1a1726; }
              .app-loading { background: #1a1726; }
              .app-loading-text { color: #b794f4; }
              .app-loading-spinner {
                border-color: rgba(183, 148, 244, 0.15);
                border-top-color: #b794f4;
                border-right-color: #ec4899;
              }
            }
          `
        }
      ],
      script: [
        {
          // 卸载 loader：mount 完成后立刻隐藏
          innerHTML: `
            window.addEventListener('app:mounted', function() {
              var el = document.getElementById('app-loading');
              if (el) el.classList.add('hide');
              setTimeout(function() { if (el && el.parentNode) el.parentNode.removeChild(el); }, 400);
            });
          `
        }
      ]
    }
  }
})
