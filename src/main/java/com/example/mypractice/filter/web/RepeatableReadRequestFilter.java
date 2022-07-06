package com.example.mypractice.filter.web;

import com.example.mypractice.model.web.RepeatableReadingRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 将所有的请求转变为可重复读取流和part的请求
 * 由于希望所有请求都过滤一下，所以直接用注解注入
 *
 * @author 张贝易
 */
@Component
public class RepeatableReadRequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        RepeatableReadingRequest repeatReadRequest = new RepeatableReadingRequest(request);
        //用包装好的request替换原来的，以后请求获取流都可以重复读取
        filterChain.doFilter(repeatReadRequest, response);
    }
}