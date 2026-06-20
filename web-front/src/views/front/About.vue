<template>
    <div class="about-page">
        <h1 class="page-title">关于本站</h1>

        <div class="about-card">
            <h2 class="card-subtitle">博客介绍</h2>
            <p class="text-desc">{{ blogConfig.aboutMe }}</p>

            <div class="divider"></div>

            <h2 class="card-subtitle">联系方式</h2>
            <div class="contact-row">
                <span class="label">QQ</span>
                <span class="value">{{ blogConfig.qq || '暂未填写' }}</span>
            </div>
            <div class="contact-row">
                <span class="label">Github</span>
                <span class="value github-text" :title="blogConfig.github">{{ blogConfig.github || '暂未填写' }}</span>
            </div>

            <div class="divider"></div>

            <h2 class="card-subtitle">站点数据统计</h2>
            <div class="stat-grid">
                <div class="stat-item">
                    <div class="num">{{ stat.articleTotal }}</div>
                    <div class="tip">文章总数</div>
                </div>
                <div class="stat-item">
                    <div class="num">{{ stat.visitorTotal }}</div>
                    <div class="tip">访客总数</div>
                </div>
                <div class="stat-item">
                    <div class="num">{{ stat.commentTotal }}</div>
                    <div class="tip">评论总数</div>
                </div>
            </div>

            <div class="divider"></div>

            <!-- 底部打赏区域 -->
            <h2 class="card-subtitle text-center">请作者喝杯咖啡</h2>
            <div class="reward-wrap">
                <!-- 替换为你的打赏二维码图片地址 -->
                <img src="/upload/cover/reward.png" alt="打赏二维码" class="reward-img" />
                <p class="reward-tip">扫码打赏，感谢支持</p>
            </div>
        </div>
    </div>
</template>

<script>
import { getBlogConfig, getBlogStat } from '@/api/front'
export default {
    name: 'About',
    data() {
        return {
            blogConfig: {
                about: '',
                qq: '',
                github: ''
            },
            stat: {
                articleTotal: 0,
                visitorTotal: 0,
                commentTotal: 0
            }
        }
    },
    mounted() {
        this.loadData()
    },
    methods: {
        async loadData() {
            // 加载站点配置
            const configRes = await getBlogConfig()
            this.blogConfig = configRes.data
            // 加载全站统计
            const statRes = await getBlogStat()
            this.stat = statRes.data
        }
    }
}
</script>

<style scoped>
.about-page {
    width: 100%;
    padding: 40px 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.page-title {
    font-size: var(--font-size-page-title);
    font-weight: 600;
    color: var(--color-text-primary);
    margin-bottom: var(--spacing-page);
    text-align: center;
}

.about-card {
    background: #fff;
    border-radius: 16px;
    padding: 32px;
    box-shadow: 0 3px 14px rgba(99, 85, 216, 0.07);
    max-width: 800px;
    width: 100%;
}

.card-subtitle {
    font-size: 16px;
    font-weight: 600;
    color: #1c1c36;
    margin: 0 0 12px;
}

.text-center {
    text-align: center;
}

.text-desc {
    font-size: 14px;
    color: #555;
    line-height: 1.8;
    margin: 0;
}

.divider {
    width: 100%;
    height: 1px;
    background: #f3f3fb;
    margin: 28px 0;
}

.contact-row {
    display: flex;
    margin: 10px 0;
    font-size: 14px;
}

.label {
    width: 80px;
    color: #777;
}

.value {
    color: #333;
}

.github-text {
    word-break: break-all;
    color: #6355d8;
}

.stat-grid {
    display: flex;
    gap: 24px;
}

.stat-item {
    text-align: center;
    padding: 16px 24px;
    background: #f9f8ff;
    border-radius: 12px;
    flex: 1;
}

.num {
    font-size: 24px;
    font-weight: 600;
    color: #6355d8;
    margin-bottom: 6px;
}

.tip {
    font-size: 13px;
    color: #888;
}

/* 打赏区域居中 */
.reward-wrap {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    margin-top: 8px;
}

.reward-img {
    width: 160px;
    height: 160px;
    border-radius: 12px;
    object-fit: cover;
}

.reward-tip {
    font-size: 13px;
    color: #999;
    margin: 0;
}

/* 暗黑模式适配，和全站统一 */
body.dark-mode .page-title {
    color: var(--color-dark-text);
}

body.dark-mode .about-card {
    background: var(--color-dark-surface);
    box-shadow: var(--shadow-card-dark);
}

body.dark-mode .card-subtitle {
    color: var(--color-dark-text);
}

body.dark-mode .text-desc,
body.dark-mode .label {
    color: #aaa;
}

body.dark-mode .value {
    color: #eee;
}

body.dark-mode .divider {
    background: #333344;
}

body.dark-mode .stat-item {
    background: #29273a;
}

body.dark-mode .reward-tip {
    color: #bbb;
}
</style>