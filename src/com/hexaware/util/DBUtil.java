package com.hexaware.util;

import java.sql.Connection;
import java.sql.SQLException;

public class DBUtil {
    public static Connection getDBConn() throws SQLException {
        String propertyFileName = "E:\\JAVA\\Order Management\\OrderManagementSystem\\src\\com\\hexaware\\util\\db.properties";
        return DBConnUtil.getConnection(propertyFileName);
    }
}