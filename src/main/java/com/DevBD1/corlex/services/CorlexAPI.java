package com.DevBD1.corlex.services;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public interface CorlexAPI {

    /**
     * Send a translated message to a command sender using their locale.
     */
    void send(CommandSender sender, String key, Map<String, String> placeholders);

    /**
     * Get a translated message as a string (does not send).
     */
    String get(Player player, String key, Map<String, String> placeholders);

    /**
     * Get the detected locale of the player (e.g., "en", "tr").
     */
    String getLocale(Player player);

    /**
     * Check if a specific locale and key exists in the language files.
     */
    boolean exists(String locale, String key);

    /**
     * Log a missing translation key (for diagnostics).
     */
    void logMissingKey(String key, String requestedLocale);

    /**
     * Reload Corlex configuration and language files.
     */
    void reload();

    /**
     * Whether Corlex logging is enabled (from config).
     */
    boolean isLoggingEnabled();

    /**
     * (Planned) Show a simple GUI to the player using the shared GUI API.
     */
    default void openTestGui(Player player) {
        throw new UnsupportedOperationException("GUI module is not implemented.");
    }

    /**
     * Returns the current version of Corlex (optional).
     */
    default String getVersion() {
        return "unknown";
    }
}
