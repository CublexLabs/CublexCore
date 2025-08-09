package io.github.devbd1.cublexcore.commands.sub;

import io.github.devbd1.cublexcore.commands.CommandConfig;
import io.github.devbd1.cublexcore.commands.SubCommand;

import io.github.devbd1.cublexcore.modules.logger.LoggingManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

public class VersionCmd implements SubCommand {
    String CMD_NAME = "version";
    private final JavaPlugin plugin;
    private final LoggingManager logger;

    public VersionCmd(JavaPlugin plugin, LoggingManager logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    public String getName() {
        return CMD_NAME;
    }

    @Override
    public List<String> getPermissions() {
        return CommandConfig.getPermissions(CMD_NAME);
    }

    @Override
    public List<String> getAliases() {
        return CommandConfig.getAliases(CMD_NAME);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        logger.info("§6[CublexCore] §f'/cublex version' command used by §e" + sender.getName());

        sender.sendMessage("§f§nChecking CublexCore plugin version, please wait...");
        String version = plugin.getPluginMeta().getVersion();
        String authors = plugin.getPluginMeta().getAuthors().getFirst();
        sender.sendMessage("§fThis server is running " + plugin.getName() + " §eversion §6" + version + " §eby §6" + authors + "§f." + " (Implementing CublexAPI version " + version + ")");
        sender.sendMessage("Download the new version at: https://www.spigotmc.org/resources/cublexcore/");
        return true;
    }

}
