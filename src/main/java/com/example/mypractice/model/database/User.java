package com.example.mypractice.model.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author 用户
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    /**
     * 静态常量
     */
    public static final Integer INUSE = 0;
    public static final Integer BANNED = 1;
    public static final Integer COMMON_USER = 0;
    public static final Integer ADMIN = 1;

    /**
     * 用户的字段
     */
    private Long id;
    private String userName;
    private Integer identity;
    private Integer isBanned;
    private String pass;

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public User(String userName) {
        this.userName = userName;
    }

    public User(String userName, String pass) {
        this.userName = userName;
        this.pass = pass;
    }

    public User(Long id, String userName, String pass) {
        this.id = id;
        this.userName = userName;
        this.pass = pass;
    }

    public User initUser(){
        this.isBanned = INUSE;
        return this;
    }

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Integer getIdentity() {
        return identity;
    }

    public User setIdentity(Integer identity) {
        this.identity = identity;
        return this;
    }

    public Integer getIsBanned() {
        return isBanned;
    }

    public User setIsBanned(Integer isBanned) {
        this.isBanned = isBanned;
        return this;
    }

    public String getPass() {
        return pass;
    }

    public User setPass(String pass) {
        this.pass = pass;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", identity=" + identity +
                ", isBanned=" + isBanned +
                ", pass='" + pass + '\'' +
                '}';
    }
}
