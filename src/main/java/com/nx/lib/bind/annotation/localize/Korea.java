package com.nx.lib.bind.annotation.localize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

@ConditionalOnExpression("{'default', 'local', 'gudev', 'dev', 'tdev', 'ndev', 'qa', 'cbt', 'production', 'crdev', 'crproduction', 'crqa'}.contains('${spring.profiles.active:default}')")
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Korea {

}
