package io.github.devbd1.cublexcore;

import io.github.devbd1.cublexcore.modules.ModuleRegistrar;
import io.github.devbd1.cublexcore.modules.text.Lang;
import io.github.devbd1.cublexcore.services.ServiceRegistrar;
import io.github.devbd1.cublexcore.services.ServiceUnregistrar;
import io.github.devbd1.cublexcore.utilities.*;
import io.github.devbd1.cublexcore.commands.CommandRegistrar;
import io.github.devbd1.cublexcore.hooks.HookRegistrar;
import io.github.devbd1.cublexcore.services.CorlexAPIImplementer;

public class PluginInitializer {
    private final Main plugin;
    private LoggingManager logger;
    private CorlexAPIImplementer api;

    public PluginInitializer(Main plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        // --- config & localization
        plugin.saveDefaultConfig();
        ConfigManager.init(plugin);
        Lang.load(plugin);
        ConfigManager.printStatus();

        // --- core services
        logger = new LoggingManager(plugin);
        api    = new CorlexAPIImplementer(plugin, logger);
        ServiceRegistrar.registerAll(plugin, api, logger);

        // --- commands
        CommandRegistrar.register(plugin, logger);

        // --- hooks (PlaceholderAPI, CubItems, RealisticSeasons…)
        HookRegistrar.register(plugin, logger);

        // ** ProtocolLib packet‐translation hook **
        ModuleRegistrar.register(plugin, api, logger);

        logger.log("CublexCore is fully enabled.");
    }

    public void disable() {
        ServiceUnregistrar.unregisterAll(plugin, api, logger);
        logger.log("CublexCore is disabled.");
    }
}
