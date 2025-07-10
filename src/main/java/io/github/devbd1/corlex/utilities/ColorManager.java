package io.github.devbd1.corlex.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorManager {
    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:#([A-Fa-f0-9]{6}):#([A-Fa-f0-9]{6})>(.*?)</gradient>");

    public static String applyColorFormatting(String input) {
        input = applyGradients(input);
        input = applyHexColors(input);
        input = replaceColorCodes(input);
        return input;
    }

    public static String applyHexColors(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            String replacement = toMinecraftColor(hex);
            matcher.appendReplacement(buffer, replacement);
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static String applyGradients(String input) {
        Matcher matcher = GRADIENT_PATTERN.matcher(input);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String start = matcher.group(1);
            String end = matcher.group(2);
            String gradientText = matcher.group(3);

            String replaced = applyGradient(gradientText, start, end);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replaced));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String applyGradient(String text, String startHex, String endHex) {
        int length = text.length();
        StringBuilder output = new StringBuilder();

        int[] startRGB = hexToRgb(startHex);
        int[] endRGB = hexToRgb(endHex);

        for (int i = 0; i < length; i++) {
            double ratio = (double) i / Math.max(1, length - 1);
            int r = (int) (startRGB[0] + ratio * (endRGB[0] - startRGB[0]));
            int g = (int) (startRGB[1] + ratio * (endRGB[1] - startRGB[1]));
            int b = (int) (startRGB[2] + ratio * (endRGB[2] - startRGB[2]));

            String hex = String.format("%02x%02x%02x", r, g, b);
            output.append(toMinecraftColor(hex)).append(text.charAt(i));
        }

        return output.toString();
    }

    private static int[] hexToRgb(String hex) {
        return new int[]{
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(2, 4), 16),
                Integer.parseInt(hex.substring(4, 6), 16)
        };
    }

    private static String toMinecraftColor(String hex) {
        StringBuilder out = new StringBuilder("§x");
        for (char c : hex.toCharArray()) {
            out.append('§').append(c);
        }
        return out.toString();
    }

    private static String replaceColorCodes(String input) {
        // & → § transform (exmp: &a, &6, &l, &r etc)
        return input.replaceAll("(?i)&([0-9A-FK-OR])", "§$1");
    }

}
