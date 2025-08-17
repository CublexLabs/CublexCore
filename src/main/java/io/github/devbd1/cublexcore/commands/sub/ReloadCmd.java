package io.github.devbd1.cublexcore.commands.sub;

import io.github.devbd1.cublexcore.commands.SubCommand;
import io.github.devbd1.cublexcore.commands.CommandConfig;
import io.github.devbd1.cublexcore.modules.logger.LoggingManager;
import io.github.devbd1.cublexcore.modules.text.Lang;
import io.github.devbd1.cublexcore.utilities.ConfigManager;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

public class ReloadCmd implements SubCommand {
    String CMD_NAME = "reload";
    private final JavaPlugin plugin;
    private final LoggingManager logger;

    public ReloadCmd(JavaPlugin plugin, LoggingManager logger) {
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
        logger.info("§6[CublexCore] §f'/cublex reload' command used by §e" + sender.getName());

        plugin.reloadConfig();
        ConfigManager.init(plugin);
        Lang.load(plugin);

        ConfigManager.printStatus();

        sender.sendMessage("§6CublexCore §freloaded.");
        // Lang.send(sender, "command.reload.success");
        // Bukkit.getLogger().info("§a[CublexCore] Reloaded by " + sender.getName());
        logger.info("§6[CublexCore] §fReloaded by §e" + sender.getName());
        return true;
    }

}
