package com.noljo.nolzo.global.aop.idempotent;

import com.noljo.nolzo.global.error.exception.DuplicateRequestException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    String key();
    String prefix();
    long expiration() default 5L;
    TimeUnit timeUnit() default TimeUnit.MINUTES;
    Class<? extends RuntimeException> exceptionOnDuplicate() default DuplicateRequestException.class;
}
