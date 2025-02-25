package me.dulce.commongames.gamemessage;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface IncomingGameData {
    String value();
}
