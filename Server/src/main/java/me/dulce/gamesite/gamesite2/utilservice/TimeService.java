package me.dulce.gamesite.gamesite2.utilservice;

import java.time.Instant;

/** Wrapper service for getting time */
public interface TimeService {
  Instant getCurrentInstant();
}
