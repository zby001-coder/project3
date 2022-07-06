package com.example.mypractice.controller.impl;

import com.example.mypractice.commons.constant.Bodies;
import com.example.mypractice.commons.constant.Headers;
import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.commons.exception.ServiceFailException;
import com.example.mypractice.commons.util.JwtGenerator;
import com.example.mypractice.commons.util.RedisUtil;
import com.example.mypractice.controller.UserController;
import com.example.mypractice.controller.checker.UserChecker;
import com.example.mypractice.model.database.User;
import com.example.mypractice.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@RestController
public class UserControllerImpl implements UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserChecker userChecker;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtGenerator jwtGenerator;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    @PostMapping("/unAuth/user/register")
    public String register(@RequestBody User user) throws FilterException, ServiceFailException, JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(1);
        userChecker.register(user);
        userService.register(user);
        result.put(Bodies.MESSAGE, Bodies.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/unAuth/user/login")
    public String login(@RequestBody User user, HttpServletResponse httpServletResponse) throws FilterException, ServiceFailException, JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        userChecker.login(user, httpServletResponse);
        user = userService.login(user);
        String token = jwtGenerator.createToken(user);
        httpServletResponse.setHeader(Headers.AUTH_TOKEN, token);
        result.put(Bodies.MESSAGE, Bodies.SUCCESS);
        result.put(Bodies.USER, user);
        //将token保存到redis中
        redisUtil.updateRedisToken(user.getId(), token);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/unAuth/user/select/userId")
    public String selectUserById(@RequestParam Long id) throws FilterException, ServiceFailException, JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        userChecker.selectUserById(id);
        User user = userService.selectUserById(new User(id));
        result.put(Bodies.MESSAGE, Bodies.SUCCESS);
        result.put(Bodies.USER, user);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/unAuth/user/select/userName")
    public String selectUserByNameCommon(@RequestParam String userName, @RequestParam Long lastIndex) throws FilterException, JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        userChecker.selectUserByNameCommon(userName, lastIndex);
        List<User> users = userService.selectUserByNameCommon(new User(userName), lastIndex);
        result.put(Bodies.MESSAGE, Bodies.SUCCESS);
        result.put(Bodies.USER, users);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/unAuth/user/count/userName")
    public String selectCountByName(@RequestParam String userName) throws FilterException, JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        userChecker.selectCountByName(userName);
        int count = userService.selectCountByName(new User(userName));
        result.put(Bodies.MESSAGE, Bodies.SUCCESS);
        result.put(Bodies.COUNT, count);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/admin/user/selectAll")
    public String selectAllUsers(@RequestParam int page) throws FilterException, JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        userChecker.selectAllUsers(page);
        List<User> users = userService.selectAllUsers(page);
        result.put(Bodies.MESSAGE, Bodies.SUCCESS);
        result.put(Bodies.USER, users);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/admin/user/select/userName")
    public String selectUserByNameAdmin(@RequestParam String userName, @RequestParam int page) throws FilterException, JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        userChecker.selectUserByNameAdmin(userName, page);
        List<User> users = userService.selectUserByNameAdmin(new User(userName), page);
        result.put(Bodies.MESSAGE, Bodies.SUCCESS);
        result.put(Bodies.USER, users);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @GetMapping("/admin/user/countAll")
    public String selectAllCount() throws JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(2);
        userChecker.selectAllCount();
        int count = userService.selectAllCount();
        result.put(Bodies.MESSAGE, Bodies.SUCCESS);
        result.put(Bodies.COUNT, count);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/admin/user/ban")
    public String banUser(@RequestBody User user) throws FilterException, ServiceFailException, JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(1);
        userChecker.banUser(user);
        userService.banUser(user);
        result.put(Bodies.MESSAGE, Bodies.SUCCESS);
        return objectMapper.writeValueAsString(result);
    }

    @Override
    @PostMapping("/admin/user/unseal")
    public String unsealUser(@RequestBody User user) throws FilterException, ServiceFailException, JsonProcessingException {
        HashMap<String, Object> result = new HashMap<>(1);
        userChecker.unsealUser(user);
        userService.unsealUser(user);
        result.put(Bodies.MESSAGE, Bodies.SUCCESS);
        //取消用户的token，防止封禁后还能登录
        redisUtil.disableRedisToken(user.getId());
        return objectMapper.writeValueAsString(result);
    }
}
