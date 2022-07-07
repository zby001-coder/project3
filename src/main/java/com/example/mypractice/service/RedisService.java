package com.example.mypractice.service;

import com.example.mypractice.dao.RedisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class RedisService {
    @Autowired
    private RedisDao redisDao;

    /**
     * 定时更新歌单数据
     *
     * @throws IOException
     */
    @Scheduled(fixedDelay = 10000)
    @Transactional(rollbackFor = Exception.class)
    public void storeMusicList() throws IOException {
        redisDao.musicListUpdate();
    }

    /**
     * 定时更新歌曲数据
     *
     * @throws IOException
     */
    @Scheduled(fixedDelay = 10000)
    @Transactional(rollbackFor = Exception.class)
    public void storeMusic() throws IOException {
        redisDao.musicUpdate();
    }
}
