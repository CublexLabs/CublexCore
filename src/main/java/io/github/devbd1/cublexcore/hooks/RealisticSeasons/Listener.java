//package io.github.devbd1.cublexcore.hooks.RealisticSeasons;
//
//import me.casperge.realisticseasons.api.SeasonChangeEvent;
//import me.casperge.realisticseasons.season.Season;
//import org.bukkit.event.EventHandler;
//import org.bukkit.plugin.java.JavaPlugin;
//import org.bukkit.World;
//
//public class Listener implements org.bukkit.event.Listener {
//
//    private final JavaPlugin plugin;
//
//    public Listener(JavaPlugin plugin) {
//        this.plugin = plugin;
//    }
//
//    @EventHandler
//    public void onSeasonChange(SeasonChangeEvent event) {
//        World world = event.getWorld();
//        Season oldSeason = event.getOldSeason();
//        Season newSeason = event.getNewSeason();
//
//        plugin.getLogger().info("[CORLEX] [RealisticSeasons] " + world.getName() + ": " +
//                oldSeason.name() + " → " + newSeason.name());
//
//        new Writer(plugin).writeSeasonJson(world, newSeason);
//    }
//}
