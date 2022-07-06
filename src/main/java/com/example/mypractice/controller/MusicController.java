package com.example.mypractice.controller;

import com.example.mypractice.commons.exception.AccessException;
import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.model.database.Music;
import com.example.mypractice.model.database.MusicList;
import com.example.mypractice.model.web.IdList;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface MusicController {
    String addMusicToLike(IdList id, HttpServletRequest request) throws FilterException, IOException;

    String deleteMusicFromLike(IdList id, HttpServletRequest request) throws FilterException, IOException;

    String addMusicToList(MusicList musicList,HttpServletRequest request) throws FilterException, IOException, AccessException;

    String deleteMusicFromList(MusicList musicList,HttpServletRequest request) throws FilterException, IOException, AccessException;

    String selectMusicFromLike(Long lastIndex,HttpServletRequest request) throws FilterException, JsonProcessingException;

    String selectCountFromLike(HttpServletRequest request) throws JsonProcessingException;

    String selectMusicById(Long id) throws FilterException, IOException;

    String selectMusicByName(String name, Long lastIndex, Double lastScore) throws FilterException, IOException;

    String selectCountByName(String name) throws FilterException, IOException;

    String selectCountByLyric(String lyric) throws FilterException, IOException;

    String selectMusicByLyric(String lyric, Long lastIndex, Double lastScore) throws FilterException, IOException;

    String addMusic(Music music, MultipartFile musicContent, MultipartFile mvContent, MultipartFile coverContent) throws FilterException, IOException;

    String deleteMusic(Music music) throws FilterException, IOException;

    String updateMusic(Music music, MultipartFile musicContent, MultipartFile mvContent, MultipartFile coverContent) throws FilterException, IOException;

    String selectAllMusic(Long lastIndex) throws FilterException, IOException;

    String selectAllCount() throws IOException;

    String ifLiked(Long musicId, Long userId) throws JsonProcessingException;

    String ifCollected(Long listId, Long musicId) throws JsonProcessingException;
}
