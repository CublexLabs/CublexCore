package io.github.devbd1.cublexcore.utilities;

import io.github.devbd1.cublexcore.hooks.PlaceholderAPI.PlaceholderApplier;
import io.github.devbd1.cublexcore.modules.logger.LoggingManager;
import org.bukkit.entity.Player;
import java.util.Map;

/**
 * Handles replacing placeholders in messages and logs each step when debug is on.
 */
public class PlaceholderManager {
    private final LoggingManager logger;

    /**
     * @param logger the LoggingManager to use for all log output
     */
    public PlaceholderManager(LoggingManager logger) {
        this.logger = logger;
    }

    /**
     * Replaces placeholders in the given input string using the provided maps.
     * All log calls honor the LoggingManager’s debug-mode setting.
     *
     * @param player              the player context (unused currently)
     * @param input               the template string containing placeholders like {key}
     * @param staticPlaceholders  key→value map for fixed replacements
     * @param dynamicPlaceholders key→value map for runtime replacements
     * @return the fully resolved message (or empty string if input was null/empty)
     */
    public String applyPlaceholders(Player player,
                                    String input,
                                    Map<String, String> staticPlaceholders,
                                    Map<String, String> dynamicPlaceholders) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        logger.log("Starting placeholder replacement: " + input);

        String result = input;
        // Static placeholders
        for (var e : staticPlaceholders.entrySet()) {
            result = result.replace("{" + e.getKey() + "}", e.getValue());
        }
        // Dynamic placeholders
        for (var e : dynamicPlaceholders.entrySet()) {
            result = result.replace("{" + e.getKey() + "}", e.getValue());
        }
        result = PlaceholderApplier.apply(player, result);
        logger.log("Finished replacement, result: " + result);
        return result;
    }

    /**
     * Overload without Player: you don’t have to pass a Player.
     */
    public String applyPlaceholders(String input,
                                    Map<String, String> staticPlaceholders,
                                    Map<String, String> dynamicPlaceholders) {
        return applyPlaceholders(null, input, staticPlaceholders, dynamicPlaceholders);
    }
}
