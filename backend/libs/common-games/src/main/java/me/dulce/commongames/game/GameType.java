package me.dulce.commongames.game;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface GameType {
    String gameId();

    String displayName();
}
