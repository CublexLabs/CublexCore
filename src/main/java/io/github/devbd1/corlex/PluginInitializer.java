package io.github.devbd1.corlex;

import io.github.devbd1.corlex.modules.text.Lang;
import io.github.devbd1.corlex.modules.text.PacketTranslator;
import io.github.devbd1.corlex.utilities.*;
import io.github.devbd1.corlex.commands.CommandRegistrar;
import io.github.devbd1.corlex.hooks.HookRegistrar;
import io.github.devbd1.corlex.services.CorlexAPIImpl;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginInitializer {
    private final Main plugin;
    private LoggingManager logger;
    private CorlexAPIImpl   api;

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
        api    = new CorlexAPIImpl(plugin, logger);
        ServiceRegistrar.registerAll(plugin, api, logger);

        // --- commands
        CommandRegistrar.register(plugin, logger);

        // --- hooks (PlaceholderAPI, CubItems, RealisticSeasons…)
        HookRegistrar.register(plugin, logger);

        // ** ProtocolLib packet‐translation hook **
        PacketTranslator.init(plugin, api, logger);

        logger.log("Corlex fully enabled.");
    }

    public void disable() {
        ServiceUnregistrar.unregisterAll(plugin, api, logger);
        logger.log("Corlex disabled.");
    }
}
