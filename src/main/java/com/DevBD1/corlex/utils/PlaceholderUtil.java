package com.DevBD1.corlex.utils;

import java.util.Map;

public class PlaceholderUtil {

    public static String applyPlaceholders(String input, Map<String, String> staticPlaceholders, Map<String, String> dynamicPlaceholders) {
        if (input == null) return "";

        System.out.println("[DEBUG] PlaceholderUtil input: " + input);
        System.out.println("[DEBUG] Static map: " + staticPlaceholders);
        System.out.println("[DEBUG] Dynamic map: " + dynamicPlaceholders);

        // Apply static placeholders like {prefix}
        for (Map.Entry<String, String> entry : staticPlaceholders.entrySet()) {
            input = input.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        // Apply dynamic placeholders like {coins}, {rank}, {player}
        for (Map.Entry<String, String> entry : dynamicPlaceholders.entrySet()) {
            input = input.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        System.out.println("[DEBUG] Final resolved message: " + input);
        return input;
    }
}
