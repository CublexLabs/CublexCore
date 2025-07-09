package io.github.devbd1.corlex;

import io.github.devbd1.corlex.commands.CommandManager;
import io.github.devbd1.corlex.commands.sub.HelpSubCommand;
import io.github.devbd1.corlex.commands.sub.ReloadCommand;
import io.github.devbd1.corlex.commands.sub.TestLocalization;
import io.github.devbd1.corlex.hooks.RealisticSeasons.Listener;
import io.github.devbd1.corlex.hooks.CubItems.ClientSideTextAdapter;
import io.github.devbd1.corlex.modules.text.Lang;
import io.github.devbd1.corlex.modules.lore.ClientSideLoreAdapter;
import io.github.devbd1.corlex.utilities.ConfigManager;
import io.github.devbd1.corlex.utilities.LoggingManager;
import io.github.devbd1.corlex.services.CorlexAPI;
import io.github.devbd1.corlex.services.CorlexAPIImpl;
import io.github.devbd1.corlex.services.ClientSideLoreService;

import io.github.devbd1.corlex.utilities.ServiceRegistrar;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private CorlexAPI api;
    private LoggingManager loggingManager;

    @Override
    public void onEnable() {
        this.api            = new CorlexAPIImpl();
        this.loggingManager = new LoggingManager(this);
        ClientSideLoreService loreService = new ClientSideLoreAdapter(this);
        ServiceRegistrar.registerAll(this, api, loreService, loggingManager);

        saveDefaultConfig();
        saveResource("lang/en.yml", false);
        saveResource("lang/tr.yml", false);

        ConfigManager.load(this);
        Lang.load(this);

        this.loggingManager = new LoggingManager(this);
        loggingManager.log("Plugin başlatıldı");

        ConfigManager.printStatusToConsole();

        PluginCommand cmd = getCommand("corlex");
        if (cmd == null) {
            getLogger().severe("COMMAND '/corlex' NOT FOUND! Is it defined in plugin.yml?");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        CommandManager manager = new CommandManager();

        manager.register(new ReloadCommand(this));
        manager.register(new TestLocalization());
        manager.register(new HelpSubCommand(manager));

        cmd.setExecutor(manager);
        cmd.setTabCompleter(manager);

        getLogger().info("Corlex loaded with localization support.");
        Lang.testNestedValue();
        // Native Translation Module
        new ClientSideTextAdapter(this).register(this);

        // RealisticSeasons Integration
        if (Bukkit.getPluginManager().getPlugin("RealisticSeasons") == null) {
            getLogger().warning("RealisticSeasons not found. Disabling season integration.");
            return;
        }

        getServer().getPluginManager().registerEvents(new Listener(this), this);

    }

    @Override
    public void onDisable() {
        Bukkit.getServicesManager().unregister(CorlexAPI.class);
    }

    public LoggingManager getLoggingManager() {
        return this.loggingManager;
    }
}
