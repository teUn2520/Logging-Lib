package com.example.logging.core;

public interface Logger {
    void trace(String message);
    void trace(String format, Object... args);

    void debug(String message);
    void debug(String format, Object... args);

    void info(String message);
    void info(String format, Object... args);

    void warn(String message);
    void warn(String format, Object... args);

    void error(String message);
    void error(String format, Object... args);
    void error(String message, Throwable throwable);
    void error(String format, Throwable throwable, Object... args);
}