package edu.upenn.cit5940.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static Logger instance;
    private final PrintWriter writer;

    private Logger(String filePath) throws IOException {
        this.writer = new PrintWriter(new FileWriter(filePath, true));
    }

    public static Logger getInstance(String filePath) throws IOException {
        if (instance == null) {
            instance = new Logger(filePath);
        }
        return instance;
    }

    public void logInfo(String message) {
        log("INFO", message);
    }

    public void logError(String message) {
        log("ERROR", message);
    }

    public void logWarning(String message) {
        log("WARN", message);
    }

    private void log(String level, String message) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        writer.println("[" + timestamp + "] " + level + " " + message);
        writer.flush();
    }

    public void close() {
        writer.close();
    }
}