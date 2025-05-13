package com.DevBD1.corlex.lang;

import com.DevBD1.corlex.util.LocaleUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Lang {
    private static final String DEFAULT_LANG = "en";
    private static final Map<String, Map<String, String>> translations = new HashMap<>();

    public static void load(JavaPlugin plugin) {
        LocaleLoader.loadTranslations(plugin, translations);
    }

    public static String t(Player player, String key) {
        String locale = LocaleUtil.getLocale(player); // "en", "tr", etc.
        return t(key, locale);
    }

    public static String t(String key, String locale) {
        Map<String, String> lang = translations.getOrDefault(locale, translations.get(DEFAULT_LANG));
        String raw = lang.getOrDefault(key, translations.get(DEFAULT_LANG).getOrDefault(key, key));
        return applyPlaceholders(raw, lang);
    }

    private static String applyPlaceholders(String msg, Map<String, String> lang) {
        if (msg == null) return "";
        for (Map.Entry<String, String> entry : lang.entrySet()) {
            msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return msg;
    }
}
