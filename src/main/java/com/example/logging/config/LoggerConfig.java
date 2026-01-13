// LoggerConfig.java
package com.example.logging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "logging.library")
public class LoggerConfig {
    private LogLevel level = LogLevel.INFO;
    private OutputType output = OutputType.CONSOLE;
    private String filePath = "logs/application.log";
    private String pattern = "%d [%thread] %level %logger - %msg%n";
    private int maxFileSize = 10; // MB
    private int maxHistory = 7; // days
    private boolean enabled = true;

    // Getters and Setters
    public LogLevel getLevel() { return level; }
    public void setLevel(LogLevel level) { this.level = level; }

    public OutputType getOutput() { return output; }
    public void setOutput(OutputType output) { this.output = output; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }

    public int getMaxFileSize() { return maxFileSize; }
    public void setMaxFileSize(int maxFileSize) { this.maxFileSize = maxFileSize; }

    public int getMaxHistory() { return maxHistory; }
    public void setMaxHistory(int maxHistory) { this.maxHistory = maxHistory; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}

