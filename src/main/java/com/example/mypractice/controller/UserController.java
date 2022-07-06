package com.example.mypractice.controller;

import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.commons.exception.ServiceFailException;
import com.example.mypractice.model.database.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.http.HttpServletResponse;

public interface UserController {
    String register(User user) throws FilterException, ServiceFailException, JsonProcessingException;

    String login(User user, HttpServletResponse response) throws FilterException, ServiceFailException, JsonProcessingException;

    String selectUserById(Long id) throws FilterException, ServiceFailException, JsonProcessingException;

    String selectUserByNameCommon(String name, Long lastIndex) throws FilterException, JsonProcessingException;

    String selectCountByName(String name) throws FilterException, JsonProcessingException;

    String selectAllUsers(int page) throws FilterException, JsonProcessingException;

    String selectUserByNameAdmin(String userName, int page) throws FilterException, JsonProcessingException;

    String selectAllCount() throws JsonProcessingException;

    String banUser(User user) throws FilterException, ServiceFailException, JsonProcessingException;

    String unsealUser(User user) throws FilterException, ServiceFailException, JsonProcessingException;
}
