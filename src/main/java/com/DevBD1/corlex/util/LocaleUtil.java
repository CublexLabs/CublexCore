package com.DevBD1.corlex.util;

import org.bukkit.entity.Player;

public class LocaleUtil {
    public static String getLocale(Player player) {
        try {
            return player.locale().getLanguage().toLowerCase(); // "en", "tr", etc.
        } catch (Exception e) {
            return "en";
        }
    }
}
