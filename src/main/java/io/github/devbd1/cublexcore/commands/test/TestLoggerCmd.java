package io.github.devbd1.cublexcore.commands.test;

import io.github.devbd1.cublexcore.commands.CommandConfig;
import io.github.devbd1.cublexcore.commands.SubCommand;
import io.github.devbd1.cublexcore.modules.logger.LoggingManager;
import io.github.devbd1.cublexcore.modules.logger.PlayerLogger;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.List;

public class TestLoggerCmd implements SubCommand {
    
    private final LoggingManager loggingManager;
    private final PlayerLogger playerLogger;
    private final JavaPlugin plugin;
    
    public TestLoggerCmd(JavaPlugin plugin, LoggingManager loggingManager) {
        this.plugin = plugin;
        this.loggingManager = loggingManager;
        this.playerLogger = new PlayerLogger(plugin, loggingManager);
    }
    
    @Override
    public String getName() {
        return "testlogger";
    }
    
    @Override
    public List<String> getPermissions() {
        return CommandConfig.getPermissions("admin_cmds");
    }

    @Override
    public List<String> getAliases() {
        return CommandConfig.getAliases("admin_cmds");
    }
    
    @Override
    public String getDescription() {
        return "Test LoggingManager and PlayerLogger functionality";
    }
    
    @Override
    public String getUsage() {
        return "/cublex testlogger [manager|player|stress|all]";
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§e=== Logger Test Commands ===");
            sender.sendMessage("§a/cublex testlogger manager §7- Test LoggingManager");
            sender.sendMessage("§a/cublex testlogger player §7- Test PlayerLogger (requires player)");
            sender.sendMessage("§a/cublex testlogger stress §7- Run stress test");
            sender.sendMessage("§a/cublex testlogger all §7- Run all tests");
            sender.sendMessage("§7");
            sender.sendMessage("§7Server logs: ../logs_custom/server_logs/");
            sender.sendMessage("§7Player logs: ../logs_custom/players/<player_name>/");
            return true;
        }
        
        String testType = args[0].toLowerCase();
        
        switch (testType) {
            case "manager" -> {
                testLoggingManager(sender);
                return true;
            }
            case "player" -> {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cThis test requires a player to run!");
                    return true;
                }
                testPlayerLogger((Player) sender);
                return true;
            }
            case "stress" -> {
                testStressTest(sender);
                return true;
            }
            case "all" -> {
                testLoggingManager(sender);
                if (sender instanceof Player) {
                    testPlayerLogger((Player) sender);
                }
                testStressTest(sender);
                return true;
            }
            default -> {
                sender.sendMessage("§cUnknown test type! Use: manager, player, stress, or all");
                return true;
            }
        }
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("manager", "player", "stress", "all");
        }
        return List.of();
    }
    
    /**
     * Test all LoggingManager log levels and methods
     */
    private void testLoggingManager(CommandSender sender) {
        sender.sendMessage("§e=== Testing LoggingManager ===");
        
        // Test all log levels
        loggingManager.severe("SEVERE: This is a severe error message");
        loggingManager.warning("WARNING: This is a warning message");
        loggingManager.info("INFO: This is an info message");
        loggingManager.debug("DEBUG: This is a debug message");
        
        // Test generic log method (defaults to INFO)
        loggingManager.log("GENERIC: This is a generic log message");
        
        // Test log with specific level
        loggingManager.log(LoggingManager.LogLevel.SEVERE, "LEVEL-SPECIFIC: Severe message using log(level, message)");
        loggingManager.log(LoggingManager.LogLevel.WARNING, "LEVEL-SPECIFIC: Warning message using log(level, message)");
        loggingManager.log(LoggingManager.LogLevel.INFO, "LEVEL-SPECIFIC: Info message using log(level, message)");
        loggingManager.log(LoggingManager.LogLevel.DEBUG, "LEVEL-SPECIFIC: Debug message using log(level, message)");
        
        // Test with special characters and formatting
        loggingManager.info("FORMATTING TEST: Message with special chars: äöü ñ 中文 🎮");
        loggingManager.info("FORMATTING TEST: Multi-line message\nSecond line\nThird line");
        
        // Show updated paths
        Path serverLogsPath = Path.of("..", "logs_custom", "server_logs").toAbsolutePath();
        sender.sendMessage("§aLoggingManager test completed!");
        sender.sendMessage("§7Check console and server log files at:");
        sender.sendMessage("§b" + serverLogsPath);
    }
    
    /**
     * Test PlayerLogger functionality
     */
    private void testPlayerLogger(Player player) {
        player.sendMessage("§e=== Testing PlayerLogger ===");
        
        // Test basic player logging
        playerLogger.logPlayer(player, "Basic player log test");
        
        // Test player logging with event
        playerLogger.logPlayer(player, "test_event", "Player logging with custom event");
        playerLogger.logPlayer(player, "login", "Player logged in for testing");
        playerLogger.logPlayer(player, "command", "Player executed test command");
        
        // Test with different locations (simulate movement)
        Location originalLoc = player.getLocation();
        
        // Log current location
        playerLogger.logPlayer(player, "location_test", "Current location logged");
        
        // Test edge cases
        playerLogger.logPlayer(player, "special_chars", "Testing with special characters: äöü ñ 中文 🎮");
        playerLogger.logPlayer(player, "long_message", "This is a very long message to test how the logger handles longer text entries that might span multiple lines or contain lots of information about what the player is doing in the game");
        
        // Show updated paths
        Path playerLogsPath = Path.of("..", "logs_custom", "players", player.getName()).toAbsolutePath();
        player.sendMessage("§aPlayerLogger test completed!");
        player.sendMessage("§7Check player log files at:");
        player.sendMessage("§b" + playerLogsPath);
    }
    
    /**
     * Run stress test to check performance and queue handling
     */
    private void testStressTest(CommandSender sender) {
        sender.sendMessage("§e=== Running Stress Test ===");
        
        // Test LoggingManager under load
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            loggingManager.info("STRESS TEST: LoggingManager message #" + i);
            if (i % 25 == 0) {
                loggingManager.warning("STRESS TEST: Warning message #" + i);
            }
            if (i % 50 == 0) {
                loggingManager.severe("STRESS TEST: Severe message #" + i);
            }
        }
        
        // Test PlayerLogger under load (if player available)
        if (sender instanceof Player player) {
            for (int i = 0; i < 50; i++) {
                playerLogger.logPlayer(player, "stress_test", "Stress test message #" + i);
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        loggingManager.info("STRESS TEST: Completed in " + duration + "ms");
        sender.sendMessage("§aStress test completed in " + duration + "ms!");
        
        // Show paths for both log types
        Path serverLogsPath = Path.of("..", "logs_custom", "server_logs").toAbsolutePath();
        sender.sendMessage("§7Server logs: §b" + serverLogsPath);
        
        if (sender instanceof Player player) {
            Path playerLogsPath = Path.of("..", "logs_custom", "players", player.getName()).toAbsolutePath();
            sender.sendMessage("§7Player logs: §b" + playerLogsPath);
        }
        
        // Test queue overflow (optional - only if you want to test queue limits)
        sender.sendMessage("§eRunning queue overflow test...");
        for (int i = 0; i < 1000; i++) {
            loggingManager.debug("OVERFLOW TEST: Message #" + i);
        }
        sender.sendMessage("§aQueue overflow test completed!");
    }
}