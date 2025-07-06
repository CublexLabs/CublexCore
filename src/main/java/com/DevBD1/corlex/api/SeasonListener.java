package com.DevBD1.corlex.api;

import com.DevBD1.corlex.util.SeasonWriter;
import me.casperge.realisticseasons.api.SeasonChangeEvent;
import me.casperge.realisticseasons.season.Season;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;

public class SeasonListener implements Listener {

    private final JavaPlugin plugin;

    public SeasonListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSeasonChange(SeasonChangeEvent event) {
        World world = event.getWorld();
        Season oldSeason = event.getOldSeason();
        Season newSeason = event.getNewSeason();

        plugin.getLogger().info("[RealisticSeasons] " + world.getName() + ": " +
                oldSeason.name() + " → " + newSeason.name());

        new SeasonWriter(plugin).writeSeasonJson(world, newSeason);
    }
}
