package com.example.mypractice.controller.impl;

import com.example.mypractice.commons.constant.Bodies;
import com.example.mypractice.commons.constant.Others;
import com.example.mypractice.commons.exception.AccessException;
import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.controller.MusicListController;
import com.example.mypractice.controller.checker.MusicListChecker;
import com.example.mypractice.model.database.MusicList;
import com.example.mypractice.model.database.User;
import com.example.mypractice.model.web.IdList;
import com.example.mypractice.service.MusicListService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
public class MusicListControllerImpl implements MusicListController {
    @Autowired
    private MusicListChecker musicListChecker;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MusicListService musicListService;

    @Override
    @GetMapping("/unAuth/musicList/select/id")
    public String selectMusicListById(@RequestParam Long id) throws FilterException, JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicListChecker.selectMusicListById(id);
        MusicList musicList = musicListService.selectMusicListById(new MusicList(id));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.MUSIC_LIST, musicList);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/unAuth/musicList/select/name")
    public String selectMusicListByName(@RequestParam String name, @RequestParam(required = false) Long lastIndex,
                                        @RequestParam(required = false) Double lastScore) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicListChecker.selectMusicListByName(name, lastIndex, lastScore);
        List<MusicList> musicList = musicListService.selectMusicListByName(new MusicList(name), lastIndex, lastScore);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.MUSIC_LIST, musicList);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/unAuth/musicList/count/name")
    public String selectCountByName(@RequestParam String name) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicListChecker.selectCountByName(name);
        Long count = musicListService.selectMusicListCountByName(new MusicList(name));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.COUNT, count);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/user/musicList/add")
    public String createMusicList(@RequestBody MusicList musicList, HttpServletRequest httpServletRequest) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(1);
        musicListChecker.createMusicList(musicList, httpServletRequest);
        Long userId = (Long) httpServletRequest.getAttribute(Others.USER_ID);
        String userName = (String) httpServletRequest.getAttribute(Others.USER_NAME);
        musicListService.creatMusicList(musicList.setAuthor(new User(userId).setUserName(userName)));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/user/musicList/delete")
    public String deleteMusicList(@RequestBody MusicList musicList, HttpServletRequest request) throws FilterException, IOException, AccessException {
        HashMap<String, Object> result = new HashMap<>(1);
        musicListChecker.deleteMusicList(musicList, request);
        musicListService.deleteMusicList(musicList);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/user/musicList/update")
    public String updateMusicList(@RequestBody MusicList musicList, HttpServletRequest request) throws FilterException, IOException, AccessException {
        HashMap<String, Object> result = new HashMap<>(1);
        musicListChecker.updateMusicList(musicList, request);
        musicListService.updateMusicList(musicList);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/user/musicList/like/add")
    public String addMusicListToLike(@RequestBody IdList id, HttpServletRequest request) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(1);
        musicListChecker.addMusicListToLike(id, request);
        musicListService.addMusicListToLike(id.getId(), (Long) request.getAttribute(Others.USER_ID));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/user/musicList/like/delete")
    public String deleteMusicListFromLike(@RequestBody IdList id, HttpServletRequest request) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(1);
        musicListChecker.deleteMusicListFromLike(id, request);
        musicListService.deleteMusicListFromLike(id.getId(), (Long) request.getAttribute(Others.USER_ID));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/user/musicList/select/self")
    public String selectMusicListCreateBySelf(@RequestParam Long lastIndex, HttpServletRequest request) throws FilterException, JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicListChecker.selectMusicListCreateBySelf(lastIndex, request);
        Long userId = (Long) request.getAttribute(Others.USER_ID);
        List<MusicList> music = musicListService.selectMusicListByAuthorId(new MusicList().setAuthor(new User(userId)), lastIndex);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.MUSIC, music);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/user/musicList/select/other")
    public String selectMusicListFromLike(@RequestParam Long lastIndex, HttpServletRequest request) throws FilterException, JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicListChecker.selectMusicListFromLike(lastIndex, request);
        Long userId = (Long) request.getAttribute(Others.USER_ID);
        List<MusicList> music = musicListService.selectMusicListFromLike(lastIndex, userId);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.MUSIC, music);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/user/musicList/count/other")
    public String selectCountCreateBySelf(HttpServletRequest request) throws JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicListChecker.selectCountCreateBySelf(request);
        Long userId = (Long) request.getAttribute(Others.USER_ID);
        Integer count = musicListService.selectCountFromLike(userId);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.COUNT, count);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/user/musicList/count/self")
    public String selectCountFromLike(HttpServletRequest request) throws JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicListChecker.selectCountFromLike(request);
        Long userId = (Long) request.getAttribute(Others.USER_ID);
        Integer count = musicListService.selectCountByAuthorId(new MusicList().setAuthor(new User(userId)));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.COUNT, count);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/admin/musicList/selectAll")
    public String selectAllMusicList(@RequestParam Long lastIndex) throws FilterException, JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicListChecker.selectAllMusicList(lastIndex);
        List<MusicList> music = musicListService.selectAllMusicList(lastIndex);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.MUSIC, music);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/admin/musicList/countAll")
    public String selectAllCount() throws JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        musicListChecker.selectAllCount();
        Integer count = musicListService.selectAllCount();
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.COUNT, count);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/admin/musicList/ban")
    public String banMusicList(@RequestBody MusicList musicList) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(1);
        musicListChecker.banMusicList(musicList);
        musicListService.banMusicList(musicList);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/admin/musicList/unseal")
    public String unsealMusicList(@RequestBody MusicList musicList) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(1);
        musicListChecker.unsealMusicList(musicList);
        musicListService.unsealMusicList(musicList);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/user/musicList/ifLike")
    public String ifLiked(@RequestParam Long listId,HttpServletRequest request) throws JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        Long userId = (Long) request.getAttribute(Others.USER_ID);
        musicListChecker.ifLiked(listId, request);
        boolean ifLiked = musicListService.ifLiked(listId, userId);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.RESULT, ifLiked);
        return objectMapper.writeValueAsString(result);
    }
}
