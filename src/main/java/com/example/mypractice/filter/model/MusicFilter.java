package com.example.mypractice.filter.model;

import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.filter.basic.StringNotEmptyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Component
public class MusicFilter {
    @Autowired
    private StringNotEmptyFilter notEmptyFilter;
    private static final Long ID_MIN = 0L;
    private static final Integer LIKE_MIN = 0;

    public MusicFilter filterId(Long id, boolean nullable) throws FilterException {
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

    public MusicFilter filterMusicName(String musicName, boolean nullable) throws FilterException {
        notEmptyFilter.doFilter(musicName, "MUSIC_NAME", nullable);
        return this;
    }

    public MusicFilter filterSingerName(String singerName, boolean nullable) throws FilterException {
        notEmptyFilter.doFilter(singerName, "SINGER_NAME", nullable);
        return this;
    }

    public MusicFilter filterDescription(String description, boolean nullable) throws FilterException {
        notEmptyFilter.doFilter(description, "DESCRIPTION", nullable);
        return this;
    }

    public MusicFilter filterReleaseTime(Date releaseTime, boolean nullable) throws FilterException {
        if (releaseTime == null) {
            if (nullable) {
                return this;
            }
            throw new FilterException("RELEASE_TIME");
        }
        return this;
    }

    public MusicFilter filterLikeCount(Integer likeCount, boolean nullable) throws FilterException {
        if (likeCount == null) {
            if (nullable) {
                return this;
            }
            throw new FilterException("LIKE_COUNT");
        }
        if (likeCount > LIKE_MIN) {
            return this;
        }
        throw new FilterException("LIKE_COUNT");
    }

    public MusicFilter filterLyric(String lyric, boolean nullable) throws FilterException {
        notEmptyFilter.doFilter(lyric, "LYRIC", nullable);
        return this;
    }

    public MusicFilter filterMusicContent(MultipartFile content, boolean nullable) throws FilterException {
        if (content == null || content.isEmpty()) {
            if (nullable) {
                return this;
            }
            throw new FilterException("MUSIC_CONTENT");
        }
        return this;
    }
}
