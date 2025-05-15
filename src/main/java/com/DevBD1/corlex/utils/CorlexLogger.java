package com.DevBD1.corlex.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CorlexLogger {

    private static File logFile;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void init(JavaPlugin plugin) {
        File dataFolder = new File(plugin.getDataFolder(), "logs");
        if (!dataFolder.exists()) dataFolder.mkdirs();

        logFile = new File(dataFolder, "corlex.log");
    }

    public static void log(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String full = "[" + timestamp + "] [Corlex] " + message;

        try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
            out.println(full);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
