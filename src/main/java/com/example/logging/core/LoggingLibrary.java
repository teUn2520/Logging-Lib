// LoggingLibrary.java
package com.example.logging.core;

import com.example.logging.config.LoggerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.logging.config.LogLevel;
import com.example.logging.config.OutputType;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class LoggingLibrary {

    private final LoggerConfig config;
    private final ConcurrentMap<String, Logger> loggers = new ConcurrentHashMap<>();
    private PrintWriter fileWriter;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Autowired
    public LoggingLibrary(LoggerConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void init() {
        if (config.isEnabled() && (config.getOutput() == OutputType.FILE || config.getOutput() == OutputType.BOTH)) {
            try {
                File logFile = new File(config.getFilePath());
                File parentDir = logFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    boolean dirsCreated = parentDir.mkdirs();
                    if (!dirsCreated) {
                        System.err.println("Failed to create directories for log file: " + config.getFilePath());
                    }
                }
                fileWriter = new PrintWriter(new FileWriter(logFile, true), true);
                logInternal("LoggingLibrary initialized successfully", LogLevel.INFO);
            } catch (IOException e) {
                System.err.println("Failed to initialize file logger: " + e.getMessage());
                fileWriter = null;
            }
        }
    }

    @PreDestroy
    public void cleanup() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }

    public Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public Logger getLogger(String name) {
        return loggers.computeIfAbsent(name, key -> new LoggerImpl(name));
    }

    private void logInternal(String message, LogLevel level) {
        if (!config.isEnabled() || level.ordinal() < config.getLevel().ordinal()) {
            return;
        }

        String formattedMessage = formatMessage(message, level, "LoggingLibrary");

        if (config.getOutput() == OutputType.CONSOLE || config.getOutput() == OutputType.BOTH) {
            writeToConsole(formattedMessage, level);
        }

        if ((config.getOutput() == OutputType.FILE || config.getOutput() == OutputType.BOTH)
                && fileWriter != null) {
            writeToFile(formattedMessage);
        }
    }

    private void writeToConsole(String message, LogLevel level) {
        switch (level) {
            case ERROR:
                System.err.println(message);
                break;
            default:
                System.out.println(message);
        }
    }

    private void writeToFile(String message) {
        synchronized (this) {
            fileWriter.println(message);
            fileWriter.flush();
        }
    }

    private String formatMessage(String message, LogLevel level, String loggerName) {
        // Используем replace вместо String.format
        String result = config.getPattern();

        // Заменяем все спецификаторы
        result = result.replace("%d", LocalDateTime.now().format(formatter))
                .replace("%thread", Thread.currentThread().getName())
                .replace("%level", level.toString())
                .replace("%logger", loggerName)
                .replace("%msg", message)
                .replace("%n", System.lineSeparator());

        // Обработка дополнительных спецификаторов
        if (loggerName.length() > 36) {
            result = result.replace("%logger{36}", loggerName.substring(0, 36));
        } else {
            result = result.replace("%logger{36}", loggerName);
        }

        // Обработка ширины уровня (%-5level)
        String levelStr = level.toString();
        if (result.contains("%-5level")) {
            String formattedLevel = String.format("%-5s", levelStr);
            result = result.replace("%-5level", formattedLevel);
        }

        return result;
    }

    // Вложенный класс LoggerImpl
    private class LoggerImpl implements Logger {
        private final String name;

        LoggerImpl(String name) {
            this.name = name;
        }

        @Override
        public void trace(String message) {
            log(message, LogLevel.TRACE);
        }

        @Override
        public void trace(String format, Object... args) {
            log(String.format(format, args), LogLevel.TRACE);
        }

        @Override
        public void debug(String message) {
            log(message, LogLevel.DEBUG);
        }

        @Override
        public void debug(String format, Object... args) {
            log(String.format(format, args), LogLevel.DEBUG);
        }

        @Override
        public void info(String message) {
            log(message, LogLevel.INFO);
        }

        @Override
        public void info(String format, Object... args) {
            log(String.format(format, args), LogLevel.INFO);
        }

        @Override
        public void warn(String message) {
            log(message, LogLevel.WARN);
        }

        @Override
        public void warn(String format, Object... args) {
            log(String.format(format, args), LogLevel.WARN);
        }

        @Override
        public void error(String message) {
            log(message, LogLevel.ERROR);
        }

        @Override
        public void error(String format, Object... args) {
            log(String.format(format, args), LogLevel.ERROR);
        }

        @Override
        public void error(String message, Throwable throwable) {
            String fullMessage = message + ": " + throwable.getMessage();
            log(fullMessage, LogLevel.ERROR);

            if (config.isEnabled() && LogLevel.ERROR.isEnabled(config.getLevel())) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                log(sw.toString(), LogLevel.ERROR);
            }
        }

        @Override
        public void error(String format, Throwable throwable, Object... args) {
            String message = String.format(format, args);
            error(message, throwable);
        }

        private void log(String message, LogLevel level) {
            if (!config.isEnabled() || !level.isEnabled(config.getLevel())) {
                return;
            }

            String formattedMessage = formatMessage(message, level, name);

            if (config.getOutput() == OutputType.CONSOLE || config.getOutput() == OutputType.BOTH) {
                writeToConsole(formattedMessage, level);
            }

            if ((config.getOutput() == OutputType.FILE || config.getOutput() == OutputType.BOTH)
                    && fileWriter != null) {
                writeToFile(formattedMessage);
            }
        }
    }
}


