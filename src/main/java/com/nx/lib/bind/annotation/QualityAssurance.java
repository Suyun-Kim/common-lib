package com.nx.lib.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

@ConditionalOnExpression("{'qa', 'jqa', 'cqa', 'crqa'}.contains('${spring.profiles.active:default}')")
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface QualityAssurance {

}
