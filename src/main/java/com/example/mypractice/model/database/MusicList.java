package com.example.mypractice.model.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

/**
 * 歌单实体
 *
 * @author 张贝易
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MusicList {
    public static final Integer BANNED = 1;
    public static final Integer IN_USE = 0;
    private Long id;
    private String name;
    private Integer musicCount;
    private User author;
    private Integer isBanned;
    private String description;
    private Date createTime;
    private Integer likeCount;
    private List<Music> music;
    private String coverUrl;
    /**
     * score为全文索引时的匹配分数
     */
    private Double score;

    public MusicList() {
    }

    public MusicList(Long id) {
        this.id = id;
    }

    public MusicList(String name) {
        this.name = name;
    }

    public MusicList(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public MusicList initMusicList(){
        this.musicCount = 0;
        this.likeCount = 0;
        this.isBanned = IN_USE;
        return this;
    }

    public Long getId() {
        return id;
    }

    public MusicList setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public MusicList setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getMusicCount() {
        return musicCount;
    }

    public MusicList setMusicCount(Integer musicCount) {
        this.musicCount = musicCount;
        return this;
    }

    public User getAuthor() {
        return author;
    }

    public MusicList setAuthor(User author) {
        this.author = author;
        return this;
    }

    public Integer getIsBanned() {
        return isBanned;
    }

    public MusicList setIsBanned(Integer isBanned) {
        this.isBanned = isBanned;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MusicList setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public MusicList setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public MusicList setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
        return this;
    }

    public List<Music> getMusic() {
        return music;
    }

    /**
     * 在此时顺便把music数量设置了
     *
     * @param music 歌曲列表
     * @return
     */
    public MusicList setMusic(List<Music> music) {
        this.music = music;
        this.musicCount = music.size();
        return this;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public MusicList setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
        return this;
    }

    public Double getScore() {
        return score;
    }

    public MusicList setScore(Double score) {
        this.score = score;
        return this;
    }

    @Override
    public String toString() {
        return "MusicList{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", musicCount=" + musicCount +
                ", author=" + author +
                ", isBanned=" + isBanned +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", likeCount=" + likeCount +
                ", music=" + music +
                ", coverUrl='" + coverUrl + '\'' +
                ", score=" + score +
                '}';
    }
}
