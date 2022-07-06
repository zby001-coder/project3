package com.example.mypractice.service;

import com.example.mypractice.commons.constant.Others;
import com.example.mypractice.commons.exception.ServiceFailException;
import com.example.mypractice.commons.util.SnowflakeIdGenerator;
import com.example.mypractice.dao.UserDao;
import com.example.mypractice.model.database.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private SnowflakeIdGenerator idGenerator;

    public User register(User user) throws ServiceFailException {
        user.setId(idGenerator.nextId());
        user.initUser();
        int register = userDao.register(user);
        if (register < 1) {
            throw new ServiceFailException("register");
        }
        return user;
    }

    public User login(User user) throws ServiceFailException {
        user = userDao.login(user);
        if (user == null) {
            throw new ServiceFailException("login");
        }
        return user;
    }

    public User selectUserById(User user) throws ServiceFailException {
        user = userDao.selectUserById(user);
        if (user == null) {
            throw new ServiceFailException("selectUserById");
        }
        return user;
    }

    public List<User> selectUserByNameCommon(User user, long lastIndex) {
        return userDao.selectUserByNameCommon(user, lastIndex);
    }

    public int selectCountByName(User user) {
        return userDao.selectCountByName(user);
    }

    public List<User> selectAllUsers(int page) {
        return userDao.selectAllUsers((page - 1) * Others.PAGE_SIZE);
    }

    public List<User> selectUserByNameAdmin(User user, int page) {
        return userDao.selectUserByNameAdmin(user, (page - 1) * Others.PAGE_SIZE);
    }

    public int selectAllCount() {
        return userDao.selectAllCount();
    }

    public void banUser(User user) throws ServiceFailException {
        int i = userDao.banUser(user);
        if (i < 1) {
            throw new ServiceFailException("banUser");
        }
    }

    public void unsealUser(User user) throws ServiceFailException {
        int i = userDao.unsealUser(user);
        if (i < 1) {
            throw new ServiceFailException("unsealUser");
        }
    }
}
