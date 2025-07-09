/**
 * Asynchronous log manager for Bukkit plugins.
 * - Queues messages and writes them into daily log files under:
 *     plugins/{pluginName}/logs/{pluginName}-YYYY-MM-DD.log
 * - Respects config keys:
 *     logging-enabled (on/off)
 *     debug-mode     (console echo)
 * - Can be looked up by other plugins via Bukkit.getServicesManager().
 */
package io.github.devbd1.corlex.utilities;

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
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final boolean logging;
    private final boolean debugging;

    /** Use this constructor in your Main: new LoggingManager(this) */
    public LoggingManager(JavaPlugin plugin) {
        this.name    = plugin.getDescription().getName();
        this.logger  = plugin.getLogger();
        this.logDir  = Path.of("plugins", name, "logs");
        this.logging = ConfigManager.getBoolean("logging-enabled", false);
        this.debugging = ConfigManager.getBoolean("debug-mode", false);

        try {
            Files.createDirectories(logDir);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not create log directory: " + logDir, e);
        }

        Thread writer = new Thread(this::processQueue, name + "-LogWriter");
        writer.setDaemon(true);
        writer.start();
    }

    /** Public entrypoint for both internal & external plugins */
    public void log(String message) {
        if (logging) {
            writeLog(message);
        }
        if (debugging) {
            logger.log(Level.INFO, "[DEBUG] [{0}] {1}", new Object[]{name, message});
        }
    }

    /** Only enqueues when logging-enabled */
    public void writeLog(String message) {
        if (!queue.offer(message)) {
            logger.warning("Log queue full, dropped message for " + name);
        }
    }

    private void processQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String msg  = queue.take();
                String ts   = TS_FMT.format(LocalDateTime.now());
                String date = DATE_FMT.format(LocalDateTime.now());
                Path file   = logDir.resolve(name.toLowerCase() + "-" + date + ".log");

                try (PrintWriter out = new PrintWriter(
                        new BufferedWriter(new FileWriter(file.toFile(), true))
                )) {
                    out.printf("[%s] [%s] %s%n", ts, name, msg);
                }
            } catch (InterruptedException ix) {
                Thread.currentThread().interrupt();
            } catch (IOException io) {
                logger.log(Level.SEVERE, "Failed to write log entry for " + name, io);
            }
        }
    }
}
