package io.github.devbd1.cublexcore.services;

import io.github.devbd1.cublexcore.modules.text.Lang;
import io.github.devbd1.cublexcore.utilities.ConfigManager;
import io.github.devbd1.cublexcore.utilities.LoggingManager;
import io.github.devbd1.cublexcore.utilities.LocaleGetter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

import static io.github.devbd1.cublexcore.modules.text.Lang.getNestedValue;

public class CorlexAPIImplementer implements CorlexAPI {
    private final JavaPlugin      plugin;
    private final LoggingManager  logger;

    /**
     * @param plugin your main JavaPlugin instance
     * @param logger shared LoggingManager for async file/console logs
     */
    public CorlexAPIImplementer(JavaPlugin plugin, LoggingManager logger) {
        this.plugin       = plugin;
        this.logger       = logger;
    }

    @Override
    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        if (sender instanceof Player p) {
            sender.sendMessage(Lang.t(p, key, placeholders));
        } else {
            sender.sendMessage(Lang.t((Player) null, key, placeholders));
        }
    }

    @Override
    public String get(Player player, String key, Map<String, String> placeholders) {
        return Lang.t(player, key, placeholders);
    }

    @Override
    public String getLocale(Player player) {
        return LocaleGetter.getPlayerLocale(player);
    }

    @Override
    public boolean exists(String locale, String key) {
        Map<String, Object> lang = Lang.getTranslations().get(locale);
        return getNestedValue(lang, key) != null;
        //return Lang.getNestedValue(Lang.getTranslations().get(locale), key) != null;
    }

    @Override
    public void logMissingKey(String key, String requestedLocale) {
        logger.log(String.format("Missing translation key '%s' for locale '%s'", key, requestedLocale));
    }

    @Override
    public void reload() {
        // 1) Reload the plugin’s core config
        plugin.reloadConfig();
        // 2) Re-initialize ConfigManager so it picks up any new defaults
        ConfigManager.init(plugin);
        // 3) Reload all language files into Lang’s in-memory map
//        Lang.reload();
        // 4) Log that we’ve reloaded everything
        logger.log("Corlex configuration and language files reloaded.");
    }

    @Override
    public boolean isLoggingEnabled() {
        return ConfigManager.getBoolean("logging-enabled", false);
    }

//    @Override
//    void setClientSideLore(ItemStack item, List<String> lore, Predicate<Player> condition);
}
