package com.es.phoneshop.utils;

public class PhoneValidator {
    public static boolean isValidNumber(String phone) {
        String cleanedPhone = phone.replaceAll("[^0-9+]","");
        String regex = "^(\\+375|375|80)(29|25|44|33)\\d{7}$";
        return cleanedPhone.matches(regex);
    }
}
