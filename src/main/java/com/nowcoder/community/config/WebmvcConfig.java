package com.nowcoder.community.config;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.controller.interceptor.AlphaInterceptor;
import com.nowcoder.community.controller.interceptor.LoginRequiredInterceptor;
import com.nowcoder.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebmvcConfig implements WebMvcConfigurer {
    @Autowired
    private AlphaInterceptor alphaInterceptor;
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;
    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //demo拦截器
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/**/*.jpg","/**/*.png","/**/*.js","/**/*.css")/*拦截器排除静态资源，即不拦截*/
                .addPathPatterns("/login");//拦截的路径
        //注册登录ticket的拦截器
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.jpg","/**/*.png","/**/*.js","/**/*.css");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.jpg","/**/*.png","/**/*.js","/**/*.css");
    }
}
