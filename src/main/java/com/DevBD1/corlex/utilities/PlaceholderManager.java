package com.DevBD1.corlex.utilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Map;

public class PlaceholderManager {

    public String applyPlaceholders(Player player, String input, Map<String, String> staticPlaceholders, Map<String, String> dynamicPlaceholders) {
        if (input == null) return "";

        RegisteredServiceProvider<LoggingManager> reg = Bukkit.getServicesManager().getRegistration(LoggingManager.class);
        if (reg != null) {
            LoggingManager lm = reg.getProvider();
            lm.log("[DEBUG] PlaceholderManager input: " + input);
            lm.log("[DEBUG] Static map: " + staticPlaceholders);
            lm.log("[DEBUG] Dynamic map: " + dynamicPlaceholders);
        }

        // Apply static placeholders like {prefix}
        for (Map.Entry<String, String> entry : staticPlaceholders.entrySet()) {
            input = input.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        // Apply dynamic placeholders like {coins}, {rank}, {player}
        for (Map.Entry<String, String> entry : dynamicPlaceholders.entrySet()) {
            input = input.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        if (ConfigManager.getKeyValue("debug-mode", Boolean.class, false) == true) {
            if (reg != null) {
                LoggingManager lm = reg.getProvider();
            lm.log("[DEBUG] Final resolved message: " + input);
            return input;
        }
    }
}
