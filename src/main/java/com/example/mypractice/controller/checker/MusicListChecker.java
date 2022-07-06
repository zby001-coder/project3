package com.example.mypractice.controller.checker;

import com.example.mypractice.commons.constant.Others;
import com.example.mypractice.commons.exception.AccessException;
import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.controller.MusicListController;
import com.example.mypractice.filter.basic.CollectionNotEmptyFilter;
import com.example.mypractice.filter.model.MusicListFilter;
import com.example.mypractice.filter.model.PagingFilter;
import com.example.mypractice.model.database.MusicList;
import com.example.mypractice.model.web.IdList;
import com.example.mypractice.service.MusicListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class MusicListChecker implements MusicListController {
    @Autowired
    private MusicListFilter musicListFilter;
    @Autowired
    private PagingFilter pagingFilter;
    @Autowired
    private CollectionNotEmptyFilter collectionNotEmptyFilter;
    @Autowired
    private MusicListService musicListService;

    @Override
    public String selectMusicListById(Long id) throws FilterException {
        musicListFilter.filterId(id, false);
        return Others.SUCCESS;
    }

    @Override
    public String selectMusicListByName(String name, Long lastIndex, Double lastScore) throws FilterException {
        musicListFilter.filterName(name, false);
        pagingFilter.filterLastIndex(lastIndex, true).filterLastScore(lastScore, true);
        return Others.SUCCESS;
    }

    @Override
    public String selectCountByName(String name) throws FilterException {
        musicListFilter.filterName(name, false);
        return Others.SUCCESS;
    }

    @Override
    public String createMusicList(MusicList musicList, HttpServletRequest httpServletRequest) throws FilterException {
        if (musicList == null) {
            throw new FilterException("MUSIC_LIST");
        }
        musicListFilter.filterName(musicList.getName(), false)
                .filterDescription(musicList.getDescription(), false)
                .filterCreateTime(musicList.getCreateTime(), false);
        return Others.SUCCESS;
    }

    @Override
    public String deleteMusicList(MusicList musicList, HttpServletRequest request) throws FilterException, AccessException {
        if (musicList == null) {
            throw new FilterException("MUSIC_LIST");
        }
        musicListFilter.filterId(musicList.getId(), false);
        if (!musicListService.ifCreated(musicList.getId(), (Long) request.getAttribute(Others.USER_ID))) {
            throw new AccessException();
        }
        return Others.SUCCESS;
    }

    @Override
    public String updateMusicList(MusicList musicList, HttpServletRequest request) throws FilterException, AccessException {
        if (musicList == null) {
            throw new FilterException("MUSIC_LIST");
        }
        musicListFilter.filterId(musicList.getId(), false)
                .filterDescription(musicList.getDescription(), true)
                .filterName(musicList.getName(), true);
        if (!musicListService.ifCreated(musicList.getId(), (Long) request.getAttribute(Others.USER_ID))) {
            throw new AccessException();
        }
        return Others.SUCCESS;
    }

    @Override
    public String addMusicListToLike(IdList id, HttpServletRequest request) throws FilterException {
        if (id == null) {
            throw new FilterException("ID");
        }
        collectionNotEmptyFilter.doFilter(id.getId(), "ID", false);
        return Others.SUCCESS;
    }

    @Override
    public String deleteMusicListFromLike(IdList id, HttpServletRequest request) throws FilterException {
        if (id == null) {
            throw new FilterException("ID");
        }
        collectionNotEmptyFilter.doFilter(id.getId(), "ID", false);
        return Others.SUCCESS;
    }

    @Override
    public String selectMusicListCreateBySelf(Long lastIndex, HttpServletRequest request) throws FilterException {
        pagingFilter.filterLastIndex(lastIndex, false);
        return Others.SUCCESS;
    }

    @Override
    public String selectMusicListFromLike(Long lastIndex, HttpServletRequest request) throws FilterException {
        pagingFilter.filterLastIndex(lastIndex, false);
        return Others.SUCCESS;
    }

    @Override
    public String selectCountCreateBySelf(HttpServletRequest request) {
        return Others.SUCCESS;
    }

    @Override
    public String selectCountFromLike(HttpServletRequest request) {
        return Others.SUCCESS;
    }

    @Override
    public String selectAllMusicList(Long lastIndex) throws FilterException {
        pagingFilter.filterLastIndex(lastIndex, false);
        return Others.SUCCESS;
    }

    @Override
    public String selectAllCount() {
        return Others.SUCCESS;
    }

    @Override
    public String banMusicList(MusicList musicList) throws FilterException {
        if (musicList == null) {
            throw new FilterException("MUSIC_LIST");
        }
        musicListFilter.filterId(musicList.getId(), false);
        return Others.SUCCESS;
    }

    @Override
    public String unsealMusicList(MusicList musicList) throws FilterException {
        if (musicList == null) {
            throw new FilterException("MUSIC_LIST");
        }
        musicListFilter.filterId(musicList.getId(), false);
        return Others.SUCCESS;
    }

    @Override
    public String ifLiked(Long listId, Long userId) {
        return Others.SUCCESS;
    }
}
