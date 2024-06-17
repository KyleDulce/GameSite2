package me.dulce.gamesite.testutils;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = SecurityContextFactory.class)
public @interface WithSecurityUser {
    String value() default "user";
}
