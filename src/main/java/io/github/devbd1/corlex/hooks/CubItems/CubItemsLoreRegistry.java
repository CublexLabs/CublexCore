// At startup or reload, reads plugins/CubItems/items.yml and builds an in-memory map of item-keys → (language → lore lines).
// Exposes a single lookup method, getLore(itemKey, lang), which returns the right list of colored lore lines (falling back to English or empty).

package io.github.devbd1.corlex.hooks.CubItems;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CubItemsLoreRegistry {

    //private static final Map<String, List<String>> loreMap = new HashMap<>();
    private static final Map<String, Map<String, List<String>>> loreMap = new HashMap<>();


    public static void load(File cubItemsFolder) {
        File itemsFile = new File(cubItemsFolder, "items.yml");
        if (!itemsFile.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(itemsFile);
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection == null) return;

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection section = itemsSection.getConfigurationSection(key);
            if (section == null) continue;

            ConfigurationSection loreSection = section.getConfigurationSection("lore");
            if (loreSection != null) {
                Map<String, List<String>> perLang = new HashMap<>();
                for (String lang : loreSection.getKeys(false)) {
                    List<String> loreLines = loreSection.getStringList(lang);
                    if (!loreLines.isEmpty()) {
                        perLang.put(lang, color(loreLines));
                    }
                }
                loreMap.put(key, perLang);
                System.out.println("[Corlex] Loaded lore for " + key + ": " + perLang.keySet());
                System.out.println("[Corlex] Loaded " + loreMap.size() + " client lore entries.");
            }
        }
    }

    public static List<String> getLore(String key, String lang) {
        Map<String, List<String>> langMap = loreMap.get(key);
        if (langMap == null) return Collections.emptyList();

        System.out.println("[Corlex] getLore(" + key + ", " + lang + ")");
        System.out.println("[Corlex] All keys: " + loreMap.keySet());
        System.out.println("[Corlex] Lang map for key: " + loreMap.get(key));

        return langMap.getOrDefault(lang, langMap.getOrDefault("en", Collections.emptyList()));
    }


    private static List<String> color(List<String> lines) {
        return lines.stream()
                .map(line -> line.replace('&', '§'))
                .toList();
    }
}
