package io.github.devbd1.cublexcore.modules.logger;

import org.bukkit.Bukkit;
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

public class PlayerLogger {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final Plugin plugin;
    private final LoggingManager logger;
    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<>();
    private final Path basePlayerLogsDir;
    private final boolean isOnlineMode;
    
    public PlayerLogger(Plugin plugin, LoggingManager logger) {
        this.plugin = plugin;
        this.logger = logger;
        // Check if server is in online mode
        this.isOnlineMode = Bukkit.getOnlineMode();
        
        // Changed path to go up one level from plugins folder and use custom structure
        this.basePlayerLogsDir = Path.of(".", "logs_custom", "players");
        
        // Create base directory
        try {
            Files.createDirectories(basePlayerLogsDir);
            plugin.getLogger().info("Player logs directory created/verified at: " + basePlayerLogsDir.toAbsolutePath());
            plugin.getLogger().info("Server online mode: " + isOnlineMode + " - Using " + 
                (isOnlineMode ? "UUIDs" : "player names") + " for player identification");
        } catch (IOException e) {
            logger.severe("Could not create player logs directory: " + basePlayerLogsDir.toAbsolutePath() + " - " + e.getMessage());
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
        // Use UUID for online servers, player name for offline servers
        String playerIdentifier = isOnlineMode ? player.getUniqueId().toString() : player.getName();
        
        LogEntry entry = new LogEntry(
            plugin.getName(),
            playerIdentifier,
            player.getName(), // Always keep the display name for logging readability
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
                
                // Use the player identifier for directory structure
                Path playerDir = basePlayerLogsDir.resolve(entry.playerIdentifier);
                Path logFile = playerDir.resolve(date + ".log");
                
                try {
                    Files.createDirectories(playerDir);
                    
                    try (PrintWriter out = new PrintWriter(
                            new BufferedWriter(new FileWriter(logFile.toFile(), true))
                    )) {
                        // Log format includes both identifier and display name for clarity
                        out.printf("[%s] [%s] [%s] [%s] [%s] [%s] [%s]%n",
                            timestamp,
                            entry.pluginName,
                            entry.playerIdentifier,
                            entry.playerDisplayName,
                            entry.location,
                            entry.event,
                            entry.notes
                        );
                    }
                } catch (IOException e) {
                    logger.severe("Failed to write player log for " + entry.playerDisplayName + " - " + e.getMessage());
                }
                
            } catch (InterruptedException ix) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private static class LogEntry {
        final String pluginName;
        final String playerIdentifier; // UUID for online mode, name for offline mode
        final String playerDisplayName; // Always the player's name for readability
        final String location;
        final String event;
        final String notes;
        
        LogEntry(String pluginName, String playerIdentifier, String playerDisplayName, String location, String event, String notes) {
            this.pluginName = pluginName;
            this.playerIdentifier = playerIdentifier;
            this.playerDisplayName = playerDisplayName;
            this.location = location;
            this.event = event;
            this.notes = notes;
        }
    }
}