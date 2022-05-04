package me.dulce.gamesite.gamesite2.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {
    @Value("${server.allowedOrigins}")
    private List<String> allowedOrigins;

    @Value("${endpoints.socket}")
    private String socketEndpoint;

    @Value("${endpoints.stomp}")
    private String stompEndpoint;

    @Value("${endpoints.frontendPrefix}")
    private String frontendPrefixEndpoint;

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public String[] getAllowedOriginsAsArray() {
        return allowedOrigins.toArray(new String[allowedOrigins.size()]);
    }

    public String getSocketEndpoint() {
        return socketEndpoint;
    }

    public String getStompEndpoint() {
        return stompEndpoint;
    }

    public String getFrontendPrefixEndpoint() {
        return frontendPrefixEndpoint;
    }
}
