<template>
    <!-- 所有 /front/* 子页面会渲染在这里 -->
    <NuxtPage />
</template>

<script setup>
// 指定当前路由分组使用 layouts/front.vue
definePageMeta({
    layout: 'front'
})
</script>