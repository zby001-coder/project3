package com.example.mypractice.commons.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

/**
 * 注入自定义的Jackson的ObjectMapper，让它序列化Long的时候写成String
 * 防止前端精度丢失
 *
 * @author 张贝易
 */
@Configuration
public class ObjectMapperConfig {
    /**
     * 序列化器
     */
    private static final JsonSerializer<Long> SERIALIZER = new JsonSerializer<Long>() {
        @Override
        public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }
            gen.writeString(value.toString());
        }
    };

    /**
     * 注入ObjectMapper
     *
     * @return 自定义的ObjectMapper
     */
    @Bean
    public ObjectMapper getObjectMapper() {
        Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder = new Jackson2ObjectMapperBuilder();
        jackson2ObjectMapperBuilder.serializerByType(Long.TYPE, SERIALIZER);
        jackson2ObjectMapperBuilder.serializerByType(Long.class, SERIALIZER);
        return jackson2ObjectMapperBuilder.build();
    }
}
