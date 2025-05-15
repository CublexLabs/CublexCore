package com.DevBD1.corlex.cmds;

import com.DevBD1.corlex.lang.Lang;
import com.DevBD1.corlex.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CorlexCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;

    public CorlexCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("corlex.admin")) {
                if (sender instanceof Player player) {
                    player.sendMessage(Lang.t(player, "corlex.reload.no-permission"));
                } else {
                    sender.sendMessage("You do not have permission to use this command.");
                }
                return true;
            }

            plugin.reloadConfig();
            Config.load(plugin);
            Lang.load(plugin);

            if (sender instanceof Player player) {
                player.sendMessage(Lang.t(player, "corlex.reload.success"));
            }

            Bukkit.getLogger().info("[Corlex] Config and language files reloaded by " + sender.getName());
            return true;
        }

        sender.sendMessage("Usage: /corlex reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("reload");
        }
        return Collections.emptyList();
    }
}
