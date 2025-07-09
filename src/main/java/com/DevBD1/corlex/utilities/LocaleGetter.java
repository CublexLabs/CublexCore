package com.DevBD1.corlex.utilities;

import org.bukkit.entity.Player;

public class LocaleGetter {

    public static String getPlayerLocale(Player player) {
        try {
            return player.locale().getLanguage().toLowerCase();
        } catch (Exception e) {
            return getDefaultLocale();
        }
    }

    public static String getDefaultLocale() {

        return ConfigManager.getKeyValue("fallback-language", String.class, "en").toLowerCase();
    }
}
