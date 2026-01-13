// LogExecution.java
package com.example.logging.annotation;

import java.lang.annotation.*;
import com.example.logging.config.LogLevel;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogExecution {
    String value() default "";
    boolean logArguments() default true;
    boolean logResult() default false;
    LogLevel level() default LogLevel.INFO;
}