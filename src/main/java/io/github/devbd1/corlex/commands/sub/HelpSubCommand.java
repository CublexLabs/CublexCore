package io.github.devbd1.corlex.commands.sub;

import io.github.devbd1.corlex.commands.CommandManager;
import io.github.devbd1.corlex.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpSubCommand implements SubCommand {

    private final CommandManager manager;

    public HelpSubCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String permission() {
        return ""; // open to all
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("§8§m------§r §aCorlex Help §8§m------");
        for (SubCommand cmd : manager.getRegistered()) {
            if (!cmd.permission().isEmpty() && !sender.hasPermission(cmd.permission())) continue;
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
