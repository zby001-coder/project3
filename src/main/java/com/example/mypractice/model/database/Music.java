package com.example.mypractice.model.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * 歌曲
 *
 * @author 张贝易
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Music {
    private Long id;
    private String musicName;
    private String singerName;
    private String description;
    private Date releaseTime;
    private String musicUrl;
    private String mvUrl;
    private String coverUrl;
    private Integer likeCount;
    private String lyric;
    /**
     * 由于歌曲保存在ElasticSearch里面，所以有一个匹配分数，用score保存
     */
    private Double score;

    public Music() {
    }

    public Music(Long id) {
        this.id = id;
    }

    public Music(String musicName) {
        this.musicName = musicName;
    }

    public Music(Long id, String musicName, String singerName) {
        this.id = id;
        this.musicName = musicName;
        this.singerName = singerName;
    }

    public Music initMusic(){
        this.likeCount = 0;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Music setId(Long id) {
        this.id = id;
        return this;
    }

    public String getMusicName() {
        return musicName;
    }

    public Music setMusicName(String musicName) {
        this.musicName = musicName;
        return this;
    }

    public String getSingerName() {
        return singerName;
    }

    public Music setSingerName(String singerName) {
        this.singerName = singerName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Music setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public Music setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
        return this;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public Music setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
        return this;
    }

    public String getMvUrl() {
        return mvUrl;
    }

    public Music setMvUrl(String mvUrl) {
        this.mvUrl = mvUrl;
        return this;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public Music setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
        return this;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public Music setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
        return this;
    }

    public String getLyric() {
        return lyric;
    }

    public Music setLyric(String lyric) {
        this.lyric = lyric;
        return this;
    }

    public Double getScore() {
        return score;
    }

    public Music setScore(Double score) {
        this.score = score;
        return this;
    }

    @Override
    public String toString() {
        return "Music{" +
                "id=" + id +
                ", musicName='" + musicName + '\'' +
                ", singerName='" + singerName + '\'' +
                ", description='" + description + '\'' +
                ", releaseTime=" + releaseTime +
                ", musicUrl='" + musicUrl + '\'' +
                ", mvUrl='" + mvUrl + '\'' +
                ", likeCount=" + likeCount +
                ", lyric='" + lyric + '\'' +
                '}';
    }
}
