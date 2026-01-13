package com.example.logging.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class LoggerConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class);

    @EnableConfigurationProperties(LoggerConfig.class)
    static class TestConfig {
    }

    @Test
    void testDefaultValues() {
        contextRunner.run(context -> {
            LoggerConfig config = context.getBean(LoggerConfig.class);

            assertThat(config.getLevel()).isEqualTo(LogLevel.INFO);
            assertThat(config.getOutput()).isEqualTo(OutputType.CONSOLE);
            assertThat(config.getFilePath()).isEqualTo("logs/application.log");
            assertThat(config.getPattern()).isEqualTo("%d [%thread] %-5level %logger{36} - %msg%n");
            assertThat(config.getMaxFileSize()).isEqualTo(10);
            assertThat(config.getMaxHistory()).isEqualTo(7);
            assertThat(config.isEnabled()).isTrue();
        });
    }

    @Test
    void testCustomConfiguration() {
        contextRunner
                .withPropertyValues(
                        "logging.library.level=DEBUG",
                        "logging.library.output=FILE",
                        "logging.library.file-path=custom.log",
                        "logging.library.pattern=[%level] %msg",
                        "logging.library.max-file-size=20",
                        "logging.library.max-history=30",
                        "logging.library.enabled=false"
                )
                .run(context -> {
                    LoggerConfig config = context.getBean(LoggerConfig.class);

                    assertThat(config.getLevel()).isEqualTo(LogLevel.DEBUG);
                    assertThat(config.getOutput()).isEqualTo(OutputType.FILE);
                    assertThat(config.getFilePath()).isEqualTo("custom.log");
                    assertThat(config.getPattern()).isEqualTo("[%level] %msg");
                    assertThat(config.getMaxFileSize()).isEqualTo(20);
                    assertThat(config.getMaxHistory()).isEqualTo(30);
                    assertThat(config.isEnabled()).isFalse();
                });
    }

    @Test
    void testSettersAndGetters() {
        LoggerConfig config = new LoggerConfig();

        config.setLevel(LogLevel.WARN);
        assertThat(config.getLevel()).isEqualTo(LogLevel.WARN);

        config.setOutput(OutputType.BOTH);
        assertThat(config.getOutput()).isEqualTo(OutputType.BOTH);

        config.setFilePath("test/path.log");
        assertThat(config.getFilePath()).isEqualTo("test/path.log");

        config.setPattern("custom pattern");
        assertThat(config.getPattern()).isEqualTo("custom pattern");

        config.setMaxFileSize(50);
        assertThat(config.getMaxFileSize()).isEqualTo(50);

        config.setMaxHistory(15);
        assertThat(config.getMaxHistory()).isEqualTo(15);

        config.setEnabled(false);
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void testLogLevelValues() {
        assertThat(LogLevel.TRACE.getLevel()).isEqualTo(0);
        assertThat(LogLevel.DEBUG.getLevel()).isEqualTo(1);
        assertThat(LogLevel.INFO.getLevel()).isEqualTo(2);
        assertThat(LogLevel.WARN.getLevel()).isEqualTo(3);
        assertThat(LogLevel.ERROR.getLevel()).isEqualTo(4);
    }

    @Test
    void testLogLevelIsEnabled() {
        LogLevel info = LogLevel.INFO;

        assertThat(LogLevel.TRACE.isEnabled(info)).isFalse();
        assertThat(LogLevel.DEBUG.isEnabled(info)).isFalse();
        assertThat(LogLevel.INFO.isEnabled(info)).isTrue();
        assertThat(LogLevel.WARN.isEnabled(info)).isTrue();
        assertThat(LogLevel.ERROR.isEnabled(info)).isTrue();
    }

    @Test
    void testOutputTypeValues() {
        assertThat(OutputType.CONSOLE.name()).isEqualTo("CONSOLE");
        assertThat(OutputType.FILE.name()).isEqualTo("FILE");
        assertThat(OutputType.BOTH.name()).isEqualTo("BOTH");
        assertThat(OutputType.NONE.name()).isEqualTo("NONE");
    }
}