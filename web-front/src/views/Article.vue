<template>
    <div class="article-management">
        <!-- 页面标题 -->
        <h1 class="page-title">文章管理</h1>

        <!-- 搜索区域 -->
        <div class="search-card">
            <el-form :model="searchForm" inline @submit.native.prevent="handleSearch">
                <el-form-item label="文章标题">
                    <el-input v-model="searchForm.title" placeholder="请输入文章标题" clearable
                        style="width: 220px"></el-input>
                </el-form-item>
                <el-form-item label="文章分类">
                    <el-select v-model="searchForm.categoryId" placeholder="全部分类" clearable style="width: 180px">
                        <el-option v-for="c in categoryList" :key="c.id" :label="c.name" :value="c.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="发布状态">
                    <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
                        <el-option label="草稿" value="1"></el-option>
                        <el-option label="已发布" value="2"></el-option>
                        <el-option label="私密" value="3"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
                    <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
                </el-form-item>
            </el-form>
        </div>

        <!-- 操作按钮 + 表格区域 -->
        <div class="table-card">
            <div class="table-header">
                <el-button type="primary" icon="el-icon-plus" @click="openDialog('add')">新增文章</el-button>
                <el-button type="danger" icon="el-icon-delete" @click="handleBatchDelete"
                    :disabled="selectedIds.length === 0">批量删除</el-button>
            </div>

            <el-table :data="articleList" border stripe v-loading="loading" @selection-change="handleSelectionChange"
                style="width: 100%">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" label="ID" width="70" align="center"></el-table-column>
                <el-table-column prop="title" label="文章标题" min-width="220" align="left"></el-table-column>
                <el-table-column prop="categoryName" label="所属分类" width="120" align="center"></el-table-column>
                <el-table-column label="文章标签" width="160" align="center">
                    <template slot-scope="scope">
                        <span v-if="scope.row.tagNameList && scope.row.tagNameList.length > 0">
                            <el-tag v-for="tag in scope.row.tagNameList" :key="tag" size="mini" style="margin:0 2px;">{{
                                tag }}</el-tag>
                        </span>
                        <span v-else>-</span>
                    </template>
                </el-table-column>
                <el-table-column label="置顶状态" width="90" align="center">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.isTop === 1" type="warning" size="small">置顶</el-tag>
                        <span v-else>-</span>
                    </template>
                </el-table-column>
                <el-table-column label="发布状态" width="100" align="center">
                    <template slot-scope="scope">
                        <el-tag :type="scope.row.status === 2 ? 'success' : scope.row.status === 1 ? 'info' : 'danger'"
                            size="small">
                            {{ scope.row.status === 1 ? '草稿' : scope.row.status === 2 ? '已发布' : '私密' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="viewCount" label="浏览量" width="90" align="center"></el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="170" align="center">
                    <template slot-scope="scope">
                        {{ formatDate(scope.row.createTime) }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" align="center">
                    <template slot-scope="scope">
                        <el-button type="text" icon="el-icon-edit" @click="openDialog('edit', scope.row)">编辑</el-button>
                        <el-button type="text" icon="el-icon-delete" style="color: #ef4444"
                            @click="handleDelete(scope.row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination-container">
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="pageNum" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
            </div>
        </div>

        <!-- 新增/编辑文章弹窗 -->
        <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="750px" :close-on-click-modal="false"
            destroy-on-close>
            <el-form ref="articleFormRef" :model="articleForm" :rules="articleRules" label-width="100px">
                <el-form-item label="文章标题" prop="title">
                    <el-input v-model="articleForm.title" placeholder="请输入文章标题"></el-input>
                </el-form-item>
                <el-form-item label="文章分类" prop="categoryId">
                    <el-select v-model="articleForm.categoryId" placeholder="请选择分类" style="width: 100%">
                        <el-option v-for="c in categoryList" :key="c.id" :label="c.name" :value="c.id"></el-option>
                    </el-select>
                </el-form-item>
                <!-- 新增：标签多选 -->
                <el-form-item label="文章标签">
                    <el-select v-model="articleForm.tagIdList" multiple placeholder="可多选标签" style="width:100%">
                        <el-option v-for="t in allTagList" :key="t.id" :label="t.name" :value="t.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="文章摘要" prop="summary">
                    <el-input v-model="articleForm.summary" type="textarea" :rows="2" placeholder="简单描述文章内容"></el-input>
                </el-form-item>
                <el-form-item label="文章封面">
                    <div v-if="articleForm.cover" style="margin-bottom:10px">
                        <img :src="articleForm.cover"
                            style="width:180px;height:110px;object-fit:cover;border-radius:6px;">
                    </div>
                    <el-upload action="" :http-request="uploadFile" :on-success="handleUploadSuccess"
                        accept="image/jpeg,image/png,image/webp">
                        <el-button icon="el-icon-upload">点击上传封面图</el-button>
                    </el-upload>
                    <div style="color:#999;font-size:12px;margin-top:4px">不上传则封面为空</div>
                </el-form-item>
                <el-form-item label="是否置顶">
                    <el-radio-group v-model="articleForm.isTop">
                        <el-radio :label="1">置顶</el-radio>
                        <el-radio :label="0">不置顶</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="发布状态" prop="status">
                    <el-radio-group v-model="articleForm.status">
                        <el-radio :label="1">草稿</el-radio>
                        <el-radio :label="2">发布</el-radio>
                        <el-radio :label="3">私密</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="Markdown内容" prop="contentMd">
                    <mavon-editor ref="md" v-model="articleForm.contentMd" @imgAdd="handleImgAdd" @imgDel="handleImgDel"
                        @change="handleChange" :toolbars="toolbars" />
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getArticleList, addArticle, updateArticle, deleteArticle, batchDeleteArticle } from '@/api/article'
import { getCategoryAll } from '@/api/category'
import { getTagList } from '@/api/tag'
import axios from 'axios'

export default {
    name: 'Article',
    computed: {
        // 从Vuex获取当前登录管理员用户ID
        ...mapGetters(['getUserId']),
        toolbars() {
            return {
                bold: true,
                italic: true,
                header: true,
                code: true,
                quote: true,
                preview: true,
                imagelink: true,
                link: true
            }
        }
    },
    data() {
        return {
            // 搜索表单
            searchForm: { title: '', categoryId: null, status: null },
            articleList: [],
            categoryList: [],
            allTagList: [], // 全部标签下拉数据源
            loading: false,
            pageNum: 1,
            pageSize: 10,
            total: 0,
            selectedIds: [],
            // 弹窗
            dialogVisible: false,
            dialogType: 'add',
            dialogTitle: '新增文章',
            submitLoading: false,
            articleForm: {
                id: null,
                userId: this.getUserId,
                title: '',
                categoryId: null,
                summary: '',
                cover: '',
                contentMd: '',
                contentHtml: '',
                isTop: 0,
                status: 1,
                tagIdList: [] // 绑定标签ID数组，用于多对多关联
            },
            articleRules: {
                title: [{ required: true, message: '文章标题不能为空', trigger: 'blur' }],
                categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
                contentMd: [{ required: true, message: '文章内容不能为空', trigger: 'blur' }]
            }
        }
    },
    mounted() {
        this.getArticleList()
        this.loadCategory()
        this.loadAllTag() // 加载全部标签
    },
    methods: {
        handleChange(md, render) {
            // 图片样式：宽度自适应、等比、圆角、上下外边距、水平居中
            const imgGlobalStyle = "max-width:60%;height:auto;border-radius:12px;margin:16px auto;display:block;object-fit:contain;";
            let processedHtml = render.replace(/<img /g, `<img style="${imgGlobalStyle}" `);
            this.articleForm.contentHtml = processedHtml;
        },
        // md编辑器图片上传修复（修复缺少param变量报错）
        handleImgAdd(pos, file) {
            const limit = 2 * 1024 * 1024
            if (file.size > limit) {
                this.$message.error('图片不能超过2MB')
                return
            }
            const formData = new FormData()
            formData.append('file', file)
            axios.post('/api/upload/cover', formData, {
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('token')
                }
            }).then(res => {
                this.$refs.md.$img2Url(pos, res.data.data)
            }).catch(() => {
                this.$message.error('图片上传失败')
            })
        },

        handleImgDel(pos) {
            // 如需后端删图在此扩展
        },
        uploadFile(param) {
            const file = param.file
            const limit = 2 * 1024 * 1024
            if (file.size > limit) {
                this.$message.error('图片不能超过2MB')
                param.onError(new Error('超出大小'))
                return
            }
            const formData = new FormData()
            formData.append('file', file)
            axios.post('/api/upload/cover', formData, {
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('token')
                }
            }).then(res => {
                param.onSuccess(res.data)
            }).catch(err => {
                this.$message.error('上传失败，请重新登录')
                param.onError(err)
            })
        },

        handleUploadSuccess(res) {
            if (res.code === 200) {
                this.articleForm.cover = res.data
                this.$message.success('封面上传成功')
            } else {
                this.$message.error(res.msg)
            }
        },
        // 加载全部分类（下拉框）
        loadCategory() {
            getCategoryAll().then(res => {
                this.categoryList = res.data
            })
        },
        // 加载全部标签（标签多选下拉）
        loadAllTag() {
            getTagList({ pageNum: 1, pageSize: 999 }).then(res => {
                this.allTagList = res.data.records
            })
        },
        // 查询文章列表（后端返回tagNameList标签名数组用于表格展示）
        getArticleList() {
            this.loading = true
            const params = { pageNum: this.pageNum, pageSize: this.pageSize, ...this.searchForm }
            getArticleList(params).then(res => {
                this.articleList = res.data.records
                this.total = res.data.total
            }).catch(() => {
                this.$message.error('获取文章列表失败')
            }).finally(() => {
                this.loading = false
            })
        },
        handleSearch() {
            this.pageNum = 1
            this.getArticleList()
        },
        handleReset() {
            this.searchForm = { title: '', categoryId: null, status: null }
            this.pageNum = 1
            this.getArticleList()
        },
        handleSizeChange(size) {
            this.pageSize = size
            this.getArticleList()
        },
        handleCurrentChange(page) {
            this.pageNum = page
            this.getArticleList()
        },
        handleSelectionChange(selection) {
            this.selectedIds = selection.map(item => item.id)
        },

        // 弹窗打开
        openDialog(type, row = null) {
            this.dialogType = type
            this.dialogTitle = type === 'add' ? '新增文章' : '编辑文章'
            this.dialogVisible = true
            this.$nextTick(() => {
                this.$refs.articleFormRef?.resetFields()
                if (type === 'add') {
                    // 新增清空，tagIdList置空数组
                    this.articleForm = {
                        id: null,
                        userId: this.getUserId,
                        title: '',
                        categoryId: null,
                        summary: '',
                        cover: '',
                        contentMd: '',
                        contentHtml: '',
                        isTop: 0,
                        status: 1,
                        tagIdList: []
                    }
                } else {
                    // 编辑回显原有数据，后端返回tagIdList标签ID数组
                    this.articleForm = { ...row }
                }
            })
        },
        handleSubmit() {
            this.$refs.articleFormRef.validate(valid => {
                if (!valid) return
                this.submitLoading = true
                const api = this.dialogType === 'add' ? addArticle : updateArticle
                // articleForm内自带tagIdList传给后端做中间表关联
                api(this.articleForm).then(() => {
                    this.$message.success('操作成功')
                    this.dialogVisible = false
                    this.getArticleList()
                }).catch(err => {
                    console.error(err)
                    this.$message.error('操作失败')
                }).finally(() => {
                    this.submitLoading = false
                })
            })
        },

        // 单条删除
        handleDelete(id) {
            this.$confirm('确定删除该文章？删除后不可恢复，关联标签同步清空', '提示', { type: 'warning' }).then(() => {
                deleteArticle(id).then(() => {
                    this.$message.success('删除成功')
                    this.getArticleList()
                })
            }).catch(() => { })
        },
        // 批量删除
        handleBatchDelete() {
            this.$confirm(`确定删除选中的 ${this.selectedIds.length} 篇文章？关联标签同步清空`, '提示', { type: 'warning' }).then(() => {
                batchDeleteArticle(this.selectedIds).then(() => {
                    this.$message.success('批量删除成功')
                    this.getArticleList()
                })
            }).catch(() => { })
        },

        // 时间格式化工具
        formatDate(date) {
            if (!date) return '-'
            const d = new Date(date)
            return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, 0)}-${String(d.getDate()).padStart(2, 0)} ${String(d.getHours()).padStart(2, 0)}:${String(d.getMinutes()).padStart(2, 0)}`
        }
    }
}
</script>

<style scoped>
.article-management {
    padding: 0;
}

.page-title {
    font-size: var(--font-size-page-title);
    font-weight: 600;
    color: var(--color-text-primary);
    margin-bottom: var(--spacing-page);
}

.search-card,
.table-card {
    position: relative;
    overflow: hidden;
    background: var(--liquid-glass-bg);
    backdrop-filter: var(--liquid-glass-blur);
    border: var(--liquid-glass-border);
    border-radius: var(--radius-xl);
    box-shadow: var(--liquid-glass-shadow);
    padding: var(--spacing-card);
    margin-bottom: var(--spacing-gap);
}

.search-card>*,
.table-card>* {
    position: relative;
    z-index: 1;
}

.table-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: var(--spacing-gap);
}

.pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: var(--spacing-gap);
}

/* 暗黑模式适配 */
body.dark-mode .page-title {
    color: var(--color-dark-text);
}

body.dark-mode .search-card,
body.dark-mode .table-card {
    background: var(--liquid-glass-bg);
    border: var(--liquid-glass-border);
    box-shadow: var(--liquid-glass-shadow);
}
</style>