package com.example.mypractice.service;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.example.mypractice.commons.util.SnowflakeIdGenerator;
import com.example.mypractice.dao.EsMusicListDao;
import com.example.mypractice.dao.SqlMusicListDao;
import com.example.mypractice.model.database.MusicList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class MusicListService {
    @Autowired
    private SqlMusicListDao sqlMusicListDao;
    @Autowired
    private EsMusicListDao esMusicListDao;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    public MusicList selectMusicListById(MusicList musicList) {
        return sqlMusicListDao.selectMusicListById(musicList);
    }

    public List<MusicList> selectMusicListByName(MusicList musicList, Long lastIndex, Double lastScore) throws IOException, ElasticsearchException {
        return esMusicListDao.selectMusicListByName(musicList, lastIndex, lastScore);
    }

    public Long selectMusicListCountByName(MusicList musicList) throws IOException, ElasticsearchException {
        return esMusicListDao.selectCountByName(musicList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void creatMusicList(MusicList musicList) throws IOException, ElasticsearchException {
        musicList.setId(snowflakeIdGenerator.nextId());
        musicList.initMusicList();
        sqlMusicListDao.addMusicList(musicList);
        esMusicListDao.addMusicList(musicList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMusicList(MusicList musicList) throws IOException, ElasticsearchException {
        sqlMusicListDao.deleteMusicList(musicList);
        esMusicListDao.deleteMusicList(musicList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMusicList(MusicList musicList) throws IOException, ElasticsearchException {
        sqlMusicListDao.updateMusicList(musicList);
        esMusicListDao.updateMusicList(musicList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addMusicListToLike(List<Long> ids, Long userId) throws IOException, ElasticsearchException {
        sqlMusicListDao.addListToLike(ids, userId);
        esMusicListDao.addMusicListToLike(ids, userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMusicListFromLike(List<Long> ids, Long userId) throws IOException, ElasticsearchException {
        sqlMusicListDao.deleteListFromLike(ids, userId);
        esMusicListDao.deleteMusicListFromLike(ids, userId);
    }

    public List<MusicList> selectMusicListByAuthorId(MusicList musicList, Long lastIndex) {
        return sqlMusicListDao.selectMusicListByAuthorId(musicList, lastIndex);
    }

    public List<MusicList> selectMusicListFromLike(Long lastIndex, Long userId) {
        return sqlMusicListDao.selectListFromLike(lastIndex, userId);
    }

    public Integer selectCountByAuthorId(MusicList musicList) {
        return sqlMusicListDao.selectMusicListCountByAuthorId(musicList);
    }

    public Integer selectCountFromLike(Long userId) {
        return sqlMusicListDao.selectListCountFromLike(userId);
    }

    public List<MusicList> selectAllMusicList(Long lastIndex) {
        return sqlMusicListDao.selectAllMusicList(lastIndex);
    }

    public boolean ifLiked(Long listId, Long userId) {
        return sqlMusicListDao.ifLiked(listId, userId) > 0;
    }

    public Integer selectAllCount() {
        return sqlMusicListDao.selectAllMusicListCount();
    }

    @Transactional(rollbackFor = Exception.class)
    public void banMusicList(MusicList musicList) throws IOException, ElasticsearchException {
        sqlMusicListDao.banMusicList(musicList);
        esMusicListDao.banMusicList(musicList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unsealMusicList(MusicList musicList) throws IOException, ElasticsearchException {
        sqlMusicListDao.unsealMusicList(musicList);
        esMusicListDao.unsealMusicList(musicList);
    }

    public boolean ifCreated(Long listId, Long userId) {
        int i = sqlMusicListDao.ifCreated(listId, userId);
        return i > 0;
    }
}
