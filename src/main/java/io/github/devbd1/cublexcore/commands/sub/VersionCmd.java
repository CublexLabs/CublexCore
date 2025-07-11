package io.github.devbd1.cublexcore.commands.sub;

import io.github.devbd1.cublexcore.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VersionCmd implements SubCommand {
    @Override
    public String name() {
        return "version";
    }

    @Override
    public String permission() {
        return "cublexcore.cmd.version";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String description() {
        return SubCommand.super.description();
    }

    @Override
    public String usage() {
        return SubCommand.super.usage();
    }
}
