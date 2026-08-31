<template>
    <el-dialog :model-value="modelValue" :title="t('article.share.title')" width="620px" :close-on-click-modal="true"
        top="8vh" destroy-on-close @update:model-value="onClose">
        <div class="share-dialog">
            <!-- 选项步骤：先选分享方式 -->
            <div v-if="step === 'menu'" class="share-menu">
                <div class="share-option" @click="goQr">
                    <span class="option-icon qr">
                        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <rect x="3" y="3" width="7" height="7"></rect>
                            <rect x="14" y="3" width="7" height="7"></rect>
                            <rect x="3" y="14" width="7" height="7"></rect>
                            <path d="M14 14h3v3h-3z"></path>
                            <path d="M21 14v3h-3"></path>
                        </svg>
                    </span>
                    <span class="option-text">
                        <span class="option-title">{{ t('article.share.qr') }}</span>
                        <span class="option-desc">{{ t('article.share.qrTip') }}</span>
                    </span>
                    <svg class="option-arrow" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><polyline points="9 18 15 12 9 6"></polyline></svg>
                </div>

                <div class="share-option" @click="goCopy">
                    <span class="option-icon copy">
                        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <rect x="9" y="9" width="13" height="13" rx="2"></rect>
                            <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                        </svg>
                    </span>
                    <span class="option-text">
                        <span class="option-title">{{ t('article.share.copy') }}</span>
                        <span class="option-desc">{{ shareUrl }}</span>
                    </span>
                    <svg class="option-arrow" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><polyline points="9 18 15 12 9 6"></polyline></svg>
                </div>

                <div class="share-option" @click="goPdf">
                    <span class="option-icon pdf">
                        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                            <polyline points="14 2 14 8 20 8"></polyline>
                            <path d="M9 16v-4h2a1 1 0 0 1 1 1v2a1 1 0 0 1-1 1z"></path>
                        </svg>
                    </span>
                    <span class="option-text">
                        <span class="option-title">{{ t('article.share.pdf') }}</span>
                        <span class="option-desc">{{ t('article.share.pdfTip') }}</span>
                    </span>
                    <svg class="option-arrow" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><polyline points="9 18 15 12 9 6"></polyline></svg>
                </div>
            </div>

            <!-- 二维码步骤 -->
            <div v-else-if="step === 'qr'" class="share-panel">
                <div class="panel-head">
                    <span class="back-btn" @click="backMenu">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><polyline points="15 18 9 12 15 6"></polyline></svg>
                        {{ t('common.back') }}
                    </span>
                    <span class="panel-title">{{ t('article.share.qr') }}</span>
                </div>
                <div class="qr-stage">
                    <div class="qr-card" ref="qrCardRef">
                        <div class="qr-site">{{ SITE_NAME }}</div>
                        <img v-if="qrDataUrl" :src="qrDataUrl" alt="QR" class="qr-img" />
                        <div class="qr-tip">{{ t('article.share.qrTip') }}</div>
                    </div>
                </div>
                <div class="panel-actions">
                    <el-button type="primary" plain :icon="Download" @click="downloadQr">{{ t('article.share.qrDownload') }}</el-button>
                    <el-button type="primary" :icon="Link" @click="copyLink">{{ t('article.share.copyBtn') }}</el-button>
                </div>
            </div>

            <!-- 复制链接步骤 -->
            <div v-else-if="step === 'copy'" class="share-panel">
                <div class="panel-head">
                    <span class="back-btn" @click="backMenu">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><polyline points="15 18 9 12 15 6"></polyline></svg>
                        {{ t('common.back') }}
                    </span>
                    <span class="panel-title">{{ t('article.share.copy') }}</span>
                </div>

                <div class="copy-stage">
                    <div class="copy-box">
                        <span class="copy-label">{{ t('article.share.copy') }}</span>
                        <div class="copy-input-row">
                            <input :value="shareUrl" readonly class="copy-input" @focus="onSelectInput" />
                            <el-button type="primary" :icon="Link" @click="copyLink">{{ t('article.share.copyBtn') }}</el-button>
                        </div>
                    </div>

                    <div class="social-tip">
                        <div class="tip-line">
                            <span class="tip-label">{{ t('article.share.subtitle') }}</span>
                            <span class="tip-url">{{ shareUrl }}</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- PDF 封面步骤 -->
            <div v-else-if="step === 'pdf'" class="share-panel">
                <div class="panel-head">
                    <span class="back-btn" @click="backMenu">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><polyline points="15 18 9 12 15 6"></polyline></svg>
                        {{ t('common.back') }}
                    </span>
                    <span class="panel-title">{{ t('article.share.pdf') }}</span>
                </div>

                <div class="pdf-stage">
                    <canvas ref="coverCanvasRef" class="cover-canvas"></canvas>
                    <div class="cover-actions">
                        <el-button type="primary" :loading="pdfLoading" :icon="Document" @click="downloadPdf">{{ t('article.share.pdfBtn') }}</el-button>
                        <el-button plain :icon="Download" @click="downloadPng">{{ t('article.share.preview') }} PNG</el-button>
                    </div>
                </div>
            </div>
        </div>
    </el-dialog>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Download, Link } from '@element-plus/icons-vue'
