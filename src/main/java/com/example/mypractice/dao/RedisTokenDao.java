package com.example.mypractice.dao;

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
public class RedisTokenDao {
    @Autowired
    private ValueOperations<String, Object> valueOperation;
    private static final String AUTH_TOKEN = "AUTH_TOKEN";

    public void updateRedisToken(Long userId, String token) {
        valueOperation.set(AUTH_TOKEN + ":" + userId, token, 30, TimeUnit.MINUTES);
    }

    public void disableRedisToken(Long userId) {
        valueOperation.getAndDelete(AUTH_TOKEN + ":" + userId);
    }

    public Object getToken(Long userId) {
        return valueOperation.get(AUTH_TOKEN + ":" + userId);
    }
}
