import { SITE_URL } from '~/utils/seo'

export default defineEventHandler((event) => {
  setHeader(event, 'Content-Type', 'text/plain; charset=utf-8')
  return `User-agent: *
Allow: /
Disallow: /admin
Disallow: /login
Disallow: /front/profile

Sitemap: ${SITE_URL}/sitemap.xml
`
})
