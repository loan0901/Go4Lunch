package com.example.myapplication.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressService {

    /**
     * This method extracts and returns the street number and name from a full address.
     *
     * @param address The full address string.
     * @return A string containing the street number and name. If no match is found, the original address is returned.
     */
    public static String getStreetAndNumber(String address) {
        // Regular expression to match street number and name
        String regex = "^d+s+[^,]+|^[^,]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(address);
        if (matcher.find()) {
            return matcher.group(0).trim();
        } else {
            return address; // If no match is found, return the original address
        }
    }
}
