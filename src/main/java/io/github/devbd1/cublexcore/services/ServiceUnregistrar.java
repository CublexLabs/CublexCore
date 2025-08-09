/**
 * Utility to unregister all Corlex services in reverse order.
 * Call this in onDisable() to cleanly remove your plugin’s service registrations.
 */
package io.github.devbd1.cublexcore.services;

import io.github.devbd1.cublexcore.modules.logger.LoggingManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServiceUnregistrar {
    private ServiceUnregistrar() { /* prevent instantiation */ }

    /**
     * @param plugin          your main JavaPlugin instance
     * @param api             the CorlexAPI implementation you registered
     * @param loggingManager  the LoggingManager you registered
     */
    public static void unregisterAll(JavaPlugin plugin,
                                     CorlexAPI api,
                                     LoggingManager loggingManager) {
        ServicesManager sm = plugin.getServer().getServicesManager();

        // 1) Unregister the core API
        sm.unregister(CorlexAPI.class, api);
        plugin.getLogger().info("CorlexAPI unregistered.");

        // 2) Unregister the logging facility
        sm.unregister(LoggingManager.class, loggingManager);
        plugin.getLogger().info("LoggingManager unregistered.");
    }
}
