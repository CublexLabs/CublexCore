package io.github.devbd1.cublexcore.commands.sub;

import io.github.devbd1.cublexcore.modules.text.Lang;
import io.github.devbd1.cublexcore.utilities.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import io.github.devbd1.cublexcore.commands.SubCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ReloadCmd implements SubCommand {

    private final JavaPlugin plugin;

    public ReloadCmd(JavaPlugin plugin) {
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
        ConfigManager.init(plugin);
        Lang.load(plugin);

        ConfigManager.printStatus();

        sender.sendMessage("§aCorlex reloaded.");
        Bukkit.getLogger().info("[Corlex] Reloaded by " + sender.getName());
        return true;
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
