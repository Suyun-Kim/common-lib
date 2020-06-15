package com.nx.lib.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.nx.lib.bind.config.NopsCorrelation;

/**
 * 공통 correlationId interceptor 사용
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(NopsCorrelation.class)
public @interface EnableNopsCorrelation {
}
