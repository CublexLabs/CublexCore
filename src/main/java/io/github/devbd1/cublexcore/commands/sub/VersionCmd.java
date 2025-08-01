package io.github.devbd1.cublexcore.commands.sub;

import io.github.devbd1.cublexcore.commands.CommandConfig;
import io.github.devbd1.cublexcore.commands.SubCommand;
import org.bukkit.command.CommandSender;

import org.bukkit.plugin.Plugin;
import io.github.devbd1.cublexcore.Main;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.List;

public class VersionCmd implements SubCommand {
    private final JavaPlugin plugin;

    public VersionCmd(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public List<String> getPermissions()
    {
        return CommandConfig.getPermissions("version");
    }

    @Override
    public List<String> getAliases() {
        return CommandConfig.getAliases("version");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("§f§nChecking CublexCore plugin version, please wait...");
        String version = plugin.getPluginMeta().getVersion();
        String authors = plugin.getPluginMeta().getAuthors().getFirst();
        sender.sendMessage("§fThis server is running " + plugin.getName() + " §eversion §6" + version + " §eby §6" + authors + "§f." + " (Implementing CublexAPI version " + version + ")");
        sender.sendMessage("Download the new version at: https://www.spigotmc.org/resources/cublexcore/");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

}
