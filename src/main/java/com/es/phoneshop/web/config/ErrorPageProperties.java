package com.es.phoneshop.web.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ErrorPageProperties {
    private static final String UNABLE_TO_FIND_PROPERTIES_FILE = "Unable to find %s in the classpath!";
    private static final String UNKNOWN_EXCEPTION_MESSAGE = "Failed to load error page properties.";

    private static final String PROPERTIES_FILE = "error-pages.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ErrorPageProperties.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null)
                throw new IOException(String.format(UNABLE_TO_FIND_PROPERTIES_FILE, PROPERTIES_FILE));
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(UNKNOWN_EXCEPTION_MESSAGE, e);
        }
    }

    public static String getErrorPagePath(int number) {
        return properties.getProperty(Integer.toString(number));
    }
}
