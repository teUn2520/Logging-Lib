package com.example.logging.config;

public enum LogLevel {
    TRACE(0),  // Самый детальный уровень
    DEBUG(1),  // Отладочная информация
    INFO(2),   // Информационные сообщения
    WARN(3),   // Предупреждения
    ERROR(4);  // Ошибки

    private final int level;

    LogLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    // Проверяет, включен ли данный уровень логирования
    // (сообщение будет залогировано, если его уровень >= текущего уровня конфигурации)
    public boolean isEnabled(LogLevel currentLevel) {
        return this.level >= currentLevel.level;
    }

    // Альтернативный метод - проверяет, должен ли быть залогирован уровень messageLevel
    // при текущем уровне конфигурации this
    public boolean shouldLog(LogLevel messageLevel) {
        return messageLevel.level >= this.level;
    }
}