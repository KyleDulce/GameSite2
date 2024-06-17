package me.dulce.gamesite.gamesite2.testutils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = SecurityContextFactory.class)
public @interface WithSecurityUser {
    String value() default "user";
}
