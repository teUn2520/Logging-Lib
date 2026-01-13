// LoggingUtils.java
package com.example.logging.util;

import com.example.logging.core.LoggingLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoggingUtils {

    private static LoggingLibrary staticLogger;

    @Autowired
    public LoggingUtils(LoggingLibrary loggingLibrary) {
        staticLogger = loggingLibrary;
    }

    public static com.example.logging.core.Logger getLogger(Class<?> clazz) {
        if (staticLogger == null) {
            throw new IllegalStateException("LoggingUtils not initialized. Make sure LoggingLibrary is configured.");
        }
        return staticLogger.getLogger(clazz);
    }

    public static void logStackTrace(Throwable throwable) {
        if (staticLogger != null) {
            com.example.logging.core.Logger logger = staticLogger.getLogger("StackTrace");
            logger.error("Stack trace:", throwable);
        }
    }
}