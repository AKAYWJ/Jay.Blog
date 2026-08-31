<template>
    <div class="about-page">
        <div class="about-card">
            <h2 class="card-subtitle">{{ t('about.blogIntro') }}  ||  <span class="text-desc">{{ configData?.blogName || '' }}</span></h2>
            <p class="text-desc">{{ configData?.aboutMe || '' }}</p>

            <div class="divider"></div>

            <h2 class="card-subtitle">{{ t('about.contact') }}</h2>
            <div class="contact-row">
                <span class="label">QQ</span>
                <span class="value">{{ configData?.qq || t('about.notFilled') }}</span>
            </div>
            <div class="contact-row">
                <span class="label">Github</span>
                <span class="value github-text" :title="configData?.github">{{ configData?.github || t('about.notFilled') }}</span>
            </div>

            <div class="divider"></div>

            <h2 class="card-subtitle">{{ t('about.siteStats') }}</h2>
            <div class="stat-grid">
                <div class="stat-item">
                    <div class="num">{{ statData?.articleTotal }}</div>
                    <div class="tip">{{ t('hero.stat.article') }}</div>
                </div>
                <div class="stat-item">
                    <div class="num">{{ statData?.visitorTotal }}</div>
                    <div class="tip">{{ t('hero.stat.visitor') }}</div>
                </div>
                <div class="stat-item">
                    <div class="num">{{ statData?.commentTotal }}</div>
                    <div class="tip">{{ t('hero.stat.comment') }}</div>
                </div>
            </div>

            <div class="divider"></div>

            <h2 class="card-subtitle text-center">{{ t('about.reward.title') }}</h2>
            <div class="reward-wrap">
                <!-- Nuxt3 静态资源规范路径 ~/assets -->
                <img src="~/assets/zsm.jpg" :alt="t('about.reward.alt')" class="reward-img" />
                <p class="reward-tip">{{ t('about.reward.tip') }}</p>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getBlogConfig, getBlogStat } from '~/api/front'
import { buildSeoMeta } from '~/utils/seo'
import { useI18n } from '~/composables/useI18n'

const { t } = useI18n()

useSeoMeta(buildSeoMeta({
  title: t('about.title'),
  description: t('about.seoDesc')
}))

// 初始化默认值
const configData = ref({ aboutMe: '', qq: '', github: '' })
const statData = ref({ articleTotal: 0, visitorTotal: 0, commentTotal: 0 })

// 页面挂载后执行请求
const loadPageData = async () => {
    try {
        const configRes = await getBlogConfig()
        configData.value = configRes.data
        const statRes = await getBlogStat()
        statData.value = statRes.data
    } catch (err) {
        console.log('接口请求失败', err)
    }
}

// 每次进入页面触发
onMounted(loadPageData)
</script>

<style scoped>
.about-page {
    max-width: 680px;
    margin: -10px auto;
    padding: 16px 0 40px;
}

.page-title {
    font-size: var(--font-size-page-title);
    font-weight: 600;
    color: var(--color-text-primary);
    margin-bottom: 24px;
}

.about-card {
    position: relative;
    overflow: hidden;
    background: var(--liquid-glass-bg);
    backdrop-filter: var(--liquid-glass-blur);
    border: var(--liquid-glass-border);
    border-radius: var(--radius-xl);
    padding: 36px;
    box-shadow: var(--liquid-glass-shadow);
}

.about-card>* {
    position: relative;
    z-index: 1;
}

.card-subtitle {
    font-size: 16px;
    font-weight: 600;
    color: var(--color-text-primary);
    margin: 0 0 14px;
}

.text-center {
    text-align: center;
}

.text-desc {
    font-size: 14px;
    color: var(--color-text-regular);
    line-height: 1.8;
    margin: 0;
}

.divider {
    height: 1px;
    background: var(--color-border);
    margin: 28px 0;
}

.contact-row {
    display: flex;
    margin: 10px 0;
    font-size: 14px;
}

.label {
    width: 80px;
    color: var(--color-text-secondary);
}

.value {
    color: var(--color-text-primary);
}

.github-text {
    word-break: break-all;
    color: var(--color-primary);
}

.stat-grid {
    display: flex;
    gap: 20px;
}

.stat-item {
    text-align: center;
    padding: 18px 20px;
    background: var(--color-primary-light-bg);
    border-radius: var(--radius-lg);
    flex: 1;
}

.num {
    font-size: 22px;
    font-weight: 600;
    color: var(--color-primary);
    margin-bottom: 4px;
}

.tip {
    font-size: 13px;
    color: var(--color-text-secondary);
}

.reward-wrap {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    margin-top: 8px;
}

.reward-img {
    width: 200px;
    height: 220px;
    border-radius: var(--radius-lg);
    object-fit: cover;
}

.reward-tip {
    font-size: 13px;
    color: var(--color-text-muted);
    margin: 0;
}

/* Nuxt3 scoped 内深度选择器用 :deep() 替代 /deep/ */
:deep(body.dark-mode) .about-card {
    background: var(--liquid-glass-bg);
    border: var(--liquid-glass-border);
    box-shadow: var(--liquid-glass-shadow);
}

:deep(body.dark-mode) .stat-item {
    background: rgba(91, 79, 207, 0.1);
}

@media screen and (max-width: 768px) {
    .about-card {
        padding: 24px 20px;
    }

    .stat-grid {
        flex-direction: column;
    }
}
</style>