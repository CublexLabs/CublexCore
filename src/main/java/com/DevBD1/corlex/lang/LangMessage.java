package com.DevBD1.corlex.lang;

import com.DevBD1.corlex.utils.PlaceholderUtil;
import com.DevBD1.corlex.utils.LocaleUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LangMessage {
    private final Player player;
    private final String locale;
    private final Map<String, String> dynamic = new HashMap<>();
    private String key;

    public LangMessage(Player player) {
        this.player = player;
        this.locale = LocaleUtil.getLocale(player);
        dynamic.put("player", player.getName());
        dynamic.put("world", player.getWorld().getName());
    }

    public LangMessage key(String key) {
        this.key = key;
        return this;
    }

    public LangMessage with(String placeholder, String value) {
        dynamic.put(placeholder, value);
        return this;
    }

    public LangMessage with(String placeholder, int value) {
        return with(placeholder, String.valueOf(value));
    }

    public LangMessage with(String placeholder, double value) {
        return with(placeholder, String.valueOf(value));
    }

    public void send() {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', build()));
    }

    public String build() {
        return Lang.t(key, locale, dynamic);
    }
}
