package com.es.phoneshop.utils;

import org.slf4j.Logger;

public class LoggerHelper {
    public static final String BEGIN = "begin";
    public static final String SUCCESS = "success";
    public static final String INIT = "init : ";
    public static final String DO_GET = "doGet : ";
    public static final String DO_POST = "doPost : ";
    public static final String EXCEPTION = "%s: %s";

    public static void logInit(Logger logger, String message) {
        logger.debug(INIT + message);
    }
    public static void logInit(Logger logger, Exception e) {
        logInit(logger, getMessageForException(e));
    }

    public static void logDoGet(Logger logger, String message) {
        logger.debug(DO_GET + message);
    }
    public static void logDoGet(Logger logger, Exception e) {
        logDoGet(logger, getMessageForException(e));
    }

    public static void logDoPost(Logger logger, String message) {
        logger.debug(DO_POST + message);
    }
    public static void logDoPost(Logger logger, Exception e) {
        logDoPost(logger,getMessageForException(e));
    }

    private static String getMessageForException(Exception e) {
        return String.format(EXCEPTION, e.getClass().getSimpleName(), e.getMessage());
    }
}
