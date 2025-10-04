package io.github.devbd1.cublexcore.hooks;

import io.github.devbd1.cublexcore.Main;
import io.github.devbd1.cublexcore.modules.logger.LoggingManager;
import io.github.devbd1.cublexcore.hooks.PlaceholderAPI.PlaceholderApplier;
//import io.github.devbd1.corlex.hooks.CubItems.ClientSideTextAdapter;
//import io.github.devbd1.cublexcore.hooks.RealisticSeasons.Listener;
import org.bukkit.Bukkit;

public final class HookRegistrar {
    private HookRegistrar() {}

    public static void register(Main plugin, LoggingManager logger) {
        // PlaceholderAPI
        PlaceholderApplier.init(logger);

        // CubItems text adapter
//        new ClientSideTextAdapter(plugin).register(plugin);
//        logger.log("CubItems text adapter hooked.");

        // RealisticSeasons
        if (Bukkit.getPluginManager().isPluginEnabled("RealisticSeasons")) {
//            Bukkit.getPluginManager().registerEvents(new Listener(plugin), plugin);
            logger.log("RealisticSeasons listener registered.");
        } else {
            logger.log("RealisticSeasons not found. Skipped integration.");
        }
    }
}
