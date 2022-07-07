package com.example.mypractice.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class RedisDao {
    @Autowired
    private EsMusicListDao esMusicListDao;
    @Autowired
    private SqlMusicListDao sqlMusicListDao;
    @Autowired
    private EsMusicDao esMusicDao;
    @Autowired
    private HashOperations<String, Object, Object> hashOperation;
    private static final String LIST_LIKE_COUNT = "LIST_LIKE_COUNT";
    private static final String MUSIC_LIKE_COUNT = "MUSIC_LIKE_COUNT";
    private static final String LIST_MUSIC_COUNT = "LIST_MUSIC_COUNT";
    private static final String LIST_COVER_URL = "LIST_COVER_URL";
    private final ReentrantReadWriteLock musicLock = new ReentrantReadWriteLock(true);
    private final ReentrantReadWriteLock musicListLock = new ReentrantReadWriteLock(true);

    public void listLikeIncrease(List<Long> musicListIds) {
        ReentrantReadWriteLock.ReadLock readLock = musicListLock.readLock();
        readLock.lock();
        try {
            for (Long id : musicListIds) {
                hashOperation.increment(LIST_LIKE_COUNT, id, 1);
            }
        } finally {
            readLock.unlock();
        }
    }

    public void listLikeDecrease(List<Long> musicListIds) {
        ReentrantReadWriteLock.ReadLock readLock = musicListLock.readLock();
        readLock.lock();
        try {
            for (Long id : musicListIds) {
                hashOperation.increment(LIST_LIKE_COUNT, id, -1);
            }
        } finally {
            readLock.unlock();
        }
    }

    public void listMusicCountChange(Long id, int count) {
        ReentrantReadWriteLock.ReadLock readLock = musicListLock.readLock();
        readLock.lock();
        try {
            hashOperation.increment(LIST_MUSIC_COUNT, id, count);
        } finally {
            readLock.unlock();
        }
    }

    public void listCoverAdd(Long listId, String coverUrl) {
        ReentrantReadWriteLock.ReadLock readLock = musicListLock.readLock();
        readLock.lock();
        try {
            hashOperation.put(LIST_COVER_URL, listId, coverUrl);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 将redis中的歌单数据放到MySQL和ES中持久化
     *
     * @throws IOException
     */
    public void musicListUpdate() throws IOException {
        ReentrantReadWriteLock.WriteLock writeLock = musicListLock.writeLock();
        writeLock.lock();
        try {
            Map<Object, Object> musicCounts = hashOperation.entries(LIST_MUSIC_COUNT);
            Map<Object, Object> urls = hashOperation.entries(LIST_COVER_URL);
            Map<Object, Object> likeCounts = hashOperation.entries(LIST_LIKE_COUNT);
            sqlMusicListDao.updateCoverUrl(urls);
            esMusicListDao.updateCoverUrl(urls);
            hashOperation.delete(LIST_COVER_URL, urls.keySet());
            sqlMusicListDao.updateMusicCount(musicCounts);
            esMusicListDao.updateMusicCount(musicCounts);
            hashOperation.delete(LIST_MUSIC_COUNT, musicCounts.keySet());
            sqlMusicListDao.updateLikeCount(likeCounts);
            esMusicListDao.updateLikeCount(likeCounts);
            hashOperation.delete(LIST_LIKE_COUNT, likeCounts.keySet());
        } finally {
            writeLock.unlock();
        }
    }

    public void musicLikeIncrease(List<Long> musicIds) {
        ReentrantReadWriteLock.ReadLock readLock = musicLock.readLock();
        readLock.lock();
        try {
            for (Long id : musicIds) {
                hashOperation.increment(MUSIC_LIKE_COUNT, id, 1);
            }
        } finally {
            readLock.unlock();
        }
    }

    public void musicLikeDecrease(List<Long> musicIds) {
        ReentrantReadWriteLock.ReadLock readLock = musicLock.readLock();
        readLock.lock();
        try {
            for (Long id : musicIds) {
                hashOperation.increment(MUSIC_LIKE_COUNT, id, -1);
            }
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 将redis中的歌曲数据放到MySQL和ES中持久化
     *
     * @throws IOException
     */
    public void musicUpdate() throws IOException {
        ReentrantReadWriteLock.WriteLock writeLock = musicLock.writeLock();
        writeLock.lock();
        try {
            Map<Object, Object> counts = hashOperation.entries(MUSIC_LIKE_COUNT);
            esMusicDao.updateMusicLike(counts);
            hashOperation.delete(MUSIC_LIKE_COUNT, counts.keySet());
        } finally {
            writeLock.unlock();
        }
    }
}
