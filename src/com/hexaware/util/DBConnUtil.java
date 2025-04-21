package com.hexaware.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnUtil {

    public static Connection getConnection(String propertyFileName) {
        String connectionString = DBPropertyUtil.getConnectionString(propertyFileName);
        if (connectionString == null) {
            System.err.println("Unable to fetch connection string.");
            return null;
        }

        try {
            Connection connection = DriverManager.getConnection(connectionString);
            return connection;
        } catch (SQLException e) {
            System.err.println("Error establishing connection: " + e.getMessage());
            return null;
        }
    }
}