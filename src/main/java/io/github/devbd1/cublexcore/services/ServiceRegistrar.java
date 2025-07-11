package io.github.devbd1.cublexcore.services;

import io.github.devbd1.cublexcore.utilities.LoggingManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServiceRegistrar {
    private ServiceRegistrar() { /* no-op */ }
    public static void registerAll(JavaPlugin plugin,
                                   CorlexAPI api,
                                   LoggingManager loggingManager) {
        ServicesManager sm = plugin.getServer().getServicesManager();

        sm.register(LoggingManager.class, loggingManager, plugin, ServicePriority.Normal);
        plugin.getLogger().info("LoggingManager registered.");

        sm.register(CorlexAPI.class, api, plugin, ServicePriority.Normal);
        plugin.getLogger().info("CorlexAPI registered.");
    }
}
