package com.example.mypractice.commons.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcExtendConfig implements WebMvcConfigurer {
    @Value("${myconf.mv.baseUrl}")
    private String mvBaseUrl;
    @Value("${myconf.music.baseUrl}")
    private String musicBaseUrl;
    @Value("${myconf.cover.baseUrl}")
    private String coverBaseUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/music/**").addResourceLocations("file:" + musicBaseUrl);
        registry.addResourceHandler("/static/mv/**").addResourceLocations("file:" + mvBaseUrl);
        registry.addResourceHandler("/static/cover/**").addResourceLocations("file:" + coverBaseUrl);
    }
}
