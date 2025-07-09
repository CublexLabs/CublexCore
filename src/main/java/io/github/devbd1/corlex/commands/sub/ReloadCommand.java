package io.github.devbd1.corlex.commands.sub;

import io.github.devbd1.corlex.modules.text.Lang;
import io.github.devbd1.corlex.utilities.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import io.github.devbd1.corlex.commands.SubCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ReloadCommand implements SubCommand {

    private final JavaPlugin plugin;

    public ReloadCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String permission() {
        return "corlex.admin";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        ConfigManager.load(plugin);
        Lang.load(plugin);

        ConfigManager.printStatusToConsole();

        sender.sendMessage("§aCorlex reloaded.");
        Bukkit.getLogger().info("[Corlex] Reloaded by " + sender.getName());
        return true;
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
