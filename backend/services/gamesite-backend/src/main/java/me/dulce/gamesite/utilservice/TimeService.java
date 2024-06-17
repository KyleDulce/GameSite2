package me.dulce.gamesite.utilservice;

import java.time.Instant;

/** Wrapper service for getting time */
public interface TimeService {
    Instant getCurrentInstant();
}
