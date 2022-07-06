package com.example.mypractice.commons.util;

import com.example.mypractice.commons.constant.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 修改redis的小工具
 *
 * @author 张贝易
 */
@Component
public class RedisUtil {
    @Autowired
    private ValueOperations<String, Object> valueOperation;

    public void updateRedisToken(Long userId, String token) {
        valueOperation.set(Headers.AUTH_TOKEN + ":" + userId, token, 30, TimeUnit.MINUTES);
    }

    public void disableRedisToken(Long userId) {
        valueOperation.getAndDelete(Headers.AUTH_TOKEN + ":" + userId);
    }

    public Object getToken(Long userId) {
        return valueOperation.get(Headers.AUTH_TOKEN + ":" + userId);
    }
}
