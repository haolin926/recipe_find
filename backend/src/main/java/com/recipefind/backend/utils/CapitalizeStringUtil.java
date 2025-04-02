package com.recipefind.backend.utils;

public class CapitalizeStringUtil {

    public static String capitalizeString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder stringBuilder = new StringBuilder();
        String[] words = input.split("\\s+");

        for (String word : words) {
            if (!word.isEmpty()) {
                stringBuilder.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return stringBuilder.toString().trim();
    }
}