import { SITE_NAME } from '~/utils/seo'

const props = defineProps({
    modelValue: { type: Boolean, default: false },
    title: { type: String, default: '' },
    summary: { type: String, default: '' },
    articleId: { type: [Number, String], default: '' }
})
const emit = defineEmits(['update:modelValue'])

const { t } = useI18n()
const route = useRoute()

const step = ref('menu')
const shareUrl = ref('')
const copyDraft = ref('')
const qrDataUrl = ref('')
const qrCardRef = ref(null)
const coverCanvasRef = ref(null)
const pdfLoading = ref(false)

const buildShareUrl = () => {
    const base = window.location.origin
    const id = props.articleId || route.query.id || ''
    return `${base}/article?id=${id}`
}

// 进入/打开弹窗时重置
watch(() => props.modelValue, (val) => {
    if (val) {
        step.value = 'menu'
        shareUrl.value = buildShareUrl()
        copyDraft.value = buildShareUrl()
    }
})

const onClose = (val) => { emit('update:modelValue', val) }

const goQr = async () => {
    step.value = 'qr'
    await nextTick()
    try {
        const QRCode = (await import('qrcode')).default
        qrDataUrl.value = await QRCode.toDataURL(shareUrl.value, {
            width: 220,
            margin: 2,
            color: { dark: '#1d1d1f', light: '#ffffff' },
            errorCorrectionLevel: 'M'
        })
    } catch (e) {
        ElMessage.error(t('article.share.qrFail') || '二维码生成失败')
    }
}

const goCopy = () => {
    step.value = 'copy'
    copyDraft.value = shareUrl.value
}

const goPdf = () => {
    step.value = 'pdf'
    nextTick(() => drawCover())
}

const backMenu = () => { step.value = 'menu' }

const onSelectInput = (e) => { e.target.select() }

const copyLink = async () => {
    try {
        await navigator.clipboard.writeText(copyDraft.value || shareUrl.value)
        ElMessage.success(t('article.share.copySuccess'))
    } catch (e) {
        // 降级：选中输入框让用户手动复制
        ElMessage.warning(t('article.share.copyFail'))
        copyDraft.value = shareUrl.value
    }
}

const downloadQr = () => {
    if (!qrDataUrl.value) return
    const a = document.createElement('a')
    a.href = qrDataUrl.value
    a.download = `qr-${props.articleId || 'article'}.png`
    a.click()
}

// 绘制封面图（canvas 2K 分辨率）
const drawCover = () => {
    const canvas = coverCanvasRef.value
    if (!canvas) return
    const W = 1200
    const H = 1600
    canvas.width = W
    canvas.height = H
    const ctx = canvas.getContext('2d')

    // 背景渐变（品牌紫）
    const grad = ctx.createLinearGradient(0, 0, 0, H)
    grad.addColorStop(0, '#5B4FCF')
    grad.addColorStop(0.6, '#7B62E0')
    grad.addColorStop(1, '#2E2A5A')
    ctx.fillStyle = grad
    ctx.fillRect(0, 0, W, H)

    // 顶部站点标识
    ctx.fillStyle = 'rgba(255,255,255,0.7)'
    ctx.font = '600 40px sans-serif'
    ctx.fillText('JAY.BLOG', 80, 110)

    // 分隔线
    ctx.strokeStyle = 'rgba(255,255,255,0.35)'
    ctx.lineWidth = 2
    ctx.beginPath()
    ctx.moveTo(80, 145)
    ctx.lineTo(W - 80, 145)
    ctx.stroke()

    // 标题（自动换行）
    ctx.fillStyle = '#FFFFFF'
    ctx.textBaseline = 'top'
    const titleText = props.title || 'Untitled'
    let lineY = 260
    const maxWidth = W - 160
    ctx.font = '700 64px sans-serif'
    const lines = wrapText(ctx, titleText, maxWidth)
    lines.slice(0, 6).forEach(line => {
        ctx.fillText(line, 80, lineY)
        lineY += 92
    })

    // 摘要
    if (props.summary) {
        lineY += 20
        ctx.fillStyle = 'rgba(255,255,255,0.85)'
        ctx.font = '400 36px sans-serif'
        const sumLines = wrapText(ctx, props.summary, maxWidth)
        sumLines.slice(0, 3).forEach(line => {
            ctx.fillText(line, 80, lineY)
            lineY += 60
        })
    }

    // 底部品牌标签 + URL
    ctx.fillStyle = 'rgba(255,255,255,0.9)'
    ctx.font = '500 34px sans-serif'
    ctx.fillText('分享自 Jay.Blog', 80, H - 130)
    ctx.fillStyle = 'rgba(255,255,255,0.6)'
    ctx.font = '400 28px sans-serif'
    ctx.fillText(shareUrl.value || '', 80, H - 80)

    // 右下角小二维码
    if (qrDataUrl.value) {
        const qrImg = new Image()
        qrImg.onload = () => {
            const qs = 180
            ctx.drawImage(qrImg, W - qs - 80, H - qs - 80, qs, qs)
        }
        qrImg.src = qrDataUrl.value
    }
}

