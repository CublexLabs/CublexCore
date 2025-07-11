package io.github.devbd1.cublexcore.modules.text;

import io.github.devbd1.cublexcore.utilities.ColorManager;
import io.github.devbd1.cublexcore.utilities.ConfigManager;
import io.github.devbd1.cublexcore.utilities.LoggingManager;
import io.github.devbd1.cublexcore.utilities.PlaceholderManager;
import org.bukkit.Bukkit;
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

        String locale = (player != null) ? getLocale(player) : DEFAULT_LANG;
        return translateForLocale(player, key, locale, dynamic);
    }

    public static String t(String key, String locale, Map<String, String> dynamic) {
        // Delegate through the same pipeline with no Player context
        return translateForLocale(null, key, locale, dynamic);
    }

    public static Map<String, Map<String, Object>> getTranslations() {
        return translations;
    }

    private static String translateForLocale(Player player,
                                             String key,
                                             String locale,
                                             Map<String, String> dynamic) {
        LoggingManager logger = Bukkit.getServicesManager().load(LoggingManager.class);
        logger.log("Translating key '" + key + "' for locale '" + locale + "'");

        logger.log("[DEBUG] --- Lang.t() called ---");
        logger.log("[DEBUG] Requested key: " + key);
        logger.log("[DEBUG] Requested locale: " + locale);

        Map<String, Object> lang = translations.getOrDefault(locale, translations.get(DEFAULT_LANG));
        logger.log("[DEBUG] Using lang map for locale: " + locale + " -> " + lang);

        String raw = getNestedValue(lang, key);
        logger.log("[DEBUG] Raw value: " + raw);

        if (raw == null) {
            logger.log("Missing key '" + key + "' in locale '" + locale + "', falling back to '" + DEFAULT_LANG + "'");
            lang = translations.get(DEFAULT_LANG);
            logger.log("[DEBUG] Fallback to default locale: " + DEFAULT_LANG);
            raw = getNestedValue(lang, key);
            logger.log("[DEBUG] Raw fallback value: " + raw);
        }

        if (raw == null) {
            logger.log("[DEBUG] Final fallback failed, returning key");
            return key;
        }

        Map<String, String> staticPlaceholders = flatten(lang);
        staticPlaceholders.putAll(ConfigManager.getAllAsPlaceholders());

        logger.log("[DEBUG] Static placeholders: " + staticPlaceholders);
        logger.log("[DEBUG] Dynamic placeholders: " + dynamic);

        // now use an instance of PlaceholderManager instead of a static call
        PlaceholderManager ph = new PlaceholderManager(logger);
        String result = ph.applyPlaceholders(player, raw, staticPlaceholders, dynamic);

        result = ColorManager.applyColorFormatting(result);
        logger.log("[DEBUG] Final result: " + result);

        return result;
    }

    private static String getLocale(Player player) {
        try {
            return player.locale().getLanguage().toLowerCase();
        } catch (Exception e) {
            return DEFAULT_LANG;
        }
    }

    public static String getNestedValue(Map<String, ?> map, String path) {
        String[] parts = path.split("\\.");
        Object current = map;
        for (String part : parts) {
            if (!(current instanceof Map)) return null;
            current = ((Map<?, ?>) current).get(part);
        }
        return (current instanceof String) ? (String) current : null;
    }

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
        LoggingManager logger = Bukkit.getServicesManager().load(LoggingManager.class);
        logger.log("[DEBUG] test corlex.status = " + value);
    }
}
