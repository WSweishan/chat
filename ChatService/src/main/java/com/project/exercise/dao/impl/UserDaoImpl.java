package com.project.exercise.dao.impl;

import com.project.exercise.dao.UserDao;
import com.project.exercise.util.C3p0Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoImpl implements UserDao {
    /**
     * 用户注册
     */
    @Override
    public int userRegister(String name, String pwd, String email) {
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        try {
            //获取Statement实例
            String sql="select name from user where name=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,name);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return 101;//101:用户名已存在，不能注册
            }
            sql="insert into User(name,pwd,email) values(?,?,?);";
            statement = connection.prepareStatement(sql);
            statement.setString(1,name);
            statement.setString(2,pwd);
            statement.setString(3,email);
            int row=statement.executeUpdate();
            if(row!=0){
                return 100;//注册成功
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 200;//注册失败
    }
    /**
     * 登录操作
     */
    @Override
    public int userLogin(String name, String pwd) {
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        try {
            //获取Statement实例
            String sql="select name,pwd from user where name=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,name);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()){
                return 301;//301:用户不存在
            }
            if(resultSet.getString(2).equals(pwd)){
                return 300;//300:用户登录成功
            }else{
                return 302;//302:用户密码错误
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 400;//400:用户登录失败
    }
    /**
     * 找回密码操作
     */
    @Override
    public int userSearchPWD(String name, String email) {
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        try {
            //获取Statement实例
            String sql="select name,email from user where name=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,name);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()){
                return 301;//301:用户不存在
            }
            if(resultSet.getString(2).equals(email)){
                return 300;//300:用户找回密码成功
            }else{
                return 303;//303:用户邮箱错误
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 400;//400:用户找回密码失败
    }
    /**
     * 通过用户名获得密码
     */
    @Override
    public String getPWDByName(String name) {
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        String pwd=null;
        try {
            //获取Statement实例
            String sql="select pwd from user where name=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,name);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            pwd=resultSet.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return pwd;
    }
    /**
     * 通过用户名获得邮箱
     */
    @Override
    public String getEmailByName(String name) {
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        String email=null;
        try {
            //获取Statement实例
            String sql="select email from user where name=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,name);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            email=resultSet.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return email;
    }
    /**
     * 修改密码操作
     */
    @Override
    public int updatePWD(String name, String pwd) {
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        try {
            //获取Statement实例
            String sql="update user set pwd=? where name=?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,pwd);
            statement.setString(2,name);
            int row=statement.executeUpdate();
            if(row!=0){
                return 100;//用户修改密码成功
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 200;//200:用户修改密码失败
    }
    /**
     * 修改邮箱操作
     */
    @Override
    public int updateEmail(String name, String email) {
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        try {
            //获取Statement实例
            String sql="update user set email=? where name=?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,email);
            statement.setString(2,name);
            int row=statement.executeUpdate();
            if(row!=0){
                return 100;//用户修改邮箱成功
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 200;//200:用户修改邮箱失败
    }
    /**
     * 通过用户名删除用户
     */
    @Override
    public int delete(String name){
        //获取connect实例
        Connection connection = C3p0Util.getConnection();
        try {
            //获取Statement实例
            String sql="delete from user where name=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,name);
            int row=statement.executeUpdate();
            if(row!=0){
                return 100;//注销成功
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 200;//注销失败
    }
}
