package com.example.mypractice.controller.impl;

import com.example.mypractice.commons.constant.Bodies;
import com.example.mypractice.commons.constant.Others;
import com.example.mypractice.commons.exception.AccessException;
import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.controller.CommentController;
import com.example.mypractice.controller.checker.CommentChecker;
import com.example.mypractice.model.database.Comment;
import com.example.mypractice.model.database.Music;
import com.example.mypractice.model.database.User;
import com.example.mypractice.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
public class CommentControllerImpl implements CommentController {
    @Autowired
    private CommentChecker commentChecker;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @PostMapping("/user/comment/music")
    public String createComment(@RequestBody Comment comment, HttpServletRequest request) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(1);
        commentChecker.createComment(comment, request);
        Long userId = (Long) request.getAttribute(Others.USER_ID);
        String userName = (String) request.getAttribute(Others.USER_NAME);
        commentService.createComment(comment.setAuthor(new User(userId, userName, "")));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/user/comment/music/selectAll")
    public String selectCommentSelf(@RequestParam(required = false) Long lastIndex, HttpServletRequest request) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        commentChecker.selectCommentSelf(lastIndex, request);
        Long userId = (Long) request.getAttribute(Others.USER_ID);
        List<Comment> comment = commentService.selectCommentByUser(new User(userId), lastIndex);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.COMMENT, comment);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/user/comment/music/count")
    public String selectCommentCountSelf(HttpServletRequest request) throws IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        commentChecker.selectCommentCountSelf(request);
        Long userId = (Long) request.getAttribute(Others.USER_ID);
        Long count = commentService.selectCountByUser(new User(userId));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.COUNT, count);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/user/comment/music/delete")
    public String deleteCommentSelf(@RequestBody Comment comment,HttpServletRequest request) throws FilterException, IOException, AccessException {
        HashMap<String, Object> result = new HashMap<>(1);
        commentChecker.deleteCommentSelf(comment,request);
        commentService.deleteComment(comment);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/unAuth/comment/music/select/commentId")
    public String selectCommentByCommentId(@RequestParam Long id) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        commentChecker.selectCommentByCommentId(id);
        Comment comment = commentService.selectCommentByCommentId(new Comment(id));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.COMMENT, comment);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/unAuth/comment/music/select/id")
    public String selectCommentByMusic(@RequestParam Long id, @RequestParam(required = false) Long lastIndex) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        commentChecker.selectCommentByMusic(id, lastIndex);
        List<Comment> comment = commentService.selectCommentByMusic(new Music(id), lastIndex);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.COMMENT, comment);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/unAuth/comment/music/count/id")
    public String selectCountByMusic(@RequestParam Long id) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        commentChecker.selectCountByMusic(id);
        Long count = commentService.selectCountByMusic(new Music(id));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.COUNT, count);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/admin/comment/music/delete")
    public String deleteComment(@RequestBody Comment comment) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(1);
        commentChecker.deleteComment(comment);
        commentService.deleteComment(comment);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/admin/comment/music/select/content")
    public String selectCommentByContent(@RequestParam String content,
                                         @RequestParam(required = false) Long lastIndex,
                                         @RequestParam(required = false) Double lastScore) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        commentChecker.selectCommentByContent(content, lastIndex, lastScore);
        List<Comment> comment = commentService.selectCommentByContent(new Comment(content), lastIndex, lastScore);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.COMMENT, comment);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/admin/comment/music/count/content")
    public String selectCountByContent(@RequestParam String content) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        commentChecker.selectCountByContent(content);
        Long count = commentService.selectCountByContent(new Comment(content));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.COUNT, count);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/admin/comment/music/select/userId")
    public String selectCommentByUser(@RequestParam Long id,@RequestParam(required = false) Long lastIndex) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        commentChecker.selectCommentByUser(id, lastIndex);
        List<Comment> comment = commentService.selectCommentByUser(new User(id), lastIndex);
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.COMMENT, comment);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/admin/comment/music/count/userId")
    public String selectCommentCountByUser(@RequestParam Long id) throws FilterException, IOException {
        HashMap<String, Object> result = new HashMap<>(2);
        commentChecker.selectCommentCountByUser(id);
        Long count = commentService.selectCountByUser(new User(id));
        result.put(Bodies.MESSAGE, Others.SUCCESS);
        result.put(Bodies.COUNT, count);
        return objectMapper.writeValueAsString(result);
    }
}
