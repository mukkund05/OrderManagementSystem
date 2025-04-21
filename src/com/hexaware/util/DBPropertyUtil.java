package com.hexaware.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBPropertyUtil {

    public static String getConnectionString(String propertyFileName) {
    	
        Properties properties = new Properties();
        
        try (FileInputStream fis = new FileInputStream(propertyFileName)) {
        	
            properties.load(fis);
            
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            
            if (url != null && user != null && password != null) {
                return url + "?user=" + user + "&password=" + password;
            } else {
                throw new IllegalArgumentException("Missing required database properties!");
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error reading or constructing connection string: " + e.getMessage());
            return null;
        }
    }
}