package com.example.mypractice.filter.model;

import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.filter.basic.StringNotEmptyFilter;
import com.example.mypractice.model.database.Music;
import com.example.mypractice.model.database.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CommentFilter {
    private static final Long ID_MIN = 0L;
    @Autowired
    private StringNotEmptyFilter notEmptyFilter;

    public CommentFilter filterId(Long id, boolean nullable) throws FilterException {
        if (id == null) {
            if (nullable) {
                return this;
            }
            throw new FilterException("ID");
        }
        if (id >= ID_MIN) {
            return this;
        }
        throw new FilterException("ID");
    }

    public CommentFilter filterAuthor(User author, boolean nullable) throws FilterException {
        if (author == null) {
            throw new FilterException("AUTHOR");
        }
        if (author.getId() == null) {
            if (nullable) {
                return this;
            }
            throw new FilterException("AUTHOR.ID");
        }
        if (author.getId() >= ID_MIN) {
            return this;
        }
        throw new FilterException("AUTHOR.ID");
    }

    public CommentFilter filterMusic(Music music, boolean nullable) throws FilterException {
        if (music == null) {
            throw new FilterException("MUSIC");
        }
        if (music.getId() == null) {
            if (nullable) {
                return this;
            }
            throw new FilterException("MUSIC.ID");
        }
        if (music.getId() >= ID_MIN) {
            return this;
        }
        throw new FilterException("MUSIC.ID");
    }

    public CommentFilter filterContent(String content, boolean nullable) throws FilterException {
        notEmptyFilter.doFilter(content, "CONTENT", nullable);
        return this;
    }

    public CommentFilter filterReleaseTime(Date releaseTime, boolean nullable) throws FilterException {
        if (releaseTime == null) {
            if (nullable) {
                return this;
            }
            throw new FilterException("RELEASE_TIME");
        }
        return this;
    }
}
