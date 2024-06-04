package me.dulce.gamesite.gamesite2.configuration;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import me.dulce.gamesite.gamesite2.utilservice.TimeService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Main App Configuration for the Server */
@Configuration
@Getter
public class AppConfig {
  /** CORS allowed origins */
  @Value("${server.allowedOrigins}")
  private List<String> allowedOrigins;

  /** The endpoint in which socket connections must connect */
  @Value("${endpoints.socket}")
  private String socketEndpoint;

  /** The endpoint in which stomp operates */
  @Value("${endpoints.stomp}")
  private String stompEndpoint;

  /** Seconds before a user is automatically timed out */
  @Value("${auth.userActivityTimeoutSeconds}")
  private long userActivityTimeoutSeconds;

  /** The auth user profile file */
  @Value("${auth.allowed-users-file}")
  private String usersFile;

  public String[] getAllowedOriginsAsArray() {
    return allowedOrigins.toArray(new String[0]);
  }

  @Bean
  @ConditionalOnMissingBean
  public TimeService getDefaultTimeService() {
    return () -> Instant.now(Clock.systemUTC());
  }

  @Bean
  public WebMvcConfigurer corsConfiguration() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(@NotNull CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .exposedHeaders(HttpHeaders.SET_COOKIE)
            .allowCredentials(true)
            .allowedOrigins(getAllowedOriginsAsArray());
      }
    };
  }
}
