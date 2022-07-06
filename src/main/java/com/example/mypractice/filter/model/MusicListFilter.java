package com.example.mypractice.filter.model;

import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.filter.basic.CollectionNotEmptyFilter;
import com.example.mypractice.filter.basic.StringNotEmptyFilter;
import com.example.mypractice.model.database.Music;
import com.example.mypractice.model.database.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class MusicListFilter {
    @Autowired
    private StringNotEmptyFilter notEmptyFilter;
    @Autowired
    private CollectionNotEmptyFilter collectionNotEmptyFilter;
    private static final Long ID_MIN = 0L;
    private static final Integer LIKE_MIN = 0;
    private static final Integer[] IS_BANNED_RANGE = new Integer[]{0, 1};

    public MusicListFilter filterId(Long id, boolean nullable) throws FilterException {
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

    public MusicListFilter filterName(String name, boolean nullable) throws FilterException {
        notEmptyFilter.doFilter(name, "NAME", nullable);
        return this;
    }

    public MusicListFilter filterMusicCount(Integer musicCount, boolean nullable) throws FilterException {
        if (musicCount == null) {
            if (nullable) {
                return this;
            }
            throw new FilterException("MUSIC_COUNT");
        }
        if (musicCount >= 0) {
            return this;
        }
        throw new FilterException("MUSIC_COUNT");
    }

    public MusicListFilter filterAuthor(User author, boolean nullable) throws FilterException {
        if (author == null || author.getId() == null) {
            throw new FilterException("AUTHOR");
        }
        if (author.getId() >= ID_MIN) {
            return this;
        }
        throw new FilterException("ID");
    }

    public MusicListFilter filterIsBanned(Integer isBanned, boolean nullable) throws FilterException {
        if (isBanned == null && nullable) {
            return this;
        }
        for (Integer integer : IS_BANNED_RANGE) {
            if (integer.equals(isBanned)) {
                return this;
            }
        }
        throw new FilterException("IS_BANNED");
    }

    public MusicListFilter filterDescription(String description, boolean nullable) throws FilterException {
        notEmptyFilter.doFilter(description, "DESCRIPTION", nullable);
        return this;
    }

    public MusicListFilter filterCreateTime(Date createTime, boolean nullable) throws FilterException {
        if (createTime == null && nullable) {
            return this;
        } else if (createTime != null) {
            return this;
        }
        throw new FilterException("CREATE_TIME");
    }

    public MusicListFilter filterLikeCount(Integer likeCount, boolean nullable) throws FilterException {
        if (likeCount == null) {
            if (nullable) {
                return this;
            }
            throw new FilterException("LIKE_COUNT");
        }
        if (likeCount >= LIKE_MIN) {
            return this;
        }
        throw new FilterException("LIKE_COUNT");
    }

    public MusicListFilter filterMusic(List<Music> music, boolean nullable) throws FilterException {
        collectionNotEmptyFilter.doFilter(music, "MUSIC", nullable);
        return this;
    }
}
