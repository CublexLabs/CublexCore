package io.github.devbd1.cublexcore.commands;

import org.bukkit.command.CommandSender;
import java.util.List;

public interface SubCommand {
    String name();
    List<String> permissions();  // Changed from single permission to list
    boolean execute(CommandSender sender, String[] args);
    List<String> tabComplete(CommandSender sender, String[] args);
    List<String> aliases();  // New method for aliases

    default String description() {
        return "No description provided.";
    }

    default String usage() {
        return "/" + name();
    }
}