// LoggingLibraryAutoConfiguration.java
package com.example.logging.autoconfigure;

import com.example.logging.config.LoggerConfig;
import com.example.logging.core.LoggingLibrary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(LoggingLibrary.class)
@ConditionalOnProperty(prefix = "logging.library", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(LoggerConfig.class)
public class LoggingLibraryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LoggingLibrary loggingLibrary(LoggerConfig config) {
        return new LoggingLibrary(config);
    }
}