const wrapText = (ctx, text, maxWidth) => {
    const lines = []
    let current = ''
    for (const ch of text) {
        const test = current + ch
        if (ctx.measureText(test).width > maxWidth && current) {
            lines.push(current)
            current = ch
        } else {
            current = test
        }
    }
    if (current) lines.push(current)
    return lines
}

const downloadPng = () => {
    drawCover()
    // 等待二维码异步绘制完成
    setTimeout(() => {
        const url = coverCanvasRef.value.toDataURL('image/png')
        const a = document.createElement('a')
        a.href = url
        a.download = `cover-${props.articleId || 'article'}.png`
        a.click()
    }, 300)
}

const downloadPdf = async () => {
    pdfLoading.value = true
    drawCover()
    await new Promise(r => setTimeout(r, 300))
    try {
        const { jsPDF } = await import('jspdf')
        const url = coverCanvasRef.value.toDataURL('image/png')
        const pdf = new jsPDF({ orientation: 'portrait', unit: 'px', format: [1200, 1600], hotfixes: ['px_scaling'] })
        pdf.addImage(url, 'PNG', 0, 0, 1200, 1600)
        pdf.save(`cover-${props.articleId || 'article'}.pdf`)
        ElMessage.success(t('article.share.pdfSuccess'))
    } catch (e) {
        ElMessage.error(t('article.share.pdfFail'))
        console.error(e)
    } finally {
        pdfLoading.value = false
    }
}
</script>

<style scoped>
.share-dialog {
    padding: 0;
}

/* 菜单选项 */
.share-option {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px 16px;
    border-radius: 16px;
    cursor: pointer;
    transition: all 0.2s;
    border: 1px solid transparent;
}

.share-option:hover {
    background: rgba(91, 79, 207, 0.06);
    border-color: rgba(91, 79, 207, 0.15);
}

.option-icon {
    width: 52px;
    height: 52px;
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    flex-shrink: 0;
}

.option-icon.qr { background: #5B4FCF; }
.option-icon.copy { background: #409EFF; }
.option-icon.pdf { background: #E6A23C; }

.option-text {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.option-title {
    font-size: 17px;
    font-weight: 600;
    color: #1d1d1f;
}

.option-desc {
    font-size: 13px;
    color: #86868b;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.option-arrow {
    color: #c0c4cc;
    flex-shrink: 0;
}

/* 面板通用 */
.panel-head {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
}

.back-btn {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: #86868b;
    cursor: pointer;
    transition: color 0.2s;
}

.back-btn:hover { color: #5B4FCF; }

.panel-title {
    font-size: 18px;
    font-weight: 600;
    color: #1d1d1f;
}

/* 二维码 */
.qr-stage {
    display: flex;
    justify-content: center;
    padding: 20px 0;
}

.qr-card {
    width: 280px;
    padding: 20px;
    border: 1px solid rgba(0,0,0,0.08);
    border-radius: 16px;
    text-align: center;
    background: #fff;
}

.qr-site {
    font-size: 14px;
    font-weight: 600;
    color: #5B4FCF;
    margin-bottom: 12px;
    letter-spacing: 1px;
}

.qr-img {
    width: 220px;
    height: 220px;
    display: block;
    margin: 0 auto;
    border-radius: 8px;
}

.qr-tip {
    font-size: 12px;
    color: #86868b;
    margin-top: 12px;
}

/* 复制 */
.copy-stage {
    padding: 10px 0;
}

.copy-box {
    background: #f7f7fa;
    border: 1px solid rgba(0,0,0,0.06);
    border-radius: 16px;
    padding: 20px;
}

.copy-label {
    display: block;
    font-size: 13px;
    font-weight: 500;
    color: #515154;
    margin-bottom: 12px;
}

.copy-input-row {
    display: flex;
    gap: 12px;
}

.copy-input {
    flex: 1;
    padding: 10px 14px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    font-size: 14px;
    color: #515154;
    outline: none;
    background: #fff;
}

.copy-input:focus { border-color: #5B4FCF; }

/* PDF 封面 */
.pdf-stage {
    text-align: center;
}

.cover-canvas {
    width: 100%;
    max-width: 280px;
    border-radius: 16px;
    box-shadow: 0 10px 30px rgba(0,0,0,0.15);
}

.cover-actions {
    display: flex;
    justify-content: center;
    gap: 12px;
    margin-top: 20px;
}

/* 暗色模式 */
body.dark-mode .option-title { color: #f5f5f7; }
body.dark-mode .option-desc { color: #909399; }
body.dark-mode .share-option:hover {
    background: rgba(179,167,255,0.1);
    border-color: rgba(179,167,255,0.2);
}
body.dark-mode .panel-title { color: #f5f5f7; }
body.dark-mode .copy-box { background: rgba(255,255,255,0.05); border-color: rgba(255,255,255,0.1); }
body.dark-mode .copy-input { background: #2a2a31; border-color: rgba(255,255,255,0.12); color: #d0d0d6; }
body.dark-mode .back-btn:hover { color: #b3a7ff; }
</style>
