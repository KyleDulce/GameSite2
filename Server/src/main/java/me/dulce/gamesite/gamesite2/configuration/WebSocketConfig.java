package me.dulce.gamesite.gamesite2.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/** Configuration for websockets */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired private AppConfig config;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(
                String.format(
                        "/%s/topic",
                        config.getSocketEndpoint()), // endpoint for subscription of broadcast
                "/queue");
        registry.setApplicationDestinationPrefixes(
                String.format(
                        "/%s/app", config.getSocketEndpoint())); // endpoint for client messages
        registry.setUserDestinationPrefix(
                String.format("/%s/user", config.getSocketEndpoint())); // user destination prefix
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(
                        String.format(
                                "/%s/%s", config.getSocketEndpoint(), config.getStompEndpoint()))
                .setAllowedOriginPatterns("*");
        registry.addEndpoint(
                        String.format(
                                "/%s/%s", config.getSocketEndpoint(), config.getStompEndpoint()))
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Bean
    public InMemoryHttpExchangeRepository createTraceRepo() {
        return new InMemoryHttpExchangeRepository();
    }
}
