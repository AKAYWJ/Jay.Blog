// 兼容层：原 front.ts 里的活接口统一转发到拆分后的模块
// 死接口（getArticleDetail/getArticleComment/addComment）已移除
export { getFrontArticleList, getHotArticle } from './article'
export { getFrontCategoryList as getFrontCategory, getCategoryAll, getCategoryList } from './category'
export { getBlogConfig, updateBlogConfig, getBlogStat } from './system'
