package me.dulce.gamesite.gamesite2.configuration;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/**")
        .addResourceLocations("classpath:/static/**")
        .resourceChain(true)
        .addResolver(
            new PathResourceResolver() {
              @Override
              protected Resource getResource(
                  @NotNull String resourcePath, @NotNull Resource location) throws IOException {
                Resource requestedResource = location.createRelative(resourcePath);

                if (requestedResource.exists() && requestedResource.isReadable()) {
                  return requestedResource;
                }
                return new ClassPathResource("/static/index.html");
              }
            });
  }
}
