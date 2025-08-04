package io.github.devbd1.cublexcore.modules.logging;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerLogger {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final Plugin plugin;
    private final Logger logger;
    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<>();
    private final Path basePlayerLogsDir;
    
    public PlayerLogger(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.basePlayerLogsDir = Path.of("plugins", "players");
        
        // Create base directory
        try {
            Files.createDirectories(basePlayerLogsDir);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not create player logs directory: " + basePlayerLogsDir, e);
        }
        
        // Start processing thread
        Thread writer = new Thread(this::processQueue, "PlayerLogger-Writer");
        writer.setDaemon(true);
        writer.start();
    }
    
    public void logPlayer(Player player, String notes) {
        logPlayer(player, "none", notes);
    }
    
    public void logPlayer(Player player, String event, String notes) {
        LogEntry entry = new LogEntry(
            plugin.getName(),
            player.getName(),
            String.format("%s, %.2f,%.2f,%.2f",
                player.getWorld().getName(),
                player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ()
            ),
            event,
            notes
        );
        
        if (!queue.offer(entry)) {
            logger.warning("Player log queue full, dropped message for " + player.getName());
        }
    }
    
    private void processQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                LogEntry entry = queue.take();
                String timestamp = TS_FMT.format(LocalDateTime.now());
                String date = DATE_FMT.format(LocalDateTime.now());
                
                Path playerDir = basePlayerLogsDir.resolve(entry.playerName);
                Path logFile = playerDir.resolve(date + ".log");
                
                try {
                    Files.createDirectories(playerDir);
                    
                    try (PrintWriter out = new PrintWriter(
                            new BufferedWriter(new FileWriter(logFile.toFile(), true))
                    )) {
                        out.printf("[%s] [%s] [%s] [%s] [%s] [%s]%n",
                            timestamp,
                            entry.pluginName,
                            entry.playerName,
                            entry.location,
                            entry.event,
                            entry.notes
                        );
                    }
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Failed to write player log for " + entry.playerName, e);
                }
                
            } catch (InterruptedException ix) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private static class LogEntry {
        final String pluginName;
        final String playerName;
        final String location;
        final String event;
        final String notes;
        
        LogEntry(String pluginName, String playerName, String location, String event, String notes) {
            this.pluginName = pluginName;
            this.playerName = playerName;
            this.location = location;
            this.event = event;
            this.notes = notes;
        }
    }
}