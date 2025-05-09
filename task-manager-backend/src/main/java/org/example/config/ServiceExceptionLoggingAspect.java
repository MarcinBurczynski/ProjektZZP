package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ServiceExceptionLoggingAspect {

    // fires on any method inside a class annotated with @Service
    @AfterThrowing(
            pointcut = "within(@org.springframework.stereotype.Service *)",
            throwing = "ex")
    public void logServiceErrors(Exception ex) {
        log.error("Service layer threw exception ⇒ {}", ex.getMessage(), ex);
    }
}

