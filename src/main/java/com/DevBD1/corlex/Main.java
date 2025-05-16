package com.DevBD1.corlex;

import com.DevBD1.corlex.cmds.CorlexCommand;
import com.DevBD1.corlex.api.CorlexAPI;
import com.DevBD1.corlex.lang.Lang;
import com.DevBD1.corlex.cmds.TestLangCommand;
import com.DevBD1.corlex.utils.Config;
import com.DevBD1.corlex.utils.CorlexLogger;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class Main extends JavaPlugin implements CorlexAPI {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        saveResource("lang/en.yml", false);
        saveResource("lang/tr.yml", false);

        Config.load(this);
        Lang.load(this);
        CorlexLogger.init(this);

        PluginCommand cmd = getCommand("corlex");
        if (cmd == null) {
            getLogger().severe("COMMAND '/corlex' NOT FOUND! Is it defined in plugin.yml?");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        CorlexCommand corlexCommand = new CorlexCommand(this);
        cmd.setExecutor(corlexCommand);
        cmd.setTabCompleter(corlexCommand);

        getCommand("testlang").setExecutor(new TestLangCommand());

        getLogger().info("Corlex loaded with localization support.");

        // Run test
        Lang.testNestedValue(); // Or Main.testNestedValue(), depending on where it's defined

        getServer().getServicesManager().register(CorlexAPI.class, this, this, ServicePriority.Normal);
        getLogger().info("Corlex API registered.");
    }

    @Override
    public void onDisable() {
        getServer().getServicesManager().unregister(CorlexAPI.class, this);
    }

    @Override
    public String translate(Player player, String key, Map<String, String> dynamic) {
        return Lang.t(player, key, dynamic);
    }
}
