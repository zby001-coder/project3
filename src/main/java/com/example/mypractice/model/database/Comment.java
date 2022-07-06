package com.example.mypractice.model.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    private Long id;
    private User author;
    private Music music;
    private String content;
    private Date releaseTime;
    /**
     * 全文搜索时的匹配分数
     */
    private Double score;

    public Comment() {
    }

    public Comment initComment(){
        return this;
    }

    public Comment(Long id) {
        this.id = id;
    }

    public Comment(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public Comment setId(Long id) {
        this.id = id;
        return this;
    }

    public User getAuthor() {
        return author;
    }

    public Comment setAuthor(User author) {
        this.author = author;
        return this;
    }

    public Music getMusic() {
        return music;
    }

    public Comment setMusic(Music music) {
        this.music = music;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Comment setContent(String content) {
        this.content = content;
        return this;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public Comment setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
        return this;
    }

    public Double getScore() {
        return score;
    }

    public Comment setScore(Double score) {
        this.score = score;
        return this;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", author=" + author +
                ", music=" + music +
                ", content='" + content + '\'' +
                ", releaseTime=" + releaseTime +
                '}';
    }
}
