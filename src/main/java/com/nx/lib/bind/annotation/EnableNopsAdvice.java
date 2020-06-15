package com.nx.lib.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.nx.lib.exception.handler.NopsGlobalExceptionHandler;

/**
 * 공통 ControllerAdvice 사용
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = { NopsGlobalExceptionHandler.class })
public @interface EnableNopsAdvice {
}
