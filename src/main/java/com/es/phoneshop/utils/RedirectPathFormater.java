package com.es.phoneshop.utils;

public class RedirectPathFormater {
    public static String formatSuccessPath(String requestContextPath, String rootPath, String successMessage) {
        return requestContextPath + String.format(rootPath, successMessage);
    }

    public static String formatSuccessPath(String requestContextPath, String rootPath, Long number, String successMessage) {
        return requestContextPath + String.format(rootPath, number, successMessage);
    }

    public static String formatErrorPath(String requestContextPath, String rootPath, Long number, String errorMessage) {
        return requestContextPath + String.format(rootPath, number, errorMessage);
    }

    public static String formatErrorPath(String requestContextPath, String rootPath, String errorMessage) {
        return requestContextPath + String.format(rootPath, errorMessage);
    }
}
