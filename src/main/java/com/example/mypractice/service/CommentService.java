package com.example.mypractice.service;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.example.mypractice.commons.util.SnowflakeIdGenerator;
import com.example.mypractice.dao.EsCommentDao;
import com.example.mypractice.model.database.Comment;
import com.example.mypractice.model.database.Music;
import com.example.mypractice.model.database.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private EsCommentDao esCommentDao;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    public void createComment(Comment comment) throws IOException, ElasticsearchException {
        comment.initComment();
        esCommentDao.createComment(comment.setId(snowflakeIdGenerator.nextId()));
    }

    public List<Comment> selectCommentByUser(User user, Long lastIndex) throws IOException, ElasticsearchException {
        return esCommentDao.selectCommentByUser(user, lastIndex);
    }

    public Long selectCountByUser(User user) throws IOException, ElasticsearchException {
        return esCommentDao.selectCountByUser(user);
    }

    public void deleteComment(Comment comment) throws IOException, ElasticsearchException {
        esCommentDao.deleteComment(comment);
    }

    public Comment selectCommentByCommentId(Comment comment) throws IOException, ElasticsearchException {
        return esCommentDao.selectCommentByCommentId(comment);
    }

    public List<Comment> selectCommentByMusic(Music music, Long lastIndex) throws IOException, ElasticsearchException {
        return esCommentDao.selectCommentByMusic(music, lastIndex);
    }

    public Long selectCountByMusic(Music music) throws IOException, ElasticsearchException {
        return esCommentDao.selectCountByMusic(music);
    }

    public List<Comment> selectCommentByContent(Comment comment, Long lastIndex, Double lastScore) throws IOException, ElasticsearchException {
        return esCommentDao.selectCommentByContent(comment, lastIndex, lastScore);
    }

    public Long selectCountByContent(Comment comment) throws IOException, ElasticsearchException {
        return esCommentDao.selectCountByContent(comment);
    }

    /**
     * 当前的评论是否为该用户创建
     *
     * @param commentId
     * @param userId
     * @return 返回0，如果用户没有创建
     */
    public boolean ifCreated(Long commentId, Long userId) throws IOException, ElasticsearchException {
        return esCommentDao.ifCreated(commentId, userId) > 0;
    }
}
