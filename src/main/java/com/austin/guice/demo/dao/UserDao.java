package com.austin.guice.demo.dao;

import javax.inject.Singleton;

/**
 * @author austin
 * @since 2019/11/22 15:34   Fri
 */
// 打上了这个标记说明是单例的，否则Guice每次回返回一个新的对象
@Singleton
public class UserDao {

    public void say() {
        System.out.println("user dao saying");
    }
}
