package com.example.mypractice.filter.web;

import com.example.mypractice.commons.constant.Bodies;
import com.example.mypractice.model.database.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * 检测是否为管理员
 *
 * @author 张贝易
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        int identity = (int) request.getAttribute(Bodies.IDENTITY);
        if (identity == User.ADMIN) {
            return true;
        }
        HashMap<String, Object> result = new HashMap<>(1);
        result.put(Bodies.MESSAGE, Bodies.ACCESS_DENY);
        //提示前端没有权限
        try (ServletOutputStream responseOutputStream = response.getOutputStream()) {
            responseOutputStream.write(objectMapper.writeValueAsBytes(result));
        }
        return false;
    }
}
