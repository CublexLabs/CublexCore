package com.DevBD1.corlex.services;

import com.DevBD1.corlex.modules.lang.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static com.DevBD1.corlex.modules.lang.Lang.getNestedValue;

public class CorlexAPIImpl implements CorlexAPI {

    @Override
    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        if (sender instanceof Player p) {
            sender.sendMessage(Lang.t(p, key, placeholders));
        } else {
            sender.sendMessage(Lang.t((Player) null, key, placeholders));
        }
    }

    @Override
    public String get(Player player, String key, Map<String, String> placeholders) {
        return Lang.t(player, key, placeholders);
    }

    @Override
    public String getLocale(Player player) {
        return player.locale().getLanguage().toLowerCase();
    }

    @Override
    public boolean exists(String locale, String key) {
        Map<String, Object> lang = Lang.getTranslations().get(locale);
        return getNestedValue(lang, key) != null;
        //return Lang.getNestedValue(Lang.getTranslations().get(locale), key) != null;
    }


    @Override
    public void logMissingKey(String key, String requestedLocale) {
        // Optional: log to file or warn admin
    }

    @Override
    public void reload() {
        // Reload lang/config here
    }

    @Override
    public boolean isLoggingEnabled() {
        return true; // or pull from Config
    }
}
