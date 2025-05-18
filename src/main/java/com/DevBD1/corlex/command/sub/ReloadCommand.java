package com.DevBD1.corlex.command.sub;

import com.DevBD1.corlex.lang.Lang;
import com.DevBD1.corlex.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import com.DevBD1.corlex.command.SubCommand;
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
        Config.load(plugin);
        Lang.load(plugin);

        sender.sendMessage("§aCorlex reloaded.");
        Bukkit.getLogger().info("[Corlex] Reloaded by " + sender.getName());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
