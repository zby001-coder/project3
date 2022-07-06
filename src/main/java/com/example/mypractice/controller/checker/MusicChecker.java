package com.example.mypractice.controller.checker;

import com.example.mypractice.commons.constant.Others;
import com.example.mypractice.commons.exception.AccessException;
import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.controller.MusicController;
import com.example.mypractice.filter.basic.CollectionNotEmptyFilter;
import com.example.mypractice.filter.model.MusicFilter;
import com.example.mypractice.filter.model.PagingFilter;
import com.example.mypractice.model.database.Music;
import com.example.mypractice.model.database.MusicList;
import com.example.mypractice.model.web.IdList;
import com.example.mypractice.service.MusicListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Component
public class MusicChecker implements MusicController {
    @Autowired
    private MusicFilter musicFilter;
    @Autowired
    private PagingFilter pagingFilter;
    @Autowired
    private CollectionNotEmptyFilter collectionFilter;
    @Autowired
    private MusicListService musicListService;

    @Override
    public String addMusicToLike(IdList id, HttpServletRequest request) throws FilterException {
        if (id == null) {
            throw new FilterException("ID");
        }
        collectionFilter.doFilter(id.getId(), "ID", false);
        return Others.SUCCESS;
    }

    @Override
    public String deleteMusicFromLike(IdList id, HttpServletRequest request) throws FilterException {
        if (id == null) {
            throw new FilterException("ID");
        }
        collectionFilter.doFilter(id.getId(), "ID", false);
        return Others.SUCCESS;
    }

    @Override
    public String addMusicToList(MusicList musicList,HttpServletRequest request) throws FilterException, AccessException {
        if (musicList == null) {
            throw new FilterException("MUSIC_LIST");
        }
        musicFilter.filterId(musicList.getId(), false);
        collectionFilter.doFilter(musicList.getMusic(), "MUSIC", false);
        if (!musicListService.ifCreated(musicList.getId(), (Long) request.getAttribute(Others.USER_ID))) {
            throw new AccessException();
        }
        return Others.SUCCESS;
    }

    @Override
    public String deleteMusicFromList(MusicList musicList,HttpServletRequest request) throws FilterException, AccessException {
        if (musicList == null) {
            throw new FilterException("MUSIC_LIST");
        }
        musicFilter.filterId(musicList.getId(), false);
        collectionFilter.doFilter(musicList.getMusic(), "MUSIC", false);
        if (!musicListService.ifCreated(musicList.getId(), (Long) request.getAttribute(Others.USER_ID))) {
            throw new AccessException();
        }
        return Others.SUCCESS;
    }

    @Override
    public String selectMusicFromLike(Long lastIndex, HttpServletRequest request) throws FilterException {
        pagingFilter.filterLastIndex(lastIndex, true);
        return Others.SUCCESS;
    }

    @Override
    public String selectCountFromLike(HttpServletRequest request) {
        return Others.SUCCESS;
    }

    @Override
    public String selectMusicById(Long id) throws FilterException {
        musicFilter.filterId(id, false);
        return Others.SUCCESS;
    }

    @Override
    public String selectMusicByName(String name, Long lastIndex, Double lastScore) throws FilterException {
        musicFilter.filterMusicName(name, false);
        pagingFilter.filterLastIndex(lastIndex, true).filterLastScore(lastScore, true);
        return Others.SUCCESS;
    }

    @Override
    public String selectCountByName(String name) throws FilterException {
        musicFilter.filterMusicName(name, false);
        return Others.SUCCESS;
    }

    @Override
    public String selectCountByLyric(String lyric) throws FilterException {
        musicFilter.filterLyric(lyric, false);
        return Others.SUCCESS;
    }

    @Override
    public String selectMusicByLyric(String lyric, Long lastIndex, Double lastScore) throws FilterException {
        musicFilter.filterLyric(lyric, false);
        pagingFilter.filterLastIndex(lastIndex, true).filterLastScore(lastScore, true);
        return Others.SUCCESS;
    }

    @Override
    public String addMusic(Music music, MultipartFile musicContent, MultipartFile mvContent, MultipartFile coverContent) throws FilterException {
        if (music == null) {
            throw new FilterException("MUSIC");
        }
        musicFilter.filterDescription(music.getDescription(), false)
                .filterMusicName(music.getMusicName(), false)
                .filterLyric(music.getLyric(), false)
                .filterReleaseTime(music.getReleaseTime(), false)
                .filterSingerName(music.getSingerName(), false)
                .filterMusicContent(musicContent, false);
        return Others.SUCCESS;
    }

    @Override
    public String deleteMusic(Music music) throws FilterException {
        if (music == null) {
            throw new FilterException("MUSIC");
        }
        musicFilter.filterId(music.getId(), false);
        return Others.SUCCESS;
    }

    @Override
    public String updateMusic(Music music, MultipartFile musicContent, MultipartFile mvContent, MultipartFile coverContent) throws FilterException {
        if (music == null) {
            throw new FilterException("MUSIC");
        }
        musicFilter.filterId(music.getId(), false)
                .filterDescription(music.getDescription(), true)
                .filterSingerName(music.getSingerName(), true)
                .filterMusicName(music.getMusicName(), true)
                .filterLyric(music.getLyric(), true);
        return Others.SUCCESS;
    }

    @Override
    public String selectAllMusic(Long lastIndex) throws FilterException {
        pagingFilter.filterLastIndex(lastIndex, true);
        return Others.SUCCESS;
    }

    @Override
    public String selectAllCount() {
        return Others.SUCCESS;
    }

    @Override
    public String ifLiked(Long musicId, Long userId) {
        return Others.SUCCESS;
    }

    @Override
    public String ifCollected(Long listId, Long musicId) {
        return Others.SUCCESS;
    }
}
