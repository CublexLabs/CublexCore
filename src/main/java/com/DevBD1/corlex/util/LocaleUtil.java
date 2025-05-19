package com.DevBD1.corlex.util;

import org.bukkit.entity.Player;

public class LocaleUtil {

    public static String getLocale(Player player) {
        try {
            return player.locale().getLanguage().toLowerCase();
        } catch (Exception e) {
            return getDefaultLocale();
        }
    }

    public static String getDefaultLocale() {
        return Config.getDefaultLanguage();
    }
}
