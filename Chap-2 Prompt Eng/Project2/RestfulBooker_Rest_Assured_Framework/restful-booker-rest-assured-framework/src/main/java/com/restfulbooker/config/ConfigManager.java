package com.restfulbooker.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized config loader. Reads src/test/resources/config/config.properties from the
 * classpath so the same jar/build runs unchanged on a dev machine or CI agent.
 */
public final class ConfigManager {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConfigManager.class.getClassLoader()
                .getResourceAsStream("config/config.properties")) {
            if (input == null) {
                throw new IllegalStateException("config/config.properties not found on classpath");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config/config.properties", e);
        }
    }

    private ConfigManager() {
    }

    public static String getBaseUri() {
        return PROPERTIES.getProperty("base.uri");
    }

    public static String getDefaultUsername() {
        return PROPERTIES.getProperty("auth.username");
    }

    public static String getDefaultPassword() {
        return PROPERTIES.getProperty("auth.password");
    }

    public static int getRequestTimeoutMs() {
        return Integer.parseInt(PROPERTIES.getProperty("request.timeout.ms"));
    }
}
