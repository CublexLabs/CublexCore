package io.github.devbd1.cublexcore.commands.sub;

import io.github.devbd1.cublexcore.commands.CommandConfig;
import io.github.devbd1.cublexcore.modules.text.Lang;
import io.github.devbd1.cublexcore.utilities.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import io.github.devbd1.cublexcore.commands.SubCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ReloadCmd implements SubCommand {
    @Override
    public String getName() {
        return "reload";
    }


    private final JavaPlugin plugin;

    public ReloadCmd(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> getPermissions()
    {
        return CommandConfig.getPermissions("reload");
    }

    @Override
    public List<String> getAliases() {
        return CommandConfig.getAliases("reload");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        ConfigManager.init(plugin);
        Lang.load(plugin);

        ConfigManager.printStatus();

        sender.sendMessage("§aCublexCore reloaded.");
        Bukkit.getLogger().info("[aCublexCore] Reloaded by " + sender.getName());
        return true;
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
