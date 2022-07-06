package com.example.mypractice.filter.web;

import com.example.mypractice.commons.constant.Bodies;
import com.example.mypractice.commons.constant.Headers;
import com.example.mypractice.commons.constant.Others;
import com.example.mypractice.commons.util.JwtGenerator;
import com.example.mypractice.commons.util.RedisUtil;
import com.example.mypractice.model.web.UserToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 确认用户token的拦截器
 * 在检测用户token后，还会在request中设置一些参数
 * 设置的参数有：用户id、用户身份identity
 *
 * @author 张贝易
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtGenerator jwtGenerator;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(Headers.AUTH_TOKEN);
        if (jwtGenerator.checkUserToken(token)) {
            UserToken userTokenData = jwtGenerator.getUserTokenData(token);
            //从redis里拿token
            Object tokenInRedis = redisUtil.getToken(userTokenData.getId());
            //用户的token应当和redis中的一致，否则就是用旧的token或者多点登录
            if (token.equals(tokenInRedis)) {
                request.setAttribute(Others.USER_ID, userTokenData.getId());
                request.setAttribute(Others.IDENTITY, userTokenData.getIdentity());
                request.setAttribute(Others.USER_NAME, userTokenData.getUserName());
                //更新token
                token = jwtGenerator.updateUserToken(userTokenData);
                response.setHeader(Headers.AUTH_TOKEN, token);
                //更新redis
                redisUtil.updateRedisToken(userTokenData.getId(), token);
                return true;
            }
        }
        //提示前端没有认证
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            HashMap<String, Object> result = new HashMap<>(1);
            result.put(Bodies.MESSAGE, Bodies.ACCESS_DENY);
            outputStream.write(objectMapper.writeValueAsBytes(result));
        }
        return false;
    }

}
