package com.vwo.framework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public final class JsonDataUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonDataUtil() {
    }

    /**
     * Reads a JSON array of objects from a classpath resource, e.g. src/test/resources/testdata/login_data.json
     */
    public static List<Map<String, String>> readRecords(String classpathResource) {
        try (InputStream input = JsonDataUtil.class.getClassLoader().getResourceAsStream(classpathResource)) {
            if (input == null) {
                throw new IllegalStateException("Test data file not found on classpath: " + classpathResource);
            }
            return MAPPER.readValue(input, MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, Map.class));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse JSON test data: " + classpathResource, e);
        }
    }
}
