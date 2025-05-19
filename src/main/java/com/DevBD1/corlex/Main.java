package com.DevBD1.corlex;

import com.DevBD1.corlex.api.CorlexAPI;
import com.DevBD1.corlex.api.CorlexAPIImpl;
import com.DevBD1.corlex.api.CorlexAPIProvider;
import com.DevBD1.corlex.command.CommandManager;
import com.DevBD1.corlex.command.sub.HelpSubCommand;
import com.DevBD1.corlex.command.sub.ReloadCommand;
import com.DevBD1.corlex.command.sub.TestLocalization;
import com.DevBD1.corlex.lang.Lang;
import com.DevBD1.corlex.util.Config;
import com.DevBD1.corlex.util.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private CorlexAPI api;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("lang/en.yml", false);
        saveResource("lang/tr.yml", false);

        Config.load(this);
        Lang.load(this);
        Logger.init(this);

        Config.printStatusToConsole();

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

        // Register API instance
        this.api = new CorlexAPIImpl();
        Bukkit.getServicesManager().register(CorlexAPI.class, api, this, ServicePriority.Normal);
        CorlexAPIProvider.register(api);

        getLogger().info("Corlex API registered.");
    }

    @Override
    public void onDisable() {
        Bukkit.getServicesManager().unregister(CorlexAPI.class);
    }
}
