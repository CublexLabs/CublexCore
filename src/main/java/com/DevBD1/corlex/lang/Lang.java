package com.DevBD1.corlex.lang;

import com.DevBD1.corlex.utils.Config;
import com.DevBD1.corlex.utils.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Lang {
    private static final String DEFAULT_LANG = "en";
    private static final Map<String, Map<String, Object>> translations = new HashMap<>();


    public static void load(JavaPlugin plugin) {
        translations.clear();
        LocaleLoader.loadTranslations(plugin, translations);

    }

    public static LangMessage forPlayer(Player player) {
        return new LangMessage(player);
    }

    public static String t(Player player, String key) {
        return t(player, key, Map.of());
    }

    public static String t(Player player, String key, Map<String, String> dynamic) {
        dynamic = new HashMap<>(dynamic);

        if (player != null) {
            dynamic.putIfAbsent("player", player.getName());
            dynamic.putIfAbsent("world", player.getWorld().getName());
        } else {
            dynamic.putIfAbsent("player", "Console");
            dynamic.putIfAbsent("world", "Console");
        }

        String locale = (player != null) ? getLocale(player) : "en";
        return t(key, locale, dynamic);
    }

    public static String t(String key, String locale, Map<String, String> dynamic) {
        System.out.println("[DEBUG] --- Lang.t() called ---");
        System.out.println("[DEBUG] Requested key: " + key);
        System.out.println("[DEBUG] Requested locale: " + locale);

        Map<String, Object> lang = translations.getOrDefault(locale, translations.get(DEFAULT_LANG));
        System.out.println("[DEBUG] Using lang map for locale: " + locale + " -> " + lang);

        String raw = getNestedValue(lang, key);
        System.out.println("[DEBUG] Raw value from getNestedValue(): " + raw);

        if (raw == null) {
            lang = translations.get(DEFAULT_LANG);
            System.out.println("[DEBUG] Fallback to default locale: " + DEFAULT_LANG);
            raw = getNestedValue(lang, key);
            System.out.println("[DEBUG] Raw fallback value: " + raw);
        }

        if (raw == null) {
            System.out.println("[DEBUG] Final fallback failed, returning key as raw string");
            return key;
        }

        Map<String, String> staticPlaceholders = flatten(lang);
        staticPlaceholders.putAll(Config.getAllAsPlaceholders());

        System.out.println("[DEBUG] Static placeholders: " + staticPlaceholders);
        System.out.println("[DEBUG] Dynamic placeholders: " + dynamic);

        String result = PlaceholderUtil.applyPlaceholders(raw, staticPlaceholders, dynamic);
        System.out.println("[DEBUG] Final result: " + result);

        return result;
    }


    public static Map<String, Map<String, Object>> getTranslations() {
        return translations;
    }

    private static String getLocale(Player player) {
        try {
            return player.locale().getLanguage().toLowerCase();
        } catch (Exception e) {
            return DEFAULT_LANG;
        }
    }

    // Supports dot notation: "corlex.welcome" resolves to nested value in the map
    private static String getNestedValue(Map<String, ?> map, String path) {
        String[] parts = path.split("\\.");
        Object current = map;

        for (String part : parts) {
            if (!(current instanceof Map)) return null;
            current = ((Map<?, ?>) current).get(part);
        }

        return (current instanceof String) ? (String) current : null;
    }

    // Flattens all top-level key-values (used for static replacements like {prefix})
    private static Map<String, String> flatten(Map<String, ?> map) {
        Map<String, String> flat = new HashMap<>();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            if (entry.getValue() instanceof String s) {
                flat.put(entry.getKey(), s);
            }
        }
        return flat;
    }

    public static void testNestedValue() {
        Map<String, Object> test = Map.of(
                "corlex", Map.of(
                        "status", "This is a test with {coins} and {rank}"
                )
        );

        String value = getNestedValue(test, "corlex.status");
        System.out.println("[DEBUG] test corlex.status = " + value);
    }

}
