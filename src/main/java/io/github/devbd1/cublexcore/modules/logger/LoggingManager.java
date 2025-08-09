/**
 * Asynchronous log manager for Bukkit plugins.
 * - Queues messages and writes them into daily log files under:
 *     ../logs_custom/server_logs/{pluginName}-YYYY-MM-DD.log
 * - Respects config keys:
 *     logging-enabled (on/off)
 *     debug-mode     (console echo)
 * - Can be looked up by other plugins via Bukkit.getServicesManager().
 */
package io.github.devbd1.cublexcore.modules.logger;

import io.github.devbd1.cublexcore.utilities.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;
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

public class LoggingManager {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TS_FMT   = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String name;
    private final Logger logger;
    private final Path logDir;
    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<>();
    private final boolean logging;
    private final boolean debugging;

    /** Custom log levels for our logging system */
    public enum LogLevel {
        SEVERE, WARNING, INFO, DEBUG
    }

    /** Use this constructor in your Main: new LoggingManager(this) */
    public LoggingManager(JavaPlugin plugin) {
        this.name    = plugin.getPluginMeta().getName();
        this.logger  = plugin.getLogger();
        // Path is correct: go up one level from CublexCore to plugins/, then to sibling logs_custom/
        this.logDir  = Path.of(".", "logs_custom", "server_logs");
        this.logging = ConfigManager.getBoolean("logging-enabled", false);
        this.debugging = ConfigManager.getBoolean("debug-mode", false);

        try {
            Files.createDirectories(logDir);
            logger.info("Server logs directory created/verified at: " + logDir.toAbsolutePath());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not create server logs directory: " + logDir.toAbsolutePath(), e);
        }

        Thread writer = new Thread(this::processQueue, name + "-LogWriter");
        writer.setDaemon(true);
        writer.start();
    }

    /** Public entrypoint for both internal & external plugins - defaults to INFO level */
    public void log(String message) {
        log(LogLevel.INFO, message);
    }

    /** Log with specific level */
    public void log(LogLevel level, String message) {
        if (logging) {
            writeLog(level, message);
        }
        if (debugging || level == LogLevel.SEVERE || level == LogLevel.WARNING) {
            // Always show severe and warning in console, debug shows all levels when enabled
            Level bukkitLevel = convertToBukkitLevel(level);
            logger.log(bukkitLevel, "[{0}] [{1}] {2}", new Object[]{level.name(), name, message});
        }
    }

    /** Convenience methods for different log levels */
    public void severe(String message) {
        log(LogLevel.SEVERE, message);
    }

    public void warning(String message) {
        log(LogLevel.WARNING, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    /** Only enqueues when logging-enabled */
    public void writeLog(LogLevel level, String message) {
        LogEntry entry = new LogEntry(level, message);
        if (!queue.offer(entry)) {
            logger.warning("Log queue full, dropped " + level.name() + " message for " + name);
        }
    }

    private Level convertToBukkitLevel(LogLevel level) {
        return switch (level) {
            case SEVERE -> Level.SEVERE;
            case WARNING -> Level.WARNING;
            case INFO -> Level.INFO;
            case DEBUG -> Level.FINE;
        };
    }

    private void processQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                LogEntry entry = queue.take();
                String ts   = TS_FMT.format(LocalDateTime.now());
                String date = DATE_FMT.format(LocalDateTime.now());
                Path file   = logDir.resolve(name.toLowerCase() + "-" + date + ".log");

                try (PrintWriter out = new PrintWriter(
                        new BufferedWriter(new FileWriter(file.toFile(), true))
                )) {
                    out.printf("[%s] [%s] [%s] %s%n", ts, name, entry.level.name(), entry.message);
                }
            } catch (InterruptedException ix) {
                Thread.currentThread().interrupt();
            } catch (IOException io) {
                logger.log(Level.SEVERE, "Failed to write log entry for " + name, io);
            }
        }
    }

    /** Internal class to hold log entries with their levels */
    private static class LogEntry {
        final LogLevel level;
        final String message;

        LogEntry(LogLevel level, String message) {
            this.level = level;
            this.message = message;
        }
    }
}