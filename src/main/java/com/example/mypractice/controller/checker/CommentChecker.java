package com.example.mypractice.controller.checker;

import com.example.mypractice.commons.constant.Others;
import com.example.mypractice.commons.exception.AccessException;
import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.controller.CommentController;
import com.example.mypractice.filter.model.CommentFilter;
import com.example.mypractice.filter.model.PagingFilter;
import com.example.mypractice.model.database.Comment;
import com.example.mypractice.model.database.Music;
import com.example.mypractice.model.database.User;
import com.example.mypractice.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class CommentChecker implements CommentController {
    @Autowired
    private CommentFilter commentFilter;
    @Autowired
    private PagingFilter pagingFilter;
    @Autowired
    private CommentService commentService;

    @Override
    public String createComment(Comment comment, HttpServletRequest request) throws FilterException {
        commentFilter.filterContent(comment.getContent(), false)
                .filterMusic(comment.getMusic(), false);
        return Others.SUCCESS;
    }

    @Override
    public String selectCommentSelf(Long lastIndex, HttpServletRequest request) throws FilterException {
        pagingFilter.filterLastIndex(lastIndex, true);
        return Others.SUCCESS;
    }

    @Override
    public String selectCommentCountSelf(HttpServletRequest request) {
        return Others.SUCCESS;
    }

    @Override
    public String deleteCommentSelf(Comment comment,HttpServletRequest request) throws FilterException, IOException, AccessException {
        commentFilter.filterId(comment.getId(), false);
        if (!commentService.ifCreated(comment.getId(), (Long) request.getAttribute(Others.USER_ID))){
            throw new AccessException();
        }
        return Others.SUCCESS;
    }

    @Override
    public String selectCommentByCommentId(Long id) throws FilterException {
        commentFilter.filterId(id, false);
        return Others.SUCCESS;
    }

    @Override
    public String selectCommentByMusic(Long id, Long lastIndex) throws FilterException {
        commentFilter.filterMusic(new Music(id), false);
        pagingFilter.filterLastIndex(lastIndex, true);
        return Others.SUCCESS;
    }

    @Override
    public String selectCountByMusic(Long id) throws FilterException {
        commentFilter.filterMusic(new Music(id), false);
        return Others.SUCCESS;
    }

    @Override
    public String deleteComment(Comment comment) throws FilterException {
        if (comment == null) {
            throw new FilterException("COMMENT");
        }
        commentFilter.filterId(comment.getId(), false);
        return Others.SUCCESS;
    }

    @Override
    public String selectCommentByContent(String content, Long lastIndex, Double lastScore) throws FilterException {
        commentFilter.filterContent(content, false);
        pagingFilter.filterLastIndex(lastIndex, true).filterLastScore(lastScore, true);
        return Others.SUCCESS;
    }

    @Override
    public String selectCountByContent(String content) throws FilterException {
        commentFilter.filterContent(content, false);
        return Others.SUCCESS;
    }

    @Override
    public String selectCommentByUser(Long id, Long lastIndex) throws FilterException {
        commentFilter.filterAuthor(new User(id), false);
        pagingFilter.filterLastIndex(lastIndex, true);
        return Others.SUCCESS;
    }

    @Override
    public String selectCommentCountByUser(Long id) throws FilterException {
        commentFilter.filterAuthor(new User(id), false);
        return Others.SUCCESS;
    }
}
