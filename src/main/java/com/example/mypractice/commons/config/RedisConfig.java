package com.example.mypractice.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * 配置redis
 *
 * @author 张贝易
 */
@Configuration
public class RedisConfig {
    /**
     * 处理需要用JSON序列化/反序列化的对象的RedisTemplate
     *
     * @param redisConnectionFactory redis连接工厂
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 默认 redisTemplate 没有过多的设置, redis 对象都是需要序列化的
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        //设置redisTemplate的序列化和反序列化的工具
        template.setKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /**
     * 处理普通String的RedisTemplate
     *
     * @param redisConnectionFactory redis连接工厂
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
