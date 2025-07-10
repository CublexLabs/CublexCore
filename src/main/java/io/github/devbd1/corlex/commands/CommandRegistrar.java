package io.github.devbd1.corlex.commands;

import io.github.devbd1.corlex.Main;
import io.github.devbd1.corlex.commands.sub.HelpSubCommand;
import io.github.devbd1.corlex.commands.sub.ReloadCommand;
import io.github.devbd1.corlex.commands.sub.TestLocalization;
import io.github.devbd1.corlex.utilities.LoggingManager;
import org.bukkit.command.PluginCommand;

public final class CommandRegistrar {
    private CommandRegistrar() {}

    public static void register(Main plugin, LoggingManager logger) {
        PluginCommand cmd = plugin.getCommand("corlex");
        if (cmd == null) {
            logger.log("COMMAND '/corlex' NOT FOUND! Aborting command registration.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        CommandManager mgr = new CommandManager();
        mgr.register(new ReloadCommand(plugin));
        mgr.register(new TestLocalization());
        mgr.register(new HelpSubCommand(mgr));

        cmd.setExecutor(mgr);
        cmd.setTabCompleter(mgr);
        logger.log("Commands registered.");
    }
}
