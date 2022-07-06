package com.example.mypractice.model.web;

import java.util.Date;

public class UserToken {
    private Long id;
    private String userName;
    private Integer identity;
    private Date expire;
    public static final String ID = "ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String IDENTITY = "IDENTITY";
    public static final String EXPIRE = "EXPIRE";

    public UserToken(Long id, String userName, Integer identity, Date expire) {
        this.id = id;
        this.userName = userName;
        this.identity = identity;
        this.expire = expire;
    }

    public Date getExpire() {
        return expire;
    }

    public UserToken setExpire(Date expire) {
        this.expire = expire;
        return this;
    }

    public Long getId() {
        return id;
    }

    public UserToken setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public UserToken setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Integer getIdentity() {
        return identity;
    }

    public UserToken setIdentity(Integer identity) {
        this.identity = identity;
        return this;
    }
}