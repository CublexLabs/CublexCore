package io.github.devbd1.cublexcore.commands.sub;

import io.github.devbd1.cublexcore.commands.CommandConfig;
import io.github.devbd1.cublexcore.commands.CommandManager;
import io.github.devbd1.cublexcore.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Collection;

public class HelpCmd implements SubCommand {
    @Override
    public String name() {
        return "help";
    }

    private final CommandManager manager;

    public HelpCmd(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public List<String> permissions()
    {
        return CommandConfig.getPermissions("help");
    }

    @Override
    public List<String> aliases() {
        return CommandConfig.getAliases("help");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("§8§m------§r §aCublexCore Help §8§m------");
        if (manager == null) return true;
    
    Collection<SubCommand> commands = manager.getCommands();
    for (SubCommand cmd : commands) {
        List<String> permissions = cmd.permissions();
        boolean hasPermission = permissions.isEmpty() || 
        permissions.stream().anyMatch(sender::hasPermission);
        
        if (!hasPermission) continue;
        
        sender.sendMessage("§e" + cmd.usage() + " §7- " + cmd.description());
    }
    return true;
}

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String description() {
        return "Shows this help menu.";
    }
}