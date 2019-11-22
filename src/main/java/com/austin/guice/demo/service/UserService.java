package com.austin.guice.demo.service;

import com.austin.guice.demo.dao.UserDao;
import com.google.inject.Inject;

/**
 * @author austin
 * @since 2019/11/22 15:35   Fri
 */
public class UserService {

    @Inject
    private UserDao userDao;

    public void say() {
        System.out.println("UserService say ... ");
        userDao.say();
    }
}
