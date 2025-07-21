package io.github.devbd1.cublexcore.commands;

import org.bukkit.command.*;
import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final Map<String, SubCommand> commands = new HashMap<>();
    private final Map<String, String> aliasToCommand = new HashMap<>();

    public void register(SubCommand cmd) {
        String name = cmd.name().toLowerCase();
        commands.put(name, cmd);

        // Register aliases from config
        for (String alias : CommandConfig.getAliases(name)) {
            aliasToCommand.put(alias.toLowerCase(), name);
        }
    }

    public Collection<SubCommand> getCommands() {
        return commands.values();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7Use /cublex <subcommand>");
            return true;
        }

        String subCmdName = args[0].toLowerCase();
        // Check if it's an alias
        if (aliasToCommand.containsKey(subCmdName)) {
            subCmdName = aliasToCommand.get(subCmdName);
        }

        SubCommand sub = commands.get(subCmdName);
        if (sub == null) {
            sender.sendMessage("§cUnknown subcommand: " + args[0]);
            return true;
        }

        // Check if player has ANY of the required permissions
        boolean hasPermission = false;
        List<String> permissions = sub.permissions();

        if (permissions.isEmpty()) {
            hasPermission = true;  // No permissions required
        } else {
            for (String permission : permissions) {
                if (sender.hasPermission(permission)) {
                    hasPermission = true;
                    break;
                }
            }
        }

        if (!hasPermission) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        return sub.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            // Add main commands and their aliases if player has permission
            for (SubCommand subCmd : commands.values()) {
                if (hasAnyPermission(sender, subCmd.permissions())) {
                    suggestions.add(subCmd.name());
                    suggestions.addAll(CommandConfig.getAliases(subCmd.name()));
                }
            }
            return suggestions;
        }

        String subCmdName = args[0].toLowerCase();
        if (aliasToCommand.containsKey(subCmdName)) {
            subCmdName = aliasToCommand.get(subCmdName);
        }

        SubCommand sub = commands.get(subCmdName);
        if (sub == null || !hasAnyPermission(sender, sub.permissions())) {
            return Collections.emptyList();
        }

        return sub.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    private boolean hasAnyPermission(CommandSender sender, List<String> permissions) {
        if (permissions.isEmpty()) return true;
        return permissions.stream().anyMatch(sender::hasPermission);
    }
}