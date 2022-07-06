package com.example.mypractice.dao;

import com.example.mypractice.model.database.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserDao {
    int register(@Param("user") User user);

    User login(@Param("user") User user);

    List<User> selectUserByNameCommon(@Param("user") User user, @Param("lastIndex") long lastIndex);

    List<User> selectUserByNameAdmin(@Param("user") User user, @Param("start") int start);

    List<User> selectAllUsers(@Param("start") int start);

    User selectUserById(@Param("user") User user);

    int selectCountByName(@Param("user") User user);

    int selectAllCount();

    int banUser(@Param("user") User user);

    int unsealUser(@Param("user") User user);

    void test(@Param("users") List<User> users);
}
