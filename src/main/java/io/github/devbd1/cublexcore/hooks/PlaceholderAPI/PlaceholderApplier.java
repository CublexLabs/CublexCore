/**
 * Applies PlaceholderAPI placeholders to given input strings.
 */
package io.github.devbd1.cublexcore.hooks.PlaceholderAPI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.PlaceholderAPI;
import io.github.devbd1.cublexcore.utilities.LoggingManager;

public class PlaceholderApplier {
    private static boolean enabled = false;
    private PlaceholderApplier() {}

    public static void init(LoggingManager logger) {
        enabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        if (enabled) {
            logger.log("PlaceholderAPI integration enabled.");
        } else {
            logger.log("PlaceholderAPI not found. Integration disabled.");
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static String apply(Player player, String input) {
        if (!enabled || player == null || input == null || input.isEmpty()) {
            return input;
        }
        return PlaceholderAPI.setPlaceholders(player, input);
    }
}
