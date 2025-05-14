package com.DevBD1.corlex;

import com.DevBD1.corlex.lang.Lang;
import com.DevBD1.corlex.cmds.TestLangCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveResource("lang/en.yml", false);
        saveResource("lang/tr.yml", false);

        Lang.load(this);

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
