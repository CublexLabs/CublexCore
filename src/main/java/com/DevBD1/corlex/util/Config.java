package com.DevBD1.corlex.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Config {
    private static FileConfiguration config;

    public static boolean isLoggingEnabled() {
        return config.getBoolean("logging-enabled", true);
    }

    public static void load(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public static String get(String key) {
        String value = config.getString(key);
        if (value == null) {
            Logger.log("Missing config key: '" + key + "'");
        }
        return value;
    }


    public static Map<String, String> getAllAsPlaceholders() {
        Map<String, String> map = new HashMap<>();
        for (String key : config.getKeys(false)) {
            String value = config.getString(key);
            if (value != null) map.put(key, value);
        }
        return map;
    }
}
