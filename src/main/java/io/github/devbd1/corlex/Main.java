package io.github.devbd1.corlex;

import io.github.devbd1.corlex.utilities.ServiceRegistrar;
import io.github.devbd1.corlex.utilities.ServiceUnregistrar;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private PluginInitializer bootstrap;

    @Override
    public void onEnable() {
        bootstrap = new PluginInitializer(this);
        bootstrap.enable();
    }

    @Override
    public void onDisable() {
        bootstrap.disable();
    }
}
