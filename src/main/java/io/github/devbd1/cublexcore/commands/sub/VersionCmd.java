package io.github.devbd1.cublexcore.commands.sub;

import io.github.devbd1.cublexcore.commands.CommandConfig;
import io.github.devbd1.cublexcore.commands.SubCommand;

import io.github.devbd1.cublexcore.modules.logger.LoggingManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.lushplugins.chatcolorhandler.ChatColorHandler;

import io.github.devbd1.cublexcore.utilities.VersionChecker;

import java.lang.reflect.Method;
import java.util.List;

public class VersionCmd implements SubCommand {
    String CMD_NAME = "version";
    private final JavaPlugin plugin;
    private final LoggingManager logger;

    public VersionCmd(JavaPlugin plugin, LoggingManager logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    public String getName() {
        return CMD_NAME;
    }

    @Override
    public List<String> getPermissions() {
        return CommandConfig.getPermissions(CMD_NAME);
    }

    @Override
    public List<String> getAliases() {
        return CommandConfig.getAliases(CMD_NAME);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        logger.info("[CublexCore] '/cublex <" + CMD_NAME + ">' command used by " + sender.getName());

        VersionChecker checker = new VersionChecker();

        String name;
        String version;
        List<String> authors;

        ChatColorHandler.sendMessage(sender, "§e§oChecking &6§oCublexCore §e§oplugin version, please wait...");
        try {
            // Paper API (>=1.20.5) -> getPluginMeta()
            Method metaMethod = JavaPlugin.class.getMethod("getPluginMeta");
            Object pluginMeta = metaMethod.invoke(plugin);

            Method getName = pluginMeta.getClass().getMethod("getName");
            Method getVersion = pluginMeta.getClass().getMethod("getVersion");
            Method getAuthors = pluginMeta.getClass().getMethod("getAuthors");

            name = (String) getName.invoke(pluginMeta);
            version = (String) getVersion.invoke(pluginMeta);
            authors = (List<String>) getAuthors.invoke(pluginMeta);

        } catch (Exception e) {
            // Spigot still uses getDescription()
            PluginDescriptionFile desc = plugin.getDescription();

            name = desc.getName();
            version = desc.getVersion();
            authors = desc.getAuthors();
        }

        //ChatColorHandler.sendMessage(sender, "&6[" + name + "] &cversion: " + version);
        //ChatColorHandler.sendMessage(sender, "&6[" + name + "] &cauthors: " + String.join(", ", authors));

        ChatColorHandler.sendMessage(sender, "§eThis server is running &6" + name + " §eversion §6" + version + " §eby §6" + authors + "§e." + " (Implementing CublexAPI version &6" + version + "&e)");

        // Version check
        checker.check(sender, "CublexLabs", name, version);

        //ChatColorHandler.sendMessage(sender, "Download the new version at: https://www.spigotmc.org/resources/cublexcore/");
        return true;
    }


}
