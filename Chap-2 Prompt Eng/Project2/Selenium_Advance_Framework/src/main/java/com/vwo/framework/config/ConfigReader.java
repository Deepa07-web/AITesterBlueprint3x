package com.vwo.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads config-{env}.properties from the classpath. Env selected via -Denv=qa|staging|prod
 * (defaults to qa). Values not stated in the PRD (staging/prod URLs) are placeholders and
 * must be replaced with real environment values before use.
 */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();
    private static final String ENV = System.getProperty("env", "qa");

    static {
        String fileName = "config/config-" + ENV + ".properties";
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IllegalStateException("Config file not found on classpath: " + fileName);
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config file: " + fileName, e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        String systemOverride = System.getProperty(key);
        if (systemOverride != null && !systemOverride.isEmpty()) {
            return systemOverride;
        }
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing config key '" + key + "' in config-" + ENV + ".properties");
        }
        return value;
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static String getEnvironment() {
        return ENV;
    }
}
