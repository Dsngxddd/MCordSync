package com.pnternn.mcordsync.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    private static final Properties properties = new Properties();
    public PropertiesUtil() {
        try {
            FileInputStream in = new FileInputStream("server.properties");
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getValue(String key){
        return properties.getProperty(key);
    }
}
