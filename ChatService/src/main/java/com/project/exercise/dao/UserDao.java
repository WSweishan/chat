package com.project.exercise.dao;

/**
 * 用户相关数据库操作
 */
public interface UserDao {
    /**
     * 用户注册
     */
    int userRegister(String name,String pwd,String email);
    /**
     * 登录操作
     */
    int userLogin(String name,String pwd);
    /**
     * 找回密码操作
     */
    int userSearchPWD(String name,String email);
    /**
     * 通过用户名获得密码
     */
    String getPWDByName(String name);
    /**
     * 通过用户名获得邮箱
     */
    String getEmailByName(String name);
    /**
     * 修改密码操作
     */
    int updatePWD(String name,String pwd);
    /**
     * 修改邮箱操作
     */
    int updateEmail(String name,String email);
    /**
     * 通过用户名删除用户
     */
    int delete(String name);
}
