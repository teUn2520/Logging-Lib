// EnableLogging.java
package com.example.logging.annotation;

import org.springframework.context.annotation.Import;
import com.example.logging.autoconfigure.LoggingLibraryAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LoggingLibraryAutoConfiguration.class)
public @interface EnableLogging {
}