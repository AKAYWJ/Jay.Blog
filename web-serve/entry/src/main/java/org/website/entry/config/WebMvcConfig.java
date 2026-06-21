package org.website.entry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.website.entry.interceptor.LoginInterceptor;
import org.website.entry.interceptor.PermissionInterceptor;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public HandlerInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    @Bean
    public HandlerInterceptor permissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/hello",
                        "/auth/login",
                        "/auth/register",
                        "/static/**",
                        "/favicon.ico",
                        "/upload/test",
                        "/upload/cover",
                        "/comment/front/list",
                        "/article/list",
                        "/category/front/list",
                        "/category/all",
                        "/blogConfig/get",
                        "/article/*",
                        "/article/front/hot",
                        "/article/front/list",
                        "/article/front/detail/*",
                        "/visitor/front/add",
                        "/stat/front"
                );

        registry.addInterceptor(permissionInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/hello",
                        "/auth/login",
                        "/auth/register",
                        "/static/**",
                        "/favicon.ico",
                        "/upload/test",
                        "/upload/cover",
                        "/comment/front/list",
                        "/article/list",
                        "/category/front/list",
                        "/category/all",
                        "/blogConfig/get",
                        "/article/*",
                        "/article/front/hot",
                        "/article/front/list",
                        "/article/front/detail/*",
                        "/visitor/front/add",
                        "/stat/front"
                );
    }

    // 静态资源映射，独立处理图片访问，不受拦截器限制
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/cover/**")
                .addResourceLocations("file:/Users/yewenjie/项目资源/MyBlog/fileupload/cover/");
    }
}