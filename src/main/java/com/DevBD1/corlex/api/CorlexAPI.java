package com.DevBD1.corlex.api;

import org.bukkit.entity.Player;
import java.util.Map;

public interface CorlexAPI {
    String translate(Player player, String key, Map<String, String> dynamic);
}
