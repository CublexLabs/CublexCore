package com.DevBD1.corlex.utilities;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private static JavaPlugin plugin;
    private static FileConfiguration config;
    // pay attention to possible NullPointerException errors
    public static String configServerName = ConfigManager.getKeyValue("server-name", String.class, "Cublex.net");
    public static String configLang = ConfigManager.getKeyValue("fallback-language", String.class, "en").toLowerCase();
    public static boolean configLog = ConfigManager.getKeyValue("logging-enabled", Boolean.class, false);
    public static boolean configRestful = ConfigManager.getKeyValue("restful-enabled", Boolean.class, false);

    public static void load(JavaPlugin plugin) {
        ConfigManager.plugin = plugin;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        File langFile = new File(ConfigManager.plugin.getDataFolder(), "lang/" + configLang + ".yml");

        if (!langFile.exists()) {
            ConfigManager.plugin.getLogger().warning("[Corlex] Language file for '" + configLang + "' not found. Generating from en.yml...");

            try (
                    InputStream in = ConfigManager.plugin.getResource("lang/en.yml");
                    OutputStream out = Files.newOutputStream(langFile.toPath())
            ) {
                if (in != null) {
                    in.transferTo(out);
                    plugin.getLogger().info("[Corlex] Created lang/" + configLang + ".yml from fallback language en.yml.");
                } else {
                    plugin.getLogger().severe("[Corlex] Missing bundled fallback en.yml in the JAR! Stop the server and create '" + configLang + ".yml' under /Corlex/lang directory.");
                }
            } catch (Exception e) {
                plugin.getLogger().severe("[Corlex] Failed to copy fallback en.yml to lang/" + configLang + ".yml: " + e.getMessage());
            }
        }
    }

    public static void printStatusToConsole() {
        plugin.getLogger().info(ConfigManager.getKeyValue("prefix", String.class, "Corlex") + " Config Status:");
        plugin.getLogger().info(" - Server Name: " + configServerName);
        plugin.getLogger().info(" - Default Language: " + configLang);
        plugin.getLogger().info(" - Logging: " + configLog);
        plugin.getLogger().info(" - REST API: " + configRestful);
        plugin.getLogger().info(" - Discord: " + ConfigManager.getKeyValue("discord", String.class, ""));
        plugin.getLogger().info(" - Telegram: " + ConfigManager.getKeyValue("telegram", String.class, ""));
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (langDir.exists() && langDir.isDirectory()) {
            String[] files = langDir.list((dir, name) -> name.toLowerCase().endsWith(".yml"));
            if (files != null && files.length > 0) {
                plugin.getLogger().info(" - Loaded locale files: " + String.join(", ", files));
            } else {
                plugin.getLogger().warning(" - No locale files found in " + langDir.getPath());
            }
        } else {
            plugin.getLogger().warning(" - Locale folder not found: " + langDir.getPath());
        }
    }

    public static FileConfiguration get() {
        return config;
    }

    public static Map<String, String> getAllAsPlaceholders() {
        Map<String, String> map = new HashMap<>();
        for (String key : config.getKeys(false)) {
            String value = config.getString(key);
            if (value != null) map.put(key, value);
        }
        return map;
    }

    private static void validateKey(String key) {
        if (key == null || !key.matches("^[a-zA-Z0-9\\.\\-]+$")) {
            throw new IllegalArgumentException("Invalid config key: " + key);
        }
    }

//    public static String getKeyValue(String key) {
//        validateKey(key);
//        String v = config.getString(key);
//        if (v == null) {
//            LoggingManager.log("Missing config key: '" + key + "'");
//            return "";
//        }
//        return v;
//    }

    @SuppressWarnings("unchecked")
    public static <T> T getKeyValue(String key, Class<T> type, T defaultValue) {
        validateKey(key);

        try {
            if (type == String.class) {
                String v = config.getString(key);
                if (v == null || v.isEmpty()) {
                    plugin.getLogger().warning("[Corlex] Missing or empty key '" + key + "'. Using default: " + defaultValue);
                    return defaultValue;
                }
                return (T) v;
            }
            if (type == Integer.class || type == int.class) {
                int v = config.getInt(key);
                if (!config.contains(key)) {
                    plugin.getLogger().warning("[Corlex] Missing key '" + key + "'. Using default: " + defaultValue);
                    return defaultValue;
                }
                return (T) Integer.valueOf(v);
            }
            if (type == Boolean.class || type == boolean.class) {
                if (!config.contains(key)) {
                    plugin.getLogger().warning("[Corlex] Missing key '" + key + "'. Using default: " + defaultValue);
                    return defaultValue;
                }
                return (T) Boolean.valueOf(config.getBoolean(key));
            }
            if (type == Double.class || type == double.class) {
                double v = config.getDouble(key);
                if (!config.contains(key)) {
                    plugin.getLogger().warning("[Corlex] Missing key '" + key + "'. Using default: " + defaultValue);
                    return defaultValue;
                }
                return (T) Double.valueOf(v);
            }
            if (List.class.isAssignableFrom(type)) {
                List<?> v = config.getList(key);
                if (v == null) {
                    plugin.getLogger().warning("[Corlex] Missing key '" + key + "'. Using default list.");
                    return defaultValue;
                }
                return (T) v;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("[Corlex] Error reading key '" + key + "': " + e.getMessage() + ". Using default.");
        }

        plugin.getLogger().warning("[Corlex] Unsupported type or missing key '" + key + "'. Using default: " + defaultValue);
        return defaultValue;
    }

}
