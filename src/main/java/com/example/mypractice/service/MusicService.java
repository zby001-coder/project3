package com.example.mypractice.service;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.example.mypractice.commons.util.SnowflakeIdGenerator;
import com.example.mypractice.dao.EsMusicDao;
import com.example.mypractice.dao.EsMusicListDao;
import com.example.mypractice.dao.SqlMusicDao;
import com.example.mypractice.model.database.Music;
import com.example.mypractice.model.database.MusicList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class MusicService {
    @Autowired
    private EsMusicDao esMusicDao;
    @Autowired
    private SqlMusicDao sqlMusicDao;
    @Autowired
    private EsMusicListDao esMusicListDao;
    @Value("${myconf.mv.baseUrl}")
    private String mvBaseUrl;
    @Value("${myconf.music.baseUrl}")
    private String musicBaseUrl;
    @Value("${myconf.cover.baseUrl}")
    private String coverBaseUrl;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    /**
     * 由于添加音乐时需要同时修改music和lyric表，所以使用事务
     *
     * @param music 音乐对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Music addMusic(Music music, MultipartFile musicContent, MultipartFile mvContent, MultipartFile coverContent) throws IOException, ElasticsearchException {
        music.setId(snowflakeIdGenerator.nextId());
        music.initMusic();
        music.setMusicUrl(generateLogicalMusicPath(music, musicContent));
        music.setMvUrl(mvContent == null ? null : generateLogicalMvPath(music, mvContent));
        music.setCoverUrl(coverContent == null ? null : generateLogicalCoverPath(music, coverContent));
        sqlMusicDao.addMusic(music);
        esMusicDao.addMusic(music);
        writeMusicFiles(music, musicContent, mvContent, coverContent);
        return music;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMusic(Music music) throws IOException, ElasticsearchException {
        music = esMusicDao.selectMusicById(music);
        sqlMusicDao.deleteMusic(music);
        esMusicDao.deleteMusic(music);
        String mvUrl = music.getMvUrl();
        String coverUrl = music.getCoverUrl();
        //如果有文件就删
        deleteFileFromDisk(generatePhysicalMusicPath(music));
        if (mvUrl != null) {
            deleteFileFromDisk(generatePhysicalMvPath(music));
        }
        if (coverUrl != null) {
            deleteFileFromDisk(generatePhysicalCoverPath(music));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMusic(Music music, MultipartFile musicFile, MultipartFile mvFile, MultipartFile coverFile) throws IOException, ElasticsearchException {
        Music oldMusic = esMusicDao.selectMusicById(music);
        //有旧文件而且新文件来了，先把旧文件删除
        if (oldMusic.getMusicUrl() != null && musicFile != null) {
            deleteFileFromDisk(generatePhysicalMusicPath(oldMusic));
        }
        if (oldMusic.getMvUrl() != null && mvFile != null) {
            deleteFileFromDisk(generatePhysicalMvPath(oldMusic));
        }
        if (oldMusic.getCoverUrl() != null && coverFile != null) {
            deleteFileFromDisk(generatePhysicalCoverPath(oldMusic));
        }
        //如果有新文件就设置新路径
        music.setMusicUrl(musicFile == null ? null : generateLogicalMusicPath(music, musicFile));
        music.setMvUrl(mvFile == null ? null : generateLogicalMvPath(music, mvFile));
        music.setCoverUrl(coverFile == null ? null : generateLogicalCoverPath(music, coverFile));
        //更新并写文件
        sqlMusicDao.updateMusic(music);
        esMusicDao.updateMusic(music);
        writeMusicFiles(music, musicFile, mvFile, coverFile);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addMusicToLike(List<Long> ids, Long userId) throws IOException, ElasticsearchException {
        sqlMusicDao.addMusicToLike(ids, userId);
        esMusicDao.addMusicToLike(ids, userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMusicFromLike(List<Long> ids, Long userId) throws IOException, ElasticsearchException {
        sqlMusicDao.deleteMusicFromLike(ids, userId);
        esMusicDao.deleteMusicFromLike(ids, userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addMusicToList(MusicList musicList) throws IOException, ElasticsearchException {
        Music lastMusic = selectMusicById(musicList.getMusic().get(musicList.getMusic().size() - 1));
        sqlMusicDao.addMusicToList(musicList, lastMusic.getCoverUrl());
        esMusicListDao.addMusicToList(musicList,lastMusic.getCoverUrl());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMusicFromList(MusicList musicList) throws IOException, ElasticsearchException {
        sqlMusicDao.deleteMusicFromList(musicList);
        esMusicListDao.deleteMusicFromList(musicList);
    }

    public List<Music> selectMusicFromLike(Long lastIndex, Long userId) {
        return sqlMusicDao.selectMusicFromLike(lastIndex, userId);
    }

    public Integer selectCountFromLike(Long userId) {
        return sqlMusicDao.selectMusicCountFromLike(userId);
    }

    public List<Music> selectAllMusic(Long lastIndex) throws IOException {
        return esMusicDao.selectAllMusic(lastIndex);
    }

    public Long selectAllCount() throws IOException {
        return esMusicDao.selectAllCount();
    }

    public Music selectMusicById(Music music) throws IOException {
        return esMusicDao.selectMusicById(music);
    }

    public List<Music> selectMusicByName(Music music, Long lastIndex, Double lastScore) throws IOException {
        return esMusicDao.selectMusicByName(music, lastIndex, lastScore);
    }

    public Long selectCountByName(Music music) throws IOException {
        return esMusicDao.selectCountByName(music);
    }

    public Long selectCountByLyric(Music music) throws IOException {
        return esMusicDao.selectCountByLyric(music);
    }

    public List<Music> selectMusicByLyric(Music music, Long lastIndex, Double lastScore) throws IOException {
        return esMusicDao.selectMusicByLyric(music, lastIndex, lastScore);
    }

    public boolean ifLiked(Long musicId, Long userId) {
        return sqlMusicDao.ifLiked(musicId, userId) > 0;
    }

    public boolean ifCollected(Long listId, Long musicId) {
        return sqlMusicDao.ifCollected(listId, musicId) > 0;
    }

    private String generateLogicalMusicPath(Music music, MultipartFile musicFile) {
        String contentType = musicFile.getContentType();
        int index = contentType.lastIndexOf('/');
        String fileType = contentType.substring(index + 1);
        return Long.toString(music.getId()) + "." + fileType;
    }

    private String generateLogicalMvPath(Music music, MultipartFile mvFile) {
        String contentType = mvFile.getContentType();
        int index = contentType.lastIndexOf('/');
        String fileType = contentType.substring(index + 1);
        return Long.toString(music.getId()) + "." + fileType;
    }

    private String generateLogicalCoverPath(Music music, MultipartFile coverFile) {
        String contentType = coverFile.getContentType();
        int index = contentType.lastIndexOf('/');
        String fileType = contentType.substring(index + 1);
        return Long.toString(music.getId()) + "." + fileType;
    }

    private String generatePhysicalMusicPath(Music music) {
        return musicBaseUrl + music.getMusicUrl();
    }

    private String generatePhysicalMvPath(Music music) {
        return mvBaseUrl + music.getMvUrl();
    }

    private String generatePhysicalCoverPath(Music music) {
        return coverBaseUrl + music.getCoverUrl();
    }

    private void writeMusicFiles(Music music, MultipartFile musicContent, MultipartFile mvContent, MultipartFile coverContent) throws IOException {
        if (musicContent != null) {
            String musicPath = generatePhysicalMusicPath(music);
            writeFileToDisk(musicPath, musicContent.getBytes());
        }
        if (mvContent != null) {
            String mvPath = generatePhysicalMvPath(music);
            writeFileToDisk(mvPath, mvContent.getBytes());
        }
        if (coverContent != null) {
            String coverPath = generatePhysicalCoverPath(music);
            writeFileToDisk(coverPath, coverContent.getBytes());
        }
    }

    private void writeFileToDisk(String path, byte[] content) throws IOException {
        int index1 = path.lastIndexOf('/');
        int index2 = path.lastIndexOf('\\');
        String filePath = path.substring(0, Math.max(index1, index2) + 1);
        File file = new File(filePath);
        if (!file.exists() && !file.mkdirs()) {
            throw new IOException("Can't create file");
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            fileOutputStream.write(content);
        }
    }

    private void deleteFileFromDisk(String path) throws IOException {
        File file = new File(path);
        if (!file.delete()) {
            throw new IOException("Can't delete file");
        }
    }
}
