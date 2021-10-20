package com.nx.lib.bind.annotation.localize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

@ConditionalOnExpression("{'default', 'local','dev', 'tdev', 'ndev', 'qa', 'cbt', 'production'}.contains('${spring.profiles.active:default}')")
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Korea {

}
