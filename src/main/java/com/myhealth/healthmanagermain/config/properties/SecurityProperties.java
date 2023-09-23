package com.myhealth.healthmanagermain.config.properties;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.security.jwt", ignoreUnknownFields = false)
public class SecurityProperties {

  @NonNull
  private String secret;
  @NonNull
  private Integer tokenValidityInSeconds;
  @NonNull
  private Integer tokenValidityInSecondsRememberMe;
}
