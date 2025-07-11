package io.github.devbd1.cublexcore.commands;

import io.github.devbd1.cublexcore.Main;
import io.github.devbd1.cublexcore.commands.sub.HelpCmd;
import io.github.devbd1.cublexcore.commands.sub.ReloadCmd;
import io.github.devbd1.cublexcore.commands.sub.GetKeyValueCmd;
import io.github.devbd1.cublexcore.utilities.LoggingManager;
import org.bukkit.command.PluginCommand;

public final class CommandRegistrar {
    private CommandRegistrar() {}

    public static void register(Main plugin, LoggingManager logger) {
        PluginCommand cmd = plugin.getCommand("cublex");
        if (cmd == null) {
            logger.log("COMMAND '/cublex' NOT FOUND! Aborting command registration.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        CommandManager mgr = new CommandManager();
        mgr.register(new ReloadCmd(plugin));
        mgr.register(new GetKeyValueCmd());
        mgr.register(new HelpCmd(mgr));

        cmd.setExecutor(mgr);
        cmd.setTabCompleter(mgr);
        logger.log("Commands registered.");
    }
}
