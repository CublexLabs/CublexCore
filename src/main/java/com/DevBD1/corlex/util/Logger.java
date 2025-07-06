package com.DevBD1.corlex.util;

import org.bukkit.plugin.java.JavaPlugin;

import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Logger {

    private static File logDir;
    private static String pluginName;
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void init(JavaPlugin plugin) {
        pluginName = plugin.getName();
        logDir = new File(plugin.getDataFolder(), "logs");
        if (!logDir.exists()) logDir.mkdirs();
    }

    public static void log(String message) {
        if (!Config.isLoggingEnabled()) return; // 🔇 logging disabled

        String timestamp = LocalDateTime.now().format(timestampFormat);
        String date = LocalDate.now().format(dateFormat);
        File logFile = new File(logDir, pluginName.toLowerCase() + "-" + date + ".log");

        try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
            out.println("[" + timestamp + "] [" + pluginName + "] " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readRecentLines(String date, int maxLines) {
        File file = new File(logDir, pluginName.toLowerCase() + "-" + date + ".log");
        if (!file.exists()) return List.of("§cNo log file for: " + date);

        List<String> lines = new ArrayList<>();
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file)) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count++ < maxLines) {
                lines.add("§7" + line);
            }
        //} catch (IOException e) {
        //    return List.of("§cError reading log: " + e.getMessage());
        }

        Collections.reverse(lines); // from oldest to newest
        return lines;
    }


}
