package com.myhealth.healthmanagermain.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("all public")
        .pathsToMatch("/api/**")
        .build();
  }

  @Bean
  public GroupedOpenApi greetingApi() {
    return GroupedOpenApi.builder()
        .group("test")
        .pathsToMatch("/test/**")
        .build();
  }

  @Bean
  public OpenAPI healthManagerAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Health Manager Main API")
            .description(
                "Simple fitness tracker where users can easily upload workouts, set goals and track overall progress.")
            .version("v0.0.1").summary("summary")
            .license(new License().name("Apache 2.0").url("http://springdoc.org")));
  }
}

