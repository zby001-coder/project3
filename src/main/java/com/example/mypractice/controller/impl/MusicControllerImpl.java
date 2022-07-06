package com.example.mypractice.controller.impl;

import com.example.mypractice.commons.constant.Bodies;
import com.example.mypractice.commons.constant.Others;
import com.example.mypractice.commons.exception.AccessException;
import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.controller.MusicController;
import com.example.mypractice.controller.checker.MusicChecker;
import com.example.mypractice.model.database.Music;
import com.example.mypractice.model.database.MusicList;
import com.example.mypractice.model.web.IdList;
import com.example.mypractice.service.MusicService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
public class MusicControllerImpl implements MusicController {
    @Autowired
    private MusicService musicService;
    @Autowired
    private MusicChecker musicChecker;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @PostMapping("/user/music/like/add")
    public String addMusicToLike(@RequestBody IdList id, HttpServletRequest request) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(1);
        musicChecker.addMusicToLike(id, request);
        musicService.addMusicToLike(id.getId(), (Long) request.getAttribute(Others.USER_ID));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/user/music/like/delete")
    public String deleteMusicFromLike(@RequestBody IdList id, HttpServletRequest request) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(1);
        musicChecker.deleteMusicFromLike(id, request);
        musicService.deleteMusicFromLike(id.getId(), (Long) request.getAttribute(Others.USER_ID));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/user/music/collect/add")
    public String addMusicToList(@RequestBody MusicList musicList,HttpServletRequest request) throws FilterException, IOException, AccessException {
        HashMap<String, Object> result = new HashMap<>(1);
        musicChecker.addMusicToList(musicList,request);
        musicService.addMusicToList(musicList);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/user/music/collect/delete")
    public String deleteMusicFromList(@RequestBody MusicList musicList,HttpServletRequest request) throws FilterException, IOException, AccessException {
        HashMap<String, Object> result = new HashMap<>(1);
        musicChecker.deleteMusicFromList(musicList,request);
        musicService.deleteMusicFromList(musicList);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/user/music/select/iLike")
    public String selectMusicFromLike(@RequestParam Long lastIndex, HttpServletRequest request) throws FilterException, JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicChecker.selectMusicFromLike(lastIndex, request);
        List<Music> music = musicService.selectMusicFromLike(lastIndex, (Long) request.getAttribute(Others.USER_ID));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.MUSIC, music);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/user/music/count/iLike")
    public String selectCountFromLike(HttpServletRequest request) throws JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicChecker.selectCountFromLike(request);
        Integer count = musicService.selectCountFromLike((Long) request.getAttribute(Others.USER_ID));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.MUSIC, count);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/unAuth/music/select/id")
    public String selectMusicById(@RequestParam Long id) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicChecker.selectMusicById(id);
        Music music = musicService.selectMusicById(new Music(id));
        result.put(Bodies.MUSIC, music);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/unAuth/music/select/name")
    public String selectMusicByName(@RequestParam String name, @RequestParam(required = false) Long lastIndex,
                                    @RequestParam(required = false) Double lastScore) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicChecker.selectMusicByName(name, lastIndex, lastScore);
        List<Music> musicList = musicService.selectMusicByName(new Music(name), lastIndex, lastScore);
        result.put(Bodies.MUSIC, musicList);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/unAuth/music/count/name")
    public String selectCountByName(@RequestParam String name) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicChecker.selectCountByName(name);
        Long count = musicService.selectCountByName(new Music(name));
        result.put(Bodies.COUNT, count);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/unAuth/music/count/lyric")
    public String selectCountByLyric(@RequestParam String lyric) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicChecker.selectCountByLyric(lyric);
        Long count = musicService.selectCountByLyric(new Music().setLyric(lyric));
        result.put(Bodies.COUNT, count);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/unAuth/music/select/lyric")
    public String selectMusicByLyric(@RequestParam String lyric, @RequestParam(required = false) Long lastIndex,
                                     @RequestParam(required = false) Double lastScore) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicChecker.selectMusicByLyric(lyric, lastIndex, lastScore);
        List<Music> musicList = musicService.selectMusicByLyric(new Music().setLyric(lyric), lastIndex, lastScore);
        result.put(Bodies.MUSIC, musicList);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/admin/music/add")
    public String addMusic(@RequestPart Music music, @RequestPart MultipartFile musicContent,
                           @RequestPart(required = false) MultipartFile mvContent,
                           @RequestPart MultipartFile coverContent) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicChecker.addMusic(music.setLikeCount(0), musicContent, mvContent, coverContent);
        music = musicService.addMusic(music, musicContent, mvContent, coverContent);
        result.put(Bodies.ID, music.getId());
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/admin/music/delete")
    public String deleteMusic(@RequestBody Music music) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(1);
        musicChecker.deleteMusic(music);
        musicService.deleteMusic(music);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/admin/music/update")
    public String updateMusic(@RequestPart Music music, @RequestPart(required = false) MultipartFile musicContent,
                              @RequestPart(required = false) MultipartFile mvContent,
                              @RequestPart(required = false) MultipartFile coverContent) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(1);
        musicChecker.updateMusic(music, musicContent, mvContent, coverContent);
        musicService.updateMusic(music, musicContent, mvContent, coverContent);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/admin/music/selectAll")
    public String selectAllMusic(@RequestParam(required = false) Long lastIndex) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicChecker.selectAllMusic(lastIndex);
        List<Music> musicList = musicService.selectAllMusic(lastIndex);
        result.put(Bodies.MUSIC, musicList);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/admin/music/countAll")
    public String selectAllCount() throws IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicChecker.selectAllCount();
        Long count = musicService.selectAllCount();
        result.put(Bodies.COUNT, count);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/user/music/ifLike")
    public String ifLiked(@RequestParam Long musicId,HttpServletRequest request) throws JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        Long userId = (Long) request.getAttribute(Others.USER_ID);
        musicChecker.ifLiked(musicId, request);
        boolean ifLiked = musicService.ifLiked(musicId, userId);
        result.put(Bodies.RESULT, ifLiked);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/user/music/ifCollect")
    public String ifCollected(@RequestParam Long listId, @RequestParam Long musicId) throws JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicChecker.ifCollected(musicId, musicId);
        boolean ifCollected = musicService.ifCollected(musicId, musicId);
        result.put(Bodies.RESULT, ifCollected);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }
}
