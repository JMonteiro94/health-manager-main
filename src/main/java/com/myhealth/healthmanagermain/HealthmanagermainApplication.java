package com.myhealth.healthmanagermain;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MarkerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.core.env.Environment;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
@ConfigurationPropertiesScan("com.myhealth.healthmanagermain.config")
public class HealthmanagermainApplication {

  @NonNull
  private final Environment env;

  @PostConstruct
  public void initApplication() {
    Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
    if (activeProfiles.contains("dev") && activeProfiles.contains("prod")) {
      log.error(
          "You have misconfigured your application! It should not run "
              + "with both the 'dev' and 'prod' profiles at the same time."
      );
    }
    if (activeProfiles.contains("dev") && activeProfiles.contains("prod")) {
      log.error(
          "You have misconfigured your application! It should not "
              + "run with both the 'dev' and 'cloud' profiles at the same time."
      );
    }
  }

  public static void main(String[] args) {

    SpringApplication app = new SpringApplication(HealthmanagermainApplication.class);
    Map<String, Object> defProperties = new HashMap<>();
    defProperties.put("spring.profiles.default", "dev");
    app.setDefaultProperties(defProperties);
    Environment env = app.run(args).getEnvironment();
    logApplicationStartup(env);

  }

  private static void logApplicationStartup(Environment env) {
    String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store"))
        .map(key -> "https").orElse("http");
    String serverPort = env.getProperty("server.port");
    String contextPath = Optional
        .ofNullable(env.getProperty("server.servlet.context-path"))
        .filter(StringUtils::isNotBlank)
        .orElse("/");
    String hostAddress = "localhost";
    try {
      hostAddress = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      log.warn("The host name could not be determined, using `localhost` as fallback");
    }
    log.info(
        MarkerFactory.getMarker("CRLF_SAFE"),
        "\n----------------------------------------------------------\n\t" +
            "Application '{}' is running! Access URLs:\n\t" +
            "Local: \t\t{}://localhost:{}{}\n\t" +
            "External: \t{}://{}:{}{}\n\t" +
            "Profile(s): \t{}\n----------------------------------------------------------",
        env.getProperty("spring.application.name"),
        protocol,
        serverPort,
        contextPath,
        protocol,
        hostAddress,
        serverPort,
        contextPath,
        env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles()
    );
  }
}
