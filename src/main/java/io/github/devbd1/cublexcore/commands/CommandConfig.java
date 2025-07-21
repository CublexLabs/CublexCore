package io.github.devbd1.cublexcore.commands;

import io.github.devbd1.cublexcore.utilities.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import java.util.List;
import java.util.Collections;

public class CommandConfig {
    private static ConfigurationSection getCommandSection(String command) {
        return ConfigManager.getConfig().getConfigurationSection("commands." + command);
    }

    public static List<String> getAliases(String command) {
        ConfigurationSection section = getCommandSection(command);
        if (section == null) return Collections.emptyList();
        return section.getStringList("aliases");
    }

    public static List<String> getPermissions(String command) {
        ConfigurationSection section = getCommandSection(command);
        if (section == null) return Collections.emptyList();
        return section.getStringList("permissions");
    }
}