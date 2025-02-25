package me.dulce.commongames;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"me.dulce.commongames", "me.dulce.game", "me.dulce.commonutils"})
public class CommonGamesReference {}
