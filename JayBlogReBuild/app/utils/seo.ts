/**
 * 全站默认 SEO 配置
 * 任何页面没显式 useSeoMeta 时继承这里的值
 *
 * ⚠️ SITE_URL 必须改成你部署后的真实公网域名，否则 OG/Twitter/微信分享卡片无法工作
 *    （爬虫只访问公网 URL；localhost 会显示为纯文本链接）
 */
export const SITE_NAME = 'Jay.Blog'
export const SITE_TITLE = 'Jay.Blog - 个人技术博客'
export const SITE_DESCRIPTION = '分享前端开发、后端架构、AI 与生活思考的个人技术博客'
export const SITE_AUTHOR = 'Jay'
export const SITE_KEYWORDS = '博客,前端,Vue,Nuxt,Java,Spring Boot,技术分享'

// 👇 部署前请改成你的真实域名
export const SITE_URL = 'https://blog.jaywk.top'

// og:image 必须是完整的公网可访问 URL（爬虫不会自动拼接当前域名）
// 推荐：1200×630 PNG（兼容性最好），低于 5MB
export const SITE_IMAGE = `${SITE_URL}/og-cover.svg`

/** 通用页面 SEO 配置工厂 */
export function buildSeoMeta(overrides: {
  title?: string
  description?: string
  keywords?: string
  image?: string
  url?: string
  type?: 'website' | 'article'
  publishedTime?: string
  updatedTime?: string
  author?: string
}) {
  const fullTitle = overrides.title ? `${overrides.title} | ${SITE_NAME}` : SITE_TITLE
  const description = overrides.description || SITE_DESCRIPTION
  const keywords = overrides.keywords || SITE_KEYWORDS
  const image = overrides.image || SITE_IMAGE
  const url = overrides.url || SITE_URL
  const type = overrides.type || 'website'
  const author = overrides.author || SITE_AUTHOR

  return {
    title: fullTitle,
    description,
    keywords,
    author,
    ogType: type,
    ogTitle: fullTitle,
    ogDescription: description,
    ogImage: image,
    ogUrl: url,
    ogSiteName: SITE_NAME,
    twitterCard: 'summary_large_image',
    twitterTitle: fullTitle,
    twitterDescription: description,
    twitterImage: image,
    ...(overrides.publishedTime ? { articlePublishedTime: overrides.publishedTime } : {}),
    ...(overrides.updatedTime ? { articleModifiedTime: overrides.updatedTime } : {})
  }
}
