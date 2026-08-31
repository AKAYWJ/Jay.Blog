import { SITE_URL } from '~/utils/seo'

/**
 * 站点地图（静态页面）
 * 动态文章列表需要从后端拉取，超出本最小实现
 */
export default defineEventHandler((event) => {
  setHeader(event, 'Content-Type', 'application/xml; charset=utf-8')

  const now = new Date().toISOString()
  const urls = [
    { loc: `${SITE_URL}/`, changefreq: 'daily', priority: '1.0' },
    { loc: `${SITE_URL}/front`, changefreq: 'daily', priority: '1.0' },
    { loc: `${SITE_URL}/front/timeline`, changefreq: 'weekly', priority: '0.8' },
    { loc: `${SITE_URL}/front/category/all`, changefreq: 'weekly', priority: '0.8' },
    { loc: `${SITE_URL}/front/about`, changefreq: 'monthly', priority: '0.6' },
    { loc: `${SITE_URL}/articlelist`, changefreq: 'daily', priority: '0.9' }
  ]

  const xml = `<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
${urls.map(u => `  <url>
    <loc>${u.loc}</loc>
    <lastmod>${now}</lastmod>
    <changefreq>${u.changefreq}</changefreq>
    <priority>${u.priority}</priority>
  </url>`).join('\n')}
</urlset>`
  return xml
})
