// LoggingAspect.java
package com.example.logging.aop;

import com.example.logging.annotation.LogExecution;
import com.example.logging.core.LoggingLibrary;
import com.example.logging.config.LogLevel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    @Autowired
    private LoggingLibrary loggingLibrary;

    @Around("@annotation(logExecution)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecution logExecution) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = logExecution.value().isEmpty() ?
                method.getDeclaringClass().getSimpleName() + "." + method.getName() :
                logExecution.value();

        com.example.logging.core.Logger logger = loggingLibrary.getLogger(method.getDeclaringClass());

        if (logExecution.logArguments()) {
            logMessage(logger, logExecution.level(),
                    "Entering {} with arguments: {}",
                    methodName, Arrays.toString(joinPoint.getArgs()));
        } else {
            logMessage(logger, logExecution.level(), "Entering {}", methodName);
        }

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            if (logExecution.logResult()) {
                logMessage(logger, logExecution.level(),
                        "Exiting {} with result: {} (execution time: {} ms)",
                        methodName, result, executionTime);
            } else {
                logMessage(logger, logExecution.level(),
                        "Exiting {} (execution time: {} ms)",
                        methodName, executionTime);
            }

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - start;
            logger.error("Exception in {} (execution time: {} ms): {}",
                    methodName, executionTime, e.getMessage(), e);
            throw e;
        }
    }

    private void logMessage(com.example.logging.core.Logger logger, LogLevel level,
                            String format, Object... args) {
        String message = String.format(format.replace("{}", "%s"), args);

        switch (level) {
            case TRACE:
                logger.trace(message);
                break;
            case DEBUG:
                logger.debug(message);
                break;
            case INFO:
                logger.info(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case ERROR:
                logger.error(message);
                break;
        }
    }
}