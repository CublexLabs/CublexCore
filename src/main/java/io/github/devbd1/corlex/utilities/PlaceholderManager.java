package io.github.devbd1.corlex.utilities;

import org.bukkit.entity.Player;
import java.util.Map;

public class PlaceholderManager {

    private final LoggingManager logger;

    /**
     * Constructor accepts a LoggingManager instance for dependency injection.
     */
    public PlaceholderManager(LoggingManager logger) {
        this.logger = logger;
    }

    /**
     * Example method demonstrating usage of the injected logger.
     */
    public void doWork() {
        logger.log("AService iş yaptı");
    }

    /**
     * Applies placeholders to the input string and logs debug information if enabled.
     */
    public String applyPlaceholders(Player player,
                                    String input,
                                    Map<String, String> staticPlaceholders,
                                    Map<String, String> dynamicPlaceholders) {
        if (input == null) {
            return "";
        }

        // Debug logging
        logger.log("[DEBUG] PlaceholderManager input: " + input);
        logger.log("[DEBUG] Static map: " + staticPlaceholders);
        logger.log("[DEBUG] Dynamic map: " + dynamicPlaceholders);

        // Apply static placeholders
        for (Map.Entry<String, String> entry : staticPlaceholders.entrySet()) {
            input = input.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        // Apply dynamic placeholders
        for (Map.Entry<String, String> entry : dynamicPlaceholders.entrySet()) {
            input = input.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        // Final debug log
        if (ConfigManager.getKeyValue("debug-mode", Boolean.class, false)) {
            logger.log("[DEBUG] Final resolved message: " + input);
        }

        return input;
    }
}