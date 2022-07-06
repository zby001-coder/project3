package com.example.mypractice.controller.checker;

import com.example.mypractice.commons.constant.Others;
import com.example.mypractice.commons.exception.FilterException;
import com.example.mypractice.controller.UserController;
import com.example.mypractice.filter.model.PagingFilter;
import com.example.mypractice.filter.model.UserFilter;
import com.example.mypractice.model.database.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public class UserChecker implements UserController {
    @Autowired
    private UserFilter userFilter;
    @Autowired
    private PagingFilter pagingFilter;

    @Override
    public String register(User user) throws FilterException {
        if (user == null) {
            throw new FilterException("USER");
        }
        userFilter.filterName(user.getUserName(), false).filterPass(user.getPass(), false)
                .filterIdentity(user.getIdentity(), false);
        return Others.SUCCESS;
    }

    @Override
    public String login(User user, HttpServletResponse response) throws FilterException {
        if (user == null) {
            throw new FilterException("USER");
        }
        userFilter.filterName(user.getUserName(), false).filterPass(user.getPass(), false);
        return Others.SUCCESS;
    }

    @Override
    public String selectUserById(Long id) throws FilterException {
        userFilter.filterId(id, false);
        return Others.SUCCESS;
    }

    @Override
    public String selectUserByNameCommon(String name, Long lastIndex) throws FilterException {
        userFilter.filterName(name, false);
        pagingFilter.filterLastIndex(lastIndex,false);
        return Others.SUCCESS;
    }

    @Override
    public String selectCountByName(String name) throws FilterException {
        userFilter.filterName(name, false);
        return Others.SUCCESS;
    }

    @Override
    public String selectAllUsers(int page) throws FilterException {
        pagingFilter.filterPage(page);
        return Others.SUCCESS;
    }

    @Override
    public String selectUserByNameAdmin(String userName, int page) throws FilterException {
        userFilter.filterName(userName, false);
        pagingFilter.filterPage(page);
        return Others.SUCCESS;
    }

    @Override
    public String selectAllCount() {
        return Others.SUCCESS;
    }

    @Override
    public String banUser(User user) throws FilterException {
        if (user == null) {
            throw new FilterException("USER");
        }
        userFilter.filterId(user.getId(), false);
        return Others.SUCCESS;
    }

    @Override
    public String unsealUser(User user) throws FilterException {
        if (user == null) {
            throw new FilterException("USER");
        }
        userFilter.filterId(user.getId(), false);
        return Others.SUCCESS;
    }
}
