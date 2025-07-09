package io.github.devbd1.corlex.modules.text;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LocaleLoader {
    public static void loadTranslations(Plugin plugin, Map<String, Map<String, Object>> translations) {
        String[] supported = {"en", "tr"};

        for (String lang : supported) {
            try {
                File file = new File(plugin.getDataFolder(), "lang/" + lang + ".yml");
                if (!file.exists()) {
                    plugin.getLogger().warning("Missing language file: " + file.getName());
                    continue;
                }
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);


                Map<String, Object> raw = config.getValues(false);
                Map<String, Object> deep = deepCast(raw);

                translations.put(lang, deep);

            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load lang/" + lang + ".yml");
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> deepCast(Map<String, Object> input) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof MemorySection section) {
                result.put(entry.getKey(), deepCast(section.getValues(false)));
            } else {
                result.put(entry.getKey(), value);
            }
        }
        return result;
    }
}
