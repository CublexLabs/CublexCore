package io.github.devbd1.corlex.utilities;

import io.github.devbd1.corlex.services.ClientSideLoreService;
import io.github.devbd1.corlex.services.CorlexAPI;
import io.github.devbd1.corlex.services.CorlexAPIProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ServiceRegistrar {
    public static void registerAll(JavaPlugin plugin,
                                   CorlexAPI api,
                                   ClientSideLoreService loreService,
                                   LoggingManager loggingManager) {
        ServicesManager sm = plugin.getServer().getServicesManager();

        sm.register(CorlexAPI.class, api, plugin, ServicePriority.Normal);
        CorlexAPIProvider.register(api);
        plugin.getLogger().info("Corlex API registered.");

        sm.register(ClientSideLoreService.class, loreService, plugin, ServicePriority.Normal);
        plugin.getLogger().info("ClientSideLoreService registered.");

        sm.register(LoggingManager.class, loggingManager, plugin, ServicePriority.Normal);
        plugin.getLogger().info("LoggingManager registered.");
    }
}
