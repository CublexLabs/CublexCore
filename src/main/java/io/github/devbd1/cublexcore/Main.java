package io.github.devbd1.cublexcore;

import io.github.devbd1.cublexcore.commands.CommandRegistrar;
import io.github.devbd1.cublexcore.hooks.HookRegistrar;
import io.github.devbd1.cublexcore.modules.ModuleRegistrar;
import io.github.devbd1.cublexcore.services.ServiceRegistrar;
import io.github.devbd1.cublexcore.services.ServiceUnregistrar;

import io.github.devbd1.cublexcore.modules.logger.LoggingManager;
import io.github.devbd1.cublexcore.modules.text.Lang;
import io.github.devbd1.cublexcore.services.CorlexAPIImplementer;
import io.github.devbd1.cublexcore.utilities.*;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private LoggingManager logger;
    private CorlexAPIImplementer api;

    @Override
    public void onEnable() {
        // --- config & localization
        saveDefaultConfig();
        ConfigManager.init(this);
        Lang.load(this);
        ConfigManager.printStatus();

        // --- core services
        logger = new LoggingManager(this);
        api    = new CorlexAPIImplementer(this, logger);
        ServiceRegistrar.registerAll(this, api, logger);

        // --- commands
        CommandRegistrar.register(this, logger);

        // --- hooks (PlaceholderAPI, CubItems, RealisticSeasons…)
        HookRegistrar.register(this, logger);

        // ** ProtocolLib packet‐translation hook **
        ModuleRegistrar.register(this, api, logger);

        logger.log("CublexCore is fully enabled.");
    }

    @Override
    public void onDisable() {
        ServiceUnregistrar.unregisterAll(this, api, logger);
        logger.log("CublexCore is disabled.");
    }

    public LoggingManager getLoggingManager() {
        return logger;
    }
}