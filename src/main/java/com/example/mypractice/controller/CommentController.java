package com.example.mypractice.controller;

import com.example.mypractice.commons.exception.AccessException;
import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.model.database.Comment;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface CommentController {
    String createComment(Comment comment, HttpServletRequest request) throws FilterException, IOException;

    String selectCommentSelf(Long lastIndex, HttpServletRequest request) throws FilterException, IOException;

    String selectCommentCountSelf(HttpServletRequest request) throws IOException;

    String deleteCommentSelf(Comment comment,HttpServletRequest request) throws FilterException, IOException, AccessException;

    String selectCommentByCommentId(Long id) throws FilterException, IOException;

    String selectCommentByMusic(Long id, Long lastIndex) throws FilterException, IOException;

    String selectCountByMusic(Long id) throws FilterException, IOException;

    String deleteComment(Comment comment) throws FilterException, IOException;

    String selectCommentByContent(String content, Long lastIndex, Double lastScore) throws FilterException, IOException;

    String selectCountByContent(String content) throws FilterException, IOException;

    String selectCommentByUser(Long id, Long lastIndex) throws FilterException, IOException;

    String selectCommentCountByUser(Long id) throws FilterException, IOException;
}
