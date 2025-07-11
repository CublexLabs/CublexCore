package io.github.devbd1.cublexcore.commands;

import org.bukkit.command.*;
import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> commands = new HashMap<>();

    public void register(SubCommand cmd) {
        commands.put(cmd.name().toLowerCase(), cmd);
    }

    public Collection<SubCommand> getRegistered() {
        return commands.values();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7Use /cublex <subcommand>");
            return true;
        }

        SubCommand sub = commands.get(args[0].toLowerCase());
        if (sub == null) {
            sender.sendMessage("§cUnknown subcommand: " + args[0]);
            return true;
        }

        if (!sub.permission().isEmpty() && !sender.hasPermission(sub.permission())) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        return sub.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(commands.keySet());
        }

        SubCommand sub = commands.get(args[0].toLowerCase());
        if (sub == null) return Collections.emptyList();
        return sub.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
    }
}
