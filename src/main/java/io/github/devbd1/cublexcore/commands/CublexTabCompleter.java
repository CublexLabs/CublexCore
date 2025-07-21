package io.github.devbd1.cublexcore.commands;

import io.github.devbd1.cublexcore.modules.text.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class CublexTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "lang", "log", "gui");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("lang")) {
            return new ArrayList<>(Lang.getTranslations().keySet());
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
