package com.tezkit.wallet.utils;

public class Strings {

    static public String capsToCamelCase(String text) {
        StringBuffer sb = new StringBuffer();
        for (String s : text.split("_")) {
            sb.append(Character.toUpperCase(s.charAt(0)));
            if (s.length() > 1) {
                sb.append(s.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

}
