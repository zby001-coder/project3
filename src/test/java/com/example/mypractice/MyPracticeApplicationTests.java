package com.example.mypractice;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.example.mypractice.commons.util.SnowflakeIdGenerator;
import com.example.mypractice.dao.EsMusicDao;
import com.example.mypractice.dao.EsMusicListDao;
import com.example.mypractice.dao.SqlMusicListDao;
import com.example.mypractice.dao.UserDao;
import com.example.mypractice.model.database.Music;
import com.example.mypractice.model.database.MusicList;
import com.example.mypractice.model.database.User;
import com.example.mypractice.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

@SpringBootTest
class MyPracticeApplicationTests {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    UserDao userDao;
    @Autowired
    UserService userService;
    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    EsMusicDao esMusicDao;
    @Autowired
    SqlMusicListDao sqlMusicListDao;
    @Autowired
    EsMusicListDao esMusicListDao;
    // Create the low-level client
    RestClient restClient = RestClient.builder(
            new HttpHost("localhost", 9200)).build();
    // Create the transport with a Jackson mapper
    ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());
    // And create the API client
    ElasticsearchClient client = new ElasticsearchClient(transport);

    
    void contextLoads() throws Exception {
        System.out.println(objectMapper.writeValueAsString(new Music()));
    }

    
    void test1() {

    }
}
