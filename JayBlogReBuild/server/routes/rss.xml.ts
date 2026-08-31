import { SITE_NAME, SITE_DESCRIPTION, SITE_URL, SITE_AUTHOR } from '~/utils/seo'

/**
 * RSS 2.0 Feed
 * 列出最近 20 篇已发布文章（status=2）
 * 让读者用 RSS 阅读器订阅博客更新
 */
export default defineEventHandler(async (event) => {
  setHeader(event, 'Content-Type', 'application/xml; charset=utf-8')

  const config = useRuntimeConfig()
  const apiBase = (config.public.apiBase as string) || 'http://localhost:8086'

  // 从后端拉最新已发布文章
  let articles: any[] = []
  try {
    const res: any = await $fetch(`${apiBase}/article/front/list`, {
      params: { pageNum: 1, pageSize: 20 }
    })
    articles = (res?.data?.records || []).filter((a: any) => a.status === 2)
  } catch (err) {
    console.error('[RSS] Failed to fetch articles:', err)
  }

  const buildDate = new Date().toUTCString()
  const now = Date.now()

  const escapeXml = (s: string) => String(s || '')
    .replace(/&/g, '&')
    .replace(/</g, '<')
    .replace(/>/g, '>')
    .replace(/"/g, '"')
    .replace(/'/g, '&apos;')

  const items = articles.map((a: any) => {
    const link = `${SITE_URL}/article?id=${a.id}`
    const pubDate = a.updateTime ? new Date(a.updateTime).toUTCString() : new Date(a.createTime || now).toUTCString()
    const description = (a.summary || '').slice(0, 300)
    return `    <item>
      <title>${escapeXml(a.title)}</title>
      <link>${link}</link>
      <guid isPermaLink="true">${link}</guid>
      <pubDate>${pubDate}</pubDate>
      <author>noreply@jaywk.top (${escapeXml(SITE_AUTHOR)})</author>
      <description>${escapeXml(description)}</description>
    </item>`
  }).join('\n')

  return `<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
  <channel>
    <title>${escapeXml(SITE_NAME)}</title>
    <link>${SITE_URL}</link>
    <atom:link href="${SITE_URL}/rss.xml" rel="self" type="application/rss+xml" />
    <description>${escapeXml(SITE_DESCRIPTION)}</description>
    <language>zh-CN</language>
    <lastBuildDate>${buildDate}</lastBuildDate>
    <generator>Jay.Blog RSS Generator</generator>
${items}
  </channel>
</rss>`
})
