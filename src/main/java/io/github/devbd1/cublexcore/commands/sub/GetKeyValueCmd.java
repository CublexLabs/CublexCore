package io.github.devbd1.cublexcore.commands.sub;

import io.github.devbd1.cublexcore.commands.CommandConfig;
import io.github.devbd1.cublexcore.commands.SubCommand;
import io.github.devbd1.cublexcore.modules.text.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class GetKeyValueCmd implements SubCommand{
    @Override
    public String name() {
        return "lang";
    }

    @Override
    public List<String> permissions()
    {
        return CommandConfig.getPermissions("lang");
    }

    @Override
    public List<String> aliases() {
        return CommandConfig.getAliases("lang");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§7Usage: /cublex lang [lang] <key> [key=value]...");
            return true;
        }

        String locale;
        String key;
        int placeholderStartIndex;

        if (Lang.getTranslations().containsKey(args[0].toLowerCase())) {
            // /cublex lang en cublex.status ...
            locale = args[0].toLowerCase();
            if (args.length < 2) {
                sender.sendMessage("§7Usage: /cublex lang <lang> <key> [key=value]...");
                return true;
            }
            key = args[1];
            placeholderStartIndex = 2;
        } else {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cConsole must specify a language.");
                return true;
            }
            locale = player.locale().getLanguage().toLowerCase();
            key = args[0];
            placeholderStartIndex = 1;
        }

        if (!Lang.getTranslations().containsKey(locale)) {
            sender.sendMessage("§cInvalid language: " + locale);
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

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(Lang.getTranslations().keySet());
        }
        if (args.length == 2) {
            String lang = args[0].toLowerCase();
            if (!Lang.getTranslations().containsKey(lang)) return List.of();
            return getAllTranslationKeysForTab(lang);
        }
        return List.of();
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
