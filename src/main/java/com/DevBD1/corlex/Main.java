package com.DevBD1.corlex;

import com.DevBD1.corlex.cmds.CorlexCommand;
import com.DevBD1.corlex.lang.Lang;
import com.DevBD1.corlex.cmds.TestLangCommand;
import com.DevBD1.corlex.utils.Config;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        saveResource("lang/en.yml", false);
        saveResource("lang/tr.yml", false);

        Config.load(this);
        Lang.load(this);

        CorlexCommand corlexCommand = new CorlexCommand(this);

        PluginCommand cmd = getCommand("corlex");
        if (cmd == null) {
            getLogger().severe("Failed to register /corlex command — check plugin.yml");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        cmd.setExecutor(new CorlexCommand(this));
        cmd.setTabCompleter(new CorlexCommand(this));
        //if (cmd != null) {
        //    cmd.setExecutor(corlexCommand);
        //    cmd.setTabCompleter(corlexCommand);
        //}

        getCommand("testlang").setExecutor(new TestLangCommand());

        getLogger().info("Corlex loaded with localization support.");

        // Run test
        Lang.testNestedValue(); // Or Main.testNestedValue(), depending on where it's defined
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
