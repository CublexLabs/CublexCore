package io.github.devbd1.cublexcore;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private PluginInitializer bootstrap;

    @Override
    public void onEnable() {
        bootstrap = new PluginInitializer(this);
        bootstrap.enable();

        getServer().getPluginManager().registerEvents(new io.github.devbd1.cublexcore.commands.test.LevelsDialogEventListener(), this);
    }

    @Override
    public void onDisable() {
        bootstrap.disable();
    }
}
