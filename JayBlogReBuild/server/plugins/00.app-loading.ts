/**
 * 首屏加载动画注入器
 *
 * 目标：消除 SSR → hydration 之间的"白屏闪一下"。
 * 原理：在 SSR 返回的 HTML <body> 末尾插入一个 CSS-only 的 loading DOM。
 * 客户端 mount 完成后，通过 app.vue 派发 'app:mounted' 事件，loader 自动卸载。
 */
export default defineNitroPlugin((nitroApp) => {
  nitroApp.hooks.hook('render:html', (html) => {
    // bodyAppend 必须是 string[]，否则 Nuxt joinTags 会抛错
    if (!Array.isArray(html.bodyAppend)) html.bodyAppend = []
    if (html.bodyAppend.some((t: string) => t.includes('app-loading'))) return

    const loaderHtml = `
<div id="app-loading" class="app-loading" role="status" aria-label="Loading">
  <div class="app-loading-spinner"></div>
  <div class="app-loading-text">JAY.BLOG</div>
</div>
`
    html.bodyAppend.push(loaderHtml)
  })
})

