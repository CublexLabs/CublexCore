package io.github.devbd1.cublexcore;

import io.github.devbd1.cublexcore.modules.dialog.DialogConfigManager;
import io.github.devbd1.cublexcore.modules.dialog.DialogEventListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private PluginInitializer bootstrap;

    @Override
    public void onEnable() {
        bootstrap = new PluginInitializer(this);
        bootstrap.enable();

        // Load dialog definitions
        DialogConfigManager.init(this);

        getServer().getPluginManager().registerEvents(new DialogEventListener(), this);
    }

    @Override
    public void onDisable() {
        bootstrap.disable();
    }
}