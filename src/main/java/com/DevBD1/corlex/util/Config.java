package com.DevBD1.corlex.util;

import com.DevBD1.corlex.util.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import com.DevBD1.corlex.lang.Lang;


import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Config {

    private static JavaPlugin plugin;
    private static FileConfiguration config;
    private static String defaultLanguage = "en";

    public static String getDefaultLanguage() {
        return defaultLanguage;
    }

    public static void load(JavaPlugin plugin) {
        Config.plugin = plugin;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        String configuredLang = config.getString("default-language", "en").toLowerCase();

        File langFile = new File(Config.plugin.getDataFolder(), "lang/" + configuredLang + ".yml");

        if (!langFile.exists()) {
            Config.plugin.getLogger().warning("[Corlex] Language file for '" + configuredLang + "' not found. Generating from en.yml...");

            try (
                    InputStream in = Config.plugin.getResource("lang/en.yml");
                    OutputStream out = Files.newOutputStream(langFile.toPath())
            ) {
                if (in != null) {
                    in.transferTo(out);
                    plugin.getLogger().info("[Corlex] Created lang/" + configuredLang + ".yml from fallback en.yml.");
                } else {
                    plugin.getLogger().severe("[Corlex] Missing bundled fallback en.yml in JAR!");
                }
            } catch (Exception e) {
                plugin.getLogger().severe("[Corlex] Failed to copy fallback en.yml to lang/" + configuredLang + ".yml: " + e.getMessage());
            }
        }

        defaultLanguage = configuredLang;
    }

    public static void printStatusToConsole() {


        String logging = isLoggingEnabled() ? "enabled" : "disabled";
        String lang = getDefaultLanguage();
        String serverName = get().getString("server-name", "Unknown");

        plugin.getLogger().info("Corlex Config Status:");
        plugin.getLogger().info(" - Logging: " + logging);
        plugin.getLogger().info(" - Default Language: " + lang);
        plugin.getLogger().info(" - Server Name: " + serverName);
    }

    public static FileConfiguration get() {
        return config;
    }

    public static boolean isLoggingEnabled() {
        return config.getBoolean("logging-enabled", true);
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
