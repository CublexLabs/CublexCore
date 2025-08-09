package io.github.devbd1.cublexcore.commands.sub;

import io.github.devbd1.cublexcore.commands.SubCommand;
import io.github.devbd1.cublexcore.commands.CommandConfig;
import io.github.devbd1.cublexcore.commands.CommandManager;

import io.github.devbd1.cublexcore.modules.logger.LoggingManager;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Collection;

public class HelpCmd implements SubCommand {
    String CMD_NAME = "help";
    private final LoggingManager logger;
    private final CommandManager manager;

    public HelpCmd(CommandManager manager, LoggingManager logger) {
        this.manager = manager;
        this.logger = logger;
    }

    @Override
    public String getName() {
        return CMD_NAME;
    }

    @Override
    public List<String> getPermissions()
    {
        return CommandConfig.getPermissions(CMD_NAME);
    }

    @Override
    public List<String> getAliases() {
        return CommandConfig.getAliases(CMD_NAME);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        logger.info("§6[CublexCore] §f'/cublex help' command used by §e" + sender.getName());

        sender.sendMessage("§8§m------§r §aCublexCore Help §8§m------");
        if (manager == null) return true;
    
    Collection<SubCommand> commands = manager.getCommands();
    for (SubCommand cmd : commands) {
        List<String> permissions = cmd.getPermissions();
        boolean hasPermission = permissions.isEmpty() || 
        permissions.stream().anyMatch(sender::hasPermission);
        
        if (!hasPermission) continue;
        
        sender.sendMessage("§e" + cmd.getUsage() + " §7- " + cmd.getDescription());
    }
    return true;
}

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String getDescription() {
        return "Shows this help menu.";
    }
}