package com.DevBD1.corlex.lang;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LocaleLoader {
    public static void loadTranslations(Plugin plugin, Map<String, Map<String, String>> translations) {
        String[] supported = {"en", "tr"};

        for (String lang : supported) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(plugin.getResource("lang/" + lang + ".yml"))
                );

                Map<String, String> map = new HashMap<>();
                for (String key : config.getKeys(false)) {
                    map.put(key, config.getString(key));
                }

                translations.put(lang, map);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load lang/" + lang + ".yml");
            }
        }
    }
}
