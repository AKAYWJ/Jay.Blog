package org.website.entry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
                .addResourceLocations("file:/home/person/JayBlog/fileupload/cover/");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*") //允许跨域的域名，可以用*表示允许任何域名使用
                        .allowedMethods("*") //允许任何方法（post、get等）
                        .allowedHeaders("*") //允许任何请求头
                        .allowCredentials(true) //带上cookie信息
                        .exposedHeaders(HttpHeaders.SET_COOKIE)
                        .maxAge(3600L); //maxAge(3600)表明在3600秒内，不需要再发送预检验请求，可以缓存该结果
            }
        };

}}