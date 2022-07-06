package com.example.mypractice.controller;

import com.example.mypractice.commons.exception.AccessException;
import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.model.database.MusicList;
import com.example.mypractice.model.web.IdList;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface MusicListController {
    String selectMusicListById(Long id) throws FilterException, JsonProcessingException;

    String selectMusicListByName(String name, Long lastIndex, Double lastScore) throws FilterException, IOException;

    String selectCountByName(String name) throws FilterException, IOException;

    String createMusicList(MusicList musicList, HttpServletRequest httpServletRequest) throws FilterException, IOException;

    String deleteMusicList(MusicList musicList,HttpServletRequest request) throws FilterException, IOException, AccessException;

    String updateMusicList(MusicList musicList,HttpServletRequest request) throws FilterException, IOException, AccessException;

    String addMusicListToLike(IdList id, HttpServletRequest request) throws FilterException, IOException;

    String deleteMusicListFromLike(IdList id, HttpServletRequest request) throws FilterException, IOException;

    String selectMusicListCreateBySelf(Long lastIndex, HttpServletRequest request) throws FilterException, JsonProcessingException;

    String selectMusicListFromLike(Long lastIndex, HttpServletRequest request) throws FilterException, JsonProcessingException;

    String selectCountCreateBySelf(HttpServletRequest request) throws JsonProcessingException;

    String selectCountFromLike(HttpServletRequest request) throws JsonProcessingException;

    String selectAllMusicList(Long lastIndex) throws FilterException, JsonProcessingException;

    String selectAllCount() throws JsonProcessingException;

    String banMusicList(MusicList musicList) throws FilterException, IOException;

    String unsealMusicList(MusicList musicList) throws FilterException, IOException;

    String ifLiked(Long listId, HttpServletRequest request) throws JsonProcessingException;
}
