package com.example.mypractice.commons.config;

import com.example.mypractice.filter.web.AdminInterceptor;
import com.example.mypractice.filter.web.CrossOriginInterceptor;
import com.example.mypractice.filter.web.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private TokenInterceptor tokenInterceptor;
    @Autowired
    private AdminInterceptor adminInterceptor;
    @Autowired
    private CrossOriginInterceptor crossOriginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(crossOriginInterceptor).addPathPatterns("/**");
        registry.addInterceptor(tokenInterceptor).excludePathPatterns("/unAuth/**","/static/**").addPathPatterns("/**");
        registry.addInterceptor(adminInterceptor).addPathPatterns("/admin/**");
    }
}
