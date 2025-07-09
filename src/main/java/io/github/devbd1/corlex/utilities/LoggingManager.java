package io.github.devbd1.corlex.utilities;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LoggingManager {

    private final String pluginName;
    private final File dataFolder;
    private final File logDir;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final Thread writerThread;
    private volatile boolean loggingEnabled;
    private volatile boolean debugMode;

    public LoggingManager(String pluginName) {
        this.pluginName = pluginName;
        this.dataFolder = new File("plugins", pluginName);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.logDir = new File(dataFolder, "logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        this.loggingEnabled = ConfigManager.getKeyValue("logging-enabled", Boolean.class, false);
        this.debugMode = ConfigManager.getKeyValue("debug-mode", Boolean.class, false);

        writerThread = new Thread(this::processQueue, pluginName + "-LogWriter");
        writerThread.setDaemon(true);
        writerThread.start();
    }

    public void log(String message) {
        writeLog(message);
        if (debugMode) {
            System.out.println("[DEBUG] [" + pluginName + "] " + message);
        }
    }

    public void writeLog(String message) {
        if (!loggingEnabled) return;
        queue.offer(message);
    }

    private void processQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String msg = queue.take();
                LocalDateTime now = LocalDateTime.now();
                String timestamp = now.format(timestampFormat);
                String date = now.toLocalDate().format(dateFormat);

                File logFile = new File(logDir,
                        pluginName.toLowerCase(Locale.ROOT) + "-" + date + ".log");

                try (PrintWriter out = new PrintWriter(
                        new BufferedWriter(new FileWriter(logFile, true))
                )) {
                    out.println("[" + timestamp + "] [" + pluginName + "] " + msg);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
