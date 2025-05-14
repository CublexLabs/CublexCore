package com.DevBD1.corlex.cmds;

import com.DevBD1.corlex.lang.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestLangCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        // Default values
        String coins = "250";
        String rank = "VIP";

        if (args.length >= 2) {
            coins = args[0];
            rank = args[1];
        }

        Lang.forPlayer(player)
                .key("corlex.welcome")
                .send();

        Lang.forPlayer(player)
                .key("corlex.status")
                .with("coins", coins)
                .with("rank", rank)
                .send();

        return true;
    }
}
