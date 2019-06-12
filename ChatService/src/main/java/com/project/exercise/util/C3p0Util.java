package com.project.exercise.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class C3p0Util {
    private static ComboPooledDataSource dataSource = new ComboPooledDataSource("mysql");

    // 从连接池中取用一个连接
    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            System.err.println("连接池初始化失败");
        }
        return null;
    }

    // 释放连接回连接池
    public static void close(Connection conn, PreparedStatement pst, ResultSet rs) {
        if (rs != null) {
            try {
                if (conn != null)
                    conn.close();
                if (rs != null)
                    rs.close();
                if (pst != null)
                    pst.close();
            } catch (SQLException e) {
            }
        }
    }
}

