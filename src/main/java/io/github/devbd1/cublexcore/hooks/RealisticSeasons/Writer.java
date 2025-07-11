package io.github.devbd1.cublexcore.hooks.RealisticSeasons;

import com.google.gson.Gson;
import me.casperge.realisticseasons.season.Season;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

public class Writer {
    private final JavaPlugin plugin;

    public Writer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void writeSeasonJson(World world, Season season) {
        Map<String, Object> json = Map.of(
                "world", world.getName(),
                "season", season.name(),
                "timestamp", System.currentTimeMillis()
        );

        try {
            Files.writeString(
                    Paths.get(plugin.getDataFolder() + "../web/season.json"),
                    new Gson().toJson(json),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to write season.json: " + e.getMessage());
        }
    }
}
