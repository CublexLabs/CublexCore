package com.DevBD1.corlex;

import com.DevBD1.corlex.lang.Lang;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveResource("lang/en.yml", false);
        saveResource("lang/tr.yml", false);

        Lang.load(this);
        getLogger().info("Corlex loaded with localization support.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
