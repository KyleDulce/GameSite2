package me.dulce.gamesite.gamesite2.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String SOCKET_ENDPOINT_GROUP = "socket";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(String.format("/%s/topic", SOCKET_ENDPOINT_GROUP), // endpoint for subscription of broadcast
                                    "/queue");
        registry.setApplicationDestinationPrefixes(String.format("/%s/app", SOCKET_ENDPOINT_GROUP));  // endpoint for client messages
        registry.setUserDestinationPrefix(String.format("/%s/user", SOCKET_ENDPOINT_GROUP));         // user destination prefix
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(String.format("/%s/stomp", SOCKET_ENDPOINT_GROUP))
                .setAllowedOriginPatterns("*");
        registry.addEndpoint(String.format("/%s/stomp", SOCKET_ENDPOINT_GROUP))
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

}
