package com.nx.lib.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.nx.lib.bind.config.NopsGeneratedCorrelation;

/**
 * 공통 correlationId interceptor 사용 (자동 생성)
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(NopsGeneratedCorrelation.class)
public @interface EnableNopsGenCorrelation {
}
