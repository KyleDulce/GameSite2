package me.dulce.gamesite.gamesite2.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Main App Configuration for the Server
 */
@Configuration
@Getter
public class AppConfig {
    /**
     * CORS allowed origins
     */
    @Value("${server.allowedOrigins}")
    private List<String> allowedOrigins;

    /**
     * The endpoint in which socket connections must connect
     */
    @Value("${endpoints.socket}")
    private String socketEndpoint;

    /**
     * The endpoint in which stomp operates
     */
    @Value("${endpoints.stomp}")
    private String stompEndpoint;

    /**
     * The frontend prefix that the frontend page operates
     */
    @Value("${endpoints.frontendPrefix}")
    private String frontendPrefixEndpoint;

    public String[] getAllowedOriginsAsArray() {
        return allowedOrigins.toArray(new String[0]);
    }
}
