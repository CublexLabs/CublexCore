package com.DevBD1.corlex.cmds;

import com.DevBD1.corlex.lang.Lang;
import com.DevBD1.corlex.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CorlexCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;

    public CorlexCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /corlex <reload|lang>");
            return true;
        }

        String sub = args[0].toLowerCase();

        // /corlex reload
        if (sub.equals("reload")) {
            if (!sender.hasPermission("corlex.admin")) {
                send(sender, "corlex.reload.no-permission");
                return true;
            }

            plugin.reloadConfig();
            Config.load(plugin);
            Lang.load(plugin);

            send(sender, "corlex.reload.success");
            Bukkit.getLogger().info("[Corlex] Reloaded config and language files by " + sender.getName());
            return true;
        }

        // /corlex lang <key> [key=value ...]
        if (sub.equals("lang")) {
            if (args.length < 2) {
                sender.sendMessage("Usage: /corlex lang [lang] <key> [key=value]...");
                return true;
            }

            String locale;
            String key;
            int placeholderStartIndex;

            if (Lang.getTranslations().containsKey(args[1].toLowerCase())) {
                // /corlex lang en corlex.status ...
                locale = args[1].toLowerCase();
                if (args.length < 3) {
                    sender.sendMessage("Usage: /corlex lang <lang> <key> [key=value]...");
                    return true;
                }
                key = args[2];
                placeholderStartIndex = 3;
            } else {
                // /corlex lang corlex.status ...
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Usage: /corlex lang <lang> <key> [key=value]...");
                    return true;
                }
                locale = player.locale().getLanguage().toLowerCase();
                key = args[1];
                placeholderStartIndex = 2;
            }

            // Validate language
            if (!Lang.getTranslations().containsKey(locale)) {
                sender.sendMessage("Invalid language: " + locale);
                return true;
            }

            Map<String, String> dynamic = new HashMap<>();
            for (int i = placeholderStartIndex; i < args.length; i++) {
                String[] split = args[i].split("=", 2);
                if (split.length == 2) {
                    dynamic.put(split[0], split[1]);
                }
            }

            String result = Lang.t(key, locale, dynamic);
            sender.sendMessage(result);
            return true;
        }



        sender.sendMessage("Unknown subcommand. Use /corlex reload or /corlex lang");
        return true;
    }

    private void send(CommandSender sender, String key) {
        if (sender instanceof Player p) {
            sender.sendMessage(Lang.t(p, key));
        } else {
            sender.sendMessage(Lang.t(null, key));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "lang");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("lang")) {
            return Arrays.asList("en", "tr");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("lang")) {
            return getAllTranslationKeysForTab(args[1]);
        }

        return Collections.emptyList();
    }

    private List<String> getAllTranslationKeysForTab(String locale) {
        Map<String, Object> lang = Lang.getTranslations().getOrDefault(locale, Map.of());
        List<String> keys = new ArrayList<>();
        collectKeys("", lang, keys);
        return keys;
    }

    @SuppressWarnings("unchecked")
    private void collectKeys(String path, Map<String, Object> section, List<String> out) {
        for (Map.Entry<String, Object> entry : section.entrySet()) {
            String fullKey = path.isEmpty() ? entry.getKey() : path + "." + entry.getKey();
            if (entry.getValue() instanceof Map<?, ?> nested) {
                collectKeys(fullKey, (Map<String, Object>) nested, out);
            } else {
                out.add(fullKey);
            }
        }
    }

}
